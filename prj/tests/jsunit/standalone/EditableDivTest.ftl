<#macro css>
</#macro>
<#macro body>
    <div class="editableDiv">
      <div class="editableDiv-target"></div>
      <div class="edit editableDiv-trigger"></div>
      <div class="submit editableDiv-trigger"></div>
    </div>
</#macro>
<#macro scripts>
    <script src="/static/js/com/i10n/fleet/utils/utils.js"></script>
    <script language="JavaScript" type="text/javascript">
        var $L = YAHOO.lang;
        var $D = YAHOO.util.Dom;
        var $E = YAHOO.util.Element;
        var $WU = getPackageForName("com.i10n.fleet.widget.util");
        var $U = getPackageForName("com.i10n.fleet.Utils");
        
        var editTriggerArea = $D.getElementsByClassName("edit")[0];
        var submitTriggerArea = $D.getElementsByClassName("submit")[0];
        var targetArea = $D.getElementsByClassName("editableDiv-target")[0];
        var editableDiv = $D.getElementsByClassName("editableDiv")[0];
        
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
            var container = $D.getElementsByClassName("editableDiv");
            if(!$L.isObject(container)) {
                fail("The body for the element is not properly loaded");
            }
        }
                
        function testTrivials() {
            /*Exercising Trivial methods to ensure none of them throw any exceptions*/
            $U.EditableDivBuilder.addListeners();
        }
        
        function testOnTrigger() {
        
            /*Setting up the test*/
            args = new Array();
            args[0] = {};
            args[1] = {
                target : editTriggerArea
            };
            $U.EditableDivBuilder.onTrigger(null,args);
            if(!$D.hasClass(editTriggerArea,"disabled")){
                fail("onTrigger not working properly");
            }
            /*Putting everything back in place*/
            args[1] = {
                target: submitTriggerArea
            };
            $U.EditableDivBuilder.onTrigger(null,args);
        }
        
        function testPrepareEngineState(){
            /*
             * Testing for the edit state charateristics
             */
            var textElement = null;
            textElement = $D.getElementsByClassName('input',null,targetArea)[0];
            if($L.isObject(textElement))
                fail("The Edit case condition is not satisfied "+textElement);
            $U.EditableDivBuilder.prepareEngineState(editableDiv,$U.EditableDivBuilder.SUBMIT_STATE);
            /*
             * Testing for the submit state charateristics
             */
             textElement = null;
             textElement = $D.getElementsByClassName('input',null,targetArea)[0];
             if(!$L.isObject(textElement)){
                fail("The Submit case condition is not satisfied");
             }
             var initialValue = textElement.value;
             $U.EditableDivBuilder.prepareEngineState(editableDiv,$U.EditableDivBuilder.EDIT_STATE);
             var finalValue = $D.getElementsByClassName("editableDiv-target")[0].innerHTML;
             assertEquals(initialValue,finalValue);
        }       
        
        function exposeTestFunctionNames() {
            return [
                'testMarkupLoaded',
                'testTrivials',
                'testOnTrigger',
                'testPrepareEngineState'
            ];
        }
    </script>
</#macro>