package com.i10n.mina.utils;

import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import org.apache.log4j.Logger;
import org.apache.mina.core.buffer.IoBuffer;

import com.i10n.fleet.container.BulkUpdateDataBean;
import com.i10n.fleet.container.GWTrackModuleDataBean;
import com.i10n.mina.codec.Constants;
import com.i10n.mina.codec.decoder.BytesPosition;
import com.i10n.mina.codec.decoder.ModuleDataException;
import com.i10n.teltonika.TeltonikaDataReader;

public class Utils {
	
	public static Logger LOG = Logger.getLogger(Utils.class);
	
	public static final int SECONDS_IN_WEEK = 7*24*60*60;
	
    /**
     * Converts the GPS TOW to IST time
     * 
     * @param tow
     *            THE GPS Time of Week (in Milliseconds)
     * @return A new Date object in IST
     */
	/**
	 * Converts the GPS TOW to IST time
	 * 
	 * @param tow
	 *            THE GPS Time of Week (in Milliseconds)
	 * @param year - Year sent by module. 
	 * @param month - Month sent by module.
	 * @param day - Day sent by module.
	 * @param weekNumber - week no. sent by module.
	 * @return A new Date object in IST
	 */
	public static Date convertTOWToIST(long tow, int year, int month, int day, int weekNumber) {
		// Get the Current Date in GMT Time Zone
		Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("GMT"));
		//Set the day sent by the module.
		calendar.set(year,month-1,day);
		//Compute seconds for packet spanning over weeks. 
		int weekSpan = weekNumber * SECONDS_IN_WEEK;
		// Set the calendar for the first day of the week
		int i = calendar.get(Calendar.DAY_OF_WEEK);

		calendar.add(Calendar.DAY_OF_MONTH, -(i - 1));
		calendar.set(Calendar.AM_PM, Calendar.AM);
		calendar.set(Calendar.HOUR, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		// The TOW field can have a max value of 521999000. Dividing this by
		// 1000 falls with in the range of Java integer
		int towSeconds = (int) (tow / 1000);
		calendar.add(Calendar.SECOND, towSeconds + weekSpan);

		// Convert TOW in GMT to IST
		DateFormat istFormat = DateFormat.getDateTimeInstance();
		TimeZone istTime = TimeZone.getTimeZone("IST");
		istFormat.setTimeZone(istTime);
		istFormat.format(calendar.getTime());
		Date istDate = null;
		try {
			istDate = istFormat.parse(istFormat.format(calendar.getTime()));
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return istDate;
	}

    /**
     * Converts the GPS TOW to IST time
     * 
     * @param tow
     *            THE GPS Time of Week (in Milliseconds)
     * @return A new Date object in IST
     */
    @SuppressWarnings("deprecation")
	public static Date convertTOWToIST(long tow) {
    	// Get the Current Date in GMT Time Zone
		Calendar c = Calendar.getInstance(TimeZone.getTimeZone("GMT"));
		// Set the calendar for the first day of the week
		int i = c.get(Calendar.DAY_OF_WEEK);
		c.add(Calendar.DAY_OF_MONTH, -(i - 1));
		c.set(Calendar.AM_PM, Calendar.AM);
		c.set(Calendar.HOUR, 0);
		c.set(Calendar.MINUTE, 0);
		c.set(Calendar.SECOND, 0);
		// The TOW field can have a max value of 521999000. Dividing this by
		// 1000 falls with in the range of Java integer
		int towSeconds = (int) (tow / 1000);
		c.add(Calendar.SECOND, towSeconds );
		
		// Convert TOW in GMT to IST
		DateFormat istFormat = DateFormat.getDateTimeInstance();	
		TimeZone istTime = TimeZone.getTimeZone("IST");		
		istFormat.setTimeZone(istTime);		
		istFormat.format(c.getTime());
		Date istDate = new Date(istFormat.format(c.getTime()));
		return istDate;
    }
    

	/**
	 * Java can handle only network byte order or high endian in a stream, not little endian. Hence this function
	 * 
	 * @param bp
	 * @param offset
	 * @param dis
	 * @return
	 * @throws IOException
	 */
	public static int processLittleEndianInt(BytesPosition bp, int offset, IoBuffer dis) throws IOException {
		if (bp.getLength() != 4) {
			throw new IOException("An int is 4 bytes long, but the passed value is [" + bp.getLength() + "] long");
		}
		int[] temp = new int[bp.getLength()];
		int t = 0;
		for (int i = 0; i < bp.getLength(); i++) {
			temp[i] = unsigned(dis.get());
		}
		for (int i = bp.getLength() - 1; i >= 0; i--) {
			int k = temp[i];
			k <<= i * 8;
			t += k;
		}
		return t;
	}
	
	  
    /**
     * Unsigned Conversion
     * @param b
     * @return
     */
    public static int unsigned(byte b) {
		return b & 0xFF;
	}
    
    

	public static final byte[] processintToByteArray(int value) {
		return new byte[] {
				(byte)(value >>> 24),
				(byte)(value >>> 16),
				(byte)(value >>> 8),
				(byte)value};
	}

	/**
	 * Java can handle only network byte order or high endian in a stream, not little endian. Hence this function
	 * 
	 * We return a long variable from this function even though we are processing an Unsigned int. This is needed because Java doesn't
	 * understand unsigned int and hence there is a chance of possible data overflow if we use Signed int[Java] to store
	 * the value of Unsigned int[C]. So a data type with higher capacity than int needs to be used and hence we used long
	 * @param bp
	 * @param offset
	 * @param dis
	 * @return
	 * @throws IOException
	 */
	public static long processLittleEndianUnsignedInt(BytesPosition bp, int offset, IoBuffer dis) throws IOException {
		if (bp.getLength() != 4) {
			throw new IOException("An int is 4 bytes long, but the passed value is [" + bp.getLength() + "] long");
		}
		long[] temp = new long[bp.getLength()];
		long t = 0;
		for (int i = 0; i < bp.getLength(); i++) {
			temp[i] = unsigned(dis.get());
		}
		for (int i = bp.getLength() - 1; i >= 0; i--) {
			long k = temp[i];
			k <<= i * 8;
			t += k;
		}
		return t;
	}
	
	/**
	 * Java can handle only network byte order or high endian in a stream, not little endian. Hence this function
	 * 
	 * We return an int variable from this function even though we are processing an Unsigned Short. This is needed because Java doesn't
	 * understand unsigned short and hence there is a chance of possible data overflow if we use Signed Short[Java] to store
	 * the value of Unsigned Short[C]. So a data type with higher capacity than Short needs to be used and hence we used int
	 * @param bp
	 * @param offset
	 * @param dis
	 * @return
	 * @throws IOException
	 */
	public static int processLittleEndianUnsignedShort(BytesPosition bp, int offset, IoBuffer dis)
	throws IOException {
		if (bp.getLength() != 2) {
			throw new IOException("A short is 2 bytes long, but the passed value is [" + bp.getLength() + "] long");
		}
		int[] temp = new int[bp.getLength()];
		int t = 0;
		for (int i = 0; i < bp.getLength(); i++) {
			temp[i] = unsigned(dis.get());
		}
		for (int i = bp.getLength() - 1; i >= 0; i--) {
			int k = temp[i];
			k <<= i * 8;
			t += k;
		}
		return t;
	}

	

	public static String processString(BytesPosition bp, int offset, IoBuffer dis) throws IOException {
		LOG.debug("V5 testing : "+bp+" offset "+offset+" dis "+dis.remaining());
		StringBuilder value = new StringBuilder();
		int indexOfLastValue = offset + bp.getLocation() + bp.getLength();
		for (int i = offset + bp.getLocation(); i < indexOfLastValue; i++) {
			value.append((char) dis.get());
		}
		LOG.debug("V5 testing Final IMEI "+value.toString());
		return value.toString();
	}
	
	public static int getBulkLength(int firmwareVersion){
		if(Utils.isNewVersionModule(firmwareVersion)){
			return Constants.POS_LATITUDE.getLength()
			+ Constants.POS_LONGITUDE.getLength() + Constants.POS_DELTA_DISTANCE.getLength()
			+ Constants.POS_TOW.getLength() + Constants.POS_WEEK_NUMBER.getLength()
			+ Constants.POS_SEQUENCE_NUMBER.getLength();
		}
		else if(Utils.isAndroidFirmware(firmwareVersion)){		// For Android GWtrack Application
			return Constants.POS_LATITUDE.getLength() + Constants.POS_LONGITUDE.getLength()
			+ Constants.POS_DELTA_DISTANCE.getLength() + Constants.POS_ANALOGUE_1_FUEL_DATA.getLength() + 8;
		}else if(Utils.isOldVersionModule(firmwareVersion) || Utils.isVersionV5_6(firmwareVersion) || Utils.isVersionV5_101(firmwareVersion)){  // For V4 with version number 6
			return Constants.POS_LATITUDE.getLength() + Constants.POS_LONGITUDE.getLength()
			+ Constants.POS_DELTA_DISTANCE.getLength() + Constants.POS_ANALOGUE_1_FUEL_DATA.getLength() + Constants.POS_TOW.getLength();
		}else if(Utils.isVersionV5_8(firmwareVersion)){  //For V5 with module version 8 
			return Constants.POS_LATITUDE.getLength() + Constants.POS_LONGITUDE.getLength()
			+ Constants.POS_DELTA_DISTANCE.getLength() + Constants.POS_ANALOGUE_1_FUEL_DATA.getLength() + Constants.POS_TOW.getLength()+4;
		}
		return 0;
	}

	public static int getNonBulkLength(int firmwareVersion){
		if(Utils.isNewVersionModule(firmwareVersion)){
			return Constants.POS_COUNT.getLength()
			+ Constants.POS_GPS_SIGNAL.getLength() + Constants.POS_DIRECTION.getLength()
			+ Constants.POS_GSM_SIGNAL.getLength()
			+ Constants.POS_TOTAL_DATA_PACKETS_SENT.getLength()
			+ Constants.POS_SUCCESSFUL_DATA_PACKETS_SENT.getLength()
			+ Constants.POS_BATTERY_VOLTAGE.getLength()
			+ Constants.POS_CUMULATIVE_DISTANCE.getLength() + Constants.POS_SPEED.getLength()
			+ Constants.POS_ANALOGUE_2.getLength() + Constants.POS_ANALOGUE_3.getLength()
			+ Constants.POS_ERROR.getLength() + Constants.POS_LAC.getLength() + Constants.POS_CID.getLength()
			+ Constants.POS_HEALTH_DATA.getLength() + Constants.POS_VERSION.getLength()
			+ Constants.POS_YEAR.getLength() + Constants.POS_MONTH.getLength()
			+ Constants.POS_DAY.getLength() + Constants.POS_IMEI.getLength() + Constants.POS_CRC.getLength();
		}
		else{
			return Constants.POS_COUNT.getLength() + Constants.POS_GPS_SIGNAL.getLength()
			+ Constants.POS_DIRECTION.getLength() + Constants.POS_GSM_SIGNAL.getLength() + Constants.POS_TOTAL_DATA_PACKETS_SENT.getLength()
			+ Constants.POS_SUCCESSFUL_DATA_PACKETS_SENT.getLength() + Constants.POS_BATTERY_VOLTAGE.getLength()
			+ Constants.POS_CUMULATIVE_DISTANCE.getLength() + Constants.POS_SPEED.getLength() + Constants.POS_ANALOGUE_2.getLength()
			+ Constants.POS_ANALOGUE_3.getLength() + Constants.POS_ERROR.getLength() + Constants.POS_LAC.getLength() + Constants.POS_CID.getLength()
			+ Constants.POS_HEALTH_DATA.getLength() + Constants.POS_VERSION.getLength() + Constants.POS_IMEI.getLength() + Constants.POS_CRC.getLength();
		}
	}
	
	
	/**
	 * Computes expected bytes in this packet
	 * @param pktLength
	 * @param firmwareVersion
	 * @return
	 */
	public static int computeExpectedBytes (int pktLength, int firmwareVersion) {
		int bulkPortionLength = 0;
		int nonBulkPortionLength = 0;
		bulkPortionLength = getBulkLength(firmwareVersion);
		nonBulkPortionLength = getNonBulkLength(firmwareVersion);
		return bulkPortionLength * pktLength + nonBulkPortionLength;
	}


	/**
	 * Tests for the appropriate module data length
	 * 
	 * @return
	 * @throws ModuleDataException
	 */
	public static boolean checkCRC(int pktBytes, byte[] bytesFromModules, int firmwareVersion) throws ModuleDataException {

		int crcCheck;
			
		if (bytesFromModules.length < 2) {
			throw new ModuleDataException("", ModuleDataException.ErrorCode.BYTE_LENGTH_LESSER);
		}

		crcCheck = bytesFromModules[0];
		for (int i = 1; i < pktBytes; i++) {
			crcCheck ^= bytesFromModules[i];
		}

		if (crcCheck != bytesFromModules[bytesFromModules.length - 1]) {
			LOG.error("Checksum error encountered");
			return false;
		}
		return true;
	}
	
	/**
	 * Compute Two powers. These variables are used for extracting individual bits from a byte
	 */
	public static final double TWO_POW_ZERO = Math.pow(2,0);
	public static final double TWO_POW_ONE = Math.pow(2,1);
	public static final double TWO_POW_TWO = Math.pow(2,2);
	public static final double TWO_POW_THREE = Math.pow(2,3);
	public static final double TWO_POW_FOUR = Math.pow(2,4);
	public static final double TWO_POW_FIVE = Math.pow(2,5);
	public static final double TWO_POW_SIX = Math.pow(2,6);
	public static final double TWO_POW_SEVEN = Math.pow(2,7);
	
	/**
	 * Process the status field sent by the Module and sets the fields corresponsdingly in GWTrackModuleDataBean
	 * 
	 * @param moduleStatus
	 *            The Status byte
	 * @param moduleDataBean
	 *            The GWTrackModuleDataBean object
	 * @param firmwareVersion           
	 * 				FirmwareVersion used to determine how to decode the data
	 */
	public static void processModuleStatus(byte moduleStatus, GWTrackModuleDataBean moduleDataBean, int firmwareVersion) {

		if ((moduleStatus & (int) TWO_POW_SEVEN) != 0) {
			moduleDataBean.setPingFlag(Boolean.TRUE);
		} else {
			moduleDataBean.setPingFlag(Boolean.FALSE);
		}

		if ((moduleStatus & (int) TWO_POW_SIX) != 0) {
			moduleDataBean.setChargerConnected(Boolean.TRUE);
		} else {
			moduleDataBean.setChargerConnected(Boolean.FALSE);
		}

		if ((moduleStatus & (int) TWO_POW_FIVE) != 0) {
			moduleDataBean.setMasterHardwareLevelRestart(Boolean.TRUE);
		} else {
			moduleDataBean.setMasterHardwareLevelRestart(Boolean.FALSE);
		}

		if ((moduleStatus & (int) TWO_POW_FOUR) != 0) {
			moduleDataBean.setModuleCodeLevelRestart(Boolean.TRUE);
		} else {
			moduleDataBean.setModuleCodeLevelRestart(Boolean.FALSE);
		}

		if(isNewVersionModule(firmwareVersion)){
			if ((moduleStatus & (int) TWO_POW_THREE) != 0) {		// Checking for moretofollow bit status for DP modules
				LOG.debug("New version module. More to follow status is true");
				moduleDataBean.setMoreToFollow(true);
			} else {
				LOG.debug("New version module. More to follow status is false");
				moduleDataBean.setMoreToFollow(false);
				moduleDataBean.setDigitalInput3(new String("0"));
			}
		}else{
			if ((moduleStatus & (int) TWO_POW_THREE) != 0) {
				moduleDataBean.setDigitalInput3(new String("1"));
			} else {
				moduleDataBean.setDigitalInput3(new String("0"));
			}
		}

		if ((moduleStatus & (int) TWO_POW_TWO) != 0) {
			moduleDataBean.setDigitalInput2(new String("1"));
		} else {
			moduleDataBean.setDigitalInput2(new String("0"));
		}

		if ((moduleStatus & (int) TWO_POW_ONE) != 0) {
			moduleDataBean.setDigitalInput1(new String("1"));
		} else {
			moduleDataBean.setDigitalInput1(new String("0"));
		}

		if ((moduleStatus & (int) TWO_POW_ZERO) != 0) {
			moduleDataBean.setPanicData(Boolean.TRUE);
		} else {
			moduleDataBean.setPanicData(Boolean.FALSE);
		}

	}


	/**
	 * Sorts the BulkData portion of GWTrackDataBean according to occurredAt with LeastRecent data in the first Position and MostRecent data 
	 * in the last position
	 * @param gwTrackData
	 */
	public static void sortBulkUpdateData(ArrayList<BulkUpdateDataBean> bulkData){

		BulkUpdateDataBean[] ba = bulkData.toArray(new BulkUpdateDataBean[0]);
		Arrays.sort(ba);

		bulkData.clear();

		for(int i=0;i<ba.length;i++){
			bulkData.add(ba[i]);
		}

	}
	
	/**
	 * Checking for the DP type of module using firmware version number 
	 * Version - 4,5 
	 * @param firmwareVersion
	 * @return
	 */
	public static boolean isNewVersionModule(int firmwareVersion){
		if(firmwareVersion == 4 || firmwareVersion == 5){
			return true;
		}else{
			return false;
		}
	}

	/**
	 * Checking for Teltonika Device version 
	 * @param firmwareVersion
	 * @return
	 */
	public static boolean isTeltonikaDevice(int firmwareVersion) {
		if(firmwareVersion == TeltonikaDataReader.TELTONIKA_FIRMWARE_VERSION)
			return true;
		return false;
	}
	
	/**
	 * Checking for the BP or KP type of module using firmware version number.
	 * version -1,2,3.
	 * @param firmwareVersion
	 * @return
	 */
	public static boolean isOldVersionModule(int firmwareVersion){
		if(firmwareVersion == 1 || firmwareVersion == 2 || firmwareVersion == 3){
			return true;
		}else{
			return false;
		}
	}
	/**
	 * Basic V5 version modules. Version-6
	 * @param firmwareVersion
	 * @return
	 */
	public static boolean isVersionV5_6(int firmwareVersion){
		if(firmwareVersion == 6){
			return true;
		}else{
			return false;
		}
	}
	
	/**
	 * Checking for Android gwtrack Application 
	 * version 10,11,13
	 * @param firmwareVersion
	 * @return
	 */
	public static boolean isAndroidFirmware(int firmwareVersion) {
		if(firmwareVersion == 10 || firmwareVersion == 11 || firmwareVersion == 13 || firmwareVersion ==50){
			return true;
		}
		return false;
	}

	/**
	 * Rounding off the double value to two digits after decimal point 
	 * @param maxspeed
	 * @return 
	 * Double value rounded to two digits after decimal point
	 */
	public static double doubleForDisplay(double value) {
		value *= 100;
		int val = (int)value;
		return (val/100);
	}
	
	/** 
	 * Time format is different from V5_6 to V5_8
	 * Version -8
	 * @param firmwareVersion
	 * @return
	 */
	public static boolean isVersionV5_8(int firmwareVersion) {
		if(firmwareVersion == 8){
			return true;
		}
		return false;
	}

	/**
	 * GVK Rajasthan bug fix version
	 * 1) Button data is sent as analog value in bulk data
	 * 2) GPS 0 packets are also sent
	 * 3) Bulk size reduction to 25
	 * @param firmwareVersion
	 * @return
	 */
	public static boolean isVersionV5_101(int firmwareVersion) {
		if(firmwareVersion == 101){
			return true;
		}
		return false;
	}
	
}
