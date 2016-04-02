<#import "/macros/skin.ftm" as skin/>
<#import "/tests/macros/test.ftm" as test/>
<#macro css>
</#macro>
<#macro body>
    <@skin.widget name="CustomReportTimeFrame"/>
    <@skin.widget name="ActivityReport"/>
</#macro>
<#macro scripts>
    <@test.widgetscripts name="ActivityReport"/>
    <@test.widgetscripts name="ReportTimeFrame"/>
    <@test.widgetscripts name="BaseReport"/>
    <script language="JavaScript" type="text/javascript">
        var $B = YAHOO.Bubbling;
        var $L = YAHOO.lang;
        var $E = YAHOO.util.Event;
        var $D = YAHOO.util.Dom;
        var $YW = YAHOO.widget;
        var $W = getPackageForName("com.i10n.fleet.widget.ui");
        var $WU = getPackageForName("com.i10n.fleet.widget.util");
        var widgetInstance=null;
        var instance = null;
        function setUpPage() {
            inform('setUpPage()');
            setUpPageStatus = 'running';
            setTimeout('setUpPageComplete()', 50);
        }
        
        function setUpPageComplete() {
            if (setUpPageStatus == 'running')
            setUpPageStatus = 'complete';
            inform('setUpPageComplete()', setUpPageStatus);
        }
        
        function testMarkupLoaded() {
            var container = $D.get("activityreport");
            if($L.isObject(container)) {
                var innerWidget1 = $D.get("reporttimeframe");
                var innerWidget2 = $D.get("vehicleactivityreport");
                if(!$L.isObject(innerWidget1) && !$L.isObject(innerWidget2)) {
                    fail("Inner Widgets are not loaded");
                }
            }
            else {
                fail("Vehicle Report Container not loaded");
            }
        }
        
        function testInitializations() {
            instance = getInstance();
            if(!($L.isObject(instance) && $L.isObject(instance._widgets) && $L.isObject(instance._widgets.timeFrameBar))) {
                fail("Widgets in VehcileReport are not initialized");
            }
        }
        
        function getInstance() {
            if(!$L.isObject(widgetInstance)) {
                widgetInstance = new $W.ActivityReport($D.get("activityreport"));
            }
            return widgetInstance;
        }
        
         function testDataTable() {
            var mockData = [];
            var mockObj = {};
            mockObj.date = "1254781862139";
            mockObj.distance = "10";
            mockObj.lat = "13.000397999999999";
            mockObj.location = "Madivala";
            mockObj.lon = "13.000397999999999";
            mockObj.speed = "20";
            mockData[0] = mockObj;
            instance.set($W.BaseReport.ATTR_DATA,mockObj);
            assertTrue($L.isObject(instance._oDataTable));
        }
        
        function testConstructConfig(){
            var config = instance.constructConfig();
            assertTrue($L.isObject(config));
            assertTrue($L.isObject(config.datasource));
            assertNotNull(config.columndefs);
        }
        
        function testGetDataURL() {
            var actualURL = instance.getDataURL();
            assertNotNull(!actualURL);
            var expectedURL = "/fleet/view/reports/?module=/blocks/json&data=view&report=activityreport";
            var selectedVehicle = "vehicle-1";
            var oTimeFrame = instance.get($W.BaseReport.ATTR_SELECTED_TIMEFRAME);
            instance.set($W.BaseReport.ATTR_CURRENT_ITEM,selectedVehicle);
            instance.set($W.BaseReport.ATTR_CURRENT_TIMEFRAME,oTimeFrame);
            instance.set($W.BaseReport.ATTR_SELECTED_ITEM,selectedVehicle);
            expectedURL += "&vehicleID=" + selectedVehicle;
            expectedURL += "&startdate=" + oTimeFrame.startDate;
            expectedURL += "&enddate=" + oTimeFrame.endDate;
            actualURL = instance.getDataURL();
            assertEquals(actualURL,expectedURL);
        }
        
        function testHideAndShow() {
            instance.hide();
            assertTrue(instance._isHidden);
            instance.show();
            assertTrue(!instance._isHidden);
        }
        
                
        function exposeTestFunctionNames() {
            return [
                'testMarkupLoaded',
                'testInitializations',
                'testDataTable',
                'testGetDataURL',
                'testHideAndShow',
                'testConstructConfig'
            ];
        }
    </script>
</#macro>
