(function(){
	var $Y = YAHOO;
	var $B = $Y.Bubbling;
	var $L = $Y.lang;
	var $YU = $Y.util;
	var $E = $Y.util.Event;
	var $D = $Y.util.Dom;
	var $YW = $Y.widget;
	var $U = getPackageForName("com.i10n.fleet.Utils");
	var $W = getPackageForName("com.i10n.fleet.widget.ui");
	var $WU = getPackageForName("com.i10n.fleet.widget.util");
	var sUserID;
	var globalScope;
	var addedVehs = [];
	var removedVehs = [];
	/**
	 * Alert Settings Sub Nav Page
	 *
	 * @author aravind
	 */
	$W.MobileAlertSettings = function(el, params){
		this.baseElement = el;
		this._oDataSource = null;
		this.initParams = params;
		this._widgets = {};
		this.init(el, params);
	};
	$L.augmentProto($W.MobileAlertSettings, $YU.EventProvider);
	$L.augmentObject($W.MobileAlertSettings.prototype, {
		/**
		 * Initialization function
		 */
		init: function(el, params){
		globalScope = this;
		var oDataSource = this.generateDataSource();
		this._oDataSource = oDataSource;
		var toolBarEl = $D.getElementsByClassName("mobilesearchtoolbar", null, el)[0];
		var toolBar = new $W.MobileSearchToolBar(toolBarEl, {
			title: 'Name Search',
			searchkey: 'name',
			datasource: oDataSource
		});
		this._widgets.toolbar = toolBar;
		var sidePaneEl = $D.getElementsByClassName("mobilealertsettingslist", null, el)[0];
		var sidepane = new $W.MobileAlertSettingsList(sidePaneEl, {
			datasource: oDataSource
		});
		this._widgets.alertsidepane = sidepane;
		var reportEl = $D.getElementsByClassName("mobilealertsettingsreport", null, el)[0];
		var reports = new $W.MobileAlertSettingsReport(reportEl, {
			datasource: oDataSource
		});
		this._widgets.report = reports;
		var flipContainer = $D.getElementsByClassName("flip-container", null, el)[0];
		/*The draw region popup is the popup that is displayed when the link is clicked*/
		var mobilealertsettingsAssignVehiclePopUp = new $W.MobileAlertSettings.MobileAlertSettingsAssignVehiclePopUp($D.getElementsByClassName("assign-vehicle", null, flipContainer)[0]);
		this._widgets.mobilealertsettingsAssignVehiclePopUp = mobilealertsettingsAssignVehiclePopUp;
		if (!params) {
			params = {};
		}
		if (!$L.isString(params.nameListClass)) {
			params.nameListClass = "name-list";
		}
		if (!$L.isString(params.vacantListClass)) {
			params.vacantListClass = "mobilevacant-list";
		}
		if (!$L.isString(params.assignedListClass)) {
			params.assignedListClass = "mobileassigned-list";
		}
		if (!$L.isString(params.assignmentControlClass)) {
			params.assignmentControlClass = "assignment-control";
		}

		this.oMobileConfiguration= params;
		this.addListeners();
		this.countUserList();
	},

	showMobileAlertSettingsAssignVehiclePopUp: function(){

		this._widgets.mobilealertsettingsAssignVehiclePopUp.render();
		this._widgets.mobilealertsettingsAssignVehiclePopUp.show();
	},

	mobilealertsettingsAssignVehicleSubmitted: function(args){
		$U.Connect.asyncRequest('GET', "/fleet/form/mobilealertsettings/?command_type=mobilealertsettings_assign&userId="
				+this.sUserID+"&assignedVehicles="+addedVehs+"&vacantVehicles="+removedVehs, this.oCallBacks.mobilealertsettingAssignVehicle,null);
		for ( var i = 0; i < addedVehs.length; i++){
			addedVehs.splice(i);
		}
		for ( var j = 0; j < addedVehs.length; j++){
			removedVehs.splice(j);
		}
		this._widgets.mobilealertsettingsAssignVehiclePopUp.hide();
	},        
	oCallBacks: {
		mobilealertsettingAssignVehicle: {
		success: function(o){

	},
	failure: function(o){
		/* Do nothing */
	}
	}
	},
	oVehicleDataReceiptCallBack: {
		success: function(o) {

		var oResponse = JSON.parse(o.responseText);
		var elList;
		var sTemplate;
		if (!oResponse.demomanage.mobilealertsettings.Error) {
			/*Reloading the Assigned List*/
			elList = $D.getElementsByClassName(globalScope.oMobileConfiguration.assignedListClass, null, this.elBase)[0];
			sTemplate = $D.getElementsByClassName("template-mobileassigned-list-item", null, this.elBase)[0].innerHTML;
			globalScope.reloadList(elList, sTemplate, oResponse.demomanage.mobilealertsettings.vehicle.assigned, function(el1, el2) {
				if (el1.name == el2.name) 
					return 0;
				else if (el1.name > el2.name) 
					return 1;
				else 
					return -1;
			});
			/*Reloading the vacant list*/
			elList = $D.getElementsByClassName(globalScope.oMobileConfiguration.vacantListClass, null, this.elBase)[0];
			sTemplate = $D.getElementsByClassName("template-mobilevacant-list-item", null, this.elBase)[0].innerHTML;
			globalScope.reloadList(elList, sTemplate, oResponse.demomanage.mobilealertsettings.vehicle.vacant, function(el1, el2) {
				if (el1.name == el2.name) 
					return 0;
				else if (el1.name > el2.name) 
					return 1;
				else 
					return -1;
			});
			/*Making the lists visible*/
			globalScope.showAssignmentControls();
			globalScope.countAssignmentLists();
		}
		else if (oResponse.demomanage.mobilealertsettings.Error.name == "ResourceNotFoundError") {

		}
		/*
		 * Preventing possible memory leaks
		 */
		elList = sTemplate = null;
	},
	failure: function(o) {
	}
	},
	/**
	 * reloads a list
	 * @param {Object} elList
	 * @param {Object} sItemTemplate
	 * @param {Object} oData
	 * @param {Function} fComparator specifies the order in which the list should be sorted
	 */
	reloadList: function(elList, sItemTemplate, oData, fComparator) {
		$D.getElementsByClassName("mobile-list-item slist-item", null, elList, function(elTarget) {
			elTarget.parentNode.removeChild(elTarget);
		});
		/*Sorting the data*/
		var newList = $U.Arrays.mapToArray(oData);
		newList = newList.sort(fComparator);
		//            $U.Element.DivElement.appendTemplatizedChildren(elList, sItemTemplate, newList);
		var childstring="";
		for(var i=0;i<newList.length;i++)
		{
			var template=$U.processTemplate(sItemTemplate,newList[i]);
			childstring+=template;
		}


		elList.innerHTML=childstring;


	},

	/**
	 * Shows the assignement controls
	 */
	showAssignmentControls: function() {
		$D.getElementsByClassName(globalScope.oMobileConfiguration.assignmentControlClass, null, this.elBase, function(elTarget) {
			if ($D.hasClass(elTarget, "disabled")) {
				$D.removeClass(elTarget, "disabled");
			}
		});
	},
	/**
	 * Convenience method to directly count lists associated with assignment control
	 */
	countAssignmentLists: function() {
		this.countVacantList();
		this.countAssignedList();
	},
	/**
	 * Displays the count of the vacant list
	 */
	countVacantList: function() {
		var elContainer = $D.getElementsByClassName("mobilevacant-list-cnt", null, this.elBase)[0];
		var elList = $D.getElementsByClassName("mobilevacant-list", null, elContainer)[0];
		var elCounter = $D.getElementsByClassName("list-count", null, elContainer)[0];
		if (elCounter) {
			elCounter.innerHTML = $D.getElementsByClassName("mobile-list-item", null, elList).length;
		}
		/**
		 * Preventing possible memory leaks
		 */
		elContainer = elList = elCounter = null;
	},
	/**
	 * Displays the count of the user list
	 */
	countUserList: function() {
		var elContainer = $D.getElementsByClassName("name-list-cnt", null, this.elBase)[0];
		var elList = $D.getElementsByClassName("name-list", null, elContainer)[0];
		var elCounter = $D.getElementsByClassName("list-count", null, elContainer)[0];
		if (elCounter) {
			elCounter.innerHTML = $D.getElementsByClassName("user-list-item", null, elList).length;
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
		var elContainer = $D.getElementsByClassName("mobileassigned-list-cnt", null, this.elBase)[0];
		var elList = $D.getElementsByClassName("mobileassigned-list", null, elContainer)[0];
		var elCounter = $D.getElementsByClassName("list-count", null, elContainer)[0];
		if (elCounter) {
			elCounter.innerHTML = $D.getElementsByClassName("mobile-list-item", null, elList).length;
		}
		/**
		 * Preventing possible memory leaks
		 */
		elContainer = elList = elCounter = null;
	},
	onMove: function(oEvent, params) {
		var oSelf = this;
		var elAssignedList = $D.getElementsByClassName("mobileassigned-list", null, this.elBase)[0];

		var elVacantList = $D.getElementsByClassName("mobilevacant-list", null, this.elBase)[0];


		$D.getElementsByClassName("mobile-list-item slist-item selected", null, this.elBase, function(elTarget) {
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
		var added = $D.getElementsByClassName("mobilevac-list-item mobile-list-item slist-item moved",null,this.elBase);
		for(var i=0; i<added.length; i++){
			addedVehs[i] = added[i].innerHTML;
		}
		var removed = $D.getElementsByClassName("mobileassng-list-item mobile-list-item slist-item moved",null,this.elBase);
		for(var i=0; i<removed.length; i++){
			removedVehs[i] = removed[i].innerHTML;
		}

		/*Calculating the list counts*/
		this.countAssignmentLists();
		/*Setting appropriate state*/

		/*
		 * Preventing possible memory leaks
		 */
		elAssignedList = elVacantList = oSelf = null;
	},
	onUserSelected: function(oEvent, oArgs) {
		/*Correcting the scope of the callback*/

		var userID = $D.getAttribute(oArgs[1].target, "value");
		this.sUserID = userID;
		$U.Connect.asyncRequest("GET", "/fleet/view/controlpanel/?markup=DemoManage&debug=true&module=/blocks/json"+
				"&data=view&subpage=mobilealertsettings&dataView=assignment&userID=" + userID, this.oVehicleDataReceiptCallBack, null);
		/*
		 * Preventing possible memory leaks
		 */
		userID = null;

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
	},

	/**
        /**
	 * Inserts a child into a sorted list
	 * @param {Object} elList
	 * @param {Object} elChild
	 */
	insertIntoList: function(elList, elChild) {
		var oSelf = this;

		var aChildren = $D.getElementsByClassName("mobile-list-item", null, elList, function(elTarget) {

			if (globalScope.listComparator(elTarget, elChild) <= 0) {
				$D.insertBefore(elChild, elTarget);
				return;
			}
		});

		if(aChildren.length == 0){
			elList.appendChild(elChild);
		}
	},
	addListeners: function(){

		this._widgets.toolbar.subscribe($W.MobileSearchToolBar.EVT_ON_ASSIGN_VEHICLE, this.showMobileAlertSettingsAssignVehiclePopUp, this, true);
		this._widgets.mobilealertsettingsAssignVehiclePopUp.subscribe($W.MobileVehiclePopUp.EVT_ON_SUBMIT, this.mobilealertsettingsAssignVehicleSubmitted, this, true);
		var oListBehaviorLayer = new $U.DOMEventManager($D.getElementsByClassName("name-list", null, this.elBase), "click");
		oListBehaviorLayer.addBehavior("user-list-item", function(oEvent, oArgs) {
			var elList = $D.getAncestorByClassName(oArgs[1].target, "name-list");
			$D.getElementsByClassName("user-list-item", null, elList, function(elTarget) {
				if (elTarget === oArgs[1].target) {
					if (!$D.hasClass(elTarget, "selected")) {
						$D.addClass(elTarget, "selected");
					}
				}
				else {
					if ($D.hasClass(elTarget, "selected")) {
						$D.removeClass(elTarget, "selected");
					}
				}
			});
		}, null, this);
		oListBehaviorLayer.addBehavior("user-list-item", this.onUserSelected, null, this);

		var oListBehaviorLayer1 = new $U.DOMEventManager($D.getElementsByClassName("assignment-control-list", null, this.elBase), "click");
		oListBehaviorLayer1.addBehavior("mobileassng-list-item mobile-list-item", function(event, args) {
			if (!$D.hasClass(args[1].target, "selected")) {
				$D.addClass(args[1].target, "selected");

			}
			else {
				$D.removeClass(args[1].target, "selected");
			}

		}, null, this);
		oListBehaviorLayer1.addBehavior("mobilevac-list-item mobile-list-item", function(event, args) {
			if (!$D.hasClass(args[1].target, "selected")) {
				$D.addClass(args[1].target, "selected");

			}
			else {
				$D.removeClass(args[1].target, "selected");
			}

		}, null, this);

		$W.Buttons.addDefaultHandler($D.getElementsByClassName("mobileremove-button", null, this.elBase)[0], this.onMove, {
			button: "remove"
		}, this);
		$W.Buttons.addDefaultHandler($D.getElementsByClassName("mobileadd-button", null, this.elBase)[0], this.onMove, {
			button: "assign"
		}, this);
	},
	generateDataSource: function(){
		if (_publish.report && _publish.report.mobilealertsettings) {

			var alertreport = _publish.report.mobilealertsettings;
			var count = 0;
			var data = {};
			for (var alertreportdata in alertreport) {
				if (alertreportdata) {
					var item = $U.cloneObject(alertreport[alertreportdata]);
					item.id = "" + count + 1;
					data[item.id] = item;
					count++;
				}
			}
			var dataSource = new $W.UpdatableReport.UpdatableDataSource(data);
			dataSource.responseType = $YU.DataSource.TYPE_JSARRAY;
			dataSource.responseSchema = {
					fields: ["id", "name", "mobilenumber", "overspeeding", "geofencing", "chargerdisconnected"]
			};
			return dataSource;
		}
	}
	});
	$W.MobileAlertSettings.MobileAlertSettingsAssignVehiclePopUp = function(el){

		/*Initializing*/
		this.initAssignVehicle(el);

	};
	$L.extend($W.MobileAlertSettings.MobileAlertSettingsAssignVehiclePopUp, $W.MobileVehiclePopUp);
	$L.augmentObject($W.MobileAlertSettings.MobileAlertSettingsAssignVehiclePopUp.prototype, {
		/*Declaring the datamembers*/
		elDisplayedContent: null,
		/**
		 * The initalization Function
		 */
		initAssignVehicle: function(el){

		this.elDisplayedContent = el;
		$W.MobileAlertSettings.MobileAlertSettingsAssignVehiclePopUp.superclass.constructor.call(this, this.elDisplayedContent, {
			fixedcenter: true,
			width: "740px",
			height: "520px"
		});

	},
	getDataFromServer: function(){
	}

	});
})();
