package com.i10n.fleet.util;

import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;

import com.i10n.module.dataprocessor.DataPool;

/**
 * @author HEMANT
 *
 */
public class DataBasePushHandler  {
	
	private static final Logger LOG = Logger.getLogger(DataBasePushHandler.class);
		
	private static DataBasePushHandler _instance = null;
	ThreadPoolExecutor executorService = null;
	Thread thread;
	private final int threadPoolSize;
	int threadId = 0;
	
	public static boolean shutdownInitiated = false;

	private DataBasePushHandler(){
		/* As the features and processes are being plugged to PushToDB and hence the size of the pool has to be increased.*/
		threadPoolSize = 100;	 
		executorService = (ThreadPoolExecutor) Executors.newFixedThreadPool(threadPoolSize);
		initialize();
	}
	
	public static DataBasePushHandler getInstance () {
		if (_instance == null) {
			_instance = new DataBasePushHandler();
		}
		return _instance;
	}
	
	private void initialize() {
        for(int i=0; i< threadPoolSize; i++){
        	try {
            	executorService.execute(new PushToDB(threadId++));
            } catch (Exception e) {
            	LOG.error("",e);
            }
        }	        
    }
	
	public void stopDataBasePushHandler(){
		shutdownInitiated = true;
		DataPool.emptyPool();
        kill();
	}
	
	private void kill(){
    	// Taken from http://java.sun.com/javase/6/docs/api/java/util/concurrent/ExecutorService.html#shutdown()
		executorService.shutdown();
        try {
        	// Wait a while for existing tasks to terminate
            if (!executorService.awaitTermination(60, TimeUnit.SECONDS)) {
            	executorService.shutdownNow(); // Cancel currently executing tasks
                // Wait a while for tasks to respond to being cancelled
                if (!executorService.awaitTermination(60, TimeUnit.SECONDS)) {
                    LOG.warn("Pool didn't termitate");
                }
            }
        } catch (InterruptedException ie) {
            // (Re-)Cancel if current thread also interrupted
        	executorService.shutdownNow();
            // Preserve interrupt status
            Thread.currentThread().interrupt();
        }
    }
	
	
}
