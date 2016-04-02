(function(){
    var $W = getPackageForName("com.i10n.fleet.widget.ui");
    var $D = YAHOO.util.Dom;
    var $L = YAHOO.lang;
    var $E = YAHOO.util.Event;
    var $YW = YAHOO.widget;
    var $WU = getPackageForName("com.i10n.fleet.widget.util");
    var $U = getPackageForName("com.i10n.fleet.Utils");
    var $YU = YAHOO.util;
    
   
    
    /*
     * TODO: Refactor this widget after the sidepanelist is refactored
     */
    $W.GeoFencingSideBar = function(el, oParams){
        /**
         * The datasoruce element that will be used
         * for the following purposes
         *
         * 1. autocomplete
         */
        this.oDataSource = null;
        /**
         * The container of this widget
         */
        this.elTsSideBarContainer = $D.getElementsByClassName("geofencingsidebar", null, el)[0];
        /**
         * Base Container of the list
         */
        this.elListBaseElement = $D.getElementsByClassName("geofencing-sidebar", null, el)[0];
        /**
         * The container of data
         */
        this.oDataContainer = $D.getElementsByClassName("data-container", null, el)[0];
        /**
         * The parameters supplied
         */
      //  this.oInitParams = oParams;
        /**
         * An instance of sortable list for sorting
         */
        this.oSortableList = new $WU.SortableList(this.elTsSideBarContainer);
        /**
         * Represents Pinning behavior of the sidebar
         */
        this.pinningEnabled = false;
        /*Initializing*/
        this.initGeoFencingSideBar(this.elTsSideBarContainer, oParams);       
        
    };
    
    $L.augmentObject($W.GeoFencingSideBar, {
        /**
         * The String that defines the name of the Attribute that is provided
         */
        ATTR_SELECTED_ITEM: "oSelectedTrip",
        EVT_SELECTED_ITEM_CHANGE: "oSelectedTripChange",
        /**
         * The trip statues
         */
        TRIP_STATUS: {
            STARTED_STATUS: "started",
            PAUSED_STATUS: "paused",
            STOPPED_STATUS: "stopped"
        },
        /**
         * Default Search String
         */
        DEFAULT_SEARCH_STRING: "Search...",
        /**
         * Comparator to sort the sidebar on basis of 1. Pin 2. ObjectName (tripname)
         * @param {Object} el1
         * @param {Object} el2
         */
        listPinComparator: function(el1, el2){
            var isEl1Pinned = $D.hasClass(el1, "pin-enabled");
            var isEl2Pinned = $D.hasClass(el2, "pin-enabled");
            var result = 0;
            if (isEl1Pinned === isEl2Pinned) {
                result = $W.GeoFencingSideBar.listComparator(el1, el2);
            }
            else if (isEl1Pinned) {
                result = -1;
            }
            else if (isEl2Pinned) {
                result = 1;
            }
            return result;
        },
        /**
         * Comparator to sort the sidebar on the basis of just the object Name
         * @param {Object} el1
         * @param {Object} el2
         */
        listComparator: function(el1, el2){
            var object1 = el1.getAttribute("item");
            var object2 = el2.getAttribute("item");
            var result = 0;
            if ($L.isString(object1) && $L.isString(object2)) {
                if (object1 === object2) {
                    result = 0;
                }
                else if (object1 > object2) {
                    result = 1;
                }
                else {
                    result = -1;
                }
            }
            else if ($L.isString(object1)) {
                result = 1;
            }
            else if ($L.isString(object2)) {
                result = -1;
            }
            return result;
        }
    });
    
    /*
     * GeoFencingSideBar extends Minimizable List
     */
    $L.extend($W.GeoFencingSideBar, $W.MinimizableList);
    $L.augmentProto($W.GeoFencingSideBar, $YU.AttributeProvider);
    $L.augmentObject($W.GeoFencingSideBar.prototype, {
        /**
         * The Initialization Function
         */
        initGeoFencingSideBar: function(baseEl, oArgs){
            /*Needed for using the Attribute provider*/
            this.setAttributeConfig($W.GeoFencingSideBar.ATTR_SELECTED_ITEM, {
                value: null
            });
            var el = $D.getElementsByClassName("geofencing-sidebar", null, baseEl)[0];
            $W.GeoFencingSideBar.superclass.constructor.call(this, baseEl, oArgs);
            if ($D.hasClass(el, "pinning-disabled")) {
                this.pinningEnabled = false;
            }
            else {
                this.pinningEnabled = true;
            }
            this.eventManager = new $U.DOMEventHandler(this.elTsSideBarContainer, {
                type: "click"
            });
            this.addEventListeners();
            this._generateDataSource();
            this.setAutoComplete();
            this.elTemplateTripItem = $D.getElementsByClassName("list-item-type", null, $D.get("templateTripItem"))[0];
            this.activate();
        },
        /*Declaring the data members*/
        oSelectedTrip: null,
        elTemplateTripItem: null,
        /*Defining the Member Methods*/
        /**
         * Set the status of a given element to one that is specified
         * @param {Object} objectName identifier of the object whose status should be changed
         * @param {Object} status the status to whcih to set the object to
         */
        setStatus: function(objectID, status){
            /*Retaining the current scope*/
            var originalScope = this;
            $D.getElementsByClassName('item', null, this.elTsSideBarContainer, function(target){
                if (objectID === target.getAttribute("item")) {
                    /*Change the Selected trip attribute value*/
                    var statusDiv = $D.getElementsByClassName("item-status", null, target)[0];
                    originalScope.removeStatusClasses(statusDiv);
                    $D.addClass(statusDiv, status);
                    statusDiv = null;
                }
            });
            /*Avioding possible memory leaks*/
            originalScope = null;
            objectID = null;
            status = null;
        },
        
        /**
         * Selects an item (trip) in the sidebar with the given item name (object)
         */
        selectItem: function(objectName, selectByID){
            /*Retaining the current scope*/
            var originalScope = this;
            $D.getElementsByClassName('item', null, this.elTsSideBarContainer, function(target){
                var key;
                if (selectByID) {
                    key = target.getAttribute("item");
                }
                else {
                    key = target.getAttribute("itemname");
                }
                if (objectName === key) {
                    /*Change the Selected trip attribute value*/
                    originalScope.set($W.GeoFencingSideBar.ATTR_SELECTED_ITEM, target);
                    originalScope.toggleSelected(target);
                }
            });
            /*Avioding possible memory leaks*/
            objectName = null;
            selectByID = null;
            originalScope = null;
        },
        
        /**
         * Activates the widget
         */
        activate: function(){
            $D.removeClass(this.elListBaseElement, "inactive");
        },
        /**
         * Setups for showing autocomplete
         */
        setAutoComplete: function(){
            var autoComplete = new $YW.AutoComplete(this.searchInput, $D.getElementsByClassName("search-autocomplete", null, this.elTsSideBarContainer)[0], this.oDataSource);
            autoComplete.prehighlightClassName = "yui-ac-prehighlight";
            autoComplete.useShadow = true;
        },
        /**
         * Method that adds a new trip to the list
         * @param {Object} trip {
         *     id: 
         *     name:
         *     status:
         * }
         */
        addToList: function(region){
        	 var el = $D.getElementsByClassName("geofencing-sidebar");
        	 for(var i=0;i<el.length;i++){
        	  if ($D.hasClass(el[i], "pinning-disabled")) {
                  $D.removeClass(el[i],"pinning-disabled");
              }
        	  }
        	  
        	  
            var newListItem = $U.processTemplate(this.elTemplateTripItem.innerHTML, {
                id: region["id"],
                name: region["name"]
            });
            var listChildElement = document.createElement("div");
            $D.setAttribute(listChildElement, "item", region["id"]);
            $D.setAttribute(listChildElement, "itemname", region["name"]);
            $D.getElementsByClassName("slist-item list-item-type item selected", null, this.elTsSideBarContainer, function(target){
            	$D.removeClass(target,"selected");
            });
            $D.addClass(listChildElement, "slist-item list-item-type item selected");
            listChildElement.innerHTML = newListItem;
            this.oDataContainer.appendChild(listChildElement);
            /*Performing necessary initialization*/
//            this.reloadSideBar();
         },
             	 
        /**
         * Reloads only the necessary elements. Mark up is not reloaded
         */
        reloadSideBar: function(){
            this.removeSideBarEventListeners();
            this.addEventListeners();
            this._generateDataSource();
            this.setAutoComplete();
            this.activate();
            this.sort();
        },
        
        /*Defining the event listeners*/
        addEventListeners: function(){
        
            this.eventManager.addListener($D.getElementsByClassName('pin', null, this.elTsSideBarContainer), function(params){
                var oTarget = $E.getTarget(params);
                if ($L.isObject(oTarget)) {
                    this.togglePin($D.getAncestorByClassName(oTarget, "item"));
                }
            }, null, this);
            
            this.eventManager.addListener($D.getElementsByClassName('list-pin', null, this.elTsSideBarContainer), function(params){
                this.togglePinning();
            }, null, this);
            
            this.eventManager.addListener($D.getElementsByClassName('item-name', null, this.elTsSideBarContainer), function(params){
                var target = $E.getTarget(params);
                var parent = $D.getAncestorByClassName(params, "item");
                /*Toggle the selected item*/
                this.selectItem(target.innerHTML);
                /*TODO:refactor the following content*/
                $E.stopEvent(params);
            }, null, this);
            this.searchInput = $D.getElementsByClassName("search-item-input", null, this.elTsSideBarContainer)[0];
            $U.addDefaultInputText(this.searchInput, $W.GeoFencingSideBar.DEFAULT_SEARCH_STRING);
            this.eventManager.addListener($D.getElementsByClassName('search-go-button', null, this.elTsSideBarContainer), function(params){
                this.selectItem(this.searchInput.value);
            }, null, this);
           
            var enterKeyListener = new $YU.KeyListener(this.searchInput, {
                "keys": 13
            }, {
                "fn": function(){
                    this.selectItem(this.searchInput.value);
                },
                "scope": this,
                "correctScope": true
            });
            enterKeyListener.enable();
            /**
             * Avoiding possible memory leaks;
             */
            enterKeyListener = null;
        },
        
        /*Defining the Utility Functions*/
        removeSideBarEventListeners: function(){
            var eventSources = $D.getElementsByClassName("event-source", null, this.elTsSideBarContainer);
            $E.purgeElement(eventSources, true);
        },
        /**
         * Helper function to remove all the status classes from a given target
         * @param {Object} target the element on which to apply the function
         */
        removeStatusClasses: function(target){
            for (var status in $W.GeoFencingSideBar.TRIP_STATUS) {
                $D.removeClass(target, $W.GeoFencingSideBar.TRIP_STATUS[status]);
            }
        },
        /**
         * Method for toggling the selected item
         */
        toggleSelected: function(newlySelectedItem){
            $D.getElementsByClassName('item', null, this.elTsSideBarContainer, function(target){
                /*Deselecting the previously selected item*/
                if ($D.hasClass(target, "selected")) 
                    $D.removeClass(target, "selected");
                else if (newlySelectedItem === target) {
                    $D.addClass(target, "selected");
                }
            });
        },
        /**
         * Generates Datasource for autocomplete
         */
        _generateDataSource: function(){
            var dataSource = [];
            $D.getElementsByClassName("item", null, this.elTsSideBarContainer, function(target){
                var itemID = target.getAttribute("item");
                var itemName = target.getAttribute("itemname");
                var data = {
                    "name": itemName,
                    "id": itemID
                };
                dataSource.push(data);
            });
            this.oDataSource = new $YU.LocalDataSource(dataSource);
            this.oDataSource.responseSchema = {
                fields: ["name"]
            };
        },
        /**
         * Sorts the SideBar Pinning/Disabled Pinning based on
         * ObjectNames
         */
        sort: function(){
            if (this.pinningEnabled) {
                this.oSortableList.sort($D.getElementsByClassName("item", null, this.oDataContainer), $W.GeoFencingSideBar.listPinComparator);
            }
            else {
                this.oSortableList.sort($D.getElementsByClassName("item", null, this.oDataContainer), $W.GeoFencingSideBar.listComparator);
            }
        },
        /**
         * Toggles pinning on an object (trip)
         */
        togglePin: function(target){
            if ($L.isObject(target)) {
                var pin = $D.hasClass(target, "pin-enabled") ? this._removePin(target) : this._addPin(target);
                if (this.pinningEnabled) {
                    this.sort();
                }
            }
        },
        /**
         * Attaches the pin to an object
         */
        _addPin: function(elTarget){
            $D.addClass(elTarget, "pin-enabled");
        },
        /**
         * Removes pin attached from an object
         */
        _removePin: function(elTarget){
            $D.removeClass(elTarget, "pin-enabled");
        },
        /**
         * Toggling Pinning behavior
         */
        togglePinning: function(){
            var pin = this.pinningEnabled ? this.disablePinning() : this.enablePinning();
        },
        /**
         * Disables Pinning Behavior
         */
        disablePinning: function(){
            if (this.pinningEnabled) {
                this.pinningEnabled = false;
                $D.addClass(this.elListBaseElement, "pinning-disabled");
                this.sort();
            }
        },
        /**
         * Enables Pinning Behavior
         */
        enablePinning: function(){ 
            if (!this.pinningEnabled) {
                this.pinningEnabled = true;
                $D.removeClass(this.elListBaseElement, "pinning-disabled");
                this.sort();
            }
        },
        /**
         * The Function to resize the window
         */
        _resizePane: function(){
            var elAccordion = $D.getElementsByClassName("data-container", null, this.elSideListBase)[0];
            var docHeight = $D.getDocumentHeight();
            var oContainerRegion = $D.getRegion(this.elSideListBase);
            var nContainerHeight = oContainerRegion.height;
            var oAccordionRegion = $D.getRegion(elAccordion);
            var nHeight = (docHeight - oAccordionRegion.top);
            if (nHeight >= 0) {
                $D.setStyle(elAccordion, 'height', nHeight + "px");
            }
        }
       /* configureAttributes: function(){
            /**
             * @attribute selectedVehicle
             * @description Current User Vehicle Selected
             * @type String
             */
          /*  this.setAttributeConfig($W.GeoFencingSideBar.ATTR_SELECTED_ITEM, {
                value: null,
                validator: function(value){
                    return ($L.isNull(value) || $L.isString(value));
                }
            });
            $W.GeoFencingSideBar.superclass.configureAttributes.call(this);
        },
        selectItem: function(objectName){
            $D.getElementsByClassName('item', null, this.elSideListBase, function(target){
                if (objectName === target.getAttribute("item")) {
                    if (!$D.hasClass(target, "selected")) {
                        var toggleElement = $D.getElementsByClassName("accordionToggleItem", null, target);
                        if ($L.isArray(toggleElement) && toggleElement.length > 0) {
                            $YW.AccordionManager.toggle(toggleElement[0]);
                            var sItemId = target.getAttribute("item");
                            this.set($W.GeoFencingSideBar.ATTR_SELECTED_ITEM, sItemId);
                        }
                    }
                }
            }, this, true);
        }*/
    });
    
})();
