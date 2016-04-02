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
	$W.AddHardware = function(el, oArgs) {
		$W.AddHardware.superclass.constructor.call(this, el, oArgs);

	};
	$L.extend($W.AddHardware, $W.AddEntity, {
		/*Defining necessary callbacks*/

		oSaveCallback: {

			success: function(o) {
				if(o.responseText.length>0){
					$D.getElementsByClassName("mesg", null, this.elBase)[0].innerHTML = "<span class='title'>Success:</span> Hardware successfully created.";
					$D.getElementsByClassName(this.oConfiguration.formClass, null, this.elBase)[0].reset();
				}else{
					$D.getElementsByClassName("mesg", null, this.elBase)[0].innerHTML = "<span class='title'>Failure:</span> Creation of Hardware failed. Because IMEI,SIM ID or MobileNumber is already existed";
				}
			},
			failure: function(o) {
				$D.getElementsByClassName("mesg", null, this.elBase)[0].innerHTML = "<span class='title'>Failure:</span> Creation of Hardware failed.";
			}
		},

		/*Over riding necessary methods*/
		getSaveURL: function() {
			return "/fleet/form/admin/";
		},
		getReloadURL: function(){

			return "/fleet/view/controlpanel/?markup=HardwareManager&";

		},
		/**
		 * Validating the Hardware being added.
		 */   
		validateForm: function(oInput) {
			var imei = oInput['imei'];
			var module = oInput['modulename'];
			var firm = oInput['firmwareversion'];
			var mobileNumber = oInput['mobilenumber'];
			var simId = oInput['simid'];
			
			// IMEI has to be number only.
			var isImei = /^-?\d+$/.test(imei);
			// Module version has to be a float value.
			var isModule = /^[-+]?\d+(\.\d+)?$/.test(module);
			// Firmware version has to be float value.
			var isFirm = /^[-+]?\d+(\.\d+)?$/.test(firm);
		
			// Check for null entries of the mandatory fields.
			if(imei == "" || module == "" ||  firm== ""){	
				this.addHardwarePopUp("empty");
				return false;
			}

			// Check for type and length validation.
			if(isImei == false || isModule == false || isFirm == false || imei.length > 20 
					|| imei.length < 15 || module.length > 5 || firm.length > 5){
				if(isImei == false || imei.length > 20 || imei.length < 15){
					this.addHardwarePopUp("imei")
				}
				if(isModule == false || module.length > 5){
					this.addHardwarePopUp("module");
				}	
				if(isFirm == false || firm.length > 5){
					this.addHardwarePopUp("firm");
				}

				return false ;
			}
			if(mobileNumber.length!=0){
				if(parseInt(mobileNumber) != 0){	
					if(mobileNumber.length != 10 || isNaN(mobileNumber)){
							this.addHardwarePopUp("mobilenumber");
							return false;
						
					}
				}	
			}

			if(simId.length != 0){
				if(parseInt(simId)!=0){
					if(simId.length < 18 || simId.length>20 || isNaN(simId)){
							this.addHardwarePopUp("simid");
							return false;
						
					}
				}
			}

			return true;
		},

		/**
		 * Function to provide a customized alert popup.
		 */
		addHardwarePopUp : function(type){
			var popUpType = "add-hardware-"+type+"-popup";
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