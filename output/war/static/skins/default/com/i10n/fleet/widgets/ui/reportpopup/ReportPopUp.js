(function(){
    var $YU = YAHOO.util;
    var $D = YAHOO.util.Dom;
    var $L = YAHOO.lang;
    var $E = YAHOO.util.Event;
    var $YW = YAHOO.widget;
    var $W = getPackageForName("com.i10n.fleet.widget.ui");
    var $WL = getPackageForName("com.i10n.fleet.widget.lib");
    var $U = getPackageForName("com.i10n.fleet.Utils");
    /**
     * An Adapter Widget for Other Widgets to interact with OpenLayers Map API.
     * @param {Object} el
     * @param {Object} oArgs
     * @author sabarish
     */
    $W.ReportPopUp = function(el, oArgs){
    	
        $W.ReportPopUp.superclass.constructor.call(this, $D.getElementsByClassName("map-container", null, el)[0], oArgs);
    };
    $L.extend($W.ReportPopUp, $WL.Maps);
})();
