(function() {
    var $W = getPackageForName("com.i10n.fleet.widget.ui");
    var $V = getPackageForName("com.i10n.fleet.widget.view");
    var $L = YAHOO.lang;
    var $YU = YAHOO.util;
    var $E = YAHOO.util.Event;
    var $D = YAHOO.util.Dom;
    /**
     * Report Time Frame Widget
     *
     * @author subramaniam, irk,aravind
     */
    $W.ReportTimeFrame = function(el, oArgs) {
        this.elContainer = el;
        this.oStartDate = null;
        this.oEndDate = null;
        this.init(el, oArgs);
    };
    /**
     * Adding Static functions/variables
     */
    $L.augmentObject($W.ReportTimeFrame, {
        ATTR_TIME_FRAME: "timeFrame",
        CUSTOM_TIME_FRAME: "Custom",
        CUSTOM_PREVE_SELECTED: "prevSelected",
        ATTR_NAV_ID: "navId",
        /**
         * initializes the custom report Popup
         */
        initPopup: function() {
            var elCustomReport = $D.get("customreporttimeframe");
            if (elCustomReport) {
                $W.ReportTimeFrame._oPopup = new $W.CustomReportTimeFrame(elCustomReport);
            }
        }
    });
    $L.extend($W.ReportTimeFrame, $YU.AttributeProvider);
    $L.augmentObject($W.ReportTimeFrame.prototype, {
        init: function(el, oArgs) {
            this.initAttributes();
            this.set($W.ReportTimeFrame.ATTR_NAV_ID, oArgs.navId);
            this._addEventListeners();
        },
        /**
         * Returns an object containing the first and last dates of a week given the week number
         * @param {Object} weekNo
         */
        getDateRangeOfWeek: function(weekNo) {
            var dateRange = {};
            var d1 = new Date();
            var numOfdaysPastSinceLastSunday = eval(d1.getDay());
            d1.setDate(d1.getDate() - numOfdaysPastSinceLastSunday);
            var weekNoToday = $YU.Date.format(d1, {
                format: "%U"
            });
            var weeksInTheFuture = weekNo - weekNoToday;
            d1.setDate(d1.getDate() + (7 * weeksInTheFuture));
            dateRange.startDate = d1;
            var d2 = new Date(d1);
            d2.setDate(d2.getDate() + 6);
            dateRange.endDate = d2;
            return dateRange;
        },
        /**
         * Adds listeners for various events in the widget
         */
        _addEventListeners: function() {
            $W.Buttons.addDefaultHandler($D.getElementsByClassName("button-generate-report", null, this.elContainer), function(oArgs) {
                var oTimeFrameData = this._getTimeFrameData();
                this.set($W.ReportTimeFrame.ATTR_TIME_FRAME, oTimeFrameData);
            }, null, this);
            $E.addListener($D.getElementsByClassName('img', null, this.elContainer), "click", function(oArgs, oSelf) {
                if ($D.getElementsByClassName('custom', null, this.elContainer)[0].checked) {
                    this._showCalendar(this.get($W.ReportTimeFrame.ATTR_NAV_ID));
                }
            }, this, true);
            $W.ReportTimeFrame._oPopup.attrs.subscribe($W.CustomReportTimeFrame.EVT_INTERVAL_SELECTED_CHANGE, this._onIntervalSelected, this, true);
        },
        /**
         * Collects the data that has to be passed for custom event.
         */
        _getTimeFrameData: function() {
            var oTimeFrameData = {};
            var aSelectInterval = $D.getElementsByClassName("select-interval", null, this.elContainer);
            if ($L.isArray(aSelectInterval) && aSelectInterval.length > 0) {
                oTimeFrameData.interval = aSelectInterval[0].value;
            }
            var aRadioEl = $D.getElementsByClassName("radio-button", null, this.elContainer);
            for (var i = 0; i < aRadioEl.length; i++) {
                if (aRadioEl[i].checked) {
                    oTimeFrameData.timeframe = aRadioEl[i].value;
                }
            }
            switch (oTimeFrameData.timeframe) {
                case 'Today':
                    oTimeFrameData.startDate = new Date();
                    oTimeFrameData.endDate = new Date();
                    oTimeFrameData.startDate.setHours(0, 0, 0);
                    oTimeFrameData.endDate.setHours(23, 59, 59);
                    break;
                case 'This Week':
                    var weekRange = this.getDateRangeOfWeek($YU.Date.format(new Date(), {
                        format: "%U"
                    }));
                    oTimeFrameData.startDate = weekRange.startDate;
                    oTimeFrameData.endDate = weekRange.endDate;
                    oTimeFrameData.startDate.setHours(0, 0, 0);
                    oTimeFrameData.endDate.setHours(23, 59, 59);
                    break;
                case 'Custom':
                    oTimeFrameData.startDate = new Date(this.oStartDate);
                    oTimeFrameData.endDate = new Date(this.oEndDate);
                    break;
            }
            /**
             * Format the date as 'MM/DD/YYYY  HH:MM:SS'
             */
            oTimeFrameData.startDate = $YU.Date.format(oTimeFrameData.startDate, {
                format: "%m/%d/%Y  %H:%M:%S"
            },"en-US");
            oTimeFrameData.endDate = $YU.Date.format(oTimeFrameData.endDate, {
                format: "%m/%d/%Y  %H:%M:%S"
            },"en-US");
            return new $W.ReportTimeFrame.TimeFrame(oTimeFrameData);
        },
        /**
         * Custom event to show Fleetcalendar
         * @param {Object} type
         * @param {Object} args
         * @param {Object} obj
         */
        _showCalendar: function(navId) {
            var oIntervalSelected = {};
            if (this.get($W.ReportTimeFrame.CUSTOM_PREVE_SELECTED)) {
                var oDate = this.get($W.ReportTimeFrame.CUSTOM_PREVE_SELECTED);
                oIntervalSelected.startDate = oDate.startDate;
                oIntervalSelected.endDate = oDate.endDate;
            }
            else {
                oIntervalSelected.startDate = new Date();
                oIntervalSelected.endDate = new Date();
                oIntervalSelected.startDate.setHours(0, 0, 0);
                oIntervalSelected.endDate.setHours(23, 59, 59);
            }
            $W.ReportTimeFrame._oPopup.show(navId, oIntervalSelected);
        },
        /**
         * Initializes the Attributes
         */
        initAttributes: function() {
            /**
             * @attribute timeFrame
             * @description Current Time Frame Selected.
             * @type Object
             */
            this.setAttributeConfig($W.ReportTimeFrame.ATTR_TIME_FRAME, {
                value: null
            });
            /**
             * @attribute navId
             * @description Current SubNav Page Id in which the widget is initialized
             * @type String
             */
            this.setAttributeConfig($W.ReportTimeFrame.ATTR_NAV_ID, {
                value: null,
                validator: function(oVal) {
                    return ($L.isNull(oVal) || $L.isString(oVal));
                }
            });
            /**
             * Attribute for storing previously selected dates
             * @param {Object} oVal
             */
            this.setAttributeConfig($W.ReportTimeFrame.CUSTOM_PREVE_SELECTED, {
                value: null
            });
        },
        /**
         * Fired when the custom interval changes
         * gets the selected custom interval
         * @param {Object} oArgs
         */
        _onIntervalSelected: function(oArgs) {
            var oDate = oArgs.newValue;
            if (oDate.id == (this.get($W.ReportTimeFrame.ATTR_NAV_ID))) {
                this.oStartDate = oDate.startDate;
                this.oEndDate = oDate.endDate;
                var oSelectedDates = {};
                oSelectedDates.startDate = new Date();
                oSelectedDates.endDate = new Date();
                oSelectedDates.startDate.setTime(Date.parse(oDate.startDate));
                oSelectedDates.endDate.setTime(Date.parse(oDate.endDate));
                this.set($W.ReportTimeFrame.CUSTOM_PREVE_SELECTED, oSelectedDates);
            }
        }
    });
    $W.ReportTimeFrame.TimeFrame = function(oArgs) {
        this.startDate = oArgs.startDate;
        this.endDate = oArgs.endDate;
        this.interval = oArgs.interval;
        this.timeframe = oArgs.timeframe;
    };
    $L.augmentObject($W.ReportTimeFrame.TimeFrame.prototype, {
        startDate: null,
        endDate: null,
        interval: null,
        timeframe: null,
        equals: function(oTimeFrame) {
            var bResult = false;
            if (oTimeFrame && oTimeFrame instanceof $W.ReportTimeFrame.TimeFrame) {
                if (oTimeFrame.timeframe === this.timeframe) {
                    if (oTimeFrame.timeframe === $W.ReportTimeFrame.CUSTOM_TIME_FRAME) {
                        if (this.startDate == oTimeFrame.startDate && this.endDate == oTimeFrame.endDate) {
                            bResult = true;
                        }
                    }
                    else {
                        bResult = true;
                    }
                    if (bResult) {
                        if (this.interval && oTimeFrame.interval) {
                            if (this.interval != oTimeFrame.interval) {
                                bResult = false;
                            }
                        }
                        else if (!(this.interval) && !(oTimeFrame.interval)) {
                            bResult = true;
                        }
                        else {
                            bResult = false;
                        }
                    }
                }
            }
            return bResult;
        }
    });
    $W.ReportTimeFrame.initPopup();
})();
