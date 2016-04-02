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
     * Add user Widget
     *
     * @author sabarish
     */
    $W.AddUser = function(el, oArgs) {
        $W.AddUser.superclass.constructor.call(this, el, oArgs);
        
    };
    $L.extend($W.AddUser, $W.AddEntity, {
        /*Defining necessary callbacks*/
        oSaveCallback: {
            success: function(o) {
                $D.getElementsByClassName("mesg", null, this.elBase)[0].innerHTML = "<span class='title'>Success:</span> User successfully created.";
                $D.getElementsByClassName(this.oConfiguration.formClass, null, this.elBase)[0].reset();
            },
            failure: function(o) {
                $D.getElementsByClassName("mesg", null, this.elBase)[0].innerHTML = "<span class='title'>Failure:</span> Creation of user failed!!!  Login id might be duplicate one.";
            }
        },
        /*Over riding necessary methods*/
        getSaveURL: function() {
       	 
            return "/fleet/form/admin/";
        },
        
        /**
         * Function to validate the user being added.
         */
		validateForm: function(oInput) {
			var firstName = oInput['firstname'];
			var lastName = oInput['lastname'];
			var loginId = oInput['loginid'];
			var password = oInput['passwd'];
			var confirm_password = oInput['passwd-confirm'];
			
			// Check for null entries for the mandatory fields.
			if(firstName == "" || lastName == "" || loginId == "" || password == "" || confirm_password == ""){
				this.addUserPopUp("empty");
				return false;
			}
			
			// Check for Length validation.
			if(firstName.length > 20 || lastName.length > 20 || loginId.length < 4 || loginId.length > 50 || password != confirm_password){
				if(firstName.length > 20){
					this.addUserPopUp("firstname");
				}
                if(lastName.length > 20){
                	this.addUserPopUp("lastname");
    			}
                if(loginId.length < 4 || loginId.length > 50){
                	this.addUserPopUp("loginid");
    			}
                // Check for mismatch of password and confirm password entries.
                if(password != confirm_password){
                	this.addUserPopUp("password");
    			}
                return false ;
			}
						
            return true;
        },
        
        /**
         * Function to provide a customized alert popup.
         */
        addUserPopUp : function (type) {
        	var popUpType = "add-user-"+type+"-popup";
        	this.oPopUp = new $W.PopUp($D.getElementsByClassName(popUpType, null, this.elBase,function(elTarget){ 
				if($D.hasClass(elTarget,"disabled")){
					$D.removeClass(elTarget,"disabled");
				}
			})[0]);
			this.oPopUp.render();
        	this.oPopUp.show();
        }
    }, true);    
})();
