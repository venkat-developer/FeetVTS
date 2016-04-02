package com.i10n.db.entity;

import java.util.Calendar;

import org.apache.log4j.Logger;

import com.i10n.db.entity.IEntity.IEntity;
import com.i10n.db.entity.primarykey.LongPrimaryKey;
import com.i10n.dbCacheManager.LoadLiveVehicleStatusRecord;

public class Driver implements IEntity<Driver> {
	
	private static Logger LOG = Logger.getLogger(Vehicle.class);

	private LongPrimaryKey id;
	private String firstName;
	private String lastName;
	private String licenseno;
	private String photo;
	private Long groupId;
	private Long vehicleId;

	private boolean deleted = false;
	private String smartcardid;
	private String displayName;
	private String imei;
	private String groupName;

	public Driver() {
		super();
		this.id = null;
		this.firstName = "";
		this.lastName = "";
		this.licenseno = "";
		this.photo = "";
		this.groupId = 0L;
	}

	public Driver(Long id, String firstName, Long vehicleId, String imei) {
		super();
		this.id = new LongPrimaryKey(id);
		this.firstName = firstName;
		this.vehicleId = vehicleId;
		this.imei = imei;

	}

	public Driver(Long id, String firstName, String lastName, String licenseno,
			String photo, Long groupId, boolean deleted) {
		super();
		this.id = new LongPrimaryKey(id);
		this.firstName = firstName;
		this.lastName = lastName;
		this.displayName = this.firstName+" "+this.lastName;
		this.licenseno = licenseno;
		this.photo = photo;
		this.groupId = groupId;
		this.deleted = deleted;
	}
		
	public Driver(Long id, String firstName, String lastName, String licenseno,
			String photo, Long groupId, boolean deleted, String groupName) {
		super();
		this.id = new LongPrimaryKey(id);
		this.firstName = firstName;
		this.lastName = lastName;
		this.displayName = this.firstName+" "+this.lastName;
		this.licenseno = licenseno;
		this.photo = photo;
		this.groupId = groupId;
		this.deleted = deleted;
		this.groupName = groupName;
	}

	public Driver(String firstName, String lastName, String licenseno,
			String photo) {
		super();
		this.firstName = firstName;
		this.lastName = lastName;
		this.licenseno = licenseno;
		this.photo = photo;
		this.groupId = 0L;
	}

	public LongPrimaryKey getId() {
		return id;
	}

	public void setId(LongPrimaryKey id) {
		this.id = id;
	}

	public String getLicenseno() {
		return licenseno;
	}

	public void setLicenseno(String licenseno) {
		this.licenseno = licenseno;
	}

	public String getPhoto() {
		return photo;
	}

	public void setPhoto(String photo) {
		this.photo = photo;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public boolean getDeletedStatus() {
		return deleted;
	}

	public void setDeletedStatus(boolean deleted) {
		this.deleted = deleted;
	}

	public Long getGroupId() {
		return groupId;
	}

	public void setGroupId(Long groupId) {
		this.groupId = groupId;
	}

	public String getSmartcardid() {
		return smartcardid;
	}

	public void setSmartcardid(String smartcardid) {
		this.smartcardid = smartcardid;
	}

	public String getDisplayName() {
		return displayName;
	}

	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}

	public String getImei() {
		return imei;
	}

	public void setImei(String imei) {
		this.imei = imei;
	}

	public Long getVehicleId() {
		return vehicleId;
	}

	public void setVehicleId(Long vehicleId) {
		this.vehicleId = vehicleId;
	}

	public boolean isDeleted() {
		return deleted;
	}

	public void setDeleted(boolean deleted) {
		this.deleted = deleted;
	}

	public String getGroupName() {
		return groupName;
	}

	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}

	public String getStatus(Long driverId) {
		String statusofDriver = "";		
		LiveVehicleStatus liveVehicleObjectStatus = LoadLiveVehicleStatusRecord.getInstance().retrieveByDriverId(driverId.longValue());
		if (liveVehicleObjectStatus == null) {
			LOG.error("Trying to load LVOS for driverId "+driverId.longValue()+" which is neither in cache nor in db");			
			return statusofDriver;
		}
		long lastupdatedDay = liveVehicleObjectStatus.getLastUpdatedAt().getTime();
		long moduleUpdateTime = liveVehicleObjectStatus.getModuleUpdateTime().getTime();
		Calendar cal = Calendar.getInstance();
		long currDate = cal.getTimeInMillis(); 
		long lastUpdatediff =	currDate-lastupdatedDay; 
		long moduleUpdatediff =	currDate-moduleUpdateTime; 
		double lastUpdatedDiffDays = lastUpdatediff/(24*60*60*1000);
		double moduleUpdatediffDay = moduleUpdatediff/(24*60*60*1000);
		//to prevent negative values
		if(lastUpdatedDiffDays < 1 || moduleUpdatediffDay < 1){
			lastUpdatedDiffDays = -(lastUpdatedDiffDays);
			moduleUpdatediffDay = -(moduleUpdatediffDay);
		}
		if( liveVehicleObjectStatus.getPingCount() > 0 ){
			statusofDriver = "idle";
		} else if( lastUpdatedDiffDays > 1 && moduleUpdatediffDay > 1 ) { 
			statusofDriver = "offline"; 
		} else {
			statusofDriver = "online";
		}
		return statusofDriver;
	}

	@Override
	public boolean equalsEntity(Driver object) {
		// TODO Auto-generated method stub
		return false;
	}
	
	@Override
	public String toString(){
		StringBuffer toString = new StringBuffer();

		toString.append("ID : ");
		toString.append(getId().getId());
		toString.append(", FirstName : ");
		toString.append(getFirstName());
		toString.append(", LastName : ");
		toString.append(getLastName());
		toString.append(", LicenseNumber : ");
		toString.append(getLicenseno());
		
		return toString.toString();
	}
}
