<#import "/macros/skin.ftm" as skin/>
<#import "/tests/macros/test.ftm" as test/>
<#macro css>
</#macro>
<#macro body>
   <div class="container">
    <div class="state-view">
        <div class="state-item inline-block">
            <span class="state-item-index">1</span>
            Select the users that you need to delete
        </div>
        <div class="state-item inline-block disabled">
            <span class="state-item-index">2</span>
            Click Delete User or Click Cancel to deselect all users
        </div>
        <div class="state-item inline-block disabled">
            <span class="state-item-index">3</span>
            Confirm Deletion
        </div>
    </div>
    <div class="note info"><span class="title">Please Note:</span> On deleting a user, all the associated details will be deleted.</div>
    <div class="bd">
        <div class="title">User List (<span class="user-count list-count">2</span>)</div>
        <div class="user-list input-list">
            <table class="tlist">
                <tbody>
                    <tr class="l-row hd-row">
                        <td class="l-col firstname first">
                            First Name
                        </td
                        ><td class="l-col lastname">
                            Last Name
                        </td
                        ><td class="l-col loginid">
                            Login ID
                        </td>
                    </tr>
                    <tr class="l-row entity-record slist-item first">
                        <td class="l-col firstname first"><input name="user" value="user-2" class="input-element delete-entity-checkbox" type="checkbox"></input>User 2</td
                        ><td class="l-col lastname">User 2</td
                        ><td class="l-col loginid">User-2</td>
                    </tr>
                    <tr class="l-row entity-record slist-item">
                        <td class="l-col firstname first"><input name="user" value="user-3" class="input-element delete-entity-checkbox" type="checkbox"></input>User 3</td
                        ><td class="l-col lastname">User 3</td
                        ><td class="l-col loginid">user-3</td>
                    </tr>
                </tbody>
            </table>
        </div>
    </div>
    <div class="ft">
        <@skin.widget name="Buttons" params={"cssClass":"inline-block delete-button buttons","style":"red","class":"del-button"}>Delete User</@skin.widget>
        <@skin.widget name="Buttons" params={"cssClass":"inline-block cancel-button buttons","style":"blue","class":"can-button"}>Cancel</@skin.widget>
    </div>
    <@skin.widget name="ConfirmationPopUp" params={
       "containerID":"DeleteUserConfirmation",
       "header": "Confirm Deletion of Users"
    }>
      Are you sure you want to delete all the selected users?
    </@skin.widget>
   </div>
</#macro>
<#macro scripts>
    <@test.widgetscripts name="DeleteEntity"/>
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
            if(!$L.isObject($D.getElementsByClassName("bd")[0])) {
                fail("The mark up is not loaded");
            }
        }
        
        function testInitializations() {
            var instance = getInstance();
            assertEquals(instance.oConfiguration.listClass,"input-list");
            assertEquals(instance.oConfiguration.listContainerClass,"bd");
            assertEquals(instance.oConfiguration.deleteButtonClass,"delete-button");
            assertEquals(instance.oConfiguration.cancelButtonClass,"cancel-button");
            assertEquals(instance.oConfiguration.checkBoxClass,"delete-entity-checkbox");
            assertEquals(instance.oConfiguration.recordClass,"entity-record");
        }
        
        function testConfiguration(){
           var instance = new $W.DeleteEntity($D.getElementsByClassName("container")[0],{
               listClass: "test",
               saveButtonClass: "test"
           });
           var config = instance.oConfiguration;
           if(!(config.listClass == config.saveButtonClass)){
               fail("The widget is not configured properly");
           }
        }
        
        function testSetState(){
            var instance = getInstance();
            instance.setState($W.DeleteEntity.DELETION_STATES.STATE_TWO);
            if($D.getElementsByClassName("state-item inline-block disabled").length != 1){
               fail("The proper number of state-items are not disabled when the widget is refreshed");
            }
        }
        
        function testOnConfirm(){
            /*Setting up the test*/
            var instance = getInstance();
            var checkboxNo = $D.getElementsByClassName("delete-entity-checkbox",null,null,function(elTarget){
              if(!elTarget.checked){
                elTarget.checked = true;
              }
            }).length;
            /*Mocking necessary elements*/
            var deleteEntity = instance.deleteEntity;
            var noOfElements = 0;
            instance.deleteEntity = function(sInput){
               noOfElements = sInput.length;
            };
            /*Firing the deletion*/
            DOMEventUtils.fireEvent($D.getElementsByClassName("yes-button")[0],"click");
            /*Verifying*/
            if(checkboxNo !=  noOfElements){
              fail("The function onConfirm failed");
            }
            /*Rolling back*/
            instance.deleteEntity = deleteEntity;
        }
        
        function testOnEntitySelected(){
          /*
           * Expected behavior: The checkbox must be selected if either the check box or any of the cells contained
           * in entity-record are clicked
           */        
           /*There will be Nine elements in the listOfColumns. A set of three belongs to each record*/
           var instance = getInstance();
           instance.onCancel();
           var listOfColumns = $D.getElementsByClassName("l-col");     
           DOMEventUtils.fireEvent(listOfColumns[3],"click");
           var noOfCheckBoxSelected = 0;
           $D.getElementsByClassName("delete-entity-checkbox",null,null,function(elTarget){
              if(elTarget.checked){
                noOfCheckBoxSelected++;
              }
           });
           if(noOfCheckBoxSelected != 1){
              fail("proper number of checkboxes are not selected");
           }
           DOMEventUtils.fireEvent(listOfColumns[4],"click");
           DOMEventUtils.fireEvent(listOfColumns[5],"click");
           noOfCheckBoxSelected =0;
           $D.getElementsByClassName("delete-entity-checkbox",null,null,function(elTarget){
              if(elTarget.checked){
                noOfCheckBoxSelected++;
              }
           });
           if(noOfCheckBoxSelected != 1){
              fail("proper number of checkboxes are not selected");
           }
        }
                
        function getInstance() {
            if(!$L.isObject(widgetInstance)) {
                widgetInstance = new $W.DeleteEntity($D.getElementsByClassName("container")[0]);
            }
            return widgetInstance;
        }
        
        function exposeTestFunctionNames() {
            return [
                'testMarkupLoaded',
                'testInitializations',
                'testConfiguration',
                'testSetState',
                'testOnConfirm',
                'testOnEntitySelected'
            ];
        }
    </script>
</#macro>