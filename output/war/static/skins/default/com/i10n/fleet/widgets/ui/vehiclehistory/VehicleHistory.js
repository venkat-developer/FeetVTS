(function(){
    var $W = getPackageForName("com.i10n.fleet.widget.ui");
    var $L = YAHOO.lang;
    var $E = YAHOO.util.Event;
    var $D = YAHOO.util.Dom;
    var $YU = YAHOO.util;
    var $YW = YAHOO.widget;
    var $U = getPackageForName("com.i10n.fleet.Utils");
    var dataReturned=null;
    var vehicleName=null;
	var vehicleID=null;
	var timeFrame=null;
    /**
     * Activity Report Vehicle Widget
     * @author Prashanth
     */
    $W.VehicleHistory = function(el, oArgs){
        this.elBase = el;
        this.initActivityReportVehicle(el, oArgs);
    };
    $L.augmentObject($W.VehicleHistory, {
        PAGE_ID: "vehiclehistory"
    });
    $L.extend($W.VehicleHistory, $W.BaseReport, {
        /**
         * Datatable representing this widget
         */
        _oDataTable: null,
        /**
         * Datasource for the widget.
         */
        _oDataSource: null,
        /**
         * Returns the data url needed.
         */
        getDataURL: function(){
    	
            var URL = null;
           
            var selectedVehicle = this.get($W.BaseReport.ATTR_SELECTED_ITEM);
            
            var oTimeFrame = this.get($W.BaseReport.ATTR_SELECTED_TIMEFRAME);
           
       
            if (oTimeFrame && selectedVehicle) {
                URL = "/fleet/view/reports/?module=/blocks/json&data=view&report=vehiclehistory";
                URL += "&localTime=" +$U.getLocalTime();
                URL += "&vehicleID=" + selectedVehicle;
                URL += "&startdate=" + oTimeFrame.startDate;
                URL += "&enddate=" + oTimeFrame.endDate;
                URL += "&interval=" + oTimeFrame.interval;
            }
           
            return URL;
        },
        /**
         * process the data that came as a result of get method
         * @param {Object} oSelf
         */
        getData: function(oSelf){
            try {
                var selectedVehicle = oSelf.get($W.BaseReport.ATTR_SELECTED_ITEM);
                var data =dataReturned= oSelf.get($W.BaseReport.ATTR_DATA);
                if ($L.isObject(data.activity[selectedVehicle])) {
                    if ($L.isArray(data.activity[selectedVehicle].positions)) {
                        return oSelf.sendData(data.activity[selectedVehicle].positions);
                    }
                }else{
                }
            } 
            catch (ex) {
                $U.alert({
                    message: "There was an eror loading the data!!!" + ex
                });
                return null;
            }
            return null;
        },
        /**
         * HACK : Due to problem returning an array
         * @param {Object} aResults
         */
        sendData: function(aResults){
            return aResults;
        },
        oCallBack : {
    		getVehicleName : {
    		success : function(o){
    		var oResponse = JSON.parse(o.responseText);
    		vehicleName = oResponse.vehicle.name ;
    		$D.getElementsByClassName("vehiclename", null, this.elBase)[0].innerHTML = vehicleName;
    	}, 
    	failure : function(o){
    		$D.getElementsByClassName("vehiclename", null, this.elBase)[0].innerHTML = this.get($W.BaseReport.ATTR_SELECTED_ITEM);
    	}

    	}
    	},
        /**
         * Renders the dataTable for the incoming data
         */
        render: function(){
            var selectedVehicle =vehicleID= this.get($W.BaseReport.ATTR_SELECTED_ITEM);
     
            
            var oData = this.get($W.BaseReport.ATTR_DATA);
            var widgetEl = $D.get("vehiclehistory");
            this.oCallBack.getVehicleName.scope = this;
    		$U.Connect.asyncRequest('GET', "/fleet/view/reports/?module=/blocks/json&data=view&report=vehiclehistory"+
    				"&getvehiclename=true&vehicleID="+selectedVehicle, this.oCallBack.getVehicleName,null);
    		var elErrDialog = $D.getElementsByClassName("simpledialog", null, this.elBase)[0];
            $D.getElementsByClassName("vehiclename", null, this.elBase)[0].innerHTML = selectedVehicle;
           
            var elErrDialog = $D.getElementsByClassName("simpledialog", null, this.elBase)[0];
            if (oData) {
                $D.removeClass(widgetEl, "empty");
                if (!$D.hasClass(elErrDialog, "disabled")) {
                    $D.addClass(elErrDialog, "disabled");
                }
               
                if (!this._oDataTable || !this._oDataSource) {
                	
                    var oConfig = this.constructConfig();
                    this._oDataSource = oConfig.datasource;
                    this._oDataTable = new $W.VehicleHistory.DataTable(this.elBase, oConfig, {
                        "selectedVehicle": selectedVehicle
                    });
                }
                var oCallback = {
                    success: this._oDataTable.onDataReturnInitializeTable,
                    failure: this._oDataTable.onDataReturnInitializeTable,
                    scope: this._oDataTable,
                    argument: this._oDataTable.getState()
                };
                var selectedTimeFrame = timeFrame= this.get($W.BaseReport.ATTR_SELECTED_TIMEFRAME);
                if (selectedVehicle && selectedTimeFrame) {
                    this._oDataTable._oReportDataSource.sendRequest(this, oCallback);
                }
                this._oDataTable.getState().pagination.paginator.setPage(1, false);
            }
            else {
                if ($D.hasClass(elErrDialog, "disabled")) {
                    $D.removeClass(elErrDialog, "disabled");
                }
            }
            elErrDialog = null;
            widgetEl = null;
        },
        show: function(){
            if (!(this._oDataTable && this._oDataSource)) {
                this._isDataStale = true;
            }
            $W.VehicleHistory.superclass.show.call(this);
        },
        getPageID: function(){
            return $W.VehicleHistory.PAGE_ID;
        },
        /**
         * Constructs a config object for Reports to initialize from the data given
         * @param {Object} reportData
         */
         constructConfig: function(){
            var config = {};
            var datasource = new $YU.FunctionDataSource(this.getData);
            datasource.responseType = $YU.DataSource.TYPE_JSARRAY;
            datasource.responseSchema = {
                fields: ["index","imei","updatedtime","updatedbyuser","vehicleattended", "batterychanged", "fusechanged"]
            };
            var columndefs = [{
    			key: "index",
    			label: "No.",
    			sortable: true
    		},{
                key: "imei",
                label: "Updated IMEI",
                sortable: true,
                resizeable: false,
//                parser: YAHOO.util.DataSource.parseNumber
                
            },
              {
                key: "updatedtime",
                label: "Updated Time",
                sortable: true,
                resizeable: false,
                formatter: $W.Report.FORMATTERS.STRING_DATE_FORMATTER
            },{
                key: "updatedbyuser",
                label: "Updated By",
                sortable: true,
                resizeable: false
            }, {
                key: "vehicleattended",
                label: "Attended By",
                sortable: true,
                resizeable: false,
                //formatter: $W.Report.FORMATTERS.STRING_DATE_FORMATTER
            }, {
                key: "batterychanged",
                label: "Battery Changed",
                sortable: true,
                resizeable: false
            },  {
                key: "fusechanged",
                label: "Fuse Changed",
                sortable: true,
                resizeable: false,
//                parser: YAHOO.util.DataSource.parseNumber
            }/*, {
                key: "distance",
                label: "Distance Travelled(KM)",
                sortable: true,
                resizeable: false,
                parser: YAHOO.util.DataSource.parseNumber
            }*/];
            config.datasource = datasource;
            config.columndefs = columndefs;
            config.reportconfig = {
                initialLoad: false
            };
            config.options = {
                "print": {
                    "enabled": true
                }
            };
            return config;
        }
    });
    $L.augmentObject($W.VehicleHistory.prototype, {
        /**
         * Initializes widget
         * @param {Object} el
         * @param {Object} params
         */
        initActivityReportVehicle: function(el, oArgs){
            if (!oArgs) {
                oArgs = {};
            }document.getElementById("exportvehiclehistory").addEventListener("click",function(){
    			var exportvalue;
    			var radiobtlength=$D.getElementsByClassName('exportvehiclehistory').length;
    			var radiobuttons=$D.getElementsByClassName('exportvehiclehistory');
    			for(var i=0;i<radiobtlength;i++){
    				if(radiobuttons[i].checked){
    					exportvalue =radiobuttons[i].value;
    				}
    			}
    			if(exportvalue != null){
    				if(dataReturned !=null){
    					var dataprint=dataReturned.activity[vehicleID];
    					if(exportvalue==="pdf" && timeFrame && vehicleID && dataprint.positions){
    						var URL="/fleet/view/reports/?module=/blocks/json&data=view&report=pdfgeneration";
    						URL += "&reporttype=vehiclehistory" ;
    						URL += "&reportformat="+exportvalue ;
    						URL += "&vehicleName=" +vehicleName;
    						URL += "&vehicleID=" +vehicleID;
    						URL += "&startdate=" +timeFrame.startDate;
    						URL += "&enddate=" +timeFrame.endDate;
    						URL += "&interval=" +timeFrame.interval;
    						YAHOO.util.Connect.asyncRequest('GET',URL,callback,null);
    					}else if(exportvalue==="excel" && timeFrame && vehicleID && dataprint.positions){
    						var URL="/fleet/view/reports/?module=/blocks/json&data=view&report=pdfgeneration";
    						URL += "&reporttype=vehiclehistory" ;
    						URL += "&reportformat="+exportvalue ;
    						URL += "&vehicleName=" + vehicleName;
    						URL += "&vehicleID=" + vehicleID;
    						URL += "&startdate=" + timeFrame.startDate;
    						URL += "&enddate=" + timeFrame.endDate;
    						URL += "&interval=" + timeFrame.interval;
    						YAHOO.util.Connect.asyncRequest('GET',URL,callback,null);
    					}else{
    						alert('No data available to create a output file!');  
    					}
    				}else{
    					alert('No data available to create a output file!');  
    				}
    			}else
    				alert('please select one of the formats specified below to download the report');

    		},false);
            oArgs.dataType = $W.BaseReport.KEY_TYPE_JSON;
            oArgs.timeframe = true;
            $W.VehicleHistory.superclass.constructor.call(this, el, oArgs);
            var aElButtons = $D.getElementsByClassName("button-done", null, el);
            $W.Buttons.addDefaultHandler(aElButtons, function(oArgs){
                var elErrDialog = $D.getElementsByClassName("simpledialog", null, this.elBase)[0];
                if (!$D.hasClass(elErrDialog, "disabled")) {
                    $D.addClass(elErrDialog, "disabled");
                }
            }, null, this);
            aElButtons = null;
            el = null;
            oArgs = null;
        }
    });
    
    /**
	 * Call back function used to open the of file requested by the user to download
	 * returns url of the file created 
	 */
	var callback =
		{   
			success: function(o){
		var oResponse = JSON.parse(o.responseText);
		var url = oResponse.pdf ;
		window.open(url);
	},      
	failure: function(o){
		alert('There is no file created for download !');
	}
		};
    /**
     * Datatable widget for the Activity report
     * @param {Object} el
     * @param {Object} config
     */
    $W.VehicleHistory.DataTable = function(el, config, params){
        this.initDataTable(el, config);
        this.initParams = params;
    };
    /**
     * overridable functions
     * @param {Object} elBufferReport
     */
    $L.extend($W.VehicleHistory.DataTable, $W.Report, {
        generatePrintableDataTable: function(elBufferReport){
            var oBufferConfig = $U.cloneObject(this._oReportConfig);
            oBufferConfig.paginator = null;
            var bufferDataTable = new $YW.DataTable(elBufferReport, this._oColumnDefs, this._oReportDataSource, oBufferConfig);
            bufferDataTable.getRecordSet().deleteRecords(0);
            bufferDataTable.getRecordSet().addRecords(this.getCurrentRecords());
            bufferDataTable.render();
        },
        /**
         * Overridable Function to show a print preview current report in a popup. Generates the markup ina buffer element
         * whose display is set to none.
         */
        printReport: function(){
            var sBufferTemplateContent = null;
            var sReportName = "report";
            var elBufferReport = $D.getElementsByClassName("print-section", null, this.reportElement)[0];
            var elPrintContent = $D.getElementsByClassName("print-skin-template", null, this.baseElement)[0];
            var elPrintTitleContent = $D.getElementsByClassName("title", null, elPrintContent)[0];
            var sTempPrintTitle = null;
            if (this.initParams && this.initParams.selectedVehicle) {
                sTempPrintTitle = elPrintTitleContent.innerHTML;
                elPrintTitleContent.innerHTML = this.initParams.selectedVehicle.toUpperCase() + "  " + elPrintTitleContent.innerHTML;
            }
            sBufferTemplateContent = $U.processTemplate(elPrintContent.innerHTML, {
                reportname: sReportName
            });
            if (this.attrs.get($W.Report.ATTR_SELECT_ENABLED)) {
                this.generateSelectedPrintableDataTable(elBufferReport);
            }
            else {
                this.generatePrintableDataTable(elBufferReport);
            }
            elPrintTitleContent.innerHTML = sTempPrintTitle;
            var tempBufferReport = elBufferReport.innerHTML;
            elBufferReport.innerHTML = sBufferTemplateContent;
            this.setReportContent(elBufferReport, sReportName, tempBufferReport);
            var sReportBody = this.getPrintReportHeader() +
            elBufferReport.innerHTML +
            this.getPrintReportFooter();
            var sOptions = this.getPrintReportPopupOptions();
            var aStyleSheets = this.getPrintReportStyleSheets();
            var aScripts = this.getPrintReportScripts();
            $U.openPrintPreviewPopup("Report Print Preview", sReportBody, aStyleSheets, aScripts, sOptions);
            $U.removeChildNodes(elBufferReport);
        }
    });
    $L.augmentObject($W.VehicleHistory.DataTable.prototype, {
        /**
         * initialises the datatable
         * @param {Object} el
         * @param {Object} config
         */
        initDataTable: function(el, config){
            $W.VehicleHistory.DataTable.superclass.constructor.call(this, el, config);
        },
        /**
         * Fetches the current set of records for the print preview
         */
        getCurrentRecords: function(){
            var aResultRecords = [];
            var aRecords = this.getRecordSet().getRecords();
            for (var i = 0; i < aRecords.length; i++) {
                aResultRecords.push(aRecords[i].getData());
            }
            return aResultRecords;
        }
    });
})();