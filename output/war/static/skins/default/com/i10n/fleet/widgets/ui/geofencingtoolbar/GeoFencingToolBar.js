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
    $W.GeoFencingToolBar = function(el, params){
        this.init = function(el, params){
            var button = $D.getElementsByClassName("button-add-region", null, el)[0];
            $W.Buttons.addDefaultHandler(button, function(params){
                this.fireEvent($W.GeoFencingToolBar.EVT_ON_ADD_REGION);
            }, null, this);
            
            var deleteregion = $D.getElementsByClassName("button-delete-region",null,el)[0];
            $W.Buttons.addDefaultHandler(deleteregion, function(params){
                this.fireEvent($W.GeoFencingToolBar.EVT_ON_DELETE_REGION);
            }, null, this);
            
            var region = $D.getElementsByClassName("button-draw-region", null, el)[0];
            $W.Buttons.addDefaultHandler(region, function(params){
                this.fireEvent($W.GeoFencingToolBar.EVT_ON_DRAW_REGION);
            }, null, this);
            
            var editregion = $D.getElementsByClassName("button-edit-region", null, el)[0];
            $W.Buttons.addDefaultHandler(editregion, function(params){
                this.fireEvent($W.GeoFencingToolBar.EVT_ON_EDIT_REGION);
            }, null, this);
            
            var saveregion = $D.getElementsByClassName("button-save-region", null, el)[0];
            $W.Buttons.addDefaultHandler(saveregion, function(params){
                this.fireEvent($W.GeoFencingToolBar.EVT_ON_SAVE_REGION);
            }, null, this);
            
            /**
             * Avoiding possible memory leaks.
             */region=null;
            button = null;
            this.configureAttributes();
        };
        this.baseElement = el;
        this.initParams = params;
        this.init(el, params);
    };
    $L.augmentObject($W.GeoFencingToolBar, {
        EVT_ON_ADD_REGION: "addRegion",
        EVT_ON_DELETE_REGION: "deleteRegion",
        EVT_ON_DRAW_REGION: "drawRegion",
        EVT_ON_EDIT_REGION: "editRegion",
        EVT_ON_SAVE_REGION: "saveRegion"
    });
    $L.augmentProto($W.GeoFencingToolBar, $YU.EventProvider);
    $L.augmentObject($W.GeoFencingToolBar.prototype, {
        /**
         * The Methods needed for initialization
         */
        configureAttributes: function(){
            /**
             * @event onAddRegion
             * @event onDrawRegion
             * @event onDeleteRegion
             * @event onSaveRegion
             * @event onEditRegion
             * @description Event is fired when a user clicks on the AddTrip button
             * @type Object
             */
            this.createEvent($W.GeoFencingToolBar.EVT_ON_ADD_REGION);
            this.createEvent($W.GeoFencingToolBar.EVT_ON_DELETE_REGION);
            this.createEvent($W.GeoFencingToolBar.EVT_ON_DRAW_REGION);
            this.createEvent($W.GeoFencingToolBar.EVT_ON_EDIT_REGION);
            this.createEvent($W.GeoFencingToolBar.EVT_ON_SAVE_REGION);
        }
    });
})();