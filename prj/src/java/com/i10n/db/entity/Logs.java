package com.i10n.db.entity;

import java.sql.Timestamp;
import java.util.Date;

import com.i10n.db.entity.IEntity.IEntity;
import com.i10n.db.entity.primarykey.LongPrimaryKey;

public class Logs implements IEntity<Logs> {

	private Long userid;
	private LongPrimaryKey id;
	private Timestamp date;
	private String ipaddress;
	private String olddata;
	private String newdata;
	private String date1;
	private Date dat;

	public Logs(LongPrimaryKey id,Long userid, Timestamp date, String ipaddress,
			String olddata, String newdata) {
		super();
		this.id=id;
		this.userid = userid;
		this.date = date;
		this.ipaddress = ipaddress;
		this.olddata = olddata;
		this.newdata = newdata;
	}

	public Logs(LongPrimaryKey id,Long userid, Date dat, String ipaddress,
			String olddata, String newdata) {
		super();
		this.id=id;
		this.userid = userid;
		this.dat = dat;
		this.ipaddress = ipaddress;
		this.olddata = olddata;
		this.newdata = newdata;
	}

	public Logs(LongPrimaryKey id,Long userid, String ipaddress, String olddata,
			String newdata, String date1) {
		super();
		this.id=id;
		this.userid = userid;
		this.ipaddress = ipaddress;
		this.olddata = olddata;
		this.newdata = newdata;
		this.date1 = date1;
	}

	public LongPrimaryKey getId() {
		return id;
	}

	public void setId(LongPrimaryKey id) {
		this.id = id;
	}

	public Long getUserid() {
		return userid;
	}

	public void setUserid(Long userid) {
		this.userid = userid;
	}

	public Timestamp getDate() {
		return date;
	}

	public void setDate(Timestamp date) {
		this.date = date;
	}

	public Date getDat() {
		return dat;
	}

	public void setDat(Date dat) {
		this.dat = dat;
	}

	public String getIpaddress() {
		return ipaddress;
	}

	public void setIpaddress(String ipaddress) {
		this.ipaddress = ipaddress;
	}

	public String getOlddata() {
		return olddata;
	}

	public void setOlddata(String olddata) {
		this.olddata = olddata;
	}

	public String getNewdata() {
		return newdata;
	}

	public void setNewdata(String newdata) {
		this.newdata = newdata;
	}

	public String getDate1() {
		return date1;
	}

	public void setDate1(String date1) {
		this.date1 = date1;
	}

	@Override
	public String toString() {
		StringBuilder strBuilder = new StringBuilder();
		strBuilder.append(getId().getId()).append(" ").append(getUserid()).append(" ").append(getDate1())
				.append(" ").append(getIpaddress()).append(" ").append(
						getOlddata()).append(" ").append(getNewdata());
		return strBuilder.toString();
	}

	@Override
	public boolean equalsEntity(Logs object) {
		// TODO Auto-generated method stub
		return false;
	}
}
