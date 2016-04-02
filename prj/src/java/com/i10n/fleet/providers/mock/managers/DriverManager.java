package com.i10n.fleet.providers.mock.managers;

import java.util.List;

import org.apache.log4j.Logger;
import com.i10n.db.dao.DAOEnum;
import com.i10n.db.dao.TripDetailsDaoImpl;
import com.i10n.db.entity.Driver;
import com.i10n.db.entity.TripDetails;
import com.i10n.db.entity.primarykey.LongPrimaryKey;
import com.i10n.db.tools.DBManager;
import com.i10n.fleet.datasets.IDataset;
import com.i10n.fleet.datasets.impl.Dataset;
import com.i10n.fleet.web.utils.SessionUtils;

/**
 * Mock Driver Manager : Manages the drivers data. This class will be removed in
 * future
 * 
 * @author Sabarish
 * @extends {@link AbstractDelegatedDataManager}
 * @see IDriverManager
 */
public class DriverManager extends AbstractDelegatedDataManager implements IDriverManager {

	private static final String FILE_DOCUMENT = "/mock/drivers.xml";

	private static Logger LOG = Logger.getLogger(DriverManager.class);

	private IDataset m_dataset = null;
	IDataset driverattrip=new Dataset();
	IDataset drivers=new Dataset();
	IDataset result = new Dataset();
	/**
	 * @see IDriverManager#getData(IDataset)
	 */
	public IDataset getData(IDataset options) {
		if (null == m_dataset) {
			result= parseDataset();
		}
		return result;
	}



	/**
	 * parses the document and sets the global dataset to the parsed data
	 */
	private IDataset parseDataset() {
		LOG.debug("In drivermanager parsedataset starting");
		
		//List<Driver> resultset = ((DriverDaoImp) DBManager.getInstance().getDao(DAOEnum.DRIVER_DAO)).getAssignedDriverWithGroupValue(uid);
		/* Vishnu : Fetching information from cache */
		LOG.debug("Attempting to fetch data for DriverManager::parseDataset from cache");
		TripDetailsDaoImpl tripDetailsDaoImpl = (TripDetailsDaoImpl)DBManager.getInstance().getDao(DAOEnum.TRIP_DETAILS_DAO);
		List<TripDetails> tripDetailsList = tripDetailsDaoImpl.getActiveTripDetailsWithLiveStatusForTheUser(new LongPrimaryKey(SessionUtils.getCurrentlyLoggedInUser().getId()));

				IDataset driverData = new Dataset();
		Driver driver=null;

		if (tripDetailsList.size() != 0) {
			for (int i = 0; i < tripDetailsList.size(); i++) {
				driver = tripDetailsList.get(i).getDriver();
				driverData.put("driver-" +  (i + 1) + ".id","driver-"+ driver.getId().getId() );
				driverData.put("driver-" +  (i + 1) + ".name",driver.getFirstName());
				driverData.put("driver-" +  (i + 1) + ".status",driver.getStatus(driver.getId().getId()));
				driverData.put("driver-" +  (i + 1) + ".firstname",driver.getFirstName());
				driverData.put("driver-" +  (i + 1) + ".lastname",driver.getLastName());
				driverData.put("driver-" +  (i + 1) + ".license", driver.getLicenseno());
				driverData.put("driver-" +  (i + 1) + ".maxspeed",80+i+"");
				driverData.put("driver-" +  (i + 1) + ".avgspeed",45+i+"");
				driverData.put("driver-" +  (i + 1) + ".distance",100+i+"");
				driverData.put("driver-" +  (i + 1) + ".assigned","false");
				driverData.put("driver-" +  (i + 1) + ".groupid","group-"+driver.getGroupId());
				driverData.put("driver-" +  (i + 1) + ".groupname",driver.getGroupName());
			}
		}
		LOG.debug("In drivermanager parsedataset Ending");
		result=driverData;
		return result;
	}

	/**
	 * @see AbstractDataManager#getDocumentName()
	 */
	@Override
	protected String getDocumentName() {
		return FILE_DOCUMENT;
	}

	/**
	 * @see AbstractDelegatedDataManager#getDataID()
	 */
	@Override
	protected String getDataID() {
		return "driver";
	}
}
