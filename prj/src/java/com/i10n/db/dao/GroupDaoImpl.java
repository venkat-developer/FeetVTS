package com.i10n.db.dao;

import java.sql.Types;
import java.util.List;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.incrementer.DataFieldMaxValueIncrementer;

import com.i10n.db.entity.Group;
import com.i10n.db.entity.primarykey.LongPrimaryKey;
import com.i10n.db.entity.rowmapper.GroupRowMapper;
import com.i10n.db.entity.rowmapper.GroupRowMapper2;
import com.i10n.db.idao.IGroupDAO;
import com.i10n.fleet.exceptions.OperationNotSupportedException;

@SuppressWarnings("unchecked")
public class GroupDaoImpl implements IGroupDAO{

    private JdbcTemplate jdbcTemplate;
    private DataFieldMaxValueIncrementer groupIdIncrementer;
     
    public void setJdbcTemplate(JdbcTemplate jdbcTemplate){
        this.jdbcTemplate = jdbcTemplate;
    }
 
    public void setGroupIdIncrementer(DataFieldMaxValueIncrementer bookIncrementer) {
        this.groupIdIncrementer = bookIncrementer;
    }
    
    public JdbcTemplate getJdbcTemplate() {
		return jdbcTemplate;
	}

	public DataFieldMaxValueIncrementer getGroupIdIncrementer() {
		return groupIdIncrementer;
	}

	@Override
    public Group delete(Group entity) throws OperationNotSupportedException {
        return null;
    }

    @Override
    public Group insert(Group entity) throws OperationNotSupportedException {
        return null;
    }

    @Override
    public List<Group> selectAll() {
    	String sql = "select * from groups";
    	List<Group> groups = jdbcTemplate.query(sql,new GroupRowMapper2()); 
        return groups;
    }

    @Override
    public List<Group> selectByPrimaryKey(LongPrimaryKey primaryKey) {
        String sql = "select a.id as group_id, a.name as groupname, b.id as group_value_id, b.group_value as group_value from groups a, " +
        		"group_values b where a.id = ? and a.id = b.groupid and b.deleted = false";
        Object[] values = new Object[]{primaryKey.getId()};
        int[] types = new int[]{Types.BIGINT};
        List<Group> groups = jdbcTemplate.query(sql, values, types, new GroupRowMapper());
        return (groups == null || groups.size() == 0)?null:groups;
    }

    @Override
    public Group update(Group entity) throws OperationNotSupportedException {
        return null;
    }
}
