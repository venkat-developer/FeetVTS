package com.i10n.db.dao;

import java.sql.Timestamp;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.incrementer.DataFieldMaxValueIncrementer;

import com.i10n.db.entity.IdlePoints;
import com.i10n.db.entity.primarykey.LongPrimaryKey;
import com.i10n.db.entity.rowmapper.IdlePointsRowMapper;
import com.i10n.db.idao.IIdlePointsDAO;
import com.i10n.fleet.exceptions.OperationNotSupportedException;

/**
 * @author vijaybharath
 * @update Dharmaraju V
 */
@SuppressWarnings("unchecked")
public class IdlePointsDaoImp implements IIdlePointsDAO {
	
	private static Logger LOG = Logger.getLogger(IdlePointsDaoImp.class);

	private JdbcTemplate jdbcTemplate;
	private DataFieldMaxValueIncrementer idlePointsIdIncrementer;
	
	public JdbcTemplate getJdbcTemplate() {
		return jdbcTemplate;
	}

	public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}

	public DataFieldMaxValueIncrementer getIdlePointsIdIncrementer() {
		return idlePointsIdIncrementer;
	}

	public void setIdlePointsIdIncrementer(
			DataFieldMaxValueIncrementer idlePointsIdIncrementer) {
		this.idlePointsIdIncrementer = idlePointsIdIncrementer;
	}


	@Override
	public IdlePoints delete(IdlePoints entity)
			throws OperationNotSupportedException {
		String sql = "delete from idlepoints where id = "
				+ entity.getId().getId();
		jdbcTemplate.execute(sql);
		return null;
	}

	@Override
	public IdlePoints insert(IdlePoints idlePoints)
			throws OperationNotSupportedException {
		Long id = idlePointsIdIncrementer.nextLongValue();
		LongPrimaryKey pKey = new LongPrimaryKey(id);
		idlePoints.setId(pKey);
		String sql = "INSERT INTO idlepoints( id, tripid, idlelocation, starttime, endtime, locationname) "
				+ "VALUES (?, ?,"
				+ "GeometryFromText('POINT ("
				+ idlePoints.getIdleLocation().getFirstPoint().getX()
				+ " "
				+ idlePoints.getIdleLocation().getFirstPoint().getY()
				+ ")',-1)" + ", ?, ?, ?)";
		Object args[] = new Object[] { id, idlePoints.getTripid(),
				idlePoints.getStarttime(), idlePoints.getEndtime(),
				idlePoints.getLocationName() };
		int types[] = new int[] { Types.BIGINT, Types.BIGINT, Types.TIMESTAMP,
				Types.TIMESTAMP, Types.VARCHAR };
		LOG.debug("Idlepoints Insert Query "+sql);
		jdbcTemplate.update(sql, args, types);
		return idlePoints;
	}

	public IdlePoints selectForTripOrderByEndTime(long tripId) {
		String sql = "select * from idlepoints where tripid = " + tripId
				+ " order by endtime desc limit 1";
		List<IdlePoints> idlePoints = jdbcTemplate.query(sql,
				new IdlePointsRowMapper());
		return (null == idlePoints || idlePoints.size() == 0) ? null
				: idlePoints.get(0);
	}

	@Override
	public List<IdlePoints> selectAll() {
		String sql = "select * from idlepoints";
		return jdbcTemplate.query(sql, new IdlePointsRowMapper());
	}

	@Override
	public List<IdlePoints> selectByPrimaryKey(LongPrimaryKey primaryKey) {
		String sql = "select * from idlepoints where id = "
				+ primaryKey.getId();
		return jdbcTemplate.query(sql, new IdlePointsRowMapper());
	}

	@Override
	public IdlePoints update(IdlePoints idlePoints)
			throws OperationNotSupportedException {

		String sql = "UPDATE idlepoints SET tripid=?, " + "idlelocation= "
				+ "GeometryFromText('POINT ("
				+ idlePoints.getIdleLocation().getFirstPoint().getX() + " "
				+ idlePoints.getIdleLocation().getFirstPoint().getY()
				+ ")',-1)" + ", starttime=?, endtime=?, locationname=?  WHERE id = ?";
		Object args[] = new Object[] { idlePoints.getTripid(), idlePoints.getStarttime(), idlePoints.getEndtime(),
				idlePoints.getLocationName(), idlePoints.getId().getId() };
		int types[] = new int[] { Types.BIGINT, Types.TIMESTAMP, Types.TIMESTAMP, Types.VARCHAR, Types.BIGINT };
		LOG.debug("Idlepoints Update Query "+sql);
		jdbcTemplate.update(sql, args, types);
		return idlePoints;
	}

	public List<IdlePoints> selectAllIdlePointsBetweenDates(long tripId, Date startDate, Date endDate) {
		List<IdlePoints> idlePointList = new ArrayList<IdlePoints>();
		List<IdlePoints> idlePointReturnList = new ArrayList<IdlePoints>();

		String sql = "select * from idlepoints where tripid = ? and endtime > ?"+
				" and starttime < ? order by starttime";
		Object[] args = { tripId, new Timestamp(startDate.getTime()),
				new Timestamp(endDate.getTime())};
		int[] types = { Types.BIGINT, Types.TIMESTAMP, Types.TIMESTAMP/*, Types.TIMESTAMP, Types.TIMESTAMP */};
		idlePointList = jdbcTemplate.query(sql, args, types, new IdlePointsRowMapper());

		if(idlePointList != null){
			for(IdlePoints idlePoint : idlePointList){
				Date endTime = idlePoint.getEndtime(); //EndTime of the trip
				long endDateMilliSeconds =endTime.getTime(); //MilliSeconds Of EndTime
				Date selectedEndTime = new Timestamp(endDate.getTime()); //Selected EndTime from TimeFrame
				long selectedEndDateMilliSeconds=selectedEndTime.getTime();//MilliSeconds Of selectedEndTime

				Date startTime = idlePoint.getStarttime();//StartTime of the trip
				long startTimeMilliSeconds =startTime.getTime();//MilliSeconds Of StartTime
				Date selectedStartTime = new Timestamp(startDate.getTime());//Selected StartTime from TimeFrame
				long selectedStartTimeMilliSeconds=selectedStartTime.getTime();//MilliSeconds Of SelectedTimeFrame

				//gives the output in IdlePointReport according to selected TimeFrame(Time Duration)
				if(startTimeMilliSeconds<selectedStartTimeMilliSeconds && endDateMilliSeconds>selectedEndDateMilliSeconds){
					idlePoint.setStarttime(new Timestamp(startDate.getTime()));
					idlePoint.setEndtime(new Timestamp(endDate.getTime()));
					idlePointReturnList.add(idlePoint);
				}else if(endDateMilliSeconds>selectedEndDateMilliSeconds){
					idlePoint.setEndtime(new Timestamp(endDate.getTime()));
					idlePointReturnList.add(idlePoint);
				}else if(startTimeMilliSeconds<selectedStartTimeMilliSeconds){
					idlePoint.setStarttime(new Timestamp(startDate.getTime()));
					idlePointReturnList.add(idlePoint);
				}else{
					idlePointReturnList.add(idlePoint);
				}
			}
		}
		return idlePointReturnList;

	}

	/**
	 * To get only 15 records between the interval provided 
	 * @param tripId
	 * @param startDate
	 * @param endDate
	 * @return
	 */
	public List<IdlePoints> selectAllIdlePointsBetweenDatesWithLimit(long tripId, Date startDate, Date endDate) {
		List<IdlePoints> idlePointList = new ArrayList<IdlePoints>();
		List<IdlePoints> idlePointReturnList = new ArrayList<IdlePoints>();

		String sql = "select * from idlepoints where tripid = ? and endtime > ?"+
				" and starttime < ? order by starttime limit 15";
		Object[] args = { tripId, new Timestamp(startDate.getTime()),
				new Timestamp(endDate.getTime())};
		int[] types = { Types.BIGINT, Types.TIMESTAMP, Types.TIMESTAMP/*, Types.TIMESTAMP, Types.TIMESTAMP */};
		idlePointList = jdbcTemplate.query(sql, args, types, new IdlePointsRowMapper());

		if(idlePointList != null){
			for(IdlePoints idlePoint : idlePointList){
				Date endTime = idlePoint.getEndtime(); //EndTime of the trip
				long endDateMilliSeconds =endTime.getTime(); //MilliSeconds Of EndTime
				Date selectedEndTime = new Timestamp(endDate.getTime()); //Selected EndTime from TimeFrame
				long selectedEndDateMilliSeconds=selectedEndTime.getTime();//MilliSeconds Of selectedEndTime

				Date startTime = idlePoint.getStarttime();//StartTime of the trip
				long startTimeMilliSeconds =startTime.getTime();//MilliSeconds Of StartTime
				Date selectedStartTime = new Timestamp(startDate.getTime());//Selected StartTime from TimeFrame
				long selectedStartTimeMilliSeconds=selectedStartTime.getTime();//MilliSeconds Of SelectedTimeFrame

				//gives the output in IdlePointReport according to selected TimeFrame(Time Duration)
				if(startTimeMilliSeconds<selectedStartTimeMilliSeconds && endDateMilliSeconds>selectedEndDateMilliSeconds){
					idlePoint.setStarttime(new Timestamp(startDate.getTime()));
					idlePoint.setEndtime(new Timestamp(endDate.getTime()));
					idlePointReturnList.add(idlePoint);
				}else if(endDateMilliSeconds>selectedEndDateMilliSeconds){
					idlePoint.setEndtime(new Timestamp(endDate.getTime()));
					idlePointReturnList.add(idlePoint);
				}else if(startTimeMilliSeconds<selectedStartTimeMilliSeconds){
					idlePoint.setStarttime(new Timestamp(startDate.getTime()));
					idlePointReturnList.add(idlePoint);
				}else{
					idlePointReturnList.add(idlePoint);
				}
			}
		}
		return idlePointReturnList;

	}

	public List<IdlePoints> selectAllIdlePointsBetweenDatesForMapreport(long vehicleId, Date startDate, Date endDate) {
			String sql = "select * from idlepoints where tripid in(select id from trips where vehicleid= ? ) and endtime > ? "
					+ " and endtime <= ? and starttime < ? and starttime >= ? ";
			Object[] args = { vehicleId, new Timestamp(startDate.getTime()),
					new Timestamp(endDate.getTime()), new Timestamp(endDate.getTime()), new Timestamp(startDate.getTime())};
			int[] types = { Types.BIGINT, Types.TIMESTAMP, Types.TIMESTAMP, Types.TIMESTAMP, Types.TIMESTAMP };
			return jdbcTemplate.query(sql, args, types, new IdlePointsRowMapper());
	}

	
	/**
	 * 
	 * Select all idle points between the given time interval based on selected driver id 
	 * @param driverId
	 * @param startDate
	 * @param endDate
	 * @return
	 */
	public List<IdlePoints> selectAllIdlePointsBetweenDatesForMapreportByDriverId(long driverId, Date startDate, Date endDate) {
			String sql = "select * from idlepoints where tripid in(select id from trips where driverid= ?  and active='t'  ) and endtime > ? "
					+ " and endtime <= ? and starttime < ? and starttime >= ? ";
			Object[] args = { driverId, new Timestamp(startDate.getTime()), new Timestamp(endDate.getTime()), 
					new Timestamp(endDate.getTime()), new Timestamp(startDate.getTime())};
			int[] types = { Types.BIGINT, Types.TIMESTAMP, Types.TIMESTAMP, Types.TIMESTAMP, Types.TIMESTAMP };
			return jdbcTemplate.query(sql, args, types, new IdlePointsRowMapper());
	}
	
	public IdlePoints selectUniqueByKey(long pKey) {
		String sql = "select * from idlepoints where id = " + pKey;
		return (IdlePoints) jdbcTemplate.query(sql, new IdlePointsRowMapper()).get(0);
	}

	public List<IdlePoints> selectAllIdlePointsBetweenDatesByVehicleId(Long vehicleId, Date startDate, Date endDate) {
		String sql = "select * from idlepoints where tripid in (select id from trips where vehicleid = ? ) and starttime> = ? and starttime <= ? "
				+ " and endtime >= ? and endtime <= ? order by starttime";
		Object[] args = { vehicleId, new Timestamp(startDate.getTime()), new Timestamp(endDate.getTime()), 
				new Timestamp(startDate.getTime()), new Timestamp(endDate.getTime())};
		int[] types = { Types.BIGINT, Types.TIMESTAMP, Types.TIMESTAMP, Types.TIMESTAMP, Types.TIMESTAMP };
		return jdbcTemplate.query(sql, args, types, new IdlePointsRowMapper());
	}

}
