(function() {
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
     * Abstract class for all the Admin Managers that have actions add,assign,edit,delete.
     *
     * @author sabarish
     */
    $W.ActionManager = function(el, oArgs) {
        var elTabView = $D.getElementsByClassName("tabview-cnt", null, el)[0];
        $W.ActionManager.superclass.constructor.call(this, elTabView);
        this.subscribe("activeTabChange", this._onActionTabChange, this, true);
        /*Initializing the default tab*/
        this._initializeDefaultTab();
    };
    $L.extend($W.ActionManager, $YW.TabView);
    $L.augmentObject($W.ActionManager.prototype, {
        _widgets: {},
        /**
         * Triggered when active tab of the tabview is changed
         * @param {Object} oArgs
         */
        _onActionTabChange: function(oArgs) {
            var oNewTab = oArgs.newValue;
            var elTabContent = oNewTab.get("contentEl");
            this._loadWidgets(elTabContent);
        },
        _onRootTabChage: function(oArgs) {
            this._initializeDefaultTab();
        },
        _initializeDefaultTab: function() {
            var oActiveTab = this.get('activeTab');
            var elTabContent = oActiveTab.get("contentEl");
            this._loadWidgets(elTabContent);
        },
        /**
         * Utility funtion to load(initalize or refresh) a tab
         * @param {Object} elTabContent
         */
        _loadWidgets: function(elTabContent) {
            var sWidgetName = $D.getAttribute(elTabContent, "widget");
            switch(sWidgetName){
			case "AddHardware":
				document.title="Fleetcheck V2.0|ControlPanel|Hardware|Add";
				break;
			case "EditHardware":
				document.title="Fleetcheck V2.0|ControlPanel|Hardware|Edit";
				break;
			case "DeleteHardware":
				document.title="Fleetcheck V2.0|ControlPanel|Hardware|Delete";
				break;
			case "AddVehicle":
				document.title="Fleetcheck V2.0|ControlPanel|Vehicles|Add";
				break;
			case "EditVehicle":
				document.title="Fleetcheck V2.0|ControlPanel|Vehicles|Edit";
				break;
			case "AssignVehicle":
				document.title="Fleetcheck V2.0|ControlPanel|Vehicles|Assign";
				break;
			case "DeleteVehicle":
				document.title="Fleetcheck V2.0|ControlPanel|Vehicles|Delete";
				break;
			case "AddUser":
				document.title="Fleetcheck V2.0|ControlPanel|User|Add";
				break;
			case "EditUser":
				document.title="Fleetcheck V2.0|ControlPanel|User|Edit";
				break;
			case "DeleteUser":
				document.title="Fleetcheck V2.0|ControlPanel|User|Delete";
				break;
			case "AddDriver":
				document.title="Fleetcheck V2.0|ControlPanel|Driver|Add";
				break;
			case "EditDriver":
				document.title="Fleetcheck V2.0|ControlPanel|Driver|Edit";
				break;
			case "AssignDriver":
				document.title="Fleetcheck V2.0|ControlPanel|Driver|Assign";
				break;
			case "DeleteDriver":
				document.title="Fleetcheck V2.0|ControlPanel|Driver|Delete";
				break;
			case "AddGroup":
				document.title="Fleetcheck V2.0|ControlPanel|Group|Add";
				break;
			case "EditGroup":
				document.title="Fleetcheck V2.0|ControlPanel|Group|Edit";
				break;
			case "AssignVehiclesToGroup":
				document.title="Fleetcheck V2.0|ControlPanel|Group|Assign";
				break;
			case "AssignDriversToGroup":
				document.title="Fleetcheck V2.0|ControlPanel|Group|Assign";
				break;
			case "DeleteGroup":
				document.title="Fleetcheck V2.0|ControlPanel|Group|Delete";
				break;
			case "AddFuelCalibration":
				document.title="Fleetcheck V2.0|ControlPanel|Fuel Calibration|Add";
				break;
			case "EditFuelCalibration":
				document.title="Fleetcheck V2.0|ControlPanel|Fuel Calibration|Edit";
				break;
			case "DeleteFuelCalibration":
				document.title="Fleetcheck V2.0|ControlPanel|Fuel Calibration|Delete";
				break;
			default:
				break;
            }
            if (sWidgetName && $L.isFunction($W[sWidgetName])) {
                if (!(this._widgets[sWidgetName.toLowerCase()])) {
                    var elWidgetContent = $D.getElementsByClassName(sWidgetName.toLowerCase(), null, elTabContent)[0];
                    var oWidgetInstance = new $W[sWidgetName](elWidgetContent, this.getInnerWidgetArgs(sWidgetName));
                    this._widgets[sWidgetName.toLowerCase()] = oWidgetInstance;
                }
                else {
                    if (this._widgets[sWidgetName.toLowerCase()].refresh) {
                        this._widgets[sWidgetName.toLowerCase()].refresh();
                    }
                }
            }
        },
        /**
         * Refreshes the widget passed
         * @param {String} sWidgetName the name of the widget
         */
        refreshWidget: function() {
            var oActiveTab = this.get('activeTab');
            var elTabContent = oActiveTab.get("contentEl");
            var sWidgetName = $D.getAttribute(elTabContent, "widget");
            this._widgets[sWidgetName.toLowerCase()].refresh();
        },
        /**
         * Returns the Widget Args for the passed widget. All the inheritors can override this function to provide widget specific args
         * @param {Object} sWidgetName
         */
        getInnerWidgetArgs: function(sWidgetName) {
            return null;
        }
    });
})();
