package com.i10n.db.entity.rowmapper;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

import com.i10n.db.entity.UserFeatureList;
import com.i10n.db.entity.primarykey.LongPrimaryKey;

public class UserFeatureListRowMapper implements RowMapper {

	@Override
	public Object mapRow(ResultSet rs, int arg1) throws SQLException {
		// TODO Auto-generated method stub
UserFeatureList user1= new UserFeatureList( new LongPrimaryKey(rs.getLong("userid")),
				rs.getLong("featureid"));
			
				return  user1;
		
	}

}
