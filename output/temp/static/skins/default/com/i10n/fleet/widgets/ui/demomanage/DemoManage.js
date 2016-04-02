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
     * Manage Widget for Demo
     *
     * @author sabarish
     */
    $W.DemoManage = function(el, oArgs) {
        $W.DemoManage.superclass.constructor.call(this);
        /*Subscribing to events*/
        this.subscribe("activeTabChange", this._refreshTab, null, this);
        this.initDemoManage(el, oArgs);
    };
    $L.extend($W.DemoManage, $YW.TabView);
    $L.augmentObject($W.DemoManage.prototype, {
        _oPassableParams: ["action"],
        _widgets: {},
        /**
         * Initializes the widget
         * @param {Object} el
         * @param {Object} oArgs
         */
        initDemoManage: function(el, oArgs) {
            if (_publish && _publish.demo && _publish.demo.manage) {
                var sSubPageID = null;
                var oData = _publish.demo.manage;
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
                    var sPageLink = "@APP_CONTEXT@/view/controlpanel/?markup=" + oTabData.widget +"&" + this._getURLParams();
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
            if (_publish && _publish.demo && _publish.demo.manage) {
                var oData = _publish.demo.manage;
                var sWidgetName = oData[sTabID].widget;
                if ($L.isFunction($W[sWidgetName])) {
                    var oWidgetInstance = new $W[sWidgetName](oArgs.tab.get("contentEl"), {});
                    this._widgets[sWidgetName] = oWidgetInstance;
                }
            }
        },
        _refreshTab: function(oArgs) {
            var sTabID = oArgs.newValue.get("pageID");
            if (_publish && _publish.demo && _publish.demo.manage) {
                var oData = _publish.demo.manage;
                var sWidgetName = oData[sTabID].widget;
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
