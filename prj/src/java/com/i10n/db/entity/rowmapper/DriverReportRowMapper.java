/**
 * 
 */
package com.i10n.db.entity.rowmapper;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

import com.i10n.db.entity.DriverReport;

/**
 * @author joshua
 *
 */
public class DriverReportRowMapper implements RowMapper{

	@Override
	public Object mapRow(ResultSet rs, int arg1) throws SQLException {
		DriverReport dreport=new DriverReport(
				rs.getDouble("max"),
				rs.getDouble("avg"),
				rs.getDouble("sum")
		
		
		);
		// TODO Auto-generated method stub
		return dreport;
	}

}
