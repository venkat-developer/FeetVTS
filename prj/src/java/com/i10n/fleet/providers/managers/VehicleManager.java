package com.i10n.fleet.providers.managers;

import com.i10n.fleet.datasets.IDataset;
import com.i10n.fleet.datasets.impl.Dataset;

public class VehicleManager implements IDataManager{

	//    private static final Logger LOG = Logger.getLogger(VehicleManager.class);

	private IDataset m_dataset = null;

	@Override
	public IDataset getData(IDataset options) {
		if (m_dataset == null || false){
			m_dataset = parseAndLoadDataSet();
		}
		return null;
	}

	private IDataset parseAndLoadDataSet(){
		
		IDataset vehiclesData = new Dataset();
		return vehiclesData;
	}
}
