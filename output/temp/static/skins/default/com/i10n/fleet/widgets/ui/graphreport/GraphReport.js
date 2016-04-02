(function() {
    var $B = YAHOO.Bubbling;
    var $L = YAHOO.lang;
    var $YU = YAHOO.util;
    var $E = YAHOO.util.Event;
    var $D = YAHOO.util.Dom;
    var $YW = YAHOO.widget;
    var $W = getPackageForName("com.i10n.fleet.widget.ui");
    var $U = getPackageForName("com.i10n.fleet.Utils");
    var $WL = getPackageForName("com.i10n.fleet.widget.lib");
	var Data;
	
	// Commenting this line for KP client
	google.load("visualization", "1", {packages:["corechart"]});

	
    /**
     * The graph Report genric widget
     *
     * @author N.Balaji
     * @update dharmaraju.v , Hemant
     */
    $W.GraphReport = function(el, oArgs) {
        $W.GraphReport.superclass.constructor.call(this, el, oArgs);
        this.initGraphReport(el, oArgs);
    };
    $L.extend($W.GraphReport, $W.BaseReport, {
        render: function() {
            var oData = this.get($W.BaseReport.ATTR_DATA);
			if(oData.toSource()!= null){
				this.setData(oData);
				// For KP client use only this block commenting out the next block and uncommenting this block
				/*{
					this.drawBirtGraph();
				}*/
				
				// For Non KP Client use this block
				{
					try{
						if(oData.type == "Speed" || oData.type == "Fuel" || oData.type == "Driver"){
							if(oData.interactiveGraph == "nodata"){
								this.drawBirtGraph();
							}else{
								this.drawInteractiveGraph();
							}
						} else {
							this.drawBirtGraph();
						}
					}catch(err){

					}
				}
            }
        },
		
        drawInteractiveGraph: function(){
        	var data = this.getData();
			var chart;
			if(data.type == "Speed"){
				chart = new google.visualization.LineChart(document.getElementsByClassName('graph-content')[1]);
			}else if(data.type == "Fuel"){
				chart = new google.visualization.LineChart(document.getElementsByClassName('graph-content')[0]);
			}else if(data.type == "Driver"){
				chart = new google.visualization.LineChart(document.getElementsByClassName('graph-content')[2]);
			}
			this.drawChart(data, chart);
        },
        
        drawBirtGraph: function(){
			var data = this.getData();
			try{
				if(data.type == "Fuel"){
					var baseElement = $D.getElementsByClassName('graph-content', null, this.elBase)[0];
					$D.setStyle(baseElement, 'background-image', "url(" + data.graphurl + ")");
					var el = $D.getElementsByClassName('selected-item', null, this.elBase)[0];
					el.innerHTML = "- " + data.key;
				} else if(data.type == "Speed"){
					var baseElement = $D.getElementsByClassName('graph-content', null, this.elBase)[0];
					$D.setStyle(baseElement, 'background-image', "url(" + data.graphurl + ")");
	                var el = $D.getElementsByClassName('selected-item', null, this.elBase)[1];
	                el.innerHTML = "- " + data.key;
				} else if(data.type == "Driver"){
					var baseElement = $D.getElementsByClassName('graph-content', null, this.elBase)[0];
					$D.setStyle(baseElement, 'background-image', "url(" + data.graphurl + ")");
	                var el = $D.getElementsByClassName('selected-item', null, this.elBase)[2];
	                el.innerHTML = "- " + data.key;
				} else {
					var baseElement = $D.getElementsByClassName('graph-content', null, this.elBase)[0];
					$D.setStyle(baseElement, 'background-image', "url(" + data.graphurl + ")");
	                var el = $D.getElementsByClassName('selected-item', null, this.elBase)[3];
	                el.innerHTML = "- " + data.key;
				}
			}catch(err){
//				console.debug('catching error while drawing birt graph');
			}
			
        },

        setData: function(oData){
			this.Data=oData;
		},
		
		getData: function(){
			return this.Data;
		},
			      
		drawChart: function(oData, chart){
			
			var type = oData.type;
			oData = oData.interactiveGraph;
			var data = new google.visualization.DataTable();
	        data.addColumn('datetime', 'Date');
	        data.addColumn('number', type);
	        for(var i in oData){
	        	var dataBackup = oData[i];
	        	var value = dataBackup.split(":");
	        	var typeValue = value[0];
	        	var stringDate = value[1];
	        	var splitDate = stringDate.split(",");
				var hours = parseInt(splitDate[3]);
                if(splitDate[3] == "08" ){
                	hours = 8;
                }else if(splitDate[3] == "09"){
                	hours = 9;
                }
                var minutes = parseInt(splitDate[4]);
				if(splitDate[4] == "08" ){
                	minutes = 8;
                }else if(splitDate[4] == "09"){
                	minutes = 9;
                }
                var seconds = parseInt(splitDate[5]);
                if(splitDate[5] == "08" ){
                	seconds = 8;
                }else if(splitDate[5] == "09"){
                	seconds = 9;
                }
                var milliSeconds = parseInt(splitDate[6]);
                if(splitDate[6] == "08" ){
                	milliSeconds = 8;
                }else if(splitDate[6] == "09"){
                	milliSeconds = 9;
                }
                var month = parseInt(splitDate[1]);
                if(splitDate[1] == "08"){
                	month = 8;
                } else if(splitDate[1] == "09"){
                	month = 9;
                }
                var dateValue = parseInt(splitDate[2]);
                if(splitDate[2] == "08"){
                	dateValue = 8;
                } else if(splitDate[2] == "09"){
                	dateValue = 9;
                }
				data.addRow([new Date(parseInt(splitDate[0]),(month-1),dateValue, hours, minutes, seconds,milliSeconds),
				             parseFloat(typeValue)]);
	        }
	        chart.draw(data, {displayAnnotations: true});
			      
		},
        /**
         * Destroying current UI to make way for a new one.
         */
        destroy: function() {
        }
    });
    $L.augmentObject($W.GraphReport.prototype, {
        /**
         * Initializes Graph Report.
         * @param {Object} el
         * @param {Object} oArgs
         */
        initGraphReport: function(el, oArgs) {
            var aElButtons = $D.getElementsByClassName('refresh', null, this.elBase)[0];
            $W.Buttons.addDefaultHandler(aElButtons, function(oArgs) {
                this.update();
            }, null, this);
            var elPrintPreview = $D.getElementsByClassName("print-preview", null, this.elBase)[0];
            $E.addListener(elPrintPreview, "click", function() {
                this.printGraphReport();
            }, null, this);
            aElButtons = null;
            elPrintPreview = null;
        },
        /**
         * Sets the content the report
         * @param {Object} sReportName
         * @param {Object} elBufferReport
         */
        _setReportContent: function(sReportName, elBufferReport) {
            var elReportContent = $D.getElementsByClassName(sReportName, null, elBufferReport)[0];
            elReportContent.innerHTML = elReportContent.innerHTML +
            "<div class='graph'>" +
            $D.getElementsByClassName("graphreport", null, this.elBase)[0].innerHTML +
            "</div>";
        },
        /**
         * Opens the print preview popup and sets the content inside it
         */
        printGraphReport: function() {
            var sReportName = "graphreport";
            var elBufferReport = $D.getElementsByClassName("print-section", null, this.elBase)[0];
            var elPrintContent = $D.getElementsByClassName("print-skin-template", null, this.elBase)[0];
            var sBufferTemplateContent = $U.processTemplate(unescape(elPrintContent.innerHTML), {
                reportname: sReportName
            });
            elBufferReport.innerHTML = sBufferTemplateContent;
            this._setReportContent(sReportName, elBufferReport);
            var sReportBody = elBufferReport.innerHTML;
            var aStyleSheets = this.getPrintReportStyleOptions();
            var sOptions = this.getPrintReportPopupOptions();
            $U.openPrintPreviewPopup("Report Print Preview", sReportBody, aStyleSheets, null, sOptions);
            $U.removeChildNodes(elBufferReport);
        },
        /**
         *get the print popup options for the popup window
         */
        getPrintReportPopupOptions: function() {
            return "height=900px,width=800px,location=0,status=0,resizable=0,toolbar=0,menubar=1,titlebar=0,scrollbars=1";
        },
        /**
         * gets the style sheets required for the print-preview
         */
        getPrintReportStyleOptions: function() {
            return _publish.graphreport.stylesheets;
        }
    });
})();
