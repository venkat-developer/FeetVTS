var SELECTED_STOP_VALUE = 0;

(function() {
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
    var grp=null;
    
    /**
     * Widget for showing sidepanelist (vehiclelist/driverlist) as a resizable left pane.
     * Uses YUI Accordion Control,YUI Autocomplete
     *
     * @author sabarish
     */
    $W.ETASidePaneList = function(el, oArgs) {
        /**
         * Datasource for autocomplete
         */
        this.dataSource = null;
        /**
         * Base Container for the widget
         */
        this.elSideListBase = el;
        /**
         * Base Container of the list
         */
        this.listBaseElement = $D.getElementsByClassName("sidepane-list", null, el)[0];
        /**
         * InitParams supplied
         */
        this.initParams = oArgs;
        /**
         * Represents Grouping behavior of the sidepanelist
         */
        this.isGrouped = true;
        /**
         * An instance of sortable list for sorting
         */
        this.sortableList = new $WU.SortableList(this.elSideListBase);
        /**
         * Represents Pinning behavior of the sidepanelist
         */
        this.pinningEnabled = false;
        this.initSidePanelList(this.elSideListBase, oArgs);
    };
    
    /**
     * Adding Default Static Comparators
     */
    $L.augmentObject($W.SidePaneList, {
        /**
         *
         */
        ATTR_SELECTED_GROUP: "selectedGroup",
        /**
         * Default Search String
         */
        DEFAULT_SEARCH_STRING: "Search...",
        /**
         *
         * @param {Object} layer
         * @param {Object} args
         */
        scrollTo: function(layer, args) {
            if (args[1].selected === false) {
                var oNode = args[1].el;
                var ancestorNode = $D.getAncestorByClassName(oNode, "sidepanelist");
                if (ancestorNode) {
                    var elAccordion = $D.getElementsByClassName("yui-cms-accordion", null, ancestorNode)[0];
                    var oNodeOffsetHeight = oNode.offsetHeight;
                    var nContainerHeight = $D.getRegion(elAccordion).height;
                    var accordionElTop = $D.getY(elAccordion);
                    var oNodePos = $D.getY(oNode) - accordionElTop;
                    if (oNodePos > nContainerHeight) {
                        elAccordion.scrollTop = elAccordion.scrollTop + oNodePos;
                    }
                    else if (!(oNodePos > oNodeOffsetHeight && oNodePos < nContainerHeight)) {
                        if (oNodePos < 0) {
                            elAccordion.scrollTop = elAccordion.scrollTop - (-1 * oNodePos);
                        }
                        else {
                            elAccordion.scrollTop = elAccordion.scrollTop - oNodePos;
                        }
                    }
                }
            }
        },
        /**
         * Comparator to sorting sidepanelist on basis of 1. Pin 2. ObjectName (vehicle/driver name)
         * @param {Object} el1
         * @param {Object} el2
         */
        listPinComparator: function(el1, el2) {
            var isEl1Pinned = $D.hasClass(el1, "pin-enabled");
            var isEl2Pinned = $D.hasClass(el2, "pin-enabled");
            var result = 0;
            if (isEl1Pinned === isEl2Pinned) {
                result = $W.SidePaneList.listComparator(el1, el2);
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
         * Comparator to sorting objects on the basis of 1. Group 2. Pin 3. ObjectName
         * @param {Object} el1
         * @param {Object} el2
         */
        groupPinComparator: function(el1, el2) {
            var group1 = el1.getAttribute("group");
            var group2 = el2.getAttribute("group");
            var result = 0;
            if ($L.isString(group1) && $L.isString(group2)) {
                if (group1 === group2) {
                    var isGroup1Head = $D.hasClass(el1, "group-item");
                    var isGroup2Head = $D.hasClass(el2, "group-item");
                    if (isGroup1Head && isGroup2Head) {
                        result = 0;
                    }
                    else if (isGroup1Head) {
                        result = -1;
                    }
                    else if (isGroup2Head) {
                        result = 1;
                    }
                    else if ($D.hasClass(el1, "item") &&
                    $D.hasClass(el2, "item")) {
                        result = $W.SidePaneList.listPinComparator(el1, el2);
                    }
                }
                else if (group1 > group2) {
                    result = 1;
                }
                else {
                    result = -1;
                }
            }
            else if ($L.isString(group1)) {
                result = 1;
            }
            else if ($L.isString(group2)) {
                result = -1;
            }
            return result;
        },
        /**
         * Comparator to sorting objects on the basis of 1. Group 2. Object Name
         * @param {Object} el1
         * @param {Object} el2
         */
        groupComparator: function(el1, el2) {
            var group1 = el1.getAttribute("group");
            var group2 = el2.getAttribute("group");
            var result = 0;
            if ($L.isString(group1) && $L.isString(group2)) {
                if (group1 === group2) {
                    var isGroup1Head = $D.hasClass(el1, "group-item");
                    var isGroup2Head = $D.hasClass(el2, "group-item");
                    if (isGroup1Head && isGroup2Head) {
                        result = 0;
                    }
                    else if (isGroup1Head) {
                        result = -1;
                    }
                    else if (isGroup2Head) {
                        result = 1;
                    }
                    else if ($D.hasClass(el1, "item") &&
                    $D.hasClass(el2, "item")) {
                        result = $W.SidePaneList.listComparator(el1, el2);
                    }
                }
                else if (group1 > group2) {
                    result = 1;
                }
                else {
                    result = -1;
                }
            }
            else if ($L.isString(group1)) {
                result = 1;
            }
            else if ($L.isString(group2)) {
                result = -1;
            }
            return result;
        },
        /**
         * Comparator to sorting the sidepane list on the basis of just the object Name
         * @param {Object} el1
         * @param {Object} el2
         */
        listComparator: function(el1, el2) {
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
            object1 = null;
            object2 = null;
            return result;
        }
    });
    $L.extend($W.SidePaneList, $W.MinimizableList, {
        configureAttributes: function() {
            this.setAttributeConfig($W.SidePaneList.ATTR_SELECTED_GROUP, {
                value: "All"
            });
            $W.SidePaneList.superclass.configureAttributes.call(this);
        }
    });
    $L.augmentObject($W.SidePaneList.prototype, {
        /**
         * Initializes the widget
         */
        initSidePanelList: function(baseEl, oArgs) {
            var el = $D.getElementsByClassName("sidepane-list", null, baseEl)[0];
            $W.SidePaneList.superclass.constructor.call(this, baseEl, oArgs.navId);
            if ($D.hasClass(el, "group-disabled")) {
                this.isGrouped = false;
            }
            else {
                this.isGrouped = true;
            }
            if ($D.hasClass(el, "pinning-disabled")) {
                this.pinningEnabled = false;
            }
            else {
                this.pinningEnabled = true;
            }
            this.oDataSource = oArgs.datasource;
            this.oDataSrc=oArgs.datasource;
            this.grp=this.oDataSrc.getGroupDataForAutocomplete();
            this.processTemplateData();
            this.configureAttributes();
            this.addEventListeners();
            this.setAutoComplete();
            this.sortByGroup();
            this.activate();
            this._resizePane();
            if (_instances && _instances.view && _instances.view.onPageResize) {
                _instances.view.onPageResize.subscribe(this._resizePane, this, true);
            }
            this.subscribe($W.MinimizableList.EVT_RESIZE, this._resizePane, this, true);
        },
        /**
         * Populates the Group Data
         * @param {Object} groupTitle
         * @param {Object} grpid
         */
        processGroupData: function(groupTitle, grpid) {
            var elGrpTemplate = $D.getElementsByClassName("markup-group-template", null, this.elSideListBase)[0];
            var sMarkupTemplate = $U.processTemplate(elGrpTemplate.innerHTML, {
                "groupTitle": groupTitle,
                "groupId": grpid
            });
            var selectTemplate = $U.processTemplate('<option class="group-item" value="{groupId}">{groupTitle}</option>', {
                "groupTitle": groupTitle,
                "groupId": grpid
            });
            return {
                template: sMarkupTemplate,
                select: selectTemplate
            };
        },
        /**
         * Populates the Item Data
         * @param {Object} oContent
         * @param {Object} obj
         * @param {Object} groupTitle
         * @param {Object} grpid
         */
        processItemData: function(oContent, obj, groupTitle, grpid) {
            var sMarkupTemplate = "", itemName, itemMake, itemBody = "", itemKey, itemStatus;
            var keys = this.initParams.fields;
            var oData = obj;
            for (var itemId in oData) {
                itemBody = "";
                itemName = oData[itemId][this.initParams.config.elementTitleKey];
                itemStatus = oData[itemId][this.initParams.config.statusKey];
                itemKey = oData[itemId][this.initParams.config.elementId];
                for (var keyId in keys) {
                    itemBody = itemBody + "<li>" + keys[keyId].title + "  :" + oData[itemId][keys[keyId].key] + "</li>";
                }
                sMarkupTemplate = sMarkupTemplate +
                $U.processTemplate(oContent, {
                    "groupTitle": groupTitle,
                    "itemName": itemName,
                    "groupId": grpid,
                    "itemStatus": itemStatus,
                    "itemKey": itemKey,
                    "itemBody": itemBody
                });
            }
            return sMarkupTemplate;
        },
        /**
         * Populates the Data
         */
        processTemplateData: function() {
            var groupTitle;
            var sMarkupTemplate = "", selectTemplate = '<select class="item-group-select"><option class="group-item" value="All" selected>All</option>';
            var elItemTemplate = $D.getElementsByClassName("markup-item-template", null, this.elSideListBase)[0];
            var elGrpSelect = $D.getElementsByClassName("select-group-container", null, this.elSideListBase)[0];
            var elCloneable = $D.getElementsByClassName("data-accordion-container", null, this.elSideListBase)[0];
            var oData = this.oDataSource.getGroupData();
            var config = this.initParams.config;
            for (var grpid in oData) {
                for (var attr in oData[grpid]) {
                    if (attr == config.groupTitleKey) {
                        groupTitle = oData[grpid][attr];
                        var result = this.processGroupData(groupTitle, grpid);
                        sMarkupTemplate = sMarkupTemplate + result.template;
                        selectTemplate = selectTemplate + result.select;
                    }
                    else if (attr == config.elementKey && oData[grpid][attr] != null) {
                        sMarkupTemplate = sMarkupTemplate + this.processItemData(elItemTemplate.innerHTML, oData[grpid][attr], groupTitle, grpid);
                    }
                }
            }
            selectTemplate = selectTemplate + "</select>";
            elGrpSelect.innerHTML = selectTemplate;
            elCloneable.innerHTML = sMarkupTemplate;
        },
        getStopName : function(stop){
        this.stopId = stop;
        },
        /**
         * Adds listeners for various events in the widget
         */
        addEventListeners: function() {
            var oListener = new $U.DOMEventHandler(this.listBaseElement, {
                type: "click"
            });
            var changeListener = new $U.DOMEventHandler(this.listBaseElement, {
                type: "change"
            });
            changeListener.addListener($D.getElementsByClassName('item-group-select', null, this.listBaseElement), function(params) {
                var oTarget = $D.getElementsByClassName('item-group-select', null, this.listBaseElement)[0];
                var group = oTarget[oTarget.selectedIndex].value;
                this.set($W.SidePaneList.ATTR_SELECTED_GROUP, group);
                this.selectGroup(group);
            }, null, this);
            oListener.addListener($D.getElementsByClassName('list-group', null, this.listBaseElement), function(params) {
                this.toggleGrouping();
            }, null, this);
            oListener.addListener($D.getElementsByClassName('pin', null, this.listBaseElement), function(params) {
                var oTarget = $E.getTarget(params);
                if ($L.isObject(oTarget)) {
                    this.togglePin($D.getAncestorByClassName(oTarget, "item"));
                }
            }, null, this);
            oListener.addListener($D.getElementsByClassName('list-pin', null, this.listBaseElement), function(params) {
                this.togglePinning();
            }, null, this);
            oListener.addListener($D.getElementsByClassName('group-openclose', null, this.listBaseElement), function(params) {
                var oTarget = $E.getTarget(params);
                var group = $D.getAncestorByClassName(oTarget, "group-item");
                this.toggleGroupDisplay(group);
            }, null, this);
            oListener.addListener($D.getElementsByClassName('item-name', null, this.listBaseElement), function(params) {
                var elTarget = $E.getTarget(params);
                var parent = $D.getAncestorByClassName(elTarget, "item");
                if (!$D.hasClass(parent, "selected")) {
                	SELECTED_STOP_VALUE = parent.getAttribute("item");
                    var toggleElement = $D.getElementsByClassName("accordionToggleItem", null, parent);
                    if ($L.isArray(toggleElement) &&
                    toggleElement.length > 0) {
                        $YW.AccordionManager.toggle(toggleElement[0]);
                    }
                }
                $E.stopEvent(params);
            }, this, true);
            oListener.addListener($D.getElementsByClassName('search-go-button', null, this.listBaseElement), function(params) {

                
            	$D.getElementsByClassName("item", null, this.elSideListBase, function(target) {
		 	this.searchInput = $D.getElementsByClassName("search-item-input", null, this.listBaseElement)[0];


                if (this.searchInput.value == target.getAttribute("itemname")) {

                    if (!$D.hasClass(target, "selected")) {

                        var toggleElement = $D.getElementsByClassName("accordionToggleItem", null, target);
                        if ($L.isArray(toggleElement) &&
                        toggleElement.length > 0) {

                            $YW.AccordionManager.toggle(toggleElement[0]);
                        }
                    }
                }
            });



            }, null, this);
            this.searchInput = $D.getElementsByClassName("search-item-input", null, this.listBaseElement)[0];
            
            var enterKeyListener = new $YU.KeyListener(this.searchInput, {
                "keys": 13
            }, {
                "fn": function() {
                    this.selectItem(this.searchInput.value);
                },
                "scope": this,
                "correctScope": true
            });
            enterKeyListener.enable();
            $U.addDefaultInputText(this.searchInput, this.searchInput.value);
            /**
             * Avoiding possible memory leaks;
             */
            enterKeyListener = null;
            oListener = null;
            changeListener = null;
        },
        /**
         * Selects an item (vehicle/driver) in the SidePaneList with the given item name (object)
         */
        selectItem: function(objectName) {
            $D.getElementsByClassName("item", null, this.elSideListBase, function(target) {
                if (objectName === target.getAttribute("item")) {
                    if (!$D.hasClass(target, "selected")) {
                        var toggleElement = $D.getElementsByClassName("accordionToggleItem", null, target);
                        if ($L.isArray(toggleElement) &&
                        toggleElement.length > 0) {
                            $YW.AccordionManager.toggle(toggleElement[0]);
                        }
                    }
                }
            });
        },
        /**
         * A listener function on toggling display behavior of groups
         */
        toggleGroupDisplay: function(groupElement) {
            var listItems = $D.getElementsByClassName('list-item-type', null, this.elSideListBase);
            var len = listItems.length;
            var groupStart = false;
            var groupToBeShown = false;
            for (var i = 0; i < len; i++) {
                var listItem = listItems[i];
                if (!groupStart) {
                    if (listItem === groupElement) {
                        groupStart = true;
                        if ($D.hasClass(listItem, "closed")) {
                            groupToBeShown = true;
                            $D.removeClass(listItem, "closed");
                        }
                        else {
                            $D.addClass(listItem, "closed");
                        }
                    }
                }
                else {
                    if ($D.hasClass(listItem, "group-item")) {
                        break;
                    }
                    else {
                        if (groupToBeShown) {
                            $D.removeClass(listItem, "disabled");
                        }
                        else {
                            $D.addClass(listItem, "disabled");
                        }
                    }
                }
            }
        },
        /**
         * Activates the widget
         */
        activate: function() {
            $D.removeClass(this.elSideListBase, "inactive");
        },
        generateDataSourceForAutoComplete: function(oData){
        	var dataSrc=[];
        	var data=null;
        	var data=$U.Arrays.mapToArray(oData);
        	
 	            for(var i=0;i<data.length;i++){
 	            	var object=data[i];
 	            	
 	            	var dat=$U.Arrays.mapToArray(object); 
 	            	
 	            	for(var j=0;j<dat.length;j++){
 	            		
 	            		var obj=$U.Arrays.mapToArray(dat[j]); 
 	            		
 	            		for(var k=0;k<obj.length;k++){
 	            	      var veh=obj[k];
 	            	      
 	            	 this.data = { 	                       
 	                        "id":veh.name
 	                    };

 	                    dataSrc.push(this.data);
	YAHOO.util.Dom.getElementsByClassName("count inline-block", null, this.elSideListBase)[0].innerHTML="("+dataSrc.length+")";	
 	            	}
 	            }
 	            }
 	            
 	           this.oDataSrc = new $YU.LocalDataSource(dataSrc);
 	            this.oDataSrc.responseSchema = {
 	                fields: ["id"]
 	            };
 	           
 	           return this.oDataSrc;
        },
        /**
         * Setups for showing autocomplete
         */
        setAutoComplete: function() {

        	var dtsrc=this.generateDataSourceForAutoComplete(this.grp);
            var autoComplete = new $YW.AutoComplete(this.searchInput, $D.getElementsByClassName("search-autocomplete", null, this.elSideListBase)[0], this.oDataSrc);
            autoComplete.prehighlightClassName = "yui-ac-prehighlight";
            autoComplete.useShadow = true;
            this.oAutoComplete = autoComplete;
        },
        /**
         * Shows the group selected from group select box
         */
        selectGroup: function(group) {
            if ($L.isString(group) && group !== 'All') {
                $D.getElementsByClassName("list-item-type", null, this.listBaseElement, function(target) {
                    $D.addClass(target, "disabled");
                });
                var filterFunction = function(target) {
                    var result = false;
                    if (group === target.getAttribute("group")) {
                        result = true;
                    }
                    return result;
                };
                $D.getElementsBy(filterFunction, null, this.listBaseElement, function(target) {
                    $D.removeClass(target, "disabled");
                    $D.removeClass(target, "closed");
                });
            }
            else {
                $D.getElementsByClassName("list-item-type", null, this.listBaseElement, function(target) {
                    $D.removeClass(target, "disabled");
                    $D.removeClass(target, "closed");
                });
            }
            this.sortByGroup();
        },
        /**
         * Sorts the SidePaneList
         */
        sort: function() {
            if (this.isGrouped) {
                this.sortByGroup();
            }
            else {
                this.sortByItem();
            }
        },
        /**
         * Sorts the SidePaneList with Pinning/Disabled Pinning based on
         * ObjectNames and giving no priority to Groups
         */
        sortByItem: function() {
            if (this.pinningEnabled) {
                this.sortableList.sort($D.getElementsByClassName("item", null, this.listBaseElement), $W.SidePaneList.listPinComparator);
            }
            else {
                this.sortableList.sort($D.getElementsByClassName("item", null, this.listBaseElement), $W.SidePaneList.listComparator);
            }
        },
        /**
         * Sorts the SidePaneList with Pinning/Disabled Pinning based on
         * Groupnames and then with the ObjectNames
         */
        sortByGroup: function() {
            if (this.pinningEnabled) {
                this.sortableList.sort($D.getElementsByClassName("list-item-type", null, this.listBaseElement), $W.SidePaneList.groupPinComparator);
            }
            else {
                this.sortableList.sort($D.getElementsByClassName("list-item-type", null, this.listBaseElement), $W.SidePaneList.groupComparator);
            }
        },
        /**
         * Toggles pinning on an object (vehicle/driver)
         */
        togglePin: function(target) {
            if ($L.isObject(target)) {
                var pin = $D.hasClass(target, "pin-enabled") ? this._removePin(target) : this._addPin(target);
                if (this.pinningEnabled) {
                    this.sort();
                    if (pin) {
                        $W.SidePaneList.scrollTo("pinned", [, {
                            "selected": false,
                            "el": target
                        }]);
                    }
                }
            }
        },
        /**
         * Attaches the pin to an object
         */
        _addPin: function(elTarget) {
            $D.addClass(elTarget, "pin-enabled");
            return 1;
        },
        /**
         * Removes pin attached from an object
         */
        _removePin: function(elTarget) {
            $D.removeClass(elTarget, "pin-enabled");
            return 0;
        },
        /**
         * Toggling Pinning behavior
         */
        togglePinning: function() {
            var pin = this.pinningEnabled ? this.disablePinning() : this.enablePinning();
        },
        /**
         * Disables Pinning Behavior
         */
        disablePinning: function() {
            if (this.pinningEnabled) {
                this.pinningEnabled = false;
                $D.addClass(this.listBaseElement, "pinning-disabled");
                this.sort();
            }
        },
        /**
         * Enables Pinning Behavior
         */
        enablePinning: function() {
            if (!this.pinningEnabled) {
                this.pinningEnabled = true;
                $D.removeClass(this.listBaseElement, "pinning-disabled");
                this.sort();
            }
        },
        /**
         * Toggles Grouping Behavior of the objects
         */
        toggleGrouping: function() {
            this.isGrouped ? this.disableGrouping() : this.enableGrouping();
        },
        /**
         * Disables Grouping of objects
         */
        disableGrouping: function() {
            if (this.isGrouped) {
                this.isGrouped = false;
                this.selectGroup();
                $D.addClass(this.listBaseElement, "group-disabled");
                this.sortByItem();
            }
        },
        /**
         * Enables Grouping of objects
         */
        enableGrouping: function() {
            if (!this.isGrouped) {
                this.isGrouped = true;
                $D.removeClass(this.listBaseElement, "group-disabled");
                var elTarget = $D.getElementsByClassName("item-group-select", null, this.elSideListBase)[0];
                var group = elTarget[elTarget.selectedIndex].value;
                this.selectGroup(group);
            }
        },
        _resizePane: function() {
            var elAccordion = $D.getElementsByClassName("yui-cms-accordion", null, this.elSideListBase)[0];
            var oContainerRegion = $D.getRegion(this.elSideListBase);
            var nContainerHeight = oContainerRegion.height;
            var oAccordionRegion = $D.getRegion(elAccordion);
            var nHeight = (oContainerRegion.height + oContainerRegion.top - oAccordionRegion.top);
            if (nHeight >= 0) {
                $D.setStyle(elAccordion, 'height', (oContainerRegion.height + oContainerRegion.top - oAccordionRegion.top) + "px");
            }
        }
    });
    $W.SidePaneList.initAccordionOpenItem = function() {
        /**
         * Subscribing To Accordion Open Complete Evt
         */
        $B.on('accordionOpenItem', $W.SidePaneList.scrollTo);
    };
    $W.SidePaneList.initAccordionOpenItem();
})();
