package com.i10n.module.led;

import java.util.Vector;

import org.apache.log4j.Logger;

import com.i10n.db.entity.EtaDisplay;
import com.i10n.db.entity.Routes;
import com.i10n.dbCacheManager.LoadEtaDisplayDetails;
import com.i10n.dbCacheManager.LoadRoutesDetails;
import com.i10n.module.command.ETAChineseCommand;
import com.i10n.module.command.ICommandBean;
import com.i10n.module.command.IResponse;
import com.i10n.module.command.LEDResponse;
import com.i10n.module.led.ledinterface.ILEDDataGenerator;
import com.mchange.lang.ByteUtils;

/**
 * This class handle the responsibility of formulating the response data to be returned back to Chinese LED device
 * @author Dharmaraju V
 *
 */
public class ChineseLEDHandler extends LEDDataGeneratorHelper implements ILEDDataGenerator{
	
	private static final Logger LOG = Logger.getLogger(ChineseLEDHandler.class);

	private Routes routeEntity = null;
	
	
	/**
	 * 1) Get the list of un-notified Vehicles arriving to the stop sorted by eta
	 * 2) Get the saved bitmap of the route and destination along with the header bit map 
	 * 3) Formulate the bit map for arrival minutes
	 * 4) Merge with the bit map got from 2)
	 * 5) Repeat 2-4 for the second vehicle arriving, if any,
	 * 6) Push the data to the LED
	 * 7) Mark the two data arriving vehicle as notified to the client
	 * 8) Repeat the same after some time gap 
	 */
	@Override
	public IResponse getLEDData(ICommandBean cmd) {

		/**	PreFixed header, configurations and tail value of LED.*/
		String headerOfLED = "01D101340200000102000000000000000000000000000000000000";
		String configurationForFirstLED = "0401000000007F000F00010001000001";
		String configurationForSecondLED = "0401000010007F001F00010001000001";
		String tail = "AA";

		LOG.debug("CHINESELEDHANDLER : PROCESSING BHOPAL ETA COMMAND.");
		Vector<EtaDisplay> etaDisplayList = selectEtaEntriesWrtStopId(((ETAChineseCommand) cmd).getBusStopId());

		int languageFlag = ((ETAChineseCommand) cmd).getLanguageFlag();
		
		LEDResponse ledResponse = null;
		String[][] completeBitMap = {
				{ "0", "017D7D7D7D7D7D7D7D7D7D7D7D7D7D01" },
				{ "1", "FDFDFDFDFDFDFDFDFDFDFDFDFDFDFDFD" },
				{ "2", "01FDFDFDFDFDFD017F7F7F7F7F7F7F01" },
				{ "3", "01FDFDFDFDFDFDC1FDFDFDFDFDFDFD01" },
				{ "4", "7F7F7F7F7F7F7D7D7D01FDFDFDFDFDFD" },
				{ "5", "017F7F7F7F7F7F01FDFDFDFDFDFDFD01" },
				{ "6", "017F7F7F7F7F7F017D7D7D7D7D7D7D01" },
				{ "7", "01FDFDFDFDFDFDFDFDFDFDFDFDFDFDFD" },
				{ "8", "017D7D7D7D7D7D017D7D7D7D7D7D7D01" },
				{ "9", "017D7D7D7D7D7D01FDFDFDFDFDFDFD01" } };

		if (etaDisplayList.size() != 0) {
			LOG.debug("CHINESELEDHANDLER : FORMULATING LED RESPONSE.");
			String allLEDBitMap = headerOfLED+configurationForFirstLED;
			int count = 0;
			for (EtaDisplay etadisplay : etaDisplayList) {
				if(count == 2){
					/** Returning only two vehicle details.*/
					break;
				}

				routeEntity = LoadRoutesDetails.getInstance().retrieve(etadisplay.getRouteId());
				if(routeEntity == null){
					/**	Route not cached. Hence skip to next instance of ETA entity.*/
					continue;
				}

				if(count == 1){
					/**	Updating configuration setting of second LED.*/
					allLEDBitMap += configurationForSecondLED;
				}
				String arrivalMinutes = etadisplay.getArrivalTime() + "";
				
				byte[] routeBitMap = getRouteBitMap(null, null, languageFlag);
				byte[] destinationBitMap = getDestinationBitMap(routeEntity, null, languageFlag);
				byte[] arrivalMinutesBitMap = getArrivalMinutesBitMap(arrivalMinutes, completeBitMap, languageFlag);
				
				allLEDBitMap += ByteUtils.toHexAscii(mergeAllLEDBitMaps(routeBitMap,destinationBitMap, arrivalMinutesBitMap));
				count++;
			}
			if(count == 2){
				allLEDBitMap += tail;
				LOG.debug("CHINESELEDHANDLER : ALLLEDBITMAP : "+ allLEDBitMap);
				ledResponse = new LEDResponse(ByteUtils.fromHexAscii(allLEDBitMap));
			}else if(count == 1){
				LOG.debug("CHINESELEDHANDLER : ONLY ONE ENTRY PRESENT FOR THE STOP. HENCE STUFF THE RESPONSE WITH FF.");
				allLEDBitMap += configurationForSecondLED;
				StringBuffer LEDBitMap = new StringBuffer();
				LEDBitMap.append(allLEDBitMap);
				for(int i = LEDBitMap.length(); i< 1142; i=i+2){
					LEDBitMap.append(ByteUtils.toHexAscii((byte)255)); 
				}
				LEDBitMap.append(tail);
				allLEDBitMap = LEDBitMap.toString();
				LOG.debug("CHINESELEDHANDLER : ALLLEDBITMAP : "+ allLEDBitMap);
				ledResponse = new LEDResponse(ByteUtils.fromHexAscii(allLEDBitMap));
			}else{
				ledResponse = new LEDResponse();
			}
		} else {
			LOG.debug("CHINESELEDHANDLER : No Vehicle Approaching.");
			/** No Vehicle Approaching the stop **/
			ledResponse = new LEDResponse();
		}
		return ledResponse;
	}

	/**
	 * Returns the list of vehicles arriving to the current stop.
	 * @param busStopId
	 * @return
	 */
	@Override
	public Vector<EtaDisplay> selectEtaEntriesWrtStopId(long busStopId) {
		LOG.debug("CHINESELEDHANDLER : SELECTING ETA ENTRIES FOR THE STOPID :"+busStopId);
		Vector<EtaDisplay> etaDisplayList = new Vector<EtaDisplay>();
		for(Long vehicleId : LoadEtaDisplayDetails.getInstance().cacheEtaDisplayDetails.keySet()){
			for(Long routeId: LoadEtaDisplayDetails.getInstance().cacheEtaDisplayDetails.get(vehicleId).keySet()){
				for(Long stopId:LoadEtaDisplayDetails.getInstance().cacheEtaDisplayDetails.get(vehicleId).get(routeId).keySet()){
					if(stopId == busStopId){
						EtaDisplay etaDisplay = LoadEtaDisplayDetails.getInstance().cacheEtaDisplayDetails.get(vehicleId).get(routeId).get(stopId); 
						etaDisplayList.add(etaDisplay);
						LOG.debug("CHINESELEDHANDLER : SELECTED DETAIL :"+etaDisplay.toString());
					}
				}
			}
		}
		return etaDisplayList;
	}
	
	/**
	 * Merges all the LED related bitmaps such as RouteName, Destination and ETA
	 * @param routeBitMap
	 * @param destinationBitMap
	 * @param arrivalMinutesBitMap
	 * @return
	 */
	@Override
	public byte[] mergeAllLEDBitMaps(byte[] routeBitMap, byte[] destinationBitMap, byte[] arrivalMinutesBitMap) {
		LOG.debug("CHINESELEDHANDLER : MergingAllLEDBitMaps.");
		String LEDBitMap = "";
		String routeBitMapString = ByteUtils.toHexAscii(routeBitMap);
		String destinationBitMapString = ByteUtils.toHexAscii(destinationBitMap);
		String arrivalminutesBitMapString = ByteUtils.toHexAscii(arrivalMinutesBitMap);

		LOG.debug("CHINESELEDHANDLER : routeBitMapString.length() :"+routeBitMapString.length());
		LOG.debug("CHINESELEDHANDLER : destinationBitMapString.length() :"+destinationBitMapString.length());
		LOG.debug("CHINESELEDHANDLER : arrivalminutesBitMapString.length() :"+arrivalminutesBitMapString.length());
		for (int i = 0, r = 0, d = 0, m = 0; i < 16; r += 8, d += 20, m += 4, i++) {
			LEDBitMap += (routeBitMapString.substring(r, r + 8)
					+ destinationBitMapString.substring(d, d + 20)
					+ arrivalminutesBitMapString.substring(m, m + 4));
		}
		LOG.debug("CHINESELEDHANDLER : LedBitmap.length :"+LEDBitMap.length());
		return ByteUtils.fromHexAscii(LEDBitMap);
	}

	/**
	 * Function to merge bit maps of two numerals of eta.
	 * @param firstDigit
	 * @param secondDigit
	 * @return
	 */
	@Override
	public byte[] mergeTwoBitMaps(String firstDigit, String secondDigit) {
		LOG.debug("CHINESELEDHANDLER : MergingtwoBitMaps.");
		String mergedMapString = "";
		for (int i = 0; i < 32; i = i + 2) {
			mergedMapString += firstDigit.substring(i, i + 2)
			+ secondDigit.substring(i, i + 2);
		}
		return ByteUtils.fromHexAscii(mergedMapString);
	}
	
	/**
	 * Formulating arrivalMinutes BitMap
	 */
	@Override
	public byte[] getArrivalMinutesBitMap(String arrivalMinutes, String[][] completeBitMap, int languageFlag) {
		char[] arrivalMinutesArray = arrivalMinutes.toCharArray();
		String firstDigit = "";
		String secondDigit = "";
		if (arrivalMinutesArray.length == 1) {
			firstDigit = "0";
			secondDigit = arrivalMinutesArray[0] + "";
		} else {
			firstDigit = arrivalMinutesArray[0] + "";
			secondDigit = arrivalMinutesArray[1] + "";
		}

		LOG.debug("CHINESELEDHANDLER : ARRIVAL MINUTES DIGIT :"+ firstDigit + secondDigit);
		for (int i = 0, flag = 0; i < completeBitMap.length; i++) {
			if (firstDigit.equalsIgnoreCase(completeBitMap[i][0])) {
				firstDigit = completeBitMap[i][1];
				flag++;
			}
			if (secondDigit.equalsIgnoreCase(completeBitMap[i][0])) {
				secondDigit = completeBitMap[i][1];
				flag++;
			}
			if (flag == 2) {
				break;
			}
		}
		
		return mergeTwoBitMaps(firstDigit, secondDigit);
	}
	
	/**
	 * Formulating Destination Bitmap
	 */
	@Override
	public byte[] getDestinationBitMap(Routes routeEntity, String[][] completeBitMap, int languageFlag) {
		return routeEntity.getDestinationBitMap();
	}
	
	/**
	 * Formulating route Bitmap
	 */
	@Override
	public byte[] getRouteBitMap(String routeName, String[][] completeBitMap, int languageFlag) {
		return routeEntity.getRouteBitMap();
	}

}
