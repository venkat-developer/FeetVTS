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
	var vehicleName=null;
	var vehicleID=null;
	var interval=null;
	var dataReturned=null;
	/**
	 * Vehicle Statistics Report Widget for Fleetcheck project.
	 *
	 * @extends com.i10n.fleet.widget.ui.Report
	 * @author sabarish
	 */
	$W.VehicleStatsReport = function(el, oArgs){
		this.initVehicleStatsReport(el, oArgs);
	};
	$L.augmentObject($W.VehicleStatsReport, {
		PAGE_ID: "vehiclestats"
	});
	$L.extend($W.VehicleStatsReport, $W.BaseReport, {
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
		var sURL = null;
		var oTimeFrame =  this.get($W.BaseReport.ATTR_SELECTED_TIMEFRAME);
		var sVehicleId = this.get($W.BaseReport.ATTR_SELECTED_ITEM);
		if (oTimeFrame && sVehicleId) {
			sURL = "/fleet/view/reports/?module=/blocks/json&data=view&report=vehiclestatsreport";
			sURL += "&localTime=" +$U.getLocalTime();
			sURL += "&startdate=" + oTimeFrame.startDate;
			sURL += "&vehicleID=" + sVehicleId;
			sURL += "&enddate=" + oTimeFrame.endDate;
		}else{
			sURL = "/fleet/view/reports/?module=/blocks/json&data=view&report=vehiclestatsreport";
			sURL += "&localTime=" +$U.getLocalTime();
			sURL += "&vehicleID=" + sVehicleId;
			sURL += "&startdate=" + oTimeFrame.startDate;
			sURL += "&enddate=" + oTimeFrame.endDate;
		}
		return sURL;
	},
	/**
	 * Initializes widget
	 * @param {Object} el
	 * @param {Object} params
	 */
	initVehicleStatsReport: function(el, oArgs){
		if (!oArgs) {
			oArgs = {};
		}
		oArgs.dataType = $W.BaseReport.KEY_TYPE_JSON;
		oArgs.timeframe = true;
		$W.VehicleStatsReport.superclass.constructor.call(this, el, oArgs);
		this.addListenersForNextAndPrevious();
		this.addExportButtonListener();
	},
	//Listener for export report button which checks for selected report format and generates the report and asks user to download.
	addExportButtonListener :  function(){
		this.exportButtonCallBack.scope = this;
		YAHOO.util.Event.addListener($D.getElementsByClassName("exportvehiclestats"), "click",function(oArgs){
			var timeFrame= this.get($W.BaseReport.ATTR_SELECTED_TIMEFRAME);
			var pdf = document.getElementById("vehiclestatspdf").checked;
			var excel = document.getElementById("vehiclestatsexcel").checked;
			if(pdf || excel){
				if(this._oDataTable.getRecordSet().getRecords().length > 0){
					if(pdf && timeFrame ){
						var URL="/fleet/view/reports/?module=/blocks/json&data=view&report=pdfgeneration";
						URL += "&reporttype=vehiclestats" ;
						URL += "&reportformat=pdf" ;
						URL += "&startdate=" + timeFrame.startDate;
						URL += "&enddate=" + timeFrame.endDate;
						URL += "&localTime=" +$U.getLocalTime();
						$U.showLayerOverlay();
						YAHOO.util.Connect.asyncRequest('GET',URL,this.exportButtonCallBack,null);
					}else if(excel && timeFrame ){
						var URL="/fleet/view/reports/?module=/blocks/json&data=view&report=pdfgeneration";
						URL += "&reporttype=vehiclestats" ;
						URL += "&reportformat=excel" ;
						URL += "&startdate=" + timeFrame.startDate;
						URL += "&enddate=" + timeFrame.endDate;
						URL += "&localTime=" +$U.getLocalTime();
						$U.showLayerOverlay();
						YAHOO.util.Connect.asyncRequest('GET',URL,this.exportButtonCallBack,null);
					}else{
						$U.alert({message: "No data available for the user between selected time interval !"});
					}
				}else{
					$U.alert({message: "No data available for the user between selected time interval !"});  
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
		$U.alert({message: "Due to some problem no file is created!"});
	}
	},
	//Listeners for next and previous buttons showed in place of pagination buttons and respective callback methods are called.
	addListenersForNextAndPrevious :  function(){
		this.nextButtonUpdate.scope = this;
		YAHOO.util.Event.addListener($D.getElementsByClassName("vehiclestatsnext"), "click",function(oArgs){
			var URL = null;
			var lastVehicleName = null;
			var aRecords = this._oDataTable.getRecordSet().getRecords();
			var oTimeFrame = this.get($W.BaseReport.ATTR_SELECTED_TIMEFRAME);
			if(aRecords.length > 0){
				lastVehicleName = aRecords[(aRecords.length)-1].getData().name;
			}
			if(this._oDataTable.get('paginator').hasNextPage()){
				this._oDataTable.get('paginator').setPage(this._oDataTable.get('paginator').getCurrentPage()+1);
				this.addListenersForAddressFetch();
			}else if (oTimeFrame && !this._oDataTable.get('paginator').hasNextPage() && ((aRecords.length) % 15) == 0 && aRecords.length > 0) {
				URL = "/fleet/view/reports/?module=/blocks/json&data=view&report=vehiclestatsreport";
				URL += "&localTime=" +$U.getLocalTime();
				URL += "&vehicleID=" + vehicleID;
				URL += "&vehicleName=" + lastVehicleName;
				URL += "&startdate=" + oTimeFrame.startDate;
				URL += "&enddate=" + oTimeFrame.endDate;
				$U.showLayerOverlay();
				$U.Connect.asyncRequest('GET', URL ,this.nextButtonUpdate,null);
			}
		},null,this);

		YAHOO.util.Event.addListener($D.getElementsByClassName("vehiclestatsprevious"), "click",function(oArgs){
			this._oDataTable.get('paginator').setPage(this._oDataTable.get('paginator').getCurrentPage()-1);
			this.addListenersForAddressFetch();
		},null,this);
	},
	nextButtonUpdate:{
		success : function(o){
		var aResultRecords = [];
		var data=JSON.parse(o.responseText);
		var aData1=$U.Arrays.mapToArray(data);
		var previousRecords=this._oDataTable.getRecordSet().getRecords();
		var currentRecords=$U.Arrays.mapToArray(aData1[0].vehicles);
		for (var i = 0; i < previousRecords.length; i++) {
			aResultRecords.push('\"'+previousRecords[i]._oData.id+'\":'+JSON.stringify(previousRecords[i]._oData));
		}
		if(currentRecords){
			for (var i = 0; i < currentRecords.length; i++) {
				aResultRecords.push('\"'+currentRecords[i].id+'\":'+JSON.stringify(currentRecords[i]));
			}
		}
		var totalRecords='{"group-0":{"id": "0" , "name": "northzone" , "vehicles":{ '+aResultRecords+'}}}';
		this.set($W.BaseReport.ATTR_DATA, JSON.parse(totalRecords));
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
			var start=JSON.parse(JSON.stringify(record))._oData.startlocation;
			if(column._nKeyIndex === 4 && JSON.parse(JSON.stringify(record))._oData.startlocation.indexOf(":") != -1){
				var URL = null;
				var latlongs=record._oData.startlocation.split(":");
				if (latlongs.length !=0) {
					URL = "/fleet/view/reports/?module=/blocks/json&data=view&report=ondemandlocationfetch";
					URL += "&lat=" +latlongs[0];
					URL += "&long=" + latlongs[1];
					URL += "&column=" + 4;
					URL += "&row=" +record._oData.index;
				}
				$U.showLayerOverlay();
				$U.Connect.asyncRequest("POST",URL, this.oAddressCallback, null);
			}else if(column._nKeyIndex === 6 && JSON.parse(JSON.stringify(record))._oData.endlocation.indexOf(":") != -1){
				var URL = null;
				var latlongs=record._oData.endlocation.split(":");
				if (latlongs.length !=0) {
					URL = "/fleet/view/reports/?module=/blocks/json&data=view&report=ondemandlocationfetch";
					URL += "&lat=" +latlongs[0];
					URL += "&long=" + latlongs[1];
					URL += "&column=" + 6;
					URL += "&row=" + record._oData.index;
				}
				$U.showLayerOverlay();
				$U.Connect.asyncRequest("POST",URL, this.oAddressCallback, null);
			}

		},null,this);
	},
	render: function(){
		var oData = dataReturned = this.get($W.BaseReport.ATTR_DATA);
		if (this._oDataTable && this._oDataSource) {
			this._oDataSource.setGroupData(oData);
		}
		else {
			var oConfig = this.constructConfig(oData);
			this._oDataSource = oConfig.datasource;
			this._oDataTable = new $W.GroupedReport(this.elBase, oConfig);
			this._oDataTable.get('paginator').setAttributeConfig('template', {value : "{CurrentPageReport}&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;Page {PageLinks} {PageCount} "});
			this._oDataTable.get('paginator').setAttributeConfig('pageReportTemplate', {value : "Showing {recordsCount} of {totalRecords}"});
			this._oDataTable.get('paginator').setAttributeConfig('pageLinks', {value : 1});
			this._oDataTable.get('paginator').setAttributeConfig('rowsPerPage', {value : 15});
		}
		this.addListenersForAddressFetch();
	},
	oAddressCallback: {
		success: function(o) {
		$U.hideLayerOverlay();
		var oData = JSON.parse(o.responseText);
		var records=this._oDataTable.getRecordSet().getRecords();
		var rownumber=oData.row-1;
		var columnnumber=oData.column;
		if(columnnumber==4){
			records[rownumber]._oData.startlocation =oData.address;
		}else if(columnnumber==6){
			records[rownumber]._oData.endlocation =oData.address;
		}
		this._oDataTable.set('source', records);
		this._oDataTable.render();
		this.addListenersForAddressFetch();
	},
	failure: function(o) {
	}
	},
	show: function(){
		if (!(this._oDataTable && this._oDataSource)) {
			this._isDataStale = true;
		}
		$W.VehicleStatsReport.superclass.show.call(this);
	},
	getPageID: function(){
		return $W.VehicleStatsReport.PAGE_ID;
	},
	/**
	 * Constructs a config object for Reports to initialize from the data given
	 * @param {Object} reportData
	 */
	constructConfig: function(reportData){
		var config = {};
		var status = reportData;
		var datasource = new $W.GroupedReport.GroupedDataSource(reportData);
		datasource.responseType = $YU.DataSource.TYPE_JSARRAY;
		datasource.responseSchema = {
				elementField: "vehicles",
				fields: ["id", "index", "name", "starttime", "startlocation", "endtime", "endlocation"/*,"idleDuration"*/, "maxspeed", "avgspeed","distance"/*, "startfuel", "fuel"*/]
		};
		var columndefs = [{
			key: "id",
			hidden: true
		}, {
			key: "index",
			label: "No.",
			sortable: true
		}, {
			key: "name",
			label: "Vehicle Name",
			sortable: true,
			sortOptions: {
			defaultDir: $YW.DataTable.CLASS_DESC
		},
		resizeable: false
		}, {
			key: "starttime",
			label: "Start Time",
			sortable: true,
			//                formatter: $W.Report.FORMATTERS.STRING_DATE_FORMATTER,
			resizeable: false
		}, {
			key: "startlocation",
			label: "Start Location",
			formatter:"button",
			sortable: true,
			resizeable: false
		}, {
			key: "endtime",
			label: "End Time",
			sortable: true,
			//                formatter: $W.Report.FORMATTERS.STRING_DATE_FORMATTER,
			resizeable: false
		}, {
			key: "endlocation",
			label: "Current Location",
			formatter:"button",
			sortable: true,
			resizeable: false
		}
		//,{
		// key: "idleDuration",
		// label: "Idle Duration",
		//sortable: true,
		//resizeable: false
		//}
		,{
			key: "maxspeed",
			label: "Maximum Speed",
			sortable: true,
			resizeable: false
		},{											  				//Commenet this block for TNCSC client
			key: "avgspeed",
			label: "Average Speed",
			sortable: true,
			resizeable: false
		},{
			key: "distance",
			label: "Distance",
			sortable: true,
			resizeable: false
		},
		/*
		 * Removed for tncsc client
		 */
		/*{
                key: "startfuel",
                label: "Start Fuel",
                sortable: true,
                formatter: $W.Report.FORMATTERS.FUEL_FORMATTER,
                resizeable: false
            }, {
                key: "fuel",
                label: "Current Fuel",
                sortable: true,
                formatter: $W.Report.FORMATTERS.FUEL_FORMATTER,
                resizeable: false
            }*/];

		config.datasource = datasource;
		config.columndefs = columndefs;
		config.reportconfig = {};
		config.options = {
				"select": {
			"enabled": true,
			"selected": true,
			"page": true
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
	}
	});
	/**
	 * Call back function used to open the of file requested by the user to download
	 * returns url of the file created 
	 */
	var callback =
		{   
			success: function(o){
		$U.hideLayerOverlay();
		var oResponse = JSON.parse(o.responseText);
		var url = oResponse.pdf ;
		window.open(url);
	},      
	failure: function(o){
		alert('There is no file created for download !');
		$U.hideLayerOverlay();
	}
		};
})();
