(function(){
    var $W = getPackageForName("com.i10n.fleet.widget.ui");
    var $L = YAHOO.lang;
    var $E = YAHOO.util.Event;
    var $D = YAHOO.util.Dom;
    var $YU = YAHOO.util;
    var $YW = YAHOO.widget;
    var $U = getPackageForName("com.i10n.fleet.Utils");
    var scopevar = null;
    /**
     * Activity Report Widget
     * @author aravind
     */
    $W.ScheduledReport = function(el, oArgs){
        this.elBase = el;
        this.initScheduledReport(el, oArgs);
    };
    $L.augmentObject($W.ScheduledReport, {
        PAGE_ID: "scheduledreport"
    });
    $L.extend($W.ScheduledReport, $W.BaseReport, {
        /**
         * Datatable representing this widget
         */
        _oDataTable: null,
        /**
         * Datasource for the widget.
         */
        _oDataSource: null,
        /**
         * Returns the data url needed.
         */
        getDataURL: function(){

        },
        /**
         * process the data that came as a result of get method
         * @param {Object} oSelf
         */
        getData: function(oSelf){
            try {
                var selectedVehicle = oSelf.get($W.BaseReport.ATTR_SELECTED_ITEM);
                var data = oSelf.get($W.BaseReport.ATTR_DATA);
                if ($L.isObject(data.scheduledreport[selectedVehicle])) {
                    if ($L.isArray(data.scheduledreport[selectedVehicle].routes)) {
                        return oSelf.sendData(data.scheduledreport[selectedVehicle].routes);
                    }
                }
            } 
            catch (ex) {
                $U.alert({
                    message: "There was an eror loading the data!!!" + ex
                });
                return null;
            }
            return null;
        },
        /**
         * HACK : Due to problem returning an array
         * @param {Object} aResults
         */
        sendData: function(aResults){
            return aResults;
        },
        /**
         * Renders the dataTable for the incoming data
         */
        render: function(){
            var selectedVehicle = this.get($W.BaseReport.ATTR_SELECTED_ITEM);
            var oData = this.get($W.BaseReport.ATTR_DATA);
            var widgetEl = $D.get("vehiclescheduledreport");
            $D.getElementsByClassName("vehiclename", null, this.elBase)[0].innerHTML = selectedVehicle;
            var elErrDialog = $D.getElementsByClassName("simpledialog", null, this.elBase)[0];
            if (oData) {
                $D.removeClass(widgetEl, "empty");
                if (!$D.hasClass(elErrDialog, "disabled")) {
                    $D.addClass(elErrDialog, "disabled");
                }
                if (!this._oDataTable || !this._oDataSource) {
                    var oConfig = this.constructConfig();
                    this._oDataSource = oConfig.datasource;
                    this._oDataTable = new $W.ScheduledReport.DataTable(this.elBase, oConfig, {
                        "selectedVehicle": selectedVehicle
                    });
                }
                var oCallback = {
                    success: this._oDataTable.onDataReturnInitializeTable,
                    failure: this._oDataTable.onDataReturnInitializeTable,
                    scope: this._oDataTable,
                    argument: this._oDataTable.getState()
                };
                if (selectedVehicle) {
                    this._oDataTable._oReportDataSource.sendRequest(this, oCallback);
                }
                this._oDataTable.getState().pagination.paginator.setPage(1, false);
            }
            else {
                if ($D.hasClass(elErrDialog, "disabled")) {
                    $D.removeClass(elErrDialog, "disabled");
                }
            }
            elErrDialog = null;
            widgetEl = null;
        },
        show: function(){
            if (!(this._oDataTable && this._oDataSource)) {
                this._isDataStale = true;
            }
            $W.ScheduledReport.superclass.show.call(this);
        },
        getPageID: function(){
            return $W.ScheduledReport.PAGE_ID;
        },
        /**
         * Constructs a config object for Reports to initialize from the data given
         * @param {Object} reportData
         */
         constructConfig: function(){
            var config = {};
            var datasource = new $YU.FunctionDataSource(this.getData);
            datasource.responseType = $YU.DataSource.TYPE_JSARRAY;
            datasource.responseSchema = {
                fields: ["vehiclename", "routename", "stopname", "sequenceno", "expectedtime"]
            };
            var columndefs = [{
                key: "vehiclename",
                label: "Vehicle Name",
                sortable: true,
                resizeable: false
            }, {
                key: "routename",
                label: "Route Name",
                sortable: true,
                resizeable: false
            }, {
                key: "stopname",
                label: "Stop Name",
                sortable: true,
                resizeable: false
            }, {
                key: "sequenceno",
                label: "Sequence No.",
                sortable: true,
                resizeable: false
            },{
                key: "expectedtime",
                label: "Expected Time",
                sortable: true,
                resizeable: false
            }];
            config.datasource = datasource;
            config.columndefs = columndefs;
            config.reportconfig = {
                initialLoad: false
            };
            config.options = {
                "print": {
                    "enabled": true
                }
            };
            return config;
        }
    });
    $L.augmentObject($W.ScheduledReport.prototype, {
        /**
         * Initializes widget
         * @param {Object} el
         * @param {Object} params
         */
        initScheduledReport: function(el, oArgs){
            if (!oArgs) {
                oArgs = {};
            }
            oArgs.dataType = $W.BaseReport.KEY_TYPE_JSON;
            oArgs.timeframe = true;
            scopevar = this;
            $W.ScheduledReport.superclass.constructor.call(this, el, oArgs);
            var aElButtons = $D.getElementsByClassName("button-done", null, el);
            $W.Buttons.addDefaultHandler(aElButtons, function(oArgs){
                var elErrDialog = $D.getElementsByClassName("simpledialog", null, this.elBase)[0];
                if (!$D.hasClass(elErrDialog, "disabled")) {
                    $D.addClass(elErrDialog, "disabled");
                }
            }, null, this);
            aElButtons = null;
            el = null;
            oArgs = null;            
            $W.Buttons.addDefaultHandler($D.getElementsByClassName("button-generate-report", null, this.elBase)[0], this.onGenerateReport, null, this);
        },

        generateSubmitted: {
            success: function(o){
            if (scopevar.get($W.BaseReport.ATTR_RPT_DATA_TYPE) == $W.BaseReport.KEY_TYPE_JSON) {
                try {
                	scopevar.set($W.BaseReport.ATTR_DATA, JSON.parse(o.responseText));
                } 
                catch (ex) {
                    $U.alert({
                        message: "The response from the server was not of the required format!"
                    });
                }
            }
            else {
            	scopevar.set($W.BaseReport.ATTR_DATA, o.responseText);
            }
            },
            failure: function(o){
                $U.alert({
                    message: "There was a failure in loading the data!This can occur if the vehicle or timeframe is invalid"
                });
            }
        },
        onGenerateReport: function(){
            var URL = null;
            var selectedVehicle = this.get($W.BaseReport.ATTR_SELECTED_ITEM);
            if (selectedVehicle) {
                URL = "?module=/blocks/json&data=view&report=scheduledreport";
                URL += "&localTime=" +$U.getLocalTime();
                URL += "&vehicleID=" + selectedVehicle;
            }
            $U.Connect.asyncRequest('GET', URL, this.generateSubmitted,null);
        	 URL = null;
        }
    });
    /**
     * Datatable widget for the Scheduled report
     * @param {Object} el
     * @param {Object} config
     */
    $W.ScheduledReport.DataTable = function(el, config, params){
        this.initDataTable(el, config);
        this.initParams = params;
    };
    /**
     * overridable functions
     * @param {Object} elBufferReport
     */
    $L.extend($W.ScheduledReport.DataTable, $W.Report, {
        generatePrintableDataTable: function(elBufferReport){
            var oBufferConfig = $U.cloneObject(this._oReportConfig);
            oBufferConfig.paginator = null;
            var bufferDataTable = new $YW.DataTable(elBufferReport, this._oColumnDefs, this._oReportDataSource, oBufferConfig);
            bufferDataTable.getRecordSet().deleteRecords(0);
            bufferDataTable.getRecordSet().addRecords(this.getCurrentRecords());
            bufferDataTable.render();
        },
        /**
         * Overridable Function to show a print preview current report in a popup. Generates the markup ina buffer element
         * whose display is set to none.
         */
        printReport: function(){
            var sBufferTemplateContent = null;
            var sReportName = "report";
            var elBufferReport = $D.getElementsByClassName("print-section", null, this.reportElement)[0];
            var elPrintContent = $D.getElementsByClassName("print-skin-template", null, this.baseElement)[0];
            var elPrintTitleContent = $D.getElementsByClassName("title", null, elPrintContent)[0];
            var sTempPrintTitle = null;
            if (this.initParams && this.initParams.selectedVehicle) {
                sTempPrintTitle = elPrintTitleContent.innerHTML;
                elPrintTitleContent.innerHTML = this.initParams.selectedVehicle.toUpperCase() + "  " + elPrintTitleContent.innerHTML;
            }
            sBufferTemplateContent = $U.processTemplate(elPrintContent.innerHTML, {
                reportname: sReportName
            });
            if (this.attrs.get($W.Report.ATTR_SELECT_ENABLED)) {
                this.generateSelectedPrintableDataTable(elBufferReport);
            }
            else {
                this.generatePrintableDataTable(elBufferReport);
            }
            elPrintTitleContent.innerHTML = sTempPrintTitle;
            var tempBufferReport = elBufferReport.innerHTML;
            elBufferReport.innerHTML = sBufferTemplateContent;
            this.setReportContent(elBufferReport, sReportName, tempBufferReport);
            var sReportBody = this.getPrintReportHeader() +
            elBufferReport.innerHTML +
            this.getPrintReportFooter();
            var sOptions = this.getPrintReportPopupOptions();
            var aStyleSheets = this.getPrintReportStyleSheets();
            var aScripts = this.getPrintReportScripts();
            $U.openPrintPreviewPopup("Report Print Preview", sReportBody, aStyleSheets, aScripts, sOptions);
            $U.removeChildNodes(elBufferReport);
        }
    });
    $L.augmentObject($W.ScheduledReport.DataTable.prototype, {
        /**
         * initialises the datatable
         * @param {Object} el
         * @param {Object} config
         */
        initDataTable: function(el, config){
            $W.ScheduledReport.DataTable.superclass.constructor.call(this, el, config);
        },
        /**
         * Fetches the current set of records for the print preview
         */
        getCurrentRecords: function(){
            var aResultRecords = [];
            var aRecords = this.getRecordSet().getRecords();
            for (var i = 0; i < aRecords.length; i++) {
                aResultRecords.push(aRecords[i].getData());
            }
            return aResultRecords;
        }
    });
})();
