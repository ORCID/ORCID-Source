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
        <!--Accounting settings-->
        <#include "/includes/ng2_templates/edit-table-ng2-template.ftl">
        <edit-table-ng2></edit-table-ng2>
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
        <div ng-controller="revokeApplicationFormCtrl" class="clearfix">
            <div ng-show="!applicationSummaryList.length > 0" ng-cloak>
                <p><@orcid.msg 'manage.none_added.trustedOrganizations'/></p>
            </div>
            <div ng-show="applicationSummaryList.length > 0" ng-cloak>
                <table class="table table-bordered settings-table normal-width">
                    <thead>
                        <tr>
                            <th width="35%">${springMacroRequestContext.getMessage("manage.trusted_organization")}</th>
                            <th width="5%">${springMacroRequestContext.getMessage("manage.thapprovaldate")}</th>
                            <th width="35%">${springMacroRequestContext.getMessage("manage.thaccesstype")}</th>
                            <td width="5%"></td>
                        </tr>
                    </thead>
                    <tbody>
                        <tr data-ng-repeat="applicationSummary in applicationSummaryList">
                            <td class="revokeApplicationName">{{applicationSummary.name}}<br />
                                <a data-ng-hide="applicationSummary.websiteValue == null" href="{{getApplicationUrlLink(applicationSummary)}}" target="applicationSummary.websiteValue">{{applicationSummary.websiteValue}}</a>
                            </td>
                            <td width="35%">{{applicationSummary.approvalDate}}</td>
                            <td width="5%">
                                <div data-ng-show="applicationSummary.scopePaths">
                                    <span data-ng-repeat="(key, value) in applicationSummary.scopePaths">
                                    {{value}}
                                    </span>
                                </div>
                            </td>
                            <td width="5%" class="tooltip-container">
                                <a id="revokeAppBtn" name="{{applicationSummary.orcidPath}}" ng-click="confirmRevoke(applicationSummary)"
                                    class="glyphicon glyphicon-trash grey"
                                    ng-hide="isPasswordConfirmationRequired">
                                        <div class="popover popover-tooltip top">
                                            <div class="arrow"></div>
                                            <div class="popover-content">
                                                <span><@spring.message "manage.revokeaccess"/></span>
                                            </div>
                                        </div>
                                    </a>
                            </td>
                        </tr>
                    </tbody>
                </table>
            </div>
        </div>
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
        <div ng-controller="DelegatesCtrlV2" class="clearfix" id="DelegatesCtrl" data-search-query-url="${searchBaseUrl}"> 
            <div ng-show="delegation.length > 0" ng-cloak>
                <div class="ng-hide" ng-show="showInitLoader == true;">
                <i id="delegates-spinner" class="glyphicon glyphicon-refresh spin x4 green"></i>
                <!--[if lt IE 8]>    
                    <img src="${staticCdn}/img/spin-big.gif" width="85" height ="85"/>
                <![endif]-->
            </div>
                <table class="table table-bordered settings-table normal-width" ng-show="delegation" ng-cloak>
                    <thead>
                        <tr>
                            <th class="width-30" ng-click="changeSorting('receiverName.value')">${springMacroRequestContext.getMessage("manage.trustindividual")}</th>
                            <th ng-click="changeSorting('receiverOrcid.value')">${springMacroRequestContext.getMessage("search_results.thORCIDID")}</th>
                            <th class="width-15" ng-click="changeSorting('approvalDate')"><@orcid.msg 'manage_delegators.delegates_table.access_granted' /></th>
                            <th class="width-10" ></th>
                        </tr>
                    </thead>
                    <tbody ng-show="!delegation.length > 0" ng-cloak>
                        <tr>
                            <td>No trusted individuals added yet</td>
                        </tr>
                    </tbody>
                    <tbody ng-show="delegation.length > 0" ng-cloak>
                        <tr ng-repeat="delegationDetails in delegation | orderBy:sort.column:sort.descending">
                            <td><a href="{{delegationDetails.receiverOrcid.uri}}" target="delegationDetails.receiverName.value">{{delegationDetails.receiverName.value}}</a></td>
                            <td><a href="{{delegationDetails.receiverOrcid.uri}}" target="delegationDetails.receiverOrcid.value">{{delegationDetails.receiverOrcid.uri}}</a></td>
                            <td>{{delegationDetails.approvalDate|date:'yyyy-MM-dd'}}</td>
                            <td class="tooltip-container">
                                <a
                                ng-hide="realUserOrcid === delegationDetails.receiverOrcid.value || isPasswordConfirmationRequired"
                                ng-click="confirmRevoke(delegationDetails.receiverName.value, delegationDetails.receiverOrcid.path)"
                                class="glyphicon glyphicon-trash grey">
                                    <div class="popover popover-tooltip top">
                                        <div class="arrow"></div>
                                        <div class="popover-content">
                                            <span><@spring.message "manage.revokeaccess"/></span>
                                        </div>
                                    </div>                            
                                </a>
                                <span ng-show="realUserOrcid === delegationDetails.receiverOrcid.path">${springMacroRequestContext.getMessage("manage_delegation.you")}</span>
                            </td>
                        </tr>
                    </tbody>
                </table>
            </div>
            <#if isPasswordConfirmationRequired>
                ${springMacroRequestContext.getMessage("manage_delegation.notallowed")}
            <#else>
                <p>${springMacroRequestContext.getMessage("manage_delegation.searchfortrustedindividuals")}</p>
                <div>
                        <form ng-submit="search()">
                            <input type="text" placeholder="${springMacroRequestContext.getMessage("manage_delegation.searchplaceholder")}" class="input-xlarge inline-input" ng-model="input.text"></input>
                            <input type="submit" class="btn btn-primary" value="<@orcid.msg 'search_for_delegates.btnSearch'/>"></input>
                        </form>
                </div>
                <div>
                    <table class="ng-cloak table" ng-show="areResults()">
                        <thead>
                            <tr>
                                <th width="20%">${springMacroRequestContext.getMessage("manage_bio_settings.thname")}</th>
                                <th width="25%">${springMacroRequestContext.getMessage("search_results.thORCIDID")}</th>
                                <th width="10%"></th>
                            </tr>
                        </thead>
                        <tbody>
                            <tr ng-repeat='result in results track by $index' class="new-search-result">
                                <td width="20%" ng-bind="getDisplayName(result)"></td>
                                <td width="25%" class='search-result-orcid-id'><a href="{{result['orcid-identifier'].uri}}" target="{{result['orcid-identifier'].path}}">{{result['orcid-identifier'].uri}}</a></td>
                                <td width="10%">
                                    <span ng-show="effectiveUserOrcid !== result['orcid-identifier'].path">
                                        <span ng-show="!delegatesByOrcid[result['orcid-identifier'].path]"
                                            ng-click="confirmAddDelegate(result['given-names'] + ' ' + result['family-name'], result['orcid-identifier'].path, $index)"
                                            class="btn btn-primary">${springMacroRequestContext.getMessage("manage.spanadd")}</span>
                                        <a ng-show="delegatesByOrcid[result['orcid-identifier'].path]"
                                            ng-click="confirmRevoke(result['given-names'] + ' ' + result['family-name'], result['orcid-identifier'].path, $index)"
                                            class="glyphicon glyphicon-trash grey"
                                            title="${springMacroRequestContext.getMessage("manage.revokeaccess")}"></a>
                                    </span>
                                    <span ng-show="effectiveUserOrcid === result['orcid-identifier'].path">${springMacroRequestContext.getMessage("manage_delegation.you")}</span>
                                </td>
                            </tr>
                        </tbody>
                    </table>
                        <div id="show-more-button-container">
                            <button id="show-more-button" type="submit" class="ng-cloak btn" ng-click="getMoreResults()" ng-show="areMoreResults">${springMacroRequestContext.getMessage("notifications.show_more")}</button>
                            <span id="ajax-loader" class="ng-cloak" ng-show="showLoader"><i class="glyphicon glyphicon-refresh spin x2 green"></i></span>
                        </div>
                    </div>
                    <div id="no-results-alert" class="orcid-hide alert alert-error no-delegate-matches"><@spring.message "orcid.frontend.web.no_results"/></div>
            </#if>
        </div>
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
<script type="text/ng-template" id="confirm-revoke-access-modal">
    <div class="lightbox-container confirm-revoke-access-modal">        
        <div class="row">
            <div class="col-md-12 col-sm-12 col-xs-12 bottomBuffer">        
                <h2><@orcid.msg 'manage.application_access.revoke.confirm_title' /></h2>         
                <p><@orcid.msg 'manage.application_access.revoke.confirm_copy_1' /></p>             
                <p><@orcid.msg 'manage.application_access.revoke.confirm_copy_2' /> {{applicationSummary.name}} (<@orcid.msg 'manage.application_access.revoke.access' /><span ng-repeat="(key, value) in applicationSummary.scopePaths">{{$last?value:value + ', '}}</span>)</p>
            </div>          
        </div>
        <div class="row">
            <div class="col-md-12 col-sm-12 col-xs-12">
                <div class="pull-right">
                    <a href="" ng-click="closeModal()"><@orcid.msg 'manage.application_access.revoke.confirm_close' /></a>&nbsp;&nbsp<button class="btn btn-danger" ng-click="revokeAccess()" id="confirmRevokeAppBtn"><@orcid.msg 'manage.application_access.revoke.remove' /></button>
                </div>
            </div>
        </div>
    </div>
</script>
<script type="text/ng-template" id="confirm-add-delegate-modal">
    <div style="padding: 20px;">
       <h3><@orcid.msg 'manage_delegation.addtrustedindividual'/></h3>
       <div ng-show="effectiveUserOrcid === delegateToAdd">
          <p class="alert alert-error"><@orcid.msg 'manage_delegation.youcantaddyourself'/></p>
          <a href="" ng-click="closeModal()"><@orcid.msg 'freemarker.btnclose'/></a>
       </div>
       <div ng-hide="effectiveUserOrcid === delegateToAdd">
          <p>{{delegateNameToAdd}} (<a href="${baseUri}/{{delegateToAdd}}" target="delegateToAdd">${baseUri}/{{delegateToAdd}}</a>)</p>
          <form ng-submit="addDelegate()">
              <div ng-show="isPasswordConfirmationRequired">
                  <h3><@orcid.msg 'check_password_modal.confirm_password' /></h3>
                  <label for="confirm_add_delegate_modal.password" class=""><@orcid.msg 'check_password_modal.password' /></label>
                  <input id="confirm_add_delegate_modal.password" type="password" name="confirm_add_delegate_modal.password" ng-model="password" class="input-large"/> <span class="required">*</span>
                  <span class="orcid-error" ng-show="errors.length > 0">
                      <span ng-repeat='error in errors' ng-bind-html="error"></span>
                  </span>
              </div>
              <button class="btn btn-primary" ><@orcid.msg 'manage.spanadd'/></button>
              <a href="" ng-click="closeModal()"><@orcid.msg 'freemarker.btnclose'/></a>
          </form>
       </div>
       <div ng-show="errors.length === 0">
           <br></br>
       </div>
    </div>
</script>
<script type="text/ng-template" id="confirm-add-delegate-by-email-modal">
    <div>
        <h3><@orcid.msg 'manage_delegation.addtrustedindividual'/></h3>
        <div ng-show="emailSearchResult.isSelf" ng-cloak>
            <p class="alert alert-error"><@orcid.msg 'manage_delegation.youcantaddyourself'/></p>
            <a href="" ng-click="closeModal()"><@orcid.msg 'freemarker.btnclose'/></a>
        </div>
        <div ng-show="!emailSearchResult.found" ng-cloak>
            <p class="alert alert-error"><@orcid.msg 'manage_delegation.sorrynoaccount1'/>{{input.text}}<@orcid.msg 'manage_delegation.sorrynoaccount2'/></p>
            <p><@orcid.msg 'manage_delegation.musthaveanaccount'/></p>
            <a href="" ng-click="closeModal()"><@orcid.msg 'freemarker.btnclose'/></a>
        </div>
        <div ng-show="!emailSearchResult.isSelf && emailSearchResult.found" ng-cloak>
            <p>{{input.text}}</p>
            <form ng-submit="addDelegateByEmail(input.text)">
                <div ng-show="isPasswordConfirmationRequired">
                    <h3><@orcid.msg 'check_password_modal.confirm_password' /></h3>
                    <label for="confirm_add_delegate_modal.password" class=""><@orcid.msg 'check_password_modal.password' /></label>
                    <input id="confirm_add_delegate_modal.password" type="password" name="confirm_add_delegate_modal.password" ng-model="password" class="input-large"/> <span class="required">*</span>
                    <span class="orcid-error" ng-show="errors.length > 0">
                        <span ng-repeat='error in errors' ng-bind-html="error"></span>
                    </span>
                </div>
                <button class="btn btn-primary"><@orcid.msg 'manage.spanadd'/></button>
                <a href="" ng-click="closeModal()"><@orcid.msg 'freemarker.btnclose'/></a>
            </form>
            <div ng-show="errors.length === 0">
                <br></br>
            </div>
        </div>
    </div>
</script>    
<script type="text/ng-template" id="revoke-delegate-modal">
    <div class="lightbox-container">
        <h3><@orcid.msg 'manage_delegation.confirmrevoketrustedindividual'/></h3>
        <p> {{delegateNameToRevoke}} (<a href="${baseUri}/{{delegateToRevoke}}" target="delegateToRevoke">${baseUri}/{{delegateToRevoke}}</a>)</p>
        <form ng-submit="revoke()">
            <div ng-show="isPasswordConfirmationRequired" ng-cloak>
                <h3><@orcid.msg 'check_password_modal.confirm_password' /></h3>
                <label for="confirm_add_delegate_modal.password" class=""><@orcid.msg 'check_password_modal.password' /></label>
                <input id="confirm_add_delegate_modal.password" type="password" name="confirm_add_delegate_modal.password" ng-model="password" class="input-large"/> <span class="required">*</span>
                <span class="orcid-error" ng-show="errors.length > 0" ng-cloak>
                    <span ng-repeat='error in errors' ng-bind-html="error"></span>
                </span>
            </div>
            <button class="btn btn-danger"><@orcid.msg 'manage_delegation.btnrevokeaccess'/></button>
            <a href="" ng-click="closeModal()"><@orcid.msg 'freemarker.btnclose'/></a>
        </form>
        <div ng-show="errors.length === 0" ng-cloak>
        </div>
    </div>
</script>
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
