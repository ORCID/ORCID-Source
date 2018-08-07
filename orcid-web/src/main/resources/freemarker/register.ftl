<@protected classes=['manage'] nav="settings">
<div>
    <div class="row">        
        <div class="col-md-9 col-md-offset-3 col-sm-12 col-xs-12">
            <#if (RequestParameters['linkRequest'])??>
                <#include "/includes/ng2_templates/link-account-ng2-template.ftl">
                <link-account-ng2></link-account-ng2> 
            <#else>
                <h2>${springMacroRequestContext.getMessage("register.labelRegisterforanORCIDiD")}</h2>
                <p>${springMacroRequestContext.getMessage("register.labelORCIDprovides")}</p>
            </#if>
            <p>
                ${springMacroRequestContext.getMessage("register.labelClause_1")}
                <a href="http://orcid.org/content/orcid-terms-use" target="terms_and_conditions">
                ${springMacroRequestContext.getMessage("register.labelClause_2")}
                </a>
                ${springMacroRequestContext.getMessage("register.labelClause_3")}
                <br /><br />
            </p>
            <#include "/includes/ng2_templates/oauth-authorization-ng2-template.ftl">
            <oauth-authorization-ng2></oauth-authorization-ng2>
            <!--Register duplicates modal-->
            <#include "/includes/ng2_templates/register-duplicates-ng2-template.ftl">
            <modalngcomponent elementHeight="400" elementId="modalRegisterDuplicates" elementWidth="780">
                <register-duplicates-ng2></register-duplicates-ng2>
            </modalngcomponent>
        </div>
    </div>
</div>
</@protected>