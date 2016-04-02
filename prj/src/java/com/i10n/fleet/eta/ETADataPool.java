package com.i10n.fleet.eta;

import java.util.concurrent.ArrayBlockingQueue;

import org.apache.log4j.Logger;

/**
 * Pooling data for ETA processing 
 * 
 * @author Dharmaraju V
 *
 */
public class ETADataPool {
	
	private static ETADataPool _instance = null;
	
	private static Logger LOG = Logger.getLogger(ETADataPool.class);
	
	private static final int QUEUE_SIZE = 10000;
	
	public static ArrayBlockingQueue<ETAEntity> etaEntityQueue = new ArrayBlockingQueue<ETAEntity>(QUEUE_SIZE);
	
	public static ETADataPool getInstance(){
		if(_instance == null){
			_instance = new ETADataPool();
		}
		return _instance;
	}
	
	public void add(ETAEntity entity){
		etaEntityQueue.offer(entity);
		if(etaEntityQueue.size() > (QUEUE_SIZE - 1000)){
			LOG.warn("Added a entity to the ETA pool. Size : "+etaEntityQueue.size());
		} else if (etaEntityQueue.size() > (QUEUE_SIZE)){
			LOG.fatal("Added a entity to the ETA pool. Size : "+etaEntityQueue.size());
		}
	}
	
	public ETAEntity take() throws InterruptedException{
		LOG.debug("Taking an entity from the ETA pool. Size : "+etaEntityQueue.size());
		return etaEntityQueue.take();
		
	}

}
