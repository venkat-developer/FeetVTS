package com.i10n.db.entity.rowmapper;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

import com.i10n.db.entity.MailingListReport;

public class MailinglistReportRowMapper implements RowMapper {

	@Override
	public Object mapRow(ResultSet rs, int arg1) throws SQLException {
		MailingListReport report = new MailingListReport(rs.getLong("id"), rs
				.getString("name"), rs.getLong("userid"), rs
				.getString("mailid"), rs.getInt("schedule"), rs
				.getTimestamp("lastscheduledat"), rs
				.getBoolean("vehiclestatistics"), rs
				.getBoolean("vehiclestatus"), rs
				.getBoolean("offlinevehiclereport"));
		return report;
	}

}
