package com.i10n.db.dao;

import java.sql.Types;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.incrementer.DataFieldMaxValueIncrementer;

import com.i10n.db.entity.RouteDeviation;
import com.i10n.db.entity.primarykey.LongPrimaryKey;
import com.i10n.db.entity.rowmapper.RouteDeviationRowMapper;
import com.i10n.db.idao.IRouteDeviationDAO;
import com.i10n.fleet.exceptions.OperationNotSupportedException;

@SuppressWarnings("unchecked")
public class RouteDeviationDaoImp implements IRouteDeviationDAO{
	
	@SuppressWarnings("unused")
	private static Logger LOG = Logger.getLogger(RouteDeviationDaoImp.class);

	private JdbcTemplate jdbcTemplate;
	private DataFieldMaxValueIncrementer routeDeviationIdIncrementer;

	public JdbcTemplate getJdbcTemplate() {
		return jdbcTemplate;
	}

	public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}

	public DataFieldMaxValueIncrementer getRouteDeviationIdIncrementer() {
		return routeDeviationIdIncrementer;
	}

	public void setRouteDeviationIdIncrementer(DataFieldMaxValueIncrementer RouteDeviationIdIncrementer) {
		this.routeDeviationIdIncrementer = RouteDeviationIdIncrementer;
	}

	@Override
	public RouteDeviation delete(RouteDeviation entity)
	throws OperationNotSupportedException {
		String sql = "delete from violation_routedeviation where id="+entity.getId().getId();
		jdbcTemplate.update(sql);
		return entity;
	}

	@Override
	public RouteDeviation insert(RouteDeviation entity)
	throws OperationNotSupportedException {
		Long id = routeDeviationIdIncrementer.nextLongValue();
		entity.setId(new LongPrimaryKey(id));
		String sql = "INSERT INTO violation_routedeviation(id, vehicleid, vehiclename, stopid, stopname, routeid,"
			+ "routename, estimateddistance, actualdistance,occurredat)"
			+ "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
		Object[] args = new Object[] { id, entity.getVehicleId(), entity.getVehicleName(), entity.getStopId(),
				entity.getStopName(), entity.getRouteId(), entity.getRouteName(), entity.getEstimatedDistance(),
				entity.getActualDistance(),entity.getOccurredAt() };
		jdbcTemplate.update(sql, args);
		return entity;
	}

	@Override
	public List<RouteDeviation> selectAll() {
		String sql = "select * from violation_routedeviation";
		return jdbcTemplate.query(sql, new RouteDeviationRowMapper());
	}

	@Override
	public List<RouteDeviation> selectByPrimaryKey(LongPrimaryKey primaryKey) {
		String sql = "select * from violation_routedeviation where id = "+primaryKey.getId();
		return jdbcTemplate.query(sql, new RouteDeviationRowMapper());
	}

	@Override
	public RouteDeviation update(RouteDeviation entity)
	throws OperationNotSupportedException {
		// TODO Auto-generated method stub
		return null;
	}

	public List<RouteDeviation> selectByUserId(long userId, Date startDate, Date endDate) {
		String sql = "select * from violation_routedeviation where vehicleid in(select vehicleid from aclvehicle where userid=?) and occurredat>=? and occurredat<=? order by occurredat";
		Object[] args = new Object[]{userId, startDate, endDate};
		int[] types = new int[]{Types.BIGINT,Types.TIMESTAMP,Types.TIMESTAMP};
		return jdbcTemplate.query(sql, args, types, new RouteDeviationRowMapper());
	}

	public List<RouteDeviation> selectByVehicleId(long vehicleId, Date startDate, Date endDate) {
		String sql = "select * from violation_routedeviation where vehicleid=? and occurredat>=? and occurredat<=? order by occurredat";
		Object[] args = new Object[]{vehicleId, startDate, endDate};
		int[] types = new int[]{Types.BIGINT,Types.TIMESTAMP,Types.TIMESTAMP};
		return jdbcTemplate.query(sql, args, types, new RouteDeviationRowMapper());
	}
}
