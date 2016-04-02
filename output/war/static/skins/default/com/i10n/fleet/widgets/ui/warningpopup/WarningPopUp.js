(function(){

    var $W = getPackageForName("com.i10n.fleet.widget.ui");
    var $L = YAHOO.lang;
    var $E = YAHOO.util.Event;
    var $D = YAHOO.util.Dom;
    var $YW = YAHOO.widget;
    var $YU = YAHOO.util;
    
    /**
     * The Confirmation PopUp Utility Widget
     *
     * @author N.Balaji
     */
    $W.WarningPopUp = function(el, oArgs){
        /*Initializing*/
        this.elBase = $D.get(el);
        $W.WarningPopUp.superclass.constructor.call(this, el, oArgs);
        this.addListeners();
        this.configureAttributes();
    };
    $L.augmentObject($W.WarningPopUp, {
        EVT_ON_CONFIRMATION: "onConfirmation"
    });
    $L.extend($W.WarningPopUp, $W.PopUp);
    $L.augmentProto($W.WarningPopUp, $YU.AttributeProvider);
    $L.augmentObject($W.WarningPopUp.prototype, {
        /**
         *Adding listeners to buttons of popup
         */
        addListeners: function(){
            $W.Buttons.addDefaultHandler($D.getElementsByClassName("yes-button", null, this.elBase), function(e){
                this.fireEvent($W.WarningPopUp.EVT_ON_CONFIRMATION, {
                    "confirmation": true
                });
            }, null, this);
            $W.Buttons.addDefaultHandler($D.getElementsByClassName("no-button", null, this.elBase), function(e){
                this.fireEvent($W.WarningPopUp.EVT_ON_CONFIRMATION, {
                    "confirmation": false
                });
            }, null, this);
        },
        /**
         * Configure attributes/events
         */
        configureAttributes: function(){
            /**
             * @event onConfirmation
             * @description Event is fired when a user clicks on either of Yes/No button. and
             * passes an object of the format
             * {
             *     confirmation : <result;true|false>
             * }
             * @type Object
             */
            this.createEvent($W.WarningPopUp.EVT_ON_CONFIRMATION, this);
        }
    });
})();
