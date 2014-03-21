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
	</div>
	<div class="col-md-9">
		<h1 id="manage-delegators">Delegators</h1>
		
		<h3>
			<b>Individuals who have given you delegate permission</b>
		</h3>
		<p>
			Other ORCID users can allow permission for their ORCID Record to be updated by you<br />
			<a href="http://support.orcid.org/knowledgebase/articles/delegation"
				target=_blank"">${springMacroRequestContext.getMessage("manage.findoutmore")}</a>
		</p>
		<div ng-controller="DelegatorsCtrl" id="DelegatorsCtrl" data-search-query-url="${searchBaseUrl}">
		    <p>Search for individuals who have given you delegate access</p>
			<p>
				<form id="delegatorsSearchForm">
					<input id="delegatorsSearch" type="text" placeholder="ORCID or names" class="input-xlarge inline-input"></input>
				</form>
			</p>
			<table class="table table-bordered settings-table normal-width" ng-show="delegation.givenPermissionBy.delegationDetails" ng-cloak>
				<thead>
					<tr>
						<th width="35%">${springMacroRequestContext.getMessage("manage.thproxy")}</th>
						<th width="5%">${springMacroRequestContext.getMessage("manage.thapprovaldate")}</th>
					</tr>
				</thead>
				<tbody>
					<tr ng-repeat="delegationDetails in delegation.givenPermissionBy.delegationDetails | orderBy:'delegateSummary.creditName.content'">
						<td width="35%"><a href="<@spring.url '/switch-user?j_username='/>{{delegationDetails.delegateSummary.orcidIdentifier.path}}" target="_blank">{{delegationDetails.delegateSummary.creditName.content}}</a></td>
						<td width="35%">{{delegationDetails.approvalDate.value|date}}</td>
					</tr>
				</tbody>
			</table>
		</div>
	</div>
</div>
</@protected>
