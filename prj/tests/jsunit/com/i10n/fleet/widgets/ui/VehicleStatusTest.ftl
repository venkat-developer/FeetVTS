<#import "/macros/skin.ftm" as skin/>
<#import "/tests/macros/test.ftm" as test/>
<#macro css>
</#macro>
<#macro body>
    <@skin.widget name="VehicleStatus"/>
</#macro>
<#macro scripts>
    <@test.widgetscripts name="VehicleStatus"/>
    <script language="JavaScript" type="text/javascript">
        var $L = YAHOO.lang;
        var $D = YAHOO.util.Dom;
        var $W = getPackageForName("com.i10n.fleet.widget.ui");
        var $WU = getPackageForName("com.i10n.fleet.widget.util");
        var $YU = YAHOO.util;
        var widgetInstance = null;
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
            var widgetContainer = $D.get('vehiclestatus');
            if(!$L.isObject(widgetContainer)) {
                fail("Widget Markup Not Loaded!");
            }
        }
        
        function testInitialization() {
            var oWidgetInstance = getInstance();
            if(!oWidgetInstance) {
                fail("Widget failed to initialize!");
            }
            
        }
        
        function testRecordsCreation() {
            var oWidgetInstance = getInstance();
            if(!$D.getElementsByClassName("yui-dt", null, $D.get('vehiclestatus'))[0]) {
                fail("Widget failed to initialize datatable!");
            }
            if(!oWidgetInstance.getRecordSet()) {
                fail("Widget Recordset is not initialized!");
            }
            assertEquals(16,oWidgetInstance.getRecordSet().getLength());
        }
        
        function getInstance() {
            if($L.isNull(widgetInstance)) {
                var oDataSource = getDataSource();
                widgetInstance = new $W.VehicleStatus($D.get('vehiclestatus'),{
                    datasource : oDataSource
                });
            }
            return widgetInstance;
        }
        
        function getDataSource() {
            var oSampleData = {
                "group-0" : {
                    "name" : "North Zone",
                    "vehicles" : {
                        "vehicle-100" : {
                            "id" : "vehicle-100",
                            "name" : "Vehicle 100",
                            "make" : "Toyota",
                            "model" : "Qualis",
                            "location" : "Eejipura",
                            "speed" : "89",
                            "driver" : "Driver 249",
                            "status" : "online",
                            "lat" : "12.340683",
                            "lon" : "77.468233",
                            "chargerdc" : "true",
                            "gps" : "2",
                            "gsm" : "3",
                            "battery" : "2",
                            "fuel" : "3",
                            "lastupdated" : "12/08/2009 12:23PM"
                        },
                        "vehicle-4" : {
                            "id" : "vehicle-4",
                            "name" : "Vehicle 4",
                            "make" : "Tata",
                            "model" : "Winger",
                            "location" : "Madivala",
                            "speed" : "94",
                            "driver" : "Driver 240",
                            "status" : "offline",
                            "lat" : "12.859053",
                            "lon" : "77.303226",
                            "chargerdc" : "true",
                            "gps" : "0",
                            "gsm" : "2",
                            "battery" : "1",
                            "fuel" : "0",
                            "lastupdated" : "12/08/2009 12:23PM"
                        },
                        "vehicle-131" : {
                            "id" : "vehicle-131",
                            "name" : "Vehicle 131",
                            "make" : "Ashok Leyland",
                            "model" : "F-150",
                            "location" : "Madivala",
                            "speed" : "89",
                            "driver" : "Driver 50",
                            "status" : "offline",
                            "lat" : "12.928756",
                            "lon" : "77.560301",
                            "chargerdc" : "false",
                            "gps" : "1",
                            "gsm" : "0",
                            "battery" : "2",
                            "fuel" : "0",
                            "lastupdated" : "12/08/2009 12:23PM"
                        },
                        "vehicle-198" : {
                            "id" : "vehicle-198",
                            "name" : "Vehicle 198",
                            "make" : "Ashok Leyland",
                            "model" : "F-150",
                            "location" : "Bommanahalli",
                            "speed" : "101",
                            "driver" : "Driver 87",
                            "status" : "idle",
                            "lat" : "12.211825",
                            "lon" : "77.216215",
                            "chargerdc" : "false",
                            "gps" : "2",
                            "gsm" : "3",
                            "battery" : "0",
                            "fuel" : "2",
                            "lastupdated" : "12/08/2009 12:23PM"
                        }
                    }
                },
                "group-1" : {
                    "name" : "South Zone",
                    "vehicles" : {
                        "vehicle-45" : {
                            "id" : "vehicle-45",
                            "name" : "Vehicle 45",
                            "make" : "Ford",
                            "model" : "Escort",
                            "location" : "BTM Layout",
                            "speed" : "63",
                            "driver" : "Driver 41",
                            "status" : "online",
                            "lat" : "12.532294",
                            "lon" : "77.169271",
                            "chargerdc" : "true",
                            "gps" : "1",
                            "gsm" : "3",
                            "battery" : "3",
                            "fuel" : "2",
                            "lastupdated" : "12/08/2009 12:23PM"
                        },
                        "vehicle-187" : {
                            "id" : "vehicle-187",
                            "name" : "Vehicle 187",
                            "make" : "Ashok Leyland",
                            "model" : "F-150",
                            "location" : "Bommanahalli",
                            "speed" : "88",
                            "driver" : "Driver 117",
                            "status" : "offline",
                            "lat" : "12.603417",
                            "lon" : "77.519164",
                            "chargerdc" : "false",
                            "gps" : "2",
                            "gsm" : "2",
                            "battery" : "3",
                            "fuel" : "3",
                            "lastupdated" : "12/08/2009 12:23PM"
                        },
                        "vehicle-84" : {
                            "id" : "vehicle-84",
                            "name" : "Vehicle 84",
                            "make" : "Toyota",
                            "model" : "Qualis",
                            "location" : "Bommanahalli",
                            "speed" : "76",
                            "driver" : "Driver 74",
                            "status" : "offline",
                            "lat" : "12.807406",
                            "lon" : "77.165606",
                            "chargerdc" : "false",
                            "gps" : "1",
                            "gsm" : "3",
                            "battery" : "1",
                            "fuel" : "2",
                            "lastupdated" : "12/08/2009 12:23PM"
                        },
                        "vehicle-31" : {
                            "id" : "vehicle-31",
                            "name" : "Vehicle 31",
                            "make" : "Ford",
                            "model" : "Escort",
                            "location" : "Madivala",
                            "speed" : "72",
                            "driver" : "Driver 232",
                            "status" : "offline",
                            "lat" : "12.253816",
                            "lon" : "77.29688",
                            "chargerdc" : "false",
                            "gps" : "0",
                            "gsm" : "1",
                            "battery" : "1",
                            "fuel" : "2",
                            "lastupdated" : "12/08/2009 12:23PM"
                        }
                    }
                },
                "group-2" : {
                    "name" : "East Zone",
                    "vehicles" : {
                        "vehicle-179" : {
                            "id" : "vehicle-179",
                            "name" : "Vehicle 179",
                            "make" : "Ford",
                            "model" : "Escort",
                            "location" : "Eejipura",
                            "speed" : "85",
                            "driver" : "Driver 49",
                            "status" : "online",
                            "lat" : "12.726641",
                            "lon" : "77.249502",
                            "chargerdc" : "true",
                            "gps" : "1",
                            "gsm" : "3",
                            "battery" : "3",
                            "fuel" : "1",
                            "lastupdated" : "12/08/2009 12:23PM"
                        },
                        "vehicle-235" : {
                            "id" : "vehicle-235",
                            "name" : "Vehicle 235",
                            "make" : "Ford",
                            "model" : "Escort",
                            "location" : "Koramangala",
                            "speed" : "109",
                            "driver" : "Driver 67",
                            "status" : "offline",
                            "lat" : "12.621504",
                            "lon" : "77.028993",
                            "chargerdc" : "true",
                            "gps" : "2",
                            "gsm" : "2",
                            "battery" : "3",
                            "fuel" : "0",
                            "lastupdated" : "12/08/2009 12:23PM"
                        },
                        "vehicle-127" : {
                            "id" : "vehicle-127",
                            "name" : "Vehicle 127",
                            "make" : "Toyota",
                            "model" : "Qualis",
                            "location" : "Bommanahalli",
                            "speed" : "67",
                            "driver" : "Driver 163",
                            "status" : "idle",
                            "lat" : "12.287707",
                            "lon" : "77.518937",
                            "chargerdc" : "true",
                            "gps" : "0",
                            "gsm" : "1",
                            "battery" : "3",
                            "fuel" : "3",
                            "lastupdated" : "12/08/2009 12:23PM"
                        },
                        "vehicle-177" : {
                            "id" : "vehicle-177",
                            "name" : "Vehicle 177",
                            "make" : "Tata",
                            "model" : "Winger",
                            "location" : "BTM Layout",
                            "speed" : "69",
                            "driver" : "Driver 237",
                            "status" : "idle",
                            "lat" : "12.122722",
                            "lon" : "77.51963",
                            "chargerdc" : "true",
                            "gps" : "2",
                            "gsm" : "1",
                            "battery" : "0",
                            "fuel" : "3",
                            "lastupdated" : "12/08/2009 12:23PM"
                        }
                    }
                },
                "group-3" : {
                    "name" : "West Zone",
                    "vehicles" : {
                        "vehicle-135" : {
                            "id" : "vehicle-135",
                            "name" : "Vehicle 135",
                            "make" : "Ford",
                            "model" : "Escort",
                            "location" : "Bommanahalli",
                            "speed" : "63",
                            "driver" : "Driver 191",
                            "status" : "idle",
                            "lat" : "12.727544",
                            "lon" : "77.432843",
                            "chargerdc" : "true",
                            "gps" : "3",
                            "gsm" : "3",
                            "battery" : "3",
                            "fuel" : "3",
                            "lastupdated" : "12/08/2009 12:23PM"
                        },
                        "vehicle-59" : {
                            "id" : "vehicle-59",
                            "name" : "Vehicle 59",
                            "make" : "Ford",
                            "model" : "Escort",
                            "location" : "Eejipura",
                            "speed" : "67",
                            "driver" : "Driver 81",
                            "status" : "offline",
                            "lat" : "12.372012",
                            "lon" : "77.001436",
                            "chargerdc" : "true",
                            "gps" : "3",
                            "gsm" : "1",
                            "battery" : "2",
                            "fuel" : "1",
                            "lastupdated" : "12/08/2009 12:23PM"
                        },
                        "vehicle-240" : {
                            "id" : "vehicle-240",
                            "name" : "Vehicle 240",
                            "make" : "Toyota",
                            "model" : "Qualis",
                            "location" : "Madivala",
                            "speed" : "80",
                            "driver" : "Driver 147",
                            "status" : "online",
                            "lat" : "12.291364",
                            "lon" : "77.54565",
                            "chargerdc" : "true",
                            "gps" : "0",
                            "gsm" : "1",
                            "battery" : "2",
                            "fuel" : "1",
                            "lastupdated" : "12/08/2009 12:23PM"
                        },
                        "vehicle-184" : {
                            "id" : "vehicle-184",
                            "name" : "Vehicle 184",
                            "make" : "Ford",
                            "model" : "Escort",
                            "location" : "Koramangala",
                            "speed" : "66",
                            "driver" : "Driver 6",
                            "status" : "offline",
                            "lat" : "12.76939",
                            "lon" : "77.154263",
                            "chargerdc" : "true",
                            "gps" : "1",
                            "gsm" : "0",
                            "battery" : "2",
                            "fuel" : "2",
                            "lastupdated" : "12/08/2009 12:23PM"
                        }
                    }
                }
            };
            var oDataSource = new $W.GroupedReport.GroupedDataSource(oSampleData);
            oDataSource.responseType = $YU.DataSource.TYPE_JSARRAY;
            oDataSource.responseSchema = {
                elementField: "vehicles",
                fields: ["id", "name", "status", "chargerdc", "gps", "gsm", "battery", "fuel", "lastupdated", "lat", "lon", "location", "make", "model", "speed", "driver"]
            };
            return oDataSource;
        }
        
        function exposeTestFunctionNames() {
            return [
                'testMarkupLoaded',
                'testInitialization',
                'testRecordsCreation'
            ];
        }
        
    </script>
</#macro>
