<#import "/macros/skin.ftm" as skin/>
<#import "/macros/json-publish.ftm" as json/>
<#macro init>
    <#local widgetName = "${parameters.widget}"/>
    <#local widgetSkin = "${parameters.skin}"/>
    <#local widgetView = "${parameters.view}"/>
    <#local widgetTemplate = "/tests/${widgetName}Test.ftl"/>
    <#import "${widgetTemplate}" as widgetInstance />
    <!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
    <html>
        <!-- JsUnit Test Case-->
        <head>
            <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
            <title>Testing Widget ${widgetName}</title>
            <link rel="stylesheet" type="text/css" href="/static/jsunit/css/jsUnitStyle.css">
            <@widgetInstance.css />
        </head>
        <body>
            <h1></h1>
           <@widgetInstance.body />
            <iframe name="documentBuffer">
            </iframe>
        </body>
        <script src="/static/jsunit/app/jsUnitCore.js"></script>
        <script src="/static/lib/yui/yahoo/yahoo.js"></script>
        <script src="/static/lib/yui/utilities/utilities.js"></script>
        <script src="/static/lib/yui/dom/dom.js"></script>
        <script src="/static/lib/yui/event/event.js"></script>
        <script src="/static/js/base.js"></script>
        <script src="/static/lib/bubbling/bubbling/bubbling.js"></script>
        <script>
            (function(){
                var DOMEventUtils =  getPackageForName("com.i10n.fleet.widget.test.DOMEventUtils");
                DOMEventUtils.EVT_TYPE_MAP = {
                    "click" : "MouseEvents",
                    "mousedown" : "MouseEvents",
                    "mousemove" : "MouseEvents",
                    "mouseout" : "MouseEvents",
                    "mouseover" : "MouseEvents",
                    "mouseup" : "MouseEvents",
                    "dblclick" : "MouseEvents", /* Not supported by all browsers */
                    "abort" : "HTMLEvents",
                    "blur" : "HTMLEvents",
                    "change" : "HTMLEvents",
                    "error" : "HTMLEvents",
                    "focus" : "HTMLEvents",
                    "load" : "HTMLEvents",
                    "reset" : "HTMLEvents",
                    "resize" : "HTMLEvents",
                    "scroll" : "HTMLEvents",
                    "select" : "HTMLEvents",
                    "submit" : "HTMLEvents",
                    "unload" : "HTMLEvents",
                    "DOMActivate" : "UIEvents",
                    "DOMFocusIn" : "UIEvents",
                    "DOMFocusOut" : "UIEvents",
                    "DOMActivate" : "UIEvents",
                    "keydown" : "UIEvents",
                    "keypress" : "UIEvents",
                    "keyup" : "UIEvents",
                    "DOMAttrModified" : "MutationEvents",
                    "DOMNodeInserted" : "MutationEvents",
                    "DOMNodeRemoved" : "MutationEvents",
                    "DOMCharacterDataModified" : "MutationEvents",
                    "DOMNodeInsertedIntoDocument" : "MutationEvents",
                    "DOMNodeRemovedFromDocument" : "MutationEvents",
                    "DOMSubtreeModified" : "MutationEvents"
                };
                DOMEventUtils.fireEvent = function(el, sEvtName) {
                    if(DOMEventUtils.EVT_TYPE_MAP[sEvtName]) {
                        DOMEventUtils.fireDOMEvent(el, DOMEventUtils.EVT_TYPE_MAP[sEvtName] , sEvtName);
                    }   
                };
                DOMEventUtils.fireDOMEvent = function(el, sType, sEvtName) {
                    if (document.createEvent) {
                        var oEvt = document.createEvent(sType);
                        oEvt.initEvent(sEvtName, true, false);
                        el.dispatchEvent(oEvt);
                    }
                    else if (document.createEventObject) {
                        el.fireEvent("on" + sEvtName);
                    }
                };
            })();
        </script>
        <@widgetInstance.scripts />
        <@json.publish data=parameters publishAs="current" context="parameters"/>
        <script>
            var _publish = <@json.print/>
        </script>
</#macro>
<@init/>