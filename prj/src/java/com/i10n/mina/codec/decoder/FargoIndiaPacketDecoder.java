package com.i10n.mina.codec.decoder;

import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import org.apache.log4j.Logger;

import com.i10n.fleet.container.BulkUpdateDataBean;
import com.i10n.fleet.container.GWTrackModuleDataBean;
import com.i10n.module.dataprocessor.DataPool;



/**
 * Decoder logic for FargoIndia client modules
 *  
 * @author Dharmaraju V
 *
 */

public class FargoIndiaPacketDecoder implements Runnable{

	private static Logger LOG = Logger.getLogger(FargoIndiaPacketDecoder.class);

	private static FargoIndiaPacketDecoder _instance = null;
	private static int mPort = 60000;
	private static boolean shutDown = false;
	
	public static FargoIndiaPacketDecoder getInstance(){
		if(_instance == null){
			_instance = new FargoIndiaPacketDecoder();
			_instance.initialize();
		}
		return _instance;
	}

	/*
	 *   Initializing the socket connection for getting module packets
	 */
	public void  initialize(){
		LOG.debug("Initializing FargoIndia packet receiver Server");
		ServerSocket serverSocket = null;
		Socket clientSocket = null;
		while(!shutDown){
			try {
				serverSocket = new ServerSocket(mPort);
				LOG.debug("Server socket successfully created on the port : "+mPort);
				int i = 0;
				while(true){
					// Client socket is closed by the device and not the server
					clientSocket = serverSocket.accept();
					Thread fargoDecoder = new Thread(new FargoDecoder(clientSocket, i));
					fargoDecoder.start();
					i++;
				}
			} catch (IOException e) {
				LOG.error("Error while decoding FargoIndia packet"+ e);
			} catch (Exception e){
				LOG.error("Handling missed out exception ",e);
			} finally {
				if(clientSocket != null){
					try {
						clientSocket.close();
					} catch (IOException e) {
						LOG.error("Error while closing the client socekt ",e);
					}
				}
				if(serverSocket != null){
					try {
						serverSocket.close();
					} catch (IOException e) {
						LOG.error("Error while closing the serverSocket ",e);
					}
				}

			}
		}
	}

	/*
	 * Fields of the module packets  
	 */

	private final int FARGO_PACKET_LENGTH = 19;
	private final int FARGO_IMEI_POS = 0;
	//private final int FARGO_PF_POS = 1;
	private final int FARGO_GPS_POS = 3;
	private final int FARGO_BV_POS = 4;
	private final int FARGO_GSM_POS = 5;
	//	private final int FARGO_STARTER_POS = 6;
	//	private final int FARGO_IGNIT_POS = 7;
	private final int FARGO_DATE_POS = 8;
	private final int FARGO_TIME_POS = 9;
	private final int FARGO_LAT_POS = 10;
	private final int FARGO_LON_POS = 11;
	//	private final int FARGO_AL_POS = 12;
	private final int FARGO_SPEED_POS = 13;
	private final int FARGO_DIR_POS = 14;
	//	private final int FARGO_SAT_POS = 15;
	//	private final int FARGO_HDOP_POS = 16;
	//	private final int FARGO_MI_POS = 17;
	//	private final int FARGO_ANA_POS = 18;



	/*
	 * Main Function
	 */
	public void run() {

		FargoIndiaPacketDecoder fargoIndiaPacketDecoder = new FargoIndiaPacketDecoder();
		fargoIndiaPacketDecoder.initialize();
	}



	private class FargoDecoder implements Runnable {

		private Socket clientSocket;

		private int id;

		public FargoDecoder(Socket clientSocket, int id ) {
			this.clientSocket= clientSocket;
			this.id = id;
		}

		@Override
		public void run() {
			InputStream inputStream;
			try {
				inputStream = clientSocket.getInputStream();
				String inputString = new String();
				try{
					clientSocket.setSoTimeout(20000);
					readingBytes(inputStream,inputString);
				} catch(Exception e ){
					LOG.error("ID:"+this.id+" Not enough bytes received hence clearing the latest packet detials read"+e);
				}
			} catch (IOException e) {
				LOG.error("ID:"+this.id+" ",e);
			} catch (Exception e){
				LOG.error("ID:"+this.id+" Hanlding left out exceptions ",e);
			} finally {
				LOG.debug("ID:"+this.id+" Waiting for socket close for the device");
				Calendar cal = Calendar.getInstance();
				Date startDate = cal.getTime();
				while(isConnected(clientSocket));
				Date endDate = cal.getTime();
				LOG.debug("ID:"+this.id+" Socket successfully closed from the client. Time taken(ms) : "
						+(endDate.getTime()- startDate.getTime()));
			}
			LOG.debug("ID:"+this.id+" Finished processing FargoIndia packet");
		}

		private boolean isConnected(Socket socket){
			synchronized (socket) {
				try {
					socket.getOutputStream().write(0);
				} catch (IOException e) {
					return false;
				}
			}
			return true;
		}

		/*
		 *  Reading packets byte by byte and generate full packet
		 */

		private void readingBytes(InputStream inputStream,String inputString ) throws IOException, InterruptedException {
			/**
			 * Read all the bytes until the end string received
			 */
			do{
				byte input = (byte)inputStream.read();
				inputString += (new String(new byte[]{input}));
			}while(!inputString.endsWith("!") && inputString.startsWith("@"));

			if(inputString.length() != 0){
				LOG.debug("ID:"+this.id+" INPUT STRING : "+inputString);
				String[] packetArray = inputString.split("!");
				for(String packet : packetArray){
					LOG.debug("ID:"+this.id+" Packet : "+packet);
				}
			} else {
				LOG.error("ID:"+this.id+" Packet read was an invalid one ");
			}
			try {
				/**
				 * Convert the data read to Gwtrack packet format 
				 */
				formulateInputDataToPacket(inputString);
			} catch (NumberFormatException e) {
				LOG.error("ID:"+this.id+" ", e);
			} catch (ParseException e) {
				LOG.error("ID:"+this.id+" ",e);
			} catch (Exception e){
				LOG.error(e);
			}
		}

		/**
		 *      Adding the packets to the DataPool
		 */

		private void formulateInputDataToPacket(String inputString) throws NumberFormatException, ParseException {

			String[] packetData = inputString.split(",");
			GWTrackModuleDataBean bean = new GWTrackModuleDataBean();
			if(packetData.length == FARGO_PACKET_LENGTH){
			
				  DateFormat utcFormat1 = new SimpleDateFormat("yy/MM/dd HH:mm:ss");
				  utcFormat1.setTimeZone(TimeZone.getTimeZone("UTC"));
				  DateFormat indianFormat = new SimpleDateFormat("yy/MM/dd HH:mm:ss");
				  indianFormat.setTimeZone(TimeZone.getTimeZone("Asia/Kolkata"));
				  Date timestamp=null;
				try {
					LOG.debug("packetData[FARGO_DATE_POS]"+packetData[FARGO_DATE_POS]);
					LOG.debug("packetData[FARGO_TIME_POS]:"+packetData[FARGO_TIME_POS]);
					String date=packetData[FARGO_DATE_POS]+" "+packetData[FARGO_TIME_POS];
					timestamp = utcFormat1.parse(date);
					LOG.debug("timestamp:"+timestamp);
				} catch (ParseException e) {
					LOG.error(e);
				}
				  String finalTime = indianFormat.format(timestamp);
				  LOG.debug("OUTPUT:"+finalTime);
			
				String[] packetDataimei = packetData[FARGO_IMEI_POS].split("@");
				String imei=packetDataimei[1];
				bean.setImei(imei);
				bean.setGpsSignalStrength(Double.parseDouble(packetData[FARGO_GPS_POS]));
				bean.setModuleBatteryVoltage(Double.parseDouble(packetData[FARGO_BV_POS]));
				bean.setMaxSpeed(Double.parseDouble(packetData[FARGO_SPEED_POS]));
				DateFormat formatter ; 
				formatter = new SimpleDateFormat("yy/MM/dd HH:mm:ss");
				ArrayList<BulkUpdateDataBean> bulkDataList=new ArrayList<BulkUpdateDataBean>();
				BulkUpdateDataBean temp = new BulkUpdateDataBean(Double.parseDouble(packetData[FARGO_LAT_POS]),
						Double.parseDouble(packetData[FARGO_LON_POS]), 0.0, 
						formatter.parse(finalTime),0);
				temp.setImei(imei);
				bulkDataList.add(temp);
				bean.setBulkUpdateData(bulkDataList);
				bean.setGsmSignalStrength(Double.parseDouble(packetData[FARGO_GSM_POS]));
				bean.setPktCount(0);
				bean.setVehicleCourse(Double.parseDouble(packetData[FARGO_DIR_POS]));					
				bean.setNumberOfPacketSendingAttempts(0);
				bean.setNumberOfSuccessPackets(0);			
				bean.setCumulativeDistance(0);
				bean.setAnalogue1(0);						
				bean.setAnalogue2(0);
				bean.setNumberOfErrorsInHardwareModule(0);	
				bean.setLocationAreaCode(0);
				bean.setCellId(0);							
				bean.setFirmwareVersion(13);
				bean.setModuleUpdateTime(new Date());

				LOG.debug("ID:"+this.id+" Bean Data is:"+bean.toString());
				DataPool.addPacket(bean);
				LOG.debug("ID:"+this.id+" Packets Added the DataPool");
			} else {
				LOG.debug("ID:"+this.id+" Read data packet lenght : "+packetData.length
						+" is not mathcing the valid packet length : "+FARGO_PACKET_LENGTH);
			}
		}
	

	}
}
