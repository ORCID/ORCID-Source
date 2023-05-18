<@protected classes=['manage'] nav="settings">
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
        <modal-unverified-email-set-primary></modal-unverified-email-set-primary>
        
        <!--Accounting settings-->
        <#include "/includes/ng2_templates/account-settings-ng2-template.ftl">
        <account-settings-ng2></account-settings-ng2>
        
        <!--Trusted organizations-->
        <#include "/includes/ng2_templates/trusted-organizations-ng2-template.ftl">
        <trusted-organizations-ng2></trusted-organizations-ng2>

        <!--Trusted individuals-->
        <#include "/includes/ng2_templates/delegates-ng2-template.ftl">
        <delegates-ng2></delegates-ng2>

        <!--Sign in as-->           
        <#include "/includes/ng2_templates/delegators-ng2-template.ftl">
        <delegators-ng2></delegators-ng2>             

        <!--Alternate signin accounts-->
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

</@protected>