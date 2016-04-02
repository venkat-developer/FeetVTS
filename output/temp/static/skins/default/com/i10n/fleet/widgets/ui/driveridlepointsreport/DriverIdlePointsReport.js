(function() {
	var $L = YAHOO.lang;
	var $E = YAHOO.util.Event;
	var $D = YAHOO.util.Dom;
	var $YW = YAHOO.widget;
	var $YU = YAHOO.util;
	var $W = getPackageForName("com.i10n.fleet.widget.ui");
	var $U = getPackageForName("com.i10n.fleet.Utils");
	var buttondata=null;  
	var vehMapReport=null;
	var driverName=null;
	var driverID=null;
	var timeFrame=null;
	var dataReturned=null;
	/**
	 * DriverIdlePointsReport, a widget in vehicle report page,
	 * extends VehicleReport.ReportItem widget
	 *
	 * @author irk
	 */
	$W.DriverIdlePointsReport = function(el, oArgs) {

		$W.DriverIdlePointsReport.superclass.constructor.call(this, el, oArgs);
		this.init();
	};
	$L.extend($W.DriverIdlePointsReport, $W.GraphReport, {
		/**
		 * Initialize widget-specific items
		 */

		init: function() {
		this.addListenersForNextAndPrevious();
		this.addExportButtonListener();
		/**
		 * Event handler for 'DONE' button in the SimpleDialog
		 */
		$W.Buttons.addDefaultHandler($D.getElementsByClassName("button-done", null, this.elBase), function(oArgs) {
			var elErrDialog = $D.getElementsByClassName("simpledialog", null, this.elBase)[0];
			if (!$D.hasClass(elErrDialog, "disabled")) {
				$D.addClass(elErrDialog, "disabled");
			}
		}, null, this);
	},
	//Listener for address fetch button.
		addListenersForAddressFetch :  function(){
			/*
			 * show a button with lat longs on it
			 */
			YAHOO.util.Event.addListener($D.getElementsByClassName("yui-dt-button"), "click",function(oArgs){
				var target = oArgs.target,
						record = this._oDataTable.getRecord(target),
						column = this._oDataTable.getColumn(target);
				this.oLocationCallback.scope= this;
				this.oSaveCallback.scope= this;
				if(column._nKeyIndex === 3 && JSON.parse(JSON.stringify(record))._oData.location.indexOf(":") != -1){
					var URL = null;
					var latlongs=record._oData.location.split(":");
					if (latlongs.length !=0) {
						URL = "/fleet/view/reports/?module=/blocks/json&data=view&report=ondemandlocationfetch";
						URL += "&lat=" +latlongs[0];
						URL += "&long=" + latlongs[1];
						URL += "&column=" + 3;
						URL += "&row=" +this._oDataTable.getRecordIndex(record);
					}
					$U.showLayerOverlay();
					$U.Connect.asyncRequest("POST",URL, this.oLocationCallback, null);
				}else {
					buttondata=this._oDataTable.getRecord(oArgs.target);
					if(column._nKeyIndex == 4){
						var URL = null;
						var selectedVehicle = this.get($W.BaseReport.ATTR_SELECTED_ITEM);
						var selectedTimeFrame = this.get($W.BaseReport.ATTR_SELECTED_TIMEFRAME);
						if (selectedTimeFrame && selectedVehicle) {
							URL = "/fleet/view/reports/?module=/blocks/json&data=view&report=drivermapreport";
							URL += "&localTime=" +$U.getLocalTime();
							URL += "&vehicleID=" + selectedVehicle;
							URL += "&startdate=" + selectedTimeFrame.startDate;
							URL += "&enddate=" + selectedTimeFrame.endDate;
						}
						if (this._widgets.items) {
							for (var itemKey in this._widgets.items) {
								if (itemKey != "drivermapreport") {
									this._widgets.items[itemKey].hide();
								}
							}
							this._widgets.items["drivermapreport"].show();
						}
						this.set($W.VehicleReport.ATTR_CURRENT_REPORT, "drivermapreport");
						$U.Connect.asyncRequest("POST",URL, this.oSaveCallback, null);
					}
				}
			},null,this);
		},
	//Listener for export report button which checks for selected report format and generates the report and asks user to download.
	addExportButtonListener :  function(){
		this.exportButtonCallBack.scope = this;
		YAHOO.util.Event.addListener($D.getElementsByClassName("exportdriveridlepoints"), "click",function(oArgs){
			var timeFrame= this.get($W.BaseReport.ATTR_SELECTED_TIMEFRAME);
			var pdf = document.getElementById("driveridlepdf").checked;
			var excel = document.getElementById("driveridleexcel").checked;
			if(pdf || excel){
				if(this._oDataTable.getRecordSet().getRecords().length > 0){
					if(pdf && timeFrame ){
						var URL="/fleet/view/reports/?module=/blocks/json&data=view&report=pdfgeneration";
						URL += "&reporttype=driveridlepoints" ;
						URL += "&reportformat=pdf" ;
						URL += "&vehicleName=" + driverName;
						URL += "&vehicleID=" + driverID;
						URL += "&startdate=" + timeFrame.startDate;
						URL += "&enddate=" + timeFrame.endDate;
						URL += "&localTime=" +$U.getLocalTime();
						$U.showLayerOverlay();
						YAHOO.util.Connect.asyncRequest('GET',URL,this.exportButtonCallBack,null);
					}else if(excel && timeFrame ){
						var URL="/fleet/view/reports/?module=/blocks/json&data=view&report=pdfgeneration";
						URL += "&reporttype=driveridlepoints" ;
						URL += "&reportformat=excel" ;
						URL += "&vehicleName=" + driverName;
						URL += "&vehicleID=" + driverID;
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
	//Listeners for next and previous buttons showed in place of pagination buttons and respective callback methods are called.
	addListenersForNextAndPrevious :  function(){
		this.oCallBack.nextButtonUpdate.scope = this;
		YAHOO.util.Event.addListener($D.getElementsByClassName("driveridlepointsnext"), "click",function(oArgs){
			var URL = null;
			var timeFrame= this.get($W.BaseReport.ATTR_SELECTED_TIMEFRAME);
			var aRecords = this._oDataTable.getRecordSet().getRecords();
			if(aRecords.length > 0){
				var dateString = aRecords[(aRecords.length)-1].getData().enddate;
				var myDate = new Date(dateString);
				var final_date = (myDate.getMonth()+1)+"/"+myDate.getDate()+"/"+myDate.getFullYear();
				var final_time = myDate.getHours()+":"+myDate.getMinutes()+":"+myDate.getSeconds();
				var finalstarttime=final_date+' '+final_time;
			}
			if(this._oDataTable.get('paginator').hasNextPage()){
				this._oDataTable.get('paginator').setPage(this._oDataTable.get('paginator').getCurrentPage()+1);
				this.addListenersForAddressFetch();
			}else if (vehicleID && timeFrame && !this._oDataTable.get('paginator').hasNextPage() && ((aRecords.length) % 15) == 0 && aRecords.length > 0) {
				URL = "/fleet/view/reports/?module=/blocks/json&data=view&report=idlepointsreport";
				URL += "&localTime=" +$U.getLocalTime();
				URL += "&vehicleID=" + vehicleID;
				URL += "&startdate=" + finalstarttime;
				URL += "&enddate=" + timeFrame.endDate;
				$U.showLayerOverlay();
				$U.Connect.asyncRequest('GET', URL ,this.oCallBack.nextButtonUpdate,null);
			}
		},null,this);

		YAHOO.util.Event.addListener($D.getElementsByClassName("driveridlepointsprevious"), "click",function(oArgs){
			this._oDataTable.get('paginator').setPage(this._oDataTable.get('paginator').getCurrentPage()-1);
			this.addListenersForAddressFetch();
		},null,this);
	},
	oCallBack : {
		getDriverName : {
		success : function(o){
		var oResponse = JSON.parse(o.responseText);
		driverName = oResponse.driver.name ;
		$D.getElementsByClassName("drivername", null, this.elBase)[0].innerHTML = driverName;
	}, 
	failure : function(o){
		$D.getElementsByClassName("drivername", null, this.elBase)[0].innerHTML = this.get($W.BaseReport.ATTR_SELECTED_ITEM);
	}

	},
	nextButtonUpdate:{
		success : function(o){
		var aResultRecords = [];
		var data=JSON.parse(o.responseText);
		var previousRecords=this._oDataTable.getRecordSet().getRecords();
		var selectedVehicle = this.get($W.BaseReport.ATTR_SELECTED_ITEM);
		var currentRecords=data[selectedVehicle];
		for (var i = 0; i < previousRecords.length; i++) {
			aResultRecords.push(JSON.stringify(previousRecords[i]._oData));
		}
		for (var i = 0; i < currentRecords.length; i++) {
			aResultRecords.push(JSON.stringify(currentRecords[i]));
		}
		var totalRecords='{"'+selectedVehicle+'":['+aResultRecords+']}';
		this.set($W.BaseReport.ATTR_DATA, JSON.parse(totalRecords));
		$U.hideLayerOverlay();
		this._oDataTable.get('paginator').setPage(this._oDataTable.get('paginator').getCurrentPage()+1);
		this.addListenersForAddressFetch();
	}, 
	failure : function(o){
		$U.alert({message: "No more records found for the selcted vehicle between the selected timeframe"});
	}
	}
	},
	/**
	 * DataTable instance of this widget
	 */
	_oDataTable: null,
	/**
	 * Over-rided function that renders the widget based on data.
	 */
	render: function() {
		var elErrDialog = $D.getElementsByClassName("simpledialog", null, this.elBase)[0];
		var widgetEl = $D.getElementsByClassName("report-cnt", null, this.elBase)[0];
		if (this._oDataTable === null) {
			var config = this.constructConfig();
			this._oDataTable = new $W.DriverIdlePointsReport.DataTable(this.elBase, config);

			vehMapReport=new $W.DriverMapReport(this.elBase, config);

			config = null;
		}
		this._oDataTable.get('paginator').setAttributeConfig('template', {value : "{CurrentPageReport}&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;Page {PageLinks} {PageCount} "});
		this._oDataTable.get('paginator').setAttributeConfig('pageReportTemplate', {value : "Showing {recordsCount} of {totalRecords}"});
		this._oDataTable.get('paginator').setAttributeConfig('pageLinks', {value : 1});
		this._oDataTable.get('paginator').setAttributeConfig('rowsPerPage', {value : 15});
		var selectedVehicle = driverID = this.get($W.BaseReport.ATTR_SELECTED_ITEM);
		this.oCallBack.getDriverName.scope = this;
		$U.Connect.asyncRequest('GET', "/fleet/view/reports/?module=/blocks/json&data=view&report=driveridlepointsreport"+
				"&getdrivername=true&driverID="+selectedVehicle, this.oCallBack.getDriverName,null);
		$D.getElementsByClassName("drivername", null, this.elBase)[0].innerHTML =driverName ;
		if (selectedVehicle) {
			$D.removeClass(widgetEl, "empty");
			var oData = this.get($W.BaseReport.ATTR_DATA);
			if (!$D.hasClass(elErrDialog, "disabled")) {
				$D.addClass(elErrDialog, "disabled");
			}
			var report = $D.getElementsByClassName("report", null, this.elBase)[0];
			if ($D.hasClass(report, "disabled")) {
				$D.removeClass(report, "disabled");
			}
			this._oDataTable.update(this);
			/*
			 * show on map button for showing the idle point data on the map
			 */
//			YAHOO.util.Event.addListener($D.getElementsByClassName("yui-dt-button"), "click",function(oArgs){  
//				buttondata=this._oDataTable.getRecord(oArgs.target);
//
//				var URL = null;
//				var selectedVehicle = this.get($W.BaseReport.ATTR_SELECTED_ITEM);
//				var selectedTimeFrame = this.get($W.BaseReport.ATTR_SELECTED_TIMEFRAME);
//				if (selectedTimeFrame && selectedVehicle) {
//					URL = "/fleet/view/reports/?module=/blocks/json&data=view&report=drivermapreport";
//					URL += "&localTime=" +$U.getLocalTime();
//					URL += "&driverID=" + selectedVehicle;
//					URL += "&startdate=" + selectedTimeFrame.startDate;
//					URL += "&enddate=" + selectedTimeFrame.endDate;
//				}
//				if (this._widgets.items) {
//					for (var itemKey in this._widgets.items) {
//						if (itemKey != "drivermapreport") {
//							this._widgets.items[itemKey].hide();
//						}
//					}
//					this._widgets.items["drivermapreport"].show();
//				}
//				this.set($W.DriverReport.ATTR_CURRENT_REPORT, "drivermapreport");
//				$U.Connect.asyncRequest("POST",URL, this.oSaveCallback, null);
//			},null,this);
		}
//		this.fireEvent($W.BaseReport.EVT_ON_RENDER_FINISHED);
		this.addListenersForAddressFetch();
	},
	oSaveCallback: {
		success: function(o) {
		var oData = JSON.parse(o.responseText);
		vehMapReport.showOnMapInfoWindow(buttondata._oData.lat,buttondata._oData.lon,buttondata._oData.startdate,buttondata._oData.enddate,buttondata._oData.location,buttondata._oData.time);
	},
	failure: function(o) {
	}
	},
	oLocationCallback: {
		success: function(o) {
		$U.hideLayerOverlay();
		var oData = JSON.parse(o.responseText);
		var records=this._oDataTable.getRecordSet().getRecords();
		var rownumber=oData.row;
		var columnnumber=oData.column;
		if(columnnumber==3){
			records[rownumber]._oData.location =oData.address;
		}
		this._oDataTable.set('source', records);
		this._oDataTable.render();
		this.addListenersForAddressFetch();
	},
	failure: function(o) {
	}
	},

	/**
	 * DataSource function for the widget.
	 * @param {Object} reportItem
	 */
	getData: function(reportItem) {
		var aResults = [];
		var selectedVehicle;
		var data = dataReturned=reportItem.get($W.BaseReport.ATTR_DATA);
		if ($L.isObject(data)) {
			selectedVehicle = reportItem.get($W.BaseReport.ATTR_SELECTED_ITEM);
			aResults = data[selectedVehicle];
		}
		return aResults;
	},
	/**
	 * Returns a url to update data.
	 */
	getDataURL: function() {
		var URL = null;
		var selectedVehicle = this.get($W.BaseReport.ATTR_SELECTED_ITEM);
		var selectedTimeFrame = timeFrame = this.get($W.BaseReport.ATTR_SELECTED_TIMEFRAME);
		if (selectedTimeFrame && selectedVehicle) {
			URL = "/fleet/view/reports/?module=/blocks/json&data=view&report=driveridlepointsreport";
			URL += "&localTime=" +$U.getLocalTime();
			URL += "&driverID=" + selectedVehicle;
			URL += "&startdate=" + selectedTimeFrame.startDate;
			URL += "&enddate=" + selectedTimeFrame.endDate;
		}
		return URL;
	},
	/**
	 * Destroying current UI to make way for a new one.
	 */
	destroy: function() {
		/**
		 * Datatable content alone needs to be changed/deleted.
		 * Will be taken care during datatable's onDataReturnInitializeTable function call
		 */
	},
	/**
	 * Constructs the config to be used by the datatable in DriverIdlePointsReport
	 */
	constructConfig: function() {
		var config = {};
		var datasource = new $YU.FunctionDataSource(this.getData);
		datasource.responseType = $YU.DataSource.TYPE_JSARRAY;
		datasource.responseSchema = {
				fields: ["startdate", "enddate", "time", "location","lat","lon"]
		};
		var columndefs = [{
			key: "startdate",
			label: "Start Time",
			sortable: true,
			resizeable: false,
			formatter: $W.Report.FORMATTERS.STRING_DATE_FORMATTER
		}, {
			key: "enddate",
			label: "End Time",
			sortable: true,
			resizeable: false,
			formatter: $W.Report.FORMATTERS.STRING_DATE_FORMATTER
		}, {
			key: "time",
			label: "Idle Time",
			sortable: true,
			resizeable: false,
			parser: "number"
		}, {
			key: "location",
			label: "Location",
			sortable: true,
			resizeable: false,
			formatter:"button"
		},{   
			key:"button", 
			label:"Show on Map", 
			formatter:"button"}];
		config.datasource = datasource;
		config.columndefs = columndefs;
		datasource = null;
		columndefs = null;
		config.reportconfig = {
				initialLoad: false
		};
		config.options = {
				"print": {
			"enabled": true
		}
		};
		return config;
	},
	/**
	 * Show this widget
	 */
	show: function() {
		$W.DriverReport.superclass.show.call(this);
	}
	});
	/**
	 * DataTable class that extends Report widget
	 * @param {Object} el
	 * @param {Object} params
	 */
	$W.DriverIdlePointsReport.DataTable = function(el, params) {
		this.initDataTable(el, params);
		this.initParams = params;
	};
	$L.extend($W.DriverIdlePointsReport.DataTable, $W.Report, {
		/**
		 * Over-rided function to generate the printable report
		 * @param {Object} elBufferReport
		 */
		generatePrintableDataTable: function(elBufferReport) {
		var oBufferConfig = $U.cloneObject(this._oReportConfig);
		oBufferConfig.paginator = null;
		var bufferDataTable = new $YW.DataTable(elBufferReport, this._oColumnDefs, this._oReportDataSource, oBufferConfig);
		bufferDataTable.getRecordSet().deleteRecords(0);
		bufferDataTable.getRecordSet().addRecords(this.getCurrentRecords());
		bufferDataTable.render();
	},
	printReport: function(){
		var sBufferTemplateContent = null;
		var sReportName = "report";
		var elBufferReport = $D.getElementsByClassName("print-section", null, this.reportElement)[0];
		var elPrintContent = $D.getElementsByClassName("print-skin-template", null, this.baseElement)[0];
		var elPrintTitleContent = $D.getElementsByClassName("title", null, elPrintContent)[0];
		var sTempPrintTitle = null;
		if (this.initParams) {
			sTempPrintTitle = elPrintTitleContent.innerHTML;
			elPrintTitleContent.innerHTML = driverName + " 's  " + elPrintTitleContent.innerHTML;
			// elPrintTitleContent.innerHTML = this.initParams.selectedVehicle.toUpperCase() + "  " + elPrintTitleContent.innerHTML;
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
	$L.augmentObject($W.DriverIdlePointsReport.DataTable.prototype, {
		/**
		 * Initialize the datatable
		 * @param {Object} el
		 * @param {Object} config
		 */
		initDataTable: function(el, config) {
		$W.DriverIdlePointsReport.DataTable.superclass.constructor.call(this, el, config);
	},
	/**
	 * Returns the current records available in the datatable,
	 * Used as input to the print preview datatable
	 */
	getCurrentRecords: function() {
		var aResultRecords = [];
		var aRecords = this.getRecordSet().getRecords();
		for (var i = 0; i < aRecords.length; i++) {
			aResultRecords.push(aRecords[i].getData());
		}
		return aResultRecords;
	},
	/**
	 * Update the datatable
	 * @param {Object} args
	 */
	update: function(args) {
		var oCallback = {
				success: this.onDataReturnInitializeTable,
				failure: this.onDataReturnInitializeTable,
				scope: this,
				argument: this.getState()
		};
		this._oReportDataSource.sendRequest(args, oCallback);


	}
	});
})();
