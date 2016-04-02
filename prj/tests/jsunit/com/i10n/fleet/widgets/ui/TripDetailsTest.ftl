<#import "/macros/skin.ftm" as skin/>
<#import "/tests/macros/test.ftm" as test/>
<#macro css>
</#macro>
<#macro body>
    <@test.widget name="TripDetails"/>
    <@test.widget name="TripSettings"/>
</#macro>
<#macro scripts>
    <@test.widgetscripts name="TripDetails"/>
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
            var container = $D.get("tripdetails");
            if(!$L.isObject(container)) {
                fail("TripDetails Container not loaded");
            }
             
        }
        
        function testInitializations() {
            var instance = getInstance();
            if(!($L.isObject(instance) && $L.isObject(instance.elBaseElement))) {
                fail("The Tripdetails widget is not properly initialized");
            }
        }
                
        function testTrivials() {
            /*Exercising Trivial methods to ensure none of them throw any exceptions*/
            var instance = getInstance();
            instance.addListeners();
            instance.removeListeners();
            instance.onStartTrip();
            instance.onStopTrip();
            instance.onPauseTrip();
            instance.onTripDetailsModified();
            instance.getModifiedTripDetails();
        }
                
        /*Utility Functions*/
        
        function clone(target){
            var result = {};
            /*Note clone does not do a deep copy on DOM elements. So care is needed*/
            $L.augmentObject(result,target);
            return result;
        }
        
        function getInstance() {
            var tripSettings = getTripSettingsInstance();
            if(!$L.isObject(widgetInstance)) {
                widgetInstance = new $W.TripDetails($D.getElementsByClassName("tripdetails", null, tripSettings.baseElement)[0]);
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
            ];
        }
    </script>
</#macro>