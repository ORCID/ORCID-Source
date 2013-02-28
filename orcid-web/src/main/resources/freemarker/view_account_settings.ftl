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
	
	<h3>Account settings</h3>	
 	<table class="table">
 		<tr>
 			<td>Email</td>
 			<td><a href="<@spring.url '/account/'/>">Edit on bio page</a></td>
 		</tr>
 		<tr>
 			<td>Password</td>
 			<td><a href="<@spring.url '/account/password'/>">Edit</a></td>
 		</tr>
 		<tr>
 			<td>Security Question</td>
 			<td><a href="<@spring.url '/account/security-question'/>">Edit</a></td>
 		</tr>
 		<tr>
 			<#assign changeNotications = sendChangeNotications?? && sendChangeNotications > 
 			<#assign sendNews = sendOrcidNews?? && sendOrcidNews >			
 			<td>Email Preferences
 				<br>(Send news)&nbsp;<input type="checkbox" ${sendNews?string("checked","")} disabled="true">
 				<br>(Send change notifications)&nbsp;<input type="checkbox" ${changeNotications?string("checked","")} disabled="true">
 			</td>
 			<td><a href="<@spring.url '/account/email-preferences'/>">Edit</td>
 		</tr>
 		<tr>
 			<td>Close Account</td>
 			<td><a href="<@spring.url '/account/deactivate-orcid'/>">Deactivate this account</a></td>
 		</tr>
 	</table>
 	<h3>Manage Permissions</h3>
 	<h4>Trusted Organisations</h4>
 	<p>You can allow permission for your ORCID record to be updated by a trusted organisation.</p>
 	<a href="http://support.orcid.org/knowledgebase/articles/131598">Find out more</a>
 	<table class="table"> 		
 		<th>Proxy</th>
 		<th>Site URL</th>
 		<th>Approval Date</th>
 		<th>Access Type</th> 		
 		<#if (profile.orcidBio.applications.applicationSummary)??>
			<div class="regFieldData">You've allowed the following applications or web sites to access your account:</div>		
			<#list profile.orcidBio.applications.applicationSummary as applicationSummary>
				<tr>
					<td>${(applicationSummary.applicationName.content)!}</td>
					<td><a href="${(applicationSummary.applicationWebsite.value)!}">${(applicationSummary.applicationWebsite.value)!}</a></td>					
					<td>Approved: ${applicationSummary.approvalDate.value.toGregorianCalendar().time?date}</td>
					<td>
						<#if (applicationSummary.scopePaths.scopePath)??>
					 	<#list applicationSummary.scopePaths.scopePath as scope>
					 		<br>${scope.value.content}
					 	</#list>
					 	<#else>No Scopes Defined
					 	</#if>
					 </td>
					 <td><a href="<@spring.url '/account/revoke-application-from-summary-view?applicationOrcid=${applicationSummary.applicationOrcid.value}'/>">Revoke Access</a></td>
				</tr>
			</#list>
		<#else><tr><td>You have not given permission for any organisation to access your ORCID</td></tr>		
		</#if>
 	</table>
 	<a href="#">Add an organisation</a><br>
 	<h4>Trusted Individuals</h4>
 	<p>You can allow permission for your ORCID record to be updated by another ORCID user.</p> 	
 	<table class="table"> 		
 		<th>Proxy</th>
 		<th>ORCID</th>
 		<th>Approval Date</th>
 		<#if (profile.orcidBio.delegation.givenPermissionTo.delegationDetails)??>
 				<#list profile.orcidBio.delegation.givenPermissionTo.delegationDetails as delegationDetails>
				<tr>
					<form action="<@spring.url '/account/revoke-delegate'/>" method="post" id="revokeDelegateForm${delegationDetails_index}">
						<td>${delegationDetails.delegateSummary.creditName.content}</td>						
						<td><a href="<@orcid.orcidUrl delegationDetails.delegateSummary.orcid.value/>">${delegationDetails.delegateSummary.orcid.value}</a></td>
						<td>Approved: ${delegationDetails.approvalDate.value.toGregorianCalendar().time?date}</td>
						<td><a href="<@spring.url '/account/revoke-delegate-from-summary-view?orcid=${delegationDetails.delegateSummary.orcid.value}'/>">Revoke Access</a></td>
					</form>
				</tr>
			</#list> 		
 		<#else><tr><td>You have not given permission for any individual to access your ORCID</td></tr>
 		</#if>	
 	</table>
 	<a href="<@spring.url '/account?activeTab=delegation-tab'/>">Add an individual</a>
</@protected>