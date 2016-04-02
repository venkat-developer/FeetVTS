package com.i10n.db.dao;

import java.sql.Types;
import java.util.List;

import org.springframework.jdbc.core.JdbcTemplate;

import com.i10n.db.entity.EtaDisplay;
import com.i10n.db.entity.EtdOfVehiclesToStops;
import com.i10n.db.entity.primarykey.LongPrimaryKey;
import com.i10n.db.entity.rowmapper.EtdOfVehiclesToStopsRowMapper;
import com.i10n.db.idao.IEtaDisplayDAO;
import com.i10n.fleet.exceptions.OperationNotSupportedException;

@SuppressWarnings("unchecked")
public class EtdOfVehiclesToStopsDaoImp implements IEtaDisplayDAO{

	private JdbcTemplate jdbcTemplate;

	public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}
	
	@Override
	public EtaDisplay delete(EtaDisplay entity)
			throws OperationNotSupportedException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public EtaDisplay insert(EtaDisplay entity)
			throws OperationNotSupportedException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<EtaDisplay> selectAll() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<EtaDisplay> selectByPrimaryKey(LongPrimaryKey primaryKey) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public EtaDisplay update(EtaDisplay entity) throws OperationNotSupportedException {
		// TODO Auto-generated method stub
		return null;
	}

	public List<EtdOfVehiclesToStops> getByVehicleIdAndStopId(long vehicleId, long stopId) {
		String sql ="select * from etdofvehiclestostops where vehicleid = ? and stopid = ?";
		Object args[] = new Object[] {vehicleId, stopId };
		int types[] = new int[] { Types.BIGINT, Types.BIGINT };
		return jdbcTemplate.query(sql, args, types, new EtdOfVehiclesToStopsRowMapper());
	}
}
