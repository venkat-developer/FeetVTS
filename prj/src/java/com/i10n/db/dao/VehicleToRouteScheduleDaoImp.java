package com.i10n.db.dao;

import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.jdbc.core.JdbcTemplate;

import com.i10n.db.entity.VehicleToRouteSchedule;
import com.i10n.db.entity.primarykey.LongPrimaryKey;
import com.i10n.db.entity.rowmapper.VehicleToRouteScheduleRowMapper;
import com.i10n.db.idao.IVehicleToRouteScheduleDAO;
import com.i10n.fleet.exceptions.OperationNotSupportedException;

@SuppressWarnings("unchecked")
public class VehicleToRouteScheduleDaoImp implements IVehicleToRouteScheduleDAO{

	@SuppressWarnings("unused")
	private Logger LOG = Logger.getLogger(VehicleToRouteScheduleDaoImp.class);

	private JdbcTemplate jdbcTemplate;

	public JdbcTemplate getJdbcTemplate() {
		return jdbcTemplate;
	}

	public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}

	public VehicleToRouteSchedule insert(VehicleToRouteSchedule entity)
			throws OperationNotSupportedException {
		String sql =" insert into vehicletorouteschedule values ("+entity.getVehicleId()+
				", "+entity.getRouteId()+", '"+entity.getScheduleTime()+"')";
		jdbcTemplate.update(sql);
		return entity;
	}

	public VehicleToRouteSchedule delete(VehicleToRouteSchedule entity)
			throws OperationNotSupportedException {
		// TODO Auto-generated method stub
		String sql ="delete from vehicletorouteschedule where vehicleid="+entity.getVehicleId()+" and routeid="+entity.getRouteId()+" and scheduletime='"+entity.getScheduleTime()+"'";
		 jdbcTemplate.update(sql);
		 return entity;
	}

	public VehicleToRouteSchedule update(VehicleToRouteSchedule entity)
			throws OperationNotSupportedException {
		// TODO Auto-generated method stub
		return null;
	}


	public List<VehicleToRouteSchedule> selectAll() {
		String sql = "select * from vehicletorouteschedule ";
		return jdbcTemplate.query(sql, new VehicleToRouteScheduleRowMapper());
	}


	public List<VehicleToRouteSchedule> selectByPrimaryKey(
			LongPrimaryKey primaryKey) {
		// TODO Auto-generated method stub
		return null;
	}

	public List<VehicleToRouteSchedule> selectByVehicleId(Long vehicleId) {
		String sql = "select * from vehicletorouteschedule where vehicleid = "+vehicleId;
		return jdbcTemplate.query(sql, new VehicleToRouteScheduleRowMapper());
	}

}
