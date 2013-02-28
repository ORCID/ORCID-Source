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
	<div class="nice-box">
		<#if (profile.orcidBio.delegation.givenPermissionTo)??>
			<div class="regFieldData">You've allowed the following parties to manage your ORCID Record on your behalf:</div>
			<table class="table table-bordered">
				<#list profile.orcidBio.delegation.givenPermissionTo.delegationDetails as delegationDetails>
					<tr>
						<form action="manage/revoke-delegate" method="post" id="revokeDelegateForm${delegationDetails_index}">
							<input type="hidden" name="receiverOrcid" value="${delegationDetails.delegateSummary.orcid.value}"/>
							<td>${delegationDetails.delegateSummary.creditName.content}</td>
							<td>ORCID: <a href="<@orcid.orcidUrl delegationDetails.delegateSummary.orcid.value/>">${delegationDetails.delegateSummary.orcid.value}</a></td>
							<td>Approved: ${delegationDetails.approvalDate.value.toGregorianCalendar().time?date}</td>
							<td><button class="btn">Revoke Access</button></td>
						</form>
					</tr>
				</#list>
			</table>
		<#else>
			<div>You've not yet allowed anyone to manage your ORCID Record on your behalf. Click the button below to add a delegated ORCID Record manager.</div>
		</#if>
		<div class="profileDataCells top-margin"><a href="<@spring.url '/account/search-for-delegates' />" class="colorbox-add">Add an individual</button></div>
		<div id="searchForDelegatesDialog"></div>
	</div>
	<#if (profile.orcidBio.delegation.givenPermissionBy)??>
		<div class="nice-box">
			<div class="regFieldData">The following parties have allowed you to manage their ORCID Record on their behalf:</div>
			<table class="table">
				<#list profile.orcidBio.delegation.givenPermissionBy.delegationDetails as delegationDetails>
					<tr>
						<form action="manage/switch-user" method="post" id="revokeDelegateForm${delegationDetails_index}">
							<input type="hidden" name="giverOrcid" value="${delegationDetails.delegateSummary.orcid.value}"/>
							<td>${delegationDetails.delegateSummary.creditName.content}</td>
							<td>ORCID: <a href="<@orcid.orcidUrl delegationDetails.delegateSummary.orcid.value/>">${delegationDetails.delegateSummary.orcid.value}</a></td>
							<td>Approved: ${delegationDetails.approvalDate.value.toGregorianCalendar().time?date}</td>
							<td>
							    <#if !inDelegationMode>
							        <button class="btn">Switch to this user</button>
							    </#if>
							</td>
						</form>
					</tr>
				</#list>
			</table>
		</div>
	</#if>
