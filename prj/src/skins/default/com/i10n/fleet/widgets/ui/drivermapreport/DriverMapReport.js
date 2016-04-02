(function(){
	var $B = YAHOO.Bubbling;
	var $L = YAHOO.lang;
	var $YU = YAHOO.util;
	var $E = YAHOO.util.Event;
	var $D = YAHOO.util.Dom;
	var $YW = YAHOO.widget;
	var $W = getPackageForName("com.i10n.fleet.widget.ui");
	var $U = getPackageForName("com.i10n.fleet.Utils");
	var $WL = getPackageForName("com.i10n.fleet.widget.lib");
	var $V = getPackageForName("com.i10n.fleet.widget.view");
	var map=null;
	var live = null;
	var toPointLat = null;
	var toPointLng = null;
	var isKPserver=false;
    /**
     * Driver Report Sub Nav Page
     *
     * @author aravind
     */
    $W.DriverMapReport = function(el, oArgs){
        this.init(el, oArgs);
        live=this;
		$V.BaseView.subscribeFn("drivermapreport",this.callback);
    };
    $L.augmentObject($W.DriverMapReport, {
    	LINE_COLORS: ["#0000FF", "#FF0000", "#00FF00", "#00FFFF", "#FFFF00", "#FF00FF", "#FFFFFF", "#000000"]
    });
    $L.extend($W.DriverMapReport, $W.GraphReport, {
    	/**
		 * Map represented by the object
		 */
		_oMap: null,
		_aPolylines: null,
		_aMarkers: null,
		/**
		 * Renders the widget based on the data.
		 */
        render: function(){
    		var oData = this.get($W.BaseReport.ATTR_DATA);
    		var elErrDialog = $D.getElementsByClassName("simpledialog", null, this.elBase)[0];
    		if (oData) {
    			if (!$D.hasClass(elErrDialog, "disabled")) {
    				$D.addClass(elErrDialog, "disabled");
    			}

    			this._clearObjects();
    			for (var driverID in oData) {
    				if(isKPserver){
    					this._addRouteForV2(oData[driverID]);
    				}else{
    					this._addRoute(oData[driverID]);
    				}
    			}
    		}else {
    			if ($D.hasClass(elErrDialog, "disabled")) {
    				$D.removeClass(elErrDialog, "disabled");
    			}
    		}
    	
        },
        callback :{
    		success: function(pushletData){
    		live.onDriverPositionUpdatePushlet(pushletData);
    	}
    	},onDriverPositionUpdatePushlet: function(result){
    		var successCnt;
    		var failCnt;
    		var successCnt = 0;
    		var failCnt = 0;
    		result = eval('('+result+')');
    		var cont = result.content;
    		for(var i in cont){ 
    			if(isKPserver){
    				this.addPushMarkerForV2(cont[i]);		
    			}else{
    				this.addPushMarker(cont[i]);	
    			}
    		}
    	},
    	addPushMarkerForV2 : function(aData){
    		try{
    			var sDriverId = this.get($W.BaseReport.ATTR_SELECTED_ITEM);
    			if(sDriverId == null){
    				return null;
    			}
    			var oTimeFrame = this.get($W.BaseReport.ATTR_SELECTED_TIMEFRAME);
    			var endDate = oTimeFrame.endDate;
    			var currentTime = aData.lastupdated;
    			try{
    				var dateTime = endDate.trim().split(" ");
    				var endDateDate = dateTime[0];
    				var endDateTime = dateTime[dateTime.length -1];
    				var endDateSplit = endDateDate.split("/");
    				var endDateTimeSplit = endDateTime.split(":");
    				endDate = new Date(endDateSplit[2],endDateSplit[0]-1, endDateSplit[1],endDateTimeSplit[0],
    						endDateTimeSplit[1],endDateTimeSplit[2]);
    				//yyyy-MM-dd HH:mm:ss
    				dateTime = currentTime.trim().split(" ");
    				var currentDateDate = dateTime[0];
    				var currentDateTime = dateTime[dateTime.length -1];
    				var currentDateSplit = currentDateDate.split("-");
    				var currentDateTimeSplit = currentDateTime.split(":");
    				currentTime = new Date(currentDateSplit[0],currentDateSplit[1]-1, currentDateSplit[2],currentDateTimeSplit[0],
    						currentDateTimeSplit[1],currentDateTimeSplit[2]);

    			} catch(e){
    				return null;
    			}

    			if(endDate.getTime() < currentTime.getTime()){
    				return null;
    			}
    			var driverIdArray = sDriverId.split("-");
    			var sDriverId = driverIdArray[driverIdArray.length -1];

    			var aDriverId = aData.id+"";
    			var aDriverIdArray = aDriverId.trim().split("-");
    			aDriverId =  aDriverIdArray[aDriverIdArray.length -1]
    					if(sDriverId != aDriverId){
    						return;
    					}

    			var check = aData.latitude+"";
    			if(check == "undefined"){
    				return null;
    			}

    			var aLatLng = [];
    			var oLatLng = new $WL.Maps.LatLng({
    				lat: parseFloat(this.toPointLat),
    				lon: parseFloat(this.toPointLng)
    			});
    			aLatLng.push(oLatLng);
    			var oLatLng = new $WL.Maps.LatLng({
    				lat: parseFloat(aData.latitude),
    				lon: parseFloat(aData.longitude)
    			});
    			aLatLng.push(oLatLng);

    			this.toPointLat = aData.latitude;
    			this.toPointLng = aData.longitude;

    			var nColorIndex = 0;		
    			var sColor = $W.DriverMapReport.LINE_COLORS[nColorIndex];
    			nColorIndex = (nColorIndex + 1) % $W.DriverMapReport.LINE_COLORS.length;
    			var oMap = this._oMap.get($WL.Maps.ATTR_MAP);
    			var oPolyLine = new $WL.Maps.PolyLine({
    				map: oMap,
    				positions: aLatLng,
    				color: sColor
    			});
    			var oBounds = new $WL.Maps.LatLngBounds();
    			oBounds.extend(oPolyLine.getBounds());
    			this._aPolylines.push(oPolyLine);
    			var oEndMarker = new $WL.Maps.Marker({
    				position: new $WL.Maps.LatLng({
    					lat: parseFloat(aData.latitude),
    					lon: parseFloat(aData.longitude)
    				}),
    				map: oMap,
    				options: {
    				iconType: "vehicles",
    				iconSubType: "onlineend"
    			},
    			title: "Trip End!"
    			});
    			var endloc= aData.location;
    			var enddate=aData.lastupdated;
    			this._aMarkers.push(oEndMarker);
    			var oInfoWindow = new $WL.Maps.FleetInfoWindow({
    				map: this._oMap.get($WL.Maps.ATTR_MAP)
    			});
    			this.addInfoWindowListener(oEndMarker,this._oMap.get($WL.Maps.ATTR_MAP),oInfoWindow,currentTime.toString(),"end",endloc,this);
    			var trackpoint = "trackpoint";
    			var course = aData.course;

    			/*if((course <= 30.0) || (course >= 330.0) ){
    				trackpoint = "trackpoint0";
    			}else if ((course <= 60.0) && (course >= 30.0)){
    				trackpoint = "trackpoint45";
    			}else if ((course <= 120.0) && (course >= 60.0)){
    				trackpoint = "trackpoint90";
    			}else if ((course <= 150.0) && (course >= 120.0)){
    				trackpoint = "trackpoint135";
    			}else if ((course <= 210.0) && (course >= 150.0)){
    				trackpoint = "trackpoint180";
    			}else if ((course <= 240.0) && (course >= 210.0)){
    				trackpoint = "trackpoint225";
    			}else if ((course <= 300.0) && (course >= 240.0)){
    				trackpoint = "trackpoint270";
    			}else if ((course <= 330.0) && (course >= 300.0)){
    				trackpoint = "trackpoint315";
    			}else{
    				trackpoint="trackpoint";
    			}*/

    			var oMarker= new $WL.Maps.Marker({
    				position: new $WL.Maps.LatLng({
    					lat: parseFloat(aData.latitude),
    					lon: parseFloat(aData.longitude)
    				}),
    				map: oMap,
    				options: {
    				iconType: "trackpoint",
    				iconSubType: trackpoint
    			},
    			title: "Trip Start!"
    			});
    			this._aMarkers.push(oMarker);
    			var oInfoWindow = new $WL.Maps.FleetInfoWindow({
    				map: this._oMap.get($WL.Maps.ATTR_MAP)
    			});

    			this.addInfoWindowListenerTrack(oMarker, this._oMap.get($WL.Maps.ATTR_MAP), oInfoWindow, aData.gsm, aData.gps, 
    					currentTime.toString(), aData.speed,this);

    		}catch(e){
    		}
    	},
    	  getPageID: function(){
              return $W.DriverMapReport.PAGE_ID;
          },

    	addPushMarker : function(aData){
    		try{
    			var sDriverId = this.get($W.BaseReport.ATTR_SELECTED_ITEM);
    			if(sDriverId == null){
    				return null;
    			}

    			var oTimeFrame = this.get($W.BaseReport.ATTR_SELECTED_TIMEFRAME);
    			var endDate = oTimeFrame.endDate;
    			var currentTime = aData.lastupdated;
    			try{
    				var dateTime = endDate.trim().split(" ");
    				var endDateDate = dateTime[0];
    				var endDateTime = dateTime[dateTime.length -1];
    				var endDateSplit = endDateDate.split("/");
    				var endDateTimeSplit = endDateTime.split(":");
    				endDate = new Date(endDateSplit[2],endDateSplit[0]-1, endDateSplit[1],endDateTimeSplit[0],
    						endDateTimeSplit[1],endDateTimeSplit[2]);

    				//				yyyy-MM-dd HH:mm:ss
    				dateTime = currentTime.trim().split(" ");
    				var currentDateDate = dateTime[0];
    				var currentDateTime = dateTime[dateTime.length -1];
    				var currentDateSplit = currentDateDate.split("-");
    				var currentDateTimeSplit = currentDateTime.split(":");
    				currentTime = new Date(currentDateSplit[0],currentDateSplit[1]-1, currentDateSplit[2],currentDateTimeSplit[0],
    						currentDateTimeSplit[1],currentDateTimeSplit[2]);

    			} catch(e){
    				return null;
    			}

    			if(endDate.getTime() < currentTime.getTime()){
    				return null;
    			}
    			var driverIdArray = sDriverId.split("-");
    			var sDriverId = driverIdArray[driverIdArray.length -1];

    			var aDriverId = aData.id+"";
    			var aDriverIdArray = aDriverId.trim().split("-");
    			aDriverId =  aDriverIdArray[aDriverIdArray.length -1]
    					if(sDriverId != aDriverIdArray){
    						return;
    					}

    			var check = aData.latitude+"";
    			if(check == "undefined"){
    				return null;
    			}

    			var aLatLng = [];
    			var oLatLng = new $WL.Maps.LatLng({
    				lat: parseFloat(this.toPointLat),
    				lon: parseFloat(this.toPointLng)
    			});
    			aLatLng.push(oLatLng);
    			var oLatLng = new $WL.Maps.LatLng({
    				lat: parseFloat(aData.latitude),
    				lon: parseFloat(aData.longitude)
    			});
    			aLatLng.push(oLatLng);

    			this.toPointLat = aData.latitude;
    			this.toPointLng = aData.longitude;

    			var nColorIndex = 0;		
    			var sColor = $W.DriverMapReport.LINE_COLORS[nColorIndex];
    			nColorIndex = (nColorIndex + 1) % $W.DriverMapReport.LINE_COLORS.length;
    			var oMap = this._oMap.get($WL.Maps.ATTR_MAP);
    			var oPolyLine = new $WL.Maps.PolyLine({
    				map: oMap,
    				positions: aLatLng,
    				strokeColor: '#FF0000'
    			});
    			var oBounds = new $WL.Maps.LatLngBounds();
    			oBounds.extend(oPolyLine.getBounds());

    			this._aPolylines.push(oPolyLine);

    			var oEndMarker = new $WL.Maps.Marker({
    				position: new $WL.Maps.LatLng({
    					lat: parseFloat(aData.longitude),
    					lon: parseFloat(aData.latitude)
    				}),
    				map: oMap,
    				options: {
    				iconType: "vehicles",
    				iconSubType: "onlineend"
    			},
    			title: "Trip End!"
    			});
    			var endloc= aData.location;
    			var enddate=aData.lastupdated;
    			this._aMarkers.push(oEndMarker);
    			var oInfoWindow = new $WL.Maps.FleetInfoWindow({
    				map: this._oMap.get($WL.Maps.ATTR_MAP),
    				position: oEndMarker.getPosition()
    			});
    			this.addInfoWindowListener(oEndMarker,this._oMap.get($WL.Maps.ATTR_MAP),oInfoWindow,currentTime.toString(),"end",endloc,this);
    			var trackpoint = "trackpoint";
    			var course = aData.course;
    			var oMarker= new $WL.Maps.Marker({
    				position: new $WL.Maps.LatLng({
    					lat: parseFloat(aData.latitude),
    					lon: parseFloat(aData.longitude)
    				}),
    				map: oMap,
    				options: {
    				iconType: "trackpoint",
    				iconSubType: "trackpoint"
    			},
    			title: "Track point!"
    			});
    			this._aMarkers.push(oMarker);
    			var oInfoWindow = new $WL.Maps.FleetInfoWindow({
    				map: this._oMap.get($WL.Maps.ATTR_MAP),
    				position: oMarker.getPosition()
    			});
    			this.addInfoWindowListenerTrack(oMarker, this._oMap.get($WL.Maps.ATTR_MAP), oInfoWindow, aData.gsm, aData.gps, 
    					currentTime.toString(), aData.speed,this);
    			//oMap.fitBounds(oBounds);

    		}catch(e){
    		}
    	},
    	show: function(){
    		$W.DriverReport.superclass.show.call(this);
    		if (!this._oMap) {
    			this._oMap = new $WL.Maps($D.getElementsByClassName("veh-maps-rpt", null, this.elBase)[0]);
    		}
    		this._oMap.checkResize();
    	},
    	_addRouteForV2: function(oData){
    		if (oData) {
    			var oMap = this._oMap.get($WL.Maps.ATTR_MAP);
    			map=oMap;
    			var oArgs=this.get($W.BaseReport.ATTR_SELECTED_ITEM);
    			var isExtended = false;
    			var oBounds = new $WL.Maps.LatLngBounds();
    			var nColorIndex = 0;
    			for (var tripID in oData) {
    				var oTripData = oData[tripID];
    				var aLatLng = [];
    				if (oTripData.track && oTripData.track.positions) {
    					var oPositions = oTripData.track.positions;
    					for (var i = 0; i < oPositions.length; i++) {
    						var oLatLng = new $WL.Maps.LatLng({
    							lat: parseFloat(oPositions[i].lat),
    							lon: parseFloat(oPositions[i].lon)
    						});
    						aLatLng.push(oLatLng);
    					}
    					var sColor = $W.DriverMapReport.LINE_COLORS[nColorIndex];
    					nColorIndex = (nColorIndex + 1) % $W.DriverMapReport.LINE_COLORS.length;
    					var oPolyLine = new $WL.Maps.PolyLine({
    						map: oMap,
    						positions: aLatLng,
    						color: sColor
    					});
    					oBounds.extend(oPolyLine.getBounds());
    					isExtended = true;
    					this._aPolylines.push(oPolyLine);


    					this._addTripMarkersForV2(oTripData.track.violations, oPositions, oMap);
    					this._addIdlePointsForV2(oTripData.idle.positions,oPositions,oMap);
    					this._addTrackPntsForV2(oTripData.track.positions,oPositions,oMap);
    					this.onDriverSelect;
    					this.onMarkerSelect;
    					this.toPointLat = oPositions[oPositions.length-1].lat;
    					this.toPointLng = oPositions[oPositions.length-1].lon;
    						
    				}

    			}
    			if (isExtended) {
    				oMap.fitBounds(oBounds);
    			}
    		}
    	},
    	
    	/**
    	 * Adds Route to the map.
    	 * @param {Object} oData
    	 */
    	_addRoute: function(oData){
    		if (oData) {
    			var oMap = this._oMap.get($WL.Maps.ATTR_MAP);
    			map=oMap;
    			var oArgs=this.get($W.BaseReport.ATTR_SELECTED_ITEM);
    			var isExtended = false;
    			var oBounds= new google.maps.LatLngBounds();
    			var nColorIndex = 0;
    			for (var tripID in oData) {
    				var oTripData = oData[tripID];
    				var aLatLng = [];
    				if (oTripData.track && oTripData.track.positions) {
    					var oPositions = oTripData.track.positions;
    					for (var i = 0; i < oPositions.length; i++) {
    							var oLatLng = new google.maps.LatLng(parseFloat(oPositions[i].lat),parseFloat(oPositions[i].lon));
    							aLatLng.push(oLatLng);
    							oBounds.extend(oLatLng);
    					}
    					var sColor = $W.DriverMapReport.LINE_COLORS[nColorIndex];
    					nColorIndex = (nColorIndex + 1) % $W.DriverMapReport.LINE_COLORS.length;
    						var line = new google.maps.Polyline({
    							path: aLatLng,
    							strokeColor: sColor,
    							strokeOpacity: 10.0,
    							strokeWeight: 2
    						});
    						line.setMap(map);
    						this._aPolylines.push(line);
    					isExtended = true;
    					this._addTripMarkers(oTripData.track.violations, oPositions, oMap);
    					this._addIdlePoints(oTripData.idle.positions,oPositions,oMap);
    					this._addTrackPnts(oTripData.track.positions,oPositions,oMap);
    					this.onDriverSelect;
    					this.onMarkerSelect;
    					this.toPointLat = oPositions[oPositions.length-1].lon;
    					this.toPointLng = oPositions[oPositions.length-1].lat;
    					map.setZoom(8);
    				}
    			}
    			if (isExtended) {
    				oMap.fitBounds(oBounds);
    			}
    		}
    	},
    	_addTripMarkersForV2: function(oViolations, oPositions, oMap){
    		if ($L.isArray(oPositions) && oPositions.length > 0) {
    			var oStartMarker = new $WL.Maps.Marker({
    				position: new $WL.Maps.LatLng({
    					lat: parseFloat(oPositions[0].lat),
    					lon: parseFloat(oPositions[0].lon)
    				}),
    				map: oMap,
    				options: {
    				iconType: "vehicles",
    				iconSubType: "onlinestart"
    			},
    			title: "Trip Start!"
    			});
    			var startloc=oPositions[0].location;
    			var startdate=new Date(oPositions[0].date);
    			var oEndMarker = new $WL.Maps.Marker({
    				position: new $WL.Maps.LatLng({
    					lat: parseFloat(oPositions[oPositions.length - 1].lat),
    					lon: parseFloat(oPositions[oPositions.length - 1].lon)
    				}),
    				map: oMap,
    				options: {
    				iconType: "vehicles",
    				iconSubType: "onlineend"
    			},
    			title: "Trip End!"
    			});
    			var endloc=oPositions[oPositions.length - 1].location;
    			var enddate=new Date(oPositions[oPositions.length - 1].date);
    			this._aMarkers.push(oStartMarker);
    			this._aMarkers.push(oEndMarker);
    			if (oViolations) {
    				for (var sViolationID in oViolations) {
    					var aViolations = oViolations[sViolationID];
    					for (var i = 0; i < aViolations.length; i++) {
    						var oViolationMarker = new $WL.Maps.Marker({
    							position: new $WL.Maps.LatLng({
    								lat: parseFloat(aViolations[i].lat),
    								lon: parseFloat(aViolations[i].lon)
    							}),
    							map: oMap,
    							options: {
    							iconType: "vehicles",
    							iconSubType: "offline"
    						},
    						title: sViolationID + "violation!"
    						});
    						this._aMarkers.push(oViolationMarker);
    					}
    				}
    			}
    			var oInfoWindow = new $WL.Maps.FleetInfoWindow({
    				map: this._oMap.get($WL.Maps.ATTR_MAP)
    			});

    			this.addInfoWindowListener(oStartMarker,this._oMap.get($WL.Maps.ATTR_MAP),oInfoWindow,startdate,"start",startloc,this);
    			this.addInfoWindowListener(oEndMarker,this._oMap.get($WL.Maps.ATTR_MAP),oInfoWindow,enddate,"end",endloc,this);

    		}
    	},
    	/**
    	 * add idle points in the  driver reports
    	 */
    	_addIdlePointsForV2: function(otrack,oViolations,oMap) {
    		var oViolationMarker;
    		var flagstart;
    		var idledate;
    		var idlehours;
    		var strtidledate;
    		var oInfoWindow = new $WL.Maps.FleetInfoWindow({
    			map: this._oMap.get($WL.Maps.ATTR_MAP)
    		});

    		for (var i = 0; i < otrack.length; i++) {

    			strtidledate=otrack[i].starttime;
    			idledate=otrack[i].endtime;
    			idlehours=otrack[i].idlehours;
    			oViolationMarker = new $WL.Maps.Marker({
    				position: new $WL.Maps.LatLng({
    					lat: parseFloat(otrack[i].lat),
    					lon: parseFloat(otrack[i].lon)
    				}),
    				map: oMap,
    				options: {
    				iconType: "vehicles",
    				iconSubType: "idlemapreport"
    			},
    			title: "idlepoint"
    			});

    			this._aMarkers.push(oViolationMarker);
    			this.addInfoWindowListeneridle(oViolationMarker,this._oMap.get($WL.Maps.ATTR_MAP),oInfoWindow,strtidledate,idledate,idlehours,"idle",otrack[i].locationname,this);

    		}

    	},

    	_addTrackPntsForV2: function(otrack,oViolations,oMap){
    		var oMarker;
    		var j=parseInt(oViolations.length/300);
    		if(oViolations.length%300== 0 && j!=0){
    			j=j-1;
    		}

    		for(var i=0;i<oViolations.length;i=i+j+1){
    			var course = oViolations[i].course;
    			var trackpoint = "trackpoint";

    			/*if((course <= 30.0) || (course >= 330.0) ){
    				trackpoint = "trackpoint0";
    			}else if ((course <= 60.0) && (course >= 30.0)){
    				trackpoint = "trackpoint45";
    			}else if ((course <= 120.0) && (course >= 60.0)){
    				trackpoint = "trackpoint90";
    			}else if ((course <= 150.0) && (course >= 120.0)){
    				trackpoint = "trackpoint135";
    			}else if ((course <= 210.0) && (course >= 150.0)){
    				trackpoint = "trackpoint180";
    			}else if ((course <= 240.0) && (course >= 210.0)){
    				trackpoint = "trackpoint225";
    			}else if ((course <= 300.0) && (course >= 240.0)){
    				trackpoint = "trackpoint270";
    			}else if ((course <= 330.0) && (course >= 300.0)){
    				trackpoint = "trackpoint315";
    			}else{
    				trackpoint="trackpoint";
    			}*/

    			oMarker= new $WL.Maps.Marker({
    				position: new $WL.Maps.LatLng({
    					lat: parseFloat(oViolations[i].lat),
    					lon: parseFloat(oViolations[i].lon)
    				}),
    				map: oMap,
    				options: {
    				iconType: "vehicles",
    				iconSubType: trackpoint
    				/*iconType: "trackpoint",
    				iconSubType: trackpoint*/
    			},
    			title: "Trip Start!"
    			});
    			this._aMarkers.push(oMarker);
    			var oInfoWindow = new $WL.Maps.FleetInfoWindow({
    				map: this._oMap.get($WL.Maps.ATTR_MAP)
    			});

    			this.addInfoWindowListenerTrack(oMarker, this._oMap.get($WL.Maps.ATTR_MAP), oInfoWindow, oViolations[i].gpssignal, 
    					oViolations[i].gsmsignal, oViolations[i].date, oViolations[i].speed, this);
    		}
    	},/**
    	 * Clears all the objects created on the map by the widget
    	 */
    	_clearObjects: function(){
    		this._clearObjectType(this._aMarkers);
    		this._clearObjectType(this._aPolylines);
    		this._aMarkers = [];
    		this._aPolylines = [];

    	},
    	/**
    	 * Clears the specified object
    	 * @param {Object} aObjectTypes
    	 */
    	_clearObjectType: function(aObjectTypes){
    		if (aObjectTypes) {
    			for (var i = 0; i < aObjectTypes.length; i++) {
    				aObjectTypes[i].setMap(null);
    			}
    		}
    	},
    	/***
    	 * Adds the trip / violations markers to the current map
    	 * @param {Object} aTripData
    	 * @param {Object} aViolations
    	 * @param {Object} oMap
    	 */
    	_addTripMarkers: function(oViolations, oPositions, oMap){
    		if ($L.isArray(oPositions) && oPositions.length > 0) {
    			var startPosition= new google.maps.LatLng(parseFloat(oPositions[0].lat),parseFloat(oPositions[0].lon));
    			var oStartMarker = new $WL.Maps.Marker({
    				position: startPosition,
    				map: oMap,
    				options: {
    				iconType: "vehicles",
    				iconSubType: "onlinestart"
    			},
    			title: "Trip Start!"
    			});
    			var startloc=oPositions[0].location;
    			var startdate=new Date(oPositions[0].date);
    			var endPosition = new google.maps.LatLng(parseFloat(oPositions[oPositions.length - 1].lat),parseFloat(oPositions[oPositions.length - 1].lon));
    			var oEndMarker = new $WL.Maps.Marker({
    				position: endPosition,
    				map: oMap,
    				options: {
    				iconType: "vehicles",
    				iconSubType: "onlineend"
    					/*iconType: "",
    				iconSubType: ""*/
    			},
    			title: "Trip End!"
    			});
    			var endloc=oPositions[oPositions.length - 1].location;
    			var enddate=new Date(oPositions[oPositions.length - 1].date);

    			this._aMarkers.push(oStartMarker);
    			this._aMarkers.push(oEndMarker);
    			if (oViolations) {
    				for (var sViolationID in oViolations) {
    					var aViolations = oViolations[sViolationID];
    					for (var i = 0; i < aViolations.length; i++) {
    						var oViolationMarker = new google.maps.Marker({
    							position: new google.maps.LatLng({
    								lat: parseFloat(aViolations[i].lon),
    								lon: parseFloat(aViolations[i].lat)
    							}),
    							map: oMap,
    							options: {
    							iconType: "vehicles",
    							iconSubType: "offline"
    						},
    						title: sViolationID + "violation!"
    						});
    						this._aMarkers.push(oViolationMarker);
    					}
    				}
    			}
    			var oInfoWindow = new $WL.Maps.FleetInfoWindow({
    				map: this._oMap.get($WL.Maps.ATTR_MAP)
    			});
    			this.addInfoWindowListener(oStartMarker,this._oMap.get($WL.Maps.ATTR_MAP),oInfoWindow,startdate,"start",startloc,this);
    			this.addInfoWindowListener(oEndMarker,this._oMap.get($WL.Maps.ATTR_MAP),oInfoWindow,enddate,"end",endloc,this);

    		}
    	},
    	/**
    	 * add trackmarkers to the driver report 
    	 * */

    	_addTrackPnts: function(otrack,oViolations,oMap){
    		var j=parseInt(oViolations.length/300);
    		if(oViolations.length%300== 0 && j!=0){
    			j=j-1;
    		}
    		for(var i=0;i<oViolations.length;i=i+j+1){
    			var course = oViolations[i].course;
    			var trackPositions= new google.maps.LatLng(parseFloat(oViolations[i].lat),parseFloat(oViolations[i].lon));
    			var oMarker = new $WL.Maps.Marker({
    				position: trackPositions,
    				map: oMap,
    				options: {
    				iconType: "vehicles",
    				iconSubType: "trackpoint"
    			},
    			title: "Track Point !"
    			});
    			this._aMarkers.push(oMarker);
    			var oInfoWindow = new $WL.Maps.FleetInfoWindow({
    				map: this._oMap.get($WL.Maps.ATTR_MAP),
    				position: oMarker.getPosition()
    			});
    			this.addInfoWindowListenerTrack(oMarker, this._oMap.get($WL.Maps.ATTR_MAP), oInfoWindow, oViolations[i].gpssignal, 
    					oViolations[i].gsmsignal, oViolations[i].date, oViolations[i].speed, this);
    		}
    	},


    	/**
    	 * add idle points in the  driver reports
    	 */
    	_addIdlePoints: function(otrack,oViolations,oMap) {
    		var oViolationMarker;
    		var flagstart;
    		var idledate;
    		var idlehours;
    		var strtidledate;
    		var oInfoWindow = new $WL.Maps.FleetInfoWindow({
    			map: this._oMap.get($WL.Maps.ATTR_MAP),
    			position: null
    		});

    		for (var i = 0; i < otrack.length; i++) {

    			strtidledate=otrack[i].starttime;
    			idledate=otrack[i].endtime;
    			idlehours=otrack[i].idlehours;
    			oViolationMarker = new $WL.Maps.Marker({
    				position: new $WL.Maps.LatLng({
    					lat: parseFloat(otrack[i].lat),
    					lon: parseFloat(otrack[i].lon)
    				}),
    				map: oMap,
    				options: {
    				iconType: "vehicles",
    				iconSubType: "idlemapreport"
    			},
    			title: "idlepoint"
    			});

    			this._aMarkers.push(oViolationMarker);
    			this.addInfoWindowListeneridle(oViolationMarker,this._oMap.get($WL.Maps.ATTR_MAP),oInfoWindow,strtidledate,idledate,idlehours,"idle",otrack[i].locationname,this);

    		}

    	},

    	/**
    	 * creting popup for the driver reports
    	 */
    	addInfoWindowListener:function(oMarker,oGMap,oInfoWindow,oTimeFrame,pnt,loc,oSelf){
    		$WL.Maps.Event.addListener(oMarker, "click", function(e) {
    			oInfoWindow.drawAndMoveTo({
    				position: oMarker.getPosition(),
    				map: oGMap,
    				content: {
    				overview: oSelf._getContentOverviewTable(oTimeFrame,pnt,loc),
    			}
    			});
    		});
    	},
    	/*
    	 * Creating popup for the track report
    	 */




    	addInfoWindowListenerTrack:function(oMarker,oGMap,oInfoWindow,gpssignal,gsmsignal,date,speed,oSelf){
    		$WL.Maps.Event.addListener(oMarker, "click", function(e) {
    			oInfoWindow.drawAndMoveTo({
    				position: oMarker.getPosition(),
    				map: oGMap,
    				content: {
    				overview: oSelf._getContentOverviewTableTrack(gpssignal,gsmsignal,date,speed),
    			}
    			});
    		});
    	},

    	/**
    	 * creating pop ups for the idle points
    	 */
    	showOnMapInfoWindow:function(lat,lon,startdate,enddate,location,time){
    		oViolationMarker = new $WL.Maps.Marker({
    			position: new $WL.Maps.LatLng({
    				lat: parseFloat(lat),
    				lon: parseFloat(lon)
    			}),
    			map: map,
    			options: {
    			iconType: "vehicles",
    			iconSubType: "idlemapreport"
    		},
    		title: "idlepoint"
    		});
    		var position=new $WL.Maps.LatLng({
    			lat: parseFloat(lat),
    			lon: parseFloat(lon)
    		})
    		var oInfoWindow = new $WL.Maps.FleetInfoWindow({
    			map: map,	
    			position: oViolationMarker.getPosition()
    		});
    		oInfoWindow.drawAndMoveTo({
    			position: oViolationMarker.getPosition(),
    			map: map,
    			content: {
    			overview: this._getContentOverviewTableidle(startdate,enddate,time,"idle",location),

    		}
    		});

    	},

    	addInfoWindowListeneridle:function(oMarker,oGMap,oInfoWindow,starttime,endtime,idlehours,pnt,loc,oSelf){
    		$WL.Maps.Event.addListener(oMarker, "click", function(e) {
    			oInfoWindow.drawAndMoveTo({
    				position: oMarker.getPosition(),
    				map: oGMap,
    				content: {
    				overview: oSelf._getContentOverviewTableidle(starttime,endtime,idlehours,pnt,loc),
    			}
    			});

    		});
    	},

    	/**
    	 * Returns Overview contents for the correspoding Marker's Info Window
    	 * @param {Object} oDriver
    	 */
    	_getContentOverviewTable: function(time,pnt,loc) {
    		var sMarkup = "";
    		var oTemplateMap = this._getTemplateMap(time,pnt,loc);
    		var elTemplateContainer = $D.getElementsByClassName("iw-template-livetrack-overview", null, this.baseElement);
    		if (elTemplateContainer.length > 0) {
    			sMarkup = $U.processTemplate(elTemplateContainer[0].innerHTML, oTemplateMap);
    		}
    		return sMarkup;
    	},
    	_getContentOverviewTableTrack: function(gpssignal,gsmsignal,date,speed) {
    		var sMarkup = "";
    		var oTemplateMap = this._getTemplateMapTrack(gpssignal,gsmsignal,date,speed);
    		var elTemplateContainer = $D.getElementsByClassName("iw-template-livetrack-track", null, this.baseElement);
    		if (elTemplateContainer.length > 0) {
    			sMarkup = $U.processTemplate(elTemplateContainer[0].innerHTML, oTemplateMap);
    		}
    		return sMarkup;
    	},

    	_getContentOverviewTableidle: function(starttime,endtime,idlehours,pnt,loc) {
    		var sMarkup = "";
    		var oTemplateMap = this._getTemplateMapidle(starttime,endtime,idlehours,pnt,loc);
    		var elTemplateContainer = $D.getElementsByClassName("iw-template-livetrack-idlepoint", null, this.baseElement);
    		if (elTemplateContainer.length > 0) {
    			sMarkup = $U.processTemplate(elTemplateContainer[0].innerHTML, oTemplateMap);
    		}

    		return sMarkup;
    	},

    	/**
    	 * Returns an object which can be passed as the template map to be processed.
    	 * @param {Object} oDriver
    	 */
    	_getTemplateMap: function(time,pnt,loc) {

    		return {
    			time:time.toLocaleString(),
    			point:pnt,
    			location:loc
    		};
    	},
    	_getTemplateMapTrack: function(gpssignal,gsmsignal,date,speed) {

    		return {
    			// gps:gpssignal.toFixed(1),
    			// gsm:gsmsignal.toFixed(1),
    			gsm:gpssignal.toFixed(1),
    			gps:gsmsignal.toFixed(1),
    			spd:speed,
    			tme:date
    		};
    	},
    	_getTemplateMapidle: function(starttime,endtime,idlehours,pnt,loc) {

    		return {
    			starttime:starttime.toLocaleString(),
    			endtime:endtime.toLocaleString(),
    			idlehours:idlehours,
    			point:pnt,
    			location:loc
    		};
    	},
        getDataURL: function(){
            var sURL = null;
            var sDriverID = this.get($W.BaseReport.ATTR_SELECTED_ITEM);
            var oTimeFrame = this.get($W.BaseReport.ATTR_SELECTED_TIMEFRAME);
            if (sDriverID && oTimeFrame) {
                sURL = "/fleet/view/reports/?module=/blocks/json&data=view&report=drivermapreport&driverID=" + sDriverID;
                sURL += "&localTime=" +$U.getLocalTime();
                sURL += "&startdate=" + oTimeFrame.startDate;
                sURL += "&enddate=" + oTimeFrame.endDate;
            }
            return sURL;
        }
    });
    $L.augmentObject($W.DriverMapReport.prototype, {
        /**
         * Initialization function
         */
        init: function(el, oArgs){
            if (!oArgs) {
                oArgs = {};
            }
            oArgs.dataType = $W.BaseReport.KEY_TYPE_JSON;
            $W.DriverMapReport.superclass.constructor.call(this, el, oArgs);
            var aElButtons = $D.getElementsByClassName("button-done", null, el);
            $W.Buttons.addDefaultHandler(aElButtons, function(oArgs){
                var elErrDialog = $D.getElementsByClassName("simpledialog", null, this.elBase)[0];
                if (!$D.hasClass(elErrDialog, "disabled")) {
                    $D.addClass(elErrDialog, "disabled");
                }
            }, null, this);
            aElButtons = null;
            el = null;
            oArgs = null;
        }
    });
})();
