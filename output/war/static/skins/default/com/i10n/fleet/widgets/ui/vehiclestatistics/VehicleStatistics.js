(function(){
    var $B = YAHOO.Bubbling;
    var $L = YAHOO.lang;
    var $YU = YAHOO.util;
    var $E = YAHOO.util.Event;
    var $D = YAHOO.util.Dom;
    var $YW = YAHOO.widget;
    var $W = getPackageForName("com.i10n.fleet.widget.ui");
    var $U = getPackageForName("com.i10n.fleet.Utils");
    
    /**
     * Vehicle Statistics Widget present inside VehicleReport widget
     *
     * @author irk
     */
    $W.VehicleStatistics = function(el, oArgs){
        $W.VehicleStatistics.superclass.constructor.call(this, el, oArgs);
        this.init();
    };
    
    $L.extend($W.VehicleStatistics, $W.VehicleReport.ReportItem, {
        /**
         * Initialize the widget
         */
        init: function(){
            $W.Buttons.addDefaultHandler($D.getElementsByClassName("button-done", null, this.elBase), function(oArgs){
                var elErrDialog = $D.getElementsByClassName("simpledialog", null, this.elBase)[0];
                if (!$D.hasClass(elErrDialog, "disabled")) {
                    $D.addClass(elErrDialog, "disabled");
                }
            }, null, this);
            $E.addListener($D.getElementsByClassName("print-preview", null, this.elBase), "click", this.printReport, this, true);
        },
        
        /**
         * Renders the widget based on the data.
         */
        render: function(){
            var elErrDialog = $D.getElementsByClassName("simpledialog", null, this.elBase)[0];
            var report = $D.getElementsByClassName("report", null, this.elBase)[0];
            var oData = this.get($W.BaseReport.ATTR_DATA);
            var selectedVehicle = this.get($W.BaseReport.ATTR_SELECTED_ITEM);
            if (selectedVehicle && oData) {
                if (!$D.hasClass(elErrDialog, "disabled")) {
                    $D.addClass(elErrDialog, "disabled");
                }
                if ($D.hasClass(report, "disabled")) {
                    $D.removeClass(report, "disabled");
                }
                var vehicleNameDiv = $D.getElementsByClassName("vehiclename", null, this.elBase)[0];
                vehicleNameDiv.innerHTML = oData.vehiclestatistics[selectedVehicle].name;
                var totalDistance = $D.getElementsByClassName("total-distance", null, this.elBase)[0];
                var maxSpeed = $D.getElementsByClassName("max-speed", null, this.elBase)[0];
                var avgSpeed = $D.getElementsByClassName("avg-speed", null, this.elBase)[0];
                totalDistance.innerHTML = oData.vehiclestatistics[selectedVehicle].distance;
                maxSpeed.innerHTML = oData.vehiclestatistics[selectedVehicle].maxspeed;
                avgSpeed.innerHTML = oData.vehiclestatistics[selectedVehicle].speed;
            }
            this.fireEvent($W.BaseReport.EVT_ON_RENDER_FINISHED);
        },
        
        /**
         * Returns a url to update data.
         */
        getDataURL: function(){
            var sResult = null;
            var sVehicleId = this.get($W.BaseReport.ATTR_SELECTED_ITEM);
            var oTimeFrame = this.get($W.BaseReport.ATTR_SELECTED_TIMEFRAME);
            if (sVehicleId && oTimeFrame) {
                sResult = "/fleet/view/reports/?module=/blocks/json&data=view&report=vehiclestatistics";
                sResult += "&localTime=" +$U.getLocalTime();
                sResult += "&vehicleID=" + sVehicleId;
                sResult += "&startdate=" + oTimeFrame.startDate;
                sResult += "&enddate=" + oTimeFrame.endDate;
            }
            return sResult;
        },
        
        /**
         * Destroying current UI to make way for a new one.
         * No specific destroying actions are required in this case
         */
        destroy: function(){
        
        },
        
        /**
         *  Print Preview Event handler
         */
        printReport: function(){
            var sReportName = "vehiclestatistics";
            var elBufferReport = $D.getElementsByClassName("print-section", null, this.elBase)[0];
            var elPrintContent = $D.getElementsByClassName("print-skin-template", null, this.elBase)[0];
            var sBufferTemplateContent = $U.processTemplate(elPrintContent.innerHTML, {
                reportname: sReportName
            });
            elBufferReport.innerHTML = sBufferTemplateContent;
            this._setReportContent(sReportName, elBufferReport);
            var sReportBody = elBufferReport.innerHTML;
            var aStyleSheets = this.getPrintReportStyleSheets();
            var sOptions = this.getPrintReportPopupOptions();
            $U.openPrintPreviewPopup("Report Print Preview", sReportBody, aStyleSheets, null, sOptions);
            $U.removeChildNodes(elBufferReport);
        },
        _setReportContent: function(sReportName, elBufferReport){
            var elReportContent = $D.getElementsByClassName(sReportName, null, elBufferReport)[0];
            elReportContent.innerHTML = elReportContent.innerHTML +
            "<div class='report'><div class='bd'>" +
            $D.getElementsByClassName("data-content", null, this.elBase)[0].innerHTML +
            "</div></div>";
        },
        
        /**
         * Returns the Print Preview pop-up options
         */
        getPrintReportPopupOptions: function(){
            return "height=700px,width=1000px,location=0,status=0,resizable=0,toolbar=0,menubar=0,titlebar=0,scrollbars=1";
        },
        
        /**
         * Return the StyleSheets required for the Print-Preview Popup
         */
        getPrintReportStyleSheets: function(){
            return _publish.report.stylesheets;
        },
        
        /**
         * Show this widget
         */
        show: function(){
            $W.VehicleReport.superclass.show.call(this);
        }
    });
})();