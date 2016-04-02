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
	 * ReportSettings sub nav page
	 *
	 *
	 * @author subramaniam
	 */
	$W.ReportSettings = function(el, oArgs){
		this._widgets = {};
		this.elBase = el;
		this.init(el, oArgs);
	};

	$L.augmentProto($W.ReportSettings,$YU.EventProvider);
	$L.augmentObject($W.ReportSettings.prototype, {
		/**
		 * Initializes the widget
		 */
		init: function(el, oArgs){
		globalScope = this;
		this._oDataSource = this._generateDataSource();
			var oToolBar = new $W.SearchToolBar($D.getElementsByClassName("searchtoolbar", null, el)[0], {
				title: "Name Search",
				searchkey: "name",
				datasource: this._oDataSource
			});

			this._widgets.toolbar = oToolBar;
			var oListPane = new $W.ReportSettingsList($D.getElementsByClassName("reportsettingslist", null, el)[0], {
				datasource: this._oDataSource
			});
			this._widgets.sidepane = oListPane;
			var oReport = new $W.ReportSettingsReport($D.getElementsByClassName("reportsettingsreport", null, el)[0], {
				datasource: this._oDataSource
			});
			this._widgets.report = oReport;
			var flipContainer = $D.getElementsByClassName("flip-container", null, el)[0];
			/*The draw region popup is the popup that is displayed when the link is clicked*/
			var reportsettingsAssignVehiclePopUp = new $W.ReportSettings.ReportSettingsAssignVehiclePopUp($D.getElementsByClassName("assign-vehicle", null, flipContainer)[0]);
			this._widgets.reportsettingsAssignVehiclePopUp = reportsettingsAssignVehiclePopUp;
			if (!oArgs) {
				oArgs = {};
			}
			if (!$L.isString(oArgs.nameListClass)) {
				oArgs.nameListClass = "name-list";
			}
			if (!$L.isString(oArgs.vacantListClass)) {
				oArgs.vacantListClass = "reportvacant-list";
			}
			if (!$L.isString(oArgs.assignedListClass)) {
				oArgs.assignedListClass = "reportassigned-list";
			}
			if (!$L.isString(oArgs.assignmentControlClass)) {
				oArgs.assignmentControlClass = "assignment-control";
			}

			this.oConfiguration= oArgs;
			this.addListeners();
			this.countUserList();	
	},

	oCallBacks: {
		assignVehicle:{
		success: function(o){
	},
	failure: function(o){
	}
	}
	},

	showReportSettingsAssignVehiclePopUp: function(){

		this._widgets.reportsettingsAssignVehiclePopUp.render();
		this._widgets.reportsettingsAssignVehiclePopUp.show();
	},
	reportsettingsAssignVehicleSubmitted: function(args){ 
		var added = $D.getElementsByClassName("reportvac-list-item report-list-item slist-item moved",null,this.elBase);

		for(var i=0; i<added.length; i++){
			addedVehs[i] = added[i].innerHTML;
		}
		var removed = $D.getElementsByClassName("reportassng-list-item report-list-item slist-item moved",null,this.elBase);

		for(var i=0; i<removed.length; i++){
			removedVehs[i] = removed[i].innerHTML;
		}
		$U.Connect.asyncRequest('GET', "/fleet/form/reportsettings/?command_type=reportsettings_assign&userId="
				+this.sUserID+"&assignedVehicles="+addedVehs+"&vacantVehicles="+removedVehs, this.oCallBacks.assignVehicle,null);

		for ( var i = 0; i < addedVehs.length; i++){
			addedVehs.splice(i);
		}
		for ( var j = 0; j < addedVehs.length; j++){
			removedVehs.splice(j);
		}
		this._widgets.reportsettingsAssignVehiclePopUp.hide();           
	},
	oVehicleDataReceiptCallBack: {
		success: function(o) {

		var oResponse = JSON.parse(o.responseText);
		var elList;
		var sTemplate;
		if (!oResponse.demomanage.reportsettings.Error) {
			/*Reloading the Assigned List*/
			elList = $D.getElementsByClassName(globalScope.oConfiguration.assignedListClass, null, this.elBase)[0];
			sTemplate = $D.getElementsByClassName("template-reportassigned-list-item", null, this.elBase)[0].innerHTML;
			globalScope.reloadList(elList, sTemplate, oResponse.demomanage.reportsettings.vehicle.assigned, function(el1, el2) {
				if (el1.name == el2.name) 
					return 0;
				else if (el1.name > el2.name) 
					return 1;
				else 
					return -1;
			});
			/*Reloading the vacant list*/
			elList = $D.getElementsByClassName(globalScope.oConfiguration.vacantListClass, null, this.elBase)[0];
			sTemplate = $D.getElementsByClassName("template-reportvacant-list-item", null, this.elBase)[0].innerHTML;
			globalScope.reloadList(elList, sTemplate, oResponse.demomanage.reportsettings.vehicle.vacant, function(el1, el2) {
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
		else if (oResponse.demomanage.reportsettings.Error.name == "ResourceNotFoundError") {

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

		$D.getElementsByClassName("report-list-item slist-item", null, elList, function(elTarget) {
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

	/**
	 * Shows the assignement controls
	 */
	showAssignmentControls: function() {
		$D.getElementsByClassName(globalScope.oConfiguration.assignmentControlClass, null, this.elBase, function(elTarget) {
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
		var elContainer = $D.getElementsByClassName("reportvacant-list-cnt", null, this.elBase)[0];
		var elList = $D.getElementsByClassName("reportvacant-list", null, elContainer)[0];
		var elCounter = $D.getElementsByClassName("list-count", null, elContainer)[0];
		if (elCounter) {
			elCounter.innerHTML = $D.getElementsByClassName("report-list-item", null, elList).length;
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
		var elContainer = $D.getElementsByClassName("reportassigned-list-cnt", null, this.elBase)[0];
		var elList = $D.getElementsByClassName("reportassigned-list", null, elContainer)[0];
		var elCounter = $D.getElementsByClassName("list-count", null, elContainer)[0];
		if (elCounter) {
			elCounter.innerHTML = $D.getElementsByClassName("report-list-item", null, elList).length;
		}
		/**
		 * Preventing possible memory leaks
		 */
		elContainer = elList = elCounter = null;
	},
	onUserSelected: function(oEvent, oArgs) {
		/*Correcting the scope of the callback*/
		this.oCallBacks.assignVehicle.scope = this;
		var userID = $D.getAttribute(oArgs[1].target, "value");
		this.sUserID = userID;
		$U.Connect.asyncRequest("GET", "/fleet/view/controlpanel/?markup=DemoManage&debug=true&module=/blocks/json"+
				"&data=view&subpage=reportsettings&dataView=assignment&userID=" + userID, this.oVehicleDataReceiptCallBack, null);
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

	onMove: function(oEvent, params) {

		/*Moving the selected items to the vacant list*/
		var oSelf = this;
		var elAssignedList = $D.getElementsByClassName("reportassigned-list", null, this.elBase)[0];

		var elVacantList = $D.getElementsByClassName("reportvacant-list", null, this.elBase)[0];


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


		/*Calculating the list counts*/
		this.countAssignmentLists();
		/*Setting appropriate state*/

		/*
		 * Preventing possible memory leaks
		 */
		elAssignedList = elVacantList = oSelf = null;
	},

	/**
        /**
	 * Inserts a child into a sorted list
	 * @param {Object} elList
	 * @param {Object} elChild
	 */
	insertIntoList: function(elList, elChild) {

		var oSelf = this;

		var aChildren = $D.getElementsByClassName("report-list-item", null, elList, function(elTarget) {

			if (oSelf.listComparator(elTarget, elChild) <= 0) {
				$D.insertBefore(elChild, elTarget);

				return;
			}
		});

		if(aChildren.length == 0){
			elList.appendChild(elChild);
		}
	},
	addListeners: function(){
		this._widgets.toolbar.subscribe($W.SearchToolBar.EVT_ON_ASSIGN_VEHICLE, this.showReportSettingsAssignVehiclePopUp, this, true);
		this._widgets.reportsettingsAssignVehiclePopUp.subscribe($W.ReportVehiclePopUp.EVT_ON_SUBMIT, this.reportsettingsAssignVehicleSubmitted, this, true);
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
		oListBehaviorLayer1.addBehavior("reportassng-list-item report-list-item", function(event, args) {
			if (!$D.hasClass(args[1].target, "selected")) {
				$D.addClass(args[1].target, "selected");

			}
			else {
				$D.removeClass(args[1].target, "selected");
			}

		}, null, this);
		oListBehaviorLayer1.addBehavior("reportvac-list-item report-list-item", function(event, args) {
			if (!$D.hasClass(args[1].target, "selected")) {
				$D.addClass(args[1].target, "selected");

			}
			else {
				$D.removeClass(args[1].target, "selected");
			}

		}, null, this);

		$W.Buttons.addDefaultHandler($D.getElementsByClassName("reportremove-button", null, this.elBase)[0], this.onMove, {
			button: "remove"
		}, this);
		$W.Buttons.addDefaultHandler($D.getElementsByClassName("reportadd-button", null, this.elBase)[0], this.onMove, {
			button: "assign"
		}, this);
	},
	/**
	 * Generates the DataSource
	 * Passed as params for Autocomplete and Reports
	 */
	_generateDataSource: function(){
		if (_publish.report && _publish.report.reportsettings) {
			var oList = _publish.report.reportsettings;
			var data = {};
			var count = 0;
			for (var sID in oList) {
				var item = $U.cloneObject(oList[sID]);
				item.id = count + 1;
				data[item.id] = item;
				count++;
			}
			var oDataSource = new $W.UpdatableReport.UpdatableDataSource(data);
			oDataSource.responseType = $YU.DataSource.TYPE_JSARRAY;
			oDataSource.responseSchema = {
					fields: ["id", "name", "email", "schedule", "vehiclestatistics", "offlinevehiclereport", "vehiclestatus"]
			};
			return oDataSource;
		}else{
			return null;
		}
	}
	});
	$W.ReportSettings.ReportSettingsAssignVehiclePopUp = function(el){

		/*Initializing*/
		this.initAssignVehicle(el);

	};
	$L.extend($W.ReportSettings.ReportSettingsAssignVehiclePopUp, $W.ReportVehiclePopUp);
	$L.augmentObject($W.ReportSettings.ReportSettingsAssignVehiclePopUp.prototype, {
		/*Declaring the datamembers*/
		elDisplayedContent: null,
		/**
		 * The initalization Function
		 */
		initAssignVehicle: function(el){
		this.elDisplayedContent = el;
		$W.ReportSettings.ReportSettingsAssignVehiclePopUp.superclass.constructor.call(this, this.elDisplayedContent, {
			fixedcenter: true,
			width: "740px",
			height: "520px"
		});

	},
	getDataFromServer: function(){
	}

	});

})();