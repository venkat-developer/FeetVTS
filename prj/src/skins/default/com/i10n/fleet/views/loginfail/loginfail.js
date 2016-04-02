(function() {
    var $B = YAHOO.Bubbling;
    var $L = YAHOO.lang;
    var $YU = YAHOO.util;
    var $E = YAHOO.util.Event;
    var $D = YAHOO.util.Dom;
    var $YW = YAHOO.widget;
    var $W = getPackageForName("com.i10n.fleet.widget.ui");
    var $V = getPackageForName("com.i10n.fleet.widget.view");
    
    /**
     * Login View
     *
     * @author sabarish
     */
    $V.LoginFail = function() {
        this.init = function(params) {
            $V.LoginFail.superclass.init.call(this, params);
            var loginFormFail = new $W.LoginFormFail($D.get("loginform"));
            this._widgets["loginform"] = loginFormFail;
        };
        
        $V.LoginFail.superclass.constructor.call(this);
    };
    YAHOO.extend($V.LoginFail, $V.BaseView);
})();