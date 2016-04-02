package com.i10n.db.entity.rowmapper;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

import com.i10n.db.entity.ACLAlerts;

public class ACLAlertsRowMapper implements RowMapper{
		@Override
	public Object mapRow(ResultSet rs, int arg1) throws SQLException {
		ACLAlerts aclAlert=new ACLAlerts(rs.getLong("vehicleid"), rs.getLong("alertuserid"));
		return aclAlert;
	}

}
