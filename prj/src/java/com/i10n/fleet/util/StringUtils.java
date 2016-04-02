package com.i10n.fleet.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Calendar;
import java.util.Date;

import com.i10n.db.entity.Address;
import com.i10n.db.entity.DateRange;
import com.i10n.db.entity.User;
import com.i10n.fleet.providers.mock.LogsDataProvider;
import com.i10n.fleet.web.utils.SessionUtils;


public class StringUtils {
	/**
	 * Returns the value alone from key value pair of format key-value
	 * 
	 * @param kvp
	 * @return
	 */
	public static String getValueFromKVP(String kvp) {
		String[] kvpArray = kvp.split("-");
		return kvpArray[kvpArray.length - 1];
	}

	/**
	 * Strip commas from strings
	 * 
	 * @param value
	 * @return
	 * @return
	 */
	public static String stripCommas(String value) {
		if (null == value) {
			return null;
		}
		value = value.trim();
		StringBuffer retVal = new StringBuffer();
		for (int itr = 0; itr < value.length(); itr++) {
			if (value.charAt(itr) != ',') {
				retVal.append(value.charAt(itr));
			}
		}
		return retVal.toString();
	}

	public static String stripSpace(String interval) {
		String intervals[] = interval.split(" ");
		String interv = intervals[0];
		return interv;
	}

	/**
	 * Depending upon the mode viz. today, month or week selection, populates
	 * the DateRange for report generation
	 * 
	 * @param mode
	 *            - Today / This Week / This Month defines the timeframe
	 * @param browserDate
	 *            - Used fpr timezone adjustment
	 * @return - DateRange with the range as per the selected mode
	 * 
	 */

	public static DateRange getDateForMode(Date browserDate, String mode) {
		Calendar browserCalDate = Calendar.getInstance();
		browserCalDate.setTime(browserDate);

		Calendar startCal = Calendar.getInstance();
		startCal.set(browserCalDate.get(Calendar.YEAR), browserCalDate.get(Calendar.MONTH), 
				browserCalDate.get(Calendar.DATE), 0, 0, 0);
		DateRange dateRange = new DateRange();


		Calendar endCal = Calendar.getInstance();
		endCal.set(browserCalDate.get(Calendar.YEAR), browserCalDate.get(Calendar.MONTH), 
				browserCalDate.get(Calendar.DATE), 23, 59, 0);

		if(mode.equalsIgnoreCase("Today")){
			// 	Add nothing
		} else if(mode.equalsIgnoreCase("This Week")){
			//	Get Sunday's date 
			int day = startCal.get(Calendar.DAY_OF_WEEK);
			startCal.add(Calendar.DATE, -(day-1));
		}

		dateRange.setStart(startCal.getTime());
		dateRange.setEnd(endCal.getTime());
		return dateRange;
	}

	public static boolean isBlank(final String str) {
		return (str != null) && (str.length() != 0);
	}

	
	public static String md5(final String str) {
		try {
			final MessageDigest md5 = MessageDigest.getInstance("MD5");
			md5.update(str.getBytes());
			return asHex(md5.digest());
		} catch (NoSuchAlgorithmException nsae) {
		}
		return "";
	}

	private static final char[] HEX_CHARS = { '0', '1', '2', '3', '4', '5',
		'6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f', };

	/**
	 * Turns array of bytes into string representing each byte as unsigned hex
	 * number.
	 * 
	 * @param hash
	 *            Array of bytes to convert to hex-string
	 * @return Generated hex string
	 */
	public static String asHex(final byte hash[]) {
		final char buf[] = new char[hash.length * 2];
		for (int i = 0, x = 0; i < hash.length; i++) {
			buf[x++] = HEX_CHARS[(hash[i] >>> 4) & 0xf];
			buf[x++] = HEX_CHARS[hash[i] & 0xf];
		}
		return new String(buf);
	}

	public static String getGSMSS(float gsmStrength) {
		String sStrength = "Normal";
		if(gsmStrength>25){
			sStrength="Strong";
		} else if(gsmStrength >=20 && gsmStrength<=25){
			sStrength="Normal";
		} else if(gsmStrength >=15 && gsmStrength<20){
			sStrength="Weak";
		} else{
			sStrength="Very Weak";
		}
		return sStrength;
	}

	public static String getGPSSS(float gpsStrength) {
		String sStrength = "Normal";

		if(gpsStrength>=0.7 && gpsStrength <= 1.0){
			sStrength="Strong";
		} else if(gpsStrength> 1.0 && gpsStrength<=2.0){
			sStrength="Normal";
		} else if(gpsStrength>=2.0 && gpsStrength<=5.0){
			sStrength="Weak";
		} else{
			sStrength="Very Weak";
		}
		return sStrength;
	}
	

	public static String getBatteryStrength(float sLevel) {
		String sStrength = "Normal";
		if(sLevel>4000){
			sStrength="Strong";
		} else if(sLevel >=3700 && sLevel<=4000) {
			sStrength="Normal";
		} else if(sLevel >=3500 && sLevel<3700){
			sStrength="Weak";
		} else{
			sStrength="Very Weak";
		}
		return sStrength;
	}

	
	/**
	 * This function is used to make logs of logged in user
	 * 
	 * @param action
	 *            -its the action by the user for ex-add driver,deleted driver
	 * 
	 */

	public static void insertLogs(String action) {
		LogsDataProvider log = new LogsDataProvider();
		User currentUser = SessionUtils.getCurrentlyLoggedInUser();
		Long uid = currentUser.getId();
		Long userid = Long.parseLong(StringUtils.stripCommas(uid + ""));
		Calendar cal = Calendar.getInstance();
		Date lastupdated = cal.getTime();
		String ip = SessionUtils.getRemoteip();
		StringBuffer actionbuffer = new StringBuffer();
		actionbuffer.append(currentUser.getLogin().toUpperCase()+ action.toUpperCase());
		log.getupDatedlogs(userid, lastupdated, ip, currentUser.getLogin(),actionbuffer.toString());
	}
	
	/**
	 * 
	 * This method tokenizes the string. This is implemented because J2ME does
	 * not have tokenizer as in J2SE
	 * 
	 * @param input
	 * @param delimiter
	 * @return
	 */

	public static String[] splitString(String input, String delimiter) {
		int stPos = 0, endPos = 0;
		int count = 0;
		while ((endPos = input.indexOf(delimiter, stPos)) != -1) {
			count++;
			stPos = endPos + 1;
		}
		if (stPos < input.length()) {
			count++;
			if (input.substring(stPos).indexOf("*") > 0) {
				count++;
			}
		}
		String[] retval = new String[count];
		stPos = 0;
		count = 0;
		while ((endPos = input.indexOf(delimiter, stPos)) != -1) {
			retval[count++] = input.substring(stPos, endPos);
			stPos = endPos + 1;
		}
		if (stPos < input.length()) {
			retval[count++] = input.substring(stPos);
		}
		if (input.substring(stPos).indexOf("*") > 0) {
			int indexOfAsterik = retval[count - 1].indexOf("*");
			retval[count - 1] = retval[count - 1].substring(0, indexOfAsterik);
			retval[count] = input.substring(indexOfAsterik + 1);
		}
		return retval;
	}
	

	/**
	 * Removes the single qoute from the string (Needed for address string) 
	 * @param value
	 * @return
	 */
	public static String removeSpecialCharacter(String value){
		if(value !=null){
			if(value.contains("'")){
				value = value.replaceAll("'", "");
			}	
		}
		return value;
	}
	/**
	 * formulating address 
	 * @param locationFetch
	 * @param vehicleId
	 * @param latitude
	 * @param longitude
	 * @return StringBuffer.
	 */
	public static StringBuffer formulateAddress(Address locationFetch, Long vehicleId, double latitude, double longitude) {
		StringBuffer location=new StringBuffer();
		if(locationFetch != null){
			if(ClientSpecificationsHandler.isTncscClient(vehicleId)){
				location.append(removeSpecialCharacter(locationFetch.toString()));
			}else{
				location.append(removeSpecialCharacter(locationFetch.toString()));
				location.append(" [Lat : ");
				location.append(latitude);
				location.append(", Lng : ");
				location.append(longitude);
				location.append("]");
			}
		}else{
			if(ClientSpecificationsHandler.isTncscClient(vehicleId)){
				location.append("Location not found");	
			}else{
				location.append("Location not found"+" [Lat : ");
				location.append(latitude);
				location.append(", Lng : ");
				location.append(longitude);
				location.append("]");	
			}
		}
		return location;
	}
	
	
	/**
	 * formulating address by driver id
	 * @param locationFetch
	 * @param driverId
	 * @param latitude
	 * @param longitude
	 * @return StringBuffer.
	 */
	public static StringBuffer formulateAddressByDriverId(Address locationFetch, Long driverId, double latitude, double longitude) {
		StringBuffer location=new StringBuffer();
		if(locationFetch != null){
			if(ClientSpecificationsHandler.isTncscClientByDriverId(driverId)){
				location.append(removeSpecialCharacter(locationFetch.toString()));
			}else{
				location.append(removeSpecialCharacter(locationFetch.toString()));
				location.append(" [Lat : ");
				location.append(latitude);
				location.append(", Lng : ");
				location.append(longitude);
				location.append("]");
			}
		}else{
			if(ClientSpecificationsHandler.isTncscClientByDriverId(driverId)){
				location.append("Location not found");	
			}else{
				location.append("Location not found"+" [Lat : ");
				location.append(latitude);
				location.append(", Lng : ");
				location.append(longitude);
				location.append("]");	
			}
		}
		return location;
	}
	
	/**
	 * formulatiing address fetch if disabled 
	 * @param vehicleId
	 * @param lattitude
	 * @param longitude
	 * @return stirngBuffer
	 */

	public static StringBuffer addressFetchDisabled(Long vehicleId, double lattitude, double longitude) {
		StringBuffer location=new StringBuffer();
		if(ClientSpecificationsHandler.isTncscClient(vehicleId)){
			location.append("location not found");
		}else{
			location.append("location not found");
			location.append(" [Lat : ");
			location.append(lattitude);
			location.append(", Lng : ");
			location.append(longitude);
			location.append("]");
		}
		return location;
	}
	
	/**
	 * formulating address fetch if disabled by driver id
	 * @param driverId
	 * @param lattitude
	 * @param longitude
	 * @return stirngBuffer
	 */

	public static StringBuffer addressFetchDisabledVyDriverId(Long driverId, double lattitude, double longitude) {
		StringBuffer location=new StringBuffer();
		if(ClientSpecificationsHandler.isTncscClientByDriverId(driverId)){
			location.append("location not found");
		}else{
			location.append("location not found");
			location.append(" [Lat : ");
			location.append(lattitude);
			location.append(", Lng : ");
			location.append(longitude);
			location.append("]");
		}
		return location;
	}
	
	
	/**
	 * Setting the start time to last 30 minutes for default time for the 
	 * violations page to get the last 30 minutes alerts only  
	 * @param browserDate
	 * @param mode
	 * @return
	 */
	public static DateRange getDateForModeViolationsPage(Date browserDate,
			String mode) {
		Calendar browserCalDate = Calendar.getInstance();
		browserCalDate.setTime(browserDate);

		Calendar startCal = Calendar.getInstance();
		
		DateRange dateRange = new DateRange();


		Calendar endCal = Calendar.getInstance();
		endCal.set(browserCalDate.get(Calendar.YEAR), browserCalDate.get(Calendar.MONTH), 
				browserCalDate.get(Calendar.DATE), 23, 59, 0);
		if(mode.equalsIgnoreCase("Today")){
			startCal.set(browserCalDate.get(Calendar.YEAR), browserCalDate.get(Calendar.MONTH), 
					browserCalDate.get(Calendar.DATE), browserCalDate.get(Calendar.HOUR),browserCalDate.get(Calendar.MINUTE)-30, 0);
			// 	Add nothing
		} else if(mode.equalsIgnoreCase("This Week")){
			//	Get Sunday's date 
			startCal.set(browserCalDate.get(Calendar.YEAR), browserCalDate.get(Calendar.MONTH), 
					browserCalDate.get(Calendar.DATE), 0, 0, 0);
			int day = startCal.get(Calendar.DAY_OF_WEEK);
			startCal.add(Calendar.DATE, -(day-1));
			}

		dateRange.setStart(startCal.getTime());
		dateRange.setEnd(endCal.getTime());
		return dateRange;
	}

}