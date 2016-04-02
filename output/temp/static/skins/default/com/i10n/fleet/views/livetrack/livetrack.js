
(function(){
	var $B = YAHOO.Bubbling;
	var $L = YAHOO.lang;
	var $YU = YAHOO.util;
	var $E = YAHOO.util.Event;
	var $D = YAHOO.util.Dom;
	var $YW = YAHOO.widget;
	var $W = getPackageForName("com.i10n.fleet.widget.ui");
	var $V = getPackageForName("com.i10n.fleet.widget.view");
	var $U = getPackageForName("com.i10n.fleet.Utils");
	var globalDataToShowPopUp;
	var pageid="livetrack";
	var isFrsClient = false;
	var oData;
	/**
	 * LiveTack View Widget. Has a SubNav View
	 *
	 * @author sabarish
	 */
	$V.LiveTrack = function(){

		$V.LiveTrack.superclass.constructor.call(this);
	};
	$L.extend($V.LiveTrack, $V.BaseSubNavView, {
		/**
		 * Initialization Function
		 */
		init: function(oArgs){

		this._oDataSource = this._createDataSource();
		this._oAlertSource = this._createAlertSource();
		var user = this.getUserName();

		$V.LiveTrack.superclass.init.call(this, oArgs);
		var elVehicleList = $D.get("vehiclelist");
		var oVehicleList = new $W.VehicleList(elVehicleList, {
			datasource: this.getDataSource()
		});
		this._widgets.vehiclelist = oVehicleList;

		/**
		 * Subscribing to resize of Vehicle List.
		 */
		oVehicleList.subscribe($W.MinimizableList.EVT_RESIZE, this.onSidePaneListResize, this, true);
		oVehicleList.subscribe($W.VehicleList.EVT_SELECTED_VEHICLE_CHANGE, this.onVehicleSelect, this, true);
		oVehicleList.subscribe($W.VehicleList.EVT_SELECTED_GROUP_CHANGE, this.onGroupSelect, this, true);
		/**
		 * Subscribing to activeTabChang in Sub Navigator
		 */
		this._widgets.subnav.subscribe("activeTabChange", this.onTabChange, this, true);
		//this.hideAlertPopup();

		// Invoke alert pop up for every 5 minutes 
		YAHOO.lang.later(300000, this, 'timerStart',[{data:'bar', data2:'zeta'}],true);
		
		/**
		 * To maintain required state if the url is deeplinked
		 */
		this.changeTo(this._widgets.subnav.get("activeTab").get("navId"));
	},
	/**
	 * Overrides BaseSubNavView#loadWidget to pass the datasource along with the init params of the widget.
	 * @param {Object} el
	 * @param {Object} sWidgetName
	 * @param {Object} sWidgetArgs
	 * @param {Object} sPageId
	 */
	loadWidget: function(el, sWidgetName, sWidgetArgs, sPageId){
		pageid=sPageId;
		if(sPageId === "alertstatus")
		{
			$V.LiveTrack.superclass.loadWidget.call(this, el, sWidgetName, {
				datasource: this.getAlertSource()
			}, sPageId);

		}else
		{
			$V.LiveTrack.superclass.loadWidget.call(this, el, sWidgetName, {
				datasource: this.getDataSource()
			}, sPageId);
		}
		if ("livetrack" === sPageId && this._widgets && this._widgets.livetrack) {
			this._widgets.livetrack.subscribe($W.LiveTrack.EVT_SELECTED_VEHICLE_CHANGE, this.onMarkerSelect, this, true);
			this.handleDeepLinks();
		}
	},
	getUserName:function(){

		var source=_publish.vehicles.vehicleData;
		var data= $U.Arrays.mapToArray(source);

		var deepdata=$U.Arrays.mapToArray(data[0]);
		var name=$U.MapToParams(deepdata);
		var suser=null;
		for (var i=0;i<name.length;i++){
			suser=name.split("=");
		}
		var userName=suser[suser.length-1];
		return userName;
	}
	});
	$L.augmentObject($V.LiveTrack.prototype, {

		/**
		 * Creates a datassource for the widgets based on the vehicleData published.
		 */
		_createDataSource: function(){
		var oDataSource = null;
		if (_publish.vehicles && _publish.vehicles.vehicleData) {

			oDataSource = new $V.LiveTrack.StreamingDataSource(_publish.vehicles.vehicleData);
			oDataSource.responseType = $YU.DataSource.TYPE_JSARRAY;

			oDataSource.responseSchema = {
					elementField: "vehicles",
					fields: ["id","index", "name", "status", "cc", "gps", "gsm", "battery", "latestbuttonpressed",
					         "battery","seatbelt","ignition","district","imeino","baselocation",
					         "crewno","fuel","violationtype","alertstatus","refid","lastupdated",
					         "moduleupdatetime","lat", "lon", "location", "make", "model", "speed", 
					         "drivername","course","icon","groupid","mobilenumber","timeinmilliseconds"]

			};         
		}
		return oDataSource;
	},

	_createAlertSource: function(){
		var oAlertSource = null;
		if (_publish.vehicles && _publish.vehicles.vehicleData) {

			oAlertSource = new $V.LiveTrack.StreamingDataSource(_publish.vehicles.vehicleData);
			oAlertSource.responseType = $YU.DataSource.TYPE_JSARRAY;

			oAlertSource.responseSchema = {
					elementField: "alerts",
					fields: ["refid","vehiclename","drivername","baselocation","crewno","location",
					         "violationtype","time","alert"]                
			};         
		}
		return oAlertSource;
	},

	/**
	 * handles deep links.
	 */
	handleDeepLinks: function(){
		if (_publish && _publish.parameters && _publish.parameters.current && _publish.parameters.current.vehicleID ) {
			this._widgets.vehiclelist.set($W.VehicleList.ATTR_SELECTED_VEHICLE, _publish.parameters.current.vehicleID);
			this._widgets.vehiclelist.set($W.VehicleList.ATTR_SELECTED_GROUP, _publish.parameters.current.groupID);
		}
	},
	/**
	 * Listener function to change in Vehicle Selection in Vehicle List Widget
	 * @param {Object} oArgs
	 */
	onVehicleSelect: function(oArgs){
		var sVehicleId = oArgs.newValue;
		var sVehicleIdArray=null;
		for (var i=0;i<sVehicleId.length;i++){
			sVehicleIdArray=sVehicleId.split("-");
		}
		if (this._widgets && this._widgets.livetrack) {
			if (sVehicleIdArray[1] !== this._widgets.livetrack.get($W.LiveTrack.ATTR_SELECTED_VEHICLE)) {
				this._widgets.livetrack.set($W.LiveTrack.ATTR_SELECTED_VEHICLE, sVehicleIdArray[1]);
			}
		}
	},
	/**
	 * Listener function to change in Group Selection in Vehicle List Widget
	 * @param {Object} oArgs
	 */
	onGroupSelect: function(oArgs){
		var sGroupId = oArgs.newValue;

		var sGroupIdArray=null;
		for (var i=0;i<sGroupId.length;i++){
			sGroupIdArray=sGroupId.split("-");
		}
  
		if (this._widgets && this._widgets.livetrack) {
			if (sGroupIdArray[1] !== this._widgets.livetrack.get($W.LiveTrack.ATTR_SELECTED_GROUP)) {
				this._widgets.livetrack.set($W.LiveTrack.ATTR_SELECTED_GROUP, sGroupIdArray[1]);
			}
		}
	},
	/**
	 * Listener function to change in Marker Selection in Maps
	 * @param {Object} oArgs
	 */
	onMarkerSelect: function(oArgs){ 
		var sVehicleId = oArgs.newValue;
		for (var i=0;i<sVehicleId.length;i++){
			sVehicleIdArray=sVehicleId.split("-");
		}
		if (this._widgets && this._widgets.vehiclelist) {
			if (sVehicleIdArray[1] !== this._widgets.vehiclelist.get($W.VehicleList.ATTR_SELECTED_VEHICLE)) {
				this._widgets.vehiclelist.set($W.VehicleList.ATTR_SELECTED_VEHICLE, sVehicleIdArray[1]);
			}
		}
	},


	/**
	 * Executed when Tabs in HeaderSubNavigator is changed. Rite now used
	 * only to hide/show the VehicleList Widget based on the view
	 */
	onTabChange: function(oArgs){
		var oNewTab = oArgs.newValue;
		sPageId = oNewTab.get("navId");
		this.changeTo(sPageId);
	},
	oCallback:{
		success: function(o) {
		var oResponse=JSON.parse(o.responseText);
		var aData=$U.Arrays.mapToArray(oResponse);
		var veh;
		var isdata;
		for(var inc=0 ; inc< aData.length;inc++){
			var object=aData[inc];
			var dat=$U.Arrays.mapToArray(object)
					for(var j=0;j<dat.length;j++){
						obj=$U.Arrays.mapToArray(dat[j]); 
						isdata=obj.length;
					}
		}

		if(isdata>0){
			var AlertPopUp = new $W.LiveTrack.TripCreationPopUp($D.getElementsByClassName("create-trip")[0], aData);
			this._widgets.TripCreationPopUp = AlertPopUp;
			this._widgets.TripCreationPopUp.hide();
			this.showAlertPopUp();
		}
	},
	failure: function(o) {

	},
	},

	timerStart:function(data) { 
		this.oCallback.scope = this;
		YAHOO.util.Connect.asyncRequest('GET',  "/fleet/view/controlpanel/?markup=DemoManage&debug=true&module=/blocks/json"+
				"&data=view&subpage=alert&dataView=assignment&isGoogleHit=false", this.oCallback,null);
	},

	showAlertPopUp: function(){
		var alert = $D.getElementsByClassName("create-trip");
		$D.removeClass(alert,"disabled");
		this._widgets.TripCreationPopUp.render();
		this._widgets.TripCreationPopUp.show();
	},

	/**
	 * Changing View UI while changing page
	 * @param {Object} sPageID
	 */
	changeTo: function(sPageID){
		if (sPageID === "vehiclestatus") {
			this._widgets.vehiclelist.disable();
		}else
		 if (sPageID === "alertstatus") {
			this._widgets.vehiclelist.disable();
		}
		else {
			this._widgets.vehiclelist.enable();
		}
	},
	/**
	 * Executed when SidePaneList is minimized/maximized.
	 */
	onSidePaneListResize: function(oArgs, oSelf){
		var elContainer = $D.getElementsByClassName("view-container")[0];
		if ($W.MinimizableList.STATE_MINIMIZED == oArgs.currentState) {
			if (!$D.hasClass(elContainer, "list-minimized")) {
				$D.addClass(elContainer, "list-minimized");
			}
		}
		else {
			if ($D.hasClass(elContainer, "list-minimized")) {
				$D.removeClass(elContainer, "list-minimized");
			}
		}
	},
	/**
	 * Returns the datasource needed for the widgets inside this view.
	 */
	getDataSource: function(){
		return this._oDataSource;
	},

	getAlertSource: function(){
		return this._oAlertSource;
	}
	});


	$W.LiveTrack.TripCreationPopUp = function(el,oArgs){
		/*Initializing*/
		var recorddata=oArgs;
		this.initCreateTrip(el,oArgs);
		var pos=0;
		this.setDataInLive(oArgs,pos);

		$W.Buttons.addDefaultHandler($D.getElementsByClassName("submit-button", null, el), function(params){
			this.pos=this.pos-1;
			this.setDataInLive(oArgs,this.pos);      	 
		},null,this);

		$W.Buttons.addDefaultHandler($D.getElementsByClassName("cancel-button", null, el), function(params){
			this.pos=this.pos+1;
			this.setDataInLive(oArgs,this.pos);      	 
		},null,this);
	};

	$L.extend($W.LiveTrack.TripCreationPopUp, $W.AlertPopUp);
	$L.augmentObject($W.LiveTrack.TripCreationPopUp.prototype, {
		/*Declaring the datamembers*/
		elDisplayedContent: null,
		/**
		 * The initalization Function
		 */
		initCreateTrip: function(el,oArgs){
		this.elDisplayedContent = el;

		$W.LiveTrack.TripCreationPopUp.superclass.constructor.call(this, this.elDisplayedContent, {
			fixedcenter: false,
			width: "300px",
			height: "auto"
		});
	},

	/**
	 * Method that retrives the data from the server each time the
	 * popup is shown
	 */


	onPrevious:function(){
		this.pos=this.pos+1;
		this.setDataInLive(this.recorddata,this.pos);
	},

	onNext:function(){
		this.setDataInLive(this.data1,this.pos++);
	},

	setDataInLive:function(oArgs,pos){
		this.pos=pos;

		for(var inc=0 ; inc< oArgs.length;inc++){
			var object=oArgs[inc];
			var dat=$U.Arrays.mapToArray(object)
					for(var j=0;j<dat.length;j++){
						obj=$U.Arrays.mapToArray(dat[j]); 
						for(var k=0;k<obj.length;k++){
							var veh=obj[pos];
							if(pos>=obj.length){
								pos=pos-1;
								this.pos=pos;
							}
							if(pos<0){
								pos=pos+1;
								this.pos=pos;
							}

							$D.get("vehiclename").innerHTML='VehicleName::  '+veh.vehiclename;
							$D.get("drivername").innerHTML='DriverName:: '+veh.drivername;
							if(isFrsClient){
								$D.get("baselocation").innerHTML='BaseLocation::  '+veh.baselocation;
								$D.get("mobilenumber").innerHTML='MobileNumber:: '+veh.crewno;
							}
							$D.get("alerttime").innerHTML='AlertTime::'+veh.alerttime;	
							$D.get("alerttype").innerHTML='AlertType:: '+veh.alerttype;
							$D.get("alertvalue").innerHTML='AlertValue:: '+veh.alertvalue;
						}
					}
		}
	},
	});

	/**
	 * Will allow support for streaming functionality. Rite now just extends
	 * GroupedDataSource and the streaming functionality is not yet implemented
	 * @param {Object} oData
	 */
	$V.LiveTrack.StreamingDataSource = function(oData){
		$V.LiveTrack.StreamingDataSource.superclass.constructor.call(this, oData);
	};
	$L.extend($V.LiveTrack.StreamingDataSource, $W.GroupedReport.GroupedDataSource);
})();
