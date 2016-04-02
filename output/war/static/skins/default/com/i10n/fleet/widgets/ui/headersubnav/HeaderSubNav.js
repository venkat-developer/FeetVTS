(function() {
    var $B = YAHOO.Bubbling;
    var $L = YAHOO.lang;
    var $YU = YAHOO.util;
    var $E = YAHOO.util.Event;
    var $D = YAHOO.util.Dom;
    var $YW = YAHOO.widget;
    var $W = getPackageForName("com.i10n.fleet.widget.ui");
    var $U = getPackageForName("com.i10n.fleet.Utils");
  
    /**
     * Custom Subnav tab used in Sub nav tabview
     * Extends Yahoo.widget.Tab
     * @param {Object} params
     */
    $W.SubNavTab = function(params) {
        $W.SubNavTab.superclass.constructor.call(this, params);
    };
    $L.extend($W.SubNavTab, $YW.Tab, {
        /**
         * Over-rided function used during dynamic load of tab
         * Uses our own custom Connection Manager
         */
        _dataConnect: function() {
            if (!$U.Connect) {
                return false;
            }
            var oCallback = {
                success: function(o) {
                    this.loadHandler.success.call(this, o);
                    this.set('dataLoaded', true);
                    this.dataConnection = null;
                    $D.removeClass(this.get('contentEl').parentNode, this.LOADING_CLASSNAME);
                    this._loading = false;
                },
                failure: function(o) {
                    this.loadHandler.failure.call(this, o);
                    this.dataConnection = null;
                    $D.removeClass(this.get('contentEl').parentNode, this.LOADING_CLASSNAME);
                    this._loading = false;
                },
                scope: this,
                timeout: this.get('dataTimeout')
            };
            $D.addClass(this.get('contentEl').parentNode, this.LOADING_CLASSNAME);
            this._loading = true;
            this.dataConnection = $U.Connect.asyncRequest(this.get('loadMethod'), this.get('dataSrc'), oCallback, this.get('postData'));
            oCallback = null;
            return true;
        }
    });
    /**
     * Header Sub Navigator Widget for Fleetcheck project
     *
     * @author sabarish
     */
    $W.HeaderSubNav = function(el, params) {
        /**
         * Since TabView Widget has its own init. let this function name be
         * initHeaderSubNav and not init
         */
        this.initHeaderSubNav = function(el, params) {
            var container = $D.getElementsByClassName("nav-container", null, el);
            if ($L.isArray(container) && container.length > 0) {
                var currentView = _publish.parameters.current.view;
                var isPreLoaded = ($D.getAttribute(container[0], "preMarkupLoad") == "true");
                if (!isPreLoaded) {
                    $W.HeaderSubNav.superclass.constructor.call(this);
                    this._configCustomAttributes();
                    this.set($W.HeaderSubNav.ATTR_IS_PRE_MARKUP_LOAD, false);
                    this._subscribeEvents();
                    if (_publish.sitemap && _publish.sitemap.sites &&
                    _publish.sitemap.sites[currentView] &&
                    _publish.sitemap.sites[currentView].subnav) {
                        this._configureSubNav(currentView);
                    }
                    this.appendTo(container[0]);
                    $U.showLayerOverlay();
                }
                else {
                    var pages = _publish.sitemap.sites[currentView].subnav;
                    $W.HeaderSubNav.superclass.constructor.call(this, container[0]);
                    this._configCustomAttributes();
                    this.set($W.HeaderSubNav.ATTR_IS_PRE_MARKUP_LOAD, true);
                    var tabs = this.get("tabs");
                    var i = 0;
                    for (var sPageID in pages) {
                        tabs[i].register("navId", {
                            value: sPageID
                        });
                        i++;
                    }
                }
                currentView = null;
            }
            container = null;
        };
        /**
         * Configures SubNav for the Given View in Post Loaded Mode.
         * @param {Object} currentView
         */
        this._configureSubNav = function(currentView) {
            var pages = _publish.sitemap.sites[currentView].subnav;
            var currentSubNavId = _publish.parameters.current.subnav;
            var isDeepLinked = false;
            if (currentSubNavId && _publish.sitemap.sites[currentView].subnav[currentSubNavId]) {
                isDeepLinked = true;
            }
            for (var pageKey in pages) {
                var pageTitle = pages[pageKey].title;
                var activeTab = false;
                if (isDeepLinked && pageKey === currentSubNavId) {
                    activeTab = true;
                }
                else if ((!isDeepLinked) && pages[pageKey]["default"]) {
                    activeTab = true;
                }
                var pageLink = "/fleet/view/" + currentView + "/?";
                if (pages[pageKey].request) {
                    pageLink = pageLink + pages[pageKey].request;
                }
                else {
                	pageLink = pageLink + "markup=" + pages[pageKey].widget;
                }
                var sURLParams = this._getURLParams();
                if (sURLParams && sURLParams.length > 0) {
                	pageLink = pageLink + "&" + sURLParams;
                }
                var tabAttr = {
                    label: pageTitle,
                    cacheData: true,
                    active: activeTab,
                    dataSrc: pageLink
                };
                var tab = new $W.SubNavTab(tabAttr);
                tab.register("navId", {
                    value: pageKey
                });
                tab.subscribe("dataLoadedChange", function(oArgs, oSelf) {
                    if (oArgs.newValue) {
                        oSelf.onTabDataLoaded.call(oSelf, {
                            tab: this,
                            loaded: true
                        });
                    }
                }, this, tab);
                this.addTab(tab);
                /**
                 * Avoiding possible memory leaks.
                 */
                tab = null;
                tabAttr = null;
                pageLink = null;
                pageTitle = null;
                activeTab = null;
            }
            pages = null;
            currentSubNavId = null;
            currentView = null;
        };
        /**
         * Configures Custom Attributes for Sub Nav.
         * 1. Configures A lastTabDataLoaded to signify changes in tabs
         */
        this._configCustomAttributes = function() {
            this.setAttributeConfig($W.HeaderSubNav.ATTR_DATA_LOADED, {
                value: null
            });
            /**
             * @attribute preMarkupLoad
             * @description if markup is pre loaded or dynamically loaded.
             * @type boolean
             */
            this.setAttributeConfig($W.HeaderSubNav.ATTR_IS_PRE_MARKUP_LOAD, {
                value: false,
                validator: $L.isBoolean
            });
        };
        /**
         * Called when data on a tab is loaded
         */
        this.onTabDataLoaded = function(params) {
            if (params && params.loaded) {
                this.set($W.HeaderSubNav.ATTR_DATA_LOADED, params.tab);
            }
            this.changeOverlay(params.tab);
        };
        /**
         * changes the overlay when the inner tabs of the view are navigated
         * @param {Object} tab
         */
        this.changeOverlay = function(oTab) {
            if (!oTab.get("dataLoaded")) {
                $U.showLayerOverlay();
            }
            else {
                $U.hideLayerOverlay();
            }
        };
        /**
         * Subscribes for the beforeActiveTabChange event
         */
        this._subscribeEvents = function() {
            this.subscribe("beforeActiveTabChange", function(oArgs) {
                this.changeOverlay(oArgs.newValue);
            });
        };
        /**
         * Returns the URL Params that is to be passed to the tab
         */
        this._getURLParams = function() {
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
        };
        /**
         * List if parameters that are to be passed to the tabs.
         */
        this._oPassableParams = ["action"];
        /**
         * Base Element on Widget
         */
        this.baseElement = el;
        /**
         * Init Params
         */
        this.initParams = params;
        this._isMarkupNeeded = true;
        this.initHeaderSubNav(this.baseElement, params);
    };
    YAHOO.extend($W.HeaderSubNav, $YW.TabView, {
        /**
         * The className to add when building from scratch.
         * @property CLASSNAME
         * @default "navset"
         */
        CLASSNAME: 'subnav-navset yui-navset',
        /**
         * The className of the HTMLElement containing the TabView's tab elements
         * to look for when building from existing markup, or to add when building
         * from scratch.
         * All childNodes of the tab container are treated as Tabs when building
         * from existing markup.
         * @property TAB_PARENT_CLASSNAME
         * @default "nav"
         */
        TAB_PARENT_CLASSNAME: 'subnav-nav yui-nav'
    });
    $L.augmentObject($W.HeaderSubNav, {
        ATTR_IS_PRE_MARKUP_LOAD: "preMarkupLoad",
        ATTR_DATA_LOADED: "lastTabDataLoaded"
    });
})();
