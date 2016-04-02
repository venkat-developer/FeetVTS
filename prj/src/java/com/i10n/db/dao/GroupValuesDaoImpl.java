package com.i10n.db.dao;

import java.sql.Types;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.incrementer.DataFieldMaxValueIncrementer;

import com.i10n.db.entity.Driver;
import com.i10n.db.entity.GroupValues;
import com.i10n.db.entity.Vehicle;
import com.i10n.db.entity.primarykey.LongPrimaryKey;
import com.i10n.db.entity.rowmapper.GroupValuesRowMapper;
import com.i10n.db.idao.IGroupValuesDAO;
import com.i10n.fleet.exceptions.OperationNotSupportedException;

@SuppressWarnings("unchecked")
public class GroupValuesDaoImpl implements IGroupValuesDAO{
	
	@SuppressWarnings("unused")
	private static Logger LOG = Logger.getLogger(GroupValuesDaoImpl.class);

    private JdbcTemplate jdbcTemplate;
    private DataFieldMaxValueIncrementer groupValuesIdIncrementer;
    
    public void setGroupValuesIdIncrementer(DataFieldMaxValueIncrementer groupValuesIdIncrementer) {
		this.groupValuesIdIncrementer = groupValuesIdIncrementer;
	}

	public void setJdbcTemplate(JdbcTemplate jdbcTemplate){
        this.jdbcTemplate = jdbcTemplate;
    }
    
	@Override
	public GroupValues delete(GroupValues entity)
			throws OperationNotSupportedException {
		String vsql = " select * from vehicles where groupid=? and deleted = false";
		Object vargs []= new Object[] {entity.getId().getId()};
		int vtypes[] = new int[] {Types.BIGINT};
		List<Vehicle> vehicle = jdbcTemplate.queryForList(vsql, vargs, vtypes);
		
		String dsql = " select * from drivers where groupid=? and deleted = false";
		Object dargs []= new Object[] {entity.getId().getId()};
		int dtypes[] = new int[] {Types.BIGINT};
		List<Driver> driver = jdbcTemplate.queryForList(dsql, dargs, dtypes);
		
		if((vehicle.size() !=0)||(driver.size() !=0)){
			return null;
		}
		
		String sql = "update group_values set deleted = true where id = ?";
		Object args []= new Object[] {entity.getId().getId()};
		int types[] = new int[] {Types.BIGINT};
		int rowsDeleted = jdbcTemplate.update(sql, args, types);
		if(rowsDeleted != 1){
			entity = null;
		}
		return entity;
	}

	@Override
	public GroupValues insert(GroupValues entity) throws OperationNotSupportedException {
		Long id = groupValuesIdIncrementer.nextLongValue();
		entity.setId(new LongPrimaryKey(id));
		String sql = "insert into group_values (id, groupid, group_value) values ("
					+entity.getId().getId()+" , "+entity.getGroupId()+", '"+entity.getGroupValue() +"')";
		Object args []= new Object[] { };
        int types[] = new int[] { };
        int rowsAffected = jdbcTemplate.update(sql, args, types);
        if(rowsAffected != 1){
        	return null;
        }
		return entity;
	}

	@Override
	public List<GroupValues> selectAll() {
		String sql = "select * from group_values";
		return jdbcTemplate.query(sql, new GroupValuesRowMapper());
	}
	
	public List<GroupValues> selectAllOwned(Long groupId) {
		String sql = "select * from group_values where groupid="+groupId+" and deleted = false";
		return jdbcTemplate.query(sql, new GroupValuesRowMapper());
	}

	@Override
	public List<GroupValues> selectByPrimaryKey(LongPrimaryKey primaryKey) {
		String sql = "select * from group_values where id = ?";
		Object args [] = new Object[] {primaryKey.getId()};
		int types[] = new int[] {Types.BIGINT};
		return jdbcTemplate.query(sql, args, types, new GroupValuesRowMapper());
	}
	
	public String getGroupValue(LongPrimaryKey primaryKey) {
		String sql = "select * from group_values where id = ?";
		Object args [] = new Object[] {primaryKey.getId()};
		int types[] = new int[] {Types.BIGINT};
		List<GroupValues> groupValues = jdbcTemplate.query(sql, args, types, new GroupValuesRowMapper());
		if(groupValues != null && groupValues.size() > 0){
			return groupValues.get(0).getGroupValue();
		}
		return null;
	}

	@Override
	public GroupValues update(GroupValues entity)
			throws OperationNotSupportedException {
		String sql = "update group_values set group_value=? where id=?";
		Object args []= new Object[] {entity.getGroupValue(),entity.getId().getId() };
		int types[] = new int[] {  Types.VARCHAR, Types.BIGINT};
		int rowsUpdated = jdbcTemplate.update(sql, args, types);
		if(rowsUpdated != 1){
			entity = null;
		}
		return entity;
	}
}
