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
     * Assign Vehicle UI Widget
     * @author sabarish
     */
    $W.AssignVehicle = function(el, oArgs) {
    	
        $W.AssignVehicle.superclass.constructor.call(this, el, oArgs);
        /*Hiding the assignment controls*/
        
        this.hideAssignmentControls();
        this.addAdditionalListeners();
        this._generateDataSourceUser();
        this.setAutoCompleteUser();
        
    };
    $L.extend($W.AssignVehicle, $W.Assignment, {
        /*Over riding necessary methods*/
        saveChanges: function(oInput) {
            /*Correcting the scope of the call back*/
            this.oSaveCallback.scope = this;
            var oRequestParams = {};
            oRequestParams.assignedVehicles = oInput.assignedEntities;
            oRequestParams.vacantVehicles = oInput.vacantEntities;
           oRequestParams.userId = oInput.userID;
            /*
             * TODO: Change this to the coressponding URL once the data providers are implemented.
             */
            $U.Connect.asyncRequest('POST', "@APP_CONTEXT@/form/admin/?command_type=assign_vehicle", this.oSaveCallback, $U.MapToParams(oRequestParams));
            /*
             * Preventing possible memory leaks
             */
            oRequestParams = null;
        }
    });
    $L.augmentObject($W.AssignVehicle.prototype, {
        /*Defining call backs*/
        oSaveCallback: {
            success: function(o) {
            },
            failure: function(o) {
            }
        },
        oVehicleDataReceiptCallBack: {
            success: function(o) {
        	
                var oResponse = JSON.parse(o.responseText);
                var elList;
                var sTemplate;
                if (!oResponse.adminmanage.vehiclemanage.Error) {
                    /*Reloading the Assigned List*/
                    elList = $D.getElementsByClassName(this.oConfiguration.assignedListClass, null, this.elBase)[0];
                    sTemplate = $D.getElementsByClassName("template-assigned-list-item", null, this.elBase)[0].innerHTML;
                    this.reloadList(elList, sTemplate, oResponse.adminmanage.vehiclemanage.vehicle.assigned, function(el1, el2) {
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
                    this.reloadList(elList, sTemplate, oResponse.adminmanage.vehiclemanage.vehicle.vacant, function(el1, el2) {
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
                    this.showAutocomplete();
                    this.setState($W.Assignment.ASSIGNMENT_STATES.STATE_TWO);
                }
                else if (oResponse.adminmanage.vehiclemanage.Error.name == "ResourceNotFoundError") {
                    /*Reloading the user list*/
                    elList = $D.getElementsByClassName(this.oConfiguration.userListClass, null, this.elBase)[0];
                    sTemplate = $D.getElementsByClassName("template-source-list-item", null, this.elBase)[0].innerHTML;
                    this.reloadList(elList, sTemplate, oResponse.adminmanage.vehiclemanage.users, function(el1, el2) {
                        if (el1.firstname == el2.firstname) 
                            return 0;
                        else if (el1.firstname > el2.firstname) 
                            return 1;
                        else 
                            return -1;
                    });
                    this.countUserList();
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
        setAutoCompleteUser: function(){
            var autoCompleteUser = new $YW.AutoComplete(this.searchInputUser, $D.getElementsByClassName("search-autocomplete-user", null, this.elBase)[0], this.oDataSourceUser);
            autoCompleteUser.prehighlightClassName = "yui-ac-prehighlight";
            autoCompleteUser.useShadow = true;
            this.assignVehicleListener();
        },
        
        /**
         * Generates Datasource for autocomplete
         */
        _generateDataSourceUser: function(){
        	  var dataSourceUser = [];
            $D.getElementsByClassName("user-list-item", null, this.elBase, function(target){
                var itemID = target.getAttribute("value");
                var itemName = target.innerHTML;
                var data = {
                    "name": itemName,
                    "id": itemID
                };
               
                dataSourceUser.push(data);
            });
            var oDataSourceUser=[];
            this.oDataSourceUser = new $YU.LocalDataSource(dataSourceUser);
            this.oDataSourceUser.responseSchema = {
                fields: ["name"]
            };
        },
        /**
         * Selects an item (user) in the sidebar with the given item name (object)
         */
        selectItemUser: function(objectName){
            /*Retaining the current scope*/
            var originalScope = this;
           $D.getElementsByClassName("user-list-item", null, this.elBase, function(target){
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
        
        assignVehicleListener: function(){

        	this.searchInputUser = $D.getElementsByClassName("search-item-input-user",null,this.elBase)[0];
            var enterKeyListenerUser = new $YU.KeyListener(this.searchInputUser, {
                "keys": 13
            }, {
                "fn": function(){
                    this.selectItemUser(this.searchInputUser.value);
                },
                "scope": this,
                "correctScope": true
            });
            enterKeyListenerUser.enable();
            $U.addDefaultInputText(this.searchInputUser, this.searchInputUser.value);
            /**
             * Avoiding possible memory leaks;
             */
            enterKeyListenerUser = null;
        },
        
        onUserSelected: function(oEvent, oArgs) {
            /*Correcting the scope of the callback*/
            this.oVehicleDataReceiptCallBack.scope = this;
            var userID = $D.getAttribute(oArgs[1].target, "value");
            this.sUserID = userID;
            $U.Connect.asyncRequest("GET", "@APP_CONTEXT@/view/controlpanel/?markup=AdminManage&debug=true&module=/blocks/json"+
            		"&data=view&subpage=vehicle&dataView=assignment&userID=" + userID, this.oVehicleDataReceiptCallBack, null);
            /*
             * Preventing possible memory leaks
             */
            userID = null;
            
        },
        /*Method to add listeners for elements that are not already installed in $W.Assignment*/
        addAdditionalListeners: function() {
            var oListBehaviorLayer = new $U.DOMEventManager($D.getElementsByClassName("user-sel", null, this.elBase), "click");
            oListBehaviorLayer.addBehavior("list-item", this.onUserSelected, null, this);
            oListBehaviorLayer.addBehavior("list-item", function(oEvent, oArgs) {
                var elList = $D.getAncestorByClassName(oArgs[1].target, "user-sel");
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

            this.searchInputUser = $D.getElementsByClassName("search-item-input-user",null,this.elBase)[0];

        }
    });
})();
