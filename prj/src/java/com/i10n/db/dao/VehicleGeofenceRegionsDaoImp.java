package com.i10n.db.dao;

import java.sql.Types;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.jdbc.core.JdbcTemplate;

import com.i10n.db.entity.VehicleGeofenceRegions;
import com.i10n.db.entity.primarykey.VehicleGeofenceRegionsPrimaryKey;
import com.i10n.db.entity.rowmapper.VehicleGeofenceRegionsRowMapper;
import com.i10n.db.idao.IVehicleGeofenceRegionsDAO;
import com.i10n.fleet.exceptions.OperationNotSupportedException;

@SuppressWarnings("unchecked")
public class VehicleGeofenceRegionsDaoImp implements IVehicleGeofenceRegionsDAO {
	private static final Logger LOG = Logger.getLogger(VehicleGeofenceRegionsDaoImp.class);
	private JdbcTemplate jdbcTemplate;

	public void setJdbcTemplate(JdbcTemplate jdbcTemplate){
		this.jdbcTemplate = jdbcTemplate;
	}

	@Override
	public List<VehicleGeofenceRegions> selectByRegionId(Long regionId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public VehicleGeofenceRegions delete(VehicleGeofenceRegions entity)
			throws OperationNotSupportedException {
		String sql="delete from vehicle_geofenceregions where vehicleid=?";
		Object args []= new Object[] {entity.getVehicleId()};
		int types[] = new int[] { Types.BIGINT};
		jdbcTemplate.update(sql, args, types);
		return entity;
	}

	public long getVehicleIDFromItsName(String vehicleName){
		String sql = "select id from vehicles where displayname=?";
		Object args[] = new Object[] {vehicleName};
		int types[] = new int[] {Types.VARCHAR};
		long vehId = jdbcTemplate.queryForLong(sql, args, types);
		return vehId;
	}

	@Override
	public List<VehicleGeofenceRegions> selectAll() {
		String sql = "select * from vehicle_geofenceregions";
		return jdbcTemplate.query(sql, new VehicleGeofenceRegionsRowMapper());
	}

	@Override
	public List<VehicleGeofenceRegions> selectByPrimaryKey(VehicleGeofenceRegionsPrimaryKey primaryKey) {
		String sql = "select * from aclvehicle where vehicleid = " + primaryKey.getVehicleid() +
				" and regionid= " + primaryKey.getRegionId();
		return jdbcTemplate.query(sql, new VehicleGeofenceRegionsRowMapper());
	}

	public List<VehicleGeofenceRegions> getVehicle_RegionByVehicleId(long Vehicleid){
		String sql="select * from vehicle_geofenceregions where vehicleid ="+Vehicleid;
		return jdbcTemplate.query(sql, new VehicleGeofenceRegionsRowMapper());
	}

	@Override
	public VehicleGeofenceRegions update(VehicleGeofenceRegions entity)
			throws OperationNotSupportedException {
		StringBuffer sql=new StringBuffer();
		sql.append("update vehicle_geofenceregions set insideregion=? where vehicleid=? and regionid=?");
		Object args[]= new Object[]{entity.isInsideRegion(),entity.getVehicleId(),entity.getRegionId()};
		int[] type=new int[]{Types.BOOLEAN,Types.BIGINT,Types.BIGINT};
		jdbcTemplate.update(sql.toString(), args,type);
		return entity;
	}

	@Override
	public VehicleGeofenceRegions insert(VehicleGeofenceRegions entity)
			throws OperationNotSupportedException {
		StringBuffer sql =new StringBuffer();
		sql.append("insert into vehicle_geofenceregions values(?,?,?)");
		Object args []= new Object[] {entity.getVehicleId(),entity.getRegionId(),entity.isInsideRegion()};
		int types[] = new int[] { Types.BIGINT, Types.BIGINT,Types.BOOLEAN};
		jdbcTemplate.update(sql.toString(), args, types);
		LOG.debug("GEO : vehicle Geo fence regions data inserted "+sql.toString());
		return entity;
	}

}
