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
	var response = true;
	/**
	 * Delete Vehicle UI Widget
	 * @author sabarish
	 */
	$W.DeleteVehicle = function(el, oArgs) {
		$W.DeleteVehicle.superclass.constructor.call(this, el, oArgs);
		this.addAdditionalListeners();

		this.setAutoCompleteVehicleList();
	};
	$L.extend($W.DeleteVehicle, $W.DeleteEntity, {
		/*Defining call backs*/
		oCallBacks: {
		oDeleteEntityCallBack: {
		success: function(o) {

		var responseData = o.responseText;
		var message = responseData.split(":");

		for(var i=0; i< message.length; i++){
			if(message[i] != 0){
				response = false;
				break;
			}
		}

		$D.getElementsByClassName("info", null, this.elBase)[0].innerHTML = "<span class='title'>Success:</span> The selected vehicles were successfully deleted.";
		this.setAutoCompleteVehicleList();
	},

	failure: function(o) {
		$D.getElementsByClassName("info", null, this.elBase)[0].innerHTML = "<span class='title'>Failure:</span>Deleteion of the selected vehicles failed.";
		this.setAutoCompleteVehicleList();
	}
	},
	oReloadListCallBack: {
		success: function(o) {
		/*Destroying behavior layers*/ 
		this.oListEventManager.destroy();
		this.elBase.innerHTML = o.responseText;
		/*Re-initializing*/
		$W.DeleteVehicle.superclass.constructor.call(this, this.elBase, this.oConfiguration);
		if(response == false){

			$D.getElementsByClassName("info", null, this.elBase)[0].innerHTML = "<span class='title'>Failure:Some of the selected vehicle/s are involved in trip, Hence deletion failed.</span>";
			response= true;
		}
		else{

			$D.getElementsByClassName("info", null, this.elBase)[0].innerHTML = "<span class='title'>Success:</span> The selected vehicle/s were successfully deleted.";
		}
		this.setAutoCompleteVehicleList();
	},
	failure: function(o) {

		$D.getElementsByClassName("info", null, this.elBase)[0].innerHTML = "<span class='title'>Failure:</span> Unable to get data from the server.Try reloading the page";
		this.setAutoCompleteVehicleList();
	}
	}
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

	addAdditionalListeners: function() {

		this.searchInputVehicleList = $D.getElementsByClassName("search-item-input-vehiclelist",null,this.elBase)[0];

	},
	/*Over riding necessary methods*/
	reloadList: function() {
		/*Correcting the scope of the call back*/
		this.oCallBacks.oReloadListCallBack.scope = this;
		var elList = $D.getElementsByClassName("tlist", null, this.elBase)[0];

		var cchild= $D.getElementsByClassName("delete-entity-checkbox", null, elList, function(elTarget) {
			if (elTarget.checked==true) {

				elList.deleteRow(elTarget);

			}


		});

		$U.Connect.asyncRequest('GET', "@APP_CONTEXT@/view/controlpanel/?markup=DeleteVehicle&body=true", this.oCallBacks.oReloadListCallBack);
	},
	deleteEntity: function(aKeys) {
		var sParams = "";
		sParams = aKeys.join(":");
		vehIds = aKeys;
		/*Hitting the server URL to delete the entities*/
		/*Correcting the scope of the call back*/
		this.oCallBacks.oDeleteEntityCallBack.scope = this;
		$U.Connect.asyncRequest('GET', "@APP_CONTEXT@/form/admin/?command_type=delete_vehicle&vehicleList=" + sParams, this.oCallBacks.oDeleteEntityCallBack, null);
	},
	refresh: function() {
		this.onCancel();
		$D.getElementsByClassName("info", null, this.elBase)[0].innerHTML = "<span class='title'>Please Note:</span> Before deleting a vehicle make sure that there is no active/paused trip for the selected vehicle";
	}
	});
})();
