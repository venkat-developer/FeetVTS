(function(){
    var $B = YAHOO.Bubbling;
    var $L = YAHOO.lang;
    var $YU = YAHOO.util;
    var $E = YAHOO.util.Event;
    var $D = YAHOO.util.Dom;
    var $YW = YAHOO.widget;
    var $U = getPackageForName("com.i10n.fleet.Utils");
    var $W = getPackageForName("com.i10n.fleet.widget.ui");
    var $V = getPackageForName("com.i10n.fleet.widget.view");
    
    /**
     * Manage Vehicles UI Widget
     *
     * @author sabarish
     */
    $W.HardwareManager = function(el, oArgs){
        $W.HardwareManager.superclass.constructor.call(this, el, oArgs);
    };
    $L.extend($W.HardwareManager, $W.ActionManager);
})();
