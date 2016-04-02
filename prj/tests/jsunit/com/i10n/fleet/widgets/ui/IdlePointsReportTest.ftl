<#import "/macros/skin.ftm" as skin/>
<#import "/tests/macros/test.ftm" as test/>
<#macro css>
</#macro>
<#macro body>
    <@skin.widget name="CustomReportTimeFrame"/>
    <@skin.widget name="ReportTimeFrame" params={"showminuteinterval":"false"}/>
    <@skin.widget name="IdlePointsReport" params={"cssClass":"idlepointsreport"}/>
</#macro>
<#macro scripts>
    <@test.widgetscripts name="IdlePointsReport"/>
    <@test.widgetscripts name="ReportTimeFrame"/>
    <script language="JavaScript" type="text/javascript">
        
        var $L = YAHOO.lang;
        var $E = YAHOO.util.Event;
        var $D = YAHOO.util.Dom;
        var $YW = YAHOO.widget;
        var $W = getPackageForName("com.i10n.fleet.widget.ui");
        var $WU = getPackageForName("com.i10n.fleet.widget.util");
        var $YU = YAHOO.util;
        
        var elTimeFrameBar,timeFrameBar;
        var elIdlePointsReport, idlePointsReport;
        
        function init(){
            elTimeFrameBar = $D.getElementsByClassName("reporttimeframe")[0];
            timeFrameBar = new $W.ReportTimeFrame(elTimeFrameBar, {
                navId: "vehiclereport"
            });
            elIdlePointsReport = $D.get("idlepointsreport");
            idlePointsReport = new $W.IdlePointsReport(elIdlePointsReport, {
                            "dataType" : "json",
                            "default" : "true",
                            "initialTimeFrame" : timeFrameBar._getTimeFrameData()
                        });
        }
        
        function setUpPage() {
            inform('setUpPage()');
            setUpPageStatus = 'running';
            setTimeout('setUpPageComplete()', 30);
            init();
        }
        
        function setUpPageComplete() {
            if (setUpPageStatus == 'running') 
                setUpPageStatus = 'complete';
            inform('setUpPageComplete()', setUpPageStatus);
        }
        
        function testDataTable() {
            idlePointsReport._oDataTable = null;
            idlePointsReport.render();
            assertTrue($L.isObject(idlePointsReport._oDataTable));
        }
        
        function testConstructConfig(){
            var config = idlePointsReport.constructConfig();
            assertTrue($L.isObject(config));
            assertTrue($L.isObject(config.datasource));
            assertNotNull(config.columndefs);
        }
        
        function testGeneratePrintableData() {
            var elBufferReport = $D.getElementsByClassName("print-section")[0];
            idlePointsReport._oDataTable.generatePrintableDataTable(elBufferReport);
            assertNotNull( $D.getElementsByClassName("yui-dt-data",null,elBufferReport)[0] );
            assertTrue($L.isObject($D.getElementsByClassName("yui-dt-data",null,elBufferReport)[0]));
        }
        
        function testGetDataURL() {
            var actualURL = idlePointsReport.getDataURL();
            assertNotNull(!actualURL);
            var expectedURL = "/fleet/view/reports/?module=/blocks/json&data=view&report=idlepointsreport";
            var selectedVehicle = "vehicle-1";
            var selectedTimeframe = timeFrameBar._getTimeFrameData();
            idlePointsReport.set($W.BaseReport.ATTR_CURRENT_ITEM,selectedVehicle);
            idlePointsReport.set($W.BaseReport.ATTR_CURRENT_TIMEFRAME,selectedTimeframe);
            idlePointsReport.set($W.BaseReport.ATTR_SELECTED_ITEM,selectedVehicle);
            idlePointsReport.set($W.BaseReport.ATTR_SELECTED_TIMEFRAME,selectedTimeframe);
            expectedURL += "&vehicleID=" + selectedVehicle;
            expectedURL += "&startdate=" + selectedTimeframe.startDate;
            expectedURL += "&enddate=" + selectedTimeframe.endDate;
            actualURL = idlePointsReport.getDataURL();
            assertEquals(actualURL,expectedURL);
        }
        
        function testHideAndShow() {
            idlePointsReport._isHidden = false;
            idlePointsReport._isDataStale = false;
            idlePointsReport.hide();
            assertTrue($D.hasClass(idlePointsReport.elBase,'rpt-hidden'));
            idlePointsReport.show();
            assertTrue(!$D.hasClass(idlePointsReport.elBase,'rpt-hidden'));
        }
        
        function exposeTestFunctionNames() {
            return [
                'testHideAndShow',
                'testDataTable',
                'testConstructConfig',
                'testGeneratePrintableData',
                'testGetDataURL'
            ];
        }
    </script>
</#macro>
