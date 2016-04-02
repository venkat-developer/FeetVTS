package com.i10n.db.dao;

import java.sql.Types;
import java.util.List;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.incrementer.DataFieldMaxValueIncrementer;

import com.i10n.db.entity.MailingListAlert;
import com.i10n.db.entity.primarykey.LongPrimaryKey;
import com.i10n.db.entity.rowmapper.MailinglistAlertRowMapper;
import com.i10n.db.idao.IMailinglistAlertDAO;
import com.i10n.fleet.exceptions.OperationNotSupportedException;
import com.i10n.fleet.web.utils.SessionUtils;

@SuppressWarnings("unchecked")
public class MailinglistAlertDaoImp implements IMailinglistAlertDAO {
	private JdbcTemplate jdbcTemplate;

	private DataFieldMaxValueIncrementer MailinglistAlertIdIncrementer;

	public JdbcTemplate getJdbcTemplate() {
		return jdbcTemplate;
	}

	public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}

	public DataFieldMaxValueIncrementer getMailinglistalertsIdIncrementer() {
		return MailinglistAlertIdIncrementer;
	}

	public void setMailinglistalertsIdIncrementer(DataFieldMaxValueIncrementer MailinglistAlertIdIncrementer) {
		this.MailinglistAlertIdIncrementer = MailinglistAlertIdIncrementer;
	}

	@Override
	public MailingListAlert delete(MailingListAlert entity) throws OperationNotSupportedException {
		String query = "delete from aclalerts where alertuserid in (select id from mailinglist_alert where name = ? and userid = ? and mailid = ?)";
		Object arguments[] = new Object[]{entity.getName(), entity.getUserid(), entity.getMailId() };
		int type[] = new int[] { Types.VARCHAR, Types.BIGINT, Types.VARCHAR };
		jdbcTemplate.update(query, arguments, type);
		entity.setId(getId(entity).getId());
		String sql = "delete from mailinglist_alert where name = ? and userid = ? and mailid = ?";
		Object args[] = new Object[] { entity.getName(), entity.getUserid(),entity.getMailId() };
		int types[] = new int[] { Types.VARCHAR, Types.BIGINT, Types.VARCHAR };
		int rowsDeleted = jdbcTemplate.update(sql, args, types);
		if (rowsDeleted < 1) {
			entity = null;
		}
		return entity;
	}

	@Override
	public MailingListAlert insert(MailingListAlert entity) throws OperationNotSupportedException {
		Long id = MailinglistAlertIdIncrementer.nextLongValue();
		entity.setId(new LongPrimaryKey(id));
		String sql = "insert into mailinglist_alert(name,userid,mailid,overspeeding,"
			+ "geofencing,chargerdisconnected,ignition) values (?,?,?,?,?,?,?)";
		Object[] args = new Object[] { entity.getName(), entity.getUserid(), entity.getMailId(),
				entity.getOverSpeeding(), entity.getGeoFencing(), entity.getChargerDisConnected(),entity.isIgnition() };
		int[] types = new int[] { Types.VARCHAR, Types.BIGINT, Types.VARCHAR, Types.BOOLEAN, Types.BOOLEAN, Types.BOOLEAN ,Types.BOOLEAN};
		jdbcTemplate.update(sql, args, types);
		return entity;
	}

	@Override
	public List<MailingListAlert> selectAll() {
		String sql = "select * from mailinglist_alert order by id";
		return jdbcTemplate.query(sql, new MailinglistAlertRowMapper());
	}

	@Override
	public List<MailingListAlert> selectByPrimaryKey(LongPrimaryKey primaryKey) {
		String sql = "select * from mailinglist_alert where id = "+primaryKey.getId();
		return jdbcTemplate.query(sql, new MailinglistAlertRowMapper());
	}

	@Override
	public MailingListAlert update(MailingListAlert entity)
	throws OperationNotSupportedException {
		String sql = "update mailinglist_alert set  overspeeding=?, geofencing=?, chargerdisconnected=?,ignition=? where name=? and userid=? and mailid=?";
		Object args[] = new Object[] { entity.getOverSpeeding(),
				entity.getGeoFencing(), entity.getChargerDisConnected(),entity.isIgnition(),
				entity.getName(), entity.getUserid(), entity.getMailId() };
		int types[] = new int[] { Types.BOOLEAN, Types.BOOLEAN, Types.BOOLEAN, Types.BOOLEAN,
				Types.VARCHAR, Types.BIGINT, Types.VARCHAR };
		int rowsUpdated = jdbcTemplate.update(sql, args, types);
		entity.setId(getId(entity).getId());
		if (rowsUpdated < 1) {
			entity = null;
		}
		return entity;
	}

	public List<MailingListAlert> selectFromMailingListAlert(Long reportid) {
		String sql = "select * from mailinglist_alert where id=?";
		Object[] args = new Object[] { reportid };
		int[] types = new int[] { Types.BIGINT };
		return jdbcTemplate.query(sql, args, types,
				new MailinglistAlertRowMapper());
	}

	public List<MailingListAlert> selectAllOwned() {
		String sql = "select * from mailinglist_alert where userid="+SessionUtils.getCurrentlyLoggedInUser().getId();
		return jdbcTemplate.query(sql, new MailinglistAlertRowMapper());
	}
	public MailingListAlert getId(MailingListAlert entity){
		String sqlForId = "select * from mailinglist_alert where name = ? and userid = ? and mailid = ?";
		Object argsForId[] = new Object[] { entity.getName(), entity.getUserid(), entity.getMailId() };
		int typesForId[] = new int[] { Types.VARCHAR, Types.BIGINT, Types.VARCHAR };
		List<MailingListAlert> mailObject=jdbcTemplate.query(sqlForId, argsForId, typesForId, new MailinglistAlertRowMapper());
		entity.setId(new LongPrimaryKey(mailObject.get(0).getId().getId()));
		return entity;
	}

}
