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
			<div class="regFieldData">${springMacroRequestContext.getMessage("manage_delegation.allowedfollowparties")}</div>
			<table class="table table-bordered">
				<#list profile.orcidBio.delegation.givenPermissionTo.delegationDetails as delegationDetails>
					<tr>
						<form action="manage/revoke-delegate" method="post" id="revokeDelegateForm${delegationDetails_index}">
							<input type="hidden" name="receiverOrcid" value="${delegationDetails.delegateSummary.orcid.value}"/>
							<td>${delegationDetails.delegateSummary.creditName.content}</td>
							<td>${springMacroRequestContext.getMessage("manage_delegation.tdORCID")} <a href="<@orcid.orcidUrl delegationDetails.delegateSummary.orcid.value/>">${delegationDetails.delegateSummary.orcid.value}</a></td>
							<td>${springMacroRequestContext.getMessage("manage_delegation.tdapproved")} ${delegationDetails.approvalDate.value.toGregorianCalendar().time?date}</td>
							<td><button class="btn">${springMacroRequestContext.getMessage("manage_delegation.btnrevokeaccess")}</button></td>
						</form>
					</tr>
				</#list>
			</table>
		<#else>
			<div>${springMacroRequestContext.getMessage("manage_delegation.yetallowedanyone")}</div>
		</#if>
		<div class="profileDataCells top-margin"><a href="<@spring.url '/account/search-for-delegates' />" class="colorbox-add">${springMacroRequestContext.getMessage("manage_delegation.addanindividual")}</a></div>
		<div id="searchForDelegatesDialog"></div>
	</div>
	<#if (profile.orcidBio.delegation.givenPermissionBy)??>
		<div class="nice-box">
			<div class="regFieldData">${springMacroRequestContext.getMessage("manage_delegation.followingpartieshaveallowed")}</div>
			<table class="table">
				<#list profile.orcidBio.delegation.givenPermissionBy.delegationDetails as delegationDetails>
					<tr>
						<form action="manage/switch-user" method="post" id="revokeDelegateForm${delegationDetails_index}">
							<input type="hidden" name="giverOrcid" value="${delegationDetails.delegateSummary.orcid.value}"/>
							<td>${delegationDetails.delegateSummary.creditName.content}</td>
							<td>${springMacroRequestContext.getMessage("manage_delegation.tdORCID")} <a href="<@orcid.orcidUrl delegationDetails.delegateSummary.orcid.value/>">${delegationDetails.delegateSummary.orcid.value}</a></td>
							<td>${springMacroRequestContext.getMessage("manage_delegation.tdapproved")} ${delegationDetails.approvalDate.value.toGregorianCalendar().time?date}</td>
							<td>
							    <#if !inDelegationMode>
							        <button class="btn">${springMacroRequestContext.getMessage("manage_delegation.btnswitchtothisuser")}</button>
							    </#if>
							</td>
						</form>
					</tr>
				</#list>
			</table>
		</div>
	</#if>
