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
		<h1 id="manage-delegators"><@orcid.msg 'manage_delegators.title' /></h1>			
		<p>
			<@orcid.msg 'manage_delegators.description' />
		</p>
		<p>
			<strong></strong><a href="<@orcid.msg 'manage_delegators.learn_more.link.url' />" target="_blank"><@orcid.msg 'manage_delegators.learn_more.link.text' /></a></strong>&nbsp;<@orcid.msg 'manage_delegators.learn_more.text' />
		</p>
		<div ng-controller="DelegatorsCtrl" id="DelegatorsCtrl" data-search-query-url="${searchBaseUrl}">
		    <p><@orcid.msg 'manage_delegators.search'/></p>
			<p>
				<form id="delegatorsSearchForm">
					<input id="delegatorsSearch" type="text" placeholder="<@orcid.msg 'manage_delegators.search.placeholder' />" class="input-xlarge inline-input"></input>
				</form>
			</p>
			<table class="table table-bordered settings-table normal-width" ng-show="delegators.delegationDetails" ng-cloak>
				<thead>
					<tr>
						<th width="35%" ng-click="changeSorting('delegateSummary.creditName.content')">${springMacroRequestContext.getMessage("manage.thproxy")}</th>
						<th width="35%" ng-click="changeSorting('delegateSummary.orcidIdentifier.path')">${springMacroRequestContext.getMessage("search_results.thORCIDID")}</th>
						<th width="15%" ng-click="changeSorting('approvalDate.value')"><@orcid.msg 'manage_delegators.delegates_table.access_granted' /></th>
						<th width="15%" ng-click="changeSorting('delegateSummary.lastModifiedDate.value')"><@orcid.msg 'manage_delegators.delegates_table.last_modified' /></th>
					</tr>
				</thead>
				<tbody>
					<tr ng-repeat="delegationDetails in delegators.delegationDetails | orderBy:sort.column:sort.descending">
						<td width="35%"><a href="<@spring.url '/switch-user?j_username='/>{{delegationDetails.delegateSummary.orcidIdentifier.path}}" target="_blank">{{delegationDetails.delegateSummary.creditName.content}}</a></td>
						<td width="35%"><a href="{{delegationDetails.delegateSummary.orcidIdentifier.uri}}" target="_blank">{{delegationDetails.delegateSummary.orcidIdentifier.path}}</a></td>
						<td width="15%">{{delegationDetails.approvalDate.value|date}}</td>
						<td width="15%">{{delegationDetails.delegateSummary.lastModifiedDate.value|date}}</td>
					</tr>
				</tbody>
			</table>
		</div>
	</div>
</div>
</@protected>
