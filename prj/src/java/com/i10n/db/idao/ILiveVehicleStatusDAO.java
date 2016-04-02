package com.i10n.db.idao;

import java.util.List;

import com.i10n.db.entity.LiveVehicleStatus;
import com.i10n.db.entity.primarykey.LongPrimaryKey;

public interface ILiveVehicleStatusDAO extends IDAO<LiveVehicleStatus, LongPrimaryKey> {
	//Fetch Live Vehicle Status based on Trip Ids
	List<LiveVehicleStatus> fetchLiveVehicleStatusByTripIDs(List<Long> tripIds);
	
	List<LiveVehicleStatus> fetchLiveVehicleStatusOfUser(long userId);
}
