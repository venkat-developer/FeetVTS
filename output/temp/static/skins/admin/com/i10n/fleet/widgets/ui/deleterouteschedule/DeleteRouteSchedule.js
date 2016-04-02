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
     * Delete RouteSchedule UI Widget
     * Delete mechanism works by the extenstion of DeleteEntity.js
     * @author Mrunal
     */
    $W.DeleteRouteSchedule = function(el, oArgs) {
        $W.DeleteRouteSchedule.superclass.constructor.call(this, el, oArgs);
    };
    $L.extend($W.DeleteRouteSchedule, $W.DeleteEntity, {
        /*Defining call backs*/
    	/**
    	 * Shows The Message Of Success Or Failure Case
    	 */
        oCallBacks: {
            oDeleteEntityCallBack: {
                success: function(o) {
                    $D.getElementsByClassName("info", null, this.elBase)[0].innerHTML = "<span class='title'>Success:</span> The selected routeschedules were successfully deleted.";
                },
                failure: function(o) {
                    $D.getElementsByClassName("info", null, this.elBase)[0].innerHTML = "<span class='title'>Failure:</span> Deleteion of the selected routeschedules failed.";
                }
            },
            oReloadListCallBack: {
                success: function(o) {
                    /*Destroying behavior layers*/
                    this.oListEventManager.destroy();
                    this.elBase.innerHTML = o.responseText;
                    /*Re-initializing*/
                    $W.DeleteRouteSchedule.superclass.constructor.call(this, this.elBase, this.oConfiguration);
                    $D.getElementsByClassName("info", null, this.elBase)[0].innerHTML = "<span class='title'>Success:</span> The selected routeschedules were successfully deleted.";
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

            $U.Connect.asyncRequest('GET', "@APP_CONTEXT@/view/controlpanel/?markup=DeleteRouteSchedule&body=true", this.oCallBacks.oReloadListCallBack);
        },
        deleteEntity: function(aKeys) {
            var sParams = "";
            sParams = aKeys.join(":");
            /*Hitting the server URL to delete the entities*/
            this.oCallBacks.oDeleteEntityCallBack.scope = this;

			$U.Connect.asyncRequest('GET', "@APP_CONTEXT@/form/admin/?command_type=delete_routeschedule&routescheduleList=" + sParams, this.oCallBacks.oDeleteEntityCallBack, null);
		 },
		//Shows The Warning and allows to cancel the procedure
        refresh: function() {
            this.onCancel();
            $D.getElementsByClassName("info", null, this.elBase)[0].innerHTML = "<span class='title'>Please Note:</span> On deleting a routeschedule, all the associated trips and its details will be deleted..";
        }
    });
})();
