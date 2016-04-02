<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
    <!-- JsUnit --><!-- ***** BEGIN LICENSE BLOCK *****
    - Version: MPL 1.1/GPL 2.0/LGPL 2.1
    -
    - The contents of this file are subject to the Mozilla Public License Version
    - 1.1 (the "License"); you may not use this file except in compliance with
    - the License. You may obtain a copy of the License at
    - http://www.mozilla.org/MPL/
    -
    - Software distributed under the License is distributed on an "AS IS" basis,
    - WITHOUT WARRANTY OF ANY KIND, either express or implied. See the License
    - for the specific language governing rights and limitations under the
    - License.
    -
    - The Original Code is Edward Hieatt code.
    -
    - The Initial Developer of the Original Code is
    - Edward Hieatt, edward@jsunit.net.
    - Portions created by the Initial Developer are Copyright (C) 2001
    - the Initial Developer. All Rights Reserved.
    -
    - Contributor(s):
    - Edward Hieatt, edward@jsunit.net (original author)
    - Bob Clary, bc@bclary.comn
    -
    - Alternatively, the contents of this file may be used under the terms of
    - either the GNU General Public License Version 2 or later (the "GPL"), or
    - the GNU Lesser General Public License Version 2.1 or later (the "LGPL"),
    - in which case the provisions of the GPL or the LGPL are applicable instead
    - of those above. If you wish to allow use of your version of this file only
    - under the terms of either the GPL or the LGPL, and not to allow others to
    - use your version of this file under the terms of the MPL, indicate your
    - decision by deleting the provisions above and replace them with the notice
    - and other provisions required by the LGPL or the GPL. If you do not delete
    - the provisions above, a recipient may use your version of this file under
    - the terms of any one of the MPL, the GPL or the LGPL.
    -
    - ***** END LICENSE BLOCK ***** -->
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>JsUnit Test Suite</title>
        <link rel="stylesheet" type="text/css" href="/static/jsunit/css/jsUnitStyle.css">
        <script language="JavaScript" type="text/javascript" src="/static/jsunit/app/jsUnitCore.js">
        </script>
        <script language="JavaScript" type="text/javascript">
            var UI_WIDGET_TYPE = "ui";
            var CONTEXT_WIDGET_TYPE = "ui";
            var VIEW_DASHBOARD = "dashboard";
            var VIEW_LIVETRACK = "livetrack";
            var VIEW_REPORTS = "reports";
            var VIEW_CONTROLPANEL = "controlpanel";
            var VIEW_TEST = "test";
            var USER_ADMIN = "admin";
            /**
            * Returns the core test suite .All the core test cases should be added here.
            */
            function coreTestSuite() {
                var newsuite = new top.jsUnitTestSuite();
                addFleetStandAloneTest(newsuite,"EditableDiv");
                addFleetWidgetTest(newsuite, "AddEntity", VIEW_CONTROLPANEL, UI_WIDGET_TYPE,USER_ADMIN);
                addFleetWidgetTest(newsuite, "EditEntity", VIEW_CONTROLPANEL, UI_WIDGET_TYPE,USER_ADMIN);
                addFleetWidgetTest(newsuite, "Assignment", VIEW_CONTROLPANEL, UI_WIDGET_TYPE,USER_ADMIN);
                addFleetWidgetTest(newsuite, "DeleteEntity", VIEW_CONTROLPANEL, UI_WIDGET_TYPE,USER_ADMIN);
                addFleetWidgetTest(newsuite, "SortableList", VIEW_LIVETRACK, UI_WIDGET_TYPE);
                addFleetWidgetTest(newsuite, "Header", VIEW_LIVETRACK, UI_WIDGET_TYPE);
                addFleetWidgetTest(newsuite, "FuelGraph", VIEW_REPORTS, UI_WIDGET_TYPE);
                addFleetWidgetTest(newsuite, "Header", VIEW_DASHBOARD, UI_WIDGET_TYPE);
                addFleetWidgetTest(newsuite, "DashboardViolations", VIEW_DASHBOARD, UI_WIDGET_TYPE);
                addFleetWidgetTest(newsuite, "Header", VIEW_REPORTS, UI_WIDGET_TYPE);
                addFleetWidgetTest(newsuite, "Header", VIEW_CONTROLPANEL, UI_WIDGET_TYPE);
                addFleetWidgetTest(newsuite, "VacantVehicles", VIEW_DASHBOARD, UI_WIDGET_TYPE);
                addFleetWidgetTestMarkup(newsuite, "TripSettings", VIEW_CONTROLPANEL, UI_WIDGET_TYPE,"tripsettings");
                addFleetWidgetTest(newsuite, "AlertSettings", VIEW_CONTROLPANEL, UI_WIDGET_TYPE);
                addFleetWidgetTestMarkup(newsuite, "TripSettingsSideBar", VIEW_CONTROLPANEL, UI_WIDGET_TYPE,"tripsettings");
                addFleetWidgetTestMarkup(newsuite, "TripDetails", VIEW_CONTROLPANEL, UI_WIDGET_TYPE,"tripsettings");
                addFleetWidgetTest(newsuite, "HeaderSubNav", VIEW_LIVETRACK, UI_WIDGET_TYPE);
                addFleetWidgetTest(newsuite, "HeaderSubNav", VIEW_REPORTS, UI_WIDGET_TYPE);
                addFleetWidgetTest(newsuite, "HeaderSubNav", VIEW_CONTROLPANEL, UI_WIDGET_TYPE);
                addFleetWidgetTest(newsuite, "DriverList", VIEW_REPORTS, UI_WIDGET_TYPE);
                addFleetWidgetTest(newsuite, "ReportSettings", VIEW_CONTROLPANEL, UI_WIDGET_TYPE);
                addFleetWidgetTest(newsuite, "DashboardVehicleHealth", VIEW_DASHBOARD, UI_WIDGET_TYPE);
                addFleetWidgetTest(newsuite, "ConfirmationPopUp", VIEW_DASHBOARD,UI_WIDGET_TYPE);
                addFleetWidgetTest(newsuite, "DialogPopUp", VIEW_DASHBOARD,UI_WIDGET_TYPE);
                addFleetWidgetTest(newsuite, "AddLinks", VIEW_DASHBOARD,UI_WIDGET_TYPE);
                addFleetWidgetTest(newsuite, "PopUp", VIEW_CONTROLPANEL,UI_WIDGET_TYPE);
                addFleetWidgetTest(newsuite, "QuickLinks", VIEW_DASHBOARD,UI_WIDGET_TYPE);
                addFleetWidgetTest(newsuite, "VehicleStatus", VIEW_LIVETRACK,UI_WIDGET_TYPE);
                addFleetWidgetTest(newsuite, "GroupedReport", VIEW_REPORTS, UI_WIDGET_TYPE);
                addFleetWidgetTest(newsuite, "VehicleStatistics", VIEW_REPORTS, UI_WIDGET_TYPE);
                addFleetWidgetTest(newsuite, "VehicleList", VIEW_LIVETRACK, UI_WIDGET_TYPE);
                addFleetWidgetTest(newsuite, "VehicleReport", VIEW_REPORTS, UI_WIDGET_TYPE);
                addFleetWidgetTest(newsuite, "ActivityReport", VIEW_REPORTS,UI_WIDGET_TYPE);
                addFleetWidgetTest(newsuite, "ViolationReport", VIEW_REPORTS, UI_WIDGET_TYPE);
                addFleetWidgetTest(newsuite, "IdlePointsReport", VIEW_REPORTS, UI_WIDGET_TYPE);
                addFleetWidgetTest(newsuite, "GraphReport", VIEW_REPORTS, UI_WIDGET_TYPE);
                return newsuite;
            }
            
            /**
            * Returns the whole test suite for the project. used by jsunit
            */
            function suite() {
                var newsuite = new top.jsUnitTestSuite();
                newsuite.addTestSuite(coreTestSuite());
                return newsuite;
            }
            /**
            * A helper function to add widget test cases
            */
            function addFleetWidgetTest(suite, widgetName, view, widgetType) {
                var testPage = "@APP_CONTEXT@/view/" + view + "/" + widgetName + "?test=true&module=/tests/test-widget&widget=com/i10n/fleet/widgets/" + widgetType + "/" + widgetName;
                suite.addTestPage(testPage);
            }
            /**
            * Over loading the function to create tests that has to be run as a specific user
            */
            function addFleetWidgetTest(suite, widgetName, view, widgetType, userName){
                var testPage = "@APP_CONTEXT@/view/" + view + "/" + widgetName + "?test=true&testUser="+ userName +"&module=/tests/test-widget&widget=com/i10n/fleet/widgets/" + widgetType + "/" + widgetName;
                suite.addTestPage(testPage);
            }
            /**
            * A helper function to add widget test cases thorugh a markup
            */
            function addFleetWidgetTestMarkup(suite, widgetName, view, widgetType,markUp) {
                var testPage = "@APP_CONTEXT@/view/" + view + "/" + widgetName + "?test=true&markup="+markUp +"&module=/tests/test-widget&widget=com/i10n/fleet/widgets/" + widgetType + "/" + widgetName;
                suite.addTestPage(testPage);
            }
            /**
             * Uitlity method to add Standalone test cases(Test cases that donot belong to widget - like the test cases for utilities)
             */
             function addFleetStandAloneTest(suite,unitName){
                 var testPage = "@APP_CONTEXT@/view/tests/?test=true&module=/tests/stand-alone&unitName="+unitName;
                suite.addTestPage(testPage);
             }
        </script>
    </head>
    <body>
        <h1>Fleetcheck JsUnit Test Suite</h1>
        <p>
            This page contains a suite of jsunit tests for testing
            Fleetcheck project.
        </p>
    </body>
</html>
