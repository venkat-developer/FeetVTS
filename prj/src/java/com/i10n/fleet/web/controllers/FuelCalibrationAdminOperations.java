package com.i10n.fleet.web.controllers;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import com.i10n.db.dao.DAOEnum;
import com.i10n.db.dao.FuelCalibrationDetailsDaoImp;
import com.i10n.db.dao.FuelCalibrationValuesDaoImp;
import com.i10n.db.dao.HardwareModuleDaoImp;
import com.i10n.db.dao.TripDaoImp;
import com.i10n.db.dao.VehicleDaoImpl;
import com.i10n.db.entity.FuelCalibrationDetails;
import com.i10n.db.entity.FuelCalibrationValues;
import com.i10n.db.entity.primarykey.LongPrimaryKey;
import com.i10n.db.tools.DBManager;
import com.i10n.fleet.exceptions.OperationNotSupportedException;
import com.i10n.fleet.web.utils.SessionUtils;

public class FuelCalibrationAdminOperations {
	
//	private static Logger LOG = Logger.getLogger(FuelCalibrationAdminOperations.class);

	public boolean addFuelValues(HttpServletRequest request){
		boolean opSuccess=true;
		long id=Long.parseLong(request.getParameter("hardwareid"));
		List<Integer> fuelList = new ArrayList<Integer>();
		List<Integer> adList = new ArrayList<Integer>();
		List<FuelCalibrationDetails> lastrow;
		LongPrimaryKey lastid = null;
		int samplenumbers=Integer.parseInt(request.getParameter("numberofsamples"));
		for(int i=1;i<=samplenumbers;i++){
			fuelList.add(Integer.parseInt(request.getParameter("fuel"+i)));
			adList.add(Integer.parseInt(request.getParameter("ad"+i)));
		}
		Long tripId = ((TripDaoImp)DBManager.getInstance().getDao(DAOEnum.TRIP_DAO)).getTripidByImeiid(id);		
		FuelCalibrationDetails fueldetails=new FuelCalibrationDetails(SessionUtils.getCurrentlyLoggedInUser().getId(), 1, 1024);
		try {
			fueldetails=((FuelCalibrationDetailsDaoImp)DBManager.getInstance().getDao(DAOEnum.FUEL_CALIBRATION_DETAILS_DAO)).insert(fueldetails);
			lastrow=((FuelCalibrationDetailsDaoImp)DBManager.getInstance().getDao(DAOEnum.FUEL_CALIBRATION_DETAILS_DAO)).getLastRow();
			if(!lastrow.isEmpty()){
				lastid =  lastrow.get(0).getId();
			}
			double gradient = 0.0;
			for(int j=0;j<samplenumbers;j++){
				FuelCalibrationValues fuelcalvalues=new FuelCalibrationValues(); 	
				gradient=0.0;
				int advalue=adList.get(j);
				int fuelvalue=fuelList.get(j);
				fuelcalvalues.setCalibrationid(lastid);
				fuelcalvalues.setAdvalue(advalue);
				fuelcalvalues.setFuelinliters(fuelvalue);
				fuelcalvalues.setBaseAd(adList.get((j == 0)?j:(j - 1)));
				fuelcalvalues.setBaseFuel(fuelList.get((j == 0)?j:(j - 1)));
				if(j==0 || (advalue-(Integer.parseInt(request.getParameter("ad"+j))))==0){
					gradient=0.0;
				}else{
					gradient = ((double)(fuelList.get(j) - fuelList.get(j - 1)))/((double)(adList.get(j) - adList.get(j - 1)));
				}
				fuelcalvalues.setGradient(gradient);
				fuelcalvalues.setTripId(tripId);
				fuelcalvalues=((FuelCalibrationValuesDaoImp)DBManager.getInstance().getDao(DAOEnum.FUEL_CALIBRATION_VALUES_DAO)).insert(fuelcalvalues);
				((HardwareModuleDaoImp) DBManager.getInstance().getDao(DAOEnum.HARDWARE_MODULES_DAO)).addfuelcal(id);
				((VehicleDaoImpl) DBManager.getInstance().getDao(DAOEnum.VEHICLE_DAO)).updateFuelCalId(tripId,id);
			}
		} catch (OperationNotSupportedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return opSuccess;
	}

	public boolean deleteFuelValues(HttpServletRequest request){
		long id=Long.parseLong(request.getParameter("hardwareid"));
		long tripId = ((TripDaoImp)DBManager.getInstance().getDao(DAOEnum.TRIP_DAO)).getTripidByImeiid(id);	
		long calids=((FuelCalibrationValuesDaoImp)DBManager.getInstance().getDao(DAOEnum.FUEL_CALIBRATION_VALUES_DAO)).getCalId(tripId);
		try {
			((FuelCalibrationValuesDaoImp)DBManager.getInstance().getDao(DAOEnum.FUEL_CALIBRATION_VALUES_DAO)).deleteCalibrated(calids);
			((FuelCalibrationDetailsDaoImp)DBManager.getInstance().getDao(DAOEnum.FUEL_CALIBRATION_DETAILS_DAO)).deleteCalibrated(calids);
			((HardwareModuleDaoImp) DBManager.getInstance().getDao(DAOEnum.HARDWARE_MODULES_DAO)).deletefuelcal(id);
		} catch (OperationNotSupportedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return true;
	}
}