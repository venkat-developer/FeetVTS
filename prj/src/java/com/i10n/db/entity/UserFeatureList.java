package com.i10n.db.entity;

import com.i10n.db.entity.IEntity.IEntity;
import com.i10n.db.entity.primarykey.LongPrimaryKey;

public class UserFeatureList implements IEntity< UserFeatureList>  {

 private LongPrimaryKey userid ;
 private Long featureid;
 
		
	public UserFeatureList(LongPrimaryKey userid, Long featureid) {
	super();
	this.userid = userid;
	this.featureid = featureid;
}

	public UserFeatureList(int i, int j) {
		// TODO Auto-generated constructor stub
	}

	public Long getFeatureid() {
		return featureid;
	}

	public void setFeatureid(Long featureid) {
		this.featureid = featureid;
	}

	public LongPrimaryKey getUserid() {
		return userid;
	}
	public void setUserid(LongPrimaryKey userid) {
		this.userid = userid;
	}
	 
	@Override
	public boolean equalsEntity( UserFeatureList object) {
	    // TODO Auto-generated method stub
	    return false;
	    
	}
	@Override
	public String toString(){
	    StringBuilder strBuilder = new StringBuilder();
	    
	    strBuilder.append(getUserid().getId()).append("-");
	    strBuilder.append(getFeatureid());
	    


	    return strBuilder.toString();
	}

	
}
