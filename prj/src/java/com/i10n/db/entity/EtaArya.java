package com.i10n.db.entity;

import java.sql.Timestamp;

import com.i10n.db.entity.IEntity.IEntity;

public class EtaArya implements IEntity<EtaArya>{

	private long Id;
	private long stopId;
	private Timestamp rec_time;



	public EtaArya( long Id ,long stopId,Timestamp rec_time){
		this.Id = Id;
		this.stopId = stopId;
		this.rec_time=rec_time;
	}

	public EtaArya() {
		// TODO Auto-generated constructor stub
	}

	public long getId() {
		return Id;
	}

	public void setId(long Id) {
		this.Id = Id;
	}
	public long getStopId() {
		return stopId;
	}

	public void setStopId(long stopId) {
		this.stopId = stopId;
	}


	public Timestamp getrec_time() {
		return rec_time;
	}

	public void setrec_time(Timestamp rec_time) {
		this.rec_time = rec_time;
	}


	public String toString(){
		StringBuffer returnString = new StringBuffer(); 
		returnString.append("Id = ");
		returnString.append(Id);
		returnString.append(", StopId = ");
		returnString.append(stopId);
		returnString.append(", rec_time = ");
		returnString.append(rec_time);
		/*returnString.append(", IsDeleted = ");
		returnString.append(deleted)*/;
		return returnString.toString();

	}

	@Override
	public boolean equalsEntity(EtaArya object) {
		// TODO Auto-generated method stub
		return false;
	}



}
