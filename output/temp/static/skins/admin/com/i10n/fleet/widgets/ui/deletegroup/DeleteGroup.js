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
    var response = true;
    /**
     * Delete Group UI Widget
     * @author dharma
     */
    $W.DeleteGroup = function(el, oArgs) {
        $W.DeleteGroup.superclass.constructor.call(this, el, oArgs);
    };
    $L.extend($W.DeleteGroup, $W.DeleteEntity, {
        /*Defining call backs*/
        oCallBacks: {
            oDeleteEntityCallBack: {
                success: function(o) {

    	var responseData = o.responseText;
    	var message = responseData.split(":");
    
    	for(var i=0; i< message.length; i++){
    	
    		if(message[i] != 0){
    			response = false;
    			break;
    		}
    	}
    	
                    $D.getElementsByClassName("info", null, this.elBase)[0].innerHTML = "<span class='title'>Success:</span> The selected group/s were successfully deleted.";
                },
                failure: function(o) {
                    $D.getElementsByClassName("info", null, this.elBase)[0].innerHTML = "<span class='title'>Failure:</span> Deleteion of the selected group/s failed.";
                }
            },
            oReloadListCallBack: {
                success: function(o) {
                    /*Destroying behavior layers*/
                    this.oListEventManager.destroy();
                    this.elBase.innerHTML = o.responseText;
                    /*Re-initializing*/
                    $W.DeleteGroup.superclass.constructor.call(this, this.elBase, this.oConfiguration);
                    if(response == false){
            	          
                        $D.getElementsByClassName("info", null, this.elBase)[0].innerHTML = "<span class='title'>Failure:Some of the selected group/s contains active trips, Hence deletion failed.</span>";
                        response= true;
        				}
        				else{
        				
        					$D.getElementsByClassName("info", null, this.elBase)[0].innerHTML = "<span class='title'>Success:</span> The selected group/s were successfully deleted.";
        				}
                },
                failure: function(o) {
                    $D.getElementsByClassName("info", null, this.elBase)[0].innerHTML = "<span class='title'>Failure:</span> Unable to get data from the server.Try reloading the page";
                }
            }
        },
        /*Over riding necessary methods*/
        reloadList: function() {
            /*Correcting the scope of the call back*/
            this.oCallBacks.oReloadListCallBack.scope = this;
            var elList = $D.getElementsByClassName("tlist", null, this.elBase)[0];

            var cchild= $D.getElementsByClassName("delete-entity-checkbox", null, elList, function(elTarget) {
                     if (elTarget.checked==true) {
                          
                         elList.deleteRow(elTarget);
                        
                     }
                     
                     
                 });

            $U.Connect.asyncRequest('GET', "@APP_CONTEXT@/view/controlpanel/?markup=DeleteGroup&body=true", this.oCallBacks.oReloadListCallBack);
        },
        deleteEntity: function(aKeys) {
            var sParams = "";
            sParams = aKeys.join(":");
            /*Hitting the server URL to delete the entities*/
            /*Correcting the scope of the call back*/
            this.oCallBacks.oDeleteEntityCallBack.scope = this;
            $U.Connect.asyncRequest('GET', "@APP_CONTEXT@/form/admin/?command_type=delete_group&groupList=" + sParams, this.oCallBacks.oDeleteEntityCallBack, null);
        },
        refresh: function() {
            this.onCancel();
            $D.getElementsByClassName("info", null, this.elBase)[0].innerHTML = "<span class='title'>Please Note:</span> Before deleting group/s make sure vehicle/driver assigned are freed.";
        }
    });
})();
