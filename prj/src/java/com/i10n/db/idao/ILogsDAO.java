
package com.i10n.db.idao;

import java.util.List;

import com.i10n.db.entity.Logs;
import com.i10n.db.entity.primarykey.LongPrimaryKey;

    public interface ILogsDAO extends IDAO<Logs, LongPrimaryKey> {

		List<Logs> selectByUserId(Long userid);
    }


