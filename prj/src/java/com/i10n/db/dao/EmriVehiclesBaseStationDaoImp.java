package com.i10n.db.dao;

import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.jdbc.core.JdbcTemplate;

import com.i10n.db.entity.EmriVehiclesBaseStation;
import com.i10n.db.entity.primarykey.LongPrimaryKey;
import com.i10n.db.entity.rowmapper.EmriVehiclesBaseStationRowMapper;
import com.i10n.db.idao.IEmriVehiclesBaseStationDAO;
import com.i10n.fleet.exceptions.OperationNotSupportedException;

@SuppressWarnings("unchecked")
public class EmriVehiclesBaseStationDaoImp implements IEmriVehiclesBaseStationDAO {

	@SuppressWarnings("unused")
	private static Logger LOG = Logger.getLogger(EmriVehiclesBaseStationDaoImp.class);

	private JdbcTemplate jdbcTemplate;

	public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}

	public List<EmriVehiclesBaseStation> selectByVehicleId(Long vehicleid) {
		StringBuffer selectByVehicleID = new StringBuffer();
		selectByVehicleID.append("select * from emrivehiclesbasestation where vehicleid=");
		selectByVehicleID.append(vehicleid);
		return jdbcTemplate.query(selectByVehicleID.toString(), new EmriVehiclesBaseStationRowMapper());
	}

	@Override
	public EmriVehiclesBaseStation insert(EmriVehiclesBaseStation entity)
			throws OperationNotSupportedException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public EmriVehiclesBaseStation delete(EmriVehiclesBaseStation entity)
			throws OperationNotSupportedException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public EmriVehiclesBaseStation update(EmriVehiclesBaseStation entity)
			throws OperationNotSupportedException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<EmriVehiclesBaseStation> selectAll() {
		StringBuffer selectAllEmriVehiclesBaseStation = new StringBuffer();
		selectAllEmriVehiclesBaseStation.append("select * from emrivehiclesbasestation");
		return jdbcTemplate.query(selectAllEmriVehiclesBaseStation.toString(),new EmriVehiclesBaseStationRowMapper());
	}

	@Override
	public List<EmriVehiclesBaseStation> selectByPrimaryKey(LongPrimaryKey primaryKey) {
		// TODO Auto-generated method stub
		return null;
	}
}