package com.i10n.db.idao;

import java.util.List;

import com.i10n.db.entity.ACLVehicle;
import com.i10n.db.entity.primarykey.ACLVehiclePrimaryKey;

public interface IACLVehicleDAO extends IDAO<ACLVehicle, ACLVehiclePrimaryKey> {
	
	List<ACLVehicle> selectByUserId(Long userId);

}
