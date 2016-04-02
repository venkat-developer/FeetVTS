package com.i10n.db.dao;

import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.jdbc.core.JdbcTemplate;

import com.i10n.db.entity.RouteTrack;
import com.i10n.db.entity.primarykey.LongPrimaryKey;
import com.i10n.db.entity.rowmapper.RouteTrackRowMapper;
import com.i10n.db.idao.IRouteTrackDAO;
import com.i10n.fleet.exceptions.OperationNotSupportedException;
import com.i10n.fleet.util.DateUtils;

@SuppressWarnings("unchecked")
public class RouteTrackDaoImp implements IRouteTrackDAO{

	private static Logger LOG = Logger.getLogger(RouteTrackDaoImp.class);

	private JdbcTemplate jdbcTemplate;

	public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}

	@Override
	public RouteTrack delete(RouteTrack entity)
			throws OperationNotSupportedException {
		String sql ="delete from routetrack where starttrackhistoryid = ?";
		Object args[] = new Object[] { entity.getStartTrackHistoryId()};
		jdbcTemplate.update(sql, args);
		return entity;
	}

	@Override
	public RouteTrack insert(RouteTrack entity)
			throws OperationNotSupportedException {
		String sql ="INSERT INTO routetrack(startdate, imei, routeid, starttrackhistoryid, endtrackhistoryid)"+
				"VALUES (?, ?, ?, ?, ?)";
		Object args[] = new Object[] { entity.getRouteStartDate(), entity.getIMEI(), entity.getRouteId(), /*entity.getTripId(),*/
				entity.getStartTrackHistoryId(), entity.getEndTrackHistoryId()};
		jdbcTemplate.update(sql, args);
		return entity;
	}
	public RouteTrack updateRouteTrack(RouteTrack entity) {
		String routeTrackUpdateQuery ="update routetrack set endtrackhistoryid = "+entity.getEndTrackHistoryId()
				+" where routeid = "+entity.getRouteId()+" and endtrackhistoryid = 0";
		LOG.debug("ETAETAETAETAETANODB : Updating EndtrackhistoryId in table : Query = "+routeTrackUpdateQuery);
		jdbcTemplate.execute(routeTrackUpdateQuery);
		return entity;
	}

	public RouteTrack insertIntoRouteTrack(RouteTrack entity, Statement statement) {
		try {
			String routeTrackInsertQuery ="INSERT INTO routetrack(startdate, imei, routeid, starttrackhistoryid, endtrackhistoryid)"+
					"VALUES ( '"+new Timestamp(entity.getRouteStartDate().getTime())+"','"+ entity.getIMEI()+"',"+ entity.getRouteId()+","
					/*+ entity.getTripId()+","*/+ entity.getStartTrackHistoryId()+","+ entity.getEndTrackHistoryId()+")";
			LOG.debug("ETAETAETAETAETANODB : Inserting into RouteTrack Table : Query = "+routeTrackInsertQuery);
			statement.executeUpdate(routeTrackInsertQuery);
		} catch (SQLException e) {
			LOG.error("ETAETAETAETAETANODB : Error while inserting into RouteTrack Table");
		}
		return entity;
	}
	
	public RouteTrack insertIntoRouteTrack(RouteTrack entity) {
		String routeTrackInsertQuery ="INSERT INTO routetrack(startdate, imei, routeid, starttrackhistoryid, endtrackhistoryid)"+
				"VALUES ( '"+new Timestamp(entity.getRouteStartDate().getTime())+"','"+ entity.getIMEI()+"',"+ entity.getRouteId()+","
				+ entity.getStartTrackHistoryId()+","+ entity.getEndTrackHistoryId()+")";
		LOG.debug("ETAETAETAETAETANODB : Inserting into RouteTrack Table : Query = "+routeTrackInsertQuery);
		jdbcTemplate.execute(routeTrackInsertQuery);
		return entity;
	}

	@Override
	public List<RouteTrack> selectAll() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<RouteTrack> selectByPrimaryKey(LongPrimaryKey primaryKey) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public RouteTrack update(RouteTrack entity)
			throws OperationNotSupportedException {
		String sql = "update routetrack set endtrackhistoryid = ? where routeid=? and endtrackhistoryid = 0";
		Object args[] = new Object[] {entity.getEndTrackHistoryId(), entity.getRouteId(), /*entity.getTripId() */};
		int types[] = new int[] { Types.BIGINT, Types.BIGINT, Types.BIGINT };
		int rowsUpdated = jdbcTemplate.update(sql, args, types);
		if (rowsUpdated < 1) {
			entity = null;
		}
		return entity;
	}

	public RouteTrack updateRouteTrack(RouteTrack entity, Statement statement) {
		try {
			String routeTrackUpdateQuery ="update routetrack set endtrackhistoryid = "+entity.getEndTrackHistoryId()
					+" where routeid = "+entity.getRouteId()+/*" and tripid = "+entity.getTripId()+*/" and endtrackhistoryid = 0";
			LOG.debug("ETAETAETAETAETANODB : Updating EndtrackhistoryId in table : Query = "+routeTrackUpdateQuery);
			statement.executeUpdate(routeTrackUpdateQuery);
		} catch (SQLException e) {
			LOG.debug("ETAETAETAETAETANODB : Error while updating EndtrackhistoryId in table",e); 
		}
		return entity;
	}

	public List<RouteTrack> getByRouteId(long routeId) {
		String sql = "select * from routetrack where routeid = ?";
		Object[] args = new Object []{routeId};
		int[] types = new int[]{Types.BIGINT};
		return jdbcTemplate.query(sql, args, types, new RouteTrackRowMapper());
	}

	public List<RouteTrack> selectByRouteIdTripId(long routeId, long tripId) {
		String sql = "select * from routetrack where routeid = ? and tripid = ? and endtrackhistoryid = 0";
		Object[] args = new Object []{routeId, tripId};
		int[] types = new int[]{Types.BIGINT, Types.BIGINT};
		return jdbcTemplate.query(sql, args, types, new RouteTrackRowMapper());
	}
	public void deleteOldEntries(Date time) {
		String sql = "delete from routetrack where startdate < '"+DateUtils.convertJavaDateToSQLDate(time)+"' and endtrackhistoryid != 0";
		int rowsAffected = jdbcTemplate.update(sql);
		LOG.debug("Successfully deleted the routetrack entries older by 3 days counting to : "+rowsAffected);
	}

	public List<RouteTrack> selectByRouteId(Long routeId) {
		String sql = "select * from routetrack where routeid = ?";
		Object[] args = new Object []{routeId};
		int[] types = new int[]{Types.BIGINT};
		return jdbcTemplate.query(sql, args, types, new RouteTrackRowMapper());
	}

}
