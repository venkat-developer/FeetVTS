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
     * Manage Widget for Admin
     *
     * @author sabarish
     */
    $W.AdminManage = function(el, oArgs) {
        $W.AdminManage.superclass.constructor.call(this);
        /*Subscribing to events*/
        this.subscribe("activeTabChange", this._refreshTab, null, this);
        this.initAdminManage(el, oArgs);
    };
    $L.extend($W.AdminManage, $YW.TabView);
    $L.augmentObject($W.AdminManage.prototype, {
        _oPassableParams: ["action"],
        _widgets: {},
        /**
         * Initializes the widget
         * @param {Object} el
         * @param {Object} oArgs
         */
        initAdminManage: function(el, oArgs) {
            if (_publish && _publish.admin && _publish.admin.manage) {
                var sSubPageID = null;
                var oData = _publish.admin.manage;
                if (_publish && _publish.parameters && _publish.parameters.current && _publish.parameters.current.subpage) {
                    var sPageID = _publish.parameters.current.subpage;
                    if (oData[sPageID]) {
                        sSubPageID = sPageID;
                    }
                }
                for (var sTabID in oData) {
                    var oTabData = oData[sTabID];
                    var bActive = false;
                    if (sSubPageID) {
                        if (sTabID == sSubPageID) {
                            bActive = true;
                        }
                    }
                    else if (oTabData["default"]) {
                        bActive = true;
                    }
                    var sPageLink = "/fleet/view/controlpanel/?markup=" + oTabData.widget +"&" + this._getURLParams();
                    var oTabAttr = {
                        label: oTabData.title,
                        cacheData: true,
                        active: bActive,
                        dataSrc: sPageLink
                    };
                    var oTab = new $YW.Tab(oTabAttr);
                    oTab.register("pageID", {
                        value: sTabID
                    });
                    oTab.subscribe("dataLoadedChange", function(oArgs, oSelf) {
                        if (oArgs.newValue) {
                            oSelf.onTabDataLoaded.call(oSelf, {
                                tab: this,
                                loaded: true
                            });
                        }
                    }, this, oTab);
                    this.addTab(oTab);
                }
                var elTabView = $D.getElementsByClassName("yui-navset", null, el)[0];
                this.appendTo(elTabView);
            }
        },
        /**
         * Triggered when a tab is loaded for the first time. This will initialize the widget to which the tab belongs to.
         * @param {Object} oArgs
         */
        onTabDataLoaded: function(oArgs) {
            var sTabID = oArgs.tab.get("pageID");
            if (_publish && _publish.admin && _publish.admin.manage) {
                var oData = _publish.admin.manage;
                var sWidgetName = oData[sTabID].widget;
                if(sWidgetName==="HardwareManager"){
					document.title="Fleetcheck V2.0|Control Panel|Hardware";
				}
                if ($L.isFunction($W[sWidgetName])) {
                    var oWidgetInstance = new $W[sWidgetName](oArgs.tab.get("contentEl"), {});
                    this._widgets[sWidgetName] = oWidgetInstance;
                }
            }
        },
        _refreshTab: function(oArgs) {
            var sTabID = oArgs.newValue.get("pageID");
            if (_publish && _publish.admin && _publish.admin.manage) {
                var oData = _publish.admin.manage;
                var sWidgetName = oData[sTabID].widget;
                if(sWidgetName==="HardwareManager"){
					document.title="Fleetcheck V2.0|Control Panel|Hardware";
				}else if(sWidgetName==="VehicleManager"){
					document.title="Fleetcheck V2.0|Control Panel|Vehicle";
				}else if(sWidgetName==="UserManager"){
					document.title="Fleetcheck V2.0|Control Panel|User";
				}else if(sWidgetName==="DriverManager"){
					document.title="Fleetcheck V2.0|Control Panel|Driver";
				}else if(sWidgetName==="GroupManager"){
					document.title="Fleetcheck V2.0|Control Panel|Group";
				}else if(sWidgetName==="LogManager"){
					document.title="Fleetcheck V2.0|Control Panel|Log";
				}else if(sWidgetName==="FuelCalibrationManager"){
					document.title="Fleetcheck V2.0|Control Panel|FuelCalibration";
				}
                if (this._widgets[sWidgetName] && $L.isFunction(this._widgets[sWidgetName].refreshWidget)) {
                    this._widgets[sWidgetName].refreshWidget();
                }
            }
        },
        /**
         * Returns the URL Params that is to be passed to the tab
         */
        _getURLParams: function() {
            var sURLParams = "";
            if (_publish.parameters.current) {
                var oParams = _publish.parameters.current;
                var aPassParams = this._oPassableParams;
                for (var i = 0; i < aPassParams.length; i++) {
                    if (oParams[aPassParams[i]]) {
                        sURLParams = sURLParams + aPassParams[i] + "=" + oParams[aPassParams[i]];
                        if (i < (aPassParams.length - 1)) {
                            sURLParams = sURLParams + "&";
                        }
                    }
                }
            }
            return sURLParams;
        }
    });
})();
