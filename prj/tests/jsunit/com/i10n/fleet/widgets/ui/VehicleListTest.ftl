<#import "/macros/skin.ftm" as skin/>
<#import "/tests/macros/test.ftm" as test/>
<#macro css>
</#macro>
<#macro body>
    <@skin.widget name="VehicleList" params={"cssClass":"inactive minimizable sidepanelist vehiclelist"} />
</#macro>
<#macro scripts>
    <@test.widgetscripts name="VehicleList" />
    <@test.widgetscripts name="GroupedReport" />
    <script language="JavaScript" type="text/javascript">
        var $B = YAHOO.Bubbling;
        var $L = YAHOO.lang;
        var $YU = YAHOO.util;
        var $E = YAHOO.util.Event;
        var $D = YAHOO.util.Dom;
        var $YW = YAHOO.widget;
        var $W = getPackageForName("com.i10n.fleet.widget.ui");
        var $WU = getPackageForName("com.i10n.fleet.widget.util");
        
        var el, vehicleList;
        
        function init() {
            el = $D.get('vehiclelist');
            vehicleList = new $W.VehicleList(el,{
                          datasource: getVehicleDataSource()
                          });
        } 

		function getVehicleDataSource() {
            var oDataSource = null;
            if (_publish.vehicles && _publish.vehicles.vehicleData) {
                oDataSource = new $W.GroupedReport.GroupedDataSource(_publish.vehicles.vehicleData);
                oDataSource.responseType = $YU.DataSource.TYPE_JSARRAY;
                oDataSource.responseSchema = {
                    elementField: "vehicles",
                    fields: ["id", "name", "status", "chargerdc", "gps", "gsm", "battery", "fuel", "lastupdated", "lat", "lon", "location", "make", "model", "speed", "drivername"]
                };
            }
            return oDataSource;
        };    
          
        function setUpPage(){
            inform('setUpPage()');
            setUpPageStatus = 'running';
            setTimeout('setUpPageComplete()', 30);
            init();
        }
        
        function setUpPageComplete(){
            if (setUpPageStatus == 'running') 
                setUpPageStatus = 'complete';
            inform('setUpPageComplete()', setUpPageStatus);
        }
                
        
        function testAddAndTogglePin(){
            var vehiclesToBePinned = $D.getElementsBy(function(target){
                return ($D.hasClass(target, 'item'));
            }, null, vehicleList.elVehicleListContainer);
            for (var i = 0; i < vehiclesToBePinned.length; i++) {
                var vehicle = vehiclesToBePinned[i];
                vehicleList._addPin(vehicle);
                assertTrue($D.hasClass(vehicle, 'pin-enabled'));
                vehicleList.togglePin(vehicle);
                assertTrue(!$D.hasClass(vehicle, 'pin-enabled'));
            }
        }
        
        function testRemovePin(){
            var pinnedVehicle = $D.getElementBy(function(target){
                return ($D.hasClass(target, 'item'));
            }, null, vehicleList.elVehicleListContainer);
            vehicleList._addPin(pinnedVehicle);
            assertTrue($D.hasClass(pinnedVehicle, 'pin-enabled'));
            vehicleList._removePin(pinnedVehicle);
            assertTrue(!$D.hasClass(pinnedVehicle, 'pin-enabled'));
        }
        
        function testEnableAndDisableGrouping(){
            vehicleList.disableGrouping();
            var baseEl = $D.getElementsByClassName("sidepane-list", null, vehicleList.elVehicleListContainer)[0];
            assertTrue($D.hasClass(baseEl, 'group-disabled'));
            vehicleList.enableGrouping();
            assertTrue(!$D.hasClass(baseEl, 'group-disabled'));
        }
        
        function testSelectGroup(){
            var groups = $D.getElementsByClassName('group-item', null, vehicleList.elVehicleListContainer);
            for (var i = 0; i < groups.length; i++) {
                var group = groups[i];
                vehicleList.selectGroup($D.getAttribute(group, 'group'));
                assertTrue(!$D.hasClass(group, 'disabled'));
            }
        }
        
        function testEnableAndDisablePinning(){
            vehicleList.enablePinning();
            assertTrue(!$D.hasClass(vehicleList.listBaseElement, 'pinning-disabled'));
            vehicleList.disablePinning();
            assertTrue($D.hasClass(vehicleList.listBaseElement, 'pinning-disabled'));
        }
        
        function testEnableAndDisableState(){
            vehicleList.disable();
            assertTrue($D.hasClass(vehicleList.elVehicleListContainer, "state-disabled"));
            vehicleList.enable();
        }
        
        function testGroupPinComparator(){
            var groups = $D.getElementsByClassName('group-item', null, vehicleList.elVehicleListContainer);
            if (groups.length > 1) {
                var group1 = groups[0];
                var group2 = groups[1];
                
                var group1name = $D.getAttribute(group1, 'group');
                var group2name = $D.getAttribute(group2, 'group');
                
                if (group1name > group2name) {
                    assertEquals(1, $W.SidePaneList.groupPinComparator(group1, group2));
                }
                else if (group1name < group2name) {
                    assertEquals(-1, $W.SidePaneList.groupPinComparator(group1, group2));
                }
            }
        }
        
        function testListPinComparator(){
            var vehicles = $D.getElementsByClassName('item', null, vehicleList.elVehicleListContainer);
            if (vehicles.length > 1) {
                var pinnedVehicle1 = vehicles[0];
                var pinnedVehicle2 = vehicles[1];
                vehicleList._addPin(pinnedVehicle1);
                vehicleList._removePin(pinnedVehicle2);
                assertEquals(-1, $W.SidePaneList.listPinComparator(pinnedVehicle1, pinnedVehicle2));
                vehicleList._addPin(pinnedVehicle2);
                assertEquals(1, $W.SidePaneList.listPinComparator(pinnedVehicle2, pinnedVehicle1));
                assertEquals(0, $W.SidePaneList.listPinComparator(pinnedVehicle1, pinnedVehicle1));
            }
        }
        
        function isSorted(items){
            var prevValue = "";
            for (var i = 0; i < items.length; i++) {
                var _item = items[i];
                var currentValue = $D.getAttribute(_item, 'itemname');
                if (currentValue < prevValue) {
                    return (-1);
                }
            }
            return 0;
        }
        
        function testSort(){
            vehicleList.disablePinning();
            vehicleList.disableGrouping();
            vehicleList.sort();
            
            var vehicles = $D.getElementsByClassName('item', null, vehicleList.elVehicleListContainer);
            assertEquals(0, isSorted(vehicles));
            
            vehicleList.enableGrouping();
            vehicleList.sort();
            
            var groups = $D.getElementsByClassName('group-item', null, vehicleList.elVehicleListContainer);
            if (groups.length > 0) {
                var groupName = $D.getAttribute(groups[(groups.length - 1)], 'group');
                var groupVehicles = $D.getElementsBy(function(target){
                    if ($D.getAttribute(target, 'group') == groupName) 
                        return true;
                }, null, vehicleList.elVehicleListContainer);
                assertEquals(0, isSorted(groupVehicles));
            }
        }
        
        /**
         * Test for selection of vehicle is split as 2 tests, testSelectVehicle & testVehicleSelection
         */
        function testSelectVehicle(){
            var vehicleToBeSelected = $D.getElementsByClassName('item', null, vehicleList.elSideListBase)[0];
            vehicleList.selectItem($D.getAttribute(vehicleToBeSelected, 'item'));
        }
        
        function testVehicleSelection(){
            var vehicleToBeSelected = $D.getElementsByClassName('item', null, vehicleList.elSideListBase)[0];
            assertTrue($D.hasClass(vehicleToBeSelected, 'selected'));
        }
        
        function exposeTestFunctionNames(){
            return [
                'testSelectVehicle',
                'testAddAndTogglePin',
                'testEnableAndDisableGrouping',
                'testRemovePin',
                'testSelectGroup',
                'testEnableAndDisableState',
                'testEnableAndDisablePinning',
                'testGroupPinComparator',
                'testListPinComparator',
                'testVehicleSelection',
                'testSort'
            ];
        }
    </script>
</#macro>