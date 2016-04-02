(function(){
    var $W = getPackageForName("com.i10n.fleet.widget.ui");
    var $L = YAHOO.lang;
    var $E = YAHOO.util.Event;
    var $D = YAHOO.util.Dom;
    var $YU = YAHOO.util;
    var $U = getPackageForName("com.i10n.fleet.Utils");
    /**
     * Driver Report Sub Nav Page
     *
     * @author aravind
     */
    $W.DriverStatsReport = function(el, oArgs){
        this.init(el, oArgs);
    };
    $L.augmentObject($W.DriverStatsReport, {
    });
    $L.extend($W.DriverStatsReport, $W.GraphReport, {
        render: function(){
            var oData = this.get($W.BaseReport.ATTR_DATA);
            var sDriverID = this.get($W.BaseReport.ATTR_SELECTED_ITEM);
            var elContent = $D.getElementsByClassName("main-bd", null, this.elBase)[0];
            var elDialog = $D.getElementsByClassName("simpledialog", null, this.elBase)[0];
            if (oData && oData.drivers && oData.drivers[sDriverID]) {
                if (!$D.hasClass(elDialog, "disabled")) {
                    $D.addClass(elDialog, "disabled");
                }
                if ($D.hasClass(elContent, "disabled")) {
                    $D.removeClass(elContent, "disabled");
                }
                
                /*Setting the contents of the graph*/
//                $W.DriverStatsReport.superclass.render.call(this);
                /*Setting the driver information*/
                
                var aElInfo = $D.getElementsByClassName("info", null, this.elBase);
                for (var i = 0; i < aElInfo.length; i++) {
                    var elInfo = aElInfo[i];
                    var sAttr = $D.getAttribute(elInfo, "attr");
                    if (sAttr && oData.drivers[sDriverID][sAttr]) {
                        elInfo.innerHTML = oData.drivers[sDriverID][sAttr];
                    }
                }
            }
            else {
                if ($D.hasClass(elDialog, "disabled")) {
                    $D.removeClass(elDialog, "disabled");
                }
                if (!$D.hasClass(elContent, "disabled")) {
                    $D.addClass(elContent, "disabled");
                }
            }
        },
        getDataURL: function(){
            var sURL = null;
            var sDriverID = this.get($W.BaseReport.ATTR_SELECTED_ITEM);
            var oTimeFrame = this.get($W.BaseReport.ATTR_SELECTED_TIMEFRAME);
            if (sDriverID && oTimeFrame) {
                sURL = "/fleet/view/reports/?module=/blocks/json&data=view&report=driverstatsreport&driverID=" + sDriverID;
                sURL += "&localTime=" +$U.getLocalTime();
                sURL += "&startdate=" + oTimeFrame.startDate;
                sURL += "&enddate=" + oTimeFrame.endDate;
            }
            return sURL;
        }
    });
    $L.augmentObject($W.DriverStatsReport.prototype, {
        /**
         * Initialization function
         */
        init: function(el, oArgs){
        	   if (!oArgs) {
                   oArgs = {};
               }
               oArgs.dataType = $W.BaseReport.KEY_TYPE_JSON;
               $W.DriverStatsReport.superclass.constructor.call(this, el, oArgs);
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
        }
    });
})();
