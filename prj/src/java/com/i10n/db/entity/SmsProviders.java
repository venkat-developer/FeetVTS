package com.i10n.db.entity;


import java.sql.Timestamp;

import com.i10n.db.entity.IEntity.IEntity;
import com.i10n.db.entity.primarykey.LongPrimaryKey;

public class SmsProviders  implements IEntity< SmsProviders> 
{
    private  LongPrimaryKey providerid;
    private String providername;
   private Boolean multiplesmssupport;
   private Boolean reportsupport;
   private  String username ;
   private String password;
   private  int numberofsmssent;
   private Timestamp lastsmssentat;
   private String lastsmssenttomobile;
   private String lastsmstext;
   private String lastsmssentat1;
   
	public SmsProviders(LongPrimaryKey providerid, String providername,
		Boolean multiplesmssupport, Boolean reportsupport, String username,
		String password, int numberofsmssent, Timestamp timestamp,
		String lastsmssenttomobile, String lastsmstext) {
	super();
	this.providerid = providerid;
	this.providername = providername;
	this.multiplesmssupport = multiplesmssupport;
	this.reportsupport = reportsupport;
	this.username = username;
	this.password = password;
	this.numberofsmssent = numberofsmssent;
	this.lastsmssentat = timestamp;
	this.lastsmssenttomobile = lastsmssenttomobile;
	this.lastsmstext = lastsmstext;
}
	
	public SmsProviders(LongPrimaryKey providerid, String providername,
			Boolean multiplesmssupport, Boolean reportsupport, String username,
			String password, int numberofsmssent, String lastsmssentat,
			String lastsmssenttomobile, String lastsmstext) {
		super();
		this.providerid = providerid;
		this.providername = providername;
		this.multiplesmssupport = multiplesmssupport;
		this.reportsupport = reportsupport;
		this.username = username;
		this.password = password;
		this.numberofsmssent = numberofsmssent;
		this.lastsmssentat1 = lastsmssentat;
		this.lastsmssenttomobile = lastsmssenttomobile;
		this.lastsmstext = lastsmstext;
	}
	
	public LongPrimaryKey getProviderid() {
		return providerid;
	}

	public void setProviderid(LongPrimaryKey providerid) {
		this.providerid = providerid;
	}

	public String getProvidername() {
		return providername;
	}

	public void setProvidername(String providername) {
		this.providername = providername;
	}

	public Boolean getMultiplesmssupport() {
		return multiplesmssupport;
	}

	public void setMultiplesmssupport(Boolean multiplesmssupport) {
		this.multiplesmssupport = multiplesmssupport;
	}

	public Boolean getReportsupport() {
		return reportsupport;
	}

	public void setReportsupport(Boolean reportsupport) {
		this.reportsupport = reportsupport;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public int getNumberofsmssent() {
		return numberofsmssent;
	}

	public void setNumberofsmssent(int numberofsmssent) {
		this.numberofsmssent = numberofsmssent;
	}

	public String getLastsmssenttomobile() {
		return lastsmssenttomobile;
	}

	public void setLastsmssenttomobile(String lastsmssenttomobile) {
		this.lastsmssenttomobile = lastsmssenttomobile;
	}

	public String getLastsmstext() {
		return lastsmstext;
	}

	public void setLastsmstext(String lastsmstext) {
		this.lastsmstext = lastsmstext;
	}

	public String getLastsmssentat1() {
		return lastsmssentat1;
	}

	public void setLastsmssentat1(String lastsmssentat1) {
		this.lastsmssentat1 = lastsmssentat1;
	}

	@Override
public boolean equalsEntity(SmsProviders object) {
    // TODO Auto-generated method stub
    return false;
    
}
	
@Override
public String toString(){
    StringBuilder strBuilder = new StringBuilder();
    strBuilder.append(getProviderid().getId()).append("-").append(getProvidername()).append("-").append(getMultiplesmssupport()).append("-").append(getReportsupport()).append("-").append(getUsername()).append("-").append(getPassword()).append("-").append(getNumberofsmssent());
    strBuilder.append(getLastsmssentat1());
    strBuilder.append(getLastsmssenttomobile());
    strBuilder.append(getLastsmstext());


    return strBuilder.toString();
}

public Timestamp getLastsmssentat() {
	return lastsmssentat;
}

public void setLastsmssentat(Timestamp lastsmssentat) {
	this.lastsmssentat = lastsmssentat;
}


 
}