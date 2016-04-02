
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
    /**
     * A Custom Report that handles Groups of Data and filtering the data based on group
     *
     * @extends $W.Report
     * @param {Object} el
     * @param {Object} oArgs
     * @author sabarish
     */
    $W.GroupReportViolation= function(el, oArgs) {
    	
    	
        this._oGroupedDataSource = null;
        this.initGroupedReport(el, oArgs);

       
    };
    
    
    $E.addListener($D.getElementsByClassName("select-violation"),"change",function(oArgs){
		var text=$D.getElementsByClassName("select-violation")[0].value; 
		if(text=="Overspeed"){
				$D.removeClass($D.getElementsByClassName("select-overspeedalertvalue"),"disabled");			
		}else{			
			var d=document.getElementById("overspeedvalue");
			d.value=0;
			d.className = d.className + " disabled";
		}	

		if(text=="Idle"){
			$D.removeClass($D.getElementsByClassName("select-idlealertvalue"),"disabled");			
		}else{				
			var d=document.getElementById("idlevalue");
			d.value=0;
			d.className = d.className + " disabled";
		}		
	});
    
    
    /**
     * Static Functions/Variable
     */
    $L.augmentObject($W.GroupReportViolation, {
        ATTR_GROUP: "group",
        EVT_GROUP_CHANGE: "groupChange"
    });
    $L.extend($W.GroupReportViolation, $W.Report, {
        /**
         * Overrides initAttributes to add Group Based Attributes
         * @param {Object} oConfigs
         */
        initAttributes: function(oConfigs) {
    	
            $W.GroupReportViolation.superclass.initAttributes.call(this, oConfigs);
            /**
             * @attribute group
             * @description Current Group Selected
             * @type String
             */
            this.setAttributeConfig($W.GroupReportViolation.ATTR_GROUP, {
                value: null,
                validator: function(value) {
                    return ($L.isNull(value) || $L.isString(value));
                }
            });
        },
        showComments:function(checked){
        },
       /* showAlertPopUp: function(){
        	
        	 var alert = $D.getElementsByClassName("comments");
        	   $D.removeClass(alert,"disabled");
        	   console.log(alert);
        	   console.log("under show popup");
        	   $W.GroupedReport.TripCreationPopUp.render();
        	   $W.GroupedReport.TripCreationPopUp.show();
        	   console.log("end of show pop up");
        },*/
        /**
         * Overridden function to generate printable report
         * @param {Object} elBufferReport
         */
        generatePrintableDataTable: function(elBufferReport) {
            var oBufferConfig = $U.cloneObject(this._oReportConfig);
            oBufferConfig.paginator = null;
            var sGroupId = this.get($W.GroupReportViolation.ATTR_GROUP);
            if ($L.isString(sGroupId)) {
                oBufferConfig.initialRequest = "group=" + sGroupId + "&selected=true";
            }
            var bufferDataTable = new $YW.DataTable(elBufferReport, this._oColumnDefs, this._oReportDataSource, oBufferConfig);
        }
    });
    $L.augmentObject($W.GroupReportViolation.prototype, {
        /**
         * Initializes the Grouped Reports
         * @param {Object} el
         * @param {Object} oArgs
         */
        initGroupedReport: function(el, oArgs) {
    	
            var oDataSource = oArgs.datasource;
            if (oDataSource instanceof $W.GroupReportViolation.GroupedDataSource) {
                this._oGroupedDataSource = oDataSource;
                var oGroupData = oDataSource.getGroupData();
                var elGroupSelectContainers = $D.getElementsByClassName("group-select", null, el);
                for (var i = 0; i < elGroupSelectContainers.length; i++) {
                    var elGroupSelect = document.createElement("select");
                    var elGroupAllOption = document.createElement("option");
                    elGroupAllOption.innerHTML = "All";
                    elGroupSelect.appendChild(elGroupAllOption);
                    for (var sGroupId in oGroupData) {
                        var sTitleId = oArgs.options.groups.titleId;
                        var sGroupName = oGroupData[sGroupId][sTitleId];
                        var elGroupOption = document.createElement("option");
                        elGroupOption.value = sGroupId;
                        elGroupOption.innerHTML = sGroupName;
                        elGroupSelect.appendChild(elGroupOption);
                    }
                    elGroupSelectContainers[i].appendChild(elGroupSelect);
                    $E.addListener(elGroupSelect, "change", function(oArgs, oSelf) {
                        var elTarget = $E.getTarget(oArgs);
                        var sValue = elTarget.value;
                        oSelf.set("group", sValue);
                    }, this, true);
                }
                oGroupData = null;
                elGroupSelectContainers = null;
               
                   /* var elStatusSelectContainers = $D.getElementsByClassName("select-status", null, el);
                    for (var i = 0; i < elStatusSelectContainers.length; i++) {
                        var elGroupSelect = document.createElement("select");
                        var elGroupAllOption = document.createElement("option");
                        
                        for (var sGroupId in oGroupData) {
                            var sTitleId = oArgs.options.groups.titleId;
                            var sGroupName = oGroupData[sGroupId][sTitleId];
                            var elGroupOption = document.createElement("option");
                            elGroupOption.value = sGroupId;
                            elGroupOption.innerHTML = sGroupName;
                            elGroupSelect.appendChild(elGroupOption);
                        }
                        elStatusSelectContainers[i].appendChild(elGroupSelect);
                        $E.addListener(elGroupSelect, "change", function(oArgs, oSelf) {
                            var elTarget = $E.getTarget(oArgs);
                            var sValue = elTarget.value;
                            alert("thisis select"+sValue)
                            oSelf.set("group", sValue);
                        }, this, true);
                }
                */
            
            }
            $W.GroupReportViolation.superclass.constructor.call(this, el, oArgs);
            this.subscribe($W.GroupReportViolation.EVT_GROUP_CHANGE, this._onGroupChange, this, true);
            oDataSource.subscribe($W.GroupReportViolation.GroupedDataSource.EVT_ON_UPDATE, function(oArgs) {
                var sGroupID = this.get("group");
                this.updateGroupData(sGroupID);
            }, this, true);
            /**
             * Avoiding possible memory leaks.
             */
            oDataSource = null;
        },
        /**
         * Listener for handling group change
         * @param {Object} oArgs
         */
        _onGroupChange: function(oArgs) {
            var sValue = oArgs.newValue;
            this.updateGroupData(sValue);
        },
        /**
         * updates the current table with the group data.
         * @param {Object} sGroupId
         */
        updateGroupData: function(sGroupId) {
            var oCallback = {
                success: this.onDataReturnInitializeTable,
                failure: this.onDataReturnInitializeTable,
                scope: this,
                argument: this.getState()
            };
            this._oGroupedDataSource.sendRequest("group=" + sGroupId, oCallback);
        }
    });
    /**
     * A DataSource representing a Group of Items. necessary for GroupedReport to function.
     * Structure followed should be of following format
     * {
     *     <repeat>
     *     "{groupId}" : {
     *         "{groupTitleId}" : titleName
     *         "{groupElementId}" : {
     *            <doc>A Map of Elements</doc>
     *         }
     *     }
     *     </repeat>
     * }
     * @param {Object} oData
     */
    $W.GroupReportViolation.GroupedDataSource = function(oData) {
        this.groupData = oData;
        this.grpData=oData;
        $W.GroupReportViolation.GroupedDataSource.superclass.constructor.call(this, this.groupSearch);
        this.createEvent($W.GroupReportViolation.GroupedDataSource.EVT_ON_UPDATE);
    };
    $L.extend($W.GroupReportViolation.GroupedDataSource, $YU.FunctionDataSource);
    $L.augmentObject($W.GroupReportViolation.GroupedDataSource, {
        EVT_ON_UPDATE: "onUpdate"
    });
    $L.augmentObject($W.GroupReportViolation.GroupedDataSource.prototype, {
        /**
         * Custom Search Function used by the DataSource to filter out records based on Group
         * @param {Object} sQuery
         */
        groupSearch: function(sQuery) {
            var aReturnData = [];
            var oGroupData = this.groupData;
            var oParsedQuery = this._parseQuery(sQuery);
            var sGroupId = oParsedQuery.group;
            if (this.groupData[sGroupId]) {
                oGroupData = {
                    "group": this.groupData[sGroupId]
                };
            }
            var groupId = null;
            if (oParsedQuery.search_key) {
                var searchValue = oParsedQuery[oParsedQuery.search_key];
                for (groupId in oGroupData) {
                    aReturnData = this._getMatchedGroupItem(oGroupData[groupId], oParsedQuery.search_key, searchValue, aReturnData);
                }
                return aReturnData;
            }
            else {
                for (groupId in oGroupData) {
                    aReturnData = this._getGroupItem(oGroupData[groupId], aReturnData);
                }
                return aReturnData;
            }
        },
        /**
         * Parses the QueryString passed , and returns an object (key : value pair) representing the querystring
         * @param {Object} sQuery
         */
        _parseQuery: function(sQuery) {
            var oParsedResult = {};
            if ($L.isString(sQuery) && sQuery.length > 0) {
                var aOptions = sQuery.split('&');
                for (var i = 0; i < aOptions.length; i++) {
                    var sOption = aOptions[i];
                    var splitIndex = sOption.indexOf('=');
                    var sOptionKey = sOption.substring(0, splitIndex);
                    var sOptionValue = sOption.substring(splitIndex + 1);
                    oParsedResult[sOptionKey] = sOptionValue;
                }
            }
            return oParsedResult;
        },
        /**
         * Appends all items that matches the tomatch string in the oGroupItem to the array aItems passed.
         * @param {Object} oGroupItem
         * @param {Object} tomatch
         * @param {Object} aItems
         */
        _getMatchedGroupItem: function(oGroupItem, search_key, searchTarget, aItems) {
            var sElementId = this.responseSchema.elementField;
            if (oGroupItem[sElementId]) {
                for (var sItemId in oGroupItem[sElementId]) {
                    var searchValue = oGroupItem[sElementId][sItemId][search_key];
                    if ((searchValue.toLowerCase().indexOf(searchTarget) === 0)) {
                        aItems.push(searchValue);
                    }
                }
            }
            return aItems;
        },
        /**
         * Appends all the items in the oGroupItem to the array aItems passed.
         * @param {Object} oGroupItem
         * @param {Object} aItems
         */
        _getGroupItem: function(oGroupItem, aItems) {
            var count = aItems.length + 1;
            var sElementId = this.responseSchema.elementField;
            if (oGroupItem[sElementId]) {
                for (var sItemId in oGroupItem[sElementId]) {
                    var oItem = $U.cloneObject(oGroupItem[sElementId][sItemId]);
                    oItem.id = sItemId;
                    oItem.index = count;
                    aItems.push(oItem);
                    count++;
                }
            }
            return aItems;
        },
        /**
         * Returns the groupData for the datasource
         */
        getGroupData: function() {
            return this.groupData;
        },
        /**
         * sets the group data to the new data so that next request has the latest data.
         * @param {Object} oData
         */
        setGroupData: function(oData) {
            this.groupData = oData;
            this.fireEvent($W.GroupReportViolation.GroupedDataSource.EVT_ON_UPDATE, oData);
        },
        /**
         * Returns the groupData for the datasource
         */
        getGroupDataForAutocomplete: function() {
            return this.grpData;
        },
        /**
         * sets the group data to the new data so that next request has the latest data.
         * @param {Object} oData
         */
        setGroupDataForAutocomplete: function(oData) {
            this.grpData = oData;
            
        }
    });
  
})();
