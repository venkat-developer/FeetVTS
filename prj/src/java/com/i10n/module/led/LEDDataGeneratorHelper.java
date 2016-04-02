package com.i10n.module.led;

import java.util.Vector;

import com.i10n.db.entity.EtaDisplay;
import com.i10n.db.entity.Routes;


public abstract class LEDDataGeneratorHelper {

	public abstract byte[] mergeTwoBitMaps(String firstDigit, String secondDigit) ;
	public abstract byte[] mergeAllLEDBitMaps(byte[] routeBitMap, byte[] destinationBitMap, byte[] arrivalMinutesBitMap);
	public abstract Vector<EtaDisplay> selectEtaEntriesWrtStopId(long busStopId);
	public abstract byte[] getRouteBitMap(String routeName, String[][] completeBitMap,int languageFlag);
	public abstract byte[] getDestinationBitMap(Routes routeEntity,String[][] completeBitMap, int languageFlag);
	public abstract byte[] getArrivalMinutesBitMap(String arrivalMinutes, String[][] completeBitMap, int languageFlag) ;
	
}
