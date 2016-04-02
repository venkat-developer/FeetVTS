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
     * Add RouteSchedule Widget
     * Add mechanism is works by The Extension of AddEntity.js
     * @author Gobi
     */
    $W.AddRouteSchedule = function(el, oArgs) {
        $W.AddRouteSchedule.superclass.constructor.call(this, el, oArgs);  
        
    };
    
    
    $L.extend($W.AddRouteSchedule, $W.AddEntity, {
        /*Redefining necessary objects*/
    	/**
    	 * oSaveCallback Shows The Message Either for Success or Failure
    	 */
        oSaveCallback: {
            success: function(o) {
                $D.getElementsByClassName("mesg", null, this.elBase)[0].innerHTML = "<span class='title'>Success:</span> RouteSchedule successfully created.";
                $D.getElementsByClassName(this.oConfiguration.formClass, null, this.elBase)[0].reset();
            },
            failure: function(o) {
                $D.getElementsByClassName("mesg", null, this.elBase)[0].innerHTML = "<span class='title'>Failure:</span> Creation of RouteSchedule failed.";
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
         * Function to validate the routeschedule being added.
         */
        validateForm: function(oInput) {
			var sequenceNo=oInput['sequenceno'];
			var time=oInput['time'];
			var estimatedDist=oInput['distance'];
			var spanDay=oInput['spanday'];
					              
			// Check for null entries for the mandatory fields.
			if(sequenceNo == "" || time == "" ||estimatedDist == "" || spanDay == ""){
				this.addRouteSchedulePopUp("empty");
				return false;
			}
			//check for time validation
					var validtime='0123456789:';
					var timelength=time.length;
					var i;

					for(i=0;i<timelength;i++){
						if(validtime.indexOf(time.charAt(i))<0){
							this.addRouteSchedulePopUp("time");
							return false;
						}	
					}

					if(time.length!=8){
						this.addRouteSchedulePopUp("time");
						return false;
					}
					else if(parseInt(time.substring(0,2))>24 || parseInt(time.substring(3,5))>59 || parseInt(time.substring(6,8))>59){
						this.addRouteSchedulePopUp("time");

						return false;
					}
					else if(time.charAt(2)!=':' || time.charAt(5)!=':' || time.charAt(0)==':' || time.charAt(1)==':' || time.charAt(3)==':' || time.charAt(4)==':' || time.charAt(6)==':' || time.charAt(7)==':'){
						this.addRouteSchedulePopUp("time");
						return false;
					}
				
			
			return true;
        },
        
        /**
         * Function to provide a customized alert popup.
         */
        addRouteSchedulePopUp : function (type) {
        	var popUpType = "add-routeschedule-"+type+"-popup";
        	this.oPopUp = new $W.PopUp($D.getElementsByClassName(popUpType, null, this.elBase,function(elTarget){ 
				if($D.hasClass(elTarget,"disabled")){
					$D.removeClass(elTarget,"disabled");
				}
			})[0]);
			this.oPopUp.render();
        	this.oPopUp.show();
        }
        
    }, true);
    $L.augmentObject($W.AddRouteSchedule.prototype, {});
})();
