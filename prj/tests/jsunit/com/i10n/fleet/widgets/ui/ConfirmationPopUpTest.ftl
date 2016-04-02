<#import "/macros/skin.ftm" as skin/>
<#import "/tests/macros/test.ftm" as test/>

<#macro css>
</#macro>
<#macro body>
  <@test.widget name="ConfirmationPopUp" params={'containerID':'test'}/>
</#macro>
<#macro scripts>
    <@test.widgetscripts name="ConfirmationPopUp"/>
    <script language="JavaScript" type="text/javascript">
        var $L = YAHOO.lang;
        var $D = YAHOO.util.Dom;
        var $W = getPackageForName("com.i10n.fleet.widget.ui");
        
        var widgetInstance = null;
        var config={close:true};
        
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
            if(!$L.isObject($D.get("test"))) {
                fail("Confirmation Container not loaded");
            }
        }
        
        function testInitializations() {
            var instance = getInstance();
            assertEquals(instance.elBase,$D.get("test"));
            assertFalse($L.isNull(instance.cfg));
            assertFalse($L.isNull(instance.cfg.getProperty("close")));
     
        }
                
        function getInstance() {
            if(!$L.isObject(widgetInstance)) {
                widgetInstance = new $W.ConfirmationPopUp("test",config);
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