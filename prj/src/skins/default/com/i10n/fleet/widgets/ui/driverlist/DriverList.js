(function(){
    var $W = getPackageForName("com.i10n.fleet.widget.ui");
    var $D = YAHOO.util.Dom;
    var $L = YAHOO.lang;
    var $E = YAHOO.util.Event;
    var $YW = YAHOO.widget;
    $W.DriverList = function(el, params){
        this.elDriverListContainer = el;
        this.initParams = params;
        $W.DriverList.superclass.constructor.call(this, el, {
            "datasource": params.datasource,
            "config": {
                "groupTitleKey": "name",
                "elementKey": "drivers",
                "elementTitleKey": "name",
                "statusKey": "status",
                "listTitle": "Drivers",
                "elementId": "id"
            },
            "fields": [{
                "key": "firstname",
                "title": "First Name"
            }, {
                "key": "lastname",
                "title": "Last Name"
            }, {
                "key": "license",
                "title": "License No."
            }],
            "navId": "driverlist"
        });
        /**
         * Creating and Passing a custom Request to DataSource
         * @param {Object} sQuery
         */
        var driverlist = this;
        this.oAutoComplete.generateRequest = function(sQuery){
            return "group=" + driverlist.get($W.SidePaneList.ATTR_SELECTED_GROUP) + "&search_key=id&" + "id=" + sQuery;
        };
    };
    $L.augmentObject($W.DriverList, {
        ATTR_SELECTED_DRIVER: "selectedDriver",
        EVT_SELECTED_DRIVER_CHANGE: "selectedDriverChange"
    });
    /*
     * DriverList extends SidePaneList
     */
    $L.extend($W.DriverList, $W.SidePaneList, {
        configureAttributes: function(){
            /**
             * @attribute selectedVehicle
             * @description Current User Vehicle Selected
             * @type String
             */
            this.setAttributeConfig($W.DriverList.ATTR_SELECTED_DRIVER, {
                value: null,
                validator: function(value){
                    return ($L.isNull(value) || $L.isString(value));
                }
            });
            $W.DriverList.superclass.configureAttributes.call(this);
        },
        
        addEventListeners: function(){
            $E.addListener($D.getElementsByClassName('item-name', null, this.elDriverListContainer), "click", function(oArgs, oSelf){
                var elParent = $D.getAncestorByClassName($E.getTarget(oArgs), "item");
                this.set($W.DriverList.ATTR_SELECTED_DRIVER, elParent.getAttribute("item"));
            }, this, true);
            $W.DriverList.superclass.addEventListeners.call(this);
            this.subscribe($W.DriverList.EVT_SELECTED_DRIVER_CHANGE, function(oArgs){
                $D.getElementsByClassName("yui-cms-item", null, this.elDriverListContainer, function(oNode){
                    if (oNode.getAttribute("item") === oArgs.newValue) {
                        if (!$D.hasClass(oNode, "selected")) {
                            var elToggler = $D.getElementsByClassName("accordionToggleItem", null, oNode)[0];
                            $YW.AccordionManager.toggle(elToggler);
                        }
                    }
                });
                oArgs = null;
            }, this, true);
        },
        
        /**
         * Over-rided function to select an driver
         * @param {Object} objectName
         */
        selectItem: function(objectName){
        	var obj=objectName.split(" ");
        	var length=obj.length;
            $D.getElementsByClassName('item', null, this.elSideListBase, function(target){
                if (obj[length-1] === target.getAttribute("item")) {
                    if (!$D.hasClass(target, "selected")) {
                        var toggleElement = $D.getElementsByClassName("accordionToggleItem", null, target);
                        if ($L.isArray(toggleElement) && toggleElement.length > 0) {
                            $YW.AccordionManager.toggle(toggleElement[0]);
                            var sItemId = target.getAttribute("item");
                            this.set($W.DriverList.ATTR_SELECTED_DRIVER, sItemId);
                        }
                    }
                }
            }, this, true);
        }
    });
})();
