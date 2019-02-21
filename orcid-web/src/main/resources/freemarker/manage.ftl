<@protected classes=['manage'] nav="settings">
<#if twitter?? && twitter>
     <div class="alert alert-success">
         <strong><@spring.message "orcid_social.twitter.enabled"/></strong>
     </div>
</#if>

<#include "/includes/ng2_templates/authorize-delegate-result-ng2-template.ftl">
<authorize-delegate-result-ng2></authorize-delegate-result-ng2>

<div class="row">
    <div class="col-md-3 col-sm-12 col-xs-12 padding-fix lhs">
        <div class="lhs">
            <#include "/includes/ng2_templates/id-banner-ng2-template.ftl"/>
            <id-banner-ng2> </id-banner-ng2>
        </div>  
    </div>
    <!-- Right side -->
    <div class="col-md-9 col-sm-12 col-xs-12" id="settings">
        <!--Accounting settings-->
        <h1 id="account-settings">${springMacroRequestContext.getMessage("manage.account_settings")}</h1>
        <div class="popover-help-container">
            <i class="glyphicon glyphicon-question-sign"></i>
            <div id="account-settings-help" class="popover bottom">
                <div class="arrow"></div>
                <div class="popover-content">
                    <p><@orcid.msg 'manage.help_popover.accountSettings'/> <a href="<@orcid.msg 'common.kb_uri_default'/>360000661693" target="manage.help_popover.accountSettings"><@orcid.msg 'common.learn_more'/></a></p>
                </div>
            </div>
        </div>
        <#assign open = "" />
        <modal-unverified-email-set-primary></modal-unverified-email-set-primary>
        
        <#include "/includes/ng2_templates/account-settings-ng2-template.ftl">
        <account-settings-ng2></account-settings-ng2>
        
        <!--Trusted organizations-->
        <div class="section-heading">
            <h1 id="manage-permissions">
                ${springMacroRequestContext.getMessage("manage.trusted_organisations")}
            </h1>
            <div class="popover-help-container">
                <i class="glyphicon glyphicon-question-sign"></i>
                <div id="trusted-organizations-help" class="popover bottom">
                    <div class="arrow"></div>
                    <div class="popover-content">
                        <p><@orcid.msg 'manage.help_popover.trustedOrganizations'/> <a href="<@orcid.msg 'common.kb_uri_default'/>360006973893" target="manage.help_popover.trustedOrganizations"><@orcid.msg 'common.learn_more'/></a></p>
                    </div>
                </div>
            </div>
        </div>
        <#include "/includes/ng2_templates/trusted-organizations-ng2-template.ftl">
        <trusted-organizations-ng2></trusted-organizations-ng2>

        <!--Trusted individuals-->
        <div class="section-heading">
            <h1>
                ${springMacroRequestContext.getMessage("settings.tdtrustindividual")}
            </h1>
            <div class="popover-help-container">
                <i class="glyphicon glyphicon-question-sign"></i>
                <div id="trusted-individuals-help" class="popover bottom">
                    <div class="arrow"></div>
                    <div class="popover-content">
                        <p><@orcid.msg 'manage.help_popover.trustedIndividuals'/> <a href="<@orcid.msg 'common.kb_uri_default'/>360006973613" target="manage.help_popover.trustedIndividuals"><@orcid.msg 'common.learn_more'/></a></p>
                    </div>
                </div>
            </div>
        </div>
        <#include "/includes/ng2_templates/delegates-ng2-template.ftl">
        <delegates-ng2></delegates-ng2>

        <!--Sign in as-->
        <#if !inDelegationMode>             
            <#include "/includes/ng2_templates/delegators-ng2-template.ftl">
            <delegators-ng2></delegators-ng2>             
        </#if>

        <!--Alternate signin accounts-->
        <div class="section-heading">
            <h1>
                <@orcid.msg 'manage_signin_title' />
            </h1>
            <div class="popover-help-container">
                <i class="glyphicon glyphicon-question-sign"></i>
                <div id="alternative-signin-accounts-help" class="popover bottom">
                    <div class="arrow"></div>
                    <div class="popover-content">
                        <p><@orcid.msg 'manage.help_popover.alternateSigninAccounts'/>  <a href="<@orcid.msg 'common.kb_uri_default'/>360006972693" target="manage.help_popover.alternateSigninAccounts"><@orcid.msg 'common.learn_more'/></a></p>
                    </div>
                </div>
            </div>
        </div>
        <#include "/includes/ng2_templates/alt-signin-accounts-ng2-template.ftl">
        <alt-signin-accounts-ng2></alt-signin-accounts-ng2> 
    </div>
</div>
        
<#include "/includes/ng2_templates/trusted-organizations-revoke-ng2-template.ftl">
<modalngcomponent elementHeight="250" elementId="modalTrustedOrganizationsRevoke" elementWidth="600">
    <trusted-organizations-revoke-ng2></trusted-organizations-revoke-ng2>
</modalngcomponent>

<#include "/includes/ng2_templates/delegates-add-ng2-template.ftl">
<modalngcomponent elementHeight="180" elementId="modalAddDelegate" elementWidth="480">
    <delegates-add-ng2></delegates-add-ng2>
</modalngcomponent> 

<#include "/includes/ng2_templates/delegates-revoke-ng2-template.ftl">
<modalngcomponent elementHeight="180" elementId="modalRevokeDelegate" elementWidth="480">
    <delegates-revoke-ng2></delegates-revoke-ng2>
</modalngcomponent>   

<#include "/includes/ng2_templates/alt-signin-accounts-revoke-ng2-template.ftl">
<modalngcomponent elementHeight="200" elementId="modalAltSigninAccountRevoke" elementWidth="500">
    <alt-signin-accounts-revoke-ng2></alt-signin-accounts-revoke-ng2>
</modalngcomponent>  
    
</script>
</@protected>