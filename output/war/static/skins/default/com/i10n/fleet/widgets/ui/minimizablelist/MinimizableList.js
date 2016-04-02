(function(){
    var $B = YAHOO.Bubbling;
    var $L = YAHOO.lang;
    var $YU = YAHOO.util;
    var $E = YAHOO.util.Event;
    var $D = YAHOO.util.Dom;
    var $YW = YAHOO.widget;
    var $W = getPackageForName("com.i10n.fleet.widget.ui");
    var $V = getPackageForName("com.i10n.fleet.widget.view");
    /**
     * A Superclass for all the lists that need minimization.
     *
     * TODO: Minimization has issues in Google Chrome Browser
     *
     * @author sabarish
     */
    $W.MinimizableList = function(el, params){
        this.initMinimizableList(el, params);
    };
    
    $L.augmentObject($W.MinimizableList, {
        /**
         * Constant representing Maximized State
         */
        STATE_MAXIMIZED: "maximized",
        /**
         * Constant representing Maximized State
         */
        STATE_MINIMIZED: "minimized",
        /**
         * Resize Event
         */
        EVT_RESIZE: "resizeEvent"
    });
    $L.extend($W.MinimizableList, $YU.AttributeProvider);
    $L.augmentObject($W.MinimizableList.prototype, {
        initMinimizableList: function(el, params){
            this.elMinListBase = el;
            this.configureAttributes();
            this._addMinimizerListeners();
        },
        _addMinimizerListeners: function(){
            $E.addListener($D.getElementsByClassName('minimizer', null, this.elMinListBase), "click", function(oArgs){
                var isDisabled = $D.hasClass(this.elMinListBase, "state-disabled");
                if (!isDisabled) {
                    this.toggleViewState();
                }
                $E.stopEvent(oArgs);
            }, this, true);
        },
        /**
         * Overridable function to configure attributes.For proper functioning any overriding function must also call this.
         */
        configureAttributes: function(){
            /**
             * @name resizeEvent
             * @descriptiion Fired when the list is minimized/maximized/enabled/disabled
             */
            this.createEvent($W.MinimizableList.EVT_RESIZE);
        },
        /**
         * Disables the widget.(i.e. Hides the widget)
         */
        disable: function(){
            if (!$D.hasClass(this.elMinListBase, "state-disabled")) {
                $D.addClass(this.elMinListBase, "state-disabled");
            }
            if (!$D.hasClass(this.elMinListBase, "minimized")) {
                $D.addClass(this.elMinListBase, "minimized");
            }
            var evtObj = {
                "currentState": $W.MinimizableList.STATE_MINIMIZED
            };
            this.fireEvent($W.MinimizableList.EVT_RESIZE, evtObj);
        },
        /**
         * Enables the widget.(i.e. Displays the widget)
         */
        enable: function(){
            if ($D.hasClass(this.elMinListBase, "state-disabled")) {
                $D.removeClass(this.elMinListBase, "state-disabled");
            }
            if ($D.hasClass(this.elMinListBase, "minimized")) {
                $D.removeClass(this.elMinListBase, "minimized");
            }
            var oEvtArgs = {
                "currentState": $W.MinimizableList.STATE_MAXIMIZED
            };
            this.fireEvent($W.MinimizableList.EVT_RESIZE, oEvtArgs);
        },
        /**
         * Toggles The View State
         */
        toggleViewState: function(params){
            var oEvtArgs = null;
            if ($D.hasClass(this.elMinListBase, "minimized")) {
                $D.removeClass(this.elMinListBase, "minimized");
                oEvtArgs = {
                    "currentState": $W.MinimizableList.STATE_MAXIMIZED
                };
            }
            else {
                $D.addClass(this.elMinListBase, "minimized");
                oEvtArgs = {
                    "currentState": $W.MinimizableList.STATE_MINIMIZED
                };
            }
            this.fireEvent($W.MinimizableList.EVT_RESIZE, oEvtArgs);
        }
    });
    
})();
