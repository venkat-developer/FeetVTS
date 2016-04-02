<#import "/macros/skin.ftm" as skin/>
<#import "/tests/macros/test.ftm" as test/>
<#macro css>
</#macro>
<#macro body>
  <div class="bd">
    <div class="mesg"></div>
    <form class="input-form">
      <input type="text" class="input-element"/>
    </form>
    <@skin.widget name="Buttons" params={"cssClass":"inline-block buttons save-button","style":"blue","class":"save-but"}>Add</@skin.widget>  
  </div>
</#macro>
<#macro scripts>
    <@test.widgetscripts name="AddEntity"/>
    <script language="JavaScript" type="text/javascript">
        var $L = YAHOO.lang;
        var $D = YAHOO.util.Dom;
        var $W = getPackageForName("com.i10n.fleet.widget.ui");
        var DOMEventUtils =  getPackageForName("com.i10n.fleet.widget.test.DOMEventUtils");
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
            if(!$L.isObject($D.getElementsByClassName("input-form")[0])) {
                fail("The mark up is not loaded");
            }
        }
        
        function testInitializations() {
            var instance = getInstance();
            assertEquals(instance.oConfiguration.formClass,"input-form");
            assertEquals(instance.oConfiguration.saveButtonClass,"save-button");
            assertEquals(instance.oConfiguration.inputElementClass,"input-element");
        }
        
        function testConfiguration(){
           var instance = new $W.AddEntity($D.getElementsByClassName("bd")[0],{
               formClass: "test",
               saveButtonClass: "test",
               inputElementClass: "test"
           });
           var config = instance.oConfiguration;
           if(!(config.formClass == config.saveButtonClass && config.formClass == config.inputElementClass)){
               fail("The widget is not configured properly");
           }
        }
        
        function testRefresh(){
            var instance = getInstance();
            var initialTextboxEntry = "test";
            $D.getElementsByClassName("input-element")[0].value = initialTextboxEntry;
            var initialMessage = $D.getElementsByClassName("mesg")[0].innerHTML;
            instance.refresh();        
            var finalTextboxEntry = $D.getElementsByClassName("input-element")[0].value;
            var finalMessage = $D.getElementsByClassName("mesg")[0].innerHTML;
            assertNotEquals(initialMessage,finalMessage);
            assertNotEquals(initialTextboxEntry,finalTextboxEntry);
        }
        
         function testAddWorkFlow(){
            /*Setting up the test*/
            var instance = getInstance();
            var flag = false;
            var temp = instance.saveEntity;
            instance.saveEntity= function(oInput){
                flag = true;
            };
            /*Firing the event*/
            DOMEventUtils.fireEvent($D.getElementsByClassName("fleet-buttons")[0],"click");            
            if(!flag){
                fail("The save function is not triggered when add button is clicked");
            }
            /*Tearing down*/
            instance.saveEntity = temp;
        }
                
        function getInstance() {
            if(!$L.isObject(widgetInstance)) {
                widgetInstance = new $W.AddEntity($D.getElementsByClassName("bd")[0]);
            }
            return widgetInstance;
        }
        
        function exposeTestFunctionNames() {
            return [
                'testMarkupLoaded',
                'testInitializations',
                'testConfiguration',
                'testRefresh',
                'testAddWorkFlow'
            ];
        }
    </script>
</#macro>