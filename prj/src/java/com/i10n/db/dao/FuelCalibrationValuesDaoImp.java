package com.i10n.db.dao;

import java.sql.Types;
import java.util.List;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.incrementer.DataFieldMaxValueIncrementer;

import com.i10n.db.entity.FuelCalibrationValues;
import com.i10n.db.entity.primarykey.LongPrimaryKey;
import com.i10n.db.entity.rowmapper.FuelCalibrationValuesRowMapper;
import com.i10n.db.idao.IFuelCalibrationValuesDAO;
import com.i10n.fleet.exceptions.OperationNotSupportedException;

@SuppressWarnings("unchecked")
public class FuelCalibrationValuesDaoImp implements IFuelCalibrationValuesDAO {
	
	private JdbcTemplate jdbcTemplate;
	private DataFieldMaxValueIncrementer fuelcalibrationvaluesIdIncrementer;

	public JdbcTemplate getJdbcTemplate() {
		return jdbcTemplate;
	}

	public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}

	public DataFieldMaxValueIncrementer getFuelcalibrationvaluesIdIncrementer() {
		return fuelcalibrationvaluesIdIncrementer;
	}

	public void setFuelcalibrationvaluesIdIncrementer(DataFieldMaxValueIncrementer fuelcalibrationvaluesIdIncrementer) {
		this.fuelcalibrationvaluesIdIncrementer = fuelcalibrationvaluesIdIncrementer;
	}

	@Override
	public FuelCalibrationValues delete(FuelCalibrationValues fuel) throws OperationNotSupportedException {
		return null;
	}

	@Override
	public FuelCalibrationValues insert(FuelCalibrationValues fuel) throws OperationNotSupportedException {
		String sql = "insert into fuelcalibrationvalues(calibrationid,advalue,fuelinliters,tripid,gradient,base_ad,base_fuel) values (?,?,?,?,?,?,?)";
		Object args[] = new Object[] { fuel.getCalibrationid().getId(),fuel.getAdvalue(), fuel.getFuelinliters(), fuel.getTripId(),
				fuel.getGradient(), fuel.getBaseAd(), fuel.getBaseFuel() };
		int types[] = new int[] { Types.INTEGER, Types.INTEGER, Types.INTEGER , Types.BIGINT , Types.DOUBLE , Types.INTEGER , Types.INTEGER };
		jdbcTemplate.update(sql, args, types);
		return fuel;
	}

	@Override
	public FuelCalibrationValues update(FuelCalibrationValues fuel) throws OperationNotSupportedException {
		return null;
	}

	@Override
	public List<FuelCalibrationValues> selectByPrimaryKey(LongPrimaryKey primaryKey) {
		String sql = "select a.calibrationid as calibrationid,a.advalue as advalue,a.fuelinliters as fuelinliters from fuelcalibrationvalues a, "
				+ "fuelcalibrationdetails b where a.calibrationid = ? and a.calibrationid = b.id";
		Object[] values = new Object[] { primaryKey.getId() };
		int[] types = new int[] { Types.BIGINT };
		List<FuelCalibrationValues> fuel = jdbcTemplate.query(sql, values, types, new FuelCalibrationValuesRowMapper());
		return (fuel == null || fuel.size() == 0) ? null : fuel;
	}

	@Override
	public List<FuelCalibrationValues> selectAll() {
		String sql = "select * from fuelcalibrationvalues";
		return jdbcTemplate.query(sql, new FuelCalibrationValuesRowMapper());
	}

	public long getCalId(long id) {
		long result;
		String sql = "select distinct(calibrationid) from fuelcalibrationvalues where tripid=?";
		Object[] args = new Object[]{id};
		int[] types = new int[]{Types.BIGINT};
		result=jdbcTemplate.queryForLong(sql, args, types);
		return result;
	}
	
	public void deleteCalibrated(long calids) throws OperationNotSupportedException {
		String sql = "delete from fuelcalibrationvalues where calibrationid ="+calids ;
		jdbcTemplate.execute(sql);
	}
	
	public long selectMinAdValueBasedOnTripId(long tripid, int ad){
		String sql = "select min(advalue) from fuelcalibrationvalues where tripid=? and advalue>=?";
		Object[] args = new Object[]{tripid, ad};
		int[] types = new int[]{Types.BIGINT, Types.INTEGER};
		return jdbcTemplate.queryForLong(sql, args, types);
	}

	public List<FuelCalibrationValues> selectByMinAdValue(long tripid, int ad){
		String sql = "select * from fuelcalibrationvalues where tripid=? and advalue=?";
		Object[] args = new Object[]{tripid, ad};
		int[] types = new int[]{Types.BIGINT, Types.INTEGER};
		return jdbcTemplate.query(sql, args, types, new FuelCalibrationValuesRowMapper());
	}
}
