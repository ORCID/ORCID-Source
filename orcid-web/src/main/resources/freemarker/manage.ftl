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
    <div class="col-md-3 col-sm-12 col-xs-12 padding-fix">
        <#include "admin_menu.ftl"/>
    </div>
    <div class="col-md-9 col-sm-12 col-xs-12">
        <h1 id="account-settings">${springMacroRequestContext.getMessage("manage.account_settings")}</h1>
        <#assign open = "" />

        <table class="table table-bordered settings-table"
            ng-controller="EditTableCtrl" style="margin: 0px, padding:  0px;">
            <tbody>
                <#if RequestParameters['OldPersonal']??>
                    <tr>
                        <!-- Personal Information -->
                        <th>${springMacroRequestContext.getMessage("public_profile.h3PersonalInformation")}</th>
                        <td>
                            <div>
                                <a href="<@orcid.rootPath '/account/manage-bio-settings'/>"
                                    class="update">${springMacroRequestContext.getMessage("settings.tdEdit")}</a>
                            </div>
                        </td>
                    </tr>
                </#if>
                <tr>
                    <!-- Email header -->
                    <th><a name="editEmail"></a>${springMacroRequestContext.getMessage("manage.thEmail")}</th>
                    <td><a href="" ng-click="toggleEmailEdit()" ng-bind="emailToggleText"></a></td>
                </tr>
                <tr ng-controller="EmailEditCtrl" ng-show="showEditEmail" ng-cloak>
                    <!-- Email edit -->
                    <td colspan="2">
                        <div class="editTablePadCell35">
                            <!-- we should never see errors here, but just to be safe -->
                            <span class="orcid-error" ng-show="emailSrvc.emails.errors.length > 0">
                                <span ng-repeat='error in emailSrvc.emails.errors'
                                ng-bind-html="error"></span>
                            </span>
                            <!-- Start -->
                            
                            <div ng-repeat="email in emailSrvc.emails.emails | orderBy:['value']" class="data-row-group">
                                <div class="row">
                                    <!-- Primary Email -->
                                    <div ng-class="{primaryEmail:email.primary}"
                                        ng-bind="email.value" class="col-md-3 col-xs-12 email"></div>
                                    <!-- Set Primary options -->
                                    <div class="col-md-2 col-xs-12">
                                        <span ng-hide="email.primary"> <a href=""
                                            ng-click="emailSrvc.setPrimary(email)">${springMacroRequestContext.getMessage("manage.email.set_primary")}</a>
                                        </span> <span ng-show="email.primary" class="muted"
                                            style="color: #bd362f;">
                                            ${springMacroRequestContext.getMessage("manage.email.primary_email")}
                                        </span>
                                    </div>
                                    
                                    <div class="data-row-group">
                                        <div class="col-md-3 col-xs-4">
                                            <!-- Current -->
                                            <div class="left">
                                                <select style="width: 100px; margin: 0px;" ng-change="emailSrvc.saveEmail()" ng-model="email.current">
                                                    <option value="true" ng-selected="email.current == true"><@orcid.msg 'manage.email.current.true' /></option>
                                                    <option value="false" ng-selected="email.current == false"><@orcid.msg 'manage.email.current.false' /></option>
                                                </select>
                                            </div>
                                        </div>
                                        <div class="col-md-2 col-xs-4">
                                            <!-- Email verified -->
                                            <div class="email-verified left">
                                                <span ng-hide="email.verified" class="left"><a href=""
                                                    ng-click="verifyEmail(email)">${springMacroRequestContext.getMessage("manage.email.verify")}</a></span>
                                                <span ng-show="email.verified" class="left">${springMacroRequestContext.getMessage("manage.email.verified")}</span>
                                            </div>
                                            <!-- Icon Trash / Privacy Settings -->
                                            <div class="right">
                                                <a href="" class="glyphicon glyphicon-trash grey"
                                                    ng-show="email.primary == false"
                                                    ng-click="confirmDeleteEmail(email)"></a>
                                            </div>
                                        </div>
                                        <div class="col-md-2 col-xs-4">
                                        
                                            <div class="emailVisibility">
	                                            <@orcid.privacyToggle3
	                                            	angularModel="email.visibility"
													questionClick="toggleClickPrivacyHelp(email.value)"
													clickedClassCheck="{'popover-help-container-show':privacyHelp[email.value]==true}" 
													publicClick="setPrivacy(email, 'PUBLIC', $event)" 
								                  	limitedClick="setPrivacy(email, 'LIMITED', $event)" 
								                  	privateClick="setPrivacy(email, 'PRIVATE', $event)" 
								                  	elementId="email.value" />    
                                            </div>
                                        </div>
                                    </div>
                                </div>
                            </div>
                            <div class="row bottom-row">
                                    <div class="col-md-12 add-email">
                                        <input type="email" placeholder="${springMacroRequestContext.getMessage("manage.add_another_email")}"
                                            ng-enter="checkCredentials()" class="input-xlarge inline-input" ng-model="emailSrvc.inputEmail.value"
                                            required /> <span
                                            ng-click="checkCredentials()" class="btn btn-primary">${springMacroRequestContext.getMessage("manage.spanadd")}</span>
                                        <span class="orcid-error"
                                            ng-show="emailSrvc.inputEmail.errors.length > 0"> <span
                                            ng-repeat='error in emailSrvc.inputEmail.errors'
                                            ng-bind-html="error"></span>
                                        </span>
                                    </div>
                                    <div class="col-md-12">
                                    <label>
                                        ${springMacroRequestContext.getMessage("manage.verificationEmail.1")} <a href="${aboutUri}/content/orcid-terms-use" target="_blank">${springMacroRequestContext.getMessage("manage.verificationEmail.2")}</a>${springMacroRequestContext.getMessage("manage.verificationEmail.3")}
                                    </label>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </td>
                </tr>
                <#if profile.orcidInternal.preferences.notificationsEnabled>
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
	                                ng-model="prefsSrvc.prefs.sendChangeNotifications.value"
	                                ng-change="prefsSrvc.savePrivacyPreferences()" />
	                                ${springMacroRequestContext.getMessage("change_notification_preferences.sendnotification")}
	                            </label>
	                            <label class="checkbox"> <input type="checkbox"
                                    id="sendAdministrativeChangeNotifcations"
                                    name="sendAdministrativeChangeNotifcations"
                                    ng-model="prefsSrvc.prefs.sendAdministrativeChangeNotifications.value"
                                    ng-change="prefsSrvc.savePrivacyPreferences()" />
                                    ${springMacroRequestContext.getMessage("change_notification_preferences.sendadministrativenotification")}
                                </label>                                
	                            <label class="checkbox"> <input type="checkbox"
	                                id="sendMemberUpdateRequests" name="sendMemberUpdateRequests"
	                                ng-model="prefsSrvc.prefs.sendMemberUpdateRequests"
	                                ng-change="prefsSrvc.savePrivacyPreferences()" />
	                                ${springMacroRequestContext.getMessage("change_notification_preferences.sendmemberupdaterequests")}
	                            </label>
	                            <label class="checkbox"> <input type="checkbox"
                                    id="sendOrcidNews" name="sendOrcidNews"
                                    ng-model="prefsSrvc.prefs.sendOrcidNews.value"
                                    ng-change="prefsSrvc.savePrivacyPreferences()" />
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
	                <!-- Email frequency -->
	                <tr>
                        <th><a name="editEmailFrequency"></a>${springMacroRequestContext.getMessage("manage.email_frequency")}</th>
                        <td><a href="" ng-click="toggleEmailFrequencyEdit()"
                            ng-bind="emailFrequencyToggleText"></a></td>
                    </tr>
                    <tr ng-controller="EmailFrequencyCtrl"
                        ng-show="showEditEmailFrequency" ng-cloak>
                        <td colspan="2">
                            <div class="control-group">
                                <div>${springMacroRequestContext.getMessage("manage.send_email_to_primary_1")}{{emailSrvc.primaryEmail.value}}${springMacroRequestContext.getMessage("manage.send_email_to_primary_2")}<a href="" ng-click="openEmailEdit()">${springMacroRequestContext.getMessage("manage.send_email_to_primary_3")}</a></div>
                                <label for="sendEmailFrequencyDays"
                                    class="">${springMacroRequestContext.getMessage("manage.send_email_frequency")}</label>
                                <div class="relative">
                                    <select id="sendEmailFrequencyDays" name="sendEmailFrequencyDays"
                                        class="input-xlarge"
                                        ng-model="prefsSrvc.prefs.sendEmailFrequencyDays"
                                        ng-change="prefsSrvc.savePrivacyPreferences()">
                                        <#list sendEmailFrequencies?keys as key>
                                        <option value="${key}"
                                            ng-selected="prefsSrvc.prefs.sendEmailFrequencyDays === ${key}">${sendEmailFrequencies[key]}</option>
                                        </#list>
                                    </select>
                                </div>
                                <div>${springMacroRequestContext.getMessage("manage.service_announcements")}</div>
                                <div><small class="italic">${springMacroRequestContext.getMessage("manage.service_announcements.note")}</small></div>
                            </div>
                        </td>
                    </tr>
	            </#if>
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
                                <button id="bottom-clear-password-changes"
                                    class="btn close-parent-popover" ng-click="getChangePassword()">${springMacroRequestContext.getMessage("freemarker.btncancel")}</button>
                            </div>
                        </div>
                    </td>
                </tr>
                
                <!-- Privacy preferences -->
                <tr>
                    <th><a name="editPrivacyPreferences"></a>${springMacroRequestContext.getMessage("manage.privacy_preferences")}</th>
                    <td><a href="" ng-click="togglePrivacyPreferencesEdit()"
                        ng-bind="privacyPreferencesToggleText"></a></td>
                </tr>
                <tr ng-controller="WorksPrivacyPreferencesCtrl"
                    ng-show="showEditPrivacyPreferences" ng-cloak>
                    <td colspan="2">
                        <div class="editTablePadCell35" id="privacy-settings">
                            ${springMacroRequestContext.getMessage("privacy_preferences.activitiesVisibilityDefault")}<br />
                            <@orcid.privacyToggle3
						    angularModel="prefsSrvc.prefs.activitiesVisibilityDefault.value"
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
                                <div class="relative">
                                    <select id="securityQuestionId" name="securityQuestionId"
                                        class="input-xlarge"
                                        ng-model="securityQuestionPojo.securityQuestionId">
                                        <#list securityQuestions?keys as key>
                                        <option value="${key}"
                                            ng-selected="securityQuestionPojo.securityQuestionId == ${key}">${securityQuestions[key]}</option>
                                        </#list>
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
                            <div class="control-group">
                                <button id="bottom-submit-security-question"
                                    class="btn btn-primary" ng-click="checkCredentials()"><@orcid.msg 'freemarker.btnsavechanges' /></button>
                                <button id="bottom-reset-security-question"
                                    class="btn close-parent-popover"
                                    ng-click="getSecurityQuestion()"><@orcid.msg 'freemarker.btncancel' /></button>
                            </div>
                        </div>
                    </td>
                </tr>
                <#if !profile.orcidInternal.preferences.notificationsEnabled>
	                <tr>
	                    <th><a name="editEmailPreferences"></a>${springMacroRequestContext.getMessage("manage.email_preferences")}</th>
	                    <td><a href="" ng-click="toggleEmailPreferencesEdit()"
	                        ng-bind="emailPreferencesToggleText"></a></td>
	                </tr>
	                <tr ng-controller="EmailPreferencesCtrl"
	                    ng-show="showEditEmailPreferences" ng-cloak>
	                    <td colspan="2">
	                        <div class="editTablePadCell35">
	                            <label class="checkbox"> <input type="checkbox"
	                                id="sendOrcidChangeNotifcations"
	                                name="sendOrcidChangeNotifcations"
	                                ng-model="prefsSrvc.prefs.sendChangeNotifications.value"
	                                ng-change="prefsSrvc.savePrivacyPreferences()" />
	                                ${springMacroRequestContext.getMessage("change_email_preferences.sendnotification")}
	                            </label> <label class="checkbox"> <input type="checkbox"
	                                id="sendOrcidNews" name="sendOrcidNews"
	                                ng-model="prefsSrvc.prefs.sendOrcidNews.value"
	                                ng-change="prefsSrvc.savePrivacyPreferences()" />
	                                ${springMacroRequestContext.getMessage("change_email_preferences.sendinformation")}
	                            </label>
	                            <p>
	                                <strong>${springMacroRequestContext.getMessage("change_email_preferences.privacy")}</strong>
	                                ${springMacroRequestContext.getMessage("change_email_preferences.yourregistrationinfo")}
	                                <a href="${aboutUri}/footer/privacy-policy" target="_blank">${springMacroRequestContext.getMessage("change_email_preferences.privacyPolicy")}</a>
	                                ${springMacroRequestContext.getMessage("change_email_preferences.and")}
	                                <a href="${aboutUri}/content/orcid-terms-use" target="_blank">${springMacroRequestContext.getMessage("change_email_preferences.termsAnd")}</a>.
	                            </p>
	                        </div>
	                    </td>
	                </tr>
	            </#if>
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
                                ${springMacroRequestContext.getMessage("deactivate_orcid.once")}
                                <br />
                                <a
                                    href="http://support.orcid.org/knowledgebase/articles/148970-closing-an-orcid-account"
                                    target="_blank">${springMacroRequestContext.getMessage("deactivate_orcid.close_an")}
                                </a>
                            </p>
                            
                            <h4>${springMacroRequestContext.getMessage("deactivate_orcid.anotherAccount")}</h4>
                            <p>
                                ${springMacroRequestContext.getMessage("deactivate_orcid.duplicate_orcid.a")} <a href='mailto:${springMacroRequestContext.getMessage("deactivate_orcid.duplicate_orcid.support_email")}'>${springMacroRequestContext.getMessage("deactivate_orcid.duplicate_orcid.support_email")}</a> ${springMacroRequestContext.getMessage("deactivate_orcid.duplicate_orcid.b")}
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
        	<div ng-show="applicationSummaryList.length > 0">
		        <table class="table table-bordered settings-table normal-width">
		            <thead>
		                <tr>
		                    <th width="35%">${springMacroRequestContext.getMessage("manage.thproxy")}</th>
		                    <th width="5%">${springMacroRequestContext.getMessage("manage.thapprovaldate")}</th>
		                    <th width="35%">${springMacroRequestContext.getMessage("manage.thaccesstype")}</th>
		                    <td width="5%"></td>
		                </tr>
		            </thead>
		            <tbody>
		                <tr data-ng-repeat="applicationSummary in applicationSummaryList">
		                	<td class="revokeApplicationName">{{applicationSummary.name}}<br />
		                        <a data-ng-hide="applicationSummary.websiteValue == null" href="{{applicationSummary.websiteValue}}">{{applicationSummary.websiteValue}}</a>
		                    </td>
		                    <td width="35%">{{applicationSummary.approvalDate}}</td>
		                    <td width="5%">
		                        <div data-ng-show="applicationSummary.scopePaths">
		                        	<span data-ng-repeat="(key, value) in applicationSummary.scopePaths">
		                        	{{value}}
		                    		</span>
		                    	</div>
		                    </td>
		                    <td width="5%">
	            				<a id="revokeAppBtn" name="{{applicationSummary.orcidPath}}" ng-click="confirmRevoke(applicationSummary)"
	            					class="glyphicon glyphicon-trash grey" title="${springMacroRequestContext.getMessage("manage.revokeaccess")}"></a>
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
            <table class="table table-bordered settings-table normal-width" ng-show="delegation.givenPermissionTo.delegationDetails" ng-cloak>
                <thead>
                    <tr>
                        <th width="40%" ng-click="changeSorting('delegateSummary.creditName.content')">${springMacroRequestContext.getMessage("manage.thproxy")}</th>
                        <th width="30%" ng-click="changeSorting('delegateSummary.orcidIdentifier.path')">${springMacroRequestContext.getMessage("search_results.thORCIDID")}</th>
                        <th width="20%" ng-click="changeSorting('approvalDate.value')"><@orcid.msg 'manage_delegators.delegates_table.access_granted' /></th>
                        <td width="10%"></td>
                    </tr>
                </thead>
                <tbody>
                    <tr ng-repeat="delegationDetails in delegation.givenPermissionTo.delegationDetails | orderBy:sort.column:sort.descending">
                        <td width="40%"><a href="{{delegationDetails.delegateSummary.orcidIdentifier.uri}}" target="_blank">{{delegationDetails.delegateSummary.creditName.content}}</a></td>
                        <td width="30%"><a href="{{delegationDetails.delegateSummary.orcidIdentifier.uri}}" target="_blank">{{delegationDetails.delegateSummary.orcidIdentifier.path}}</a></td>
                        <td width="20%">{{delegationDetails.approvalDate.value|date:'yyyy-MM-dd'}}</td>
                        <td width="10%">
                            <a
                            ng-hide="realUserOrcid === delegationDetails.delegateSummary.orcidIdentifier.path"
                            ng-click="confirmRevoke(delegationDetails.delegateSummary.creditName.content, delegationDetails.delegateSummary.orcidIdentifier.path)"
                            class="glyphicon glyphicon-trash grey"
                            title="${springMacroRequestContext.getMessage("manage.revokeaccess")}"></a>
                            <span ng-show="realUserOrcid === delegationDetails.delegateSummary.orcidIdentifier.path">${springMacroRequestContext.getMessage("manage_delegation.you")}</span>
                        </td>
                    </tr>
                </tbody>
            </table>
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
        </div>
        <#if ((RequestParameters['social'])?? ||(RequestParameters['shibboleth'])??)>
        <div ng-controller="SocialCtrl" id="SocialCtrl" ng-show="socialAccounts" ng-cloak>
            <h1>
                <@orcid.msg 'manage_signin_title' />
            </h1>
            <p>
            	<@orcid.msg 'manage_signin_subtitle' />
            </p>
            <div>
                <table class="table table-bordered settings-table normal-width">
                    <thead>
                        <tr>
                            <th width="40%" ng-click="changeSorting('providerUserId')"><@orcid.msg 'manage_signin_table_header1' /></th>
                            <th width="30%" ng-click="changeSorting('providerId')"><@orcid.msg 'manage_signin_table_header2' /></th>
                            <th width="20%" ng-click="changeSorting('dateCreated')"><@orcid.msg 'manage_delegators.delegates_table.access_granted' /></th>
                            <td width="10%"></td>
                        </tr>
                    </thead>
                    <tbody>
                        <tr ng-repeat="socialAccount in socialAccounts | orderBy:sort.column:sort.descending">
                            <td width="40%">{{socialAccount.email}}</a></td>
                            <td width="30%">{{socialAccount.id.providerid}}</a></td>
                            <td width="20%">{{socialAccount.dateCreated|date:'yyyy-MM-dd'}}</td>
                            <td width="10%">
                                <a
                                ng-click="confirmRevoke(socialAccount.id)"
                                class="glyphicon glyphicon-trash grey"
                                title="${springMacroRequestContext.getMessage("manage.revokeaccess")}"></a>
                            </td>
                        </tr>
                    </tbody>
                </table>
            </div>
        </#if>
    </div>
</div>

<script type="text/ng-template" id="deactivate-account-modal">
    <div style="padding: 20px;"><h3>${springMacroRequestContext.getMessage("manage.deactivateSend")} {{primaryEmail}}</h3>
    <button class="btn" ng-click="closeModal()">${springMacroRequestContext.getMessage("manage.deactivateSend.close")}</button>
	</div>
</script>
        
<script type="text/ng-template" id="verify-email-modal">
    <div style="padding: 20px;"><h3>${springMacroRequestContext.getMessage("manage.email.verificationEmail")} {{verifyEmailObject.value}}</h3>
    <button class="btn" ng-click="closeModal()">${springMacroRequestContext.getMessage("manage.email.verificationEmail.close")}</button>
	</div>
</script>

<script type="text/ng-template" id="delete-email-modal">
    <div style="padding: 20px;"><h3><@orcid.msg 'manage.email.pleaseConfirmDeletion' /> {{emailSrvc.delEmail.value}}</h3>
    <button class="btn btn-danger" ng-click="deleteEmail(emailSrvc.delEmail)"><@orcid.msg 'manage.email.deleteEmail' /></button>
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

<script type="text/ng-template" id="confirm-revoke-access-modal">
    <div style="padding: 20px;">
       <h3><@orcid.msg 'manage.application_access.revoke.confirm_revoke' /> {{appName}}</h3>
       <button class="btn btn-danger" ng-click="revokeAccess()" id="confirmRevokeAppBtn"><@orcid.msg 'manage.application_access.revoke.confirm' /></button>
       <a href="" ng-click="closeModal()"><@orcid.msg 'freemarker.btncancel' /></a>
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
              <div>
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
                <div>
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
            <div>
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
            <div>
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
    <div class="lightbox-container">
        <h3>Revoke Social Account</h3>
        <p>{{socialRemoteUserToRevoke}}</p>
        <form ng-submit="revoke()">
            <div>
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

<script type="text/ng-template" id="confirm-disable-developer-tools">
    <div style="padding: 20px;">
       <h3><@spring.message "manage.developer_tools.confirm.title"/></h3>
       <p><@spring.message "manage.developer_tools.confirm.message"/></p>
       <button class="btn btn-danger" ng-click="disableDeveloperTools()"><@spring.message "manage.developer_tools.confirm.button"/></button>
       <a href="" ng-click="closeModal()"><@spring.message "freemarker.btncancel"/></a>
    </div>
</script>

</@protected>
