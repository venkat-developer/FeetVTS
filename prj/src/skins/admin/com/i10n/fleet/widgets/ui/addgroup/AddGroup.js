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
     * Add Group Widget
     *
     * @author dharma
     */
    $W.AddGroup = function(el, oArgs) {
        $W.AddGroup.superclass.constructor.call(this, el, oArgs);
        
         
        
    };
    $L.extend($W.AddGroup, $W.AddEntity, {
        /*Redefining necessary objects*/
        oSaveCallback: {
            success: function(o) {
                $D.getElementsByClassName("mesg", null, this.elBase)[0].innerHTML = "<span class='title'>Success:</span> Group successfully created.";
                $D.getElementsByClassName(this.oConfiguration.formClass, null, this.elBase)[0].reset();
            },
            failure: function(o) {
                $D.getElementsByClassName("mesg", null, this.elBase)[0].innerHTML = "<span class='title'>Failure:</span> Creation of group failed. Groupname might be duplicate one.";
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
         * Function to validate the group being added.
         */
        validateForm: function(oInput) {
			var groupName=oInput['name'];
			// groupname has to be a string.
			var isValid = /[a-zA-Z]+/.test(groupName);
                       
			// Check for null entries for the mandatory fields.
			if(groupName == "" ){
				this.addGroupPopUp("empty");
				return false;
			}
			
			// Check for length and type validation.
			if(isValid == false || groupName.length > 20){
				this.addGroupPopUp("groupname");	
				return false;
			}
						
		    return true;
        },
        
        /**
         * Function to provide a customized alert popup.
         */
        addGroupPopUp : function (type) {
        	var popUpType = "add-group-"+type+"-popup";
        	this.oPopUp = new $W.PopUp($D.getElementsByClassName(popUpType, null, this.elBase,function(elTarget){ 
				if($D.hasClass(elTarget,"disabled")){
					$D.removeClass(elTarget,"disabled");
				}
			})[0]);
			this.oPopUp.render();
        	this.oPopUp.show();
        }
        
    }, true);
    $L.augmentObject($W.AddGroup.prototype, {});
})();
