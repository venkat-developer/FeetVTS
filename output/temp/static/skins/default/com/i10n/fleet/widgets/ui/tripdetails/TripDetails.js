(function() {
    var $W = getPackageForName("com.i10n.fleet.widget.ui");
    var $D = YAHOO.util.Dom;
    var $L = YAHOO.lang;
    var $E = YAHOO.util.Event;
    var $YW = YAHOO.widget;
    var $WU = getPackageForName("com.i10n.fleet.widget.util");
    var $U = getPackageForName("com.i10n.fleet.Utils");
    var $YU = YAHOO.util;
    var $B = YAHOO.Bubbling;
   
    /**
     * Trip Details Widget
     *
     * @param {Object} el
     * @param {Object} oParams
     */
    $W.TripDetails = function(el, oParams) {
        /*Declaring necessary members*/
        this.elBaseElement = el;
        /*Initializing*/
        this.setAttributeConfig($W.TripDetails.ATTR_DISPLAYED_TRIP, {
            value: null
        });
        this.initTripDetails(el, oParams);
        /*Hiding the widget*/
        this.hideTripDetails();
    };
    $L.augmentObject($W.TripDetails, {
        ATTR_MODIFIABLE_DETAILS: "oModifiableDetails",
        ATTR_DISPLAYED_TRIP: "oDisplayedTripID",
        EVT_TRIP_CHANGED: "oDisplayedTripIDChange",
        EVT_TRIP_DETAILS_MODIFIED: "oModifiableDetailsChange",
        EVT_TRIP_STATUS_MODIFIED: "tripStatusModified",
        TRIP_STATUS: {
            TRIP_STARTED: 0,
            TRIP_STOPPED: 1,
            TRIP_PAUSED: 2
        },
        MODIFIABLE_CLASS: "editable"
    });
    $L.augmentProto($W.TripDetails, $YU.AttributeProvider);
    
    var tripId;
    var tripname;
    
    $L.augmentObject($W.TripDetails.prototype, {
    	
    	
    	
        /**
         * The initialization function
         */
        initTripDetails: function(el, oParams) {
            this.oEventHandler = new $U.DOMEventHandler(this.elBaseElement, {
                type: "click"
            });
            this.elContainer = $D.getElementsByClassName("trip-details-container", null, el)[0];
            this.setAttributeConfig($W.TripDetails.ATTR_MODIFIABLE_DETAILS, {
                value: null
            });
            /*
             * Creating events
             */
            this.createEvent($W.TripDetails.EVT_TRIP_STATUS_MODIFIED, {
                scope: this
            });
            /*Adding listeners*/
            this.addListeners();
        },
        /*Declaring the member properties*/
        elContainer: null,
        oModifiableDetails: null,
        oDisplayedTripID: null,
        /*Defining the member methods*/
        /**
         * Hides the trip details
         */
        hideTripDetails: function() {
            if (!$D.hasClass(this.elContainer, "hidden")) {
                $D.addClass(this.elContainer, "hidden");
            }
        },
        /**
         * Shows the Trip details
         */
        showTripDetails: function() {
            /*Removing information dialog box if it is still present*/
            var tripSettings = $D.getAncestorByClassName(this.elBaseElement, "tripsettings");
            var simpleDialog = $D.getElementsByClassName("simpledialog", null, tripSettings)[0];
            if (!$D.hasClass(simpleDialog, "disabled")) {
                $D.addClass(simpleDialog, "disabled");
            }
            if ($D.hasClass(this.elContainer, "hidden")) {
                $D.removeClass(this.elContainer, "hidden");
            }
            /*Preventing possible memory leaks*/
            tripSettings = null;
            simpleDialog = null;
        },
        /**
         * Forces the widget to display the details of the trip identified by the tripID
         * @param {Object} tripID A unique identifier for the trip
         */
        displayTripDetails: function(tripID) {
            /*Query the server using the trip id, get the details and display them*/
            this.set($W.TripDetails.ATTR_DISPLAYED_TRIP, tripID);
            tripId=tripID;
            /*Correcting the scope of the callback element*/
            this.oCallBacks.displayTrip.scope = this;
            $U.showLayerOverlay();
            
            $U.Connect.asyncRequest("GET", window.location.pathname + "?markup=TripDetails&body=true&tripID=" + tripID+"&localTime="+ $U.getLocalTime(), this.oCallBacks.displayTrip, null);
        },
        reloadTripDetails: function() {
            /*Preventing possible memory leaks*/
            this.removeListeners();
            this.elContainer = null;
            this.initTripDetails(this.elBaseElement);
        },
        showFailurePopUp: function() {
            if ($D.hasClass($D.getElementsByClassName("simpledialog", null, this.elBaseElement)[0], "disabled")) {
                $D.removeClass($D.getElementsByClassName("simpledialog", null, this.elBaseElement)[0], "disabled");
            }
        },
        hideFailurePopUp: function() {
            if (!$D.hasClass($D.getElementsByClassName("simpledialog", null, this.elBaseElement)[0], "disabled")) {
                $D.addClass($D.getElementsByClassName("simpledialog", null, this.elBaseElement)[0], "disabled");
            }
        },
        getTripName: function(tripName){
        tripname = tripName;
        },
        /*Defining call backs*/
        oCallBacks: {
            displayTrip: {
                success: function(o) {
                    this.removeListeners();
                    this.elBaseElement.innerHTML = o.responseText;
                    this.reloadTripDetails();
                    this.showTripDetails();
                    $U.hideLayerOverlay();
                },
                failure: function(o) {
                    this.showFailurePopUp();
                    this.hideTripDetails();
                    $U.hideLayerOverlay();
                }
            },
            stopTrip: {
                success: function(o) {
                    this.fireEvent($W.TripDetails.EVT_TRIP_STATUS_MODIFIED, [this.get($W.TripDetails.ATTR_DISPLAYED_TRIP), $W.TripDetails.TRIP_STATUS.TRIP_STOPPED]);
                    $D.setStyle($D.getElementsByClassName("slist-item list-item-type item selected") , "visibility", "hidden");
                    $D.setStyle($D.getElementsByClassName("trip-details-container") , "visibility", "hidden");
                },
                failure: function(o) {
                    this.showFailurePopUp();
                    this.hideTripDetails();
                }
            },
            pauseTrip: {
                success: function(o) {
                    this.fireEvent($W.TripDetails.EVT_TRIP_STATUS_MODIFIED, [this.get($W.TripDetails.ATTR_DISPLAYED_TRIP), $W.TripDetails.TRIP_STATUS.TRIP_PAUSED]);
                },
                failure: function(o) {
                    this.showFailurePopUp();
                    this.hideTripDetails();
                }
            },
            startTrip: {
                success: function(o) {
                    this.fireEvent($W.TripDetails.EVT_TRIP_STATUS_MODIFIED, [this.get($W.TripDetails.ATTR_DISPLAYED_TRIP), $W.TripDetails.TRIP_STATUS.TRIP_STARTED]);
                },
                failure: function(o) {
                    this.showFailurePopUp();
                    this.hideTripDetails();
                }
            }
        },
        onStopTrip: function() {
            /*Logic to update the server*/
            /*Correcting the scope of the callback obj*/
            this.oCallBacks.stopTrip.scope = this;
            /* 
             * TODO : Change the URL to the proper one once the data providers are exposed
             */
            $U.Connect.asyncRequest('GET', '@APP_CONTEXT@/form/tripsettings/?command_type=status_type&status=stop&tripId='+tripId+'&tripName='+tripname+'&localTime='+$U.getLocalTime(), this.oCallBacks.stopTrip, null);
        },
        onPauseTrip: function() {
            /*Logic to update the server*/
            /*Correcting the scope of the callback obj*/
            this.oCallBacks.pauseTrip.scope = this;
            /* 
             * TODO : Change the URL to the proper one once the data providers are exposed
             */
            $U.Connect.asyncRequest('GET', '@APP_CONTEXT@/form/tripsettings/?command_type=status_type&status=pause&tripId='+tripId+'&tripName='+tripname, this.oCallBacks.pauseTrip, null);
        },
        onStartTrip: function() {
            /*Logic to update the server*/
            /*Correcting the scope of the callback obj*/
            this.oCallBacks.startTrip.scope = this;
            /* 
             * TODO : Change the URL to the proper one once the data providers are exposed
             */
            $U.Connect.asyncRequest('GET', '@APP_CONTEXT@/form/tripsettings/?command_type=status_type&status=start&tripId='+tripId+'&tripName='+tripname, this.oCallBacks.startTrip, null);
        },
        onTripDetailsModified: function() {
        	
            this.set($W.TripDetails.ATTR_MODIFIABLE_DETAILS, this.getModifiedTripDetails());
        },
        /**
         * The event manager function for the buttons
         * @param {Object} layer
         * @param {Object} args
         */
        buttonEventManager: function(event) {
            var target = $E.getTarget(event);
            var sourceButton = $D.hasClass(target, 'fleet-buttons') ? target : $D.getAncestorByClassName(target, "fleet-buttons");
            if (!$D.hasClass(sourceButton, "disabled")) {
                if ($D.hasClass(sourceButton, 'start-button')) {
                    this.onStartTrip();
                }
                else if ($D.hasClass(sourceButton, 'stop-button')) {
                    this.onStopTrip();
                }
                else if ($D.hasClass(sourceButton, 'pause-button')) {
                    this.onPauseTrip();
                }
            }
            $E.stopEvent(event);
        },
        /*Installing Listeners*/
        addListeners: function() {
            this.oEventHandler.addListener($D.getElementsByClassName('buttons', null, this.elBaseElement), this.buttonEventManager, null, this);
            /*The listener for the save button goes here - should call the onTripDetailsModified*/
            $W.Buttons.addDefaultHandler($D.getElementsByClassName("buttons", null, $D.getElementsByClassName("simpledialog", null, this.elBaseElement)[0])[0], function(event, args, me) {
                $D.addClass($D.getElementsByClassName("simpledialog", null, this.elBaseElement)[0], "disabled");
            }, null, this);
        },
        /*Defining the utility functions*/
        /**
         * Utility function to remove all the listeners
         */
        removeListeners: function() {
            this.oEventHandler.purge();
        },
        /**
         * Utility function to get the modified trip details
         */
        getModifiedTripDetails: function() {
            var aModifiableElements = $D.getElementsByClassName($W.TripDetails.MODIFIABLE_CLASS, null, this.elContainer);
            var result = {};
            for (var element in aModifiableElements) {
                if ($L.isString($D.getAttribute(aModifiableElements[element], "name"))) {
                    result[$D.getAttribute(aModifiableElements[element], "name")] = aModifiableElements[element].innerHTML;
                }
            }
            aModifiableElements = null;
            return result;
        }
    });
    /**
     * Helper class that loads the given Given Geo-Fence limit into the details page
     *
     * TODO: Move this class to a generic location such as the utils.js and make it a generic
     *       functionality.
     * TODO: Implement the functionality
     * @author N.Balaji
     *
     * @param {Object} el The target element into which the map should be loaded
     */
    $W.TripDetails.GeoFenceLoader = function(el) {
        /* TODO : Initializing*/
        this.initGeoFenceLoader(el);
    };
    $L.augmentObject($W.TripDetails.GeoFenceLoader.prototype, {
        /**
         * The initialization function
         * @param {Object} el
         */
        initGeoFenceLoader: function(el) {
            this.elMapWindow = el;
        },
        /*Declaring the data memebers*/
        /**
         * The target into which the geo-Fence limit should be loaded
         */
        elMapWindow: null,
        /*Declaring the member methods*/
        /**
         * Member method that loads the map into a decided target
         * @param {Object} mapEl
         */
        loadMap: function(mapEl) {
        },
        /*Declaring utilty methods*/
        /**
         * Helper function for deceding the appropriate magnification
         * limit for the given geo fence limit
         */
        chooseMagnification: function() {
        }
    });
})();