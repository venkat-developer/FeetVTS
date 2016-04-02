package com.i10n.db.entity;

//import org.apache.log4j.Logger;
import com.i10n.db.entity.IEntity.IEntity;
import com.i10n.db.entity.primarykey.LongPrimaryKey;

/**
 * Entity class for holding the latest know values of the vehicle and the tracking device
 * @author DVasudeva
 *
 */
public class EmriVehiclesBaseStation implements IEntity<EmriVehiclesBaseStation> {

	// Logger instance
	//private static Logger LOG = Logger.getLogger(EmriRajasthan.class);

    // id
	private LongPrimaryKey id;
	// vehicleId  
	private long vehicleId;
	// district and baselocation and crewno of the device
	private String district;
	private String baseLocation;
	private long crewNo;
	
	public EmriVehiclesBaseStation() {
		super();
		setId(new LongPrimaryKey(vehicleId));
		setVehicleId(vehicleId);
		setDistrict(district);
		setBaseLocation(baseLocation);
		setCrewNo(crewNo);
		
	}

	public EmriVehiclesBaseStation(Long id,Long vehicleId,String district,String baseLocation,Long crewNo) {
		super();
		setId(new LongPrimaryKey(vehicleId));
		setVehicleId(vehicleId);
		setDistrict(district);
		setBaseLocation(baseLocation);
		setCrewNo(crewNo);
	}


	public Long getVehicleId() {
		return vehicleId;
	}

	public void setVehicleId(long vehicleId) {
		this.vehicleId = vehicleId;
	}

	public void setId(LongPrimaryKey id) {
		this.id = id;
	}

   public LongPrimaryKey getID(){
	   return id;
   }

	public String getDistrict() {
		return district;
	}


	public void setDistrict(String district) {
		this.district = district;
	}


	public String getBaseLocation() {
		return baseLocation;
	}


	public void setBaseLocation(String baseLocation) {
		this.baseLocation = baseLocation;
	}

	public Long getCrewNo() {
		return crewNo;
	}

	public void setCrewNo(long crewNo) {
		this.crewNo = crewNo;
	}
	@Override
	public boolean equalsEntity(EmriVehiclesBaseStation object) {
		// TODO Auto-generated method stub
		return false;
	}






	
}
