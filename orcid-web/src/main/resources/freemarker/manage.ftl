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
			
			<table class="table table-bordered settings-table" id="ng-app" ng-app="orcidApp" ng-controller="EditTableCtrl">
				<tbody>
				    <#--<tr>
                        <th>${springMacroRequestContext.getMessage("manage.personal_details")}</th>
                        <td>
                            <@orcid.settingsPopover "password" "/manage_personal_info.ftl" "Edit personal details" open />
                        </td>
                    </tr>-->
					<tr>
						<th>${springMacroRequestContext.getMessage("public_profile.h3PersonalInformation")}</th>
						<td>
							<div><a href="<@spring.url '/account/manage-bio-settings'/>" class="update">${springMacroRequestContext.getMessage("settings.tdEdit")}</a></div>
						</td>
					</tr>
						<tr>
							<th>${springMacroRequestContext.getMessage("manage.thEmail")}</th>
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
		   							<div style="width: 300px; display: inline-block; *display: inline;" ng-class="{primaryEmail:email.primary}" ng-bind="email.value"></div>
		   							<div style="width: 100px; display: inline-block; *display: inline;">
		   							
		   							<span ng-hide="email.primary" ><a href="" ng-click="setPrimary($index)" ng-bind="email.primary | emailPrimaryFtr"></a></span>
		   							<span ng-show="email.primary" class="muted" style="color: #bd362f"ng-bind="email.primary | emailPrimaryFtr"></span>
		   							</div> 
		   							<select style="width: 100px; height: 26px;" ng-change="save()" ng-model="email.current">
              							<option value="true" ng-selected="email.current == true">Current</option>
              							<option value="false" ng-selected="email.current == false">Past</option>              
            						</select>
		   							<span ng-hide="email.verified"><a href="" ng-click="verifyEmail($index)">Verify</a></span>
		   							<span ng-show="email.verified">Verified</span>		
		   							<span class="orcid-error" ng-show="email.errors.length > 0">
		   							   <span ng-repeat='error in email.errors' ng-bind-html-unsafe="error"></span>
		   							</span>
		   							<div style="display:inline-block;">
		   							      <button ng-show="email.primary == false" ng-click="deleteEmail($index)" class="btn btn-small" style="position: absolute; top: 0;">X</button>
		   							</div>
		   							<div style="display:inline-block;">
								        <div class="btn-group abs-left-top"  ng-class="{open: email.value==curPrivToggle}">
								            <button class="btn dropdown-toggle privacy-toggle btn-small" ng-class="email.visibility | emailVisibilityBtnClassFtr" ng-bind-html-unsafe="email.visibility | emailVisibilityFtr" ng-click="togglePrivacySelect($index)"></button>
											<ul class="dropdown-menu privacy-menu show">
								                <li><a class="btn btn-success btn-privacy btn-small" href="#" ng-click="setPrivacy($index, 'PUBLIC', $event)">${springMacroRequestContext.getMessage("manage.lipublic")} <span class="caret"></span></a></li>
								                <li><a class="btn btn-warning btn-privacy btn-small" href="#" ng-click="setPrivacy($index, 'LIMITED', $event)">${springMacroRequestContext.getMessage("manage.lilimited")} <span class="caret"></span></a></li>
								                <li><a class="btn btn-danger btn-privacy btn-small" href="#" ng-click="setPrivacy($index, 'PRIVATE', $event)">${springMacroRequestContext.getMessage("manage.liprivate")} <span class="caret"></span></a></li>	
								                <li><a class="btn btn-small" href="http://support.orcid.org/knowledgebase/articles/124518" target="_blank">${springMacroRequestContext.getMessage("manage.lihelp")} <span class="caret"></span></a></li>
	            							</ul>        								        
								        </div>
									</div>
		   						</div>
		   						<div>
		   							<input type="email" placeholder="Add Another Email" class="input-xlarge" ng-model="inputEmail.value" style="margin: 0px;" required/> <span ng-click="add()" class="btn">${springMacroRequestContext.getMessage("manage.spanadd")}</span>
		   							<span class="orcid-error" ng-show="inputEmail.errors.length > 0">
			   							<span ng-repeat='error in inputEmail.errors' ng-bind-html-unsafe="error"></span>
			   						</span>
			   					</div>	
							</td>
						</tr>
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
                                    <td width="5%"><button class="btn btn-link">${springMacroRequestContext.getMessage("manage.revokeaccess")}</button></td>
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