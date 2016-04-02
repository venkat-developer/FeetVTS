package com.i10n.db.dao;

import java.util.List;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.incrementer.DataFieldMaxValueIncrementer;

import com.i10n.db.entity.GWTerminalLatestPacketDetails;
import com.i10n.db.entity.primarykey.LongPrimaryKey;
import com.i10n.db.idao.IGWTerminalLatestPacketDetailsDAO;
import com.i10n.fleet.exceptions.OperationNotSupportedException;

/**
 * @author joshua
 *
 */
public class GWLatestPacketDetailDaoImpl implements IGWTerminalLatestPacketDetailsDAO {

	private JdbcTemplate jdbcTemplate;
	private DataFieldMaxValueIncrementer GTlatestPacketIdIncrementer;



	public DataFieldMaxValueIncrementer getGTlatestPacketIdIncrementer() {
		return GTlatestPacketIdIncrementer;
	}

	public void setGTlatestPacketIdIncrementer(
			DataFieldMaxValueIncrementer gTlatestPacketIdIncrementer) {
		GTlatestPacketIdIncrementer = gTlatestPacketIdIncrementer;
	}

	public JdbcTemplate getJdbcTemplate() {
		return jdbcTemplate;
	}

	public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}

	@Override
	public GWTerminalLatestPacketDetails delete(GWTerminalLatestPacketDetails entity)
	throws OperationNotSupportedException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public GWTerminalLatestPacketDetails insert(GWTerminalLatestPacketDetails entity)
	throws OperationNotSupportedException {

		String sql="INSERT INTO gwterminallatestpacket(imei, gsmsignal,lac, cellid, batteryvoltage,mrs, sqd, sqg, rs, cc, analog1, analog2, analog3, analog4, analog5,isidle, occuredat)VALUES (?, ?,?, ?, ?,?, ?, ?, ?, ?, ?, ?, ?, ?, ?,?, ?)";
		Object args[] = new Object[] {entity.getImei(),entity.getGsmStrength(),entity.getLac(),entity.getCellId(),entity.getBatteryVoltage(),entity.isMrs(),entity.getSqd(),entity.getSqg(),entity.isRs(),entity.isChargerConnected(),entity.getAnalogue1(),entity.getAnalogue2(),entity.getAnalogue3(),entity.getAnalogue4(),entity.getAnalogue5(),entity.isIdle(),entity.getOccuredat()};
		jdbcTemplate.update(sql, args);
		// TODO Auto-generated method stub
		return entity;
	}

	@Override
	public List<GWTerminalLatestPacketDetails> selectAll() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<GWTerminalLatestPacketDetails> selectByPrimaryKey(
			LongPrimaryKey primaryKey) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public GWTerminalLatestPacketDetails update(GWTerminalLatestPacketDetails entity)
	throws OperationNotSupportedException {
		// TODO Auto-generated method stub
		return null;
	}

}
