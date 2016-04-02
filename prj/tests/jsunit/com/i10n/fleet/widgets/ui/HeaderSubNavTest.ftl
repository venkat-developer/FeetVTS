<#import "/macros/skin.ftm" as skin/>
<#import "/tests/macros/test.ftm" as test/>
<#macro css>
</#macro>
<#macro body>
    <@skin.widget name="HeaderSubNav"/>
</#macro>
<#macro scripts>
    <script src="/static/lib/yui/connection/connection.js"></script>
    <script src="/static/lib/yui/element/element.js"></script>
    <script src="/static/lib/yui/tabview/tabview.js"></script>
    
    <@test.widgetscripts name="HeaderSubNav"/>
    <script language="JavaScript" type="text/javascript">
        var $L = YAHOO.lang;
        var $D = YAHOO.util.Dom;
        var $W = getPackageForName("com.i10n.fleet.widget.ui");
        var $WU = getPackageForName("com.i10n.fleet.widget.util");
        
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
            var container = $D.get("headersubnav");
            if(!($L.isObject(container))) {
                fail("Markup Not yet Loaded!");
            }
            else {
                var innerContent = $D.getElementsByClassName("nav-container",null,container);
                if(!($L.isArray(innerContent) && innerContent.length > 0)) {
                    fail("Inner Markup Not yet Loaded!");
                }
            }
        }

        function testInitialization() {
            var instance = getInstance();
            if($L.isObject(instance)) {
                var currentView = _publish.parameters.current.view;
                if (_publish.sitemap && _publish.sitemap.sites
                        && _publish.sitemap.sites[currentView]
                        && _publish.sitemap.sites[currentView].subnav) {
                    var pages = _publish.sitemap.sites[currentView].subnav;
                    var tabs = instance.get("tabs");
                    for(var i in tabs) {
                        var tab = tabs[i];
                        var pageId = tab.get("navId");
                        if(!$L.isObject(pages[pageId])) {
                            fail("HeaderSubNav Tabs Not Loaded Properly");
                        }
                        else {
                            var pageLink = "@APP_CONTEXT@/view/" + currentView + "/?";
                            if (pages[pageId].request) {
                                pageLink = pageLink + pages[pageId].request;
                            }
                            else {
                                pageLink = pageLink + "markup=" + pages[pageId].widget;
                            }
                            assertEquals(pageLink,tab.get("dataSrc"));
                        }
                    }
                }
                else {
                    fail("HeaderSubNav Widget failed to publish");
                }
            }
            else {
                fail("HeaderSubNav Widget failed to init");
            } 
        }

        function getInstance() {
            if(!$L.isObject(widgetInstance)) {
                widgetInstance = new $W.HeaderSubNav($D.get("headersubnav"));
            }
            return widgetInstance;
        }

        function exposeTestFunctionNames() {
            return [
                'testMarkupLoaded',
                'testInitialization'
            ];
        }
    </script>
</#macro>