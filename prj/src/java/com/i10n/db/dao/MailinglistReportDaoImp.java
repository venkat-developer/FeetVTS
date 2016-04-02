package com.i10n.db.dao;

import java.sql.Types;
import java.util.List;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.incrementer.DataFieldMaxValueIncrementer;

import com.i10n.db.entity.MailingListReport;
import com.i10n.db.entity.primarykey.LongPrimaryKey;
import com.i10n.db.entity.rowmapper.MailinglistReportRowMapper;
import com.i10n.db.idao.IMailinglistReportDAO;
import com.i10n.fleet.exceptions.OperationNotSupportedException;
import com.i10n.fleet.web.utils.SessionUtils;

@SuppressWarnings("unchecked")
public class MailinglistReportDaoImp implements IMailinglistReportDAO {

	private JdbcTemplate jdbcTemplate;

	private DataFieldMaxValueIncrementer MailinglistReportIdIncrementer;

	public JdbcTemplate getJdbcTemplate() {
		return jdbcTemplate;
	}

	public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}

	public DataFieldMaxValueIncrementer getMailinglistreportsIdIncrementer() {
		return MailinglistReportIdIncrementer;
	}

	public void setMailinglistreportsIdIncrementer(DataFieldMaxValueIncrementer MailinglistReportIdIncrementer) {
		this.MailinglistReportIdIncrementer = MailinglistReportIdIncrementer;
	}

	@Override
	public MailingListReport delete(MailingListReport entity)
	throws OperationNotSupportedException {
		String query = "delete from aclreports where reportid in (select id from mailinglist_report where name = ? and userid = ? and mailid = ?)";
		Object arguments[] = new Object[]{ entity.getName(), entity.getUserId(), entity.getMailId()};
		int type[] = new int[] {Types.VARCHAR, Types.BIGINT, Types.VARCHAR};
		jdbcTemplate.update(query, arguments, type);

		String sql = "delete from mailinglist_report where name = ? and userid = ? and mailid = ?";
		Object args []= new Object[] {entity.getName(), entity.getUserId(), entity.getMailId()};
		int types[] = new int[] {Types.VARCHAR, Types.BIGINT, Types.VARCHAR};
		int rowsDeleted = jdbcTemplate.update(sql, args, types);
		if(rowsDeleted < 1){
			entity = null;
		}
		return entity;
	}

	@Override
	public MailingListReport insert(MailingListReport entity)
	throws OperationNotSupportedException {
		Long id = MailinglistReportIdIncrementer.nextLongValue();
		entity.setId(new LongPrimaryKey(id));
		String sql = "insert into mailinglist_report(id,name,userid,mailid,schedule,lastscheduledat,vehiclestatistics,"
			+ "vehiclestatus,offlinevehiclereport) values (?,?,?,?,?,?,?,?,?)";
		Object[] args = new Object[] { id, entity.getName(), entity.getUserId(), entity.getMailId(), entity.getSchedule(),
				entity.getLastScheduledAt(), entity.getVehicleStatistics(), entity.getVehicleStatus(), entity.getOfflineVehicleReport() };
		int[] types = new int[] { Types.BIGINT, Types.VARCHAR, Types.BIGINT, Types.VARCHAR, Types.INTEGER, Types.TIMESTAMP, Types.BOOLEAN,
				Types.BOOLEAN, Types.BOOLEAN };
		jdbcTemplate.update(sql, args, types);
		return entity;
	}

	@Override
	public List<MailingListReport> selectAll() {
		String sql = "select * from mailinglist_report";
		return jdbcTemplate.query(sql, new MailinglistReportRowMapper());
	}

	public List<MailingListReport> selectAllOwned(){
		String sql = "select * from mailinglist_report where userid ="+SessionUtils.getCurrentlyLoggedInUser().getId();
		return jdbcTemplate.query(sql, new MailinglistReportRowMapper()); 
	}

	@Override
	public List<MailingListReport> selectByPrimaryKey(LongPrimaryKey primaryKey) {
		String sql = "select * from mailinglist_report where id="+primaryKey.getId();
		return jdbcTemplate.query(sql, new MailinglistReportRowMapper());
	}

	@Override
	public MailingListReport update(MailingListReport entity)
	throws OperationNotSupportedException {
		String sql = "update mailinglist_report set schedule=?, lastscheduledat=?, vehiclestatistics=?, vehiclestatus=?, offlinevehiclereport=? where name=? and userid=? and mailid=?";
		Object args[] = new Object[] { entity.getSchedule(), entity.getLastScheduledAt(), entity.getVehicleStatistics(),
				entity.getVehicleStatus(), entity.getOfflineVehicleReport(), entity.getName(), entity.getUserId(), entity.getMailId() };
		int types[] = new int[] { Types.INTEGER, Types.TIMESTAMP, Types.BOOLEAN, Types.BOOLEAN, Types.BOOLEAN, Types.VARCHAR, Types.BIGINT, Types.VARCHAR };
		int rowsUpdated = jdbcTemplate.update(sql, args, types);
		if (rowsUpdated < 1) {
			entity = null;
		}
		return entity;
	}

}
