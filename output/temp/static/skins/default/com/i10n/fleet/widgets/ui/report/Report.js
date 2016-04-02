/**
 * Custom UI components to display the 'of page count'
 */
(function(){
	var Paginator = YAHOO.widget.Paginator;
	var l = YAHOO.lang;
	/**
	 * ui Component to display the 'of page count'.
	 *
	 * @namespace YAHOO.widget.Paginator.ui
	 * @class PageCount
	 * @for YAHOO.widget.Paginator
	 * @constructor
	 * @param p
	 *             {Paginator} Paginator instance to attach to
	 */
	Paginator.ui.PageCount = function(p){
		this.paginator = p;
		p.subscribe('recordOffsetChange', this.update, this, true);
		p.subscribe('totalRecordsChange', this.update, this, true);
		p.subscribe('destroy', this.destroy, this, true);
	};
	Paginator.ui.PageCount.init = function(p){
	};
	Paginator.ui.PageCount.prototype = {
			/**
			 * Span node
			 * @property span
			 * @type HTMLElement
			 * @private
			 */
			span: null,
			/**
			 * Generate the nodes and return the appropriate node
			 *
			 * @method render
			 * @param id_base
			 *            {string} used to create unique ids for generated nodes
			 * @return {HTMLElement}
			 */
			render: function(id_base){
		var p = this.paginator;
		this.span = document.createElement('span');
		this.span.id = id_base + '-pageCount-span';
		YAHOO.util.Dom.addClass(this.span, id_base + '-pageCount-span');
		YAHOO.util.Dom.addClass(this.span, 'yui-pg-pagecount');
		/**
		 *  Displaying the page count
		 */
		if (p.getTotalPages() > 0) {
			this.span.innerHTML = " of " + p.getTotalPages();
		}
		else {
			this.span.innerHTML = "";
		}
		return this.span;
	},
	/**
	 * Called when the total page count changes
	 *
	 * @method update
	 * @param e
	 */
	update: function(e){
		var p = this.paginator;
		if (p.getTotalPages() > 0)
			this.span.innerHTML = " of " + p.getTotalPages();
		else
			this.span.innerHTML = "";
	},
	/**
	 *  Removes the span node and clears event listeners
	 *
	 * @method destroy
	 * @private
	 **/
	destroy: function(){
		YAHOO.util.Event.purgeElement(this.span);
		this.span.parentNode.removeChild(this.span);
		this.span = null;
	},
	/**
	 * executed when the page is changed
	 */
	resizePage: function(){
	}
	};
})();
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
	/**
	 * Default Reports Widget for Fleetcheck project. All The Reports should extends this
	 * widget. All the users will pass the config as params while initializing.
	 * Required Structure of params
	 * params = {
	 *     reportconfig : <doc>Will contain overrides/additions to config that will passes on the YI Datatable</doc>,
	 *     options : {
	 *         select : {
	 *             enabled : <doc>Enable adddition of select checkboxes as the first column in each row</doc>,
	 *             selected : <doc>If true will select all the records by default</doc>
	 *         },
	 *         "print": {
	 *             "enabled": <doc>Enable print preview</doc>
	 *         }
	 *     },
	 *     columndefs : [
	 *         <doc>Array containing columndefs required by the Datatable.
	 *         If select is enabled. This columndefs must have a filed with key as id</doc>
	 *     ]
	 * }
	 *
	 * @author sabarish
	 */
	$W.Report = function(el, params){
		this.DEFAULT_REPORT_CONFIG = {
				paginator: new $YW.Paginator({
					containers: $D.getElementsByClassName("paginator", null, el),
					pageReportValueGenerator: function(paginator){
					var curPage = paginator.getCurrentPage(), records = paginator.getPageRecords();
					return {
						'currentPage': records ? curPage : 0,
								'totalPages': paginator.getTotalPages(),
								'startIndex': records ? records[0] : 0,
										'endIndex': records ? records[1] : 0,
												'recordsCount': records ? records[1] - records[0] + 1 : 0,
														'startRecord': records ? records[0] + 1 : 0,
																'endRecord': records ? records[1] + 1 : 0,
																		'totalRecords': paginator.get('totalRecords')
					};
				},
				template: "{CurrentPageReport}&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;Page {FirstPageLink}{PreviousPageLink} {PageLinks} {PageCount} {NextPageLink} {LastPageLink}",
				pageReportTemplate: "Showing {recordsCount} of {totalRecords}",
				firstPageLinkLabel: "&nbsp;",
				firstPageLinkClass: "yui-pg-first inline-block",
				previousPageLinkLabel: "&nbsp;",
				previousPageLinkClass: "yui-pg-previous inline-block",
				nextPageLinkLabel: "&nbsp;",
				nextPageLinkClass: "yui-pg-next inline-block",
				lastPageLinkLabel: "&nbsp;",
				lastPageLinkClass: "yui-pg-last inline-block",
				pageLinks: 5,
				rowsPerPage: 15
				})
		};
		this.baseElement = el;
		this.initParams = params;
		this.attrs = new $YU.AttributeProvider();
		if ($D.hasClass(el, 'report')) {
			this.reportElement = el;
		}
		else {
			this.reportElement = $D.getElementsByClassName('report', null, el)[0];
		}
		this.reportConfig = null;
		this.initReports(this.reportElement, params);
	};
	/**
	 * Augmenting Static variables
	 */
	$L.augmentObject($W.Report, {
		ATTR_SELECT_ENABLED: "selectEnabled",
		ATTR_PRINT_ENABLED: "printEnabled",
		ATTR_GROUPING_ENABLED: "groupingEnabled",
		ATTR_SELECT_ALL_ENABLED: "selectAllEnabled",
		KEY_SELECT: "select",
		/**
		 * A utilty function used by the above formatters to convert numerical level of strength to
		 * strength in english
		 * @param {Object} sLevel
		 */
		getStrengthFromLevel: function(sLevel){
		var sStrength = "Normal";


		if(sLevel>=0.7 && sLevel <= 1.0){

			sStrength="Strong";
		}else if(sLevel>1.0 && sLevel<=2.0)
		{
			sStrength="Normal";
		}
		else if(sLevel> 2.0 && sLevel<=5.0){
			sStrength="Weak";
		}
		else
		{
			sStrength="Very Weak";
		}
		return sStrength;
	},
	getGSMStrengthFromLevel: function(sLevel){
		var sStrength = "Normal";


		if(sLevel>25){

			sStrength="Strong";
		}else if(sLevel >=20 && sLevel<=25)
		{
			sStrength="Normal";
		}
		else if(sLevel >=15 && sLevel<20){
			sStrength="Weak";
		}
		else
		{
			sStrength="Very Weak";
		}
		return sStrength;
	},
	getBatteryStrengthFromLevel: function(sLevel){
		var sStrength = "Normal";


		if(sLevel>4000){

			sStrength="Strong";
		}else if(sLevel >=3700 && sLevel<=4000)
		{
			sStrength="Normal";
		}
		else if(sLevel >=3500 && sLevel<3700){
			sStrength="Weak";
		}
		else
		{
			sStrength="Very Weak";
		}
		return sStrength;
	},
	getFuelStrengthFromLevel: function(sLevel){
		var sStrength = "Normal";

		if(sLevel>1000){

			sStrength="Strong";
		}else if(sLevel >=700 && sLevel<=1000)
		{
			sStrength="Normal";
		}
		else if(sLevel >=500 && sLevel<=700){
			sStrength="Weak";
		}
		else
		{
			sStrength="Very Week";
		}
		return sStrength;

	},



	FORMATTERS: {
		/**
		 * Custom Cell Formatter for Date formatting
		 * @param {Object} elCell
		 * @param {Object} oRecord
		 * @param {Object} oColumn
		 * @param {Object} oData
		 */
		DATE_FORMATTER: function(elCell, oRecord, oColumn, oData){
		if (oData) {

			var oDate = $U.Date.getReportDate(oData);

			elCell.innerHTML = $YU.Date.format(oDate, {
				format: "%a %d %b %G %T"
			});

		}

	},

	STRING_DATE_FORMATTER: function(elCell, oRecord, oColumn, oData){
		if (oData) {

			elCell.innerHTML=oData;
		}

	},
	/**
	 * Custom Cell Formatter for formatting Fuel Levels
	 * @param {Object} elCell
	 * @param {Object} oRecord
	 * @param {Object} oColumn
	 * @param {Object} oData
	 */
	"FUEL_FORMATTER": function(elCell, oRecord, oColumn, oData){
		var sStrength = $W.Report.getFuelStrengthFromLevel(oData);
		var level=null;

		if(sStrength=="Strong"){
			level=3;
		}else if (sStrength=="Normal"){
			level=2;
		}else if(sStrength=="Weak"){
			level=1;
		}else{
			level=0;
		}                 
		elCell.innerHTML = "<div class='inline-block cell-custom cell-fuel icons fuel-icon-level-" + level + "'></div><div class='inline-block data'>" + sStrength + "</div>";
	},

	"FORMATTER_SEATBELT": function(elCell, oRecord, oColumn, oData){
		if (oData) {

			elCell.innerHTML=oData;
		}

	},
	/**
	 * Custom Cell Formatter for formatting GSM cell
	 * @param {Object} elCell
	 * @param {Object} oRecord
	 * @param {Object} oColumn
	 * @param {Object} oData
	 */
	"GSM_FORMATTER": function(elCell, oRecord, oColumn, oData){
		var sStrength = oData;//$W.Report.getGSMStrengthFromLevel(oData);
		var level=null;

		if(sStrength=="Strong"){
			level=3;
		}else if (sStrength=="Normal"){
			level=2
		}else if(sStrength=="Weak"){
			level=1;
		}else{
			level=0;
		}
		elCell.innerHTML = "<div class='inline-block cell-custom cell-gsm icons gsm-icon-level-" + level + "'></div><div class='inline-block data'>" + sStrength + "</div>";
	},
	/**
	 * Custom Cell Formatter for formatting GPS cell
	 * @param {Object} elCell
	 * @param {Object} oRecord
	 * @param {Object} oColumn
	 * @param {Object} oData
	 */
	"GPS_FORMATTER": function(elCell, oRecord, oColumn, oData){
		var sStrength = oData;//$W.Report.getStrengthFromLevel(oData);
		var level=null;
		if(sStrength=="Strong"){
			level=3;
		}else if (sStrength=="Normal"){
			level=2
		}else if(sStrength=="Weak"){
			level=1;
		}else{
			level=0;
		}
		elCell.innerHTML = "<div class='inline-block cell-custom cell-gps icons gps-icon-level-" + level + "'></div><div class='inline-block data'>" + sStrength + "</div>";
	},
	/**
	 * Custom Cell Formatter for formatting battery cell
	 * @param {Object} elCell
	 * @param {Object} oRecord
	 * @param {Object} oColumn
	 * @param {Object} oData
	 */
	"BATTERY_FORMATTER": function(elCell, oRecord, oColumn, oData){
		var sStrength =oData; //$W.Report.getBatteryStrengthFromLevel(oData);
		var level=null;
		if(sStrength=="Strong"){
			level=3;
		}else if (sStrength=="Normal"){
			level=2
		}else if(sStrength=="Weak"){
			level=1;
		}else{
			level=0;
		}
		elCell.innerHTML = "<div class='inline-block cell-custom cell-battery icons battery-icon-level-" + level + "'></div><div class='inline-block data'>" + sStrength + "</div>";
	},
	/**
	 * Custom Cell Formatter for formatting Charger Connected cell
	 * @param {Object} elCell
	 * @param {Object} oRecord
	 * @param {Object} oColumn
	 * @param {Object} oData
	 */
	"CHARGERDC_FORMATTER": function(elCell, oRecord, oColumn, oData){
		elCell.innerHTML = "<div class='inline-block cell-custom cell-chargerdc level-" + oData + "'>" + oData + "</div>";
	}
	}
	});
	$L.extend($W.Report, $YW.DataTable, {
		/**
		 * Overrides onDataReturnInitializeTable in DataTable for Custom Post Processing
		 * @param {Object} sRequest
		 * @param {Object} oResponse
		 * @param {Object} oPayload
		 */
		onDataReturnInitializeTable: function(sRequest, oResponse, oPayload){
		$W.Report.superclass.onDataReturnInitializeTable.call(this, sRequest, oResponse, oPayload);
		this._processDataTable();
		this.render();
		/**
		 * Seems like a bug in datatable. When data is returned the sort state is not maintained.
		 */
		var oState = this.getState();

		if (oState && oState.sortedBy && oState.sortedBy.key) {
			var sDir = oState.sortedBy.dir;
			var sKey = oState.sortedBy.key;
			this.sortColumn(this.getColumn(sKey), sDir);
		}
	}
	});
	$L.augmentObject($W.Report.prototype, {
		_oReportColumnDefs: null,
		_oReportDataSource: null,
		_oReportConfig: null,
		/**
		 * Initializes the Reports Widget
		 * @param {Object} el
		 * @param {Object} params
		 */
		initReports: function(el, params){
		this._configureOptionAttributes();
		this._parseOptions();
		var reportBody = $D.getElementsByClassName('bd', null, el)[0];
		var optionColumnConfig = this._getOptionConfig(params);
		this._oReportDataSource = params.datasource;
		$L.augmentObject(optionColumnConfig, params.columndefs);
		this._oColumnDefs = optionColumnConfig;
		this._renderHeader();
		if ($L.isObject(reportBody) && $L.isObject(params) && $L.isObject(params.datasource)) {
			this._oReportConfig = this.getReportConfig();
			$W.Report.superclass.constructor.call(this, reportBody, optionColumnConfig, params.datasource, this._oReportConfig);
			this.subscribe("checkboxClickEvent", this.onCheckBoxClick, this, true);
			this._processDataTable();
			this.render();
		}
		this._subscribeEvents();
		/**
		 * Avoiding possible memory leaks.
		 */
		reportBody = null;
		optionColumnConfig = null;
	},
	/**
	 * Subscribing events
	 */
	_subscribeEvents: function(){
		if (this.reportConfig && this.reportConfig.paginator) {
			this.subscribe('postRenderEvent', this.resizePage, this, true);
		}
	},
	/**
	 * resizes all the resizable elements of the view
	 */
	resizePage: function(){
		if (_instances.view) {
			_instances.view.resizePage();
		}
	},
	/**
	 * Initilaze Reports. Uses default paginator. If paginator is to be disabled,set
	 * params.reportconfig.paginator as null
	 * @param {Object} el
	 * @param {Object} params
	 */
	getReportConfig: function(){
		var params = this.initParams;
		if (!$L.isObject(this.reportConfig)) {
			var reportConfig = $U.cloneObject(this.DEFAULT_REPORT_CONFIG);
			/**
			 * Augmenting config supplied with default config
			 */
			if ($L.isObject(params.reportconfig)) {
				$L.augmentObject(reportConfig, params.reportconfig, true);
			}
			if (reportConfig.paginator) {
				$D.addClass(this.reportElement, "paginated");
			}
			this.reportConfig = reportConfig;
		}
		return this.reportConfig;
	},
	/**
	 * Triggered when a checkbox is clicked on the report.By default applies true/false to the record value
	 * based on the checkbox value.
	 * @param {Object} oArgs
	 */
	onCheckBoxClick: function(oArgs){
		var elCheckbox = oArgs.target;
		var oRecord = this.getRecord(elCheckbox);
		var sColKey = this.getColumn(elCheckbox).getKey();
		this.getRecordSet().updateRecordValue(oRecord, sColKey, elCheckbox.checked);
	},
	/**
	 * Renders the Header of the Reports and attaches listeners to the elements in the header
	 */
	_renderHeader: function(){
		var params = this.initParams;
		var oListener = new $U.DOMEventHandler($D.getElementsByClassName("item-border", null, this.reportElement), {
			type: "click"
		});
		if (this.attrs.get($W.Report.ATTR_SELECT_ENABLED)) {
			$D.addClass(this.reportElement, "select-enabled");
			var elSelectAll = $D.getElementsByClassName("select-all", null, this.reportElement);
			var elSelectNone = $D.getElementsByClassName("select-none", null, this.reportElement);
			var elSelectPage = $D.getElementsByClassName("select-page", null, this.reportElement);
			oListener.addListener(elSelectAll, function(oArgs, oSelf){
				oSelf.bulkUpdateRecords($W.Report.KEY_SELECT, true);
				oSelf.render();
				$E.stopEvent(oArgs);
			}, this, true);
			oListener.addListener(elSelectNone, function(oArgs, oSelf){
				oSelf.bulkUpdateRecords($W.Report.KEY_SELECT, false);
				oSelf.render();
				$E.stopEvent(oArgs);
			}, this, true);
			oListener.addListener(elSelectPage, function(oArgs, oSelf){
				var paginator = oSelf.get('paginator');
				this.bulkUpdateRecords($W.Report.KEY_SELECT, false);
				var startIndex = paginator.getStartIndex();
				var numRecords = paginator.getRowsPerPage();
				var aRecords = oSelf.getRecordSet().getRecords(startIndex, numRecords);
				oSelf.bulkUpdateRecords($W.Report.KEY_SELECT, true, aRecords);
				oSelf.render();
				$E.stopEvent(oArgs);
			}, this, true);
			elSelectAll = null;
			elSelectNone = null;
			elSelectPage = null;
		}
		/**
		 * If Print Preview is enabled ,add Listeners for the elements
		 */
		if (this.attrs.get($W.Report.ATTR_PRINT_ENABLED)) {
			$E.addListener($D.getElementsByClassName("print-preview", null, this.reportElement), "click", function(oArgs, oSelf){
				oSelf.printReport();
			}, this, true);
		}
		/**
		 * Avoiding possible memory leaks.
		 */
		params = null;
		oListener = null;
	},
	/**
	 * Overridable Function to show a print preview current report in a popup. Generates the markup ina buffer element
	 * whose display is set to none.
	 */
	printReport: function(){
		var sReportName = "report";
		var elBufferReport = $D.getElementsByClassName("print-section", null, this.reportElement)[0];
		var elPrintContent = $D.getElementsByClassName("print-skin-template", null, this.baseElement)[0];
		var sBufferTemplateContent = $U.processTemplate(elPrintContent.innerHTML, {
			reportname: sReportName
		});
		if (this.attrs.get($W.Report.ATTR_SELECT_ENABLED)) {
			this.generateSelectedPrintableDataTable(elBufferReport);
		}
		else {
			this.generatePrintableDataTable(elBufferReport);
		}
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
	},
	setReportContent: function(elBufferReport, sReportName, tempBufferReport){
		var elReportContent = $D.getElementsByClassName(sReportName, null, elBufferReport)[0];
		elReportContent.innerHTML += tempBufferReport;
	},
	/**
	 * Returns a String that will be passes as options for opening the popup.
	 */
	getPrintReportPopupOptions: function(){
		return "height=700px,width=1000px,location=0,status=0,resizable=0,toolbar=0,menubar=0,titlebar=0,scrollbars=1";
	},
	/**
	 * Returns an array of stylesheet links required for print preview.
	 */
	getPrintReportStyleSheets: function(){
		return _publish.report.stylesheets;
	},
	/**
	 * Returns an array of script links/inline scripts required for print preview.
	 */
	getPrintReportScripts: function(){
		return [""];
	},
	/**
	 * Gives the HTML String representing the report's Header. All the extending Reports
	 * can override this to get the custom Header
	 */
	getPrintReportHeader: function(){
		return "";
	},
	/**
	 * Gives the HTML String representing the report's Footer
	 */
	getPrintReportFooter: function(){
		return "";
	},
	/**
	 * Overridable Function to generate a print preview of the current report in a temporary buffer element
	 */
	generatePrintableDataTable: function(elBufferReport){
		var oBufferConfig = $U.cloneObject(this._oReportConfig);
		oBufferConfig.paginator = null;
		var bufferDataTable = new $YW.DataTable(elBufferReport, this._oColumnDefs, this._oReportDataSource, oBufferConfig);
	},
	/**
	 * Overridable Function to generate a print preview of the current report with just the selected records
	 * in a temporary buffer element
	 * @param {Object} elBufferReport
	 */
	generateSelectedPrintableDataTable: function(elBufferReport){
		var oBufferConfig = $U.cloneObject(this._oReportConfig);
		oBufferConfig.paginator = null;
		var oBufferDataSource = new $YU.DataSource({});
		oBufferDataSource.responseSchema = this._oReportDataSource.responseSchema;
		oBufferDataSource.responseType = this._oReportDataSource.responseType;
		var bufferDataTable = new $YW.DataTable(elBufferReport, this._oColumnDefs, oBufferDataSource, oBufferConfig);
		bufferDataTable.getRecordSet().deleteRecords(0);
		bufferDataTable.getRecordSet().addRecords(this._getSelectedRecordsData());
		bufferDataTable.render();
	},
	/**
	 * Returns a list of selected record's data;
	 */
	_getSelectedRecordsData: function(){
		var aResultRecords = [];
		var aRecords = this.getRecordSet().getRecords();
		for (var i = 0; i < aRecords.length; i++) {
			if (aRecords[i].getData($W.Report.KEY_SELECT)) {
				aResultRecords.push(aRecords[i].getData());
			}
		}
		return aResultRecords;
	},
	/**
	 * Returns a list of selected record's ;
	 */
	_getSelectedRecords: function(){
		var aResultRecords = [];
		var aRecords = this.getRecordSet().getRecords();
		for (var i = 0; i < aRecords.length; i++) {
			if (aRecords[i].getData($W.Report.KEY_SELECT)) {
				aResultRecords.push(aRecords[i]);
			}
		}
		return aResultRecords;
	},
	/**
	 * Configures Option Attributes
	 */
	_configureOptionAttributes: function(){
		/**
		 * @attribute selectEnabled
		 * @description If Select Functionality is enabled or disabled
		 * @type boolean
		 */
		this.attrs.setAttributeConfig($W.Report.ATTR_SELECT_ENABLED, {
			value: false,
			validator: $L.isBoolean
		});
		/**
		 * @attribute selectAllEnabled
		 * @description If All the records are to be selected by default
		 * @type boolean
		 */
		this.attrs.setAttributeConfig($W.Report.ATTR_SELECT_ALL_ENABLED, {
			value: false,
			validator: $L.isBoolean
		});
		/**
		 * @attribute groupingEnabled
		 * @description If grouping is to be enabled/disabled
		 * @type boolean
		 */
		this.attrs.setAttributeConfig($W.Report.ATTR_GROUPING_ENABLED, {
			value: false,
			validator: $L.isBoolean
		});
		/**
		 * @attribute deleteEnabled
		 * @description If Delete Functionality is enabled or disabled
		 * @type boolean
		 */
		this.attrs.setAttributeConfig($W.Report.ATTR_PRINT_ENABLED, {
			value: false,
			validator: $L.isBoolean
		});
	},
	/**
	 * Parses Options in params and applies values to necessary attributes
	 */
	_parseOptions: function(){
		var oOptions = this.initParams.options;
		if ($L.isObject(oOptions)) {
			if (oOptions.select) {
				if (oOptions.select.enabled) {
					this.attrs.set($W.Report.ATTR_SELECT_ENABLED, true);
					if (oOptions.select.selected) {
						this.attrs.set($W.Report.ATTR_SELECT_ALL_ENABLED, true);
					}
				}
			}
			if (oOptions.grouped) {
				this.attrs.set($W.Report.ATTR_GROUPING_ENABLED, true);
			}
			if (oOptions.print) {
				if (oOptions.print.enabled) {
					this.attrs.set($W.Report.ATTR_PRINT_ENABLED, true);
				}
			}
		}
	},
	/**
	 * Processes stuffs necessary after initializing superclass datatable
	 */
	_processDataTable: function(){
		/**
		 * It seems YUI forgot to create all the events for RecordSet and DataTable.
		 * Till the bug is resolved.Manually creating the event
		 */
		this.getRecordSet().subscribe("recordValueUpdateEvent", function(oArgs){
			if ($W.Report.KEY_SELECT == oArgs.key) {
				if (oArgs.newData) {
					this.selectRow(this.getTrEl(oArgs.record));
				}
				else {
					this.unselectRow(this.getTrEl(oArgs.record));
				}
			}
		}, this, true);
		if (this.attrs.get($W.Report.ATTR_SELECT_ALL_ENABLED)) {
			this.bulkUpdateRecords($W.Report.KEY_SELECT, true);
		}
	},
	/**
	 * Useful for bulk updates of a field in the datatable's recordeset
	 * @param {Object} key
	 * @param {Object} value
	 * @param {Object} records
	 */
	bulkUpdateRecords: function(key, value, records){
		if (!$L.isArray(records)) {
			records = this.getRecordSet().getRecords();
		}
		for (var i = 0; i < records.length; i++) {
			this.getRecordSet().updateRecordValue(records[i], key, value);
		}
	},
	/**
	 * Returns the ColumnDefs necessary based on options passed
	 * @param {Object} params
	 */
	_getOptionConfig: function(params){
		var optionConfigs = [];
		if (this.attrs.get($W.Report.ATTR_SELECT_ENABLED)) {
			optionConfigs.push({
				key: $W.Report.KEY_SELECT,
				label: "select",
				formatter: "checkbox"
			});
		}
		return optionConfigs;
	}
	});
})();