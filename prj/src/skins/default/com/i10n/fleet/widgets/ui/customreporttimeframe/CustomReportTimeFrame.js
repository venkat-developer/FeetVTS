(function() {
    var $W = getPackageForName("com.i10n.fleet.widget.ui");
    var $L = YAHOO.lang;
    var $E = YAHOO.util.Event;
    var $D = YAHOO.util.Dom;
    var $YW = YAHOO.widget;
    var $YU = YAHOO.util;
    var $U = getPackageForName("com.i10n.fleet.Utils");
    /**
     * The Add Links Utility Widget
     *
     * @author aravind
     */
    $W.CustomReportTimeFrame = function(el) {
        /*Declaring Member Properties*/
        this.elBase = el;
        /*Initializing*/
        $W.CustomReportTimeFrame.superclass.constructor.call(this, el, {
            fixedcenter: true,
            height: "auto",
            width: "525px"
        });
        this.render();
        this.hide();
    };
    $L.augmentObject($W.CustomReportTimeFrame, {
        INTERVAL_SELECTED: "intervalselected",
        EVT_INTERVAL_SELECTED_CHANGE: "intervalselectedChange",
        ATTR_NAV_ID: "navId",
        _oCalendar: null,
        _oDialog: null,
        /**
         * initializes the calendar in the custom report
         */
        initCalendar: function() {
            if (!$W.CustomReportTimeFrame._oCalendar) {
                $W.CustomReportTimeFrame._oCalendar = new $W.CustomReportTimeFrame.Calendar("calendar", {
                    pages: 1
                });
                $W.CustomReportTimeFrame._oCalendar.render();
            }
        },
        initCalendarDialog: function() {
            function closeHandler() {
                $W.CustomReportTimeFrame._oDialog.hide();
                var dateObj = $W.CustomReportTimeFrame._oCalendar.getDate();
                var sDateState = $W.CustomReportTimeFrame._oCalendar.get($W.CustomReportTimeFrame.Calendar.DATE_STATE);
                var oObject = {
                    "state": sDateState,
                    "dateObj": dateObj
                };
                $W.CustomReportTimeFrame._oDialog.onDialogClose.fire(oObject);
            }
            $W.CustomReportTimeFrame._oDialog = new YAHOO.widget.Dialog("calendar-container", {
                visible: false,
                draggable: false,
                close: true,
                x: 685,
                y: 290,
                width: "210px",
                height: "80px",
                zindex: 2220,
                modal: true,
                buttons: [{
                    text: "Close",
                    handler: closeHandler,
                    isDefault: true
                }]
            });
            $W.CustomReportTimeFrame._oDialog.setBody('<div id="calendar"></div>');
            $W.CustomReportTimeFrame._oDialog.render(document.body);
            $W.CustomReportTimeFrame._oDialog.onDialogClose = new $YU.CustomEvent("onDialogClose", this);
        }
    });
    $L.extend($W.CustomReportTimeFrame, $W.DialogPopUp, {
        /**
         * Configures Attributes.
         */
        configureAttributes: function() {
            $W.CustomReportTimeFrame.superclass.configureAttributes.call(this);
            this.attrs.setAttributeConfig($W.CustomReportTimeFrame.INTERVAL_SELECTED, {
                value: null
            });
            this.attrs.setAttributeConfig($W.CustomReportTimeFrame.ATTR_NAV_ID, {
                value: null,
                validator: function(oVal) {
                    return ($L.isNull(oVal) || $L.isString(oVal));
                }
            });
        },
        /**
         * Shows the popup after clearing the messages in the popup.
         */
        show: function(navId, intervalSelected) {
            this._setDates(intervalSelected);
            $W.DialogPopUp.superclass.show.call(this);
            this.attrs.set($W.CustomReportTimeFrame.ATTR_NAV_ID, navId);
        },
        /**
         * Overrides addListeners of DialogPopUp and adds the widget's cutom listener's
         * after adding listenersof the superclass.
         */
        addListeners: function() {
            $E.addListener($D.getElementsByClassName('calendar-image', null, this.elBase)[0], "click", function(oArgs, oSelf) {
                this._showCalendar("startDate");
            }, this, true);
            $E.addListener($D.getElementsByClassName('calendar-image', null, this.elBase)[1], "click", function(oArgs, oSelf) {
                this._showCalendar("endDate");
            }, this, true);
            $W.CustomReportTimeFrame._oDialog.onDialogClose.subscribe(function(oType, aArgs) {
                this._assignDates(aArgs[0].state, aArgs[0].dateObj);
            }, this, true);
            $W.CustomReportTimeFrame.superclass.addListeners.call(this);
            this.subscribe($W.DialogPopUp.EVT_ON_SUBMIT, this._onLinkSubmit, this, true);
        },
        /**
         * Triggered when Submit button is clicked. Also fires onSubmit event
         * @param {Object} oArgs
         */
        onSubmit: function(oArgs) {
            this.fireEvent($W.DialogPopUp.EVT_ON_SUBMIT, {
                formValue: this.getFormValue()
            });
        }
    });
    $L.augmentObject($W.CustomReportTimeFrame.prototype, {
        /**
         * Triggered when Popup fires onSubmit event
         * @param {Object} oArgs
         */
        _onLinkSubmit: function(oArgs) {
            var oFormValue = oArgs.formValue;
            if (oFormValue) {
                if (this._checkDatesSubmitted(oFormValue)) {
                    var sStartDate = oFormValue.startMonth + "/" + oFormValue.startDay + "/" + oFormValue.startYear + " " + oFormValue.startHour + ":" + oFormValue.startMin + ":" + "00";
                    var sEndDate = oFormValue.endMonth + "/" + oFormValue.endDay + "/" + oFormValue.endYear + " " + oFormValue.endHour + ":" + oFormValue.endMin + ":" + "00";
                    var oSelectedInterval = {
                        "startDate": sStartDate,
                        "endDate": sEndDate,
                        "id": this.attrs.get($W.CustomReportTimeFrame.ATTR_NAV_ID)
                    };
                    this.hide();
                    this.attrs.set($W.CustomReportTimeFrame.INTERVAL_SELECTED, oSelectedInterval);
                }
                else {
                    $U.alert({
                        message: "Invalid Date Selection"
                    });
                }
            }
        },
        /**
         * Called when the calendar should be shown
         */
        _showCalendar: function(dateState) {
            $W.CustomReportTimeFrame._oCalendar.show(dateState);
            $W.CustomReportTimeFrame._oDialog.show();
        },
        /**
         * Assigns the start Date and the End date
         * @param {Object} dateObj
         * @param {Object} dateState
         */
        _assignDates: function(dateState, dateObj) {
            var elOptions = $D.getElementsByClassName("input-element", null, this.elBase);
            if (dateObj && dateState) {
                if (dateState == "startDate") {
                    elOptions[0].selectedIndex = dateObj.day;
                    elOptions[1].selectedIndex = dateObj.month + 1;
                    elOptions[2].selectedIndex = dateObj.year - 2000 + 1;
                }
                else if (dateState == "endDate") {
                    elOptions[5].selectedIndex = dateObj.day;
                    elOptions[6].selectedIndex = dateObj.month + 1;
                    elOptions[7].selectedIndex = dateObj.year - 2000 + 1;
                }
            }
        },
        /**
         * Checks the validity of the Start and the End dates
         * @param {Object} oFormValue
         */
        _checkDatesSubmitted: function(oFormValue) {
            var bresult = false;
            var oStartingDate = Date.parse(oFormValue.startMonth + "/" + oFormValue.startDay + "/" + oFormValue.startYear + " " + oFormValue.startHour + ":" + oFormValue.startMin + ":" + "00");
            var oEndingDate = Date.parse(oFormValue.endMonth + "/" + oFormValue.endDay + "/" + oFormValue.endYear + " " + oFormValue.endHour + ":" + oFormValue.endMin + ":" + "00");
            if (oStartingDate && oEndingDate) {
                if (oStartingDate < oEndingDate) {
                    bresult = true;
                }
            }
            return bresult;
        },
        /**
         * Sets the desired date for the custom report
         * @param {Object} intervalSelected
         */
        _setDates: function(intervalSelected) {
            var inputEl = $D.getElementsByClassName("input-element", null, this.elBase);
            inputEl[0].selectedIndex = intervalSelected.startDate.getDate();
            inputEl[1].selectedIndex = intervalSelected.startDate.getMonth() + 1;
            inputEl[2].selectedIndex = intervalSelected.startDate.getFullYear() - 2000 + 1;
            inputEl[3].selectedIndex = intervalSelected.startDate.getHours() + 1;
            inputEl[4].selectedIndex = intervalSelected.startDate.getMinutes() + 1;
            inputEl[5].selectedIndex = intervalSelected.endDate.getDate();
            inputEl[6].selectedIndex = intervalSelected.endDate.getMonth() + 1;
            inputEl[7].selectedIndex = intervalSelected.endDate.getFullYear() - 2000 + 1;
            inputEl[8].selectedIndex = intervalSelected.endDate.getHours() + 1;
            inputEl[9].selectedIndex = intervalSelected.endDate.getMinutes() + 1;
        }
    });
    $W.CustomReportTimeFrame.Calendar = function(container, cfg) {
        this.cfg = cfg ||
        {};
        this.configureAttributes();
        this.cfg.multi_select = false;
        $W.CustomReportTimeFrame.Calendar.superclass.constructor.call(this, container, cfg);
    };
    $L.augmentObject($W.CustomReportTimeFrame.Calendar, {
        DATE_STATE: "state"
    });
    $L.extend($W.CustomReportTimeFrame.Calendar, YAHOO.widget.Calendar, {
        show: function(dateState) {
            this.set($W.CustomReportTimeFrame.Calendar.DATE_STATE, dateState);
            $W.CustomReportTimeFrame.Calendar.superclass.show.call(this);
        },
        configureAttributes: function() {
            /**
             * Attribute for the state of the calendar
             * @param {Object} oVal
             */
            this.setAttributeConfig($W.CustomReportTimeFrame.Calendar.DATE_STATE, {
                value: null,
                validator: function(oVal) {
                    return ($L.isString(oVal));
                }
            });
        }
    });
    $L.augmentObject($W.CustomReportTimeFrame.Calendar.prototype, {
        /**
         * Gets the selected date from the calendar.
         */
        getDate: function() {
            var oSelectedDate = this.getSelectedDates(this)[0];
            if (oSelectedDate) {
                var oDate = {
                    "day": oSelectedDate.getDate(),
                    "month": oSelectedDate.getMonth(),
                    "year": oSelectedDate.getFullYear()
                };
                return oDate;
            }
            else {
                return null;
            }
        }
    });
    $L.augmentProto($W.CustomReportTimeFrame.Calendar, $YU.AttributeProvider);
    $W.CustomReportTimeFrame.initCalendarDialog();
    $W.CustomReportTimeFrame.initCalendar();
})();
