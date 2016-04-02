(function(){
    var $W = getPackageForName("com.i10n.fleet.widget.ui");
    var $L = YAHOO.lang;
    var $E = YAHOO.util.Event;
    var $D = YAHOO.util.Dom;
    
    /**
     * System Settings Sub Nav Page
     *
     * @author irk
     */
    $W.SystemSettings = function(el, params){
        this.baseElement = el;
        this._widgets = {};
        this.init(el, params);
    };
    
    $L.augmentObject($W.SystemSettings.prototype, {
        /**
         * Initializing fnuction
         * @param {Object} el
         * @param {Object} params
         */
        init: function(el, params){
            $W.Buttons.addDefaultHandler($D.getElementsByClassName("settings-form-button", null, this.baseElement), this.handleUpdate, null, this);
            var oToolBar = new $W.SearchToolBar($D.getElementsByClassName("searchtoolbar", null, el)[0], {
                title: "Global Search",
                searchkey: ""
            });
            this._widgets.toolbar = oToolBar;
        },
        
        /**
         * Event Handler for update of fetch method (Live Streaming or Polling)
         * @param {Object} oArgs
         */
        handleUpdate: function(oArgs){
            var selectedOption;
            var aRadioEl = $D.getElementsByClassName("radio-button", null, this.baseElement);
            for (var i = 0; i < aRadioEl.length; i++) {
                if (aRadioEl[i].checked) {
                    selectedOption = aRadioEl[i].value;
                }
            }
        }
    });
})();
