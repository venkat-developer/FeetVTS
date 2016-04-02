package com.i10n.db.entity.rowmapper;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

import com.i10n.db.entity.MailingListAlert;

public class MailinglistAlertRowMapper implements RowMapper {

	@Override
	public Object mapRow(ResultSet rs, int arg1) throws SQLException {
		MailingListAlert alert = new MailingListAlert(rs.getLong("id"), rs
				.getString("name"), rs.getLong("userid"), rs
				.getString("mailid"), rs.getBoolean("overspeeding"), rs
				.getBoolean("geofencing"), rs.getBoolean("chargerdisconnected"),rs.getBoolean("ignition"));
		return alert;
	}

}
