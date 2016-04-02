package com.i10n.db.entity;

import org.postgis.Geometry;

import com.i10n.db.entity.IEntity.IEntity;


/**
 * @author joshua
 *
 * Contains data a lat, long position and its address.
 */

public class Address implements IEntity<Address> {
	
	/**
	 * Fetch status, useful when asynchronously fetched
	 * @author vishnu
	 */
	public enum ADDRESS_STATUS_ENUM {
		FETCHED(1),
		QUEUED(2),
		EMPTY(3);
		
		private int val;
		ADDRESS_STATUS_ENUM(int value) {
			this.val = value;
		}
		public int getValue() {
			return val;
		}
	};

	private ADDRESS_STATUS_ENUM fetchStatus;
  
    private double lat;
   
    private long id;
    
    private double lng;

  
    private String line1;

  
    private String line2;

 
    private String line3;

   
    private String line4;
   
  
    private String street;
   
  
    private String state;
   

    private String country;
    
    private Geometry latlon_point;
    
    public Address(double lat,double lng,String line1,String line2,String line3,String line4,String street,String state,String country,Geometry latlon){
    	this.lat=lat;
    	this.lng=lng;
    	this.line1=line1;
    	this.line2=line2;
    	this.line3=line3;
    	this.line4=line4;
    	this.street=street;
    	this.state=state;
    	this.country=country;
    	this.latlon_point=latlon;
    	this.fetchStatus = ADDRESS_STATUS_ENUM.FETCHED;    	
    }
    
    public Address(Long id, double lat,double lng,String line1,String line2,String line3,String line4,String street,String state,String country,Geometry latlon){
    	this.id = id;
    	this.lat=lat;
    	this.lng=lng;
    	this.line1=line1;
    	this.line2=line2;
    	this.line3=line3;
    	this.line4=line4;
    	this.street=street;
    	this.state=state;
    	this.country=country;
    	this.latlon_point=latlon;
    	this.fetchStatus = ADDRESS_STATUS_ENUM.FETCHED;    	
    }
    public Address(double lat,double lng,String line1,String line2,String line3,String line4,String street,String state,String country){
    	this.lat=lat;
    	this.lng=lng;
    	this.line1=line1;
    	this.line2=line2;
    	this.line3=line3;
    	this.line4=line4;
    	this.street=street;
    	this.state=state;
    	this.country=country;
    	this.fetchStatus = ADDRESS_STATUS_ENUM.FETCHED;    	
    }
    
    // Used for signaling when fetch is completed
    Boolean signalObject;
    
    public Boolean getSignalObject() {
		return signalObject;
	}
	public void setSignalObject(Boolean signalObject) {
		this.signalObject = signalObject;
	}
	/**
     * Address to be fetched
     * @param lat
     * @param lng
     */
    public Address(double lat, double lng) {
    	this.lat=lat;
    	this.lng=lng;
    	this.fetchStatus = ADDRESS_STATUS_ENUM.QUEUED;    	
    }
    
    public Address(){
    	this.fetchStatus = ADDRESS_STATUS_ENUM.EMPTY;    	
    }
   
    public Geometry getLatlon_point() {
		return latlon_point;
	}

	public void setLatlon_point(Geometry latlonPoint) {
		latlon_point = latlonPoint;
	}

	public String getCountry() {
        return country;
    }
   
    public String getLine1() {
        return line1;
    }
   
    public String getLine2() {
        return line2;
    }
   
    public String getLine3() {
        return line3;
    }
   
    public String getLine4() {
        return line4;
    }
   
    public String getState() {
        return state;
    }
   
    public String getStreet() {
        return street;
    }
   
    public void setCountry(String country) {
        this.country = country;
    }
   
    public void setLine1(String line1) {
        this.line1 = line1;
    }
   
    public void setLine2(String line2) {
        this.line2 = line2;
    }
   
    public void setLine3(String line3) {
        this.line3 = line3;
    }
   
    public void setLine4(String line4) {
        this.line4 = line4;
    }
   
    public void setState(String state) {
        this.state = state;
    }
   
    public void setStreet(String street) {
        this.street = street;
    }
   
    public double getLat() {
        return lat;
    }
   
    public double getLng() {
        return lng;
    }
   
    public void setLat(double lat) {
        this.lat = lat;
    }
   
    public void setLng(double lng) {
        this.lng = lng;
    }
   
    @Override
    public String toString() {
        try{
            String address="";
            if(line1 != null && !line1.equals("")){
            	address = line1;
            }   
            if(line2 != null && !line2.equals("")){
            	if(address.equals("")){
            		address = line2;
            	}else{
                	 address += ", " + line2; 
                 }
            }
            if(line3 != null && !line3.equals("")){
            	if(address.equals("")){
            		address = line3;
            	}                    
                else{
                	address += ", " + line3;
                }
            }
            if(line4 != null && !line4.equals("")){
            	if(address.equals("")){
            		address = line4;
            	}else{
            		address += ", " + line4;
            	}
            }
                
            if(address.equals("")){
            	address+=" Loacation not found ";
            }
            return address;
        } catch(Exception e){

            return "";
        }
       
    }

	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	@Override
	public boolean equalsEntity(Address object) {
		// TODO Auto-generated method stub
		return false;
	}
	public ADDRESS_STATUS_ENUM getFetchStatus() {
		return fetchStatus;
	}
	public void setFetchStatus(ADDRESS_STATUS_ENUM fetchStatus) {
		this.fetchStatus = fetchStatus;
	}
	
}