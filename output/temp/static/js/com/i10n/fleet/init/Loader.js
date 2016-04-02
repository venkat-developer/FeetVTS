(function(){
    var $B = YAHOO.Bubbling;
    var $L = YAHOO.lang;
    var $YU = YAHOO.util;
    var $E = YAHOO.util.Event;
    var $D = YAHOO.util.Dom;
    var $YW = YAHOO.widget;
    var $U = getPackageForName("com.i10n.fleet.Utils");
    var $V = getPackageForName("com.i10n.fleet.widget.view");
    var $I = getPackageForName("com.i10n.fleet.init");
    var $C = getPackageForName("com.i10n.fleet.web.controllers");
  
    
    /**
     * Fleet Application Widget/View Loader
     * 
     * @author sabarish
     */
    $I.Loader = function(){
        this.load = function(){
            $U.showLayerOverlay();
            try {
                this.loadView();
                this.enableFlips();
            } 
            catch (ex) {
                $U.alert({
                    message: "There was an error loading the site. Please reload the page.\nDetails : \n" + ex
                });
            }
            $U.hideLayerOverlay();
        };
        /**
         * Loads the View
         */
        this.loadView = function(){
            for (var viewName in $V) {
                if (viewName.indexOf("Base") !== 0) {
                    var view = new $V[viewName]();
                    _instances.view = view;
                    view.init();
                    break;
                }
            }
            
        };
        this.enableFlips = function(){
            var elFlipContainer = $D.get("flip-container");
            if (elFlipContainer) {
                $D.setStyle(elFlipContainer, "display", "block");
            }
        };
    };
    _instances.loader = new $I.Loader();
    $E.onDOMReady(function(){
        _instances.loader.load();
    });
})();
