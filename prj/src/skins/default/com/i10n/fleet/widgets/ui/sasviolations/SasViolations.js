(function() {
    var $L = YAHOO.lang;
    var $E = YAHOO.util.Event;
    var $D = YAHOO.util.Dom;
    var $YU = YAHOO.util;
    var $YW = YAHOO.widget;
    var $U = getPackageForName("com.i10n.fleet.Utils");
    var $W = getPackageForName("com.i10n.fleet.widget.ui");
    var $V = getPackageForName("com.i10n.fleet.widget.view");
    /**
     * Violation Report Sub Nav Page.
     * TODO : This widget initializes all the report during load. This will be changed to onload.
     * @author sabarish
     */
    $W.SasViolations = function(el, oArgs) {
        this._oTabView = new $YW.TabView($D.getElementsByClassName("yui-navset", null, el)[0]);
        if (!oArgs) {
            oArgs = {};
        }
        oArgs.timeframe = true;
        oArgs.dataType = $W.BaseReport.KEY_TYPE_JSON;
        $W.SasViolations.superclass.constructor.call(this, el, oArgs);
    };
    $L.augmentObject($W.SasViolations, {
        FORMATTERS: {
            TIME_FORMATTER: function(elCell, oRecord, oColumn, oData) {
                if (oData) {
                    elCell.innerHTML = oData;
                }
            }
        }
    });
    $L.augmentObject($W.SasViolations, {
        PAGE_ID: "sasviolations",
        /**
         * Config Data for Reports.
         */
        CONFIG_REPORTS: {
            "timedev": {
                columndefs: [{
                    key: "id",
                    hidden: true
                }, {
                    key: "index",
                    label: "No.",
                    sortable: true
                }, {
                    key: "vehiclename",
                    label: "Vehicle Name",
                    sortable: true,
                    sortOptions: {
                        defaultDir: $YW.DataTable.CLASS_DESC
                    },
                    resizeable: false
                }, {
                    key: "stopname",
                    label: "Stop Name",
                    sortable: true,
                    resizeable: false
                }, {
                    key: "routename",
                    label: "Route Name",
                    sortable: true,
                    resizeable: false
                }, {
                    key: "expectedtime",
                    label: "Expected Time",
                    sortable: true,
                    resizeable: false
                }, {
                    key: "actualtime",
                    label: "Actual Time",
                    sortable: true,
                    resizeable: false,
                    formatter: $W.Report.FORMATTERS.STRING_DATE_FORMATTER,
                    sortOptions: {
                        defaultDir: $YW.DataTable.CLASS_DESC
                    }
                }, {
                    key: "deviation",
                    label: "Deviation(in mins)",
                    sortable: true,
                    resizeable: false
                }],
                "datasource": {
                    responseType: $YU.DataSource.TYPE_JSARRAY,
                    responseSchema: {
                        elementField: "violations",
                        fields: ["id", "index", "vehiclename", "stopname", "routename", "expectedtime", "actualtime", "deviation"]
                    }
                },
                options: {
                    "select": {
                        "enabled": true,
                        "selected": true
                    },
                    "print": {
                        "enabled": true
                    },
                    "groups": {
                        "enabled": true,
                        "titleId": "name"
                    }
                },
                reportconfig: {}
            },
            "routedev": {
                columndefs: [{
                    key: "id",
                    hidden: true
                }, {
                    key: "index",
                    label: "No.",
                    sortable: true
                }, {
                    key: "vehiclename",
                    label: "Vehicle Name",
                    sortable: true,
                    sortOptions: {
                        defaultDir: $YW.DataTable.CLASS_DESC
                    },
                    resizeable: false
                }, {
                    key: "stopname",
                    label: "Stop Name",
                    sortable: true,
                    resizeable: false
                }, {
                    key: "routename",
                    label: "Route Name",
                    sortable: true,
                    resizeable: false
                }, {
                    key: "estimateddistance",
                    label: "Estimated Distance",
                    sortable: true,
                    resizeable: false
                }, {
                    key: "actualdistance",
                    label: "Actual Distance",
                    sortable: true,
                    resizeable: false
                }, {
                    key: "deviation",
                    label: "Deviation(in meters)",
                    sortable: true,
                    resizeable: false
                },{                    
                	key: "occurredat",
                    label: "Occurred At",
                    sortable: true,
                    resizeable: false,
                    formatter: $W.Report.FORMATTERS.STRING_DATE_FORMATTER,
                    sortOptions: {
                        defaultDir: $YW.DataTable.CLASS_DESC
                    }
                }],
                "datasource": {
                    responseType: $YU.DataSource.TYPE_JSARRAY,
                    responseSchema: {
                        elementField: "violations",
                        fields: ["id", "index", "vehiclename", "stopname", "routename", "estimateddistance", "actualdistance", "deviation", "occurredat"]
                    }
            },
                options: {
                    "select": {
                        "enabled": true,
                        "selected": true
                    },
                    "print": {
                        "enabled": true
                    },
                    "groups": {
                        "enabled": true,
                        "titleId": "name"
                    }
                },
                reportconfig: {}
            },
            "stopdev": {
                columndefs: [{
                    key: "id",
                    hidden: true
                }, {
                    key: "index",
                    label: "No.",
                    sortable: true
                }, {
                    key: "vehiclename",
                    label: "Vehicle Name",
                    sortable: true,
                    sortOptions: {
                        defaultDir: $YW.DataTable.CLASS_DESC
                    },
                    resizeable: false
                }, {
                    key: "missedstopname",
                    label: "Missed Stop Name",
                    sortable: true,
                    resizeable: false
                }, {
                    key: "routename",
                    label: "Route Name",
                    sortable: true,
                    resizeable: false
                }, {
                    key: "expectedtime",
                    label: "Expected Time",
                    sortable: true,
                    resizeable: false
                },{                    
                	key: "occurredat",
                    label: "Occurred At",
                    sortable: true,
                    resizeable: false,
                    formatter: $W.Report.FORMATTERS.STRING_DATE_FORMATTER,
                    sortOptions: {
                        defaultDir: $YW.DataTable.CLASS_DESC
                    }
                }],
                "datasource": {
                    responseType: $YU.DataSource.TYPE_JSARRAY,
                    responseSchema: {
                        elementField: "violations",
                        fields: ["id", "index", "vehiclename", "missedstopname", "routename", "expectedtime", "occurredat"]
                    }
                },
                options: {
                    "select": {
                        "enabled": true,
                        "selected": true
                    },
                    "print": {
                        "enabled": true
                    },
                    "groups": {
                        "enabled": true,
                        "titleId": "name"
                    }
                },
                reportconfig: {}
            },
            "tripmissdev": {
            	  columndefs: [{
                      key: "id",
                      hidden: true
                  },  {
                      key: "vehiclename",
                      label: "Vehicle Name",
                      sortable: true,
                      sortOptions: {
                          defaultDir: $YW.DataTable.CLASS_DESC
                      },
                      resizeable: false
                  },  {
                      key: "routename",
                      label: "Route Name",
                      sortable: true,
                      resizeable: false
                  }, {
                      key: "expectedtime",
                      label: "Expected Time",
                      sortable: true,
                      resizeable: false
                  },{                    
                  	key: "occurredat",
                      label: "Occurred At",
                      sortable: true,
                      resizeable: false,
                      formatter: $W.Report.FORMATTERS.STRING_DATE_FORMATTER,
                      sortOptions: {
                          defaultDir: $YW.DataTable.CLASS_DESC
                      }
                  }],
                  "datasource": {
                      responseType: $YU.DataSource.TYPE_JSARRAY,
                      responseSchema: {
                          elementField: "violations",
                          fields: ["id", "index", "vehiclename", "routename", "expectedtime", "occurredat"]
                      }
                  },
                  options: {
                      "select": {
                          "enabled": true,
                          "selected": true
                      },
                      "print": {
                          "enabled": true
                      },
                      "groups": {
                          "enabled": true,
                          "titleId": "name"
                      }
                  },
                  reportconfig: {}
            }
        }
    });
    $L.extend($W.SasViolations, $W.BaseReport, {
        render: function() {
            if (this._isDataTableInitialized) {
                var oData = this.get($W.BaseReport.ATTR_DATA);
                if (oData) {
                    for (var sReportID in oData) {
                        if (this._oGPDataSource[sReportID]) {
                            this._oGPDataSource[sReportID].setGroupData(oData[sReportID]);
                        }
                    }
                }
            }
            else {
                this.initReports(this.elBase);
            }
        },
        getDataURL: function() {
            var sResult = null;
            var selectedVehicle = this.get($W.BaseReport.ATTR_SELECTED_ITEM);
            var oTimeFrame = this.get($W.BaseReport.ATTR_SELECTED_TIMEFRAME);
            if (!(oTimeFrame && selectedVehicle)) {
                if (oTimeFrame.startDate && oTimeFrame.endDate) {
                    sResult = "@APP_CONTEXT@/view/sas/?module=/blocks/json&data=view&report=sasviolations&localTime="+$U.getLocalTime()+"&startdate=" + oTimeFrame.startDate + "&enddate=" + oTimeFrame.endDate;
                }
                else {
                    sResult = "@APP_CONTEXT@/view/sas/?module=/blocks/json&data=view&report=sasviolations&localTime="+$U.getLocalTime();
                }
            }
            else {
                if (oTimeFrame.startDate && oTimeFrame.endDate) {
                    sResult = "@APP_CONTEXT@/view/sas/?module=/blocks/json&data=view&report=sasviolations&localTime="+$U.getLocalTime()+"&vehicleId="+selectedVehicle+"&startdate=" + oTimeFrame.startDate + "&enddate=" + oTimeFrame.endDate;
                }
                else {
                    sResult = "@APP_CONTEXT@/view/sas/?module=/blocks/json&data=view&report=sasviolations&localTime="+$U.getLocalTime();
                }
            }
            return sResult;
        },
        getPageID: function() {
            return $W.SasViolations.PAGE_ID;
        }
    });
    $L.augmentObject($W.SasViolations.prototype, {
        /**
         * Object Map of widgets initialized by the this widget
         */
        _widgets: {},
        _isDataTableInitialized: false,
        /**
         * Contains reports initialized
         */
        _reports: {},
        _oGPDataSource: {},
        /**
         * initializes the reports
         * @param {Object} el
         */
        initReports: function(el) {
            this._createReport("routedev", el);
            this._createReport("timedev", el);
            this._createReport("stopdev", el);
            this._createReport("tripmissdev", el);
            this._isDataTableInitialized = true;
        },
        /**
         * A utility function to initializes the report specified by the reportid
         * @param {Object} sReportID
         * @param {Object} el
         */
        _createReport: function(sReportID, el) {
            var elReport = $D.getElementsByClassName(sReportID + "-rpt", null, el)[0];
            if (elReport) {
                var oConfig = this._getConfig(sReportID);
                if (oConfig) {
                    var oReport = new $W.GroupedReport(elReport, oConfig);
                    oReport.sortColumn(oReport.getColumn("startdate"), $YW.DataTable.CLASS_DESC);
                    this._reports[sReportID] = oReport;
                }
            }
        },
        /**
         * Creates the config required for the report.
         * @param {Object} sReportID
         */
        _getConfig: function(sReportID) {
            var oTemplateConfig = $W.SasViolations.CONFIG_REPORTS[sReportID];
            var oConfig = null;
            var oData = this.get($W.BaseReport.ATTR_DATA);
            if (oData && oData[sReportID]) {
                oConfig = {};
                var oDataSource = new $W.GroupedReport.GroupedDataSource(oData[sReportID]);
                $L.augmentObject(oDataSource, oTemplateConfig.datasource, true);
                oConfig.datasource = oDataSource;
                this._oGPDataSource[sReportID] = oDataSource;
                oConfig.columndefs = oTemplateConfig.columndefs;
                oConfig.reportconfig = oTemplateConfig.reportconfig;
                oConfig.options = oTemplateConfig.options;
            }
            return oConfig;
        }
    });
})();
