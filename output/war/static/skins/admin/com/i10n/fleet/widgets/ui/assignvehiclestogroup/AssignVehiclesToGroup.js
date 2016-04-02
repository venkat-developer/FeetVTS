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
     * Assign Group UI Widget
     * @author dharma
     */
    $W.AssignVehiclesToGroup = function(el, oArgs) {
        $W.AssignVehiclesToGroup.superclass.constructor.call(this, el, oArgs);
        /*Hiding the assignment controls*/
        this.hideAssignmentControls();
        this.addAdditionalListeners();
        this._generateDataSourceGroups();
        this.setAutoCompleteGroups();
    };
    $L.extend($W.AssignVehiclesToGroup, $W.Assignment, {
        /*Over riding necessary methods*/
        saveChanges: function(oInput) {
            /*Correcting the scope of the call back*/
            this.oSaveCallback.scope = this;
            var oRequestParams = {};
            oRequestParams.assignedVehicles = oInput.assignedEntities;
            oRequestParams.vacantVehicles = oInput.vacantEntities;
           oRequestParams.groupId = oInput.groupID;
            /*
             * TODO: Change this to the coressponding URL once the data providers are implemented.
             */
            $U.Connect.asyncRequest('POST', "/fleet/form/admin/?command_type=assign_group", this.oSaveCallback, $U.MapToParams(oRequestParams));
          
            /*
             * Preventing possible memory leaks
             */
            oRequestParams = null;
        }
    });
    $L.augmentObject($W.AssignVehiclesToGroup.prototype, {
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
                if (!oResponse.adminmanage.groupmanage.Error) {
                    /*Reloading the Assigned List*/
                    elList = $D.getElementsByClassName(this.oConfiguration.assignedListClass, null, this.elBase)[0];
                    sTemplate = $D.getElementsByClassName("template-assigned-list-item", null, this.elBase)[0].innerHTML;
                    this.reloadList(elList, sTemplate, oResponse.adminmanage.groupmanage.groupvehicles.assigned, function(el1, el2) {
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
                    this.reloadList(elList, sTemplate, oResponse.adminmanage.groupmanage.groupvehicles.vacant, function(el1, el2) {
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
                    this.showAutoCompleteForGroups();
                    this.setState($W.Assignment.ASSIGNMENT_STATES.STATE_TWO);
                }
                else if (oResponse.adminmanage.groupmanage.Error.name == "ResourceNotFoundError") {
                    /*Reloading the group list*/
                    elList = $D.getElementsByClassName(this.oConfiguration.groupListClass, null, this.elBase)[0];
                    sTemplate = $D.getElementsByClassName("template-source-list-item", null, this.elBase)[0].innerHTML;
                    this.reloadList(elList, sTemplate, oResponse.adminmanage.groupmanage.groups, function(el1, el2) {
                        if (el1.firstname == el2.firstname) 
                            return 0;
                        else if (el1.firstname > el2.firstname) 
                            return 1;
                        else 
                            return -1;
                    });
                    this.countGroupList();
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
        setAutoCompleteGroups: function(){
            var autoCompleteGroups = new $YW.AutoComplete(this.searchInputGroups, $D.getElementsByClassName("search-autocomplete-groups", null, this.elBase)[0], this.oDataSourceGroups);
            autoCompleteGroups.prehighlightClassName = "yui-ac-prehighlight";
            autoCompleteGroups.useShadow = true;
            this.GroupsListener();
        },
        
        /**
         * Generates Datasource for autocomplete
         */
        _generateDataSourceGroups: function(){
        	  var dataSourceGroups = [];
            $D.getElementsByClassName("group-sel-item", null, this.elBase, function(target){
                var itemID = target.getAttribute("value");
                var itemName = target.innerHTML;
                var data = {
                    "name": itemName,
                    "id": itemID
                };
               
                dataSourceGroups.push(data);
            });
            var oDataSourceGroups=[];
            this.oDataSourceGroups = new $YU.LocalDataSource(dataSourceGroups);
            this.oDataSourceGroups.responseSchema = {
                fields: ["name"]
            };
        },
        /**
         * Selects an item in the search with the given item name (object)
         */
        selectItemGroups: function(objectName){
            /*Retaining the current scope*/
            var originalScope = this;
           $D.getElementsByClassName("group-sel-item", null, this.elBase, function(target){
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
        
        GroupsListener: function(){
        	 
        	 this.searchInputGroups = $D.getElementsByClassName("search-item-input-groups",null,this.elBase)[0];
            var enterKeyListenerGroups = new $YU.KeyListener(this.searchInputGroups, {
                "keys": 13
            }, {
                "fn": function(){
                    this.selectItemGroups(this.searchInputGroups.value);
                },
                "scope": this,
                "correctScope": true
            });
            enterKeyListenerGroups.enable();
            $U.addDefaultInputText(this.searchInputGroups,this.searchInputGroups.value);
            /**
             * Avoiding possible memory leaks;
             */
            enterKeyListenerGroups = null;
        },
        
        onGroupSelected: function(oEvent, oArgs) {
            /*Correcting the scope of the callback*/
            this.oVehicleDataReceiptCallBack.scope = this;
            var groupID = $D.getAttribute(oArgs[1].target, "value");
            this.sGroupID = groupID;
            $U.Connect.asyncRequest("GET", "/fleet/view/controlpanel/?markup=AdminManage&debug=true&module=/blocks/json&data=view&subpage=group&dataView=assignment&groupID=" + groupID, this.oVehicleDataReceiptCallBack, null);
            /*
             * Preventing possible memory leaks
             */
            groupID = null;
        },
        /*Method to add listeners for elements that are not already installed in $W.Assignment*/
        addAdditionalListeners: function() {
            var oListBehaviorLayer = new $U.DOMEventManager($D.getElementsByClassName("group-sel", null, this.elBase), "click");
            oListBehaviorLayer.addBehavior("list-item", this.onGroupSelected, null, this);
            oListBehaviorLayer.addBehavior("list-item", function(oEvent, oArgs) {
                var elList = $D.getAncestorByClassName(oArgs[1].target, "group-sel");
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
            this.searchInputGroups = $D.getElementsByClassName("search-item-input-groups",null,this.elBase)[0];
        }
    });
})();
