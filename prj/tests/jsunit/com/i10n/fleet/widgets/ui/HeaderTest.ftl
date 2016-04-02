<#import "/macros/skin.ftm" as skin/>
<#import "/tests/macros/test.ftm" as test/>
<#macro css>
</#macro>
<#macro body>
    <@skin.widget name="Header"/>
</#macro>
<#macro scripts>
    <@test.widgetscripts name="Header"/>
    <script language="JavaScript" type="text/javascript">
        var $L = YAHOO.lang;
        var $D = YAHOO.util.Dom;
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
            var containerArr = $D.getElementsByClassName("header-container", null, $D.get("header"));
            if(!($L.isArray(containerArr) && containerArr.length > 0)) {
                fail("Markup Not yet Loaded!");
            }
        }
        function testSelection() {
            var url = "" + window.location;
            var items = $D.getElementsByClassName("globalnav-item", null, $D.get("header"));
            if($L.isArray(items)) {
                var len = items.length;
                for(var i = 0; i < len; i++) {
                    var item = items[i];
                    if($D.hasClass(item,"selected")) {
                        var view = item.getAttribute("view");
                        if(url.indexOf("/view/" + view) < 0) {
                            fail("Wrong Item Selected for current url - " + url);
                        }
                    }
                }
            }
            else {
                fail("Markup Not Properly Loaded! Global Nav Items not found");
            }
        }
        function exposeTestFunctionNames() {
            return [
                'testMarkupLoaded',
                'testSelection'
            ];
        }
    </script>
</#macro>