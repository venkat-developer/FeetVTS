( function() {
    var $B = YAHOO.Bubbling;
    var $L = YAHOO.lang;
    var $YU = YAHOO.util;
    var $E = YAHOO.util.Event;
    var $D = YAHOO.util.Dom;
    var $YW = YAHOO.widget;
    var $W = getPackageForName("com.i10n.fleet.widget.ui");
    var $V = getPackageForName("com.i10n.fleet.widget.view");
    $V.Dashboard = function() {
        this.init = function(params) {
            var vehicleEl = $D.get("vehiclelist");
            var vehicleList = new $W.VehicleList(vehicleEl);
        }
    }
})();