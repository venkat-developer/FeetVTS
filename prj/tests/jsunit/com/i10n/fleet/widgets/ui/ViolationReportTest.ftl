<#import "/macros/skin.ftm" as skin/>
<#import "/tests/macros/test.ftm" as test/>
<#macro css>
</#macro>
<#macro body>
    <@skin.widget name="CustomReportTimeFrame"/>
    <@skin.widget name="ViolationReport"/>
</#macro>
<#macro scripts>
    <@test.widgetscripts name="ViolationReport"/>
    <script language="JavaScript" type="text/javascript">
        var $L = YAHOO.lang;
        var $D = YAHOO.util.Dom;
        var $W = getPackageForName("com.i10n.fleet.widget.ui");
        var $WU = getPackageForName("com.i10n.fleet.widget.util");
        var $YW = YAHOO.widget;
        
        var widgetInstance = null;
        
        var oSampleData = {
            "geofence" : {
                "group-0": { 
                    "id": "group-0" , 
                    "name": "North Zone" , 
                    "violations": [ { 
                        "avgspeed": "41" , 
                        "drivername": "Driver 178" , 
                        "enddate": "1253216742537" , 
                        "location": "BTM Layout" , 
                        "startdate": "1253216082537" , 
                        "time": "11" , 
                        "vehiclename": "Vehicle 214" , 
                        "id": "violation-0" 
                    }, { 
                        "avgspeed": "47" , 
                        "drivername": "Driver 147" , 
                        "enddate": "1253231262537" , 
                        "location": "Bommanahalli" , 
                        "startdate": "1253230482537" , 
                        "time": "13" , 
                        "vehiclename": "Vehicle 45" , 
                        "id": "violation-1" 
                    }]
                }
            },
            "overspeed" : {
                "group-0": { 
                    "id": "group-0" , 
                    "name": "North Zone" , 
                    "violations": [ { 
                        "avgspeed": "41" , 
                        "drivername": "Driver 178" , 
                        "enddate": "1253216742537" , 
                        "location": "BTM Layout" , 
                        "startdate": "1253216082537" , 
                        "time": "11" , 
                        "vehiclename": "Vehicle 214" , 
                        "id": "violation-0" 
                    }, { 
                        "avgspeed": "47" , 
                        "drivername": "Driver 147" , 
                        "enddate": "1253231262537" , 
                        "location": "Bommanahalli" , 
                        "startdate": "1253230482537" , 
                        "time": "13" , 
                        "vehiclename": "Vehicle 45" , 
                        "id": "violation-1" 
                    }]
                }
            },
            "chargerdc" : {
                "group-0": { 
                    "id": "group-0" , 
                    "name": "North Zone" , 
                    "violations": [ {
                        "avgspeed": "41" , 
                        "drivername": "Driver 178" , 
                        "enddate": "1253216742537" , 
                        "location": "BTM Layout" , 
                        "startdate": "1253216082537" , 
                        "time": "11" , 
                        "vehiclename": "Vehicle 214" , 
                        "id": "violation-0" 
                    }, { 
                        "avgspeed": "47" , 
                        "drivername": "Driver 147" , 
                        "enddate": "1253231262537" , 
                        "location": "Bommanahalli" , 
                        "startdate": "1253230482537" , 
                        "time": "13" , 
                        "vehiclename": "Vehicle 45" , 
                        "id": "violation-1" 
                    }]
                }
            }
        };
        
        function setUpPage() {
            inform('setUpPage()');
            setUpPageStatus = 'running';
            setTimeout('setUpPageComplete()', 30);
        }
        
        function setUpPageComplete() {
            if (setUpPageStatus == 'running')
            setUpPageStatus = 'complete';
            inform('setUpPageComplete()', setUpPageStatus);
        }
        
        function testMarkupLoaded() {
            var containerArr = $D.getElementsByClassName("rpt-container", null, $D.get("violationreport"));
            if(!($L.isArray(containerArr) && containerArr.length > 0)) {
                fail("Markup Not yet Loaded!");
            }
        }
        
        
        function testInitialization() {
            var oWidgetInstance = getInstance();
            assertNotNull("oWidgetInstance is null", oWidgetInstance);
            oWidgetInstance.set($W.BaseReport.ATTR_DATA,oSampleData);
            oWidgetInstance.render();
            assertNotNull("oWidgetInstance._reports is null", oWidgetInstance._reports);
            assertNotUndefined("oWidgetInstance._reports is undefined", oWidgetInstance._reports);
            assertNotNull("oWidgetInstance._reports.overspeed is null", oWidgetInstance._reports.overspeed);
            assertNotUndefined("oWidgetInstance._reports.overspeed is undefined", oWidgetInstance._reports.overspeed);
            assertNotNull("oWidgetInstance._reports.geofence is null", oWidgetInstance._reports.geofence);
            assertNotUndefined("oWidgetInstance._reports.geofence is undefined",oWidgetInstance._reports.geofence);
            assertNotNull("oWidgetInstance._reports.chargerdc is null", oWidgetInstance._reports.chargerdc);
            assertNotUndefined("oWidgetInstance._reports.chargerdc is undefined", oWidgetInstance._reports.chargerdc);
            assertTrue("Over Speeding Report is not instance of GroupedReport", oWidgetInstance._reports.overspeed instanceof $W.GroupedReport);
            assertTrue("Geo Fencing Report is not instance of GroupedReport", oWidgetInstance._reports.geofence instanceof $W.GroupedReport);
            assertTrue("Charger Report is not instance of GroupedReport", oWidgetInstance._reports.chargerdc instanceof $W.GroupedReport);
            
            assertTrue("Widget is not instance of BaseReport", oWidgetInstance instanceof $W.BaseReport);
        }
        
        function testConfigCreation() {
            testConfigReport("overspeed");
            testConfigReport("geofence");
            testConfigReport("chargerdc");
        }
        
        function testConfigReport(reportId) {
            var oConfig = getInstance()._getConfig(reportId);
            assertEquals(oConfig.columndefs, $W.ViolationReport.CONFIG_REPORTS[reportId].columndefs);
            assertEquals(oConfig.reportconfig, $W.ViolationReport.CONFIG_REPORTS[reportId].reportconfig);
            assertEquals(oConfig.options, $W.ViolationReport.CONFIG_REPORTS[reportId].options);
            assertNotNull("Config Datasource is null", oConfig.datasource);
            assertNotUndefined("Config Datasource is undefined", oConfig.datasource);
            assertEquals(oConfig.datasource.responseSchema, $W.ViolationReport.CONFIG_REPORTS[reportId].datasource.responseSchema);
        }
        
        function getInstance() { 
            if($L.isNull(widgetInstance)) {
                widgetInstance = new $W.ViolationReport($D.get("violationreport"));
            }
            return widgetInstance;
        }
        
        function exposeTestFunctionNames() {
            return [
                'testMarkupLoaded',
                'testInitialization',
                'testConfigCreation'
            ];
        }
    </script>
</#macro>