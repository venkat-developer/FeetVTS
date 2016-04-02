(function(){
    var $W = getPackageForName("com.i10n.fleet.widget.ui");
    var $L = YAHOO.lang;
    var $E = YAHOO.util.Event;
    var $D = YAHOO.util.Dom;
    /**
     * Buttons Widget for Fleetcheck project
     *
     * @author sabarish
     */
    $W.Buttons = function(el, params){
        this.init = function(el, params){
        
        };
        this.baseElement = el;
        this.initParams = params;
        this.init(this.baseElement, params);
    };
    
    $W.Buttons.addDefaultHandler = function(button, handler, data, context){
        var buttons = [];
        if ($L.isString(button)) {
            buttons = $D.getElementsByClassName(button);
        }
        else if ($L.isObject(button)) {
            buttons.push(button);
        }
        else if ($L.isArray(button)) {
            buttons = button;
        }
        if (typeof data === "undefined") {
            data = null;
        }
        if (!$L.isObject(context)) {
            context = this;
        }
        $E.addListener(buttons, "click", function(oArgs, oSelf){
            var target = oArgs.target;
            if (!$D.hasClass(target, "fleet-buttons")) {
                target = $D.getAncestorByClassName(target, "fleet-buttons");
            }
            if (!$D.hasClass(target, "disabled")) {
                try {
                    handler.call(context, {
                        target: target,
                        data: data
                    });
                } 
                catch (ex) {
                    /**
                     * TODO : Add Log here.
                     */
                }
            }
            $E.stopEvent(oArgs);
        }, this, true);
        /**
         * Avoiding possible memory leaks.
         */
        buttons = null;
        button = null;
    };
    
    $W.Buttons.disable = function(button){
        var buttons = [];
        if ($L.isString(button)) {
            buttons = $D.getElementsByClassName(button);
        }
        else if ($L.isArray(button)) {
            buttons = button;
        }
        else if ($L.isObject(button)) {
            buttons.push(button);
        }
        var len = buttons.length;
        for (var i = 0; i < len; i++) {
            var currentButton = buttons[i];
            if (!$D.hasClass(currentButton, "disabled")) {
                $D.addClass(currentButton, "disabled");
            }
        }
    };
    
    $W.Buttons.enable = function(button){
        var buttons = [];
        if ($L.isString(button)) {
            buttons = $D.getElementsByClassName(button);
        }
        else if ($L.isArray(button)) {
            buttons = button;
        }
        else if ($L.isObject(button)) {
            buttons.push(button);
        }
        var len = buttons.length;
        for (var i = 0; i < len; i++) {
            var currentButton = buttons[i];
            if ($D.hasClass(currentButton, "disabled")) {
                $D.removeClass(currentButton, "disabled");
            }
        }
    };
})();
