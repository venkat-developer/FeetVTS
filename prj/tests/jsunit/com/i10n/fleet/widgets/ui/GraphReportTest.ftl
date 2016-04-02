<#import "/macros/skin.ftm" as skin/>
<#import "/tests/macros/test.ftm" as test/>
<#macro css>
</#macro>
<#macro body>
    <@skin.widget name="GraphReport"/>
</#macro>
<#macro scripts>
    <@test.widgetscripts name="GraphReport"/>
    <script language="JavaScript" type="text/javascript">
        var $L = YAHOO.lang;
        var $D = YAHOO.util.Dom;
        var $W = getPackageForName("com.i10n.fleet.widget.ui");
        var $WU = getPackageForName("com.i10n.fleet.widget.util");
        var $DEU = getPackageForName("com.i10n.fleet.widget.test.DOMEventUtils");
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
        
        function testRender(){
            /*Mocking the context*/
           var mockContext = {};
           var mockData = {
                  vehicleid:"Vehicle-111",
                  graphurl:"test"
           };
           mockContext.get = function(mockValue){
              return {
                  key :"Vehicle-111"
              };
           };
           var instance = getInstance();
           instance.render.call(mockContext);
           var el = $D.getElementsByClassName('selected-item')[0];
           if(!el.innerHTML.match(mockData.vehicleid)){
              fail("The graph was not rendered properly");
           }
        }
        
        function testRefresh(){
           /*Mocking up*/
           var instance = getInstance();
           var holder = instance.update;
           var count = 0;
           instance.update = function(){
              count++;
           };
           $DEU.fireEvent($D.getElementsByClassName('buttons')[0],"click");
           if(count==0){
             fail("Update was not called");
           }
           /*Replacing update*/
           instance.update = holder;
        }
        
        function getInstance() {
            if(!$L.isObject(widgetInstance)) {
                widgetInstance = new $W.GraphReport($D.getElementsByClassName("graphreport")[0]);
            }
            return widgetInstance;
        }
        
        function exposeTestFunctionNames() {
            return [
                'testInitializations',
                'testRender',
                'testRefresh'
            ];        
        }
    </script>
</#macro>