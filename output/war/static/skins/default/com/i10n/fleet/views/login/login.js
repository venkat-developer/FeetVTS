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
    $V.Login = function() {
    	document.title="Fleetcheck V2.0|LogIn";
        this.init = function(params) {
            $V.Login.superclass.init.call(this, params);
            var loginForm = new $W.LoginForm($D.get("loginform"));
            this._widgets["loginform"] = loginForm;
        };
        
        $V.Login.superclass.constructor.call(this);
    };
    YAHOO.extend($V.Login, $V.BaseView);
})();
