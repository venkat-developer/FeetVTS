<#import "/macros/skin.ftm" as skin/>
<#import "/tests/macros/test.ftm" as test/>
<#import "/macros/skin.ftm" as skin/>
<#import "/macros/json-publish.ftm" as json/>
<#include "/mock/reports/AlertSettingsReport.ftd" >
<#macro css>
</#macro>
<#macro body>
    <@skin.widget name="AlertSettings"/>
</#macro>
<#macro scripts>
    <@test.widgetscripts name="AlertSettings"/>
    <@json.publish data=mockAlertSettings publishAs="alertsettings" context="report"/>
    <script language="JavaScript" type="text/javascript">
        var $L = YAHOO.lang;
        var $D = YAHOO.util.Dom;
        var $W = getPackageForName("com.i10n.fleet.widget.ui");
        var $WU = getPackageForName("com.i10n.fleet.widget.util");
        var instance = null;
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
        
        function testToolbarMarkupLoaded() {
            var container = $D.get("alertsettings");
            if($L.isObject(container)) {
                var innerWidget = $D.get("searchtoolbar");
                if(!$L.isObject(innerWidget)) {
                    fail("AlertSettingsToolBar not loaded");    
                }    
            }
            else {
                fail("AlertSettings Container not loaded");
            }
             
        }
        
        function testInitializations() {
            
            instance = getInstance();
            if(!($L.isObject(instance) && $L.isObject(instance._widgets))) {
                fail("AlertSettings are not initialized");
               
             }
            else if($L.isObject(instance._widgets.toolbar) && $L.isObject(instance._widgets.sidepane) && $L.isObject(instance._widgets.report)){
                fail("Widgets in Alert Settings are not loaded");
             }
        }
        
        function testSidePaneMarkUpLoaded(){
            var sidepane = $D.get('alertsettingslist');
            if(!$L.isObject(sidepane)) {
                fail("AlertSettingsSidepane not loaded");    
            }
            if($D.hasClass(sidepane,'resizer')){
                fail("Minimizer not loaded in the widget");
            }  
        }
        
        function testReportMarkUpLoaded(){
           
            var report = $D.get('alertsettingsreport');
            if(!$L.isObject(report)) {
                fail("AlertSettingsReport not loaded");    
            }
           if(!($D.getElementsByClassName('reports',null,report))){
                fail("Report content not loaded"); 
            }
           else {
                
                var reportInstance = widgetInstance._widgets.report;
                var recordSet = reportInstance.getRecordSet();
                if(!$L.isObject(recordSet)){
                    fail("Record Set not loaded for the report");
                }
                else{
                   var records = recordSet.getRecords();
                    if(!$L.isArray(records)){
                        fail("Records for the reports are not fetched");
                    }   
                }
           }
        }
        
        function getInstance() {
            if(!$L.isObject(widgetInstance)) {
                widgetInstance = new $W.AlertSettings($D.get("alertsettings"));
            }
            return widgetInstance;
        }
        
        function exposeTestFunctionNames() {
            return [
                
                'testInitializations',
                'testToolbarMarkupLoaded',
                'testSidePaneMarkUpLoaded',
                'testReportMarkUpLoaded'       
           
           ];        
        }
    </script>
</#macro>