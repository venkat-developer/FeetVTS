(function(){
    var $Y = YAHOO;
    var $L = $Y.lang;
    var $YU = $Y.util;
    var $E = $Y.util.Event;
    var $D = $Y.util.Dom;
    var $W = getPackageForName("com.i10n.fleet.widget.ui");
    var $U = getPackageForName("com.i10n.fleet.Utils");
    
    /**
     * Widget for showing ReportSettings list as a resizable left pane.
     * extends MinimizableList
     *
     * @author subramaniam
     */
    $W.ReportSettingsList = function(el, oArgs){
    
        this.elBase = el;
        this.initReportSettingsList(el, oArgs);
    };
    $L.augmentObject($W.ReportSettingsList, {
        EVT_ADD_RECORD: "onRecordAdd"
    });
    $L.extend($W.ReportSettingsList, $W.MinimizableList, {
        /**
         * Configures Attributes required for the widget
         */
        configureAttributes: function(){
            $W.ReportSettingsList.superclass.configureAttributes.call(this);
            /**
             * @attribute onRecordAdd
             * @description Add To List Evt.
             * @type Object
             */
            this.createEvent($W.ReportSettingsList.EVT_ADD_RECORD);
        }
    });
    $L.augmentObject($W.ReportSettingsList.prototype, {
        /**
         * Initializes the widget
         */
        initReportSettingsList: function(el, oArgs){
            $W.ReportSettingsList.superclass.constructor.call(this, el, oArgs);
            this._oDataSource = oArgs.datasource;
            this._nextId = 1;
            var button = $D.getElementsByClassName("button-addto-list", null, el);
            $W.Buttons.addDefaultHandler(button, function(oArgs){
                var oReportSettingsData = this._getReportSettingsData();
                oReportSettingsData.id = "auto-" + this._nextId;
                this._nextId++;
                this._oDataSource.addItem(oReportSettingsData);
                this.fireEvent($W.ReportSettingsList.EVT_ADD_RECORD, oReportSettingsData);
            }, null, this);
            button = null;
            this.subscribe($W.MinimizableList.EVT_RESIZE, this.onSidePaneListResize, this, true);
            this.addEventListeners();
        },
        _getReportSettingsData: function(){
            var baseEl = this.elBase;
            var oData = $U.Forms.getFormValue(this.elBase);
            if (oData.all) {
                oData.vehiclestatistics = true;
                oData.vehiclestatus = true;
                oData.offlinevehiclereport = true;
            }
            delete (oData.all);
            return oData;
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
        addEventListeners: function(){
            var elNameInput = $D.getElementsByClassName("txt-user-name", null, this.elBase)[0];
            $U.addDefaultInputText(elNameInput, elNameInput.value);
            var elMailInput = $D.getElementsByClassName("txt-user-mail", null, this.elBase)[0];
            $U.addDefaultInputText(elMailInput, elMailInput.value);
        }
    });
})();
