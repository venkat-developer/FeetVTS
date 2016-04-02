(function(){
	var $W = getPackageForName("com.i10n.fleet.widget.ui");
	var $WU = getPackageForName("com.i10n.fleet.widget.util");
	var $U = getPackageForName("com.i10n.fleet.Utils");
	var $L = YAHOO.lang;
	var $E = YAHOO.util.Event;
	var $D = YAHOO.util.Dom;
	var $YU = YAHOO.util;
	var $B = YAHOO.Bubbling;
	var $YU = YAHOO.util;
	var $YW = YAHOO.widget;
	var $V = getPackageForName("com.i10n.fleet.widget.view");
	var region=null;
	var regionShape = null;
	var allMarkers=[];
	var regionIdinAddEntity = null;

	/**
	 * Geofencing Sub Nav Page
	 *
	 * @author sabarish,N.Balaji,dharmaraju
	 */
	$W.GeoFencing = function(el, params){
		/*Declaring the memeber properties*/
		this.elBase = el;
		this.elContainer = el;
		this._widgets = {};
		/*Initializing*/
		this.init(el, params);
		this.handleDeepLinks();
	};

	$L.augmentProto($W.GeoFencing, $YU.EventProvider);
	$L.augmentObject($W.GeoFencing.prototype, {


		/*Declaring all the member properties*/
		gofencingCreationPopUp: null,
		geoFencingSideBar: null,
		/**
		 * Initialization function
		 */
		init: function(el, params){


		/*Creating the region settings toolbar*/
		var toolBarEl = $D.getElementsByClassName("geofencingtoolbar", null, el)[0];
		var toolBar = new $W.GeoFencingToolBar(toolBarEl);
		this._widgets.toolbar = toolBar;

		/*Instantiating the necessary variables*/

		/*flipcontainer is the container that holds all the popups in this page*/
		var flipContainer = $D.getElementsByClassName("flip-container", null, this.elBase)[0];

		/*The create region popup is the popup that is displayed when the addregion button is clicked*/
		var geofencingCreationPopUp = new $W.GeoFencing.GeoFencingCreationPopUp($D.getElementsByClassName("add-region", null, flipContainer)[0]);
		this._widgets.geofencingCreationPopUp = geofencingCreationPopUp;

		var flipContainer1 = $D.getElementsByClassName("flip-container1", null, this.elBase)[0];
		/*The howtodraw popup is the popup that is displayed when the Howtodraw button is clicked*/
		var howtodrawPopUp = new $W.GeoFencing.HowToDrawPopUp($D.getElementsByClassName("draw-region",null,flipContainer1)[0]);
		this._widgets.howtodrawPopUp = howtodrawPopUp;

		var flipContainer2 = $D.getElementsByClassName("flip-container2", null, this.elBase)[0];
		/*The draw region popup is the popup that is displayed when the link is clicked*/
		var geofencingEditRegionPopUp = new $W.GeoFencing.GeoFencingEditRegionPopUp($D.getElementsByClassName("edit-region", null, flipContainer2)[0]);
		this._widgets.geofencingEditRegionPopUp = geofencingEditRegionPopUp;

		var flipContainer3 = $D.getElementsByClassName("flip-container3", null, this.elBase)[0];
		/*The draw region popup is the popup that is displayed when the link is clicked*/
		var geofencingDeleteRegionPopUp = new $W.GeoFencing.GeoFencingDeleteRegionPopUp($D.getElementsByClassName("delete-region", null, flipContainer3)[0]);
		this._widgets.geofencingDeleteRegionPopUp = geofencingDeleteRegionPopUp;

		var flipContainer4 = $D.getElementsByClassName("flip-container4", null, this.elBase)[0];
		/*The draw region popup is the popup that is displayed when the link is clicked*/
		var geofencingSaveRegionPopUp = new $W.GeoFencing.GeoFencingSaveRegionPopUp($D.getElementsByClassName("save-region", null, flipContainer4)[0]);
		this._widgets.geofencingSaveRegionPopUp = geofencingSaveRegionPopUp;

		/*Creating the Geofencing Sidebar*/
		this._widgets.geoFencingSideBar = new $W.GeoFencingSideBar($D.getElementsByClassName("side-pane", null, this.elBase)[0]);
		/*Creating the Region Deatils Widget*/
		this._widgets.geoFencingDetails = new $W.GeoFencingDetails($D.getElementsByClassName("geofencingdetails", null, this.elBase)[0]);

		if (!params) {
			params = {};
		}
		if (!$L.isString(params.vacantListClass)) {
			params.vacantListClass = "vacant-list";
		}
		if (!$L.isString(params.assignedListClass)) {
			params.assignedListClass = "assigned-list";
		}
		if (!$L.isString(params.assignmentControlClass)) {
			params.assignmentControlClass = "assignment-control";
		}

		this.oConfiguration= params;
		/*Installing listeners*/
		this.addListeners();

	},
	/**
	 * Handles Deeplinking to assign a vehicle.
	 */
	handleDeepLinks: function(){
		if (_publish.parameters && _publish.parameters.current && _publish.parameters.current.action && _publish.parameters.current.action == "assign") {
			/**
			 * Assign Action
			 */
			var sVehicleID = _publish.parameters.current.vehicleID;
			var sDriverID = _publish.parameters.current.driverID;
			if (sVehicleID) {
				var elVehList = $D.getElementsByClassName("list-veh", "select", this.elBase)[0];
				$D.getElementsBy(function(el){
					var bResult = false;
					if ($D.getAttribute(el, "value") == sVehicleID) {
						bResult = true;
					}
					return bResult;
				}, "option", elVehList, function(el){
					$D.setAttribute(el, "selected", "true");
				});
				elVehList = null;
			}

			if (sDriverID) {
				var elDrvList = $D.getElementsByClassName("list-drv", "select", this.elBase)[0];
				elDrvList.value = sDriverID;
				$D.getElementsBy(function(el){
					var bResult = false;
					if ($D.getAttribute(el, "value") == sDriverID) {
						bResult = true;
					}
					return bResult;
				}, "option", elDrvList, function(el){
					$D.setAttribute(el, "selected", "true");
				});
				elDrvList = null;
			}
			sVehicleID = null;
			sDriverID = null;
			this.showGeoFencingCreationPopup();
		}
	},

	/**
	 * The handler function that is fired when the add region button on the
	 * Geofencing toolbar is clicked
	 */
	showGeoFencingCreationPopup: function(){
		this._widgets.geofencingCreationPopUp.render();
		this._widgets.geofencingCreationPopUp.show();
	},

	showGeoFencingDeleteRegionPopUp: function(){
		if(this.regionId != null){

			this.oCallBacks.deleteRegion.scope = this;

			$U.Connect.asyncRequest("GET", "/fleet/view/dashboard/&regionID="
					+ this.regionId, this.oCallBacks.deleteRegion, null);

			this._widgets.geofencingDeleteRegionPopUp.render();
			this._widgets.geofencingDeleteRegionPopUp.show();
		}

		else if(this.regionIdinAddEntity != null){
			this.oCallBacks.deleteRegion.scope = this;

			$U.Connect.asyncRequest("GET", "/fleet/view/dashboard/&regionID="
					+ this.regionId, this.oCallBacks.deleteRegion, null);

			this._widgets.geofencingDeleteRegionPopUp.render();
			this._widgets.geofencingDeleteRegionPopUp.show();

		}
		else {
			alert("region not selected");
		}

	},

	geofencingDeleteRegionSubmitted: function(args){

		this.deleteEntity();
		this._widgets.geofencingDeleteRegionPopUp.hide();
	},      

	showGeoFencingSaveRegionPopUp: function(){
	},

	geofencingSaveRegionSubmitted: function(args){

		if(this.regionId != null){
			//                  this._widgets.geoFencingDetails.onSaveRegion(this.regionId);
			this.oCallBacks.saveRegion.scope = this;
			allMarkers = this._widgets.geoFencingDetails.getAllMarkers();
			var arrayOfLatLngs = [];
			for(var j=0; j<allMarkers.length; j++){
				arrayOfLatLngs[j]=allMarkers[j].getPosition();
			}
			
			var xpoints =new google.maps.LatLng(0, 0)
			for(var j=allMarkers.length;j<(allMarkers.length+4);j++){
				var lat=xpoints.lat()+(j+1);
				var lng=xpoints.lng()+(j+1);
				var latlng= new google.maps.LatLng(lat,lng);
				arrayOfLatLngs[j]=latlng;
		}
			$U.Connect.asyncRequest("GET", "@APP_CONTEXT@/form/geofencing/?command_type=geo_fencing_save&arrayOfLatLngs="
					+arrayOfLatLngs+"&regionId="+this.regionId, this.oCallBacks.saveRegion, null);
		}
		else if(this.regionIdinAddEntity != null){
			this.oCallBacks.saveRegion.scope = this;
						allMarkers = this._widgets.geoFencingDetails.getAllMarkers();
			var arrayOfLatLngs = [];
			for(var j=0; j<allMarkers.length; j++){
				arrayOfLatLngs[j]=allMarkers[j].getPoint();
			}
			$U.Connect.asyncRequest("GET", "@APP_CONTEXT@/form/geofencing/?command_type=geo_fencing_save&arrayOfLatLngs="
					+arrayOfLatLngs+"&regionId="+this.regionIdinAddEntity, this.oCallBacks.saveRegion, null);

		}
		else {
			alert("region not selected");
		}
	}, 

	showHowToDrawPopUp: function(){
		this._widgets.howtodrawPopUp.render();
		this._widgets.howtodrawPopUp.show();
	},

	howtodrawSubmitted: function(args){
		this._widgets.howtodrawPopUp.hide();
	},


	showGeoFencingEditRegionPopup: function(){

		if(this.regionId != null){

			this.oCallBacks.editRegion.scope = this;

			$U.Connect.asyncRequest("GET", "@APP_CONTEXT@/view/controlpanel/?markup=DemoManage&debug=true&module=/blocks/json"+
					"&data=view&subpage=geofencing&dataView=assignment&regionID="
					+ this.regionId, this.oCallBacks.editRegion, null);

			this._widgets.geofencingEditRegionPopUp.render();
			this._widgets.geofencingEditRegionPopUp.show();
		}
		else {
			alert("region not selected");
		}

	},

	geofencingEditRegionSubmitted: function(args){

		this._widgets.geofencingEditRegionPopUp.hide();
	},

	/**
	 * The handler function that is fired when a region is submitted
	 * through the regionCreationPopUp
	 *
	 * @param {Object} args
	 */

	geofencingSubmitted: function(args){
		/*Write the data back to the server*/
		/*Correcting the scope of the callback obj*/

		this.oCallBacks.geofencingCreation.scope = this;
		this.oCallBacks.oAddEntity.scope = this ;

		this.regionName= $D.getElementsByClassName("region-name")[0].value;
		this.speedLimit= $D.get("speed").value;           

		var aRadioEl = $D.getElementsByClassName("region-shape", null, this.elContainer);
		for (var i = 0; i < aRadioEl.length; i++) {
			if (aRadioEl[i].checked) {
				this.regionShape=aRadioEl[i].value;
			}
		}
		/*
		 * TODO: Once the data logic is implemented, replace the
		 *       URL with the proper URL to Hit and select the
		 *       newly added region by default
		 */
		// $U.Connect.asyncRequest('GET',"/fleet/view/dashboard/", this.geofencingCreation,null);:
		$U.Connect.asyncRequest('GET', "@APP_CONTEXT@/form/geofencing/?command_type=geo_fencing_add&region-name="+this.regionName
				+"&speed-limit="+this.speedLimit+"&region-shape="+this.regionShape, this.oCallBacks.geofencingCreation,null);
		this._widgets.geofencingCreationPopUp.hide();
	},

	/**
	 * Shows the assignement controls
	 */
	showAssignmentControls: function() {
		$D.getElementsByClassName(this.oConfiguration.assignmentControlClass, null, this.elBase, function(elTarget) {
			if ($D.hasClass(elTarget, "disabled")) {
				$D.removeClass(elTarget, "disabled");
			}
		});
	},

	/**
	 * reloads a list
	 * @param {Object} elList
	 * @param {Object} sItemTemplate
	 * @param {Object} oData
	 * @param {Function} fComparator specifies the order in which the list should be sorted
	 */
	reloadList: function(elList, sItemTemplate, oData, fComparator) {
		$D.getElementsByClassName("list-item", null, elList, function(elTarget) {
			elTarget.parentNode.removeChild(elTarget);
		});
		/*Sorting the data*/
		var newList = $U.Arrays.mapToArray(oData);
		newList = newList.sort(fComparator);
//		$U.Element.DivElement.appendTemplatizedChildren(elList, sItemTemplate, newList);
		var childstring="";
		for(var i=0;i<newList.length;i++)
		{
			var template=$U.processTemplate(sItemTemplate,newList[i]);
			childstring+=template;
		}


		elList.innerHTML=childstring;


	},

	oCallBacks: {
		oDeleteEntityCallBack: {
		success: function(o) {
		$D.getElementsByClassName("slist-item list-item-type item selected",null,this.elBase,function(elTarget) {
			elTarget.parentNode.removeChild(elTarget);
		});
		this._widgets.geoFencingDetails.clearMap();
		// $D.getElementsByClassName("info", null, this.elBase)[0].innerHTML = "<span class='title'>Success:</span> The selected vehicles were successfully deleted.";
	},
	failure: function(o) {
		//  $D.getElementsByClassName("info", null, this.elBase)[0].innerHTML = "<span class='title'>Failure:</span> Deleteion of the selected vehicles failed.";
	}
	},

	geofencingAddRegion: {
		success: function(o){
		window.location.reload(true);
	},
	failure: function(o){
		/* Do nothing */
	}
	},
	geofencingEditRegion: {
		success: function(o){

	},
	failure: function(o){
		/* Do nothing */
	}
	},
	oAddEntity: {
		success: function(o) {

		var regionName = null;

		var oResponse = JSON.parse(o.responseText);

		var regionid = oResponse.demomanage.geofencing.regions.geofencingregion1.id ;
		this.regionIdinAddEntity = "geofencing-region-"+regionid;
		var regionName = oResponse.demomanage.geofencing.regions.geofencingregion1.name;
		this._widgets.geoFencingSideBar.addToList({
			id: this.regionIdinAddEntity,
			name: regionName
		});

		$U.Connect.asyncRequest('GET', "@APP_CONTEXT@/view/controlpanel/?markup=DemoManage&debug=true&module=/blocks/json"+
				"&data=view&subpage=geofencing&regionID="+this.regionIdinAddEntity, this.oCallBacks.geofencingAddRegion,null);
		// $D.getElementsByClassName("info", null, this.elBase)[0].innerHTML = "<span class='title'>Success:</span> The selected vehicles were successfully deleted.";
	},
	failure: function(o) {
		//  $D.getElementsByClassName("info", null, this.elBase)[0].innerHTML = "<span class='title'>Failure:</span> Deleteion of the selected vehicles failed.";
	}
	},

	/*Defining Callbacks*/
	/**
	 * The object that is passed as the callback arg of  YAHOO.util.Connect.asyncRequest created when a new region is created
	 */
//	geofencingCreation: {
//	success: function(o){

//	$U.Connect.asyncRequest('GET', "@APP_CONTEXT@/view/controlpanel/?markup=DemoManage&debug=true&module=/blocks/json"+
//	"&data=view&subpage=geofencing&regionName="+this.regionName,this.oCallBacks.oAddEntity,null);

//	var returnval =this._widgets.geoFencingDetails.addMarkers(this.regionShape);
//	//console.debug("return val :"+returnval);


//	},

	geofencingCreation: {
		success: function(o){
		var responseText = o.responseText;

		allMarkers = this._widgets.geoFencingDetails.addMarkers(this.regionShape);

		var arrayOfLatLngs = [];
		for(var j=0; j<allMarkers.length; j++){
			arrayOfLatLngs[j]=allMarkers[j].getPosition();
		}
		
		if(this.regionShape != "custom"){
			var xpoints =new google.maps.LatLng(0, 0)
			for(var j=allMarkers.length;j<(allMarkers.length+4);j++){
				var lat=xpoints.lat()+(j+1);
				var lng=xpoints.lng()+(j+1);
		var latlng= new google.maps.LatLng(lat,lng);
				arrayOfLatLngs[j]=latlng;
		}
			
			$U.Connect.asyncRequest('GET', "@APP_CONTEXT@/form/geofencing/?command_type=geo_fencing_add&arrayOfLatLngs="
					+arrayOfLatLngs+"&regionName="+this.regionName, this.oCallBacks.geofencingAddRegion,null);   
		}
		$U.Connect.asyncRequest('GET', "@APP_CONTEXT@/view/controlpanel/?markup=DemoManage&debug=true&module=/blocks/json"+
				"&data=view&subpage=geofencing&regionName="+this.regionName,this.oCallBacks.oAddEntity,null);
	},
	failure: function(o){
		/*Do nothing*/
	}
	},


	saveRegion: {
		success: function(o) {
		window.location.reload(true);
	},
	failure: function(o) {
	}
	},

	deleteRegion: {
		success: function(o) {
	},
	failure: function(o) {
	}
	},

	displayRegion: {
		success: function(o) {
		var oResponse = JSON.parse(o.responseText);
		var geofenceid = this.regionId;
		var regions =  oResponse.demomanage.geofencing.regions ;
		dataArray = $U.Arrays.mapToArray(regions);
		var deepdata=$U.Arrays.mapToArray(dataArray[0]);
		var name=$U.MapToParams(deepdata);
		var shape = deepdata[2];
		var points=null;
		for (var i=0;i<name.length;i++){
			points=name.split("=");
		}
		var geoPoints = [];
		geoPoints = points[4].split(",");
		this._widgets.geoFencingDetails.clearMap();
		this._widgets.geoFencingDetails.updateRegionDetails(shape,geoPoints,geofenceid);
	},
	failure: function(o) {
	}
	},

	editRegion : {
		success: function(o) {

		var oResponse = JSON.parse(o.responseText);
		var elList;
		var sTemplate;
		if (!oResponse.demomanage.geofencing.Error) {
			/*Reloading the Assigned List*/
			elList = $D.getElementsByClassName(this.oConfiguration.assignedListClass, null, this.elBase)[0];
			sTemplate = $D.getElementsByClassName("template-assigned-list-item", null, this.elBase)[0].innerHTML;
			this.reloadList(elList, sTemplate, oResponse.demomanage.geofencing.vehicle.assigned, function(el1, el2) {
				if (el1.name == el2.name)
					return 0;
				else if (el1.name > el2.name)
					return 1;
				else
					return -1;
			});
			/*Reloading the vacant list*/
			elList = $D.getElementsByClassName(this.oConfiguration.vacantListClass, null, this.elBase)[0];
			sTemplate = $D.getElementsByClassName("template-vacant-list-item", null, this.elBase)[0].innerHTML;
			this.reloadList(elList, sTemplate, oResponse.demomanage.geofencing.vehicle.vacant, function(el1, el2) {
				if (el1.name == el2.name)
					return 0;
				else if (el1.name > el2.name)
					return 1;
				else
					return -1;
			});
			/*Making the lists visible*/
			this.showAssignmentControls();
			this.countAssignmentLists();
		}
		else if (oResponse.demomanage.geofencing.Error.name == "ResourceNotFoundError") {
		}
		/*
		 * Preventing possible memory leaks
		 */
		elList = sTemplate = null;
	},
	failure: function(o) {
	}
	},

	},

	deleteEntity: function(args) {
		this.oCallBacks.oDeleteEntityCallBack.scope = this;
		if(this.regionIdinAddEntity != null){

			$U.Connect.asyncRequest('GET', "@APP_CONTEXT@/form/geofencing/?command_type=geo_fencing_delete&regionID=" + this.regionIdinAddEntity, this.oCallBacks.oDeleteEntityCallBack, null);

		}
		else{
			$U.Connect.asyncRequest('GET', "@APP_CONTEXT@/form/geofencing/?command_type=geo_fencing_delete&regionID=" + this.regionId, this.oCallBacks.oDeleteEntityCallBack, null);
		}
	}, 
	/**
	 * The handler function that is fired when the selected item in the SideBar is changed
	 * @param {Object} args
	 */
	onGeoFencingSelected: function(args){
		 //console.debug('In onGeoFencingSelected');
		this.oCallBacks.displayRegion.scope = this;
		this.regionIdinAddEntity = null;
//		this.displayGeoFencingDetails($D.getAttribute(args.newValue, "item"));
		this.regionId=$D.getAttribute(args.newValue, "item");
		$U.Connect.asyncRequest('GET', "@APP_CONTEXT@/view/controlpanel/?markup=DemoManage&debug=true&module=/blocks/json"+
				"&data=view&subpage=geofencing&regionID="+this.regionId, this.oCallBacks.displayRegion,null);
	},

	/**
	 * Forces the widget to display the details of the region identified by the regionID
	 * @param {Object} regionID A unique identifier for the region
	 */
	displayGeoFencingDetails: function(regionID) {
		 //console.debug('In displayGeoFencingDetails');
		this.oCallBacks.displayRegion.scope = this;
		$U.Connect.asyncRequest("GET","@APP_CONTEXT@/view/controlpanel/?markup=GeoFencingDetails&body=true&regionID=" + regionID, this.oCallBacks.displayRegion, null);
	},


	/**
        /**
	 * Inserts a child into a sorted list
	 * @param {Object} elList
	 * @param {Object} elChild
	 */
	insertIntoList: function(elList, elChild) {

		var oSelf = this;


		var aChildren = $D.getElementsByClassName("list-item", null, elList, function(elTarget) {

			if (oSelf.listComparator(elTarget, elChild) <= 0) {
				$D.insertBefore(elChild, elTarget);

				return;
			}
		});

		if(aChildren.length == 0){
			elList.appendChild(elChild);
		}
	},
	/**
	 * Displays the count of the vacant list
	 */
	countVacantList: function() {
		var elContainer = $D.getElementsByClassName("vacant-list-cnt", null, this.elBase)[0];
		var elList = $D.getElementsByClassName("vacant-list", null, elContainer)[0];
		var elCounter = $D.getElementsByClassName("list-count", null, elContainer)[0];
		if (elCounter) {
			elCounter.innerHTML = $D.getElementsByClassName("list-item", null, elList).length;
		}
		/**
		 * Preventing possible memory leaks
		 */
		elContainer = elList = elCounter = null;
	},
	/**
	 * Displays the count of the assigned list
	 */
	countAssignedList: function() {
		var elContainer = $D.getElementsByClassName("assigned-list-cnt", null, this.elBase)[0];
		var elList = $D.getElementsByClassName("assigned-list", null, elContainer)[0];
		var elCounter = $D.getElementsByClassName("list-count", null, elContainer)[0];
		if (elCounter) {
			elCounter.innerHTML = $D.getElementsByClassName("list-item", null, elList).length;
		}
		/**
		 * Preventing possible memory leaks
		 */
		elContainer = elList = elCounter = null;
	},
	/**
	 * Convenience method to directly count lists associated with assignment control
	 */
	countAssignmentLists: function() {
		this.countVacantList();
		this.countAssignedList();
	},

	onMove: function(oEvent, params) {
		/*Moving the selected items to the vacant list*/
		var oSelf = this;
		var elAssignedList = $D.getElementsByClassName("assigned-list", null, this.elBase)[0];

		var elVacantList = $D.getElementsByClassName("vacant-list", null, this.elBase)[0];


		$D.getElementsByClassName("selected", null, this.elBase, function(elTarget) {
			var elSourceList;
			var elTargetList;
			if ($D.isAncestor(elAssignedList, elTarget) && oEvent.data.button == "remove") {
				elSourceList = elAssignedList;
				elTargetList = elVacantList;

			}
			else if ($D.isAncestor(elVacantList, elTarget) && oEvent.data.button == "assign") {
				elSourceList = elVacantList;
				elTargetList = elAssignedList;

			}
			else {
				return;
			}

			/*Marking the element as moved*/
			if (!$D.hasClass(elTarget, "moved")) {
				/*Being moved for the first time*/
				$D.addClass(elTarget, "moved");
			}
			else {
				/*Being moved back*/
				$D.removeClass(elTarget, "moved");
			}
			/*Removing the element from this assigned list and adding it to the vacant list*/
			oSelf.insertIntoList(elTargetList, elSourceList.removeChild(elTarget));
			$D.removeClass(elTarget, "selected");
			/*
			 * Preventing possible memory leaks
			 */
			elSourceList = elTargetList = null;
		}, null, this);
		var added = $D.getElementsByClassName("vac-list-item list-item slist-item moved",null,this.elBase);
		var addedVehs = [];
		for(var i=0; i<added.length; i++){
			addedVehs[i] = added[i].innerHTML;
		}
		var removed = $D.getElementsByClassName("assng-list-item list-item slist-item moved",null,this.elBase);
		var removedVehs = [];
		for(var i=0; i<removed.length; i++){
			removedVehs[i] = removed[i].innerHTML;
		}
		$U.Connect.asyncRequest('GET', "@APP_CONTEXT@/form/geofencing/?command_type=geo_fencing_edit&regionId="
				+this.regionId+"&assignedVehicles="+addedVehs+"&vacantVehicles="+removedVehs, this.oCallBacks.geofencingEditRegion,null);
		/*Calculating the list counts*/
		this.countAssignmentLists();
		/*Setting appropriate state*/

		/*
		 * Preventing possible memory leaks
		 */
		elAssignedList = elVacantList = oSelf = null;
	},

	/*Defining Utilities*/
	addListeners: function(){
		this._widgets.toolbar.subscribe($W.GeoFencingToolBar.EVT_ON_ADD_REGION, this.showGeoFencingCreationPopup, this, true);
		this._widgets.toolbar.subscribe($W.GeoFencingToolBar.EVT_ON_DELETE_REGION,this.showGeoFencingDeleteRegionPopUp,this,true);
		this._widgets.toolbar.subscribe($W.GeoFencingToolBar.EVT_ON_SAVE_REGION,this.geofencingSaveRegionSubmitted,this,true);
		this._widgets.toolbar.subscribe($W.GeoFencingToolBar.EVT_ON_EDIT_REGION, this.showGeoFencingEditRegionPopup, this, true);
		this._widgets.toolbar.subscribe($W.GeoFencingToolBar.EVT_ON_DRAW_REGION,this.showHowToDrawPopUp,this,true);

		this._widgets.howtodrawPopUp.subscribe($W.DialogPopUp.EVT_ON_SUBMIT,this.howtodrawSubmitted,this,true);           

		this._widgets.geofencingDeleteRegionPopUp.subscribe($W.DialogPopUp.EVT_ON_SUBMIT,this.geofencingDeleteRegionSubmitted,this,true);
		this._widgets.geofencingEditRegionPopUp.subscribe($W.EditVehiclePopUp.EVT_ON_SUBMIT, this.geofencingEditRegionSubmitted, this, true);
		this._widgets.geofencingCreationPopUp.subscribe($W.DialogPopUp.EVT_ON_SUBMIT, this.geofencingSubmitted, this, true);
		this._widgets.geoFencingSideBar.subscribe($W.GeoFencingSideBar.EVT_SELECTED_ITEM_CHANGE, this.onGeoFencingSelected, this, true);
		/*Adding listeners to the buttons*/
		var oListBehaviorLayer = new $U.DOMEventManager($D.getElementsByClassName("assignment-control-list", null, this.elBase), "click");
		oListBehaviorLayer.addBehavior("list-item", function(event, args) {
			if (!$D.hasClass(args[1].target, "selected")) {
				$D.addClass(args[1].target, "selected");

			}
			else {
				$D.removeClass(args[1].target, "selected");
			}
		}, null, this);

		$W.Buttons.addDefaultHandler($D.getElementsByClassName("remove-button", null, this.elBase)[0], this.onMove, {
			button: "remove"
		}, this);
		$W.Buttons.addDefaultHandler($D.getElementsByClassName("add-button", null, this.elBase)[0], this.onMove, {
			button: "assign"
		}, this);

		/*
		 * Preventing possible memory leaks
		 */
		oListBehaviorLayer = null;
		/*Handling the behaviour of the information dialog*/
		var body = $D.getElementsByClassName("bd", null, this.elBase)[0];
		$W.Buttons.addDefaultHandler($D.getElementsByClassName("button-done", null, body)[0],this.doneButton, {
			button: "done"
		},this);
		/*Preventing memory leaks*/
		body = null;
	},
	doneButton:function(event, args, me){
		var bd = $D.getElementsByClassName("bd", null, this.elBase)[0];
		var simpleDialog = $D.getElementsByClassName("simpledialog", null,bd )[0];
		$D.addClass(simpleDialog, "disabled");
	},


	listComparator: function(el1, el2) {

		var result = -1;
		if ($L.isObject(el1) && $L.isObject(el2)) {
			var value1 = $L.trim(el1.innerHTML);
			var value2 = $L.trim(el2.innerHTML);



			if (value1 == value2) {
				result = 0;
			}
			else if (value1 != value2) {
				result = -1;
			}
			else {
				result = -1;
			}
		}
		else if ($L.isObject(el1)) {
			result = 1;
		}
		else if ($L.isObject(el2)) {
			result = -1;
		}
		else if(value1 !=null && $L.isObject(el1)){
			result = -1;
		}
		else if(value2 !=null && $L.isObject(el2)){

			result=-1;
		}
		else{
			result=0;
		}

		return result;
	}


	});

	/**
	 * The Region Creation Element
	 *
	 * @author N.Balaji
	 * CAUTION:
	 *      THE YUI PANEL USES A FUNCTION NAMED init.THIS FUNCTION NAME SHOULD NOT BE USED
	 *      BY ANY Object THAT extends THIS Object.
	 * @param {Object} el: The base-div containing content
	 */
	$W.GeoFencing.GeoFencingCreationPopUp = function(el){
		/*Initializing*/
		this.initCreateGeoFencing(el);
	};
	$L.extend($W.GeoFencing.GeoFencingCreationPopUp, $W.DialogPopUp);
	$L.augmentObject($W.GeoFencing.GeoFencingCreationPopUp.prototype, {
		/*Declaring the datamembers*/
		elDisplayedContent: null,
		/**
		 * The initalization Function
		 */
		initCreateGeoFencing: function(el){
		this.elDisplayedContent = el;
		$W.GeoFencing.GeoFencingCreationPopUp.superclass.constructor.call(this, this.elDisplayedContent, {
			fixedcenter: true,
			width: "420px",
			height: "auto"
		});
	},

	}),


	$W.GeoFencing.HowToDrawPopUp = function(el){

		/*Initializing*/
		this.initHowToDraw(el);
	};
	$L.extend($W.GeoFencing.HowToDrawPopUp, $W.DialogPopUp);
	$L.augmentObject($W.GeoFencing.HowToDrawPopUp.prototype, {
		/*Declaring the datamembers*/
		elDisplayedContent: null,
		/**
		 * The initalization Function
		 */
		initHowToDraw: function(el){
		this.elDisplayedContent = el;
		$W.GeoFencing.HowToDrawPopUp.superclass.constructor.call(this, this.elDisplayedContent, {
			fixedcenter: true,
			width: "500px",
			height: "250px"
		});
	},


	}),
	$W.GeoFencing.GeoFencingEditRegionPopUp = function(el){

		/*Initializing*/
		this.initEditRegion(el);

	};
	$L.extend($W.GeoFencing.GeoFencingEditRegionPopUp, $W.EditVehiclePopUp);
	$L.augmentObject($W.GeoFencing.GeoFencingEditRegionPopUp.prototype, {
		/*Declaring the datamembers*/
		elDisplayedContent: null,
		/**
		 * The initalization Function
		 */
		initEditRegion: function(el){
		this.elDisplayedContent = el;
		$W.GeoFencing.GeoFencingEditRegionPopUp.superclass.constructor.call(this, this.elDisplayedContent, {
			fixedcenter: true,
			width: "520px",
			height: "520px"
		});

	},
	getDataFromServer: function(){
	}

	}),

	$W.GeoFencing.GeoFencingDeleteRegionPopUp = function(el){

		/*Initializing*/
		this.initDeleteRegion(el);
	};
	$L.extend($W.GeoFencing.GeoFencingDeleteRegionPopUp, $W.DialogPopUp);
	$L.augmentObject($W.GeoFencing.GeoFencingDeleteRegionPopUp.prototype, {
		/*Declaring the datamembers*/
		elDisplayedContent: null,
		/**
		 * The initalization Function
		 */
		initDeleteRegion: function(el){
		this.elDisplayedContent = el;
		$W.GeoFencing.GeoFencingDeleteRegionPopUp.superclass.constructor.call(this, this.elDisplayedContent, {
			fixedcenter: true,
			width: "320px",
			height: "auto"
		});
	},
	getDataFromServer: function(){
	}

	});

	$W.GeoFencing.GeoFencingSaveRegionPopUp = function(el){

		/*Initializing*/
		this.initSaveRegion(el);
	};
	$L.extend($W.GeoFencing.GeoFencingSaveRegionPopUp, $W.DialogPopUp);
	$L.augmentObject($W.GeoFencing.GeoFencingSaveRegionPopUp.prototype, {
		/*Declaring the datamembers*/
		elDisplayedContent: null,
		/**
		 * The initalization Function
		 */
		initSaveRegion: function(el){
		this.elDisplayedContent = el;
		$W.GeoFencing.GeoFencingSaveRegionPopUp.superclass.constructor.call(this, this.elDisplayedContent, {
			fixedcenter: true,
			width: "320px",
			height: "auto"
		});
	},
	getDataFromServer: function(){
	}

	});

})();
