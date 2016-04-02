package com.i10n.db.entity;

import java.sql.Timestamp;

import com.i10n.db.entity.IEntity.IEntity;
import com.i10n.db.entity.primarykey.LongPrimaryKey;

/**
 * Entity class for Trip Miss deviation event
 * @author Dharmaraju V
 *
 */
public class TripMissDeviation implements IEntity<TripMissDeviation> {
	
	private LongPrimaryKey id;
	private Long vehicleId;
	private Long routeId;
	private Timestamp expectedTime;
	private Timestamp occurredAt;
	
	public TripMissDeviation(Long id, Long vehicleId, Long routeId, Timestamp expectedTime, Timestamp occurredAt) {
		setId(new LongPrimaryKey(id));
		setVehicleId(vehicleId);
		setRouteId(routeId);
		setExpectedTime(expectedTime);
		setOccurredAt(occurredAt);
	}
	
	public TripMissDeviation(Long vehicleId, Long routeId, Timestamp expectedTime, Timestamp occurredAt) {
		setVehicleId(vehicleId);
		setRouteId(routeId);
		setExpectedTime(expectedTime);
		setOccurredAt(occurredAt);
	}

	public LongPrimaryKey getId() {
		return id;
	}

	public void setId(LongPrimaryKey id) {
		this.id = id;
	}

	public Long getVehicleId() {
		return vehicleId;
	}

	public void setVehicleId(Long vehicleId) {
		this.vehicleId = vehicleId;
	}

	public Long getRouteId() {
		return routeId;
	}

	public void setRouteId(Long routeId) {
		this.routeId = routeId;
	}

	public Timestamp getExpectedTime() {
		return expectedTime;
	}

	public void setExpectedTime(Timestamp expectedTime) {
		this.expectedTime = expectedTime;
	}

	public Timestamp getOccurredAt() {
		return occurredAt;
	}

	public void setOccurredAt(Timestamp occurredAt) {
		this.occurredAt = occurredAt;
	}

	@Override
	public boolean equalsEntity(TripMissDeviation object) {
		if(id.getId() == object.getId().getId()){
			return true;
		}
		return false;
	}

}
