<#import "/macros/skin.ftm" as skin/>
<#import "/tests/macros/test.ftm" as test/>
<#macro css>
</#macro>
<#macro body>
  <div class="bd">
    <div class="edit-sheet">
        <div class="mesg"></div>
        <form class="input-form">
          <input type="text" name="test" class="input-element"/>
        </form>
        <@skin.widget name="Buttons" params={"cssClass":"inline-block buttons save-button","style":"blue","class":"save-but"}>Add</@skin.widget>
        <@skin.widget name="Buttons" params={"cssClass":"inline-block buttons cancel-button","style":"blue","class":"save-but"}>Cancel</@skin.widget>
    </div>  
  </div>
</#macro>
<#macro scripts>
    <@test.widgetscripts name="EditEntity"/>
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
            assertEquals(instance.oConfiguration.editSheetClass,"edit-sheet");
            assertEquals(instance.oConfiguration.cancelButtonClass,"cancel-button");
        }
        
        function testConfiguration(){
           var instance = new $W.EditEntity($D.getElementsByClassName("bd")[0],{
               formClass: "test",
               saveButtonClass: "test",
               inputElementClass: "test",
               editSheetClass:"test",
               cancelButtonClass:"test"
           });
           var config = instance.oConfiguration;
           if(!("test" == config.formClass &&
                "test" == config.saveButtonClass &&
                "test" == config.inputElementClass && 
                "test" == config.editSheetClass &&
                "test" == config.cancelButtonClass)){
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
        
         function testEntitySelectedWorkFlow(){
            /*Setting up the test*/
            var instance = getInstance();
            var flag = false;
            var temp = instance.getEntity;
            instance.getEntity= function(oInput){
                flag = true;
            };
            /*Firing the event*/
            instance.fireEvent($W.EditEntity.EVT_SELECTED_ENTITY_CHANGE);            
            if(!flag){
                fail("The save function is not triggered when add button is clicked");
            }
            /*Tearing down*/
            instance.saveEntity = temp;
        }
        
        function testProcessData(){
             var testData = {
                 test: "test"
             };   
             var instance = getInstance();
             instance.processData(testData);
             assertEquals("test",$D.getElementsByClassName("input-element")[0].value);
        }
        
        function testShowEditSheet(){
            var instance = getInstance();
            var editSheet = $D.getElementsByClassName("edit-sheet")[0];
            var inputForm = $D.getElementsByClassName("input-form")[0];
            $D.addClass(editSheet,"disabled");
            $D.addClass(inputForm,"disabled");
            instance.showEditSheet();
            if($D.hasClass(editSheet,"disabled") || $D.hasClass(inputForm,"disabled")){
               fail("testShowEditSheet failed");
            }
        }
        
        function testShowInputForm(){
            var instance = getInstance();
            var inputForm = $D.getElementsByClassName("input-form")[0];
            $D.addClass(inputForm,"disabled");
            instance.showInputForm();
            if($D.hasClass(inputForm,"disabled")){
               fail("testShowInputForm failed");
            }
        }
        
        function testHideEditSheet(){
            var instance = getInstance();
            var editSheet = $D.getElementsByClassName("edit-sheet")[0];
            var inputForm = $D.getElementsByClassName("input-form")[0];
            instance.hideEditSheet();
            if(!$D.hasClass(editSheet,"disabled") || !$D.hasClass(inputForm,"disabled")){
               fail("testHideEditSheet failed");
            }
        }
        
        function testHideInputForm(){
            var instance = getInstance();
            var inputForm = $D.getElementsByClassName("input-form")[0];
            instance.hideInputForm();
            if(!$D.hasClass(inputForm,"disabled")){
               fail("testHideInputForm failed");
            }
        }
                
        function getInstance() {
            if(!$L.isObject(widgetInstance)) {
                widgetInstance = new $W.EditEntity($D.getElementsByClassName("bd")[0]);
            }
            return widgetInstance;
        }
        
        function exposeTestFunctionNames() {
            return [
                'testMarkupLoaded',
                'testInitializations',
                'testConfiguration',
                'testRefresh',
                'testEntitySelectedWorkFlow',
                'testProcessData',
                'testShowEditSheet',
                'testHideEditSheet',
                'testShowInputForm',
                'testHideInputForm'
            ];
        }
    </script>
</#macro>