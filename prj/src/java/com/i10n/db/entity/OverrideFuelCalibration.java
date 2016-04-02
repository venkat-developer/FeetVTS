package com.i10n.db.entity;

import com.i10n.db.entity.IEntity.IEntity;
import com.i10n.db.entity.primarykey.LongPrimaryKey;

public class OverrideFuelCalibration implements IEntity< OverrideFuelCalibration> {
    private  LongPrimaryKey tripid;
private Long calibrationid;

public OverrideFuelCalibration(LongPrimaryKey tripid, Long calibrationid) {
	super();
	this.tripid = tripid;
	this.calibrationid = calibrationid;
}

public OverrideFuelCalibration(Object object, boolean b, String string,
		String string2, String string3, String string4, String string5,
		String string6, String string7) {
	// TODO Auto-generated constructor stub
}

public LongPrimaryKey getTripid() {
	return tripid;
}

public void setTripid(LongPrimaryKey tripid) {
	this.tripid = tripid;
}

public Long getCalibrationid() {
	return calibrationid;
}

public void setCalibrationid(Long calibrationid) {
	this.calibrationid = calibrationid;
}

public String toString() {
    StringBuilder strBuilder = new StringBuilder();
    strBuilder.append(getTripid().getId()).append("-").append(getCalibrationid());
    return strBuilder.toString();
}
@Override
public boolean equalsEntity(OverrideFuelCalibration object) {
    // TODO Auto-generated method stub
    return false;
}


}
