package com.i10n.fleet.util;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

import org.apache.log4j.Logger;


/**
 * The SMS Gateway class  
 * @author sreekanth
 *
 */
@SuppressWarnings("deprecation")
public class SMSGateWay {
	private static Logger LOG = Logger.getLogger(SMSGateWay.class);
	/**The URL for sending the SMS
    private static final String URL = "http://sms.hrn.in/send_sms.php?";
	 */
	//The user name for SMS gateway
	private static final String USER_NAME = "interchain";

	//The password for SMS gateway
	private static final String PASSWORD = "interchain";		//interchain 

	// SenderId to display ion top of the message 
	private static final String SENDER_ID = "ICHAIN";		//interchain

	// CDMA header 
	private static final String CDMA_HEADER = "ICHAIN";		//interchain

	//The URL for sending the SMS
	private static final String URL ="http://transapi.msandesh.in/desk2web/SendSMS.aspx?"; 
	
	private static int smsCount=0;
	 
	/**
	 * Sends SMS to the given number
	 * @param phoneNumber
	 * The mobile number of the receiver [It should start with 91 For Ex: 919739086905]
	 * @param message
	 * The message to be sent
	 * @return
	 * Success/Failure in sending the message
	 */
	public static void sendSMSToNumber(String phoneNumber, String message){
		URL url;
		InputStream is = null;
		phoneNumber=removeCodeIfAddedByClient(phoneNumber);
		try {
			removeCodeIfAddedByClient(phoneNumber);
			String urlValue = createURL(phoneNumber, message);
			url = new URL(urlValue);
			is = url.openStream();
			DataInputStream dis = new DataInputStream(new BufferedInputStream(is));
			String s = null;
			boolean msgSent = false;
			LOG.info("Sending SMS to " + phoneNumber + " with message [" + message + "] sent status "+dis.readLine());
			while ((s = dis.readLine()) != null) {	
				if(s.contains("Message Sent Successfully")){
					LOG.info("SMS Sending to number [" + phoneNumber + "] failed !!!");
					msgSent = true;
				}
			}
			if (!msgSent){
				smsCount=smsCount+1;
				LOG.info("Total sms sent from gateway are "+smsCount);
			}
		} catch (MalformedURLException e) {
			LOG.error("Error while Sending SMS to phone : "+phoneNumber);
		} catch (IOException e){
			LOG.error("SMS I/O error Phone : "+phoneNumber);
		} catch (Exception e){
			LOG.error("Error while Sending SMS Phone : "+phoneNumber);
		}
	}

	private static String removeCodeIfAddedByClient(String mobileNumber){
		if(mobileNumber.startsWith("+")){
			mobileNumber = mobileNumber.substring(1, mobileNumber.length());
		}
		if(mobileNumber.length()==10)
			mobileNumber = "91" + mobileNumber;
		return mobileNumber;
	}

	/**
	 * Creates the URL as needed by the SMS gate way
	 * @param phoneNumber
	 * The mobile number of the receiver
	 * @param message
	 * The message to be sent
	 * @return
	 * The formatted URL with query parameters
	 */
	private static String createURL(String phoneNumber, String message){
		StringBuilder urlParams = new StringBuilder();
		message=message.replace(" ", "%20");
		urlParams.append(URL);
		urlParams.append("UserName="+USER_NAME);
		urlParams.append("&password="+PASSWORD);
		urlParams.append("&MobileNo="+phoneNumber);
		urlParams.append("&SenderID="+SENDER_ID);
		urlParams.append("&CDMAHeader="+CDMA_HEADER);
		urlParams.append("&Message="+message);
		urlParams.append("&isFlash=false");
		return urlParams.toString();		
	}
}