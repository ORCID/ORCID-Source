<#--

    =============================================================================

    ORCID (R) Open Source
    http://orcid.org

    Copyright (c) 2012-2013 ORCID, Inc.
    Licensed under an MIT-Style License (MIT)
    http://orcid.org/open-source-license

    This copyright and license information (including a link to the full license)
    shall be included in its entirety in all copies or substantial portion of
    the software.

    =============================================================================

-->

<@protected classes=['manage'] nav="settings">
<div class="row">
	<div class="col-md-3 lhs override">
		<ul class="settings-nav">
			<li><a href="#account-settings">${springMacroRequestContext.getMessage("manage.accountsettings")}</a></li>
			<li><a href="#manage-permissions">${springMacroRequestContext.getMessage("manage.managepermission")}</a></li>
			<#if (profile.groupType)?? && ((profile.groupType) = "BASIC" ||
			(profile.groupType) = "PREMIUM" || (profile.groupType) =
			"BASIC_INSTITUTION" || (profile.groupType) = "PREMIUM_INSTITUTION")>
			<li><a href="<@spring.url "/manage-clients" />">${springMacroRequestContext.getMessage("workspace.ManageClientCredentials")}</a></li>
			</#if> <@security.authorize ifAnyGranted="ROLE_ADMIN">
			<li><a href="<@spring.url "/admin-actions" />"><@orcid.msg
				'admin.workspace_link' /></a></li> </@security.authorize>
		</ul>
	</div>
	<div class="col-md-9">
		<h1 id="account-settings">${springMacroRequestContext.getMessage("manage.account_settings")}</h1>
		<#assign open = "" />

		<table class="table table-bordered settings-table"
			ng-controller="EditTableCtrl" style="margin: 0px, padding:  0px;">
			<tbody>
				<tr>
					<!-- Personal Information -->
					<th>${springMacroRequestContext.getMessage("public_profile.h3PersonalInformation")}</th>
					<td>
						<div>
							<a href="<@spring.url '/account/manage-bio-settings'/>"
								class="update">${springMacroRequestContext.getMessage("settings.tdEdit")}</a>
						</div>
					</td>
				</tr>
				<tr>
					<!-- Email header -->
					<th><a name="editEmail"></a>${springMacroRequestContext.getMessage("manage.thEmail")}</th>
					<td><a href="" ng-click="toggleEmailEdit()"
						ng-bind="emailToggleText"></a></td>
				</tr>
				<tr ng-controller="EmailEditCtrl" ng-show="showEditEmail" ng-cloak>
					<!-- Email edit -->
					<td colspan="2">
						<div class="editTablePadCell35">
							<!-- we should never see errors here, but just to be safe -->
							<span class="orcid-error" ng-show="emailsPojo.errors.length > 0">
								<span ng-repeat='error in emailsPojo.errors'
								ng-bind-html-unsafe="error"></span>
							</span>
							<!-- Start -->
							
							<div ng-repeat='email in emailsPojo.emails' class="data-row-group">
								<div class="row">
									<!-- Primary Email -->
									<div ng-class="{primaryEmail:email.primary}"
										ng-bind="email.value" class="col-md-4 col-xs-12 email"></div>
									<!-- Set Primary options -->
									<div class="col-md-3 col-xs-12">
										<span ng-hide="email.primary"> <a href=""
											ng-click="setPrimary($index)">${springMacroRequestContext.getMessage("manage.email.set_primary")}</a>
										</span> <span ng-show="email.primary" class="muted"
											style="color: #bd362f;">
											${springMacroRequestContext.getMessage("manage.email.primary_email")}
										</span>
									</div>
									<div class="data-row-group">
										<div class="col-md-2 col-xs-5">
											<!-- Email verified -->
											<div class="email-verified left">
												<span ng-hide="email.verified" class="left"><a href=""
													ng-click="verifyEmail($index)">${springMacroRequestContext.getMessage("manage.email.verify")}</a></span>
												<span ng-show="email.verified" class="left">${springMacroRequestContext.getMessage("manage.email.verified")}</span>
											</div>
											<!-- Icon Trash / Privacy Settings -->
											<div class="right">
												<a href="" class="glyphicon glyphicon-trash grey"
													ng-show="email.primary == false"
													ng-click="confirmDeleteEmail($index)"></a>
											</div>
										</div>
										<div class="col-md-3 col-xs-6">
											<div class="emailVisibility"><@orcid.privacyToggle
												angularModel="email.visibility" 
												questionClick="toggleClickPrivacyHelp(email.value)"
												clickedClassCheck="{'popover-help-container-show':privacyHelp[email.value]==true}" 
												publicClick="setPrivacy($index, 'PUBLIC', $event)" 
			                    				limitedClick="setPrivacy($index, 'LIMITED', $event)" 
			                    				privateClick="setPrivacy($index, 'PRIVATE', $event)" />			        
											</div>
										</div>
									</div>
								</div>
							</div>
							<div class="row bottom-row">
									<div class="col-md-12 add-email">
										<input type="email" placeholder="${springMacroRequestContext.getMessage("manage.add_another_email")}"
											class="input-xlarge" ng-model="inputEmail.value"
											style="margin: 0px;" required /> <span
											ng-click="checkCredentials()" class="btn btn-primary">${springMacroRequestContext.getMessage("manage.spanadd")}</span>
										<span class="orcid-error"
											ng-show="inputEmail.errors.length > 0"> <span
											ng-repeat='error in inputEmail.errors'
											ng-bind-html-unsafe="error"></span>
										</span>
									</div>
								</div>
							</div>
						</div>
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
									ng-bind-html-unsafe="error"></div>
							</span>
							<div>
								<label for="passwordField" class="">${springMacroRequestContext.getMessage("change_password.oldpassword")}</label>
								<div class="relative">
									<input id="passwordField" type="password" name="oldPassword"
										ng-model="changePasswordPojo.oldPassword" class="input-xlarge" />
									<span class="required">*</span>
								</div>
							</div>
							<div>
								<label for="passwordField" class="">${springMacroRequestContext.getMessage("change_password.newpassword")}</label>
								<div class="relative">
									<input id="password" type="password" name="password"
										ng-model="changePasswordPojo.password" class="input-xlarge" />
									<span class="required">*</span> <@orcid.passwordHelpPopup />
								</div>
							</div>
							<div>
								<label for="retypedPassword" class="">${springMacroRequestContext.getMessage("change_password.confirmnewpassword")}</label>
								<div class="relative">
									<input id="retypedPassword" type="password"
										name="retypedPassword"
										ng-model="changePasswordPojo.retypedPassword"
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
				<tr>
					<th><a name="editPrivacyPreferences"></a>${springMacroRequestContext.getMessage("manage.privacy_preferences")}</th>
					<td><a href="" ng-click="togglePrivacyPreferencesEdit()"
						ng-bind="privacyPreferencesToggleText"></a></td>
				</tr>
				<tr ng-controller="WorksPrivacyPreferencesCtrl"
					ng-show="showEditPrivacyPreferences" ng-cloak>
					<td colspan="2">
						<div class="editTablePadCell35" id="privacy-settings">
							${springMacroRequestContext.getMessage("privacy_preferences.labelDefaultprivacyfornewWorks")}<br />
							<@orcid.privacyToggle
							angularModel="prefsSrvc.prefs.workVisibilityDefault.value"
							questionClick="toggleClickPrivacyHelp('workPrivHelp')"
							clickedClassCheck="{'popover-help-container-show':privacyHelp['workPrivHelp']==true}" 
							publicClick="updateWorkVisibilityDefault('PUBLIC', $event)"
							limitedClick="updateWorkVisibilityDefault('LIMITED', $event)"
							privateClick="updateWorkVisibilityDefault('PRIVATE', $event)" />
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
								ng-repeat='error in errors' ng-bind-html-unsafe="error"></span>
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
										ng-model="securityQuestionPojo.securityAnswer">
								</div>
							</div>
							<div class="control-group">
								<button id="bottom-submit-security-question"
									class="btn btn-primary" ng-click="checkCredentials()">Save
									changes</button>
								<button id="bottom-reset-security-question"
									class="btn close-parent-popover"
									ng-click="getSecurityQuestion()">Cancel</button>
							</div>
						</div>
					</td>
				</tr>
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
								ng-click="prefsSrvc.savePrivacyPreferences()" />
								${springMacroRequestContext.getMessage("change_email_preferences.sendnotification")}
							</label> <label class="checkbox"> <input type="checkbox"
								id="sendOrcidNews" name="sendOrcidNews"
								ng-model="prefsSrvc.prefs.sendOrcidNews.value"
								ng-click="prefsSrvc.savePrivacyPreferences()" />
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
				<tr>
					<th><a name="editDeactivate"></a>${springMacroRequestContext.getMessage("manage.close_account")}</th>
					<td><a href="" ng-click="toggleDeactivateEdit()"
						ng-bind="deactivateToggleText"></a></td>
				</tr>
				<tr ng-controller="DeactivateAccountCtrl"
					ng-show="showEditDeactivate" ng-cloak>
					<td colspan="2">
						<div class="editTablePadCell35">
							<p>${springMacroRequestContext.getMessage("deactivate_orcid.you_may")}</p>
							<p>${springMacroRequestContext.getMessage("deactivate_orcid.once")}</p>
							<a
								href="http://support.orcid.org/knowledgebase/articles/148970-closing-an-orcid-account"
								target="_blank">${springMacroRequestContext.getMessage("deactivate_orcid.close_an")}</a><br />
							<br /> <strong>${springMacroRequestContext.getMessage("deactivate_orcid.listTitle")}</strong>
							<ol>
								<li>${springMacroRequestContext.getMessage("deactivate_orcid.b1")}</li>
								<li>${springMacroRequestContext.getMessage("deactivate_orcid.b2")}</li>
								<li>${springMacroRequestContext.getMessage("deactivate_orcid.b3")}</li>
							</ol>
							<button ng-click="sendDeactivateEmail()" class="btn btn-primary">${springMacroRequestContext.getMessage("deactivate_orcid.deactivatemyOrcidaccount")}</button>
						</div>
					</td>
				</tr>
			</tbody>
		</table>

		<h1 id="manage-permissions">${springMacroRequestContext.getMessage("manage.manage_permissions")}</h1>
		<h3>
			<b>${springMacroRequestContext.getMessage("manage.trusted_organisations")}</b>
		</h3>
		<p>
			${springMacroRequestContext.getMessage("manage.youcanallowpermission")}<br />
			<a href="http://support.orcid.org/knowledgebase/articles/131598"
				target=_blank"">${springMacroRequestContext.getMessage("manage.findoutmore")}</a>
		</p>
		<#if (profile.orcidBio.applications.applicationSummary)??>
		<table ng-controller="revokeApplicationFormCtrl"
			class="table table-bordered settings-table normal-width">
			<thead>
				<tr>
					<th width="35%">${springMacroRequestContext.getMessage("manage.thproxy")}</th>
					<th width="5%">${springMacroRequestContext.getMessage("manage.thapprovaldate")}</th>
					<th width="35%">${springMacroRequestContext.getMessage("manage.thaccesstype")}</th>
					<td width="5%"></td>
				</tr>
			</thead>
			<tbody>
				<#list profile.orcidBio.applications.applicationSummary as
				applicationSummary>
				<tr>
					<form action="manage/revoke-application" method="post"
						class="revokeApplicationForm"
						id="revokeApplicationForm${applicationSummary_index}">
						<td class="revokeApplicationName">${(applicationSummary.applicationName.content)!}<br />
						<a href="<@orcid.absUrl applicationSummary.applicationWebsite/>">${applicationSummary.applicationWebsite.value}</a></td>
						<td width="35%">${applicationSummary.approvalDate.value.toGregorianCalendar().time?date}</td>
						<td width="5%"><input type="hidden" name="applicationOrcid"
							value="${applicationSummary.applicationOrcid.path}" /> <input
							type="hidden" name="confirmed" value="no" /> <input type="hidden"
							name="revokeApplicationName"
							value="${applicationSummary.applicationName.content}" /> <#if
							applicationSummary.scopePaths??> <#list
							applicationSummary.scopePaths.scopePath as scopePath> <input
							type="hidden" name="scopePaths"
							value="${scopePath.value.value()}" /> <@spring.message
							"${scopePath.value.declaringClass.name}.${scopePath.value}"/>
							<#if scopePath_has_next>;&nbsp;</#if> </#list> </#if></td width="35%">
						<td width="5%"><a
							ng-click="confirmRevoke('${applicationSummary.applicationName.content?js_string}', '${applicationSummary_index}')"
							class="glyphicon glyphicon-trash grey"
							title="${springMacroRequestContext.getMessage("manage.revokeaccess")}"></a></td>
					</form>
				</tr>
				</#list>
			</tbody>
		</table>
		</#if>
	</div>
</div>

<script type="text/ng-template" id="deactivate-account-modal">
		<div style="padding: 20px;"><h3>${springMacroRequestContext.getMessage("manage.deactivateSend")} {{primaryEmail}}</h3>
		<button class="btn" ng-click="closeModal()">${springMacroRequestContext.getMessage("manage.deactivateSend.close")}</button>
	</script>
		
	<script type="text/ng-template" id="verify-email-modal">
		<div style="padding: 20px;"><h3>${springMacroRequestContext.getMessage("manage.email.verificationEmail")} {{emailsPojo.emails[verifyEmailIdx].value}}</h3>
		<button class="btn" ng-click="closeModal()">${springMacroRequestContext.getMessage("manage.email.verificationEmail.close")}</button>
	</script>

	<script type="text/ng-template" id="delete-email-modal">
		<div style="padding: 20px;"><h3>${springMacroRequestContext.getMessage("manage.email.pleaseConfirmDeletion")} {{emailsPojo.emails[deleteEmailIdx].value}}</h3>
		<button class="btn btn-danger" ng-click="deleteEmail()">${springMacroRequestContext.getMessage("manage.email.deleteEmail")}</button> 
		<a href="" ng-click="closeModal()">${springMacroRequestContext.getMessage("manage.email.cancel")}</a><div>
	</script>		
	
	<#-- Script that will display a modal to ask for user password 												-->
	<#-- If someone wants to use this modal, it should consider the following: 									-->
	<#-- 1) There should be a password variable in his scope, there we be saved the value of this input.		-->
	<#-- 2) There should be a function submitModal() to submit the form with the desired info and the password.	-->
	<#-- 3) There should be a function closeModal() to close the the modal.										-->
	<script type="text/ng-template" id="check-password-modal">
		<div style="padding: 20px;"><h2><@orcid.msg 'check_password_modal.confirm_password' /></h2>		
		   <label for="check_password_modal.password" class=""><@orcid.msg 'check_password_modal.password' /></label>
	       <input id="check_password_modal.password" type="password" name="check_password_modal.password" ng-model="password" class="input-xlarge"/>
	       <br />
	       <button id="bottom-submit" class="btn btn-primary" ng-click="submitModal()"><@orcid.msg 'check_password_modal.submit'/></button>				
		   <button class="btn" ng-click="closeModal()"><@orcid.msg 'check_password_modal.close'/></button>
		</div>
	</script>
	
	<script type="text/ng-template" id="confirm-revoke-access-modal">
		<div style="padding: 20px;">
		   <h3>${springMacroRequestContext.getMessage("manage.application_access.revoke.confirm_revoke")} {{appName}}</h3>
		   <button class="btn btn-danger" ng-click="revokeAccess()">${springMacroRequestContext.getMessage("manage.application_access.revoke.confirm")}</button> 
		   <a href="" ng-click="closeModal()">${springMacroRequestContext.getMessage("manage.application_access.revoke.cancel")}</a>
		</div>
	</script>
</@protected>
