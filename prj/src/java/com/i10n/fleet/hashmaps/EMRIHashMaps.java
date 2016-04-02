package com.i10n.fleet.hashmaps;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.log4j.Logger;

/**
 * 
 * @author Dharmaraju V
 *
 */
public class EMRIHashMaps {

	private Logger LOG = Logger.getLogger(EMRIHashMaps.class);

	private static EMRIHashMaps _instance = null;


	public enum ButtonCode{
		START_OF_TRIP (1),
		REACHED_THE_SCENE(2),
		START_FROM_SCENE(3),
		REACHED_HOSPITAL(4),
		START_FROM_HOSPITAL(5),
		BACK_TO_BASE(6),
		REFUEL(7),
		DEMO(8),
		ACCIDENT(9),
		MAINTAINANCE(10),
		BREAK_DOWN(11),
		END_OF_TRIP(12),
		TEMP_BASE_STATION(13),
		BACK_TO_REGULAR_BASE(14),
		SOS_1(15),
		SOS_2(16),
		F1(17),
		F2(18),
		F3(19),
		F4(20),
		IDLE(88),
		BATTERY_DISCONNECTED(89), 
		GPRS_TO_NON_GPRS(90),
		NON_GPRS_TO_GPRS(91),
		BATTERY_CONNECTED(92),
		ANTENNA_CONNECTED(93),
		ANTENNA_DISCONNECTED(94),
		UNAUTHORIZED_START(99),
		INVALID(9999),
		NO_BUTTON_PRESSED(0);

		private int value;
		private static final Map<Integer, ButtonCode> lookup = new HashMap<Integer, ButtonCode>();

		private ButtonCode(int value) {
			this.value = value;
		}

		public int getValue() {
			return value;
		}

		public static ButtonCode get(int value) {
			if(lookup.get(value) == null ){
				return INVALID;
			}else{
				return lookup.get(value);
			}
		}

		static {
			for (ButtonCode s : EnumSet.allOf(ButtonCode.class)){
				lookup.put(s.getValue(), s);
			}
		}
	}

	public static EMRIHashMaps getInstance(){
		if(_instance == null){
			_instance = new EMRIHashMaps();
		}
		return _instance;
	}

	/**
	 * Mapping IMEI with the list of ButtonId corresponding to that vehicle
	 * 
	 *     ButtonId 								Type Value
	 *     
	 *     1										Start of Trip
	 *     2										Reached the Scene
	 *     3										Start from Scene
	 *     4										Reached Hospital
	 *     5										Start from Hospital
	 *     6										Back to Base
	 *     7										Refuel
	 *     8										Demo
	 *     9										Accident
	 *     10										Maintenance
	 *     11										BreakDown
	 *     12										End
	 *     13										Temp base station
	 *     14										Back to regular base
	 *     15										Sos1
	 *     16										Sos2
	 *     17										F1
	 *     18										F2
	 *     19										F3
	 *     20										F4
	 *     88										Idle
	 *     89										Battery Disconnected
	 *     90										GPRS to Non-GPRS
	 *     91										Non-GPRS to GPRS
	 *     92										Battery Connected
	 *     93										Antenna Connected
	 *     94										Antenna Disconnected
	 *     99										Unauthorized Start
	 */
	public static ConcurrentHashMap<String, Vector<Integer>> simToButtonListMap = new ConcurrentHashMap<String, Vector<Integer>>();

	public void loadHashMaps() {
		loadSimToButtonListMap();

	}

	private void loadSimToButtonListMap() {
		LOG.debug("");
	}


}
