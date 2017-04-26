(function(){
	var $W = getPackageForName("com.i10n.fleet.widget.ui");
	var $L = YAHOO.lang;
	var $E = YAHOO.util.Event;
	var $D = YAHOO.util.Dom;
	var $YU = YAHOO.util;
	var $YW = YAHOO.widget;
	var $U = getPackageForName("com.i10n.fleet.Utils");
	var vehicleName=null;
	var vehicleID=null;
	var timeFrame=null;
	var interval=null;
	var dataReturned=null;
	/**
	 * Activity Report Widget
	 * @author aravind
	 */
	$W.ActivityReport = function(el, oArgs){
		this.elBase = el;
		this.initActivityReport(el, oArgs);
	};
	$L.augmentObject($W.ActivityReport, {
		PAGE_ID: "activityreport"
	});
	$L.extend($W.ActivityReport, $W.BaseReport, {
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
			URL = "/fleet/view/reports/?module=/blocks/json&data=view&report=activityreport";
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
	//Listener for export report button which checks for selected report format and generates the report and asks user to download.
	addExportButtonListener :  function(){
		this.exportButtonCallBack.scope = this;
		YAHOO.util.Event.addListener($D.getElementsByClassName("exportactivityreport"), "click",function(oArgs){
			var exportvalue;
			var pdf = document.getElementById("activitypdf").checked;
			var excel = document.getElementById("activityexcel").checked;
			if(pdf || excel){
				if(this._oDataTable.getRecordSet().getRecords().length > 0){
					if(pdf && timeFrame && vehicleID ){
						var URL="/fleet/view/reports/?module=/blocks/json&data=view&report=pdfgeneration";
						URL += "&reporttype=acitivity" ;
						URL += "&reportformat=pdf" ;
						URL += "&vehicleName=" + vehicleName;
						URL += "&vehicleID=" + vehicleID;
						URL += "&startdate=" + timeFrame.startDate;
						URL += "&enddate=" + timeFrame.endDate;
						URL += "&interval=" + timeFrame.interval;
						URL += "&localTime=" +$U.getLocalTime();
						$U.showLayerOverlay();
						YAHOO.util.Connect.asyncRequest('GET',URL,this.exportButtonCallBack,null);
					}else if(excel && timeFrame && vehicleID ){
						var URL="/fleet/view/reports/?module=/blocks/json&data=view&report=pdfgeneration";
						URL += "&reporttype=acitivity" ;
						URL += "&reportformat=excel" ;
						URL += "&vehicleName=" + vehicleName;
						URL += "&vehicleID=" + vehicleID;
						URL += "&startdate=" + timeFrame.startDate;
						URL += "&enddate=" + timeFrame.endDate;
						URL += "&interval=" + timeFrame.interval;
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
		this.nextButtonUpdate.scope = this;
		YAHOO.util.Event.addListener($D.getElementsByClassName("activitynext"), "click",function(oArgs){
			var finalstarttime;
			var URL = null;
			var lastVehicleName = null;
			var aRecords = this._oDataTable.getRecordSet().getRecords();
			var oTimeFrame = this.get($W.BaseReport.ATTR_SELECTED_TIMEFRAME);
			var selectedVehicle = this.get($W.BaseReport.ATTR_SELECTED_ITEM);
			if(aRecords.length > 0){
				var dateString = aRecords[(aRecords.length)-1].getData().date;
				var myDate = new Date(dateString);
				var final_date = (myDate.getMonth()+1)+"/"+myDate.getDate()+"/"+myDate.getFullYear();
				var final_time = myDate.getHours()+":"+myDate.getMinutes()+":"+myDate.getSeconds();
				finalstarttime = final_date+' '+final_time;
			}
			if(this._oDataTable.get('paginator').hasNextPage()){
				this._oDataTable.get('paginator').setPage(this._oDataTable.get('paginator').getCurrentPage()+1);
				this.addListenersForAddressFetch();
			}else if (oTimeFrame && !this._oDataTable.get('paginator').hasNextPage() && ((aRecords.length) % 15) == 0 && aRecords.length > 0) {
				URL = "/fleet/view/reports/?module=/blocks/json&data=view&report=activityreport";
				URL += "&localTime=" +$U.getLocalTime();
				URL += "&vehicleID=" + selectedVehicle;
				URL += "&startdate=" + finalstarttime;
				URL += "&enddate=" + oTimeFrame.endDate;
				URL += "&interval=" + oTimeFrame.interval;
				URL += "&isFirstPage=false";
				$U.showLayerOverlay();
				$U.Connect.asyncRequest('GET', URL ,this.nextButtonUpdate,null);
			}
		},null,this);

		YAHOO.util.Event.addListener($D.getElementsByClassName("activityprevious"), "click",function(oArgs){
			this._oDataTable.get('paginator').setPage(this._oDataTable.get('paginator').getCurrentPage()-1);
			this.addListenersForAddressFetch();
		},null,this);
	},
	nextButtonUpdate:{
		success : function(o){
		var aResultRecords = [];
		var cumulativeDistance = 0;
		var selectedVehicle = this.get($W.BaseReport.ATTR_SELECTED_ITEM);
		var data=JSON.parse(o.responseText);
		var previousRecords=this._oDataTable.getRecordSet().getRecords();
		cumulativeDistance=previousRecords[previousRecords.length - 1]._oData.distance;
		var aData1=$U.Arrays.mapToArray(data.activity);
		var currentRecords=$U.Arrays.mapToArray(aData1[0].positions);
		for (var i = 0; i < previousRecords.length; i++) {
			aResultRecords.push(JSON.stringify(previousRecords[i]._oData));
		}
		if(currentRecords){
			for (var i = 0; i < currentRecords.length; i++) {
				aResultRecords.push(JSON.stringify(currentRecords[i]));
			}
		}
		var totalRecords='{"activity":{"'+selectedVehicle+'":{ "positions": ['+aResultRecords+']}}}';
		this.set($W.BaseReport.ATTR_DATA, JSON.parse(totalRecords));
		var allRecords=this._oDataTable.getRecordSet().getRecords();
		for (var i =previousRecords.length ; i < allRecords.length; i++) {
			allRecords[i]._oData.distance=parseFloat(+allRecords[i]._oData.distance + +cumulativeDistance).toFixed(2);
			cumulativeDistance=parseFloat(allRecords[i]._oData.distance).toFixed(2);
		}
		$U.hideLayerOverlay();
		this._oDataTable.get('paginator').setPage(this._oDataTable.get('paginator').getCurrentPage()+1);
		this.addListenersForAddressFetch();
	}, 
	failure : function(o){
		$U.alert({message: "No more records found for the selcted vehicle between the selected timeframe"});
	}
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
			this.oAddressCallback.scope= this;
			var start=JSON.parse(JSON.stringify(record))._oData.location;
			if(JSON.parse(JSON.stringify(record))._oData.location.indexOf(":") != -1){
				var URL = null;
				var latlongs=record._oData.location.split(":");
				if (latlongs.length !=0) {
					URL = "/fleet/view/reports/?module=/blocks/json&data=view&report=ondemandlocationfetch";
					URL += "&lat=" +latlongs[0];
					URL += "&long=" + latlongs[1];
					URL += "&column=" + 2;
					URL += "&row=" +this._oDataTable.getRecordIndex(record);
				}
				$U.showLayerOverlay();
				$U.Connect.asyncRequest("POST",URL, this.oAddressCallback, null);
			}

		},null,this);
	},
	oAddressCallback: {
		success: function(o) {
		$U.hideLayerOverlay();
		var oData = JSON.parse(o.responseText);
		var records=this._oDataTable.getRecordSet().getRecords();
		var rownumber=oData.row;
		records[rownumber]._oData.location =oData.address;
		this._oDataTable.set('source', records);
		this._oDataTable.render();
		this.addListenersForAddressFetch();
	},
	failure: function(o) {
	}
	},	
	/**
	 * Renders the dataTable for the incoming data
	 */
	render: function(){
		var selectedVehicle=vehicleID = this.get($W.BaseReport.ATTR_SELECTED_ITEM);
		var oData = this.get($W.BaseReport.ATTR_DATA);
		var widgetEl = $D.get("vehicleactivityreport");
		this.oCallBack.getVehicleName.scope = this;
		$U.Connect.asyncRequest('GET', "/fleet/view/reports/?module=/blocks/json&data=view&report=activityreport"+
				"&getvehiclename=true&vehicleID="+selectedVehicle, this.oCallBack.getVehicleName,null);
		var elErrDialog = $D.getElementsByClassName("simpledialog", null, this.elBase)[0];
		if (oData) {
			$D.removeClass(widgetEl, "empty");
			if (!$D.hasClass(elErrDialog, "disabled")) {
				$D.addClass(elErrDialog, "disabled");
			}
			if (!this._oDataTable || !this._oDataSource) {
				var oConfig = this.constructConfig();
				this._oDataSource = oConfig.datasource;
				this._oDataTable = new $W.ActivityReport.DataTable(this.elBase, oConfig, {
					"selectedVehicle": selectedVehicle
				});
				this._oDataTable.get('paginator').setAttributeConfig('template', {value : "{CurrentPageReport}&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;Page {PageLinks} {PageCount} "});
				this._oDataTable.get('paginator').setAttributeConfig('pageReportTemplate', {value : "Showing {recordsCount} of {totalRecords}"});
				this._oDataTable.get('paginator').setAttributeConfig('pageLinks', {value : 1});
				this._oDataTable.get('paginator').setAttributeConfig('rowsPerPage', {value : 15});
			}
			var oCallback = {
					success: this._oDataTable.onDataReturnInitializeTable,
					failure: this._oDataTable.onDataReturnInitializeTable,
					scope: this._oDataTable,
					argument: this._oDataTable.getState()
			};
			var selectedTimeFrame = timeFrame= this.get($W.BaseReport.ATTR_SELECTED_TIMEFRAME);
			interval=selectedTimeFrame.interval;
			if (selectedVehicle && selectedTimeFrame) {
				this._oDataTable._oReportDataSource.sendRequest(this, oCallback);
			}
		}
		else {
			if ($D.hasClass(elErrDialog, "disabled")) {
				$D.removeClass(elErrDialog, "disabled");
			}
		}
		elErrDialog = null;
		widgetEl = null;
		this.addListenersForAddressFetch();
	},
	show: function(){
		if (!(this._oDataTable && this._oDataSource)) {
			this._isDataStale = true;
		}
		$W.ActivityReport.superclass.show.call(this);
	},
	getPageID: function(){
		return $W.ActivityReport.PAGE_ID;
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
				fields: ["date", "location","speed"]
		};
		var columndefs = [{
			key: "date",
			label: "Time",
			sortable: true,
			resizeable: false,
			formatter: $W.Report.FORMATTERS.STRING_DATE_FORMATTER
		}, {
			key: "location",
			label: "Location",
			formatter:"button",
			sortable: true,
			resizeable: false
		},{
			key: "speed",
			label: "Speed",
			sortable: true,
			resizeable: false,
			parser: YAHOO.util.DataSource.parseNumber
		}];
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
	$L.augmentObject($W.ActivityReport.prototype, {
		/**
		 * Initializes widget
		 * @param {Object} el
		 * @param {Object} params
		 */
		initActivityReport: function(el, oArgs){
		if (!oArgs) {
			oArgs = {};
		}
		oArgs.dataType = $W.BaseReport.KEY_TYPE_JSON;
		oArgs.timeframe = true;
		$W.ActivityReport.superclass.constructor.call(this, el, oArgs);
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
		this.addListenersForNextAndPrevious();
		this.addExportButtonListener();
		this.addListenersForAddressFetch();
	}
	});


	/**
	 * Datatable widget for the Activity report
	 * @param {Object} el
	 * @param {Object} config
	 */
	$W.ActivityReport.DataTable = function(el, config, params){
		this.initDataTable(el, config);
		this.initParams = params;
	};
	/**
	 * overridable functions
	 * @param {Object} elBufferReport
	 */
	$L.extend($W.ActivityReport.DataTable, $W.Report, {
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
			elPrintTitleContent.innerHTML = "Vehicle Number : "+ vehicleName + "  " + elPrintTitleContent.innerHTML;
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
	$L.augmentObject($W.ActivityReport.DataTable.prototype, {
		/**
		 * initialises the datatable
		 * @param {Object} el
		 * @param {Object} config
		 */
		initDataTable: function(el, config){
		$W.ActivityReport.DataTable.superclass.constructor.call(this, el, config);
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
