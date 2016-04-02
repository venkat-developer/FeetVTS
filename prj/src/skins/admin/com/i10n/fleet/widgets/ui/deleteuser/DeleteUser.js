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
     * Delete User UI Widget
     * @author sabarish
     */
    $W.DeleteUser = function(el, oArgs) {
        $W.DeleteUser.superclass.constructor.call(this, el, oArgs);
    };
    $L.extend($W.DeleteUser, $W.DeleteEntity, {
        /*Defining call backs*/
        oCallBacks: {
            oDeleteEntityCallBack: {
                success: function(o) {
                    $D.getElementsByClassName("info", null, this.elBase)[0].innerHTML = "<span class='title'>Success:</span> The selected users were successfully deleted.";
                },
                failure: function(o) {
                    $D.getElementsByClassName("info", null, this.elBase)[0].innerHTML = "<span class='title'>Failure:</span> Deleteion of the selected users failed.";
                }
            },
            oReloadListCallBack: {
                success: function(o) {
                    /*Destroying behavior layers*/
                    this.oListEventManager.destroy();
                    this.elBase.innerHTML = o.responseText;
                    /*Re-initializing*/
                    $W.DeleteUser.superclass.constructor.call(this, this.elBase, this.oConfiguration);
                    $D.getElementsByClassName("info", null, this.elBase)[0].innerHTML = "<span class='title'>Success:</span> The selected users were successfully deleted.";
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

            $U.Connect.asyncRequest('GET', "@APP_CONTEXT@/view/controlpanel/?markup=DeleteUser&body=true", this.oCallBacks.oReloadListCallBack);
        },
        deleteEntity: function(aKeys) {
            var sParams = "";
            sParams = aKeys.join(":");
            /*Hitting the server URL to delete the entities*/
            this.oCallBacks.oDeleteEntityCallBack.scope = this;

			$U.Connect.asyncRequest('GET', "@APP_CONTEXT@/form/admin/?command_type=delete_user&userList=" + sParams, this.oCallBacks.oDeleteEntityCallBack, null);
			//$U.Connect.asyncRequest('GET', "@APP_CONTEXT@/view/controlpanel/?userList=" + sParams, this.oCallBacks.oDeleteEntityCallBack, null);
        },
        refresh: function() {
            this.onCancel();
            $D.getElementsByClassName("info", null, this.elBase)[0].innerHTML = "<span class='title'>Please Note:</span> On deleting a user, all the associated trips and its details will be deleted..";
        }
    });
})();
