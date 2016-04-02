<#import "/macros/skin.ftm" as skin/>
<#import "/tests/macros/test.ftm" as test/>
<#macro css>
</#macro>
<#macro body>
    <@skin.widget name="VacantVehicles"/>
</#macro>
<#macro scripts>
    <@test.widgetscripts name="VacantVehicles"/>
    <script language="JavaScript" type="text/javascript">
        var $L = YAHOO.lang;
        var $D = YAHOO.util.Dom;
        var $W = getPackageForName("com.i10n.fleet.widget.ui");
        var $WU = getPackageForName("com.i10n.fleet.widget.util");
        
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
            var widgetContainer = $D.getElementsByClassName('vacant-vehicles-container');
            if(!$L.isArray(widgetContainer) || !$L.isObject(widgetContainer[0])) {
                fail("Widget Markup Not Loaded!");
            }
        }
        
        function exposeTestFunctionNames() {
            return [
                'testMarkupLoaded'
            ];
        }
    </script>
</#macro>
