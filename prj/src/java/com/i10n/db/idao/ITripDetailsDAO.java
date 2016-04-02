package com.i10n.db.idao;

import java.util.List;

import com.i10n.db.entity.TripDetails;
import com.i10n.db.entity.primarykey.LongPrimaryKey;

public interface ITripDetailsDAO extends IDAO<TripDetails, LongPrimaryKey>  {
    public List<TripDetails> getActiveTripDetailsWithLiveStatusForTheUser(LongPrimaryKey userId);
}
