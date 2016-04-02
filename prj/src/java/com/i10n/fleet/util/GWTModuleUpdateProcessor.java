package com.i10n.fleet.util;

import java.lang.Thread.State;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;

import com.i10n.fleet.container.GWTerminalModuleDataBean;

/**
 * @author joshua
 *
 */
public class GWTModuleUpdateProcessor implements Runnable {
	
	private static final Logger LOG = Logger.getLogger(GWTModuleUpdateProcessor.class);
	
	public static GWTModuleUpdateProcessor moduleUpdateProcessor=new GWTModuleUpdateProcessor();
	private static LinkedBlockingQueue<GWTerminalModuleDataBean> moduleUpdates = new LinkedBlockingQueue<GWTerminalModuleDataBean>();
	Thread thread;
	boolean stopThread;
	ThreadPoolExecutor executorService = (ThreadPoolExecutor) Executors.newFixedThreadPool(200);
	Map<String, GWTerminalModuleDataBean> activelyRunningThreads = new HashMap<String, GWTerminalModuleDataBean>();
	Map<String, PriorityBlockingQueue<GWTerminalModuleDataBean>> blockedThreads = new HashMap<String, PriorityBlockingQueue<GWTerminalModuleDataBean>>();
	GWTerminalModuleUpdateBeanComparator gwterminalModuleUpdateBeanComparator = new GWTerminalModuleUpdateBeanComparator();
	long totalNumberOfModuleUpdatesReceived = 0;
	long totalAmountOfDurationBetweenEachModuleUpdate = 0;
	long timeInMilliSecondsOfLastUpdateReceived = 0;
	
	public GWTModuleUpdateProcessor(){
		stopThread=false;
	}
	
	public static void printActiveThreadCount(){
		LOG.debug("Total Tasks ever been scheduled[" + moduleUpdateProcessor.executorService.getTaskCount()
				+ "] total tasks completed[" + moduleUpdateProcessor.executorService.getCompletedTaskCount()
				+ "] total tasks executing[" + moduleUpdateProcessor.executorService.getActiveCount() + "]"
				+ " Pending Tasks["+(moduleUpdateProcessor.executorService.getTaskCount()
						- moduleUpdateProcessor.executorService.getCompletedTaskCount() - moduleUpdateProcessor.executorService.getActiveCount())+"]");
	}

	public void startModuleUpdateProcessor() {
		if (thread == null) {
			thread = new Thread(this);
		}
		if (!thread.isAlive() && thread.getState() == State.NEW) {
			thread.start();
		}
	}

	public void stopModuleUpdateProcessor() {
		stopThread = true;
		moduleUpdates.notifyAll();
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
	
	public static GWTModuleUpdateProcessor getInstance(){
		return moduleUpdateProcessor;
	}

	public void addModuleUpdates(GWTerminalModuleDataBean gwterminalModuleDataBean){
		synchronized (moduleUpdates) {
			if (!activelyRunningThreads.containsKey(gwterminalModuleDataBean.getImei())){
				activelyRunningThreads.put(gwterminalModuleDataBean.getImei(), gwterminalModuleDataBean);
				moduleUpdates.add(gwterminalModuleDataBean);
				long currentTimeInMilliSeconds = System.currentTimeMillis();
				if (timeInMilliSecondsOfLastUpdateReceived != 0){
					totalAmountOfDurationBetweenEachModuleUpdate = totalAmountOfDurationBetweenEachModuleUpdate + 
					(currentTimeInMilliSeconds - timeInMilliSecondsOfLastUpdateReceived);
				}else{
					totalAmountOfDurationBetweenEachModuleUpdate = 0;
				}
				timeInMilliSecondsOfLastUpdateReceived = currentTimeInMilliSeconds;
				totalNumberOfModuleUpdatesReceived = totalNumberOfModuleUpdatesReceived + 1;
			}else{
				PriorityBlockingQueue<GWTerminalModuleDataBean> blockedPriorityQueue = null;
				if (blockedThreads.containsKey(gwterminalModuleDataBean.getImei())){
					blockedPriorityQueue = blockedThreads.get(gwterminalModuleDataBean.getImei());
				}else{
					blockedPriorityQueue = new PriorityBlockingQueue<GWTerminalModuleDataBean>(11, gwterminalModuleUpdateBeanComparator);
					blockedThreads.put(gwterminalModuleDataBean.getImei(), blockedPriorityQueue);
				}
				blockedPriorityQueue.put(gwterminalModuleDataBean);
			}
			moduleUpdates.notifyAll();
		}
	}

	@Override
	public void run() {
		while (!stopThread) {
			synchronized (moduleUpdates) {
				if (moduleUpdates.size() == 0) {
					try {
						moduleUpdates.wait();
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
				LOG.debug("ModuleUpdate Size in"+moduleUpdates.size());
				for (int i = 0; i < moduleUpdates.size(); i++) {
					GWTerminalModuleDataBean gwterminalModuleDataBean = moduleUpdates.remove();
					LOG.debug("Executing GWTerminalModuleUpdateHandler for :" + gwterminalModuleDataBean.getImei());
					executorService.execute(new GWTerminalModuleUpdateHandler(gwterminalModuleDataBean, getNewCallbackThread(gwterminalModuleDataBean.getImei())));
				}
			}
		}
	}

	public long getAverageTimeTakenBetweenTwoModuleUpdates(){
		if (totalNumberOfModuleUpdatesReceived == 0){
			return 0;
		}
		return (totalAmountOfDurationBetweenEachModuleUpdate / totalNumberOfModuleUpdatesReceived);
	}

	/**
	 * This callback function will be called by the ModuleUpdateHandler Instance to which
	 * this thread was assigned. This function call will be done by only one
	 * ModuleUpdateHandler per IMEI since that mutual exclusiveness is maintained thru
	 * the queuing up of the new updates and making only one update getting processed.
	 * @param imei The IMEI number of the module which is currently done with processing
	 * @return Returns a thread that is used to call back after the ModuleUpdate Processing
	 * is done.
	 */
	private Thread getNewCallbackThread(final String imei){
		return new Thread(new Runnable(){
			public void run(){
				activelyRunningThreads.remove(imei);
				if (blockedThreads.containsKey(imei)){
					PriorityBlockingQueue<GWTerminalModuleDataBean> blockedPriorityQueue = blockedThreads.get(imei);
					GWTerminalModuleDataBean gwterminalModuleDataBean = blockedPriorityQueue.poll();
					if (blockedPriorityQueue.size() == 0){
						blockedThreads.remove(imei);
					}
					if (gwterminalModuleDataBean != null){
						addModuleUpdates(gwterminalModuleDataBean);
					}
				}
			}
		});
	}

	private class GWTerminalModuleUpdateBeanComparator implements Comparator<GWTerminalModuleDataBean>{
		@Override
		public int compare(GWTerminalModuleDataBean o1, GWTerminalModuleDataBean o2) {
			if (o1 == null || o2 == null){
				return -1;
			}
			if (o1.getModuleUpdateTime().getTime() == o2.getModuleUpdateTime().getTime()){
				return 0;
			}
			if (o1.getModuleUpdateTime().getTime() > o2.getModuleUpdateTime().getTime()){
				return 1;
			}
			return -1;
		}
	}
}
