package com.i10n.db.entity;

import java.util.Set;

import com.i10n.db.entity.IEntity.IEntity;
import com.i10n.db.entity.primarykey.LongPrimaryKey;

public class Group implements IEntity<Group>{

    private LongPrimaryKey id;
    
    private String name;
    
    private Set<GroupValues> groupValues;
    
    public Group(LongPrimaryKey id, String name, Set<GroupValues> groupValues){
        this.id = id;
        this.name = name;
        this.groupValues = groupValues;
    }
    
    public Group(LongPrimaryKey id, String name){
    	this.id = id;
    	this.name = name;
    	this.groupValues = null;
    }

    public LongPrimaryKey getId() {
        return id;
    }

    public void setId(LongPrimaryKey id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
    
    public Set<GroupValues> getGroupValues(){
    	return groupValues;
    }
    
    public void setGroupValues(Set<GroupValues> groupValues){
    	this.groupValues = groupValues;
    }

    @Override
    public boolean equalsEntity(Group object) {
        return false;
    }    
}
