/**
 * 
 */
package com.i10n.db.entity.rowmapper;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

import com.i10n.db.entity.Driver;

/**
 * @author joshua
 *
 */
public class AssignDriverMapper implements RowMapper{

	@Override
	public Object mapRow(ResultSet rs, int arg1) throws SQLException {
		// TODO Auto-generated method stub
		Driver newDriver = new Driver(rs.getLong("id"),
				null,
				null,
				null,
				null,
				null,
				false);
		return newDriver;
	
	}

}
