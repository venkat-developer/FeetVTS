package com.i10n.db.dao;

import java.sql.Types;
import java.util.List;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.incrementer.DataFieldMaxValueIncrementer;

import com.i10n.db.entity.HardwareModuleHistory;
import com.i10n.db.entity.primarykey.LongPrimaryKey;
import com.i10n.db.entity.rowmapper.HardwareModuleHistoryRowMapper;
import com.i10n.db.idao.IHardwareModuleHistoryDAO;
import com.i10n.fleet.exceptions.OperationNotSupportedException;

@SuppressWarnings("unchecked")
public class HardwareModuleHistoryDaoImp implements IHardwareModuleHistoryDAO{
	
	private JdbcTemplate jdbcTemplate;
	private DataFieldMaxValueIncrementer hardwaremodulehistoryIdIncrementer;

	public JdbcTemplate getJdbcTemplate() {
		return jdbcTemplate;
	}

	public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}

	public DataFieldMaxValueIncrementer getHardwaremodulehistoryIdIncrementer() {
		return hardwaremodulehistoryIdIncrementer;
	}

	public void setHardwaremodulehistoryIdIncrementer(DataFieldMaxValueIncrementer hardwaremodulehistoryIdIncrementer) {
		this.hardwaremodulehistoryIdIncrementer = hardwaremodulehistoryIdIncrementer;
	}

	@Override
	public HardwareModuleHistory delete(HardwareModuleHistory hardware) throws OperationNotSupportedException {
		return null;
	}

	@Override
	public HardwareModuleHistory insert(HardwareModuleHistory hardware) throws OperationNotSupportedException {
		Long id = hardwaremodulehistoryIdIncrementer.nextLongValue();
		hardware.setHardwareid(new LongPrimaryKey(id));
		java.sql.Timestamp sqlDate = new java.sql.Timestamp(new java.util.Date().getTime());
		hardware.setUpdatedat(sqlDate); 
		String sql = "insert into hardwaremodulehistory(hardwareid,updatedat,modulestatus,updatedbyemp) values (?,?,?,?)";
		Object args []= new Object[] {hardware.getHardwareid().getId(),hardware.getUpdatedat(),hardware.getModulestatus(),hardware.getUpdatedbyemp()};
		int types[] = new int[] {Types.BIGINT, Types.TIMESTAMP,Types.INTEGER,Types.BIGINT};
		jdbcTemplate.update(sql, args, types);
		return hardware;
	}

	@Override
	public HardwareModuleHistory update(HardwareModuleHistory hardware) throws OperationNotSupportedException {
		return null;
	}

	@Override
	public List<HardwareModuleHistory> selectAll() {
		String sql = "select * from hardwaremodulehistory";
		return jdbcTemplate.query(sql, new HardwareModuleHistoryRowMapper());
	}

	@Override
	public List<HardwareModuleHistory> selectByPrimaryKey(LongPrimaryKey primaryKey) {
		String sql = "select * from hardwaremodulehistory a, " +
		"hardwaremodules b,employees c where a.hardwareid = ? and a.hardwareid=b.id and a.updatedbyemp = c.id ";
		Object[] values = new Object[]{primaryKey.getId()};
		int[] types = new int[]{Types.BIGINT};
		List<HardwareModuleHistory> hardware = jdbcTemplate.query(sql, values, types, new HardwareModuleHistoryRowMapper());
		return ( hardware== null || hardware.size() == 0)?null:hardware;
	}

}		

