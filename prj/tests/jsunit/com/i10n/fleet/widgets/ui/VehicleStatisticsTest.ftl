<#import "/macros/skin.ftm" as skin/>
<#import "/tests/macros/test.ftm" as test/>
<#macro css>
</#macro>
<#macro body>
    <@skin.widget name="VehicleStatistics" params={"cssClass":"vehiclestatistics"}/>
</#macro>
<#macro scripts>
    <@test.widgetscripts name="VehicleStatistics"/>
    <script language="JavaScript" type="text/javascript">
        var $L = YAHOO.lang;
        var $E = YAHOO.util.Event;
        var $D = YAHOO.util.Dom;
        var $YU = YAHOO.util;
        var $W = getPackageForName("com.i10n.fleet.widget.ui");
        var $DU = getPackageForName("com.i10n.fleet.widget.test.DOMEventUtils");
        var $U = getPackageForName("com.i10n.fleet.Utils");
        
        var vehicleStatistics , elVehicleStatistics;
        
        /**
         * Initialize the widget for testing
         */
        function init() {
            elVehicleStatistics = $D.get("vehiclestatistics");
            vehicleStatistics = new $W.VehicleStatistics(elVehicleStatistics , {
                                        "dataType": "json",
                                        "default": "true"
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
        
        
        function testInitialization() {
            if(!($L.isObject(vehicleStatistics) && $L.isObject(vehicleStatistics.elBase))) {
                fail("VehicleStatistics widget is not properly initialized");
            }
        }
        
        function testGetDataURL() {
            var actualURL = vehicleStatistics.getDataURL();
            assertNull(actualURL);
            var expectedURL = "/fleet/view/reports/?module=/blocks/json&data=view&report=vehiclestatistics";
            var selectedVehicle = "vehicle-1";
            vehicleStatistics.set($W.BaseReport.ATTR_CURRENT_ITEM,selectedVehicle);
            vehicleStatistics.set($W.BaseReport.ATTR_SELECTED_ITEM,selectedVehicle);
            var oTimeFrame = {
                startDate : "09/09/2009 00:00:00",
                endDate : "09/09/2009 23:59:59"
            };
            vehicleStatistics.set($W.BaseReport.ATTR_CURRENT_TIMEFRAME,oTimeFrame);
            vehicleStatistics.set($W.BaseReport.ATTR_SELECTED_TIMEFRAME,oTimeFrame);
            expectedURL += "&vehicleID=" + selectedVehicle + "&startdate=" + oTimeFrame.startDate + "&enddate=" + oTimeFrame.endDate;
            actualURL = vehicleStatistics.getDataURL();
            assertEquals(actualURL,expectedURL);
        }
        
        function testEventHandlers() {
            var elErrDialog = $D.getElementsByClassName("simpledialog", null, this.elBase)[0];
            assertTrue(!$D.hasClass(elErrDialog, "disabled"));
            var el = $D.getElementsByClassName("button-done", null, vehicleStatistics.elBase)[0];
            $DU.fireEvent(el, "click");
            assertTrue($D.hasClass(elErrDialog, "disabled"));
            var printEl = $D.getElementsByClassName("print-preview", null, vehicleStatistics.elBase)[0];
            try {
                $DU.fireEvent(printEl, "click");
            }
            catch(ex) {
                fail("Print Preview Event not triggered properly");
            }
        }
        
        function exposeTestFunctionNames() {
            return [
                'testInitialization',
                'testEventHandlers',
                'testGetDataURL'
            ];
        }
    </script>
</#macro>