/**
 * 
 */
package com.i10n.db.idao;

import java.util.List;

import com.i10n.db.entity.Address;
import com.i10n.db.entity.primarykey.LongPrimaryKey;

/**
 * @author joshua
 *
 */
public interface IAddressDAO extends IDAO<Address, LongPrimaryKey> {

	List<Address> fetchLocationfromDB(Double x,Double y);
	
	
	
	
}
