package com.i10n.module.led.utils;

import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;

import org.apache.log4j.Logger;

/**
 * Manages the threads for LED data push to the leds at the bus stops
 * @author Dharmaraju V
 *
 */
public class LEDDataPushHandler implements Runnable {
	
	private static Logger LOG = Logger.getLogger(LEDDataPushHandler.class);
	
	/**
	 * Server listens on this port of LED module requests
	 */
	private final static int LED_PORT = 12540;
	
	/**
	 * Shutdown management variable
	 */
	private static boolean isShutDown = false;

	/**
	 * 1) Create the server socket 
	 * 2) Accept the client connections 
	 * 3) Manage the connections 
	 */
	@Override
	public void run(){
		ServerSocket serverSocket = null;
		try {
			serverSocket = new ServerSocket(LED_PORT);
			LOG.info("Listening on port "+LED_PORT+" for LED Data push");
			HashMap<String , Socket> modulesRequested = new HashMap<String, Socket>();
			while(!isShutDown){
				Socket clientSocket = null;
				try {
					clientSocket = serverSocket.accept();
					LOG.debug("Socket connection accepted from the client");
					InputStream inputStream = clientSocket.getInputStream();
					
					/*
					 * Frame Head 	GPRS ID 	Zone 	Function Code 	Zone valid 	CheckSum 	Data
					 	2bytes 		11byte 		4bytes 	1byte 			1byte 		1bytes 		N bytes*/
					// Reading Single header
					byte[] header = new byte[20];
					//			 byte [] frame = new byte[2];
					//			 inputStream.read(frame);
					inputStream.read(header, 0, 2);

					//			 byte [] gprsId = new byte[11];
					inputStream.read(header, 2, 11);
					//			 inputStream.read(gprsId);

					//			 byte[] zone = new byte[4];
					inputStream.read(header, 13, 4);
					//			 inputStream.read(zone);

					//			 byte[] funcitonCode = new byte[1];
					inputStream.read(header, 17, 1);
					//			 inputStream.read(funcitonCode);

					//			 byte[] zoneValid = new byte[1];
					inputStream.read(header, 18, 1);
					//			 inputStream.read(zoneValid);

					//			 byte[] checkSum = new byte[1];
					inputStream.read(header, 19, 1);
					//			 inputStream.read(checkSum);
					String deviceID = new String(header, 2, 11);
					if(modulesRequested.containsKey(deviceID)){
						LOG.debug("Socket connection already opened for this device");
						Socket closeSocket = modulesRequested.get(deviceID);
						try{
							LOG.debug("Trying to close the old connection");
							closeSocket.close();
							LOG.debug("Successfully closed the old connection");
						} catch (Exception e ){
							LOG.error("Error while closing old socket",e);
						}
					} else {
						LOG.debug("This is a new client connection");
					}
					modulesRequested.put(deviceID, clientSocket);
					
					Thread ledDataPushThread = new Thread(new LEDDataPush(clientSocket, deviceID, header));
					ledDataPushThread.start();
				} catch (IOException e) {
					LOG.error(e);
				} catch (Exception e ){
					LOG.error(e);
				}
			}
		} catch (IOException e) {
			LOG.error("Error while opeing server socket",e);
		} catch (Exception e ){
			LOG.error("Error in LEDDataPushHandler ",e);
		} finally {
			if(serverSocket != null){
				try {
					serverSocket.close();
				} catch (IOException e) {
					LOG.error(e);
				}
			}
		}
		
	}
	
	public static void shutDown(){
		isShutDown = true;
		LEDDataPush.isTerminate = true;
	}

}
