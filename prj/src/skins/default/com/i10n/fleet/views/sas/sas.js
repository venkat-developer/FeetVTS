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
     * Eta Panel View Widget. Has a SubNav View
     *
     * @author sabarish
     */
    $V.Sas = function() {

        /**
         * Initialization Function
         */
    	document.title="Fleetcheck V2.0|SAS";
        this.init = function(params) {
        	
            $V.Sas.superclass.init.call(this, params);
           
            /**
             * Adding VehicleList and DriverList widgets
             */
            var vehicleListEl = $D.get("vehiclelist");
            var vehicleList = new $W.VehicleList(vehicleListEl, {
                datasource: this.getVehicleDataSource()
            });
            this._widgets.vehiclelist = vehicleList;
            this._widgets.vehiclelist.disable();
           // var stopListEl = $D.get("stoplist");
           // var stopList = new $W.EtaDisplaySideBar(stopListEl);
            //this._widgets.stoplist = stopList;
           // this._widgets.stoplist.disable();
            var stopListEl = $D.get("stoplist");
            var stopList = new $W.StopList(stopListEl, {
                datasource: this.getStopDataSource()
            });
            this._widgets.stoplist = stopList;
            this._widgets.stoplist.disable();
            /**
             * Subscribing DriverList & VehicleList for their resize event.
             */
            //stopList.subscribe($W.MinimizableList.EVT_RESIZE, this.onSidePaneListResize, this, true);
            vehicleList.subscribe($W.MinimizableList.EVT_RESIZE, this.onSidePaneListResize, this, true);
            vehicleList.subscribe($W.VehicleList.EVT_SELECTED_VEHICLE_CHANGE, this._onVehicleChange, this, true);
             stopList.subscribe($W.MinimizableList.EVT_RESIZE, this.onSidePaneListResize, this, true);
            /**
             * Subscribing for the selected vehicle and driver change
             */
            //stopList.subscribe($W.StopList.EVT_SELECTED_DRIVER_CHANGE, this.onSelectedDriverChange, this, true);
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
                if (_publish.parameters.current && _publish.parameters.current.stopID) {
                    this._widgets.stoplist.set($W.StopList.ATTR_SELECTED_STOP, _publish.parameters.current.stopID);
                }
            }
        };
        /**
         * triggered when there is a change in vehicle.
         */
        this._onVehicleChange = function(oArgs) {
            if (this._widgets.sasviolations) {
                this._widgets.sasviolations.set($W.BaseReport.ATTR_SELECTED_ITEM, oArgs.newValue);
            } 
            if (this._widgets.scheduledreport) {
                this._widgets.scheduledreport.set($W.BaseReport.ATTR_SELECTED_ITEM, oArgs.newValue);
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
        	 if (pageId === "sasviolations") {
                 document.title="Fleetcheck V2.0|SAS|SAS Violation";
                 this._widgets.stoplist.disable();
                 this._widgets.vehiclelist.enable();
              }
              else if(pageId === "scheduledreport"){
              	document.title="Fleetcheck V2.0|SAS|Scheduled Report";
              	this._widgets.stoplist.disable();
                  this._widgets.vehiclelist.enable();
              }
              else if (pageId === "etadisplay") {
              	document.title="Fleetcheck V2.0|SAS|ETA Display";
                  this._widgets.vehiclelist.disable();
                  this._widgets.stoplist.enable();
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
                    fields: ["id", "name", "status", "chargerdc", "gps", "gsm", "battery", "fuel", "lastupdated", "lat", "lon", "location", "make", "model", "speed", "drivername"]
                };
            }
            return oDataSource;
        };
this.getStopDataSource = function() {
               var oDataSource = null;
            if (_publish.stops && _publish.stops.stopData) {
                oDataSource = new $W.GroupedReport.GroupedDataSource(_publish.stops.stopData);
                oDataSource.responseType = $YU.DataSource.TYPE_JSARRAY;
                oDataSource.responseSchema = {
                elementField: "stops",
                    fields: ["id", "name", "ownerid", "knownas", "lat", "lon", "assigned", "group"]
                };
                }
            return oDataSource;
        };
        $V.Sas.superclass.constructor.call(this);
    };
    YAHOO.extend($V.Sas, $V.BaseSubNavView);
})();
