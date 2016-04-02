<#ftl attributes={"content_type": "application/json"}>
<#import "/macros/json.ftm" as json/>
<#if (parameters.data)??>
    <#assign data = "${parameters.data}"?eval/>
    <@json.hash value=data/>
</#if>