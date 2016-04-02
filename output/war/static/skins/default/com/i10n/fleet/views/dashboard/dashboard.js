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
     * Dashboard View
     *
     * @author sabarish
     */
    $V.Dashboard = function() {
    	document.title="Fleetcheck V2.0|DashBoard";
        this.init = function(params) {
            $V.Dashboard.superclass.init.call(this, params);
            /*
             * Initializing necessary widgets
             */
            var dashboardViolationsEl = $D.get('dashboardviolations');
            if (dashboardViolationsEl) {
                this._widgets.violations = new $W.DashboardViolations(dashboardViolationsEl);
            }
            var quickLinksEl = $D.get("quicklinks");
            if (quickLinksEl) {
                this._widgets.quicklinks = new $W.QuickLinks(quickLinksEl);
            }
            var supportDeskEl = $D.get("supportdesk");
            if(supportDeskEl){
            	this._widgets.supportdesk = new $W.SupportDesk(supportDeskEl);
            }
        };
        $V.Dashboard.superclass.constructor.call(this);
    };
    YAHOO.extend($V.Dashboard, $V.BaseView);
})();
