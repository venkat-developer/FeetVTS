<#import "/macros/skin.ftm" as skin/>
<#import "/tests/macros/test.ftm" as test/>
<#macro css>
</#macro>
<#macro body>
    <@skin.widget name="FuelGraph"/>
    <@skin.widget name="VehicleList"/>
</#macro>
<#macro scripts>
    <@test.widgetscripts name="FuelGraph"/>
    <@test.widgetscripts name="MinimizableList" />
    <@test.widgetscripts name="SidePaneList" />
    <@test.widgetscripts name="VehicleList" />
    <script language="JavaScript" type="text/javascript">
        var $L = YAHOO.lang;
        var $D = YAHOO.util.Dom;
        var $W = getPackageForName("com.i10n.fleet.widget.ui");
        var $WU = getPackageForName("com.i10n.fleet.widget.util");
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
        
        function testInitializations() {    
            var instance = getInstance();
            if(!($L.isObject(instance))) {
                fail("Graph report is not initialized");
            }
        }
        
        function testGetDataURL() {
           /*Mocking the context*/
           var mockContext = {};
           mockContext.get = function(mockValue){
              return $D.getElementsByClassName("item-name")[0];
           };
           /*Creting the instance*/
           var instance = getInstance();
           var result = instance.getDataURL.call(mockContext);
           assertNotNull(result);
        }
       
        function getInstance() {
            if(!$L.isObject(widgetInstance)) {
                widgetInstance = new $W.FuelGraph($D.getElementsByClassName("graph")[0]);
            }
            return widgetInstance;
        }
        
        function exposeTestFunctionNames() {
            return [
                'testInitializations',
                'testGetDataURL' 
           ];        
        }
    </script>
</#macro>