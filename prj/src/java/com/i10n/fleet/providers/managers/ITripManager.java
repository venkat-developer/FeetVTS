package com.i10n.fleet.providers.managers;

import com.i10n.fleet.datasets.IDataset;
import com.i10n.fleet.web.request.RequestParameters;


public interface ITripManager extends IDataManager{
	IDataset getData(RequestParameters params,IDataset options);

	IDataset getData(IDataset options, String status);
    
}
