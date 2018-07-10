<@public>
<div class="row">
    <div class ="col-md-9 col-md-offset-3 col-sm-9 col-sm-offset-3 col-xs-12">
        <#if springMacroRequestContext.requestUri?contains("/unsubscribe") >
            <#include "/includes/ng2_templates/unsubscribe-ng2-template.ftl">
        </#if>
        <unsubscribe-ng2></unsubscribe-ng2>
    </div>
</div>
</@public>