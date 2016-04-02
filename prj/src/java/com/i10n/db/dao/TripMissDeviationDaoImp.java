package com.i10n.db.dao;

import java.sql.Timestamp;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.incrementer.DataFieldMaxValueIncrementer;

import com.i10n.db.entity.TripMissDeviation;
import com.i10n.db.entity.primarykey.LongPrimaryKey;
import com.i10n.db.entity.rowmapper.TripMissRowMapper;
import com.i10n.db.idao.ITripMissDAO;
import com.i10n.fleet.exceptions.OperationNotSupportedException;
import com.i10n.fleet.util.DateUtils;

/**
 * 
 * @author Dharmaraju V
 *
 */
@SuppressWarnings("unchecked")
public class TripMissDeviationDaoImp implements ITripMissDAO{
	private static Logger LOG = Logger.getLogger(TripMissDeviationDaoImp.class);
	private JdbcTemplate jdbcTemplate;
	private DataFieldMaxValueIncrementer tripMissIdIncrementer;

	public JdbcTemplate getJdbcTemplate() {
		return jdbcTemplate;
	}

	public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}

	public void setTripMissIdIncrementer(
			DataFieldMaxValueIncrementer tripMissIdIncrementer) {
		this.tripMissIdIncrementer = tripMissIdIncrementer;
	}

	public DataFieldMaxValueIncrementer getTripMissIdIncrementer() {
		return tripMissIdIncrementer;
	}

	@Override
	public TripMissDeviation insert(TripMissDeviation entity)
			throws OperationNotSupportedException {
		Long id = tripMissIdIncrementer.nextLongValue();
		entity.setId(new LongPrimaryKey(id));
		String query = "INSERT INTO violation_tripmissdeviation (id, vehicleid, routeid, expectedtime, occurredat)" +
				" values (?, ?, ?, ?, ?)";
		Object args [] = new Object[]{entity.getId().getId(), entity.getVehicleId(), entity.getRouteId(), entity.getExpectedTime(), entity.getOccurredAt()};
		int types [] = new int [] {Types.BIGINT, Types.BIGINT, Types.BIGINT, Types.TIMESTAMP, Types.TIMESTAMP};
		jdbcTemplate.update(query, args, types);
		return entity;
	}

	@Override
	public TripMissDeviation delete(TripMissDeviation entity)
			throws OperationNotSupportedException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public TripMissDeviation update(TripMissDeviation entity)
			throws OperationNotSupportedException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<TripMissDeviation> selectAll() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<TripMissDeviation> selectByPrimaryKey(LongPrimaryKey primaryKey) {
		// TODO Auto-generated method stub
		return null;
	}

	public List<TripMissDeviation> selectFromVehicleIdRouteIdExpecteTime(Long vehicleId, Long routeId, Timestamp expectedTime) {
		List<TripMissDeviation> tripMissList = new ArrayList<TripMissDeviation>();
		String query = "Select * from violation_tripmissdeviation where vehicleid = "
				+vehicleId
				+" and routeid = "
				+routeId
				+" and expectedtime = '"
				+DateUtils.convertJavaDateToSQLDate(expectedTime)+"'";
		LOG.debug("selectFromVehicleIdRouteIdExpecteTime : "+query);

		tripMissList = jdbcTemplate.query(query, new TripMissRowMapper());

		return tripMissList;
	}

	public List<TripMissDeviation> selectByUserId(long userId, Date startDate, Date endDate) {
		List<TripMissDeviation> tripMissList = new ArrayList<TripMissDeviation>();
		String query = "select * from violation_tripmissdeviation where vehicleid in(select vehicleid from aclvehicle where userid = "
				+userId
				+") and occurredat >= '"
				+DateUtils.convertJavaDateToSQLDate(startDate)
				+"' and occurredat <= '"
				+DateUtils.convertJavaDateToSQLDate(endDate)
				+"' order by occurredat";
		LOG.debug("selectByUserId : "+query);

		tripMissList = jdbcTemplate.query(query, new TripMissRowMapper());
		return tripMissList;
	}

	public List<TripMissDeviation> selectByVehicleId(long vehicleId, Date startDate, Date endDate) {
		List<TripMissDeviation> tripMissList = new ArrayList<TripMissDeviation>();
		String sql = "select * from violation_tripmissdeviation where vehicleid = "+vehicleId
				+" and occurredat >= '"+DateUtils.convertJavaDateToSQLDate(startDate)
				+"' and occurredat <= '"+DateUtils.convertJavaDateToSQLDate(endDate)
				+"' order by occurredat";
		tripMissList = jdbcTemplate.query(sql, new TripMissRowMapper());
		return tripMissList;
	}


}
