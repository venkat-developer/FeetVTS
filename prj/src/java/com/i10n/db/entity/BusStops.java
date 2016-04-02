package com.i10n.db.entity;


import org.postgis.Geometry;

import com.i10n.db.entity.IEntity.IEntity;
import com.i10n.db.entity.primarykey.LongPrimaryKey;

public class BusStops implements IEntity<BusStops>{

    private LongPrimaryKey id;
    
    private String busstopname;
    
    private Geometry location;
    public BusStops(LongPrimaryKey id,String busstopname,Geometry location)
	{
		super();
		this.id = id;
		this.busstopname = busstopname;
		this.location = location;
	}
    
	public LongPrimaryKey getId() {
        return id;
    }

    public void setId(LongPrimaryKey id) {
        this.id = id;
    }

    public String getBusstopName() {
        return busstopname;
    }

    public void setBusstopName(String busstopname) {
        this.busstopname = busstopname;
    }
    public Geometry getLocation() {
        return location;
    }

    public void setLocation(Geometry location) {
        this.location =location;
    }
    
    @Override
	public String toString() {
        StringBuilder strBuilder = new StringBuilder();
        strBuilder.append(getId().getId()).append(" ").append(getBusstopName()).append(" ").append(getLocation());
        return strBuilder.toString();
    }

    @Override
    public boolean equalsEntity(BusStops object) {
        return false;
    }   
}