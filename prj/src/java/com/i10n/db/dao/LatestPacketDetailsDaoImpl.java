package com.i10n.db.dao;

import java.sql.Types;
import java.util.List;

import org.springframework.jdbc.core.JdbcTemplate;

import com.i10n.db.entity.LatestPacketDetails;
import com.i10n.db.entity.primarykey.LongPrimaryKey;
import com.i10n.db.entity.rowmapper.LatestPacketDetailsRowMapper;
import com.i10n.db.idao.ILatestPacketDetailsDAO;
import com.i10n.fleet.exceptions.OperationNotSupportedException;

@SuppressWarnings("unchecked")
public class LatestPacketDetailsDaoImpl implements ILatestPacketDetailsDAO {

	private JdbcTemplate jdbcTemplate;

	public JdbcTemplate getJdbcTemplate() {
		return jdbcTemplate;
	}

	public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}

	public LatestPacketDetails refresh(LatestPacketDetails latestPacketDetails) {
		List<LatestPacketDetails> latestPackets = selectByPrimaryKey(latestPacketDetails
				.getId());
		return (latestPackets == null) ? null : latestPackets.get(0);
	}

	@Override
	public LatestPacketDetails delete(LatestPacketDetails entity)
			throws OperationNotSupportedException {
		String sql = "delete from latest_packet_details where liveid = "
				+ entity.getId().getId();
		jdbcTemplate.update(sql);
		return entity;
	}

	@Override
	public LatestPacketDetails insert(LatestPacketDetails entity)
			throws OperationNotSupportedException {
		String sql = "insert into latest_packet_details (liveid, trackhistory_id, idle_points_id, last_saved_trackhistory_id, latest_trackhistory_pingcounter, latest_but_one_trackhistory_pingcounter, last_computed_idle_points_ids, last_computed_idle_point_end_time) values (?,?,?,?,?,?,?,?)";
		Object args[] = new Object[] { entity.getId().getId(), entity.getTrackhistoryId(), entity.getIdlePointsId(),
				entity.getLastSavedTrackhistoryId(), entity.getLatestTrackhistoryPingcounter(),
				entity.getLatestButOneTrackhistoryPingcounter(), entity.getLastComputedIdlePointsIds(),
				entity.getLastComputedIdlePointEndTime() };
		int types[] = new int[] { Types.BIGINT, Types.BIGINT, Types.BIGINT, Types.BIGINT, Types.BIGINT, Types.BIGINT, Types.VARCHAR, Types.DATE };
		jdbcTemplate.update(sql, args, types);
		return entity;
	}

	@Override
	public List<LatestPacketDetails> selectAll() {
		String sql = "select * from latest_packet_details";
		return jdbcTemplate.query(sql, new LatestPacketDetailsRowMapper());
	}

	@Override
	public List<LatestPacketDetails> selectByPrimaryKey(LongPrimaryKey primaryKey) {
		String sql = "select * from latest_packet_details where liveid = "+ primaryKey.getId();
		return jdbcTemplate.query(sql, new LatestPacketDetailsRowMapper());
	}

	@Override
	public LatestPacketDetails update(LatestPacketDetails entity) throws OperationNotSupportedException {
		String sql = "update latest_packet_details set trackhistory_id = " + entity.getTrackhistoryId()
				+ ", idle_points_id = " + entity.getIdlePointsId()+ ", last_saved_trackhistory_id = "
				+ entity.getLastSavedTrackhistoryId()+ ", latest_trackhistory_pingcounter = "
				+ entity.getLatestTrackhistoryPingcounter()+ ", latest_but_one_trackhistory_pingcounter = "
				+ entity.getLatestButOneTrackhistoryPingcounter()+ ", last_computed_idle_points_ids = '"
				+ entity.getLastComputedIdlePointsIds() + "'"+"where liveid="+entity.getId().getId();
		jdbcTemplate.execute(sql);
		return entity;
	}

}
