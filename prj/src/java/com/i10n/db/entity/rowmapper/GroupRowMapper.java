package com.i10n.db.entity.rowmapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Set;

import org.springframework.jdbc.core.RowMapper;

import com.i10n.db.entity.Group;
import com.i10n.db.entity.GroupValues;
import com.i10n.db.entity.primarykey.LongPrimaryKey;

public class GroupRowMapper implements RowMapper{
    
    private Group group;
    
    private Set<GroupValues> groupNameValues;

    @Override
    public Object mapRow(ResultSet resultSet, int currentRowNumber) throws SQLException {
    	
        if (groupNameValues == null){
            groupNameValues = new HashSet<GroupValues>();
        }
    	Long primaryKey = resultSet.getLong("group_id");
        String groupName = resultSet.getString("groupname");
        GroupValues groupValue = new GroupValues();
        String groupValueName = null;
        Long valuePrimaryKey ;
        /*
        groupValue.setId(new LongPrimaryKey(primaryKey));
        groupValue.setGroupValue(groupName);*/
        while (!resultSet.isLast()){
        	valuePrimaryKey = resultSet.getLong("group_value_id");
        	groupValueName = resultSet.getString("group_value");
            groupValue = new GroupValues();
            groupValue.setId(new LongPrimaryKey(valuePrimaryKey));
            groupValue.setGroupValue(groupValueName);
            groupNameValues.add(groupValue);
            resultSet.next();
        }
        valuePrimaryKey = resultSet.getLong("group_value_id");
    	groupValueName = resultSet.getString("group_value");
        groupValue = new GroupValues();
        groupValue.setId(new LongPrimaryKey(valuePrimaryKey));
        groupValue.setGroupValue(groupValueName);        
        groupNameValues.add(groupValue);
        
        group = new Group(new LongPrimaryKey(primaryKey), groupName, groupNameValues);
        return group;
    }

}
