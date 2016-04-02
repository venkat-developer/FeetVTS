(function(){
    var $W = getPackageForName("com.i10n.fleet.widget.ui");
    var $L = YAHOO.lang;
    var $E = YAHOO.util.Event;
    var $D = YAHOO.util.Dom;
    var $YU = YAHOO.util;
    var $U = getPackageForName("com.i10n.fleet.Utils");
    /**
     * Driver Report Sub Nav Page
     *
     * @author aravind
     */
    $W.DriverReport = function(el, oArgs){
        if (!oArgs) {
            oArgs = {};
        }
        oArgs.timeframe = true;
        oArgs.dataType = $W.BaseReport.KEY_TYPE_JSON;
      
        $W.DriverReport.superclass.constructor.call(this, el, oArgs);
       
        this.init(el, oArgs);
    };
    $L.augmentObject($W.DriverReport, {
        PAGE_ID: "driverreport",
        ATTR_CURRENT_REPORT: "currentReport",
        EVT_REPORT_CHANGE: "currentReportChange"
    
    });
    $L.extend($W.DriverReport, $W.GraphReport, {
        render: function(){

            var sCurrentReportID = this.get($W.DriverReport.ATTR_CURRENT_REPORT);
            if (this._widgets.items && this._widgets.items[sCurrentReportID]) {
                this._widgets.items[sCurrentReportID].render();
            }
        },
        getDataURL: function(){
            return null;
            }, 
            /**
             * update is overriden as it will do nothing here.
             */
            update: function(){
            
            },
            /**
             * Overrides $W.BaseReport.show . Shows the current selected report.
             */
            show: function(){
                $W.DriverReport.superclass.show.call(this);
                var sCurrentReportID = this.get($W.DriverReport.ATTR_CURRENT_REPORT);
                if (this._widgets.items && this._widgets.items[sCurrentReportID]) {
                    this._widgets.items[sCurrentReportID].show();
                }
            },
            /**
             * Overrides $W.BaseReport.hide . Hides the inner widgets.
             */
            hide: function(){
                $W.DriverReport.superclass.hide.call(this);
                var oItems = this._widgets.items;
                for (var sItemID in oItems) {
                    oItems[sItemID].hide();
                }
            },
            /**
             * destroys the widget.
             */
            destroy: function(){
            
            },
        getPageID: function(){
            return $W.DriverReport.PAGE_ID;
        },
        /**
         * Initializes the attributes.
         */
        initAttributes: function(){
            $W.DriverReport.superclass.initAttributes.call(this);
            /**
             * @attribute currentVehicle
             * @description Current Vehicle whose report's are to be displayed
             * @type String
             */
            this.setAttributeConfig($W.DriverReport.ATTR_CURRENT_REPORT, {
                value: "drivermapreport",
                validator: function(val){
                    return $L.isString(val) || $L.isNull(val);
                }
            });
        }
    });
    $L.augmentObject($W.DriverReport.prototype, {
        /**
         * Initialization function
         */
        init: function(el, oArgs){
        	this.initAttributes();
            this._addReportItems(el);
            this._addListeners(el);
        }, /**
         * Adds report item widget instances.
         * @param {Object} el
         */
        _addReportItems: function(el){
            var elReportContainer = $D.getElementsByClassName("rpt-container", null, el)[0];
            var sDefaultPageID = $D.getAttribute(elReportContainer, "defaultPage");
            if (_publish.report && _publish.report.DriverReportItems) {
                var oReportItems = _publish.report.DriverReportItems;
                for (var sItemKey in oReportItems) {
                    var sWidgetName = oReportItems[sItemKey].widget;
                    if (sWidgetName && $L.isFunction($W[sWidgetName])) {
                        var isDefault = (sDefaultPageID == sItemKey);
                        var oItem = new $W[sWidgetName]($D.getElementsByClassName("rpt-" + sItemKey, null, el)[0], {
                            "dataType": oReportItems[sItemKey].dataType,
                            "default": isDefault,
                            "initialTimeframe": this._widgets.timeFrameBar._getTimeFrameData()
                        });
                        this._widgets.items[sItemKey] = oItem;
                        if (isDefault) {
                            this.set($W.DriverReport.ATTR_CURRENT_REPORT, sItemKey);
                        }
                    }
                }
            }
        },
        /**
         * Adds listeners to attributes and dom elements.
         * @param {Object} el
         */
        _addListeners: function(el){
            $E.addListener($D.getElementsByClassName('rpt-select', null, el), 'change', this._onReportSelect, this, true);
            this.subscribe($W.BaseReport.EVT_SEL_ITEM_CHANGE, function(oArgs){
                var oItems = this._widgets.items;
                for (var sItemID in oItems) {
                    oItems[sItemID].set($W.BaseReport.ATTR_SELECTED_ITEM, oArgs.newValue);
                }
                
            }, this, true);
            this.subscribe($W.BaseReport.EVT_SEL_TIMEFRAME_CHANGE, function(oArgs){
                var oItems = this._widgets.items;
                for (var sItemID in oItems) {
                    oItems[sItemID].set($W.BaseReport.ATTR_SELECTED_TIMEFRAME, oArgs.newValue);
                }
            }, this, true);
            
            this.subscribe($W.DriverReport.EVT_REPORT_CHANGE, this._onReportChange, this, true);
        },
        /**
         * Listener to change in currentReport Attribute
         * @param {Object} oArgs
         */
        _onReportChange: function(oArgs){
            if (this._widgets.items) {
                for (var itemKey in this._widgets.items) {
                    if (itemKey != oArgs.newValue) {
                        this._widgets.items[itemKey].hide();
                    }
                }
                this._widgets.items[oArgs.newValue].show();
 
            }
        },
        /**
         * Triggered when a report is selected.
         * @param {Object} oArgs
         */
        _onReportSelect: function(oArgs){
            var elTarget = $E.getTarget(oArgs);
            var sReportId = elTarget.value;
            this.set($W.DriverReport.ATTR_CURRENT_REPORT, sReportId);
        }
    });
})();
