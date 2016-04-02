(function() {
	var $W = getPackageForName("com.i10n.fleet.widget.ui");
	var $WL = getPackageForName("com.i10n.fleet.widget.lib");
	var $U = getPackageForName("com.i10n.fleet.Utils");
	var $L = YAHOO.lang;
	var $D = YAHOO.util.Dom;
	var $V = getPackageForName("com.i10n.fleet.widget.view");
	var live = null;
	var groupId=null;
	var prvgroupId=null;
	var selectedStatus=null;
	var count=0;
	var isPushLetAlive=true;
	/**
	 * LiveTrack Widget for Fleetcheck project
	 * 
	 * @author sabarish
	 * 
	 */
	var oMarkers = {};
	var oInfoWindow = null;
	$W.LiveTrack = function(el, oArgs) {
		/**
		 * InitParams supplied
		 */
		this.init = function(el, oArgs) {
			this.initParams = oArgs;
			$W.LiveTrack.superclass.constructor.call(this, el, oArgs);
			this.subscribe($W.LiveTrack.EVT_SELECTED_VEHICLE_CHANGE, function(oEvtArgs) {
				$WL.Maps.Event.trigger(this.get($W.LiveTrack.ATTR_VEHICLE_MARKERS)[oEvtArgs.newValue], "click");
			}, this, true);
			this.subscribe($W.LiveTrack.EVT_SELECTED_GROUP_CHANGE, function(oEvtArgs) {
				groupId=oEvtArgs.newValue;
				var oTarget = $D.getElementsByClassName('item-group-select', null, this.listBaseElement)[0];
				//Selecting group from dropdown list.
				selectedStatus = oTarget[oTarget.selectedIndex].value;
				if(groupId==null  || groupId!=prvgroupId){
					if(groupId==null && prvgroupId!=null){
						$W.LiveTrack.superclass.constructor.call(this,el,oArgs);
						this.initializeMapMarkers(oArgs.datasource);
					}
					if(groupId!=prvgroupId){
						$W.LiveTrack.superclass.constructor.call(this,el,oArgs);
						this.initializeMapMarkers(oArgs.datasource);
					}
				}else if(prvgroupId!=null){
					$W.LiveTrack.superclass.constructor.call(this,el,oArgs);
					this.initializeMapMarkers(oArgs.datasource);
				}
				else{
					this.initializeMapMarkers(oArgs.datasource);
				}	
				prvgroupId=groupId;
			}, this, true);
			this.initializeMapMarkers(oArgs.datasource);
			live=this;
			$V.BaseView.subscribeFn("livetrack",this.callback);
		};

		this.baseElement = el;
		this.initParams = oArgs;
		this.init(this.baseElement, oArgs);

		//		YAHOO.lang.later(30*1000, this, 'timerStart',[{data:'bar', data2:'zeta'}],true);
	};

	$L.augmentObject($W.LiveTrack, {
		ATTR_VEHICLE_MARKERS: "vehicleMarkers",
		ATTR_GROUP_MARKERS: "GroupMarkers",
		ATTR_SELECTED_VEHICLE: "selectedVehicle",
		ATTR_SELECTED_GROUP: "selectedGroup",
		EVT_SELECTED_VEHICLE_CHANGE: "selectedVehicleChange",
		EVT_SELECTED_GROUP_CHANGE: "selectedGroupChange",
	});

	$L.extend($W.LiveTrack, $W.FleetMap, {
		/**
		 * Initializes the Attributes
		 */
		initAttributes: function() {
		$W.LiveTrack.superclass.initAttributes.call(this);
		/**
		 * @attribute vehicleMarkers
		 * @description Current Vehicle Markers added to the map
		 * @type Object
		 */
		this.setAttributeConfig($W.LiveTrack.ATTR_VEHICLE_MARKERS, {
			value: null
		});

		/**
		 * @attribute groupMarkers
		 * @description Current Group Markers added to the map
		 * @type Object
		 */
		this.setAttributeConfig($W.LiveTrack.ATTR_GROUP_MARKERS, {
			value: null
		});
		/**
		 * @attribute selectedVehicle
		 * @description Current Vehicle Selected.
		 * @type Object
		 */
		this.setAttributeConfig($W.LiveTrack.ATTR_SELECTED_VEHICLE, {
			value: null
		});
		/**
		 * @attribute selectedGroup
		 * @description Current Group Selected.
		 * @type Object
		 */
		this.setAttributeConfig($W.LiveTrack.ATTR_SELECTED_GROUP, {
			value: null
		});


	}
	});
	$L.augmentObject($W.LiveTrack.prototype, {
		onDataReturnInitializeMap: function(sRequest, oResponse, oPayload) {
		if (oResponse && $L.isArray(oResponse.results)) {
			oInfoWindow=new $WL.Maps.FleetInfoWindow({
				map: this.get($WL.Maps.ATTR_MAP)
			});
			if(groupId==null){
				this.addMarkers(oResponse.results);
			}else{
				//this.removemarkers(oResponse.results);
				this.addMarkers(oResponse.results);
			}
		}
	},
	removemarkers:function(aData){
		for (var i = 0; i<aData.length; i++){
			var oVehicle = aData[i];
			var icon=null;
			var iconid=oVehicle.icon;
			if(iconid > 0  && iconid < 100){
				icon="custom-"+iconid+"";
			}else
				icon="vehicles";
			var status=oVehicle.status;
			var mark=this.get($W.LiveTrack.ATTR_VEHICLE_MARKERS)[aData.id];
			if(mark!=null){
				mark.setMap(null);
			}
		}
	},
	timerStart:function(data) {
		this.callback.scope = this;
		YAHOO.util.Connect.asyncRequest('GET',  "/fleet/view/controlpanel/?markup=DemoManage&debug=true&module=/blocks/json"+
				"&data=view&subpage=liveData&dataView=assignment", this.callback,null);
	},

	callback:{
		success: function(liveUpdateData) {
		if(isPushLetAlive){
			live.onVehiclePositionUpdatePushlet(liveUpdateData);
		}else{
			// Use this for non pushlet based auto update
			live.onVehiclePositionUpdateTimer(liveUpdateData);
		}
	},
	failure: function(o) {

	},
	},

	onVehiclePositionUpdateTimer: function(result){
		var oResponse=JSON.parse(result.responseText);
		var aData1=$U.Arrays.mapToArray(oResponse);
		var veh;
		var isdata;
		var allmarkers = {};
		for(var m=0 ; m< aData1.length;m++){
			var mobject=aData1[m];
			var aData=$U.Arrays.mapToArray(mobject);
			for(var n=0 ; n< aData.length;n++){
				var nobject=aData[n];
				var ndat=$U.Arrays.mapToArray(nobject);
				for(var j=0;j<ndat.length;j++){
					var jobject=ndat[j];
					var obj=$U.Arrays.mapToArray(jobject);
					live.addPushMarkersinChunks(obj);
					//					for(var k=0;k<obj.length;k++){
					//					var item =YAHOO.util.Dom.getElementsByClassName("item-name");
					//					var itemStatus=YAHOO.util.Dom.getElementsByClassName("item-status");
					//					for(var i=0;i<item.length;i++){
					//					var test=item[i].innerHTML.split("[");
					//					var div=itemStatus[i];

					//					if(test[0].trim()==obj[k].make){
					//					div.setAttribute("class","item-status inline-block item-status "+obj[k].status);
					//					}else{
					//					continue;
					//					}
					//					}
					//					live.addPushMarker(obj[k]);
					//					}	
				}
			}
		}
	},

	addPushMarkersinChunks : function(array){
		// set this to whatever number of items you can process at once
		var chunk = 10;
		var index = 0;
		function doChunk() {
			var cnt = chunk;
			while (cnt-- && index < array.length) {
				var item =YAHOO.util.Dom.getElementsByClassName("item-name");
				var itemStatus=YAHOO.util.Dom.getElementsByClassName("item-status");
				for(var i=0;i<item.length;i++){
					var test=item[i].innerHTML.split("[");
					var div=itemStatus[i];
					if(test[0].trim()==array[index].make.trim()){
						div.setAttribute("class","item-status inline-block item-status "+array[index].status);
					} else{
						continue;
					}
				}
				live.addPushMarker(array[index]);
				++index;
			}
			if (index < array.length) {
				setTimeout(doChunk, 1);
			}
		}
		doChunk();
	},
	addMarkers: function(aData) { 
		var oLatLngBounds = new $WL.Maps.LatLngBounds();
		for (var i = 0; i < aData.length; i++) {
			var oVehicle = aData[i];
			var sVehicleId = aData[i].id;
			var grpid=aData[i].groupid;
			var sGroupId = null;
			sGroupId=grpid.split("-");
			var icon=null;
			var sVehicleIdArray=null;
			for (var j=0;j<sVehicleId.length;j++){
				sVehicleIdArray=sVehicleId.split("-");
			}
			var nLatitude = parseFloat(oVehicle.lat);
			var nLongitude = parseFloat(oVehicle.lon);			 
			var status = oVehicle.status;
			var selected = null;
			if(selectedStatus != null){
				selected=selectedStatus.split("-");
			}
			if(groupId == null || groupId==sGroupId[1] || selected[1] == status ){
				var oLatLng = new $WL.Maps.LatLng({
					lat: nLatitude,
					lon: nLongitude
				});
				var iconid=oVehicle.icon;
				if(iconid > 0  && iconid < 100){
					icon="custom-"+iconid+"";
				}			
				else
					icon="vehicles";

				if(status == "online" || status == "moving"){
					if((oVehicle.course <= 30.0) || (oVehicle.course >= 330.0) ){//&& (oVehicle.course != 0) ){
						status = "online0";
					}else if ((oVehicle.course <= 60.0) && (oVehicle.course >= 30.0)){
						status = "online45";
					}else if ((oVehicle.course <= 120.0) && (oVehicle.course >= 60.0)){
						status = "online90";
					}else if ((oVehicle.course <= 150.0) && (oVehicle.course >= 120.0)){
						status = "online135";
					}else if ((oVehicle.course <= 210.0) && (oVehicle.course >= 150.0)){
						status = "online180";
					}else if ((oVehicle.course <= 240.0) && (oVehicle.course >= 210.0)){
						status = "online225";
					}else if ((oVehicle.course <= 300.0) && (oVehicle.course >= 240.0)){
						status = "online270";
					}else if ((oVehicle.course <= 330.0) && (oVehicle.course >= 300.0)){
						status = "online315";
					}else{
						status="online";
					}
				}       
				/*	var user = YAHOO.util.Dom.getElementsByClassName('user-link')[0].innerHTML;
					if(user == "bpl"){
						if(status == "idle"){
							status="idleblurb";
						}
					}
				 */
				oLatLngBounds.extend(oLatLng);

				var oMarker = new $WL.Maps.Marker({
					position: oLatLng,
					map: this.get($WL.Maps.ATTR_MAP),
					options: {
					iconType: icon,
					iconSubType: status
				},
				title: oVehicle.name
				});

				oMarker.vehicleId = sVehicleIdArray[1];
				oMarkers[sVehicleIdArray[1]] = oMarker;
				this.addInfoWindowListener(oMarker, this.get($WL.Maps.ATTR_MAP), oInfoWindow, oVehicle, this);
			}
			/**
			 * Applying Lat Lng Bounds to obtain a best fit with the available markers.
			 */
			this.get($WL.Maps.ATTR_MAP).fitBounds(oLatLngBounds);
			this.set($W.LiveTrack.ATTR_VEHICLE_MARKERS, oMarkers);
			this.set($W.LiveTrack.ATTR_GROUP_MARKERS,oMarkers);
		}
	},

	addPushMarker : function(aData){
		var livemap=this.get($WL.Maps.ATTR_MAP);
		var mark=this.get($W.LiveTrack.ATTR_VEHICLE_MARKERS)[aData.vehicleId];
		if(mark!=null){
			// For google v3 we have to use this function. 
			mark.setMap(null);
			//For open layers comment above block and uncomment below block. 
			/*var aMarkerLayers = livemap.getLayersByName("Markers");
			if (aMarkerLayers && (0 < aMarkerLayers.length)) {
				aMarkerLayers[0].removeMarker(mark);
			}*/
		}	
		var status = "online";
		var icon="";
		var oLatLngBounds = new $WL.Maps.LatLngBounds();
		var oVehicle = aData;
		var sVehicleId = aData.vehicleId;
		var nLatitude = parseFloat(oVehicle.lat);
		var nLongitude = parseFloat(oVehicle.lon);
		var oLatLng = new $WL.Maps.LatLng({
			lat: nLatitude,
			lon: nLongitude
		});
		oLatLngBounds.extend(oLatLng);
		var vehicleStatus = oVehicle.status;
		var selected = null;
		if(selectedStatus != null){
			selected=selectedStatus.split("-");
		}
		/*if(vehicleStatus==1){
			status="online";
		}else if(vehicleStatus==2){
			status="idle";
		}else if(vehicleStatus==3){
			status="offline";
		}else{
			status="stopped";
		}*/
		var grpid=oVehicle.groupid;
		var sGroupId = null;
		sGroupId=grpid.split("-");
		//Updating only selected group/status .
		var oTarget = $D.getElementsByClassName('item-group-select', null, live.listBaseElement)[0];
		//Selecting group from dropdown list value.
		selectedStatus = oTarget[oTarget.selectedIndex].value;
		if(selectedStatus =='All' || (selected !=null && selected[1] == status) || groupId==sGroupId[1]){
			if(vehicleStatus=="online" || vehicleStatus == "moving"){
				if((oVehicle.course <= 30.0) || (oVehicle.course >= 330.0) ){
					status = "online0";
				}else if ((oVehicle.course <= 60.0) && (oVehicle.course >= 30.0)){
					status = "online45";
				}else if ((oVehicle.course <= 120.0) && (oVehicle.course >= 60.0)){
					status = "online90";
				}else if ((oVehicle.course <= 150.0) && (oVehicle.course >= 120.0)){
					status = "online135";
				}else if ((oVehicle.course <= 210.0) && (oVehicle.course >= 150.0)){
					status = "online180";
				}else if ((oVehicle.course <= 240.0) && (oVehicle.course >= 210.0)){
					status = "online225";
				}else if ((oVehicle.course <= 300.0) && (oVehicle.course >= 240.0)){
					status = "online270";
				}else if ((oVehicle.course <= 330.0) && (oVehicle.course >= 300.0)){
					status = "online315";
				}else{
					status="online";
				}
			}
			else if(vehicleStatus=="idle"){
				status="idle";
			}else if(vehicleStatus=="offline"){
				status="offline";
			}else {
				status="stopped";
			}
			var iconid=oVehicle.icon;
			if(iconid > 0  && iconid < 100){
				icon="custom-"+iconid+"";
			}			
			else{
				icon="vehicles";
			}
			var oMarker = new $WL.Maps.Marker({
				position: oLatLng,
				map: live.get($WL.Maps.ATTR_MAP),
				options: {
				iconType: icon,
				iconSubType:status,
			},
			title: oVehicle.make
			});

			oMarker.vehicleId = sVehicleId;
			oMarkers[sVehicleId] = oMarker;


			live.addInfoWindowListener(oMarker, live.get($WL.Maps.ATTR_MAP), oInfoWindow, oVehicle, this);
			/**
			 * Applying Lat Lng Bounds to obtain a best fit with the available
			 * markers.
			 */
			live.set(live.get($W.LiveTrack.ATTR_VEHICLE_MARKERS)[aData.vehicleId], oMarkers);

			/**
			 * Applying Lat Lng Bounds to obtain a best fit with the available
			 * markers.
			 */
			live.set($W.LiveTrack.ATTR_VEHICLE_MARKERS, oMarkers);

		}
	},
	onVehiclePositionUpdatePushlet: function(result){
		var successCnt;
		var failCnt;
		var successCnt = 0;
		var failCnt = 0;
		//		alert('Result Before process is '+result);
		result = eval('('+result+')');
		//		alert('Result after process is '+result);
		var cont = result.content;
		for(var i in cont){ 
			this.addPushMarker(cont[i]);
		}
	},
	/**
	 * Adds Vehicle Markers to the Map.
	 */
	initializeMapMarkers: function(oDataSource) {
		var oCallback = {
				success: this.onDataReturnInitializeMap,
				failure: this.onDataReturnInitializeMap,
				scope: this

		};
		oDataSource.sendRequest(null, oCallback);
	},
	/**
	 * Adss Listeners to the Marker based on the Map,Window, and Vehicle passed.
	 * @param {Object} oMarker
	 * @param {Object} oGMap
	 * @param {Object} oInfoWindow
	 * @param {Object} oVehicle
	 * @param {Object} oSelf
	 */
	addInfoWindowListener: function(oMarker, oGMap, oInfoWindow, oVehicle, oSelf) {

		var el=YAHOO.util.Dom.getElementsByClassName("marker-info-window");

		if(this.get($W.LiveTrack.ATTR_SELECTED_VEHICLE)== oVehicle.id){
			for(var i=0;i<el.length;i++){
				if ( el[i].style.display != 'none' ){
					el[i].style.display = 'none';
				}
			}	
		}
		$WL.Maps.Event.addListener(oMarker, "click", function(e) {
			if (oMarker.vehicleId !== oSelf.get($W.LiveTrack.ATTR_SELECTED_VEHICLE)) {
				oSelf.set($W.LiveTrack.ATTR_SELECTED_VEHICLE, oMarker.vehicleId);
			}
			else{
				oInfoWindow.drawAndMoveTo({
					position: oMarker.getPosition(),
					map: oGMap,
					content: {
					overview: oSelf._getContentOverviewTable(oVehicle),
					details: oSelf._getContentDetailTable(oVehicle)
				}

				});
			}
		});

	},
	/**
	 * Returns Overview contents for the correspoding Marker's Info Window
	 * @param {Object} oVehicle
	 */
	_getContentOverviewTable: function(oVehicle) {
		var sMarkup = "";
		var oTemplateMap ="";
		oTemplateMap=this._getTemplateMap(oVehicle);
		var elTemplateContainer ="";
		elTemplateContainer = $D.getElementsByClassName("iw-template-livetrack-overview", null, this.baseElement);
		if (elTemplateContainer.length > 0) {
			sMarkup = $U.processTemplate(elTemplateContainer[0].innerHTML, oTemplateMap);
		}
		return sMarkup;
	},
	/**
	 * Returns Details contents for the correspoding Marker's Info Window
	 * @param {Object} oVehicle
	 */
	_getContentDetailTable: function(oVehicle) {
		var sMarkup = "";
		var oTemplateMap = "";
		oTemplateMap=this._getTemplateMap(oVehicle);
		var elTemplateContainer ="";
		elTemplateContainer=$D.getElementsByClassName("iw-template-livetrack-details", null, this.baseElement);
		if (elTemplateContainer.length > 0) {
			sMarkup = $U.processTemplate(elTemplateContainer[0].innerHTML, oTemplateMap);
		}
		return sMarkup;
	},
	/**
	 * Returns an object which can be passed as the template map to be processed.
	 * @param {Object} oVehicle
	 */
	_getTemplateMap: function(oVehicle) {
		if(oVehicle.drivername!=null){
			return {
				vehicleName: oVehicle.make,
				driverName: oVehicle.drivername,
				location: oVehicle.location,
				speed: oVehicle.speed,
				time: oVehicle.lastupdated,
				mobileNumber:oVehicle.mobilenumber,
				crewno: oVehicle.crewno,
				latestButtonPressed: oVehicle.latestbuttonpressed,
				gps: oVehicle.gps,
				gsm: oVehicle.gsm,
				cc: oVehicle.cc,
				battery: oVehicle.battery
			};
		}
		else{
			return {
				vehicleName: oVehicle.make,
				driverName: oVehicle.driverfirstname,
				location: oVehicle.location,
				speed: oVehicle.speed,
				time: oVehicle.lastupdatedat,
				mobileNumber:oVehicle.mobilenumber,
				crewno: oVehicle.crewno,
				latestButtonPressed: oVehicle.latestbuttonpressed,
				gps: oVehicle.gps,
				gsm: oVehicle.gsm,
				cc: oVehicle.cc,
				battery: oVehicle.battery
			};
		}
	}
	});
})();