package com.i10n.db.dao;

import java.sql.Types;
import java.util.Date;
import java.util.List;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.incrementer.DataFieldMaxValueIncrementer;

import com.i10n.db.entity.Logs;
import com.i10n.db.entity.primarykey.LongPrimaryKey;
import com.i10n.db.entity.rowmapper.LogsRowMapper;
import com.i10n.db.idao.ILogsDAO;
import com.i10n.fleet.exceptions.OperationNotSupportedException;

@SuppressWarnings("unchecked")
public class LogsDaoImp implements ILogsDAO {

	private JdbcTemplate jdbcTemplate;
	private DataFieldMaxValueIncrementer logsIdIncrementer;

	public JdbcTemplate getJdbcTemplate() {
		return jdbcTemplate;
	}

	public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}

	public DataFieldMaxValueIncrementer getLogsIdIncrementer() {
		return logsIdIncrementer;
	}

	public void setLogsIdIncrementer(DataFieldMaxValueIncrementer logsIdIncrementer) {
		this.logsIdIncrementer = logsIdIncrementer;
	}

	@Override
	public Logs delete(Logs logs) throws OperationNotSupportedException {
		return null;
	}

	@Override
	public Logs insert(Logs log) throws OperationNotSupportedException {
		Long id = logsIdIncrementer.nextLongValue();
		log.setId((new LongPrimaryKey(id)));
		java.sql.Timestamp sqlDate = new java.sql.Timestamp(new java.util.Date().getTime());
		log.setDate(sqlDate);
		String sql = "insert into logs(id,userid,date,ipaddress,olddata,newdata) values (?,?,?,?,?,?)";
		Object args[] = new Object[] { id,log.getUserid(), log.getDate(), log.getIpaddress(), log.getOlddata(), log.getNewdata() };
		int types[] = new int[] { Types.BIGINT,Types.BIGINT, Types.TIMESTAMP, Types.VARCHAR, Types.VARCHAR, Types.VARCHAR };
		jdbcTemplate.update(sql, args, types);
		return log;
	}

	@Override
	public Logs update(Logs log) throws OperationNotSupportedException {
		return null;
	}

	@Override
	public List<Logs> selectByPrimaryKey(LongPrimaryKey primaryKey) {
		String sql = "select * from logs where id=?";
		Object[] values = new Object[] { primaryKey.getId() };
		int[] types = new int[] { Types.BIGINT };
		List<Logs> log = jdbcTemplate.query(sql, values, types, new LogsRowMapper());
		return (log == null || log.size() == 0) ? null : log;
	}

	@Override
	public List<Logs> selectAll() {
		String sql = "select * from logs";
		return jdbcTemplate.query(sql, new LogsRowMapper());
	}

	public List<Logs> selectByUserId(Long uid) {
		String sql = "select * from logs where userid=?";
		Object[] args = new Object[] { uid };
		int[] types = new int[] { Types.BIGINT };
		return jdbcTemplate.query(sql, args, types, new LogsRowMapper());
	}

	public List<Logs> selectByIpaddress(Long uid, Date startdate, Date enddate, String ip) {
		String sql = "select * from logs where date>=? and date<=? and userid=? and ipaddress=?";
		Object[] args = new Object[] { startdate, enddate, uid, ip };
		int[] types = new int[] { Types.TIMESTAMP, Types.TIMESTAMP, Types.BIGINT, Types.VARCHAR };
		return jdbcTemplate.query(sql, args, types, new LogsRowMapper());
	}

	public List<Logs> selectLastLogin(Long userid){
		String sql="select * from logs where userid=? and newdata like('%LOGGED IN') order by date desc limit 2";
		Object[] args=new Object[]{userid};
		int[] types=new int[]{Types.BIGINT};
		return jdbcTemplate.query(sql, args, types, new LogsRowMapper());
	}

	public List<Logs> selectLogedinUsers(Long userid){
		String sql="select * from logs where userid=? and newdata like('%LOGGED IN') order by date desc";
		Object[] args=new Object[]{userid};
		int[] types=new int[]{Types.BIGINT};
		return jdbcTemplate.query(sql, args, types, new LogsRowMapper());
	}

}
