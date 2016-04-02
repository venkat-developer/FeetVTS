<#import "/macros/skin.ftm" as skin/>
<#import "/tests/macros/test.ftm" as test/>
<#import "/macros/skin.ftm" as skin/>
<#import "/macros/json-publish.ftm" as json/>
<#include "/mock/reports/ReportSettingsReport.ftd" >
<#macro css>
</#macro>
<#macro body>
    <@skin.widget name="ReportSettings"/>
</#macro>
<#macro scripts>  
    <@test.widgetscripts name="Buttons"/>
    <@test.widgetscripts name="ReportSettings"/>
    <@json.publish data=mockReportSettings.subscribers publishAs="reportsettings" context="report"/>      
    <script language="JavaScript" type="text/javascript">
         var $L = YAHOO.lang;
         var $YU = YAHOO.util;
         var $E = YAHOO.util.Event;
         var $D = YAHOO.util.Dom;
         var $YW = YAHOO.widget;
         var $U = getPackageForName("com.i10n.fleet.Utils");
         var $W = getPackageForName("com.i10n.fleet.widget.ui");
         var $V = getPackageForName("com.i10n.fleet.widget.view");
        
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
            var container = $D.get("reportsettings");
            if($L.isObject(container)) {
                var toolbar = $D.get("searchtoolbar");
                if(!$L.isObject(toolbar)) {
                    fail("SearchToolBar not loaded");    
                }  
                var sidelistpane = $D.get("reportsettingslist");
                if(!$L.isObject(sidelistpane)) {
                    fail("ReportSettingsList not loaded");    
                }  
                var report = $D.get("reportsettingsreport");
                if(!$L.isObject(report)) {
                    fail("ReportSettingsReport not loaded");    
                }    
            }
            else {
                fail("ReportSettings Container not loaded");
            }
             
        }
        
        function testInitializations() {
            var instance = getInstance();
            if(!($L.isObject(instance) && $L.isObject(instance._widgets) && $L.isObject(instance._widgets.toolbar)&& $L.isObject(instance._widgets.sidepane)&& $L.isObject(instance._widgets.report))) {
                fail("Widgets in ReportSettings are not initialized");
            }
        }
        
        function getInstance() {
            if(!$L.isObject(widgetInstance)) {
                widgetInstance = new $W.ReportSettings($D.get("reportsettings"));
            }
            return widgetInstance;
        }
        
        function exposeTestFunctionNames() {
            return [
                'testMarkupLoaded',
                'testInitializations'
            ];
        }
    </script>
</#macro>
