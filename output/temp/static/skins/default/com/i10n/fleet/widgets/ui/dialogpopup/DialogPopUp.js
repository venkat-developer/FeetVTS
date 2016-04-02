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
    $W.DialogPopUp = function(el, oArgs) {
        /*Declaring Member Properties*/
        this.elBase = $D.get(el);
        /*Initializing*/
        $W.DialogPopUp.superclass.constructor.call(this, el, oArgs);
        this.attrs = new $YU.AttributeProvider();
        this.configureAttributes();
        this.addListeners();
    };
    $L.augmentObject($W.DialogPopUp, {
        EVT_ON_SUBMIT: "onSubmit",
        EVT_ON_CANCEL: "onCancel"
    });
    $L.extend($W.DialogPopUp, $W.PopUp, {
        /**
         * Overriding the show method of the superclass
         */
        show: function() {
            /*Refreshing the form*/
            var elForm = $D.getElementsByClassName("dialog-form", null, this.elBase)[0];
            elForm.reset();
            $W.DialogPopUp.superclass.show.call(this);
        }
    });
    $L.augmentObject($W.DialogPopUp.prototype, {
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
            this.createEvent($W.DialogPopUp.EVT_ON_SUBMIT);
            /**
             * @event onSubmit
             * @description Event is fired when a user clicks on cancel button.
             * @type Object
             */
            this.createEvent($W.DialogPopUp.EVT_ON_CANCEL);
        },
        /**
         * Adds listeners to the buttons of the popup.
         */
        addListeners: function() {
            $W.Buttons.addDefaultHandler($D.getElementsByClassName("submit-button", null, this.elBase), this.onSubmit, null, this);
            $W.Buttons.addDefaultHandler($D.getElementsByClassName("cancel-button", null, this.elBase), this.onCancel, null, this);
        },
        /**
         * Triggered when Submit button is clicked. Also fires onSubmit event
         * @param {Object} oArgs
         */
        onSubmit: function(oArgs) {
            this.fireEvent($W.DialogPopUp.EVT_ON_SUBMIT, {
                formValue: this.getFormValue()
            });
            /*Refreshing the form*/
            var elForm = $D.getElementsByClassName("dialog-form", null, this.elBase)[0];
            elForm.reset();
            /*Hiding the form from the user*/
            //this.hide();
        },
        /**
         * Triggered when Cancel button is clicked. Also fires onCancel event
         * @param {Object} oArgs
         */
        onCancel: function(oArgs) {
        	
            var elForm = $D.getElementsByClassName("dialog-form", null, this.elBase)[0];
            elForm.reset();
            this.fireEvent($W.DialogPopUp.EVT_ON_CANCEL, {
                formValue: this.getFormValue()
            });
            this.hide();
        }
    });
})();
