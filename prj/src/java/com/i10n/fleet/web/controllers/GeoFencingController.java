package com.i10n.fleet.web.controllers;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.postgis.Geometry;
import org.postgis.Point;
import org.springframework.validation.BindException;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.SimpleFormController;
import org.springframework.web.servlet.view.RedirectView;

import com.i10n.db.dao.DAOEnum;
import com.i10n.db.dao.GeoFenceRegionsDaoImp;
import com.i10n.db.dao.VehicleGeofenceRegionsDaoImp;
import com.i10n.db.entity.GeoFenceRegions;
import com.i10n.db.entity.User;
import com.i10n.db.entity.VehicleGeofenceRegions;
import com.i10n.db.entity.primarykey.LongPrimaryKey;
import com.i10n.db.tools.DBManager;
import com.i10n.fleet.exceptions.OperationNotSupportedException;
import com.i10n.fleet.util.StringUtils;
import com.i10n.fleet.web.utils.SessionUtils;

/*
 * @author dharmaraju.v
 *
 */

public class GeoFencingController extends SimpleFormController {

	public enum GeoFencing {
		GEO_FENCING_ADD("geo_fencing_add"),GEO_FENCING_DELETE("geo_fencing_delete"),GEO_FENCING_SAVE("geo_fencing_save"),
		GEO_FENCING_EDIT("geo_fencing_edit");

		private static Map<String, GeoFencing> lookup = new HashMap<String, GeoFencing>();
		private String val;

		private GeoFencing(String val) {
			this.val = val;
		}

		public String getVal() {
			return val;
		}

		public static GeoFencing get(String val) {
			return lookup.get(val);
		}

		static {
			for (GeoFencing s : EnumSet.allOf(GeoFencing.class)) {
				lookup.put(s.getVal(), s);
			}
		}
	}

	/**
	 * Handles login/logout requests and checks redirects to appproaprate view.
	 * If its a logout request, it removes the session attribute and redirects
	 * to login page. If a login request handling is passed to
	 * {@link #onSubmit(Object)} which on success returns a {@link RedirectView}
	 * to {@link #getSuccessView()} page and then finally adds the currents user
	 * as a session attribute. This will be reimplemented once the user database
	 * is available.
	 *
	 * @see SimpleFormController#handleRequest(HttpServletRequest,
	 *      HttpServletResponse)
	 */
	@Override
	public ModelAndView handleRequestInternal(HttpServletRequest request, HttpServletResponse response) throws Exception {
		ModelAndView modelAndView = processRequest(request);
		return modelAndView;
	}

	/**
	 * Handles the login submit requests. It will validate if the user/password
	 * are matching and on success will return a {@link RedirectView} to
	 * {@link #getSuccessView()} page.
	 *
	 */
	@Override
	protected ModelAndView onSubmit(HttpServletRequest request, HttpServletResponse response, Object command, BindException errors)
	throws Exception {
		ModelAndView modelAndView = processRequest(request);
		return modelAndView;
	}

	private ModelAndView processRequest(HttpServletRequest request) {
		ModelAndView modelAndView = null;
		String operation = request.getParameter("command_type");
		String regionname = null,regionid = null,regionShape = null;

		switch (GeoFencing.get(operation)) {
		case GEO_FENCING_ADD: {
			regionname = request.getParameter("regionName");
			regionShape = request.getParameter("region-shape");
			if(regionname ==null){
				String regionName = request.getParameter("region-name");
				String speedLimit = request.getParameter("speed-limit");
//				String polygon = request.getParameter("polygon");

				double speed = Integer.parseInt(speedLimit);
				int shape=0;
				if(regionShape.equalsIgnoreCase("circle")){
					shape=0;
				}else if(regionShape.equalsIgnoreCase("square")){
					shape=1;
				}else if(regionShape.equalsIgnoreCase("custom")){
					shape=2;
				}

				User user = SessionUtils.getCurrentlyLoggedInUser();
				long userId = user.getId();
				Point p=new org.postgis.Point(25.2712232, 55.428641);
				Geometry location = (Geometry)p;
				GeoFenceRegions entity = new GeoFenceRegions(new LongPrimaryKey(0L), 
						location, speed, regionName, userId, shape);
				try {
					entity = ((GeoFenceRegionsDaoImp)DBManager.getInstance().getDao(DAOEnum.GEO_FENCE_REGIONS_DAO)).insert(entity);
				} catch (OperationNotSupportedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			else if(request.getParameter("arrayOfLatLngs") != null){
				String arraylatlng = request.getParameter("arrayOfLatLngs");
				String[] array1 = arraylatlng.split(",");
				double x[] = new double[(array1.length)/2];
				double y[] = new double[(array1.length)/2];
				for(int i=0;i<array1.length-1;i++){
					array1[i] = array1[i].replace("(", " ").trim();
					array1[i+1] = array1[i+1].replace(")", " ").trim();
				}
				for(int i=0,j=0,k=0;i<array1.length;i++){
					if(i%2 == 0){
						x[j] = Double.parseDouble(array1[i]);
						j++;
					}
					else{
						y[k] = Double.parseDouble(array1[i]);
						k++;
					}
				}
				List<Point> pointsList = new ArrayList<Point>();
				Point[] points = new Point[x.length];
				for (int i = 0; i < points.length; i++) {
					points[i] = new Point();
					points[i].x = Double.valueOf(x[i]);
					points[i].y = Double.valueOf(y[i]);
					pointsList.add(points[i]);
				}

				List<GeoFenceRegions> regionResultSet = ((GeoFenceRegionsDaoImp)DBManager.getInstance().getDao(DAOEnum.GEO_FENCE_REGIONS_DAO)).selectByRegionName(regionname);
				GeoFenceRegions region = regionResultSet.get(0);
				try {
					region = ((GeoFenceRegionsDaoImp)DBManager.getInstance().getDao(DAOEnum.GEO_FENCE_REGIONS_DAO)).
					updateRegion(region, points);
				} catch (OperationNotSupportedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		break;

		case GEO_FENCING_EDIT: {
			String regionIdString=request.getParameter("regionId");
			String regionIdArray[]=regionIdString.split("-");
			regionid=StringUtils.stripCommas(regionIdArray[2]);
			String regId = StringUtils.stripCommas(regionid.trim());
			Long  regionID= Long.parseLong(regId);
			String assignedvehlist = request.getParameter("assignedVehicles");
			String vacantvehlist = request.getParameter("vacantVehicles");
			String[] assinedvehs = assignedvehlist.trim().split(",");
			String[] vacantvehs = vacantvehlist.trim().split(",");
			if (assinedvehs != null || vacantvehs != null) {
				for (int i = 0; i < assinedvehs.length; i++) {
					Long avehicles = null;
					if (!assinedvehs[i].equals("") || assinedvehs[i].length() != 0) {
						avehicles = ((VehicleGeofenceRegionsDaoImp) DBManager.getInstance().getDao(DAOEnum.VEHICLE_GEOFENCE_REGIONS_DAO)).
								getVehicleIDFromItsName(assinedvehs[i].trim());
					} else {
						continue;
					}
					VehicleGeofenceRegions Aclreg = new VehicleGeofenceRegions(avehicles, regionID,true);//to be changed
					try {
						Aclreg = ((VehicleGeofenceRegionsDaoImp) DBManager.getInstance().getDao(DAOEnum.VEHICLE_GEOFENCE_REGIONS_DAO)).insert(Aclreg);
					} catch (OperationNotSupportedException e) {
						e.printStackTrace();
					}
				}
				for (int i = 0; i < vacantvehs.length; i++) {
					Long vvehs = null;
					if (!vacantvehs[i].equals("") || vacantvehs[i].length() != 0) {
						vvehs = ((VehicleGeofenceRegionsDaoImp) DBManager.getInstance().getDao(DAOEnum.VEHICLE_GEOFENCE_REGIONS_DAO)).getVehicleIDFromItsName(vacantvehs[i].trim());
					} else {
						continue;
					}
					VehicleGeofenceRegions Aclreg = new VehicleGeofenceRegions(vvehs, regionID);
					try {
						Aclreg = ((VehicleGeofenceRegionsDaoImp) DBManager.getInstance().getDao(DAOEnum.VEHICLE_GEOFENCE_REGIONS_DAO)).delete(Aclreg);
					} catch (OperationNotSupportedException e) {
						e.printStackTrace();
					}
				}	
			}
		} 
		break;

		case GEO_FENCING_DELETE: {
			String regionIdString=request.getParameter("regionID");
			String regionIdArray[]=regionIdString.split("-");
			regionid=StringUtils.stripCommas(regionIdArray[2]);
			Long regionId= Long.parseLong(regionid);
			GeoFenceRegions regions = new GeoFenceRegions();
			regions.setId(new LongPrimaryKey(regionId));
			try {
				regions=((GeoFenceRegionsDaoImp)DBManager.getInstance().getDao(DAOEnum.GEO_FENCE_REGIONS_DAO)).delete(regions);
			} catch (OperationNotSupportedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		break;

		case GEO_FENCING_SAVE: {
			regionid = request.getParameter("regionId");
			String[] IdArray = regionid.split("-");
			Long regId = Long.parseLong(IdArray[2]);
			String arraylatlng = request.getParameter("arrayOfLatLngs");
			String[] array1 = arraylatlng.split(",");
			double x[] = new double[(array1.length)/2];
			double y[] = new double[(array1.length)/2];
			for(int i=0;i<array1.length-1;i++){
				array1[i] = array1[i].replace("(", " ").trim();
				array1[i+1] = array1[i+1].replace(")", " ").trim();
			}
			for(int i=0,j=0,k=0;i<array1.length;i++){
				if(i%2 == 0){
					x[j] = Double.parseDouble(array1[i]);
					j++;
				}else{
					y[k] = Double.parseDouble(array1[i]);
					k++;
				}
			}
			List<Point> pointsList = new ArrayList<Point>();
			Point[] points = new Point[x.length];
			for (int i = 0; i < points.length; i++) {
				points[i] = new Point();
				points[i].x = Double.valueOf(x[i]);
				points[i].y = Double.valueOf(y[i]);
				pointsList.add(points[i]);
			}
			List<GeoFenceRegions> regionResultSet = ((GeoFenceRegionsDaoImp)DBManager.getInstance().
					getDao(DAOEnum.GEO_FENCE_REGIONS_DAO)).selectByPrimaryKey(new LongPrimaryKey(regId));
			GeoFenceRegions region = regionResultSet.get(0);

			try {
				region = ((GeoFenceRegionsDaoImp)DBManager.getInstance().getDao(DAOEnum.GEO_FENCE_REGIONS_DAO)).updateRegion(region, points);
			} catch (OperationNotSupportedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		}   
		return modelAndView;
	}
}