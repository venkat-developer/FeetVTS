<#import "/macros/skin.ftm" as skin/>
<#import "/tests/macros/test.ftm" as test/>
<#include "/mock/SiteData.ftd">
<#macro css>
</#macro>
<#macro body>
  <@skin.widget name="AddLinks"/>
  <@skin.widget name="QuickLinks"/>
</#macro>
<#macro scripts>
    <@test.widgetscripts name="QuickLinks"/>
    <script language="JavaScript" type="text/javascript">
        var $L = YAHOO.lang;
        var $D = YAHOO.util.Dom;
        var $W = getPackageForName("com.i10n.fleet.widget.ui");
        
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
            var container = $D.get("quicklinks");
            if(!$L.isObject(container)) {
                fail("Quicklinks Container not loaded");
            }
             
        }
        
        function testInitializations() {
            var instance = getInstance();
            if(!($L.isObject(instance) && $L.isObject(instance.elBase))) {
                fail("The AddLinks Widget inside Quicklinks is not initialized");
            }
        }
        
        function testAddLink() {
            var instance = getInstance();
            var initialCount = instance.get($W.QuickLinks.ATTR_TOTAL_LINKS);
            instance._addLink("test","test"); 
            /** Should not have added as no. of links is the max*/
            assertEquals(initialCount, instance.get($W.QuickLinks.ATTR_TOTAL_LINKS));
            instance.set($W.QuickLinks.ATTR_MAX_LINKS,instance.get($W.QuickLinks.ATTR_MAX_LINKS)+1);
            instance._addLink("test","test"); 
            /** Would have added now.*/
            assertEquals(initialCount+1, instance.get($W.QuickLinks.ATTR_TOTAL_LINKS));
            instance.set($W.QuickLinks.ATTR_MAX_LINKS,instance.get($W.QuickLinks.ATTR_MAX_LINKS)-1);
        }
        
        function testRemoveLink() {
            var _listItems = [];
            var fItemConstructor = $W.QuickLinks.ListItem;
            $W.QuickLinks.ListItem = function(el,oArgs) {
                fItemConstructor.call(this,el,oArgs);
                _listItems.push(this);
            };
            $L.augmentObject($W.QuickLinks.ListItem,fItemConstructor);
            $L.augmentProto($W.QuickLinks.ListItem,fItemConstructor);
            var widgetInstance = new $W.QuickLinks($D.get("quicklinks"));
            var initialCount = widgetInstance.get($W.QuickLinks.ATTR_TOTAL_LINKS);
            if(_listItems.length > 0) {
                _listItems[0].fireEvent(fItemConstructor.EVT_LINK_REMOVED,_listItems[0]);
            }
            else {
                fail("List Items were not created");
            }
            assertEquals(initialCount-1,widgetInstance.get($W.QuickLinks.ATTR_TOTAL_LINKS));
            $W.QuickLinks.ListItem = fItemConstructor;
        }
        
        function getInstance() {
            if(!$L.isObject(widgetInstance)) {
                widgetInstance = new $W.QuickLinks($D.get("quicklinks"));
            }
            return widgetInstance;
        }
        
        function exposeTestFunctionNames() {
            return [
                'testMarkupLoaded',
                'testInitializations',
                'testAddLink',
                'testRemoveLink'
            ];
        }
    </script>
</#macro>