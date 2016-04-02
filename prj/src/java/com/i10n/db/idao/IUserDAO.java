package com.i10n.db.idao;

import java.util.List;

import com.i10n.db.entity.User;
import com.i10n.db.entity.primarykey.LongPrimaryKey;

public interface IUserDAO extends IDAO<User, LongPrimaryKey>{
	
	boolean authenticateUser(String userName, String password);
	
	User getUser(String username);
	
	List<User> getUsersForOwner(Long owner_id);


}
