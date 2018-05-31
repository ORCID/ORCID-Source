<@public>
<div class="row">
    <div class ="col-md-9 col-md-offset-3 col-sm-9 col-sm-offset-3 col-xs-12">
        <#if springMacroRequestContext.requestUri?contains("/reset-password") >
            <#include "/includes/ng2_templates/reset-password-ng2-template.ftl">
        </#if>
        <reset-password-ng2></reset-password-ng2>
    </div>
</div>
</@public>