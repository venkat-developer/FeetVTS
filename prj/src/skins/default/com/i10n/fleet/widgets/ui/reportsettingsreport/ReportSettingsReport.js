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
     * @extends com.i10n.fleet.widget.ui.UpdatableReport
     * @author subramaniam
     */
    $W.ReportSettingsReport = function(el, oArgs){
        this.initReportSettingsReport(el, oArgs);
    };
    $L.extend($W.ReportSettingsReport, $W.UpdatableReport);
    $L.augmentObject($W.ReportSettingsReport.prototype, {
        /**
         * Initializes widget
         * @param {Object} el
         * @param {Object} params
         */
        initReportSettingsReport: function(el, oArgs){
            var oConfig = this.constructConfig(oArgs.datasource);
          
            $W.ReportSettingsReport.superclass.constructor.call(this, el, oConfig);
            this.addEventListeners();
           /*
			    if(oConfig)
	            {
	            	var oRecord = new $W.Report(el, oConfig);
	             
	            	
	            }
            */
            $W.SearchToolBar.setDataTable(el, oConfig);
            
        },
        addEventListeners: function(){
            this.subscribe("dropdownChangeEvent", function(oArgs){
                var elDropdown = oArgs.target;
                var oRecord = this.getRecord(elDropdown);
                this.getRecordSet().updateRecordValue(oRecord, this.getColumn(elDropdown).key, elDropdown.options[elDropdown.selectedIndex].value);
            }, this, true);
            
        },
        
        /**
         * Constructs a config object for Reports to initialize from the data given
         *
         */
        constructConfig: function(oDataSource){
            var config = {};
            var columndefs = [{
                key: "id",
                hidden: true
            }, {
                key: "name",
                label: "Name",
                sortable: true,
                sortOptions: {
                    defaultDir: $YW.DataTable.CLASS_DESC
                },
                resizeable: false
            }, {
                key: "email",
                label: "E-mail Id",
                sortable: true,
                resizeable: false,
                formatter: "email"
            }, {
                key: "schedule",
                label: "schedule",
                sortable: true,
                formatter: "dropdown",
                dropdownOptions: ["Daily", "Weekly", "Monthly"]
            }, {
                key: "vehiclestatistics",
                label: "vehicle statistics",
                resizeable: false,
                formatter: "checkbox"
            }, {
                key: "vehiclestatus",
                label: "vehicle status",
                resizeable: false,
                formatter: "checkbox"
            }, {
                key: "offlinevehiclereport",
                label: "offline vehicle report",
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
