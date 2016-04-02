package com.i10n.db.entity.primarykey;

import java.io.Serializable;

import com.i10n.db.entity.IEntity.IPrimaryKey.IPrimaryKey;

public class LongPrimaryKey implements IPrimaryKey , Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 4L;
	private Long id;
	
	public LongPrimaryKey(Long id){
		this.id = id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getId() {
		return id;
	}
	
}
