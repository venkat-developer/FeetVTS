<#ftl attributes={"content_type": "application/json"}>
<#import "/macros/json-publish.ftm" as json/>
<#macro init>
    <#if (parameters.widget)??>
        <#local widget = parameters.widget/>
        <#local skin = parameters.skin/>
        <#local view = parameters.view/>
        <#if (skins["${skin}"].views["${view}"].widgets["${widget}"].template)??>
            <#local widgetTemplateFile = skins["${skin}"].views["${view}"].widgets["${widget}"].template/>
            <#import "${widgetTemplateFile}" as widgetInstance/>
            <@widgetInstance.data params=parameters>
            </@widgetInstance.data>
        </#if>
    </#if>
    <@json.print/>
</#macro>
<@init/>
 