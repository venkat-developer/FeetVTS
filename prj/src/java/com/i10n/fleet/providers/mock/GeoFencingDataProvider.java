package com.i10n.fleet.providers.mock;

import java.util.ArrayList;
import java.util.List;

import org.postgis.LinearRing;
import org.postgis.Point;
import org.postgis.Polygon;

import com.i10n.db.dao.DAOEnum;
import com.i10n.db.dao.GeoFenceRegionsDaoImp;
import com.i10n.db.dao.VehicleDaoImpl;
import com.i10n.db.entity.GeoFenceRegions;
import com.i10n.db.entity.Vehicle;
import com.i10n.db.entity.primarykey.LongPrimaryKey;
import com.i10n.db.tools.DBManager;
import com.i10n.fleet.datasets.IDataset;
import com.i10n.fleet.datasets.impl.Dataset;
import com.i10n.fleet.providers.impl.IDataProvider;
import com.i10n.fleet.util.StringUtils;
import com.i10n.fleet.web.request.RequestParameters;

/**
 * Mock : Data Provider for Trip Settings. This class will be removed in future.
 * 
 * @author Sabarish
 * 
 */
public class GeoFencingDataProvider implements IDataProvider {

	/**
	 * @see IDataProvider#getDataset(RequestParameters)
	 */
	public IDataset getDataset(RequestParameters params) {
		IDataset result = new Dataset();
		IDataset geoFencingDataset = null;
		IDataset vacantVehicleData = null;
		IDataset assignedVehicleData = null;
		String regionId = params.getRequestParameter("regionID");
		String regionName = params.getRequestParameter("regionName");
		String dataView = params.getRequestParameter("dataView");

		if (regionName != null) {
			geoFencingDataset = new Dataset();

			List<GeoFenceRegions> selectedResultset = ((GeoFenceRegionsDaoImp) DBManager.getInstance().getDao(DAOEnum.GEO_FENCE_REGIONS_DAO)).selectByRegionName(regionName);
			GeoFenceRegions geofenceregionSelected = selectedResultset.get(0);

			geoFencingDataset.put("geofencingregion1" + ".id",geofenceregionSelected.getId().getId());
			geoFencingDataset.put("geofencingregion1" + ".name",geofenceregionSelected.getRegionName());
			geoFencingDataset.put("geofencingregion1" + ".vehiclemake", "");
			geoFencingDataset.put("geofencingregion1" + ".vehiclemodel", "");
			geoFencingDataset.put("geofencingregion1" + ".drivername", "");
			geoFencingDataset.put("geofencingregion1" + ".driverlastname", "");
			geoFencingDataset.put("geofencingregion1" + ".startdate", "");
			geoFencingDataset.put("geofencingregion1" + ".location", "");
			geoFencingDataset.put("geofencingregion1" + ".lastupdated", "");
			geoFencingDataset.put("geofencingregion1" + ".distance", "");
			geoFencingDataset.put("geofencingregion1" + ".status", "");
			geoFencingDataset.put("geofencingregion1" + ".maxspeed", "");
			geoFencingDataset.put("geofencingregion1" + ".idlepointlimit", "");
			result.put("regions", geoFencingDataset);
			return result;
		}

		if (dataView == null || dataView.isEmpty()) {
			if (regionId != null) {
				geoFencingDataset = new Dataset();
				regionId = StringUtils.getValueFromKVP(regionId);
				Long regionID = Long.parseLong(StringUtils.stripCommas(regionId));
				List<GeoFenceRegions> selectedResultset = ((GeoFenceRegionsDaoImp) DBManager.getInstance().getDao(DAOEnum.GEO_FENCE_REGIONS_DAO)).selectByPrimaryKey(new LongPrimaryKey(regionID));
				GeoFenceRegions geofenceregionSelected = selectedResultset.get(0);

				geoFencingDataset.put("geofencing-region-"+ geofenceregionSelected.getId().getId() + ".id",geofenceregionSelected.getId().getId());
				geoFencingDataset.put("geofencing-region-"+ geofenceregionSelected.getId().getId() + ".name",geofenceregionSelected.getRegionName());
				geoFencingDataset.put("geofencing-region-"+ geofenceregionSelected.getId().getId()+ ".vehiclemake", "");
				geoFencingDataset.put("geofencing-region-"+ geofenceregionSelected.getId().getId()+ ".vehiclemodel", "");
				geoFencingDataset.put("geofencing-region-"+ geofenceregionSelected.getId().getId()+ ".drivername", "");
				geoFencingDataset.put("geofencing-region-"+ geofenceregionSelected.getId().getId()+ ".driverlastname", "");
				geoFencingDataset.put("geofencing-region-"+ geofenceregionSelected.getId().getId()+ ".startdate", "");
				geoFencingDataset.put("geofencing-region-"+ geofenceregionSelected.getId().getId() + ".location","");
				geoFencingDataset.put("geofencing-region-"+ geofenceregionSelected.getId().getId()+ ".lastupdated", "");
				geoFencingDataset.put("geofencing-region-"+ geofenceregionSelected.getId().getId() + ".distance","");
				geoFencingDataset.put("geofencing-region-"+ geofenceregionSelected.getId().getId() + ".status","");
				geoFencingDataset.put("geofencing-region-"+ geofenceregionSelected.getId().getId() + ".maxspeed","");
				geoFencingDataset.put("geofencing-region-"+ geofenceregionSelected.getId().getId()+ ".idlepointlimit", "");
				geoFencingDataset.put("geofencing-region-"+ geofenceregionSelected.getId().getId() + ".shape",geofenceregionSelected.getShape() + "");
				List<Point> pointsList = new ArrayList<Point>();

				if (geofenceregionSelected.getPolygon().getTypeString().equals("POLYGON")) {
					Polygon polygon = (Polygon) geofenceregionSelected.getPolygon();

					if (null != polygon) {
						LinearRing ring = polygon.getRing(0);
						if (null != ring) {
							Point[] points = ring.getPoints();
							for(int point=0;point<points.length;point++){
								points[point].x = points[point].x / 100000.0;
								points[point].y = points[point].y / 100000.0;
							}
							for (int i = 0; i < points.length; i++)
								pointsList.add(points[i]);
						}
					}
				} else if (geofenceregionSelected.getPolygon().getTypeString().equals("POINT")) {
					Point p = (Point) geofenceregionSelected.getPolygon();
					pointsList.add(p);
				}
				geoFencingDataset.put("geofencing-region-"+ geofenceregionSelected.getId().getId() + ".point",pointsList);
				result.put("regions", geoFencingDataset);
			} else {
				geoFencingDataset = new Dataset();
				List<GeoFenceRegions> resultset = ((GeoFenceRegionsDaoImp) DBManager.getInstance().getDao(DAOEnum.GEO_FENCE_REGIONS_DAO)).selectAllOwned();
				GeoFenceRegions geofenceregion = null;
				if (resultset != null) {
					for (int i = 0; i < resultset.size(); i++) {
						geofenceregion = resultset.get(i);

						geoFencingDataset.put("geofencing-region-"+ geofenceregion.getId().getId() + ".id",geofenceregion.getId().getId() + "");
						geoFencingDataset.put("geofencing-region-"+ geofenceregion.getId().getId() + ".name",geofenceregion.getRegionName());
						geoFencingDataset.put("geofencing-region-"+ geofenceregion.getId().getId()+ ".vehiclemake", "");
						geoFencingDataset.put("geofencing-region-"+ geofenceregion.getId().getId()+ ".vehiclemodel", "");
						geoFencingDataset.put("geofencing-region-"+ geofenceregion.getId().getId()+ ".drivername", "");
						geoFencingDataset.put("geofencing-region-"+ geofenceregion.getId().getId()+ ".driverlastname", "");
						geoFencingDataset.put("geofencing-region-"+ geofenceregion.getId().getId()+ ".startdate", "");
						geoFencingDataset.put("geofencing-region-"+ geofenceregion.getId().getId() + ".location","");
						geoFencingDataset.put("geofencing-region-"+ geofenceregion.getId().getId()+ ".lastupdated", "");
						geoFencingDataset.put("geofencing-region-"+ geofenceregion.getId().getId() + ".distance","");
						geoFencingDataset.put("geofencing-region-"+ geofenceregion.getId().getId() + ".status","");
						geoFencingDataset.put("geofencing-region-"+ geofenceregion.getId().getId() + ".maxspeed","");
						geoFencingDataset.put("geofencing-region-"+ geofenceregion.getId().getId()+ ".idlepointlimit", "");
						geoFencingDataset.put("geofencing-region-"+ geofenceregion.getId().getId() + ".shape",geofenceregion.getShape() + "");

						List<Point> pointsList = new ArrayList<Point>();
						if (geofenceregion.getPolygon().getTypeString().equals("POLYGON")) {
							Polygon polygon = (Polygon) geofenceregion.getPolygon();
							if (null != polygon) {
								LinearRing ring = polygon.getRing(0);
								if (null != ring) {
									Point[] points = ring.getPoints();
									for(int point=0;point<points.length;point++){
										points[point].x = points[point].x / 100000.0;
										points[point].y = points[point].y / 100000.0;
									}
									for (int j = 0; j < points.length; j++)
										pointsList.add(points[j]);
								}
							}
						} else if (geofenceregion.getPolygon().getTypeString().equals("POINT")) {
							Point p = (Point) geofenceregion.getPolygon();
							pointsList.add(p);
						}
						geoFencingDataset.put("geofencing-region-"+ geofenceregion.getId().getId() + ".point",pointsList);
					}
				}
				result.put("regions", geoFencingDataset);
			}
		} else if (dataView.equalsIgnoreCase("assignment")) {
			regionId = StringUtils.getValueFromKVP(regionId);
			Long regionID = Long.parseLong(StringUtils.stripCommas(regionId));

			List<Vehicle> vacantVehicleResultset = ((VehicleDaoImpl) DBManager.getInstance().getDao(DAOEnum.VEHICLE_DAO)).getGeoFencingVacantVehicles(regionID);

			Vehicle vehicle = null;
			vacantVehicleData = new Dataset();
			if (vacantVehicleResultset != null) {
				for (int j = 0; j < vacantVehicleResultset.size(); j++) {
					vehicle = vacantVehicleResultset.get(j);
					
					vacantVehicleData.put("vehicle-" + vehicle.getId().getId()+ ".id", "vehicle-" + vehicle.getId().getId());
					vacantVehicleData.put("vehicle-" + vehicle.getId().getId()+ ".name", vehicle.getDisplayName());
					vacantVehicleData.put("vehicle-" + vehicle.getId().getId()+ ".make", vehicle.getMake());
					vacantVehicleData.put("vehicle-" + vehicle.getId().getId()+ ".model", vehicle.getModel());
					vacantVehicleData.put("vehicle-" + vehicle.getId().getId()+ ".year", vehicle.getModelYear());
					vacantVehicleData.put("vehicle-" + vehicle.getId().getId()+ ".imei", vehicle.getImeiId());
				}
			}
			result.put("vehicle.vacant", vacantVehicleData);

			List<Vehicle> assignedDriverResultset = ((VehicleDaoImpl) DBManager.getInstance().getDao(DAOEnum.VEHICLE_DAO)).getGeoFencingAssignedVehicles(regionID);

			Vehicle aVehicle = null;
			assignedVehicleData = new Dataset();
			if (assignedDriverResultset != null) {
				for (int j = 0; j < assignedDriverResultset.size(); j++) {
					aVehicle = assignedDriverResultset.get(j);
					
					assignedVehicleData.put("vehicle-"+ aVehicle.getId().getId() + ".id", aVehicle.getId().getId()+ "");
					assignedVehicleData.put("vehicle-"+ aVehicle.getId().getId() + ".name", aVehicle.getDisplayName());
					assignedVehicleData.put("vehicle-"+ aVehicle.getId().getId() + ".make", aVehicle.getMake());
					assignedVehicleData.put("vehicle-"+ aVehicle.getId().getId() + ".model", aVehicle.getModel());
					assignedVehicleData.put("vehicle-"+ aVehicle.getId().getId() + ".year", aVehicle.getModelYear());
					assignedVehicleData.put("vehicle-"+ aVehicle.getId().getId() + ".imei", aVehicle.getImeiId());
				}
			}
			result.put("vehicle.assigned", assignedVehicleData);
		}
		return result;
	}

	/**
	 * @see IDataProvider#getName()
	 */
	public String getName() {
		return "geofencing";
	}

}