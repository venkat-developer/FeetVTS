package com.i10n.fleet.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Time;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import org.apache.log4j.Logger;

import com.i10n.db.entity.DateRange;
import com.i10n.db.entity.LiveVehicleStatus;

public class DateUtils {
	private static Logger LOG = Logger.getLogger(DateUtils.class);

	public static String getFormattedDateWithoutTime(Date date) {
		return new SimpleDateFormat("yyyy-MM-dd").format(date);
	}
	public static String convertJavaDateToSQLDate(Date date){
		return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(date);
	}
	public static String convertJavaDateToJsDate(Date date){
		return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(date);
	}
	/**
	 * get time difference between end date to start date and formulate the appropriate format.
	 * @param startDate
	 * @param endDate
	 * @return
	 */
	public static String getDateDiff(Date startDate, Date endDate) {
		Calendar startCal=Calendar.getInstance();
		startCal.setTime(startDate);
		Calendar endCal=Calendar.getInstance();
		endCal.setTime(endDate);
		return formatTimeDifference(endCal.getTimeInMillis(),startCal.getTimeInMillis());

	}
	/**
	 * Formulating time in DD days,XX hours,XX minutes,XX seconds.
	 * @param endTimeInMillis
	 * @param startTimeInMillis
	 * @return formated time string.
	 */
	public static String formatTimeDifference(long endTimeInMillis,
			long startTimeInMillis) {
		long diff = endTimeInMillis-startTimeInMillis;
		long diffSeconds = ((diff % (1000*60*60)) % (1000*60)) / 1000;
		long diffMinutes = (diff % (1000*60*60)) / (1000*60);
		long diffHours = diff / (60 * 60 * 1000);
		long diffDays = diff / (24 * 60 * 60 * 1000);
		return formulateTime(diffDays,diffHours,diffMinutes,diffSeconds).toString();
	}

	private static StringBuffer formulateTime(long diffDays, long diffHours,
			long diffMinutes, long diffSeconds) {
		StringBuffer updatedStatus = new StringBuffer();
		long hours=0L;
		if(diffHours>=24){
			hours=(diffHours%24);
		}else{
			hours=diffHours;
		}
		if(diffDays!=0){
			updatedStatus.append(diffDays);
			if(diffDays>1){
				updatedStatus.append(" days ");	
			}else{
				updatedStatus.append(" day ");
			}
		}
		if(hours!=0){
			updatedStatus.append(hours);
			if(hours>1){
				updatedStatus.append(" hours ");	
			}else{
				updatedStatus.append(" hour ");
			}

		}
		if(diffMinutes!=0){
			updatedStatus.append(diffMinutes);
			if(diffMinutes>1){
				updatedStatus.append(" minutes ");	
			}else{
				updatedStatus.append(" minute ");
			}

		}
		if(diffSeconds!=0){
			updatedStatus.append(diffSeconds);
			if(diffSeconds>1){
				updatedStatus.append(" seconds.");	
			}else{
				updatedStatus.append(" second.");
			}
		}

		return updatedStatus;
	}
	public static long adjustToClientTimeInMilliSeconds(String clientZoneId, Date dbTime) {
		LOG.debug("Databse time is "+dbTime);
		SimpleDateFormat df = new SimpleDateFormat("EEE dd MMM yyyy HH:mm:ss", Locale.US);
		df.setTimeZone(TimeZone.getTimeZone(clientZoneId));
		String  actualTimeStr= df.format(dbTime);
		try {
			LOG.debug("In try Catch Method "+df.parse(actualTimeStr));
			return df.parse(actualTimeStr).getTime();
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			LOG.debug("Parse Exceptionnnn",e);
		}
		return dbTime.getTime();
	}
	public static String adjustToClientTime(String clientZoneId, Date dbTime) {
		SimpleDateFormat df = new SimpleDateFormat("EEE dd MMM yyyy HH:mm:ss", Locale.US);
		df.setTimeZone(TimeZone.getTimeZone(clientZoneId));
		String actualTimeStr = df.format(dbTime);
		return actualTimeStr;
	}

	public static Date adjustToLocalTime(Date dbTime, Date browserTime) {
		Date currentServerTime = new Date();
		long timeDiff = browserTime.getTime() - currentServerTime.getTime();
		Date actualTime = new Date(dbTime.getTime() + timeDiff);
		return actualTime;
	}

	public static Date adjustToServerTime(Date reqTime, Date browserTime) {
		Date currentServerTime = new Date();
		long timeDiff = browserTime.getTime() - currentServerTime.getTime();
		Date actualTime = new Date(browserTime.getTime());
		actualTime.setTime(reqTime.getTime() - timeDiff);
		return actualTime;
	}

	public static String sha512(String str) throws NoSuchAlgorithmException {
		MessageDigest sha512 = MessageDigest.getInstance("SHA-512");
		sha512.update(str.getBytes());
		return StringUtils.asHex(sha512.digest());
	}

	@SuppressWarnings("deprecation")
	public static Time convertLocalTimeToRailwayTime(String strtime){
		String hourstrtime = strtime.substring(0,strtime.length()-9);
		String minstrtime = strtime.substring(hourstrtime.length()+1,strtime.length()-6);
		String secstrtime = strtime.substring(strtime.length()-5,strtime.length()-3);
		String modestrtime = strtime.substring(strtime.length()-2,strtime.length());

		Time time = null;

		int inthour = Integer.parseInt(hourstrtime.trim());
		int intmin = Integer.parseInt(minstrtime.trim());
		int intsec = Integer.parseInt(secstrtime.trim());

		if ((modestrtime.trim().equals("AM")) && inthour == 12){
			inthour = 00;
		}
		if (modestrtime.trim().equals("PM")){
			switch(inthour){
			case 1:
				inthour = 13;
				break;
			case 2:
				inthour = 14;
				break;
			case 3:
				inthour = 15;
				break;
			case 4:
				inthour = 16;
				break;
			case 5:
				inthour = 17;
				break;
			case 6:
				inthour = 18;
				break;
			case 7:
				inthour = 19;
				break;
			case 8:
				inthour = 20;
				break;
			case 9:
				inthour = 21;
				break;
			case 10:
				inthour = 22;
				break;
			case 11:
				inthour = 23;
				break;
			default:
				break;
			}
		}
		time = new Time(inthour, intmin, intsec);
		return time;
	}
	/**
	 * Returnig mode of report to be generate based on request .
	 * i.e today/this week /custom
	 * @param localTime
	 * @param startdate
	 * @param enddate
	 * @return dateRange
	 */
	@SuppressWarnings("deprecation")
	public static DateRange getModeOfReport(String localTime, String startdate,
			String enddate) {
		DateRange dateRange=new DateRange();
		String mode="";
		Date clientTime = new Date(localTime);
		// Mode calculation
		Date startDate = new Date(startdate);
		Date endDate = new Date(enddate);
		long endDateInLong = endDate.getTime();
		long startdateInLong = startDate.getTime();
		long diff = endDateInLong - startdateInLong;
		long diffDays = diff / (24 * 60 * 60 * 1000);
		Calendar startCal = Calendar.getInstance();
		SimpleDateFormat sdf = new java.text.SimpleDateFormat( "dd-MMM-yy" );
		int day = startCal.get(Calendar.DAY_OF_WEEK);
		startCal.add(Calendar.DATE, -(day-1));
		Date startDateofThisWeek = new Date(sdf.format(startCal.getTime()));
		int flag = startDate.compareTo(startDateofThisWeek);
		
		if (diff == 86399000) {
			mode = "Today";
		} else if (diffDays == 6 && flag == 0) {
			mode = "This Week";
		} else {
			mode = "Custom";
		}

		// depending upon mode get the dateRange
		if (!mode.equalsIgnoreCase("Custom")) {
			dateRange = StringUtils.getDateForMode(clientTime, mode);
		} else {
			dateRange.setStart(startDate);
			dateRange.setEnd(endDate);

		}
		// adjust dateRange to server time
		dateRange.setStart(DateUtils.adjustToServerTime(dateRange.getStart(), clientTime));
		dateRange.setEnd(DateUtils.adjustToServerTime(dateRange.getEnd(), clientTime));
		return dateRange;
	}
	public static StringBuffer dateFormatForIdleAlert(LiveVehicleStatus liveVehicleObject) {
		long dif = liveVehicleObject.getIdleDuration()*1000;
		long difDays = dif / (24L * 60L * 60L * 1000L);
		long diffHours = dif / (60L*60L*1000L);
		if(diffHours >= 24L){
			diffHours = diffHours%24L;
		}
		long diffMinutes = dif / (60L*1000L);
		if(diffMinutes >= 60L){
			diffMinutes = diffMinutes%60L;
		}
		long diffSeconds = dif / (1000L);
		if(diffSeconds >= 60L){
			diffSeconds = diffSeconds%60L;
		}
		return formulateTime(difDays,diffHours,diffMinutes,diffSeconds);
	}
}
