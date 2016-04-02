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
     * A subitem of Vehicle Report
     *
     * @author sabarish
     */
    $W.VehicleGraphReport = function(el, oArgs) {
        $W.VehicleGraphReport.superclass.constructor.call(this, el, oArgs);
    };
    $L.extend($W.VehicleGraphReport, $W.VehicleReport.ReportItem);
})();
