(function() {
    var $B = YAHOO.Bubbling;
    var $L = YAHOO.lang;
    var $YU = YAHOO.util;
    var $E = YAHOO.util.Event;
    var $D = YAHOO.util.Dom;
    var $YW = YAHOO.widget;
    var $W = getPackageForName("com.i10n.fleet.widget.ui");
    var $V = getPackageForName("com.i10n.fleet.widget.view");
    /**
     * Control Panel View Widget. Has a SubNav View
     *
     * @author sabarish
     */
    $V.ControlPanel = function() {
    	document.title="Fleetcheck V2.0|Control Panel";
        this.init = function(params) {
            $V.ControlPanel.superclass.init.call(this, params);
        };
        $V.ControlPanel.superclass.constructor.call(this);
        this._baseElement = $D.get("view-controlpanel");
    };
    YAHOO.extend($V.ControlPanel, $V.BaseSubNavView);
})();
