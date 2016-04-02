(function(){
    var $W = getPackageForName("com.i10n.fleet.widget.ui");
    var $WU = getPackageForName("com.i10n.fleet.widget.util");
    var $U = getPackageForName("com.i10n.fleet.Utils");
    var $L = YAHOO.lang;
    var $E = YAHOO.util.Event;
    var $D = YAHOO.util.Dom;
    var $YU = YAHOO.util;
    var $B = YAHOO.Bubbling;
    var $YW = YAHOO.widget;
    var $V = getPackageForName("com.i10n.fleet.widget.view");
   
    /**
     * Trip Settings Sub Nav Page
     *
     * @author sabarish,N.Balaji
     */
    $W.TripSettings = function(el, params){
        /*Declaring the memeber properties*/
        this.elBase = el;
        this._widgets = {};
        /*Initializing*/
        this.init(el, params);
        this.handleDeepLinks();
    };
    $L.augmentProto($W.TripSettings, $YU.EventProvider);
    $L.augmentObject($W.TripSettings.prototype, {
   
        /*Declaring all the member properties*/
        tripCreationPopUp: null,
        tripSettingsSideBar: null,
        /**
         * Initialization function
         */
        init: function(el, params){
            /*Creating the trip settings toolbar*/
            var toolBarEl = $D.getElementsByClassName("tripsettingstoolbar", null, el)[0];
            var toolBar = new $W.TripSettingsToolBar(toolBarEl);
            this._widgets.toolbar = toolBar;
           
            /*Instantiating the necessary variables*/
           
            /*flipcontainer is the container that holds all the popups in this page*/
            var flipContainer = $D.getElementsByClassName("flip-container", null, this.elBase)[0];
           
            /*The create trip popup is the popup that is displayed when the addtrip button is clicked*/
            var tripCreationPopUp = new $W.TripSettings.TripCreationPopUp($D.getElementsByClassName("create-trip", null, flipContainer)[0]);
            this._widgets.tripCreationPopUp = tripCreationPopUp;
            /*Creating the Trip Settings Sidebar*/
            this._widgets.tripSettingsSideBar = new $W.TripSettingsSideBar($D.getElementsByClassName("side-pane", null, this.elBase)[0]);
           
            /*Creating the Trip Deatils Widget*/
            this._widgets.tripDetails = new $W.TripDetails($D.getElementsByClassName("tripdetails", null, this.elBase)[0]);
           
            /*Installing listeners*/
            this.addListeners();
           
        },
        /**
         * Hides the message
         */
        hidecomment:function(){
             var elSheet = $D.getElementsByClassName("comment-item");
                 $D.addClass(elSheet, "disabled");
             elSheet = null;
        },
        /**
         * Displays the message when speedlimit and idlepointtimelimit fields are empty
         */
        showComment:function(){
            var elSheet = $D.getElementsByClassName("comment-item");
            if ($D.hasClass(elSheet, "disabled")) {
                $D.removeClass(elSheet, "disabled");
            }
            elSheet = null;
       },
      
       /**
        * The handler function that is fired when the create Trip button on the
        * tripsettings toolbar is clicked
        */
       showTripCreationPopup: function(){
           this.hidecomment();
           this._widgets.tripCreationPopUp.render();
           this._widgets.tripCreationPopUp.show();
       },
        /**
         * Handles Deeplinking to assign a vehicle.
         */
        handleDeepLinks: function(){
            if (_publish.parameters && _publish.parameters.current && _publish.parameters.current.action && _publish.parameters.current.action == "assign") {
                /**
                 * Assign Action
                 */
                var sVehicleID = _publish.parameters.current.vehicleID;
                var sDriverID = _publish.parameters.current.driverID;
                if (sVehicleID) {
                    var elVehList = $D.getElementsByClassName("list-veh", "select", this.elBase)[0];
                    $D.getElementsBy(function(el){
                        var bResult = false;
                        if ($D.getAttribute(el, "value") == sVehicleID) {
                            bResult = true;
                        }
                        return bResult;
                    }, "option", elVehList, function(el){
                            $D.setAttribute(el, "selected", "true");
                    });
                    elVehList = null;
                }
               
                if (sDriverID) {
                    var elDrvList = $D.getElementsByClassName("list-drv", "select", this.elBase)[0];
                    elDrvList.value = sDriverID;
                    $D.getElementsBy(function(el){
                        var bResult = false;
                        if ($D.getAttribute(el, "value") == sDriverID) {
                            bResult = true;
                        }
                        return bResult;
                    }, "option", elDrvList, function(el){
                        $D.setAttribute(el, "selected", "true");
                    });
                    elDrvList = null;
                }
                sVehicleID = null;
                sDriverID = null;
               
                this.showTripCreationPopup();
              
            }
        },
       
        /*Defining Callbacks*/
        /**
         * The object that is passed as the callback arg of  YAHOO.util.Connect.asyncRequest created when a new trip is created
         */
        tripCreation: {
            success: function(o){
                var responseText = o.responseText;
                /*
                 * TODO: Implement the logic for retreving the ID and the
                 *       name of the trip that was created
                 *       once the data part is completed
                 */
                /*Force the tripsettings sidebar to load itslef
                this._widgets.tripSettingsSideBar.addToList({
                    id: "",
                    name: "",
                    status: $W.TripSettingsSideBar.TRIP_STATUS.STOPPED_STATUS
                });*/
               // $U.Connect.asyncRequest('GET',"/fleet/view/dashboard/", this.tripCreation,null);
            },
            failure: function(o){
                /*Do nothing*/
            }
        },
       /**
        * Returns false when speedlimit and idlepointtimelimit fields are empty else returns true
        */
        validateForm:function(){
            var speedLimit= $D.getElementsByClassName('speed-lim')[0].value;
            var idlePointTimeLimit= $D.getElementsByClassName('iptl')[0].value;
            var tripName= $D.getElementsByClassName('trip')[0].value;
            if(speedLimit=="" || idlePointTimeLimit=="" || tripName==""){
                return false;
            }
            return true;
      },
        /**
         * The handler function that is fired when a trip is submitted
         * through the tripCreationPopUp
         *
         * @param {Object} args
         */
        tripSubmitted: function(args){
            /*Write the data back to the server*/
            /*Correcting the scope of the callback obj*/
            this.tripCreation.scope = this;
            if(this.validateForm()){
            var vacantVehicle= YAHOO.util.Dom.getElementsByClassName('list-veh')[0].value;
            var vacantDriver= YAHOO.util.Dom.getElementsByClassName('list-drv')[0].value;
            var speedLimit= YAHOO.util.Dom.getElementsByClassName('speed-lim')[0].value;
            //var geoFenceLimit= YAHOO.util.Dom.getElementsByClassName('geo-lim')[0].value;
            var idlePointTimeLimit= YAHOO.util.Dom.getElementsByClassName('iptl')[0].value;
            var tripName= YAHOO.util.Dom.getElementsByClassName('trip')[0].value;
          
           
            /*
             * TODO: Once the data logic is implemented, replace the
             *       URL with the proper URL to Hit and select the
             *       newly added trip by default
             */
            $U.Connect.asyncRequest('GET', "/fleet/form/tripsettings/?command_type=trip_settings&localTime="+$U.getLocalTime()+"&vacant-vehilce="+vacantVehicle+"&vacant-driver="+vacantDriver+
                        /*"&geo-fence-limit="+geoFenceLimit+*/"&speed-limit="+speedLimit+"&idle-point-time-limit="+idlePointTimeLimit+"&tripName="+tripName, this.tripCreation,null);
          
            this._widgets.tripCreationPopUp.hide();
            }
            else
            {
                
            	this.showComment();
            }
           
        },
        /**
         * The handler function that is fired when the selected item in the SideBar is changed
         * @param {Object} args
         */
        onTripSelected: function(args){
        	
            this._widgets.tripDetails.displayTripDetails($D.getAttribute(args.newValue, "item"));
            this._widgets.tripDetails.getTripName($D.getAttribute(args.newValue, "itemname"));
        },
        onTripStatusModified: function(args){
            /*informing the sidebar*/
            switch (args[1]) {
                case $W.TripDetails.TRIP_STATUS.TRIP_STARTED:
                    $W.Buttons.disable($D.getElementsByClassName("start-button", null, this.elBase));
                    $W.Buttons.enable($D.getElementsByClassName("pause-button", null, this.elBase));
                    $W.Buttons.enable($D.getElementsByClassName("stop-button", null, this.elBase));
                    this._widgets.tripSettingsSideBar.setStatus(args[0], $W.TripSettingsSideBar.TRIP_STATUS.STARTED_STATUS);
                    break;
                case $W.TripDetails.TRIP_STATUS.TRIP_PAUSED:
                    $W.Buttons.disable($D.getElementsByClassName("pause-button", null, this.elBase));
                    $W.Buttons.enable($D.getElementsByClassName("stop-button", null, this.elBase));
                    $W.Buttons.enable($D.getElementsByClassName("start-button", null, this.elBase));
                    this._widgets.tripSettingsSideBar.setStatus(args[0], $W.TripSettingsSideBar.TRIP_STATUS.PAUSED_STATUS);
                    break;
                case $W.TripDetails.TRIP_STATUS.TRIP_STOPPED:
                    $W.Buttons.disable($D.getElementsByClassName("pause-button", null, this.elBase));
                    $W.Buttons.disable($D.getElementsByClassName("stop-button", null, this.elBase));
                    $W.Buttons.enable($D.getElementsByClassName("start-button", null, this.elBase));
                    this._widgets.tripSettingsSideBar.setStatus(args[0], $W.TripSettingsSideBar.TRIP_STATUS.STOPPED_STATUS);
                    break;
            }
        },
       
        /*Defining Utilities*/
        addListeners: function(){
            this._widgets.toolbar.subscribe($W.TripSettingsToolBar.EVT_ON_ADDTRIP, this.showTripCreationPopup, this, true);
            this._widgets.tripCreationPopUp.subscribe($W.DialogPopUp.EVT_ON_SUBMIT, this.tripSubmitted, this, true);
            this._widgets.tripSettingsSideBar.subscribe($W.TripSettingsSideBar.EVT_SELECTED_ITEM_CHANGE, this.onTripSelected, this, true);
            this._widgets.tripDetails.subscribe($W.TripDetails.EVT_TRIP_STATUS_MODIFIED, this.onTripStatusModified, null, this);
            /*Handling the behaviour of the information dialog*/
            var body = $D.getElementsByClassName("bd", null, this.elBase)[0];
            $W.Buttons.addDefaultHandler($D.getElementsByClassName("buttons", null, body)[0], function(event, args, me){
                var simpleDialog = $D.getElementsByClassName("simpledialog", null, body)[0];
                $D.addClass(simpleDialog, "disabled");
            }, null, this);
            /*Preventing memory leaks*/
            body = null;
        }
       
    });
   
    /**
     * The Trip Creation Element
     *
     * @author N.Balaji
     * CAUTION:
     *      THE YUI PANEL USES A FUNCTION NAMED init.THIS FUNCTION NAME SHOULD NOT BE USED
     *      BY ANY Object THAT extends THIS Object.
     * @param {Object} el: The base-div containing content
     */
    $W.TripSettings.TripCreationPopUp = function(el){
        /*Initializing*/
        this.initCreateTrip(el);
    };
    $L.extend($W.TripSettings.TripCreationPopUp, $W.DialogPopUp);
    $L.augmentObject($W.TripSettings.TripCreationPopUp.prototype, {
        /*Declaring the datamembers*/
        elDisplayedContent: null,
        /**
         * The initalization Function
         */
        initCreateTrip: function(el){
            this.elDisplayedContent = el;
            $W.TripSettings.TripCreationPopUp.superclass.constructor.call(this, this.elDisplayedContent, {
                fixedcenter: true,
                width: "420px",
                height: "auto"
            });
        },
       
        /**
         * Method that retrives the data from the server each time the
         * popup is shown
         */
        getDataFromServer: function(){
        }
    });
   
})();
