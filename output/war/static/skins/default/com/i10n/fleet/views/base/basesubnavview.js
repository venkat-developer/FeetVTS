(function() {
    var $B = YAHOO.Bubbling;
    var $L = YAHOO.lang;
    var $YU = YAHOO.util;
    var $E = YAHOO.util.Event;
    var $D = YAHOO.util.Dom;
    var $YW = YAHOO.widget;
    var $W = getPackageForName("com.i10n.fleet.widget.ui");
    var $U = getPackageForName("com.i10n.fleet.Utils");
    var $V = getPackageForName("com.i10n.fleet.widget.view");
    /**
     * Base View For Views which have Header Sub Navigator. Provides all the
     * functionalities of Tab Loading along with it
     *
     * @author sabarish
     */
    $V.BaseSubNavView = function() {
        $V.BaseSubNavView.superclass.constructor.call(this);
    };
    $L.augmentObject($V.BaseSubNavView, {
        EVT_ON_PAGE_RESIZE: "onPageResize",
        ID_NAV_PAGE: "page"
    });
    $L.extend($V.BaseSubNavView, $V.BaseView, {
        /**
         * Initialization function
         */
        init: function(params) {
            this.onPageResize = new $YU.CustomEvent('onPageResize', this, true);
            $V.BaseSubNavView.superclass.init.call(this, params);
            this._addResizeHook();
            var subNavEl = $D.get("headersubnav");
            var subNavInstance = new $W.HeaderSubNav(subNavEl);
            this._widgets.subnav = subNavInstance;
            /**
             * Subscribing to activeTabChange in Sub Navigator
             */
            if (this._widgets.subnav && !this._widgets.subnav.get($W.HeaderSubNav.ATTR_IS_PRE_MARKUP_LOAD)) {
                subNavInstance.subscribe("lastTabDataLoadedChange", this.onTabLoad, this, true);
            }
            else {
                this.initializeWidgets(subNavEl);
            }
            subNavInstance.subscribe("activeTabChange", this._onActiveTabChange, this, true);
            this.resizePage();
        }
    });
    $L.augmentObject($V.BaseSubNavView.prototype, {
        onPageResize: null,
        _resizables: {},
        _isResizeHookAdded: false,
        /**
         * Called when tab of the subnav is changed
         * @param {Object} oArgs
         */
        _onActiveTabChange: function(oArgs) {
            var oNewTab = oArgs.newValue;
            var sPageId = oNewTab.get("navId");
            if(sPageId==="tripsettings"){
				document.title="Fleetcheck V2.0|Control Panel|Trip Settings";
			}else if(sPageId==="geofencing"){
				document.title="Fleetcheck V2.0|Control Panel|Geo Fencing";
			}else if(sPageId==="reportsettings"){
				document.title="Fleetcheck V2.0|Control Panel|Report Settings";
			}else if(sPageId==="alertsettings"){
				document.title="Fleetcheck V2.0|Control Panel|Alert Settings";
			}else if(sPageId==="mobilealertsettings"){
				document.title="Fleetcheck V2.0|Control Panel|Mobile Alert Settings";
			}
			else if(sPageId==="systemsettings"){
				document.title="Fleetcheck V2.0|Control Panel|System Settings";
			}
            this._resizeSubNavPage(sPageId);
            this._resizeSubNavPage($V.BaseSubNavView.ID_NAV_PAGE);
            this.onPageResize.fire();
        },
        /**
         * Adds a resize hook on the resizable elements in the subnav page.
         * If the navId is not specified , the whole of the page is taken.
         * @param {Object} navId
         * @param {Object} el
         */
        _addResizeHook: function(navId, el) {
            var elResizables = $D.getElementsByClassName("cnt-resizable", null, el);
            if (!(navId)) {
                navId = $V.BaseSubNavView.ID_NAV_PAGE;
            }
            if (elResizables.length > 0) {
                this._resizables[navId] = elResizables;
                if (!this._isResizeHookAdded) {
                    $E.addListener(window, 'resize', this.resizePage, this, true);
                    this._isResizeHookAdded = true;
                }
            }
        },
        /**
         * Resizes all the resizable elements in the page.
         */
        resizePage: function() {
            for (var navId in this._resizables) {
                this._resizeSubNavPage(navId);
            }
            this.onPageResize.fire();
        },
        /**
         * Resizes the resizable elements in the subnav page.
         * @param {Object} navId
         */
        _resizeSubNavPage: function(navId) {
            if (this._resizables[navId]) {
                for (var i = 0; i < this._resizables[navId].length; i++) {
                    var elResizable = this._resizables[navId][i];
                    var nViewportHeight = $D.getViewportHeight();
                    var nRegion = $D.getRegion(elResizable);
                    if (nRegion) {
                        var nOffset = nRegion.top;
                        if (!$D.hasClass(elResizable, "minht")) {
                            $D.setStyle(elResizable, "height", (nViewportHeight - nOffset) + "px");
                        }
                        else {
                            $D.setStyle(elResizable, "minHeight", (nViewportHeight - nOffset) + "px");
                        }
                    }
                }
            }
        },
        /**
         * Called when a Tab in HeaderSubNav is loaded
         */
        onTabLoad: function(oArgs) {
            try {
                var oNewTab = oArgs.newValue;
                this.loadTab(oNewTab);
            } 
            catch (ex) {
                $U.alert({
                    message: "There was an error loading the tab! \nPlease reload the page or tab.\nDetails : \n" + ex
                });
            }
        },
        /**
         * Loads a Tab and initializes the widget registered with the tab
         */
        loadTab: function(oTab) {
            if ($L.isObject(oTab) && oTab.get("dataLoaded")) {
                var sPageId = oTab.get("navId");
                if (!this._widgets[sPageId]) {
                    var currentView = _publish.parameters.current.view;
                    if (_publish.sitemap.sites &&
                    _publish.sitemap.sites[currentView] &&
                    _publish.sitemap.sites[currentView].subnav &&
                    _publish.sitemap.sites[currentView].subnav[sPageId] &&
                    _publish.sitemap.sites[currentView].subnav[sPageId].widget) {
                        var sWidgetName = _publish.sitemap.sites[currentView].subnav[sPageId].widget;
                        var el = $D.getElementsByClassName(sWidgetName.toLowerCase(), null, oTab.get("contentEl"))[0];
                        this.loadWidget(el, sWidgetName, null, sPageId);
                        this._addResizeHook(sPageId, el);
                        this._resizeSubNavPage(sPageId);
                        this._resizeSubNavPage($V.BaseSubNavView.ID_NAV_PAGE);
                        this.onPageResize.fire();
                    }
                }
            }
        },
        loadWidget: function(el, sWidgetName, oWidgetArgs, sPageId) {
        	if(sPageId==="tripsettings"){
				document.title="Fleetcheck V2.0|Control Panel|Trip Settings";
			}
            if ($L.isObject(el) && $L.isFunction($W[sWidgetName])) {
                var oInstance = new $W[sWidgetName](el, oWidgetArgs);
                this._widgets[sPageId] = oInstance;
            }
        },
        /**
         * Initializes all the widgets when in preload markup mode.
         * @param {Object} subNavEl
         */
        initializeWidgets: function(subNavEl) {
            var currentView = _publish.parameters.current.view;
            var pages = _publish.sitemap.sites[currentView].subnav;
            var container = $D.getElementsByClassName("nav-container", null, subNavEl);
            for (var pageKey in pages) {
                var sWidgetName = pages[pageKey].widget;
                var el = $D.getElementsByClassName(sWidgetName.toLowerCase(), null, container[0])[0];
                if ($L.isObject(el) && $L.isFunction($W[sWidgetName])) {
                    var oInitArgs = {};
                    if (_publish.parameters.current.subnav) {
                        if (pageKey == _publish.parameters.current.subnav) {
                            oInitArgs["default"] = true;
                        }
                    }
                    else if (pages[pageKey]["default"]) {
                        oInitArgs["default"] = true;
                    }
                    this.loadWidget(el, sWidgetName, oInitArgs, pageKey);
                }
            }
        }
    });
})();
