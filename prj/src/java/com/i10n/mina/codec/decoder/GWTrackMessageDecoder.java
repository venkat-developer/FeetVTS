package com.i10n.mina.codec.decoder;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.text.DateFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import org.apache.log4j.Logger;
import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolDecoderOutput;
import org.apache.mina.filter.codec.demux.MessageDecoder;
import org.apache.mina.filter.codec.demux.MessageDecoderResult;

import com.i10n.dbCacheManager.NewVersionPacketsCache;
import com.i10n.fleet.container.BulkUpdateDataBean;
import com.i10n.fleet.container.GWTrackModuleDataBean;
import com.i10n.fleet.container.GWTrackModuleDataBean.ModuleVersion;
import com.i10n.fleet.util.EnvironmentInfo;
import com.i10n.mina.codec.Constants;
import com.i10n.mina.utils.Utils;
import com.i10n.module.command.CommandInterpretationResult;
import com.i10n.module.command.ModuleCommandInterpreter;
import com.i10n.module.dataprocessor.PacketProcessingException;

/**
 * A {@link MessageDecoder} that decodes message header and forwards
 * the decoding of body to a subclass.
 *
 */
public class GWTrackMessageDecoder implements MessageDecoder {

	/**
	 * Adjusting the module distance by 10% increment
	 */
	private static final float DECODER_DISTANCE_INCREMENT = 1.1F;

	private static Logger LOG = Logger.getLogger(GWTrackMessageDecoder.class);

	public GWTrackMessageDecoder() {
	}

	@Override
	public MessageDecoderResult decodable(IoSession session, IoBuffer in) {
		// VISHNU : We could do some basic test here if required
		return MessageDecoderResult.OK;
	}

	/**
	 * Check for required number of bytes from the module
	 */
	@Override
	public MessageDecoderResult decode(IoSession session, IoBuffer dis,
			ProtocolDecoderOutput out) throws Exception {
		/* Ensuring that the packet count bytes are available */
		try {
			LOG.debug(" Hex Dump : "+dis.getHexDump());
		} catch(Exception e){
			LOG.error("",e);
		}
		if (dis.remaining() < 2) {
			return  MessageDecoderResult.NEED_DATA;			
		}

		/*	Redirecting packet to other server for Sever analysis and data replication*/
		dis.mark();
		if(Boolean.valueOf(EnvironmentInfo.getProperty("IS_PACKET_REDIRECTION_ENABLED"))){
			LOG.info("Redirecting to "+EnvironmentInfo.getProperty("REDIRECT_TO_SERVER"));
			redirectPacket(dis);
		}
		dis.reset();

		int pktCount, firmwareVersion, expectedBytes;

		//Sets the buffer's mark at its position. 
		dis.mark();
		// Read packet count
		pktCount = dis.getShort();
		if(pktCount == 0){
			LOG.debug("Processing Command packet");
			ModuleCommandInterpreter moduleCommandInterpreter = new ModuleCommandInterpreter();
			CommandInterpretationResult commandInterpretationResult = moduleCommandInterpreter.interpretCommandFromStream(dis);
			out.write(commandInterpretationResult.getResponse());
			return MessageDecoderResult.OK;
		}

		if (dis.remaining()<37) {
			dis.reset();
			return  MessageDecoderResult.NEED_DATA;    		
		}
		//Forwards the position of this buffer as the 34 bytes.
		dis.skip(34);
		// Read firmware version
		firmwareVersion = Utils.unsigned(dis.get());

		LOG.info("Pkt count : "+pktCount+". FirmwareVersion : "+firmwareVersion);
		// Compute expected bytes
		expectedBytes = Utils.computeExpectedBytes (pktCount, firmwareVersion);
		LOG.debug("Expected byte : "+expectedBytes);
		dis.reset();
		if (dis.remaining() == expectedBytes) {		

			Date startTimeForDecoder = new Date ();
			// Do CRC check
			//Sets this buffer's mark at its position.
			dis.mark();
			byte [] dataArray = dis.array();
			boolean crcCheck = Utils.checkCRC(expectedBytes, dataArray, firmwareVersion);
			dis.reset();
			if (crcCheck) {
				// Process the actual packet
				GWTrackModuleDataBean dataBean=new GWTrackModuleDataBean();
				try {
					dataBean = decodeBody(session, dis);
					if (dataBean != null) {
						out.write(dataBean);
					}
				}
				catch (Exception e) {
					LOG.error("Exception in decoding data received",e);
				}				
				Date endTimeForDecoder = new Date ();
				if ((endTimeForDecoder.getTime()-startTimeForDecoder.getTime())>2000L) {
					PacketProcessingException packetException = new PacketProcessingException(PacketProcessingException.PacketErrorCode.UNTIMELY_RESPONSE_TO_MODULE);
					LOG.error ("Untimely response for module "+(endTimeForDecoder.getTime() - startTimeForDecoder.getTime()), packetException);
				}
				LOG.debug ("Time for giving response to module "+(endTimeForDecoder.getTime() - startTimeForDecoder.getTime()));
				return  MessageDecoderResult.OK;	    		
			} else {
				LOG.debug("Improper CRC");
			}
			return  MessageDecoderResult.NOT_OK;
		}else {
			LOG.debug("Byte length not as expected");
			return  MessageDecoderResult.NEED_DATA;
		}
	}

	private void redirectPacket(IoBuffer dis) {
		LOG.info("Packet redirection is enabled to the server "+EnvironmentInfo.getProperty("REDIRECT_TO_SERVER"));
		Socket socket = null;
		DataOutputStream outputStream = null;
		try{
			socket = new Socket(EnvironmentInfo.getProperty("REDIRECT_TO_SERVER"), 8060);
			socket.setSoTimeout(10000);
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			outputStream = new DataOutputStream(baos);
			// Writing complete IoBuffer in byte array for redirection
			byte[] dataArray = dis.array();
			int size = dis.remaining();
			for(int index = 0; index < size; index++){
				outputStream.write(dataArray[index]);
			}
			socket.getOutputStream().write(baos.toByteArray());
			socket.getOutputStream().flush();
			LOG.info("Successfully redirected the packet to the server "+EnvironmentInfo.getProperty("REDIRECT_TO_SERVER"));
		} catch (Exception e){
			LOG.error("Error while redirecting packet to "+EnvironmentInfo.getProperty("REDIRECT_TO_SERVER")+" server",e);
		} finally {
			// Making sure the socket is closed properly
			if(outputStream != null){
				try {
					outputStream.close();
				} catch (IOException e) {
					LOG.error("Error while closing output stream",e);
				}
			}
			if(socket != null){
				try{
					socket.close();
				}catch(Exception e){
					LOG.error("Error while closing socket",e);
				}
			}
		}

	}

	/**
	 * Extracts all debugrmation common to various versions of the modules
	 * 
	 * @param session
	 * @param dis
	 * @param out
	 * @return
	 * @throws Exception
	 */
	protected GWTrackModuleDataBean decodeBody(IoSession session, IoBuffer dis) throws Exception {
		LOG.debug("Inside decode body");
		Date startOfOperation = new Date();

		GWTrackModuleDataBean moduleDataBean = new GWTrackModuleDataBean();
		byte healthStatusOfModule = 0;
		int firmwareVersion;

		int count = dis.getShort();

		if (count < 1 || count > 300) {
			throw new ModuleDataException("", ModuleDataException.ErrorCode.UNEXPECTED_BYTE_LENGTH);
		}

		moduleDataBean.setPktCount(count);
		moduleDataBean.setGpsSignalStrength(dis.get() / 10.0);
		moduleDataBean.setVehicleCourse(dis.getShort());
		moduleDataBean.setGsmSignalStrength(dis.get());
		moduleDataBean.setNumberOfPacketSendingAttempts(dis.getInt());
		moduleDataBean.setNumberOfSuccessPackets(dis.getInt());
		moduleDataBean.setModuleBatteryVoltage(dis.getShort());

		int cumDistance = dis.getInt();

		moduleDataBean.setCumulativeDistance((int)(cumDistance*DECODER_DISTANCE_INCREMENT));
		moduleDataBean.setMaxSpeed(dis.getShort());
		//		moduleDataBean.setVehicleBatteryVoltage(dis.getShort()); // not used as of now.
		moduleDataBean.setAnalogue1(dis.getShort());
		moduleDataBean.setAnalogue2(dis.getShort());
		moduleDataBean.setNumberOfErrorsInHardwareModule(dis.get());

		int locationAreaCode = 0;
		int cellID = 0;
		try{
			locationAreaCode = dis.getInt();
		}catch(NumberFormatException e){
			locationAreaCode = 0;
		}
		try{
			cellID = dis.getInt();
		}catch(NumberFormatException e){
			cellID = 0;
		}
		moduleDataBean.setLocationAreaCode(locationAreaCode);
		moduleDataBean.setCellId(cellID);

		healthStatusOfModule = dis.get();

		//store firmware version to decide DP or v2 
		firmwareVersion = dis.get();
		moduleDataBean.setFirmwareVersion(firmwareVersion);
		dis.shrink();

		Utils.processModuleStatus(healthStatusOfModule, moduleDataBean,firmwareVersion);
		moduleDataBean.setModuleUpdateTime(new Date());

		LOG.debug("Successfully read the non bulk part");
		
		MessageDecoderResult res = MessageDecoderResult.NOT_OK;
		if (Utils.isOldVersionModule(firmwareVersion) || Utils.isVersionV5_6(firmwareVersion) || Utils.isVersionV5_101(firmwareVersion)){
			res = decodeForOldFirmware (session, dis, moduleDataBean);
		}else if(Utils.isNewVersionModule(firmwareVersion)){
			res = decodeForNewFirmware (session, dis, moduleDataBean);
		} else if (Utils.isAndroidFirmware(firmwareVersion)){ // for android gwtrack application  
			res = decodeForAndroidApplication(session, dis, moduleDataBean);
		}else if(Utils.isVersionV5_8(firmwareVersion)){
			res = decodeGwtrackV5Data(session, dis, moduleDataBean);
		}
		int crcValue=dis.get();
		LOG.debug("crcValue is "+crcValue);
		if (res == MessageDecoderResult.NOT_OK) { 
			return null;
		}
		Date endOfOperation = new Date();
		LOG.debug("Time taken for processing the packet "+(endOfOperation.getTime()-startOfOperation.getTime()));

		LOG.info("Packet : "+moduleDataBean.toString());

		return moduleDataBean;
	}

	/**
	 * Decodes the data from the android device 
	 * @param session
	 * @param dis
	 * @param moduleDataBean
	 * @return
	 * @throws IOException
	 */
	private MessageDecoderResult decodeForAndroidApplication(IoSession session, IoBuffer dis, GWTrackModuleDataBean moduleDataBean) 
			throws Exception {
		String imei;
		moduleDataBean.setModuleVersion(ModuleVersion.MODULE_VERSION_3_5);
		imei = Utils.processString(Constants.POS_IMEI, 0, dis);
		moduleDataBean.setImei(imei);
		ArrayList<BulkUpdateDataBean> bulk = new ArrayList<BulkUpdateDataBean>();

		for (int i = 0; i < moduleDataBean.getPktCount(); i++) {
			double latitude = dis.getInt() / 10000000.0;
			double longitude = dis.getInt() / 10000000.0;

			if(!isValidLocation(latitude, longitude)){
				LOG.error("Rejecting the sub packet because of unlikely lat long values for IMEI : "+moduleDataBean.getImei()
						+" Where lat long are : "+latitude+" : "+longitude);
				continue;
			}

			int deltaDistance = (int) dis.getShort();

			long timeInMilliseconds = dis.getLong();
			Date updatedAt = new Date(timeInMilliseconds);
			if(isFutureDate(updatedAt)){
				/* The packet is newer by 15 minutes*/
				LOG.error("Rejecting new fw sub-packet from IMEI : "+moduleDataBean.getImei()
						+" resulting in date new by 15 minutes"
						+Utils.convertTOWToIST(timeInMilliseconds));
				continue;
			}
			if(isOldDate(updatedAt)){
				/* The packet is older by a 1 month */
				LOG.error("Rejecting new fw sub-packet from IMEI : "+moduleDataBean.getImei()+" resulting in date older by 1 month "
						+Utils.convertTOWToIST(timeInMilliseconds));
				continue;
			}
			int analogFuelData = dis.getShort();

			BulkUpdateDataBean temp = new BulkUpdateDataBean(latitude, longitude, deltaDistance, updatedAt, getFuelCalibratedValue(analogFuelData));
			temp.setImei(imei);
			bulk.add(temp);
		}

		//Sort the BulkData portion of GWTrackDataBean
		Utils.sortBulkUpdateData(bulk);

		// Add the bulk update portion
		moduleDataBean.setBulkUpdateData(bulk);
		return MessageDecoderResult.OK;
	}

	/**
	 * Decodes debugrmation specific to older firmware modules
	 * 
	 * @param session
	 * @param dis
	 * @param out
	 * @param moduleDataBean
	 * @return
	 * @throws Exception
	 */
	public MessageDecoderResult decodeForOldFirmware (IoSession session, IoBuffer dis,GWTrackModuleDataBean moduleDataBean) throws Exception {
		LOG.debug("Decoding version number : "+moduleDataBean.getFirmwareVersion());
		String imei="";
		moduleDataBean.setModuleVersion(ModuleVersion.MODULE_VERSION_3_5);
		imei = Utils.processString(Constants.POS_IMEI, 0, dis);
		moduleDataBean.setImei(imei);
		LOG.debug("IMEI : "+imei);
		ArrayList<BulkUpdateDataBean> bulk = new ArrayList<BulkUpdateDataBean>();
		for (int i = 0; i < moduleDataBean.getPktCount(); i++) {
			double latitude = (Utils.processLittleEndianInt(Constants.POS_LATITUDE, 0, dis)) / 10000000.0;
			double longitude = (Utils.processLittleEndianInt(Constants.POS_LONGITUDE, 0, dis)) / 10000000.0;
			BulkUpdateDataBean temp = new BulkUpdateDataBean();

			LOG.debug("Validating location data");
			if(!isValidLocation(latitude, longitude)){
				if(Utils.isVersionV5_101(moduleDataBean.getFirmwareVersion())){
					/**
					 * Accept GPS zero packets as well for Version 101 with GPS lock status false  
					 */
					LOG.debug("IMEI : "+imei+" GPS zero packet for version : "+moduleDataBean.getFirmwareVersion());
					temp.setNoGPSLock(true);
				} else {
					LOG.error("Rejecting the sub packet because of unlikely lat long values for IMEI : "+moduleDataBean.getImei()
							+" Where lat long are : "+latitude+" : "+longitude);
					continue;
				}
			}

			int deltaDistance = (int) (Utils.processLittleEndianUnsignedShort(Constants.POS_DELTA_DISTANCE, 0, dis)*DECODER_DISTANCE_INCREMENT);

			long tow = Utils.processLittleEndianUnsignedInt(Constants.POS_TOW, 0, dis);
			LOG.debug("Calculated TOW for IMEI : "+imei+" is : "+tow);
			if(!isValidTOW(tow)){
				LOG.error("Rejecting old fw sub-packet from IMEI : "+moduleDataBean.getImei()+" for invalid TOW value = "+tow+" resulting in date "+Utils.convertTOWToIST(tow));
				continue;
			}

			int analogValue = Utils.processLittleEndianUnsignedShort(Constants.POS_ANALOGUE_1_FUEL_DATA, 0, dis);

			Date updatedAt = new Date();
			updatedAt = Utils.convertTOWToIST(tow);

			if(isFutureDate(updatedAt)){
				/* The packet is newer by a 15 minutes*/
				LOG.error("Rejecting new fw sub-packet from IMEI : "+moduleDataBean.getImei()+" for TOW value = "+tow
						+" resulting in date newer by 15 minutes "+Utils.convertTOWToIST(tow));
				continue;
			}
			if(isOldDate(updatedAt)){
				/* The packet is older by a 1 month */
				LOG.error("Rejecting new fw sub-packet from IMEI : "+moduleDataBean.getImei()+" for TOW value = "+tow
						+" resulting in date older by 1 month "+Utils.convertTOWToIST(tow));
				continue;
			}

			if(!Utils.isVersionV5_101(moduleDataBean.getFirmwareVersion())){
				analogValue = getFuelCalibratedValue(analogValue);
			}

			temp.setLatitude(latitude);
			temp.setLongitude(longitude);
			temp.setDeltaDistance(deltaDistance);
			temp.setOccurredAt(updatedAt);
			temp.setAnalogue(analogValue);
			temp.setImei(imei);
			bulk.add(temp);
		}
		LOG.debug("Successfully read all bulk packets");
		//Sort the BulkData portion of GWTrackDataBean
		Utils.sortBulkUpdateData(bulk);

		// Add the bulk update portion
		moduleDataBean.setBulkUpdateData(bulk);

		return MessageDecoderResult.OK;
	}


	private boolean isOldDate(Date updatedAt) {
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.MONTH, -1);
		if (updatedAt.getTime() < cal.getTimeInMillis()) {
			return true;			
		}
		return false;

	}

	private boolean isFutureDate(Date updatedAt) {
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.MINUTE, 15);
		if (updatedAt.getTime() > cal.getTimeInMillis()) {
			return true;			
		}
		return false;
	}

	private boolean isValidTOW(long tow) {
		if((tow/1000L) > 604800L){
			return false;
		}
		return true;
	}

	private boolean isValidLocation(double latitude, double longitude) {
		if((latitude > 90 || latitude < -90) || (longitude > 180 || longitude < -180) || (latitude == 0 || longitude == 0)){
			return false;
		}
		return true;
	}

	/**
	 * Fuel calibration function very specific to Vepomon sensor installation in
	 * CP vehicle.
	 * @param analogValue
	 * @return
	 */
	private int getFuelCalibratedValue (int analogValue) {
		double fuel [][] = new double [][] {{0,0}, {44,4.4},{80,4} ,{112,3.73} ,{138,3.45} ,{165,3.3} ,{190,3.16666} ,{215,3.0714} ,
				{238,2.975} ,{262,2.911111}, {286,2.86}, {310,2.81818},{333,2.775},{357,2.746153},{380,2.714285},{406,2.706666},
				{425,2.65625}, {450,2.6470588}, {475,2.638888}, {502,2.64210526},{530,2.65},{561,2.671428},{596,2.790909},{641,2.786956},
				{655, 2.77778}, {Integer.MAX_VALUE, 2.77778}};
		int i = 0;
		while (analogValue>=fuel[i][0] && analogValue>fuel[i+1][0]) i++;
		return (int) Math.round ( analogValue / fuel[i+1][1] );
	}


	/**
	 * Decodes debug pertaining to newer modules
	 * 
	 * @param session
	 * @param dis
	 * @param out
	 * @param moduleDataBean
	 * @return
	 * @throws Exception
	 */
	public MessageDecoderResult decodeForNewFirmware (IoSession session, IoBuffer dis,GWTrackModuleDataBean moduleDataBean) throws Exception{

		int year, month, day, weekNumber = 0;
		int sequenceNumber = -1;
		int fuelValue = 0; // Not programmed for New version modules, Hence inserting default 0 

		String imei;

		moduleDataBean.setModuleVersion(ModuleVersion.MODULE_VERSION_3_5);

		/** Applicable for v4 and v5 **/
		year = dis.getShort();
		month = dis.get();
		day = dis.get();

		imei = Utils.processString(Constants.DP_IMEI, 0, dis);
		moduleDataBean.setImei(imei);

		ArrayList<BulkUpdateDataBean> bulk = null;
		if(NewVersionPacketsCache.getInstance().newVersionPacketsCache.containsKey(imei)){
			bulk = NewVersionPacketsCache.getInstance().newVersionPacketsCache.get(imei).getBulkUpdateData();
			LOG.debug("Previous packet was having more to follow true hence taking bulk data from the cache for IMEI "+imei+" pkts to be committed "+bulk.size());
		}else{
			bulk = new ArrayList<BulkUpdateDataBean>();
		}
		for (int i = 0; i < moduleDataBean.getPktCount(); i++) {

			Date updatedAt = new Date();
			double latitude = (Utils.processLittleEndianInt(Constants.DP_LATITUDE, 0, dis)) / 10000000.0;
			double longitude = (Utils.processLittleEndianInt(Constants.DP_LONGITUDE, 0, dis)) / 10000000.0;

			if(!isValidLocation(latitude, longitude)){
				LOG.error("Rejecting the sub packet because of unlikely lat long values for IMEI : "+moduleDataBean.getImei()
						+" Where lat long are : "+latitude+" : "+longitude);
				continue;
			}

			int deltaDistance = (int)(Utils.processLittleEndianUnsignedShort(Constants.DP_DELTA_DISTANCE, 0, dis)*DECODER_DISTANCE_INCREMENT);

			long tow = Utils.processLittleEndianUnsignedInt(Constants.DP_TOW, 0, dis);
			LOG.debug("Calculated TOW for IMEI : "+imei+" is : "+tow);

			if(!isValidTOW(tow)){
				LOG.error("Rejecting new fw sub-packet from IMEI : "+moduleDataBean.getImei()+" for invalid TOW value = "+tow+" resulting in date "+Utils.convertTOWToIST(tow, year, month, day, weekNumber));
				continue;
			}

			weekNumber = Utils.unsigned(dis.get());

			sequenceNumber = Utils.unsigned(dis.get());

			updatedAt = Utils.convertTOWToIST(tow, year, month, day,weekNumber);
			if(isFutureDate(updatedAt)){
				/* The packet is newer by a 15 minutes */
				LOG.error("Rejecting new fw sub-packet from IMEI : "+moduleDataBean.getImei()+" for TOW value = "+tow+" day="
						+day+" month ="+month+" year= "+year
						+" resulting in date newer by 15 minutes "+Utils.convertTOWToIST(tow, year, month, day, weekNumber));
				continue;
			}
			if(isOldDate(updatedAt)){
				/* The packet is older by a 1 month */
				LOG.error("Rejecting new fw sub-packet from IMEI : "+moduleDataBean.getImei()+" for TOW value = "+tow
						+" resulting in date older by 1 month "+Utils.convertTOWToIST(tow, year, month, day, weekNumber));
				continue;
			}
			BulkUpdateDataBean temp = new BulkUpdateDataBean( latitude, longitude, deltaDistance, weekNumber, sequenceNumber,  fuelValue, updatedAt);

			if(weekNumber>64){
				/*LOG.debug("Must be a digital data instead of weekNumber for version 5");
				byte[] digit=new byte[4];
				digit=Utils.processintToByteArray(weekNumber);
				Utils.processDigitalData(digit[0], temp);*/
			} else {
				//				temp.setWeekNumber(weekNumber);
			}
			temp.setImei(imei);

			bulk.add(temp);
		}
		//Sort the BulkData portion of GWTrackDataBean
		Utils.sortBulkUpdateData(bulk);

		moduleDataBean.setBulkUpdateData(bulk);

		return MessageDecoderResult.OK;
	}

	@Override
	public void finishDecode(IoSession arg0, ProtocolDecoderOutput arg1)
			throws Exception {

	}

	/**
	 * Decode body for GWTrack V5 module with data format change for Time and date update
	 * 
	 * OLD Data Format	No of Bytes		New Data Format		No of Bytes
	 * Latitude			4				Latitude			4
	 * Longitude		4				Longitude			4
	 * Delta distance	2				Delta distance		2
	 * TOW				4				Time				4
	 * Fuel Analog		2				Fuel Analog			2
	 * 									Date				4
	 * @param session
	 * @param dis
	 * @param moduleDataBean
	 * @return
	 * @throws IOException
	 */
	private MessageDecoderResult decodeGwtrackV5Data(IoSession session,
			IoBuffer dis, GWTrackModuleDataBean moduleDataBean) throws IOException {
		String imei;
		moduleDataBean.setModuleVersion(ModuleVersion.MODULE_VERSION_3_5);
		imei = Utils.processString(Constants.POS_IMEI, 0, dis);
		moduleDataBean.setImei(imei);
		ArrayList<BulkUpdateDataBean> bulk = new ArrayList<BulkUpdateDataBean>();
		for (int i = 0; i < moduleDataBean.getPktCount(); i++) {
			double latitude = (Utils.processLittleEndianInt(Constants.POS_LATITUDE, 0, dis)) / 10000000.0;
			double longitude = (Utils.processLittleEndianInt(Constants.POS_LONGITUDE, 0, dis)) / 10000000.0;

			if(!isValidLocation(latitude, longitude)){
				LOG.error("Rejecting the sub packet because of unlikely lat long values for IMEI : "+moduleDataBean.getImei()
						+" Where lat long are : "+latitude+" : "+longitude);
				continue;
			}

			int deltaDistance = (int) (Utils.processLittleEndianUnsignedShort(Constants.POS_DELTA_DISTANCE, 0, dis)*DECODER_DISTANCE_INCREMENT);

			int seconds = dis.get();
			int minutes = dis.get();
			int hours = dis.get();
			@SuppressWarnings("unused")
			int placeHolder = dis.get();

			int analogFuelData = Utils.processLittleEndianUnsignedShort(Constants.POS_ANALOGUE_1_FUEL_DATA, 0, dis);

			Byte date = dis.get();
			Byte month = dis.get();
			int year2 = Utils.unsigned(dis.get());
			int year1=Utils.unsigned(dis.get());
			int year=(year1 << 8) | year2;
			LOG.debug("Date sent by the module si == "+year+" "+month+" "+date+" "+hours+":"+minutes+":"+seconds);

			Calendar bulkUpdateCalendarInstance = Calendar.getInstance(TimeZone.getTimeZone("GMT"));
			bulkUpdateCalendarInstance.set(year, month-1, date, hours, minutes, seconds);

			Date updatedAt = bulkUpdateCalendarInstance.getTime();
			if(isFutureDate(updatedAt)){
				/* The packet is newer by a 15 minutes*/
				LOG.error("Rejecting gwtrack v5 sub-packet from IMEI : "+moduleDataBean.getImei()+" for being newer by more than 15 minutes  resulting in date "
						+updatedAt.toString());
				continue;
			}
			if(isOldDate(updatedAt)){
				/* The packet is older by a 1 month */
				LOG.error("Rejecting new fw sub-packet from IMEI : "+moduleDataBean.getImei()+" resulting in date older by 1 month "+updatedAt.toString());
				continue;
			}
			//Converting GMT to IST
			DateFormat istFormat = DateFormat.getDateTimeInstance();
			TimeZone istTime = TimeZone.getTimeZone("IST");
			istFormat.setTimeZone(istTime);
			istFormat.format(bulkUpdateCalendarInstance.getTime());
			Date istDate = null;
			try {
				istDate = istFormat.parse(istFormat.format(bulkUpdateCalendarInstance.getTime()));
			} catch (ParseException e) {
				LOG.error(e);
			}

			BulkUpdateDataBean temp = new BulkUpdateDataBean(latitude, longitude, deltaDistance, istDate, getFuelCalibratedValue(analogFuelData));
			temp.setImei(imei);
			bulk.add(temp);
		}

		//Sort the BulkData portion of GWTrackDataBean
		Utils.sortBulkUpdateData(bulk);

		// Add the bulk update portion
		moduleDataBean.setBulkUpdateData(bulk);

		return MessageDecoderResult.OK;
	}

}