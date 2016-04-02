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
     * Reports View Widget. Has a SubNav View
     *
     * @author sabarish,irk,aravind
     */
    $V.Reports = function() {
        /**
         * Initialization Function
         */
    	document.title="Fleetcheck V2.0|Reports";
        this.init = function(params) {
        	
            $V.Reports.superclass.init.call(this, params);
           
            /**
             * Adding VehicleList and DriverList widgets
             */
            var vehicleListEl = $D.get("vehiclelist");
            var vehicleList = new $W.VehicleList(vehicleListEl, {
                datasource: this.getVehicleDataSource()
            });
            this._widgets.vehiclelist = vehicleList;
            this._widgets.vehiclelist.disable();
            var driverListEl = $D.get("driverlist");
            var driverList = new $W.DriverList(driverListEl, {
                datasource: this.getDriverDataSource()
            });
            this._widgets.driverlist = driverList;
            this._widgets.driverlist.disable();
            /**
             * Subscribing DriverList & VehicleList for their resize event.
             */
            driverList.subscribe($W.MinimizableList.EVT_RESIZE, this.onSidePaneListResize, this, true);
            vehicleList.subscribe($W.MinimizableList.EVT_RESIZE, this.onSidePaneListResize, this, true);
            vehicleList.subscribe($W.VehicleList.EVT_SELECTED_VEHICLE_CHANGE, this._onVehicleChange, this, true);
            /**
             * Subscribing for the selected vehicle and driver change
             */
            driverList.subscribe($W.DriverList.EVT_SELECTED_DRIVER_CHANGE, this.onSelectedDriverChange, this, true);
            /**
             * Subscribing to activeTabChang in Sub Navigator
             */
            this._widgets.subnav.subscribe("activeTabChange", this.onTabChange, this, true);
            /**
             * To maintain required state if the url is deeplinked
             */
            this.changeTo(this._widgets.subnav.get("activeTab").get("navId"));
            this.handleDeepLinks();
        };
        /**
         * Handles Deep Links to any specific subnav Reports Page
         */
        this.handleDeepLinks = function() {
            if (_publish && _publish.parameters) {
                if (_publish.parameters.current && _publish.parameters.current.vehicleID) {
                    this._widgets.vehiclelist.set($W.VehicleList.ATTR_SELECTED_VEHICLE, _publish.parameters.current.vehicleID);
                }
                if (_publish.parameters.current && _publish.parameters.current.driverID) {
                    this._widgets.driverlist.set($W.DriverList.ATTR_SELECTED_DRIVER, _publish.parameters.current.driverID);
                }
            }
        };
        /**
         * triggered when there is a change in vehicle.
         */
        this._onVehicleChange = function(oArgs) {
        	if (this._widgets.vehiclereport) {
            	this._widgets.vehiclereport.set($W.BaseReport.ATTR_SELECTED_ITEM, oArgs.newValue);
            }
            if (this._widgets.activityreport) {
            	this._widgets.activityreport.set($W.BaseReport.ATTR_SELECTED_ITEM, oArgs.newValue);
            }
            if(this._widgets.activityreportvehicle){
            	this._widgets.activityreportvehicle.set($W.BaseReport.ATTR_SELECTED_ITEM, oArgs.newValue);
            }
            if(this._widgets.vehiclehistory){
            	this._widgets.vehiclehistory.set($W.BaseReport.ATTR_SELECTED_ITEM, oArgs.newValue);
            }
            if(this._widgets.tncsc){
            	this._widgets.tncsc.set($W.BaseReport.ATTR_SELECTED_ITEM, oArgs.newValue);
            }
            if(this._widgets.vehiclestats){
            	this._widgets.vehiclestats.set($W.BaseReport.ATTR_SELECTED_ITEM, oArgs.newValue);
            }
        };
        /**
         * Executed when Tabs in HeaderSubNavigator is changed. Rite now
         * used only to hide/show the VehicleList Widget based on the view
         */
        this.onTabChange = function(event) {
        	var oldTab = event.prevValue;
            var newTab = event.newValue;
            var prevPageId = oldTab.get("navId");
            if (this._widgets[prevPageId] && this._widgets[prevPageId].hide) {
            	this._widgets[prevPageId].hide();
            }
            var pageId = newTab.get("navId");
            this.changeTo(pageId);
            if (this._widgets[pageId] && this._widgets[pageId].show && $L.isFunction(this._widgets[pageId].show)) {
                this._widgets[pageId].show.call(this._widgets[pageId]);
            }
        };
        /**
         * Change the state of the page to the required state in the page id given
         * @param {Object} pageId
         */
        this.changeTo = function(pageId) {
        	if (pageId === "vehiclereport" || pageId === "activityreportvehicle" || pageId === "tncsc" || pageId === "vehiclehistory"){
        		document.title="FleetCheck V2.0|Reports|Vehicle Report";
        		this._widgets.driverlist.disable();
                this._widgets.vehiclelist.enable();
            }
            else if (pageId === "driverreport") {
            	document.title="FleetCheck V2.0|Reports|Driver Report";
                this._widgets.vehiclelist.disable();
                this._widgets.driverlist.enable();
            }
			else if(pageId === "activityreport"){
				document.title="FleetCheck V2.0|Reports|Activity Report";
				this._widgets.driverlist.disable();
                this._widgets.vehiclelist.enable();
			
			}//Comenting For Side Panel Vehicle List 
        	/*else if(pageId==="vehiclestats"){
				document.title="FleetCheck V2.0|Reports|Vehicle Statistics";
				this._widgets.driverlist.disable();
                this._widgets.vehiclelist.enable();
			}*/
            else {
			document.title="FleetCheck V2.0|Reports|Violation Report";
                this._widgets.vehiclelist.disable();
                this._widgets.driverlist.disable();
            }
            // $W.ReportTimeFrame._oDialog.hide();
        };
        /**
         * Executed when SidePaneList (VehicleList/DriverList) is minimized/maximized.
         */
        this.onSidePaneListResize = function(oArgs, oSelf) {
            var elContainer = $D.getElementsByClassName("view-container")[0];
            if ($W.MinimizableList.STATE_MINIMIZED == oArgs.currentState) {
                if (!$D.hasClass(elContainer, "list-minimized")) {
                    $D.addClass(elContainer, "list-minimized");
                }
            }
            else {
                if ($D.hasClass(elContainer, "list-minimized")) {
                    $D.removeClass(elContainer, "list-minimized");
                }
            }
        };
        /**
         * Generates a Vehicle DataSource
         */
        this.getVehicleDataSource = function() {
            var oDataSource = null;
            if (_publish.vehicles && _publish.vehicles.vehicleData) {
                oDataSource = new $W.GroupedReport.GroupedDataSource(_publish.vehicles.vehicleData);
                oDataSource.responseType = $YU.DataSource.TYPE_JSARRAY;
                oDataSource.responseSchema = {
                    elementField: "vehicles",
                    fields: ["id", "name", "status", "chargerdc", "gps", "gsm", "battery", "fuel", "lastupdated","seatbelt","ignition", "lat", "lon", "location", "make", "model", "speed", "drivername","mobilenumber"]
                };
            }
            return oDataSource;
        };
        this.getDriverDataSource = function() {
            var oDataSource = null;
            if (_publish.drivers && _publish.drivers.driverData) {
                oDataSource = new $W.GroupedReport.GroupedDataSource(_publish.drivers.driverData);
                oDataSource.responseType = $YU.DataSource.TYPE_JSARRAY;
                oDataSource.responseSchema = {
                    elementField: "drivers",
                    fields: ["id", "name", "status", "firstname", "lastname", "license", "maxspeed", "avgspeed", "distance", "assigned", "group"]
                };
            }
            return oDataSource;
        };
        /**
         * Executed when the selected driver in the driverlist changes
         * @param {Object} oArgs
         * @param {Object} oSelf
         */
        this.onSelectedDriverChange = function(oArgs, oSelf) {
            if (this._widgets && this._widgets.driverreport) {
                this._widgets.driverreport.set($W.BaseReport.ATTR_SELECTED_ITEM, oArgs.newValue);
            }
        };
        $V.Reports.superclass.constructor.call(this);
    };
    YAHOO.extend($V.Reports, $V.BaseSubNavView);
})();
