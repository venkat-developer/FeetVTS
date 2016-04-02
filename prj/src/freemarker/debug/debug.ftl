<#ftl attributes={"content_type": "text/xml"}>
<?xml version="1.0" encoding="UTF-8"?>
<data>
<#if (parameters.data)??>
    <#assign data = "${parameters.data}"?eval/>
    <@show data=data/>
<#else>
    <@show data=.data_model/>
</#if>

<#macro show data={}>
    <#if data?is_hash_ex>
        <#list data?keys as key>
            <entry key="${key?replace('&','&amp;')}">
                <@show data=data[key]/>
            </entry>
        </#list>
    <#elseif data?is_sequence>
        <#list data as val>
            <entry key="${val_index}">
                <@show data=val/>
            </entry>
        </#list>
    <#elseif data?is_node>
        <#list data?children as child>
            <#if child?node_type == "text">
                ${child}
            <#else>
                <entry node="${child?node_name}" 
                    <#list child.@@ as attr>
                        ${attr?node_name}="${attr}"
                    </#list>
                >
                    <@show data=child/>
                </entry>
            </#if>
        </#list>
    <#elseif (data?is_string)>
        ${data?replace('&','&amp;')}
    <#elseif (data?is_number || data?is_date)>
        ${data}
    <#elseif (data?is_boolean)>
        ${data?string}
    </#if>
</#macro>
</data>