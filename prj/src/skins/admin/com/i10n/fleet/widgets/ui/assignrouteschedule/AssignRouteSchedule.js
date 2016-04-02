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
    /**
     * Assign RouteSchedule UI Widget
     * @author Gobi
     */
    $W.AssignRouteSchedule = function(el, oArgs) {
    	
        $W.AssignRouteSchedule.superclass.constructor.call(this, el, oArgs);
        /*Hiding the assignment controls*/
        
        this.hideAssignmentControls();
        this.addAdditionalListeners();
        this._generateDataSourceVehicle();
        this.setAutoCompleteVehicle();
        
    };
    $L.extend($W.AssignRouteSchedule, $W.Assignment, {
        /*Over riding necessary methods*/
        saveChanges: function(oInput) {
            /*Correcting the scope of the call back*/
            this.oSaveCallback.scope = this;
            var oRequestParams = {};
            oRequestParams.assignedRouteSchedules = oInput.assignedEntities;
            oRequestParams.vacantRouteSchedules = oInput.vacantEntities;
           oRequestParams.vehicleId = oInput.userID;
            /*
             * TODO: Change this to the coressponding URL once the data providers are implemented.
             */
            $U.Connect.asyncRequest('POST', "/fleet/form/admin/?command_type=assign_routeschedule", this.oSaveCallback, $U.MapToParams(oRequestParams));
            /*
             * Preventing possible memory leaks
             */
            oRequestParams = null;
        }
    });
    $L.augmentObject($W.AssignRouteSchedule.prototype, {
        /*Defining call backs*/
        oSaveCallback: {
            success: function(o) {
            },
            failure: function(o) {
            }
        },
        oRouteScheduleDataReceiptCallBack: {
            success: function(o) {
        	
                var oResponse = JSON.parse(o.responseText);
                var elList;
                var sTemplate;
                if (!oResponse.adminmanage.routeschedulemanage.Error) {
                    /*Reloading the Assigned List*/
                    elList = $D.getElementsByClassName(this.oConfiguration.assignedListClass, null, this.elBase)[0];
                    sTemplate = $D.getElementsByClassName("template-assigned-list-item", null, this.elBase)[0].innerHTML;
                    this.reloadList(elList, sTemplate, oResponse.adminmanage.routeschedulemanage.routeSchedules.assigned, function(el1, el2) {
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
                    this.reloadList(elList, sTemplate, oResponse.adminmanage.routeschedulemanage.routeSchedules.vacant, function(el1, el2) {
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
                    this.showAutocompleteForRouteSchedule();
                    this.setState($W.Assignment.ASSIGNMENT_STATES.STATE_TWO);
                }
                else if (oResponse.adminmanage.routeschedulemanage.Error.name == "ResourceNotFoundError") {
                    /*Reloading the vehicle list*/
                    elList = $D.getElementsByClassName(this.oConfiguration.vehicleListClass, null, this.elBase)[0];
                    sTemplate = $D.getElementsByClassName("template-source-list-item", null, this.elBase)[0].innerHTML;
                    this.reloadList(elList, sTemplate, oResponse.adminmanage.routeschedulemanage.vehicles, function(el1, el2) {
                        if (el1.firstname == el2.firstname) 
                            return 0;
                        else if (el1.firstname > el2.firstname) 
                            return 1;
                        else 
                            return -1;
                    });
                    this.countVehicleList();
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
         * Default Search String
         */
        DEFAULT_SEARCH_STRING: "Search...",
        
        /**
         * Setups for showing autocomplete
         */
        setAutoCompleteVehicle: function(){
            var autoCompleteVehicle = new $YW.AutoComplete(this.searchInputVehicle, $D.getElementsByClassName("search-autocomplete-vehicle", null, this.elBase)[0], this.oDataSourceVehicle);
            autoCompleteVehicle.prehighlightClassName = "yui-ac-prehighlight";
            autoCompleteVehicle.useShadow = true;
            this.assignRouteScheduleListener();
        },
        
        /**
         * Generates Datasource for autocomplete
         */
        _generateDataSourceVehicle: function(){
        	  var dataSourceVehicle = [];
            $D.getElementsByClassName("vehicle-list-item", null, this.elBase, function(target){
                var itemID = target.getAttribute("value");
                var itemName = target.innerHTML;
                var data = {
                    "name": itemName,
                    "id": itemID
                };
               
                dataSourceVehicle.push(data);
            });
            var oDataSourceVehicle=[];
            this.oDataSourceVehicle = new $YU.LocalDataSource(dataSourceVehicle);
            this.oDataSourceVehicle.responseSchema = {
                fields: ["name"]
            };
        },
        /**
         * Selects an item (vehicle) in the sidebar with the given item name (object)
         */
        selectItemVehicle: function(objectName){
            /*Retaining the current scope*/
            var originalScope = this;
           $D.getElementsByClassName("vehicle-list-item", null, this.elBase, function(target){
                var key;

                    key = target.innerHTML;
                  
               // }
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
        
        assignRouteScheduleListener: function(){
        	this.searchInputVehicle = $D.getElementsByClassName("search-item-input-vehicle",null,this.elBase)[0];
            var enterKeyListenerVehicle = new $YU.KeyListener(this.searchInputVehicle, {
                "keys": 13
            }, {
                "fn": function(){
                    this.selectItemVehicle(this.searchInputVehicle.value);
                },
                "scope": this,
                "correctScope": true
            });
            enterKeyListenerVehicle.enable();
            $U.addDefaultInputText(this.searchInputVehicle, this.searchInputVehicle.value);
            /**
             * Avoiding possible memory leaks;
             */
            enterKeyListenerVehicle = null;
        },
        
        onVehicleSelected: function(oEvent, oArgs) {
            /*Correcting the scope of the callback*/
            this.oRouteScheduleDataReceiptCallBack.scope = this;
            var vehicleID = $D.getAttribute(oArgs[1].target, "value");
            this.sVehicleID = vehicleID;
            $U.Connect.asyncRequest("GET", "/fleet/view/controlpanel/?markup=AdminManage&debug=true&module=/blocks/json&data=view&subpage=routeschedule&dataView=assignment&vehicleID=" + vehicleID, this.oRouteScheduleDataReceiptCallBack, null);
            /*
             * Preventing possible memory leaks
             */
            vehicleID = null;
            
        },
        /*Method to add listeners for elements that are not already installed in $W.Assignment*/
        addAdditionalListeners: function() {
            var oListBehaviorLayer = new $U.DOMEventManager($D.getElementsByClassName("vehicle-sel", null, this.elBase), "click");
            oListBehaviorLayer.addBehavior("list-item", this.onVehicleSelected, null, this);
            oListBehaviorLayer.addBehavior("list-item", function(oEvent, oArgs) {
                var elList = $D.getAncestorByClassName(oArgs[1].target, "vehicle-sel");
                $D.getElementsByClassName("list-item", null, elList, function(elTarget) {
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

            this.searchInputVehicle = $D.getElementsByClassName("search-item-input-vehicle",null,this.elBase)[0];

        }
    });
})();
