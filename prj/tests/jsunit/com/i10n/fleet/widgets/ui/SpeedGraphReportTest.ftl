<#import "/macros/skin.ftm" as skin/>
<#import "/tests/macros/test.ftm" as test/>

<#macro css>
</#macro>
<#macro body>
    <@skin.widget name="SpeedGraphReport"/>
</#macro>
<#macro scripts>    
    <@test.widgetscripts name="SpeedGraphReport"/>
    <@test.widgetscripts name="ReportTimeFrame"/>
    <#--
     TODO : once the async requests for getting the graph data for the driver
     is done the jsunit test cases will be done .
    -->
</#macro>