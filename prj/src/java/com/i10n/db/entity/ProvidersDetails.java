package com.i10n.db.entity;

import com.i10n.db.entity.IEntity.IEntity;
import com.i10n.db.entity.primarykey.LongPrimaryKey;

public class ProvidersDetails  implements IEntity<ProvidersDetails>{

	private LongPrimaryKey  id ;
	private   boolean isget ;
	private String  baseurl ;
	 private String mobilenumberparamname;
	  private String messageparamname ;
	 private String  multiplenumberdelimiter; 
	 private String  userparamname;
	 private String passparamname ;
	  private String creditcheckurl ;
	 
	
	public ProvidersDetails(LongPrimaryKey id, boolean isget, String baseurl,
			String mobilenumberparamname, String messageparamname,
			String multiplenumberdelimiter, String userparamname,
			String passparamname, String creditcheckurl) {
		super();
		this.id = id;
		this.isget = isget;
		this.baseurl = baseurl;
		this.mobilenumberparamname = mobilenumberparamname;
		this.messageparamname = messageparamname;
		this.multiplenumberdelimiter = multiplenumberdelimiter;
		this.userparamname = userparamname;
		this.passparamname = passparamname;
		this.creditcheckurl = creditcheckurl;
	}


	public LongPrimaryKey getId() {
		return id;
	}


	public void setId(LongPrimaryKey id) {
		this.id = id;
	}


	public boolean isIsget() {
		return isget;
	}


	public void setIsget(boolean isget) {
		this.isget = isget;
	}


	public String getBaseurl() {
		return baseurl;
	}


	public void setBaseurl(String baseurl) {
		this.baseurl = baseurl;
	}


	public String getMobilenumberparamname() {
		return mobilenumberparamname;
	}


	public void setMobilenumberparamname(String mobilenumberparamname) {
		this.mobilenumberparamname = mobilenumberparamname;
	}


	public String getMessageparamname() {
		return messageparamname;
	}


	public void setMessageparamname(String messageparamname) {
		this.messageparamname = messageparamname;
	}


	public String getMultiplenumberdelimiter() {
		return multiplenumberdelimiter;
	}


	public void setMultiplenumberdelimiter(String multiplenumberdelimiter) {
		this.multiplenumberdelimiter = multiplenumberdelimiter;
	}


	public String getUserparamname() {
		return userparamname;
	}


	public void setUserparamname(String userparamname) {
		this.userparamname = userparamname;
	}


	public String getPassparamname() {
		return passparamname;
	}


	public void setPassparamname(String passparamname) {
		this.passparamname = passparamname;
	}


	public String getCreditcheckurl() {
		return creditcheckurl;
	}


	public void setCreditcheckurl(String creditcheckurl) {
		this.creditcheckurl = creditcheckurl;
	}


	@Override
	public boolean equalsEntity(ProvidersDetails object) {
	    // TODO Auto-generated method stub
	    return false;
	    
	}
	
	
	@Override
	public String toString(){
	    StringBuilder strBuilder = new StringBuilder();
	    strBuilder.append(getId().getId()).append(isIsget()).append(getBaseurl()).append(getMobilenumberparamname()).append(getMessageparamname()).append(getMultiplenumberdelimiter());
	    strBuilder.append(getUserparamname());
	    strBuilder.append(getPassparamname());
	    strBuilder.append(getCreditcheckurl());



	    return strBuilder.toString();
	}

}
