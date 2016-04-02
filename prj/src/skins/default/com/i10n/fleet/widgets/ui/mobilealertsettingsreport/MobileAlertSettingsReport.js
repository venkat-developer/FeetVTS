(function(){
    var $L = YAHOO.lang;
    var $YU = YAHOO.util;
    var $E = YAHOO.util.Event;
    var $D = YAHOO.util.Dom;
    var $YW = YAHOO.widget;
    var $U = getPackageForName("com.i10n.fleet.Utils");
    var $W = getPackageForName("com.i10n.fleet.widget.ui");
    var $V = getPackageForName("com.i10n.fleet.widget.view");
    /**
     * Report Settings Report Widget for Fleetcheck project.
     *
     * @extends com.i10n.fleet.widget.ui.Report
     * @author subramaniam
     */
    $W.MobileAlertSettingsReport = function(el, oArgs){
        this.initMobileAlertSettingsReport(el, oArgs);
    };
    $L.extend($W.MobileAlertSettingsReport, $W.UpdatableReport);
    $L.augmentObject($W.MobileAlertSettingsReport.prototype, {
        /**
         * Object Map of widgets initialized by the this widget
         */
        _widgets: {},
        /**
         * Initializes widget
         * @param {Object} el
         * @param {Object} params
         */
        initMobileAlertSettingsReport: function(el, oArgs){
            var oConfig = this.constructConfig(oArgs.datasource);
            $W.MobileAlertSettingsReport.superclass.constructor.call(this, el, oConfig);
           $W.MobileSearchToolBar.setMobileDataTable(el, oConfig);
        },
        /**
         * Constructs a config object for Reports to initialize from the data given
         * @param {Object} reportData
         */
        constructConfig: function(oDataSource){
        	var config = {};
            var columndefs = [{
                key: "id",
                hidden: true
            }, {
                key: "name",
                label: "Name",
                sortable: true
            }, {
                key: "mobilenumber",
                label: "Mobile Number",
                sortable: false
            }, {
                key: "overspeeding",
                label: "Over Speeding",
                resizeable: false,
                formatter: "checkbox"
            }, {
                key: "geofencing",
                label: "Geo Fencing",
                resizeable: false,
                formatter: "checkbox"
            
            }, {
                key: "chargerdisconnected",
                label: "Charger Disconnected",
                resizeable: false,
                formatter: "checkbox"
            }];
            config.datasource = oDataSource;
            config.columndefs = columndefs;
            config.reportconfig = {
                paginator: null
            };
            config.options = {
                "select": {
                    "enabled": true,
                    "selected": true
                }
            };
            return config;
        }
    });
})();

