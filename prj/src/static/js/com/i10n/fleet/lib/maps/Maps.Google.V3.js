(function() {
	var $GM = (google && google.maps) ? google.maps : null;
	var $YU = YAHOO.util;
	var $D = YAHOO.util.Dom;
	var $L = YAHOO.lang;
	var $E = YAHOO.util.Event;
	var $YW = YAHOO.widget;
	var $W = getPackageForName("com.i10n.fleet.widget.lib");
	var $U = getPackageForName("com.i10n.fleet.Utils");
	var i=0;
	var pointerArray = new Array();
	var markerArray = [];
	var directionsDisplay;
	var allMapMarkers=[];
	var isDistanceEnabled=false;
	// Need to set true for SHSM Client.
	var isSHSMClient=false;
	var isKMLRequestSent=false;

	/**
	 * An Adapter Widget for Other Widgets to interact with Google Map API.
	 * @param {Object} el
	 * @param {Object} oArgs
	 * @author sabarish
	 */
	$W.Maps = function(el, oArgs) {
		if (!$L.isNull($GM)) {
			this.initFleetMap(el, oArgs);
		}
	};

	$L.augmentObject($W.Maps, {

		"ATTR_MAP" : "map",
		//		"DEFAULT_LATLNG" : new GLatLng(23.259849,77.412672),		// MP-Bhopal
		"DEFAULT_LATLNG" : new $GM.LatLng(12.971599, 77.594563),	// Karnataka-Bengaluru
		//		"DEFAULT_LATLNG" : new $GM.LatLng(28.6469655,77.0932634),	// Delhi
		//		"DEFAULT_LATLNG" : new $GM.LatLng(26.91235, 75.787333),		// Rajasthan-Jaipur
		"MARKER_SPRITE_CONFIG": {
		"vehicles": {
		"idle": new $GM.MarkerImage( '/static/img/vehicles/blurb/idle.png',
				new $GM.Size(20, 35),
				new $GM.Point(0, 0),
				new $GM.Point(20, 25)
				),
				"idlemapreport": new $GM.MarkerImage( '/static/img/maps/pin-orange.png',
						new $GM.Size(20, 25),
						new $GM.Point(0, 0),
						new $GM.Point(12, 25)
						),
						"online": new $GM.MarkerImage( '/static/img/vehicles/blurb/blurb.png',
								new $GM.Size(20, 35),
								new $GM.Point(0, 0),
								new $GM.Point(10, 35)
								),
								"online0": new $GM.MarkerImage( '/static/img/vehicles/blurb/0360.png',
										new $GM.Size(20, 35),
										new $GM.Point(0, 0),
										new $GM.Point(10, 35)
										),
										"online45": new $GM.MarkerImage( '/static/img/vehicles/blurb/45.png',
												new $GM.Size(20, 35),
												new $GM.Point(0, 0),
												new $GM.Point(10, 35)
												),
												"online90": new $GM.MarkerImage( '/static/img/vehicles/blurb/90.png',
														new $GM.Size(20, 35),
														new $GM.Point(0, 0),
														new $GM.Point(10, 35)
														),
														"online135": new $GM.MarkerImage( '/static/img/vehicles/blurb/135.png',
																new $GM.Size(20, 35),
																new $GM.Point(0, 0),
																new $GM.Point(10, 35)
																),
																"online180": new $GM.MarkerImage( '/static/img/vehicles/blurb/180.png',
																		new $GM.Size(20, 35),
																		new $GM.Point(0, 0),
																		new $GM.Point(10, 35)
																		),
																		"online225": new $GM.MarkerImage( '/static/img/vehicles/blurb/225.png',
																				new $GM.Size(20, 35),
																				new $GM.Point(0, 0),
																				new $GM.Point(10, 35)
																				),
																				"online270": new $GM.MarkerImage( '/static/img/vehicles/blurb/270.png',
																						new $GM.Size(20, 35),
																						new $GM.Point(0, 0),
																						new $GM.Point(10, 35)
																						),
																						"online315": new $GM.MarkerImage( '/static/img/vehicles/blurb/315.png',
																								new $GM.Size(20, 35),
																								new $GM.Point(0, 0),
																								new $GM.Point(10, 35)
																								),                
																								"onlinestart": new $GM.MarkerImage( '/static/img/maps/From.png',
																										new $GM.Size(45, 45),
																										new $GM.Point(2, 2),
																										new $GM.Point(23, 25)
																										),
																										"onlineend": new $GM.MarkerImage( '/static/img/maps/To.png',
																												new $GM.Size(45, 45),
																												new $GM.Point(2, 2),
																												new $GM.Point(23, 25)
																												),
																												"trackpoint":new $GM.MarkerImage( '/static/img/buttons/bullet.png',
																														new $GM.Size(7, 7),
																														new $GM.Point(0, 0),
																														new $GM.Point(3, 5)
																														),
																														"offline": new $GM.MarkerImage( '/static/img/vehicles/blurb/offline.png',
																																new $GM.Size(20, 35),
																																new $GM.Point(0, 0),
																																new $GM.Point(10, 35)
																																),
																																"stopped": new $GM.MarkerImage( '/static/img/vehicles/blurb/stopped.png',
																																		new $GM.Size(20, 35),
																																		new $GM.Point(0, 0),
																																		new $GM.Point(10, 35)

																																		),
																																		"offroad": new $GM.MarkerImage( '/static/img/vehicles/blurb/offline.png',
																																				new $GM.Size(20, 35),
																																				new $GM.Point(0, 0),
																																				new $GM.Point(10, 35)
																																				)
	},

	"trackpoint" : {
		"trackpoint":new $GM.MarkerImage( '/static/img/buttons/bullet.png',
				new $GM.Size(7, 7),
				new $GM.Point(0, 0),
				new $GM.Point(3, 5)
				),
				"trackpoint0":new $GM.MarkerImage( '/static/img/trackpoint/trackpoint0.png',
						new $GM.Size(7, 7),
						new $GM.Point(0, 0),
						new $GM.Point(3, 5)
						),
						"trackpoint45":new $GM.MarkerImage( '/static/img/trackpoint/trackpoint45.png',
								new $GM.Size(7, 7),
								new $GM.Point(0, 0),
								new $GM.Point(3, 5)
								),
								"trackpoint90":new $GM.MarkerImage( '/static/img/trackpoint/trackpoint90.png',
										new $GM.Size(7, 7),
										new $GM.Point(0, 0),
										new $GM.Point(3, 5)
										),
										"trackpoint135":new $GM.MarkerImage( '/static/img/trackpoint/trackpoint135.png',
												new $GM.Size(7, 7),
												new $GM.Point(0, 0),
												new $GM.Point(3, 5)
												),
												"trackpoint180":new $GM.MarkerImage( '/static/img/trackpoint/trackpoint180.png',
														new $GM.Size(7, 7),
														new $GM.Point(0, 0),
														new $GM.Point(3, 5)
														),
														"trackpoint225":new $GM.MarkerImage( '/static/img/trackpoint/trackpoint225.png',
																new $GM.Size(7, 7),
																new $GM.Point(0, 0),
																new $GM.Point(3, 5)
																),
																"trackpoint270":new $GM.MarkerImage( '/static/img/trackpoint/trackpoint270.png',
																		new $GM.Size(7, 7),
																		new $GM.Point(0, 0),
																		new $GM.Point(3, 5)
																		),
																		"trackpoint315":new $GM.MarkerImage( '/static/img/trackpoint/trackpoint315.png',
																				new $GM.Size(7, 7),
																				new $GM.Point(0, 0),
																				new $GM.Point(3, 5)
																				)
	},

	"custom-1":{
		"idle": new $GM.MarkerImage( '/static/img/vehicles/bike/idle.png',
				new $GM.Size(39, 30),
				new $GM.Point(0, 0),
				new $GM.Point(12, 45)
				),
				"online": new $GM.MarkerImage( '/static/img/vehicles/bike/bike.png',
						new $GM.Size(39, 30),
						new $GM.Point(0, 0),
						new $GM.Point(12, 45)
						),
						"online0": new $GM.MarkerImage( '/static/img/vehicles/bike/0360.png',
								new $GM.Size(39, 30),
								new $GM.Point(0, 0),
								new $GM.Point(12, 45)
								),
								"online45": new $GM.MarkerImage( '/static/img/vehicles/bike/45.png',
										new $GM.Size(39, 30),
										new $GM.Point(0, 0),
										new $GM.Point(12, 45)
										),
										"online90": new $GM.MarkerImage( '/static/img/vehicles/bike/90.png',
												new $GM.Size(39, 30),
												new $GM.Point(0, 0),
												new $GM.Point(12, 45)
												),
												"online135": new $GM.MarkerImage( '/static/img/vehicles/bike/135.png',
														new $GM.Size(39, 30),
														new $GM.Point(0, 0),
														new $GM.Point(12, 45)
														),
														"online180": new $GM.MarkerImage( '/static/img/vehicles/bike/180.png',
																new $GM.Size(39, 30),
																new $GM.Point(0, 0),
																new $GM.Point(12, 45)
																),
																"online225": new $GM.MarkerImage( '/static/img/vehicles/bike/225.png',
																		new $GM.Size(39, 30),
																		new $GM.Point(0, 0),
																		new $GM.Point(12, 45)
																		),
																		"online270": new $GM.MarkerImage( '/static/img/vehicles/bike/270.png',
																				new $GM.Size(39, 30),
																				new $GM.Point(0, 0),
																				new $GM.Point(12, 45)
																				),
																				"online315": new $GM.MarkerImage( '/static/img/vehicles/bike/315.png',
																						new $GM.Size(39, 30),
																						new $GM.Point(0, 0),
																						new $GM.Point(12, 45)
																						),                
																						"offline": new $GM.MarkerImage( '/static/img/vehicles/bike/offline.png',
																								new $GM.Size(39, 30),
																								new $GM.Point(0, 0),
																								new $GM.Point(12, 45)
																								),
																								"stopped": new $GM.MarkerImage( '/static/img/vehicles/bike/stopped.png',
																										new $GM.Size(39, 30),
																										new $GM.Point(0, 0),
																										new $GM.Point(12, 45)
																										),
																										"offroad": new $GM.MarkerImage( '/static/img/vehicles/bike/offline.png',
																												new $GM.Size(39, 30),
																												new $GM.Point(0, 0),
																												new $GM.Point(12, 45)
																												)

	},

	"custom-2":{
		"idle": new $GM.MarkerImage( '/static/img/vehicles/blurb/idle.png',
				new $GM.Size(20, 35),
				new $GM.Point(2, 0),
				new $GM.Point(32, 40)
				),
				"online": new $GM.MarkerImage( '/static/img/vehicles/blurb/blurb.png',
						new $GM.Size(20, 35),
						new $GM.Point(2, 0),
						new $GM.Point(32, 40)
						),
						"online0": new $GM.MarkerImage( '/static/img/vehicles/blurb/0360.png',
								new $GM.Size(20, 35),
								new $GM.Point(2, 0),
								new $GM.Point(32, 40)
								),
								"online45": new $GM.MarkerImage( '/static/img/vehicles/blurb/45.png',
										new $GM.Size(20, 35),
										new $GM.Point(2, 0),
										new $GM.Point(32, 40)
										),
										"online90": new $GM.MarkerImage( '/static/img/vehicles/blurb/90.png',
												new $GM.Size(20, 35),
												new $GM.Point(2, 0),
												new $GM.Point(32, 40)
												),
												"online135": new $GM.MarkerImage( '/static/img/vehicles/blurb/135.png',
														new $GM.Size(20, 35),
														new $GM.Point(2, 0),
														new $GM.Point(32, 40)
														),
														"online180": new $GM.MarkerImage( '/static/img/vehicles/blurb/180.png',
																new $GM.Size(20, 35),
																new $GM.Point(2, 0),
																new $GM.Point(32, 40)
																),
																"online225": new $GM.MarkerImage( '/static/img/vehicles/blurb/225.png',
																		new $GM.Size(20, 35),
																		new $GM.Point(2, 0),
																		new $GM.Point(32, 40)
																		),
																		"online270": new $GM.MarkerImage( '/static/img/vehicles/blurb/270.png',
																				new $GM.Size(20, 35),
																				new $GM.Point(2, 0),
																				new $GM.Point(32, 40)
																				),
																				"online315": new $GM.MarkerImage( '/static/img/vehicles/blurb/315.png',
																						new $GM.Size(20, 35),
																						new $GM.Point(2, 0),
																						new $GM.Point(32, 40)
																						),
																						"offline": new $GM.MarkerImage( '/static/img/vehicles/blurb/offline.png',
																								new $GM.Size(20, 35),
																								new $GM.Point(2, 0),
																								new $GM.Point(32, 40)
																								),
																								"stopped": new $GM.MarkerImage( '/static/img/vehicles/blurb/stopped.png',
																										new $GM.Size(20, 35),
																										new $GM.Point(2, 0),
																										new $GM.Point(32, 40)
																										),
																										"offroad": new $GM.MarkerImage( '/static/img/vehicles/blurb/offline.png',
																												new $GM.Size(20, 35),
																												new $GM.Point(2, 0),
																												new $GM.Point(32, 40)
																												)

	},

	"custom-3":{
		"idle": new $GM.MarkerImage( '/static/img/vehicles/blurb/stopped.png',
				new $GM.Size(25, 35),
				new $GM.Point(2, 0),
				new $GM.Point(32, 40)
				),
				"idleblurb": new $GM.MarkerImage( '/static/img/vehicles/blurb/stopped.png',
						new $GM.Size(20, 35),
						new $GM.Point(2, 0),
						new $GM.Point(32, 40)
						),
						"online": new $GM.MarkerImage( '/static/img/vehicles/busblue/blue.png',
								new $GM.Size(40, 44),
								new $GM.Point(0, 0),
								new $GM.Point(14, 22)
								),
								"online0": new $GM.MarkerImage( '/static/img/vehicles/busblue/0360.png',
										new $GM.Size(40, 44),
										new $GM.Point(0, 0),
										new $GM.Point(14, 22)
										),
										"online45": new $GM.MarkerImage( '/static/img/vehicles/busblue/45.png',
												new $GM.Size(40, 44),
												new $GM.Point(0, 0),
												new $GM.Point(14, 22)
												),
												"online90": new $GM.MarkerImage( '/static/img/vehicles/busblue/90.png',
														new $GM.Size(40, 44),
														new $GM.Point(0, 0),
														new $GM.Point(14, 22)
														),
														"online135": new $GM.MarkerImage( '/static/img/vehicles/busblue/135.png',
																new $GM.Size(40, 44),
																new $GM.Point(0, 0),
																new $GM.Point(14, 22)
																),
																"online180": new $GM.MarkerImage( '/static/img/vehicles/busblue/180.png',
																		new $GM.Size(40, 44),
																		new $GM.Point(0, 0),
																		new $GM.Point(14, 22)
																		),
																		"online225": new $GM.MarkerImage( '/static/img/vehicles/busblue/225.png',
																				new $GM.Size(40, 44),
																				new $GM.Point(0, 0),
																				new $GM.Point(14, 22)
																				),
																				"online270": new $GM.MarkerImage( '/static/img/vehicles/busblue/270.png',
																						new $GM.Size(40, 44),
																						new $GM.Point(0, 0),
																						new $GM.Point(14, 22)
																						),
																						"online315": new $GM.MarkerImage( '/static/img/vehicles/busblue/315.png',
																								new $GM.Size(40, 44),
																								new $GM.Point(0, 0),
																								new $GM.Point(14, 22)
																								),
																								"offline": new $GM.MarkerImage( '/static/img/vehicles/busoffline.png',
																										new $GM.Size(40, 44),
																										new $GM.Point(0, 0),
																										new $GM.Point(14, 22)
																										),
																										"stopped": new $GM.MarkerImage( '/static/img/vehicles/busstopped.png',
																												new $GM.Size(40, 44),
																												new $GM.Point(0, 0),
																												new $GM.Point(14, 22)
																												),
																												"offroad": new $GM.MarkerImage( '/static/img/vehicles/busoffline.png',
																														new $GM.Size(40, 44),
																														new $GM.Point(0, 0),
																														new $GM.Point(14, 22)
																														)

	},

	"custom-4":{
		"idle": new $GM.MarkerImage( '/static/img/vehicles/blurb/stopped.png',
				new $GM.Size(25, 35),
				new $GM.Point(2, 0),
				new $GM.Point(32, 40)
				),
				"idleblurb": new $GM.MarkerImage( '/static/img/vehicles/blurb/stopped.png',
						new $GM.Size(20, 35),
						new $GM.Point(2, 0),
						new $GM.Point(32, 40)
						),
						"online": new $GM.MarkerImage( '/static/img/vehicles/busbrown/brown.png',
								new $GM.Size(40, 44),
								new $GM.Point(0, 0),
								new $GM.Point(14, 22)
								),
								"online0": new $GM.MarkerImage( '/static/img/vehicles/busbrown/0360.png',
										new $GM.Size(40, 44),
										new $GM.Point(0, 0),
										new $GM.Point(14, 22)
										),
										"online45": new $GM.MarkerImage( '/static/img/vehicles/busbrown/45.png',
												new $GM.Size(40, 44),
												new $GM.Point(0, 0),
												new $GM.Point(14, 22)
												),
												"online90": new $GM.MarkerImage( '/static/img/vehicles/busbrown/90.png',
														new $GM.Size(40, 44),
														new $GM.Point(0, 0),
														new $GM.Point(14, 22)
														),
														"online135": new $GM.MarkerImage( '/static/img/vehicles/busbrown/135.png',
																new $GM.Size(40, 44),
																new $GM.Point(0, 0),
																new $GM.Point(14, 22)
																),
																"online180": new $GM.MarkerImage( '/static/img/vehicles/busbrown/180.png',
																		new $GM.Size(40, 44),
																		new $GM.Point(0, 0),
																		new $GM.Point(14, 22)
																		),
																		"online225": new $GM.MarkerImage( '/static/img/vehicles/busbrown/225.png',
																				new $GM.Size(40, 44),
																				new $GM.Point(0, 0),
																				new $GM.Point(14, 22)
																				),
																				"online270": new $GM.MarkerImage( '/static/img/vehicles/busbrown/270.png',
																						new $GM.Size(40, 44),
																						new $GM.Point(0, 0),
																						new $GM.Point(14, 22)
																						),
																						"online315": new $GM.MarkerImage( '/static/img/vehicles/busbrown/315.png',
																								new $GM.Size(40, 44),
																								new $GM.Point(0, 0),
																								new $GM.Point(14, 22)
																								),
																								"offline": new $GM.MarkerImage( '/static/img/vehicles/busoffline.png',
																										new $GM.Size(40, 44),
																										new $GM.Point(0, 0),
																										new $GM.Point(14, 22)
																										),
																										"stopped": new $GM.MarkerImage( '/static/img/vehicles/busstopped.png',
																												new $GM.Size(40, 44),
																												new $GM.Point(0, 0),
																												new $GM.Point(14, 22)
																												),
																												"offroad": new $GM.MarkerImage( '/static/img/vehicles/busoffline.png',
																														new $GM.Size(40, 44),
																														new $GM.Point(0, 0),
																														new $GM.Point(14, 22)
																														)
	},

	"custom-5":{
		"idle": new $GM.MarkerImage( '/static/img/vehicles/blurb/stopped.png',
				new $GM.Size(25, 35),
				new $GM.Point(2, 0),
				new $GM.Point(32, 40)
				),
				"idleblurb": new $GM.MarkerImage( '/static/img/vehicles/blurb/stopped.png',
						new $GM.Size(20, 35),
						new $GM.Point(2, 0),
						new $GM.Point(32, 40)
						),
						"online": new $GM.MarkerImage( '/static/img/vehicles/busdarkyellow/darkyellow.png',
								new $GM.Size(40, 44),
								new $GM.Point(0, 0),
								new $GM.Point(14, 22)
								),
								"online0": new $GM.MarkerImage( '/static/img/vehicles/busdarkyellow/0360.png',
										new $GM.Size(40, 44),
										new $GM.Point(0, 0),
										new $GM.Point(14, 22)
										),
										"online45": new $GM.MarkerImage( '/static/img/vehicles/busdarkyellow/45.png',
												new $GM.Size(40, 44),
												new $GM.Point(0, 0),
												new $GM.Point(14, 22)
												),
												"online90": new $GM.MarkerImage( '/static/img/vehicles/busdarkyellow/90.png',
														new $GM.Size(40, 44),
														new $GM.Point(0, 0),
														new $GM.Point(14, 22)
														),
														"online135": new $GM.MarkerImage( '/static/img/vehicles/busdarkyellow/135.png',
																new $GM.Size(40, 44),
																new $GM.Point(0, 0),
																new $GM.Point(14, 22)
																),
																"online180": new $GM.MarkerImage( '/static/img/vehicles/busdarkyellow/180.png',
																		new $GM.Size(40, 44),
																		new $GM.Point(0, 0),
																		new $GM.Point(14, 22)
																		),
																		"online225": new $GM.MarkerImage( '/static/img/vehicles/busdarkyellow/225.png',
																				new $GM.Size(40, 44),
																				new $GM.Point(0, 0),
																				new $GM.Point(14, 22)
																				),
																				"online270": new $GM.MarkerImage( '/static/img/vehicles/busdarkyellow/270.png',
																						new $GM.Size(40, 44),
																						new $GM.Point(0, 0),
																						new $GM.Point(14, 22)
																						),
																						"online315": new $GM.MarkerImage( '/static/img/vehicles/busdarkyellow/315.png',
																								new $GM.Size(40, 44),
																								new $GM.Point(0, 0),
																								new $GM.Point(14, 22)
																								),
																								"offline": new $GM.MarkerImage( '/static/img/vehicles/busoffline.png',
																										new $GM.Size(40, 44),
																										new $GM.Point(0, 0),
																										new $GM.Point(14, 22)
																										),
																										"stopped": new $GM.MarkerImage( '/static/img/vehicles/busstopped.png',
																												new $GM.Size(40, 44),
																												new $GM.Point(0, 0),
																												new $GM.Point(14, 22)
																												),
																												"offroad": new $GM.MarkerImage( '/static/img/vehicles/busoffline.png',
																														new $GM.Size(40, 44),
																														new $GM.Point(0, 0),
																														new $GM.Point(14, 22)
																														)
	},

	"custom-6":{
		"idle": new $GM.MarkerImage( '/static/img/vehicles/blurb/stopped.png',
				new $GM.Size(25, 35),
				new $GM.Point(2, 0),
				new $GM.Point(32, 40)
				),
				"idleblurb": new $GM.MarkerImage( '/static/img/vehicles/blurb/stopped.png',
						new $GM.Size(20, 35),
						new $GM.Point(2, 0),
						new $GM.Point(32, 40)
						),
						"online": new $GM.MarkerImage( '/static/img/vehicles/busgreen/green.png',
								new $GM.Size(40, 44),
								new $GM.Point(0, 0),
								new $GM.Point(14, 22)
								),
								"online0": new $GM.MarkerImage( '/static/img/vehicles/busgreen/0360.png',
										new $GM.Size(40, 44),
										new $GM.Point(0, 0),
										new $GM.Point(14, 22)
										),
										"online45": new $GM.MarkerImage( '/static/img/vehicles/busgreen/45.png',
												new $GM.Size(40, 44),
												new $GM.Point(0, 0),
												new $GM.Point(14, 22)
												),
												"online90": new $GM.MarkerImage( '/static/img/vehicles/busgreen/90.png',
														new $GM.Size(40, 44),
														new $GM.Point(0, 0),
														new $GM.Point(14, 22)
														),
														"online135": new $GM.MarkerImage( '/static/img/vehicles/busgreen/135.png',
																new $GM.Size(40, 44),
																new $GM.Point(0, 0),
																new $GM.Point(14, 22)
																),
																"online180": new $GM.MarkerImage( '/static/img/vehicles/busgreen/180.png',
																		new $GM.Size(40, 44),
																		new $GM.Point(0, 0),
																		new $GM.Point(14, 22)
																		),
																		"online225": new $GM.MarkerImage( '/static/img/vehicles/busgreen/225.png',
																				new $GM.Size(40, 44),
																				new $GM.Point(0, 0),
																				new $GM.Point(14, 22)
																				),
																				"online270": new $GM.MarkerImage( '/static/img/vehicles/busgreen/270.png',
																						new $GM.Size(40, 44),
																						new $GM.Point(0, 0),
																						new $GM.Point(14, 22)
																						),
																						"online315": new $GM.MarkerImage( '/static/img/vehicles/busgreen/315.png',
																								new $GM.Size(40, 44),
																								new $GM.Point(0, 0),
																								new $GM.Point(14, 22)
																								),
																								"offline": new $GM.MarkerImage( '/static/img/vehicles/busoffline.png',
																										new $GM.Size(40, 44),
																										new $GM.Point(0, 0),
																										new $GM.Point(14, 22)
																										),
																										"stopped": new $GM.MarkerImage( '/static/img/vehicles/busstopped.png',
																												new $GM.Size(40, 44),
																												new $GM.Point(0, 0),
																												new $GM.Point(14, 22)
																												),
																												"offroad": new $GM.MarkerImage( '/static/img/vehicles/busoffline.png',
																														new $GM.Size(40, 44),
																														new $GM.Point(0, 0),
																														new $GM.Point(14, 22)
																														)
	},

	"custom-7":{
		"idle": new $GM.MarkerImage( '/static/img/vehicles/blurb/stopped.png',
				new $GM.Size(25, 35),
				new $GM.Point(2, 0),
				new $GM.Point(32, 40)
				),
				"idleblurb": new $GM.MarkerImage( '/static/img/vehicles/blurb/stopped.png',
						new $GM.Size(20, 35),
						new $GM.Point(2, 0),
						new $GM.Point(32, 40)
						),
						"online": new $GM.MarkerImage( '/static/img/vehicles/buspink/pink.png',
								new $GM.Size(40, 44),
								new $GM.Point(0, 0),
								new $GM.Point(14, 22)
								),
								"online0": new $GM.MarkerImage( '/static/img/vehicles/buspink/0360.png',
										new $GM.Size(40, 44),
										new $GM.Point(0, 0),
										new $GM.Point(14, 22)
										),
										"online45": new $GM.MarkerImage( '/static/img/vehicles/buspink/45.png',
												new $GM.Size(40, 44),
												new $GM.Point(0, 0),
												new $GM.Point(14, 22)
												),
												"online90": new $GM.MarkerImage( '/static/img/vehicles/buspink/90.png',
														new $GM.Size(40, 44),
														new $GM.Point(0, 0),
														new $GM.Point(14, 22)
														),
														"online135": new $GM.MarkerImage( '/static/img/vehicles/buspink/135.png',
																new $GM.Size(40, 44),
																new $GM.Point(0, 0),
																new $GM.Point(14, 22)
																),
																"online180": new $GM.MarkerImage( '/static/img/vehicles/buspink/180.png',
																		new $GM.Size(40, 44),
																		new $GM.Point(0, 0),
																		new $GM.Point(14, 22)
																		),
																		"online225": new $GM.MarkerImage( '/static/img/vehicles/buspink/225.png',
																				new $GM.Size(40, 44),
																				new $GM.Point(0, 0),
																				new $GM.Point(14, 22)
																				),
																				"online270": new $GM.MarkerImage( '/static/img/vehicles/buspink/270.png',
																						new $GM.Size(40, 44),
																						new $GM.Point(0, 0),
																						new $GM.Point(14, 22)
																						),
																						"online315": new $GM.MarkerImage( '/static/img/vehicles/buspink/315.png',
																								new $GM.Size(40, 44),
																								new $GM.Point(0, 0),
																								new $GM.Point(14, 22)
																								),
																								"offline": new $GM.MarkerImage( '/static/img/vehicles/busoffline.png',
																										new $GM.Size(40, 44),
																										new $GM.Point(0, 0),
																										new $GM.Point(14, 22)
																										),
																										"stopped": new $GM.MarkerImage( '/static/img/vehicles/busstopped.png',
																												new $GM.Size(40, 44),
																												new $GM.Point(0, 0),
																												new $GM.Point(14, 22)
																												),
																												"offroad": new $GM.MarkerImage( '/static/img/vehicles/busoffline.png',
																														new $GM.Size(40, 44),
																														new $GM.Point(0, 0),
																														new $GM.Point(14, 22)
																														)
	},

	"custom-8":{
		"idle": new $GM.MarkerImage( '/static/img/vehicles/blurb/stopped.png',
				new $GM.Size(25, 35),
				new $GM.Point(2, 0),
				new $GM.Point(32, 40)
				),
				"idleblurb": new $GM.MarkerImage( '/static/img/vehicles/blurb/stopped.png',
						new $GM.Size(20, 35),
						new $GM.Point(2, 0),
						new $GM.Point(32, 40)
						),
						"online": new $GM.MarkerImage( '/static/img/vehicles/buspurple/purple.png',
								new $GM.Size(40, 44),
								new $GM.Point(0, 0),
								new $GM.Point(14, 22)
								),
								"online0": new $GM.MarkerImage( '/static/img/vehicles/buspurple/0360.png',
										new $GM.Size(40, 44),
										new $GM.Point(0, 0),
										new $GM.Point(14, 22)
										),
										"online45": new $GM.MarkerImage( '/static/img/vehicles/buspurple/45.png',
												new $GM.Size(40, 44),
												new $GM.Point(0, 0),
												new $GM.Point(14, 22)
												),
												"online90": new $GM.MarkerImage( '/static/img/vehicles/buspurple/90.png',
														new $GM.Size(40, 44),
														new $GM.Point(0, 0),
														new $GM.Point(14, 22)
														),
														"online135": new $GM.MarkerImage( '/static/img/vehicles/buspurple/135.png',
																new $GM.Size(40, 44),
																new $GM.Point(0, 0),
																new $GM.Point(14, 22)
																),
																"online180": new $GM.MarkerImage( '/static/img/vehicles/buspurple/180.png',
																		new $GM.Size(40, 44),
																		new $GM.Point(0, 0),
																		new $GM.Point(14, 22)
																		),
																		"online225": new $GM.MarkerImage( '/static/img/vehicles/buspurple/225.png',
																				new $GM.Size(40, 44),
																				new $GM.Point(0, 0),
																				new $GM.Point(14, 22)
																				),
																				"online270": new $GM.MarkerImage( '/static/img/vehicles/buspurple/270.png',
																						new $GM.Size(40, 44),
																						new $GM.Point(0, 0),
																						new $GM.Point(14, 22)
																						),
																						"online315": new $GM.MarkerImage( '/static/img/vehicles/buspurple/315.png',
																								new $GM.Size(40, 44),
																								new $GM.Point(0, 0),
																								new $GM.Point(14, 22)
																								),
																								"offline": new $GM.MarkerImage( '/static/img/vehicles/busoffline.png',
																										new $GM.Size(40, 44),
																										new $GM.Point(0, 0),
																										new $GM.Point(14, 22)
																										),
																										"stopped": new $GM.MarkerImage( '/static/img/vehicles/busstopped.png',
																												new $GM.Size(40, 44),
																												new $GM.Point(0, 0),
																												new $GM.Point(14, 22)
																												),
																												"offroad": new $GM.MarkerImage( '/static/img/vehicles/busoffline.png',
																														new $GM.Size(40, 44),
																														new $GM.Point(0, 0),
																														new $GM.Point(14, 22)
																														)
	},

	"custom-9":{
		"idle": new $GM.MarkerImage( '/static/img/vehicles/blurb/stopped.png',
				new $GM.Size(25, 35),
				new $GM.Point(2, 0),
				new $GM.Point(32, 40)
				),
				"idleblurb": new $GM.MarkerImage( '/static/img/vehicles/blurb/stopped.png',
						new $GM.Size(20, 35),
						new $GM.Point(2, 0),
						new $GM.Point(32, 40)
						),
						"online": new $GM.MarkerImage( '/static/img/vehicles/busskyblue/skyblue.png',
								new $GM.Size(40, 44),
								new $GM.Point(0, 0),
								new $GM.Point(14, 22)
								),
								"online0": new $GM.MarkerImage( '/static/img/vehicles/busskyblue/0360.png',
										new $GM.Size(40, 44),
										new $GM.Point(0, 0),
										new $GM.Point(14, 22)
										),
										"online45": new $GM.MarkerImage( '/static/img/vehicles/busskyblue/45.png',
												new $GM.Size(40, 44),
												new $GM.Point(0, 0),
												new $GM.Point(14, 22)
												),
												"online90": new $GM.MarkerImage( '/static/img/vehicles/busskyblue/90.png',
														new $GM.Size(40, 44),
														new $GM.Point(0, 0),
														new $GM.Point(14, 22)
														),
														"online135": new $GM.MarkerImage( '/static/img/vehicles/busskyblue/135.png',
																new $GM.Size(40, 44),
																new $GM.Point(0, 0),
																new $GM.Point(14, 22)
																),
																"online180": new $GM.MarkerImage( '/static/img/vehicles/busskyblue/180.png',
																		new $GM.Size(40, 44),
																		new $GM.Point(0, 0),
																		new $GM.Point(14, 22)
																		),
																		"online225": new $GM.MarkerImage( '/static/img/vehicles/busskyblue/225.png',
																				new $GM.Size(40, 44),
																				new $GM.Point(0, 0),
																				new $GM.Point(14, 22)
																				),
																				"online270": new $GM.MarkerImage( '/static/img/vehicles/busskyblue/270.png',
																						new $GM.Size(40, 44),
																						new $GM.Point(0, 0),
																						new $GM.Point(14, 22)
																						),
																						"online315": new $GM.MarkerImage( '/static/img/vehicles/busskyblue/315.png',
																								new $GM.Size(40, 44),
																								new $GM.Point(0, 0),
																								new $GM.Point(14, 22)
																								),
																								"offline": new $GM.MarkerImage( '/static/img/vehicles/busoffline.png',
																										new $GM.Size(40, 44),
																										new $GM.Point(0, 0),
																										new $GM.Point(14, 22)
																										),
																										"stopped": new $GM.MarkerImage( '/static/img/vehicles/busstopped.png',
																												new $GM.Size(40, 44),
																												new $GM.Point(0, 0),
																												new $GM.Point(14, 22)
																												),
																												"offroad": new $GM.MarkerImage( '/static/img/vehicles/busoffline.png',
																														new $GM.Size(40, 44),
																														new $GM.Point(0, 0),
																														new $GM.Point(14, 22)
																														)
	},

	"custom-10":{
		"idle": new $GM.MarkerImage( '/static/img/vehicles/blurb/stopped.png',
				new $GM.Size(25, 35),
				new $GM.Point(2, 0),
				new $GM.Point(32, 40)
				),
				"idleblurb": new $GM.MarkerImage( '/static/img/vehicles/blurb/stopped.png',
						new $GM.Size(20, 35),
						new $GM.Point(2, 0),
						new $GM.Point(32, 40)
						),
						"online": new $GM.MarkerImage( '/static/img/vehicles/busviolet/violet.png',
								new $GM.Size(40, 44),
								new $GM.Point(0, 0),
								new $GM.Point(14, 22)
								),
								"online0": new $GM.MarkerImage( '/static/img/vehicles/busviolet/0360.png',
										new $GM.Size(40, 44),
										new $GM.Point(0, 0),
										new $GM.Point(14, 22)
										),
										"online45": new $GM.MarkerImage( '/static/img/vehicles/busviolet/45.png',
												new $GM.Size(40, 44),
												new $GM.Point(0, 0),
												new $GM.Point(14, 22)
												),
												"online90": new $GM.MarkerImage( '/static/img/vehicles/busviolet/90.png',
														new $GM.Size(40, 44),
														new $GM.Point(0, 0),
														new $GM.Point(14, 22)
														),
														"online135": new $GM.MarkerImage( '/static/img/vehicles/busviolet/135.png',
																new $GM.Size(40, 44),
																new $GM.Point(0, 0),
																new $GM.Point(14, 22)
																),
																"online180": new $GM.MarkerImage( '/static/img/vehicles/busviolet/180.png',
																		new $GM.Size(40, 44),
																		new $GM.Point(0, 0),
																		new $GM.Point(14, 22)
																		),
																		"online225": new $GM.MarkerImage( '/static/img/vehicles/busviolet/225.png',
																				new $GM.Size(40, 44),
																				new $GM.Point(0, 0),
																				new $GM.Point(14, 22)
																				),
																				"online270": new $GM.MarkerImage( '/static/img/vehicles/busviolet/270.png',
																						new $GM.Size(40, 44),
																						new $GM.Point(0, 0),
																						new $GM.Point(14, 22)
																						),
																						"online315": new $GM.MarkerImage( '/static/img/vehicles/busviolet/315.png',
																								new $GM.Size(40, 44),
																								new $GM.Point(0, 0),
																								new $GM.Point(14, 22)
																								),
																								"offline": new $GM.MarkerImage( '/static/img/vehicles/busoffline.png',
																										new $GM.Size(40, 44),
																										new $GM.Point(0, 0),
																										new $GM.Point(14, 22)
																										),
																										"stopped": new $GM.MarkerImage( '/static/img/vehicles/busstopped.png',
																												new $GM.Size(40, 44),
																												new $GM.Point(0, 0),
																												new $GM.Point(14, 22)
																												),
																												"offroad": new $GM.MarkerImage( '/static/img/vehicles/busoffline.png',
																														new $GM.Size(40, 44),
																														new $GM.Point(0, 0),
																														new $GM.Point(14, 22)
																														)
	},

	"custom-11":{
		"idle": new $GM.MarkerImage( '/static/img/vehicles/blurb/stopped.png',
				new $GM.Size(25, 35),
				new $GM.Point(2, 0),
				new $GM.Point(32, 40)
				),
				"idleblurb": new $GM.MarkerImage( '/static/img/vehicles/blurb/stopped.png',
						new $GM.Size(20, 35),
						new $GM.Point(2, 0),
						new $GM.Point(32, 40)
						),
						"online": new $GM.MarkerImage( '/static/img/vehicles/busyellow/yellow.png',
								new $GM.Size(40, 44),
								new $GM.Point(0, 0),
								new $GM.Point(14, 22)
								),
								"online0": new $GM.MarkerImage( '/static/img/vehicles/busyellow/0360.png',
										new $GM.Size(40, 44),
										new $GM.Point(0, 0),
										new $GM.Point(14, 22)
										),
										"online45": new $GM.MarkerImage( '/static/img/vehicles/busyellow/45.png',
												new $GM.Size(40, 44),
												new $GM.Point(0, 0),
												new $GM.Point(14, 22)
												),
												"online90": new $GM.MarkerImage( '/static/img/vehicles/busyellow/90.png',
														new $GM.Size(40, 44),
														new $GM.Point(0, 0),
														new $GM.Point(14, 22)
														),
														"online135": new $GM.MarkerImage( '/static/img/vehicles/busyellow/135.png',
																new $GM.Size(40, 44),
																new $GM.Point(0, 0),
																new $GM.Point(14, 22)
																),
																"online180": new $GM.MarkerImage( '/static/img/vehicles/busyellow/180.png',
																		new $GM.Size(40, 44),
																		new $GM.Point(0, 0),
																		new $GM.Point(14, 22)
																		),
																		"online225": new $GM.MarkerImage( '/static/img/vehicles/busyellow/225.png',
																				new $GM.Size(40, 44),
																				new $GM.Point(0, 0),
																				new $GM.Point(14, 22)
																				),
																				"online270": new $GM.MarkerImage( '/static/img/vehicles/busyellow/270.png',
																						new $GM.Size(40, 44),
																						new $GM.Point(0, 0),
																						new $GM.Point(14, 22)
																						),
																						"online315": new $GM.MarkerImage( '/static/img/vehicles/busyellow/315.png',
																								new $GM.Size(40, 44),
																								new $GM.Point(0, 0),
																								new $GM.Point(14, 22)
																								),
																								"offline": new $GM.MarkerImage( '/static/img/vehicles/busoffline.png',
																										new $GM.Size(40, 44),
																										new $GM.Point(0, 0),
																										new $GM.Point(14, 22)
																										),
																										"stopped": new $GM.MarkerImage( '/static/img/vehicles/busstopped.png',
																												new $GM.Size(40, 44),
																												new $GM.Point(0, 0),
																												new $GM.Point(14, 22)
																												),
																												"offroad": new $GM.MarkerImage( '/static/img/vehicles/busoffline.png',
																														new $GM.Size(40, 44),
																														new $GM.Point(0, 0),
																														new $GM.Point(14, 22)
																														)
	},

	"custom-12":{
		"idle": new $GM.MarkerImage( '/static/img/vehicles/car/idle.png',
				new $GM.Size(30, 20),
				new $GM.Point(0, 0),
				new $GM.Point(14, 22)
				),
				"online": new $GM.MarkerImage( '/static/img/vehicles/car/car.png',
						new $GM.Size(30, 20),
						new $GM.Point(0, 0),
						new $GM.Point(14, 22)
						),
						"online0": new $GM.MarkerImage( '/static/img/vehicles/car/0360.png',
								new $GM.Size(30, 20),
								new $GM.Point(0, 0),
								new $GM.Point(14, 22)
								),
								"online45": new $GM.MarkerImage( '/static/img/vehicles/car/45.png',
										new $GM.Size(30, 20),
										new $GM.Point(0, 0),
										new $GM.Point(14, 22)
										),
										"online90": new $GM.MarkerImage( '/static/img/vehicles/car/90.png',
												new $GM.Size(30, 20),
												new $GM.Point(0, 0),
												new $GM.Point(14, 22)
												),
												"online135": new $GM.MarkerImage( '/static/img/vehicles/car/135.png',
														new $GM.Size(30, 20),
														new $GM.Point(0, 0),
														new $GM.Point(14, 22)
														),
														"online180": new $GM.MarkerImage( '/static/img/vehicles/car/180.png',
																new $GM.Size(30, 20),
																new $GM.Point(0, 0),
																new $GM.Point(14, 22)
																),
																"online225": new $GM.MarkerImage( '/static/img/vehicles/car/225.png',
																		new $GM.Size(30, 20),
																		new $GM.Point(0, 0),
																		new $GM.Point(14, 22)
																		),
																		"online270": new $GM.MarkerImage( '/static/img/vehicles/car/270.png',
																				new $GM.Size(30, 20),
																				new $GM.Point(0, 0),
																				new $GM.Point(14, 22)
																				),
																				"online315": new $GM.MarkerImage( '/static/img/vehicles/car/315.png',
																						new $GM.Size(30, 20),
																						new $GM.Point(0, 0),
																						new $GM.Point(14, 22)
																						),
																						"offline": new $GM.MarkerImage( '/static/img/vehicles/car/offline.png',
																								new $GM.Size(30, 20),
																								new $GM.Point(0, 0),
																								new $GM.Point(14, 22)
																								),
																								"stopped": new $GM.MarkerImage( '/static/img/vehicles/car/stopped.png',
																										new $GM.Size(30, 20),
																										new $GM.Point(0, 0),
																										new $GM.Point(14, 22)
																										),
																										"offroad": new $GM.MarkerImage( '/static/img/vehicles/car/offline.png',
																												new $GM.Size(30, 20),
																												new $GM.Point(0, 0),
																												new $GM.Point(14, 22)
																												)
	},

	"custom-13":{
		"idle": new $GM.MarkerImage( '/static/img/vehicles/jeep/idle.png',
				new $GM.Size(36, 42),
				new $GM.Point(0, 0),
				new $GM.Point(14, 22)
				),
				"online": new $GM.MarkerImage( '/static/img/vehicles/jeep/jeep.png',
						new $GM.Size(36, 42),
						new $GM.Point(0, 0),
						new $GM.Point(14, 22)
						),
						"online0": new $GM.MarkerImage( '/static/img/vehicles/jeep/0360.png',
								new $GM.Size(36, 42),
								new $GM.Point(0, 0),
								new $GM.Point(14, 22)
								),
								"online45": new $GM.MarkerImage( '/static/img/vehicles/jeep/45.png',
										new $GM.Size(36, 42),
										new $GM.Point(0, 0),
										new $GM.Point(14, 22)
										),
										"online90": new $GM.MarkerImage( '/static/img/vehicles/jeep/90.png',
												new $GM.Size(36, 42),
												new $GM.Point(0, 0),
												new $GM.Point(14, 22)
												),
												"online135": new $GM.MarkerImage( '/static/img/vehicles/jeep/135.png',
														new $GM.Size(36, 42),
														new $GM.Point(0, 0),
														new $GM.Point(14, 22)
														),
														"online180": new $GM.MarkerImage( '/static/img/vehicles/jeep/180.png',
																new $GM.Size(36, 42),
																new $GM.Point(0, 0),
																new $GM.Point(14, 22)
																),
																"online225": new $GM.MarkerImage( '/static/img/vehicles/jeep/225.png',
																		new $GM.Size(36, 42),
																		new $GM.Point(0, 0),
																		new $GM.Point(14, 22)
																		),
																		"online270": new $GM.MarkerImage( '/static/img/vehicles/jeep/270.png',
																				new $GM.Size(36, 42),
																				new $GM.Point(0, 0),
																				new $GM.Point(14, 22)
																				),
																				"online315": new $GM.MarkerImage( '/static/img/vehicles/jeep/315.png',
																						new $GM.Size(36, 42),
																						new $GM.Point(0, 0),
																						new $GM.Point(14, 22)
																						),
																						"offline": new $GM.MarkerImage( '/static/img/vehicles/jeep/offline.png',
																								new $GM.Size(36, 42),
																								new $GM.Point(0, 0),
																								new $GM.Point(14, 22)
																								),
																								"stopped": new $GM.MarkerImage( '/static/img/vehicles/jeep/stopped.png',
																										new $GM.Size(36, 42),
																										new $GM.Point(0, 0),
																										new $GM.Point(14, 22)
																										),
																										"offroad": new $GM.MarkerImage( '/static/img/vehicles/jeep/offline.png',
																												new $GM.Size(36, 42),
																												new $GM.Point(0, 0),
																												new $GM.Point(14, 22)
																												)
	},

	"custom-14":{
		"idle": new $GM.MarkerImage( '/static/img/vehicles/jeep1/idle.png',
				new $GM.Size(38, 29),
				new $GM.Point(0, 0),
				new $GM.Point(14, 22)
				),
				"online": new $GM.MarkerImage( '/static/img/vehicles/jeep1/jeep1.png',
						new $GM.Size(38, 29),
						new $GM.Point(0, 0),
						new $GM.Point(14, 22)
						),
						"online0": new $GM.MarkerImage( '/static/img/vehicles/jeep1/0360.png',
								new $GM.Size(38, 29),
								new $GM.Point(0, 0),
								new $GM.Point(14, 22)
								),
								"online45": new $GM.MarkerImage( '/static/img/vehicles/jeep1/45.png',
										new $GM.Size(38, 29),
										new $GM.Point(0, 0),
										new $GM.Point(14, 22)
										),
										"online90": new $GM.MarkerImage( '/static/img/vehicles/jeep1/90.png',
												new $GM.Size(38, 29),
												new $GM.Point(0, 0),
												new $GM.Point(14, 22)
												),
												"online135": new $GM.MarkerImage( '/static/img/vehicles/jeep1/135.png',
														new $GM.Size(38, 29),
														new $GM.Point(0, 0),
														new $GM.Point(14, 22)
														),
														"online180": new $GM.MarkerImage( '/static/img/vehicles/jeep1/180.png',
																new $GM.Size(38, 29),
																new $GM.Point(0, 0),
																new $GM.Point(14, 22)
																),
																"online225": new $GM.MarkerImage( '/static/img/vehicles/jeep1/225.png',
																		new $GM.Size(38, 29),
																		new $GM.Point(0, 0),
																		new $GM.Point(14, 22)
																		),
																		"online270": new $GM.MarkerImage( '/static/img/vehicles/jeep1/270.png',
																				new $GM.Size(38, 29),
																				new $GM.Point(0, 0),
																				new $GM.Point(14, 22)
																				),
																				"online315": new $GM.MarkerImage( '/static/img/vehicles/jeep1/315.png',
																						new $GM.Size(38, 29),
																						new $GM.Point(0, 0),
																						new $GM.Point(14, 22)
																						),
																						"offline": new $GM.MarkerImage( '/static/img/vehicles/jeep1/offline.png',
																								new $GM.Size(38, 29),
																								new $GM.Point(0, 0),
																								new $GM.Point(14, 22)
																								),
																								"stopped": new $GM.MarkerImage( '/static/img/vehicles/jeep1/stopped.png',
																										new $GM.Size(38, 29),
																										new $GM.Point(0, 0),
																										new $GM.Point(14, 22)
																										),
																										"offroad": new $GM.MarkerImage( '/static/img/vehicles/jeep1/offline.png',
																												new $GM.Size(38, 29),
																												new $GM.Point(0, 0),
																												new $GM.Point(14, 22)
																												)
	},

	"custom-15":{
		"idle": new $GM.MarkerImage( '/static/img/vehicles/pointer/idle.png',
				new $GM.Size(15, 44),
				new $GM.Point(0, 0),
				new $GM.Point(14, 22)
				),
				"online": new $GM.MarkerImage( '/static/img/vehicles/pointer/pointer.png',
						new $GM.Size(15, 44),
						new $GM.Point(0, 0),
						new $GM.Point(14, 22)
						),
						"online0": new $GM.MarkerImage( '/static/img/vehicles/pointer/0360.png',
								new $GM.Size(15, 44),
								new $GM.Point(0, 0),
								new $GM.Point(14, 22)
								),
								"online45": new $GM.MarkerImage( '/static/img/vehicles/pointer/45.png',
										new $GM.Size(15, 44),
										new $GM.Point(0, 0),
										new $GM.Point(14, 22)
										),
										"online90": new $GM.MarkerImage( '/static/img/vehicles/pointer/90.png',
												new $GM.Size(15, 44),
												new $GM.Point(0, 0),
												new $GM.Point(14, 22)
												),
												"online135": new $GM.MarkerImage( '/static/img/vehicles/pointer/135.png',
														new $GM.Size(15, 44),
														new $GM.Point(0, 0),
														new $GM.Point(14, 22)
														),
														"online180": new $GM.MarkerImage( '/static/img/vehicles/pointer/180.png',
																new $GM.Size(15, 44),
																new $GM.Point(0, 0),
																new $GM.Point(14, 22)
																),
																"online225": new $GM.MarkerImage( '/static/img/vehicles/pointer/225.png',
																		new $GM.Size(15, 44),
																		new $GM.Point(0, 0),
																		new $GM.Point(14, 22)
																		),
																		"online270": new $GM.MarkerImage( '/static/img/vehicles/pointer/270.png',
																				new $GM.Size(15, 44),
																				new $GM.Point(0, 0),
																				new $GM.Point(14, 22)
																				),
																				"online315": new $GM.MarkerImage( '/static/img/vehicles/pointer/315.png',
																						new $GM.Size(15, 44),
																						new $GM.Point(0, 0),
																						new $GM.Point(14, 22)
																						),
																						"offline": new $GM.MarkerImage( '/static/img/vehicles/pointer/offline.png',
																								new $GM.Size(15, 44),
																								new $GM.Point(0, 0),
																								new $GM.Point(14, 22)
																								),
																								"stopped": new $GM.MarkerImage( '/static/img/vehicles/pointer/stopped.png',
																										new $GM.Size(15, 44),
																										new $GM.Point(0, 0),
																										new $GM.Point(14, 22)
																										),
																										"offroad": new $GM.MarkerImage( '/static/img/vehicles/pointer/offline.png',
																												new $GM.Size(15, 44),
																												new $GM.Point(0, 0),
																												new $GM.Point(14, 22)
																												)
	},

	"custom-16":{
		"idle": new $GM.MarkerImage( '/static/img/vehicles/tractor/idle.png',
				new $GM.Size(40, 36),
				new $GM.Point(0, 0),
				new $GM.Point(14, 22)
				),
				"online": new $GM.MarkerImage( '/static/img/vehicles/tractor/tractor.png',
						new $GM.Size(40, 36),
						new $GM.Point(0, 0),
						new $GM.Point(14, 22)
						),
						"online0": new $GM.MarkerImage( '/static/img/vehicles/tractor/0360.png',
								new $GM.Size(40, 36),
								new $GM.Point(0, 0),
								new $GM.Point(14, 22)
								),
								"online45": new $GM.MarkerImage( '/static/img/vehicles/tractor/45.png',
										new $GM.Size(40, 36),
										new $GM.Point(0, 0),
										new $GM.Point(14, 22)
										),
										"online90": new $GM.MarkerImage( '/static/img/vehicles/tractor/90.png',
												new $GM.Size(40, 36),
												new $GM.Point(0, 0),
												new $GM.Point(14, 22)
												),
												"online135": new $GM.MarkerImage( '/static/img/vehicles/tractor/135.png',
														new $GM.Size(40, 36),
														new $GM.Point(0, 0),
														new $GM.Point(14, 22)
														),
														"online180": new $GM.MarkerImage( '/static/img/vehicles/tractor/180.png',
																new $GM.Size(40, 36),
																new $GM.Point(0, 0),
																new $GM.Point(14, 22)
																),
																"online225": new $GM.MarkerImage( '/static/img/vehicles/tractor/225.png',
																		new $GM.Size(40, 36),
																		new $GM.Point(0, 0),
																		new $GM.Point(14, 22)
																		),
																		"online270": new $GM.MarkerImage( '/static/img/vehicles/tractor/270.png',
																				new $GM.Size(40, 36),
																				new $GM.Point(0, 0),
																				new $GM.Point(14, 22)
																				),
																				"online315": new $GM.MarkerImage( '/static/img/vehicles/tractor/315.png',
																						new $GM.Size(40, 36),
																						new $GM.Point(0, 0),
																						new $GM.Point(14, 22)
																						),
																						"offline": new $GM.MarkerImage( '/static/img/vehicles/tractor/offline.png',
																								new $GM.Size(40, 36),
																								new $GM.Point(0, 0),
																								new $GM.Point(14, 22)
																								),
																								"stopped": new $GM.MarkerImage( '/static/img/vehicles/tractor/stopped.png',
																										new $GM.Size(40, 36),
																										new $GM.Point(0, 0),
																										new $GM.Point(14, 22)
																										),
																										"offroad": new $GM.MarkerImage( '/static/img/vehicles/tractor/offline.png',
																												new $GM.Size(40, 36),
																												new $GM.Point(0, 0),
																												new $GM.Point(14, 22)
																												)
	},

	"custom-17":{
		"idle": new $GM.MarkerImage( '/static/img/vehicles/truck/idle.png',
				new $GM.Size(40, 30),
				new $GM.Point(0, 0),
				new $GM.Point(14, 22)
				),
				"online": new $GM.MarkerImage( '/static/img/vehicles/truck/truck.png',
						new $GM.Size(40, 30),
						new $GM.Point(0, 0),
						new $GM.Point(14, 22)
						),
						"online0": new $GM.MarkerImage( '/static/img/vehicles/truck/0360.png',
								new $GM.Size(40, 30),
								new $GM.Point(0, 0),
								new $GM.Point(14, 22)
								),
								"online45": new $GM.MarkerImage( '/static/img/vehicles/truck/45.png',
										new $GM.Size(40, 30),
										new $GM.Point(0, 0),
										new $GM.Point(14, 22)
										),
										"online90": new $GM.MarkerImage( '/static/img/vehicles/truck/90.png',
												new $GM.Size(40, 30),
												new $GM.Point(0, 0),
												new $GM.Point(14, 22)
												),
												"online135": new $GM.MarkerImage( '/static/img/vehicles/truck/135.png',
														new $GM.Size(40, 30),
														new $GM.Point(0, 0),
														new $GM.Point(14, 22)
														),
														"online180": new $GM.MarkerImage( '/static/img/vehicles/truck/180.png',
																new $GM.Size(40, 30),
																new $GM.Point(0, 0),
																new $GM.Point(14, 22)
																),
																"online225": new $GM.MarkerImage( '/static/img/vehicles/truck/225.png',
																		new $GM.Size(40, 30),
																		new $GM.Point(0, 0),
																		new $GM.Point(14, 22)
																		),
																		"online270": new $GM.MarkerImage( '/static/img/vehicles/truck/270.png',
																				new $GM.Size(40, 30),
																				new $GM.Point(0, 0),
																				new $GM.Point(14, 22)
																				),
																				"online315": new $GM.MarkerImage( '/static/img/vehicles/truck/315.png',
																						new $GM.Size(40, 30),
																						new $GM.Point(0, 0),
																						new $GM.Point(14, 22)
																						),
																						"offline": new $GM.MarkerImage( '/static/img/vehicles/truck/offline.png',
																								new $GM.Size(40, 30),
																								new $GM.Point(0, 0),
																								new $GM.Point(14, 22)
																								),
																								"stopped": new $GM.MarkerImage( '/static/img/vehicles/truck/stopped.png',
																										new $GM.Size(40, 30),
																										new $GM.Point(0, 0),
																										new $GM.Point(14, 22)
																										),
																										"offroad": new $GM.MarkerImage( '/static/img/vehicles/truck/offline.png',
																												new $GM.Size(40, 30),
																												new $GM.Point(0, 0),
																												new $GM.Point(14, 22)
																												)
	},

	"custom-18":{
		"idle": new $GM.MarkerImage( '/static/img/vehicles/van/idle.png',
				new $GM.Size(44, 36),
				new $GM.Point(0, 0),
				new $GM.Point(14, 22)
				),
				"online": new $GM.MarkerImage( '/static/img/vehicles/van/van.png',
						new $GM.Size(44, 36),
						new $GM.Point(0, 0),
						new $GM.Point(14, 22)
						),
						"online0": new $GM.MarkerImage( '/static/img/vehicles/van/0360.png',
								new $GM.Size(44, 36),
								new $GM.Point(0, 0),
								new $GM.Point(14, 22)
								),
								"online45": new $GM.MarkerImage( '/static/img/vehicles/van/45.png',
										new $GM.Size(44, 36),
										new $GM.Point(0, 0),
										new $GM.Point(14, 22)
										),
										"online90": new $GM.MarkerImage( '/static/img/vehicles/van/90.png',
												new $GM.Size(44, 36),
												new $GM.Point(0, 0),
												new $GM.Point(14, 22)
												),
												"online135": new $GM.MarkerImage( '/static/img/vehicles/van/135.png',
														new $GM.Size(44, 36),
														new $GM.Point(0, 0),
														new $GM.Point(14, 22)
														),
														"online180": new $GM.MarkerImage( '/static/img/vehicles/van/180.png',
																new $GM.Size(44, 36),
																new $GM.Point(0, 0),
																new $GM.Point(14, 22)
																),
																"online225": new $GM.MarkerImage( '/static/img/vehicles/van/225.png',
																		new $GM.Size(44, 36),
																		new $GM.Point(0, 0),
																		new $GM.Point(14, 22)
																		),
																		"online270": new $GM.MarkerImage( '/static/img/vehicles/van/270.png',
																				new $GM.Size(44, 36),
																				new $GM.Point(0, 0),
																				new $GM.Point(14, 22)
																				),
																				"online315": new $GM.MarkerImage( '/static/img/vehicles/van/315.png',
																						new $GM.Size(44, 36),
																						new $GM.Point(0, 0),
																						new $GM.Point(14, 22)
																						),
																						"offline": new $GM.MarkerImage( '/static/img/vehicles/van/offline.png',
																								new $GM.Size(44, 36),
																								new $GM.Point(0, 0),
																								new $GM.Point(14, 22)
																								),
																								"stopped": new $GM.MarkerImage( '/static/img/vehicles/van/stopped.png',
																										new $GM.Size(44, 36),
																										new $GM.Point(0, 0),
																										new $GM.Point(14, 22)
																										),
																										"offroad": new $GM.MarkerImage( '/static/img/vehicles/van/offline.png',
																												new $GM.Size(44, 36),
																												new $GM.Point(0, 0),
																												new $GM.Point(14, 22)
																												)
	},

	"custom-19":{
		"idle": new $GM.MarkerImage( '/static/img/vehicles/fueltanker/idle.png',
				new $GM.Size(50, 40),
				new $GM.Point(0, 0),
				new $GM.Point(14, 22)
				),
				"online": new $GM.MarkerImage( '/static/img/vehicles/fueltanker/fueltanker.png',
						new $GM.Size(50, 40),
						new $GM.Point(0, 0),
						new $GM.Point(14, 22)
						),
						"online0": new $GM.MarkerImage( '/static/img/vehicles/fueltanker/0360.png',
								new $GM.Size(50, 40),
								new $GM.Point(0, 0),
								new $GM.Point(14, 22)
								),
								"online45": new $GM.MarkerImage( '/static/img/vehicles/fueltanker/45.png',
										new $GM.Size(50, 40),
										new $GM.Point(0, 0),
										new $GM.Point(14, 22)
										),
										"online90": new $GM.MarkerImage( '/static/img/vehicles/fueltanker/90.png',
												new $GM.Size(50, 40),
												new $GM.Point(0, 0),
												new $GM.Point(14, 22)
												),
												"online135": new $GM.MarkerImage( '/static/img/vehicles/fueltanker/135.png',
														new $GM.Size(50, 40),
														new $GM.Point(0, 0),
														new $GM.Point(14, 22)
														),
														"online180": new $GM.MarkerImage( '/static/img/vehicles/fueltanker/180.png',
																new $GM.Size(50, 40),
																new $GM.Point(0, 0),
																new $GM.Point(14, 22)
																),
																"online225": new $GM.MarkerImage( '/static/img/vehicles/fueltanker/225.png',
																		new $GM.Size(50, 40),
																		new $GM.Point(0, 0),
																		new $GM.Point(14, 22)
																		),
																		"online270": new $GM.MarkerImage( '/static/img/vehicles/fueltanker/270.png',
																				new $GM.Size(50, 40),
																				new $GM.Point(0, 0),
																				new $GM.Point(14, 22)
																				),
																				"online315": new $GM.MarkerImage( '/static/img/vehicles/fueltanker/315.png',
																						new $GM.Size(50, 40),
																						new $GM.Point(0, 0),
																						new $GM.Point(14, 22)
																						),
																						"offline": new $GM.MarkerImage( '/static/img/vehicles/fueltanker/offline.png',
																								new $GM.Size(50, 40),
																								new $GM.Point(0, 0),
																								new $GM.Point(14, 22)
																								),
																								"stopped": new $GM.MarkerImage( '/static/img/vehicles/fueltanker/stopped.png',
																										new $GM.Size(50, 40),
																										new $GM.Point(0, 0),
																										new $GM.Point(14, 22)
																										),
																										"offroad": new $GM.MarkerImage( '/static/img/vehicles/fueltanker/offline.png',
																												new $GM.Size(50, 40),
																												new $GM.Point(0, 0),
																												new $GM.Point(14, 22)
																												)
	},
	"custom-20":{
		"idle": new $GM.MarkerImage( '/static/img/vehicles/tncsc/20.png',
				new $GM.Size(60, 66),
				new $GM.Point(0, 0),
				new $GM.Point(14, 22)
		/*url: '/static/img/vehicles/blurb/stopped.png',
			 new $GM.Size(20, 35),
			 new $GM.Point(2, 0),
			 new $GM.Point(32, 40)*/
				),
				"idleblurb": new $GM.MarkerImage( '/static/img/vehicles/tncsc/20.png',
						new $GM.Size(20, 35),
						new $GM.Point(2, 0),
						new $GM.Point(32, 40)
						),
						"online": new $GM.MarkerImage( '/static/img/vehicles/tncsc/20.png',
								new $GM.Size(60, 66),
								new $GM.Point(0, 0),
								new $GM.Point(14, 22)
								),
								"online0": new $GM.MarkerImage( '/static/img/vehicles/tncsc/20.png',
										new $GM.Size(60, 66),
										new $GM.Point(0, 0),
										new $GM.Point(14, 22)
										),
										"online45": new $GM.MarkerImage( '/static/img/vehicles/tncsc/20.png',
												new $GM.Size(60, 66),
												new $GM.Point(0, 0),
												new $GM.Point(14, 22)
												),
												"online90": new $GM.MarkerImage( '/static/img/vehicles/tncsc/20.png',
														new $GM.Size(60, 66),
														new $GM.Point(0, 0),
														new $GM.Point(14, 22)
														),
														"online135": new $GM.MarkerImage( '/static/img/vehicles/tncsc/20.png',
																new $GM.Size(60, 66),
																new $GM.Point(0, 0),
																new $GM.Point(14, 22)
																),
																"online180": new $GM.MarkerImage( '/static/img/vehicles/tncsc/20.png',
																		new $GM.Size(60, 66),
																		new $GM.Point(0, 0),
																		new $GM.Point(14, 22)
																		),
																		"online225": new $GM.MarkerImage( '/static/img/vehicles/tncsc/20.png',
																				new $GM.Size(60, 66),
																				new $GM.Point(0, 0),
																				new $GM.Point(14, 22)
																				),
																				"online270": new $GM.MarkerImage( '/static/img/vehicles/tncsc/20.png',
																						new $GM.Size(60, 66),
																						new $GM.Point(0, 0),
																						new $GM.Point(14, 22)
																						),
																						"online315": new $GM.MarkerImage( '/static/img/vehicles/tncsc/20.png',
																								new $GM.Size(60, 66),
																								new $GM.Point(0, 0),
																								new $GM.Point(14, 22)
																								),
																								"offline": new $GM.MarkerImage( '/static/img/vehicles/tncsc/20.png',
																										new $GM.Size(60, 66),
																										new $GM.Point(0, 0),
																										new $GM.Point(14, 22)
																										),
																										"stopped": new $GM.MarkerImage( '/static/img/vehicles/tncsc/20.png',
																												new $GM.Size(60, 66),
																												new $GM.Point(0, 0),
																												new $GM.Point(14, 22)
																												),
																												"offroad": new $GM.MarkerImage( '/static/img/vehicles/tncsc/20.png',
																														new $GM.Size(60, 66),
																														new $GM.Point(0, 0),
																														new $GM.Point(14, 22)
																														)
	},
	"custom-21":{
		"idle": new $GM.MarkerImage( '/static/img/vehicles/tncsc/idle.png',
				new $GM.Size(60, 66),
				new $GM.Point(0, 0),
				new $GM.Point(14, 22)
		/*url: '/static/img/vehicles/blurb/stopped.png',
			 new $GM.Size(20, 35),
			 new $GM.Point(2, 0),
			 new $GM.Point(32, 40)*/
				),
				"online": new $GM.MarkerImage( '/static/img/vehicles/tncsc/21.png',
						new $GM.Size(60, 66),
						new $GM.Point(0, 0),
						new $GM.Point(14, 22)
						),
						"online0": new $GM.MarkerImage( '/static/img/vehicles/tncsc/0360.png',
								new $GM.Size(60, 66),
								new $GM.Point(0, 0),
								new $GM.Point(14, 22)
								),
								"online45": new $GM.MarkerImage( '/static/img/vehicles/tncsc/45.png',
										new $GM.Size(60, 66),
										new $GM.Point(0, 0),
										new $GM.Point(14, 22)
										),
										"online90": new $GM.MarkerImage( '/static/img/vehicles/tncsc/90.png',
												new $GM.Size(60, 66),
												new $GM.Point(0, 0),
												new $GM.Point(14, 22)
												),
												"online135": new $GM.MarkerImage( '/static/img/vehicles/tncsc/135.png',
														new $GM.Size(60, 66),
														new $GM.Point(0, 0),
														new $GM.Point(14, 22)
														),
														"online180": new $GM.MarkerImage( '/static/img/vehicles/tncsc/180.png',
																new $GM.Size(60, 66),
																new $GM.Point(0, 0),
																new $GM.Point(14, 22)
																),
																"online225": new $GM.MarkerImage( '/static/img/vehicles/tncsc/225.png',
																		new $GM.Size(60, 66),
																		new $GM.Point(0, 0),
																		new $GM.Point(14, 22)
																		),
																		"online270": new $GM.MarkerImage( '/static/img/vehicles/tncsc/270.png',
																				new $GM.Size(60, 66),
																				new $GM.Point(0, 0),
																				new $GM.Point(14, 22)
																				),
																				"online315": new $GM.MarkerImage( '/static/img/vehicles/tncsc/315.png',
																						new $GM.Size(60, 66),
																						new $GM.Point(0, 0),
																						new $GM.Point(14, 22)
																						),
																						"offline": new $GM.MarkerImage(
																								'/static/img/vehicles/tncsc/offline.png',
																								new $GM.Size(60, 66),
																								new $GM.Point(0, 0),
																								new $GM.Point(14, 22)
																								),
																								"stopped": new $GM.MarkerImage( '/static/img/vehicles/tncsc/stopped.png',
																										new $GM.Size(60, 66),
																										new $GM.Point(0, 0),
																										new $GM.Point(14, 22)
																										),
																										"offroad": new $GM.MarkerImage(
																												'/static/img/vehicles/tncsc/offline.png',
																												new $GM.Size(60, 66),
																												new $GM.Point(0, 0),
																												new $GM.Point(14, 22)
																												)
	},
	"custom-22":{
		"idle": new $GM.MarkerImage( '/static/img/vehicles/tncsc/22.png',
				new $GM.Size(60, 66),
				new $GM.Point(0, 0),
				new $GM.Point(14, 22)
		/*url: '/static/img/vehicles/blurb/stopped.png',
			 new $GM.Size(20, 35),
			 new $GM.Point(2, 0),
			 new $GM.Point(32, 40)*/
				),
				"idleblurb": new $GM.MarkerImage( '/static/img/vehicles/tncsc/22.png',
						new $GM.Size(20, 35),
						new $GM.Point(2, 0),
						new $GM.Point(32, 40)
						),
						"online": new $GM.MarkerImage( '/static/img/vehicles/tncsc/22.png',
								new $GM.Size(60, 66),
								new $GM.Point(0, 0),
								new $GM.Point(14, 22)
								),
								"online0": new $GM.MarkerImage( '/static/img/vehicles/tncsc/22.png',
										new $GM.Size(60, 66),
										new $GM.Point(0, 0),
										new $GM.Point(14, 22)
										),
										"online45": new $GM.MarkerImage( '/static/img/vehicles/tncsc/22.png',
												new $GM.Size(60, 66),
												new $GM.Point(0, 0),
												new $GM.Point(14, 22)
												),
												"online90": new $GM.MarkerImage( '/static/img/vehicles/tncsc/22.png',
														new $GM.Size(60, 66),
														new $GM.Point(0, 0),
														new $GM.Point(14, 22)
														),
														"online135": new $GM.MarkerImage( '/static/img/vehicles/tncsc/22.png',
																new $GM.Size(60, 66),
																new $GM.Point(0, 0),
																new $GM.Point(14, 22)
																),
																"online180": new $GM.MarkerImage( '/static/img/vehicles/tncsc/22.png',
																		new $GM.Size(60, 66),
																		new $GM.Point(0, 0),
																		new $GM.Point(14, 22)
																		),
																		"online225": new $GM.MarkerImage( '/static/img/vehicles/tncsc/22.png',
																				new $GM.Size(60, 66),
																				new $GM.Point(0, 0),
																				new $GM.Point(14, 22)
																				),
																				"online270": new $GM.MarkerImage( '/static/img/vehicles/tncsc/22.png',
																						new $GM.Size(60, 66),
																						new $GM.Point(0, 0),
																						new $GM.Point(14, 22)
																						),
																						"online315": new $GM.MarkerImage( '/static/img/vehicles/tncsc/22.png',
																								new $GM.Size(60, 66),
																								new $GM.Point(0, 0),
																								new $GM.Point(14, 22)
																								),
																								"offline": new $GM.MarkerImage( '/static/img/vehicles/tncsc/22.png',
																										new $GM.Size(60, 66),
																										new $GM.Point(0, 0),
																										new $GM.Point(14, 22)
																										),
																										"stopped": new $GM.MarkerImage( '/static/img/vehicles/tncsc/22.png',
																												new $GM.Size(60, 66),
																												new $GM.Point(0, 0),
																												new $GM.Point(14, 22)
																												),
																												"offroad": new $GM.MarkerImage( '/static/img/vehicles/tncsc/22.png',
																														new $GM.Size(60, 66),
																														new $GM.Point(0, 0),
																														new $GM.Point(14, 22)
																														)
	},
	"custom-25":{
		"idle": new $GM.MarkerImage( '/static/img/vehicles/tncsc/25.png',
				new $GM.Size(60, 66),
				new $GM.Point(0, 0),
				new $GM.Point(14, 22)
		/*url: '/static/img/vehicles/blurb/stopped.png',
			 new $GM.Size(20, 35),
			 new $GM.Point(2, 0),
			 new $GM.Point(32, 40)*/
				),
				"idleblurb": new $GM.MarkerImage( '/static/img/vehicles/tncsc/25.png',
						new $GM.Size(20, 35),
						new $GM.Point(2, 0),
						new $GM.Point(32, 40)
						),
						"online": new $GM.MarkerImage( '/static/img/vehicles/tncsc/25.png',
								new $GM.Size(60, 66),
								new $GM.Point(0, 0),
								new $GM.Point(14, 22)
								),
								"online0": new $GM.MarkerImage( '/static/img/vehicles/tncsc/25.png',
										new $GM.Size(60, 66),
										new $GM.Point(0, 0),
										new $GM.Point(14, 22)
										),
										"online45": new $GM.MarkerImage( '/static/img/vehicles/tncsc/25.png',
												new $GM.Size(60, 66),
												new $GM.Point(0, 0),
												new $GM.Point(14, 22)
												),
												"online90": new $GM.MarkerImage( '/static/img/vehicles/tncsc/25.png',
														new $GM.Size(60, 66),
														new $GM.Point(0, 0),
														new $GM.Point(14, 22)
														),
														"online135": new $GM.MarkerImage( '/static/img/vehicles/tncsc/25.png',
																new $GM.Size(60, 66),
																new $GM.Point(0, 0),
																new $GM.Point(14, 22)
																),
																"online180": new $GM.MarkerImage( '/static/img/vehicles/tncsc/25.png',
																		new $GM.Size(60, 66),
																		new $GM.Point(0, 0),
																		new $GM.Point(14, 22)
																		),
																		"online225": new $GM.MarkerImage( '/static/img/vehicles/tncsc/25.png',
																				new $GM.Size(60, 66),
																				new $GM.Point(0, 0),
																				new $GM.Point(14, 22)
																				),
																				"online270": new $GM.MarkerImage( '/static/img/vehicles/tncsc/25.png',
																						new $GM.Size(60, 66),
																						new $GM.Point(0, 0),
																						new $GM.Point(14, 22)
																						),
																						"online315": new $GM.MarkerImage( '/static/img/vehicles/tncsc/25.png',
																								new $GM.Size(60, 66),
																								new $GM.Point(0, 0),
																								new $GM.Point(14, 22)
																								),
																								"offline": new $GM.MarkerImage( '/static/img/vehicles/tncsc/25.png',
																										new $GM.Size(60, 66),
																										new $GM.Point(0, 0),
																										new $GM.Point(14, 22)
																										),
																										"stopped": new $GM.MarkerImage( '/static/img/vehicles/tncsc/25.png',
																												new $GM.Size(60, 66),
																												new $GM.Point(0, 0),
																												new $GM.Point(14, 22)
																												),
																												"offroad": new $GM.MarkerImage( '/static/img/vehicles/tncsc/25.png',
																														new $GM.Size(60, 66),
																														new $GM.Point(0, 0),
																														new $GM.Point(14, 22)
																														)
	},"custom-26":{

		"idle": new $GM.MarkerImage( '/static/img/vehicles/shsm/dispensary/26.png',
				new $GM.Size(60, 66),
				new $GM.Point(0, 0),
				new $GM.Point(14, 22)
		/*url: '/static/img/vehicles/blurb/stopped.png',
			 new $GM.Size(20, 35),
			 new $GM.Point(2, 0),
			 new $GM.Point(32, 40)*/
				),
				"idleblurb": new $GM.MarkerImage( '/static/img/vehicles/shsm/dispensary/26.png',
						new $GM.Size(20, 35),
						new $GM.Point(2, 0),
						new $GM.Point(32, 40)
						),
						"online": new $GM.MarkerImage( '/static/img/vehicles/shsm/dispensary/26.png',
								new $GM.Size(60, 66),
								new $GM.Point(0, 0),
								new $GM.Point(14, 22)
								),
								"online0": new $GM.MarkerImage( '/static/img/vehicles/shsm/dispensary/26.png',
										new $GM.Size(60, 66),
										new $GM.Point(0, 0),
										new $GM.Point(14, 22)
										),
										"online45": new $GM.MarkerImage( '/static/img/vehicles/shsm/dispensary/26.png',
												new $GM.Size(60, 66),
												new $GM.Point(0, 0),
												new $GM.Point(14, 22)
												),
												"online90": new $GM.MarkerImage( '/static/img/vehicles/shsm/dispensary/26.png',
														new $GM.Size(60, 66),
														new $GM.Point(0, 0),
														new $GM.Point(14, 22)
														),
														"online135": new $GM.MarkerImage( '/static/img/vehicles/shsm/dispensary/26.png',
																new $GM.Size(60, 66),
																new $GM.Point(0, 0),
																new $GM.Point(14, 22)
																),
																"online180": new $GM.MarkerImage( '/static/img/vehicles/shsm/dispensary/26.png',
																		new $GM.Size(60, 66),
																		new $GM.Point(0, 0),
																		new $GM.Point(14, 22)
																		),
																		"online225": new $GM.MarkerImage( '/static/img/vehicles/shsm/dispensary/26.png',
																				new $GM.Size(60, 66),
																				new $GM.Point(0, 0),
																				new $GM.Point(14, 22)
																				),
																				"online270": new $GM.MarkerImage( '/static/img/vehicles/shsm/dispensary/26.png',
																						new $GM.Size(60, 66),
																						new $GM.Point(0, 0),
																						new $GM.Point(14, 22)
																						),
																						"online315": new $GM.MarkerImage( '/static/img/vehicles/shsm/dispensary/26.png',
																								new $GM.Size(60, 66),
																								new $GM.Point(0, 0),
																								new $GM.Point(14, 22)
																								),
																								"offline": new $GM.MarkerImage( '/static/img/vehicles/shsm/dispensary/26.png',
																										new $GM.Size(60, 66),
																										new $GM.Point(0, 0),
																										new $GM.Point(14, 22)
																										),
																										"stopped": new $GM.MarkerImage( '/static/img/vehicles/shsm/dispensary/26.png',
																												new $GM.Size(60, 66),
																												new $GM.Point(0, 0),
																												new $GM.Point(14, 22)
																												),
																												"offroad": new $GM.MarkerImage( '/static/img/vehicles/shsm/dispensary/26.png',
																														new $GM.Size(60, 66),
																														new $GM.Point(0, 0),
																														new $GM.Point(14, 22)
																														)
	},"custom-27":{
		"idle": new $GM.MarkerImage( '/static/img/vehicles/shsm/support/27.png',
				new $GM.Size(60, 66),
				new $GM.Point(0, 0),
				new $GM.Point(14, 22)
				),
				"idleblurb": new $GM.MarkerImage( '/static/img/vehicles/shsm/support/27.png',
						new $GM.Size(20, 35),
						new $GM.Point(2, 0),
						new $GM.Point(32, 40)
						),
						"online": new $GM.MarkerImage( '/static/img/vehicles/shsm/support/27.png',
								new $GM.Size(60, 66),
								new $GM.Point(0, 0),
								new $GM.Point(14, 22)
								),
								"online0": new $GM.MarkerImage( '/static/img/vehicles/shsm/support/27.png',
										new $GM.Size(60, 66),
										new $GM.Point(0, 0),
										new $GM.Point(14, 22)
										),
										"online45": new $GM.MarkerImage( '/static/img/vehicles/shsm/support/27.png',
												new $GM.Size(60, 66),
												new $GM.Point(0, 0),
												new $GM.Point(14, 22)
												),
												"online90": new $GM.MarkerImage( '/static/img/vehicles/shsm/support/27.png',
														new $GM.Size(60, 66),
														new $GM.Point(0, 0),
														new $GM.Point(14, 22)
														),
														"online135": new $GM.MarkerImage( '/static/img/vehicles/shsm/support/27.png',
																new $GM.Size(60, 66),
																new $GM.Point(0, 0),
																new $GM.Point(14, 22)
																),
																"online180": new $GM.MarkerImage( '/static/img/vehicles/shsm/support/27.png',
																		new $GM.Size(60, 66),
																		new $GM.Point(0, 0),
																		new $GM.Point(14, 22)
																		),
																		"online225": new $GM.MarkerImage( '/static/img/vehicles/shsm/support/27.png',
																				new $GM.Size(60, 66),
																				new $GM.Point(0, 0),
																				new $GM.Point(14, 22)
																				),
																				"online270": new $GM.MarkerImage( '/static/img/vehicles/shsm/support/27.png',
																						new $GM.Size(60, 66),
																						new $GM.Point(0, 0),
																						new $GM.Point(14, 22)
																						),
																						"online315": new $GM.MarkerImage( '/static/img/vehicles/shsm/support/27.png',
																								new $GM.Size(60, 66),
																								new $GM.Point(0, 0),
																								new $GM.Point(14, 22)
																								),
																								"offline": new $GM.MarkerImage( '/static/img/vehicles/shsm/support/27.png',
																										new $GM.Size(60, 66),
																										new $GM.Point(0, 0),
																										new $GM.Point(14, 22)
																										),
																										"stopped": new $GM.MarkerImage( '/static/img/vehicles/shsm/support/27.png',
																												new $GM.Size(60, 66),
																												new $GM.Point(0, 0),
																												new $GM.Point(14, 22)
																												),
																												"offroad": new $GM.MarkerImage( '/static/img/vehicles/shsm/support/27.png',
																														new $GM.Size(60, 66),
																														new $GM.Point(0, 0),
																														new $GM.Point(14, 22)
																														)

	},
	}

	});
	$L.extend($W.Maps, $YU.AttributeProvider);
	$L.augmentObject($W.Maps.prototype, {
		/**
		 * Initializes the Attributes
		 */
		initAttributes: function() {
		/**
		 * @attribute map
		 * @description Current Map represented by the object.
		 * @type Object
		 */
		this.setAttributeConfig($W.Maps.ATTR_MAP, {
			value: null
		});
	},
	initFleetMap: function(el, oArgs) {
		this.initAttributes();
		var oCenterLatLng = $W.Maps.DEFAULT_LATLNG;
		var oConfig = {
				zoom: 8,
				minZoom: 3,
				center: oCenterLatLng,
				mapTypeId: $GM.MapTypeId.ROADMAP,
				panControl: true,
				zoomControl: true,
				mapTypeControl: true,
				scaleControl: false,
				streetViewControl: false,
				overviewMapControl: true		
		};
		oGMap = new $GM.Map(el, oConfig);
		this.set($W.Maps.ATTR_MAP, oGMap);
		var container = document.createElement('div');
		/*	The below snippet is commented to avoid custom panning tool from Google map*/
		//			var fleet = new $W.Maps.FleetZoomBar(container, oGMap);
		//			oGMap.controls[$GM.ControlPosition.TOP_LEFT].push(fleet);
		/**
		 * Adding distance and Location search option on the Map.
		 */
		if(isSHSMClient){
			if(new String(window.location).indexOf('livetrack') !=-1){
				$W.Maps.addDistanceOption();
				$W.Maps.searchLocation();
			}
		}

		$GM.event.trigger(oGMap, 'resize');
	},
	checkResize: function() {
	}

	});
	$W.Maps.addKMLLayers = function(oArgs) {
		var URL='/kml/'+oArgs.fileName;
		var kmlParser=new geoXML3.parser({
			map: oGMap,
			zoom:false
		});
		kmlParser.parse(URL);
	};
	$W.Maps.searchLocation = function() {
		var searchDiv = document.createElement('div');
		searchDiv.style.backgroundColor = 'white';
		searchDiv.style.backgroundImage="url('/static/img/icons/search.png')";
		searchDiv.style.backgroundRepeat="no-repeat";
		searchDiv.style.backgroundPosition="right";
		searchDiv.style.backgroundSize="10%";
		searchDiv.style.padding = '8px';
		searchDiv.style.borderStyle = 'solid';
		searchDiv.style.borderWidth = '1px';
		searchDiv.style.borderColor = '#717B87';
		searchDiv.style.width = "345px";
		searchDiv.style.height = "18px";
		searchDiv.style.cursor = 'pointer';
		searchDiv.style.marginLeft = '85px';
		searchDiv.style.marginTop = '6px';
		searchDiv.style.boxShadow='0px 2px 4px rgba(0, 0, 0, 0.4)';


		var searchInput = document.createElement('input');
		searchInput.id = 'textField';
		searchInput.type = "text";
		searchInput.style.color = "rgb(0,0,0)";
		searchInput.value = "Enter a location";
		searchInput.style.fontSize = '15px';
		searchInput.style.width = "90%";
		searchInput.style.height = "100%";
		searchInput.style.position ='relative';
		searchInput.style.padding = '1px 1px 1px 1px';
		searchInput.style.borderStyle = 'none';
		searchDiv.appendChild(searchInput);

		oGMap.controls[$GM.ControlPosition.TOP_LEFT].push(searchDiv);

		$GM.event.addDomListener(searchInput, 'click', function() {
			var input = document.getElementById('textField');
			if(input.value=='Enter a location'){
				input.value = "";	
			}
			input.Placeholder="Enter a location";
			var autocomplete = new $GM.places.Autocomplete(input);
			autocomplete.bindTo('bounds', oGMap);

			//var infowindow = new $GM.InfoWindow();
			var marker = new $GM.Marker({
				map: oGMap
			});

			$GM.event.addListener(autocomplete, 'place_changed', function() {
				//infowindow.close();
				marker.setVisible(false);
				input.className = '';
				var place = autocomplete.getPlace();
				if (!place.geometry) {
					// Inform the user that the place was not found and return.
					input.className = 'notfound';
					return;
				}

				// If the place has a geometry, then present it on a map.
				if (place.geometry.viewport) {
					oGMap.fitBounds(place.geometry.viewport);
				} else {
					oGMap.setCenter(place.geometry.location);
					oGMap.setZoom(17);  // Why 17? Because it looks good.
				}
				marker.setIcon(/** @type {$GM.Icon} */({
					url: place.icon,
					size: new $GM.Size(71, 71),
					origin: new $GM.Point(0, 0),
					anchor: new $GM.Point(17, 34),
					scaledSize: new $GM.Size(35, 35)
				}));
				marker.setPosition(place.geometry.location);
				marker.setVisible(true);

				var address = '';
				if (place.address_components) {
					address = [
					           (place.address_components[0] && place.address_components[0].short_name || ''),
					           (place.address_components[1] && place.address_components[1].short_name || ''),
					           (place.address_components[2] && place.address_components[2].short_name || '')
					           ].join(' ');
				}
				//infowindow.setContent('<div><strong>' + place.name + '</strong><br>' + address);
				//infowindow.open(oGMap, marker);
			});
		});
	},

	$W.Maps.addDistanceOption = function(origin,destination) {
		var controlDiv = document.createElement('div');
		controlDiv.style.paddingTop = '6px';
		controlDiv.index = 1;
		controlDiv.id = 'getDistanceId';

		var controlUI = document.createElement('div');
		controlUI.style.backgroundColor = 'activeborder';
		controlUI.style.borderStyle = 'solid';
		controlUI.style.borderWidth = '1px';
		controlUI.style.borderColor = '#717B87';
		controlUI.style.color = 'GrayText';
		controlUI.style.cursor = 'pointer';
		controlUI.style.textAlign = 'center';
		controlUI.style.boxShadow='0px 2px 4px rgba(0, 0, 0, 0.4)';
		controlUI.title = 'Click to get the distance';
		controlDiv.appendChild(controlUI);

		// Set CSS for the control interior.
		var controlText = document.createElement('div');
		controlText.style.fontFamily = 'Arial,sans-serif';
		controlText.style.position ='relative';
		controlText.style.fontSize = '13px';
		controlText.style.fontWeight = 'bold';
		controlText.style.padding = '1px 6px 0px 0px';
		//controlText.style.color = 'black';
		controlText.innerHTML = 'Get Distance';
		controlUI.appendChild(controlText);
		oGMap.controls[$GM.ControlPosition.TOP_RIGHT].push(controlDiv);

		var distanceDiv = document.createElement('div');
		distanceDiv.id='distanceDiv';
		//distanceDiv.style.visibility='hidden';
		distanceDiv.style.paddingTop = '1px';
		distanceDiv.style.paddingRight = '5px';

		var distanceUI = document.createElement('div');
		distanceUI.style.backgroundColor = 'white';
		distanceUI.style.borderStyle = 'solid';
		distanceUI.style.borderWidth = '1px';
		distanceUI.style.borderColor = '#717B87';
		distanceUI.style.cursor = 'pointer';
		distanceUI.style.textAlign = 'center';
		distanceUI.style.boxShadow='0px 2px 4px rgba(0, 0, 0, 0.4)';
		distanceDiv.appendChild(distanceUI);

		// Set CSS for the control interior.
		var distanceText = document.createElement('div');
		distanceText.id='distanceText';
		distanceText.style.fontFamily = 'Arial,sans-serif';
		distanceText.style.position ='relative';
		distanceText.style.fontSize = '13px';
		distanceText.style.padding = '1px 6px 0px 0px';
		distanceText.style.color = 'black';
		distanceUI.appendChild(distanceText);
		oGMap.controls[$GM.ControlPosition.TOP_RIGHT].push(distanceDiv);

		// Setup the click event listeners: simply set the map.
		$GM.event.addDomListener(controlUI, "click", function() {
			controlUI.title = 'Click to enable distance.';
			controlUI.style.backgroundColor = 'white';
			controlText.style.color = 'black';
			isDistanceEnabled=true;
		});	
		$GM.event.addListener(oGMap, "click", function(events) {
			if(isDistanceEnabled){
				var marker=new $GM.Marker({map:oGMap,position:events.latLng});
				allMapMarkers.push(marker);
				pointerArray.push(events.latLng);
			}
		});	
		$GM.event.addDomListener(controlUI, 'click', function() {
			if(isDistanceEnabled){
				if(pointerArray.length>=2){
					var directionsService = new $GM.DirectionsService();
					// Create a renderer for directions and bind it to the map.
					var rendererOptions = {
							map: oGMap,
							draggable: true,
							suppressMarkers: false
					}
					if(directionsDisplay != null) {
						directionsDisplay.setMap(null);
						directionsDisplay = null;
					}
					directionsDisplay = new $GM.DirectionsRenderer(rendererOptions);
					directionsDisplay.setMap(oGMap);
					$GM.event.addListener(directionsDisplay, 'directions_changed', function() {
						var total = 0;
						var myroute = directionsDisplay.directions.routes[0];
						var text = '<strong>From : </strong>';
						text += myroute.legs[0].start_address;
						text += '<br>';
						text += '<strong>To : </strong>';
						text += myroute.legs[0].end_address;
						text += '<br>';
						text += '<strong>Distance : </strong>';
						text += myroute.legs[0].distance.text;
						document.getElementById("distanceDiv").style.display='block';
						document.getElementById('distanceText').innerHTML=text;
					});
					var request = {
							origin: pointerArray[0],
							destination: pointerArray[1],
							travelMode: $GM.TravelMode.DRIVING
					};
					directionsService.route(request, function(response, status) {
						if (status == $GM.DirectionsStatus.OK) {
							directionsDisplay.setDirections(response);
						}else{
							alert('Failed to get distance ');
						}
					});
					for(var m=0;m<allMapMarkers.length;m++){
						allMapMarkers[m].setMap(null);
					}
					pointerArray=[];
					isDistanceEnabled=false;
				}else{
					alert('Select two points');
				}	
			}
		});
	};
	/**
	 * Custom Marker Tabbed Info Window for GoogleMaps. Uses Custom Styling and is configurable.
	 * @param {Object} oArgs
	 */
	$W.Maps.FleetInfoWindow = function(oArgs) {
		$W.Maps.FleetInfoWindow.superclass.constructor.call(this);
		this.oBoundsListener = null;
		this.oCloseListener = null;
		this.nHeight = 317;
		this.nWidth = 290;
		this.nOffsetVertical = -295;
		this.nOffsetHorizontal = 5;
		this.elBase = null;
		this.oLatLng = oArgs.position;
		this.oGMap = oArgs.map;
		this.setMap(this.oGMap);
		this.addListeners(this);
		this.isClosed = true;
		/*if(!isKMLRequestSent){
			YAHOO.util.Connect.asyncRequest('GET',  "/fleet/view/controlpanel/?markup=DemoManage&debug=true&module=/blocks/json"+
					"&data=view&subpage=liveData&dataView=assignment&kmlFolderSize=true",this.getKMLFiles,null);	
			isKMLRequestSent=true;
		}*/
	};
	/** 
	 * FleetInfoWindow extends GOverlay class from the Google Maps API.
	 * Part usage of code from http://gmaps-samples-v3.googlecode.com/svn/trunk/infowindow_custom/infowindow-custom.html
	 */
	$L.extend($W.Maps.FleetInfoWindow, $GM.OverlayView, {
		/**
		 * Once initialized , the listeners to the marker's click will execute this function
		 * the $GM.LatLng object container latitude and longitude will be passed in the argument
		 * @param {Object} oArgs
		 */
		drawAndMoveTo: function(oArgs) {
		var oLatLng = oArgs.position;
		var distanceDiv=document.getElementById("distanceDiv");
		if(distanceDiv!=null){
			distanceDiv.style.display='none';
		}
		if(directionsDisplay != null) {
			directionsDisplay.setMap(null);
			directionsDisplay = null;
		}
		if ((!oLatLng.equals(this.oLatLng)) || this.isClosed) {
			this.oLatLng = oLatLng;
			$W.Maps.FleetInfoWindow.superclass.constructor.call(this);
			this.panMap();
			this.createElement();
			if (this.elBase) {
				this.setContent(oArgs);
				if (this.positionWindow()) {
					$D.setStyle(this.elBase, "display", "block");
				}
				this.addListeners(this);
				this.isClosed = false;
			}
		}
	},
	/**
	 * Removes the elements
	 */
	remove: function() {
		if (this.elBase) {
			this.elBase.parentNode.removeChild(this.elBase);
			this.elBase = null;
		}
	},
	/**
	 * Adds Listener
	 * @param {Object} oSelf
	 */
	addListeners: function(oSelf) {
		if ($L.isNull(this.oBoundsListener)) {
			this.oBoundsListener = $GM.event.addListener(this.oGMap, "bounds_changed", function() {
				return oSelf.positionWindow();
			});
		}
		if ($L.isNull(this.oCloseListener) && this.elBase) {
			this.oCloseListener = $E.addListener($D.getElementsByClassName("close-button", null, this.elBase), "click", function(oArgs, oArgsSelf) {
				$D.setStyle(oArgsSelf.elBase, "display", "none");
				$GM.event.removeListener(oArgsSelf.oBoundsListener);
				oArgsSelf.oBoundsListener = null;
				this.isClosed = true;
			}, oSelf, true);
		}
	},
	/**
	 * Redraw the Bar based on the current projection and zoom level
	 */
	draw: function(oArgs) {
	},
	/**
	 * Sets the contents of the tabs based on the arguments passed
	 * @param {Object} oArgs
	 */
	setContent: function(oArgs) {
		if (oArgs && oArgs.content) {
			for (var sTabId in oArgs.content) {
				var elTabContainer = $D.getElementsByClassName("tabcontent-" + sTabId, null, this.elBase)[0];
				if (elTabContainer) {
					elTabContainer.innerHTML = oArgs.content[sTabId];
				}
			}
		}
	},
	/**
	 * Positions the Info Window based on the position of the marker.
	 */
	positionWindow: function() {
		// Calculate the DIV coordinates of two opposite corners of our bounds to
		// get the size and position of our Bar 
		var bResult = true;
		var padLeft = 5;
		var padTop = 25;
		var overlay = new $GM.OverlayView();
		overlay.draw = function() {};
		overlay.setMap(this.oGMap);
		var oPixPosition = overlay.getProjection().fromLatLngToDivPixel(this.oLatLng);
		if (oPixPosition) {
			$D.setStyle(this.elBase, "width", this.nWidth + "px");
			$D.setStyle(this.elBase, "left", (oPixPosition.x + this.nOffsetHorizontal - padLeft) + "px");
			$D.setStyle(this.elBase, "height", this.nHeight + "px");
			$D.setStyle(this.elBase, "top", (oPixPosition.y + this.nOffsetVertical - padTop) + "px");
			bResult = true;
		}
		return bResult;
	},

	getKMLFiles:{
		success: function(o) {
		var oResponse=JSON.parse(o.responseText);
		var aData1=$U.Arrays.mapToArray(oResponse);
		var veh;
		var isdata;
		var allmarkers = {};
		var that=this;
		for(var m=0 ; m< aData1.length;m++){
			var mobject=aData1[m];
			var aData=$U.Arrays.mapToArray(mobject);
			for(var n=0 ; n< aData.length;n++){
				var nobject=aData[n];
				var ndat=$U.Arrays.mapToArray(nobject);
				for(var j=0;j<ndat.length;j++){
					var jobject=ndat[j];
					var obj=$U.Arrays.mapToArray(jobject);
					for(var k=0;k<obj.length;k++){
						$W.Maps.addKMLLayers(obj[k]);
					}
				}
			}
		}
	},
	failure: function(o) {

	},
	},

	/**
	 * Creates the DIV representing this InfoBox in the floatPane.  If the panes
	 * object, retrieved by calling get_panes, is null, remove the element from the
	 * DOM.  If the div exists, but its parent is not the floatPane, move the div
	 * to the new pane.
	 * Called from within draw.  Alternatively, this can be called specifically on
	 * a panes_changed event.
	 */
	createElement: function() {
		var panes = this.getPanes();
		var elBase = this.elBase;
		if (!elBase) {
			// This does not handle changing panes.  You can set the map to be null and
			// then reset the map to move the div.
			var elTemplateContainer = $D.get("template-info-window");
			elBase = ($D.getElementsByClassName("marker-info-window", null, elTemplateContainer)[0]).cloneNode(true);
			elBase.style.display = 'none';
			var tabView = new $YW.TabView(elBase);
			this.elBase = elBase;
			panes.floatPane.appendChild(this.elBase);
		}else if (elBase.parentNode != panes.floatPane) {
			// The panes have changed.  Move the div.
			elBase.parentNode.removeChild(elBase);
			panes.floatPane.appendChild(elBase);
		}
	},
	/**
	 *  Obtained from http://gmaps-samples-v3.googlecode.com/svn/trunk/infowindow_custom/infowindow-custom.html
	 *  Pan the map to fit the InfoBox.
	 */
	panMap: function() {
		// if we go beyond map, pan map
		var map = this.oGMap;
		if (map) {
			var bounds = map.getBounds();
			if (!bounds){
				return;
			} 
			// The position of the infowindow
			var position = this.oLatLng;
			if (position) {
				map.setZoom(18);
				map.setCenter(position);
			}
		}
	}
	});
	/**
	 * Fleet Custom Marker for Google Maps.
	 * @extends $GM.Marker
	 * @param {Object} oArgs
	 */
	$W.Maps.Marker = function(oArgs) {
		var oConfig = $U.cloneObject(oArgs);
		var oOptions = oConfig.options;
		oConfig.icon = $W.Maps.MARKER_SPRITE_CONFIG[oOptions.iconType][oOptions.iconSubType];
		oConfig.options = null;
		$GM.Marker.call(this, oConfig);
	};
	$L.augmentProto($W.Maps.Marker, $GM.Marker);
	$L.augmentObject($W.Maps.Marker.prototype, {
		getPosition: function() {
		return this.getPosition();
	}
	});
	/**
	 * Fleet Custom Class representing latitude and longitude.
	 * @extends $GM.LatLng
	 * @param {Object} oArgs
	 */
	$W.Maps.LatLng = function(oArgs) {
		$W.Maps.LatLng.superclass.constructor.call(this, oArgs.lat, oArgs.lon);
	};
	$L.extend($W.Maps.LatLng, $GM.LatLng);
	/**
	 * Fleet Custom Class representing latitude and longitude bounds.
	 * @extends $GM.LatLng
	 * @param {Object} oArgs
	 */
	$W.Maps.LatLngBounds = function(oArgs) {
		if (!oArgs) {
			oArgs = {};
		}
		$W.Maps.LatLngBounds.superclass.constructor.call(this, oArgs.sw, oArgs.ne);
	};
	$L.extend($W.Maps.LatLngBounds, $GM.LatLngBounds);
	$W.Maps.Event = {        /**
	 * Is A Static Class;
	 */
	};
	$L.augmentObject($W.Maps.Event, $GM.event);
	/**
	 * Function for i10n Zoom levels and url implementation.
	 */
	$W.Maps.CustomGetTileUrl = function(a, b) {

		var z = 17 - b;

		var f = "/maps/"+a.x+"-"+a.y+"-"+(z)+ ".png";		
		return f;

	};


	$W.Maps.getNormalizedCoord =function(coord, zoom) {
		var y = coord.y;
		var x = coord.x;

		// tile range in one direction range is dependent on zoom level
		// 0 = 1 tile, 1 = 2 tiles, 2 = 4 tiles, 3 = 8 tiles, etc
		var tileRange = 1 << zoom;

		// don't repeat across y-axis (vertically)
		if (y < 0 || y >= tileRange) {
			return null;
		}

		// repeat across x-axis
		if (x < 0 || x >= tileRange) {
			x = (x % tileRange + tileRange) % tileRange;
		}

		return {
			x: x,
			y: y
		};
	}
	/**
	 * Function for yahoo Zoom levels and url implementation  
	 */

	$W.Maps.yCustomGetTileUrl = function(a, b) {

		var newY = ((1 << b) >> 1) - 1 - a.y;
		b = b + 1;
		if (b == 1)
			b = 2;
		var f = "http://us.maps1.yimg.com/us.tile.maps.yimg.com/tl?v=4.2&x="
				+ a.x + "&y=" + newY + "&z=" + b + "&r=1";
		return f;
	};
	/**
	 * A Custom Fleet ZoomBar for Google Maps
	 * @extends Gcontrol
	 * @param {Object} map
	 * @param {Object} slideRatio
	 */
	$W.Maps.FleetZoomBar = function(container,map,slideRatio) {
		this.oMap = map;
		if (slideRatio) {
			this.slideRatio = slideRatio;
		}
		this.slideFactor = 50; 
		var el = document.createElement("div");
		this.container = el;
		/**
		 * Commented by naresh.
		 * The below snippet is commented to avoid custom panning tool from Google map
		 * */
		//		$D.addClass(el, 'fleetzoombar');
		//		this.addButton($W.Maps.FleetZoomBar.PAN_EAST);
		//		this.addButton($W.Maps.FleetZoomBar.PAN_WEST);
		//		this.addButton($W.Maps.FleetZoomBar.PAN_NORTH);
		//		this.addButton($W.Maps.FleetZoomBar.PAN_SOUTH);
		//		this.addButton($W.Maps.FleetZoomBar.SLIDER);
		//		this.addButton($W.Maps.FleetZoomBar.ZOOM_BAR);
		//		this.addButton($W.Maps.FleetZoomBar.ZOOM_IN);
		//		this.addButton($W.Maps.FleetZoomBar.PAN_TO_CENTER);
		//		this.addButton($W.Maps.FleetZoomBar.ZOOM_OUT);
		if(i!=0){
			$D.get(this.oMap.getDiv()).appendChild(el);
		}
		else{
			i=1;
		}
		this.slideMinTop = parseInt($D.getStyle(
				this._zoombar, 'top'), 10) + 7;
		this.slideMaxTop = this.slideMinTop
				+ parseInt($D.getStyle(this._zoombar,
						'height'), 10) - 12;
		this.initializeSlider();
		return this.container;
	};
	/**
	 * we are not using custom zoom controllers so commetting.
	 * The below snippet is commented to avoid custom panning tool from Google map
	 * 	@Naresh
	 * 	*/
	//	$L.augmentObject($W.Maps.FleetZoomBar, {
	//		PAN_SOUTH : "pandown",
	//		PAN_NORTH : "panup",
	//		PAN_WEST : "panleft",
	//		PAN_EAST : "panright",
	//		ZOOM_OUT : "zoomout",
	//		ZOOM_IN : "zoomin",
	//		ZOOM_BAR : "zoombar",
	//		SLIDER : "slider",
	//		PAN_TO_CENTER : "panToCenter"
	//	});

	$L.augmentObject(
			$W.Maps.FleetZoomBar.prototype,
			{
				getSlideFactor : function(dim) {
				if (this.slideRatio) {
					if (dim == "width") {
						return this.oMap.getSize().width
								* this.slideRatio;
					} else {
						return this.oMap.getSize().height
								* this.slideRatio;
					}
				} else {
					return this.slideFactor;
				}
			},
			/**
			 * Sets the Slider Position according to new ZoomLevel
			 * @param {Object} option
			 */
			moveSlider : function(option) {
				var baseEl = this.slider;
				var top;
				if (option) {
					top = parseInt($D.getStyle(baseEl, 'top'), 10) - 11;
					if (top >= this.slideMinTop) {
						$D.setStyle(baseEl, 'top', top + "px");
					}
				} else {
					top = parseInt($D.getStyle(baseEl, 'top'), 10) + 11;
					if (top < this.slideMaxTop - 5) {
						$D.setStyle(baseEl, 'top', top + "px");
					}
				}
			},
			/**
			 * Adds a Button and action according to id
			 * @param {Object} id
			 */
			addButton : function(id) {
				var el = document.createElement("div");
				$D.addClass(el, id);
				var parent = this.container;
				var map = this.oMap;
				var zoombar = this;
				parent.appendChild(el);
				switch (id) {
				case $W.Maps.FleetZoomBar.ZOOM_IN:
					$W.Maps.Event.addDomListener(el, "click",
							function() {
						zoombar.moveSlider("zoomin");
						if (map.getZoom() < 18) {
							//map.zoomIn();
							map.setZoom(map.getZoom() +1);

						}
					});
					break;
				case $W.Maps.FleetZoomBar.ZOOM_OUT:
					$W.Maps.Event.addDomListener(el, "click",
							function() {
						zoombar.moveSlider();
						if (map.getZoom() > 0) {
							map.setZoom(map.getZoom() - 1);
						}
					});
					break;
				case $W.Maps.FleetZoomBar.PAN_EAST:
					$W.Maps.Event.addDomListener(el, "click",
							function() {
						var distance = zoombar
								.getSlideFactor("width");
						map.panBy(distance, 0);
					});
					break;
				case $W.Maps.FleetZoomBar.PAN_WEST:
					$W.Maps.Event.addDomListener(el, "click",
							function() {
						var distance = zoombar
								.getSlideFactor("width");
						map.panBy(-distance, 0);
					});
					break;
				case $W.Maps.FleetZoomBar.PAN_NORTH:
					$W.Maps.Event.addDomListener(el, "click",
							function() {
						var distance = zoombar
								.getSlideFactor("height");
						map.panBy(0, distance);
					});
					break;
				case $W.Maps.FleetZoomBar.PAN_SOUTH:
					$W.Maps.Event.addDomListener(el, "click",
							function() {
						var distance = zoombar
								.getSlideFactor("height");
						map.panBy(0, -distance);
					});
					break;
				case $W.Maps.FleetZoomBar.PAN_TO_CENTER:
					$W.Maps.Event.addDomListener(el, "click",
							function() {
						map.panTo($W.Maps.DEFAULT_LATLNG);
					});
					break;
				case $W.Maps.FleetZoomBar.SLIDER:
					this.slider = el;
					var posy;

					$W.Maps.Event
					.addDomListener(
							el,
							"mousedown",
							function(event) {
								if (event.pageY) {
									posy = event.pageY;
								} else if (event.clientY) {
									posy = event.clientY
											+ document.body.scrollTop
											+ document.documentElement.scrollTop;
								}
								this.startDrag = posy;
							});
					$W.Maps.Event.addDomListener(el, "mouseup",
							function(event) {
						this.startDrag = null;
						var diff = parseInt($D.getStyle(el,
								'top'), 10)
								- parseInt($D.getStyle(
										zoombar._zoombar,
										'top'), 10);
						var level = Math.abs(Math
								.floor((diff) / 11));
						map.setZoom(18 - level - 1);
						var newtop = (level) * 11
								+ zoombar.slideMinTop;
						$D.setStyle(el, 'top',
								newtop + 'px');
					});

					$W.Maps.Event
					.addDomListener(
							el,
							"mousemove",
							function(event) {
								if (this.startDrag !== null
										&& typeof this.startDrag !== 'undefined') {
									if (event.pageY) {
										posy = event.pageY;
									} else if (event.clientY) {
										posy = event.clientY
												+ document.body.scrollTop
												+ document.documentElement.scrollTop;
									}
									var top = parseInt($D
											.getStyle(el,
													'top'),
													10)
													- (this.startDrag - posy);
									if ((event.clientY - zoombar
											.pagePosition(zoombar._zoombar)[1]) > 0
											&& (event.clientY - zoombar
													.pagePosition(zoombar._zoombar)[1]) < parseInt(
															$D
															.getStyle(
																	zoombar._zoombar,
																	'height'),
																	10) - 5) {
										$D.setStyle(el,
												'top',
												top + "px");
										this.startDrag = posy;
									}
								}
							});

					break;
				case $W.Maps.FleetZoomBar.ZOOM_BAR:
					this._zoombar = el;
					$W.Maps.Event
					.addDomListener(
							el,
							"click",
							function(event) {
								var posy;
								if (event.pageY) {
									posy = event.pageY;
								} else if (event.clientX
										|| event.clientY) {
									posy = event.clientY
											+ document.body.scrollTop
											+ document.documentElement.scrollTop;
								}
								var zoomtop = zoombar
										.pagePosition(el)[1];
								var levels = Math
										.abs(Math
												.floor((zoomtop - posy) / 11));
								var newtop = zoombar.slideMinTop
										+ ((levels - 1) * 11);
								$D.setStyle(zoombar.slider,
										'top', newtop
										+ "px");
								map
								.setZoom(18 - levels - 1);
							});

					$W.Maps.Event.addDomListener(el, "mouseup",
							function(event) {
						zoombar.slider.startDrag = null;
						var diff = parseInt($D.getStyle(
								zoombar.slider, 'top'), 10)
								- parseInt($D.getStyle(el,
										'top'), 10);
						var level = Math.abs(Math
								.floor((diff) / 11));
						map.setZoom(18 - level - 1);
						var newtop = (level) * 11
								+ zoombar.slideMinTop;
						$D.setStyle(zoombar.slider, 'top',
								newtop + 'px');
					});
					$W.Maps.Event
					.addDomListener(
							el,
							"mousemove",
							function(event) {
								if (zoombar.slider.startDrag !== null
										&& typeof zoombar.slider.startDrag !== 'undefined') {
									var posy;
									if (event.pageY) {
										posy = event.pageY;
									} else if (event.clientY) {
										posy = event.clientY
												+ document.body.scrollTop
												+ document.documentElement.scrollTop;
									}
									var top = parseInt(
											$D
											.getStyle(
													zoombar.slider,
													'top'),
													10)
													- (zoombar.slider.startDrag - posy);
									$D.setStyle(
											zoombar.slider,
											'top', top
											+ "px");
									zoombar.slider.startDrag = posy;
								}
							});

					break;

				}
			},
			/**
			 * Sets the Slider position based on the Map Zoom
			 */

			initializeSlider : function() {
				var newtop = this.slideMinTop + this.oMap.getZoom() * 11;
				$D.setStyle(this.slider, 'top', newtop + "px");
			},

			pagePosition : function(forElement) {
				var valueT = 0, valueL = 0;
				var element = forElement;
				var child = forElement;
				while (element) {
					if (element == document.body) {
						if ($D.getStyle(child, 'position') == 'absolute') {
							break;
						}
					}
					valueT += element.offsetTop || 0;
					valueL += element.offsetLeft || 0;
					child = element;
					try {
						// wrapping this in a try/catch because IE chokes on the offsetParent
						element = element.offsetParent;
					} catch (e) {
						break;
					}
				}
				element = forElement;
				while (element) {
					valueT -= element.scrollTop || 0;
					valueL -= element.scrollLeft || 0;
					element = element.parentNode;
				}
				return [ valueL, valueT ];
			}
			});


})();
