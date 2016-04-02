(function() {
    var $Y = YAHOO;
    var $B = $Y.Bubbling;
    var $L = $Y.lang;
    var $YU = $Y.util;
    var $E = $Y.util.Event;
    var $D = $Y.util.Dom;
    var $YW = $Y.widget;
    var $U = getPackageForName("com.i10n.fleet.Utils");
    var $W = getPackageForName("com.i10n.fleet.widget.ui");
    var $WU = getPackageForName("com.i10n.fleet.widget.util");
    var $R=getPackageForName("com.i10n.fleet.rowfilter");
    var flag = false;
    
var indexs=null;
var oRecord;
var backupdata;
var reportel;
var reportconfig;
     /**
     * Search Tool Bar Widget(Top Bar)
     *
     * @author subramaniam
     */
    $W.MobileSearchToolBar = function(el, oArgs) {
        this.elBase = el;
       
        this._widgets = {};
        this._oInitParams = oArgs;
        this.init(el, oArgs);
    };
   $W.MobileSearchToolBar.setDataTable=function(elreport,config){
	   reportel=elreport;
	   reportconfig=config;

             //table.getRecordSet().reset();
             //table.refreshView();
            
        
     };
 $W.MobileSearchToolBar.setMobileDataTable=function(elreport,config){
	   reportel=elreport;
	   reportconfig=config;

             //table.getRecordSet().reset();
             //table.refreshView();
            
        
     };
    $L.augmentObject($W.MobileSearchToolBar, {
        DEFAULT_KEY_SEARCH: "name",
        EVT_ON_ASSIGN_VEHICLE: "assignVehicle"
    });
    // $L.extend($W.MobileSearchToolBar, $W.GroupedReport);
    $L.augmentProto($W.MobileSearchToolBar, $YU.EventProvider);
    $L.augmentObject($W.MobileSearchToolBar.prototype, {
        /**
         * Datasource of the widget
         */
        _oDataSource: null,
        /**
         * Autocomplete Object
         */
        _oAutoComplete: null,
        /**
         * Initializer function
         * @param {Object} el
         * @param {Object} oArgs
         */
        init: function(el, oArgs) {
           
            this._searchKey = $W.MobileSearchToolBar.DEFAULT_KEY_SEARCH;
            if (oArgs.searchkey) {
                this._searchKey = oArgs.searchkey;
            }
            this._oDataSource = oArgs.datasource;
            

            
            this.addEventListeners();
            this._setAutoComplete(this.SEARCH_KEY);
          
            var enterKeyListener = new $YU.KeyListener(document, {
                "keys" : 13
            }, {
                "fn" : function() {
                    this.search();
                },
                "scope" : this,
                "correctScope" : true
            });
            enterKeyListener.enable();
            /**
             * Avoiding possible memory leaks.
             */
            searchButton = null;
            var assignvehicle = $D.getElementsByClassName("button-assign-vehicle");
            $W.Buttons.addDefaultHandler(assignvehicle, function(oArgs){
                this.fireEvent($W.MobileSearchToolBar.EVT_ON_ASSIGN_VEHICLE);
            }, null, this);
            this.configureAttributes();
        },
        configureAttributes: function(){
           this.createEvent($W.MobileSearchToolBar.EVT_ON_ASSIGN_VEHICLE);
        },
       search: function(){
           /* TODO : To Be Handled*/
        	  var oData = {};
              
              oData = $U.Arrays.mapToArray(this._oDataSource._oData);
      
            var oRecord = new $W.Report(reportel, reportconfig);

              //check for Reportsettings or Alertsettings
        
              var delrow=0;
              var schedule = oData[0].schedule;
              var email = oData[0].email;

         
             
              if(schedule != null){
                   flag = true;
                  //fetch the id of first row of Datatable
                 var oRecdata=oRecord.getRecordSet().getRecords();
                 
                   var data=$U.Arrays.mapToArray(oRecdata);

                   
                 
                   for( var i=0;i<=data.length;i++)

                   {
                  //alert(oData.toSource());
               
                      if(data[i]._oData.name == $D.getElementsByClassName("search-input", null, this.elBase)[0].value)
                     {
                    delrow++;
                    status=true;
                     }
                    else
                        {                       		
                         oRecord.getRecordSet().deleteRecord(delrow);
                         oRecord.refreshView();
                          }
                        status=false;

                   }                                  
                               
                             

                  /*var tagIdInReport = $D.getElementsByClassName("yui-dt-first yui-dt-even")[0].id;
                
                  var firstRowNumberInReport =  tagIdInReport.replace("yui-rec","");
                  firstRowNumberInReport = firstRowNumberInReport.trim();
                  for(var i = 0,j = firstRowNumberInReport; i < oData.length; i++,j++){
                      if($D.getElementsByClassName("search-input", null, this.elBase)[0].value != "Eg.username")
                      {
                        
                      $D.addClass('yui-rec'+j, "disabled");
                      if(oData[i].name == $D.getElementsByClassName("search-input", null, this.elBase)[0].value){
                          $D.removeClass('yui-rec'+j, "disabled");
                          }
                      }
                  }*/
              
              }
              else if(schedule == null && email != null){
            
                  //fetch the id of first row of Datatable
                
                   var oRecdata=oRecord.getRecordSet().getRecords();
                 
                   var data=$U.Arrays.mapToArray(oRecdata);

                   
                 //alert(data[0]._oData.length);
                   for( var i=0;i<=data.length;i++)

                   {
                  //alert(oData.toSource());
               
                    
                  if(data[i]._oData.name == $D.getElementsByClassName("search-input", null, this.elBase)[0].value)
                     {
                    delrow++;
                    status=true;
                     }
                    else
                        {                       		
                         oRecord.getRecordSet().deleteRecord(delrow);
                         oRecord.refreshView();
                          }
                        status=false;
                     // delrow=0;	

                   }
              

                 
              }
              else if(email == null){
     
                     //fetch the id of first row of Datatable
                 
                 var oRecdata=oRecord.getRecordSet().getRecords();
                 
                   var data=$U.Arrays.mapToArray(oRecdata);

                   
                 //alert(data[0]._oData.length);
                   for( var i=0;i<=data.length;i++)

                   {
                  //alert(oData.toSource());
               
                    
                  if(data[i]._oData.name == $D.getElementsByClassName("search-input", null, this.elBase)[0].value)
                     {
                    delrow++;
                    status=true;
                     }
                    else
                        {                       		
                         oRecord.getRecordSet().deleteRecord(delrow);
                         oRecord.refreshView();
                          }
                        status=false;
                     // delrow=0;	

                   }
              
              }
       },
     
        /**
         * Adds Event Listeners.
         */
        addEventListeners: function() {
                      
                      var status=false;
      
            $W.Buttons.addDefaultHandler($D.getElementsByClassName("search-button", null, this.elBase),function(){
                 /* TODO : To Be Handled*/
                var oData = {};
              
                oData = $U.Arrays.mapToArray(this._oDataSource._oData);
     
              var oRecord = new $W.Report(reportel, reportconfig);
             
                //check for Reportsettings or Alertsettings
            
                var delrow=0;
                var schedule = oData[0].schedule;
                var email = oData[0].email;

           
               
                if(schedule != null){
                     flag = true;
                    //fetch the id of first row of Datatable
                   var oRecdata=oRecord.getRecordSet().getRecords();
                   
                     var data=$U.Arrays.mapToArray(oRecdata);

                     
                   
                     for( var i=0;i<=data.length;i++)

                     {
                   
                 
                        if(data[i]._oData.name == $D.getElementsByClassName("search-input", null, this.elBase)[0].value)
                       {
                      delrow++;
                      status=true;
                       }
                      else
                          {                       		
                           oRecord.getRecordSet().deleteRecord(delrow);
                           oRecord.refreshView();
                            }
                          status=false;

                     }                                  
                                 
                               

                    /*var tagIdInReport = $D.getElementsByClassName("yui-dt-first yui-dt-even")[0].id;
                  
                    var firstRowNumberInReport =  tagIdInReport.replace("yui-rec","");
                    firstRowNumberInReport = firstRowNumberInReport.trim();
                    for(var i = 0,j = firstRowNumberInReport; i < oData.length; i++,j++){
                        if($D.getElementsByClassName("search-input", null, this.elBase)[0].value != "Eg.username")
                        {
                          
                        $D.addClass('yui-rec'+j, "disabled");
                        if(oData[i].name == $D.getElementsByClassName("search-input", null, this.elBase)[0].value){
                            $D.removeClass('yui-rec'+j, "disabled");
                            }
                        }
                    }*/
                
                }
                else if(schedule == null && email != null){
              
                    //fetch the id of first row of Datatable
                  
                     var oRecdata=oRecord.getRecordSet().getRecords();
                   
                     var data=$U.Arrays.mapToArray(oRecdata);

                     
                   //alert(data[0]._oData.length);
                     for( var i=0;i<=data.length;i++)

                     {
                    //alert(oData.toSource());
                 
                      
                    if(data[i]._oData.name == $D.getElementsByClassName("search-input", null, this.elBase)[0].value)
                       {
                      delrow++;
                      status=true;
                       }
                      else
                          {                       		
                           oRecord.getRecordSet().deleteRecord(delrow);
                           oRecord.refreshView();
                            }
                          status=false;
                       // delrow=0;	

                     }
                

                   
                }
                else if(email == null){
       
                       //fetch the id of first row of Datatable
                   
                   var oRecdata=oRecord.getRecordSet().getRecords();
                   
                     var data=$U.Arrays.mapToArray(oRecdata);

                     
                   //alert(data[0]._oData.length);
                     for( var i=0;i<=data.length;i++)

                     {
                    //alert(oData.toSource());
                 
                      
                    if(data[i]._oData.name == $D.getElementsByClassName("search-input", null, this.elBase)[0].value)
                       {
                      delrow++;
                      status=true;
                       }
                      else
                          {                       		
                           oRecord.getRecordSet().deleteRecord(delrow);
                           oRecord.refreshView();
                            }
                          status=false;
                       // delrow=0;	

                     }
                
                }
            }, null, this);
          
            var elSearchBox = $D.getElementsByClassName("search-input", null, this.elBase)[0];
            $U.addDefaultInputText(elSearchBox, elSearchBox.value);
            elSearchBox = null;
        },
     
        /**
         * Setups for showing Autocomplete
         */
        _setAutoComplete: function() {
            var oAutoComplete = new $YW.AutoComplete($D.getElementsByClassName("search-input", null, this.elBase)[0], $D.getElementsByClassName("search-autocomplete", null, this.elBase)[0], this._oDataSource);
            oAutoComplete.prehighlightClassName = "yui-ac-prehighlight";
            oAutoComplete.useShadow = true;
            var searchKey = this._searchKey;
            /**
             * TODO : The following is a hack to produce search results.
             * A decorator Datasource needs to be implemented which runs on actual datasource.
             * and filters the results  based on the query.
             */
            oAutoComplete.generateRequest = function(sQuery) {
                return sQuery;
            };
            var superHandler = oAutoComplete.handleResponse;
            oAutoComplete.handleResponse = function(sQuery, oResponse, oPayload) {
                if (oResponse.results && $L.isArray(oResponse.results)) {
                    var aFilteredData = [];
                    for (var i = 0; i < oResponse.results.length; i++) {
                        var oResult = oResponse.results[i];
                        if ($L.isString(oResult[searchKey])) {
                            if (oResult[searchKey].toLowerCase().indexOf(sQuery.toLowerCase()) === 0) {
                                aFilteredData.push(oResult[searchKey]);
                            }
                        }
                    }
                    oResponse.results = aFilteredData;
                }
                superHandler.call(this, sQuery, oResponse, oPayload);
            };
            this._oAutoComplete = oAutoComplete;
            oAutoComplete = null;
        }
    });
 
})();
