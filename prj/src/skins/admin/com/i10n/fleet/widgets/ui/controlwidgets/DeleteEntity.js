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
	/**
	 * Generic javascript for the deleting an entity pattern
	 *
	 * Works with the delete user interfaces in the "Delete Vehicle","Delete User",
	 * "Delete Driver" in the control panel view of the admin skin
	 *
	 * The deleting mechanism (eg: the URL to hit) is very specific to individual
	 * widgets. Thus they have to be implemented in their respective scripts
	 *
	 * @author N.Balaji
	 */
	$W.DeleteEntity = function(el, oArgs) {
		this.initDeleteEntity(el, oArgs);
	};
	$L.augmentObject($W.DeleteEntity, {
		DELETION_STATES: {
		STATE_ONE: 1,
		STATE_TWO: 2,
		STATE_THREE: 3
	}
	});
	$L.augmentObject($W.DeleteEntity.prototype, {
		/**
		 * The initialization function
		 * @param {Object} el The base element that contains the form , save button pattern
		 * @param {Object} oArgs Contains the configuration attributes
		 */
		initDeleteEntity: function(el, oArgs) {
		if (!oArgs) {
			oArgs = {};
		}
		if (!$L.isString(oArgs.listClass)) {
			oArgs.listClass = "input-list";
		}
		if (!$L.isString(oArgs.listContainerClass)) {
			oArgs.listContainerClass = "bd";
		}
		if (!$L.isString(oArgs.deleteButtonClass)) {
			oArgs.deleteButtonClass = "delete-button";
		}
		if (!$L.isString(oArgs.cancelButtonClass)) {
			oArgs.cancelButtonClass = "cancel-button";
		}
		if (!$L.isString(oArgs.checkBoxClass)) {
			oArgs.checkBoxClass = "delete-entity-checkbox";
		}
		if (!$L.isString(oArgs.recordClass)) {
			oArgs.recordClass = "entity-record";
		}
		this.elBase = el;
		this.oConfirmation = new $W.ConfirmationPopUp($D.getElementsByClassName("confirmationpopup", null, this.elBase)[0]);
		this.oConfirmation.render();
		this.oConfirmation.hide();
		this.oWarningpopup = new $W.WarningPopUp($D.getElementsByClassName("warningpopup", null, this.elBase)[0]);
		this.oWarningpopup.render();
		this.oWarningpopup.hide();
		this.oConfiguration = oArgs;
		this.setState($W.DeleteEntity.DELETION_STATES.STATE_ONE);
		this.addBasicListeners();
	},
	/*Declaring the member properties*/
	elBase: null,
	oConfiguration: null,
	oListEventManager: null,
	oConfirmation: null,
	oWarningpopup: null,
	/*Defining methods*/
	/**
	 * Refreshes the widget
	 */
	refresh: function() {
		this.onCancel();
	},
	/**
	 * Abstract method. Specific widgets should override this method to hit the particular url
	 * to delete the entities specified
	 */
	deleteEntity: function(aKeys) {
	},
	/**
	 * Abstract method. Specific widgets should override this method to hit the particular url
	 * to get data and reload the list
	 */
	reloadList: function() {
	},
	/**
	 * Sets the widget state to one of the available values
	 * @param {Object} oState use one of $W.DeleteEntity.DELETION_STATES
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
	/*Defining callbacks*/
	onAbort: function() {
		/*Asking for confirmation*/
		this.setState($W.DeleteEntity.DELETION_STATES.STATE_TWO);
		this.oConfirmation.hide();
		this.oWarningpopup.hide();
	},
	onDelete: function(oEvent, oArgs) {
		var aResult = new Array();
		var elList = $D.getElementsByClassName(this.oConfiguration.listClass, null, this.elBase)[0];
		$D.getElementsByClassName(this.oConfiguration.checkBoxClass, null, elList, function(elTarget) {
			if (elTarget.checked) {
				aResult.push(elTarget.value);
			}
		});
		if(aResult.length>0){
			/*Asking for confirmation*/
			this.setState($W.DeleteEntity.DELETION_STATES.STATE_THREE);
			this.oConfirmation.show();
		}
		else{
			this.oWarningpopup.show();
		}
	},
	onConfirm: function(oArgs) {
		if (oArgs.confirmation) {
			var aResult = new Array();
			var elList = $D.getElementsByClassName(this.oConfiguration.listClass, null, this.elBase)[0];
			$D.getElementsByClassName(this.oConfiguration.checkBoxClass, null, elList, function(elTarget) {
				if (elTarget.checked) {
					aResult.push(elTarget.value);
				}
			});
			this.deleteEntity(aResult);
			this.reloadList();
			this.setState($W.DeleteEntity.DELETION_STATES.STATE_ONE);
			/*
			 * Preventing possible memory leaks
			 */
			 elList = aResult = null;
		}
		else {
			this.onAbort();
		}
	},
	onCancel: function(oEvent, oArgs) {
		/*Refreshing all the check boxes*/
		var elList = $D.getElementsByClassName(this.oConfiguration.listClass, null, this.elBase)[0];
		$U.Forms.refreshForm(elList);
		this.setState($W.DeleteEntity.DELETION_STATES.STATE_ONE);
		/*
		 * Preventing possible memory leaks
		 */
		elList = null;
	},
	onEntitySelected: function(event, args) {
		var elTarget = args[1].target;
		var elRecord;
		if (!$D.hasClass(elTarget, this.oConfiguration.recordClass)) {
			elRecord = $D.getAncestorByClassName(elTarget, this.oConfiguration.recordClass);
		}
		else {
			elRecord = elTarget;
		}
		/*Selecting the check box*/
		var elCheckbox = $D.getElementsByClassName(this.oConfiguration.checkBoxClass, null, elRecord)[0];
		if (elCheckbox && (elTarget != elCheckbox)) {
			elCheckbox.checked = !elCheckbox.checked;
		}
		/*
		 * Setting the proper state
		 */
		var bResult = false;
		$D.getElementsByClassName("delete-entity-checkbox", null, this.elBase, function(elTarget) {
			bResult |= elTarget.checked;
		}, null, this);
		if (bResult) {
			this.setState($W.DeleteEntity.DELETION_STATES.STATE_TWO);
		}
		else {
			this.setState($W.DeleteEntity.DELETION_STATES.STATE_ONE);
		}
		/*
		 * Preventing possible memory leaks
		 */
		elTarget = elRecord = elCheckbox = bResult = null;
	},
	/**
	 * Default Search String
	 */
	DEFAULT_SEARCH_STRING: "Search...",

	/**
	 * Setups for showing autocomplete
	 */
	setAutoCompleteVehicleList: function(){
		this._generateDataSourceVehicleList();
		var autoCompleteVehicleList = new $YW.AutoComplete(this.searchInputVehicleList, $D.getElementsByClassName("search-autocomplete-vehiclelist")[0], this.oDataSourceVehicleList);
		autoCompleteVehicleList.prehighlightClassName = "yui-ac-prehighlight";
		autoCompleteVehicleList.useShadow = true;
		this.deleteVehicleListener();
	},

	/**
	 * Generates Datasource for autocomplete
	 */
	_generateDataSourceVehicleList: function(){
		var dataSourceVehicleList = [];

		var val=null;
		var result=new Array();
		var elList = YAHOO.util.Dom.getElementsByClassName("tlist", null, this.elBase)[0];

		var cchild= YAHOO.util.Dom.getElementsByClassName("l-col name first", null, elList, function(elTarget) {        	           

			var re=elTarget.innerHTML;
			for(var i=0;i<re.length;i++)
			{
				val=re.split(">");

			}
			if(val[1]!=null){

				var data={
						"name":val[1]
				};
			}
			dataSourceVehicleList.push(data);
		});

		var oDataSourceVehicleList=null;
		this.oDataSourceVehicleList = new $YU.LocalDataSource(dataSourceVehicleList);
		this.oDataSourceVehicleList.responseSchema = {
				fields: ["name"]
		};

	},
	/**
	 * Selects an item in the search with the given item name (object)
	 */
	selectItemVehicleList: function(objectName){
		/*Retaining the current scope*/
		var val=null;
		var result=new Array();
		var elList = YAHOO.util.Dom.getElementsByClassName("tlist",null,this.elBase)[0];

		var cchild= YAHOO.util.Dom.getElementsByClassName("l-row entity-record slist-item", null, elList, function(elTarget) {        	           
			YAHOO.util.Dom.getElementsByClassName("l-col name first", null, elTarget, function(el) { 

				var re=el.innerHTML;

				for(var i=0;i<re.length;i++){
					val=re.split(">");
				}

				if(val[1]!=objectName){
					YAHOO.util.Dom.addClass(elTarget,"yui-hidden");
				}else{
					if(YAHOO.util.Dom.hasClass(elTarget,"yui-hidden")){
						YAHOO.util.Dom.removeClass(elTarget,"yui-hidden");
					}		
				}
			}); 
		});
	},

	deleteVehicleListener:function(){
		this.searchInputVehicleList = $D.getElementsByClassName("search-item-input-vehiclelist",null,this.elBase)[0];
		var enterKeyListenerUserList = new $YU.KeyListener(this.searchInputVehicleList, {
			"keys": 13
		}, {
			"fn": function(){
			this.selectItemVehicleList(this.searchInputVehicleList.value);
		},
		"scope": this,
		"correctScope": true
		});
		enterKeyListenerUserList.enable();
		$U.addDefaultInputText(this.searchInputVehicleList,this.searchInputVehicleList.value);
		/**
		 * Avoiding possible memory leaks;
		 */
		enterKeyListenerUserList = null;
	},
	/**
	 * Setups for showing autocomplete
	 */
	setAutoCompleteHardwareList: function(){
		this._generateDataSourceHardwareList();
		var autoCompleteHardwareList = new $YW.AutoComplete(this.searchInputHardwareList, $D.getElementsByClassName("search-autocomplete-hardwarelist")[0], this.oDataSourceHardwareList);
		autoCompleteHardwareList.prehighlightClassName = "yui-ac-prehighlight";
		autoCompleteHardwareList.useShadow = true;
		this.deleteHardwareListener();
	},

	/**
	 * Generates Datasource for autocomplete
	 */
	_generateDataSourceHardwareList: function(){
		var dataSourceHardwareList = [];

		var val=null;
		var result=new Array();
		var elList = YAHOO.util.Dom.getElementsByClassName("tlist", null, this.elBase)[0];

		var cchild= YAHOO.util.Dom.getElementsByClassName("l-col imei first", null, elList, function(elTarget) {        	           

			var re=elTarget.innerHTML;
			for(var i=0;i<re.length;i++)
			{
				val=re.split(">");

			}
			if(val[1]!=null){

				var data={
						"name":val[1]
				};
			}
			dataSourceHardwareList.push(data);
		});


		var oDataSourceHardwareList=null;
		this.oDataSourceHardwareList = new $YU.LocalDataSource(dataSourceHardwareList);
		this.oDataSourceHardwareList.responseSchema = {
				fields: ["name"]
		};

	},
	/**
	 * Selects an item in the search with the given item name (object)
	 */
	selectItemHardwareList: function(objectName){
		/*Retaining the current scope*/
		var val=null;
		var result=new Array();
		var elList = YAHOO.util.Dom.getElementsByClassName("tlist")[0];

		var cchild= YAHOO.util.Dom.getElementsByClassName("l-row entity-record slist-item", null, elList, function(elTarget) {        	           
			YAHOO.util.Dom.getElementsByClassName("l-col imei first", null, elTarget, function(el) { 

				var re=el.innerHTML;
				for(var i=0;i<re.length;i++)
				{
					val=re.split(">");

				}
				if(val[1]!=objectName){

					YAHOO.util.Dom.addClass(elTarget,"yui-hidden");

				}else{

					if(YAHOO.util.Dom.hasClass(elTarget,"yui-hidden")){
						YAHOO.util.Dom.removeClass(elTarget,"yui-hidden");
					}		
				}
			}); 

		});
	},

	deleteHardwareListener: function(){
		this.searchInputHardwareList = $D.getElementsByClassName("search-item-input-hardwarelist",null,this.elBase)[0];
		var enterKeyListenerUserList = new $YU.KeyListener(this.searchInputHardwareList, {
			"keys": 13
		}, {
			"fn": function(){
			this.selectItemHardwareList(this.searchInputHardwareList.value);
		},
		"scope": this,
		"correctScope": true
		});
		enterKeyListenerUserList.enable();
		$U.addDefaultInputText(this.searchInputHardwareList,this.searchInputHardwareList.value);
		/**
		 * Avoiding possible memory leaks;
		 */
		enterKeyListenerUserList = null;

	},

	/**
	 * Setups for showing autocomplete
	 */
	setAutoCompleteDriverList: function(){
		this._generateDataSourceDriverList();
		var autocompleteDriver = new $YW.AutoComplete(this.searchInputDriverList, $D.getElementsByClassName("search-autocomplete-driverlist")[0], this.oDataSourceDriverList);
		autocompleteDriver.prehighlightClassName = "yui-ac-prehighlight";
		autocompleteDriver.useShadow = true;
		this.deleteDriverListener();


	},

	/**
	 * Generates Datasource for autocomplete
	 */
	_generateDataSourceDriverList: function(){
		var dataSourceDriverList = [];

		var val=null;
		var result=new Array();
		var elList = YAHOO.util.Dom.getElementsByClassName("tlist", null, this.elBase)[0];

		var cchild= YAHOO.util.Dom.getElementsByClassName("l-col firstname first", null, elList, function(elTarget) {        	           

			var re=elTarget.innerHTML;
			for(var i=0;i<re.length;i++){
				val=re.split(">");
			}
			if(val[1]!=null){
				var data={
						"name":val[1]
				};
			}
			dataSourceDriverList.push(data);
		});

		var oDataSourceDriverList=null;
		this.oDataSourceDriverList = new $YU.LocalDataSource(dataSourceDriverList);
		this.oDataSourceDriverList.responseSchema = {
				fields: ["name"]
		};

	},
	/**
	 * Selects an item in the search with the given item name (object)
	 */
	selectItemDriverList: function(objectName){
		/*Retaining the current scope*/

		var val=null;
		var result=new Array();
		var elList = YAHOO.util.Dom.getElementsByClassName("tlist",null,this.elBase)[0];

		var cchild= YAHOO.util.Dom.getElementsByClassName("l-row entity-record slist-item", null, elList, function(elTarget) {        	           
			YAHOO.util.Dom.getElementsByClassName("l-col firstname first", null, elTarget, function(el) { 

				var re=el.innerHTML;
				for(var i=0;i<re.length;i++){
					val=re.split(">");
				}

				if(val[1]!=objectName){
					YAHOO.util.Dom.addClass(elTarget,"yui-hidden");
				}else{
					if(YAHOO.util.Dom.hasClass(elTarget,"yui-hidden")){
						YAHOO.util.Dom.removeClass(elTarget,"yui-hidden");
					}		
				}
			}); 
		});
	},

	deleteDriverListener:function(){
		this.searchInputDriverList = $D.getElementsByClassName("search-item-input-driverlist",null,this.elBase)[0];
		var enterKeyListenerUserList = new $YU.KeyListener(this.searchInputDriverList, {
			"keys": 13
		}, {
			"fn": function(){
			this.selectItemDriverList(this.searchInputDriverList.value);
		},
		"scope": this,
		"correctScope": true
		});
		enterKeyListenerUserList.enable();
		$U.addDefaultInputText(this.searchInputDriverList,this.searchInputDriverList.value);
		/**
		 * Avoiding possible memory leaks;
		 */
		enterKeyListenerUserList = null;
	},
	/*Installing listeners*/
	/**
	 * The method that adds listeners to the basic elements like the delete button
	 *
	 * Established in this way so that the more specific widgets can install
	 * other additional listeners without disturbing the base listeners
	 */
	addBasicListeners: function() {
		/*Adding listener to the confirmation popup*/
		this.oConfirmation.subscribe($W.ConfirmationPopUp.EVT_ON_CONFIRMATION, this.onConfirm, null, this);
		this.oWarningpopup.subscribe($W.ConfirmationPopUp.EVT_ON_CONFIRMATION, this.onAbort, null, this);
		/*Adding the listeners to the records*/
		var elList = $D.getElementsByClassName(this.oConfiguration.listClass, null, this.elBase)[0];
		this.oListEventManager = new $U.DOMEventManager(elList, "click");
		this.oListEventManager.addBehavior(this.oConfiguration.recordClass, this.onEntitySelected, null, this);
		/*Adding listeners to the Save button*/
		$W.Buttons.addDefaultHandler($D.getElementsByClassName(this.oConfiguration.deleteButtonClass, null, this.elBase)[0], this.onDelete, null, this);
		$W.Buttons.addDefaultHandler($D.getElementsByClassName(this.oConfiguration.cancelButtonClass, null, this.elBase)[0], this.onCancel, null, this);
		/*
		 * Preventing possible memory leaks
		 */
		elList = null;
		this.searchInputVehicleList = $D.getElementsByClassName("search-item-input-vehiclelist",null,this.elBase)[0];

		this.searchInputHardwareList = $D.getElementsByClassName("search-item-input-hardwarelist",null,this.elBase)[0];

		this.searchInputDriverList = $D.getElementsByClassName("search-item-input-driverlist",null,this.elBase)[0];
	}
	});
})();
