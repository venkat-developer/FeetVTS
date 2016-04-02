package com.i10n.db.dao;

import java.sql.Timestamp;
import java.sql.Types;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.incrementer.DataFieldMaxValueIncrementer;

import com.i10n.db.entity.TimeDeviation;
import com.i10n.db.entity.primarykey.LongPrimaryKey;
import com.i10n.db.entity.rowmapper.TimeDeviationRowMapper;
import com.i10n.db.idao.ITimeDeviationDAO;
import com.i10n.fleet.exceptions.OperationNotSupportedException;

@SuppressWarnings("unchecked")
public class TimeDeviationDaoImp implements ITimeDeviationDAO{

	@SuppressWarnings("unused")
	private static Logger LOG = Logger.getLogger(TimeDeviationDaoImp.class);
	private JdbcTemplate jdbcTemplate;
	private DataFieldMaxValueIncrementer TimeDeviationIdIncrementer;

	public JdbcTemplate getJdbcTemplate() {
		return jdbcTemplate;
	}

	public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}

	public DataFieldMaxValueIncrementer getTimeDeviationIdIncrementer() {
		return TimeDeviationIdIncrementer;
	}

	public void setTimeDeviationIdIncrementer(DataFieldMaxValueIncrementer TimeDeviationIdIncrementer) {
		this.TimeDeviationIdIncrementer = TimeDeviationIdIncrementer;
	}

	@Override
	public TimeDeviation delete(TimeDeviation entity)
	throws OperationNotSupportedException {
		String sql = "delete from violation_timedeviation where id="+entity.getId().getId();
		jdbcTemplate.update(sql);
		return entity;
	}

	@Override
	public TimeDeviation insert(TimeDeviation entity)
	throws OperationNotSupportedException {
		Long id = TimeDeviationIdIncrementer.nextLongValue();
		entity.setId(new LongPrimaryKey(id));
		String sql = "INSERT INTO violation_timedeviation(id, vehicleid, vehiclename, stopid, stopname, routeid,"
			+ "routename, expectedtime, actualtime)"
			+ "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
		Object[] args = new Object[] { id, entity.getVehicleId(), entity.getVehicleName(), entity.getStopId(),
				entity.getStopName(), entity.getRouteId(), entity.getRouteName(), entity.getExpectedTime(), entity.getActualTime() };
		jdbcTemplate.update(sql, args);
		return entity;
	}

	@Override
	public List<TimeDeviation> selectAll() {
		String sql = "select * from violation_timedeviation";
		return jdbcTemplate.query(sql, new TimeDeviationRowMapper());
	}

	@Override
	public List<TimeDeviation> selectByPrimaryKey(LongPrimaryKey primaryKey) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public TimeDeviation update(TimeDeviation entity)
	throws OperationNotSupportedException {
		// TODO Auto-generated method stub
		return null;
	}

	public List<TimeDeviation> selectByUserId(long userId, Date startDate, Date endDate) {
		String sql = "select * from violation_timedeviation where vehicleid in(select vehicleid from aclvehicle where userid=?) and actualtime>=? and actualtime<=? order by actualtime";
		Object[] args = new Object[]{userId,new Timestamp(startDate.getTime()),new Timestamp(endDate.getTime())};
		int[] types = new int[]{Types.BIGINT,Types.TIMESTAMP,Types.TIMESTAMP};
		return jdbcTemplate.query(sql, args, types, new TimeDeviationRowMapper());
	}

	public List<TimeDeviation> selectByVehicleId(long vehicleId, Date startDate, Date endDate) {
		String sql = "select * from violation_timedeviation where vehicleid=? and actualtime>=? and actualtime<=? order by actualtime";
		Object[] args = new Object[]{vehicleId, new Timestamp(startDate.getTime()), new Timestamp(endDate.getTime())};
		int[] types = new int[]{Types.BIGINT,Types.TIMESTAMP,Types.TIMESTAMP};
		return jdbcTemplate.query(sql, args, types, new TimeDeviationRowMapper());
	}
}
