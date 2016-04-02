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
    $W.MobileAlertSettingsList = function(el, oArgs){
        this.elBase = el;
        this.initMobileAlertSettingsList(el, oArgs);
    };
    $L.augmentObject($W.MobileAlertSettingsList, {
        EVT_ADD_RECORD: "onRecordAdd"
    });
    $L.extend($W.MobileAlertSettingsList, $W.MinimizableList, {
        /**
         * Configures Attributes required for the widget
         */
        configureAttributes: function(){
            $W.MobileAlertSettingsList.superclass.configureAttributes.call(this);
            /**
             * @attribute onRecordAdd
             * @description Add To List Evt.
             * @type Object
             */
            this.createEvent($W.MobileAlertSettingsList.EVT_ADD_RECORD);
        }
    });
    $L.augmentObject($W.MobileAlertSettingsList.prototype, {
        /**
         * Adds Event listeners required for the widget
         */
        addEventListeners: function(){
            var elNameInput = $D.getElementsByClassName("txt-user-name", null, this.elBase)[0];
            $U.addDefaultInputText(elNameInput, elNameInput.value);
            var elMobilenumberInput = $D.getElementsByClassName("txt-user-mobilenumber", null, this.elBase)[0];
            $U.addDefaultInputText(elMobilenumberInput, elMobilenumberInput.value);
        },
        /**
         * Initializes the widget
         */
        initMobileAlertSettingsList: function(el, params){
        	$W.MobileAlertSettingsList.superclass.constructor.call(this, el, params);
            this._oDataSource = params.datasource;
            this._nextId = 1;
            $W.Buttons.addDefaultHandler($D.getElementsByClassName("button-addto-list", null, el), function(params){
            	var oAlertSettingsData = this._getMobileAlertSettingsData();
                oAlertSettingsData.id = "auto-" + this._nextId;
                this._nextId++;
                this._oDataSource.addItem(oAlertSettingsData);
                this.fireEvent($W.MobileAlertSettingsList.EVT_ADD_RECORD, oAlertSettingsData);
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
        _getMobileAlertSettingsData: function(){
            var baseEl = this.elBase;
            var oData = $U.Forms.getFormValue(this.elBase);
            if (oData.all) {
            	oData.geofencing = true;
                oData.overspeeding = true;
                oData.chargerdisconnected = true;
            }
            delete (oData.all);
            return oData;
        }
    });
})();
