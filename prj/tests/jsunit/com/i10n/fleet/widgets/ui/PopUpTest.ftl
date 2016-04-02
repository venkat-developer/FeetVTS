<#import "/macros/skin.ftm" as skin/>
<#import "/tests/macros/test.ftm" as test/>
<#macro css>
</#macro>
<#macro body>
    <@skin.widget name="PopUp"/>
</#macro>
<#macro scripts>
    <@test.widgetscripts name="PopUp"/>
    <script language="JavaScript" type="text/javascript">
        var $L = YAHOO.lang;
        var $D = YAHOO.util.Dom;
        var $W = getPackageForName("com.i10n.fleet.widget.ui");
        
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
        
        
        function testInit() {
            var oTestConfig = {
                width:"auto",
                height:"auto"
            };
            var testPopUp = new $W.PopUp("testDiv",oTestConfig);
            assertNotNull(testPopUp.elBase);
        } 
        
        function testWidgetCreated()
        {
            var testConfig = {
                width:"auto",
                height:"auto"
            };
            var testPopUp = new $W.PopUp("testDiv",testConfig);
            assertNotNull(testPopUp.render);
        }
                
        function exposeTestFunctionNames() {
            return [
     
                'testInit',
                'testWidgetCreated'
            ];
        }
    </script>
</#macro>
