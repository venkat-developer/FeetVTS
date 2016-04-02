<#import "/macros/skin.ftm" as skin/>
<#import "/tests/macros/test.ftm" as test/>
<#macro css>
</#macro>
<#macro body>
    <@test.widget name="TripSettings"/>
</#macro>
<#macro scripts>
    <@test.widgetscripts name="TripSettings"/>
    <script language="JavaScript" type="text/javascript">
        var $L = YAHOO.lang;
        var $D = YAHOO.util.Dom;
        var $W = getPackageForName("com.i10n.fleet.widget.ui");
        var $WU = getPackageForName("com.i10n.fleet.widget.util");
        
        var widgetInstance = null;
        var widgetPopUpInstance = null;
        
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
            var container = $D.get("tripsettings");
            if($L.isObject(container)) {
                var innerWidget = $D.get("tripsettingstoolbar");
                if(!$L.isObject(innerWidget)) {
                    fail("TripSettingsToolBar not loaded");    
                }    
            }
            else {
                fail("TripSettings Container not loaded");
            }
             
        }
        
        function testInitializations() {
            var instance = getInstance();
            if(!($L.isObject(instance) && $L.isObject(instance._widgets) && $L.isObject(instance._widgets.toolbar))) {
                fail("Widgets in TripSettings are not initialized");
            }
            if(!$L.isObject(instance.elBase))
            {
                fail("Widgets in TripSettings are not initialized");
            }
        }
        
        /**
         * Methods to test the CreateTripPopup inner widget
         */
        function testCreateTripPopUp(){
            /*Variable to make sure that all the functions were properly executed*/
            var count = 0;
            count = testPopupMarkUpLoaded(count);
            count = testPopupInitializations(count);
            count = testPopupIsDialogPopUpInherited(count);
            if(count!=3)
                fail("some of the tests were not executed");
        }
        
        function testPopupMarkUpLoaded(count) {
            var instance = getInstance();
            var createTripDiv = $D.getElementsByClassName("flip-container",null,instance.baseElement);
            if(createTripDiv==null){
                fail("The markup for the create trip popup is not set");
            }
            return ++count;
        }
        
        function testPopupInitializations(count){
            var instance = getPopUpInstance();
            if(!$L.isObject(instance.elBase))
                fail("The superclass for the popup is not properly initialized");
            return ++count;
        }
        
        function testPopupIsDialogPopUpInherited(count){
            var instance = getInstance();
            var createTripDiv = $D.getElementsByClassName("dialogpopup",null,instance.baseElement);
            if(createTripDiv==null){
                fail("The markup for the dialog popup is not inserted properly");
            }
            return ++count;
        }
        
        function getInstance() {
            if(!$L.isObject(widgetInstance)) {
                widgetInstance = new $W.TripSettings($D.get("tripsettings"));
            }
            return widgetInstance;
        }
        
        function getPopUpInstance() {
            var instance = getInstance();
            if(!$L.isObject(widgetPopUpInstance)) {
                widgetPopUpInstance = new $W.TripSettings.TripCreationPopUp($D.getElementsByClassName("create-trip")[0]);
            }
            return widgetPopUpInstance;
        }
        
        
        function exposeTestFunctionNames() {
            return [
                'testMarkupLoaded',
                'testInitializations',
                'testCreateTripPopUp'
            ];
        }
    </script>
</#macro>