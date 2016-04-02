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
     * Delete Hardware UI Widget
     * @author joshua
     */
    $W.DeleteHardware = function(el, oArgs) {
    	this.elBase=el;
        $W.DeleteHardware.superclass.constructor.call(this, el, oArgs);
        this.addAdditionalListeners();
        var autocomplete=this.setAutoCompleteHardwareList();
        this.setAutoCompleteHardwareList();
    };
    $L.extend($W.DeleteHardware, $W.DeleteEntity, {
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
    	
                    $D.getElementsByClassName("info", null, this.elBase)[0].innerHTML = "<span class='title'>Success:</span> The selected Hardwares were successfully deleted.";
                    this.setAutoCompleteHardwareList();
                },
                failure: function(o) {
                    $D.getElementsByClassName("info", null, this.elBase)[0].innerHTML = "<span class='title'>Failure:</span> Deleteion of the selected Hardwares failed.";
                    this.setAutoCompleteHardwareList();
                }
            },
            oReloadListCallBack: {
                success: function(o) {
                    /*Destroying behavior layers*/
                    this.oListEventManager.destroy();
                    this.elBase.innerHTML = o.responseText;
                    /*Re-initializing*/
                    
                    $W.DeleteHardware.superclass.constructor.call(this, this.elBase, this.oConfiguration);
                    if(response == false){
          	          
                        $D.getElementsByClassName("info", null, this.elBase)[0].innerHTML = "<span class='title'>Failure:Some of the selected hardware module/s used by vehicles are still involved in active trip, Hence deletion failed.</span>";
                        response= true;
        				}
        				else{
        				
        					$D.getElementsByClassName("info", null, this.elBase)[0].innerHTML = "<span class='title'>Success:</span> The selected hardware/s were successfully deleted.";
        				}
                    this.setAutoCompleteHardwareList();
                },
                failure: function(o) {
                    $D.getElementsByClassName("info", null, this.elBase)[0].innerHTML = "<span class='title'>Failure:</span> Unable to get data from the server.Try reloading the page";
                    this.setAutoCompleteHardwareList();
                }
            }
        },
        /**
         * Default Search String
         */
        DEFAULT_SEARCH_STRING: "Search...",
        
        /**
         * Setups for showing autocomplete
         */
        setAutoCompleteHardwareList: function(){
        	this._generateDataSourceHardwareList();
            var autoCompleteHardwareList = new $YW.AutoComplete(this.searchInputHardwareList, $D.getElementsByClassName("search-autocomplete-hardwarelist")[0], this.oDataSourceHardwareList);
            autoCompleteHardwareList.prehighlightClassName = "yui-ac-prehighlight";
            autoCompleteHardwareList.useShadow = true;
            this.deleteHardwareListener();
        },
        
        /**
         * Generates Datasource for autocomplete
         */
        _generateDataSourceHardwareList: function(){
        	  var dataSourceHardwareList = [];
       
        	  var val=null;
        	  var result=new Array();
        	  var elList = YAHOO.util.Dom.getElementsByClassName("tlist", null, this.elBase)[0];

        	       var cchild= YAHOO.util.Dom.getElementsByClassName("l-col imei first", null, elList, function(elTarget) {        	           
        	                     
        	  var re=elTarget.innerHTML;
        	  for(var i=0;i<re.length;i++)
        	  {
        	  val=re.split(">");

        	  }
        	  if(val[1]!=null){
        	
        	          var data={
        	            "name":val[1]
        	            };
        	  }
        	        dataSourceHardwareList.push(data);
        	  });


            var oDataSourceHardwareList=null;
            this.oDataSourceHardwareList = new $YU.LocalDataSource(dataSourceHardwareList);
            this.oDataSourceHardwareList.responseSchema = {
                fields: ["name"]
            };

        },
        /**
         * Selects an item in the search with the given item name (object)
         */
        selectItemHardwareList: function(objectName){
            /*Retaining the current scope*/
      	var val=null;
      	var result=new Array();
      	var elList = YAHOO.util.Dom.getElementsByClassName("tlist")[0];

      	var cchild= YAHOO.util.Dom.getElementsByClassName("l-row entity-record slist-item", null, elList, function(elTarget) {        	           
        YAHOO.util.Dom.getElementsByClassName("l-col imei first", null, elTarget, function(el) { 

      	 var re=el.innerHTML;
      		  for(var i=0;i<re.length;i++)
      		  {
      		  val=re.split(">");

      		  }
      	if(val[1]!=objectName){
      			
      	  YAHOO.util.Dom.addClass(elTarget,"yui-hidden");
      	  
      	}else{
      		
      		if(YAHOO.util.Dom.hasClass(elTarget,"yui-hidden")){
      			YAHOO.util.Dom.removeClass(elTarget,"yui-hidden");
      		}		
      	}
      	    }); 

      	 });
        },
        
        deleteHardwareListener: function(){
        	 this.searchInputHardwareList = $D.getElementsByClassName("search-item-input-hardwarelist",null,this.elBase)[0];
        	 var enterKeyListenerUserList = new $YU.KeyListener(this.searchInputHardwareList, {
                 "keys": 13
             }, {
                 "fn": function(){
                     this.selectItemHardwareList(this.searchInputHardwareList.value);
                 },
                 "scope": this,
                 "correctScope": true
             });
             enterKeyListenerUserList.enable();
             $U.addDefaultInputText(this.searchInputHardwareList,this.searchInputHardwareList.value);
             /**
              * Avoiding possible memory leaks;
              */
             enterKeyListenerUserList = null;
            
        },
        
        addAdditionalListeners: function() {
            this.searchInputHardwareList = $D.getElementsByClassName("search-item-input-hardwarelist",null,this.elBase)[0];
            
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
            
            $U.Connect.asyncRequest('GET', "/fleet/view/controlpanel/?markup=DeleteHardware&body=true", this.oCallBacks.oReloadListCallBack);
        },
        deleteEntity: function(aKeys) {
            var sParams = "";
            sParams = aKeys.join(":");
            /*Hitting the server URL to delete the entities*/
            this.oCallBacks.oDeleteEntityCallBack.scope = this;

           
            
            $U.Connect.asyncRequest('GET', "/fleet/form/admin/?command_type=delete_hardware&hardwareList=" + sParams, this.oCallBacks.oDeleteEntityCallBack, null);
        },
        refresh: function() {
            this.onCancel();
            $D.getElementsByClassName("info", null, this.elBase)[0].innerHTML = "<span class='title'>Please Note:</span> Before deleting a hardware module make sure that there is no vehicle being assigned to the selected module ..";
        }
    });
})();
