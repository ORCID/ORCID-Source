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
				<li><a href="#account-settings">Account Settings</a></li>
				<li><a href="#manage-permissions">Manage Permissions</a></li>
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
			
			<table class="table table-bordered settings-table" id="ng-app" ng-app="orcidApp" ng-controller="EditTableCtrl">
				<tbody>
				    <#--<tr>
                        <th>${springMacroRequestContext.getMessage("manage.personal_details")}</th>
                        <td>
                            <@orcid.settingsPopover "password" "/manage_personal_info.ftl" "Edit personal details" open />
                        </td>
                    </tr>-->
					<tr>
						<th>${springMacroRequestContext.getMessage("manage.emails")}</th>
						<td>
							<div><a href="<@spring.url '/account/manage-bio-settings'/>" class="update">Edit personal information</a></div>
						</td>
					</tr>
					<#if (RequestParameters['showMulti'])??>
						<tr>
							<th>Email</th>
							<td>
								<a href="" ng-click="toggleEmail()" ng-bind="toggleText"></a>
							</td>
						</tr>
						<tr ng-controller="EmailEdit" ng-show="showEditEmail" ng-cloak>
							<td colspan="2">
								<!-- we should never see errors here, but just to be safe -->
		   						<span class="orcid-error" ng-show="emailsPojo.errors.length > 0">
			   						<span ng-repeat='error in emailsPojo.errors' ng-bind-html-unsafe="error"></span>
			   					</span>
		   						<div ng-repeat='email in emailsPojo.emails' style="height: 35px;">
		   							<div style="width: 300px; display: inline-block; *display: inline;" ng-bind="email.value"></div>
		   							<div style="width: 100px; display: inline-block; *display: inline;"><a href="" ng-click="setPrimary($index)" ng-class="{muted: email.primary==false}" ng-bind="email.primary | emailPrimaryFtr"></a>
		   							</div> 
		   							<div ng-click="toggleCurrent($index)" ng-bind="email.current | emailCurrentFtr" style="width: 70px; display: inline-block; *display: inline;"></div> 
		   							<span ng-bind="email.verified | emailVerifiedFtr" ng-click="verifyEmail($index)"></span>
		   							<span class="orcid-error" ng-show="email.errors.length > 0">
		   							   <span ng-repeat='error in email.errors' ng-bind-html-unsafe="error"></span>
		   							</span>
		   							<div style="width 30px; display: inline-block; *display: inline;">
		   								<span ng-show="email.primary == false" ng-click="deleteEmail($index)" class="btn btn-danger">X</span>
		   							</div>
		   							<div class="privacy-tool" style="display:inline-block;">
								        <div class="btn-group privacy-group abs-left-top">
								            <button class="btn dropdown-toggle privacy-toggle" ng-class="email.visibility | emailVisibilityBtnClassFtr" ng-bind="email.visibility | emailVisibilityFtr" ng-click="toggleVisibility($index)"></button>
								        </div>
									</div>
		   						</div>
		   						<div>
		   							<input type="text" placeholder="Add Another Email" class="input-xlarge" ng-model="inputEmail.value" style="margin: 0px;"/> <span ng-click="add()" class="btn">Add</span>
		   							<span class="orcid-error" ng-show="inputEmail.errors.length > 0">
			   							<span ng-repeat='error in inputEmail.errors' ng-bind-html-unsafe="error"></span>
			   						</span>
			   					</div>	
							</td>
						</tr>
					</#if>
					<tr>
						<th>${springMacroRequestContext.getMessage("manage.password")}</th>
						<td>
							<div><@orcid.settingsPopover "password" "/account/password" "Change password" open /></div>
						</td>
					</tr>
					<tr>
						<th>${springMacroRequestContext.getMessage("manage.privacy_preferences")}</th>
						<td>
							<div><@orcid.settingsPopover "security" "/account/privacy-preferences" "Change privacy preferences" open /></div>
						</td>
					</tr>					
					<tr>
						<th>${springMacroRequestContext.getMessage("manage.security_question")}</th>
						<td>
							<div><@orcid.settingsPopover "security" "/account/security-question" "Update security question" open /></div>
						</td>
					</tr>
					<tr>
                        <th>${springMacroRequestContext.getMessage("manage.email_preferences")}</th>
                        <td>
                            <div><@orcid.settingsPopover "security" "/account/email-preferences" "Change email preferences" open /></div>
                        </td>
					</tr>
					<tr>
						<th>${springMacroRequestContext.getMessage("manage.close_account")}</th>
						<td><div><@orcid.settingsPopover "security" "/account/deactivate-orcid" "Deactivate this ORCID record..." open "Deactivate this ORCID record..."/></div></td>
					</tr>
				</tbody>
			</table>
			
			
              <div class="popover bottom password-details settings-password">
                <div class="arrow"></div>
                <div class="popover-content">
                    <div class="help-block">
                    	<p>Must be 8 or more characters and contain:</p>
                     	<ul>
                           	<li>at least 1 numeral: 0 - 9</li>
                           	<li>at least 1 of the following:</li>
                               	<ul>
                                  	<li>alpha character, case-sensitive a-Z</li>
                                    <li>any of the following symbols:<br /> ! @ # $ % ^ * ( ) ~ `{ } [ ] | \ &amp; _</li>
                                </ul>
                            <li>optionally the space character, i.e ' ' and other punctuation such as . , ;</li>
                        </ul>
                        <p>Example: sun% moon2</p>
                    </div>
                </div>
            </div>   
            
            
            <h1 id="manage-permissions">${springMacroRequestContext.getMessage("manage.manage_permissions")}</h1>
			<h3><b>${springMacroRequestContext.getMessage("manage.trusted_organisations")}</b></h3>
			<p>You can allow permission for your ORCID Record to be updated by a trusted organisation.<br /> <a href="http://support.orcid.org/knowledgebase/articles/131598">Find out more</a></p>
			<#if (profile.orcidBio.applications.applicationSummary)??>
    			<table class="table table-bordered settings-table normal-width">
    				<thead>
    					<tr>
    						<th width="35%">Proxy</th>
    						<th width="5%">Approval date</th>
    						<th width="35%">Access type</th>
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
                                    <td width="5%"><button class="btn btn-link">Revoke Access</button></td>
                                </form>
                            </tr>
                        </#list>
    				</tbody>
    			</table>
			</#if>
			
			<#--<h4><b>Trusted Individuals</b></h4>
			<#include "/manage_delegation.ftl" />-->
			
		</div>
	</div>




</@protected>