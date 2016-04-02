package com.i10n.db.idao;

import java.util.List;

import com.i10n.db.entity.Driver;
import com.i10n.db.entity.primarykey.LongPrimaryKey;

public interface IDriverDAO extends IDAO<Driver, LongPrimaryKey> {

	List<Driver>  getDriver(LongPrimaryKey licenseno);

}
