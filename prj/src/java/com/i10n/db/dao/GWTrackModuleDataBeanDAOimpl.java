/**
 * 
 */
package com.i10n.db.dao;

import java.sql.Types;
import java.util.List;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.incrementer.DataFieldMaxValueIncrementer;

import com.i10n.db.entity.primarykey.LongPrimaryKey;
import com.i10n.db.entity.rowmapper.GWTrackModuleDataBeanRowMapper;
import com.i10n.db.idao.IGWTrackModuleDataBeanDAO;
import com.i10n.fleet.container.GWTrackModuleDataBean;
import com.i10n.fleet.exceptions.OperationNotSupportedException;

/**
 * @author joshua
 *
 */
@SuppressWarnings("unchecked")
public class GWTrackModuleDataBeanDAOimpl implements IGWTrackModuleDataBeanDAO {
	private JdbcTemplate jdbcTemplate;
	private DataFieldMaxValueIncrementer GWTrackModuledatabeanIdIncrementer;
	
	
	
	public JdbcTemplate getJdbcTemplate() {
		return jdbcTemplate;
	}

	public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}

	public DataFieldMaxValueIncrementer getGWTrackModuledatabeanIdIncrementer() {
		return GWTrackModuledatabeanIdIncrementer;
	}

	public void setGWTrackModuledatabeanIdIncrementer(DataFieldMaxValueIncrementer gWTrackModuledatabeanIdIncrementer) {
		GWTrackModuledatabeanIdIncrementer = gWTrackModuledatabeanIdIncrementer;
	}

	@Override
	public GWTrackModuleDataBean delete(GWTrackModuleDataBean bean) throws OperationNotSupportedException {
		String sql="delete from gwtrackmoduledata where imei="+bean.getImei();
		jdbcTemplate.update(sql);
		return bean;
	}

	@Override
	public GWTrackModuleDataBean insert(GWTrackModuleDataBean bean) throws OperationNotSupportedException {
		String sql = "INSERT INTO gwtrackmoduledata(gps_signal_strength,vehicle_course ,gsm_signal_strength, sqd, sqg, battery_voltage,cumulative_distance,"
			+ " speed, analogue2, analogue3, lac, cellid, pingflag,charger_connected,master_restart, module_restarted,moretofollow,"
			+ "digitalinput1, digitalinput2, digitalinput3,"
			+ "panicdata,firmwareversion,moduleversion,year,month,day,imei,moduleupdatetime)"
			+ " VALUES (?, ?, ?, ?, ?, ?, ?, ?,"
			+ "?, ?, ?, ?, ?, ?, ?, ?, ?, "
			+ "?, ?, ?, ?, ?, "
			+ "?, ?,?,?,? "
			+ " , ?)";
		
		Object arg[]=new Object[]{bean.getGpsSignalStrength(),bean.getVehicleCourse(),bean.getGsmSignalStrength(),bean.getNumberOfSuccessPackets(),bean.getNumberOfPacketSendingAttempts(),
				bean.getModuleBatteryVoltage(),bean.getCumulativeDistance(),bean.getMaxSpeed(),bean.getAnalogue1(),bean.getAnalogue2(),bean.getLocationAreaCode(),bean.getCellId(),bean.isPingFlag(),bean.isChargerConnected(),bean.isMasterHardwareLevelRestart()
				,bean.isModuleCodeLevelRestart(),bean.isMoreToFollow(),bean.getDigitalInput1(),bean.getDigitalInput2(),bean.getDigitalInput3(),bean.isPanicData()
				,bean.getFirmwareVersion(),bean.getModuleVersion(),bean.getYear(),bean.getMonth(),bean.getDay(),bean.getImei(),bean.getModuleUpdateTime()};
		jdbcTemplate.update(sql, arg);
		return bean;
	}

	@Override
	public List<GWTrackModuleDataBean> selectAll() {
		String Sql="select * from gwtrackmoduledata ";
		return jdbcTemplate.query(Sql, new GWTrackModuleDataBeanRowMapper());
	}
	
	public List<GWTrackModuleDataBean> selectbyImei(String imei) {
		String sql="select * from gwtrackmoduledata where imei=?";
		Object args[] = new Object[] {imei};
		int types[] = new int[] {Types.VARCHAR};
		return jdbcTemplate.query(sql,args,types,new GWTrackModuleDataBeanRowMapper());
	}

	@Override
	public List<GWTrackModuleDataBean> selectByPrimaryKey(LongPrimaryKey primaryKey) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public GWTrackModuleDataBean update(GWTrackModuleDataBean entity) throws OperationNotSupportedException {
		// TODO Auto-generated method stub
		return null;
	}

}