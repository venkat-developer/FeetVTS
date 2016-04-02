(function() {
    var $W = getPackageForName("com.i10n.fleet.widget.ui");
    var $L = YAHOO.lang;
    var $E = YAHOO.util.Event;
    var $D = YAHOO.util.Dom;
    var $YW = YAHOO.widget;
    var $YU = YAHOO.util;
    var $U = getPackageForName("com.i10n.fleet.Utils");
    /**
     * The Quick Links widget
     *
     * @author N.Balaji
     */
    $W.QuickLinks = function(el, oArgs) {
        this.elBase = $D.get(el);
        this.elContainer = $D.getElementsByClassName("quicklinks-container", null, el)[0];
        this._oAddLinksFlip = new $W.AddLinks($D.get("addlinks"));
        /*Initializing*/
        this.init(el, oArgs);
    };
    $L.augmentObject($W.QuickLinks, {
        ATTR_MAX_LINKS: "maxLinks",
        ATTR_TOTAL_LINKS: "totalLinks",
        EVT_TOTAL_LINKS_CHANGE: "totalLinksChange"
    });
    $L.augmentProto($W.QuickLinks, $YU.AttributeProvider);
    $L.augmentObject($W.QuickLinks.prototype, {
        /**
         * Initializes the widget
         * @param {Object} el
         * @param {Object} params
         */
        init: function(el, params) {
            this.configureAttributes();
            this._addListeners();
            var elLinkItemsParent = $D.getElementsByClassName("link-elements", null, this.elContainer)[0];
            /**
             * Setting the max links the widget can support.
             */
            var nMaxLinks = $D.getAttribute(this.elContainer, "maxLinks");
            if (nMaxLinks) {
                this.set($W.QuickLinks.ATTR_MAX_LINKS, parseInt(nMaxLinks));
            }
            /**
             * Populating list items.
             */
            var aListItems = $D.getElementsByClassName("link-element", null, elLinkItemsParent);
            this.set($W.QuickLinks.ATTR_TOTAL_LINKS, aListItems.length);
            for (var i = 0; i < aListItems.length; i++) {
                this._loadLinkItem(aListItems[i]);
            }
        },
        /**
         * Listener function to handle link removal.
         * @param {Object} oArgs
         */
        _onLinkRemoval: function(oArgs) {
            this.set($W.QuickLinks.ATTR_TOTAL_LINKS, this.get($W.QuickLinks.ATTR_TOTAL_LINKS) - 1);
            oArgs.unsubscribe($W.QuickLinks.ListItem.EVT_LINK_REMOVED, this._onLinkRemoval);
        },
        /**
         * Loads a link item to the dom.
         * @param {Object} elItem
         */
        _loadLinkItem: function(elItem) {
            var oQuickLinkItem = new $W.QuickLinks.ListItem(elItem);
            /*Subscribing to Custom Events*/
            oQuickLinkItem.subscribe($W.QuickLinks.ListItem.EVT_LINK_REMOVED, this._onLinkRemoval, this, true);
        },
        /**
         * Disables the add link popup link
         */
        disableAddLink: function() {
            if (!$D.hasClass(this.elContainer, "add-link-disabled")) {
                $D.addClass(this.elContainer, "add-link-disabled");
            }
        },
        /**
         * Enables the add link popup link
         */
        enableAddLink: function() {
            if ($D.hasClass(this.elContainer, "add-link-disabled")) {
                $D.removeClass(this.elContainer, "add-link-disabled");
            }
        },
        /**
         * Configures Attributes necessary for the widget
         */
        configureAttributes: function() {
            /**
             * @attribute maxLinks
             * @description Max Number of Links this widget will support
             * @type Object
             */
            this.setAttributeConfig($W.QuickLinks.ATTR_MAX_LINKS, {
                value: 3,
                validator: $L.isNumber
            });
            /**
             * @attribute totalLinks
             * @description Total Links current widget is holding.
             * @type Object
             */
            this.setAttributeConfig($W.QuickLinks.ATTR_TOTAL_LINKS, {
                value: 0,
                validator: $L.isNumber
            });
        },
        /**
         * Adds title/link to the current widget
         * @param {Object} sTitle
         * @param {Object} sLink
         */
        _addLink: function(sLink, sTitle) {
            if (this.get($W.QuickLinks.ATTR_TOTAL_LINKS) < this.get($W.QuickLinks.ATTR_MAX_LINKS)) {
                if (sTitle && sLink && sLink.length > 0 && sTitle.length > 0) {
                    var elMarkupTemplate = $D.getElementsByClassName("template-link-item", null, this.elBase)[0];
                    var elCloneable = $D.getElementsByClassName("link-element", null, elMarkupTemplate)[0];
                    var sMarkupTemplate = $U.processTemplate(unescape(elCloneable.innerHTML), {
                        link: sLink,
                        title: sTitle
                    });
                    var elListItem = document.createElement("div");
                    $D.addClass(elListItem, "inline-block link-element");
                    elListItem.innerHTML = sMarkupTemplate;
                    var elParent = $D.getElementsByClassName("link-elements", null, this.elContainer)[0];
                    elParent.appendChild(elListItem);
                    this._loadLinkItem(elListItem, elParent);
                    this.set($W.QuickLinks.ATTR_TOTAL_LINKS, this.get($W.QuickLinks.ATTR_TOTAL_LINKS) + 1);
                }
            }
        },
        /**
         * Adds listener to the current widget dom elements.
         */
        _addListeners: function() {
            $E.addListener($D.getElementsByClassName("add-links", null, this.elContainer), "click", this._showAddLinkPopup, this, true);
            this._oAddLinksFlip.subscribe($W.AddLinks.EVT_LINK_ADDED, this._onLinkAddition, this, true);
            this.subscribe($W.QuickLinks.EVT_TOTAL_LINKS_CHANGE, function(oArgs) {
                if (oArgs.newValue >= this.get($W.QuickLinks.ATTR_MAX_LINKS)) {
                    this.disableAddLink();
                }
                else {
                    this.enableAddLink();
                }
            }, this, true);
        },
        /**
         * Triggered when a user selects a link from addlinks popup to add.
         * @param {Object} oArgs
         */
        _onLinkAddition: function(oArgs) {
            this._addLink(oArgs.link, oArgs.title);
            this._oAddLinksFlip.hide();
        },
        /**
         * Trigerred when a user clicks on Add Link link and show AddLinks Popup.
         */
        _showAddLinkPopup: function() {
            this._oAddLinksFlip.enableButtons();
            this._oAddLinksFlip.clearMessage();
            this._oAddLinksFlip.show();
        }
    });
    /**
     * ListItem Class representing a link item in QuickLinks Widget
     * @param {Object} domElement
     * @param {Object} parentDomElement
     */
    $W.QuickLinks.ListItem = function(el, oArgs) {
        /*Declaring Member properties*/
        this.elBase = el;
        this.init(el, oArgs);
    };
    $L.augmentObject($W.QuickLinks.ListItem, {
        EVT_LINK_REMOVED: "linkRemoved"
    });
    $L.augmentProto($W.QuickLinks.ListItem, $YU.EventProvider);
    $L.augmentObject($W.QuickLinks.ListItem.prototype, {
        /**
         * Initializing.
         * @param {Object} el
         * @param {Object} elParent
         */
        init: function(el, oArgs) {
            this.createEvent($W.QuickLinks.ListItem.EVT_LINK_REMOVED, {
                scope: this
            });
            $E.addListener($D.getElementsByClassName("close", null, el), "click", this._onLinkRemove, this, true);
        },
        /**
         * Triggered when a link is removed from the item
         * @param {Object} e
         */
        _onLinkRemove: function(e) {
            if (this.elBase.parentNode) {
                this.elBase.parentNode.removeChild(this.elBase);
                this.fireEvent($W.QuickLinks.ListItem.EVT_LINK_REMOVED, this);
            }
        }
    });
})();
