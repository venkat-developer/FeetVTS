package com.i10n.module.dataprocessor;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Calendar;
import java.util.Date;

import org.apache.log4j.Logger;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import com.google.gson.Gson;
import com.i10n.fleet.container.BulkUpdateDataBean;
import com.i10n.fleet.container.GWTrackModuleDataBean;
import com.i10n.fleet.util.EnvironmentInfo;

/**
 * Enable or disable the versions required by changing the boolean values in Boolean.valueOf(EnvironmentInfo.getProperty("IS_ETA_ENABLED")).java class 
 * 
 * @author KChitturi
 *
 */
public class Simulator extends Thread {
	private static boolean startSimulator=true;
	//ByteArray holding android module data
	private ByteArrayOutputStream mAndroidByteArrayOutputStream;
	private DataOutputStream mAndroidDataOutputStream;
	//ByteArray holding V4 module data
	private ByteArrayOutputStream mV4ByteArrayOutputStream;
	private DataOutputStream mV4DataOutputStream;
	//ByteArray holding V5 module data
	private ByteArrayOutputStream mV5ByteArrayOutputStream;
	private DataOutputStream mV5DataOutputStream;
	//ByteArray holding Teltonika module data
	private ByteArrayOutputStream mTeltonikaByteArrayOutputStream;
	private DataOutputStream mTeltonikaDataOutputStream;
	public static Logger LOG = Logger.getLogger(Simulator.class);
	private String mImei="222222222222222";


	@Override
	public void run() {
		while (startSimulator) {
			LOG.debug("in construct packet method ");
			readAndWriteIntoDataBean();
		}
		super.run();
	}

	/**
	 * From which activated module data is sent
	 */
	private void readAndWriteIntoDataBean() {
		JSONParser parser = new JSONParser();
		FileReader mFileReader;
		try {
			mFileReader = new  FileReader(new File("/usr/local/tomcat6/webapps/test.txt"));
			JSONArray mArray = (JSONArray) parser.parse(mFileReader);
			for (int i = 0; i < mArray.size(); i++) {
				mAndroidByteArrayOutputStream=new ByteArrayOutputStream();
				mAndroidDataOutputStream=new DataOutputStream(mAndroidByteArrayOutputStream);
				mV4ByteArrayOutputStream=new ByteArrayOutputStream();
				mV4DataOutputStream=new DataOutputStream(mV4ByteArrayOutputStream);
				mV5ByteArrayOutputStream=new ByteArrayOutputStream();
				mV5DataOutputStream=new DataOutputStream(mV5ByteArrayOutputStream);
				mTeltonikaByteArrayOutputStream=new ByteArrayOutputStream();
				mTeltonikaDataOutputStream=new DataOutputStream(mTeltonikaByteArrayOutputStream);
				JSONObject mObject =  (JSONObject) mArray.get(i);
				Gson mGson=new Gson();
				GWTrackModuleDataBean moduleDataBean=mGson.fromJson(mObject.toJSONString(), GWTrackModuleDataBean.class);
				LOG.debug("in json parsing and the data packet is "+moduleDataBean.toString());
				if(Boolean.valueOf(EnvironmentInfo.getProperty("IS_ANDROID_ENABLED"))){
					this.sendPacket(constructAndroidDataPacket(mAndroidDataOutputStream,moduleDataBean),
							Integer.parseInt((EnvironmentInfo.getProperty("DATAPACKET_PORT_NUMBER"))));
					try {
						Thread.sleep(Integer.parseInt((EnvironmentInfo.getProperty("PACKET_INTERVAL"))));
					} catch (InterruptedException e) {
						LOG.error("Thread interrupted Exception "+e.getMessage());
					}
				}
				if(Boolean.valueOf(EnvironmentInfo.getProperty("IS_V4_ENABLED"))){
					this.sendPacket(constructV4DataPacket(mV4DataOutputStream, moduleDataBean),
							Integer.parseInt((EnvironmentInfo.getProperty("DATAPACKET_PORT_NUMBER"))));
					try {
						Thread.sleep(Integer.parseInt((EnvironmentInfo.getProperty("PACKET_INTERVAL"))));
					} catch (InterruptedException e) {
						LOG.error("Thread interrupted Exception "+e.getMessage());
					}
				}
				if(Boolean.valueOf(EnvironmentInfo.getProperty("IS_V5_ENABLED"))){
					this.sendPacket(constructV5DataPacket(mV5DataOutputStream, moduleDataBean),
							Integer.parseInt((EnvironmentInfo.getProperty("DATAPACKET_PORT_NUMBER"))));
					try {
						Thread.sleep(Integer.parseInt((EnvironmentInfo.getProperty("PACKET_INTERVAL"))));
					} catch (InterruptedException e) {
						LOG.error("Thread interrupted Exception "+e.getMessage());
					}
				}
				if(Boolean.valueOf(EnvironmentInfo.getProperty("IS_TELTONIKA_ENABLED"))){
					ByteArrayOutputStream mArrayOutputStream=new ByteArrayOutputStream();
					DataOutputStream mDataOutputStream=new DataOutputStream(mArrayOutputStream);
					mDataOutputStream.writeShort(15);
					mDataOutputStream.write(mImei.getBytes());
					Socket mSocket=null;
					try{
						mSocket=new Socket("localhost", Integer.parseInt((EnvironmentInfo.getProperty("TELTONIKA_PORT_NUMBER"))));
						mSocket.setSoTimeout(10000);
						OutputStream os = mSocket.getOutputStream();
						InputStream is = mSocket.getInputStream();
						os.write(mArrayOutputStream.toByteArray());
						os.flush();
						DataInputStream mDataInputStream=new DataInputStream(is);
						Byte mResponse=mDataInputStream.readByte();
						if(mResponse==1){
							this.sendPacket(constructTeltonikaDataPacket(mTeltonikaDataOutputStream, moduleDataBean),mSocket);
						}else{
							LOG.debug("Response sent is not the expected response "+mResponse);
						}
					}catch(Exception e){
						LOG.error("Exception while sending teltonika packet "+e.getMessage());
					}finally{
						if(mSocket != null)
							try {
								mSocket.close();
							} catch (IOException e) {
								LOG.error(" IO Exception while sending teltonika packet "+e.getMessage());
							}
					}
					try {
						Thread.sleep(Integer.parseInt((EnvironmentInfo.getProperty("PACKET_INTERVAL"))));
					} catch (InterruptedException e) {
						LOG.error("Thread interrupted Exception "+e.getMessage());					}
				}
			}   
			mFileReader.close();
		} catch (FileNotFoundException e) {
			LOG.error("File notfound exception  "+e.getMessage());
		} catch (IOException e) {
			LOG.error("IO exception  "+e.getMessage());
		} catch (ParseException e) {
			LOG.error("Parse exception  "+e.getMessage());
		}
	}



	private void sendPacket(ByteArrayOutputStream constructTeltonikaDataPacket,
			Socket mSocket) {
		OutputStream os;
		try {
			os = mSocket.getOutputStream();
			os.write(constructTeltonikaDataPacket.toByteArray());
			os.flush();
		} catch (IOException e) {
			LOG.error("IO Exception while sending teltonika packet "+e.getMessage());
		}finally{
			try{
				mSocket.close();
			}catch(IOException e){
				LOG.error("IO Exception while sending teltonika packet "+e.getMessage());
			}
		}
	}

	/**
	 * Method constructing packet for V4 module and imei of the module is '444444444444444'
	 * @param dataos
	 * @param moduleDataBean
	 * @return
	 */
	private ByteArrayOutputStream constructV4DataPacket(DataOutputStream dataos,GWTrackModuleDataBean moduleDataBean) {
		BulkUpdateDataBean mBulkUpdateDataBean= moduleDataBean.getBulkUpdateData().get(0);
		LOG.debug("in Construct packet method for v4 module and data packet is "+mBulkUpdateDataBean.getLatitude()+"  " +mBulkUpdateDataBean.getLongitude());

		try{
			//add count of packets data 
			dataos.writeShort(1);

			//add gps signal
			dataos.writeByte((int) moduleDataBean.getGpsSignalStrength());

			//add azimuth
			dataos.writeShort(0);

			//add gsm signal
			dataos.writeByte((int)moduleDataBean.getGsmSignalStrength());

			//total datapackets sent
			dataos.writeInt((int) moduleDataBean.getNumberOfPacketSendingAttempts());

			//successfuldatapackets sent
			dataos.writeInt((int) moduleDataBean.getNumberOfSuccessPackets());

			//BatteryVoltage
			dataos.writeShort((short)moduleDataBean.getModuleBatteryVoltage()); 

			//cumulative distance
			dataos.writeInt((int)moduleDataBean.getCumulativeDistance()); //: add cum distance logic

			//speed
			dataos.writeShort((short)moduleDataBean.getMaxSpeed());

			//fuel a2d data
			dataos.writeShort(0);

			//engine oil data 
			dataos.writeShort((short)mBulkUpdateDataBean.getFuel());

			//error
			dataos.writeByte(0);

			//LAC
			dataos.writeInt(moduleDataBean.getLocationAreaCode());

			//CID
			dataos.writeInt(moduleDataBean.getCellId());

			//BitwiseData
			dataos.writeByte(0);

			//version
			dataos.writeByte(6);
			String mString="444444444444444";
			//imei of the module
			dataos.write(mString.getBytes());

			//latitude
			dataos.writeInt(converttoLittleEndian((int)(mBulkUpdateDataBean.getLatitude()*10000000.0D)));
			//longitude
			dataos.writeInt(converttoLittleEndian((int)(mBulkUpdateDataBean.getLongitude()*10000000.0D)));

			//delta distance
			dataos.writeShort(converttoLittleEndian((short)mBulkUpdateDataBean.getDeltaDistance()));

			Date mDate=new Date();
			//tow
			dataos.writeInt(converttoLittleEndian((int) timeOftheweek(mDate.getTime()-19800000)));
			//Fuel
			dataos.writeShort(converttoLittleEndian((short)mBulkUpdateDataBean.getFuel()));
			byte check=writeByteWithChecksum(mV4ByteArrayOutputStream);
			//CRC
			dataos.writeByte(check);
		} catch(IOException e){
			LOG.error("IO Exception while constructing V4 module packet "+e.getMessage());
		}

		return mV4ByteArrayOutputStream;

	}


	/**
	 *  Method constructing packet for V5 module and imei of the module is '555555555555555'
	 * @param dataos
	 * @param moduleDataBean
	 * @return
	 */
	@SuppressWarnings("static-access")
	private ByteArrayOutputStream constructV5DataPacket(DataOutputStream dataos,GWTrackModuleDataBean moduleDataBean) {
		BulkUpdateDataBean mBulkUpdateDataBean= moduleDataBean.getBulkUpdateData().get(0);
		LOG.debug("in Construct packet method for v5 module and data packet is "+mBulkUpdateDataBean.getLatitude()+"  " +mBulkUpdateDataBean.getLongitude());

		try{
			//add count of packets data 
			dataos.writeShort(1);

			//add gps signal
			dataos.writeByte((int) moduleDataBean.getGpsSignalStrength());

			//add azimuth
			dataos.writeShort(0);

			//add gsm signal
			dataos.writeByte((int)moduleDataBean.getGsmSignalStrength());

			//total datapackets sent
			dataos.writeInt((int) moduleDataBean.getNumberOfPacketSendingAttempts());

			//successfuldatapackets sent
			dataos.writeInt((int) moduleDataBean.getNumberOfSuccessPackets());

			//BatteryVoltage
			dataos.writeShort((short)moduleDataBean.getModuleBatteryVoltage()); 

			//cumulative distance
			dataos.writeInt((int)moduleDataBean.getCumulativeDistance()); //: add cum distance logic

			//speed
			dataos.writeShort((short)moduleDataBean.getMaxSpeed());

			//fuel a2d data
			dataos.writeShort(0);

			//engine oil data 
			dataos.writeShort((short)mBulkUpdateDataBean.getFuel());

			//error
			dataos.writeByte(0);

			//LAC
			dataos.writeInt(moduleDataBean.getLocationAreaCode());

			//CID
			dataos.writeInt(moduleDataBean.getCellId());

			//BitwiseData
			dataos.writeByte(0);

			//version
			dataos.writeByte(8);
			String mString="555555555555555";
			//imei of the module
			dataos.write(mString.getBytes());

			//latitude
			dataos.writeInt(converttoLittleEndian((int)(mBulkUpdateDataBean.getLatitude()*10000000.0D)));
			//longitude
			dataos.writeInt(converttoLittleEndian((int)(mBulkUpdateDataBean.getLongitude()*10000000.0D)));

			//delta distance
			dataos.writeShort(converttoLittleEndian((short)mBulkUpdateDataBean.getDeltaDistance()));

			//time
			Date mDate=new Date();
			//			SimpleDateFormat mDateFormat=new SimpleDateFormat("yy");
			Calendar mCalendar=Calendar.getInstance();
			mCalendar.setTimeInMillis(mDate.getTime()-19800000);
			dataos.write(mCalendar.get(Calendar.SECOND));
			dataos.write(mCalendar.get(mCalendar.MINUTE));
			dataos.write(mCalendar.get(mCalendar.HOUR));
			dataos.write(0);
			//Fuel
			dataos.writeShort(converttoLittleEndian((short)mBulkUpdateDataBean.getFuel()));
			//date
			dataos.write(mCalendar.get(mCalendar.DATE));
			dataos.write(mCalendar.get(mCalendar.MONTH)+1);
			int year=mCalendar.get(mCalendar.YEAR);
			int year1=year & 0xFF;
			int year2=(year >> 8) & 0xFF;
			dataos.write(year1);
			dataos.write(year2);
			byte check=writeByteWithChecksum(mV5ByteArrayOutputStream);
			//CRC
			dataos.writeByte(check);
		} catch(IOException e){
			LOG.error("IO Exception while constructing V5 module packet "+e.getMessage());
		}

		return mV5ByteArrayOutputStream;

	}


	/**
	 * 
	 * Method constructing packet for android module and imei of the module is '111111111111111'
	 * @param dataos
	 * @param moduleDataBean
	 * @return
	 */
	private ByteArrayOutputStream constructAndroidDataPacket(DataOutputStream dataos, GWTrackModuleDataBean moduleDataBean) {
		BulkUpdateDataBean mBulkUpdateDataBean= moduleDataBean.getBulkUpdateData().get(0);
		LOG.debug("in Construct packet method for android module and data packet is "+mBulkUpdateDataBean.getLatitude()+"  " +mBulkUpdateDataBean.getLongitude());

		try{
			//add count of packets data 
			dataos.writeShort(1);

			//add gps signal
			dataos.writeByte((int)moduleDataBean.getGpsSignalStrength());

			//add azimuth
			dataos.writeShort(0);

			//add gsm signal
			dataos.writeByte((int)moduleDataBean.getGsmSignalStrength());

			//total datapackets sent
			dataos.writeInt((int) moduleDataBean.getNumberOfPacketSendingAttempts());

			//successfuldatapackets sent
			dataos.writeInt((int) moduleDataBean.getNumberOfSuccessPackets());

			//BatteryVoltage
			dataos.writeShort((int)moduleDataBean.getModuleBatteryVoltage()); 

			//cumulative distance
			dataos.writeInt((int)moduleDataBean.getCumulativeDistance()); //: add cum distance logic

			//speed
			dataos.writeShort((short)moduleDataBean.getMaxSpeed());

			//fuel a2d data
			dataos.writeShort((short)mBulkUpdateDataBean.getFuel());

			//engine oil data 
			dataos.writeShort((short)moduleDataBean.getAnalogue1());

			//error
			dataos.writeByte(0);

			//LAC
			dataos.writeInt(moduleDataBean.getLocationAreaCode());

			//CID
			dataos.writeInt(moduleDataBean.getCellId());

			//BitwiseData
			dataos.writeByte(0);

			//version
			dataos.writeByte(11);

			String mString="111111111111111";
			//imei of the module
			dataos.write(mString.getBytes());

			//latitude
			dataos.writeInt((int)(mBulkUpdateDataBean.getLatitude()*10000000.0D));

			//longitude
			dataos.writeInt((int)(mBulkUpdateDataBean.getLongitude()*10000000.0D));

			//delta distance
			dataos.writeShort((int)mBulkUpdateDataBean.getDeltaDistance());

			Date mDate=new Date();
			//tow
			dataos.writeLong(mDate.getTime());
			dataos.writeShort((int)255);
			byte check=writeByteWithChecksum(mAndroidByteArrayOutputStream);
			//CRC
			dataos.writeByte(check);
		} catch(IOException e){
			LOG.error("IO Exception while constructing android module packet "+e.getMessage());
		}

		return mAndroidByteArrayOutputStream;

	}

	/**
	 * @param baos
	 * @param port 
	 */
	private void sendPacket(ByteArrayOutputStream baos, int port) {
		Socket mSocket=null;
		LOG.debug("in send packet method and port is "+port+" and bytearray stream length is "+baos.size());
		try{
			mSocket=new Socket("localhost", port);
			mSocket.setSoTimeout(10000);
			OutputStream os = mSocket.getOutputStream();
			InputStream is = mSocket.getInputStream();
			os.write(baos.toByteArray());
			os.flush();
			DataInputStream mDataInputStream=new DataInputStream(is);
			int response=mDataInputStream.readByte();
			if(response==1)
				LOG.debug("Received success response from the server :: "+response);
			else if(response==4)
				LOG.debug("Received fialure response from the server ::"+response);
		}catch(Exception e){
			LOG.error("Exception while sending the packet to the socket "+e.getMessage());
		}finally{
			try {
				mSocket.close();
			} catch (IOException e) {
				LOG.error("IO Exception while sending the packet "+e.getMessage());
			}
		}
	}

	public static byte writeByteWithChecksum(ByteArrayOutputStream bos)
			throws IOException {
		// long based checksum will work well until 2^7 after that there will be
		// an unexpected behaviour
		byte checksum = 0;
		byte[] data = bos.toByteArray();
		for (int i = 0; i < data.length; i++) {
			checksum ^= (data[i]);
		}
		return checksum;
	}


	/**
	 * Change the current time to TOW 
	 * @param currentGpsTime
	 * @return
	 */
	public static int timeOftheweek(long currentTime) {

		Calendar thisMonth = Calendar.getInstance();
		int dayOfWeek = Calendar.SUNDAY; // or whatever
		thisMonth.set(Calendar.DAY_OF_WEEK, dayOfWeek);
		thisMonth.set(Calendar.HOUR_OF_DAY, 0);
		thisMonth.set(Calendar.MINUTE, 0);
		thisMonth.set(Calendar.SECOND, 0);
		return (int) (currentTime - thisMonth.getTimeInMillis());
	}

	/**
	 * Converts BigEndian int to LittleEndian int
	 * @param i
	 * @return
	 */
	private int converttoLittleEndian(int i) {

		return((i&0xff)<<24)+((i&0xff00)<<8)+((i&0xff0000)>>8)+((i>>24)&0xff);
	} 


	/**
	 *  Method constructing packet for Teltonika module and imei of the module is '222222222222222'
	 * @param dataos
	 * @param moduleDataBean
	 * @return
	 */
	private ByteArrayOutputStream constructTeltonikaDataPacket(DataOutputStream dataos,GWTrackModuleDataBean moduleDataBean) {
		BulkUpdateDataBean mBulkUpdateDataBean= moduleDataBean.getBulkUpdateData().get(0);
		LOG.debug("in Construct packet method for Teltonika module and data packet is "+mBulkUpdateDataBean.getLatitude()+"  " +mBulkUpdateDataBean.getLongitude());

		try{
			//Zero Bytes 
			dataos.writeInt(0);

			//Length of remaining bytes
			dataos.writeInt(37);

			//Codec Id
			dataos.write(8);

			//Number of bulk packets
			dataos.write(moduleDataBean.getBulkUpdateData().size());
			Date mDate=new Date();
			//Time in UTC
			dataos.writeLong(mDate.getTime());

			//Priority value
			dataos.write(0);

			//Longitude
			dataos.writeInt((int)(mBulkUpdateDataBean.getLongitude()*10000000.0D)); 

			//Latitude
			dataos.writeInt((int)(mBulkUpdateDataBean.getLatitude()*10000000.0D));

			//Altitude
			dataos.writeShort(902);

			//Direction
			dataos.writeShort((int) moduleDataBean.getVehicleCourse());

			//Satellite count always 1 
			dataos.write(1);

			//Speed
			dataos.writeShort((int) moduleDataBean.getMaxSpeed());

			//Dummy data
			dataos.writeInt(0);
			dataos.writeShort(0);

			//Number of bulk packets in the end for padding
			dataos.write(moduleDataBean.getBulkUpdateData().size());

			int check=writeByteWithChecksum(mTeltonikaByteArrayOutputStream);
			//CRC
			dataos.writeInt(check);
		} catch(IOException e){
			LOG.error("IO Exception while constructing Teltonika module packet "+e.getMessage());
		}

		return mTeltonikaByteArrayOutputStream;

	}
}
