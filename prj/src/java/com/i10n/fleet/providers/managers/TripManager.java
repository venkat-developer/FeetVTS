package com.i10n.fleet.providers.managers;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import com.i10n.db.dao.DAOEnum;
import com.i10n.db.dao.TripDetailsDaoImpl;
import com.i10n.db.entity.TripDetails;
import com.i10n.db.entity.User;
import com.i10n.db.entity.Vehicle;
import com.i10n.db.entity.primarykey.LongPrimaryKey;
import com.i10n.db.tools.DBManager;
import com.i10n.fleet.datasets.IDataset;
import com.i10n.fleet.datasets.impl.Dataset;
import com.i10n.fleet.web.request.RequestParameters;
import com.i10n.fleet.web.utils.SessionUtils;

public class TripManager implements ITripManager{

	
	/**
	 * For all the vehicles data based on status and is common for all livetrack page 
	 */
	@Override
	public IDataset getData(IDataset options,String groupId) {
		if(groupId == null){
			return parseDataSetForAllVehicles();
		}
		return parseDataSetForGroup(groupId);
	}
	/**
	 * Formulate data for livetrack page for selecetd group.(From dashboard).
	 * @param groupName
	 * @return
	 */
	private IDataset parseDataSetForGroup(String groupName) {
		User user = SessionUtils.getCurrentlyLoggedInUser();
		TripDetailsDaoImpl tripDetailsDaoImpl = (TripDetailsDaoImpl)DBManager.getInstance().getDao(DAOEnum.TRIP_DETAILS_DAO);
		List<TripDetails> tripDetailsList = tripDetailsDaoImpl.getActiveTripDetailsWithLiveStatusForTheUser(
				new LongPrimaryKey(user.getId()));
		List<TripDetails> procesedTripsList = new ArrayList<TripDetails>();
		for(TripDetails trip : tripDetailsList){
			Vehicle vehicle = trip.getVehicle();
			if(vehicle.getGroupName().equalsIgnoreCase(groupName)){
				procesedTripsList.add(trip);
			}
		}
		return getLivetrackData(procesedTripsList);
	}
	/**
	 * Formulate data for livetrack page for all vheicles.
	 * @return
	 */
	private IDataset parseDataSetForAllVehicles() {
		User user = SessionUtils.getCurrentlyLoggedInUser();
		TripDetailsDaoImpl tripDetailsDaoImpl = (TripDetailsDaoImpl)DBManager.getInstance().getDao(DAOEnum.TRIP_DETAILS_DAO);
		List<TripDetails> tripDetailsList = tripDetailsDaoImpl.getActiveTripDetailsWithLiveStatusForTheUser(
				new LongPrimaryKey(user.getId()));
		return getLivetrackData(tripDetailsList);
	}
	/**
	 * Formulating data for livetrack page. 
	 * @param tripDetailsList
	 * @return
	 */
	private IDataset getLivetrackData(List<TripDetails> tripDetailsList) {
		IDataset tripsData = new Dataset();
		if (tripDetailsList.size() != 0){
			Collections.sort(tripDetailsList, new Comparator<TripDetails>() {
				public int compare(TripDetails o1, TripDetails o2) {
					return o1.getVehicle().getDisplayName().compareTo(o2.getVehicle().getDisplayName());
				}});
			for(TripDetails tripDetails : tripDetailsList){
				IDataset map = tripDetails.toMap();

				if(tripsData.get("vehicles.groupsdata.group-" + tripDetails.getGroupValue() + ".vehicles") != null){
					IDataset dataset =  (IDataset)tripsData.get("vehicles.groupsdata.group-" + tripDetails.getGroupValue() + ".vehicles");
					dataset.put("" + map.get("id"), map);
				}else {
					IDataset dataset =  new Dataset();
					dataset.put("" + map.get("id"), map);
					tripsData.put("vehicles.groupsdata.group-" + tripDetails.getGroupValue() + ".vehicles", dataset);
					tripsData.put("vehicles.groupsdata.group-" + tripDetails.getGroupValue() +".id", "group-" + tripDetails.getGroupValue());
					tripsData.put("vehicles.groupsdata.group-" + tripDetails.getGroupValue() + ".name", tripDetails.getGroupName());
					tripsData.put("vehicles.groupsdata.group-" + tripDetails.getGroupValue() + ".username",SessionUtils.getCurrentlyLoggedInUser().getLogin());
				}
			}
		}
		return tripsData;
	}
	@Override
	public IDataset getData(IDataset options) {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public IDataset getData(RequestParameters params, IDataset options) {
		// TODO Auto-generated method stub
		return null;
	}
} 