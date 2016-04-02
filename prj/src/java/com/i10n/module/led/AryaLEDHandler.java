package com.i10n.module.led;

import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.Vector;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.log4j.Logger;

import com.i10n.db.entity.EtaDisplay;
import com.i10n.db.entity.Routes;
import com.i10n.dbCacheManager.LoadEtaDisplayDetails;
import com.i10n.dbCacheManager.LoadRoutesDetails;
import com.i10n.module.command.ETAAryaCommand;
import com.i10n.module.command.ICommandBean;
import com.i10n.module.command.IResponse;
import com.i10n.module.command.LEDResponse;
import com.i10n.module.led.ledinterface.ILEDDataGenerator;
import com.mchange.lang.ByteUtils;

/**
 * This class handle the responsibility of formulating the response data to be returned back to Arya LED device
 * @author Dharmaraju V
 *
 */
public class AryaLEDHandler extends LEDDataGeneratorHelper implements ILEDDataGenerator{

	private static final Logger LOG = Logger.getLogger(AryaLEDHandler.class);

	/**
	 * Function to merge all(route name, destination and ETA) the bit map processed.
	 */
	@Override
	public byte[] mergeAllLEDBitMaps(byte[] routeBitMap, byte[] destinationBitMap, byte[] arrivalMinutesBitMap) {
		StringBuffer returnString = new StringBuffer();
		returnString.append(ByteUtils.toHexAscii(routeBitMap));
		returnString.append(ByteUtils.toHexAscii(destinationBitMap));
		returnString.append(ByteUtils.toHexAscii(arrivalMinutesBitMap));
		return ByteUtils.fromHexAscii((returnString.toString()));
	}

	/**
	 * Function to merger bit maps of two numerals of eta.
	 * @param firstDigit
	 * @param secondDigit
	 * @return
	 */
	@Override
	public byte[] mergeTwoBitMaps(String firstDigit, String secondDigit) {
		LOG.debug("ARYALEDHANDLER : MergingtwoBitMaps.");
		return ByteUtils.fromHexAscii(firstDigit+secondDigit);
	}

	/**
	 * Returns the list of vehicles arriving to the current stop.
	 * @param busStopId
	 * @return
	 */
	@Override
	public Vector<EtaDisplay> selectEtaEntriesWrtStopId(long busStopId) {
		LOG.debug("ARYALEDHANDLER : Selecting ETA entries for the Stop :"+busStopId);
		Vector<EtaDisplay> etaDisplayList = new Vector<EtaDisplay>();
		for(Long vehicleId : LoadEtaDisplayDetails.getInstance().cacheEtaDisplayDetails.keySet()){
			for(Long routeId: LoadEtaDisplayDetails.getInstance().cacheEtaDisplayDetails.get(vehicleId).keySet()){
				for(Long stopId:LoadEtaDisplayDetails.getInstance().cacheEtaDisplayDetails.get(vehicleId).get(routeId).keySet()){
					if(stopId == busStopId){
						EtaDisplay etaDisplay = LoadEtaDisplayDetails.getInstance().cacheEtaDisplayDetails.get(vehicleId).get(routeId).get(stopId); 
						etaDisplayList.add(etaDisplay);
						LOG.debug("ARYALEDHANDLER : Selected entity is :"+etaDisplay.toString());
					}
				}
			}
		}
		return etaDisplayList;
	}

	/**
	 * Function gives the necessary data required for Arya LED device.
	 */
	@Override
	public IResponse getLEDData(ICommandBean cmd) {
		LOG.debug("ARYALEDHANDLER : Processing ETA Bhopal command for Arya LED display");

		Vector<EtaDisplay> etaDisplayList = selectEtaEntriesWrtStopId(((ETAAryaCommand) cmd).getBusStopId());

		int languageFlag = ((ETAAryaCommand)cmd).getLanguageFlag();
		LEDResponse ledResponse = null;

		if (etaDisplayList.size() != 0) {
			String[][] completeBitMap = {
					{"0","FFFFFFFFFFC19C9C949C9CC1FFFFFFFF"},
					{"1","FFFFFFFFFF81E7E7E7A7C7E7FFFFFFFF"},      
					{"2","FFFFFFFFFF8199CFE3F999C3FFFFFFFF"}, 
					{"3","FFFFFFFFFFC399F9E3F999C3FFFFFFFF"},
					{"4","FFFFFFFFFFF0F98099C9E1F1FFFFFFFF"},
					{"5","FFFFFFFFFFC399F9F9839F81FFFFFFFF"},
					{"6","FFFFFFFFFFC39999839FCFE3FFFFFFFF"},
					{"7","FFFFFFFFFFE7E7E7F3739981FFFFFFFF"},
					{"8","FFFFFFFFFFC39999C39999C3FFFFFFFF"}, 
					{"9","FFFFFFFFFFC7E3E9C19999C3FFFFFFFF"},
					{"A","FFFFFFFFFF9999819999C3E7FFFFFFFF"},
					{"B","FFFFFFFFFF81CCCCC1CCCC81FFFFFFFF"},
					{"C","FFFFFFFFFFE1CC9F9F9FCCE1FFFFFFFF"},
					{"D","FFFFFFFFFF83C9CCCCCCC983FFFFFFFF"},
					{"E","FFFFFFFFFF80CECBC3CBCE80FFFFFFFF"},
					{"F","FFFFFFFFFF87CFCBC3CBCE80FFFFFFFF"},
					{"G","FFFFFFFFFFE0CC989F9FCCE1FFFFFFFF"},
					{"H","FFFFFFFFFF99999981999999FFFFFFFF"},
					{"I","FFFFFFFFFFC3E7E7E7E7E7C3FFFFFFFF"},
					{"J","FFFFFFFFFFC39999F9F9F9F0FFFFFFFF"},
					{"K","FFFFFFFFFF8CCCC9C3C9CC8CFFFFFFFF"},
					{"L","FFFFFFFFFF80CCCECFCFCF87FFFFFFFF"},
					{"M","FFFFFFFFFF9C9C948080889CFFFFFFFF"},
					{"N","FFFFFFFFFF9C9C9890848C9CFFFFFFFF"},
					{"O","FFFFFFFFFFC19C9C9C9C9CC1FFFFFFFF"},
					{"P","FFFFFFFFFF87CFCFC1CCCC81FFFFFFFF"},
					{"Q","FFFFFFFFFFF1C391999999C3FFFFFFFF"},
					{"R","FFFFFFFFFF8CCCC9C1CCCC81FFFFFFFF"},
					{"S","FFFFFFFFFFC399F3E7CF99C3FFFFFFFF"},
					{"T","FFFFFFFFFFC3E7E7E7E7A581FFFFFFFF"},
					{"U","FFFFFFFFFF81999999999999FFFFFFFF"},
					{"V","FFFFFFFFFFE7C39999999999FFFFFFFF"},
					{"W","FFFFFFFFFF9C8880949C9C9CFFFFFFFF"},
					{"X","FFFFFFFFFF9CC9E3E3C99C9CFFFFFFFF"},
					{"Y","FFFFFFFFFFC3E7E7C3999999FFFFFFFF"},
					{"Z","FFFFFFFFFF80CCE6F3B99C80FFFFFFFF"},
					{" ","FFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFF"}};
			LOG.debug("ARYALEDHANDLER : Formulating response for Arya LED");
			String allLEDBitMap = "";
			/**	Getting the vehicles which are not displayed yet.*/
			Vector<EtaDisplay> undisplayedEtaDisplayList = getListOfVehiclesLeftUndisplayedOnLED(etaDisplayList);
			allLEDBitMap = getAllLEDBitMapForTheRequestedStop(undisplayedEtaDisplayList, completeBitMap, languageFlag);
			if(allLEDBitMap.length() != 0){
				LOG.info("ARYALEDHANDLER : All LED BitMap : "+ allLEDBitMap);
				ledResponse = new LEDResponse(ByteUtils.fromHexAscii(allLEDBitMap));
			}else{
				LOG.error("ARYALEDHANDLER : Not able to formulate LED bit map");
				ledResponse = new LEDResponse();
			}
		} else {
			LOG.info("ARYALEDHANDLER : No Vehicle Approaching.");
			/** No Vehicle Approaching the stop **/
			ledResponse = new LEDResponse();
		}
		return ledResponse;
	}

	/**
	 * 
	 * @param etaDisplayList
	 * @param completeBitMap
	 * @param languageFlag
	 * @return Bit map to displayed on PIS
	 */
	private String getAllLEDBitMapForTheRequestedStop(Vector<EtaDisplay> etaDisplayList, String[][] completeBitMap, int languageFlag) {
		String allLEDBitMap = "";
		for (EtaDisplay etaDisplay : etaDisplayList) {
			Routes routeEntity = LoadRoutesDetails.getInstance().cacheRoute.get(etaDisplay.getRouteId());
			if(routeEntity == null){
				/**	Route not cached. Hence skip to next instance of ETA entity.*/
				continue;
			}

			byte[] routeBitMap = getRouteBitMap(routeEntity.getRouteName(), completeBitMap, languageFlag);
			byte[] destinationBitMap = getDestinationBitMap(routeEntity, completeBitMap, languageFlag);
			byte[] arrivalMinutesBitMap = getArrivalMinutesBitMap(etaDisplay.getArrivalTime() + "",completeBitMap, languageFlag);
			LOG.debug("ARYALEDHANDLER : Route BitMap : "+ ByteUtils.toHexAscii(routeBitMap));
			LOG.debug("ARYALEDHANDLER : Destination BitMap : "+ ByteUtils.toHexAscii(destinationBitMap));
			LOG.debug("ARYALEDHANDLER : Arrival Minutes BitMap : "+ ByteUtils.toHexAscii(arrivalMinutesBitMap));
			allLEDBitMap += ByteUtils.toHexAscii(mergeAllLEDBitMaps(routeBitMap, destinationBitMap, arrivalMinutesBitMap));
			/**	As of now the Arya LED is capable of displaying only one entry hence breaking out.*/
			etaDisplay.setType(1);						// Changing the status of the ETA entry after displaying on LED 
			updateTypeInEtaDisplyaCache(etaDisplay);
			LOG.info("ARYALEDHANDLER : Selected EtaDisplay Entity is : "+etaDisplay.toString());
			break;
		}
		return allLEDBitMap;
	}

	/**
	 * Get the vehicle details to display on LED. 
	 * @param etaDisplayList
	 * @return
	 */
	private Vector<EtaDisplay> getListOfVehiclesLeftUndisplayedOnLED(Vector<EtaDisplay> etaDisplayList) {
		Vector<EtaDisplay> filterEtaDisplays = new Vector<EtaDisplay>();
		LOG.debug("ARYALEDHANDLER : UnSorted EtaDisplayList is");
		for(EtaDisplay etaDisplay : etaDisplayList)
			LOG.debug("ARYALEDHANDLER : "+etaDisplay.toString());
		
		//	Sort the etadisplay list on time
		Collections.sort(etaDisplayList, new Comparator<EtaDisplay>() {
			public int compare(EtaDisplay o1, EtaDisplay o2) {
				return o1.getArrivalTime().compareTo(o2.getArrivalTime());
			}});
		LOG.debug("ARYALEDHANDLER : Sorted EtaDisplayList is");
		for(EtaDisplay etaDisplay : etaDisplayList)
			LOG.debug("ARYALEDHANDLER : "+etaDisplay.toString());
		
		filterEtaDisplays = initializeFilterEtaDisplayListWithAlreadyDisplayedEntities(etaDisplayList);
		
		// Get the list of etadisplays (One per Route) having type = 0 
		filterEtaDisplays = filterEtaDisplays(etaDisplayList, filterEtaDisplays);
		
		if(filterEtaDisplays.size() != 0)
			return filterEtaDisplays;
		LOG.debug("ARYALEDHANDLER : All related entities have been displayed. Hence resetting the display status of the entities");
		resetTypeInEtaDisplayCache(etaDisplayList);
		
		filterEtaDisplays = filterEtaDisplays(etaDisplayList, filterEtaDisplays);
		
		LOG.debug("ARYALEDHANDLER : ReturnEtaDisplayList is ");
		for(EtaDisplay etaDisplay : filterEtaDisplays)
			LOG.debug("ARYALEDHANDLER : "+etaDisplay.toString());
		return filterEtaDisplays;
	}

	/**
	 * Making sure that LED in given with least arriving vehicle's data
	 * @param etaDisplayList
	 * @return
	 */
	private Vector<EtaDisplay> initializeFilterEtaDisplayListWithAlreadyDisplayedEntities(Vector<EtaDisplay> etaDisplayList) {
		Vector<EtaDisplay> filterEtaDisplays = new Vector<EtaDisplay>();
		for(EtaDisplay etaDisplay : etaDisplayList){
			if(etaDisplay.getType() == 1){
				filterEtaDisplays.add(etaDisplay);
			}
		}
		return filterEtaDisplays;
	}

	private Vector<EtaDisplay> filterEtaDisplays(Vector<EtaDisplay> etaDisplayList, Vector<EtaDisplay> filterEtaDisplays) {
		for(EtaDisplay etaDisplay : etaDisplayList){
			boolean routeAddFlag = true;
			for(EtaDisplay returnEtaDisplay : filterEtaDisplays){
				LOG.debug("ARYALEDHANDLER : Comparing EtadisplayLists \n "+etaDisplay.toString()+" with \n "+returnEtaDisplay.toString());
				if(returnEtaDisplay.getRouteId() == etaDisplay.getRouteId()){
					routeAddFlag = false;
				}
			}
			if(routeAddFlag && (etaDisplay.getType() == 0)){
				LOG.debug("ARYALEDHANDLER : Filtered etadisplay for display is : "+etaDisplay.toString());
				filterEtaDisplays.add(etaDisplay);
			}
		}
		Iterator<EtaDisplay> etaIterator = filterEtaDisplays.iterator();
		while(etaIterator.hasNext()){
			EtaDisplay etaDisplay = etaIterator.next();
			if(etaDisplay.getType() == 1 ){
				etaIterator.remove();
			}
		}
		return filterEtaDisplays;
	}

	/**
	 * Resetting the type of etadisplay status of LED display after all entries being displayed. 
	 * @param etaDisplayList
	 */
	private void resetTypeInEtaDisplayCache(Vector<EtaDisplay> etaDisplayList) {
		Iterator<EtaDisplay> etaDisplayIList = etaDisplayList.iterator();
		while(etaDisplayIList.hasNext()){
			EtaDisplay etaDisplay = etaDisplayIList.next();
			etaDisplay.setType(0);
			updateTypeInEtaDisplyaCache(etaDisplay);
		}

	}

	/**
	 * Updating only cache with the changed type which corresponds to status of being displayed on eta.
	 * @param etaDisplay
	 */
	private void updateTypeInEtaDisplyaCache(EtaDisplay etaDisplay) {
		LOG.debug("ARYALEDHANDLER : Updating Type into Eta cache after selecting for display");
		ConcurrentHashMap<Long, ConcurrentHashMap<Long, EtaDisplay>> routeList = LoadEtaDisplayDetails.getInstance().cacheEtaDisplayDetails.get(etaDisplay.getVehicleId());
		ConcurrentHashMap<Long, EtaDisplay> stopList = routeList.get(etaDisplay.getRouteId());
		stopList.put(etaDisplay.getStopId(), etaDisplay);
		routeList.put(etaDisplay.getRouteId(), stopList);
		LoadEtaDisplayDetails.getInstance().cacheEtaDisplayDetails.put(etaDisplay.getVehicleId(), routeList);
	}

	/**
	 * Formulating bitmap for routeNumber/routeName
	 * @param routeName
	 * @param completeBitMap
	 * @return
	 */
	@Override
	public byte[] getRouteBitMap(String routeName, String[][] completeBitMap, int languageFlag) {
		String routeBitMapString = "";
		routeName.trim();
		char[] routeNameCharacterArray = routeName.toCharArray();		
		for(int i=0; i< routeNameCharacterArray.length; i++){
			for(int j=0; j<completeBitMap.length; j++){
				if((routeNameCharacterArray[i]+"").equalsIgnoreCase(completeBitMap[j][0])){
					routeBitMapString += completeBitMap[j][1];
				}
			}
		}

		return ByteUtils.fromHexAscii(routeBitMapString);
	}

	/**
	 * Formulating bitmap for destination.
	 * @param destination
	 * @param completeBitMap
	 * @return
	 */
	@Override
	public byte[] getDestinationBitMap(Routes routeEntity, String[][] completeBitMap, int languageFlag) {
		/** For Hindi font */
		if(languageFlag == 2 ){
			return routeEntity.getDestinationBitMap();
		}
		// For temporary testing... 
		else if (languageFlag == 1 ){
			return routeEntity.getDestinationBitMap();
		}
		/** English font */
		String destinationName = routeEntity.getEndPoint();
		String destinationBitMapString = "";
		destinationName.trim();
		/**	Restricting the length of the destination name as per the demands of LED display*/
		if(destinationName.length() > 9){
			destinationName = destinationName.substring(0, 9);
		}else{
			for(;destinationName.length() < 9; ){
				destinationName += " ";
			}
		}
		char[] destinationNameCharacterArray = destinationName.toCharArray();		
		for(int i=0; i< destinationName.length(); i++){
			for(int j=0;j<completeBitMap.length; j++){
				if((destinationNameCharacterArray[i]+"").equalsIgnoreCase(completeBitMap[j][0])){
					destinationBitMapString += completeBitMap[j][1];
				}
			}
		}
		return ByteUtils.fromHexAscii(destinationBitMapString);
	}

	/**
	 * Formulating bitmap string for arrival minutes. 
	 * @param arrivalMinutes
	 * @param completeBitMap
	 * @return
	 */
	@Override
	public byte[] getArrivalMinutesBitMap(String arrivalMinutes, String[][] completeBitMap, int languageFlag) {
		LOG.debug("ARYALEDHANDLER : Arrival Minutes :"+arrivalMinutes);
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
}
