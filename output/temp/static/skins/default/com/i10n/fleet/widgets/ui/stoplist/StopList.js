(function(){
    var $W = getPackageForName("com.i10n.fleet.widget.ui");
    var $D = YAHOO.util.Dom;
    var $L = YAHOO.lang;
    var $E = YAHOO.util.Event;
    var $YW = YAHOO.widget;
    $W.StopList = function(el, params){
        this.elStopListContainer = el;
        this.initParams = params;
        $W.StopList.superclass.constructor.call(this, el, {
            "datasource": params.datasource,
            "config": {
                 "groupTitleKey": "name",
                 "elementKey": "stops",
                 "elementTitleKey": "name",
                 "statusKey": "status",
                 "listTitle": "Stops",
                 "elementId": "id"
            },
             
             "navId": "stoplist"
        });
        /**
         * Creating and Passing a custom Request to DataSource
         * @param {Object} sQuery
         */
        var stoplist = this;
        this.oAutoComplete.generateRequest = function(sQuery){
            return "group=" + stoplist.get($W.SidePaneList.ATTR_SELECTED_GROUP) + "&search_key=id&" + "id=" + sQuery;
        };
    };
    $L.augmentObject($W.StopList, {
        ATTR_SELECTED_STOP: "selectedStop",
        EVT_SELECTED_STOP_CHANGE: "selectedStopChange"
    });
    /*
     * StopList extends SidePaneList
     */
    $L.extend($W.StopList, $W.SidePaneList, {
        configureAttributes: function(){
            /**
             * @attribute selectedVehicle
             * @description Current User Vehicle Selected
             * @type String
             */
            this.setAttributeConfig($W.StopList.ATTR_SELECTED_STOP, {
                value: null,
                validator: function(value){
                    return ($L.isNull(value) || $L.isString(value));
                }
            });
            $W.StopList.superclass.configureAttributes.call(this);
        },
        
        addEventListeners: function(){
            $E.addListener($D.getElementsByClassName('item-name', null, this.elStopListContainer), "click", function(oArgs, oSelf){
                var elParent = $D.getAncestorByClassName($E.getTarget(oArgs), "item");
                this.set($W.StopList.ATTR_SELECTED_STOP, elParent.getAttribute("item"));
            }, this, true);
            $W.StopList.superclass.addEventListeners.call(this);
            this.subscribe($W.StopList.EVT_SELECTED_STOP_CHANGE, function(oArgs){
               $D.getElementsByClassName("yui-cms-item", null, this.elStopListContainer, function(oNode){
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
         * Over-rided function to select an stop
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
                            this.set($W.StopList.ATTR_SELECTED_STOP, sItemId);
                            }
                    }
                }
            }, this, true);
        }
    });
})();
