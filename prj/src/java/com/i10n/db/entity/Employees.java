package com.i10n.db.entity;

import com.i10n.db.entity.IEntity.IEntity;
import com.i10n.db.entity.primarykey.LongPrimaryKey;

public class Employees implements IEntity< Employees> {

private LongPrimaryKey id;
private String name;
public Employees(LongPrimaryKey id, String name) {
	super();
	this.id = id;
	this.name = name;
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
@Override
public boolean equalsEntity(Employees object) {
    // TODO Auto-generated method stub
    return false;
}
    @Override
    public String toString(){
        StringBuilder strBuilder = new StringBuilder();
        strBuilder.append(getId().getId()).append("-").append(getName());
       
        return strBuilder.toString();
    


}



}
