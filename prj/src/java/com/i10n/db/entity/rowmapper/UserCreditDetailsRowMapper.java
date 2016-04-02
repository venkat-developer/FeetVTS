package com.i10n.db.entity.rowmapper;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

import com.i10n.db.entity.UserCreditDetails;
import com.i10n.db.entity.primarykey.LongPrimaryKey;
public class UserCreditDetailsRowMapper implements RowMapper {

	@Override
	public Object mapRow(ResultSet rs, int arg1) throws SQLException {
		UserCreditDetails newuser = new UserCreditDetails(new LongPrimaryKey(rs.getLong("userid")),
				rs.getInt("totalcreditpurchased"),
				rs.getInt("creditused"),
				rs.getString("lastcreditpurchasedate"),
				rs.getLong("lastupdatedbyemp"));
		return newuser;
	}

}

