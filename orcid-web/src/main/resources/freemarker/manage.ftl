<@protected classes=['manage'] nav="settings">
<#if twitter?? && twitter>
     <div class="alert alert-success">
         <strong><@spring.message "orcid_social.twitter.enabled"/></strong>
     </div>
</#if>
<#if admin_delegate_approved??>
    <div class="alert alert-success">
        <strong>${admin_delegate_approved}</strong>
    </div>
</#if>
 <#if admin_delegate_failed??>
    <div class="alert alert-success">
        <strong>${admin_delegate_failed}</strong>
    </div>
</#if>
<#if admin_delegate_not_you??>
    <div class="alert alert-success">
        <strong><@orcid.msg 'wrong_user.Wronguser' /></strong> <a href="<@orcid.rootPath '/signout'/>"><@orcid.msg 'public-layout.sign_out' /></a> <@orcid.msg 'wrong_user.andtryagain' />
    </div>
</#if>
<div class="row">
    <div class="col-md-3 col-sm-12 col-xs-12 padding-fix lhs">
        <div class="lhs">
            <#include "includes/id_banner.ftl"/>
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
        <delegates-ng2></delegatess-ng2>

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
        <div ng-controller="SocialCtrl" class="clearfix" id="SocialCtrl" ng-cloak>
            <div ng-show="!socialAccounts.length > 0" ng-cloak>
                <p><@orcid.msg 'manage.none_added.alternateSigninAccounts'/></p>
            </div>
            <div ng-show="socialAccounts.length > 0" ng-cloak>
                <table class="table table-bordered settings-table normal-width" ng-show="socialAccounts">
                    <thead>
                        <tr>
                            <th width="40%" ng-click="changeSorting('accountIdForDisplay')"><@orcid.msg 'manage_signin_table_header1' /></th>
                            <th width="30%" ng-click="changeSorting('idpName')"><@orcid.msg 'manage_signin_table_header2' /></th>
                            <th width="20%" ng-click="changeSorting('dateCreated')"><@orcid.msg 'manage_delegators.delegates_table.access_granted' /></th>
                            <td width="10%"></td>
                        </tr>
                    </thead>
                    <tbody>
                        <tr ng-repeat="socialAccount in socialAccounts | orderBy:sort.column:sort.descending">
                            <td width="40%" style="word-break:break-all">{{socialAccount.accountIdForDisplay}}</a></td>
                            <td width="30%" style="word-break:break-all">{{socialAccount.idpName}}</a></td>
                            <td width="20%" style="word-break:break-all">{{socialAccount.dateCreated|date:'yyyy-MM-dd'}}</td>
                            <td width="10%">
                                <a
                                ng-click="confirmRevoke(socialAccount)"
                                ng-hide="isPasswordConfirmationRequired"
                                class="glyphicon glyphicon-trash grey"
                                title="${springMacroRequestContext.getMessage("manage_signin_unlink")}"></a>
                            </td>
                        </tr>
                    </tbody>
                </table>
                <#if isPasswordConfirmationRequired>
                    <@orcid.msg 'manage_signin_not_allowed' />
                </#if>
            </div>
        </div>
    </div>
</div>
        
<script type="text/ng-template" id="settings-verify-email-modal">
    <div style="padding: 20px;">
        <h4><@orcid.msg 'manage.email.verificationEmail'/> {{verifyEmailObject.value}}</h4>
        <p><@orcid.msg 'workspace.check_your_email'/></p>
        <br />
        <button class="btn" ng-click="closeColorBox()"><@orcid.msg 'freemarker.btnclose'/></button>
    </div>
</script>

<script type="text/ng-template" id="delete-email-modal">
    <div style="padding: 20px;"><h3><@orcid.msg 'manage.email.pleaseConfirmDeletion' /> {{emailSrvc.delEmail.value}}</h3>
    <button id="confirm-delete-email_{{emailSrvc.delEmail.value}}" class="btn btn-danger" ng-click="deleteEmail(emailSrvc.delEmail)"><@orcid.msg 'manage.email.deleteEmail' /></button>
    <a href="" ng-click="closeModal()"><@orcid.msg 'freemarker.btncancel' /></a></div>
</script>

<#-- Script that will display a modal to ask for user password                                                 -->
<#-- If someone wants to use this modal, it should consider the following:                                     -->
<#-- 1) There should be a password variable in his scope, there we be saved the value of this input.        -->
<#-- 2) There should be a function submitModal() to submit the form with the desired info and the password.    -->
<#-- 3) There should be a function closeModal() to close the the modal.                                        -->
<script type="text/ng-template" id="check-password-modal">
    <div style="padding: 20px;"><h2><@orcid.msg 'check_password_modal.confirm_password' /></h2>
       <label for="check_password_modal.password" class=""><@orcid.msg 'check_password_modal.password' /></label>
       <input id="check_password_modal.password" type="password" name="check_password_modal.password" ng-model="password" ng-enter="submitModal()" class="input-xlarge"/>
       <br />
       <button id="bottom-submit" class="btn btn-primary" ng-click="submitModal()"><@orcid.msg 'check_password_modal.submit'/></button>
       <button class="btn" ng-click="closeModal()"><@orcid.msg 'check_password_modal.close'/></button>
    </div>
</script>
<script type="text/ng-template" id="confirm-deprecate-account-modal">
    <div class="lightbox-container confirm-deprecate-account-modal">
       <div class="row">
            <div class="col-md-12 col-sm-12 col-xs-12 bottomBuffer">       
                <h2><@orcid.msg 'deprecate_orcid_modal.heading' /></h2>     
                <span class="orcid-error italic"><@orcid.msg 'deprecate_orcid_modal.warning_1' /><br /><strong class="italic"><@orcid.msg 'deprecate_orcid_modal.warning_2' /></strong></span>
                <strong><@orcid.msg 'deprecate_orcid_modal.remove_this' /></strong><br />
                <a href="${baseUri}/{{deprecateProfilePojo.deprecatingOrcid}}" target="deprecatingOrcid">${baseUri}/<span ng-bind="deprecateProfilePojo.deprecatingOrcid"></span></a><br />
                <span><@orcid.msg 'deprecate_orcid_modal.name_label' /></span><span ng-bind="deprecateProfilePojo.deprecatingAccountName"></span><br />
                <span><@orcid.msg 'deprecate_orcid_modal.emails_label' /></span><ul class="inline comma"><li ng-repeat="email in deprecateProfilePojo.deprecatingEmails" ng-bind="email"></li></ul><br /><br />
                <strong><@orcid.msg 'deprecate_orcid_modal.keep_this' /></strong><br />
                <a href="${baseUri}/{{deprecateProfilePojo.primaryOrcid}}" target="primaryOrcid">${baseUri}/<span ng-bind="deprecateProfilePojo.primaryOrcid"></a></span><br />
                <span><@orcid.msg 'deprecate_orcid_modal.name_label' /></span><span ng-bind="deprecateProfilePojo.primaryAccountName"></span><br />
                <span><@orcid.msg 'deprecate_orcid_modal.emails_label' /></span><ul class="inline comma"><li ng-repeat="email in deprecateProfilePojo.primaryEmails" ng-bind="email" ></li></ul><br /><br />
            </div>          
        </div>
        <div class="row">
            <div class="col-md-12 col-sm-12 col-xs-12">
                <div class="pull-left">
                    <button id="bottom-submit" class="btn btn-primary" ng-click="submitModal()"><@orcid.msg 'deprecate_orcid_modal.confirm'/></button><a href="" class="cancel-right" ng-click="closeModal()"><@orcid.msg 'deprecate_orcid_modal.cancel' /></a>
                </div>
            </div>
        </div>
    </div>
</script>   
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

<script type="text/ng-template" id="revoke-shibboleth-account-modal">
    <div class="lightbox-container">
        <h3>Revoke Shibboleth Account</h3>
        <p>{{shibbolethRemoteUserToRevoke}}</p>
        <form ng-submit="revoke()">
            <div ng-show="isPasswordConfirmationRequired">
                <h3><@orcid.msg 'check_password_modal.confirm_password' /></h3>
                <label for="confirm_add_delegate_modal.password" class=""><@orcid.msg 'check_password_modal.password' /></label>
                <input id="confirm_add_delegate_modal.password" type="password" name="confirm_add_delegate_modal.password" ng-model="password" class="input-large"/> <span class="required">*</span>
                <span class="orcid-error" ng-show="errors.length > 0">
                    <span ng-repeat='error in errors' ng-bind-html="error"></span>
                </span>
            </div>
            <button class="btn btn-danger"><@orcid.msg 'manage_delegation.btnrevokeaccess'/></button>
            <a href="" ng-click="closeModal()"><@orcid.msg 'freemarker.btnclose'/></a>
        </form>
        <div ng-show="errors.length === 0">
            <br></br>
        </div>
    </div>
</script>

<script type="text/ng-template" id="revoke-social-account-modal">
    <div class="lightbox-container revoke-social">
        <div class="row">
            <div class="col-md-12 col-sm-12 col-xs-12">
                <h3><@orcid.msg 'social.revoke'/></h3>
                <p><@orcid.msg 'social.revoke.body.1'/>{{socialAccount.idpName}}<@orcid.msg 'social.revoke.body.2'/>{{socialAccount.accountIdForDisplay}}<@orcid.msg 'social.revoke.body.3'/></p>
                <form ng-submit="revoke()">
                    <button class="btn btn-danger"><@orcid.msg 'social.revoke.button'/></button>
                    <a href="" ng-click="closeModal()"><@orcid.msg 'social.revoke.cancel'/></a>
                </form>
            </div>
        </div>        
    </div>
</script>

<script type="text/ng-template" id="confirm-disable-developer-tools">
    <div style="padding: 20px;">
       <h3><@spring.message "manage.developer_tools.confirm.title"/></h3>
       <p><@spring.message "manage.developer_tools.confirm.message"/></p>
       <button class="btn btn-danger" ng-click="disableDeveloperTools()"><@spring.message "manage.developer_tools.confirm.button"/></button>
       <a href="" ng-click="closeModal()"><@spring.message "freemarker.btncancel"/></a>
    </div>
</script>

<modalngcomponent elementHeight="200" elementId="modalDeactivateAccountMessage" elementWidth="645">
    <deactivate-account-message-ng2></deactivate-account-message-ng2>
</modalngcomponent><!-- Ng2 component -->

</@protected>
