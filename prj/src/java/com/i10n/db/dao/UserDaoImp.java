package com.i10n.db.dao;

import java.sql.Types;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.incrementer.DataFieldMaxValueIncrementer;

import com.i10n.db.entity.ACLVehicle;
import com.i10n.db.entity.User;
import com.i10n.db.entity.primarykey.LongPrimaryKey;
import com.i10n.db.entity.rowmapper.ACLVehicleRowMapper;
import com.i10n.db.entity.rowmapper.UserRowMapper;
import com.i10n.db.idao.IUserDAO;
import com.i10n.fleet.exceptions.OperationNotSupportedException;
import com.i10n.fleet.util.DateUtils;
import com.i10n.fleet.util.StringUtils;
import com.i10n.fleet.web.utils.SessionUtils;

@SuppressWarnings("unchecked")
public class UserDaoImp implements IUserDAO {
	
	private static final Logger LOG = Logger.getLogger(UserDaoImp.class);

	private JdbcTemplate jdbcTemplate;
	private DataFieldMaxValueIncrementer userIdIncrementer;

	public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
		LOG.debug("");
		this.jdbcTemplate = jdbcTemplate;
	}

	public void setUserIdIncrementer(DataFieldMaxValueIncrementer bookIncrementer) {
		this.userIdIncrementer = bookIncrementer;
	}

	@Override
	public User insert(User user) throws OperationNotSupportedException {
		// get next autoincrement value
		Long id = userIdIncrementer.nextLongValue();
		String md5password = StringUtils.md5(user.getPassword());
		user.setId(new LongPrimaryKey(id));
		String sql = "insert into users (id, username, pass, role, userstatus, owner_id,firstname,lastname, groupid) values (?,?,?,?,?,?,?,?,?)";
		Object args[] = new Object[] { id, user.getLogin(), md5password,user.getRole().getValue(), user.getUserStatus().getValue(),
				user.getOwnerId(), user.getFirstname(), user.getLastname(), user.getGroupId() };
		int types[] = new int[] { Types.BIGINT, Types.VARCHAR, Types.VARCHAR,Types.INTEGER, Types.INTEGER, Types.BIGINT, Types.VARCHAR,
				Types.VARCHAR, Types.BIGINT };
		int rowsAffected = jdbcTemplate.update(sql, args, types);
		LOG.debug("Rows Affected : "+rowsAffected);
		if(rowsAffected != 1){
			user = null;
		}
		jdbcTemplate.execute("NOTIFY users");
		return user;
	}

	@Override
	public User delete(User user) throws OperationNotSupportedException {
		Date endDate = Calendar.getInstance().getTime();

		String dquery = "update trips set active = false , enddate ='"+DateUtils.convertJavaDateToSQLDate(endDate)+"'  where driverid in "+
		"((select driverid from acldriver where userid = "+user.getId()+") INTERSECT (select id from drivers where ownerid = "
		+SessionUtils.getCurrentlyLoggedInUser().getId()+")) and enddate is null";
		jdbcTemplate.update(dquery);

		String vquery = "update trips set active = false , enddate ='"+DateUtils.convertJavaDateToSQLDate(endDate)+"'  where vehicleid in "+
		"((select vehicleid from aclvehicle where userid = "+user.getId()+") INTERSECT (select id from vehicles where ownerid = "
		+SessionUtils.getCurrentlyLoggedInUser().getId()+")) and enddate is null";
		jdbcTemplate.update(vquery);
		
		String lquery = "delete from logs where userid =?";
		Object largs[] = new Object[]{user.getId() };
		int ltypes[] = new int[] {Types.BIGINT};
		jdbcTemplate.update(lquery, largs, ltypes);

		String query = "delete from aclvehicle where userid=?";
		Object vargs[] = new Object[] { user.getId() };
		int vtypes[] = new int[] { Types.BIGINT };
		jdbcTemplate.update(query, vargs, vtypes);
		
		query = " delete from acldriver where userid = ?";
		Object dargs[] = new Object[] { user.getId() };
		int dtypes[] = new int[] { Types.BIGINT };
		jdbcTemplate.update(query, dargs, dtypes);
		
		String sql = "delete from users where id = ?";
		Object args[] = new Object[] { user.getId() };
		int types[] = new int[] { Types.BIGINT };

		int rowsAffected = jdbcTemplate.update(sql, args, types);
		LOG.debug("Rows Affected : "+rowsAffected);
		if (rowsAffected != 1) {
			user = null;
		}
		jdbcTemplate.execute("NOTIFY update");
		return user;
	}

	@Override
	public User update(User user) throws OperationNotSupportedException {
		String sql = "update users set firstname =?,lastname=?,pass = ?, offroadcount = ?, nogprscount = ? where id = ?";
		Object args[] = new Object[] { user.getFirstname(), user.getLastname(), StringUtils.md5(user.getPassword()), 
				user.getOffroadCount(), user.getNoGPRSCount(), user.getId() };
		int types[] = new int[] { Types.VARCHAR, Types.VARCHAR, Types.VARCHAR, Types.INTEGER, Types.INTEGER, Types.BIGINT };

		int rowsAffected = jdbcTemplate.update(sql, args, types);
		LOG.debug("Rows affected : "+rowsAffected);
		if (rowsAffected != 1) {
			user = null;
		}
		jdbcTemplate.execute("NOTIFY update");
		return user;
	}

	@Override
	public List<User> selectAll() {
		LOG.debug("In UserdaoImp for selectAll()");
		String sql = "select * from users";
		return jdbcTemplate.query(sql, new UserRowMapper());
	}

	@Override
	public boolean authenticateUser(String userName, String password) {
		boolean authenticationSuccess = false;
		String sql = "select * from users where username = ? and pass = ?";
		Object args[] = new Object[] { userName, StringUtils.md5(password) };
		int types[] = new int[] { Types.VARCHAR, Types.VARCHAR };
		List<User> result = jdbcTemplate.query(sql, args, types, new UserRowMapper());
		if (null != result && result.size() != 0) {
			authenticationSuccess = true;
		}
		return authenticationSuccess;
	}

	@Override
	public List<User> selectByPrimaryKey(LongPrimaryKey primaryKey) {
		String sql = "select * from users where id = " + primaryKey.getId();
		return jdbcTemplate.query(sql, new UserRowMapper());
	}

	@Override
	public User getUser(String username) {
		User user = null;
		String sql = "select * from users where username =  ? ";
		Object args[] = new Object[] { username };
		int types[] = new int[] { Types.VARCHAR };
		List<User> result = jdbcTemplate.query(sql, args, types,
				new UserRowMapper());
		if (result != null && result.size() == 1) {
			user = result.get(0);
		}
		return user;
	}

	public User getUserList(Long userid) {
		User user = null;
		String sql = "select * from users where id =  ? ";
		Object args[] = new Object[] { userid };
		int types[] = new int[] { Types.BIGINT };
		List<User> result = jdbcTemplate.query(sql, args, types,
				new UserRowMapper());
		if (result != null && result.size() == 1) {
			user = result.get(0);
		}
		return user;
	}

	@Override
	public List<User> getUsersForOwner(Long ownerId) {
		List<User> user = null;
		String sql = "select * from users where owner_id = ?";
		Object args[] = new Object[] { ownerId };
		int types[] = new int[] { Types.BIGINT };
		user = jdbcTemplate.query(sql, args, types, new UserRowMapper());
		return user;
	}
	
	/**
	 * List of users to whom the vehicles are assigned
	 * @param vehicleId
	 * @return
	 */
	public List<User> getUsersToWhichTheVehicleIsAssigned(long vehicleId) {
		
		StringBuffer sqlQueryForUserList = new StringBuffer();
		sqlQueryForUserList.append("Select * from users where id in (Select userid from aclvehicle where vehicleid = ");
		sqlQueryForUserList.append(vehicleId);
		sqlQueryForUserList.append(")");
		LOG.debug("Query framed "+sqlQueryForUserList.toString());
		List<User> userList = jdbcTemplate.query(sqlQueryForUserList.toString(), new UserRowMapper());
		return userList;
	}
	
	public List<ACLVehicle> getUserListForVehcileId(Long vehicleid){
		List<ACLVehicle> user = null;
		String sql = "select * from aclvehicle where vehicleid = ?";
		Object args []= new Object[] {vehicleid};
		int types[] = new int[] {Types.BIGINT};
		user = jdbcTemplate.query(sql, args, types, new ACLVehicleRowMapper());
		return user;
	}

	public List<User> selectAllUsers(Long groupId,Long userId) {
		String sql = "select * from users where owner_id =? and groupid=?";
		Object args []= new Object[] {userId,groupId};
		int types[] = new int[] {Types.BIGINT,Types.BIGINT};
		return jdbcTemplate.query(sql, args, types, new UserRowMapper());
	}

}
