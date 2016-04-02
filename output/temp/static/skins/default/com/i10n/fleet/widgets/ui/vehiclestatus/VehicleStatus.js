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
	var isEmriClient = false;
	var isPushLetAlive=true;
	/**
	 * Vehicle Status Report Widget for Fleetcheck project.
	 *
	 * @extends com.i10n.fleet.widget.ui.GroupedReport
	 * @author sabarish
	 */
	$W.VehicleStatus = function(el, oArgs){
		thisvariable = this;
		this.initVehicleStatus(el, oArgs);

		$V.BaseView.subscribeFn("statusUpdate",this.callback);
	};
	$L.augmentObject($W.VehicleStatus, {
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
	$L.extend($W.VehicleStatus, $W.GroupedReport);
	$L.augmentObject($W.VehicleStatus.prototype, {
		/**
		 * Object Map of widgets initialized by the this widget
		 */
		_widgets: {},
		/**
		 * Initializes widget
		 * @param {Object} el
		 * @param {Object} oArgs
		 */
		initVehicleStatus: function(el, oArgs){

			var config = this.constructConfig(oArgs);

			if(config){
				oRecord = new $W.GroupedReport(el, config);

			}
			this.addListenersForNextAndPrevious();
			document.getElementById("exportlivevehiclestatus").addEventListener("click",function(){
				var exportvalue;
				var radiobtlength=$D.getElementsByClassName('exportlivevehiclestatus').length;
				var radiobuttons=$D.getElementsByClassName('exportlivevehiclestatus');
				for(var i=0;i<radiobtlength;i++){
					if(radiobuttons[i].checked){
						exportvalue =radiobuttons[i].value;
					}
				}
				if(exportvalue != null){
					if(exportvalue=="pdf"){
						var URL="/fleet/view/reports/?module=/blocks/json&data=view&report=pdfgeneration";
						URL += "&reporttype=livevehiclestatus" ;
						URL += "&reportformat="+exportvalue ;
						$U.showLayerOverlay();
						YAHOO.util.Connect.asyncRequest('GET',URL,fileCallback,null);
					}else if(exportvalue=="excel"){
						var URL="/fleet/view/reports/?module=/blocks/json&data=view&report=pdfgeneration";
						URL += "&reporttype=livevehiclestatus" ;
						URL += "&reportformat="+exportvalue ;
						$U.showLayerOverlay();
						YAHOO.util.Connect.asyncRequest('GET',URL,fileCallback,null);
					}else{
						alert('No data available to create a output file!');  
					}
				}else{
					alert('please select one of the formats specified below to download the report');
				}

			},false);
			oRecord.get('paginator').setAttributeConfig('template', {value : "{CurrentPageReport}&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;Page {PageLinks} {PageCount} "});
			oRecord.get('paginator').setAttributeConfig('pageReportTemplate', {value : "Showing {recordsCount} of {totalRecords}"});
			oRecord.get('paginator').setAttributeConfig('pageLinks', {value : 1});
			oRecord.get('paginator').setAttributeConfig('rowsPerPage', {value : 15});
			oRecord.render();

		},
		//Listeners for next and previous buttons showed in place of pagination buttons and respective callback methods are called.
		addListenersForNextAndPrevious :  function(){
			YAHOO.util.Event.addListener($D.getElementsByClassName("vehiclestatsnext"), "click",function(oArgs){
				oRecord.get('paginator').setPage(oRecord.get('paginator').getCurrentPage()+1);

			},null,this);

			YAHOO.util.Event.addListener($D.getElementsByClassName("vehiclestatsprevious"), "click",function(oArgs){
				oRecord.get('paginator').setPage(oRecord.get('paginator').getCurrentPage()-1);
			},null,this);
		},

		/**
		 * Constructs a config object for Reports to initialize from the data given
		 * @param {Object} oArgs
		 */
		constructConfig: function(oArgs){
			var config = {};
			var data = [];
			var count = 0;
			var columndefs = null;
			if(isEmriClient){
				columndefs = [{
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
					key: "district",
					label: "District",
					sortable:true,
					resizeable:false
				},{
					key: "baselocation",
					label: "Base Location",
					sortable:true,
					resizeable:false
				},{
					key: "crewno",
					label: "Crew Number",
					sortable:true,
					resizeable:false
				},{
					key: "status",
					label: "Status",
					formatter: $W.VehicleStatus.FORMATTER_STATUS,
					sortable: true,
					resizeable: false
				}, 
				{
					key:"speed",
					label:"Speed",
					sortable:true,
					resizeable:false
				},
				{
					key:"location",
					label:"Current Location",
					sortable:true,
					resizeable:false
				},
				{
					key: "timeinmilliseconds",
					label: "Last Updated",
					formatter: $W.Report.FORMATTERS.DATE_FORMATTER,
					sortable: true,
					resizeable: false
				}, {
					key:"imeino",
					label:"Unit No",
					sortable:true,
					resizeable:false
				}
				];
			}
			else{
				columndefs = [{
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
					key: "status",
					label: "Status",
					formatter: $W.VehicleStatus.FORMATTER_STATUS,
					sortable: true,
					resizeable: false
				}, {
					key: "cc",
					label: "Charger Connected",
					formatter: $W.Report.FORMATTERS.CHARGERDC_FORMATTER,
					sortable: true,
					resizeable: false
				}, {
					key:"location",
					label:"Current Location",
					sortable:true,
					resizeable:false
				},
				/*
				 * removed for TNCSC client
				 */
				/*{
                	key:"seatbelt",
                	label:"SeatBeltStatus",
                //	formatter:$W.Report.FORMATTERS.FORMATTER_SEATBELT,
                	sortable:true,
                	resizeable:false
                },*/

				/*{
                	key:"ignition",
                	label:"IgnitionStatus",
                //  formatter: $W.Report.FORMATTERS.FORMATTER_SEATBELT,
                	sortable:true,
                	resizeable:false
                },*/
				{
					key: "gps",
					label: "GPS Strength",
					formatter: $W.Report.FORMATTERS.GPS_FORMATTER,
					sortable: true,
					resizeable: false
				}, {
					key: "gsm",
					label: "GSM Strength",
					formatter: $W.Report.FORMATTERS.GSM_FORMATTER,
					sortable: true,
					resizeable: false
				}, {
					key: "battery",
					label: "Battery Voltage",
					formatter: $W.Report.FORMATTERS.BATTERY_FORMATTER,
					sortable: true,
					resizeable: false
				}, {
					key: "timeinmilliseconds",
					label: "Last Updated",
					formatter: $W.Report.FORMATTERS.DATE_FORMATTER,
					sortable: true,
					resizeable: false
				}
				/*{
                    key: "moduleupdatetime",
                    label: "Module Update Time",
                    formatter: $W.Report.FORMATTERS.STRING_DATE_FORMATTER,
                    sortable: true,
                    resizeable: false
                }*/];
			}
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

		callback:{
			success: function(liveUpdateData){
			if(isPushLetAlive){
				thisvariable.liveupdate(liveUpdateData);
			}else{
				// Use this for Timer based auto update
				thisvariable.timerLiveUpdate(liveUpdateData);
			}
		}
		},

		liveupdate:function(result){
			result = eval('('+result+')');
			var cont = result.content;
			for(var i in cont){
				for(j=0;j<oRecord.getRecordSet().getLength();j++){
					var oData = cont[i];
					var pushletDataId = oData.id;
					var tableDataId = oRecord.getRecordSet().getRecord(j).getData("id");
					if( pushletDataId.trim() == tableDataId.trim()){
						var indx=oRecord.getRecordSet().getRecords()[j].getData().index;
						oData.select = oRecord.getRecordSet().getRecord(j).getData("select");
						oRecord.getRecordSet().setRecord(oData,oRecord.getRecordSet().getRecordIndex(oRecord.getRecordSet().getRecord(j)));
						oRecord.getRecordSet().getRecords()[j].getData().index=indx;
						oRecord.render();
						break;
					}
				}
			}
		},

		timerLiveUpdate:function(result){
			var oResponse=JSON.parse(result.responseText);
			var cont = oResponse.demomanage.livedata.liveData;
			if(cont){
				thisvariable.mainDataChunks(cont);
			}
		},
		mainDataChunks : function(array){
			// set this to whatever number of items you can process at once
			var chunk = 10;
			var index = 0;
			function doChunk() {
				var cnt = chunk;
				while (cnt-- && index < array.length) {
					var oData = array[index];
					thisvariable.innerDataChunks(oRecord.getRecordSet(),oData);
					++index;
				}
				if (index < array.length) {
					setTimeout(doChunk, 1);
				}
			}
			doChunk();
		},
		innerDataChunks: function(array,oData){
			// set this to whatever number of items you can process at once
			var chunk = 10;
			var index = 0;
			function doChunk() {
				var cnt = chunk;
				while (cnt-- && index < array.getLength()) {
					var pushletDataId = 'vehicle-'+oData.vehicleId;
					var tableDataId = array.getRecord(index).getData("id");
					if( pushletDataId.trim() == tableDataId.trim()){
						oData.select = array.getRecord(index).getData("select");
						var indx=oRecord.getRecordSet().getRecords()[index].getData().index;
						oRecord.getRecordSet().setRecord(oData,oRecord.getRecordSet().getRecordIndex(oRecord.getRecordSet().getRecord(index)));
						oRecord.getRecordSet().getRecords()[index].getData().id=tableDataId;
						oRecord.getRecordSet().getRecords()[index].getData().index=indx;
						oRecord.render();
					}
					// process array[index] here
					++index;
				}
				if (index < array.getLength()) {
					setTimeout(doChunk, 1);
				}
			}
			doChunk();
		}
	});
	/**
	 * Call back function used to open the file requested by the user to download
	 * returns url of the file created 
	 */
	var fileCallback ={   
			success: function(o){
		$U.hideLayerOverlay();
		var oResponse = JSON.parse(o.responseText);
		var url = oResponse.filepath ;
		window.open(url);
	},      
	failure: function(o){
		$U.hideLayerOverlay();
		alert('There is no file created for download !');
	}
	};
})();
