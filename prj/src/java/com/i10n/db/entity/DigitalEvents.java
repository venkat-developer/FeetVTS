package com.i10n.db.entity;

import com.i10n.db.entity.IEntity.IEntity;
import com.i10n.db.entity.primarykey.LongPrimaryKey;

public class DigitalEvents implements IEntity<DigitalEvents> {
	private LongPrimaryKey id;
	private String eventname;
	private int labelfortrue;
	private int labelforfalse;

	public LongPrimaryKey getId() {
		return id;
	}

	public DigitalEvents(LongPrimaryKey id, String eventname, int labelfortrue,
			int labelforfalse) {
		super();
		this.id = id;
		this.eventname = eventname;
		this.labelfortrue = labelfortrue;
		this.labelforfalse = labelforfalse;
	}

	public void setId(LongPrimaryKey id) {
		this.id = id;
	}

	public String getEventname() {
		return eventname;
	}

	public void setEventname(String eventname) {
		this.eventname = eventname;
	}

	public int getLabelfortrue() {
		return labelfortrue;
	}

	public void setLabelfortrue(int labelfortrue) {
		this.labelfortrue = labelfortrue;
	}

	public int getLabelforfalse() {
		return labelforfalse;
	}

	public void setLabelforfalse(int labelforfalse) {
		this.labelforfalse = labelforfalse;
	}

	@Override
	public boolean equalsEntity(DigitalEvents object) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public String toString() {
		StringBuilder strBuilder = new StringBuilder();
		strBuilder.append(getId().getId()).append("-").append(getEventname())
				.append("-").append(getLabelfortrue()).append("-");
		strBuilder.append(getLabelforfalse());
		return strBuilder.toString();

	}
}
