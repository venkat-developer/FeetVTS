package com.i10n.fleet.web.context;

import javax.servlet.ServletContextEvent;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;
import org.springframework.web.context.ContextLoaderListener;

import com.i10n.dbCacheManager.CacheManager;
import com.i10n.fleet.util.CometQueue;
import com.i10n.fleet.util.EnvironmentInfo;
import com.i10n.fleet.util.PushletHandler;

/**
 * Custom {@link ContextLoaderListener} for Fleetcheck
 * 
 * @see ContextLoaderListener
 * 
 */
public class FleetContextLoaderListener extends ContextLoaderListener {

	private static final Logger LOG = Logger.getLogger(FleetContextLoaderListener.class);
	
	/**
	 * Custom Initialization for Fleet to initialize fleet 
	 * 
	 * @see {@link ContextLoaderListener#contextInitialized(ServletContextEvent)}
	 */
	@Override
	public void contextInitialized(ServletContextEvent event) { 

		//LOG.info("Intializing context.");

		BasicConfigurator.configure();

		EnvironmentInfo.load(this);
		
		LOG.info("Loading cached providers");
		// maintaining cache for faster access
		loadCachedDataProviders();
		LOG.debug("Cache Loaded successfully");
		
		
		LOG.debug("Initializing Pushlet handler");
		PushletHandler.getInstance();
		LOG.debug("Successfully initialized PushletHandler");
		
		// Initializing data pool 
//		DataPool.getDataPoolInstance();
//		LOG.debug("DataPool Initialized successfully");
		
//		DataBasePushHandler.getInstance();
//		LOG.debug("DataBasePushHandler Initialized successfully");
//
		CometQueue.getCometQueue().startCometQueue();
		LOG.debug("Pushlet thread started");
//
//		AddressRetrieverEngine.getInstance();
//		LOG.debug("Address Retriever thread started");

//		initializeViolationAlertThread();
//		initializeMailSenderThread();
//		
//		initializeLEDDataHandler();
//		initializeFargoPacketReader();
//		initializeTeltonikaPacketReader();
//		initializeCalixtoPacketReader();
//
//		if(Boolean.valueOf(Boolean.valueOf(EnvironmentInfo.getProperty("IS_PACKET_BACKUP_ENABLED")))){
//			//Reading the backed up packets from the file and adding back them to data pool before listening to server socket
//			ReadWriteJsonToFIle.readDataPacketFromJson();
//		}
//
//		PacketServer.initialize ();
//		LOG.debug("Module socket server");
//		/*
//		 * Enable the simulator by changing the DataProcessorParameters.IS_SIMULATOR_ENABLED boolean value
//		 */
//		if(Boolean.valueOf(EnvironmentInfo.getProperty("IS_SIMULATOR_ENABLED"))){
//			Simulator mSimulator=new Simulator();
//			mSimulator.start();
//		}
			
		super.contextInitialized(event);
	}

	/*private void initializeViolationAlertThread() {
		InstantViolationAlerts instantViolationAlerts = new InstantViolationAlerts();
		Thread violationAlertsThread = new Thread(instantViolationAlerts);
		violationAlertsThread.start();
		LOG.debug("Violation alerts Thread initialized");		
	}

	private void initializeMailSenderThread() {
		MailSender mailSenderInstance = new MailSender();
		Thread mailThread = new Thread(mailSenderInstance);
		mailThread.start();
		LOG.debug("Mail daemon created");
	}

	private void initializeLEDDataHandler() {
		if(Boolean.valueOf(EnvironmentInfo.getProperty("IS_ETA_ENABLED"))){
			LEDDataPushHandler ledDataPushHandler = new LEDDataPushHandler();
			Thread ledDataPushThread = new Thread(ledDataPushHandler);
			ledDataPushThread.start();
			LOG.debug("Initialized LED data push handler ");
		}
	}
	
	private void initializeFargoPacketReader() {
		if(Boolean.valueOf(EnvironmentInfo.getProperty("IS_FARGOINDIA_MODULE_ENABLED"))){
			LOG.debug("FargoIndiaPacketDecoder Thread Started");
			FargoIndiaPacketDecoder fargo = new FargoIndiaPacketDecoder();
			Thread fargoindiapacket = new Thread(fargo);
			fargoindiapacket.start();
			LOG.debug("FargoIndiaPacketDecoder finished");
		}
	}

	private void initializeCalixtoPacketReader() {
		if(Boolean.valueOf(EnvironmentInfo.getProperty("IS_CALIXTO_ENABLED"))){
			LOG.debug("Initializing Calixto device handler Thread");
			CalixtoDeviceHandler calixtoDeviceHandler = new CalixtoDeviceHandler();
			Thread calixtoDeviceHandlerThread = new Thread(calixtoDeviceHandler);
			calixtoDeviceHandlerThread.start();
			LOG.debug(" Calixto device handle initialization completed");
		}
	}

	private void initializeTeltonikaPacketReader() {
		LOG.debug("FMDecoder Thread Started");
		FMDecoder fmdecode = new FMDecoder();
		Thread fm = new Thread(fmdecode);
		fm.start();
		LOG.debug("FMDecoderDecoder finished");
	}
*/
	private void loadCachedDataProviders ()  {
		CacheManager.loadCache();
	}

	@Override
	public void contextDestroyed(ServletContextEvent servletContextEvent) {

//		if(Boolean.valueOf(EnvironmentInfo.getProperty("IS_ETA_ENABLED"))){
//			LOG.info("Stopping LED Data push thread");
//			LEDDataPushHandler.shutDown();
//		}
//
//		LOG.info("Stopping Address Retriever thread");
//		AddressRetrieverEngine.getInstance().stop();
//
//		LOG.info("SHUTDOWN: Stopping Comet Threads");
//		CometQueue.getCometQueue().stopCometQueue();
//
//		LOG.info("SHUTDOWN: Data Receiver socket shutdown and socket connection closed");
//		PacketServer.shutdown();		
//
//		DataBasePushHandler.getInstance().stopDataBasePushHandler();
//		LOG.info("Released all db processing threads");		

		Thread[] threads = new Thread[Thread.activeCount()];
		int numberOfThreads = Thread.enumerate(threads);
		LOG.info("SHUTDOWN: Active threads after shutdown : "+numberOfThreads);

		/** Checking active threads after shutdown **/
		StackTraceElement[] stackTraceElement;	
		for (int k = 0; k < numberOfThreads; k++) {
			stackTraceElement = threads[k].getStackTrace();
			for (int j = 0; j < stackTraceElement.length; j++) {
				try {
					LOG.info("SHUTDOWN: GetStackTraceElement: [" + k + ", " + j + "] "
							+ stackTraceElement[j].toString());
				} catch (Exception e) {
					LOG.info("Exception while receiving active thread info after shutdown ",e);
				}
			}
		}

	}
}