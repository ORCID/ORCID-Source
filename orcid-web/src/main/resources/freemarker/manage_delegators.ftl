<@protected classes=['manage'] nav="settings">
	<div class="row">
		<div class="col-md-3 col-sm-12 col-xs-12 padding-fix">
			<div class="lhs">
		<#include "includes/id_banner.ftl"/>
	</div>
	</div>
	<div class="col-md-9">
		<h1 id="manage-delegators"><@orcid.msg 'manage_delegators.title' /></h1>			
		<p>
			<@orcid.msg 'manage_delegators.description' />
		</p>
		<p>
			<strong></strong><a href="<@orcid.msg 'manage_delegators.learn_more.link.url' />" target="manage_delegators.learn_more.link.text"><@orcid.msg 'manage_delegators.learn_more.link.text' /></a></strong>&nbsp;<@orcid.msg 'manage_delegators.learn_more.text' />
		</p>
		<div ng-controller="DelegatorsCtrl" id="DelegatorsCtrl" data-search-query-url="${searchBaseUrl}">
		    <p><@orcid.msg 'manage_delegators.search'/></p>
			<p>
				<form id="delegatorsSearchForm">
					<input id="delegatorsSearch" type="text" placeholder="<@orcid.msg 'manage_delegators.search.placeholder' />" class="input-xlarge inline-input"></input>
				</form>
			</p>
			<table class="table table-bordered settings-table normal-width" ng-show="delegators.length > 0" ng-cloak>
				<thead>
					<tr>
						<th width="35%" ng-click="changeSorting('receiverName.value')">${springMacroRequestContext.getMessage("manage.thproxy")}</th>
						<th width="35%" ng-click="changeSorting('receiverOrcid.path')">${springMacroRequestContext.getMessage("search_results.thORCIDID")}</th>
						<th width="15%" ng-click="changeSorting('approvalDate')"><@orcid.msg 'manage_delegators.delegates_table.access_granted' /></th>
						<th width="15%" ng-click="changeSorting('lastModifiedDate')"><@orcid.msg 'manage_delegators.delegates_table.last_modified' /></th>
					</tr>
				</thead>
				<tbody>
					<tr ng-repeat="delegationDetails in delegators | orderBy:sort.column:sort.descending">
						<td width="35%"><a href="<@orcid.rootPath '/switch-user?username='/>{{delegationDetails.giverOrcid.path}}" target="giverName.value">{{delegationDetails.giverName.value}}</a></td>
						<td width="35%"><a href="{{delegationDetails.giverOrcid.uri}}" target="{{delegationDetails.giverOrcid.path}}">{{delegationDetails.giverOrcid.path}}</a></td>						
						<td width="15%">{{delegationDetails.approvalDate|date:'yyyy-MM-dd'}}</td>
						<td width="15%">{{delegationDetails.lastModifiedDate|date:'yyyy-MM-dd'}}</td>
					</tr>
				</tbody>
			</table>
		</div>
	</div>
</div>
</@protected>
