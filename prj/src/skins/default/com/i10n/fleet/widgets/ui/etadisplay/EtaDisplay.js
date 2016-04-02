(function(){
    var $W = getPackageForName("com.i10n.fleet.widget.ui");
    var $WU = getPackageForName("com.i10n.fleet.widget.util");
    var $U = getPackageForName("com.i10n.fleet.Utils");
    var $L = YAHOO.lang;
    var $E = YAHOO.util.Event;
    var $D = YAHOO.util.Dom;
    var $YU = YAHOO.util;
    var $B = YAHOO.Bubbling;
    var $YW = YAHOO.widget;
    var $V = getPackageForName("com.i10n.fleet.widget.view");
  
    /**
     * Sas Report Settings Sub Nav Page(ETA Display)
     *
     * @author Swathi
     */
    $W.EtaDisplay = function(el, params){
        /*Declaring the memeber properties*/
        this.elBase = el;
        this._widgets = {};
        /*Initializing*/
        this.init(el, params);
       
    };
    $L.augmentProto($W.EtaDisplay, $YU.EventProvider);
  
    $L.augmentObject($W.EtaDisplay, {
        PAGE_ID: "etadisplay"
    });

    $L.augmentObject($W.EtaDisplay.prototype, {
    	
        /**
         * Initialization function
         */
        init: function(el, params){
         
           if (!params) {
               params = {};
           }

            var aElButtons = $D.getElementsByClassName("button-done", null, el)[0];
            $W.Buttons.addDefaultHandler(aElButtons, function(oArgs){
                var elErrDialog = $D.getElementsByClassName("simpledialog", null, this.elBase)[0];
                if (!$D.hasClass(elErrDialog, "disabled")) {
                    $D.addClass(elErrDialog, "disabled");
                }
            }, null, this);
            
            this.addEventListeners();
       
        },

        generateSubmitted: {
            success: function(o){
                var oResponse = JSON.parse(o.responseText);
                var data = oResponse.etadata;
        		var etatablesDiv = $D.getElementsByClassName("etatables")[0];
        		var space = "<br></br><br></br>";
        		etatablesDiv.innerHTML = space;
        		if(data.length != 0){
        		for(var i=0; i<data.length; i++){
                    var tableStr = "<br></br><br></br><table class='stopinfo' cellspacing='0'>"
                    +"<tr><td colspan='4'class='stopdetail'>Stop Name: "+data[i].stopname+"</td></tr>"
                    +"<tr><td class='stopdetail'>Vehicle Name</td><td class='stopdetail'>Route Name (Destination)</td>"
                    //+"<td class='stopdetail'>Next 3 Stops</td>"
                    +"<td class='stopdetail'>Expected Time(in mins)</td></tr>"
                    +"<tr><td class='stopdata'>"+data[i].vehiclename+"</td>"
                    +"<td class='stopdata'>"+data[i].routename+"</td>"
                    /*+"<td class='stopdata'><select class='next3stops'>";
                    var stopnames = data[i].stopnames;
                    for(var j=(stopnames.length)-1; j>=0; j--){
                    tableStr += "<option>"+"Stop"+"</option>"
                    }
                    tableStr += "</select></td>"*/
                    +"<td class='stopdata'>"+data[i].expectedtime+"</td>"
                    +"</tr>"
                    +"</table>";
                    etatablesDiv.innerHTML += tableStr;
        		}
        		}else{
        			alert("The selected Stop doesn't has any incoming vehicles.");
        		}

            },
            failure: function(o){
            }
        },
        onGenerateReport: function(){
        	if(SELECTED_STOP_VALUE == 0){
        		alert("Please select the stop from the list");
        	} else{
            	$U.Connect.asyncRequest('GET', "/fleet/view/sas/?module=/blocks/json&data=view&report=etadisplay&localTime="+$U.getLocalTime()+"&stopvalue="+SELECTED_STOP_VALUE, this.generateSubmitted,null);
        	 	elList = aResult = sParams = null;
        	}
        },
        addEventListeners: function(){
        	$W.Buttons.addDefaultHandler($D.getElementsByClassName("button-generate-report", null, this.elBase)[0], this.onGenerateReport, null, this);
        	/*Preventing memory leaks*/
            body = null;
        }
      
    });
   
  
})();
