<!DOCTYPE html PUBLIC "-//W3C/DTD HTML 4.01//EN"
  "http://www.w3.org/TR/html4/DTD/strict.dtd">
<#import "/macros/skin.ftm" as skin/>
<#import "/macros/json-publish.ftm" as json/>
<#macro init>
    <html>
        <head>
            <#-- TODO : Temporarily forcing to IE7 Mode . Will be removed later -->
            <meta http-equiv="X-UA-Compatible"
                content="IE=EmulateIE7"
            />
            <title>
                Fleetcheck V2.0
            </title>
        </head>
        <#-- Head Scripts Section -->
        <#-- Adding CSS -->
        <@skin.styles params={}/>
        <body class="${parameters.skin} yui-skin-sam fleet-body">
            <div id="overlay" class="overlay loader">
            </div>
            <div class="fleet-container cnt-${parameters.view}">
                <@skin.view params={}/>
            </div>
        </body>
        <#-- Adding Scripts -->
        <@json.publish data=parameters publishAs="current" context="parameters"/>
        <script>
            var _publish = <@json.print/>
        </script>
        <@skin.scripts params={}/>
    </html>
</#macro>
<@init/>