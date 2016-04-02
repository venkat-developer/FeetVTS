
package com.i10n.module.dataprocessor;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import org.apache.log4j.Logger;

import com.i10n.fleet.container.GWTrackModuleDataBean;
import com.i10n.fleet.util.DataBasePushHandler;
import com.i10n.fleet.util.EnvironmentInfo;

/**
 * 
 * This class is designed to house all the processed packets and
 * offer it to threads which will consume it and push it to DB.
 * 
 * So packets reside here between decode and db push.
 * 
 * @author dharmaraju
 *
 */
public class DataPool {
	private static final Logger LOG = Logger.getLogger(DataPool.class);
	
	private static DataPool instance = null;
	
	public static BlockingQueue<GWTrackModuleDataBean> packetPool  = new ArrayBlockingQueue<GWTrackModuleDataBean>(10000);
	
	/**
	 * Singleton 
	 * @return
	 */
	public static DataPool getDataPoolInstance(){
		if(instance == null){
			instance = new DataPool();
		}
		return instance;
	}
	
	/**
	 * Adding packet
	 */
	public static void addPacket (GWTrackModuleDataBean dataPacket) {
		if (!packetPool.offer(dataPacket)) {
			PacketProcessingException packetException = new PacketProcessingException(PacketProcessingException.PacketErrorCode.QUEUE_FULL);
			LOG.error("Skipped adding from IMEI : "+dataPacket.getImei(), packetException);		
			if(Boolean.valueOf(Boolean.valueOf(EnvironmentInfo.getProperty("IS_PACKET_BACKUP_ENABLED")))){
				//As the data pool is full writing the data packet into a file
				ReadWriteJsonToFIle.writeDataPacketToFile(dataPacket);
			}
		}
		else {
			LOG.info("Added packet, total count : "+packetPool.size());
		}
	}
	
	/**
	 * Retrieving packet
	 * @return
	 */
	public static GWTrackModuleDataBean getPacket () {
		GWTrackModuleDataBean pkt = null;
		while(!DataBasePushHandler.shutdownInitiated){
			pkt =  packetPool.poll();
			if(pkt != null){
				LOG.debug("Retrieving packet for IMEI "+ pkt.getImei()+" remaining packets : "+ (packetPool.size()-1));
				return pkt;
			}
		}
		return pkt;
	}
	
	/**
	 * Retrieving packet
	 * @return
	 */
	public static GWTrackModuleDataBean emptyPool () {
		try {
			packetPool.clear();
		} catch (Exception e) {
			LOG.fatal("DB Pool empty returned exception ", e);
		}
		return null;
	}
	
}
