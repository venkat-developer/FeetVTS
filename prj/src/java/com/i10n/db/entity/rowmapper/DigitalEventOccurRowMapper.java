package com.i10n.db.entity.rowmapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import org.springframework.jdbc.core.RowMapper;
import com.i10n.db.entity.DigitalEventOccur;
import com.i10n.db.entity.primarykey.LongPrimaryKey;

public class DigitalEventOccurRowMapper implements RowMapper {
	@Override
	public Object mapRow(ResultSet rs, int arg1) throws SQLException {
		DigitalEventOccur newDig = new DigitalEventOccur(
				new LongPrimaryKey(rs.getLong("tripid")),
				rs.getLong("eventid"),
				rs.getString("truedate"),
				rs.getString("falsedate"));
		return newDig;
	}
}