(function(){
    var $B = YAHOO.Bubbling;
    var $L = YAHOO.lang;
    var $YU = YAHOO.util;
    var $E = YAHOO.util.Event;
    var $D = YAHOO.util.Dom;
    var $YW = YAHOO.widget;
    var $U = getPackageForName("com.i10n.fleet.Utils");
    var $W = getPackageForName("com.i10n.fleet.widget.ui");
    var $V = getPackageForName("com.i10n.fleet.widget.view");
    var thisvariable = null;
    var oRecord = null;
    var checked; 
    /**
     * Vehicle Status Report Widget for Fleetcheck project.
     *
     * @extends com.i10n.fleet.widget.ui.GroupedReport
     * @author sabarish
     */
    $W.AlertStatus = function(el, oArgs){
        this.initAlertStatus(el, oArgs);
       
        thisvariable = this;
        $V.BaseView.subscribeFn("statusUpdate",this.callback);
				
    };
    $L.augmentObject($W.AlertStatus, {
        /**
         * Custom Cell Formatter for formatting status cell
         * @param {Object} elCell
         * @param {Object} oRecord
         * @param {Object} oColumn
         * @param {Object} oData
         */
        "FORMATTER_STATUS": function(elCell, oRecord, oColumn, oData){
            elCell.innerHTML = "<div class='inline-block cell-custom cell-status " + oData + "'>" + oData + "</div>";
        }
    });
    $L.extend($W.AlertStatus, $W.GroupedReport);
    $L.augmentObject($W.AlertStatus.prototype, {
        /**
         * Object Map of widgets initialized by the this widget
         */
        _widgets: {},
        /**
         * Initializes widget
         * @param {Object} el
         * @param {Object} oArgs
         */
           initAlertStatus: function(el, oArgs){
	
          
            var config = this.constructConfig(oArgs);
           
            //$W.AlertStatus.superclass.constructor.call(this, el, config);
            var AlertPopUp = new $W.AlertStatus.TripCreationPopUp($D.getElementsByClassName("comments")[0],oArgs);
            this._widgets.TripCreationPopUp = AlertPopUp;
  		  if(config)
            {
             oRecord = new $W.GroupedReport(el, config);
         
             
            }
  		 YAHOO.util.Event.addListener($D.getElementsByClassName("yui-dt-button"), "click",function(oArgs){  
             
                var oRow = oRecord.getRecord(oArgs.target);
                var Countval=oRow.getCount();                                        
         	checked=oRow.getData();
                $D.addClass('yui-rec'+Countval, "disabled");
               
            
         	//alert(" checked true"+checked.refid.toSource());
         	//var delereRow=oRecord.getRecordSet().deleteRecord();
                     	
         	this.showAlertPopUp();
         	
             },null,this);
  		  this._widgets.TripCreationPopUp.subscribe($W.ViolationPopUp.EVT_ON_SUBMIT,this.onSubmit , this, true);
		
        },
       
       /**
         * Constructs a config object for Reports to initialize from the data given
         * @param {Object} oArgs
         */
        constructConfig: function(oArgs){
        	
        	var config = {};
            var data = [];
            var count = 0;
            var columndefs = [{
                key: "refid",
                hidden: true
            }, {
                key: "vehiclename",
                label: "Vehicle Name",
                sortable: true,
                sortOptions: {
                    defaultDir: $YW.DataTable.CLASS_DESC
                },
                resizeable: false
            },  {
                key: "drivername",
                label: "Driver Name",
                sortable: true,
                resizeable: false
            }, {
            	key:"alerttime",
            	label:"Alert Time",
            	formatter: $W.Report.FORMATTERS.STRING_DATE_FORMATTER,
            	sortable:true,
            	resizeable:false
            },{
            	key:"alertlocation",
            	label:"Alert Location",
            	sortable:true,
            	resizeable:false
            },
               {
                key: "alerttype",
                label: "Alert Type",
                sortable: true,
                resizeable: false
            },
            {
                key: "alertvalue",
                label: "Alert Value",
                sortable: true,
                resizeable: false
            }];
            config.datasource = oArgs.datasource;
            config.columndefs = columndefs;
            config.reportconfig = {};
            config.options = {
                "select": {
                    "enabled": true,
                    "selected": true
                },
                "print": {
                    "enabled": true
                },
                "groups": {
                    "enabled": true,
                    "titleId": "name"
                }
            };
            return config;
         
        },
        oSaveCallback: {
            success: function(o) {
        	alert("in success");
    	
        	
            },
            failure: function(o) {
            	alert("in failure");
            }
    },
        onSubmit:function()
        {
        
         message= YAHOO.util.Dom.get('comments-id').value;
         
         $U.Connect.asyncRequest("POST","/fleet/form/reportsettings/?command_type=violation&message="+message, this.oSaveCallback, $U.MapToParams(checked));
         this._widgets.TripCreationPopUp.hide();
         
        	
        },
        showAlertPopUp: function(){
        	
       	 var alert = $D.getElementsByClassName("comments");
       	   $D.removeClass(alert,"disabled");
       	this._widgets.TripCreationPopUp.render();
           this._widgets.TripCreationPopUp.show();
           
       },
        
        callback:{
        	success: function(pushletData){
        		thisvariable.liveupdate(pushletData);
        	}
        },
        
        liveupdate:function(result){
			result = eval('('+result+')');
			var cont = result.content;
			 
			for(var i in cont){
				for(j=0;j<oRecord.getRecordSet().getLength();j++){
					var oData = cont[i];
					var pushletDataId = oData.refid;
					var tableDataId = oRecord.getRecordSet().getRecord(j).getData("refid");
					if( pushletDataId === tableDataId){
						oData.select = oRecord.getRecordSet().getRecord(j).getData("select");
						oRecord.getRecordSet().setRecord(oData,oRecord.getRecordSet().getRecordIndex(oRecord.getRecordSet().getRecord(j)));
						oRecord.render();
					}
				}
			}
		}
    });
    $W.AlertStatus.TripCreationPopUp = function(el,oArgs){
        
        /*Initializing*/
    	var recorddata=oArgs;
        this.initCreateTrip(el,oArgs);
       
       
      this.message=YAHOO.util.Dom.get('comments-id').value;
      
       // $W.Buttons.addDefaultHandler($D.getElementsByClassName("submit-button", null, el),this.onUpdate ,null,this);
       
        $W.Buttons.addDefaultHandler($D.getElementsByClassName("cancel-button", null, el),this.onClose ,null,this);
      
        
    };
    $L.extend($W.AlertStatus.TripCreationPopUp, $W.ViolationPopUp);
    $L.augmentObject($W.AlertStatus.TripCreationPopUp.prototype, {
        /*Declaring the datamembers*/
        elDisplayedContent: null,
        
      
        /**
         * The initalization Function
         */
        initCreateTrip: function(el,oArgs){
            this.elDisplayedContent = el;
             
            $W.AlertStatus.TripCreationPopUp.superclass.constructor.call(this, this.elDisplayedContent, {
                fixedcenter: false,
                width: "300px",
                height: "auto"
            });
        },
        
        onClose:function()
        {
        	this._widgets.TripCreationPopUp.hide();
        },
    });
})();
