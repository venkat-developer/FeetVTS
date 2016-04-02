package com.i10n.fleet.util;

import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.Map.Entry;

import org.apache.log4j.Logger;

import com.i10n.fleet.update.IStreamUpdate;
import com.i10n.fleet.web.comet.PushletStreamerJMS;

public class CometQueue extends TimerTask {

	private static final Logger LOG = Logger.getLogger(CometQueue.class);

	/**
	 * The time to wait in seconds before doing a sync with the internal cache 
	 * is in sync with the database.
	 */
	private int m_syncWaitTime = 2;

	/**
	 * The timer object which would give the callback after
	 * {@link #m_syncWaitTime} seconds.
	 */
	private Timer m_downloadResynTimer;


	private static CometQueue COMETQUEUE = null;

	private LinkedHashMap<String,IStreamUpdate> m_streamUpdateMap = new LinkedHashMap<String, IStreamUpdate>();

	private CometQueue() {
		
	}
	
	public void startCometQueue(){
		if (COMETQUEUE == null){
			COMETQUEUE = new CometQueue();
		}
		if (m_downloadResynTimer != null) {
			LOG.debug("Comet Queue already Started! Stop it before attempting to start again!");
		}else{
			// In some weird scenario, if this thread hangs. It should not stop
			// shutting down the vm.
			m_downloadResynTimer = new Timer(true);
			m_downloadResynTimer.schedule(this, new Date(System.currentTimeMillis()
					+ (m_syncWaitTime * 6000)), m_syncWaitTime * 1000);
			LOG.debug("CometQueue Service Started Successfully!!");
		}
	}

	public static final CometQueue getCometQueue() {
		if (COMETQUEUE == null){
			COMETQUEUE = new CometQueue();
		}
		return COMETQUEUE;
	}

    public void stopCometQueue(){
        if (m_downloadResynTimer != null){
            m_downloadResynTimer.cancel();
        }
        m_downloadResynTimer = null;
        LOG.debug("CometQueue Service Shutdown Successfully!!");
    }

	public void addStreamUpdate(IStreamUpdate update) {
		try {
			synchronized (m_streamUpdateMap) {
				m_streamUpdateMap.put(update.getType() + update.getId(), update);
			}
		} catch (Exception e) {
			LOG.debug("Error while adding a position update to cometqueue");
		}
	}
	
	public void addStreamUpdates(List<IStreamUpdate> updates) {
		try {
			synchronized (m_streamUpdateMap) {
				for(IStreamUpdate update : updates) {
					m_streamUpdateMap.put(update.getType() + update.getId(), update);
				}
			}
		} catch (Exception e) {
			LOG.debug("Error while adding a position update to cometqueue");
		}
	}

	@Override
	public void run() {
		boolean emptyFlag;
		LinkedHashMap<String,IStreamUpdate> dummyUpdateMap = new LinkedHashMap<String, IStreamUpdate>();
		synchronized (m_streamUpdateMap) {
			dummyUpdateMap.putAll(m_streamUpdateMap);
			m_streamUpdateMap.clear();
		}
		emptyFlag = dummyUpdateMap.isEmpty();
		if (!emptyFlag) {
			for (Entry<String, IStreamUpdate> typeEntry : dummyUpdateMap.entrySet()) {
				IStreamUpdate update = typeEntry.getValue();
				String type = update.getType();
				StringBuffer cometMessageBuffer = new StringBuffer("");
				cometMessageBuffer.append("{");
				cometMessageBuffer.append("\"handler\":\"" + type + "\"");
				cometMessageBuffer.append(",\"content\":[");
				cometMessageBuffer.append(update.toJSONString());
				cometMessageBuffer.append("]}");
				try{
					LOG.debug(update.toJSONString());
					if(type.equalsIgnoreCase("statusUpdate")){
						LOG.debug("PUSHLET statusupdate ");
						PushletStreamerJMS.getInstance().publishEvent(update.getLogin(), 
								cometMessageBuffer.toString(), "statusUpdate");
					} else if(type.equalsIgnoreCase("vehiclemapreport")){
						LOG.debug("PUSHLET vehiclemapreport");
						PushletStreamerJMS.getInstance().publishEvent(update.getLogin(), 
								cometMessageBuffer.toString(), "vehiclemapreport");
					} else{
						LOG.debug("PUSHLET livetrack");
						PushletStreamerJMS.getInstance().publishEvent(update.getLogin(), 
							cometMessageBuffer.toString(), "livetrack");
					}
				}catch (Exception e) {
					LOG.debug("Error sending comet event " + cometMessageBuffer);						
				}				
			}
		}
	}
}

