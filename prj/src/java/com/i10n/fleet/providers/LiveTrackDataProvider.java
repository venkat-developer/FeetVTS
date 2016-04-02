package com.i10n.fleet.providers;

import com.i10n.fleet.datasets.IDataset;
import com.i10n.fleet.datasets.impl.Dataset;
import com.i10n.fleet.providers.impl.IDataProvider;
import com.i10n.fleet.providers.managers.ITripManager;
import com.i10n.fleet.web.request.RequestParameters;
import com.i10n.fleet.web.request.RequestParameters.RequestParams;

public class LiveTrackDataProvider implements IDataProvider{
	/**
	 * Mock Trip manager
	 */
	private ITripManager m_tripManager;

	@Override
	public IDataset getDataset(RequestParameters params) {
		IDataset result = new Dataset();
		String groupId = params.getRequestParameter("groupId");
		if (null == params.getParameter(RequestParams.markup)) {
			if(groupId != null){
				IDataset tripData = m_tripManager.getData(null,groupId);
				result = tripData;
			}else{
				IDataset tripData = m_tripManager.getData(null,groupId);
				result = tripData;	
			}
		}
		return result;
	}

	@Override
	public String getName() {
		return "view";
	}

	public ITripManager getTripManager() {
		return m_tripManager;
	}

	public void setTripManager(ITripManager tripManager) {
		m_tripManager = tripManager;
	}

}