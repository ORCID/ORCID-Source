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
<@protected>
	
	<h3>${springMacroRequestContext.getMessage("manage.accountsettings")}</h3>	
 	<table class="table">
 		<tr>
 			<td>${springMacroRequestContext.getMessage("duplicate_researcher.thEmail")}</td>
 			<td><a href="<@spring.url '/account/'/>">${springMacroRequestContext.getMessage("view_account_settings.tdEditonbiopage")}</a></td>
 		</tr>
 		<tr>
 			<td>${springMacroRequestContext.getMessage("claim.password")}</td>
 			<td><a href="<@spring.url '/account/password'/>">${springMacroRequestContext.getMessage("settings.tdEdit")}</a></td>
 		</tr>
 		<tr>
 			<td>${springMacroRequestContext.getMessage("change_security_question.securityquestion")}</td>
 			<td><a href="<@spring.url '/account/security-question'/>">${springMacroRequestContext.getMessage("settings.tdEdit")}</a></td>
 		</tr>
 		<tr>
 			<#assign changeNotications = sendChangeNotications?? && sendChangeNotications > 
 			<#assign sendNews = sendOrcidNews?? && sendOrcidNews >			
 			<td>${springMacroRequestContext.getMessage("manage.email_preferences")}
 				<br>(${springMacroRequestContext.getMessage("view_account_settings.tdSendnews")})&nbsp;<input type="checkbox" ${sendNews?string("checked","")} disabled="true">
 				<br>(${springMacroRequestContext.getMessage("view_account_settings.tdSendchangenotifications")})&nbsp;<input type="checkbox" ${changeNotications?string("checked","")} disabled="true">
 			</td>
 			<td><a href="<@spring.url '/account/email-preferences'/>">${springMacroRequestContext.getMessage("settings.tdEdit")}</td>
 		</tr>
 		<tr>
 			<td>${springMacroRequestContext.getMessage("manage.close_account")}</td>
 			<td><a href="<@spring.url '/account/deactivate-orcid'/>">${springMacroRequestContext.getMessage("view_account_settings.tdDeactivatethisaccount")}</a></td>
 		</tr>
 	</table>
 	<h3>${springMacroRequestContext.getMessage("manage.managepermission")}</h3>
 	<h4>${springMacroRequestContext.getMessage("manage.trusted_organisations")}</h4>
 	<p>${springMacroRequestContext.getMessage("manage.youcanallowpermission")}</p>
 	<a href="http://support.orcid.org/knowledgebase/articles/131598">${springMacroRequestContext.getMessage("manage.findoutmore")}</a>
 	<table class="table"> 		
 		<th>${springMacroRequestContext.getMessage("manage.thproxy")}</th>
 		<th>${springMacroRequestContext.getMessage("settings.tdSiteURL")}</th>
 		<th>${springMacroRequestContext.getMessage("manage.thapprovaldate")}</th>
 		<th>${springMacroRequestContext.getMessage("manage.thaccesstype")}</th> 		
 		<#if (profile.orcidBio.applications.applicationSummary)??>
			<div class="regFieldData">${springMacroRequestContext.getMessage("view_account_settings.tdallowfollowingapp")}</div>		
			<#list profile.orcidBio.applications.applicationSummary as applicationSummary>
				<tr>
					<td>${(applicationSummary.applicationName.content)!}</td>
					<td><a href="${(applicationSummary.applicationWebsite.value)!}">${(applicationSummary.applicationWebsite.value)!}</a></td>					
					<td>${springMacroRequestContext.getMessage("manage_delegation.tdapproved")} ${applicationSummary.approvalDate.value.toGregorianCalendar().time?date}</td>
					<td>
						<#if (applicationSummary.scopePaths.scopePath)??>
					 	<#list applicationSummary.scopePaths.scopePath as scope>
					 		<br>${scope.value.content}
					 	</#list>
					 	<#else>${springMacroRequestContext.getMessage("view_account_settings.tdNoScopesDefined")}
					 	</#if>
					 </td>
					 <td><a href="<@spring.url '/account/revoke-application-from-summary-view?applicationOrcid=${applicationSummary.applicationOrcid.value}'/>">${springMacroRequestContext.getMessage("manage.revokeaccess")}</a></td>
				</tr>
			</#list>
		<#else><tr><td>${springMacroRequestContext.getMessage("view_account_settings.tdnotgivenpermission")}</td></tr>		
		</#if>
 	</table>
 	<a href="#">${springMacroRequestContext.getMessage("settings.tdorganisation")}</a><br>
 	<h4>${springMacroRequestContext.getMessage("manage.trustindividuals")}</h4>
 	<p>${springMacroRequestContext.getMessage("settings.tdallowpermission")}</p> 	
 	<table class="table"> 		
 		<th>${springMacroRequestContext.getMessage("manage.thproxy")}</th>
 		<th>${springMacroRequestContext.getMessage("home.ORCID")}</th>
 		<th>${springMacroRequestContext.getMessage("manage.thapprovaldate")}</th>
 		<#if (profile.orcidBio.delegation.givenPermissionTo.delegationDetails)??>
 				<#list profile.orcidBio.delegation.givenPermissionTo.delegationDetails as delegationDetails>
				<tr>
					<form action="<@spring.url '/account/revoke-delegate'/>" method="post" id="revokeDelegateForm${delegationDetails_index}">
						<td>${delegationDetails.delegateSummary.creditName.content}</td>						
						<td><a href="<@orcid.orcidUrl delegationDetails.delegateSummary.orcid.value/>">${delegationDetails.delegateSummary.orcid.value}</a></td>
						<td>${springMacroRequestContext.getMessage("manage_delegation.tdapproved")} ${delegationDetails.approvalDate.value.toGregorianCalendar().time?date}</td>
						<td><a href="<@spring.url '/account/revoke-delegate-from-summary-view?orcid=${delegationDetails.delegateSummary.orcid.value}'/>">${springMacroRequestContext.getMessage("manage.revokeaccess")}</a></td>
					</form>
				</tr>
			</#list> 		
 		<#else><tr><td>${springMacroRequestContext.getMessage("view_account_settings.tdnotgivenpermissionindividual")}</td></tr>
 		</#if>	
 	</table>
 	<a href="<@spring.url '/account?activeTab=delegation-tab'/>">${springMacroRequestContext.getMessage("manage_delegation.addanindividual")}</a>
</@protected>