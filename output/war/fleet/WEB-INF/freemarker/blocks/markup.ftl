<#import "/macros/skin.ftm" as skin>
<#import "/macros/json-publish.ftm" as json/>
<#macro init>
    <#if (parameters.markup)??>
        <#local widget = parameters.markup>
        <#if widget?has_content>
        <#if (parameters.body)?? && (parameters.body)?has_content && (parameters.body) == "true">
            <@skin.widgetBody name="${widget}"/>
        <#else>
            <@skin.widget name="${widget}"/>
        </#if>
        <#-- Since script is a noscope element Adding script in IE requires a 
        scoped element like input to preceed script tag-->
        <input type="hidden"/><script type="text/javascript" defer="true">
             var data = <@json.print/>;
               if(!(_publish)) {
                 var _publish = {};
               }
            <#-- 
              TODO : Check if YUI augment is better for the logic below and is it worth adding it here.
            -->
            for(var context in data) {
               if(_publish[context]) {
                    for(var key in data[context]) {
                       _publish[context][key] = data[context][key];
                    }
               }
               else {
                   _publish[context] = data[context];
                }
            }
        </script>
        </#if>
    </#if>
</#macro>
<@init/>
 