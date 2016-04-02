(function(){

    var $YW = YAHOO.widget;
    var $YU = YAHOO.util;
    var $W = getPackageForName("com.i10n.fleet.widget.ui");
    var $D = YAHOO.util.Dom;
    var $E = YAHOO.util.Event;
    var $L = YAHOO.lang;
    var $U = getPackageForName("com.i10n.fleet.Utils");
    /**
     * Dashboard Violations Widget for Fleetcheck project
     *
     * @author aravind
     */
    $W.DashboardViolations = function(el, oArgs){
    	
        this.elBase = el;
        this.oInitArgs = oArgs;
        this.initDashboardViolations(this.elBase, this.oInitArgs);
    };
    $L.extend($W.DashboardViolations, $YW.TabView);
    $L.augmentObject($W.DashboardViolations, {
        "FORMATTERS": {
            /**
             * Date Formatter.
             * @param {Object} el
             */
            "date": function(el){
    			
                el.innerHTML = $U.Date.getReportDate(el.innerHTML).toLocaleString();
            }
        }
    });
    $L.augmentObject($W.DashboardViolations.prototype, {
        /**
         * initializes the widget
         * @param {Object} el
         * @param {Object} params
         */
        initDashboardViolations: function(el, oArgs){

    		
            var elTabContainer = $D.getElementsByClassName('violations-tabview', null, el)[0];
            $W.DashboardViolations.superclass.constructor.call(this, elTabContainer);
            this._format(elTabContainer);
        },
        /**
         * Formats the markups based on the format attribute of elements having class format.
         * Using the format attribute value the appropriate element formatter is used
         * from $W.DashboardViolations.FORMATTERS
         */
        _format: function(el){
            var elFormats = $D.getElementsByClassName("format", null, el);
            for (var i = 0; i < elFormats.length; i++) {
                var sFormatter = $D.getAttribute(elFormats[i], "format");
                if (sFormatter && sFormatter.length > 0 && $W.DashboardViolations.FORMATTERS[sFormatter]) {
                    $W.DashboardViolations.FORMATTERS[sFormatter].call(this, elFormats[i]);
                }
            }
        }
    });
})();
