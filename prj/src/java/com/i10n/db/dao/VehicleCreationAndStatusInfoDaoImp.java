package com.i10n.db.dao;

import java.sql.Types;
import java.util.List;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.incrementer.DataFieldMaxValueIncrementer;

import com.i10n.db.entity.VehicleCreationAndStatusInfo;
import com.i10n.db.entity.primarykey.LongPrimaryKey;
import com.i10n.db.entity.rowmapper.VehicleCreationAndStatusInfoRowMapper;
import com.i10n.db.idao.IVehicleCreationAndStatusInfoDAO;
import com.i10n.fleet.exceptions.OperationNotSupportedException;

@SuppressWarnings("unchecked")
public class VehicleCreationAndStatusInfoDaoImp implements IVehicleCreationAndStatusInfoDAO{

	private JdbcTemplate jdbcTemplate;
	private DataFieldMaxValueIncrementer vehiclecreationandstatusinfoIdIncrementer;

	public JdbcTemplate getJdbcTemplate() {
		return jdbcTemplate;
	}

	public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}

	public DataFieldMaxValueIncrementer getVehiclecreationandstatusinfoIdIncrementer() {
		return vehiclecreationandstatusinfoIdIncrementer;
	}

	public void setVehiclecreationandstatusinfoIdIncrementer(DataFieldMaxValueIncrementer vehiclecreationandstatusinfoIdIncrementer) {
		this.vehiclecreationandstatusinfoIdIncrementer = vehiclecreationandstatusinfoIdIncrementer;
	}

	@Override
	public VehicleCreationAndStatusInfo delete(VehicleCreationAndStatusInfo entity)
	throws OperationNotSupportedException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public VehicleCreationAndStatusInfo insert(VehicleCreationAndStatusInfo newvehicle)
	throws OperationNotSupportedException {
		Long id = vehiclecreationandstatusinfoIdIncrementer.nextLongValue();
		newvehicle.setVehicleid(new LongPrimaryKey(id));
		java.sql.Timestamp sqlDate = new java.sql.Timestamp(new java.util.Date().getTime());
		newvehicle.setCreatedat(sqlDate); 
		String sql = "insert into vehiclecreationandstatusinfo(vehicleid,createdat,lastupdateduserid,status,createduserid,currentownerid) values (?,?,?,?,?,?)";
		Object args []= new Object[] {newvehicle.getVehicleid().getId(),newvehicle.getCreatedat(),newvehicle.getLastupdateduserid(),newvehicle.getStatus(),newvehicle.getCreateduserid(),newvehicle.getCurrentownerid()};
		int types[] = new int[] {Types.BIGINT, Types.TIMESTAMP,Types.BIGINT,Types.INTEGER,Types.BIGINT,Types.BIGINT};
		jdbcTemplate.update(sql, args, types);
		return newvehicle;
	}

	@Override
	public List<VehicleCreationAndStatusInfo> selectAll() {
		String sql = "select * from vehiclecreationandstatusinfo";
		return jdbcTemplate.query(sql, new VehicleCreationAndStatusInfoRowMapper());
	}


	@Override
	public List<VehicleCreationAndStatusInfo> selectByPrimaryKey(LongPrimaryKey primaryKey) {
		String sql = "select a.vehicleid,a.createdat,a.lastupdateduserid,a.status,a.createduserid,a.currentownerid from vehiclecreationandstatusinfo a,users b,vehicles c " +" where a.vehicleid = ? and a.vehicleid = c.id and a.createduserid=b.id and a.currentownerid=b.id and a.lastupdateduserid=b.id";
		Object[] values = new Object[]{primaryKey.getId()};
		int[] types = new int[]{Types.BIGINT};
		List<VehicleCreationAndStatusInfo> newvehicle = jdbcTemplate.query(sql, values, types, new VehicleCreationAndStatusInfoRowMapper());
		return (newvehicle == null || newvehicle.size() == 0)?null:newvehicle;
	}

	@Override
	public VehicleCreationAndStatusInfo update(VehicleCreationAndStatusInfo entity)
	throws OperationNotSupportedException {
		// TODO Auto-generated method stub
		return null;
	}
}
