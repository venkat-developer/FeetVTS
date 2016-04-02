package com.i10n.db.dao;

import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.jdbc.core.JdbcTemplate;

import com.i10n.db.entity.RouteSchedule;
import com.i10n.db.entity.primarykey.LongPrimaryKey;
import com.i10n.db.entity.rowmapper.RouteScheduleRowMapper;
import com.i10n.db.entity.rowmapper.RouteScheduleWithRouteRowMapper;
import com.i10n.db.entity.rowmapper.RouteScheduleAssignRowMapper;
import com.i10n.db.idao.IRouteScheduleDAO;
import com.i10n.fleet.exceptions.OperationNotSupportedException;

@SuppressWarnings("unchecked")
public class RouteScheduleDaoImpl implements IRouteScheduleDAO {
	private Logger LOG =Logger.getLogger(RouteScheduleDaoImpl.class);

	private JdbcTemplate jdbcTemplate;

	public JdbcTemplate getJdbcTemplate() {
		return jdbcTemplate;
	}

	public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}

	

	public List<RouteSchedule> selectAll() {
		String sql = "select * from routeschedule order by routescheduleid, expectedtime asc";
		LOG.debug("Loading LoadRouteSchedule query is "+sql);
		return jdbcTemplate.query(sql, new RouteScheduleRowMapper());
	}

	public List<RouteSchedule> selectByPrimaryKey(LongPrimaryKey primaryKey) {
		// TODO Auto-generated method stub
		return null;
	}

	public List<RouteSchedule> selectByRouteScheduleId(String routeScheduleId) {
		String sql = "select * from routeschedule where routescheduleid like '"+routeScheduleId+"' order by expectedtime asc";
		return jdbcTemplate.query(sql, new RouteScheduleRowMapper());
	}
	/**
	 * Function will connect with the database and 
	 * insert the values of RouteSchedules into the RouteSchedules Table
	 * @return the RouteSchedule with all entered parameters
	 * 
	*/
	@Override
	public RouteSchedule insert(RouteSchedule rs)
			throws OperationNotSupportedException {
		// TODO Auto-generated method stub

		String sql = "insert into routeschedule(routescheduleid,routeid,stopid,seqno,expectedtime,estimateddistance,spanday)" +
				" values(?,?,?,1,?,0,1)";
		Object args[] = new Object[] {rs.getRouteScheduleId(),rs.getRouteId(),rs.getStopId(),rs.getExpectedTime()};
		int types[] = new int[] {Types.VARCHAR,Types.BIGINT,Types.BIGINT,Types.TIME};
		jdbcTemplate.update(sql, args, types);
		return rs;

	}
	/**
	 * List getRouteScheduleIDList returns the Existing List Of RouteScheduleID and 
	 * @return the list of Existed RouteSchedulesID
	 */
	public List<RouteSchedule> getRouteSchedulesIDList(){
		String sql = "select rs.id,rs.routescheduleid,rs.routeid,rs.stopid,rs.seqno,"+
					"rs.expectedtime,rs.estimateddistance,rs.spanday,r.id,r.routename,r.ownerid,"+
					"s.id,s.stopname from routeschedule rs,routes r,stops s "+
					" where rs.seqno = '1' and r.id = rs.routeid and s.id = rs.stopid";
		return jdbcTemplate.query(sql, new RouteScheduleWithRouteRowMapper());
	}
	/**
	 * getRouteSchedulesList gets the data from Database for selected RouteScheduleID and prints into the table
	 * @param rsID
	 * @return RouteScheduleList of Selected RouteScheduleID
	 * @throws SQLException
	 */
	
	public List<RouteSchedule> getRouteSchedulesList(String rsID) throws SQLException{
		List<RouteSchedule> routeSchedule = new ArrayList<RouteSchedule>();
		try{
			String sql = "select rs.id,rs.routescheduleid,rs.routeid,rs.stopid,rs.seqno,"+
					     "rs.expectedtime,rs.estimateddistance, rs.spanday,r.routename, s.stopname"+
					     " from routeschedule rs,routes r,stops s where r.id = rs.routeid and s.id = rs.stopid " +
					     "and rs.routescheduleid='"+rsID+"'";
			return  jdbcTemplate.query(sql, new RouteScheduleWithRouteRowMapper());
		}catch(Exception e){
			LOG.error("Exception In Fetching",e);
			
		}
		return routeSchedule;
	}

	/**
	 * Function Allows to Delete The Selected RouteSchedules
	 * @return the selected RouteSchedule to delete
	 */
	@Override
	public RouteSchedule delete(RouteSchedule rs)
			throws OperationNotSupportedException {
		// TODO Auto-generated method stub
		String sql = "delete from routeschedule where id = ?";
		Object args[] = new Object[]{rs.getId().getId()};
		int types[] = new int[]{Types.BIGINT};
		jdbcTemplate.update(sql,args,types);
		int rowsDeleted = jdbcTemplate.update(sql, args, types);
		LOG.info("rowsDeleted : "+rowsDeleted);
		if (rowsDeleted < 1) {
			rs = null;
		}	
		return rs;
	}
	
	/**
	 * Function allows to update the parameters of Selected RouteSchedule
	 * @return the Updated RouteSchedule 
	 */
	@Override
	public RouteSchedule update(RouteSchedule rs)
			throws OperationNotSupportedException {
		// TODO Auto-generated method stub
		String sql = "update routeschedule set expectedtime=?,estimateddistance=?,spanday=? where routescheduleid = ?";
		Object args[] = new Object[]{rs.getExpectedTime(),rs.getEstimatedDistance(),rs.getSpanDay(),rs.getSpanDay()};
		int types[] = new int[]{Types.TIME,Types.REAL,Types.REAL,Types.VARCHAR};

		int rowsUpdated = jdbcTemplate.update(sql, args, types);
		if (rowsUpdated < 1) {
			rs = null;
		}		
		return rs;
	}
	
	/**
	 * Gets all The Existed RouteScheduleID Of RouteSchedules
	 * @return List of All RouteScheduleID
	 * @throws SQLException 
	 */
	public List<RouteSchedule> getRouteScheduleIDAll(Long ownerid) throws SQLException{
		List<RouteSchedule> routeSchedule= new ArrayList<RouteSchedule>();
		try{
			String sql ="select rs.id,rs.routescheduleid,rs.routeid,"+
					"rs.stopid,rs.seqno,rs.expectedtime,"+
					"rs.estimateddistance,rs.spanday,r.id,r.routename,"+
					"r.ownerid,s.id,s.stopname"+
					" from routeschedule rs,routes r,stops s where r.id = rs.routeid and s.id = rs.stopid and r.ownerid = "+ownerid;
			return  jdbcTemplate.query(sql, new RouteScheduleWithRouteRowMapper());
		}catch(Exception e){
			LOG.error("Error In Route Schedules",e);
		}
		return routeSchedule;
	}

	public List<RouteSchedule> getAssignedRouteSchedules(Long vehicleid) {
		LOG.debug("Assigned vehicleid:"+vehicleid);
		String sql = "select DISTINCT(routescheduleid) from routeschedule where routescheduleid || ':00'  " +
				"in (select routeid || '-' || scheduletime from vehicletorouteschedule where vehicleid=?)";
		Object[] args = new Object[] {vehicleid};
		int[] types = new int[] { Types.BIGINT};
		return jdbcTemplate.query(sql, args, types, new RouteScheduleAssignRowMapper());
		
	}


	public List<RouteSchedule> getVacantRouteSchedules(Long vehicleid) {
		// TODO Auto-generated method stub
		LOG.debug("Vacant vehicleid:"+vehicleid);
		String sql = "select DISTINCT(routescheduleid) from routeschedule where routescheduleid || ':00' not " +
				"in (select routeid || '-' || scheduletime from vehicletorouteschedule)";
		return jdbcTemplate.query(sql, new RouteScheduleAssignRowMapper());
		
		
	}

}
