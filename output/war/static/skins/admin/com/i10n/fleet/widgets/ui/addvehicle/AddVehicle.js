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
    $W.AddVehicle = function(el, oArgs) {
        $W.AddVehicle.superclass.constructor.call(this, el, oArgs);


        var vehicleiconid=$D.getElementsByClassName("vehicleiconid",null,el);
        var element=new YAHOO.util.Element($D.get("iconid"));
        var vehicleIcon=null;
        var aRadio=$D.getElementsByClassName("input-element",null,el);
        for(var i=0;i<aRadio.length;i++){
            if(aRadio[i].checked){
                vehicleIcon=aRadio[i].value;
            }
        }
        element.set('value',vehicleIcon);
 
       


        
    };
    $L.extend($W.AddVehicle, $W.AddEntity, {
        /*Redefining necessary objects*/
        oSaveCallback: {
            success: function(o) {
                $D.getElementsByClassName("mesg", null, this.elBase)[0].innerHTML = "<span class='title'>Success:</span> Vehicle successfully created.";
                $D.getElementsByClassName(this.oConfiguration.formClass, null, this.elBase)[0].reset();
            },
            failure: function(o) {
                $D.getElementsByClassName("mesg", null, this.elBase)[0].innerHTML = "<span class='title'>Failure:</span> Creation of vehicle failed.";
            }
        },
        /*Over riding necessary methods*/
        getSaveURL: function() {
            /*
             * TODO: Change this to the coressponding URL once the data providers are implemented.
             */
            return "/fleet/form/admin/";
        },
       
        /**
         * Function to validate the vehicle being added.
         */
        validateForm: function(oInput) {
			var displayName=oInput['name'];
			var make=oInput['make'];
			var model=oInput['model'];
			var year=oInput['year'];
			var imei=oInput['imei'];
			// Validating the type of the year.
			var isYear = /^-?\d+$/.test(year);
					              
			// Check for null entries for the mandatory fields.
			if(displayName == "" || make == "" || model == "" || year == "" || imei == ""){
				this.addVehiclePopUp("empty");
				return false;
			}
			
			// Check for length and type validation.
			if(displayName.length > 20 || make.length > 20 || model.length > 20 || isYear == false || year.length != 4){
				if(displayName.length > 20){
					this.addVehiclePopUp("displayname");
				}
				if(make.length > 20){
                	this.addVehiclePopUp("make");
    			}
                if(model.length > 20){
                	this.addVehiclePopUp("model");
    			}
                if(isYear == false || year.length != 4){
    				this.addVehiclePopUp("year");
    			}
                return false ;
			}
			
		    return true;
        },
        
        /**
         * Function to provide a customized alert popup.
         */
        addVehiclePopUp : function (type) {
        	var popUpType = "add-vehicle-"+type+"-popup";
        	this.oPopUp = new $W.PopUp($D.getElementsByClassName(popUpType, null, this.elBase,function(elTarget){ 
				if($D.hasClass(elTarget,"disabled")){
					$D.removeClass(elTarget,"disabled");
				}
			})[0]);
			this.oPopUp.render();
        	this.oPopUp.show();
        }
        
    }, true);
    $L.augmentObject($W.AddVehicle.prototype, {});
})();
