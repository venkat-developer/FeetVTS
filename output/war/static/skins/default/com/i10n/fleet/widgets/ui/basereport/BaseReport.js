(function() {
    var $B = YAHOO.Bubbling;
    var $L = YAHOO.lang;
    var $YU = YAHOO.util;
    var $E = YAHOO.util.Event;
    var $D = YAHOO.util.Dom;
    var $YW = YAHOO.widget;
    var $W = getPackageForName("com.i10n.fleet.widget.ui");
    var $U = getPackageForName("com.i10n.fleet.Utils");
    
    /**
     * Base Report Widget for all the subnav widgets of Reports Page. All the inheriting widgets should
     * override the following methods : render, getDataURL and destroy
     *
     * @param {Object} el
     * @param {Object} oArgs
     */
    $W.BaseReport = function(el, oArgs){
        this.elBase = el;
        this.initBaseReport(el, oArgs);
    };
    $L.augmentObject($W.BaseReport, {
        ATTR_SELECTED_ITEM: "selectedItem",
        ATTR_CURRENT_ITEM: "currentItem",
        ATTR_CURRENT_TIMEFRAME: "currentTimeFrame",
        ATTR_SELECTED_TIMEFRAME: "selectedTimeFrame",
        ATTR_RPT_DATA_TYPE: "dataType",
        ATTR_DATA: "data",
        EVT_SEL_ITEM_CHANGE: "selectedItemChange",
        EVT_SEL_TIMEFRAME_CHANGE: "selectedTimeFrameChange",
        EVT_CUR_ITEM_CHANGE: "currentItemChange",
        EVT_CUR_TIMEFRAME_CHANGE: "currentTimeFrameChange",
        EVT_DATA_CHANGE: "dataChange",
        KEY_TYPE_JSON: "json",
        KEY_TYPE_MARKUP: "markup",
        EVT_ON_RENDER_FINISHED: "onRenderFinished"
    });
    $L.extend($W.BaseReport, $YU.AttributeProvider);
    $L.augmentObject($W.BaseReport.prototype, {
        /**
         * Object Map of widgets initialized by the this widgetz
         */
        _widgets: {},
        _isHidden: true,
        /**
         * Says if the current data is stale or not.
         */
        _isDataStale: true,
        /**
         * initializer function
         * @param {Object} el
         * @param {Object} oArgs
         */
        initBaseReport: function(el, oArgs) {
            if (oArgs && oArgs.timeframe) {
                this.initWidgets();
            }
            this.initAttributes(oArgs);
            this.addListeners();
            if (oArgs && oArgs["default"]) {
                this.show();
                this.fireEvent($W.BaseReport.EVT_ON_RENDER_FINISHED);
            }
        },
        /**
         * Initializes Common Widgets accros the children.
         */
        initWidgets: function() {
            var elTimeFrameBar = $D.getElementsByClassName("reporttimeframe", null, this.elBase)[0];
            var oTimeFrameBar = new $W.ReportTimeFrame(elTimeFrameBar, {
                navId: this.getPageID()
            });
            oTimeFrameBar.subscribe("timeFrameChange", function(oArgs) {
                this.set($W.BaseReport.ATTR_SELECTED_TIMEFRAME, oArgs.newValue);
            }, this, true);
            this._widgets.timeFrameBar = oTimeFrameBar;
            elTimeFrameBar = null;
            oTimeFrameBar = null;
        },
        /**
         * Adds listeners to various attributes and dom elements.
         */
        addListeners: function() {
            this.subscribe($W.BaseReport.EVT_SEL_ITEM_CHANGE, this.onSelectedAttributeChange, this, true);
            this.subscribe($W.BaseReport.EVT_SEL_TIMEFRAME_CHANGE, this.onSelectedAttributeChange, this, true);
            this.subscribe($W.BaseReport.EVT_DATA_CHANGE, function(oArgs) {
                this.destroy();
              
                this.render();
                this.fireEvent($W.BaseReport.EVT_ON_RENDER_FINISHED);
                this.set($W.BaseReport.ATTR_CURRENT_ITEM, this.get($W.BaseReport.ATTR_SELECTED_ITEM));
                this.set($W.BaseReport.ATTR_CURRENT_TIMEFRAME, this.get($W.BaseReport.ATTR_SELECTED_TIMEFRAME));
            }, this, true);
            this.subscribe($W.BaseReport.EVT_ON_RENDER_FINISHED, function(oArgs) {
                $U.hideLayerOverlay();
                if (_instances && _instances.view && _instances.view.resizePage) {
                    _instances.view.resizePage.call(_instances.view);
                }
            }, this, true);
        },
        /**
         * Triggered when timeframe or vehicle changes
         * @param {Object} oArgs
         */
        onSelectedAttributeChange: function(oArgs) {
            var _isChanged = true;
            if ($U.Objects.equals(this.get($W.BaseReport.ATTR_SELECTED_ITEM), this.get($W.BaseReport.ATTR_CURRENT_ITEM))) {
                if (this.get($W.BaseReport.ATTR_SELECTED_TIMEFRAME).equals(this.get($W.BaseReport.ATTR_CURRENT_TIMEFRAME))) {
                    _isChanged = false;
                }
            }
            if (_isChanged) {
                if (!this._isHidden) {
                    this.update();
                    this._isDataStale = false;
                }
                else {
                    this._isDataStale = true;
                }
            }
            else {
                this._isDataStale = false;
            }
        },
        /**
         * Initializes attributes for the widget
         * @param {Object} oArgs
         */
        initAttributes: function(oArgs) {
            /**
             * @attribute selectedVehicle
             * @description Current Vehicle whose report's are to be displayed
             * @type String
             */
            this.setAttributeConfig($W.BaseReport.ATTR_SELECTED_ITEM, {
                value: null,
                validator: function(val) {
                    return $L.isString(val) || $L.isNull(val);
                }
            });
            /**
             * @attribute currentVehicle
             * @description Current Vehicle whose report's are to be displayed
             * @type String
             */
            this.setAttributeConfig($W.BaseReport.ATTR_CURRENT_ITEM, {
                value: null,
                validator: function(val) {
                    return $L.isString(val) || $L.isNull(val);
                }
            });
            var initialTimeframe = null;
            if (oArgs && oArgs.initialTimeframe) 
                initialTimeframe = oArgs.initialTimeframe;
            else if ($L.isObject(this._widgets.timeFrameBar)) 
                initialTimeframe = this._widgets.timeFrameBar._getTimeFrameData();
            /**
             * @attribute selectedTimeFrame
             * @description selecteTimeFrame of the report
             * @type String
             */
            this.setAttributeConfig($W.BaseReport.ATTR_SELECTED_TIMEFRAME, {
                value: initialTimeframe
            });
            /**
             * @attribute currentTimeFrame
             * @description currentTimeFrame of the report
             * @type String
             */
            this.setAttributeConfig($W.BaseReport.ATTR_CURRENT_TIMEFRAME, {
                value: initialTimeframe
            });
            var sDataType = (oArgs && oArgs.dataType && (oArgs.dataType == "markup" || oArgs.dataType == "json")) ? oArgs.dataType : "markup";
            /**
             * @attribute dataType
             * @description Data Type of the report, Supports only the values : markup , json
             * @type String
             */
            this.setAttributeConfig($W.BaseReport.ATTR_RPT_DATA_TYPE, {
                value: sDataType,
                readOnly: true
            });
            /**
             * @attribute data
             * @description Data of the report (can be a markup or a object)
             * @type String
             */
            this.setAttributeConfig($W.BaseReport.ATTR_DATA, {
                value: null
            });
            this.createEvent($W.BaseReport.EVT_ON_RENDER_FINISHED);
        },
        /**
         * Shows the current widget
         */
        show: function() {
            if (this._isHidden) {
                $D.removeClass(this.elBase, "rpt-hidden");
                this._isHidden = false;
            }
            if (this._isDataStale) {
                this.update();
                this._isDataStale = false;
            }
            if (_instances && _instances.view && _instances.view.resizePage) {
                _instances.view.resizePage();
            }
        },
        /**
         * Hides the current widget
         */
        hide: function() {
            if (!this._isHidden) {
                $D.addClass(this.elBase, "rpt-hidden");
                this._isHidden = true;
            }
        },
        /**
         * Updates the data for the widget by communicating to the data url.
         */
        update: function() {
            var oCallback = {
                success: function(o) {
                    if (this.get($W.BaseReport.ATTR_RPT_DATA_TYPE) == $W.BaseReport.KEY_TYPE_JSON) {
                        try {
                            this.set($W.BaseReport.ATTR_DATA, JSON.parse(o.responseText));
                        } 
                        catch (ex) {
                            $U.alert({
                                message: "The response from the server was not of the required format!"
                            });
                        }
                    }
                    else {
                        this.set($W.BaseReport.ATTR_DATA, o.responseText);
                    }
                },
                failure: function(o) {
                    $U.alert({
                        message: "There was a failure in loading the data!This can occur if the vehicle or timeframe is invalid"
                    });
                },
                scope: this
            };
            var url = this.getDataURL();
            if (url) {
                $U.showLayerOverlay();
                $U.Connect.asyncRequest('GET', url, oCallback);
            }
        },
        /**
         * A function that all class will have to override.
         * All overriding classes will have to fire $W.BaseReport.EVT_ON_RENDER_FINISHED event to show finish of rendering.
         */
        render: function() {
            this.fireEvent($W.BaseReport.EVT_ON_RENDER_FINISHED);
        },
        /**
         * A function that all inheriting class will override;
         */
        getDataURL: function() {
            return null;
        },
        /**
         * Member function that must be overidden by the inheriting class.
         * Will be executed when the data is replaced.
         */
        destroy: function() {
        }
    });
})();
