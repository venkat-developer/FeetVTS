<#import "/macros/skin.ftm" as skin/>
<#import "/tests/macros/test.ftm" as test/>
<#import "/macros/json-publish.ftm" as json/>
<#include "/mock/reports/VehicleStatsReport.ftd">
<#macro css>
</#macro>
<#macro body>
    <@skin.widget name="GroupedReport" params={"cssClass":"report groupedreport"}/>
    <@json.publish data=vehiclestatus publishAs="reportdata" context="report"/>
</#macro>
<#macro scripts>
    <@test.widgetscripts name="VehicleStatsReport"/>
    
    <script language="JavaScript" type="text/javascript">
        var $B = YAHOO.Bubbling;
        var $L = YAHOO.lang;
        var $E = YAHOO.util.Event;
        var $D = YAHOO.util.Dom;
        var $YW = YAHOO.widget;
        var $W = getPackageForName("com.i10n.fleet.widget.ui");
        var $WU = getPackageForName("com.i10n.fleet.widget.util");
        var $U = getPackageForName("com.i10n.fleet.Utils");
        var $YU = YAHOO.util;
        var grpReportEl;
        var config;
        var groupedReport;
        
        /**
         * Constructs a config object from the published mockdata
         */
        function constructConfig(reportData) {
            var config = {};
            var status = reportData;
            var data = [];
            var count = 0;
            
            for (var groupId in status) {
                if (status[groupId].vehicles) {
                    for (var vehicleId in status[groupId].vehicles) {
                        var item = $U.cloneObject(status[groupId].vehicles[vehicleId]);
                        item.id = vehicleId;
                        item.index = count + 1;
                        data.push(item);
                        count++;
                    }
                }
            }
            var datasource = new $W.GroupedReport.GroupedDataSource(reportData);
            datasource.responseType = $YU.DataSource.TYPE_JSARRAY;
            datasource.responseSchema = {
                elementField: "vehicles",
                fields: ["id", "index", "vehiclename", "driver", "region", "date", "time", "speed"]
            };
            var columndefs = [{
                key: "id",
                hidden: true
            }, {
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
                key: "driver",
                label: "Driver Name",
                sortable: true,
                resizeable: false
            }, {
                key: "region",
                label: "Region",
                sortable: true,
                resizeable: false
            }, {
                key: "date",
                label: "Date",
                formatter: $YW.DataTable.formatDate,
                sortable: true,
                    resizeable: false
            }, {
                key: "time",
                label: "Time",
                sortable: true,
                resizeable: false
            }, {
                key: "speed",
                label: "Speed",
                sortable: true,
                resizeable: false
            }];
            config.datasource = datasource;
            config.columndefs = columndefs;
            config.reportconfig = {};
            config.options = {
                "select": {
                    "enabled": true,
                    "selected": true
                },
                "groups": {
                    "enabled": true,
                    "titleId": "name"
                }
            };
            return config;
        }
        
        function setUpPage() {
            inform('setUpPage()');
            setUpPageStatus = 'running';
            setTimeout('setUpPageComplete()', 30);
            grpReportEl = $D.get("groupedreport");
            config = constructConfig(_publish.report.reportdata);
            groupedReport = new $W.GroupedReport(grpReportEl,config);
        }
        
        function setUpPageComplete() {
            if (setUpPageStatus == 'running')
            setUpPageStatus = 'complete';
            inform('setUpPageComplete()', setUpPageStatus);
        }
        
        function testParseQuery() {
            var KEY_1 = "group" , VAL_1 = "group-1";
            var KEY_2 = "selected" , VAL_2 = "true";
            var parsedResult = groupedReport._oGroupedDataSource._parseQuery(KEY_1+"="+VAL_1+"&"+KEY_2+"="+VAL_2);
            assertEquals(parsedResult[KEY_1],VAL_1);
            assertEquals(parsedResult[KEY_2],VAL_2);
        }
        
        function testGetGroupData() {
            var objGroupData = groupedReport._oGroupedDataSource.getGroupData();
            var configGroupData= config.datasource.getGroupData();
            assertEquals(objGroupData, configGroupData );
        }
        
        function testGetGroupItem() {
            var oGroupData = groupedReport._oGroupedDataSource.groupData;
            var configGroupData = config.datasource.getGroupData();
            for( groups in oGroupData ) {
                var elements = [];
                elements = groupedReport._oGroupedDataSource._getGroupItem(oGroupData[groups],elements);
                var group1 = configGroupData[groups];
                var groupElements = [];
                for( a in group1["vehicles"] ) {
                    groupElements.push( group1["vehicles"][a]);
                }
                assertEquals(elements.length,groupElements.length);
                /*
                 * TO DO: Compare the objects in both the above two arrays
                 */
            }
        }
        
        function testPrintableDataTable() {
            var elBufferReport = $D.getElementsByClassName("print-section", null, this.reportElement)[0];
            groupedReport.generatePrintableDataTable(elBufferReport);
            assertNotNull( $D.getElementsByClassName("yui-dt-data")[0] );
        }
        
        function testSelectedPrintableDataTable() {
            var elBufferReport = $D.getElementsByClassName("print-section", null, this.reportElement)[0];
            groupedReport.generateSelectedPrintableDataTable(elBufferReport);
            assertNotNull( $D.getElementsByClassName("yui-dt-data")[0] );
        }
        
        function testGetReportConfig() {
            assertNotNull(groupedReport.getReportConfig());
        }
        
        function testGetOptionConfig() {
            assertNotNull(groupedReport._getOptionConfig());
        }
        
        function testGetSelectedRecords() {
            var records = groupedReport._getSelectedRecords();
            var data = config.datasource.getGroupData();
            var groupElements = [];
            for( groups in data ) {
                var group = data[groups];
                for( a in group["vehicles"] ) {
                    groupElements.push( group["vehicles"][a]);
                }
            }
            assertEquals(records.length , groupElements.length );
        }
        
        function testPageCount() {
            var expectedCount = groupedReport.DEFAULT_REPORT_CONFIG.paginator.getTotalPages();
            var pageCount = $D.get("yui-pg0-0-pageCount-span").innerHTML;
            if( expectedCount > 0 ) 
                assertEquals( pageCount, " of " + expectedCount );
            else {
                assertEquals( pageCount , "");
            }
        }
        
        function exposeTestFunctionNames() {
            return [
                'testParseQuery',
                'testGetGroupData',
                'testGetGroupItem',
                'testPrintableDataTable',
                'testSelectedPrintableDataTable',
                'testGetReportConfig',
                'testGetOptionConfig',
                'testGetSelectedRecords',
                'testPageCount'
            ];
        }
    </script>
</#macro>