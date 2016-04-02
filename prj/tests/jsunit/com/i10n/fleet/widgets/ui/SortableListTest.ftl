<#import "/macros/skin.ftm" as skin/>
<#import "/tests/macros/test.ftm" as test/>
<#macro css>
</#macro>
<#macro body>
    <@skin.widget name="VehicleList"/>
</#macro>
<#macro scripts>
    <@test.widgetscripts name="SortableList"/>
    <@test.widgetscripts name="VehicleList"/>
    <script language="JavaScript" type="text/javascript">
        var $L = YAHOO.lang;
        var $E = YAHOO.util.Event;
        var $D = YAHOO.util.Dom;
        var $YW = YAHOO.widget;
        var $W = getPackageForName("com.i10n.fleet.widget.ui");
        var $WU = getPackageForName("com.i10n.fleet.widget.util");
        
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
            assertTrue($D.hasClass("sidepane-list","sidepane-list"));
        }
        function testSorting() {
            var el = $D.get("vehiclelist");
            var sortableList = new $WU.SortableList(el);
            sortableList.sort($D.getElementsByClassName("item",null,el),$W.VehicleList.listComparator);
            var prevValue = "";
            var items = $D.getElementsByClassName("item",null,el);
            var len = items.length;
            for(var i = 0; i < len; i++) {
                var item = items[i];
                var currentValue = $D.getAttribute(item,"itemname");
                if(currentValue < prevValue) {
                    fail("Incorrect Sorting : " + currentValue + " >= " + prevValue);
                }
            }
        }
        function exposeTestFunctionNames() {
            return [
                'testMarkupLoaded',
                'testSorting'
            ];
        }
    </script>
</#macro>