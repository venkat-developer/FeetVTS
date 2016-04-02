package com.i10n.fleet.util;

import org.apache.log4j.Logger;

/**
 * @author HEMANT
 *
 */
public class ThreadMonitor {

	private static Logger LOG = Logger.getLogger(ThreadMonitor.class);
	
	private static ThreadMonitor instance = null;
	
	public ThreadMonitor () {
		
	}
	
	public static ThreadMonitor getInstance () {
		if (instance == null) {
			instance = new ThreadMonitor ();
		}
		return instance;
	}
	
	public void threadwait () {
		synchronized (instance) {
			try {
				instance.wait();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	public void threadnotify () {
		LOG.debug("FleetCheck : ThreadMonitor : Notifying thread");
		synchronized (instance) {
			instance.notify();
		}
	}
}