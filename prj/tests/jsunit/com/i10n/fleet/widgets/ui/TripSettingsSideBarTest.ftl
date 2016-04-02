<#import "/macros/skin.ftm" as skin/>
<#import "/tests/macros/test.ftm" as test/>
<#macro css>
</#macro>
<#macro body>
    <@test.widget name="TripSettingsSideBar"/>
    <@test.widget name="TripSettings"/>
</#macro>
<#macro scripts>
    <@test.widgetscripts name="TripSettingsSideBar"/>
    <@test.widgetscripts name="TripSettings"/>
    <script language="JavaScript" type="text/javascript">
        var $L = YAHOO.lang;
        var $D = YAHOO.util.Dom;
        var $E = YAHOO.util.Element;
        var $W = getPackageForName("com.i10n.fleet.widget.ui");
        var $WU = getPackageForName("com.i10n.fleet.widget.util");
        
        var widgetInstance = null;
        var tripSettingsInstance = null;
        
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
            var container = $D.get("tripsettingssidebar");
            if(!$L.isObject(container)) {
                fail("TripSettingsSideBar Container not loaded");
            }
             
        }
        
        function testInitializations() {
            var instance = getInstance();
            if(!($L.isObject(instance) && $L.isObject(instance.oDataSource) && $L.isObject(instance.oSortableList))) {
                fail("Widgets in TripSettingsSideBar are not initialized");
            }
        }
        
        function testSelectItem() {
            var instance = getInstance();
            /*Setting up the mock*/
            var mockObj1 = $D.getElementsByClassName('item', null, instance.elTsSideBarContainer)[0];
            var mockObj2 = $D.getElementsByClassName('item', null, instance.elTsSideBarContainer)[1];
            var mockObj1Name = $D.getAttribute(mockObj1,'itemname');
            var mockObj2Name = $D.getAttribute(mockObj2,'itemname');
            selectObject(instance,mockObj1,mockObj1Name,mockObj2,mockObj2Name);
        }
        
        function selectObject(target,obj1,name1,obj2,name2){
            $D.addClass(obj1,"selected");
            target.selectItem(name2);
            /*Testing the selection of difference object*/
            if($D.hasClass(obj1,"selected"))
                fail("The old item is not deselected");
            if(!$D.hasClass(obj2,"selected"))
                fail("The old item is not deselected");
        }
        
        function testSetStatus(){
            var instance = getInstance();
            /*Setting up the mock*/
            var mockObj1 = $D.getElementsByClassName('item', null, instance.elTsSideBarContainer)[0];
            var mockStatusDiv = $D.getElementsByClassName("item-status", null, mockObj1)[0];
            var mockObj1ID = $D.getAttribute(mockObj1,'item');
            instance.setStatus(mockObj1ID,$W.TripSettingsSideBar.TRIP_STATUS["STARTED_STATUS"]);
            if(!$D.hasClass(mockStatusDiv,$W.TripSettingsSideBar.TRIP_STATUS["STARTED_STATUS"]))
                fail("Failed to set the status");
            instance.setStatus(mockObj1ID,$W.TripSettingsSideBar.TRIP_STATUS["PAUSED_STATUS"]);
            if($D.hasClass(mockStatusDiv,$W.TripSettingsSideBar.TRIP_STATUS["STARTED_STATUS"]))
                fail("Previously set status is not deselected");
        }
        
        function testAddToList(){
            var instance = getInstance();
            var preCondition =  $D.getElementsByClassName('item', null, instance.elTsSideBarContainer).length;
            instance.addToList({
                    id: "",
                    name: "",
                    status: $W.TripSettingsSideBar.TRIP_STATUS.STOPPED_STATUS
            });
            var postCondition = $D.getElementsByClassName('item', null, instance.elTsSideBarContainer).length;
            if(postCondition != (preCondition +1))
                fail("The element is not properly added to the list");
            /*Removing the most recently added item*/
            $D.getElementsByClassName('item', null, instance.elTsSideBarContainer)[length-1].parentNode.removeChild($D.getElementsByClassName('item', null, instance.elTsSideBarContainer)[length-1]);
        }
        
        function testRemoveStatusClasses() {
            var instance = getInstance();
            /*Setting up the mock*/
            var mockObj1 = $D.getElementsByClassName('item', null, instance.elTsSideBarContainer)[0];
            var mockStatusDiv = $D.getElementsByClassName("item-status", null, mockObj1)[0];
            var mockObj1ID = $D.getAttribute(mockObj1,'item');
            instance.setStatus(mockObj1ID,$W.TripSettingsSideBar.TRIP_STATUS["STARTED_STATUS"]);
            instance.setStatus(mockObj1ID,$W.TripSettingsSideBar.TRIP_STATUS["PAUSED_STATUS"]);
            instance.removeStatusClasses(mockStatusDiv);
            if($D.hasClass(mockStatusDiv,$W.TripSettingsSideBar.TRIP_STATUS["STARTED_STATUS"]) || $D.hasClass(mockStatusDiv,$W.TripSettingsSideBar.TRIP_STATUS["PAUSED_STATUS"]))
                fail("Remove status classes failed");
        }
        
        function testPinning() {
            try
            {
                var instance = getInstance();
                var original = $D.hasClass(instance.elListBaseElement,"pinning-disabled");
                instance.togglePinning();
                var result = $D.hasClass(instance.elListBaseElement,"pinning-disabled");
                assertEquals(!original,result);
            }catch(ex){
                fail("Exception in test pinning "+ ex);
             }
        }
               
        function testDisablePin(){
                var instance = getInstance();
            /*Setting up the mock*/
            var mockObj1 = $D.getElementsByClassName('item', null, instance.elTsSideBarContainer)[0];
            var mockPinDiv = $D.getElementsByClassName("pin", null, mockObj1)[0];
            var mockObj1ID = $D.getAttribute(mockObj1,'item');
            instance._addPin(mockPinDiv);
            instance._addPin(mockPinDiv);
            instance._removePin(mockPinDiv);
            if($D.hasClass(mockPinDiv,"pin-enabled"))
                fail("the method _removePin failed");
        }
        
        function testTrivials() {
            /*Exercising Trivial methods to ensure none of them throw any exceptions*/
            var instance = getInstance();
            instance.addEventListeners();
            instance.setAutoComplete();
            instance.removeSideBarEventListeners();
            instance._generateDataSource();
            instance._resizePane();
            instance.sort();
        }
        
        function getInstance() {
            var tripSettings = getTripSettingsInstance();
            if(!$L.isObject(widgetInstance)) {
                widgetInstance = new $W.TripSettingsSideBar(tripSettings.baseElement);
            }
            return widgetInstance;
        }
        
        function getTripSettingsInstance() {
            if(!$L.isObject(tripSettingsInstance)) {
                tripSettingsInstance = new $W.TripSettings($D.get("tripsettings"));
            }
            return tripSettingsInstance;
        }
        
        function exposeTestFunctionNames() {
            return [
                'testMarkupLoaded',
                'testInitializations',
                'testTrivials',
                'testSelectItem',
                'testSetStatus',
                'testAddToList',
                'testRemoveStatusClasses',
                'testPinning',
                'testDisablePin'
            ];
        }
    </script>
</#macro>