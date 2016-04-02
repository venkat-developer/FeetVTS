<#import "/macros/skin.ftm" as skin/>
<#import "/tests/macros/test.ftm" as test/>
<#macro css>
</#macro>
<#macro body>
    <@skin.widget name="DashboardViolations"/>
</#macro>
<#macro scripts>    
    <@test.widgetscripts name="DashboardViolations"/>
    <script language="JavaScript" type="text/javascript">
          var $L = YAHOO.lang;
          var $D = YAHOO.util.Dom;
          var $W = getPackageForName("com.i10n.fleet.widget.ui");
          var $WU = getPackageForName("com.i10n.fleet.widget.util");
          var el = $D.get('dashboardviolations');
          var vehicleList = new $W.DashboardViolations(el);
         
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
        
        function testTabViewLoad(){
           
             var container = $D.getElementsByClassName('violations-tabview',null,el)[0];
            if(!($L.isObject(container))) {
                fail("Markup Not yet Loaded!");
            }
            else {
                var tabs = $D.getElementsByClassName("tabitem",null,container);
                if(!($D.hasClass(tabs,"overspeed") &&  $D.hasClass(tabs,"geo-fencing") && $D.hasClass(tabs,"chargerstatus"))) {
                    fail("Inner Tabs Not yet Loaded!");
                }
            }
        
        }
   function exposeTestFunctionNames() {
            return [
                'testTabViewLoad'
            ];
        }
    </script>
</#macro>
