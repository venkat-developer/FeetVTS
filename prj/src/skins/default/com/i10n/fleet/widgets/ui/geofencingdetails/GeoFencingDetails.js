(function() {
	var $W = getPackageForName("com.i10n.fleet.widget.ui");
	var $D = YAHOO.util.Dom;
	var $L = YAHOO.lang;
	var $E = YAHOO.util.Event;
	var $YW = YAHOO.widget;
	var $WU = getPackageForName("com.i10n.fleet.widget.util");
	var $U = getPackageForName("com.i10n.fleet.Utils");
	var $YU = YAHOO.util;
	var $B = YAHOO.Bubbling;
	var $WL = getPackageForName("com.i10n.fleet.widget.lib");
	var $GM = google.maps;
	var oldCenterLatLng;
	var centerLatLng;
	var endLatLng;
	var oldEndLatLng;
	var regionShape;
	var map;
	//var center ;
	var allMapMarkers=[];
	//var radius ;
	var marker=[];
	var point=[];
	var line=[];
	var allMarkers=[];
	var allLat=[],allLng=[];
	var geofencedetails;

	var mouseclickListener;
	var mousemoveListener;
	var PolygonMarkers=[];//Array for Map Markers  
	var PolygonPoints=[];//Array for Polygon Node Markers  
	var Polygon;//Polygon overlay object  
	var bounds = new  $GM.LatLngBounds; //Polygon Bounds  
	var polygon_resizing = false; //To track Polygon Resizing 
	//Polygon Marker/Node icon
	
	
	var latdiff;//for draw circle
    var lngdiff;//for draw circle
	var circle ;//for draw circle
	var distance;
	
	var rectangle;
	
	var currentGeoFenceId;
	var poly;//for draw custom
    var markers = [];//for draw custom
    var path = new google.maps.MVCArray;//for draw custom

	
	var icon = new $GM.MarkerImage( '/static/img/maps/blue-pushpin.png',
			new $GM.Size(60, 66),
			new $GM.Point(0, 0),
			new $GM.Point(14, 22)
			);
	/**
	 * Trip Details Widget
	 *
	 * @param {Object} el
	 * @param {Object} oParams
	 */
	$W.GeoFencingDetails = function(el, oParams) {
		/*Declaring necessary members*/
		this.elBaseElement = el;
		/*Initializing*/
		//Commented By me 
		/*this.setAttributeConfig($W.GeoFencingDetails.ATTR_DISPLAYED_TRIP, {
			value: null
		});*/
		this.initGeoFencingDetails(el, oParams);
		/*Hiding the widget*/
		this.hideGeoFencingDetails();
	};

	/**
	 *  $L.augmentObject($W. {WidgetName}, {
	 *  	{Constant Name} : {Constant Value}
	 *  	{Static Methods/Object Name} : {Static Methods/Object}
	 *  });
	 */
	$L.augmentObject($W.GeoFencingDetails, {
		ATTR_MODIFIABLE_DETAILS: "oModifiableDetails",
		ATTR_DISPLAYED_TRIP: "oDisplayedTripID",
		EVT_TRIP_CHANGED: "oDisplayedTripIDChange",
		EVT_TRIP_DETAILS_MODIFIED: "oModifiableDetailsChange",
		EVT_TRIP_STATUS_MODIFIED: "tripStatusModified",
		TRIP_STATUS: {
		TRIP_STARTED: 0,
		TRIP_STOPPED: 1,
		TRIP_PAUSED: 2
	},
	MODIFIABLE_CLASS: "editable"
	});

	//$L.augmentProto($W.GeoFencingDetails, $YU.AttributeProvider);

	var tripid;
	_oMap: null


	/**
	 * 
	 *   $L.augmentObject($W. {WidgetName}.prototype, {
	 *            **
	 *            * Initializes the widget.
	 *            * @param {Object} el
	 *            * @param {Object} oArgs
	 *            *
	 *		initWidget: function(el, oArgs){
	 *      **
	 *      * Initialization should be done here.
	 *      *
	 *      },
	 *      
	 *      {Other member functions should be placed here}
	 *      
	 *	});
	 */
	$L.augmentObject($W.GeoFencingDetails.prototype, {



		/**
		* The initialization function
		*/
		initGeoFencingDetails: function(el, oParams) {
		geofencedetails = this;
		this.oEventHandler = new $U.DOMEventHandler(this.elBaseElement, {
		});
		this.show();
		this.addListeners();
	},


	show: function(){
		if (!this._oMap) {
			this._oMap = new $WL.Maps($D.getElementsByClassName("veh-maps-rpt", null, this.elBase)[0]);
		}
		this._oMap.checkResize();
	},

	clearMap: function(){

		map = this._oMap.get($WL.Maps.ATTR_MAP);
		//map.clearOverlays();
	
	},


	addMarkers: function(Shape) {
		map = this._oMap.get($WL.Maps.ATTR_MAP);
		overviewMapControl: true
		map.addControl(new overviewMapControl(true));
	    //map.Overview();
		/*map.addControl(new GOverviewMapControl());
            map.setCenter(new GLatLng(12.971599, 77.594563), 13);*/
		for (var i = 0; i < allMapMarkers.length; i++ ) {
			allMapMarkers[i].setMap(null);
			
		}
		regionShape = Shape;
		
		if(regionShape == "circle"){
			this.addCircle();
			
			return allMarkers;
		}
		if(regionShape == "square"){
			this.addSquare();
			return allMarkers;
		}
		if(regionShape == "custom"){
			//console.debug('regionShape is '+regionShape);
			this.clearMap();
			this.addCustomRegion();
			return allMarkers;

		}
	},
	updateRegionDetails: function(regShape, Gpoints,geofenceid){
		var oMarkers=[];
		
		if(circle){
			circle.setMap(null);
		}
		
		if(rectangle){
			rectangle.setMap(null);
		}
		
		if(poly){
			 for(var i= markers.length;i>=0;i--){             
                poly.getPath().removeAt(i-1);
				//markers[i].setMap(null);
				var a=allMarkers.pop();
            }
		}
		for (var i = 0; i < allMapMarkers.length; i++ ) {
			allMapMarkers[i].setMap(null);
		}
		map = this._oMap.get($WL.Maps.ATTR_MAP);
		//For mini map on right bottom side
		//map.addControl(new overviewMapControl(true));
		if(regShape == 0){
			var centerPoint = Gpoints[0];
			centerPoint = centerPoint.split(" ");
			var centerX = (centerPoint[0].substring(6).trim())*100000;
			var centerY = (centerPoint[1].substring(0,(centerPoint[1].length-1)).trim())*100000;

			var endPoint = Gpoints[1];
			endPoint = endPoint.split(" ");
			var endX = (endPoint[0].substring(6).trim())*100000;
			var endY = (endPoint[1].substring(0,(endPoint[1].length-1)).trim())*100000;
			
			 centerMarker = new google.maps.Marker({
                map: map,
                icon:icon,
                position: new google.maps.LatLng(centerX,centerY),
                draggable: true,
                title: 'Drag me!'
            });

            endMarker = new google.maps.Marker({
                map: map,
                icon:icon,
                position: new google.maps.LatLng(endX,endY),
                draggable: true,
                  title: 'Drag me!'
            });
			
			oMarkers[0] = centerMarker;
			oMarkers[1] = endMarker;
			
			allMarkers = oMarkers;
			allMapMarkers.push(centerMarker);
			allMapMarkers.push(endMarker);
			regionShape = "circle";
			this.drawCircle(centerMarker,endMarker);
			
		}
		else if(regShape == 1){
		var centerPoint = Gpoints[0];
			centerPoint = centerPoint.split(" ");
			var centerX = (centerPoint[0].substring(6).trim())*100000;
			var centerY = (centerPoint[1].substring(0,(centerPoint[1].length-1)).trim())*100000;

			var endPoint = Gpoints[1];
			endPoint = endPoint.split(" ");
			var endX = (endPoint[0].substring(6).trim())*100000;
			var endY = (endPoint[1].substring(0,(endPoint[1].length-1)).trim())*100000;
			
			 centerMarker = new google.maps.Marker({
                map: map,
                icon:icon,
                position: new google.maps.LatLng(centerX,centerY),
                draggable: true,
                title: 'Drag me!'
            });

            endMarker = new google.maps.Marker({
                map: map,
                icon:icon,
                position: new google.maps.LatLng(endX,endY),
                draggable: true,
                  title: 'Drag me!'
            });
					
			oMarkers[0] = centerMarker;
			oMarkers[1] = endMarker;

			allMarkers = oMarkers;
			allMapMarkers.push(centerMarker);
			allMapMarkers.push(endMarker);
			regionShape = "square";
			this.drawSquare(centerMarker,endMarker);
			
		}
		else if(regShape == 2){
		
			currentGeoFenceId=geofenceid;

		
			if(Gpoints.length<2){	
			   this.drawPolygon(geofenceid);
			}
			else{
				
				for(var i=0;i<(Gpoints.length-5);i++){
					var centerPoint = Gpoints[i];
					centerPoint = centerPoint.split(" ");
					var centerX = (centerPoint[0].substring(6).trim())*100000;
					var centerY = (centerPoint[1].substring(0,(centerPoint[1].length-1)).trim())*100000;	
					var pointLatLng=new google.maps.LatLng(centerX,centerY);
					path.insertAt(path.length, pointLatLng);
							
					var marker = new google.maps.Marker({
						position: pointLatLng,
						map: map,
						draggable: true
					});
					
					
					markers.push(marker);
					allMarkers.push(marker);
					allMapMarkers.push(marker);
					marker.setTitle("#" + path.length);
					
					google.maps.event.addListener(marker, 'click', function(event) {
							var markerPosition;
							for(var k=0;k<markers.length;k++){
								if(markers[k].getPosition()==event.latLng){
									markerPosition=k;
								}
							}
							markers[markerPosition].setMap(null);
							
							markers.splice(markerPosition, 1);
							allMarkers.splice(markerPosition, 1);
							path.removeAt(markerPosition);		
							
					});
					
					google.maps.event.addListener(marker, 'drag', function(event) {
							var markerPosition;
							for(var k=0;k<markers.length;k++){
								if(markers[k].getPosition()==event.latLng){
									markerPosition=k;
								}
							}
							path.setAt(markerPosition, markers[markerPosition].getPosition());
							
					});
					
					}
					poly = new google.maps.Polygon({
							strokeWeight: 1,
					});

					poly.setMap(map);
					poly.setPaths(new google.maps.MVCArray([path]));
						
					google.maps.event.addListener(map, 'click', function(event){
						if(currentGeoFenceId==geofenceid){
						
							path.insertAt(path.length, event.latLng);
							var marker = new google.maps.Marker({
								position: event.latLng,
								map: map,
								draggable: true
							});
							markers.push(marker);
							allMarkers.push(marker);
							allMapMarkers.push(marker);
							marker.setTitle("#" + path.length);

							google.maps.event.addListener(marker, 'click', function() {
									marker.setMap(null);
								//	allMarkers.setMap(null);
									for (var i = 0, I = markers.length; i < I && markers[i] != marker; ++i);
									markers.splice(i, 1);
									path.removeAt(i);
								}
							);
							
							google.maps.event.addListener(marker, 'drag', function() {
									for (var i = 0, I = markers.length; i < I && markers[i] != marker; ++i);
										path.setAt(i, marker.getPosition());
									}
							);
						}	
					});	
								
						
			}
		}
	},

	addCircle: function(){
		var oMarkers=[];

		centerMarker = new google.maps.Marker({
            map: map,
            position: new google.maps.LatLng(12.971599, 77.594563),
            draggable: true,
            title: 'Center Marker'
        });
	
	endMarker = new google.maps.Marker({
            map: map,
          position: new google.maps.LatLng(12.8812232, 77.728641),
            draggable: true,
              title: 'End marker'
        });
	
		oMarkers[0] = centerMarker;
		oMarkers[1] = endMarker;

		allMarkers = oMarkers;
		this.drawCircle(centerMarker, endMarker);
	},
	
	
	drawCircle: function(centerMarker,endMarker){
	
		circle = new google.maps.Circle({
                map: map
            });
	        lngdiff=centerMarker.getPosition().lng()-endMarker.getPosition().lng();
            latdiff=centerMarker.getPosition().lat()-endMarker.getPosition().lat();

            google.maps.event.addListener(centerMarker, 'drag', function(){
			 var lat=centerMarker.getPosition().lat()-latdiff;
            var lng=centerMarker.getPosition().lng()-lngdiff;

            var latlng=new google.maps.LatLng(lat,lng);
            endMarker.setPosition(latlng);

            circle.bindTo('center', centerMarker, 'position');
			});
            google.maps.event.addListener(endMarker, 'drag',function(){
				distance=google.maps.geometry.spherical.computeDistanceBetween (centerMarker.getPosition(),endMarker.getPosition());
				circle.setRadius(distance);
				circle.bindTo('center', centerMarker, 'position');
			
			});

            google.maps.event.addListener(centerMarker, 'dragend', function(){
				lngdiff=centerMarker.getPosition().lng()-endMarker.getPosition().lng();
				latdiff=centerMarker.getPosition().lat()-endMarker.getPosition().lat();
			});
            google.maps.event.addListener(endMarker, 'dragend', function(){
				lngdiff=centerMarker.getPosition().lng()-endMarker.getPosition().lng();
				latdiff=centerMarker.getPosition().lat()-endMarker.getPosition().lat();
			});


            distance=google.maps.geometry.spherical.computeDistanceBetween (centerMarker.getPosition(),endMarker.getPosition());

            circle.setRadius(distance);
            circle.bindTo('center', centerMarker, 'position');
			
			
	},

	
	
	
	addSquare: function(){
		//console.debug('In adding Square ');
		var oMarkers=[];
		 centerMarker = new google.maps.Marker({
             map: map,
             position: new google.maps.LatLng(13.021599, 77.464563),
             draggable: true,
             title: 'Center Marker'
         });
		
		endMarker = new google.maps.Marker({
             map: map,
           position: new google.maps.LatLng(12.8812232, 77.728641),
             draggable: true,
               title: 'End marker'
         });
			
		oMarkers[0] = centerMarker;
		oMarkers[1] = endMarker;
		allMarkers = oMarkers;

		this.drawSquare(centerMarker,endMarker);
	},
	
	
	drawSquare: function(centerMarker,endMarker){
		// map.clearOverlays();
		

		rectangle = new google.maps.Rectangle({
            map: map
        });
		
		google.maps.event.addListener(centerMarker, 'drag', function(){
		if(centerMarker.getPosition().lng()<endMarker.getPosition().lng()){
            var latLngBounds = new google.maps.LatLngBounds(
                    centerMarker.getPosition(),
                    endMarker.getPosition()
            );
		}else{
		    var latLngBounds = new google.maps.LatLngBounds(
                    endMarker.getPosition(),
                    centerMarker.getPosition()
            );
		}
		
            rectangle.setBounds(latLngBounds);
		});
        google.maps.event.addListener(endMarker, 'drag', function(){
		if(centerMarker.getPosition().lng()<endMarker.getPosition().lng()){
            var latLngBounds = new google.maps.LatLngBounds(
                    centerMarker.getPosition(),
                    endMarker.getPosition()
            );
		}else{
		    var latLngBounds = new google.maps.LatLngBounds(
                    endMarker.getPosition(),
                    centerMarker.getPosition()
            );
		}
		
            rectangle.setBounds(latLngBounds);
		});
           
        if(centerMarker.getPosition().lng()<endMarker.getPosition().lng()){
            var latLngBounds = new google.maps.LatLngBounds(
                    centerMarker.getPosition(),
                    endMarker.getPosition()
            );
		}else{
		    var latLngBounds = new google.maps.LatLngBounds(
                    endMarker.getPosition(),
                    centerMarker.getPosition()
            );
		}
		
        rectangle.setBounds(latLngBounds);
	},

	addCustomRegion: function(){
		//this.drawPolygon();
	},

	//Draw Polygon from the PolygonMarkers Array
	drawPolygon: function(geofenceid){
		 poly = new google.maps.Polygon({
                strokeWeight: 1,
            });

            poly.setMap(map);

            poly.setPaths(new google.maps.MVCArray([path]));
		
            google.maps.event.addListener(map, 'click', function(event){
				if(currentGeoFenceId==geofenceid){
					path.insertAt(path.length, event.latLng);
					var marker = new google.maps.Marker({
						position: event.latLng,
						map: map,
						draggable: true
					});
					markers.push(marker);
					allMarkers.push(marker);
					allMapMarkers.push(marker);
					marker.setTitle("#" + path.length);

					google.maps.event.addListener(marker, 'click', function() {
								marker.setMap(null);
								allMarkers.setMap(null);
								for (var i = 0, I = markers.length; i < I && markers[i] != marker; ++i);
								markers.splice(i, 1);
								path.removeAt(i);
							}
					);

					google.maps.event.addListener(marker, 'drag', function() {
								for (var i = 0, I = markers.length; i < I && markers[i] != marker; ++i);
								path.setAt(i, marker.getPosition());
							}
					);
				}	
			});
	} ,
	
	adjustZoomEndPoint: function() {
	/*	////console.debug('adjusting ZoomEndPoint ');
		var latDiff = endLatLng.getPosition().lat() - centerLatLng.getPosition().lat();
		var lngDiff = endLatLng.getPosition().lng() - centerLatLng.getPosition().lng();
		var halfLength = 0;
		if (latDiff > lngDiff)
			halfLength = latDiff;
		else
			halfLength = lngDiff;
		var point = new google.maps.LatLngBounds(centerLatLng.getPosition().lng() + halfLength,
				centerLatLng.getPosition().lat() + halfLength);
		//endLatLng.setPoint(point);
		endLatLng.setPosition(point);
		geofencedetails.drawSquare();
*/		
	},
	adjustEndPoint: function() {
	/*	//console.debug('In adjusting EndPoint '+oldCenterLatLng+' ');
		var latDiff = oldCenterLatLng.lat() - centerLatLng.lat();
		//console.debug('latDiff '+latDiff);
		var lngDiff = oldCenterLatLng.lng() - centerLatLng.lng();
		//console.debug('lngDiff '+lngDiff);
		var point = new google.maps.LatLngBounds(endLatLng.lng() - lngDiff, endLatLng.lat()- latDiff);
		//console.debug('point is '+point);
		endLatLng.setPosition(point);
		//console.debug('endLatLng after set '+endLatLng);
		if (regionShape == "circle"){
			geofencedetails.drawCircle();
		}
		else if(regionShape == "square"){
			//console.debug('regionShape is '+regionShape);
			geofencedetails.drawSquare();
		}
	
*/	
	},
	
	
	getAllMarkers: function(){
		return allMarkers;
	},

	/*Declaring the member properties*/
	elContainer: null,
	oModifiableDetails: null,
	oDisplayedTripID: null,

	/*Defining the member methods*/
	/**
	 * Hides the trip details
	 */
	hideGeoFencingDetails: function() {
		if (!$D.hasClass(this.elContainer, "hidden")) {
			$D.addClass(this.elContainer, "hidden");
		}
	},

	/**
	* Shows the Trip details
	*/
	showGeoFencingDetails: function() {
		/*Removing information dialog box if it is still present*/
		var tripSettings = $D.getAncestorByClassName(this.elBaseElement, "geofencing");
		var simpleDialog = $D.getElementsByClassName("simpledialog", null, tripSettings)[0];
		if (!$D.hasClass(simpleDialog, "disabled")) {
			$D.addClass(simpleDialog, "disabled");
		}
		if ($D.hasClass(this.elContainer, "hidden")) {
			$D.removeClass(this.elContainer, "hidden");
		}
		/*Preventing possible memory leaks*/
		tripSettings = null;
		simpleDialog = null;
	},

	/**
	* Forces the widget to display the details of the trip identified by the tripID
	* @param {Object} tripID A unique identifier for the trip
	*/
	displayGeoFencingDetails: function(tripID) {
		this.show();

	},

	reloadGeoFencingDetails: function() {
		/*Preventing possible memory leaks*/
		this.removeListeners();
		this.elContainer = null;
		this.initGeoFencingDetails(this.elBaseElement);
	},

	showFailurePopUp: function() {
		if ($D.hasClass($D.getElementsByClassName("simpledialog", null, this.elBaseElement)[0], "disabled")) {
			$D.removeClass($D.getElementsByClassName("simpledialog", null, this.elBaseElement)[0], "disabled");
		}
	},

	hideFailurePopUp: function() {
		if (!$D.hasClass($D.getElementsByClassName("simpledialog", null, this.elBaseElement)[0], "disabled")) {
			$D.addClass($D.getElementsByClassName("simpledialog", null, this.elBaseElement)[0], "disabled");
		}
	},

	/*Defining call backs*/
	oCallBacks: {

		onGeoFencingDetailsModified: function() {

		this.set($W.GeoFencingDetails.ATTR_MODIFIABLE_DETAILS, this.getModifiedGeoFencingDetails());
	}
	},

	/**
	* The event manager function for the buttons
	* @param {Object} layer
	* @param {Object} args
	*/
	buttonEventManager: function(event) {
		var target = $E.getTarget(event);
		var sourceButton = $D.hasClass(target, 'fleet-buttons') ? target : $D.getAncestorByClassName(target, "fleet-buttons");

		$E.stopEvent(event);
	},

	/*Installing Listeners*/
	addListeners: function() {
		this.oEventHandler.addListener($D.getElementsByClassName('buttons', null, this.elBaseElement), this.buttonEventManager, null, this);
		/*The listener for the save button goes here - should call the onGeoFencingDetailsModified*/
		$W.Buttons.addDefaultHandler($D.getElementsByClassName("buttons", null, $D.getElementsByClassName("simpledialog", null, this.elBaseElement)[0])[0], function(event, args, me) {
			$D.addClass($D.getElementsByClassName("simpledialog", null, this.elBaseElement)[0], "disabled");
		}, null, this);
	},

	/*Defining the utility functions*/
	/**
	 * Utility function to remove all the listeners
	 */
	removeListeners: function() {
		this.oEventHandler.purge();
	},

	/**
	* Utility function to get the modified trip details
	*/
	getModifiedGeoFencingDetails: function() {
		var aModifiableElements = $D.getElementsByClassName($W.GeoFencingDetails.MODIFIABLE_CLASS, null, this.elContainer);
		var result = {};
		for (var element in aModifiableElements) {
			if ($L.isString($D.getAttribute(aModifiableElements[element], "name"))) {
				result[$D.getAttribute(aModifiableElements[element], "name")] = aModifiableElements[element].innerHTML;
			}
		}
		aModifiableElements = null;
		return result;
	}
	});
	/**
	 * Helper class that loads the given Given Geo-Fence limit into the details page
	 *
	 * TODO: Move this class to a generic location such as the utils.js and make it a generic
	 *       functionality.
	 * TODO: Implement the functionality
	 * @author N.Balaji
	 *
	 * @param {Object} el The target element into which the map should be loaded
	 */
	$W.GeoFencingDetails.GeoFenceLoader = function(el) {
		/* TODO : Initializing*/
		this.initGeoFenceLoader(el);
	};
	$L.augmentObject($W.GeoFencingDetails.GeoFenceLoader.prototype, {
		/**
		* The initialization function
		* @param {Object} el
		*/
		initGeoFenceLoader: function(el) {
			this.elMapWindow = el;
		},
		/*Declaring the data memebers*/
		/**
		 * The target into which the geo-Fence limit should be loaded
		 */
		elMapWindow: null,
		/*Declaring the member methods*/
		/**
		 * Member method that loads the map into a decided target
		 * @param {Object} mapEl
		 */
		loadMap: function(mapEl) {
		},
		/*Declaring utilty methods*/
		/**
		 * Helper function for deceding the appropriate magnification
		 * limit for the given geo fence limit
		 */
		chooseMagnification: function() {
		}
	});
})();
