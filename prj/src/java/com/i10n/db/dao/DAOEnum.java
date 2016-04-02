package com.i10n.db.dao;

public enum DAOEnum {
			
			// Basic 
			GROUP_DAO("groupDao"),
			GROUP_VALUES_DAO("groupValuesDao"), 
			USER_DAO("userDao"),
			HARDWARE_MODULES_DAO("hardwareModulesDao"),
			DRIVER_DAO("driverDao"), 
			VEHICLE_DAO("vehicleDao"),
			LIVE_VEHICLE_STATUS_DAO("liveVehicleStatusDao"), 
			ACL_VEHICLE_DAO("aclVehicleDao"),
			ACL_DRIVER_DAO("aclDriverDao"), 
			TRIP_DAO("tripDao"),
			TRACK_HISTORY_DAO("trackHistoryDao"),
			IDLE_POINTS_DAO("idlePointsDao"), 
			EMPLOYEES_DAO("employeesDao"), 
			TRIP_DETAILS_DAO("tripDetailsDao"), 
			VIOLATION_OVERSPEED_DAO("violationOverSpeedDao"), 
			VIOLATION_GEOFENCE_DAO("violationGeoFenceDao"),
			
			
			// Application usage log
			LOGS_DAO("logsDao"),
			VEHICLE_HISTORY_DAO("vehiclehistoryDao"),
			
			// Mail and Mobile alerts realted
			MAILINGLIST_REPORT_DAO("mailinglistReportDao"), 
			MAILINGLIST_ALERT_DAO("mailinglistAlertDao"), 
			MOBILENUMBER_DAO("mobileNumberDao"),
			ACL_REPORTS_DAO("aclreportsdao"),
			ACL_ALERTS_DAO("aclalertsdao"),
			ACL_MOBILE_DAO("aclmobiledao"),
			
			// SAS related 
			ROUTES_DAO("routesDao"),
			STOPS_DAO("stopsDao"),
			VEHICLE_TO_ROUTE_DAO("vehicleToRouteDao"),
			TIME_DEVIATIONS_DAO("timeDeviationDao"),
			ROUTE_DEVIATIONS_DAO("routeDeviationDao"),
			STOP_DEVIATIONS_DAO("stopDeviationDao"),
			TIME_DEVIATIONS("timeDeviationDao"),
			ROUTE_DEVIATIONS("routeDeviationDao"),
			STOP_DEVIATIONS("stopDeviationDao"),
			STOPHISTORY_DAO("stopHistoryDao"),
			VEHICLE_TO_STUDENT("vehicleToStudentDao"), 
			STUDENTS_DAO("studentsDao"),
			STUDENT_HISTORY_DAO("studentHistoryDao"),
			ETA_DISPLAY_DAO("etaDisplayDao"),
			ROUTE_TRACK_DAO("routeTrackDao"),
			TRIP_MISS_DEVIATIONS_DAO("tripMissDeviationDao"),
			ETA_ARYA_DAO("etaAryaDao"),
			VEHICLE_TO_ROUTE_SCHEDULE_DAO("vehicleToRouteScheduleDao"),
			ROUTE_SCHEDULE_DAO("routeScheduleDao"),
			LED_TO_BUS_STOP_DAO("ledToBusStopDao"),
			
			// Un used
			LATEST_PACKET_DETAILS_DAO("latestPacketDetailsDao"),
			DIGITAL_EVENT_DAO("digitaleventDao"), 
			DIGITAL_EVENTOCCUR_DAO("digitaleventoccurDao"), 
			SPATIAL_REF_SYS_DAO("spatialRefSysDao"), 
			
			VEHICLE_TO_BUS_DAO("vehiclebusDao"), 
			SMS_PROVIDERS_DAO("smsProvidersDao"), 
			PROVIDERS_DETAILS_DAO("providersDao"),
			USER_FEATURE_LIST_DAO("userFeatureListDao"), 
			VEHICLE_CREATION_DAO("vehicleCreationDao"), 
			OVERRIDE_FUEL_CALIBRATION_DAO("overrideFuelDao"), 
			VEHICLE_CREATION_AND_STATUS_INFO_DAO("vehiclecreationandstatusinfoDao"), 
			TRACKHISTORY_BULK_DATA_DAO("trackhistorybulkdataDao"), 
			BUS_ROUTES_DAO("busRoutesDao"), 
			BUS_STOPS_DAO("busStopDao"), 
			FUEL_CALIBRATION_DETAILS_DAO("fuelcalibrationdetailsDao"), 
			FUEL_CALIBRATION_VALUES_DAO("fuelcalibrationvaluesDao"), 
//			VEHICLE_HISTORY_DAO("vehiclehistoryDao"), 
			DRIVER_CREATION_AND_STATUS_INFO_DAO("drivercreationandstatusinfoDao"), 
			DRIVER_HISTORY_DAO("driverhistoryDao"), 
			HARDWARE_MODULE_HISTORY_DAO("hardwaremodulehistoryDao"), 
			USER_CREDIT_DETAILS_DAO("usercreditdetailsDao"), 
			ALERT_DAO("alertDao"),
			ADDRESS_DAO("addressDao"),
			USER_HISTORY_DAO("userhistoryDao"),
			IMEI_SEQUENCE_DAO("imeisequencedao"),
			GWTRACK_MODULEDATA_DAO("GWTrackModuledatabeandao"),
			GT_LATEST_PACKET_DETAIL_DAO("gtlatestpacketdetailDao"),
			AC_LATEST_PACKET_DETAIL_DAO("AclatestpacketdetailDao") ,
			IMEI_SEQNO("imeiseqnoDao"),
			
			// GeoFencing related
			GEO_FENCE_REGIONS_DAO("geoFenceRegionsDao"),
			VEHICLE_GEOFENCE_REGIONS_DAO("vehicleGeofenceRegionsDao"),
			
			// To be scraped 
			ETD_OF_VEHICLESTOSTOPS("etdofvehiclestostopsDao"),
			//Emri Rajasthan
			EMRI_RAJASTHAN_DAO("emrirajasthanDao")
			;
	private String val;

	DAOEnum(String value) {
		this.val = value;
	}

	public String getValue() {
		return val;
	}
}
