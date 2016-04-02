package com.i10n.teltonika;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;
import java.util.Vector;

import org.apache.log4j.Logger;

import com.i10n.db.entity.LiveVehicleStatus;
import com.i10n.db.entity.Teltonika;
import com.i10n.db.entity.TeltonikaAVLData;
import com.i10n.dbCacheManager.LoadLiveVehicleStatusRecord;
import com.i10n.fleet.container.BulkUpdateDataBean;
import com.i10n.fleet.container.GWTrackModuleDataBean;
import com.i10n.mina.utils.Utils;
import com.i10n.module.dataprocessor.DataPool;
import com.ibm.icu.math.BigDecimal;
import com.mchange.lang.ByteUtils;

public class TeltonikaDataReader implements Runnable{
	
	private Logger LOG = Logger.getLogger(TeltonikaDataReader.class);
	public final static int TELTONIKA_FIRMWARE_VERSION = 25;
	private Socket clientSocket;
	
	public TeltonikaDataReader(Socket clientSocket) {
		this.clientSocket = clientSocket;
	}
	
	@Override
	public void run() {
		try {
			readAVLPacket(clientSocket);
		} catch (IOException e) {
			LOG.error("Error while reading AVL packet",e);
		} finally {
			if(clientSocket != null){
				try {
					clientSocket.close();
				} catch (IOException e) {
					LOG.error("TTTTT Error while closing the client connection"+e);
				}
			}
		}
	}

	/**
	 * The data from Teltonika device is read here 
	 * The overview of the data goes like this
	 * 1) Read IMEI
	 * 2) Send IMEI read response
	 * 3) Read AVL DataPacket header  : Four zeros + AVL data array length
	 * 4) Read AVL Data : CodecID + Number of data + Data elements+ Number of data
	 * 5) Read CRC
	 * 6) Send data reception acknowledgment : 4 bytes(Number of data read)
	 * @param clientSocket
	 * @throws IOException 
	 */
	private void readAVLPacket(Socket clientSocket) throws IOException {
		LOG.debug("TTTTT Reading AVL Data Packet");
		String IMEI = readIMEI(clientSocket);
		if(IMEI == null){
			LOG.error("TTTTT Error while reading IMEI ");
			return;
		}
		try {
			LOG.debug("TTTTT Sending the IMEI read response");
			String responseStatus = sendIMEIReadResponse(clientSocket, IMEI);
			if(responseStatus.equals("SUCCESS")){
				// Continue with data read
				LOG.debug("TTTTT Device : "+IMEI+" exists");
			} else {
				LOG.info("TTTTT Device : "+IMEI+" doesnt exist (or) Trip Not Assigned");
				return;
			}
		} catch (IOException e) {
			LOG.error("TTTTT Error while sending IMEI Read response",e);
			return;
		}
		int bytesToRead = readAVLDataArrayHeader(clientSocket);
		if(bytesToRead > 0 ){
			Teltonika dataPacket = new Teltonika();
			readAVLDataArray(clientSocket, bytesToRead, dataPacket);
			if(dataPacket.getCodecId() == 8 ){
				sendFinalResponse(clientSocket, dataPacket.getDataPacketLength());
				if(dataPacket.getDataPacketLength() == dataPacket.getDataPacketLenghtPadding()){
					try {
						formulateInputDataToPacket(IMEI,dataPacket);
					} catch (NumberFormatException e) {
						LOG.error("TTTTT NumberFormatException while formulating data to packet format",e);
						return;
					} catch (ParseException e) {
						LOG.error("TTTTT ParseException while formulating data to packet format",e);
						return;
					} catch (Exception e){
						LOG.error("TTTTT Exception while formulation data to packet format",e);
						return;
					}
				} else {
					LOG.error("TTTTT ReadDataPacketLength : "+ dataPacket.getDataPacketLength()
							+" != LastRecordLength : "+dataPacket.getDataPacketLenghtPadding());
				}
			} else {
				LOG.error("TTTTT Codec ID : "+dataPacket.getCodecId()+" error ");
			}
		} else {
			LOG.debug("TTTTT No data to read ");
		}
		
	}

	/**
	 * Data array consists of the following 
	 * 1) Coded ID 					: 1 byte (value 08)
	 * 2) No of Data packets 		: 1 byte
	 * 3) Data array :  Each data 	: TimeStamp = 8 bytes : Priority = 1 byte : GPS Element = 15 bytes : IO element
	 * 					Each GPSele : Longitude = 4 bytes : Latitude = 4 bytes : Altitude = 2 bytes : Angle = 2 bytes : Satellite = 1 byte : Speed = 2 byte 
	 * @param clientSocket
	 * @param bytesToRead
	 * @param dataPacket 
	 * @return
	 * @throws IOException
	 */
	private void readAVLDataArray(Socket clientSocket, int bytesToRead, Teltonika dataPacket) throws IOException {
		
		DataInputStream inputStream = new DataInputStream(clientSocket.getInputStream());


		LOG.debug("TTTTT Reading AVL Data Array");

		dataPacket.setCodecId((int) inputStream.read());
		LOG.debug("TTTTT CodeID : "+dataPacket.getCodecId());
		dataPacket.setDataPacketLength((int) inputStream.read());
		LOG.debug("TTTTT Data packet length : "+dataPacket.getDataPacketLength());

		Vector<TeltonikaAVLData> avlDataArray = new Vector<TeltonikaAVLData>();
		
		for(int i=0; i< dataPacket.getDataPacketLength(); i++){
			TeltonikaAVLData avlData = new TeltonikaAVLData();
			Long timeInUTC = inputStream.readLong();
			avlData.setUtcTime(timeInUTC);
			avlData.setPriority((int)inputStream.read());
			avlData.setLongitude(inputStream.readInt());
			avlData.setLatitude(inputStream.readInt());
			avlData.setAltitude(inputStream.readShort());
			avlData.setDirection(inputStream.readShort());
			avlData.setSatelliteCount((int)inputStream.read());
			avlData.setSpeed(inputStream.readShort());

			readIOElement(inputStream);
			avlDataArray.add(avlData);
		}

		dataPacket.setAvlDataArray(avlDataArray);
		
		dataPacket.setDataPacketLenghtPadding((int)inputStream.read());
		LOG.debug("TTTTT Read LastRecordLength : "+dataPacket.getDataPacketLenghtPadding());
		dataPacket.setCrc(inputStream.readInt());

	
	}

	/**
	 * AVL data header 
	 * @param clientSocket
	 * @return
	 * @throws IOException
	 */
	private int readAVLDataArrayHeader(Socket clientSocket) throws IOException {
		LOG.debug("TTTTT Reading the AvlData in module");
		DataInputStream inputStream = new DataInputStream(clientSocket.getInputStream());
		int zero = inputStream.readInt();
		int bytesToRead = inputStream.readInt();
		LOG.debug("TTTTT Zero : "+zero+" BytesToRead : "+bytesToRead);
		return bytesToRead;
	}

	/**
	 * Send IMEI response. 
	 * @param clientSocket
	 * @param IMEI
	 * @return 01 = Success : 00 = Failure
	 * @throws IOException
	 */
	private String sendIMEIReadResponse(Socket clientSocket, String IMEI) throws IOException {
		LiveVehicleStatus liveobject=LoadLiveVehicleStatusRecord.getInstance().retrieve(IMEI);
		if(liveobject != null){
			byte response = (byte)0x01;
			LOG.debug("Response:"+response);
			clientSocket.getOutputStream().write(response);
			return "SUCCESS";
		} else {
			byte response = (byte)0x00;
			LOG.debug("Response:"+response);
			clientSocket.getOutputStream().write(response);
			return "FAILURE";
		}
		
	}

	/**
	 * Read the IMEI from the Teltonika device
	 * @param clientSocket
	 * @return
	 */
	private String readIMEI(Socket clientSocket) {
		LOG.debug("TTTTT Reading IMEI ");
		DataInputStream inputStream;
		try {
			inputStream = new DataInputStream(clientSocket.getInputStream());
		} catch (IOException e) {
			LOG.error("TTTTT Error while reading IMEI "+e);
			return null;
		}
		byte[] imeiArrSample=new byte[17];
		try {
			inputStream.read(imeiArrSample,0,17);
		} catch (IOException e) {
			LOG.error("TTTTT Error while reading IMEI",e);
			return null;
		}
		LOG.debug("TTTTT Read value : "+ByteUtils.toHexAscii(imeiArrSample));
		String IMEI =new String(imeiArrSample,2, 15);
		LOG.debug("TTTTT Received IMEI :"+IMEI);
		return IMEI;
	}

	

	/**
	 * Adding the packets to the DataPool
	 * @param IMEI
	 * @throws NumberFormatException
	 * @throws ParseException
	 */
	private void formulateInputDataToPacket(String IMEI,Teltonika dataPacket) throws NumberFormatException, ParseException {
		GWTrackModuleDataBean bean = new GWTrackModuleDataBean();
		bean.setImei(IMEI);
		bean.setGpsSignalStrength(Double.parseDouble("0"));
		bean.setModuleBatteryVoltage(Double.parseDouble("0"));
		bean.setGsmSignalStrength(0.0);               
		bean.setPktCount(0);

		bean.setNumberOfPacketSendingAttempts(0);
		bean.setNumberOfSuccessPackets(0);            bean.setCumulativeDistance(0);
		bean.setAnalogue1(0);                        bean.setAnalogue2(0);
		bean.setNumberOfErrorsInHardwareModule(0);    bean.setLocationAreaCode(0);
		bean.setCellId(0);                            bean.setFirmwareVersion(TELTONIKA_FIRMWARE_VERSION);
		bean.setModuleUpdateTime(new Date());


		ArrayList<BulkUpdateDataBean> bulkDataList=new ArrayList<BulkUpdateDataBean>();
		LOG.debug("readDataPacketLength:"+dataPacket);
		for(int i=0; i< dataPacket.getDataPacketLength(); i++){

			DateFormat utcFormat1 = new SimpleDateFormat("dd/MM/yy HH:mm:ss");
			utcFormat1.setTimeZone(TimeZone.getTimeZone("UTC"));
			DateFormat indianFormat = new SimpleDateFormat("dd/MM/yy HH:mm:ss");
			utcFormat1.setTimeZone(TimeZone.getTimeZone("Asia/Kolkata"));
			Calendar calendar = Calendar.getInstance();
			calendar.setTimeInMillis(dataPacket.getAvlDataArray().get(i).getUtcTime());
			String finaltime = indianFormat.format(calendar.getTime());
			LOG.debug("finaltime:"+finaltime);

			LOG.debug("Lat:"+dataPacket.getAvlDataArray().get(i).getLatitude()+"Lon:"+dataPacket.getAvlDataArray().get(i).getLongitude()
					+"Speed:"+dataPacket.getAvlDataArray().get(i).getSpeed());

			BigDecimal FinalLon=new BigDecimal(dataPacket.getAvlDataArray().get(i).getLongitude()).movePointLeft(7);
			BigDecimal FinalLat=new BigDecimal(dataPacket.getAvlDataArray().get(i).getLatitude()).movePointLeft(7);

			//bean.setMaxSpeed(Double.parseDouble(String.valueOf(speedFinal.get(i))));
			DateFormat formatter = new SimpleDateFormat("dd/MM/yy HH:mm:ss");

			BulkUpdateDataBean temp = new BulkUpdateDataBean(FinalLat.doubleValue(),FinalLon.doubleValue(), 0.0,
					formatter.parse(finaltime),0,dataPacket.getAvlDataArray().get(i).getSpeed(),
					Double.parseDouble(String.valueOf(dataPacket.getAvlDataArray().get(i).getDirection())));

			temp.setImei(IMEI);
			//bean.setVehicleCourse(Double.parseDouble(String.valueOf(direction.get(i))));

			/*if(temp.getOccurredAt().getTime() > LoadLiveVehicleStatusRecord.getInstance().retrieve(imei).getLastupdatedAt().getTime()){
				LOG.debug("Adding new Packet to the list");
				bulkDataList.add(temp);
			} else {
				// Only adding the packets which are new
				LOG.debug("This is a old packet hence not adding to the list : "+temp.toString());
			}*/
			bulkDataList.add(temp);

		}

		Utils.sortBulkUpdateData(bulkDataList);
		bean.setBulkUpdateData(bulkDataList);

		LOG.info("Bean Data is:"+bean.toString());
		DataPool.addPacket(bean);
		LOG.debug("Packets Added the DataPool");

	}

	/**
	 * Reading IO values. We are not going to use these values.
	 * @param dis
	 * @throws IOException
	 */
	private void readIOElement(DataInputStream dis) throws IOException {
		int ioID = dis.read();
		LOG.debug("TTTTT IOID : "+ByteUtils.toHexAscii((byte)ioID));

		int ioDataLenght = (int)dis.read();
		LOG.debug("TTTTT IODataLength : "+ioDataLenght);
		readOneByteLengthIOValue(dis);
		readTwoByteLengthIOValue(dis);
		readFourByteLengthIOValue(dis);
		readEightByteLengthIOValue(dis);
		LOG.debug("TTTTT Successfully Compleated reading IO elements ");
	}

	private void readEightByteLengthIOValue(DataInputStream dis) throws IOException {
		int noOfEightByteLengthIOValues = dis.read();
		LOG.debug("TTTTT  noOfEightByteLengthIOValues : "+noOfEightByteLengthIOValues);
		for(int i= 0; i < noOfEightByteLengthIOValues; i++){
			readIOID(dis);
			readIOValue(dis, 8);
		}
	}

	private void readFourByteLengthIOValue(DataInputStream dis) throws IOException {
		int noOfFourByteLengthIOValues = (int)dis.read();
		LOG.debug("TTTTT noOfFourByteLengthIOValues : "+noOfFourByteLengthIOValues);
		for(int i= 0; i < noOfFourByteLengthIOValues; i++){
			readIOID(dis);
			readIOValue(dis, 4);
		}
	}

	private void readTwoByteLengthIOValue(DataInputStream dis) throws IOException {
		int noOfTwoByteLengthIOValues = (int)dis.read();
		LOG.debug("TTTTT noOfTwoByteLengthIOValues : "+noOfTwoByteLengthIOValues);
		for(int i= 0; i < noOfTwoByteLengthIOValues; i++){
			readIOID(dis);
			readIOValue(dis, 2);
		}
	}

	private void readOneByteLengthIOValue(DataInputStream dis) throws IOException {
		int noOfOneByteLengthIOValues =(int)dis.read();
		LOG.debug("TTTTT noOfOneByteLengthIOValues : "+noOfOneByteLengthIOValues);
		for(int i= 0; i < noOfOneByteLengthIOValues; i++){
			readIOID(dis);
			readIOValue(dis, 1);
		}
	}

	private void readIOValue(DataInputStream dis, int lenght) throws IOException {
		byte[] ioValueArray = new byte[lenght];
		for(int i=0; i< lenght; i++){
			ioValueArray[i] = dis.readByte();
		}
		LOG.debug("TTTTT IOValue : "+ByteUtils.toHexAscii(ioValueArray));
	}

	private void readIOID(DataInputStream dis) throws IOException {
		byte ioId = dis.readByte();
		LOG.debug("TTTTT IOID : "+ByteUtils.toHexAscii(ioId));
	}

	/**
	 * Send final response to the server with data element
	 * @param clientSocket
	 * @param recordLength
	 */
	private void sendFinalResponse(Socket clientSocket, int recordLength){
		LOG.debug("TTTTT Sending Final Response to the module");

		try{
			LOG.debug("TTTTT Senidng Record Length :"+recordLength+" as final response");
			DataOutputStream dos = new DataOutputStream(clientSocket.getOutputStream());	
			dos.writeInt(recordLength);
			dos.flush();
			LOG.debug("TTTTT Successfully sent the final response");
		} catch (Exception e){
			LOG.error("TTTTT Error while sending final response",e);
		}

	}
}
