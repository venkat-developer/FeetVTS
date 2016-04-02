package com.i10n.fleet.providers.mock;

import java.util.Map;

import com.i10n.fleet.datasets.IDataset;
import com.i10n.fleet.datasets.impl.Dataset;
import com.i10n.fleet.providers.impl.IDataProvider;
import com.i10n.fleet.providers.managers.DriverManager;
import com.i10n.fleet.providers.mock.managers.IStopManager;
import com.i10n.fleet.providers.mock.managers.IVehicleManager;
import com.i10n.fleet.web.request.RequestParameters;
import com.i10n.fleet.web.request.RequestParameters.RequestParams;

/**
 * A Mock Data Provider for Reports Page. Uses {@link IVehicleManager} and
 * {@link DriverManager} to get vehicles and drivers data. Also supports
 * delegation of specific report's data to the corresponding
 * {@link IDataProvider}. This class will be removed in future.
 * 
 * @author Sabarish
 * 
 */
public class SasDataProvider extends AbstractGroupedDataProvider implements IDataProvider {

	private IVehicleManager m_vehicleManager;
	private IStopManager m_stopManager;
	private Map<String, IDataProvider> m_delegates = null;

	/**
	 * @see IDataProvider#getDataset(RequestParameters)
	 */
	public IDataset getDataset(RequestParameters params) {
		IDataset result = new Dataset();
		String reportID = params.getParameter(RequestParams.report);
		if (null != reportID && null != m_delegates.get(reportID)) {
			IDataProvider delegator = m_delegates.get(reportID);
			result = delegator.getDataset(params);

		}
		else {
			if (null == params.getParameter(RequestParams.markup)) {
				IDataset vehicleData = groupDataset(m_vehicleManager.getParseDataset(getVehicleOptions()), "vehicles");
				result.put("vehicles.groupsdata", vehicleData);
				final IDataset stopData = groupDataset(m_stopManager.getData(getStopOptions()), "stops");
				result.put("stops.groupsdata",stopData);
			}
		}
		return result;
	}

	/**
	 * Returns the data options for Vehicle Manager
	 * 
	 * @return
	 */
	private IDataset getVehicleOptions() {
		IDataset result = new Dataset();
		result.put("filter.assigned", "true");
		result.put("skip.trip", true);
		result.put("skip.assigned", true);
		result.put("skip.drivermaxspeed", true);
		result.put("skip.driveravgspeed", true);
		result.put("skip.driverdistance", true);
		result.put("skip.driverstatus", true);
		result.put("skip.driverfirstname", true);
		result.put("skip.driverlastname", true);
		result.put("skip.driverlicense", true);
		result.put("skip.driverassigned", true);
		result.put("skip.drivergroupid", true);
		result.put("skip.drivergroupname", true);
		result.put("skip.maxspeed", true);
		result.put("skip.distance", true);
		result.put("skip.startlocation", true);
		result.put("skip.startfuel", true);
		return result;
	}

	/**
	 * Returns the data options for Driver Manager
	 * 
	 * @return
	 */
	private IDataset getStopOptions() {
		IDataset result = new Dataset();
		result.put("filter.assigned", "true");
		result.put("skip.assigned", true);
		return result;
	}

	/**
	 * @see IDataProvider#getName()
	 */
	public String getName() {
		return "view";
	}

	/**
	 * Sets the Vehicle Manager
	 * 
	 * @param vehicleManager
	 */
	public void setVehicleManager(IVehicleManager vehicleManager) {
		m_vehicleManager = vehicleManager;
	}

	/**
	 * Returns the Vehicle Manager
	 * 
	 * @return
	 */
	public IVehicleManager getVehicleManager() {
		return m_vehicleManager;
	}

	/**
	 * Returns the Driver Manager
	 * 
	 * @return
	 */
	public IStopManager getStopManager() {
		return m_stopManager;
	}

	/**
	 * Sets the Driver Manager
	 * 
	 * @param manager
	 */
	public void setStopManager(IStopManager manager) {
		m_stopManager = manager;
	}

	/**
	 * Returns the {@link Map} of {@link IDataProvider}
	 * 
	 * @return
	 */
	public Map<String, IDataProvider> getReportProviders() {
		return m_delegates;
	}

	/**
	 * Sets the {@link Map} of {@link IDataProvider}
	 * 
	 * @param providers
	 */
	public void setReportProviders(Map<String, IDataProvider> providers) {
		m_delegates = providers;
	}

}