(function(){
    var $W = getPackageForName("com.i10n.fleet.widget.ui");
    var $L = YAHOO.lang;
    var $E = YAHOO.util.Event;
    var $D = YAHOO.util.Dom;
    var $YU = YAHOO.util;
    
    /**
     * Trip Settings Tool Bar Widget(Top Bar)
     *
     * @author sabarish
     */
    $W.TripSettingsToolBar = function(el, params){
        this.init = function(el, params){
            var button = $D.getElementsByClassName("button-add-trip", null, el)[0];
            $W.Buttons.addDefaultHandler(button, function(params){
                this.fireEvent($W.TripSettingsToolBar.EVT_ON_ADDTRIP);
            }, null, this);
            /**
             * Avoiding possible memory leaks.
             */
            button = null;
            this.configureAttributes();
        };
        this.baseElement = el;
        this.initParams = params;
        this.init(el, params);
    };
    $L.augmentObject($W.TripSettingsToolBar, {
        EVT_ON_ADDTRIP: "addTrip"
    });
    $L.augmentProto($W.TripSettingsToolBar, $YU.EventProvider);
    $L.augmentObject($W.TripSettingsToolBar.prototype, {
        /**
         * The Methods needed for initialization
         */
        configureAttributes: function(){
            /**
             * @event onAddTrip
             * @description Event is fired when a user clicks on the AddTrip button
             * @type Object
             */
            this.createEvent($W.TripSettingsToolBar.EVT_ON_ADDTRIP);
        }
    });
})();