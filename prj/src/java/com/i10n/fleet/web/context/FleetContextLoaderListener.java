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


		BasicConfigurator.configure();

		EnvironmentInfo.load(this);
		
		LOG.info("Loading cached providers");
		loadCachedDataProviders();
		LOG.debug("Cache Loaded successfully");
		
		
		//LOG.debug("Initializing Pushlet handler");
		//PushletHandler.getInstance();
		//LOG.debug("Successfully initialized PushletHandler");
		
		CometQueue.getCometQueue().startCometQueue();
		LOG.debug("Pushlet thread started");
			
		super.contextInitialized(event);
	}


	private void loadCachedDataProviders ()  {
		CacheManager.loadCache();
	}

	@Override
	public void contextDestroyed(ServletContextEvent servletContextEvent) {

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