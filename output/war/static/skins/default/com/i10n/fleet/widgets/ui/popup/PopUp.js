(function() {
    var $W = getPackageForName("com.i10n.fleet.widget.ui");
    var $L = YAHOO.lang;
    var $E = YAHOO.util.Event;
    var $D = YAHOO.util.Dom;
    var $YW = YAHOO.widget;
    var $U = getPackageForName("com.i10n.fleet.Utils");
    /**
     * The Popup Utility Widget
     * CAUTION:
     *      THE YUI PANEL USES A FUNCTION NAMED init.THIS FUNCTION NAME SHOULD NOT BE USED
     *      BY ANY Object THAT extends THIS Object.
     * @author N.Balaji,aravind
     */
    $W.PopUp = function(el, oArgs) {
        this.initPopup(el, oArgs);
    };
    $L.extend($W.PopUp, $YW.Panel, {
        show: function() {
            $U.showPopupOverlay();
            $W.PopUp.superclass.show.call(this);
        },
        hide: function() {
            $U.hidePopupOverlay();
            $W.PopUp.superclass.hide.call(this);
        }
    });
    $L.augmentObject($W.PopUp.prototype, {
        /**
         * Constructs a config for popup based on the default requirements for the popup.
         * @param {Object} oConfig
         */
        constructConfig: function(oConfig) {
            var oUserConfig = (oConfig) ? oConfig : {};
            var oDefaultConfig = {
                draggable: false,
                fixedcenter: true,
                constraintoviewport: true,
                zindex: 2000,
                close: false,
                width: "389px",
                height: "153px"
            };
            $L.augmentObject(oUserConfig, oDefaultConfig);
            /**
             * Setting some Default values that are always applicable whatever
             * the user preferences are
             */
            oUserConfig.close = false;
            /** 
             * Handling the auto values
             */
            if (oUserConfig.width == "auto") 
                oUserConfig.width = null;
            if (oUserConfig.height == "auto") 
                oUserConfig.height = null;
            return oUserConfig;
        },
        /**
         * Initializes the popup widget.
         * @param {Object} el
         * @param {Object} oArgs
         */
        initPopup: function(el, oArgs) {
            var oConfig = this.constructConfig(oArgs);
            $W.PopUp.superclass.constructor.call(this, $D.getElementsByClassName("popup-markup", null, el)[0], oConfig);
            $E.addListener($D.getElementsByClassName("close", null, el), "click", function(oArgs) {
                this.hide();
                $E.stopEvent(oArgs);
            }, this, true);
            oConfig = null;
            
            
        }
    });
})();
