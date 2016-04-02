package com.i10n.teltonika;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import org.apache.log4j.Logger;


/**
 * Decoder logic for FM1100 client modules
 *
 * @author Gobinath, Dharmaraju V
 *
 */

public class FMDecoder implements Runnable{

	private static Logger LOG = Logger.getLogger(FMDecoder.class);
	private static FMDecoder _instance = null;
	private static int mPort = 60000;
	private static boolean shutDown = false;
	
	public static FMDecoder getInstance(){
		if(_instance == null){
			_instance = new FMDecoder();
			_instance.initialize();
		}
		return _instance;
	}

	/*
	 *   Initialising the socket connection for getting module packets
	 */
	public void  initialize(){
		LOG.info("TTTTT Initializing Teltonika packet receiver Server");
		ServerSocket serverSocket = null;
		try {
			serverSocket = new ServerSocket(mPort);
			LOG.info("TTTTT Server socket successfully created on the port : "+mPort);

			while(!shutDown){
				try{
					Socket clientSocket = serverSocket.accept();
					clientSocket.setSoTimeout(60000);
					LOG.debug("TTTTT Client connection established");
					Thread avlDataReadThread = new Thread(new TeltonikaDataReader(clientSocket));
					avlDataReadThread.start();
				} catch (Exception e ){
					LOG.error("TTTTT Error while reading avl data packet",e);
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
			LOG.info("SHUTDOWN : Gracefully shutting down Teltonika process. Port number : "+mPort);
		} catch (IOException e) {
			LOG.error("TTTTT Error while decoding FargoIndia packet"+ e);
		} catch (Exception e){
			LOG.error("TTTTT Handling missed out exception ",e);
		} finally {
			if(serverSocket != null){
				try {
					serverSocket.close();
					LOG.info("TTTTT Successfully closed the server socket");
				} catch (IOException e) {
					LOG.error("TTTTT Error while closing server socket",e);
				}
			}

		}
	}

	/**
	 * Main Function
	 */
	public void run() {
		FMDecoder fmDecoder = new FMDecoder();
		fmDecoder.initialize();
	}

	public static void shutDown(){
		shutDown = true;
	}

}


