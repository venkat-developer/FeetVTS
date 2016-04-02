(function() {
	var $L = YAHOO.lang;
	var $E = YAHOO.util.Event;
	var $D = YAHOO.util.Dom;
	var $YU = YAHOO.util;
	var $YW = YAHOO.widget;
	var $U = getPackageForName("com.i10n.fleet.Utils");
	var $W = getPackageForName("com.i10n.fleet.widget.ui");
	var $V = getPackageForName("com.i10n.fleet.widget.view");
	var checked;
	var message;
	var oReportData;
	var oReport={};
	var oRec={};
	var oRecordData={};
	var num=0;
	var osetRecord={};
	var oIdle={};
	var oOverspeed={};
	var oBunching={};
	var oChargerConnected={};
	var oChargerDisConnected={};
	var oAllReport={};
	var vehicleName=null;
	var dataReturned=null;
	var myVar;
	var oldRecordLength=0;
	var el1;
	/**
	 * Violation Report Sub Nav Page.
	 * TODO : This widget initializes all the report during load. This will be changed to onload.
	 * @author sabarish
	 */
	$W.ViolationReport = function(el, oArgs) {
		this._oTabView = new $YW.TabView($D.getElementsByClassName("yui-navset", null, el)[0]);
		var AlertPopUp = new $W.ViolationReport.TripCreationPopUp($D.getElementsByClassName("comments")[0],oArgs);
		el1=el;
		this._widgets.TripCreationPopUp = AlertPopUp;

		if (!oArgs) {
			oArgs = {};
		}
		this.addListenersForNextAndPrevious();
		this.addListenersForAddressFetch();
		this.addExportButtonListener();
		oArgs.timeframe = true;
		oArgs.dataType = $W.BaseReport.KEY_TYPE_JSON;
		$W.ViolationReport.superclass.constructor.call(this, el, oArgs);

	};
	$L.augmentObject($W.ViolationReport, {
		FORMATTERS: {
		TIME_FORMATTER: function(elCell, oRecord, oColumn, oData) {
		if (oData) {
			elCell.innerHTML = oData;                    
		}
	}
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

	$L.augmentObject($W.ViolationReport, {

		PAGE_ID: "violationreport",
		/**
		 * Config Data for Reports.
		 */
		CONFIG_REPORTS: {

		"alertstatus": {
		columndefs: [{
			key: "refid",
			hidden: true                   
		},  {
			key: "index",
			label: "No.",
			sortable: true
		}, {
			key: "vehiclename",
			label: "Vehicle Name",
			sortable: true,
			sortOptions: {
			defaultDir: $YW.DataTable.CLASS_DESC
		},
		resizeable: false
		}, {
			key: "drivername",
			label: "Driver Name",
			sortable: true,
			resizeable: false
		}, {
			key: "time",
			label: "AlertTime",
			sortable: true,
			resizeable: false
		}, {
			key: "alertlocation",
			label: "AlertLocation",
			formatter:"button",
			sortable: true,
			resizeable: false
		}, {
			key: "alerttype",
			label: "AlertType",
			sortable: true,
			resizeable: false
		},{
			key: "alerttypevalue",
			label: "AlertValue",
			sortable: true,
			resizeable: false
		}],
		"datasource": {
		responseType: $YU.DataSource.TYPE_JSARRAY,
		responseSchema: {
		elementField: "violations",
		fields: ["refid","index", "vehiclename", "drivername", "time", "alertlocation","alerttypevalue",{
			key: "alerttype",
			//parser: "number"
		}]
	}
	},
	options: {
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
	},


	reportconfig: {}
	}
	}

	});
	$L.extend($W.ViolationReport, $W.BaseReport, {
		/**
		 * Datatable representing this widget
		 */
		_oDataTable: null,
		oSaveCallback: {
		success: function(o) {
	},
	failure: function(o) {
	}
	},
	//Listeners for next and previous buttons showed in place of pagination buttons and respective callback methods are called
	addListenersForAddressFetch :  function(){
		YAHOO.util.Event.addListener($D.getElementsByClassName("yui-dt-button"), "click",function(oArgs){
			var target = oArgs.target,
					record = this._oDataTable.getRecord(target),
					column = this._oDataTable.getColumn(target);
			this.oLocationCallback.scope= this;
			if(JSON.parse(JSON.stringify(record))._oData.alertlocation.indexOf(":") != -1){
				var URL = null;
				var latlongs=record._oData.alertlocation.split(":");
				if (latlongs.length !=0) {
					URL = "/fleet/view/reports/?module=/blocks/json&data=view&report=ondemandlocationfetch";
					URL += "&lat=" +latlongs[0];
					URL += "&long=" + latlongs[1];
					URL += "&column=" + 4;
					URL += "&row=" +this._oDataTable.getRecordIndex(record);
				}
				$U.showLayerOverlay();
				$U.Connect.asyncRequest("POST",URL, this.oLocationCallback, null);
			}
		},null,this);
	},
	oLocationCallback: {
		success: function(o) {
		$U.hideLayerOverlay();
		var oData = JSON.parse(o.responseText);
		var records=this._oDataTable.getRecordSet().getRecords();
		var rownumber=oData.row;
		records[rownumber]._oData.alertlocation =oData.address;
		this._oDataTable.set('source', records);
		this._oDataTable.render();
		this.addListenersForAddressFetch();
	},
	failure: function(o) {
	}
	},	
	//Listener for export report button which checks for selected report format and generates the report and asks user to download
		addExportButtonListener :  function(){
			this.exportButtonCallBack.scope = this;
			YAHOO.util.Event.addListener($D.getElementsByClassName("exportviolationreport"), "click",function(oArgs){
				var timeFrame= this.get($W.BaseReport.ATTR_SELECTED_TIMEFRAME);
				var pdf = document.getElementById("violationpdf").checked;
				var excel = document.getElementById("violationexcel").checked;
				if(pdf || excel){
					if(this._oDataTable.getRecordSet().getRecords().length > 0){
						if(pdf && timeFrame ){
							var URL="/fleet/view/reports/?module=/blocks/json&data=view&report=pdfgeneration";
							URL += "&reporttype=violation" ;
							URL += "&reportformat=pdf" ;
							URL += "&startdate=" + timeFrame.startDate;
							URL += "&enddate=" + timeFrame.endDate;
							URL += "&localTime=" +$U.getLocalTime();
							$U.showLayerOverlay();
							YAHOO.util.Connect.asyncRequest('GET',URL,this.exportButtonCallBack,null);
						}else if(excel && timeFrame ){
							var URL="/fleet/view/reports/?module=/blocks/json&data=view&report=pdfgeneration";
							URL += "&reporttype=violation" ;
							URL += "&reportformat=excel" ;
							URL += "&startdate=" + timeFrame.startDate;
							URL += "&enddate=" + timeFrame.endDate;
							URL += "&localTime=" +$U.getLocalTime();
							$U.showLayerOverlay();
							YAHOO.util.Connect.asyncRequest('GET',URL,this.exportButtonCallBack,null);
						}else{
							$U.alert({message: "No data available for the vehicle between selected time interval !"});
						}
					}else{
						$U.alert({message: "No data available for the vehicle between selected time interval !"});  
					}
				}else{
					$U.alert({message: "please select one of the formats specified below to download the report"});
				}

			},null,this);
		},
		exportButtonCallBack:{
			success: function(o){
			var oResponse = JSON.parse(o.responseText);
			var url = oResponse.filepath ;
			$U.hideLayerOverlay();
			window.open(url);
		},      
		failure: function(o){
			$U.alert({message: "There is no file created for download !"});
		}
		},
	//Listeners for next and previous buttons showed in place of pagination buttons and respective callback methods are called
	addListenersForNextAndPrevious :  function(){
		this.nextButtonUpdate.scope = this;
		YAHOO.util.Event.addListener($D.getElementsByClassName("violationnext"), "click",function(oArgs){
			var URL = null;
			var timeFrame= this.get($W.BaseReport.ATTR_SELECTED_TIMEFRAME);
			var aRecords = this._oDataTable.getRecordSet().getRecords();
			if(aRecords.length > 0){
				var dateString = aRecords[(aRecords.length)-1].getData().time;
				var myDate = new Date(dateString);
				var final_date = (myDate.getMonth()+1)+"/"+myDate.getDate()+"/"+myDate.getFullYear();
				var final_time = myDate.getHours()+":"+myDate.getMinutes()+":"+(myDate.getSeconds());
				var finalstarttime=final_date+' '+final_time;
			}
			if(this._oDataTable.get('paginator').hasNextPage()){
				this._oDataTable.get('paginator').setPage(this._oDataTable.get('paginator').getCurrentPage()+1);
				this.addListenersForAddressFetch();
			}else if (timeFrame && !this._oDataTable.get('paginator').hasNextPage() && ((aRecords.length) % 15) == 0 && aRecords.length > 0) {
				URL = "/fleet/view/reports/?module=/blocks/json&data=view&report=violationreport";
				URL += "&localTime=" +$U.getLocalTime();
				URL += "&startdate=" + timeFrame.startDate;
				URL += "&enddate=" + finalstarttime;
				$U.showLayerOverlay();
				$U.Connect.asyncRequest('GET', URL ,this.nextButtonUpdate,null);
			}
		},null,this);

		YAHOO.util.Event.addListener($D.getElementsByClassName("violationprevious"), "click",function(oArgs){
			this._oDataTable.get('paginator').setPage(this._oDataTable.get('paginator').getCurrentPage()-1);
			this.addListenersForAddressFetch();
		},null,this);
	},
	nextButtonUpdate:{
		success : function(o){
		var aResultRecords = [];
		var data=JSON.parse(o.responseText);
		var previousRecords=this._oDataTable.getRecordSet().getRecords();
		var selectedVehicle = this.get($W.BaseReport.ATTR_SELECTED_ITEM);
		var currentRecords=data.alertstatus.nogroup;
		for (var i = 0; i < previousRecords.length; i++) {
			aResultRecords.push(JSON.stringify(previousRecords[i]._oData));
		}
		if(currentRecords){
			if(currentRecords.violations){
				for (var i = 0; i < currentRecords.violations.length; i++) {
					aResultRecords.push(JSON.stringify(currentRecords.violations[i]));
				}
			}
		}
		var totalRecords='{"vehiclestatus":'+JSON.stringify(data.vehiclestatus)+',"alertstatus":{"nogroup": { "violations":['+aResultRecords+']}}}';
		this.set($W.BaseReport.ATTR_DATA, JSON.parse(totalRecords));
		$U.hideLayerOverlay();
		this._oDataTable.get('paginator').setPage(this._oDataTable.get('paginator').getCurrentPage()+1);
		this.addListenersForAddressFetch();
	}, 
	failure : function(o){
		$U.alert({message: "No more records found for the vehicles between the selected timeframe"});
	}
	},
	render: function() {
//		if (this._isDataTableInitialized) {
//			var oData = dataReturned=this.get($W.BaseReport.ATTR_DATA);
//			if (oData) {
//				for (var sReportID in oData) {
//					if (this._oGPDataSource[sReportID]) {
//						this._oGPDataSource[sReportID].setGroupData(oData[sReportID]);
//					}
//				}
//			}
//		} else {
			if(this._oDataTable !=null){
				var currentpage=this._oDataTable.get('paginator').getCurrentPage();
			}
			var oConfig = this._getConfig("alertstatus");
			this._oGPDataSource = oConfig.datasource;
			this._oDataTable = new $W.GroupedReport(this.elBase, oConfig);
			this._oDataTable.get('paginator').setAttributeConfig('template', {value : "{CurrentPageReport}&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;Page {PageLinks} {PageCount} "});
			this._oDataTable.get('paginator').setAttributeConfig('pageReportTemplate', {value : "Showing {recordsCount} of {totalRecords}"});
			this._oDataTable.get('paginator').setAttributeConfig('pageLinks', {value : 1});
			this._oDataTable.get('paginator').setAttributeConfig('rowsPerPage', {value : 15});
			this._oDataTable.get('paginator').setPage(currentpage+1);
			//			this.initReports(this.elBase);
			//			this._isDataTableInitialized=true;
//		}
		this.addListenersForAddressFetch();
	},

	onButtonClick: function(){
	},

	onSubmit:function(){
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

	getDataURL: function() {
		var sResult = null;
		var oTimeFrame =timeFrame= this.get($W.BaseReport.ATTR_SELECTED_TIMEFRAME);
		if (oTimeFrame) {
			if (oTimeFrame.startDate && oTimeFrame.endDate) {
				sResult = "/fleet/view/reports/?module=/blocks/json&data=view&report=violationreport&localTime="+$U.getLocalTime()+"&startdate=" + oTimeFrame.startDate + "&enddate=" + oTimeFrame.endDate;
			} else {
				sResult = "/fleet/view/reports/?module=/blocks/json&data=view&report=violationreport&localTime="+$U.getLocalTime();
			}
		}
		return sResult;
	},

	getPageID: function() {
		return $W.ViolationReport.PAGE_ID;
	}
	});
	$L.augmentObject($W.ViolationReport.prototype, {
		/**
		 * Object Map of widgets initialized by the this widget
		 */
		_widgets: {},
		_isDataTableInitialized: false,
		/**
		 * Contains reports initialized
		 */
		_reports: {},
		_oGPDataSource: {},


		/**
		 * initializes the reports
		 * @param {Object} el
		 */
		initReports: function(el) {
			this._createReport("vehiclestatus",el);
			this._createReport("alertstatus",el);
			this._isDataTableInitialized = true;
			this.dataFilter("vehiclestatus",el);
			this.dataFilter("alertstatus",el);
		},

		dataFilter:function(sReportID, el){

			var elReport = $D.getElementsByClassName(sReportID + "-rpt", null, el)[0];
			if (elReport) {
				var oConfig = this._getConfig(sReportID);

				if (oConfig) {
					oReportData=new $W.GroupReportViolation(elReport, oConfig);
					oReport = oReportData;



					this._widgets.TripCreationPopUp.subscribe($W.ViolationPopUp.EVT_ON_SUBMIT,this.onSubmit , this, true);

					oIdle = new $W.GroupReportViolation(elReport, oConfig);
					oOverspeed = new $W.GroupReportViolation(elReport, oConfig);
					oBunching = new $W.GroupReportViolation(elReport, oConfig);
					oChargerConnected = new $W.GroupReportViolation(elReport, oConfig);
					oChargerDisConnected = new $W.GroupReportViolation(elReport, oConfig);
					oAllReport = new $W.GroupReportViolation(elReport, oConfig);

					/**
					 * Listener for Filtring violation types
					 * @param {Object} elReport
					 * @param {Object} oConfig
					 */ 
					var oDataset1={};


					YAHOO.util.Event.addListener($D.getElementsByClassName("button-generate-report"), "mouseup",function(oArgs){ 

						myVar=setInterval(function(){
							oReportData=new $W.GroupReportViolation(elReport, oConfig);
							oReport = oReportData;
							oRec=oReport.getRecordSet().getRecords();
							oDataset1=$U.Arrays.mapToArray(oRec);

							if(oDataset1.length!=oldRecordLength){
								var delrow=0;
								oReportData=new $W.GroupReportViolation(elReport, oConfig);
								oReport = oReportData;
								oRec=oReport.getRecordSet().getRecords();
								oDataset1=$U.Arrays.mapToArray(oRec);
								oldRecordLength=oDataset1.length;
								for(var k=(oDataset1.length-1);k>=0;k--){
									var oRow={};
									oRow=$U.Arrays.mapToArray(oDataset1[k]);

									var oColumrecord={};
									oColumrecord=$U.Arrays.mapToArray(oRow[2]);

									if(oColumrecord[0]=='OverSpeed'){

										oIdle.getRecordSet().deleteRecord(k);
										//oOverspeed.getRecordSet().deleteRecord(delrow);
										oBunching.getRecordSet().deleteRecord(k);
										oChargerConnected.getRecordSet().deleteRecord(k);
										oChargerDisConnected.getRecordSet().deleteRecord(k);
									}else if(oColumrecord[0]=='Bunching'){

										oIdle.getRecordSet().deleteRecord(k);
										oOverspeed.getRecordSet().deleteRecord(k);
										//oBunching.getRecordSet().deleteRecord(delrow);
										oChargerConnected.getRecordSet().deleteRecord(k);
										oChargerDisConnected.getRecordSet().deleteRecord(k);

									}else if(oColumrecord[0]=='Idle' ){
										//oIdle.getRecordSet().deleteRecord(delrow);
										oOverspeed.getRecordSet().deleteRecord(k);
										oBunching.getRecordSet().deleteRecord(k);
										oChargerConnected.getRecordSet().deleteRecord(k);
										oChargerDisConnected.getRecordSet().deleteRecord(k);

									}else if(oColumrecord[0]=='Charger Connected') {
										oIdle.getRecordSet().deleteRecord(k);
										oOverspeed.getRecordSet().deleteRecord(k);
										oBunching.getRecordSet().deleteRecord(k);
										//oChargerConnected.getRecordSet().deleteRecord(delrow);
										oChargerDisConnected.getRecordSet().deleteRecord(k);



									}else if(oColumrecord[0]=='Charger DisConnected') {
										oIdle.getRecordSet().deleteRecord(k);
										oOverspeed.getRecordSet().deleteRecord(k);
										oBunching.getRecordSet().deleteRecord(k);
										oChargerConnected.getRecordSet().deleteRecord(k);
										//oChargerDisConnected.getRecordSet().deleteRecord(delrow);

									}else{
										oIdle.getRecordSet().deleteRecord(k);
										oOverspeed.getRecordSet().deleteRecord(k);
										oBunching.getRecordSet().deleteRecord(k);
										oChargerConnected.getRecordSet().deleteRecord(k);
										oChargerDisConnected.getRecordSet().deleteRecord(delrow);
									}
								}
								clearInterval(myVar);

							}		

						},1000);

					},null,this);

					YAHOO.util.Event.addListener($D.getElementsByClassName("select-violation"),"change",function(){
						var index=YAHOO.util.Dom.getElementsByClassName("select-violation")[0].selectedIndex;
						var text=YAHOO.util.Dom.getElementsByClassName("select-violation")[0].options[index].value; 

						var oDataset={};

						oRec=oReport.getRecordSet().getRecords();
						oDataset=$U.Arrays.mapToArray(oRec);
						for(var k=(oDataset.length-1);k>=0;k--){
							oReport.getRecordSet().deleteRecord(k);

						}
						oReport.refreshView();




						oRec=oReport.getRecordSet().getRecords();
						oDataset=$U.Arrays.mapToArray(oRec);

						if(text=="Overspeed"){
							osetRecord = oOverspeed;
							oRec=osetRecord.getRecordSet().getRecords();
							oDataset=$U.Arrays.mapToArray(oRec);
						}else if(text=="Bunching"){
							osetRecord = oBunching;
							oRec=osetRecord.getRecordSet().getRecords();
							oDataset=$U.Arrays.mapToArray(oRec);
						}else if(text=="Idle"){
							osetRecord = oIdle;
							oRec=osetRecord.getRecordSet().getRecords();
							oDataset=$U.Arrays.mapToArray(oRec);
						}else if(text=="CCtrue") {
							osetRecord = oChargerConnected;
							oRec=osetRecord.getRecordSet().getRecords();
							oDataset=$U.Arrays.mapToArray(oRec);
						}else if(text=="CCfalse") {
							osetRecord = oChargerDisConnected;
							oRec=osetRecord.getRecordSet().getRecords();
							oDataset=$U.Arrays.mapToArray(oRec);

						}else if(text=="All") { 
							osetRecord = oAllReport;
							oRec=osetRecord.getRecordSet().getRecords();
							oDataset=$U.Arrays.mapToArray(oRec);
						}
						for(i=0;i<oDataset.length;i++){
							var oData={};
							oData=$U.Arrays.mapToArray(oDataset[i]);
							oReport.getRecordSet().addRecords(oData[2],i);
							oReport.refreshView();
						}


					},null,this); 

					YAHOO.util.Event.addListener($D.getElementsByClassName("select-overspeedalertvalue"),"change",function(){
						var index=YAHOO.util.Dom.getElementsByClassName("select-overspeedalertvalue")[0].selectedIndex;
						var text=YAHOO.util.Dom.getElementsByClassName("select-overspeedalertvalue")[0].options[index].value; 
						var oDataset={};
						var oOverspeedRecord={};
						var flag1=0;

						oRec=oReport.getRecordSet().getRecords();
						oDataset=$U.Arrays.mapToArray(oRec);
						for(var k=(oDataset.length-1);k>=0;k--){
							oReport.getRecordSet().deleteRecord(k);
						}
						oReport.refreshView();

						oOverspeedRecord = oOverspeed;
						oRec=oOverspeedRecord.getRecordSet().getRecords();
						oDataset=$U.Arrays.mapToArray(oRec);
						for(var k=0;k<oDataset.length;k++){
							var oRow={};
							oRow=$U.Arrays.mapToArray(oDataset[k]);
							var oColumrecord={};
							oColumrecord=$U.Arrays.mapToArray(oRow[2]);
							if((parseInt(oColumrecord[1])<parseInt(text) && parseInt(oColumrecord[1])>parseInt((text-10))) || parseInt(oColumrecord[1])>60 && parseInt(text)==60){
								var oData={};
								oData=$U.Arrays.mapToArray(oDataset[k]);
								oReport.getRecordSet().addRecords(oData[2],flag1);
								flag1++;
								oReport.refreshView();
							}

							if(parseInt(text)==0){
								var oData={};
								oData=$U.Arrays.mapToArray(oDataset[k]);
								oReport.getRecordSet().addRecords(oData[2],flag1);
								flag1++;
								oReport.refreshView();
							}
						}
						oRec=oReport.getRecordSet().getRecords();
						oDataset=$U.Arrays.mapToArray(oRec);

					},null,this); 

					YAHOO.util.Event.addListener($D.getElementsByClassName("select-idlealertvalue"),"change",function(){
						var index=YAHOO.util.Dom.getElementsByClassName("select-idlealertvalue")[0].selectedIndex;
						var text=YAHOO.util.Dom.getElementsByClassName("select-idlealertvalue")[0].options[index].value; 
						var oDataset={};
						var oIdleRecord={};
						var flag1=0;

						oRec=oReport.getRecordSet().getRecords();
						oDataset=$U.Arrays.mapToArray(oRec);
						for(var k=(oDataset.length-1);k>=0;k--){
							oReport.getRecordSet().deleteRecord(k);
						}
						oReport.refreshView();

						oIdleRecord = oIdle;
						oRec=oIdleRecord.getRecordSet().getRecords();
						oDataset=$U.Arrays.mapToArray(oRec);
						for(var k=0;k<oDataset.length;k++){
							var oRow={};
							oRow=$U.Arrays.mapToArray(oDataset[k]);
							var oColumrecord={};
							oColumrecord=$U.Arrays.mapToArray(oRow[2]);
							if((parseInt(oColumrecord[1])<parseInt(text) && parseInt(oColumrecord[1])>parseInt((text-10))) || parseInt(oColumrecord[1])>70 && parseInt(text)==70){
								var oData={};
								oData=$U.Arrays.mapToArray(oDataset[k]);
								oReport.getRecordSet().addRecords(oData[2],flag1);
								flag1++;
								oReport.refreshView();
							}

							if(parseInt(text)==0){
								var oData={};
								oData=$U.Arrays.mapToArray(oDataset[k]);
								oReport.getRecordSet().addRecords(oData[2],flag1);
								flag1++;
								oReport.refreshView();
							}
						}
						oRec=oReport.getRecordSet().getRecords();
						oDataset=$U.Arrays.mapToArray(oRec);

					},null,this); 


					oReport.sortColumn(oReport.getColumn("startdate"), $YW.DataTable.CLASS_DESC);
					this._reports[sReportID] = oReport;
				}
			}
		},
		/**
		 * A utility function to initializes the report specified by the reportid
		 * @param {Object} sReportID
		 * @param {Object} el
		 */
		_createReport: function(sReportID, el) {
		},


		/**
		 * Creates the config required for the report.
		 * @param {Object} sReportID
		 */
		_getConfig: function(sReportID) {
			var oTemplateConfig = $W.ViolationReport.CONFIG_REPORTS[sReportID];
			var oConfig = null;
			var oData = dataReturned=this.get($W.BaseReport.ATTR_DATA);
			if (oData && oData[sReportID]) {
				oConfig = {};
				var oDataSource = new $W.GroupReportViolation.GroupedDataSource(oData[sReportID]);

				$L.augmentObject(oDataSource, oTemplateConfig.datasource, true);
				oConfig.datasource = oDataSource;
				this._oGPDataSource[sReportID] = oDataSource;
				oConfig.columndefs = oTemplateConfig.columndefs;
				oConfig.reportconfig = oTemplateConfig.reportconfig;
				oConfig.options = oTemplateConfig.options;
			}

			return oConfig;
		}
	});
	$W.ViolationReport.TripCreationPopUp = function(el,oArgs){

		/*Initializing*/
		var recorddata=oArgs;
		this.initCreateTrip(el,oArgs);
		this.message=YAHOO.util.Dom.get('comments-id').value;
		$W.Buttons.addDefaultHandler($D.getElementsByClassName("cancel-button", null, el),this.onClose ,null,this);
	};

	$L.extend($W.ViolationReport.TripCreationPopUp, $W.ViolationPopUp);
	$L.augmentObject($W.ViolationReport.TripCreationPopUp.prototype, {
		/*Declaring the datamembers*/
		elDisplayedContent: null,

		/**
		 * The initalization Function
		 */
		initCreateTrip: function(el,oArgs){
		this.elDisplayedContent = el;

		$W.ViolationReport.TripCreationPopUp.superclass.constructor.call(this, this.elDisplayedContent, {
			fixedcenter: false,
			width: "300px",
			height: "auto"
		});
	},
	onUpdate:function() {
		var message1 =YAHOO.util.Dom.get('comments-id').value;
		$U.Connect.asyncRequest("POST","/fleet/form/reportsettings/?command_type=violation&message="+message1, this.oSaveCallback, $U.MapToParams(checked));
	},

	onClose:function() {
	},
	});
})();