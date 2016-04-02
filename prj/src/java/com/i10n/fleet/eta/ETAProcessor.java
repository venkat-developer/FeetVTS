package com.i10n.fleet.eta;

import org.apache.log4j.Logger;

import com.i10n.fleet.providers.mock.EtaDisplayDataHelper;

/**
 * Thread to process the ETA data
 * 
 * @author Dharmaraju V
 *
 */
public class ETAProcessor implements Runnable{

	private static Logger LOG = Logger.getLogger(ETAProcessor.class);

	private  static boolean stopThread = false;

	@Override
	public void run() {
		while (!stopThread){
			try {
				ETAEntity entity = ETADataPool.getInstance().take();
				new EtaDisplayDataHelper().processETA(entity.getVehicleId(), entity.getTripId(), entity.getIMEI(), 
						entity.getTrackHistoryId(), entity.getBulkDataTime(), entity.getModuleUpdateTime(), entity.getLatitude(), 
						entity.getLongitude(), entity.isPing());
			} catch (Exception e) {
				LOG.error(e);
			}
		}
	}
	public static void stopThread(){
		stopThread = true;
	}
}