(function(){
    var $Y = YAHOO;
    var $L = $Y.lang;
    var $YU = $Y.util;
    var $E = $Y.util.Event;
    var $D = $Y.util.Dom;
    var $W = getPackageForName("com.i10n.fleet.widget.ui");
    var $WU = getPackageForName("com.i10n.fleet.widget.util");
    var $U = getPackageForName("com.i10n.fleet.Utils");
    
    /**
     * Widget for showing AlertSettings list as a resizable left pane.
     * extends MinimizableList
     *
     * @author subramaniam
     */
    $W.AlertSettingsList = function(el, oArgs){
        this.elBase = el;
       
        this.initAlertSettingsList(el, oArgs);
    };
    $L.augmentObject($W.AlertSettingsList, {
        EVT_ADD_RECORD: "onRecordAdd"
    });
    $L.extend($W.AlertSettingsList, $W.MinimizableList, {
        /**
         * Configures Attributes required for the widget
         */
        configureAttributes: function(){
            $W.AlertSettingsList.superclass.configureAttributes.call(this);
            /**
             * @attribute onRecordAdd
             * @description Add To List Evt.
             * @type Object
             */
            this.createEvent($W.AlertSettingsList.EVT_ADD_RECORD);
        }
    });
    $L.augmentObject($W.AlertSettingsList.prototype, {
        /**
         * Adds Event listeners required for the widget
         */
        addEventListeners: function(){
            var elNameInput = $D.getElementsByClassName("txt-user-name", null, this.elBase)[0];
            $U.addDefaultInputText(elNameInput, elNameInput.value);
            var elMailInput = $D.getElementsByClassName("txt-user-mail", null, this.elBase)[0];
            $U.addDefaultInputText(elMailInput, elMailInput.value);
        },
        /**
         * Initializes the widget
         */
        initAlertSettingsList: function(el, params){
            $W.AlertSettingsList.superclass.constructor.call(this, el, params);
            this._oDataSource = params.datasource;
            this._nextId = 1;
            $W.Buttons.addDefaultHandler($D.getElementsByClassName("button-addto-list", null, el), function(params){
                var oAlertSettingsData = this._getAlertSettingsData();
                oAlertSettingsData.id = "auto-" + this._nextId;
                this._nextId++;
                this._oDataSource.addItem(oAlertSettingsData);
                this.fireEvent($W.AlertSettingsList.EVT_ADD_RECORD, oAlertSettingsData);
            }, null, this);
            this.subscribe($W.MinimizableList.EVT_RESIZE, this.onSidePaneListResize, this, true);
            this.addEventListeners();
        },
        /**
         * Executed when SidePaneList is minimized/maximized.
         */
        onSidePaneListResize: function(oArgs, oSelf){
            var elContainer = $D.getElementsByClassName("view-container")[0];
            if ($W.MinimizableList.STATE_MINIMIZED == oArgs.currentState) {
                if (!$D.hasClass(elContainer, "list-minimized")) {
                    $D.addClass(elContainer, "list-minimized");
                }
            }
            else {
                if ($D.hasClass(elContainer, "list-minimized")) {
                    $D.removeClass(elContainer, "list-minimized");
                }
            }
        },
        _getAlertSettingsData: function(){
            var baseEl = this.elBase;
            var oData = $U.Forms.getFormValue(this.elBase);
           
            if (oData.all) {
                oData.geofencing = true;
                oData.overspeeding = true;
                oData.chargerdisconnected = true;
                oData.ignition=true;
                
            }
            delete (oData.all);
            return oData;
        }
    });
})();
