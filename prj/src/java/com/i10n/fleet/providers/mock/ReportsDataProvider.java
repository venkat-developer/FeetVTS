package com.i10n.fleet.providers.mock;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;

import com.i10n.fleet.datasets.IDataset;
import com.i10n.fleet.datasets.impl.Dataset;
import com.i10n.fleet.providers.impl.IDataProvider;
import com.i10n.fleet.providers.managers.DriverManager;
import com.i10n.fleet.providers.mock.managers.IDriverManager;
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
public class ReportsDataProvider extends AbstractGroupedDataProvider implements IDataProvider {
	private IVehicleManager m_vehicleManager;
	private IDriverManager m_driverManager;
	private Map<String, IDataProvider> m_delegates = null;

	private static final Logger LOG = Logger.getLogger(ReportsDataProvider.class);


	/**
	 * @see IDataProvider#getDataset(RequestParameters)
	 */
	public IDataset getDataset(RequestParameters params) {
		LOG.debug("Page hit for reports page ack");
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
				IDataset driverData = groupDataset(m_driverManager.getData(getDriverOptions()), "drivers");
				result.put("drivers.groupsdata", driverData);
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
	private IDataset getDriverOptions() {
		IDataset result = new Dataset();
		result.put("filter.assigned", "true");
		result.put("skip.assigned", true);
		result.put("skip.maxspeed", true);
		result.put("skip.avgspeed", true);
		result.put("skip.distance", true);
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
	public IDriverManager getDriverManager() {
		return m_driverManager;
	}

	/**
	 * Sets the Driver Manager
	 * 
	 * @param manager
	 */
	public void setDriverManager(IDriverManager manager) {
		m_driverManager = manager;
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

	public Object upadteStatus(HttpServletRequest request) {
		// TODO : Commented for new architecture implementation
		/*AlertOrViolation alert=null;
		String message=request.getParameter("message");
		message+="::"+SessionUtils.getCurrentlyLoggedInUser().getFirstname();
		Long refid = Long.parseLong(request.getParameter("refid"));
		if (refid != null ) {
			
			alert = new Alert(refid,message);
			try{
				alert = ((AlertDaoImpl) DBManager.getInstance().getDao(DAOEnum.ALERT_DAO)).updateStatus(alert);
			}
			catch (OperationNotSupportedException e) {
				e.printStackTrace();
			}
		}
		return alert;*/
		return null;
	}
}