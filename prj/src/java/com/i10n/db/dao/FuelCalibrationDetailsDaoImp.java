package com.i10n.db.dao;

import java.sql.Types;
import java.util.List;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.incrementer.DataFieldMaxValueIncrementer;

import com.i10n.db.entity.FuelCalibrationDetails;
import com.i10n.db.entity.primarykey.LongPrimaryKey;
import com.i10n.db.entity.rowmapper.FuelCalibrationDetailsRowMapper;
import com.i10n.db.idao.IFuelCalibrationDetailsDAO;
import com.i10n.fleet.exceptions.OperationNotSupportedException;

@SuppressWarnings("unchecked")
public class FuelCalibrationDetailsDaoImp implements IFuelCalibrationDetailsDAO{
	
	private JdbcTemplate jdbcTemplate;
	private DataFieldMaxValueIncrementer fuelcalibrationdetailsIdIncrementer;
	
	public JdbcTemplate getJdbcTemplate() {
		return jdbcTemplate;
	}

	public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}

	public DataFieldMaxValueIncrementer getFuelcalibrationdetailsIdIncrementer() {
		return fuelcalibrationdetailsIdIncrementer;
	}

	public void setFuelcalibrationdetailsIdIncrementer(
			DataFieldMaxValueIncrementer fuelcalibrationdetailsIdIncrementer) {
		this.fuelcalibrationdetailsIdIncrementer = fuelcalibrationdetailsIdIncrementer;
	}

	@Override
	public FuelCalibrationDetails delete(FuelCalibrationDetails fuel) throws OperationNotSupportedException {
		return null;
	}

	@Override
	public FuelCalibrationDetails insert(FuelCalibrationDetails fuel) throws OperationNotSupportedException {
		java.sql.Timestamp sqlDate = new java.sql.Timestamp(new java.util.Date().getTime());
		fuel.setCaldate(sqlDate);
		String sql = "insert into fuelcalibrationdetails(calibratedat,calibratedbyemp,minad,maxad) values (?,?,?,?)";
		Object args []= new Object[] {fuel.getCaldate(),fuel.getCalibratedbyemp(),fuel.getMinAd(),fuel.getMaxAd()};
		int types[] = new int[] { Types.TIMESTAMP,Types.BIGINT,Types.INTEGER,Types.INTEGER};
		jdbcTemplate.update(sql, args, types);
		return fuel;
	}

	@Override
	public FuelCalibrationDetails update(FuelCalibrationDetails fuel) throws OperationNotSupportedException {
		return null;
	}
	
	@Override
	public List<FuelCalibrationDetails> selectByPrimaryKey(LongPrimaryKey primaryKey) {
		String sql = "select a.id as id,a.calibratedat as calibratedat,a.calibratedbyemp as calibratedbyemp from fuelcalibrationdetails a, " +
		"employees b where a.id = ? and a.calibratedbyemp = b.id";
		Object[] values = new Object[]{primaryKey.getId()};
		int[] types = new int[]{Types.BIGINT};
		List<FuelCalibrationDetails> fuel = jdbcTemplate.query(sql, values, types, new FuelCalibrationDetailsRowMapper());
		return (fuel == null || fuel.size() == 0)?null:fuel;
	}
	
	@Override
	public List<FuelCalibrationDetails> selectAll() {
		String sql = "select * from fuelcalibrationdetails";
		return jdbcTemplate.query(sql, new FuelCalibrationDetailsRowMapper());
	}

	public List<FuelCalibrationDetails> selectBasedOnVehicle(long calibrationIdOfVehicle){
		String sql = "select * from fuelcalibrationdetails where id=?";
		Object[] args = new Object[]{calibrationIdOfVehicle};
		int[] types = new int[]{Types.BIGINT};
		return jdbcTemplate.query(sql, args, types, new FuelCalibrationDetailsRowMapper());
	}

	public void updateMinMaxAd(FuelCalibrationDetails fuelCalDetails) {
		String sql = "update fuelcalibrationdetails set minad=?,maxad=? where id=?";
		Object[] args = new Object[]{fuelCalDetails.getMinAd(),fuelCalDetails.getMaxAd(),fuelCalDetails.getId().getId()};
		jdbcTemplate.update(sql, args);	
	}
	
	public List<FuelCalibrationDetails> getLastRow(){
		String sql = "select * from fuelcalibrationdetails where id=(select max(id) FROM fuelcalibrationdetails);";
		return jdbcTemplate.query(sql, new FuelCalibrationDetailsRowMapper());
	}

	public void deleteCalibrated(long calids) throws OperationNotSupportedException {
		String sql = "delete from fuelcalibrationdetails where id ="+calids ;
		jdbcTemplate.execute(sql);
	}
}		

