/**
 * 
 */
package com.i10n.db.dao;

import java.util.List;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.incrementer.DataFieldMaxValueIncrementer;

import com.i10n.db.entity.AccelerometerLatestPacketDetail;
import com.i10n.db.entity.primarykey.LongPrimaryKey;
import com.i10n.db.idao.IAccelerometerLatestPacketDetailDAO;
import com.i10n.fleet.exceptions.OperationNotSupportedException;
import com.i10n.module.command.SmartCardUpdateCommand;

/**
 * @author joshua
 *
 */
public class AccelerometerLatestPacketDetailDAOImpl implements IAccelerometerLatestPacketDetailDAO {

	private JdbcTemplate jdbcTemplate;

	private DataFieldMaxValueIncrementer aclatestPacketIdIncrementer;



	public JdbcTemplate getJdbcTemplate() {
		return jdbcTemplate;
	}

	public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}

	public DataFieldMaxValueIncrementer getAclatestPacketIdIncrementer() {
		return aclatestPacketIdIncrementer;
	}

	public void setAclatestPacketIdIncrementer(
			DataFieldMaxValueIncrementer aclatestPacketIdIncrementer) {
		this.aclatestPacketIdIncrementer = aclatestPacketIdIncrementer;
	}

	@Override
	public AccelerometerLatestPacketDetail delete(
			AccelerometerLatestPacketDetail entity)
	throws OperationNotSupportedException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public AccelerometerLatestPacketDetail insert(AccelerometerLatestPacketDetail entity)
	throws OperationNotSupportedException {
		String sql="INSERT INTO accelerometerlatestpacket(imei, gsmstrength, gpsstrength, sqd, sqg, batteryvoltage,lac, cid, cc, mrs, rs, speed, cumulativedistance, location,fuel, xminimum, yminimum, zminimum, xmaximun, ymaximum, zmaximum,"
			+"occuredat)"
			+"VALUES (?, ?, ?, ?, ?, ?,"
			+"?, ?, ?, ?, ?, ?, ?,"
			+ "GeometryFromText('POINT ("
			+ entity.getLat() + " "
			+ entity.getLng() + ")',-1) "

			+",?, ?, ?, ?, ?, ?, ?,"
			+"?)";
		Object args[] = new Object[] {entity.getImei(),entity.getGsmSignal(),entity.getGpsSignal(),entity.getSqd(),entity.getSqg(),entity.getBatteryVoltage(),entity.getLac(),entity.getCid(),entity.isChargerConnected(),entity.isMrs(),entity.isRestart(),entity.getSpeed(),entity.getCumulativeDistance(),entity.getFuel(),entity.getXmin(),entity.getYmin(),entity.getZmin(),entity.getXmax(),entity.getYmax(),entity.getZmax(),entity.getOccurredat()};
		jdbcTemplate.update(sql, args);

		// TODO Auto-generated method stub
		return entity;
	}

	@Override
	public List<AccelerometerLatestPacketDetail> selectAll() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<AccelerometerLatestPacketDetail> selectByPrimaryKey(
			LongPrimaryKey primaryKey) {
		// TODO Auto-generated method stub
		return null;
	}
	
	public SmartCardUpdateCommand  insertSmartcardDetails(SmartCardUpdateCommand smartcard){
		String Sql="INSERT INTO smartcard(imei, smartcardid, location, occurredat)VALUES (?, ?, " + "GeometryFromText('POINT ("
		+ smartcard.getM_longitude() + " "
		+ smartcard.getM_latitude() + ")',-1) "
		+	", ?);";
		Object args[] = new Object[] {smartcard.getM_imei(),smartcard.getM_smartcardid(),smartcard.getM_occurredat()};
		jdbcTemplate.update(Sql, args);
		jdbcTemplate.update(Sql, args);
		return smartcard;
	}
	
	@Override
	public AccelerometerLatestPacketDetail update(AccelerometerLatestPacketDetail entity)
	throws OperationNotSupportedException {
		// TODO Auto-generated method stub
		return null;
	}

}
