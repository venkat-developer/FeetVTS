package com.i10n.fleet.calixto;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import org.apache.log4j.Logger;

import com.i10n.fleet.calixto.decoder.CalixtoDeviceDecoder;


/**
 * Decoder handler method for Calixto Devices 
 * @author DVasudeva
 *
 */

public class CalixtoDeviceHandler implements Runnable{

	private static Logger LOG = Logger.getLogger(CalixtoDeviceHandler.class);
	private static CalixtoDeviceHandler _instance = null;
	private static int mPort = 8000;
	private static boolean shutDown = false;
	
	public static CalixtoDeviceHandler getInstance(){
		if(_instance == null){
			_instance = new CalixtoDeviceHandler();
			_instance.initialize();
		}
		return _instance;
	}

	/**
	 * Initializing the data decoder server for Calixto Server 
	 */
	public void  initialize(){
		LOG.debug("Initializing Calixto packet receiver Server");
		ServerSocket serverSocket = null;
		try {
			serverSocket = new ServerSocket(mPort);
			LOG.debug("Server socket successfully created on the port : "+mPort);

			while(!shutDown){
				try{
					Socket clientSocket = serverSocket.accept();
					clientSocket.setSoTimeout(60000);
					LOG.debug("TTTTT Client connection established");
					Thread calixtoDeviceDecoderThread = new Thread(new CalixtoDeviceDecoder(clientSocket));
					calixtoDeviceDecoderThread.start();
				} catch (Exception e ){
					LOG.error("Error while reading Calixto packet",e);
				} finally {
					/*if(clientSocket != null){
						try {
							clientSocket.close();
						} catch (IOException e) {
							LOG.error("TTTTT Error while closing the client connection"+e);
						}
					}*/
				}
			}
			LOG.info("SHUTDOWN : Gracefully shutting down Calixto packet process. Port number : "+mPort);
		} catch (IOException e) {
			LOG.error("Error while decoding Calixto packet"+ e);
		} catch (Exception e){
			LOG.error("Handling missed out exception ",e);
		} finally {
			if(serverSocket != null){
				try {
					serverSocket.close();
					LOG.info("Successfully closed the server socket");
				} catch (IOException e) {
					LOG.error("Error while closing server socket",e);
				}
			}

		}
	}

	public void run() {
		CalixtoDeviceHandler calixtoDeviceHandler = new CalixtoDeviceHandler();
		calixtoDeviceHandler.initialize();
	}

	public static void shutDown(){
		shutDown = true;
	}

}


