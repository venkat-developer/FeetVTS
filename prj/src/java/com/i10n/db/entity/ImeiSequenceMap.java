package com.i10n.db.entity;

import com.i10n.db.entity.IEntity.IEntity;

/**
 * Class to handle new version module packet sequence
 * @author dharmaraju
 *
 */
public class ImeiSequenceMap implements IEntity<ImeiSequenceMap>{
	
	private String imei;
	
	private long sequenceNumber;
	
	public ImeiSequenceMap()
	{
		
	}
	public ImeiSequenceMap(String imei,long sequenceNumber){
	 this.imei=imei;
	 this.sequenceNumber=sequenceNumber;
	}
	
	
	public String getImei() {
		return imei;
	}


	public void setImei(String imei) {
		this.imei = imei;
	}


	public long getSequenceNumber() {
		return sequenceNumber;
	}

	public void setSequenceNumber(long sequenceNumber) {
		this.sequenceNumber = sequenceNumber;
	}

	@Override
	public boolean equalsEntity(ImeiSequenceMap object) {
		// TODO Auto-generated method stub
		return false;
	}
	
	public String toString(){
		StringBuffer returnString = new StringBuffer();
		returnString.append("IMEI : ");
		returnString.append(getImei());
		returnString.append(", SequenceNumber : ");
		returnString.append(getSequenceNumber());
		return returnString.toString();
	}
}
