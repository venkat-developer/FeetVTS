/**
 * 
 */
package com.i10n.db.idao;

import java.util.List;

import com.i10n.db.entity.ACLDriver;
import com.i10n.db.entity.primarykey.ACLDriverPrimaryKey;

/**
 * @author joshua
 *
 */
public interface IACLDriverDAO extends IDAO<ACLDriver,ACLDriverPrimaryKey > {

	List<ACLDriver> SelectByUserId(Long userid); 

}
