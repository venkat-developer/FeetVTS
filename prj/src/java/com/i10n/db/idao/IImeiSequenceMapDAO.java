/**
 * 
 */
package com.i10n.db.idao;

import com.i10n.db.entity.ImeiSequenceMap;
import com.i10n.db.entity.primarykey.LongPrimaryKey;

/**
 * @author joshua
 *
 */
public interface IImeiSequenceMapDAO extends IDAO<ImeiSequenceMap, LongPrimaryKey> {

	public ImeiSequenceMap saveOrUpdate(ImeiSequenceMap imeiseq);

}
