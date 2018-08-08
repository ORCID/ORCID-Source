<@public classes=['home'] nav="signin">
    <div class="row">
        <@spring.bind "loginForm" />
        <@spring.showErrors "<br/>" "error" /> 
            <#include "/includes/ng2_templates/request-password-reset-ng2-template.ftl">
            <#include "/includes/ng2_templates/oauth-authorization-ng2-template.ftl">
            <oauth-authorization-ng2></oauth-authorization-ng2>
            <!--Register duplicates modal-->
            <#include "/includes/ng2_templates/register-duplicates-ng2-template.ftl">
            <modalngcomponent elementHeight="400" elementId="modalRegisterDuplicates" elementWidth="780">
                <register-duplicates-ng2></register-duplicates-ng2>
            </modalngcomponent>
        <div class="col-md-3"></div>
    </div><!--row-->
</@public>