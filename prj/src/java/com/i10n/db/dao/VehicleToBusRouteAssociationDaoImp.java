package com.i10n.db.dao;

import java.sql.Types;
import java.util.List;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.incrementer.DataFieldMaxValueIncrementer;

import com.i10n.db.entity.VehicleToBusRouteAssociation;
import com.i10n.db.entity.primarykey.LongPrimaryKey;
import com.i10n.db.entity.rowmapper.VehicleToBusRouteAssociationRowMapper;
import com.i10n.db.idao.IVehicleToBusRouteAssociationDAO;
import com.i10n.fleet.exceptions.OperationNotSupportedException;

@SuppressWarnings("unchecked")
public class VehicleToBusRouteAssociationDaoImp implements IVehicleToBusRouteAssociationDAO {
	
	private JdbcTemplate jdbcTemplate;
	private DataFieldMaxValueIncrementer vehicletobusIdIncrementer;
	
	public JdbcTemplate getJdbcTemplate() {
		return jdbcTemplate;
	}
	
	public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}
	
	public DataFieldMaxValueIncrementer getVehicletobusIdIncrementer() {
		return vehicletobusIdIncrementer;
	}
	
	public void setVehicletobusIdIncrementer(
			DataFieldMaxValueIncrementer vehicletobusIdIncrementer) {
		this.vehicletobusIdIncrementer = vehicletobusIdIncrementer;
	}
	
	@Override
	public VehicleToBusRouteAssociation delete(VehicleToBusRouteAssociation entity)
	throws OperationNotSupportedException {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public VehicleToBusRouteAssociation insert(VehicleToBusRouteAssociation vehicletobus)
	throws OperationNotSupportedException {
		final Long did = vehicletobusIdIncrementer.nextLongValue();
		vehicletobus.setId(new LongPrimaryKey(did));
		String sql = "insert into vehicletobusrouteassociation(id,busroutenumber,vehicleid,lastcrossedstopsequence ,currentshift ,initialdelayduration) values (?,?,?,?,?,?)";
		Object args []= new Object[] {vehicletobus.getId().getId(),vehicletobus.getBusroutenumber(),vehicletobus.getVehicleid(),vehicletobus.getLastcrossedstopsequence(),vehicletobus.getCurrentshift(),vehicletobus.getInitialdelayduration()};
		int types[] = new int[] { Types.BIGINT, Types.VARCHAR,  Types.BIGINT, Types.INTEGER,Types.INTEGER,Types.INTEGER};
		jdbcTemplate.update(sql, args, types);
		return vehicletobus;
	}
	@Override
	public List<VehicleToBusRouteAssociation> selectAll() {
		String sql="select * from vehicletobusrouteassociation ";
		return jdbcTemplate.query(sql, new VehicleToBusRouteAssociationRowMapper());

	}
	@Override
	public List<VehicleToBusRouteAssociation> selectByPrimaryKey(
		LongPrimaryKey primaryKey) {
		String sql = "select a.id,a.busroutenumber,a.vehicleid ,a.lastcrossedstopsequence ,a.currentshift ,a.initialdelayduration from vehicletobusrouteassociation a, vehicles b "+"where a.id=? and a.vehicleid=b.id ";
		Object[] values = new Object[]{primaryKey.getId()};
		int[] types = new int[]{Types.BIGINT};
		return jdbcTemplate.query(sql, values, types, new VehicleToBusRouteAssociationRowMapper());
	}


	@Override
	public VehicleToBusRouteAssociation update(VehicleToBusRouteAssociation entity)
	throws OperationNotSupportedException {
		// TODO Auto-generated method stub
		return null;
	}
}