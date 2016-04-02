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
     * Add Vehicle Widget
     *
     * @author sabarish
     */
   
    $W.AddDriver = function(el, oArgs) {
    	

    	//YAHOO.util.Connect.initHeader("Accept", "multipart/form-data");
    	
        $W.AddDriver.superclass.constructor.call(this, el, oArgs);
        
    };
    $L.extend($W.AddDriver, $W.AddEntity, {
        /*Defining necessary callbacks*/
        oSaveCallback: {
            success: function(o) {
    	        
                $D.getElementsByClassName("mesg", null, this.elBase)[0].innerHTML = "<span class='title'>Success:</span> Driver successfully created.";
                $D.getElementsByClassName(this.oConfiguration.formClass, null, this.elBase)[0].reset();
             
            },
            failure: function(o) {
                $D.getElementsByClassName("mesg", null, this.elBase)[0].innerHTML = "<span class='title'>Failure:</span> Creation of driver failed.";
            },
            upload:function(o)
            {
            	
            	if (o.responseText == undefined || o.responseText == null)
            	{
            		$D.getElementsByClassName("mesg", null, this.elBase)[0].innerHTML = "<span class='title'>Upload:</span> Connection timeout. Please try again later..";
            	}
            	else
            	{
                $D.getElementsByClassName("mesg", null, this.elBase)[0].innerHTML = "<span class='title'>Success:</span> Driver successfully created.";
                $D.getElementsByClassName(this.oConfiguration.formClass, null, this.elBase)[0].reset();
            	}
            	
            }
            
            
      },
       
        /*Over riding necessary methods*/
        getSaveURL: function() {
            /*
             * TODO: Change this to the coressponding URL once the data providers are implemented.
             */
            return "@APP_CONTEXT@/form/admin/";
        },
        
        /**
         * Function to validate the driver being added.
         */
        validateForm: function(oInput){
        	var firstName=oInput['firstname'];
        	var lastName=oInput['lastname'];
        	var licenseNo=oInput['licenseno'];
        	// License number has to a valid number.
        	var isValidLicenseNumber = /^-?\d+$/.test(licenseNo);
        	var photo=oInput['photo'];
        	
        	// Check for null entries for the mandatory fields.
        	if(firstName == "" || lastName == "" || licenseNo == "" || photo == ""){
				this.addDriverPopUp("empty");
				return false;
        	}
        	
        	// Check for length and type validation.
        	if(firstName.length > 50 || lastName.length > 50 || isValidLicenseNumber == false || licenseNo.length > 20){
				if(firstName.length > 50) {
					this.addDriverPopUp("firstname");
				}
                if(lastName.length > 50){
                	this.addDriverPopUp("lastname");
    			}
                if(isValidLicenseNumber == false || licenseNo.length > 20){
                	this.addDriverPopUp("licenseno");
    			}
                return false ;
			}
        	
        	return  true;
        },
        
        /**
         * Function to provide a customized alert popup.
         */
        addDriverPopUp : function (type) {
        	var popUpType = "add-driver-"+type+"-popup"
        	this.oPopUp = new $W.PopUp($D.getElementsByClassName(popUpType, null, this.elBase,function(elTarget){ 
				if($D.hasClass(elTarget,"disabled")){
					$D.removeClass(elTarget,"disabled");
				}
			})[0]);
			this.oPopUp.render();
        	this.oPopUp.show();
        }
        
    }, true);
    $L.augmentObject($W.AddDriver.prototype,{});
    
})();
