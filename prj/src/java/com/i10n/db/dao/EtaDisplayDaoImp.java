package com.i10n.db.dao;

import java.sql.Types;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.jdbc.core.JdbcTemplate;

import com.i10n.db.entity.EtaDisplay;
import com.i10n.db.entity.primarykey.LongPrimaryKey;
import com.i10n.db.entity.rowmapper.EtaDisplayRowMapper;
import com.i10n.db.idao.IEtaDisplayDAO;
import com.i10n.fleet.exceptions.OperationNotSupportedException;

@SuppressWarnings("unchecked")
public class EtaDisplayDaoImp implements IEtaDisplayDAO{

	private static Logger LOG = Logger.getLogger(EtaDisplayDaoImp.class);

	private JdbcTemplate jdbcTemplate;

	public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}

	@Override
	public EtaDisplay delete(EtaDisplay entity) throws OperationNotSupportedException {
		String sql = "delete from etadisplay where vehicleid = ? and routeid = ? and stopid = ? ";
		Object[] args = new Object[]{entity.getVehicleId(),entity.getRouteId(), entity.getStopId()};
		int[] types = new int[]{Types.BIGINT,Types.BIGINT,Types.BIGINT};
		jdbcTemplate.update(sql, args, types);
		return entity;
	}

	@Override
	public EtaDisplay insert(EtaDisplay entity) throws OperationNotSupportedException {
		String sql = "INSERT INTO etadisplay(vehicleid, routeid, stopid, arrivaltime,routename,seqno)VALUES (?, ?, ?, ?, ?, ?)";
		Object[] args = new Object[]{entity.getVehicleId(),entity.getRouteId(),entity.getStopId(),entity.getArrivalTime(),
				entity.getRouteName(),entity.getSequneceNumber()};
		int[] types = new int[]{Types.BIGINT,Types.BIGINT,Types.BIGINT,Types.INTEGER,Types.VARCHAR,Types.INTEGER};
		jdbcTemplate.update(sql, args, types);
		return entity;
	}

	@Override
	public List<EtaDisplay> selectAll() {
		String sql = "select * from etadisplay order by vehicleid desc";
		return jdbcTemplate.query(sql, new EtaDisplayRowMapper());
	}

	@Override
	public List<EtaDisplay> selectByPrimaryKey(LongPrimaryKey primaryKey) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public EtaDisplay update(EtaDisplay entity) throws OperationNotSupportedException {
		String sql = "update etadisplay set arrivalTime = ? where vehicleid = ? and routeid = ? and stopid = ?";
		Object[] args = new Object[]{entity.getArrivalTime(),entity.getVehicleId(),entity.getRouteId(),entity.getStopId()};
		int[] types = new int[]{Types.INTEGER,Types.BIGINT,Types.BIGINT,Types.BIGINT};
		int rowsUpdated = jdbcTemplate.update(sql, args, types);
		if (rowsUpdated < 1) {
			entity = null;
		}
		return entity;
	}

	public EtaDisplay updateEtaDisplay(EtaDisplay entity) {
		String etaDisplayUpdateQuery ="update etadisplay set arrivalTime = "+entity.getArrivalTime()+" where vehicleid = "+
				entity.getVehicleId()+" and routeid = "+entity.getRouteId()+" and stopid = "+entity.getStopId();
		LOG.debug("ETAETAETAETAETANODB : Updating EtaDisplay entry with latest arrival time : Query = "+etaDisplayUpdateQuery);
		jdbcTemplate.update(etaDisplayUpdateQuery);
		return entity;
	}

	public List<EtaDisplay> selectByVehicleIdRouteIdAndStopId(long vehicleId, long routeId, long stopId) {
		String sql = "select * from etadisplay where vehicleid = ? and routeid = ? and stopid = ? order by vehicleid desc";
		Object[] args = new Object[]{vehicleId, routeId, stopId};
		int[] types = new int[]{Types.BIGINT,Types.BIGINT,Types.BIGINT};
		return jdbcTemplate.query(sql, args, types, new EtaDisplayRowMapper());
	}

	public void deleteOnRouteDeactivation(long vehicleId, long routeId) {
		LOG.debug("ETAETAETAETAETANODB : Deleting etaDisplay entries on deactivating Route : "+routeId+" for Vehicle : "+vehicleId);
		// Checking for the entries which are going to be deleted
		String toBeDeletedEtaEntries = "select * from etadisplay where vehicleid = "+vehicleId+" and routeid = "+routeId;
		List<EtaDisplay> etaList = jdbcTemplate.query(toBeDeletedEtaEntries, new EtaDisplayRowMapper());
		LOG.debug("ETAETAETAETAETANODB : Eta entities going to be deleted from table level are ..");
		for(EtaDisplay etaDisplay : etaList){
			LOG.debug("ETAETAETAETAETANODB : "+ etaDisplay.toString());
		}
		String deleteETAOnRouteDeactivationQuery = "delete from etadisplay where vehicleid = "+vehicleId+" and routeid = "+routeId;
		int numberOfRowsDeleted = jdbcTemplate.update(deleteETAOnRouteDeactivationQuery);
		LOG.debug("ETAETAETAETAETANODB : Deleted entries on deactivating Route : "+routeId+" for Vehicle : "+vehicleId+" with query : "+
				deleteETAOnRouteDeactivationQuery+"\n Number of rows deleted : "+numberOfRowsDeleted);
	}

	public List<EtaDisplay> selectByStopId(Long busStopId) {
		String sql = "select * from etadisplay where stopid = ?";
		Object[] args = new Object[]{busStopId};
		int[] types = new int[]{Types.BIGINT};
		return jdbcTemplate.query(sql, args, types, new EtaDisplayRowMapper());
	}

	public List<EtaDisplay> selectByStopIdForGujrat(Long busStopId) {
		String sql = "select * from etadisplay where stopid = ? and arrivalTime < 60 ";
		Object[] args = new Object[]{busStopId};
		int[] types = new int[]{Types.BIGINT};
		return jdbcTemplate.query(sql, args, types, new EtaDisplayRowMapper());
	}

	public List<EtaDisplay> selectByStopIdAndRouteId(long busStopId, long routeId) {
		String sql = "select * from etadisplay where stopid = ? and routeid = ?";
		Object[] args = new Object[]{busStopId, routeId};
		int[] types = new int[]{Types.BIGINT, Types.BIGINT};
		return jdbcTemplate.query(sql, args, types, new EtaDisplayRowMapper());
	}

	/**
	 * Once the vehicle is with in 10 kms then update the type as arriving.
	 * @param entity
	 */
	public EtaDisplay updateType(EtaDisplay entity) {
		String updateEtaDisplayTypeQuery = "update etadisplay set type = ?, deleted = ? where vehicleid = ? and routeid = ? and stopid = ?";
		Object[] args = new Object[]{entity.getType(), entity.isDeleted(), entity.getVehicleId(), entity.getRouteId(), entity.getStopId()};
		int[] types = new int[]{Types.INTEGER, Types.BOOLEAN, Types.BIGINT, Types.BIGINT, Types.BIGINT};
		jdbcTemplate.update(updateEtaDisplayTypeQuery, args, types);
		return entity;
	}

	/**
	 * Once the vehicle has arrived delete the entry virtually.
	 * @param entity
	 */
	public EtaDisplay deleteType(EtaDisplay entity) {
		String updateEtaDisplayTypeQuery = "update etadisplay set deleted = true , type = ? where vehicleid = ? and routeid = ? and stopid = ?";
		Object[] args = new Object[]{entity.getType(), entity.getVehicleId(), entity.getRouteId(), entity.getStopId()};
		int[] types = new int[]{Types.INTEGER, Types.BIGINT, Types.BIGINT, Types.BIGINT};
		jdbcTemplate.update(updateEtaDisplayTypeQuery, args, types);
		return entity;
	}

	public void deleteOnStopReaching(long vehicleId, long routeId, int sequenceNumber) {
		String sql = "delete from etadisplay where vehicleid = "+vehicleId+" and routeid = "+routeId+" and seqno <= "+sequenceNumber;
		Object[] args = new Object[]{vehicleId, routeId, sequenceNumber};
		int[] types = new int[]{Types.BIGINT, Types.BIGINT, Types.INTEGER};
		jdbcTemplate.update(sql, args, types);
	}

	public List<EtaDisplay> selectByVehicleId(long vehicleId) {
		String sql = "select * from etadisplay where vehicleid = "+vehicleId+" order by vehicleid desc";
		Object[] args = new Object[]{vehicleId};
		int[] types = new int[]{Types.BIGINT};
		return jdbcTemplate.query(sql, args, types, new EtaDisplayRowMapper());
	}
}
