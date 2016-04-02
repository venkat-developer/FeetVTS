package com.i10n.db.idao;
import java.util.List;

import com.i10n.db.entity.BusRoutes;
import com.i10n.db.entity.primarykey.LongPrimaryKey;

public interface IBusRoutesDAO extends IDAO<BusRoutes, LongPrimaryKey> {

	List<BusRoutes> selectByBusstopId(Long busstopid);
	
}

