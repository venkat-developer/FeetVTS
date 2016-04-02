(function() {
	var $B = YAHOO.Bubbling;
	var $L = YAHOO.lang;
	var $YU = YAHOO.util;
	var $E = YAHOO.util.Event;
	var $D = YAHOO.util.Dom;
	var $YW = YAHOO.widget;
	var $U = getPackageForName("com.i10n.fleet.Utils");
	var $W = getPackageForName("com.i10n.fleet.widget.ui");
	var $V = getPackageForName("com.i10n.fleet.widget.view");
	var report = null;
	/**
	 * UpdatableReports Widget for Fleetcheck project that need to be Updatable.
	 * 
	 * @extends com.i10n.fleet.widget.ui.UpdatableReport All the users will pass
	 *          the config as params while initializing. Required Structure of
	 *          params params = { reportconfig : <doc>Will contain
	 *          overrides/additions to config that will passes on the YI
	 *          Datatable</doc>, options : { select : { enabled : <doc>Enable
	 *          adddition of select checkboxes as the first column in each row</doc>,
	 *          selected : <doc>If true will select all the records by default</doc> } },
	 *          columndefs : [ <doc>Array containing columndefs required by the
	 *          Datatable. If select is enabled. This columndefs must have a
	 *          filed with key as id</doc> ] }
	 * 
	 * @author sabarish
	 */
	$W.UpdatableReport = function(el, params) {
		this.baseElement = el;
		this.initParams = params;
		this.initUpdatableReports(el, params);
	};
	/**
	 * Augmenting Static variables
	 */
	$L.augmentObject($W.UpdatableReport, {
		EVT_DATA_UPDATE : "onDataUpdate"
	});
	$L.extend($W.UpdatableReport, $W.Report, {
		initAttributes : function(oConfigs) {
			$W.UpdatableReport.superclass.initAttributes.call(this, oConfigs);
			this.createEvent($W.UpdatableReport.EVT_DATA_UPDATE);
		},
		_initRecordSet : function() {
			if (this._oRecordSet) {
				this._oRecordSet.reset();
			} else {
				this._oRecordSet = new $W.UpdatableRecordSet();
			}
		}
	});
	$L.augmentObject($W.UpdatableReport.prototype, {
		/**
		 * Initializes the UpdatableReports Widget
		 * 
		 * @param {Object}
		 *            el
		 * @param {Object}
		 *            params
		 */
		
		initUpdatableReports : function(el, params) {
			$W.UpdatableReport.superclass.constructor.call(this, el, params);
			this._addListeners();
		},
		oSaveCallback : {
			success : function(o) {
			},
			failure : function(o) {
			}
		},
		_addListeners : function() {

			if (this._oDataSource) {
				/**
				 * TODO : Performance Optimizations required. When there will be
				 * huge no. of records here. update will be slow. Instead only
				 * the difference should be handled.
				 */
				this.oSaveCallback.scope = this;
				this._oDataSource.subscribe($W.UpdatableReport.EVT_ON_ADD_DATA, this._updateDataReport, this, true);
				this._oDataSource.subscribe( $W.UpdatableReport.EVT_ON_REMOVE_DATA, this._updateDataReport, this, true);
			}
			if (this._oRecordSet) {
				this._oRecordSet.subscribe("recordValueUpdateEvent", function(oArgs) {
					var sKey = oArgs.key;
					var oData = oArgs.record.getData();
									
					if((oData.overspeeding == true || oData.geofencing == true || oData.chargerdisconnected == true) && (oData.email != null)){
						report = "alertsettings";
					} else if(oData.mobilenumber != null){
						report = "mobilealertsettings";
					} else {	
						report = "reportsettings";
					}
						
					var oDataItem = this._oDataSource.getItem(oData);
					if (!$L.isNull(oDataItem[sKey]) && "undefined" !== typeof (oDataItem[sKey])) {
						oDataItem[sKey] = oData[sKey];
						this._oDataSource.updateItem(oDataItem);
					}
				}, this, true);
			}
			var button = $D.getElementsByClassName("button-update-report", null, this.reportElement);
			$W.Buttons.addDefaultHandler(button, function(params) {
				var oData = this._oDataSource.getChanges();
				this.fireEvent($W.UpdatableReport.EVT_DATA_UPDATE, oData);

				$U.Connect.asyncRequest('GET', "/fleet/form/"+report+"/?addeddata="
					+ oData.add.toSource() +"&addeddatalen="+ oData.add.length +"&removeddata="
					+ oData.del.toSource() +"&removeddatalen="+ oData.del.length +"&updateddata="
					+ oData.updates.toSource() +"&updateddatalen="+ oData.updates.length +"&localTime="+ $U.getLocalTime(), this.oSaveCallback);
				this._oDataSource.resetUpdates();
			}, null, this);
			var elSelectdelete = $D.getElementsByClassName("select-delete", null, this.reportElement);
			$E.addListener(elSelectdelete, "click", function(oArgs) {
				var oData = [];
				var aSelectedData = this._getSelectedRecordsData();
				this._oDataSource.removeItems(aSelectedData);
			}, this, true);
			elSelectdelete = null;
			button = null;
		},
		_updateDataReport : function(oArgs) {
			this._oDataSource.sendRequest(null, {
				success : this.onDataReturnInitializeTable,
				failure : this.onDataReturnInitializeTable,
				scope : this,
				argument : this.getState()
			});
		}
	});

	$W.UpdatableRecordSet = function(oData) {
		$W.UpdatableRecordSet.superclass.constructor.call(this, oData);

		/**
		 * The following events are supposed to be created by YUI RecordSet. But
		 * it seems the YUI RecordSet has a bug that does not create these
		 * events. Hence till the YUI bug is resolved. These events will be
		 * manually created.
		 */
		this.createEvent("recordAddEvent");
		this.createEvent("recordDeleteEvent");
		this.createEvent("recordValueUpdateEvent");
	};

	$L.extend($W.UpdatableRecordSet, $YW.RecordSet);

	$W.UpdatableReport.UpdatableDataSource = function(oData, oArgs) {

		this._sDataId = "id";
		if (oArgs && oArgs.idKey) {
			this._sDataId = oArgs.idKey;
		}
		this._oData = oData;
		this._addedRecords = {};
		this._updatedRecords = {};
		this._deletedRecords = {};
		$W.UpdatableReport.UpdatableDataSource.superclass.constructor.call(this, this.getValues);
		/**
		 * Creating Events
		 */
		/**
		 * Fired when a data is added to the datasource.
		 * @event onDataAddition
		 * @param oArgs.data
		 *            {Array} The added data.
		 */
		this.createEvent($W.UpdatableReport.EVT_ON_ADD_DATA);
		/**
		 * Fired when a data is removed from the datasource.
		 * 
		 * @event onDataRemoval
		 * @param oArgs.data
		 *            {Array} The removed data.
		 */
		this.createEvent($W.UpdatableReport.EVT_ON_REMOVE_DATA);
		/**
		 * Fired when a data is updated in the datasource.
		 * 
		 * @event onDataUpdate
		 * @param oArgs.data
		 *            {Array} The updated data.
		 */
		this.createEvent($W.UpdatableReport.EVT_ON_UPDATE_DATA);
	};
	$L.augmentObject($W.UpdatableReport, {
		EVT_ON_ADD_DATA : "onDataAddition",
		EVT_ON_REMOVE_DATA : "onDataRemoval",
		EVT_ON_UPDATE_DATA : "onDataUpdate"
	});
	$L.extend($W.UpdatableReport.UpdatableDataSource, $YU.FunctionDataSource);

	$L.augmentObject($W.UpdatableReport.UpdatableDataSource.prototype, {
		addItem : function(oData, sId) {
			if (!sId) {
				sId = oData[this._sDataId];
			}
			this._oData[sId] = oData;
			this._addedRecords[sId] = oData;
			this.fireEvent($W.UpdatableReport.EVT_ON_REMOVE_DATA, {
				data : [ oData ] 
			});
		},
		addItems : function(aData) {
			var aArgs = [];
			for ( var i = 0; i < aData.length; i++) {
				var sId = aData[i][this._sDataId];
				this._oData[sId] = aData[i];
				this._addedRecords[sId] = this._oData[sId];
				aArgs.push(aData[i]);
			}
			this.fireEvent($W.UpdatableReport.EVT_ON_REMOVE_DATA, {
				data : aArgs
			});
		},
		removeItem : function(oData, sId) {
			if (!sId) {
				sId = oData[this._sDataId];
			}
			this._oData[sId] = null;
			if (this._addedRecords[sId]) {
				delete (this._addedRecords[sId]);
			} else {
				if (this._updatedRecords[sId]) {
					delete (this._updatedRecords[sId]);
				}
				this._deletedRecords[sId] = oData;
			}
			this.fireEvent($W.UpdatableReport.EVT_ON_REMOVE_DATA, {
				data : [ oData ]
			});
		},
		removeItems : function(aData) {
			var aArgs = [];
			for ( var i = 0; i < aData.length; i++) {
				var sId = aData[i][this._sDataId];
				var oData = this._oData[sId];
				this._oData[sId] = null;
				if (this._addedRecords[sId]) {
					delete (this._addedRecords[sId]);
				} else {
					if (this._updatedRecords[sId]) {
						delete (this._updatedRecords[sId]);
					}
					this._deletedRecords[sId] = oData;
				}
				aArgs.push(oData);
			}
			this.fireEvent($W.UpdatableReport.EVT_ON_REMOVE_DATA, {
				data : aArgs
			});
		},
		updateItem : function(oData, sId) {
			if (!sId) {
				sId = oData[this._sDataId];
			}
			this._oData[sId] = oData;
			if (this._addedRecords[sId]) {
				this._addedRecords[sId] = oData;
			} else {
				this._updatedRecords[sId] = oData;
			}
			this.fireEvent($W.UpdatableReport.EVT_ON_UPDATE_DATA, {
				data : [ oData ]
			});
		},
		getItem : function(oArgs) {
			var sId = oArgs;
			if (!$L.isString(oArgs)) {
				sId = oArgs[this._sDataId];
			}
			return this._oData[sId];
		},
		getValues : function(sQuery) {
			var _oMatches = [];
			if (sQuery) {
				var aData = sQuery.split("=");
				var sKey = aData[0];
				var sValue = aData[1];
				if (sKey && sValue) {
					var oFilter = {
						content : {}
					};
					oFilter.content[sKey] = sValue;
					_oMatches = $U.Arrays.mapToArray(this._oData, oFilter);
				} else {
					_oMatches = $U.Arrays.mapToArray(this._oData);
				}
			} else {
				_oMatches = $U.Arrays.mapToArray(this._oData);
			}
			return _oMatches;
		},
		resetUpdates : function() {
			this._addedRecords = {};
			this._updatedRecords = {};
			this._deletedRecords = {};
		},

		/**
		 * Returns the groupData for the datasource
		 */
		getData : function() {
			return this._oData;
		},
		getChanges : function() {
			var oData = {};
			oData.add = $U.Arrays.mapToArray(this._addedRecords);
			oData.del = $U.Arrays.mapToArray(this._deletedRecords);
			oData.updates = $U.Arrays.mapToArray(this._updatedRecords);

			return oData;
		}

	});
})();
