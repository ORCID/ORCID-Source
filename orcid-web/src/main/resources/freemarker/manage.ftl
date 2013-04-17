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
		<div class="span3 lhs override">
			<ul class="settings-nav">
				<li><a href="#account-settings">${springMacroRequestContext.getMessage("manage.accountsettings")}</a></li>
				<li><a href="#manage-permissions">${springMacroRequestContext.getMessage("manage.managepermission")}</a></li>
			</ul>
		</div>
		<div class="span9">
			<h1 id="account-settings">${springMacroRequestContext.getMessage("manage.account_settings")}</h1>
			<#assign open = "" />
			<@spring.bind "managePasswordOptionsForm.*" />
			<#if spring.status.error>
				<#assign open="password" />
				<div class="alert alert-error">
				    <ul class="validationerrors">
				        <#list spring.status.errorMessages?sort as error> <li>${error}</li> </#list>
				    </ul>
				</div>
			</#if>
			<#if passwordOptionsSaved?? && passwordOptionsSaved>
				<div class="alert alert-success">
				    <strong><@spring.message "orcid.frontend.web.passwordoptions_changed"/></strong>
				</div>
			</#if>
			
			<table class="table table-bordered settings-table" id="ng-app" ng-app="orcidApp" ng-controller="EditTableCtrl" style="margin: 0px, padding: 0px;">
				<tbody>
					<tr>
						<th>${springMacroRequestContext.getMessage("public_profile.h3PersonalInformation")}</th>
						<td>
							<div><a href="<@spring.url '/account/manage-bio-settings'/>" class="update">${springMacroRequestContext.getMessage("settings.tdEdit")}</a></div>
						</td>
					</tr>
						<tr>
							<th><a name="editEmail"></a>${springMacroRequestContext.getMessage("manage.thEmail")}</th>
							<td>
								<a href="" ng-click="toggleEmailEdit()" ng-bind="emailToggleText"></a>
							</td>
						</tr>
						<tr ng-controller="EmailEdit" ng-show="showEditEmail" ng-cloak>
							<td colspan="2">
							<div class="editTablePadCell35">
								<!-- we should never see errors here, but just to be safe -->
								<span class="orcid-error" ng-show="emailsPojo.errors.length > 0">
			   						<span ng-repeat='error in emailsPojo.errors' ng-bind-html-unsafe="error"></span>
			   					</span>	
			   					<table id="emailTable">
			   						<tr ng-repeat='email in emailsPojo.emails'>
			   						  <td class="padRgt" ng-class="{primaryEmail:email.primary}" ng-bind="email.value">
			   						  </td>
			   						  <td class="padRgt">
			   						  		<span ng-hide="email.primary" ><a href="" ng-click="setPrimary($index)" ng-bind="email.primary | emailPrimaryFtr"></a></span>
			   							    <span ng-show="email.primary" class="muted" style="color: #bd362f;" ng-bind="email.primary | emailPrimaryFtr"></span>
			   						  </td> 
			   						  <td class="padRgt">
			   						  	<select style="width: 100px; margin: 0px;" ng-change="saveEmail()" ng-model="email.current">
              							    <option value="true" ng-selected="email.current == true">Current</option>
              							    <option value="false" ng-selected="email.current == false">Past</option>              
            						    </select>
			   						  </td>
			   						  <td class="padRgt">
			   						      <span ng-hide="email.verified"><a href="" ng-click="verifyEmail($index)">${springMacroRequestContext.getMessage("manage.email.verify")}</a></span>
		   							      <span ng-show="email.verified">${springMacroRequestContext.getMessage("manage.email.verified")}</span>		
			   						  </td>
			   						  <td class="padRgt">
			   						  	<a href="" class="icon-trash grey" ng-show="email.primary == false" ng-click="deleteEmail($index)"></a>
			   						  </td>
			   						  <td class="padRgt">
			   						     <ul class="privacyToggle">
		   							       <li class="publicActive" ng-class="{publicInActive: email.visibility != 'PUBLIC'}"><a href="" title="PUBLIC" ng-click="setPrivacy($index, 'PUBLIC', $event)"></a></li>
		   							       <li class="limitedActive" ng-class="{limitedInActive: email.visibility != 'LIMITED'}"><a href="" title="LIMITED" ng-click="setPrivacy($index, 'LIMITED', $event)"></a></li>
		   							       <li class="privateActive" ng-class="{privateInActive: email.visibility != 'PRIVATE'}"><a href="" title="PRIVATE" ng-click="setPrivacy($index, 'PRIVATE', $event)"></a></li>
		   							     </ul>
			   						  </td>
			   						  <td style="width: 20px;">
			   						  <div class="privacyLegendHide">
			   						  <a href="javascript:void(0);"><i class="icon-question-sign"></i></a>
			   						  <div class="privacyLegend"></div>
			   						  </div>
                               	      </td>
			   						</tr>
				   					</table>
				   					<div>
			   							<input type="email" placeholder="Add Another Email" class="input-xlarge" ng-model="inputEmail.value" style="margin: 0px;" required/> <span ng-click="addEmail()" class="btn btn-primary">${springMacroRequestContext.getMessage("manage.spanadd")}</span>
			   							<span class="orcid-error" ng-show="inputEmail.errors.length > 0">
				   							<span ng-repeat='error in inputEmail.errors' ng-bind-html-unsafe="error"></span>
				   						</span>
				   					</div>	
			   					</div>
							</td>
					</tr>
					<tr>
						<th><a name="editPassword"></a>${springMacroRequestContext.getMessage("manage.password")}</th>
						<td>
						    <a href="" ng-click="togglePasswordEdit()" ng-bind="passwordToggleText"></a>
						</td>
					</tr>
					<tr ng-controller="PasswordEdit" ng-show="showEditPassword" ng-cloak>
						<td colspan="2">
						<div class="editTablePadCell35">
							    <span class="orcid-error" ng-show="changePasswordPojo.errors.length > 0">
				   						<div ng-repeat='error in changePasswordPojo.errors' ng-bind-html-unsafe="error"></div>
				   				</span>
							    <div>
							    	<label for="passwordField" class="">${springMacroRequestContext.getMessage("change_password.oldpassword")}</label>
							    	<div class="relative">
							        	<input id="passwordField" type="password" name="oldPassword" ng-model="changePasswordPojo.oldPassword" class="input-xlarge"/>
							        	<span class="required">*</span>            
							    	</div>
								</div>
								<div>
							    	<label for="passwordField" class="">${springMacroRequestContext.getMessage("change_password.newpassword")}</label>
							    	<div class="relative">
							        	<input id="password" type="password" name="password" ng-model="changePasswordPojo.password" class="input-xlarge"/>
							        	<span class="required">*</span>
							        	<a class="password-info" href="#"><i class="icon-question-sign"></i></a>    
							    	</div>
								</div>
								<div>
							    	<label for="retypedPassword" class="">${springMacroRequestContext.getMessage("change_password.confirmnewpassword")}</label>
							    	<div class="relative">
							    		<input id="retypedPassword" type="password" name="retypedPassword" ng-model="changePasswordPojo.retypedPassword" class="input-xlarge"/>
							        	<span class="required">*</span>
							    	</div>        
								</div><br />
							    <div>
							        <button id="bottom-submit-password-change" class="btn btn-primary" ng-click="saveChangePassword()">${springMacroRequestContext.getMessage("freemarker.btnsavechanges")}</button>
							        <button id="bottom-clear-password-changes" class="btn close-parent-popover" ng-click="getChangePassword()">${springMacroRequestContext.getMessage("freemarker.btncancel")}</button>
							    </div>
						</div>
						</td>
					</tr>
					<tr>
						<th><a name="editPrivacyPreferences"></a>${springMacroRequestContext.getMessage("manage.privacy_preferences")}</th>
						<td>
							<a href="" ng-click="togglePrivacyPreferencesEdit()" ng-bind="privacyPreferencesToggleText"></a>
						</td>
					</tr>
					<tr ng-controller="PrivacyPreferences" ng-show="showEditPrivacyPreferences" ng-cloak>
						<td colspan="2">
						<div class="editTablePadCell35">
							${springMacroRequestContext.getMessage("privacy_preferences.labelDefaultprivacyfornewWorks")}<br />
							<div>
								<ul class="privacyToggle">
			   						<li class="publicActive" ng-class="{publicInActive: privacyPreferences.workVisibilityDefault.value != 'PUBLIC'}"><a href="" title="PUBLIC" ng-click="updateWorkVisibilityDefault('PUBLIC', $event)"></a></li>
			   						<li class="limitedActive" ng-class="{limitedInActive: privacyPreferences.workVisibilityDefault.value != 'LIMITED'}"><a href="" title="LIMITED" ng-click="updateWorkVisibilityDefault('LIMITED', $event)"></a></li>
			   						<li class="privateActive" ng-class="{privateInActive: privacyPreferences.workVisibilityDefault.value != 'PRIVATE'}"><a href="" title="PRIVATE" ng-click="updateWorkVisibilityDefault('PRIVATE', $event)"></a></li>
			   					</ul>
			   					<div class="privacyLegendHide" style="position: absolute; left: 110px; top: 25px;">
				   				    <a href="javascript:void(0);"><i class="icon-question-sign"></i></a>
				   				    <div class="privacyLegend"></div>
				   				</div>
			   				</div>
						</div>
						</td>
					</tr>
				    <tr>
						<th><a name="editSecurityQuestion"></a>${springMacroRequestContext.getMessage("manage.security_question")}</th>
						<td>
							<a href="" ng-click="toggleSecurityQuestionEdit()" ng-bind="securityQuestionToggleText"></a>
						</td>
					</tr>
					<tr ng-controller="SecurityQuestionEdit" ng-show="showEditSecurityQuestion" ng-cloak>
						<td colspan="2">
						<div class="editTablePadCell35">
							<span class="orcid-error" ng-show="securityQuestionPojo.errors.length > 0">
			   					<span ng-repeat='error in securityQuestionPojo.errors' ng-bind-html-unsafe="error"></span><br />
			   				</span>	
							<label for="changeSecurityQuestionForm.securityQuestionAnswer" class="">${springMacroRequestContext.getMessage("manage.security_question")}</label>
							<select id="securityQuestionId" name="securityQuestionId" class="input-xlarge" ng-model="securityQuestionPojo.securityQuestionId">
								<#list securityQuestions?keys as key>
								   <option value="${key}" ng-selected="securityQuestionPojo.securityQuestionId == ${key}">${securityQuestions[key]}</option>
								</#list>
	    					</select> 
	    					<label for="changeSecurityQuestionForm.securityQuestionAnswer" class="">${springMacroRequestContext.getMessage("manage.securityAnswer")}</label>
	    					<input type="text" id="securityQuestionAnswer" name="securityQuestionAnswer" class="input-xlarge" ng-model="securityQuestionPojo.securityAnswer"><br />
	    					<br />
	    					<button id="bottom-submit-security-question" class="btn btn-primary" ng-click="saveSecurityQuestion()">Save changes</button>
	    					<button id="bottom-reset-security-question" class="btn close-parent-popover" ng-click="getSecurityQuestion()">Cancel</button>
						</div>
						</td>
					</tr>							
					<tr>
						<th><a name="editEmailPreferences"></a>${springMacroRequestContext.getMessage("manage.email_preferences")}</th>
						<td>
							<a href="" ng-click="toggleEmailPreferencesEdit()" ng-bind="emailPreferencesToggleText"></a>
						</td>
					</tr>
					<tr ng-controller="PrivacyPreferences" ng-show="showEditEmailPreferences" ng-cloak>
						<td colspan="2">
						<div class="editTablePadCell35">
							    <label class="checkbox">
				                <input type="checkbox" id="sendOrcidChangeNotifcations" name="sendOrcidChangeNotifcations" ng-model="privacyPreferences.sendChangeNotifications.value" ng-click="savePrivacyPreferences()" />
				                    ${springMacroRequestContext.getMessage("change_email_preferences.sendnotification")}
				                </label>
				                <label class="checkbox">
				                <input type="checkbox" id="sendOrcidNews" name="sendOrcidNews" ng-model="privacyPreferences.sendOrcidNews.value" ng-click="savePrivacyPreferences()" />
				                    ${springMacroRequestContext.getMessage("change_email_preferences.sendinformation")}
				                </label>
				                <br />
				                <p>
				                	<strong>${springMacroRequestContext.getMessage("change_email_preferences.privacy")}</strong> 
				                	${springMacroRequestContext.getMessage("change_email_preferences.yourregistrationinfo")}
				                	<a href="${aboutUri}/footer/privacy-policy?lang=${locale}" target="_blank">${springMacroRequestContext.getMessage("change_email_preferences.privacyPolicy")}</a>
				                	${springMacroRequestContext.getMessage("change_email_preferences.and")}
				                	<a href="${aboutUri}/content/orcid-terms-use?lang=${locale}" target="_blank">${springMacroRequestContext.getMessage("change_email_preferences.termsAnd")}</a>.
				                </p>
						</div>
						</td>
					</tr>					
					<tr>
						<th><a name="editDeactivate"></a>${springMacroRequestContext.getMessage("manage.close_account")}</th>
						<td>
							<a href="" ng-click="toggleDeactivateEdit()" ng-bind="deactivateToggleText"></a>
						</td>
					</tr>
					<tr ng-controller="DeactivateAccount" ng-show="showEditDeactivate" ng-cloak>
						<td colspan="2">
						<div class="editTablePadCell35">
								<p>${springMacroRequestContext.getMessage("deactivate_orcid.you_may")}</p>
								<p>${springMacroRequestContext.getMessage("deactivate_orcid.once")}</p>
						        <a href="http://support.orcid.org/knowledgebase/articles/148970-closing-an-orcid-account" target="_blank">${springMacroRequestContext.getMessage("deactivate_orcid.close_an")}</a><br />
						     	<br />
						     	<strong>${springMacroRequestContext.getMessage("deactivate_orcid.listTitle")}</strong>
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
			
			
              <div class="popover bottom password-details settings-password">
                <div class="arrow"></div>
                <div class="popover-content">
                    <div class="help-block">
                    	<p>${springMacroRequestContext.getMessage("manage.must8morecharacters")}</p>
                     	<ul>
                           	<li>${springMacroRequestContext.getMessage("manage.liatleast09")}</li>
                           	<li>${springMacroRequestContext.getMessage("manage.liatleast1following")}</li>
                               	<ul>
                                  	<li>${springMacroRequestContext.getMessage("manage.lialphacharacter")}</li>
                                    <li>${springMacroRequestContext.getMessage("manage.lianysymbols")}<br /> ! @ # $ % ^ * ( ) ~ `{ } [ ] | \ &amp; _</li>
                                </ul>
                            <li>${springMacroRequestContext.getMessage("manage.limanagespacecharacters")}</li>
                        </ul>
                        <p>${springMacroRequestContext.getMessage("manage.examplesunmoon2")}</p>
                    </div>
                </div>
            </div>   
            
            
            <h1 id="manage-permissions">${springMacroRequestContext.getMessage("manage.manage_permissions")}</h1>
			<h3><b>${springMacroRequestContext.getMessage("manage.trusted_organisations")}</b></h3>
			<p>${springMacroRequestContext.getMessage("manage.youcanallowpermission")}<br /> <a href="http://support.orcid.org/knowledgebase/articles/131598">${springMacroRequestContext.getMessage("manage.findoutmore")}</a></p>
			<#if (profile.orcidBio.applications.applicationSummary)??>
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
    				    <#list profile.orcidBio.applications.applicationSummary as applicationSummary>
                	       <tr>       	       		
                                <form action="manage/revoke-application" method="post" class="revokeApplicationForm" id="revokeApplicationForm${applicationSummary_index}">
                                    <td class="revokeApplicationName">${(applicationSummary.applicationName.content)!}<br /><a href="<@orcid.absUrl applicationSummary.applicationWebsite/>">${applicationSummary.applicationWebsite.value}</a></td>
                                    <input type="hidden" name="applicationOrcid" value="${applicationSummary.applicationOrcid.value}"/>
                                    <input type="hidden" name="confirmed" value="no"/>
                                    <input type="hidden" name="revokeApplicationName" value="${applicationSummary.applicationName.content}"/>
                                    <td width="35%">${applicationSummary.approvalDate.value.toGregorianCalendar().time?date}</td>
                                    <td width="5%">
                                        <#if applicationSummary.scopePaths??>
                                            <#list applicationSummary.scopePaths.scopePath as scopePath>
                                                <input type="hidden" name="scopePaths" value="${scopePath.value.value()}"/>
                                                <@spring.message "${scopePath.value.declaringClass.name}.${scopePath.value}"/>
                                                <#if scopePath_has_next>;&nbsp;</#if> 
                                            </#list>
                                        </#if>
                                    </td width="35%">                                    
                                    <td width="5%"><button class="btn btn-link" onclick="orcidGA.gaPush(['_trackEvent', 'Disengagement', 'Revoke_Access', 'OAuth ${applicationSummary.applicationName.content}']);">${springMacroRequestContext.getMessage("manage.revokeaccess")}</button></td>
                                </form>
                            </tr>
                        </#list>
    				</tbody>
    			</table>
			</#if>
			
			<#--<h4><b>${springMacroRequestContext.getMessage("manage.trustindividuals")}</b></h4>
			<#include "/manage_delegation.ftl" />-->
			
		</div>
	</div>
	




</@protected>