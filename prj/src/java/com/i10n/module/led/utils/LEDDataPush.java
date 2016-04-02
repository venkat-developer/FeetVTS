package com.i10n.module.led.utils;

import java.io.IOException;
//import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.ArrayList;
//import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Vector;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.log4j.Logger;

import com.i10n.db.entity.EtaDisplay;
import com.i10n.db.entity.LEDToBusStop;
import com.i10n.db.entity.Routes;
import com.i10n.dbCacheManager.LoadEtaDisplayDetails;
import com.i10n.dbCacheManager.LoadLEDToBusStopMap;
import com.i10n.dbCacheManager.LoadRoutesDetails;
import com.mchange.lang.ByteUtils;

/**
 * Thread which handles the data push to the LED module by which the thread is spawned 
 * 
 * @author Dharmaraju V
 *
 */
public class LEDDataPush implements Runnable{

	private static Logger LOG = Logger.getLogger(LEDDataPush.class);

	/**
	 * Shutdown management variable;
	 */
	public static boolean isTerminate = false;

	/**
	 * Client connection holder
	 */
	private Socket clientSocket;

	/**
	 * This is precisely the GPRS ID of the device
	 */
	private String deviceID;

	private byte[] header;

	private static final String LED_INITIALIZE = "0101";

	private static final String CLEAR_CHINESE_LED = "01D101A401000001010000000000000000000000000000000000000401000000005F001F00010001000001FFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFAA";
	private static final String CLEAR_ARYA_LED = "0101FFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFF";
	private static final String TAIL = "AA";

//	private static final int HEADER_LENGTH = 20;
	private static final int THREAD_SLEEP_TIME = 30000; // 30 seconds
//	private static final int HEADER_RESPONSE_ALARM_TIME = 90000; // 90 seconds

	public LEDDataPush(Socket clientSocket, String deviceID, byte[] header) {
		this.clientSocket = clientSocket;
		this.deviceID = deviceID;
		this.header = header;
	}

	/**
	 * Function to merge all(route name, destination and ETA) the bit map processed.
	 * @param selectedEtaEntriesCount 
	 */
	public byte[] mergeAllLEDBitMaps(byte[] routeBitMap, byte[] destinationBitMap, byte[] arrivalMinutesBitMap) {
		StringBuffer returnString = new StringBuffer();
		returnString.append(ByteUtils.toHexAscii(routeBitMap));
		returnString.append(ByteUtils.toHexAscii(destinationBitMap));
		returnString.append(ByteUtils.toHexAscii(arrivalMinutesBitMap));
		returnString.append(TAIL);

		return ByteUtils.fromHexAscii((returnString.toString()));
	}

	/**
	 * Function to merger bit maps of two numerals of eta.
	 * @param firstDigit
	 * @param secondDigit
	 * @return
	 */
	public String mergeArrivalMinutesBitMap(String firstDigit, String secondDigit) {
		LOG.debug("LEDDataPush : MergingtwoBitMaps.");
		//		return ByteUtils.fromHexAscii(firstDigit+secondDigit);
		final int NUMBER_OF_LED_ROWS = 16;
		String arrivalMinutesBitMap="";
		for(int i=0, j=0; i<NUMBER_OF_LED_ROWS; i++,j+=2){
			arrivalMinutesBitMap += (firstDigit.substring(j, j+2)+secondDigit.substring(j,j+2));
		}

		return arrivalMinutesBitMap;
	}

	/**
	 * Returns the list of vehicles arriving to the current stop.
	 * @param busStopId
	 * @return
	 */
	public Vector<EtaDisplay> selectEtaEntriesWrtStopId(long busStopId) {
		LOG.debug("LEDDataPush : Selecting ETA entries for the Stop :"+busStopId);
		Vector<EtaDisplay> etaDisplayList = new Vector<EtaDisplay>();
		for(Long vehicleId : LoadEtaDisplayDetails.getInstance().cacheEtaDisplayDetails.keySet()){
			for(Long routeId : LoadEtaDisplayDetails.getInstance().cacheEtaDisplayDetails.get(vehicleId).keySet()){
				for(Long stopId : LoadEtaDisplayDetails.getInstance().cacheEtaDisplayDetails.get(vehicleId).get(routeId).keySet()){
					if(stopId == busStopId){
						EtaDisplay etaDisplay = LoadEtaDisplayDetails.getInstance().cacheEtaDisplayDetails.
								get(vehicleId).get(routeId).get(stopId); 
						etaDisplayList.add(etaDisplay);
						LOG.debug("LEDDataPush : Selected entity is :"+etaDisplay.toString());
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
	public void run() {
		LOG.debug("LEDDataPush : Processing LEDDataPush for device : "+this.deviceID);
		LEDToBusStop ledToBusStop = LoadLEDToBusStopMap.getInstance().retrieve(this.deviceID);

		Long busStopId = getBusStopIdAssociatedWithTheDevice(deviceID);
		if(busStopId == 0){
			LOG.warn("StopId not mapped with the device");
			return;
		}

//		Calendar socketStartTime = Calendar.getInstance();
		byte[] headerResponse = getHeartBeatResponse(this.header);
		try {
			this.clientSocket.getOutputStream().write(headerResponse);
			this.clientSocket.getOutputStream().flush();
			LOG.debug(this.deviceID+" Responded to the heart beat with : "+ByteUtils.toHexAscii(headerResponse));

			/**
			 * For Chinese led there is a tail data push requirement to avoid flickering
			 */
			if(ledToBusStop.getLedType() == LEDToBusStop.CHINESE_LED){
				this.clientSocket.getOutputStream().write(hex2Byte(TAIL));
				this.clientSocket.getOutputStream().flush();
				Thread.sleep(2000);
			}

			while(!isTerminate){
				try{
					Vector<EtaDisplay> etaDisplayList = selectEtaEntriesWrtStopId(busStopId);

					byte[] ledResponse = null;

					if (etaDisplayList.size() != 0) {
						HashMap<String, ArrayList<String>> completeBitMap = getCompleteBitMap();
						LOG.debug("LEDDataPush : Formulating response for LED : "+ledToBusStop.getLedType());
						String allLEDBitMap = "";

						/**	Getting the vehicles which are not displayed yet.*/
						Vector<EtaDisplay> undisplayedEtaDisplayList = getListOfVehiclesLeftUndisplayedOnLED(etaDisplayList);
						allLEDBitMap = getAllLEDBitMapForTheRequestedStop(undisplayedEtaDisplayList, completeBitMap, ledToBusStop.getLedType());
						if(allLEDBitMap.length() != 0){
							LOG.debug(this.deviceID+" All LED BitMap : "+ allLEDBitMap);
							ledResponse = hex2Byte(allLEDBitMap);
						}else{
							LOG.error(this.deviceID+" Not able to formulate LED bit map");
							ledResponse = getLEDClearData(ledToBusStop);
						}
					} else {
						LOG.debug(this.deviceID+" No Vehicle Approaching.");
						/** 
						 * No Vehicle Approaching the stop
						 * Push clear LED data
						 **/
						ledResponse = getLEDClearData(ledToBusStop);
					}

					if(ledResponse != null){
						writeDataToLED(ledResponse, ledToBusStop);
					}

					/**
					 * Process wait between the LED data push and heart beat response
					 * 
					 */
					LOG.debug(this.deviceID+" Sleeping for 30 sec");
					Thread.sleep(THREAD_SLEEP_TIME);
					
					/**
					 * Not managing the heart beat response as data sent frequently
					 */
					/*LOG.info(this.deviceID+" Checking for Heart beat read");
					Calendar currentTime = Calendar.getInstance();
					if((currentTime.getTime().getTime() - socketStartTime.getTime().getTime()) >= (HEADER_RESPONSE_ALARM_TIME)){
						LOG.info(this.deviceID+" Its time to respond for heart beat");
						respondToHeartBeat(this.clientSocket.getInputStream(), this.clientSocket.getOutputStream(), ledToBusStop);
						socketStartTime = Calendar.getInstance();
					} else {
						LOG.info(this.deviceID+" Still time left to respond for heart beat");
					}*/
				} catch(Exception e){
					LOG.error(this.deviceID+" Error while pushing data to LED ",e);
					return;
				}
			}
		} catch (IOException e) {
			LOG.error(this.deviceID+" ",e);
		} catch (InterruptedException e) {
			LOG.error(this.deviceID+" ",e);
		}

	}

	/**
	 * Push the calculated data to the requested device
	 * 1) Push LED initialise data for configuring LED for display
	 * 2) Wait for 5ms (Mandatory) 
	 * 3) Push the actual data 
	 * @param ledResponse
	 * @param ledToBusStop
	 */
	private void writeDataToLED(byte[] ledResponse, LEDToBusStop ledToBusStop) {
		OutputStream out = null;
		try {
			out = this.clientSocket.getOutputStream();
			out.write(hex2Byte(LED_INITIALIZE));
			out.flush();
			if(ledToBusStop.getLedType() == LEDToBusStop.CHINESE_LED){
				/**
				 * For Chinese LED this delay between the initialise data and the actual data is must
				 */
				try {
					Thread.sleep(500);
				} catch (InterruptedException e) {
					LOG.error(this.deviceID+" Thread half a second sleep interruption");
					return;
				}
			}
			out.write(ledResponse);
			out.flush();
		} catch (IOException e) {
			LOG.error(e);
		} catch (Exception e) {
			LOG.error(e);
		} finally {
			
		}
	}

	/**
	 * Get the LED clear data based on the type of the LED 
	 * @param ledToBusStop
	 * @return
	 */
	private byte[] getLEDClearData(LEDToBusStop ledToBusStop) {
		if(ledToBusStop.getLedType() == LEDToBusStop.CHINESE_LED){
			return hex2Byte(CLEAR_CHINESE_LED);
		} 
		// TODO : Add an else if more led types come up in future
		else {//if(ledToBusStop.getLedType() == LEDToBusStop.ARYA_LED){
			return hex2Byte(CLEAR_ARYA_LED);
		}
	}

	/**
	 * BitMap details of the "digits" for Chinese LED.
	 * For 2 digit format of ETA following are the bit map of
	 * 1) First digit of ETA in English at index 0
	 * 2) Second digit of ETA in English at index 1
	 * 3) First digit of ETA in Marathi at index 2
	 * 4) Second digit of ETA in Marathi at index 3
	 * 5) Arya LED bit map for the Numerals
	 * @return
	 */
	private HashMap<String, ArrayList<String>> getCompleteBitMap() {
		HashMap<String, ArrayList<String>> completeBitMap = new HashMap<String, ArrayList<String>>();
		ArrayList<String> bitMapString = new ArrayList<String>();

		bitMapString.add(0, "04015000000057000F00010001000001FFFFFFC7BBBBBBBBBBBBBBBBC7FFFFFF");
		bitMapString.add(1, "0401580000005F000F00010001000001FFFFFFC7BBBBBBBBBBBBBBBBC7FFFFFF");
		bitMapString.add(2, "04015000100057001F00010001000001FFFFFFFFE3DDBDBDDDE3FFFFFFFFFFFF");
		bitMapString.add(3, "0401580010005F001F00010001000001FFFFFFFFE3DDBDBDDDE3FFFFFFFFFFFF");
		bitMapString.add(4, "FFFFFFFFFFC19C9C949C9CC1FFFFFFFF");
		completeBitMap.put("0",bitMapString);

		bitMapString = new ArrayList<String>();;
		bitMapString.add(0, "04015000000057000F00010001000001FFFFFFFDF1FDFDFDFDFDFDFDFDFFFFFF");
		bitMapString.add(1, "0401580000005F000F00010001000001FFFFFFFDF1FDFDFDFDFDFDFDFDFFFFFF");
		bitMapString.add(2, "04015000100057001F00010001000001FFFFFFE3DBD9E1FDFDFDFDFFFFFFFFFF");
		bitMapString.add(3, "0401580010005F001F00010001000001FFFFFFE3DBD9E1FDFDFDFDFFFFFFFFFF");
		bitMapString.add(4, "FFFFFFFFFF81E7E7E7A7C7E7FFFFFFFF");
		completeBitMap.put("1",bitMapString);
		
		bitMapString = new ArrayList<String>();;
		bitMapString.add(0, "04015000000057000F00010001000001FFFFFFC7BBBBFBF7EFDFBFBF83FFFFFF");
		bitMapString.add(1, "0401580000005F000F00010001000001FFFFFFC7BBBBFBF7EFDFBFBF83FFFFFF");
		bitMapString.add(2, "04015000100057001F00010001000001FFFFFFE3DDFDFDCBC7F7FBFDFFFFFFFF");
		bitMapString.add(3, "0401580010005F001F00010001000001FFFFFFE3DDFDFDCBC7F7FBFDFFFFFFFF");
		bitMapString.add(4, "FFFFFFFFFF8199CFE3F999C3FFFFFFFF");
		completeBitMap.put("2",bitMapString);

		bitMapString = new ArrayList<String>();;
		bitMapString.add(0, "04015000000057000F00010001000001FFFFFFC7BBFBFBE7FBFBFBBBC7FFFFFF");
		bitMapString.add(1, "0401580000005F000F00010001000001FFFFFFC7BBFBFBE7FBFBFBBBC7FFFFFF");
		bitMapString.add(2, "04015000100057001F00010001000001FFFFFFE3DBFBE7F9FDC9C3F7F3FFFFFF");
		bitMapString.add(3, "0401580010005F001F00010001000001FFFFFFE3DBFBE7F9FDC9C3F7F3FFFFFF");
		bitMapString.add(4, "FFFFFFFFFFC399F9E3F999C3FFFFFFFF");
		completeBitMap.put("3",bitMapString);

		bitMapString = new ArrayList<String>();;
		bitMapString.add(0, "04015000000057000F00010001000001FFFFFFFBF3EBEBDBBB83FBFBFBFFFFFF");
		bitMapString.add(1, "0401580000005F000F00010001000001FFFFFFFBF3EBEBDBBB83FBFBFBFFFFFF");
		bitMapString.add(2, "04015000100057001F00010001000001FFFFFFDDFDD9E7EBDDDBE3FFFFFFFFFF");
		bitMapString.add(3, "0401580010005F001F00010001000001FFFFFFDDFDD9E7EBDDDBE3FFFFFFFFFF");
		bitMapString.add(4, "FFFFFFFFFFF0F98099C9E1F1FFFFFFFF");
		completeBitMap.put("4",bitMapString);

		bitMapString = new ArrayList<String>();;
		bitMapString.add(0, "04015000000057000F00010001000001FFFFFF83BFBFBF87BBFBFBBBC7FFFFFF");
		bitMapString.add(1, "0401580000005F000F00010001000001FFFFFF83BFBFBF87BBFBFBBBC7FFFFFF");
		bitMapString.add(2, "04015000100057001F00010001000001FFFFFFDFB9B9DBC1FDFDFDFDFFFFFFFF");
		bitMapString.add(3, "0401580010005F001F00010001000001FFFFFFDFB9B9DBC1FDFDFDFDFFFFFFFF");
		bitMapString.add(4, "FFFFFFFFFFC399F9F9839F81FFFFFFFF");
		completeBitMap.put("5",bitMapString);
				bitMapString = new ArrayList<String>();;

		bitMapString.add(0, "04015000000057000F00010001000001FFFFFFC7BBBFBFA79BBBBBBBC7FFFFFF");
		bitMapString.add(1, "0401580000005F000F00010001000001FFFFFFC7BBBFBFA79BBBBBBBC7FFFFFF");
		bitMapString.add(2, "04015000100057001F00010001000001FFFFFFC3DFDFE3EFDFC9E1FBFDFFFFFF");
		bitMapString.add(3, "0401580010005F001F00010001000001FFFFFFC3DFDFE3EFDFC9E1FBFDFFFFFF");
		bitMapString.add(4, "FFFFFFFFFFC39999839FCFE3FFFFFFFF");
		completeBitMap.put("6",bitMapString);
				bitMapString = new ArrayList<String>();;

		bitMapString.add(0, "04015000000057000F00010001000001FFFFFF83FBFBF7F7EFEFDFDFDFFFFFFF");
		bitMapString.add(1, "0401580000005F000F00010001000001FFFFFF83FBFBF7F7EFEFDFDFDFFFFFFF");
		bitMapString.add(2, "04015000100057001F00010001000001FFFFFFDFB1B5B5B5D9DDE3FFFFFFFFFF");
		bitMapString.add(3, "0401580010005F001F00010001000001FFFFFFDFB1B5B5B5D9DDE3FFFFFFFFFF");
		bitMapString.add(4, "FFFFFFFFFFE7E7E7F3739981FFFFFFFF");
		completeBitMap.put("7",bitMapString);
				bitMapString = new ArrayList<String>();;

		bitMapString.add(0, "04015000000057000F00010001000001FFFFFFC7BBBBBBC7BBBBBBBBC7FFFFFF");
		bitMapString.add(1, "0401580000005F000F00010001000001FFFFFFC7BBBBBBC7BBBBBBBBC7FFFFFF");
		bitMapString.add(2, "04015000100057001F00010001000001FFFFFFFBF7EFDFDFBFDDE3FFFFFFFFFF");
		bitMapString.add(3, "0401580010005F001F00010001000001FFFFFFFBF7EFDFDFBFDDE3FFFFFFFFFF");
		bitMapString.add(4, "FFFFFFFFFFC39999C39999C3FFFFFFFF");
		completeBitMap.put("8",bitMapString);
				bitMapString = new ArrayList<String>();;

		bitMapString.add(0, "04015000000057000F00010001000001FFFFFFC7BBBBBBB3CBFBFBBBC7FFFFFF");
		bitMapString.add(1, "0401580000005F000F00010001000001FFFFFFC7BBBBBBB3CBFBFBBBC7FFFFFF");
		bitMapString.add(2, "04015000100057001F00010001000001FFFFFFC3DBDBC3EFF7FBFDF9FFFFFFFF");
		bitMapString.add(3, "0401580010005F001F00010001000001FFFFFFC3DBDBC3EFF7FBFDF9FFFFFFFF");
		bitMapString.add(4, "FFFFFFFFFFC7E3E9C19999C3FFFFFFFF");
		completeBitMap.put("9",bitMapString);
		
		bitMapString = new ArrayList<String>();;
		bitMapString.add(0,"");
		bitMapString.add(1,"");
		bitMapString.add(2,"");
		bitMapString.add(3,"");
		bitMapString.add(4, "FFFFFFFFFF9999819999C3E7FFFFFFFF");
		completeBitMap.put("A",bitMapString);

		bitMapString = new ArrayList<String>();;
		bitMapString.add(0,"");
		bitMapString.add(1,"");
		bitMapString.add(2,"");
		bitMapString.add(3,"");
		bitMapString.add(4, "FFFFFFFFFF81CCCCC1CCCC81FFFFFFFF");
		completeBitMap.put("B",bitMapString);
		
		bitMapString = new ArrayList<String>();;
		bitMapString.add(0,"");
		bitMapString.add(1,"");
		bitMapString.add(2,"");
		bitMapString.add(3,"");
		bitMapString.add(4, "FFFFFFFFFFE1CC9F9F9FCCE1FFFFFFFF");
		completeBitMap.put("C",bitMapString);
		
		bitMapString = new ArrayList<String>();;
		bitMapString.add(0,"");
		bitMapString.add(1,"");
		bitMapString.add(2,"");
		bitMapString.add(3,"");
		bitMapString.add(4, "FFFFFFFFFF83C9CCCCCCC983FFFFFFFF");
		completeBitMap.put("D",bitMapString);
		
		bitMapString = new ArrayList<String>();;
		bitMapString.add(0,"");
		bitMapString.add(1,"");
		bitMapString.add(2,"");
		bitMapString.add(3,"");
		bitMapString.add(4, "FFFFFFFFFF80CECBC3CBCE80FFFFFFFF");
		completeBitMap.put("E",bitMapString);
		
		bitMapString = new ArrayList<String>();;
		bitMapString.add(0,"");
		bitMapString.add(1,"");
		bitMapString.add(2,"");
		bitMapString.add(3,"");
		bitMapString.add(4, "FFFFFFFFFF87CFCBC3CBCE80FFFFFFFF");
		completeBitMap.put("F",bitMapString);
		
		bitMapString = new ArrayList<String>();;
		bitMapString.add(0,"");
		bitMapString.add(1,"");
		bitMapString.add(2,"");
		bitMapString.add(3,"");
		bitMapString.add(4, "FFFFFFFFFFE0CC989F9FCCE1FFFFFFFF");
		completeBitMap.put("G",bitMapString);
		
		bitMapString = new ArrayList<String>();;
		bitMapString.add(0,"");
		bitMapString.add(1,"");
		bitMapString.add(2,"");
		bitMapString.add(3,"");
		bitMapString.add(4, "FFFFFFFFFF99999981999999FFFFFFFF");
		completeBitMap.put("H",bitMapString);
		
		bitMapString = new ArrayList<String>();;
		bitMapString.add(0,"");
		bitMapString.add(1,"");
		bitMapString.add(2,"");
		bitMapString.add(3,"");
		bitMapString.add(4, "FFFFFFFFFFC3E7E7E7E7E7C3FFFFFFFF");
		completeBitMap.put("I",bitMapString);
		
		bitMapString = new ArrayList<String>();;
		bitMapString.add(0,"");
		bitMapString.add(1,"");
		bitMapString.add(2,"");
		bitMapString.add(3,"");
		bitMapString.add(4, "FFFFFFFFFFC39999F9F9F9F0FFFFFFFF");
		completeBitMap.put("J",bitMapString);
		
		bitMapString = new ArrayList<String>();;
		bitMapString.add(0,"");
		bitMapString.add(1,"");
		bitMapString.add(2,"");
		bitMapString.add(3,"");
		bitMapString.add(4, "FFFFFFFFFF8CCCC9C3C9CC8CFFFFFFFF");
		completeBitMap.put("K",bitMapString);
		
		bitMapString = new ArrayList<String>();;
		bitMapString.add(0,"");
		bitMapString.add(1,"");
		bitMapString.add(2,"");
		bitMapString.add(3,"");
		bitMapString.add(4, "FFFFFFFFFF80CCCECFCFCF87FFFFFFFF");
		completeBitMap.put("L",bitMapString);
		
		bitMapString = new ArrayList<String>();;
		bitMapString.add(0,"");
		bitMapString.add(1,"");
		bitMapString.add(2,"");
		bitMapString.add(3,"");
		bitMapString.add(4, "FFFFFFFFFF9C9C948080889CFFFFFFFF");
		completeBitMap.put("M",bitMapString);
		
		bitMapString = new ArrayList<String>();;
		bitMapString.add(0,"");
		bitMapString.add(1,"");
		bitMapString.add(2,"");
		bitMapString.add(3,"");
		bitMapString.add(4, "FFFFFFFFFF9C9C9890848C9CFFFFFFFF");
		completeBitMap.put("N",bitMapString);
		
		bitMapString = new ArrayList<String>();;
		bitMapString.add(0,"");
		bitMapString.add(1,"");
		bitMapString.add(2,"");
		bitMapString.add(3,"");
		bitMapString.add(4, "FFFFFFFFFFC19C9C9C9C9CC1FFFFFFFF");
		completeBitMap.put("O",bitMapString);
		
		bitMapString = new ArrayList<String>();;
		bitMapString.add(0,"");
		bitMapString.add(1,"");
		bitMapString.add(2,"");
		bitMapString.add(3,"");
		bitMapString.add(4, "FFFFFFFFFF87CFCFC1CCCC81FFFFFFFF");
		completeBitMap.put("P",bitMapString);
		
		bitMapString = new ArrayList<String>();;
		bitMapString.add(0,"");
		bitMapString.add(1,"");
		bitMapString.add(2,"");
		bitMapString.add(3,"");
		bitMapString.add(4, "FFFFFFFFFFF1C391999999C3FFFFFFFF");
		completeBitMap.put("Q",bitMapString);
		
		bitMapString = new ArrayList<String>();;
		bitMapString.add(0,"");
		bitMapString.add(1,"");
		bitMapString.add(2,"");
		bitMapString.add(3,"");
		bitMapString.add(4, "FFFFFFFFFF8CCCC9C1CCCC81FFFFFFFF");
		completeBitMap.put("R",bitMapString);
		
		bitMapString = new ArrayList<String>();;
		bitMapString.add(0,"");
		bitMapString.add(1,"");
		bitMapString.add(2,"");
		bitMapString.add(3,"");
		bitMapString.add(4, "FFFFFFFFFFC399F3E7CF99C3FFFFFFFF");
		completeBitMap.put("S",bitMapString);
		
		bitMapString = new ArrayList<String>();;
		bitMapString.add(0,"");
		bitMapString.add(1,"");
		bitMapString.add(2,"");
		bitMapString.add(3,"");
		bitMapString.add(4, "FFFFFFFFFFC3E7E7E7E7A581FFFFFFFF");
		completeBitMap.put("T",bitMapString);
		
		bitMapString = new ArrayList<String>();;
		bitMapString.add(0,"");
		bitMapString.add(1,"");
		bitMapString.add(2,"");
		bitMapString.add(3,"");
		bitMapString.add(4, "FFFFFFFFFF81999999999999FFFFFFFF");
		completeBitMap.put("U",bitMapString);
		
		bitMapString = new ArrayList<String>();;
		bitMapString.add(0,"");
		bitMapString.add(1,"");
		bitMapString.add(2,"");
		bitMapString.add(3,"");
		bitMapString.add(4, "FFFFFFFFFFE7C39999999999FFFFFFFF");
		completeBitMap.put("V",bitMapString);
		
		bitMapString = new ArrayList<String>();;
		bitMapString.add(0,"");
		bitMapString.add(1,"");
		bitMapString.add(2,"");
		bitMapString.add(3,"");
		bitMapString.add(4, "FFFFFFFFFF9C8880949C9C9CFFFFFFFF");
		completeBitMap.put("W",bitMapString);
		
		bitMapString = new ArrayList<String>();;
		bitMapString.add(0,"");
		bitMapString.add(1,"");
		bitMapString.add(2,"");
		bitMapString.add(3,"");
		bitMapString.add(4, "FFFFFFFFFF9CC9E3E3C99C9CFFFFFFFF");
		completeBitMap.put("X",bitMapString);
		
		bitMapString = new ArrayList<String>();;
		bitMapString.add(0,"");
		bitMapString.add(1,"");
		bitMapString.add(2,"");
		bitMapString.add(3,"");
		bitMapString.add(4, "FFFFFFFFFFC3E7E7C3999999FFFFFFFF");
		completeBitMap.put("Y",bitMapString);
		
		bitMapString = new ArrayList<String>();;
		bitMapString.add(0,"");
		bitMapString.add(1,"");
		bitMapString.add(2,"");
		bitMapString.add(3,"");
		bitMapString.add(4, "FFFFFFFFFF80CCE6F3B99C80FFFFFFFF");
		completeBitMap.put("Z",bitMapString);
		
		bitMapString = new ArrayList<String>();;
		bitMapString.add(0,"");
		bitMapString.add(1,"");
		bitMapString.add(2,"");
		bitMapString.add(3,"");
		bitMapString.add(4, "FFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFF");
		completeBitMap.put(" ",bitMapString);

		return completeBitMap;
	}

	/**
	 * Process the heart beat response to the device to retain continuous data flow
	 * @param in
	 * @param outputStream
	 * @param ledToBusStop
	 * @throws IOException
	 */
	/*private void respondToHeartBeat(InputStream in, OutputStream outputStream, LEDToBusStop ledToBusStop) throws IOException {
		int j = 0;
		byte[] dataByteArray = new byte[HEADER_LENGTH];

		while(true){
			try{
				byte dataByte = (byte)in.read();
				if(ByteUtils.toHexAscii(dataByte).equalsIgnoreCase("A8")){
					dataByteArray[j++] = dataByte;
					LOG.debug(""+ByteUtils.toHexAscii(dataByte));
					while(j<(HEADER_LENGTH-1)){
						dataByte = (byte)in.read();
						if(!ByteUtils.toHexAscii(dataByte).equalsIgnoreCase("FF")){
							LOG.debug(""+ByteUtils.toHexAscii(dataByte));
							dataByteArray[j] = dataByte;
							j++;
						}
					}
					break;
				}
			} catch (Exception e){
				LOG.info(this.deviceID+" Error while reading heart beat");
				break;
			}
		}

		String hearBeatBack = ByteUtils.toHexAscii(getHeartBeatResponse(dataByteArray));
		outputStream.write(hex2Byte(hearBeatBack));
		outputStream.flush();
		if(ledToBusStop.getLedType() == LEDToBusStop.CHINESE_LED){
			outputStream.write(hex2Byte(TAIL));
			outputStream.flush();
		}
		LOG.info(this.deviceID+" Responded to the heart beat with : "+hearBeatBack);
	}*/

	private byte[] getHeartBeatResponse(byte[] dataByteArray) {
		dataByteArray[17] = (byte)03;
		byte crc = 0;
		for (int i = 0; i < dataByteArray.length - 1; i++) {
			crc += dataByteArray[i];
		}
		dataByteArray[19] = crc;

		return dataByteArray;
	}

	/**
	 * Utility method which returns the hex byte of the string
	 * @param str
	 * @return byte[]
	 */
	public static byte[] hex2Byte(String str)
	{
		byte[] bytes = new byte[str.length() / 2];
		for (int i = 0; i < bytes.length; i++){
			bytes[i] = (byte) Integer.parseInt(str.substring(2 * i, 2 * i + 2), 16);
		}
		return bytes;
	}

	/**
	 * 
	 * @param etaDisplayList
	 * @param completeBitMap
	 * @param ledType
	 * @return Bit map to displayed on PIS
	 */
	private String getAllLEDBitMapForTheRequestedStop(Vector<EtaDisplay> etaDisplayList, HashMap<String, 
			ArrayList<String>> completeBitMap, int ledType) {
		try{
			String allLEDBitMap = "";
			for (EtaDisplay etaDisplay : etaDisplayList) {
				Routes routeEntity = LoadRoutesDetails.getInstance().cacheRoute.get(etaDisplay.getRouteId());
				if(routeEntity == null){
					/**	Route not cached. Hence skip to next instance of ETA entity.*/
					continue;
				}

				/**
				 * For this entry of ETA do the following 
				 * 1) Get the bit maps of the route and destination in English and local language as well from the DB
				 * 2) Send two row data. a) detail in English. b) detail in Local language.
				 * 3) Update the ETA cache for the selected one with displayed status
				 */
				/*String englishRouteBitMap = routeEntity.getEnglishRouteBitMap();
			String localLanguageRouteBitMap = routeEntity.getLocalLanguageRouteBitMap();

			String englishDestinationBitMap = routeEntity.getEnglishDestinationBitMap();
			String localLanguageDestinationBitMap = routeEntity.getLocalLanguageDestinationBitMap();*/
				
				if(ledType == LEDToBusStop.CHINESE_LED){
					String englishRouteAndDestinationBitMap = routeEntity.getEnglishRouteAndDestinationBitMap();
					String hindiRouteAndDestinationBitMap = routeEntity.getLocalRouteAndDestinationBitMap();
					String arrivalMinutesBitMap = getArrivalMinutesBitMap(etaDisplay.getArrivalTime() + "",completeBitMap, ledType);
					allLEDBitMap = englishRouteAndDestinationBitMap + hindiRouteAndDestinationBitMap + arrivalMinutesBitMap + TAIL;
				} else if(ledType == LEDToBusStop.ARYA_LED){
					String routeBitMap = getRouteBitMap(routeEntity.getRouteName(), completeBitMap);
					String destinationBitMap = getDestinationBitMap(routeEntity);
					String arrivalMinutesBitMap = getArrivalMinutesBitMap(etaDisplay.getArrivalTime() + "",completeBitMap, ledType);
					allLEDBitMap = routeBitMap + destinationBitMap + arrivalMinutesBitMap;
				} else {
					LOG.error(this.deviceID+" : Unknown led type");
					break;
				}

				etaDisplay.setType(1);						// Changing the status of the ETA entry as displayed on LED
				updateTypeInEtaDisplyaCache(etaDisplay);
				LOG.info("LEDDataPush : Selected EtaDisplay Entity is : "+etaDisplay.toString());
				break;
			}
			
			return allLEDBitMap;
		} catch (Exception e){
			LOG.error(e);
			return "";
		}
	}

	/**
	 * Get the vehicle details to display on LED. 
	 * @param etaDisplayList
	 * @return
	 */
	private Vector<EtaDisplay> getListOfVehiclesLeftUndisplayedOnLED(Vector<EtaDisplay> etaDisplayList) {
		Vector<EtaDisplay> filterEtaDisplays = new Vector<EtaDisplay>();
		/*LOG.debug("LEDDataPush : UnSorted EtaDisplayList is");
		for(EtaDisplay etaDisplay : etaDisplayList)
			LOG.debug("LEDDataPush : "+etaDisplay.toString());*/

		//	Sort the etadisplay list on time
		Collections.sort(etaDisplayList, new Comparator<EtaDisplay>() {
			public int compare(EtaDisplay o1, EtaDisplay o2) {
				return o1.getArrivalTime().compareTo(o2.getArrivalTime());
			}});
		/*LOG.debug("LEDDataPush : Sorted EtaDisplayList is");
		for(EtaDisplay etaDisplay : etaDisplayList)
			LOG.debug("LEDDataPush : "+etaDisplay.toString());*/

		filterEtaDisplays = initializeFilterEtaDisplayListWithAlreadyDisplayedEntities(etaDisplayList);

		// Get the list of etadisplays (One per Route) having type = 0 
		filterEtaDisplays = filterEtaDisplays(etaDisplayList, filterEtaDisplays);

		if(filterEtaDisplays.size() != 0)
			return filterEtaDisplays;
		LOG.debug("LEDDataPush : All related entities have been displayed. Hence resetting the display status of the entities");
		resetTypeInEtaDisplayCache(etaDisplayList);

		filterEtaDisplays = filterEtaDisplays(etaDisplayList, filterEtaDisplays);

		/*LOG.debug("LEDDataPush : ReturnEtaDisplayList is ");
		for(EtaDisplay etaDisplay : filterEtaDisplays)
			LOG.debug("LEDDataPush : "+etaDisplay.toString());*/
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
				// TODO: Change Log level to debug once testing done .. 
				LOG.debug("LEDDataPush : Comparing EtadisplayLists \n "+etaDisplay.toString()+" with \n "+returnEtaDisplay.toString());
				if(returnEtaDisplay.getRouteId() == etaDisplay.getRouteId()){
					routeAddFlag = false;
				}
			}
			if(routeAddFlag && (etaDisplay.getType() == 0)){
				LOG.debug("LEDDataPush : Filtered etadisplay for display is : "+etaDisplay.toString());
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
		LOG.debug("LEDDataPush : Updating Type into Eta cache after selecting for display");
		ConcurrentHashMap<Long, ConcurrentHashMap<Long, EtaDisplay>> routeList = 
				LoadEtaDisplayDetails.getInstance().cacheEtaDisplayDetails.get(etaDisplay.getVehicleId());
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
	public String getRouteBitMap(String routeName, HashMap<String, ArrayList<String>> completeBitMap) {
		String routeBitMapString = "";
		routeName.trim();
		char[] routeNameCharacterArray = routeName.toCharArray();		
		for (char element : routeNameCharacterArray)
			routeBitMapString += completeBitMap.get(element+"").get(4);

		return routeBitMapString;
	}

	/**
	 * Formulating bitmap for destination.
	 * @param destination
	 * @return
	 */
	public String getDestinationBitMap(Routes routeEntity) {
		return ByteUtils.toHexAscii(routeEntity.getDestinationBitMap());
	}

	/**
	 * Formulating bitmap string for arrival minutes. 
	 * @param arrivalMinutes
	 * @param completeBitMap
	 * @param ledType 
	 * @param selectedEtaEntriesCount 
	 * @return
	 */
	public String getArrivalMinutesBitMap(String arrivalMinutes, HashMap<String, ArrayList<String>> completeBitMap, int ledType) {
		LOG.debug("LEDDataPush : Arrival Minutes :"+arrivalMinutes);
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

		if(ledType == LEDToBusStop.CHINESE_LED){
			firstDigit = completeBitMap.get(firstDigit).get(0)+completeBitMap.get(firstDigit).get(2);
			secondDigit = completeBitMap.get(secondDigit).get(1)+completeBitMap.get(secondDigit).get(3);
		}
		// TODO : Add an "else if" once a other led type comes in 
		// Right now assuming only two types of led on field
		else {
			firstDigit = completeBitMap.get(firstDigit).get(4);
			secondDigit = completeBitMap.get(secondDigit).get(4);
		}

		return firstDigit+secondDigit;

	}

	private long getBusStopIdAssociatedWithTheDevice(String deviceID) {
		LEDToBusStop ledToBusStop = LoadLEDToBusStopMap.getInstance().retrieve(deviceID);
		if(ledToBusStop != null){
			return ledToBusStop.getBusStopId();
		}
		return 0;
	}

	public void terminate(){
		isTerminate = true;
	}

}
