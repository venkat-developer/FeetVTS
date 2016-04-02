<#import "/macros/skin.ftm" as skin/>
<#import "/tests/macros/test.ftm" as test/>
<#macro css>
</#macro>
<#macro body>
  <div class="container">
    <div class="state-view">
      <div class="state-item inline-block">
         <span class="state-item-index">1</span>
         Select the User
      </div>
      <div class="state-item inline-block disabled">
          <span class="state-item-index">2</span>
          Add or Remove Entities for the user selected
      </div>
      <div class="state-item inline-block disabled">
          <span class="state-item-index">3</span>
          Save Changes
      </div>
    </div>
    <div class="bd">
      <div class="user-list-cnt list-cnt inline-block">
         <div class="user-title list-title">
            User List (<span class="user-count list-count">8</span>)
         </div>
         <div class="user-list list" size="12" name="user"
         ><div class="user-list-item list-item slist-item" value="s">User 1</div
         ><div class="user-list-item list-item slist-item" value="s2">User 2</div
         ><div class="user-list-item list-item slist-item" value="s3">User 3</div
         ><div class="user-list-item list-item slist-item" value="s4">User 4</div
         ><div class="user-list-item list-item slist-item" value="s5">User 444 S</div
         ><div class="user-list-item list-item slist-item" value="s6">User 333 D</div
         ><div class="user-list-item list-item slist-item" value="s7">User 111 E</div
         ></div>
      </div>
      <div class="assignment-control assigned-list-cnt list-cnt inline-block">
        <div class="assng-title list-title">
           Assigned Vehicles (<span class="assng-count list-count">10</span>)
        </div>
        <div class="assigned-list assignment-control-list list" name="assng-veh" size="12"
        ><div class="assng-list-item list-item slist-item" assigned="true" value="s">Vehicle A 1</div
        ><div class="assng-list-item list-item slist-item" assigned="true" value="s2">Vehicle A 2</div
        ><div class="assng-list-item list-item slist-item" assigned="true" value="s3">Vehicle A 3</div
        ><div class="assng-list-item list-item slist-item" assigned="true" value="s4">Vehicle A 4</div
        ><div class="assng-list-item list-item slist-item" assigned="true" value="s5">Vehicle A 444 S</div
        ><div class="assng-list-item list-item slist-item" assigned="true" value="s6">Vehicle A 333 D</div
        ><div class="assng-list-item list-item slist-item" assigned="true" value="s7">Vehicle A 111 E</div
        ></div>
      </div>
      <div class="assignment-control vacant-list-cnt list-cnt inline-block">
        <div class="vac-title list-title">
            Vacant Vehicles (<span class="vac-count list-count">11</span>)
        </div>
        <div class="vacant-list assignment-control-list list" name="vac-veh"
        ><div class="vac-list-item list-item slist-item" assigned="false" value="s">Vehicle 1</div
        ><div class="vac-list-item list-item slist-item" assigned="false" value="s2">Vehicle 2</div
        ><div class="vac-list-item list-item slist-item" assigned="false" value="s3">Vehicle 3</div
        ><div class="vac-list-item list-item slist-item" assigned="false" value="s4">Vehicle 4</div
        ><div class="vac-list-item list-item slist-item" assigned="false" value="s5">Vehicle 444 S</div
        ><div class="vac-list-item list-item slist-item" assigned="false" value="s6">Vehicle 333 D</div
        ><div class="vac-list-item list-item slist-item" assigned="false" value="s7">Vehicle 111 E</div
        ></div>
      </div>
    </div>
    <div class="ft">
        <@skin.widget name="Buttons" params={"cssClass":"inline-block assignment-control remove-button buttons","style":"red","class":"rem-button"}><div class="rem-text">Remove</div></@skin.widget>
        <@skin.widget name="Buttons" params={"cssClass":"inline-block assignment-control add-button buttons","style":"blue","class":"add-button"}><div class="add-text">Add</div></@skin.widget>
        <@skin.widget name="Buttons" params={"cssClass":"inline-block assignment-control save-button buttons","style":"blue","class":"save-button"}>Save Changes</@skin.widget>
    </div>
  </div>
</#macro>
<#macro scripts>
    <@test.widgetscripts name="Assignment"/>
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
            if(!$L.isObject($D.getElementsByClassName("container")[0])) {
                fail("The mark up is not loaded");
            }
        }
        
        function testInitializations() {
            var instance = getInstance();
            assertEquals(instance.oConfiguration.userListClass,"user-list");
            assertEquals(instance.oConfiguration.userListContainerClass,"user-list-cnt");
            assertEquals(instance.oConfiguration.assignmentControlClass,"assignment-control");
            assertEquals(instance.oConfiguration.assignmentControlListClass,"assignment-control-list");
            assertEquals(instance.oConfiguration.vacantListClass,"vacant-list");
            assertEquals(instance.oConfiguration.vacantListContainerClass,"vacant-list-cnt");
            assertEquals(instance.oConfiguration.assignedListClass,"assigned-list");
            assertEquals(instance.oConfiguration.assignedListContainerClass,"assigned-list-cnt");
            assertEquals(instance.oConfiguration.removeButtonClass,"remove-button");
            assertEquals(instance.oConfiguration.addButtonClass,"add-button");
            assertEquals(instance.oConfiguration.saveButtonClass,"save-button");
        }
        
        function testRefresh() {
            var instance = getInstance();
            instance.refresh();
            if($D.getElementsByClassName("state-item inline-block disabled").length != 2){
               fail("The proper number of state-items are not disabled when the widget is refreshed");
            }
            $D.getElementsByClassName("assignment-control", null, null, function(elTarget) {
                if (!$D.hasClass(elTarget, "disabled")) {
                    fail("One or more assignment controls are not disabled when the widget is refreshed");
                }
            });
        }
        
        function testSetState(){
            var instance = getInstance();
            instance.setState($W.Assignment.ASSIGNMENT_STATES.STATE_TWO);
            if($D.getElementsByClassName("state-item inline-block disabled").length != 1){
               fail("The proper number of state-items are not disabled when the widget is refreshed");
            }
        }
        
        function testShowAssignmentControls(){
              var instance = getInstance();
              instance.showAssignmentControls();
              var hiddenControls = 0;
              $D.getElementsByClassName(instance.oConfiguration.assignmentControlClass, null, this.elBase,function(elTarget){
                  if ($D.hasClass(elTarget, "disabled")) {
                    hiddenControls++;
                  }
              });
              if(hiddenControls != 0){
                  fail("showAssignmentControls failed");
              }
        }
        
        function testHideAssinmentControls(){
             var instance = getInstance();
             instance.hideAssignmentControls();
             var visibleControls = 0;
             $D.getElementsByClassName(instance.oConfiguration.assignmentControlClass, null, this.elBase,function(elTarget){
                 if (!$D.hasClass(elTarget, "disabled")) {
                   visibleControls++;
                 }
             });
             if(visibleControls != 0){
                 fail("hideAssignmentControls failed");
             }
        }
        
        function testToggleAssignmentControls(){
          var instance = getInstance();
          /*Tested code*/
          instance.hideAssignmentControls();
          instance.toggleAssignmentControls();
          var hiddenControls = 0;
          $D.getElementsByClassName(instance.oConfiguration.assignmentControlClass, null, this.elBase,function(elTarget){
              if ($D.hasClass(elTarget, "disabled")) {
                hiddenControls++;
              }
          });
          if(hiddenControls != 0){
              fail("showAssignmentControls failed");
          }
        }
        
        function testCountAssignedList(){
          var instance = getInstance();
          instance.countAssignedList();
          /*Verifying the count*/
          var calculatedCount = $D.getElementsByClassName("assng-count")[0].innerHTML;
          assertEquals(calculatedCount,"7");        
        }
        
        function testCountVacantList(){
          var instance = getInstance();
          instance.countVacantList();
          /*Verifying the count*/
          var calculatedCount = $D.getElementsByClassName("vac-count")[0].innerHTML;
          assertEquals(calculatedCount,"7");
        }
        
        function testCountUserList(){
          var instance = getInstance();
          instance.countUserList();
          /*Verifying the count*/
          var calculatedCount = $D.getElementsByClassName("user-count")[0].innerHTML;
          assertEquals(calculatedCount,"7");
        }
        
        function testOnSave(){
          /*Setting up the test*/
          var instance = getInstance();
          /*Mocking essential stuff*/
          var saveChanges = instance.onSave();
          var flag = false;
          instance.saveChanges = function(oInput){
                              flag = true;
                            };
          /*Firing the test*/
          DOMEventUtils.fireEvent($D.getElementsByClassName("save-button")[0],"click");
          if(!flag){
            fail("The function on save failed");
          }
          /*Rolling back*/
          instance.saveChanges = saveChanges;
        }
        
        function testOnMove(){
          /*Setting up the test*/
          var instance = getInstance();
          var elAssignedList = $D.getElementsByClassName("assigned-list")[0];
          var elVacantList = $D.getElementsByClassName("vacant-list")[0];
          /*Performing the test*/
          var assignedElements = $D.getElementsByClassName("list-item",null,elAssignedList);
          $D.addClass(assignedElements[0],"selected");
          $D.addClass(assignedElements[4],"selected");
          DOMEventUtils.fireEvent($D.getElementsByClassName("remove-button")[0],"click");
          var vacantElements = $D.getElementsByClassName("list-item",null,elVacantList);
          $D.addClass(vacantElements[3],"selected");
          DOMEventUtils.fireEvent($D.getElementsByClassName("add-button")[0],"click");
          /*Verifying*/
          var assignedLength = $D.getElementsByClassName("list-item",null,elAssignedList).length;
          var vacantLength = $D.getElementsByClassName("vac-list-item",null,elVacantList).length;
          if(assignedLength+1 != vacantLength){
            fail("The function onMove failed");
          }
        }
                
        function getInstance() {
            if(!$L.isObject(widgetInstance)) {
                widgetInstance = new $W.Assignment($D.getElementsByClassName("container")[0]);
            }
            return widgetInstance;
        }
        
        function exposeTestFunctionNames() {
            return [
                'testMarkupLoaded',
                'testInitializations',
                'testRefresh',
                'testSetState',
                'testShowAssignmentControls',
                'testHideAssinmentControls',
                'testToggleAssignmentControls',
                'testCountAssignedList',
                'testCountVacantList',
                'testCountUserList',
                'testOnMove',
                'testOnSave'
            ];
        }
    </script>
</#macro>