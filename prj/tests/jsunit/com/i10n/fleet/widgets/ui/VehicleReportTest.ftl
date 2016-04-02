<#import "/macros/skin.ftm" as skin/>
<#import "/tests/macros/test.ftm" as test/>
<#macro css>
</#macro>
<#macro body>
    <@skin.widget name="CustomReportTimeFrame"/>
    <@skin.widget name="VehicleReport"/>
</#macro>
<#macro scripts>
    <@test.widgetscripts name="VehicleReport"/>
    <script language="JavaScript" type="text/javascript">
        var $B = YAHOO.Bubbling;
        var $L = YAHOO.lang;
        var $E = YAHOO.util.Event;
        var $D = YAHOO.util.Dom;
        var $YW = YAHOO.widget;
        var $W = getPackageForName("com.i10n.fleet.widget.ui");
        var $WU = getPackageForName("com.i10n.fleet.widget.util");
        var widgetInstance=null;
        
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
            var container = $D.get("vehiclereport");
            if($L.isObject(container)) {
                var innerWidget = $D.get("reporttimeframe");
                if(!$L.isObject(innerWidget)) {
                    fail("ReportTimeFrame not loaded");    
                }
            }
            else {
                fail("Vehicle Report Container not loaded");
            }
        }
        
        function testInitializations() {
            var instance = getInstance();
            if(!($L.isObject(instance) && $L.isObject(instance._widgets) && $L.isObject(instance._widgets.timeFrameBar))) {
                fail("Widgets in VehcileReport are not initialized");
            }
        }
        
        function getInstance() {
            if(!$L.isObject(widgetInstance)) {
                widgetInstance = new $W.VehicleReport($D.get("vehiclereport"));
            }
            return widgetInstance;
        }
        
        function exposeTestFunctionNames() {
            return [
                'testMarkupLoaded',
                'testInitializations'
            ];
        }
    </script>
</#macro>
