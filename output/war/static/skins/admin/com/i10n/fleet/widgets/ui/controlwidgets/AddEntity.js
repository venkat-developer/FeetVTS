(function() {
    var $B = YAHOO.Bubbling;
    var $L = YAHOO.lang;
    var $YU = YAHOO.util;
    var $E = YAHOO.util.Event;
    var $D = YAHOO.util.Dom;
    var $YW = YAHOO.widget;
    var $U = getPackageForName("com.i10n.fleet.Utils");
    var $W = getPackageForName("com.i10n.fleet.widget.ui");
    var $V = getPackageForName("com.i10n.fleet.widget.view");
    /**
     * Generic javascript for the adding an entity pattern
     *
     * Takes a form input that describes the new entity and writes it into
     * the database
     *
     * The writing mechanism (eg: the URL to hit) is very specific to individual
     * widgets. Thus they have to be implemented in their respective scripts
     *
     * @author N.Balaji
     */
    $W.AddEntity = function(el, oArgs) {
        this.initAddEntity(el, oArgs);
    };
    $L.augmentObject($W.AddEntity, {
        ATTR_RECEIVED_DATA: "oReceivedData",
        EVT_RECEIVED_DATA_CHANGE: "oReceivedDataChange"
    });
    $L.augmentProto($W.AddEntity, $YU.AttributeProvider);
    $L.augmentObject($W.AddEntity.prototype, {
        /**
         * The initialization function
         * @param {Object} el The base element that contains the form , save button pattern
         * @param {Object} oArgs Contains the configuration attributes
         */
        initAddEntity: function(el, oArgs) {
            if (!oArgs) {
                oArgs = {};
            }
            if (!$L.isString(oArgs.formClass)) {
                oArgs.formClass = "input-form";
            }
            if (!$L.isString(oArgs.saveButtonClass)) {
                oArgs.saveButtonClass = "save-button";
            }
            if (!$L.isString(oArgs.inputElementClass)) {
                oArgs.inputElementClass = "input-element";
            }
            this.elBase = el;
            this.oConfiguration = oArgs;
        	this.oPopUp = new $W.PopUp($D.getElementsByClassName("popup", null, this.elBase)[0]);
			this.oPopUp.render();
        	this.oPopUp.hide();
        
            this.setAttributeConfig($W.AddEntity.ATTR_RECEIVED_DATA, {
                value: null
            });
            this.addBasicListeners();
        },
        /*Declaring the member properties*/
        elBase: null,
        oConfiguration: null,
        /*Needs to be redefined by subclasses to implement success and failure behavior*/
        oSaveCallback: {
            success: function(o) {
        	
            },
            failure: function(o) {
            },
            upload: function(o){
            }
        },
        oReloadCallback: {
            success: function(o) {
        	$D.getElementsByClassName("mesg", null, this.elBase)[0].innerHTML = "<span class='title'>Success:</span> Reloading the page...";
        	
        	
            },
            failure: function(o) {
            	$D.getElementsByClassName("mesg", null, this.elBase)[0].innerHTML = "<span class='title'>Failure:</span> Failed to Reload....";
            }
        },
        /*Defining methods*/
        /**
         * Abstract method that has to be overridden by subclasses
         * to return the URL to hit for saving the data
         */
        getSaveURL: function() {
        },
        getReloadURL: function(){
        },
        Reload: function(){
        	this.oReloadCallback.scope = this;
        	 $U.Connect.asyncRequest('GET', this.getReloadURL(), this.oReloadCallback);
        },
        /**
         * Saves the input. Can be over-ridden for more specific implementations
         */
        saveEntity: function(oInput) {
            /*Correcting the scope of the call back*/
             this.oSaveCallback.scope = this;
             var formObject = null;
             var edit=null;
             var pic=$D.getElementsByClassName("pic-cur-img");
             
             var sValue=null;
            	 var commandtype=null;
             for (var attribute in oInput) {
                 var aValues = $L.isArray(oInput[attribute]) ? oInput[attribute] : [oInput[attribute]];
               for (var i = 0; i < aValues.length; i++) {
                     sValue =aValues[i];
                     if(sValue=="add_driver" || sValue=="edit_driver"){
                    	 commandtype=sValue;
                     }
                     
                 }
             }
              
              
              if(commandtype=='add_driver'){
            	 formObject = document.getElementById('formid');
            	 YAHOO.util.Connect.setDefaultPostHeader(false);
            	  YAHOO.util.Connect.setForm(formObject,true,true);
             	 $U.Connect.asyncRequest('POST', this.getSaveURL(), this.oSaveCallback, $U.MapToParams(oInput));
              }
              else if(commandtype=='edit_driver')
             {
                     	 
             edit=$D.get('editformid');
             var sFile=null;
             sFile= $D.get("editformid")[3].value;
             if(sFile==null || sFile=="")
             {
            	 
            	 $U.Connect.asyncRequest('POST', this.getSaveURL(), this.oSaveCallback, $U.MapToParams(oInput));
             }
             else
             {
            	
             YAHOO.util.Connect.setDefaultPostHeader(false);
        	 YAHOO.util.Connect.setForm(edit,true,true);
        	 
        	 $U.Connect.asyncRequest('POST', this.getSaveURL(), this.oSaveCallback, $U.MapToParams(oInput));
             }
             
        	 }else{
            
            	 
            	 $U.Connect.asyncRequest('POST', this.getSaveURL(), this.oSaveCallback, $U.MapToParams(oInput));
            	
             }
         
        },
        /**
         * Refreshes the widget
         */
        refresh: function() {
        	 this.oSaveCallback.scope = this;
        	$D.getElementsByClassName("mesg", null, this.elBase)[0].innerHTML = "<span class='title'>Please Note:</span>  All fields are mandatory.";
            $D.getElementsByClassName(this.oConfiguration.formClass, null, this.elBase)[0].reset();
            
        },
        /**
         * Checks whether the input given is valid or not
         *
         * This method must be over-ridden by children that want validation functionality
         *
         * @param {Object} oInput
         */
        validateForm: function(oInput) {
            return true;
        },
        /*Defining callbacks*/
        onSave: function() {
        	var oInput = $U.Forms.getFormValue($D.getElementsByClassName("input-form", null, this.elBase)[0]);
           /*Convert the input elements to a map*/
            if (this.validateForm(oInput)) {
                this.saveEntity(oInput);
                
                 }
            /*Preventing possible memory leaks*/
            oInput = null;
        },
        /*Installing listeners*/
        /**
         * The method that adds listeners to the basic elements like the save button
         *
         * Established in this way so that the more specific widgets can install
         * other additional listeners without disturbing the base listeners
         */
        addBasicListeners: function() {
            /*Adding listeners to the Save button*/
            $W.Buttons.addDefaultHandler($D.getElementsByClassName(this.oConfiguration.saveButtonClass, null, this.elBase)[0], this.onSave, null, this);
        }
    }, true);
})();
