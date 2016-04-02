package com.i10n.db.entity;

import java.sql.Timestamp;

import com.i10n.db.entity.IEntity.IEntity;
import com.i10n.db.entity.primarykey.LongPrimaryKey;

public class FuelCalibrationDetails implements IEntity<FuelCalibrationDetails> {

	private LongPrimaryKey id;

	private Timestamp caldate;
	private String calibratedate;
	private Long calibratedbyemp;
	private int minad;
	private int maxad;

	public FuelCalibrationDetails(LongPrimaryKey id, Timestamp calibratedate,
			Long calibratedbyemp, int minad, int maxad) {
		super();
		this.id = id;
		this.caldate = calibratedate;
		this.calibratedbyemp = calibratedbyemp;
		this.minad = minad;
		this.maxad = maxad;
	}

	public LongPrimaryKey getId() {
		return id;
	}

	public void setId(LongPrimaryKey id) {
		this.id = id;
	}

	public Timestamp getCaldate() {
		return caldate;
	}

	public void setCaldate(Timestamp caldate) {
		this.caldate = caldate;
	}

	public String getCalibratedate() {
		return calibratedate;
	}

	public void setCalibratedate(String calibratedate) {
		this.calibratedate = calibratedate;
	}

	public Long getCalibratedbyemp() {
		return calibratedbyemp;
	}

	public void setCalibratedbyemp(Long calibratedbyemp) {
		this.calibratedbyemp = calibratedbyemp;
	}

	public int getMinAd() {
		return minad;
	}

	public void setMinAd(int minad) {
		this.minad = minad;
	}

	public int getMaxAd() {
		return maxad;
	}

	public void setMaxAd(int maxad) {
		this.maxad = maxad;
	}

	public FuelCalibrationDetails(LongPrimaryKey id, String date,
			Long calibratedbyemp, int minad, int maxad) {
		super();
		this.id = id;
		this.calibratedate = date;
		this.calibratedbyemp = calibratedbyemp;
		this.minad = minad;
		this.maxad = maxad;
	}
	public FuelCalibrationDetails(Long calibratedbyemp, int minad, int maxad) {
		super();
	
		
		this.calibratedbyemp = calibratedbyemp;
		this.minad = minad;
		this.maxad = maxad;
	}

	@Override
	public String toString() {
		StringBuilder strBuilder = new StringBuilder();
		strBuilder.append(getId().getId()).append(" ").append(
				getCalibratedate()).append(" ").append(getCalibratedbyemp())
				.append(" ").append(getMinAd()).append(" ").append(getMaxAd());
		return strBuilder.toString();
	}

	@Override
	public boolean equalsEntity(FuelCalibrationDetails object) {
		return false;
	}
}