package com.i10n.db.dao;

import java.sql.Types;
import java.util.List;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.incrementer.DataFieldMaxValueIncrementer;

import com.i10n.db.entity.MailingListAlert;
import com.i10n.db.entity.MobileNumber;
import com.i10n.db.entity.primarykey.LongPrimaryKey;
import com.i10n.db.entity.rowmapper.MobileNumberRowMapper;
import com.i10n.db.idao.IMobileNumberDAO;
import com.i10n.fleet.exceptions.OperationNotSupportedException;
import com.i10n.fleet.web.utils.SessionUtils;

@SuppressWarnings("unchecked")
public class MobileNumberDaoImp implements IMobileNumberDAO {
	private JdbcTemplate jdbcTemplate;

	private DataFieldMaxValueIncrementer MobileNumberIdIncrementer;

	public JdbcTemplate getJdbcTemplate() {
		return jdbcTemplate;
	}

	public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}

	public DataFieldMaxValueIncrementer getMobilenumberIdIncrementer() {
		return MobileNumberIdIncrementer;
	}

	public void setMobilenumberIdIncrementer(DataFieldMaxValueIncrementer MobileNumberIdIncrementer) {
		this.MobileNumberIdIncrementer = MobileNumberIdIncrementer;
	}

	public List<MailingListAlert> selectFromMobileNumber(Long userid) {
		String sql = "select * from mobilenumber where userid=?";
		Object[] args = new Object[] { userid };
		int[] types = new int[] { Types.BIGINT };
		return jdbcTemplate.query(sql, args, types, new MobileNumberRowMapper());
	}

	@Override
	public MobileNumber delete(MobileNumber entity)
	throws OperationNotSupportedException {
		String query = "delete from aclmobile where mobileid in (select id from mobilenumber where name = ? and userid = ? and mobilenumber = ?)";
		Object arguments[] = new Object[]{entity.getName(), entity.getUserid(), entity.getMobileNumber() };
		int type[] = new int[] { Types.VARCHAR, Types.BIGINT, Types.BIGINT };
		jdbcTemplate.update(query, arguments, type);
		entity.setId(getId(entity).getId());
		String sql = "delete from mobilenumber where name = ? and userid = ? and mobilenumber = ?";
		Object args[] = new Object[] { entity.getName(), entity.getUserid(), entity.getMobileNumber() };
		int types[] = new int[] { Types.VARCHAR, Types.BIGINT, Types.BIGINT };
		int rowsDeleted = jdbcTemplate.update(sql, args, types);
		if (rowsDeleted < 1) {
			entity = null;
		}
		return entity;
	}

	@Override
	public MobileNumber insert(MobileNumber entity)
	throws OperationNotSupportedException {
		Long id = MobileNumberIdIncrementer.nextLongValue();
		entity.setId(new LongPrimaryKey(id));
		String sql = "insert into mobilenumber(id,name,userid,mobilenumber,overspeeding,"
				+ "geofencing,chargerdisconnected) values (?,?,?,?,?,?,?)";
		Object[] args = new Object[] { id, entity.getName(), entity.getUserid(), entity.getMobileNumber(),
				entity.getOverSpeeding(), entity.getGeoFencing(), entity.getChargerDisConnected() };
		int[] types = new int[] { Types.BIGINT, Types.VARCHAR, Types.BIGINT, Types.BIGINT, Types.BOOLEAN, Types.BOOLEAN, Types.BOOLEAN };
		jdbcTemplate.update(sql, args, types);
		return entity;
	}

	@Override
	public MobileNumber update(MobileNumber entity)
	throws OperationNotSupportedException {
		String sql = "update mobilenumber set  overspeeding=?, geofencing=?, chargerdisconnected=? where name=? and userid=? and mobilenumber=?";
		Object args[] = new Object[] { entity.getOverSpeeding(), entity.getGeoFencing(), entity.getChargerDisConnected(),
				entity.getName(), entity.getUserid(), entity.getMobileNumber() };
		int types[] = new int[] { Types.BOOLEAN, Types.BOOLEAN, Types.BOOLEAN, Types.VARCHAR, Types.BIGINT, Types.BIGINT };
		int rowsUpdated = jdbcTemplate.update(sql, args, types);
		entity.setId(getId(entity).getId());
		if (rowsUpdated < 1) {
			entity = null;
		}
		return entity;
	}

	@Override
	public List<MobileNumber> selectAll() {
		String sql = "select * from mobilenumber order by id ";
		return jdbcTemplate.query(sql, new MobileNumberRowMapper());
	}

	@Override
	public List<MobileNumber> selectByPrimaryKey(LongPrimaryKey primaryKey) {
		String sql = "select * from mobilenumber where id = ?";
		Object[] args = new Object[]{primaryKey.getId()};
		int[] types = new int[]{Types.BIGINT};
		return jdbcTemplate.query(sql, args, types, new MobileNumberRowMapper());
	}

	public List<MobileNumber> selectByUserId(Long userid) {
		String sql = "select * from mobilenumber where userid=?";
		Object[] args = new Object[]{userid};
		int[] types = new int[]{Types.BIGINT};
		return jdbcTemplate.query(sql, args, types, new MobileNumberRowMapper());
	}

	public List<MobileNumber> selectByReportId(Long reportid) {
		String sql = "select * from mobilenumber where id=?";
		Object[] args = new Object[]{reportid};
		int[] types = new int[]{Types.BIGINT};
		return jdbcTemplate.query(sql, args, types, new MobileNumberRowMapper());
	}

	public List<MobileNumber> selectAllOwned() {
		String sql = "select * from mobilenumber where userid = "+SessionUtils.getCurrentlyLoggedInUser().getId();
		return jdbcTemplate.query(sql, new MobileNumberRowMapper());
	}
	public MobileNumber getId(MobileNumber entity){
		String sqlForId = "select * from mobilenumber where name = ? and userid = ? and mobilenumber = ?";
		Object argsForId[] = new Object[] { entity.getName(), entity.getUserid(), entity.getMobileNumber() };
		int typesForId[] = new int[] { Types.VARCHAR, Types.BIGINT, Types.BIGINT };
		List<MobileNumber> mobObject=jdbcTemplate.query(sqlForId, argsForId, typesForId, new MobileNumberRowMapper());
		entity.setId(new LongPrimaryKey(mobObject.get(0).getId().getId()));
		return entity;
	}

}
