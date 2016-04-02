package com.i10n.fleet.providers.mock;

import com.i10n.fleet.datasets.IDataset;
import com.i10n.fleet.datasets.impl.Dataset;
import com.i10n.fleet.providers.impl.IDataProvider;
import com.i10n.fleet.providers.mock.managers.IVehicleManager;
import com.i10n.fleet.web.request.RequestParameters;
import com.i10n.fleet.web.request.RequestParameters.RequestParams;

/**
 * A Mock Data Provider LiveTrack View. Uses a mock Vehicle Manager to provide
 * vehicle data. Will be removed in future.
 * 
 * @author Sabarish
 * 
 */
public class LiveTrackDataProvider extends AbstractGroupedDataProvider implements
        IDataProvider {
//	private static final Logger LOG = Logger.getLogger(LiveTrackDataProvider.class);
    /**
     * Mock vehicle manager
     */
    private IVehicleManager m_vehicleManager;

    /**
     * @see IDataProvider#getDataset(RequestParameters)
     */
    public IDataset getDataset(RequestParameters params) {
        IDataset result = new Dataset();
        if (null == params.getParameter(RequestParams.markup)) {
        	IDataset vehicleData = groupDataset(m_vehicleManager.getData(getVehicleOptions()), "vehicles");
        	result.put("vehicles.groupsdata", vehicleData);
            }
        return result;
    }

    /**
     * return data options for Vehicle Manager
     * 
     * @return
     */
    private IDataset getVehicleOptions() {
        IDataset result = new Dataset();
        result.put("filter.assigned", "true");
        result.put("skip.trip", true);
        result.put("skip.driverstatus", true);
        result.put("skip.driverfirstname", true);
        result.put("skip.driverlastname", true);
        result.put("skip.driverlicense", true);
        result.put("skip.driverassigned", true);
        result.put("skip.drivergroupid", true);
        result.put("skip.drivergroupname", true);
        result.put("skip.drivermaxspeed", true);
        result.put("skip.driveravgspeed", true);
        result.put("skip.driverdistance", true);
        result.put("skip.maxspeed", true);
        result.put("skip.distance", true);
        result.put("skip.startlocation", true);
        result.put("skip.startfuel", true);
        return result;
    }

    /**
     * @see IDataProvider#getName()
     */
    public String getName() {
        return "view";
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
     * Sets the name of the vehicle manager.
     * 
     * @param vehicleManager
     */
    public void setVehicleManager(IVehicleManager vehicleManager) {
        m_vehicleManager = vehicleManager;
    }
}