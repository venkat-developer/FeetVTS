package com.i10n.db.entity;

import com.i10n.db.entity.IEntity.IEntity;
import com.i10n.db.entity.primarykey.LongPrimaryKey;

public class GroupValues implements IEntity<GroupValues>{
    
    private LongPrimaryKey id;
    
    private String groupValue;
    
    private Long groupId;
    
    private boolean deleted;
    
    public GroupValues(){
    	
    }
    
    public GroupValues(LongPrimaryKey id, String groupName,Long groupId,boolean deleted){
    	this.id = id;
    	this.groupValue = groupName;
    	this.groupId = groupId;
    	this.deleted = deleted;
    }
    public GroupValues(LongPrimaryKey id, String groupName){
    	this.id = id;
    	this.groupValue = groupName;
    }
    
    public GroupValues( String groupName){

    	this.groupValue = groupName;
    }

    @Override
    public boolean equalsEntity(GroupValues object) {
        return false;
    }

    public LongPrimaryKey getId() {
        return id;
    }

    public void setId(LongPrimaryKey id) {
        this.id = id;
    }

    public String getGroupValue() {
        return groupValue;
    }

    public void setGroupValue(String groupValue) {
        this.groupValue = groupValue;
    }
    
    public Long getGroupId() {
        return groupId;
    }

    public void setGroupId(Long groupId) {
        this.groupId = groupId;
    }
    
    public boolean getDeletedStatus() {
        return deleted;
    }

    public void setDeletedStatus(boolean deleted) {
        this.deleted =deleted;
    }
}
