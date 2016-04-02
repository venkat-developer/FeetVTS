package com.i10n.db.dao;

import java.sql.Types;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.incrementer.DataFieldMaxValueIncrementer;

import com.i10n.db.entity.StopHistory;
import com.i10n.db.entity.Stops;
import com.i10n.db.entity.primarykey.LongPrimaryKey;
import com.i10n.db.entity.rowmapper.StopHistoryRowMapper;
import com.i10n.db.entity.rowmapper.StopsRowMapper;
import com.i10n.db.idao.IStopsDAO;
import com.i10n.fleet.exceptions.OperationNotSupportedException;

public class StopsDaoImp implements IStopsDAO{

	@SuppressWarnings("unused")
	private static final Logger LOG = Logger.getLogger(StopsDaoImp.class);
	private JdbcTemplate jdbcTemplate;
	private DataFieldMaxValueIncrementer stopsIdIncrementer;

	public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}

	public void setStopsIdIncrementer(
			DataFieldMaxValueIncrementer bookIncrementer) {
		this.stopsIdIncrementer = bookIncrementer;
	}

	public DataFieldMaxValueIncrementer getStopsIdIncrementer() {
		return stopsIdIncrementer;
	}
	
	@Override
	public Stops delete(Stops entity) throws OperationNotSupportedException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Stops insert(Stops entity) throws OperationNotSupportedException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Stops> selectAll() {
		// TODO Auto-generated method stub
		return null;
	}
	/**
	 * gets the existed stops from the stops table and prints into the ListBox
	 */

	@SuppressWarnings("unchecked")
	public List<Stops> getStopNameList(Long ownerid){
		String sql = "select * from stops where ownerid = "+ownerid;
		return jdbcTemplate.query(sql, new StopsRowMapper());	
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Stops> selectByPrimaryKey(LongPrimaryKey primaryKey) {
		String sql = "select * from stops where id = " + primaryKey.getId();
		return jdbcTemplate.query(sql, new StopsRowMapper());
	}
	
	public String getVehicleName(long vehicleid){
		String sql = "select displayname from vehicles where id=?";
		Object[] args = new Object[]{vehicleid};
		int[] types = new int[]{Types.BIGINT};
		return jdbcTemplate.queryForObject(sql, args, types, String.class).toString();
	}
	
	public String getStopName(long stopid){
		String sql = "select stopname from stops where id=?";
		Object[] args = new Object[]{stopid};
		int[] types = new int[]{Types.BIGINT};
		return jdbcTemplate.queryForObject(sql, args, types, String.class).toString();
	}
	
	@Override
	public Stops update(Stops entity) throws OperationNotSupportedException {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * Get the previous stop sequence number.
	 * @param stopid
	 * @param routeid
	 * @return
	 */
	public int getPreviousStopSeqNo(long stopid,long routeid) {
		String sql = "select seqno from VehicleRouteStopDetails where stopid=? and routeid=?";
		Object[] args = new Object[]{stopid,routeid};
		int[] types = new int[]{Types.BIGINT,Types.BIGINT};
		int seq = 0;
		seq = jdbcTemplate.queryForInt(sql, args, types);
		if(seq != 0){
		return (seq-1);
		}
		return seq;
	}

	/**
	 * Check whether the stop is already reached or not for the current route.
	 * @param stopId
	 * @param routeId
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public boolean isCurrentStopReached(long stopId, long routeId){
		boolean flag = false;
		String sql = "select * from stophistory where stopid=? and routeid=? and active=true";
		Object[] args = new Object[]{stopId,routeId};
		int[] types = new int[]{Types.BIGINT,Types.BIGINT};
		List<StopHistory> history = jdbcTemplate.query(sql, args, types, new StopHistoryRowMapper());
		if(history.size() != 0){
			flag = true;
		}
		return flag;
	}

}