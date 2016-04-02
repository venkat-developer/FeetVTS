package com.i10n.db.entity.rowmapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import org.springframework.jdbc.core.RowMapper;
import com.i10n.fleet.container.GWTrackModuleDataBean;

/**
 * @author joshua
 *
 */
public class GWTrackModuleDataBeanRowMapper implements RowMapper {

	@Override
	public Object mapRow(ResultSet rs, int arg1) throws SQLException {
		GWTrackModuleDataBean bean=new GWTrackModuleDataBean(rs.getDouble(1), rs.getDouble(2), rs.getDouble(3), rs.getLong(4), rs.getLong(5),
				rs.getDouble(6), rs.getLong(7), rs.getDouble(8), rs.getInt(9), rs.getInt(10),
				rs.getInt(11), rs.getInt(12),rs.getInt(13), rs.getBoolean(14), rs.getBoolean(15), rs.getBoolean(16), rs.getBoolean(17),
				rs.getBoolean(18), rs.getString(19), rs.getString(20), rs.getString(21), rs.getBoolean(22),rs.getInt(23),rs.getInt(25),
				rs.getInt(26),rs.getInt(27),rs.getString(28) ,rs.getDate(32));

		return bean;
	}

}
