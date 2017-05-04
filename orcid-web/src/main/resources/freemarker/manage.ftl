<#--

    =============================================================================

    ORCID (R) Open Source
    http://orcid.org

    Copyright (c) 2012-2014 ORCID, Inc.
    Licensed under an MIT-Style License (MIT)
    http://orcid.org/open-source-license

    This copyright and license information (including a link to the full license)
    shall be included in its entirety in all copies or substantial portion of
    the software.

    =============================================================================

-->
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
        <#include "admin_menu.ftl"/>
    </div>
    <!-- Right side -->
    <div class="col-md-9 col-sm-12 col-xs-12">
    	<h1 id="account-settings">${springMacroRequestContext.getMessage("manage.account_settings")}</h1>
        <#assign open = "" />
		<modal-unverified-email-set-primary></modal-unverified-email-set-primary>
        <table class="table table-bordered settings-table account-settings"
            ng-controller="EditTableCtrl" style="margin: 0px, padding:  0px;">
            <tbody>
                <tr>
                    <!-- Email header -->
                    <th><a name="editEmail"></a>${springMacroRequestContext.getMessage("manage.thEmail")}</th>
                    <td><a href="" id="account-settings-toggle-email-edit" ng-click="toggleEmailEdit()" ng-bind="emailToggleText"></a></td>
                </tr>
                <tr ng-include="'edit-emails'">
                	<!-- Injecting Emails -->
                </tr>
                
                <tr>
					<th><a name="editLanguage"></a>${springMacroRequestContext.getMessage("manage.language")}</th>
                    <td><a href="" id="" ng-click="toggleLanguageEdit()" ng-bind="languageToggleText"></a></td>
                </tr>
                
                <tr ng-controller="languageCtrl" ng-show="showEditLanguage" ng-cloak>                                
                	<td colspan="2">
                		<p>${springMacroRequestContext.getMessage("manage.language_copy")}</p>
                		<div class="row">
                			<div class="col-md-12" ng-include="'edit-language'">
                				
                			</div>                			
                			
                		</div>                		
                	</td>
                </tr>
                
                <!-- Notifications -->
                <tr>
                    <th><a name="editEmailPreferences"></a>${springMacroRequestContext.getMessage("manage.notification_preferences")}</th>
                    <td><a href="" ng-click="toggleEmailPreferencesEdit()"
                        ng-bind="emailPreferencesToggleText"></a></td>
                </tr>
                <tr ng-controller="NotificationPreferencesCtrl"
                    ng-show="showEditEmailPreferences" ng-cloak>
                    <td colspan="2">
                    	<p>${springMacroRequestContext.getMessage("manage.notification_header")}</p>
                        <div class="editTablePadCell35">                                
                            <label class="checkbox"> <input type="checkbox"
                                id="sendOrcidChangeNotifcations"
                                name="sendOrcidChangeNotifcations"
                                ng-model="prefsSrvc.prefs['send_change_notifications']"
                                ng-change="prefsSrvc.updateNotificationPreferences()" />
                                ${springMacroRequestContext.getMessage("change_notification_preferences.sendnotification")}
                            </label>
                            <label class="checkbox"> <input type="checkbox"
                                id="sendAdministrativeChangeNotifcations"
                                name="sendAdministrativeChangeNotifcations"
                                ng-model="prefsSrvc.prefs['send_administrative_change_notifications']"
                                ng-change="prefsSrvc.updateNotificationPreferences()" />
                                ${springMacroRequestContext.getMessage("change_notification_preferences.sendadministrativenotification")}
                            </label>                                
                            <label class="checkbox"> <input type="checkbox"
                                id="sendMemberUpdateRequests" name="sendMemberUpdateRequests"
                                ng-model="prefsSrvc.prefs['send_member_update_requests']"
                                ng-change="prefsSrvc.updateNotificationPreferences()" />
                                ${springMacroRequestContext.getMessage("change_notification_preferences.sendmemberupdaterequests")}
                            </label>
                            <label class="checkbox"> <input type="checkbox"
                                id="sendOrcidNews" name="sendOrcidNews"
                                ng-model="prefsSrvc.prefs['send_orcid_news']"
                                ng-change="prefsSrvc.updateNotificationPreferences()" />
                                ${springMacroRequestContext.getMessage("change_notification_preferences.news")}
                            <label>
                        </div>
                        <p>
                            ${springMacroRequestContext.getMessage("change_notification_preferences.sendinformation")}
                        </p>
                        <p>
                        	${springMacroRequestContext.getMessage("change_notification_preferences.learn_more")}
                        </p>
                    </td>
                </tr>
                <tr>
                    <th><a name="editPassword"></a>${springMacroRequestContext.getMessage("manage.password")}</th>
                    <td><a href="" ng-click="togglePasswordEdit()"
                        ng-bind="passwordToggleText"></a></td>
                </tr>
                <tr ng-controller="PasswordEditCtrl" ng-show="showEditPassword"
                    ng-cloak>
                    <td colspan="2">
                        <div class="editTablePadCell35" id="password-edit">
                            <span class="orcid-error"
                                ng-show="changePasswordPojo.errors.length > 0">
                                <div ng-repeat='error in changePasswordPojo.errors'
                                    ng-bind-html="error"></div>
                            </span>
                            <div>
                                <label for="passwordField" class="">${springMacroRequestContext.getMessage("change_password.oldpassword")}</label>
                                <div class="relative">
                                    <input id="passwordField" type="password" name="oldPassword" ng-enter="saveChangePassword()" 
                                        ng-model="changePasswordPojo.oldPassword" class="input-xlarge" />
                                    <span class="required">*</span>
                                </div>
                            </div>
                            <div>
                                <label for="passwordField" class="">${springMacroRequestContext.getMessage("change_password.newpassword")}</label>
                                <div class="relative">
                                    <input id="password" type="password" name="password" ng-enter="saveChangePassword()" 
                                        ng-model="changePasswordPojo.password" class="input-xlarge" />
                                    <span class="required">*</span> <@orcid.passwordHelpPopup />
                                </div>
                            </div>
                            <div>
                                <label for="retypedPassword" class="">${springMacroRequestContext.getMessage("change_password.confirmnewpassword")}</label>
                                <div class="relative">
                                    <input id="retypedPassword" type="password"
                                        name="retypedPassword"
                                        ng-model="changePasswordPojo.retypedPassword" ng-enter="saveChangePassword()" 
                                        class="input-xlarge" /> <span class="required">*</span>
                                </div>
                            </div>
                            <br />
                            <div>
                                <button id="bottom-submit-password-change"
                                    class="btn btn-primary" ng-click="saveChangePassword()">${springMacroRequestContext.getMessage("freemarker.btnsavechanges")}</button>                                    
                                <a class="cancel-option inner-row" ng-click="getChangePassword()" id="bottom-clear-password-changes">${springMacroRequestContext.getMessage("freemarker.btncancel")}</a>                                    
                            </div>
                        </div>
                    </td>
                </tr>
                
                
                <!-- Privacy preferences -->
                <tr>
                    <th><a name="editPrivacyPreferences"></a>${springMacroRequestContext.getMessage("manage.privacy_preferences")}</th>
                    <td><a href="" ng-click="togglePrivacyPreferencesEdit()"
                        ng-bind="privacyPreferencesToggleText" id="privacyPreferencesToggle"></a></td>
                </tr>
                <tr ng-controller="WorksPrivacyPreferencesCtrl"
                    ng-show="showEditPrivacyPreferences" id="privacyPreferencesSection" ng-cloak>
                    <td colspan="2">
                        <div class="editTablePadCell35" id="privacy-settings">
                            ${springMacroRequestContext.getMessage("privacy_preferences.activitiesVisibilityDefault.who_can_see_this")}<br />
                            <@orcid.privacyToggle3
						    angularModel="prefsSrvc.prefs['default_visibility']"
						    questionClick="toggleClickPrivacyHelp('workPrivHelp')"
						    clickedClassCheck="{'popover-help-container-show':privacyHelp['workPrivHelp']==true}" 
						    publicClick="updateActivitiesVisibilityDefault('PUBLIC', $event)" 
						    limitedClick="updateActivitiesVisibilityDefault('LIMITED', $event)" 
						    privateClick="updateActivitiesVisibilityDefault('PRIVATE', $event)" 
						    elementId="workPrivHelp" />    
                        </div>
                    </td>
                </tr>
                <tr>
                    <th><a name="editSecurityQuestion"></a>${springMacroRequestContext.getMessage("manage.security_question")}</th>
                    <td><a href="" ng-click="toggleSecurityQuestionEdit()"
                        ng-bind="securityQuestionToggleText"></a></td>
                </tr>
                <tr ng-controller="SecurityQuestionEditCtrl"
                    ng-show="showEditSecurityQuestion" ng-cloak>
                    <td colspan="2">
                        <div class="editTablePadCell35">
                            <span class="orcid-error" ng-show="errors.length > 0"> <span
                                ng-repeat='error in errors' ng-bind-html="error"></span>
                            </span>
                            <div class="control-group">
                                <label for="changeSecurityQuestionForm.securityQuestionAnswer"
                                    class="">${springMacroRequestContext.getMessage("manage.security_question")}</label>                                    
                                <div class="relative" ng-init="securityQuestions = [<#list securityQuestions?keys as key>'${securityQuestions[key]?js_string}',</#list>]">
                                    <select id="securityQuestionId" name="securityQuestionId"
                                        class="input-xlarge"
                                        ng-model="securityQuestionPojo.securityQuestionId"
                                        ng-options="securityQuestions.indexOf(securityOption) as securityOption for securityOption in securityQuestions">
                                        >                                        
                                    </select>
                                </div>
                            </div>
                            <div class="control-group">
                                <label for="changeSecurityQuestionForm.securityQuestionAnswer"
                                    class="">${springMacroRequestContext.getMessage("manage.securityAnswer")}</label>
                                <div class="relative">
                                    <input type="text" id="securityQuestionAnswer"
                                        name="securityQuestionAnswer" class="input-xlarge"
                                        ng-model="securityQuestionPojo.securityAnswer"
                                        ng-enter="checkCredentials()">
                                </div>
                            </div>
                            <#if isPasswordConfirmationRequired>
                                <@orcid.msg 'manage.security_question.not_allowed' />
                            <#else>
                                <div class="control-group">
                                    <button id="bottom-submit-security-question"
                                        class="btn btn-primary" ng-click="checkCredentials()"><@orcid.msg 'freemarker.btnsavechanges' /></button>                                        
                                    <a id="bottom-reset-security-question" class="cancel-option inner-row" ng-click="getSecurityQuestion()"><@orcid.msg 'freemarker.btncancel' /></a>                                    
                                </div>
                            </#if>
                        </div>
                    </td>
                </tr>
                <!-- Deactivate Account -->
                <tr>
                    <th><a name="editDeactivate"></a>${springMacroRequestContext.getMessage("manage.close_account")}</th>
                    <td><a href="" ng-click="toggleDeactivateEdit()"
                        ng-bind="deactivateToggleText"></a></td>
                </tr>
                <tr ng-controller="DeactivateAccountCtrl"
                    ng-show="showEditDeactivate" ng-cloak>
                    <td colspan="2">
                        <div class="editTablePadCell35 close-account-container">
                            <p>${springMacroRequestContext.getMessage("deactivate_orcid.you_may")}</p>
                            
                            <h4>${springMacroRequestContext.getMessage("deactivate_orcid.whatHappens")}</h4>
                            <p>
                                ${springMacroRequestContext.getMessage("deactivate_orcid.once")} <a
                                    href="${knowledgeBaseUri}/articles/148970-closing-an-orcid-account"
                                    target="_blank">${springMacroRequestContext.getMessage("deactivate_orcid.close_an")}</a>
                            </p>
                            
                            <h4>${springMacroRequestContext.getMessage("deactivate_orcid.anotherAccount")}</h4>
                            <p>
                                ${springMacroRequestContext.getMessage("deactivate_orcid.duplicate_orcid.a")}&nbsp;<strong>${springMacroRequestContext.getMessage("deactivate_orcid.duplicate_orcid.b")}</strong>
                                <a
	                                    href="${knowledgeBaseUri}/articles/580410"
	                                    target="_blank" class="no-wrap">${springMacroRequestContext.getMessage("deprecate_orcid.learn_more_link")}</a> 
                            </p>
                            
                                                            
                            <h4>${springMacroRequestContext.getMessage("deactivate_orcid.listTitle")}</h4>
                            <ol>
                                <li>${springMacroRequestContext.getMessage("deactivate_orcid.b1")}</li>
                                <li>${springMacroRequestContext.getMessage("deactivate_orcid.b2")}</li>
                                <li>${springMacroRequestContext.getMessage("deactivate_orcid.b3")}</li>
                            </ol>
                            <button ng-click="sendDeactivateEmail()" class="btn btn-primary">${springMacroRequestContext.getMessage("deactivate_orcid.deactivatemyOrcidaccount")}</button>
                        </div>
                    </td>
                </tr>
                <!-- / Deactivate Account -->
                <!-- Deprecate duplicate account -->
                <tr>
                    <th><a name="editDeprecate"></a>${springMacroRequestContext.getMessage("manage.removeDuplicate")}</th>
                    <td><a href="" ng-click="toggleDeprecateEdit()"
                        ng-bind="deprecateToggleText"></a></td>
                </tr>
                <tr ng-controller="DeprecateAccountCtrl"
                    ng-show="showEditDeprecate" ng-cloak>
                    <td colspan="2">
                        <div class="editTablePadCell35 close-account-container">
                            <p>${springMacroRequestContext.getMessage("deprecate_orcid.if_you_have")}</p>
                            <p>${springMacroRequestContext.getMessage("deprecate_orcid.information_in")}</p>
                            
                            <p>${springMacroRequestContext.getMessage("deprecate_orcid.if_you_have_more")}<br />
                                <a
                                    href="${knowledgeBaseUri}/articles/580410"
                                    target="_blank">${springMacroRequestContext.getMessage("deprecate_orcid.learn_more_link")}</a>
                            </p>
                            <div>
                                <label for="emailOrId" class="">${springMacroRequestContext.getMessage("deprecate_orcid.email_or_id")}</label>
                                <div class="relative">
                                    <input id="emailOrId" type="text" name="emailOrId" ng-enter="deprecateORCID()" 
                                        ng-model="deprecateProfilePojo.deprecatingOrcidOrEmail" class="input-xlarge" />
                                    <span class="required">*</span>
                                </div>
                            </div>
                            <div>
                                <label for="password" class="">${springMacroRequestContext.getMessage("deprecate_orcid.password")}</label>
                                <div class="relative">
                                    <input id="password" type="password"
                                        name="password"
                                        ng-model="deprecateProfilePojo.deprecatingPassword" ng-enter="deprecateORCID()" 
                                        class="input-xlarge" /> <span class="required">*</span>
                                </div>
                            </div>
                           <span class="orcid-error"
                                ng-show="deprecateProfilePojo.errors.length > 0">
                                <div ng-repeat='error in deprecateProfilePojo.errors'
                                    ng-bind-html="error"></div>
                            </span>
                            <button ng-click="deprecateORCID()" class="btn btn-primary">${springMacroRequestContext.getMessage("deprecate_orcid.remove_record")}</button>
                        </div>
                    </td>
                </tr>
                <#if RequestParameters['OrcidSocial']??>
                    <tr>
                        <th><a name="editSocialNetworks"></a>${springMacroRequestContext.getMessage("manage.social_networks")}</th>
                        <td><a href="" ng-click="toggleSocialNetworksEdit()"
                            ng-bind="socialNetworksToggleText"></a></td>
                    </tr>
                    <tr ng-controller="SocialNetworksCtrl" ng-show="showEditSocialSettings" ng-cloak id="social-networks">
                        <td colspan="2">
                            <div class="editTablePadCell35">
                                <p><@orcid.msg 'manage.social_networks_label_1'/>
                                    <div class="grey-box">
                                        <form role="form" id="social-network-options">
                                          <div class="checkbox-inline">
                                            <label>
                                              <input type="checkbox" name="twitter" ng-change="updateTwitter()" ng-model="twitter"><img alt="Twitter" src="${staticCdn}/img/social/twitter.png">
                                            </label>
                                          </div>
                                          <div class="checkbox-inline">
                                            <label>
                                              <input type="checkbox" name="facebook" disabled><img src="${staticCdn}/img/social/facebook.png" alt="Facebook" />
                                            </label>
                                          </div>
                                          <div class="checkbox-inline">
                                            <label>
                                              <input type="checkbox" name="google-plus"  disabled><img src="${staticCdn}/img/social/google-plus.png" alt="Google+" />
                                            </label>
                                          </div>
                                        </form>
                                    </div>
                                </p>
                            </div>
                        </td>
                    </tr>
                </#if>
            </tbody>
        </table>
        
        <h1 id="manage-permissions">
            ${springMacroRequestContext.getMessage("manage.trusted_organisations")}
        </h1>
        <p>
            ${springMacroRequestContext.getMessage("manage.youcanallowpermission")}<br />
            <a href="${springMacroRequestContext.getMessage("manage.findoutmore.trustedOrganizations.url")}"
                target=_blank"">${springMacroRequestContext.getMessage("manage.findoutmore")}</a>
        </p>
        <div ng-controller="revokeApplicationFormCtrl" >
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
		                        <a data-ng-hide="applicationSummary.websiteValue == null" href="{{getApplicationUrlLink(applicationSummary)}}" target="_blank">{{applicationSummary.websiteValue}}</a>
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
        <h1>
            ${springMacroRequestContext.getMessage("settings.tdtrustindividual")}
        </h1>
        <p>
            ${springMacroRequestContext.getMessage("settings.tdallowpermission")}<br />
            <a href="${springMacroRequestContext.getMessage("manage.findoutmore.trustedIndividual.url")}"
                target=_blank"">${springMacroRequestContext.getMessage("manage.findoutmore")}</a>
        </p>
        <div ng-controller="DelegatesCtrl" id="DelegatesCtrl" data-search-query-url="${searchBaseUrl}"> 
            <div class="ng-hide" ng-show="showInitLoader == true;">
                <i id="delegates-spinner" class="glyphicon glyphicon-refresh spin x4 green"></i>
                <!--[if lt IE 8]>    
                    <img src="${staticCdn}/img/spin-big.gif" width="85" height ="85"/>
                <![endif]-->
            </div>
            <table class="table table-bordered settings-table normal-width" ng-show="delegation" ng-cloak>
                <thead>
                    <tr>
                        <th width="40%" ng-click="changeSorting('receiverName.value')">${springMacroRequestContext.getMessage("manage.trustindividual")}</th>
                        <th width="30%" ng-click="changeSorting('receiverOrcid.value')">${springMacroRequestContext.getMessage("search_results.thORCIDID")}</th>
                        <th width="20%" ng-click="changeSorting('approvalDate')"><@orcid.msg 'manage_delegators.delegates_table.access_granted' /></th>
                        <td width="10%"></td>
                    </tr>
                </thead>
                <tbody>
                    <tr ng-repeat="delegationDetails in delegation | orderBy:sort.column:sort.descending">
                        <td width="40%"><a href="${baseUriHttp}/{{delegationDetails.receiverOrcid.value}}" target="_blank">{{delegationDetails.receiverName.value}}</a></td>
                        <td width="30%"><a href="${baseUriHttp}/{{delegationDetails.receiverOrcid.value}}" target="_blank">{{delegationDetails.receiverOrcid.value}}</a></td>
                        <td width="20%">{{delegationDetails.approvalDate|date:'yyyy-MM-dd'}}</td>
                        <td width="10%" class="tooltip-container">
                            <a
                            ng-hide="realUserOrcid === delegationDetails.receiver.value || isPasswordConfirmationRequired"
                            ng-click="confirmRevoke(delegationDetails.receiverName.value, delegationDetails.receiverOrcid.value)"
                            class="glyphicon glyphicon-trash grey"">
                           		<div class="popover popover-tooltip top">
    								<div class="arrow"></div>
    								<div class="popover-content">
										<span><@spring.message "manage.revokeaccess"/></span>
    								</div>
   								</div>                            
                            </a>
                            <span ng-show="realUserOrcid === delegationDetails.delegateSummary.orcidIdentifier.path">${springMacroRequestContext.getMessage("manage_delegation.you")}</span>
                        </td>
                    </tr>
                </tbody>
            </table>
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
                                <th width="20%">${springMacroRequestContext.getMessage("manage.thproxy")}</th>
                                <th width="25%">${springMacroRequestContext.getMessage("search_results.thORCIDID")}</th>
                                <th width="10%"></th>
                            </tr>
                        </thead>
                        <tbody>
                            <tr ng-repeat='result in results' class="new-search-result">
                                <td width="20%"><a href="{{result['orcid-profile']['orcid-identifier'].uri}}" target="_blank" ng-bind="getDisplayName(result)"></a></td>
                                <td width="25%" class='search-result-orcid-id'><a href="{{result['orcid-profile']['orcid-identifier'].uri}}" target="_blank">{{result['orcid-profile']['orcid-identifier'].path}}</td>
                                <td width="10%">
                                    <span ng-show="effectiveUserOrcid !== result['orcid-profile']['orcid-identifier'].path">
                                        <span ng-show="!delegatesByOrcid[result['orcid-profile']['orcid-identifier'].path]"
                                            ng-click="confirmAddDelegate(result['orcid-profile']['orcid-bio']['personal-details']['given-names'].value + ' ' + result['orcid-profile']['orcid-bio']['personal-details']['family-name'].value, result['orcid-profile']['orcid-identifier'].path, $index)"
                                            class="btn btn-primary">${springMacroRequestContext.getMessage("manage.spanadd")}</span>
                                        <a ng-show="delegatesByOrcid[result['orcid-profile']['orcid-identifier'].path]"
                                            ng-click="confirmRevoke(result['orcid-profile']['orcid-bio']['personal-details']['given-names'].value + ' ' + result['orcid-profile']['orcid-bio']['personal-details']['family-name'].value, result['orcid-profile']['orcid-identifier'].path, $index)"
                                            class="glyphicon glyphicon-trash grey"
                                            title="${springMacroRequestContext.getMessage("manage.revokeaccess")}"></a>
                                    </span>
                                    <span ng-show="effectiveUserOrcid === result['orcid-profile']['orcid-identifier'].path">${springMacroRequestContext.getMessage("manage_delegation.you")}</span>
                                </td>
                            </tr>
                        </tbody>
                    </table>
                    <div id="show-more-button-container">
                        <button id="show-more-button" type="submit" class="ng-cloak btn" ng-click="getMoreResults()" ng-show="areMoreResults">Show more</button>
                        <span id="ajax-loader" class="ng-cloak" ng-show="showLoader"><i class="glyphicon glyphicon-refresh spin x2 green"></i></span>
                    </div>
                </div>
                <div id="no-results-alert" class="orcid-hide alert alert-error no-delegate-matches"><@spring.message "orcid.frontend.web.no_results"/></div>
            </#if>
        </div>
        <div ng-controller="SocialCtrl" id="SocialCtrl" ng-cloak>
            <h1>
                <@orcid.msg 'manage_signin_title' />
            </h1>
            <p>
            	<@orcid.msg 'manage_signin_subtitle' />
            	<br>
            	<a href="${knowledgeBaseUri}/articles/892920"
            target=_blank"">${springMacroRequestContext.getMessage("manage.findoutmore")}</a>
            </p>
            <div>
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

<script type="text/ng-template" id="deactivate-account-modal">
    <div style="padding: 20px;"><h3>${springMacroRequestContext.getMessage("manage.deactivateSend")} {{primaryEmail}}</h3>
    <button class="btn" ng-click="closeModal()">${springMacroRequestContext.getMessage("manage.deactivateSend.close")}</button>
	</div>
</script>
        
<script type="text/ng-template" id="settings-verify-email-modal">
    <div style="padding: 20px;"><h3>${springMacroRequestContext.getMessage("manage.email.verificationEmail")} {{verifyEmailObject.value}}</h3>
    <button class="btn" ng-click="closeModal()">${springMacroRequestContext.getMessage("manage.email.verificationEmail.close")}</button>
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
		        <span ng-bind="deprecateProfilePojo.deprecatingOrcid"></span><br />
		        <span><@orcid.msg 'deprecate_orcid_modal.name_label' /></span><span ng-bind="deprecateProfilePojo.deprecatingAccountName"></span><br />
		        <span><@orcid.msg 'deprecate_orcid_modal.emails_label' /></span><ul class="inline comma"><li ng-repeat="email in deprecateProfilePojo.deprecatingEmails" ng-bind="email"></li></ul><br /><br />
		        <strong><@orcid.msg 'deprecate_orcid_modal.keep_this' /></strong><br />
		        <span ng-bind="deprecateProfilePojo.primaryOrcid"></span><br />
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

<script type="text/ng-template" id="deprecate-account-confirmation-modal">
	<div class="lightbox-container deprecate-account-confirmation-modal">
		<div class="row">
			<div class="col-md-12 col-sm-12 col-xs-12 bottomBuffer">
				<h2><@orcid.msg 'deprecate_orcid_confirmation_modal.heading' /></h2>	
       			<p><@orcid.msg 'deprecate_orcid_confirmation_modal.text_1' />&nbsp;${baseUriHttp}/<span ng-bind="deprecateProfilePojo.deprecatingOrcid"></span>&nbsp;<@orcid.msg 'deprecate_orcid_confirmation_modal.text_2' />&nbsp;${baseUriHttp}/<span ng-bind="deprecateProfilePojo.primaryOrcid"></span></p>
    		</div>
    	</div>
    	<div class="row">
			<div class="col-md-12 col-sm-12 col-xs-12">
				<div class="pull-left">
	      			<a href="" ng-click="closeModal()"><@orcid.msg 'deprecate_orcid_confirmation_modal.close' /></a>
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
          <p>{{delegateNameToAdd}} ({{delegateToAdd}})</p>
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
    <div style="padding: 20px;">
        <h3><@orcid.msg 'manage_delegation.addtrustedindividual'/></h3>
        <div ng-show="emailSearchResult.isSelf">
            <p class="alert alert-error"><@orcid.msg 'manage_delegation.youcantaddyourself'/></p>
            <a href="" ng-click="closeModal()"><@orcid.msg 'freemarker.btnclose'/></a>
        </div>
        <div ng-show="!emailSearchResult.found" >
            <p class="alert alert-error"><@orcid.msg 'manage_delegation.sorrynoaccount1'/>{{input.text}}<@orcid.msg 'manage_delegation.sorrynoaccount2'/></p>
            <p><@orcid.msg 'manage_delegation.musthaveanaccount'/></p>
            <a href="" ng-click="closeModal()"><@orcid.msg 'freemarker.btnclose'/></a>
        </div>
        <div ng-show="!emailSearchResult.isSelf && emailSearchResult.found">
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
        <p> {{delegateNameToRevoke}} ({{delegateToRevoke}})</p>
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

<#include "/includes/record/email_settings.ftl">
<#include "/includes/language_selector.ftl">

</@protected>
