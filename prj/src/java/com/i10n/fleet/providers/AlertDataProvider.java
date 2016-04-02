package com.i10n.fleet.providers;

import java.util.Calendar;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.util.WebUtils;

import com.i10n.db.dao.AlertDaoImpl;
import com.i10n.db.dao.DAOEnum;
import com.i10n.db.dao.EmriVehiclesBaseStationDaoImp;
import com.i10n.db.entity.Address;
import com.i10n.db.entity.AlertOrViolation;
import com.i10n.db.entity.EmriVehiclesBaseStation;
import com.i10n.db.tools.DBManager;
import com.i10n.dbCacheManager.LoadDriverDetails;
import com.i10n.dbCacheManager.LoadEmriVehiclesBaseStationDetails;
import com.i10n.dbCacheManager.LoadVehicleDetails;
import com.i10n.fleet.datasets.IDataset;
import com.i10n.fleet.datasets.impl.Dataset;
import com.i10n.fleet.providers.impl.IDataProvider;
import com.i10n.fleet.providers.managers.IAlertManager;
import com.i10n.fleet.util.Constants;
import com.i10n.fleet.util.DateUtils;
import com.i10n.fleet.util.EnvironmentInfo;
import com.i10n.fleet.util.GeoUtils;
import com.i10n.fleet.util.StringUtils;
import com.i10n.fleet.web.request.RequestParameters;
import com.i10n.fleet.web.utils.SessionUtils;

/**
 * 
 * @author Dharmaraju V
 *
 */
public class AlertDataProvider implements IDataProvider {

	public static Logger LOG = Logger.getLogger(AlertDataProvider.class);

	private IAlertManager m_alertManager;
	int totalCount=0;
	int date = Calendar.getInstance().get(Calendar.DATE);
	@Override
	public IDataset getDataset(RequestParameters params) {

		HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
		String localTimeZone = (String)WebUtils.getSessionAttribute(request, Constants.SESSION.ATTR_TIMEZone);
		IDataset finalresult = new Dataset();
		IDataset result = new Dataset();
		String isGoogleHit = request.getParameter("isGoogleHit");
		LOG.debug("isGoogleHit : "+isGoogleHit);
		int currentDate = Calendar.getInstance().get(Calendar.DATE);
		if(isGoogleHit.equals("true")){
			LOG.debug("Trying for Google HitCount");
			LOG.debug("Date:"+date+"Current Date:"+currentDate);
			if(date != currentDate){
				LOG.debug(" Assigning Google Map Hit totalCount as zero because of new day");
				date =	currentDate;
				totalCount=0;          
			}
			totalCount=totalCount+1;
			LOG.info("Total No of Google Map Hit Count is: "+totalCount);
		}else{
			LOG.debug("Trying for Alert Process");
			List<AlertOrViolation> alertResultset = ((AlertDaoImpl) DBManager.getInstance().getDao(DAOEnum.ALERT_DAO))
					.selectUndisplayedAlertsForUser(SessionUtils.getCurrentlyLoggedInUser().getId());

			if(alertResultset.size() != 0){
				for(int i=0;i<alertResultset.size();i++){
					AlertOrViolation alertdata = alertResultset.get(i);
					String vehicleName = LoadVehicleDetails.getInstance().retrieve(alertdata.getVehicleId()).getDisplayName();
					String driverName = LoadDriverDetails.getInstance().retrieve(alertdata.getDriverId()).getFirstName();
					result.put("alert-"+(i+1)+".id", alertdata.getId().getId());
					result.put("alert-"+(i+1)+".vehicleid", alertdata.getVehicleId());
					result.put("alert-"+(i+1)+".vehiclename", vehicleName);
					LOG.debug("Vehicleid:"+alertdata.getVehicleId());
					if(Boolean.valueOf(EnvironmentInfo.getProperty("IS_FRS_EMRI_CLIENT"))){
						EmriVehiclesBaseStation cachedEmriRajasthan=null;
						List<EmriVehiclesBaseStation> emriList = ((EmriVehiclesBaseStationDaoImp)DBManager.getInstance().
								getDao(DAOEnum.EMRI_RAJASTHAN_DAO)).selectByVehicleId(alertdata.getVehicleId());
						if(emriList != null && emriList.size() > 0){
							Long emriRajasthanId = emriList.get(0).getID().getId();
							cachedEmriRajasthan = LoadEmriVehiclesBaseStationDetails.getInstance().retrieve(emriRajasthanId);
							LOG.debug("BaseLocation: "+cachedEmriRajasthan.getBaseLocation()+"CrewNo:"+cachedEmriRajasthan.getCrewNo());
						}
						if(cachedEmriRajasthan != null){
							LOG.debug("District in:"+cachedEmriRajasthan.getDistrict());
							result.put("alert-"+(i+1)+".baselocation",cachedEmriRajasthan.getBaseLocation());
							result.put("alert-"+(i+1)+".crewno", cachedEmriRajasthan.getCrewNo());
						} else{
							LOG.debug("District :"+"Not Available");
							result.put("alert-"+(i+1)+".baselocation","Not Available");
							result.put("alert-"+(i+1)+".crewno", 0);
						}
					} else {
						result.put("alert-"+(i+1)+".baselocation","Not Available");
						result.put("alert-"+(i+1)+".crewno", 0);
					}
					result.put("alert-"+(i+1)+".drivername", driverName);
					String actualTime = DateUtils.adjustToClientTime(localTimeZone, alertdata.getAlertTime());
					result.put("alert-"+(i+1)+".alerttime", actualTime);
					result.put("alert-"+(i+1)+".time", actualTime);
					result.put("alert-"+(i+1)+".alerttype", alertdata.getAlertType().toString());
					result.put("alert-"+(i+1)+".alertvalue", alertdata.getAlertTypeValue());
					result.put("alert-"+(i+1)+".alerttypevalue", alertdata.getAlertTypeValue());
					StringBuffer location=new StringBuffer();
					if(alertdata.getAlertLocationReferenceId() == 0){
						if(Boolean.valueOf(EnvironmentInfo.getProperty("IS_ADDRESS_FETCH_VIOLATIONS_ENABLED"))){
							Address address = GeoUtils.fetchNearestLocation(alertdata.getAlertLocation().getFirstPoint().y, 
									alertdata.getAlertLocation().getFirstPoint().x, false);
							location=StringUtils.formulateAddress(address, alertdata.getVehicleId(), 
									alertdata.getAlertLocation().getFirstPoint().y, alertdata.getAlertLocation().getFirstPoint().x);
							if(address != null){
								alertdata.setAlertLocationReferenceId(address.getId());
							}
							alertdata.setAlertLocationText(location.toString());
//							((AlertDaoImpl) DBManager.getInstance().getDao(DAOEnum.ALERT_DAO)).updateAlertLocation(alertdata);
						} else{
							location.append("Alert ");
							location.append(StringUtils.addressFetchDisabled(alertdata.getVehicleId(),
									alertdata.getAlertLocation().getFirstPoint().y,	alertdata.getAlertLocation().getFirstPoint().x).toString());
							alertdata.setAlertLocationText(location.toString());
						}
					}
					result.put("alert-"+(i+1)+".alertlocation", alertdata.getAlertLocationText());
					result.put("alert-"+(i+1)+".driver", driverName);
					result.put("alert-"+(i+1)+".occurdat", actualTime);
					result.put("alert-"+(i+1)+".comments", "");

				}
				finalresult.put("alert", result);

				((AlertDaoImpl) DBManager.getInstance().getDao(DAOEnum.ALERT_DAO)).updateAlertsNotifiedStatus(alertResultset); 
			}
		}
		return result;
	}

	@Override
	public String getName() {
		return "alertdata";
	}

	public IAlertManager getAlertManager() {
		return m_alertManager;
	}

	public void setAlertManager(IAlertManager alertManager) {
		m_alertManager = alertManager;
	}
}