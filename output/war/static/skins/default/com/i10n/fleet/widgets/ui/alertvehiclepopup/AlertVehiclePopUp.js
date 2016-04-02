(function() {
    var $L = YAHOO.lang;
    var $E = YAHOO.util.Event;
    var $D = YAHOO.util.Dom;
    var $YW = YAHOO.widget;
    var $YU = YAHOO.util;
    var $W = getPackageForName("com.i10n.fleet.widget.ui");
    var $U = getPackageForName("com.i10n.fleet.Utils");
    /**
     * The Dialog PopUp Utility Widget
     *CAUTION:
     *      THE YUI PANEL USES A FUNCTION NAMED init.THIS FUNCTION NAME SHOULD NOT BE USED
     *      BY ANY Object THAT extends THIS Object.
     * @author N.Balaji
     */
    $W.AlertVehiclePopUp = function(el, oArgs) {
        /*Declaring Member Properties*/
        this.elBase = $D.get(el);
        /*Initializing*/
        $W.AlertVehiclePopUp.superclass.constructor.call(this, el, oArgs);
        this.attrs = new $YU.AttributeProvider();
        this.configureAttributes();
        this.addListeners();
    };
    $L.augmentObject($W.AlertVehiclePopUp, {
        EVT_ON_SUBMIT: "onSubmit",
        EVT_ON_CANCEL: "onCancel"
    });
    $L.extend($W.AlertVehiclePopUp, $W.PopUp, {
        /**
         * Overriding the show method of the superclass
         */
        show: function() {
            /*Refreshing the form*/
            var elForm = $D.getElementsByClassName("dialog-form", null, this.elBase)[0];
            elForm.reset();
            $W.AlertVehiclePopUp.superclass.show.call(this);
        }
    });
    $L.augmentObject($W.AlertVehiclePopUp.prototype, {
        /**
         * Disables buttons on the popup. If a button class is specified , then just the
         * butons of that class is disabled.
         */
        disableButtons: function(sButtonClass) {
            if (sButtonClass && $L.isString(sButtonClass) && sButtonClass.length > 0) {
                $W.Buttons.disable($D.getElementsByClassName(sButtonClass, null, this.elBase));
            }
            else {
                $W.Buttons.disable($D.getElementsByClassName("submit-button", null, this.elBase));
                $W.Buttons.disable($D.getElementsByClassName("cancel-button", null, this.elBase));
            }
        },
        /**
         * Enables buttons on the popup. If a button class is specified , then just the
         * butons of that class is enabled.
         * @param {Object} sButtonClass
         */
        enableButtons: function(sButtonClass) {
            if (sButtonClass && $L.isString(sButtonClass) && sButtonClass.length > 0) {
                $W.Buttons.enable($D.getElementsByClassName(sButtonClass, null, this.elBase));
            }
            else {
                $W.Buttons.enable($D.getElementsByClassName("submit-button", null, this.elBase));
                $W.Buttons.enable($D.getElementsByClassName("cancel-button", null, this.elBase));
            }
        },
        /**
         * Returns an object representing the form of the popup. Searches all the elements with
         * class input-element and creates a name value pair object based on the name and
         * value of the input-element's
         */
        getFormValue: function() {
            return $U.Forms.getFormValue(this.elBase);
        },
        configureAttributes: function() {
            /**
             * @event onSubmit
             * @description Event is fired when a user clicks on submit button. It also passes an object
             * representing the form in a name value pair.
             * @type Object
             */
            this.createEvent($W.AlertVehiclePopUp.EVT_ON_SUBMIT);
            /**
             * @event onSubmit
             * @description Event is fired when a user clicks on cancel button.
             * @type Object
             */
            this.createEvent($W.AlertVehiclePopUp.EVT_ON_CANCEL);
        },
        /**
         * Adds listeners to the buttons of the popup.
         */
        addListeners: function() {
            $W.Buttons.addDefaultHandler($D.getElementsByClassName("submit-button", null, this.elBase), this.onSubmit, null, this);
            $W.Buttons.addDefaultHandler($D.getElementsByClassName("cancel-button", null, this.elBase), this.onCancel, null, this);
        },
        /**
         * Abstract method. Specific widgets should override this method to hit the particular url
         * to save the entity that was added
         */
        saveChanges: function(oInput) {
        this.oSaveCallback.scope = this;
        var oRequestParams = {};
        oRequestParams.assignedVehicles = oInput.assignedEntities;
        oRequestParams.vacantVehicles = oInput.vacantEntities;
       //oRequestParams.userId = oInput.userID;
        /*
         * TODO: Change this to the coressponding URL once the data providers are implemented.
         */
        $U.Connect.asyncRequest('POST', "/fleet/view/controlpanel/?markup=alertsettings", this.oSaveCallback, $U.MapToParams(oRequestParams));
        /*
         * Preventing possible memory leaks
         */
        oRequestParams = null;
        },
        oSaveCallback: {
            success: function(o) {
            },
            failure: function(o) {
            }
        },
        /**
         * Triggered when Submit button is clicked. Also fires onSubmit event
         * @param {Object} oArgs
         */
        onSubmit: function(oArgs) {

            /*Defining callbacks*/
          //  onSave: function() {
                var oInput = {};
                oInput.assignedEntities = new Array();
                oInput.vacantEntities = new Array();

                oInput.userID = new Array();

                /*Getting the assigned entities*/
                var elList = $D.getElementsByClassName("assigned-list", null, this.elBase)[0];
                $D.getElementsByClassName("list-item", null, elList, function(elTarget) {
                    /*Pushing only the newly assigned entities*/
                    if ($D.getAttribute(elTarget, "assigned") == "false") {
                        oInput.assignedEntities.push($D.getAttribute(elTarget, "value"));
                    }
                });
                /*
                var elList = $D.getElementsByClassName(this.oConfiguration.userListClass, null, this.elBase)[0];
                $D.getElementsByClassName("slist-item selected", null, elList,function(elTarget){
                	oInput.userID.push($D.getAttribute(elTarget,"value"));
                });*/

                /*Getting the vacant entities*/
                elList = $D.getElementsByClassName("vacant-list", null, this.elBase)[0];
                $D.getElementsByClassName("list-item", null, elList, function(elTarget) {
                    /*Pushing only the newly vacanted entities*/
                    if ($D.getAttribute(elTarget, "assigned") == "true") {
                        oInput.vacantEntities.push($D.getAttribute(elTarget, "value"));
                    }
                });
                /*Convert the input elements to a map*/
                this.saveChanges(oInput);
                this.hide();
                this.fireEvent($W.AlertVehiclePopUp.EVT_ON_SUBMIT);
            }, /**
             * Hides the assignment controls
             */
            hideAssignmentControls: function() {
                $D.getElementsByClassName("assignment-control", null, this.elBase, function(elTarget) {
                    if (!$D.hasClass(elTarget, "disabled")) {
                        $D.addClass(elTarget, "disabled");
                    }
                });
            },
            /*this.fireEvent($W.AlertVehiclePopUp.EVT_ON_SUBMIT, {
                formValue: this.getFormValue()
            });
            /*Refreshing the form*/
           /* var elForm = $D.getElementsByClassName("dialog-form", null, this.elBase)[0];
            elForm.reset();
            /*Hiding the form from the user*/
            //this.hide();
       // },
        /**
         * Triggered when Cancel button is clicked. Also fires onCancel event
         * @param {Object} oArgs
         */
        onCancel: function(oArgs) {
        	
            var elForm = $D.getElementsByClassName("dialog-form", null, this.elBase)[0];
            elForm.reset();
            this.fireEvent($W.AlertVehiclePopUp.EVT_ON_CANCEL, {
                formValue: this.getFormValue()
            });
            this.hide();
        }
    });
})();
