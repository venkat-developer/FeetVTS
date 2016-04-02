(function() {
    var $B = YAHOO.Bubbling;
    var $L = YAHOO.lang;
    var $YU = YAHOO.util;
    var $E = YAHOO.util.Event;
    var $D = YAHOO.util.Dom;
    var $YW = YAHOO.widget;
    var $W = getPackageForName("com.i10n.fleet.widget.ui");
    var $U = getPackageForName("com.i10n.fleet.Utils");
    var $WL = getPackageForName("com.i10n.fleet.widget.lib");
    /**
     * The vehicle fuel graph report widget
     *
     * @author N.Balaji
     */
    $W.FuelGraph = function(el, oArgs) {
        $W.FuelGraph.superclass.constructor.call(this, el, oArgs);
    };
    $L.extend($W.FuelGraph, $W.GraphReport, {
        /**
         * Returns a url to update data.
         */
        getDataURL: function() {
            var sResult = null;
            var oTimeFrame = this.get($W.BaseReport.ATTR_SELECTED_TIMEFRAME);
            var sVehicleId = this.get($W.BaseReport.ATTR_SELECTED_ITEM);
            if (oTimeFrame && sVehicleId) {
                sResult = "/fleet/view/reports/?debug=true&module=/blocks/json&data=view&vehicleID=" + sVehicleId + "&report=fuelgraph";
                sResult += "&localTime=" +$U.getLocalTime();
                sResult += "&startdate=" + oTimeFrame.startDate;
                sResult += "&enddate=" + oTimeFrame.endDate;
            }
            return sResult;
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
        }
        var vehicleNameDiv = $D.getElementsByClassName("graph-content", null, this.elBase)[0];
        vehicleNameDiv.style.backgroundImage="url("+oData.graphurl+")";
    }
    });
})();