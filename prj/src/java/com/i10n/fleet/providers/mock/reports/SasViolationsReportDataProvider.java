package com.i10n.fleet.providers.mock.reports;

import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.util.WebUtils;

import com.i10n.db.entity.DateRange;
import com.i10n.fleet.datasets.IDataset;
import com.i10n.fleet.datasets.impl.Dataset;
import com.i10n.fleet.providers.impl.IDataProvider;
import com.i10n.fleet.providers.mock.AbstractGroupedDataProvider;
import com.i10n.fleet.providers.mock.managers.GroupManager;
import com.i10n.fleet.providers.mock.managers.RouteDeviationManager;
import com.i10n.fleet.providers.mock.managers.StopDeviationManager;
import com.i10n.fleet.providers.mock.managers.TimeDeviationManager;
import com.i10n.fleet.providers.mock.managers.TripMissDeviationManager;
import com.i10n.fleet.util.Constants;
import com.i10n.fleet.util.DateUtils;
import com.i10n.fleet.web.request.RequestParameters;
 
/**
 * Mock : Mock Data Provider for Violation Report . This class will be removed
 * in future.
 *
 * @author Sabarish
 *
 */
public class SasViolationsReportDataProvider extends AbstractGroupedDataProvider implements IDataProvider {
	public static Logger LOG = Logger.getLogger(SasViolationsReportDataProvider.class);
    private TimeDeviationManager m_timeDevManager;
    private RouteDeviationManager m_routeDevManager;
    private StopDeviationManager m_stopDevManager;
    private TripMissDeviationManager m_tripMissDevManager;
    private GroupManager m_groupManager;
    
    private String startdate;
    private String enddate;
    private DateRange dateRange = new DateRange();
    private String localTime = null,localTimeZone = null;
    private String vehicleId = null;
 
    /**
     * @see IDataProvider#getDataset(RequestParameters)
     */
    
	public IDataset getDataset(RequestParameters params) {
        startdate = params.getRequestParameter("startdate");
        enddate = params.getRequestParameter("enddate");
        vehicleId = params.getRequestParameter("vehicleId");
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        localTime = (String)WebUtils.getSessionAttribute(request, Constants.SESSION.ATTR_TIME);
        localTimeZone = (String)WebUtils.getSessionAttribute(request, Constants.SESSION.ATTR_TIMEZone);
        dateRange=DateUtils.getModeOfReport(localTime, startdate, enddate);
        IDataset routeDevData = m_routeDevManager.getData(getDataOptions(params));
        IDataset timeDevData = m_timeDevManager.getData(getDataOptions(params));
        IDataset stopDevData = m_stopDevManager.getData(getDataOptions(params));
        IDataset tripMissDevData = m_tripMissDevManager.getData(getDataOptions(params));
        IDataset result = mergeData(routeDevData, timeDevData, stopDevData, tripMissDevData);
        return result;
        
    }
    /**
     * Returns Data Options for Violation Data Managers.
     *
     * @param params
     * @return
     */
    private IDataset getDataOptions(RequestParameters params) {
    	IDataset options = new Dataset();
        options.put("filter.startdate", dateRange.getStart());
        options.put("filter.enddate", dateRange.getEnd());
        options.put("startdate", startdate);
        options.put("enddate", enddate);
        options.put("localTime", localTime);
        options.put("localTimeZone", localTimeZone);
        options.put("vehicleID", vehicleId);
        options.put("from", "sasviolations");
        return options;
    }
 
    /**
     * Merges data of all the violations into one dataset.
     *
     * @param geoFenceData
     * @param overSpeedData
     * @param chargerDCData
     * @return
     */
    private IDataset mergeData(IDataset routeDevsData, IDataset timeDevsData, IDataset stopDevsData, IDataset tripmissreportDevsData) {
        IDataset result = new Dataset();
        result.put("routedev", expandData(routeDevsData));
        result.put("timedev", expandData(timeDevsData));
        result.put("stopdev", expandData(stopDevsData));
        result.put("tripmissdev", expandData(tripmissreportDevsData));
        return result;
    }
    
    /**
     * Decorates and adds Group/Vehicles info into the dataset
     * 
     * @param data
     * @return
     */
    @SuppressWarnings("unchecked")
    private IDataset expandData(IDataset data) {
        IDataset grpData = m_groupManager.getData(null).copy();
        List<IDataset> list = new ArrayList<IDataset>();
        for (Entry<String, Object> entry : data.entrySet()) {
            list.addAll((List<IDataset>) entry.getValue());
        }
        for (IDataset listData : list) {
            String grpID = listData.getValue("groupid");
            List<IDataset> grpList = null;
            if (null == grpData.get(grpID + "." + "violations")) {
                grpList = new ArrayList<IDataset>();
                grpData.put(grpID + "." + "violations", grpList);
            } else {
                grpList = (List<IDataset>) grpData.get(grpID + "."+ "violations");
            }
            IDataset refinedSet = listData.copy();
            refinedSet.put("id", "violation-" + list.indexOf(listData));
            refinedSet.remove("groupid");
            grpList.add(refinedSet);
        }
        return grpData;
    }

 
    /**
     * @see IDataProvider#getName()
     */
    public String getName() {
        return "sasviolations";
    }

	public void setTimeDeviationManager(TimeDeviationManager m_timeDevManager) {
		this.m_timeDevManager = m_timeDevManager;
	}

	public TimeDeviationManager getTimeDeviationManager() {
		return m_timeDevManager;
	}

	public void setRouteDeviationManager(RouteDeviationManager m_routeDevManager) {
		this.m_routeDevManager = m_routeDevManager;
	}

	public RouteDeviationManager getRouteDeviationManager() {
		return m_routeDevManager;
	}

	public void setStopDeviationManager(StopDeviationManager m_stopDevManager) {
		this.m_stopDevManager = m_stopDevManager;
	}

	public StopDeviationManager getStopDeviationManager() {
		return m_stopDevManager;
	}
	
	public void setTripMissDeviationManager(TripMissDeviationManager m_tripMissDevManager) {
		this.m_tripMissDevManager = m_tripMissDevManager;
	}

	public TripMissDeviationManager getTripMissDeviationManager() {
		return m_tripMissDevManager;
	}
	
    /**
     * Sets the Group Manager
     * 
     * @param manager
     */
    public void setGroupManager(GroupManager manager) {
        m_groupManager = manager;
    }
 
    /**
     * Returns the Group Manager
     * 
     * @return
     */
    public GroupManager getGroupManager() {
        return m_groupManager;
    }
 
}