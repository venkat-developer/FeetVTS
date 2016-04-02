<#import "/macros/skin.ftm" as skin/>
<#import "/tests/macros/test.ftm" as test/>
<#macro css>
</#macro>
<#macro body>
    <@skin.widget name="DriverList" params={"cssClass":"inactive minimizable sidepanelist driverlist"}/>
</#macro>
<#macro scripts>
    <@test.widgetscripts name="DriverList" />
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
        
        var el, driverList;
        
        /**
         * Initialize the widget for testing
         */
        function init() {
            el = $D.get('driverlist');
            driverList = new $W.DriverList(el,{
                          datasource: getDriverDataSource()
                          });
        } 
        
        function getDriverDataSource() {
            var oDataSource = null;
            if (_publish.drivers && _publish.drivers.driverData) {
                oDataSource = new $W.GroupedReport.GroupedDataSource(_publish.drivers.driverData);
                oDataSource.responseType = $YU.DataSource.TYPE_JSARRAY;
                oDataSource.responseSchema = {
                    elementField: "drivers",
                    fields: ["id", "name", "status", "firstname", "lastname", "license", "maxspeed", "avgspeed", "distance", "assigned", "group"]
                };
            }
            return oDataSource;
        }
        
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
            var driversToBePinned = $D.getElementsBy(function(target){
                return ($D.hasClass(target, 'item'));
            }, null, driverList.elDriverListContainer);
            for (var i = 0; i < driversToBePinned.length; i++) {
                var driver = driversToBePinned[i];
                driverList._addPin(driver);
                assertTrue($D.hasClass(driver, 'pin-enabled'));
                driverList.togglePin(driver);
                assertTrue(!$D.hasClass(driver, 'pin-enabled'));
            }
        }
        
        function testRemovePin(){
            var pinnedDriver = $D.getElementBy(function(target){
                return ($D.hasClass(target, 'item'));
            }, null, driverList.elDriverListContainer);
            driverList._addPin(pinnedDriver);
            assertTrue($D.hasClass(pinnedDriver, 'pin-enabled'));
            driverList._removePin(pinnedDriver);
            assertTrue(!$D.hasClass(pinnedDriver, 'pin-enabled'));
        }
        
        function testEnableAndDisableGrouping(){
            driverList.disableGrouping();
            var baseEl = $D.getElementsByClassName("sidepane-list", null, driverList.elDriverListContainer)[0];
            assertTrue($D.hasClass(baseEl, 'group-disabled'));
            driverList.enableGrouping();
            assertTrue(!$D.hasClass(baseEl, 'group-disabled'));
        }
        
        function testSelectGroup(){
            var groups = $D.getElementsByClassName('group-item', null, driverList.elDriverListContainer);
            for (var i = 0; i < groups.length; i++) {
                var group = groups[i];
                driverList.selectGroup($D.getAttribute(group, 'group'));
                assertTrue(!$D.hasClass(group, 'disabled'));
            }
        }
        
        function testEnableAndDisablePinning(){
            driverList.enablePinning();
            assertTrue(!$D.hasClass(driverList.listBaseElement, 'pinning-disabled'));
            driverList.disablePinning();
            assertTrue($D.hasClass(driverList.listBaseElement, 'pinning-disabled'));
        }
        
        function testEnableAndDisableState(){
            driverList.disable();
            assertTrue($D.hasClass(driverList.elDriverListContainer, "state-disabled"));
            driverList.enable();
        }
        
        /**
         * Test for selection of driver is split as 2 tests, testSelectDriver & testDriverSelection
         */
        function testSelectDriver(){
            var driverToBeSelected = $D.getElementsByClassName('item', null, driverList.elSideListBase)[0];
            driverList.selectItem($D.getAttribute(driverToBeSelected, 'item'));
        }
        
        function testDriverSelection(){
            var driverToBeSelected = $D.getElementsByClassName('item', null, driverList.elSideListBase)[0];
            assertTrue($D.hasClass(driverToBeSelected, 'selected'));
        }
        
        function exposeTestFunctionNames(){
            return [
                'testSelectDriver',
                'testAddAndTogglePin',
                'testEnableAndDisableGrouping',
                'testRemovePin',
                'testSelectGroup',
                'testEnableAndDisableState',
                'testEnableAndDisablePinning',
                'testDriverSelection'
            ];
        }
    </script>
</#macro>