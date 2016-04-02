package com.i10n.db.entity;

import java.util.Date;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.StringTokenizer;

import com.i10n.db.entity.IEntity.IEntity;
import com.i10n.db.entity.primarykey.LongPrimaryKey;



public class LatestPacketDetails implements IEntity<LatestPacketDetails>{

	private LongPrimaryKey id;
	private long trackhistoryId;
	private long idlePointsId;
	private long lastSavedTrackhistoryId;
	private long latestTrackhistoryPingcounter;
	private long latestButOneTrackhistoryPingcounter;
	private String lastComputedIdlePointsIds;
	private Date lastComputedIdlePointEndTime;
	
	
	
	public LatestPacketDetails() {
		// TODO Auto-generated constructor stub
	}

	public LatestPacketDetails(long id, long trackHistoryId, long idlePointId,
								long lastSavedTrackhistoryId, long latestTrackhistoryPingcounter,
								long latestButOneTrackhistoryPingcounter,
								String lastComputedIdlePointsIds, Date lastComputedIdlePointEndTime){
		this.id = new LongPrimaryKey(id);
		this.trackhistoryId = trackHistoryId;
		this.idlePointsId = idlePointId;
		this.lastSavedTrackhistoryId = lastSavedTrackhistoryId;
		this.latestTrackhistoryPingcounter = latestTrackhistoryPingcounter;
		this.latestButOneTrackhistoryPingcounter = latestButOneTrackhistoryPingcounter;
		this.lastComputedIdlePointsIds = lastComputedIdlePointsIds;
		this.lastComputedIdlePointEndTime = lastComputedIdlePointEndTime;
	}
	
	public LongPrimaryKey getId() {
		return id;
	}

	public void setId(LongPrimaryKey id) {
		this.id = id;
	}

	public long getTrackhistoryId() {
		return trackhistoryId;
	}

	public void setTrackhistoryId(long trackhistoryId) {
		this.trackhistoryId = trackhistoryId;
	}

	public long getIdlePointsId() {
		return idlePointsId;
	}

	public void setIdlePointsId(long idlePointsId) {
		this.idlePointsId = idlePointsId;
	}

	public long getLastSavedTrackhistoryId() {
		return lastSavedTrackhistoryId;
	}

	public void setLastSavedTrackhistoryId(long lastSavedTrackhistoryId) {
		this.lastSavedTrackhistoryId = lastSavedTrackhistoryId;
	}

	public long getLatestTrackhistoryPingcounter() {
		return latestTrackhistoryPingcounter;
	}

	public void setLatestTrackhistoryPingcounter(long latestTrackhistoryPingcounter) {
		this.latestTrackhistoryPingcounter = latestTrackhistoryPingcounter;
	}

	public long getLatestButOneTrackhistoryPingcounter() {
		return latestButOneTrackhistoryPingcounter;
	}

	public void setLatestButOneTrackhistoryPingcounter(
			long latestButOneTrackhistoryPingcounter) {
		this.latestButOneTrackhistoryPingcounter = latestButOneTrackhistoryPingcounter;
	}
	
	public void setLastComputedIdlePointsIds(List<Long> lastComputedIdlePointsIdsCollection) {
		if (lastComputedIdlePointsIdsCollection != null && lastComputedIdlePointsIdsCollection.size() != 0){
			StringBuffer lastComputedIdlePointsBuffer = new StringBuffer();
			Iterator<Long> iterator = lastComputedIdlePointsIdsCollection.iterator();
			while (iterator.hasNext()){
				if (lastComputedIdlePointsBuffer.length() != 0){
					lastComputedIdlePointsBuffer.append(",");
				}
				lastComputedIdlePointsBuffer.append(iterator.next());
			}
			this.lastComputedIdlePointsIds = lastComputedIdlePointsBuffer.toString();
		}else{
			this.lastComputedIdlePointsIds = "";
		}
	}

	@Override
	public String toString() {
		return "LatestPacketDetails [id=" + id + ", idlePointsId="
				+ idlePointsId + ", lastComputedIdlePointEndTime="
				+ lastComputedIdlePointEndTime + ", lastComputedIdlePointsIds="
				+ lastComputedIdlePointsIds + ", lastSavedTrackhistoryId="
				+ lastSavedTrackhistoryId
				+ ", latestButOneTrackhistoryPingcounter="
				+ latestButOneTrackhistoryPingcounter
				+ ", latestTrackhistoryPingcounter="
				+ latestTrackhistoryPingcounter + ", trackhistoryId="
				+ trackhistoryId + "]";
	}

	public Date getLastComputedIdlePointEndTime() {
		return lastComputedIdlePointEndTime;
	}

	public void setLastComputedIdlePointEndTime(Date lastComputedIdlePointEndTime) {
		this.lastComputedIdlePointEndTime = lastComputedIdlePointEndTime;
	}
	
	public List<Long> getLastComputedIdlePointsIds() {
		List<Long> lastComputedIdlePointsIdsCollection = null;
		if (lastComputedIdlePointsIds != null && lastComputedIdlePointsIds.length() != 0){
			lastComputedIdlePointsIdsCollection = new LinkedList<Long>();
			StringTokenizer commaSplitter = new StringTokenizer(lastComputedIdlePointsIds, ",", false);
			while (commaSplitter.hasMoreElements()){
				lastComputedIdlePointsIdsCollection.add(Long.valueOf(commaSplitter.nextToken()));
			}
		}
		return lastComputedIdlePointsIdsCollection;
	}

	@Override
	public boolean equalsEntity(LatestPacketDetails object) {
		// TODO Auto-generated method stub
		return false;
	}

	
}
