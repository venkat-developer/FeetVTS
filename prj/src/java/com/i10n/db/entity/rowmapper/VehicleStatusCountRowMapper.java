package com.i10n.db.entity.rowmapper;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

import com.i10n.db.entity.VehicleStatusCount;

public class VehicleStatusCountRowMapper implements RowMapper {

	@Override
	public Object mapRow(ResultSet rs, int arg1) throws SQLException {
		VehicleStatusCount statusCount = new VehicleStatusCount();
		statusCount.setLastupdatedatdiff(rs.getBoolean("lastupdatedatdiff"));
		statusCount.setModuleupdatetimediff(rs.getBoolean("moduleupdatetimediff"));
		return statusCount;
	}

}
