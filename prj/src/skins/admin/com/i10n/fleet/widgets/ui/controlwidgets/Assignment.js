(function() {
	var $B = YAHOO.Bubbling;
	var $L = YAHOO.lang;
	var $YU = YAHOO.util;
	var $E = YAHOO.util.Event;
	var $D = YAHOO.util.Dom;
	var $YW = YAHOO.widget;
	var $U = getPackageForName("com.i10n.fleet.Utils");
	var $W = getPackageForName("com.i10n.fleet.widget.ui");
	var $V = getPackageForName("com.i10n.fleet.widget.view");
	var $WU = getPackageForName("com.i10n.fleet.widget.util");
	var autoCompleteAssign=null;
	var autoCompleteVacant=null;
	var autoCompleteAssignDriver=null;
	var autoCompleteVacantDriver=null;
	var autoCompleteAssignGroup=null;
	var autoCompleteVacantGroup=null;
	var autoCompleteAssignDriverGroup=null;
	var autoCompleteVacantDriverGroup=null;
	var autoCompleteAssignRouteSchedule=null;
	var autoCompleteVacantRouteSchedule=null;
	/**
	 * Generic javascript for the assignment pattern
	 *
	 * Works with the delete user interfaces in the "Assign Vehicle",
	 * "Assign Driver" in the control panel view of the admin skin
	 *
	 * The deleting mechanism (eg: the URL to hit) is very specific to individual
	 * widgets. Thus they have to be implemented in their respective scripts
	 *
	 * @author N.Balaji
	 */
	$W.Assignment = function(el, oArgs) {
		this.initAssignment(el, oArgs);
	};
	$L.augmentObject($W.Assignment, {
		ASSIGNMENT_STATES: {
			STATE_ONE: 1,
			STATE_TWO: 2,
			STATE_THREE: 3
		}
	});
	$L.augmentObject($W.Assignment.prototype, {
		/**
		 * The initialization function
		 * @param {Object} el The base element that contains the form , save button pattern
		 * @param {Object} oArgs Contains the configuration attributes
		 */
		initAssignment: function(el, oArgs) {
			if (!oArgs) {
				oArgs = {};
			}
			if (!$L.isString(oArgs.userListClass)) {
				oArgs.userListClass = "user-sel";
			}
			if (!$L.isString(oArgs.userListContainerClass)) {
				oArgs.userListContainerClass = "user-list-cnt";
			}
			if (!$L.isString(oArgs.groupListContainerClass)) {
				oArgs.groupListContainerClass = "group-list-cnt";
			}
			if (!$L.isString(oArgs.groupListClass)){
				oArgs.groupListClass = "group-sel";
			}
			if (!$L.isString(oArgs.assignmentControlClass)) {
				oArgs.assignmentControlClass = "assignment-control";
			}
			if (!$L.isString(oArgs.assignmentControlListClass)) {
				oArgs.assignmentControlListClass = "assignment-control-list";
			}
			if (!$L.isString(oArgs.vacantListClass)) {
				oArgs.vacantListClass = "vacant-list";
			}
			if (!$L.isString(oArgs.vacantListContainerClass)) {
				oArgs.vacantListContainerClass = "vacant-list-cnt";
			}
			if (!$L.isString(oArgs.assignedListClass)) {
				oArgs.assignedListClass = "assigned-list";
			}
			if (!$L.isString(oArgs.assignedListContainerClass)) {
				oArgs.assignedListContainerClass = "assigned-list-cnt";
			}
			if (!$L.isString(oArgs.removeButtonClass)) {
				oArgs.removeButtonClass = "remove-button";
			}
			if (!$L.isString(oArgs.addButtonClass)) {
				oArgs.addButtonClass = "add-button";
			}
			if (!$L.isString(oArgs.saveButtonClass)) {
				oArgs.saveButtonClass = "save-button";
			}

			this.elBase = el;
			this.oConfiguration = oArgs;
			this.setState($W.Assignment.ASSIGNMENT_STATES.STATE_ONE);
			this.addBasicListeners();
			this.countUserList();
			this.countGroupList();
		},
		/*Declaring the member properties*/
		elBase: null,
		oConfiguration: null,
		/*Storing the currently selected user's ID*/
		sUserID: null,
		sGroupID: null,
		/*Defining methods*/
		/**
		 * Default Search String
		 */
		DEFAULT_SEARCH_STRING: "Search...",


		/**
		 * Setups for showing autocomplete for assign driver
		 */
		showAutoCompleteForAssignDriver: function(){
			this.setAutoCompleteForAssignDriver();
			this.setAutoCompleteForVacantDriver();
		},
		/**
		 * auto complete for assign vehicle
		 */
		showAutocomplete: function(){
			this.setAutoCompleteForAssignVehicle();
			this.setAutoCompleteForVacantVehicle();
		},
		/**
		 * Setups for showing autocomplete for assign vehicle list
		 */
		setAutoCompleteForAssignVehicle: function(){

			this._generateDataSourceForAssignVehicle();
			if(this.autoCompleteAssign){
				this.autoCompleteAssign.destroy();
				this.autoCompleteAssign=null;
				var autoCompleteAssignBuffer = new $YW.AutoComplete(this.searchAssignVehicleInput, $D.getElementsByClassName("search-autocomplete-assignvehicle")[0], this.oDataSourceAssign);
				this.autoCompleteAssign=autoCompleteAssignBuffer;
				this.autoCompleteAssign.prehighlightClassName = "yui-ac-prehighlight";
				this.autoCompleteAssign.useShadow = true;
				this.assignVehicleListListener();
			}
			else {
				this.autoCompleteAssign = new $YW.AutoComplete(this.searchAssignVehicleInput, $D.getElementsByClassName("search-autocomplete-assignvehicle")[0], this.oDataSourceAssign);
				this.autoCompleteAssign.prehighlightClassName = "yui-ac-prehighlight";
				this.autoCompleteAssign.useShadow = true;
				this.assignVehicleListListener();
			}
		},
		/**
		 * Setups for showing autocomplete for vacant vehicle list
		 */
		setAutoCompleteForVacantVehicle: function(){
			this._generateDataSourceForVacantVehicle();

			if(this.autoCompleteVacant){
				this.autoCompleteVacant.destroy();
				this.autoCompleteVacant=null;
				var autoCompleteVacantBuffer = new $YW.AutoComplete(this.searchVacantVehicleInput, $D.getElementsByClassName("search-autocomplete-vacantvehicle")[0], this.oDataSourceVacant);
				this.autoCompleteVacant=autoCompleteVacantBuffer;
				this.autoCompleteVacant.prehighlightClassName = "yui-ac-prehighlight";
				this.autoCompleteVacant.useShadow = true;
				this.vacantVehicleListListener();
			}else {
				this.autoCompleteVacant = new $YW.AutoComplete(this.searchVacantVehicleInput, $D.getElementsByClassName("search-autocomplete-vacantvehicle")[0], this.oDataSourceVacant);
				this.autoCompleteVacant.prehighlightClassName = "yui-ac-prehighlight";
				this.autoCompleteVacant.useShadow = true;
				this.vacantVehicleListListener();
			}
		},

		assignVehicleListListener:function(){
			this.searchAssignVehicleInput = $D.getElementsByClassName("search-item-input-assign",null,this.elBase)[0];
			var enterKeyListener = new $YU.KeyListener(this.searchAssignVehicleInput, {
				"keys": 13
			}, {
				"fn": function(){
					this.selectItem(this.searchAssignVehicleInput.value);
				},
				"scope": this,
				"correctScope": true
			});
			enterKeyListener.enable();
			$U.addDefaultInputText(this.searchAssignVehicleInput, this.searchAssignVehicleInput.value);
			/**
			 * Avoiding possible memory leaks;
			 */
			enterKeyListener=null;

		},

		vacantVehicleListListener: function(){
			this.searchVacantVehicleInput = $D.getElementsByClassName("search-item-input-vacant",null,this.elBase)[0];
			var enterKeyListenerVacantVehicle = new $YU.KeyListener(this.searchVacantVehicleInput, {
				"keys": 13
			}, {
				"fn": function(){
					this.selectVacantItem(this.searchVacantVehicleInput.value);
				},
				"scope": this,
				"correctScope": true
			});
			enterKeyListenerVacantVehicle.enable();
			$U.addDefaultInputText(this.searchVacantVehicleInput, this.searchVacantVehicleInput.value);
			/**
			 * Avoiding possible memory leaks;
			 */
			enterKeyListenerVacantVehicle = null;
		},

		/**
		 * Generates Datasource for autocomplete for assign vehicle list
		 */
		_generateDataSourceForAssignVehicle: function(){

			var dataSourceAssignVehicle = [];
			dataSourceAssignVehicle.length=0;
			this.elAssign=$D.getElementsByClassName("assignvehicle")[0];
			this.elAssignContainer = $D.getElementsByClassName("assigned-list",null,this.elAssign)[0];

			$D.getElementsByClassName("assng-list-item", null, this.elAssignContainer, function(target){

				var itemID = target.getAttribute("value");

				var itemName = target.innerHTML;

				var data = {
						"name": itemName,
						"id": itemID
				};

				dataSourceAssignVehicle.push(data);

			});

			var oDataSourceAssign=[];  
			this.oDataSourceAssign = new $YU.LocalDataSource(dataSourceAssignVehicle);
			this.oDataSourceAssign.responseSchema = {
					fields: ["name"]
			};

		},

		/**
		 * Generates Datasource for autocomplete for vacant vehicle list
		 */
		_generateDataSourceForVacantVehicle: function(){

			var dataSourceVacantVehicle = [];

			this.elVacant=$D.getElementsByClassName("assignvehicle")[0];
			this.elVacantContainer = $D.getElementsByClassName("vacant-list",null,this.elVacant)[0];

			$D.getElementsByClassName("vac-list-item ", null, this.elVacantContainer, function(target){

				var itemID = target.getAttribute("value");
				var itemName = target.innerHTML;

				var data = {
						"name": itemName,
						"id": itemID
				};

				dataSourceVacantVehicle.push(data);

			});

			var oDataSourceVacant=[];
			this.oDataSourceVacant = new $YU.LocalDataSource(dataSourceVacantVehicle);
			this.oDataSourceVacant.responseSchema = {
					fields: ["name"]
			};


		},




		/**
		 * auto complete for RouteSchedule vehicle
		 */
		showAutocompleteForRouteSchedule: function(){
			this.setAutoCompleteForAssignRouteSchedule();
			this.setAutoCompleteForVacantRouteSchedule();
		},
		/**
		 * Setups for showing autocomplete for assign RouteSchedule list
		 */
		setAutoCompleteForAssignRouteSchedule: function(){
			this._generateDataSourceForAssignRouteSchedule();
			if(this.autoCompleteAssignRouteSchedule){
				this.autoCompleteAssignRouteSchedule.destroy();
				this.autoCompleteAssignRouteSchedule=null;
				var autoCompleteAssignRouteScheduleBuffer = new $YW.AutoComplete(this.searchAssignRouteScheduleInput, $D.getElementsByClassName("search-autocomplete-assignrouteschedule")[0], this.oDataSourceAssignRouteSchedule);
				this.autoCompleteAssignRouteSchedule=autoCompleteAssignRouteScheduleBuffer;
				this.autoCompleteAssignRouteSchedule.prehighlightClassName = "yui-ac-prehighlight";
				this.autoCompleteAssignRouteSchedule.useShadow = true;
				this.assignRouteScheduleListListener();
			}
			else {
				this.autoCompleteAssignRouteSchedule = new $YW.AutoComplete(this.searchAssignRouteScheduleInput, $D.getElementsByClassName("search-autocomplete-assignrouteschedule")[0], this.oDataSourceAssignRouteSchedule);
				this.autoCompleteAssignRouteSchedule.prehighlightClassName = "yui-ac-prehighlight";
				this.autoCompleteAssignRouteSchedule.useShadow = true;
				this.assignRouteScheduleListListener();
			}
		},
		/**
		 * Setups for showing autocomplete for vacant RouteSchedule list
		 */
		setAutoCompleteForVacantRouteSchedule: function(){
			this._generateDataSourceForVacantRouteSchedule();

			if(this.autoCompleteVacantRouteSchedule){
				this.autoCompleteVacantRouteSchedule.destroy();
				this.autoCompleteVacantRouteSchedule=null;
				var autoCompleteVacantRouteScheduleBuffer = new $YW.AutoComplete(this.searchVacantRouteScheduleInput, $D.getElementsByClassName("search-autocomplete-vacantrouteschedule")[0], this.oDataSourceVacantRouteSchedule);
				this.autoCompleteVacantRouteSchedule=autoCompleteVacantRouteScheduleBuffer;
				this.autoCompleteVacantRouteSchedule.prehighlightClassName = "yui-ac-prehighlight";
				this.autoCompleteVacantRouteSchedule.useShadow = true;
				this.vacantRouteScheduleListListener();
			}else {
				this.autoCompleteVacantRouteSchedule = new $YW.AutoComplete(this.searchVacantRouteScheduleInput, $D.getElementsByClassName("search-autocomplete-vacantrouteschedule")[0], this.oDataSourceVacantRouteSchedule);
				this.autoCompleteVacantRouteSchedule.prehighlightClassName = "yui-ac-prehighlight";
				this.autoCompleteVacantRouteSchedule.useShadow = true;
				this.vacantRouteScheduleListListener();
			}
		},

		assignRouteScheduleListListener:function(){
			this.searchAssignRouteScheduleInput = $D.getElementsByClassName("search-item-input-assignrouteschedule",null,this.elBase)[0];
			var enterKeyListenerAssignRouteSchedule = new $YU.KeyListener(this.searchAssignRouteScheduleInput, {
				"keys": 13
			}, {
				"fn": function(){
					this.selectItem(this.searchAssignRouteScheduleInput.value);
				},
				"scope": this,
				"correctScope": true
			});
			enterKeyListenerAssignRouteSchedule.enable();
			$U.addDefaultInputText(this.searchAssignRouteScheduleInput, this.searchAssignRouteScheduleInput.value);
			/**
			 * Avoiding possible memory leaks;
			 */
			enterKeyListenerAssignRouteSchedule=null;

		},

		vacantRouteScheduleListListener: function(){
			this.searchVacantRouteScheduleInput = $D.getElementsByClassName("search-item-input-vacantrouteschedule",null,this.elBase)[0]; 
			var enterKeyListenerVacantRouteSchedule = new $YU.KeyListener(this.searchVacantRouteScheduleInput, {
				"keys": 13
			}, {
				"fn": function(){
					this.selectVacantItem(this.searchVacantRouteScheduleInput.value);
				},
				"scope": this,
				"correctScope": true
			});
			enterKeyListenerVacantRouteSchedule.enable();
			$U.addDefaultInputText(this.searchVacantRouteScheduleInput, this.searchVacantRouteScheduleInput.value);
			/**
			 * Avoiding possible memory leaks;
			 */
			enterKeyListenerVacantRouteSchedule = null;
		},

		/**
		 * Generates Datasource for autocomplete for assign RouteSchedule list
		 */
		_generateDataSourceForAssignRouteSchedule: function(){
			var dataSourceAssignRouteSchedule = [];
			dataSourceAssignRouteSchedule.length=0;
			this.elAssign=$D.getElementsByClassName("assignrouteschedule")[0];
			this.elAssignContainer = $D.getElementsByClassName("assigned-list",null,this.elAssign)[0];

			$D.getElementsByClassName("assng-list-item", null, this.elAssignContainer, function(target){

				var itemID = target.getAttribute("value");
				var itemName = target.innerHTML;

				var data = {
						"routescheduleid": itemName,
						"routescheduleid": itemID
				};

				dataSourceAssignRouteSchedule.push(data);

			});

			var oDataSourceAssignRouteSchedule=[];  
			this.oDataSourceAssignRouteSchedule = new $YU.LocalDataSource(dataSourceAssignRouteSchedule);
			this.oDataSourceAssignRouteSchedule.responseSchema = {
					fields: ["routescheduleid"]
			};

		},

		/**
		 * Generates Datasource for autocomplete for vacant RouteSchedule list
		 */
		_generateDataSourceForVacantRouteSchedule: function(){
			var dataSourceVacantRouteSchedule = [];

			this.elVacant=$D.getElementsByClassName("assignrouteschedule")[0];
			this.elVacantContainer = $D.getElementsByClassName("vacant-list",null,this.elVacant)[0];

			$D.getElementsByClassName("vac-list-item ", null, this.elVacantContainer, function(target){

				var itemID = target.getAttribute("value");

				var itemName = target.innerHTML;

				var data = {
						"routescheduleid": itemName,
						"routescheduleid": itemID
				};

				dataSourceVacantRouteSchedule.push(data);

			});

			var oDataSourceVacantRouteSchedule=[];
			this.oDataSourceVacantRouteSchedule = new $YU.LocalDataSource(dataSourceVacantRouteSchedule);
			this.oDataSourceVacantRouteSchedule.responseSchema = {
					fields: ["routescheduleid"]
			};


		},
		/**
		 * Setups for showing autocomplete for assign driver list
		 */
		setAutoCompleteForAssignDriver: function(){
			this._generateDataSourceForAssignDriver();
			if(this.autoCompleteAssignDriver){
				this.autoCompleteAssignDriver.destroy();
				this.autoCompleteAssignDriver=null;
				var autoCompleteAssignDriverBuffer = new $YW.AutoComplete(this.searchAssignDriverInput, $D.getElementsByClassName("search-autocomplete-assigndriver", null, this.elBase)[0], this.oDataSourceAssignDriver);
				this.autoCompleteAssignDriver=autoCompleteAssignDriverBuffer;
				this.autoCompleteAssignDriver.prehighlightClassName = "yui-ac-prehighlight";
				this.autoCompleteAssignDriver.useShadow = true;   
				this.assignDriverListListener();
			}
			else{
				this.autoCompleteAssignDriver = new $YW.AutoComplete(this.searchAssignDriverInput, $D.getElementsByClassName("search-autocomplete-assigndriver", null, this.elBase)[0], this.oDataSourceAssignDriver);
				this.autoCompleteAssignDriver.prehighlightClassName = "yui-ac-prehighlight";
				this.autoCompleteAssignDriver.useShadow = true;
				this.assignDriverListListener();
			}
		},
		/**
		 * Setups for showing autocomplete for vacant driver list
		 */
		setAutoCompleteForVacantDriver: function(){
			this._generateDataSourceForVacantDriver();
			if(this.autoCompleteVacantDriver){
				this.autoCompleteVacantDriver.destroy();
				this.autoCompleteVacantDriver=null;
				var autoCompleteVacantDriverBuffer = new $YW.AutoComplete(this.searchVacantDriverInput, $D.getElementsByClassName("search-autocomplete-vacantdriver", null, this.elBase)[0], this.oDataSourceVacantDriver);
				this.autoCompleteVacantDriver=autoCompleteVacantDriverBuffer;
				this.autoCompleteVacantDriver.prehighlightClassName = "yui-ac-prehighlight";
				this.autoCompleteVacantDriver.useShadow = true;
				this.vacantDriverListListener();
			}
			else
			{
				this.autoCompleteVacantDriver = new $YW.AutoComplete(this.searchVacantDriverInput, $D.getElementsByClassName("search-autocomplete-vacantdriver", null, this.elBase)[0], this.oDataSourceVacantDriver);
				this.autoCompleteVacantDriver.prehighlightClassName = "yui-ac-prehighlight";
				this.autoCompleteVacantDriver.useShadow = true;
				this.vacantDriverListListener();
			}
		},
		assignDriverListListener:function(){
			this.searchAssignDriverInput = $D.getElementsByClassName("search-item-input-assigndriver",null,this.elBase)[0];
			var enterKeyListenerAssignDriver = new $YU.KeyListener(this.searchAssignDriverInput, {
				"keys": 13
			}, {
				"fn": function(){
					this.selectAssignDriverItem(this.searchAssignDriverInput.value);
				},
				"scope": this,
				"correctScope": true
			});
			enterKeyListenerAssignDriver.enable();
			$U.addDefaultInputText(this.searchAssignDriverInput,this.searchAssignDriverInput.value );
			/**
			 * Avoiding possible memory leaks;
			 */
			enterKeyListenerAssignDriver = null;
		},

		vacantDriverListListener: function(){
			this.searchVacantDriverInput = $D.getElementsByClassName("search-item-input-vacantdriver",null,this.elBase)[0];
			var enterKeyListenerVacantDriver = new $YU.KeyListener(this.searchVacantDriverInput, {
				"keys": 13
			}, {
				"fn": function(){
					this.selectVacantDriverItem(this.searchVacantDriverInput.value);
				},
				"scope": this,
				"correctScope": true
			});
			enterKeyListenerVacantDriver.enable();
			$U.addDefaultInputText(this.searchVacantDriverInput, this.searchVacantDriverInput.value);
			/**
			 * Avoiding possible memory leaks;
			 */
			enterKeyListenerVacantDriver = null;
		},
		/**
		 * Generates Datasource for autocomplete for assign driver list
		 */
		_generateDataSourceForAssignDriver: function(){

			var dataSourceAssignDriver = [];
			this.elAssignContainer = $D.getElementsByClassName("assigned-list",null,this.elBase)[0];

			$D.getElementsByClassName("assng-list-item ", null, this.elAssignContainer, function(target){

				var itemID = target.getAttribute("value");

				var itemName = target.innerHTML;

				var data = {
						"name": itemName,
						"id": itemID
				};

				dataSourceAssignDriver.push(data);

			});
			var oDataSourceAssignDriver=[];
			this.oDataSourceAssignDriver = new $YU.LocalDataSource(dataSourceAssignDriver);
			this.oDataSourceAssignDriver.responseSchema = {
					fields: ["name"]
			};
		},

		/**
		 * Generates Datasource for autocomplete for vacant driver list
		 */
		_generateDataSourceForVacantDriver: function(){

			var dataSourceVacantDriver = [];
			this.elVacantContainer = $D.getElementsByClassName("vacant-list",null,this.elBase)[0];

			$D.getElementsByClassName("vac-list-item ", null, this.elVacantContainer, function(target){
				var itemID = target.getAttribute("value");

				var itemName = target.innerHTML;

				var data = {
						"name": itemName,
						"id": itemID
				};

				dataSourceVacantDriver.push(data);

			});
			var oDataSourceVacantDriver=[];
			this.oDataSourceVacantDriver = new $YU.LocalDataSource(dataSourceVacantDriver);
			this.oDataSourceVacantDriver.responseSchema = {
					fields: ["name"]
			};
		},

		/**
		 * Selects an item (trip) in the search with the given item name (object)
		 */
		selectItem: function(objectName){
			/*Retaining the current scope*/

			this.elAssignContainer = $D.getElementsByClassName("assigned-list",null,this.elBase)[0];
			$D.getElementsByClassName("assng-list-item", null, this.elAssignContainer, function(target){
				var key;
				key=target.innerHTML;

				if (objectName != key) {
					/*Change the Selected trip attribute value*/
					$D.addClass(target, "disabled");
				}
				else {
					if($D.hasClass(target,"disabled")){
						$D.removeClass(target,"disabled");
					}
				}
			});
			/*Avoiding possible memory leaks*/
			objectName = null;

		},

		/**
		 * Selects an item (trip) in the search with the given item name (object)
		 */
		selectVacantItem: function(objectName){
			/*Retaining the current scope*/

			this.elAssignContainer = $D.getElementsByClassName("vacant-list",null,this.elBase)[0];
			$D.getElementsByClassName("vac-list-item", null, this.elAssignContainer, function(target){
				var key;
				key = target.innerHTML;
				if (objectName != key) {
					/*Change the Selected trip attribute value*/
					$D.addClass(target, "disabled");
				}
				else {
					if($D.hasClass(target,"disabled")){
						$D.removeClass(target,"disabled");
					}
				}
			});
			/*Avioding possible memory leaks*/
			objectName = null;


		},
		/**
		 * Selects an item (trip) in the search with the given item name (object)
		 */
		selectAssignDriverItem: function(objectName){
			/*Retaining the current scope*/
			this.elAssignContainer = $D.getElementsByClassName("assigned-list",null,this.elBase)[0];
			$D.getElementsByClassName("assng-list-item", null, this.elAssignContainer, function(target){
				var key;
				key = target.innerHTML;
				if (objectName != key) {
					/*Change the Selected trip attribute value*/
					$D.addClass(target, "disabled");
				}
				else {
					if($D.hasClass(target,"disabled")){
						$D.removeClass(target,"disabled");
					}
				}
			});
			/*Avioding possible memory leaks*/
			objectName = null;

		},
		/**
		 * Selects an item (trip) in the search with the given item name (object)
		 */
		selectVacantDriverItem: function(objectName){
			/*Retaining the current scope*/

			this.elAssignContainer = $D.getElementsByClassName("vacant-list",null,this.elBase)[0];
			$D.getElementsByClassName("vac-list-item", null, this.elAssignContainer, function(target){
				var key;
				key = target.innerHTML;
				if (objectName != key) {
					/*Change the Selected trip attribute value*/
					$D.addClass(target, "disabled");
				}
				else {
					if($D.hasClass(target,"disabled")){
						$D.removeClass(target,"disabled");
					}
				}
			});
			/*Avioding possible memory leaks*/
			objectName = null;

		},



		showAutoCompleteForGroups: function(){
			this.setAutoCompleteForAssignGroup();
			this.setAutoCompleteForVacantGroup();
		},
		/**
		 * Setups for showing autocomplete for assign vehicle list
		 */
		setAutoCompleteForAssignGroup: function(){

			this._generateDataSourceForAssignGroup();
			if(this.autoCompleteAssignGroup){
				this.autoCompleteAssignGroup.destroy();
				this.autoCompleteAssignGroup=null;
				var autoCompleteAssignGroupBuffer = new $YW.AutoComplete(this.searchAssignGroupInput, $D.getElementsByClassName("search-autocomplete-assignvehiclestogroup")[0], this.oDataSourceAssignGroup);
				this.autoCompleteAssignGroup=autoCompleteAssignGroupBuffer;
				this.autoCompleteAssignGroup.prehighlightClassName = "yui-ac-prehighlight";
				this.autoCompleteAssignGroup.useShadow = true;
				this.assignGroupListListener();
			}
			else {
				this.autoCompleteAssignGroup = new $YW.AutoComplete(this.searchAssignGroupInput, $D.getElementsByClassName("search-autocomplete-assignvehiclestogroup")[0], this.oDataSourceAssignGroup);
				this.autoCompleteAssignGroup.prehighlightClassName = "yui-ac-prehighlight";
				this.autoCompleteAssignGroup.useShadow = true;
				this.assignGroupListListener();
			}
		},
		/**
		 * Setups for showing autocomplete for vacant vehicle list
		 */
		setAutoCompleteForVacantGroup: function(){
			this._generateDataSourceForVacantGroup();

			if(this.autoCompleteVacantGroup){
				this.autoCompleteVacantGroup.destroy();
				this.autoCompleteVacantGroup=null;
				var autoCompleteVacantGroupBuffer = new $YW.AutoComplete(this.searchVacantGroupInput, $D.getElementsByClassName("search-autocomplete-vacantvehiclestogroup")[0], this.oDataSourceVacantGroup);
				this.autoCompleteVacantGroup=autoCompleteVacantGroupBuffer;
				this.autoCompleteVacantGroup.prehighlightClassName = "yui-ac-prehighlight";
				this.autoCompleteVacantGroup.useShadow = true;
				this.vacantGroupListListener();
			}else {
				this.autoCompleteVacantGroup = new $YW.AutoComplete(this.searchVacantGroupInput, $D.getElementsByClassName("search-autocomplete-vacantvehiclestogroup")[0], this.oDataSourceVacantGroup);
				this.autoCompleteVacantGroup.prehighlightClassName = "yui-ac-prehighlight";
				this.autoCompleteVacantGroup.useShadow = true;
				this.vacantGroupListListener();
			}
		},


		assignGroupListListener:function(){
			this.searchAssignGroupInput = $D.getElementsByClassName("search-item-input-assignvehiclestogroup",null,this.elBase)[0];
			var enterKeyListener = new $YU.KeyListener(this.searchAssignGroupInput, {
				"keys": 13
			}, {
				"fn": function(){
					this.selectAssignGroupItem(this.searchAssignGroupInput.value);
				},
				"scope": this,
				"correctScope": true
			});
			enterKeyListener.enable();
			$U.addDefaultInputText(this.searchAssignGroupInput, this.searchAssignGroupInput.value);
			/**
			 * Avoiding possible memory leaks;
			 */
			enterKeyListener=null;

		},

		vacantGroupListListener: function(){
			this.searchVacantGroupInput = $D.getElementsByClassName("search-item-input-vacantvehiclestogroup",null,this.elBase)[0];
			var enterKeyListenerVacantVehicle = new $YU.KeyListener(this.searchVacantGroupInput, {
				"keys": 13
			}, {
				"fn": function(){
					this.selectVacantGroupItem(this.searchVacantGroupInput.value);
				},
				"scope": this,
				"correctScope": true
			});
			enterKeyListenerVacantVehicle.enable();
			$U.addDefaultInputText(this.searchVacantGroupInput, this.searchVacantGroupInput.value);
			/**
			 * Avoiding possible memory leaks;
			 */
			enterKeyListenerVacantVehicle = null;
		},

		/**
		 * Generates Datasource for autocomplete for assign vehicle list
		 */
		_generateDataSourceForAssignGroup: function(){

			var dataSourceAssignGroup = [];

			this.elAssign=$D.getElementsByClassName("assignvehiclestogroup")[0];
			this.elAssignContainer = $D.getElementsByClassName("assigned-list",null,this.elAssign)[0];

			$D.getElementsByClassName("assng-list-item", null, this.elAssignContainer, function(target){

				var itemID = target.getAttribute("value");

				var itemName = target.innerHTML;

				var data = {
						"name": itemName,
						"id": itemID
				};

				dataSourceAssignGroup.push(data);

			});

			var oDataSourceAssignGroup=[];  
			this.oDataSourceAssignGroup = new $YU.LocalDataSource(dataSourceAssignGroup);
			this.oDataSourceAssignGroup.responseSchema = {
					fields: ["name"]
			};


		},

		/**
		 * Generates Datasource for autocomplete for vacant vehicle list
		 */
		_generateDataSourceForVacantGroup: function(){

			var dataSourceVacantGroup = [];
			this.elVacant=$D.getElementsByClassName("assignvehiclestogroup")[0];
			this.elVacantContainer = $D.getElementsByClassName("vacant-list",null,this.elVacant)[0];

			$D.getElementsByClassName("vac-list-item ", null, this.elVacantContainer, function(target){

				var itemID = target.getAttribute("value");

				var itemName = target.innerHTML;

				var data = {
						"name": itemName,
						"id": itemID
				};

				dataSourceVacantGroup.push(data);

			});

			var oDataSourceVacantGroup=[];
			oDataSourceVacantGroup.length=0;
			if(oDataSourceVacantGroup.length==0){
				this.oDataSourceVacantGroup = new $YU.LocalDataSource(dataSourceVacantGroup);
				this.oDataSourceVacantGroup.responseSchema = {
						fields: ["name"]
				};

			}

		},

		/**
		 * Selects an item (trip) in the search with the given item name (object)
		 */
		selectAssignGroupItem: function(objectName){
			/*Retaining the current scope*/

			this.elAssignContainer = $D.getElementsByClassName("assigned-list",null,this.elBase)[0];
			$D.getElementsByClassName("assng-list-item", null, this.elAssignContainer, function(target){
				var key;
				key=target.innerHTML;

				if (objectName != key) {
					/*Change the Selected trip attribute value*/
					$D.addClass(target, "disabled");
				}
				else {
					if($D.hasClass(target,"disabled")){
						$D.removeClass(target,"disabled");
					}
				}
			});
			/*Avoiding possible memory leaks*/
			objectName = null;

		},

		/**
		 * Selects an item (trip) in the search with the given item name (object)
		 */
		selectVacantGroupItem: function(objectName){
			/*Retaining the current scope*/

			this.elAssignContainer = $D.getElementsByClassName("vacant-list",null,this.elBase)[0];
			$D.getElementsByClassName("vac-list-item", null, this.elAssignContainer, function(target){
				var key;
				key = target.innerHTML;
				if (objectName != key) {
					/*Change the Selected trip attribute value*/
					$D.addClass(target, "disabled");
				}
				else {
					if($D.hasClass(target,"disabled")){
						$D.removeClass(target,"disabled");
					}
				}
			});
			/*Avioding possible memory leaks*/
			objectName = null;


		},
		showAutoCompleteForAssignDriverToGroups: function(){
			this.setAutoCompleteForAssignDriverGroup();
			this.setAutoCompleteForVacantDriverGroup();
		},
		/**
		 * Setups for showing autocomplete for assign vehicle list
		 */
		setAutoCompleteForAssignDriverGroup: function(){

			this._generateDataSourceForAssignDriverGroup();
			if(this.autoCompleteAssignDriverGroup){
				this.autoCompleteAssignDriverGroup.destroy();
				this.autoCompleteAssignDriverGroup=null;
				var autoCompleteAssignDriverGroupBuffer = new $YW.AutoComplete(this.searchAssignDriverGroupInput, $D.getElementsByClassName("search-autocomplete-assigndriverstogroup")[0], this.oDataSourceAssignDriverGroup);
				this.autoCompleteAssignDriverGroup=autoCompleteAssignDriverGroupBuffer;
				this.autoCompleteAssignDriverGroup.prehighlightClassName = "yui-ac-prehighlight";
				this.autoCompleteAssignDriverGroup.useShadow = true;
				this.assignDriverGroupListListener();
			}
			else {
				this.autoCompleteAssignDriverGroup = new $YW.AutoComplete(this.searchAssignDriverGroupInput, $D.getElementsByClassName("search-autocomplete-assigndriverstogroup")[0], this.oDataSourceAssignDriverGroup);
				this.autoCompleteAssignDriverGroup.prehighlightClassName = "yui-ac-prehighlight";
				this.autoCompleteAssignDriverGroup.useShadow = true;
				this.assignDriverGroupListListener();
			}
		},
		/**
		 * Setups for showing autocomplete for vacant vehicle list
		 */
		setAutoCompleteForVacantDriverGroup: function(){
			this._generateDataSourceForVacantDriverGroup();

			if(this.autoCompleteVacantDriverGroup){
				this.autoCompleteVacantDriverGroup.destroy();
				this.autoCompleteVacantDriverGroup=null;
				var autoCompleteVacantDriverGroupBuffer = new $YW.AutoComplete(this.searchVacantDriverGroupInput, $D.getElementsByClassName("search-autocomplete-vacantdriverstogroup")[0], this.oDataSourceVacantDriverGroup);
				this.autoCompleteVacantDriverGroup=autoCompleteVacantDriverGroupBuffer;
				this.autoCompleteVacantDriverGroup.prehighlightClassName = "yui-ac-prehighlight";
				this.autoCompleteVacantDriverGroup.useShadow = true;
				this.vacantDriverGroupListListener();
			}else {
				this.autoCompleteVacantDriverGroup = new $YW.AutoComplete(this.searchVacantDriverGroupInput, $D.getElementsByClassName("search-autocomplete-vacantdriverstogroup")[0], this.oDataSourceVacantDriverGroup);
				this.autoCompleteVacantDriverGroup.prehighlightClassName = "yui-ac-prehighlight";
				this.autoCompleteVacantDriverGroup.useShadow = true;
				this.vacantDriverGroupListListener();
			}
		},


		assignDriverGroupListListener:function(){
			this.searchAssignDriverGroupInput = $D.getElementsByClassName("search-item-input-assigndriverstogroup",null,this.elBase)[0];
			var enterKeyListener = new $YU.KeyListener(this.searchAssignDriverGroupInput, {
				"keys": 13
			}, {
				"fn": function(){
					this.selectAssignDriverGroupItem(this.searchAssignDriverGroupInput.value);
				},
				"scope": this,
				"correctScope": true
			});
			enterKeyListener.enable();
			$U.addDefaultInputText(this.searchAssignDriverGroupInput, this.searchAssignDriverGroupInput.value);
			/**
			 * Avoiding possible memory leaks;
			 */
			enterKeyListener=null;

		},

		vacantDriverGroupListListener: function(){
			this.searchVacantDriverGroupInput = $D.getElementsByClassName("search-item-input-vacantdriverstogroup",null,this.elBase)[0];
			var enterKeyListenerVacantVehicle = new $YU.KeyListener(this.searchVacantDriverGroupInput, {
				"keys": 13
			}, {
				"fn": function(){
					this.selectVacantDriverGroupItem(this.searchVacantDriverGroupInput.value);
				},
				"scope": this,
				"correctScope": true
			});
			enterKeyListenerVacantVehicle.enable();
			$U.addDefaultInputText(this.searchVacantDriverGroupInput, this.searchVacantDriverGroupInput.value);
			/**
			 * Avoiding possible memory leaks;
			 */
			enterKeyListenerVacantVehicle = null;
		},

		/**
		 * Generates Datasource for autocomplete for assign vehicle list
		 */
		_generateDataSourceForAssignDriverGroup: function(){

			var dataSourceAssignDriverGroup = [];

			this.elAssign=$D.getElementsByClassName("assigndriverstogroup")[0];
			this.elAssignContainer = $D.getElementsByClassName("assigned-list",null,this.elAssign)[0];

			$D.getElementsByClassName("assng-list-item", null, this.elAssignContainer, function(target){

				var itemID = target.getAttribute("value");

				var itemName = target.innerHTML;

				var data = {
						"name": itemName,
						"id": itemID
				};

				dataSourceAssignDriverGroup.push(data);

			});

			var oDataSourceAssignDriverGroup=[];  
			this.oDataSourceAssignDriverGroup = new $YU.LocalDataSource(dataSourceAssignDriverGroup);
			this.oDataSourceAssignDriverGroup.responseSchema = {
					fields: ["name"]
			};


		},

		/**
		 * Generates Datasource for autocomplete for vacant vehicle list
		 */
		_generateDataSourceForVacantDriverGroup: function(){

			var dataSourceVacantDriverGroup = [];
			this.elVacant=$D.getElementsByClassName("assigndriverstogroup")[0];
			this.elVacantContainer = $D.getElementsByClassName("vacant-list",null,this.elVacant)[0];

			$D.getElementsByClassName("vac-list-item ", null, this.elVacantContainer, function(target){

				var itemID = target.getAttribute("value");

				var itemName = target.innerHTML;

				var data = {
						"name": itemName,
						"id": itemID
				};

				dataSourceVacantDriverGroup.push(data);

			});

			var oDataSourceVacantDriverGroup=[];
			oDataSourceVacantDriverGroup.length=0;
			if(oDataSourceVacantDriverGroup.length==0){
				this.oDataSourceVacantDriverGroup = new $YU.LocalDataSource(dataSourceVacantDriverGroup);
				this.oDataSourceVacantDriverGroup.responseSchema = {
						fields: ["name"]
				};

			}

		},

		/**
		 * Selects an item (trip) in the search with the given item name (object)
		 */
		selectAssignDriverGroupItem: function(objectName){
			/*Retaining the current scope*/

			this.elAssignContainer = $D.getElementsByClassName("assigned-list",null,this.elBase)[0];
			$D.getElementsByClassName("assng-list-item", null, this.elAssignContainer, function(target){
				var key;
				key=target.innerHTML;

				if (objectName != key) {
					/*Change the Selected trip attribute value*/
					$D.addClass(target, "disabled");
				}
				else {
					if($D.hasClass(target,"disabled")){
						$D.removeClass(target,"disabled");
					}
				}
			});
			/*Avoiding possible memory leaks*/
			objectName = null;

		},

		/**
		 * Selects an item (trip) in the search with the given item name (object)
		 */
		selectVacantDriverGroupItem: function(objectName){
			/*Retaining the current scope*/

			this.elAssignContainer = $D.getElementsByClassName("vacant-list",null,this.elBase)[0];
			$D.getElementsByClassName("vac-list-item", null, this.elAssignContainer, function(target){
				var key;
				key = target.innerHTML;
				if (objectName != key) {
					/*Change the Selected trip attribute value*/
					$D.addClass(target, "disabled");
				}
				else {
					if($D.hasClass(target,"disabled")){
						$D.removeClass(target,"disabled");
					}
				}
			});
			/*Avioding possible memory leaks*/
			objectName = null;


		},

		/**
		 * Refreshes the widget
		 */
		refresh: function() {
			this.setState($W.Assignment.ASSIGNMENT_STATES.STATE_ONE);
			this.hideAssignmentControls();
		},
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
//			$U.Element.DivElement.appendTemplatizedChildren(elList, sItemTemplate, newList);
			var childstring="";
			for(var i=0;i<newList.length;i++)
			{
				var template=$U.processTemplate(sItemTemplate,newList[i]);
				childstring+=template;
			}


			elList.innerHTML=childstring;


		},
		/**
		 * Abstract method. Specific widgets should override this method to hit the particular url
		 * to save the entity that was added
		 */
		saveChanges: function(oInput) {
		},
		/**
		 * Sets the widget state to one of the available values
		 * @param {Object} oState use one of $W.Assignement.ASSIGNEMENT_STATES
		 */
		setState: function(oState) {
			var elStateView = $D.getElementsByClassName("state-view", null, this.elBase)[0];
			var i = 0;
			$D.getElementsByClassName("state-item", null, elStateView, function(elTarget) {
				if (i < oState) {
					if ($D.hasClass(elTarget, "disabled")) {
						$D.removeClass(elTarget, "disabled");
					}
				}
				else {
					if (!$D.hasClass(elTarget, "disabled")) {
						$D.addClass(elTarget, "disabled");
					}
				}
				i++;
			});
			/*
			 * Preventing possible memory leaks
			 */
			elStateView = i = null;
		},
		/**
		 * Hides or shows the assignment controls
		 */
		toggleAssignmentControls: function() {
			$D.getElementsByClassName(this.oConfiguration.assignmentControlClass, null, this.elBase, function(elTarget) {
				if (!$D.hasClass(elTarget, "disabled")) {
					$D.addClass(elTarget, "disabled");
				}
				else {
					$D.removeClass(elTarget, "disabled");
				}
			});
		},
		/**
		 * Hides the assignment controls
		 */
		hideAssignmentControls: function() {
			$D.getElementsByClassName(this.oConfiguration.assignmentControlClass, null, this.elBase, function(elTarget) {
				if (!$D.hasClass(elTarget, "disabled")) {
					$D.addClass(elTarget, "disabled");
				}
			});
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
		 * Displays the count of the user list
		 * TODO: Try taking DOM out of the counting logic for all scenarios
		 */
		countUserList: function() {
			var elContainer = $D.getElementsByClassName(this.oConfiguration.userListContainerClass, null, this.elBase)[0];
			var elList = $D.getElementsByClassName(this.oConfiguration.userListClass, null, elContainer)[0];
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
		 * Displays the count of the group list
		 * TODO: Try taking DOM out of the counting logic for all scenarios
		 */
		countGroupList: function() {
			var elContainer = $D.getElementsByClassName(this.oConfiguration.groupListContainerClass, null, this.elBase)[0];
			var elList = $D.getElementsByClassName(this.oConfiguration.groupListClass, null, elContainer)[0];
			var elCounter = $D.getElementsByClassName("list-count", null, elContainer)[0];
			if (elCounter) {
				elCounter.innerHTML = $D.getElementsByClassName("group-list-item", null, elList).length;
			}
			/**
			 * Preventing possible memory leaks
			 */

			elContainer = elList = elCounter = null;
		},
		/**
		 * Displays the count of the vacant list
		 */
		countVacantList: function() {
			var elContainer = $D.getElementsByClassName(this.oConfiguration.vacantListContainerClass, null, this.elBase)[0];
			var elList = $D.getElementsByClassName(this.oConfiguration.vacantListClass, null, elContainer)[0];
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
			var elContainer = $D.getElementsByClassName(this.oConfiguration.assignedListContainerClass, null, this.elBase)[0];
			var elList = $D.getElementsByClassName(this.oConfiguration.assignedListClass, null, elContainer)[0];
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
		/**
		 * Convenience method to directly count all lists
		 */
		countAllLists: function() {
			this.countGroupList();
			this.countUserList();
			this.countAssignmentLists();

		},
		/*Defining callbacks*/
		onSave: function() {
			var oInput = {};
			oInput.assignedEntities = new Array();
			oInput.vacantEntities = new Array();

			oInput.userID = new Array();
			oInput.groupID = new Array();

			/*Getting the assigned entities*/
			var elList = $D.getElementsByClassName(this.oConfiguration.assignedListClass, null, this.elBase)[0];
			$D.getElementsByClassName("list-item", null, elList, function(elTarget) {
				/*Pushing only the newly assigned entities*/
				if ($D.getAttribute(elTarget, "assigned") == "false") {
					oInput.assignedEntities.push($D.getAttribute(elTarget, "value"));
				}
			});

			var elList = $D.getElementsByClassName(this.oConfiguration.userListClass, null, this.elBase)[0];
			$D.getElementsByClassName("slist-item selected", null, elList,function(elTarget){
				oInput.userID.push($D.getAttribute(elTarget,"value"));
			});

			var glList = $D.getElementsByClassName(this.oConfiguration.groupListClass, null, this.elBase)[0];
			$D.getElementsByClassName("glist-item selected", null, glList,function(elTarget){
				oInput.groupID.push($D.getAttribute(elTarget,"value"));
			});

			/*Getting the vacant entities*/
			elList = $D.getElementsByClassName(this.oConfiguration.vacantListClass, null, this.elBase)[0];
			$D.getElementsByClassName("list-item", null, elList, function(elTarget) {
				/*Pushing only the newly vacanted entities*/
				if ($D.getAttribute(elTarget, "assigned") == "true") {
					oInput.vacantEntities.push($D.getAttribute(elTarget, "value"));
				}
			});
			/*Convert the input elements to a map*/
			this.saveChanges(oInput);
			this.setState($W.Assignment.ASSIGNMENT_STATES.STATE_ONE);
			this.hideAssignmentControls();
		},
		onMove: function(oEvent, oArgs) {

			/*Moving the selected items to the vacant list*/
			var oSelf = this;
			var elAssignedList = $D.getElementsByClassName(this.oConfiguration.assignedListClass, null, this.elBase)[0];

			var elVacantList = $D.getElementsByClassName(this.oConfiguration.vacantListClass, null, this.elBase)[0];


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
			if ($D.getElementsByClassName("moved", null, this.elBase)[0]) {
				this.setState($W.Assignment.ASSIGNMENT_STATES.STATE_THREE);
			}
			else {
				this.setState($W.Assignment.ASSIGNMENT_STATES.STATE_TWO);
			}
			/*
			 * Preventing possible memory leaks
			 */
			elAssignedList = elVacantList = oSelf = null;
		},
		/*Installing listeners*/
		/**
		 * The method that adds listeners to the basic elements like the save button
		 *
		 * Established in this way so that the more specific widgets can install
		 * other additional listeners without disturbing the base listeners
		 */
		addBasicListeners: function() {
			/*Simulating select behavior for list items*/
			var oListBehaviorLayer = new $U.DOMEventManager($D.getElementsByClassName(this.oConfiguration.assignmentControlListClass, null, this.elBase), "click");
			oListBehaviorLayer.addBehavior("list-item", function(event, args) {
				if (!$D.hasClass(args[1].target, "selected")) {
					$D.addClass(args[1].target, "selected");
				}
				else {
					$D.removeClass(args[1].target, "selected");
				}
			}, null, this);
			/*Adding listeners to the buttons*/
			$W.Buttons.addDefaultHandler($D.getElementsByClassName(this.oConfiguration.removeButtonClass, null, this.elBase)[0], this.onMove, {
				button: "remove"
			}, this);
			$W.Buttons.addDefaultHandler($D.getElementsByClassName(this.oConfiguration.addButtonClass, null, this.elBase)[0], this.onMove, {
				button: "assign"
			}, this);
			$W.Buttons.addDefaultHandler($D.getElementsByClassName(this.oConfiguration.saveButtonClass, null, this.elBase)[0], this.onSave, null, this);
			/*
			 * Preventing possible memory leaks
			 */
			oListBehaviorLayer = null;

			this.searchAssignVehicleInput = $D.getElementsByClassName("search-item-input-assign",null,this.elBase)[0];

			this.searchVacantVehicleInput = $D.getElementsByClassName("search-item-input-vacant",null,this.elBase)[0];

			this.searchAssignRouteScheduleInput = $D.getElementsByClassName("search-item-input-assignrouteschedule",null,this.elBase)[0];

			this.searchVacantRouteScheduleInput = $D.getElementsByClassName("search-item-input-vacantrouteschedule",null,this.elBase)[0];

			this.searchAssignDriverInput = $D.getElementsByClassName("search-item-input-assigndriver",null,this.elBase)[0];

			this.searchVacantDriverInput = $D.getElementsByClassName("search-item-input-vacantdriver",null,this.elBase)[0];


			this.searchAssignGroupInput = $D.getElementsByClassName("search-item-input-assignvehiclestogroup",null,this.elBase)[0];

			this.searchVacantGroupInput = $D.getElementsByClassName("search-item-input-vacantvehiclestogroup",null,this.elBase)[0];

			this.searchAssignDriverGroupInput = $D.getElementsByClassName("search-item-input-assigndriverstogroup",null,this.elBase)[0];

			this.searchVacantDriverGroupInput = $D.getElementsByClassName("search-item-input-vacantdriverstogroup",null,this.elBase)[0];
		},


		/*Defining utilities*/
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
})();
