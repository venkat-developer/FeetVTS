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
     * Assign Driver UI Widget
     * @author sabarish
     */
    $W.EditFuelCalibration = function(el, oArgs) {
        $W.EditFuelCalibration.superclass.constructor.call(this, el, oArgs);
        /*Hiding the assignment controls*/
       
        this.addAdditionalListeners();
        this._generateDataSourceUserList();
        this.setAutoCompleteUserList();
    };
    $L.extend($W.EditFuelCalibration, $W.Assignment, {
        /*Over riding necessary methods*/
        saveChanges: function(oInput) {
            /*Correcting the scope of the call back*/
            this.oSaveCallback.scope = this;
            var oRequestParams = {};
            oRequestParams.assignedDrivers = oInput.assignedEntities;
            oRequestParams.vacantDrivers = oInput.vacantEntities;
            oRequestParams.userID=oInput.userID; 
            
            /*
             * TODO: Change this to the coressponding URL once the data providers are implemented.
             */
            $U.Connect.asyncRequest('POST', "/fleet/form/admin/?command_type=assign_driver", this.oSaveCallback, $U.MapToParams(oRequestParams));
            /*
             * Preventing possible memory leaks
             */
            oRequestParams = null;
        }
    });
    $L.augmentObject($W.EditFuelCalibration.prototype, {
        /*Defining call backs*/
        oSaveCallback: {
            success: function(o) {
            },
            failure: function(o) {
            }
        },
        oDriverDataReceiptCallBack: {
            success: function(o) {
                var oResponse = JSON.parse(o.responseText);
                var elList;
                var sTemplate;
                if (!oResponse.adminmanage.drivermanage.Error) {
                    /*Reloading the Assigned List*/
                    elList = $D.getElementsByClassName(this.oConfiguration.assignedListClass, null, this.elBase)[0];
                    sTemplate = $D.getElementsByClassName("template-assigned-list-item", null, this.elBase)[0].innerHTML;
                    this.reloadList(elList, sTemplate, oResponse.adminmanage.drivermanage.driver.assigned, function(el1, el2) {
                        if (el1.firstname == el2.firstname) 
                            return 0;
                        else if (el1.firstname > el2.firstname) 
                            return 1;
                        else 
                            return -1;
                    });
                    /*Reloading the vacant list*/
                    elList = $D.getElementsByClassName(this.oConfiguration.vacantListClass, null, this.elBase)[0];
                    sTemplate = $D.getElementsByClassName("template-vacant-list-item", null, this.elBase)[0].innerHTML;
                    this.reloadList(elList, sTemplate, oResponse.adminmanage.drivermanage.driver.vacant, function(el1, el2) {
                        if (el1.firstname == el2.firstname) 
                            return 0;
                        else if (el1.firstname > el2.firstname) 
                            return 1;
                        else 
                            return -1;
                    });
                    /*Making the lists visible*/
                    this.showAssignmentControls();
                    this.countAssignmentLists();
                    this.showAutoCompleteForAssignDriver();
                    this.setState($W.Assignment.ASSIGNMENT_STATES.STATE_TWO);
                }
                else if (oResponse.adminmanage.drivermanage.Error.name == "ResourceNotFoundError") {
                    /*Reloading the user list*/
                    elList = $D.getElementsByClassName(this.oConfiguration.userListClass, null, this.elBase)[0];
                    sTemplate = $D.getElementsByClassName("template-source-list-item", null, this.elBase)[0].innerHTML;
                    this.reloadList(elList, sTemplate, oResponse.adminmanage.drivermanage.users, function(el1, el2) {
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
        setAutoCompleteUserList: function(){
            var autoCompleteUserList = new $YW.AutoComplete(this.searchInputUserList, $D.getElementsByClassName("search-autocomplete-userlist", null, this.elBase)[0], this.oDataSourceUserList);
            autoCompleteUserList.prehighlightClassName = "yui-ac-prehighlight";
            autoCompleteUserList.useShadow = true;
            this.assignDriverListener();
        },
        
        /**
         * Generates Datasource for autocomplete
         */
        _generateDataSourceUserList: function(){
        	  var dataSourceUserList = [];
            $D.getElementsByClassName("user-list-item", null, this.elBase, function(target){
                var itemID = target.getAttribute("value");
                var itemName = target.innerHTML;
                var data = {
                    "name": itemName,
                    "id": itemID
                };
               
                dataSourceUserList.push(data);
            });
            var oDataSourceUserList=[];
            this.oDataSourceUserList = new $YU.LocalDataSource(dataSourceUserList);
            this.oDataSourceUserList.responseSchema = {
                fields: ["name"]
            };
        },
        /**
         * Selects an item in the search with the given item name (object)
         */
        selectItemUserList: function(objectName){
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
        
        assignDriverListener: function(){
        	 
        	 this.searchInputUserList = $D.getElementsByClassName("search-item-input-userlist",null,this.elBase)[0];
            var enterKeyListenerUserList = new $YU.KeyListener(this.searchInputUserList, {
                "keys": 13
            }, {
                "fn": function(){
                    this.selectItemUserList(this.searchInputUserList.value);
                },
                "scope": this,
                "correctScope": true
            });
            enterKeyListenerUserList.enable();
            $U.addDefaultInputText(this.searchInputUserList,this.searchInputUserList.value);
            /**
             * Avoiding possible memory leaks;
             */
            enterKeyListenerUserList = null;
        },
        
        onUserSelected: function(oEvent, oArgs) {
            /*Correcting the scope of the callback*/
            this.oDriverDataReceiptCallBack.scope = this;
            var userID = $D.getAttribute(oArgs[1].target, "value");
            this.sUserID = userID;
            $U.Connect.asyncRequest("GET", "/fleet/view/controlpanel/?markup=AdminManage&debug=true&module=/blocks/json&data=view&subpage=driver&dataView=assignment&userID=" + userID, this.oDriverDataReceiptCallBack, null);
            /*
             * Preventing possible memory leaks
             */
            userID = null;
        },
        /*Method to add listeners for elements that are not already installed in $W.Assignment*/
        addAdditionalListeners: function() {
            var oListBehaviorLayer = new $U.DOMEventManager($D.getElementsByClassName("user-list", null, this.elBase), "click");
            oListBehaviorLayer.addBehavior("list-item", this.onUserSelected, null, this);
            oListBehaviorLayer.addBehavior("list-item", function(oEvent, oArgs) {
                var elList = $D.getAncestorByClassName(oArgs[1].target, "user-list");
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
            
            this.searchInputUserList = $D.getElementsByClassName("search-item-input-userlist",null,this.elBase)[0];
           
        }
    });
})();
