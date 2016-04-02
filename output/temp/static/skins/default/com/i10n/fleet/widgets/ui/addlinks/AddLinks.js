(function() {
    var $W = getPackageForName("com.i10n.fleet.widget.ui");
    var $L = YAHOO.lang;
    var $E = YAHOO.util.Event;
    var $D = YAHOO.util.Dom;
    var $YW = YAHOO.widget;
    var $YU = YAHOO.util;
    /**
     * The Add Links Utility Widget
     *
     * @author N.Balaji
     */
    $W.AddLinks = function(el) {
        /*Declaring Member Properties*/
        this.elBase = $D.get(el);
        /*Initializing*/
        $W.AddLinks.superclass.constructor.call(this, el, {
            fixedcenter: true,
            height: "auto"
        });
        this.render();
        this.hide();
    };
    $L.augmentObject($W.AddLinks, {
        EVT_LINK_ADDED: "linkAdded"
    });
    $L.extend($W.AddLinks, $W.DialogPopUp, {
        /**
         * Configures Attributes.
         */
        configureAttributes: function() {
            $W.AddLinks.superclass.configureAttributes.call(this);
            this.createEvent($W.AddLinks.EVT_LINK_ADDED, {
                scope: this
            });
        },
        /**
         * Shows the popup after clearing the messages in the popup.
         */
        show: function() {
            this.clearMessage();
            $W.AddLinks.superclass.show.call(this);
        },
        /**
         * Overrides addListeners of DialogPopUp and adds the widget's cutom listener's
         * after adding listenersof the superclass.
         */
        addListeners: function() {
            $W.AddLinks.superclass.addListeners.call(this);
            this.subscribe($W.DialogPopUp.EVT_ON_SUBMIT, this._onLinkSubmit, this, true);
        }
    });
    $L.augmentObject($W.AddLinks.prototype, {
        /**
         * Triggered when Popup fires onSubmit event
         * @param {Object} oArgs
         */
        _onLinkSubmit: function(oArgs) {
            var oFormValue = oArgs.formValue;
            if (oFormValue) {
                var sSubNavPage = oFormValue.link;
                if (sSubNavPage) {
                    var aFrmVal = sSubNavPage.split("|");
                    if (aFrmVal.length >= 0) {
                        this.fireEvent($W.AddLinks.EVT_LINK_ADDED, {
                            link: aFrmVal[0],
                            title: aFrmVal[1],
                            id: aFrmVal[2]
                        });
                    }
                }
            }
        },
        /**
         * Shows a custom message on the popup.
         * @param {Object} sMessage
         */
        showMessage: function(sMessage) {
            var elMessage = $D.getElementsByClassName("add-link-message", null, this.elBase)[0];
            if (elMessage) {
                elMessage.innerHTML = sMessage;
            }
        },
        /**
         * Clears all the messages in the popup.
         */
        clearMessage: function() {
            var elMessage = $D.getElementsByClassName("add-link-message", null, this.elBase)[0];
            if (elMessage) {
                elMessage.innerHTML = " ";
            }
        }
    });
})();
