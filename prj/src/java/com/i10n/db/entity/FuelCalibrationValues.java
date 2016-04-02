package com.i10n.db.entity;

import com.i10n.db.entity.IEntity.IEntity;
import com.i10n.db.entity.primarykey.LongPrimaryKey;

public class FuelCalibrationValues implements IEntity<FuelCalibrationValues> {

	private LongPrimaryKey calibrationid;

	private int advalue;
	private int fuelinliters;
	private long tripid;
	private double gradient;
	private int base_ad;
	private int base_fuel;
	
	public FuelCalibrationValues(){
		gradient = 0.0;
	}
	
	public FuelCalibrationValues(LongPrimaryKey calibrationid, int advalue,
			int fuelinliters, long tripid, double gradient, int base_ad,
			int base_fuel) {
		super();
		this.calibrationid = calibrationid;
		this.advalue = advalue;
		this.fuelinliters = fuelinliters;
		this.tripid = tripid;
		this.gradient = gradient;
		this.base_ad = base_ad;
		this.base_fuel = base_fuel;
    }

	public LongPrimaryKey getCalibrationid() {
		return calibrationid;
	}

	public void setCalibrationid(LongPrimaryKey calibrationid) {
		this.calibrationid = calibrationid;
	}

	public int getAdvalue() {
		return advalue;
	}

	public void setAdvalue(int advalue) {
		this.advalue = advalue;
	}

	public int getFuelinliters() {
		return fuelinliters;
	}

	public void setFuelinliters(int fuelinliters) {
		this.fuelinliters = fuelinliters;
	}

	public long getTripId() {
		return tripid;
	}

	public void setTripId(long tripid) {
		this.tripid = tripid;
	}

	public double getGradient() {
		return gradient;
	}

	public void setGradient(double gradient) {
		this.gradient = gradient;
	}

	public int getBaseAd() {
		return base_ad;
	}

	public void setBaseAd(int base_ad) {
		this.base_ad = base_ad;
	}

	public int getBaseFuel() {
		return base_fuel;
	}

	public void setBaseFuel(int base_fuel) {
		this.base_fuel = base_fuel;
	}
	
	@Override
	public String toString() {
		StringBuilder strBuilder = new StringBuilder();
		strBuilder.append(getCalibrationid().getId()).append(" ").append(
				getAdvalue()).append(" ").append(getFuelinliters()).append(" ")
				.append(getTripId()).append(" ").append(getGradient()).append(
						" ").append(getBaseAd()).append(" ").append(
						getBaseFuel());
		return strBuilder.toString();
	}

	@Override
	public boolean equalsEntity(FuelCalibrationValues object) {
		return false;
	}
}