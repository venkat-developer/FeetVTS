(function(){
	var $W = getPackageForName("com.i10n.fleet.widget.ui");
	var $D = YAHOO.util.Dom;
	var $L = YAHOO.lang;
	var $E = YAHOO.util.Event;
	var $B = YAHOO.Bubbling;
	var $YW = YAHOO.widget;
	var livevehicle = null;
	var $V = getPackageForName("com.i10n.fleet.widget.view");
	var oTarget = ({});
	var isSHSMClient=false;
	var isFRSClient=false;
	var isPushLetAlive=true;

	$W.VehicleList = function(el, params){

		this.elVehicleListContainer = el;
		this.elGroupListContainer = el;
		this.initParams = params;

		this.pinningEnabled = false;

		livevehicle=this;
		$V.BaseView.subscribeFn("livetrack",this.callback);

		if(isFRSClient){
			$W.VehicleList.superclass.constructor.call(this, el, {
				"datasource": params.datasource,
				"config": {
					"groupTitleKey": "name",
					"elementKey": "vehicles",
					"elementTitleKey": "name",
					"statusKey": "status",
					"elementId": "id"
				},
				"fields": [{
					"key": "name",
					"title": "Name"
				}, {
					"key": "baselocation",
					"title": "BaseLocation"
				}, {
					"key": "model",
					"title": "Model"
				}, {
					"key": "crewno",
					"title": "MobileNumber"
				}],
				"navId": "vehiclelist"
			});
		}

		else if(isSHSMClient){
			$W.VehicleList.superclass.constructor.call(this, el, {
				"datasource": params.datasource,
				"config": {
					"groupTitleKey": "name",
					"elementKey": "vehicles",
					"elementTitleKey": "name",
					"statusKey": "status",
					"elementId": "id"
				},
				"fields": [{
					"key": "name",
					"title": "Name"
				}, {
					"key": "make",
					"title": "Make"
				}, {
					"key": "model",
					"title": "Model"
				}, {
					"key": "drivername",
					"title": "Driver"
				}, {
					"key": "mobilenumber",
					"title": "Mobile Number"
				}],
				"navId": "vehiclelist"
			});
		} else{
			$W.VehicleList.superclass.constructor.call(this, el, {
				"datasource": params.datasource,
				"config": {
					"groupTitleKey": "name",
					"elementKey": "vehicles",
					"elementTitleKey": "name",
					"statusKey": "status",
					"elementId": "id"
				},
				"fields": [{
					"key": "name",
					"title": "Name"
				}, {
					"key": "make",
					"title": "Make"
				}, {
					"key": "model",
					"title": "Model"
				}, {
					"key": "drivername",
					"title": "Driver"
				}],
				"navId": "vehiclelist"
			});
		}
		/**
		 * Creating and Passing a custom Request to DataSource
		 * @param {Object} sQuery
		 */
		var vehiclelist = this;
		this.oAutoComplete.generateRequest = function(sQuery){
			return "group=" + vehiclelist.get($W.SidePaneList.ATTR_SELECTED_GROUP) + "&search_key=id&" + "id=" + sQuery;
		};
	};

	$L.augmentObject($W.VehicleList, {
		ATTR_SELECTED_VEHICLE: "selectedVehicle",
		ATTR_SELECTED_GROUP: "selectedGroup",
		EVT_SELECTED_VEHICLE_CHANGE: "selectedVehicleChange",
		EVT_SELECTED_GROUP_CHANGE: "selectedGroupChange"
	});

	/*
	 * VehicleList extends SidePaneList
	 */
	$L.extend($W.VehicleList, $W.SidePaneList, {
		/**
		 * Overrides SuperClass configureAttributes to configure personal attributes
		 */
		configureAttributes: function(){
			/**
			 * @attribute selectedVehicle
			 * @description Current User Vehicle Selected
			 * @type String
			 */
			this.setAttributeConfig($W.VehicleList.ATTR_SELECTED_VEHICLE, {
				value: null,
				validator: function(value){
					return ($L.isNull(value) || $L.isString(value));
				}
			});

			/**
			 * @attribute selectedGroup
			 * @description Current User Group Selected
			 * @type String
			 */
			this.setAttributeConfig($W.VehicleList.ATTR_SELECTED_GROUP, {
				value: null,
				validator: function(value){
					return ($L.isNull(value) || $L.isString(value));
				}
			});

			this.setAttributeConfig($W.VehicleList.EVT_TOGGLE, {
				value: null
			});
			$W.VehicleList.superclass.configureAttributes.call(this);
		},

		addEventListeners: function(){
			$E.addListener($D.getElementsByClassName('item-name', null, this.elVehicleListContainer), "click", function(oArgs, oSelf){
				var elParent = $D.getAncestorByClassName($E.getTarget(oArgs), "item");
				var sItemId = elParent.getAttribute("item");
				this.set($W.VehicleList.ATTR_SELECTED_VEHICLE, sItemId);
				elParent = null;
				sItemId = null;
			}, this, true);


			$W.VehicleList.superclass.addEventListeners.call(this);
			/**
			 * TODO : Critical : To be optimized.
			 * @param {Object} oArgs
			 */
		},

		callback :{
			success: function(liveUpdateData){
			if(isPushLetAlive){
				livevehicle.liveupdate(liveUpdateData);
			}else{
				// Use this if its not pushlet update
				livevehicle.timerLiveUpdate(liveUpdateData);
			}
			}
		},

		_addPin: function(elTarget) {
			$D.addClass(elTarget, "pin-enabled");
			return 1;
		},

		_removePin: function(elTarget) {
			$D.removeClass(elTarget, "pin-enabled");
			return 0;
		},

		togglePin: function(target) {
			if ($L.isObject(target)) {
				/* if($D.hasClass(target, "pin-enabled")){ 
				 this._removePin(target);
			 } */
				var pin = $D.hasClass(target, "pin-enabled")? this._removePin(target) : this._addPin(target);
				if (this.pinningEnabled) {
					this.sort();
					if (pin) {
						/*	$W.SidePaneList.scrollTo("pinned", [, {
						"selected": false,
						"el": target
					}]); */
					} 
				}
			}
		}, 

		timerLiveUpdate:function(result){
			var oResponse=JSON.parse(result.responseText);
			var cont = oResponse.demomanage.livedata.liveData;

			for(var i in cont){
				var oVehicle = cont[i];
				var status = oVehicle.status;
				/*
				if(status == 1){
					status = "online";
				}else if(status == 2){
					status = "idle";
				}else if(status == 3){
					status = "offline";
				}else {
					status = "stopped";
				} 
				*/
//				var displayName = oVehicle.vehicleDisplayname;
				var displayName=oVehicle.make;
				var itemStatus =YAHOO.util.Dom.getElementsByClassName("item-status");
				var item =YAHOO.util.Dom.getElementsByClassName("item-name");

				for(var i=0;i<item.length;i++){
					var test=item[i].innerHTML;
					var testarray;
					testarray=test.split("[");
					var div=itemStatus[i];
					if(testarray[0].match(displayName)){
						// Moves the online vehicle up in the side pane list
						/*if(status=="online" || status == "moving") {
						this.togglePin($D.getAncestorByClassName(item[i], "item"));
					}*/
						div.setAttribute("class","item-status inline-block item-status "+status);
						break;
					}else{
						continue;
					}
				}
			}
		},

		liveupdate:function(result){
			result = eval('('+result+')');
			var cont = result.content;

			for(var i in cont){
				var oVehicle = cont[i];
				var status = oVehicle.status;
				/*
				if(status == 1){
					status = "online";
				}else if(status == 2){
					status = "idle";
				}else if(status == 3){
					status = "offline";
				}else {
					status = "stopped";
				} 
				*/
				var displayName=oVehicle.vehicleDisplayname;
				var itemStatus =YAHOO.util.Dom.getElementsByClassName("item-status");
				var item =YAHOO.util.Dom.getElementsByClassName("item-name");

				for(var i=0;i<item.length;i++){
					var test=item[i].innerHTML;
					var testarray;
					testarray=test.split("[");
					var div=itemStatus[i];
					if(testarray[0].match(displayName)){
						// Moves the online vehicle up in the side pane list
						/*if(status=="online" || status == "moving") {
						this.togglePin($D.getAncestorByClassName(item[i], "item"));
						}*/
						div.setAttribute("class","item-status inline-block item-status "+status);
						break;
					}else{
						continue;
					}
				}
			}
		},

		/**
		 * Overrided function to select a vehicle
		 * @param {Object} objectName
		 */
		selectItem: function(objectName){
			var obj=objectName.split(" ");
			var length=obj.length;
			$D.getElementsByClassName("item", null, this.elSideListBase, function(target){
				if (obj[length-1] === target.getAttribute("item")) {
					if (!$D.hasClass(target, "selected")) {
						var toggleElement = $D.getElementsByClassName("accordionToggleItem", null, target);
						if ($L.isArray(toggleElement) && toggleElement.length > 0) {
							$YW.AccordionManager.toggle(toggleElement[0]);
							var sItemId = target.getAttribute("item");
							this.set($W.VehicleList.ATTR_SELECTED_VEHICLE, sItemId);
						}
					}
				}
			}, this, true);
		}      
	});
})();