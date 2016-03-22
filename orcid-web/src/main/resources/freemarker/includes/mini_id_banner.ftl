<#--

    =============================================================================

    ORCID (R) Open Source
    http://orcid.org

    Copyright (c) 2012-2014 ORCID, Inc.
    Licensed under an MIT-Style License (MIT)
    http://orcid.org/open-source-license

    This copyright and license information (including a link to the full license)
    shall be included in its entirety in all copies or substantial portion of
    the software.

    =============================================================================

-->
<div class="id-banner <#if inDelegationMode>delegation-mode</#if>">
	<div class="full-name">
		{{requestInfoForm.userName}}		
	</div>
	<div class="oid">
		<#if (locked)?? && !locked>
			<div ng-controller="SwitchUserCtrl">
				<div class="dropdown id-banner-container" ng-show="unfilteredLength"
					ng-cloak>
					<a ng-click="openMenu($event)" class="id-banner-switch">
						<div class="orcid-id-container">
							${baseUriHttp}/{{requestInfoForm.userOrcid}}
							<span class="glyphicon glyphicon-chevron-down"></span>
						</div>
					</a>
					<div class="dropdown-menu id-banner-dropdown" ng-show="isDroppedDown" ng-cloak>
						<div class="id-banner-header"><@orcid.msg'public-layout.manage_proxy_account'/></div>
						<ul class="id-banner-dropdown" ng-cloak>
							<li>
								<input id="delegators-search" type="text" ng-model="searchTerm" ng-change="search()" placeholder="<@orcid.msg 'manage_delegators.search.placeholder'/>"></input>
							</li>
							<li ng-show="me && !searchTerm">
								<a ng-click="switchUser(me.delegateSummary.orcidIdentifier.path)">
									<ul>
										<li><@orcid.msg 'id_banner.switchbacktome'/></li>
										<li>{{me.delegateSummary.orcidIdentifier.uri}}</li>
									</ul>
								</a>
							</li>
							<li ng-repeat="delegationDetails in delegators.delegationDetails | orderBy:'delegateSummary.creditName.content' | limitTo:10">
								<a ng-click="switchUser(delegationDetails.delegateSummary.orcidIdentifier.path)">
									<ul>
										<li>{{delegationDetails.delegateSummary.creditName.content}}</li>
										<li>{{delegationDetails.delegateSummary.orcidIdentifier.uri}}</li>
									</ul>
								</a>
							</li>
						</ul>
					</div>
				</div>
				<div ng-hide="me || unfilteredLength > 0" ng-cloak>
					<div class="pull-right">
						${baseUriHttp}/{{requestInfoForm.userOrcid}}
					</div>
				</div>
			</div>
		</#if>
	</div>
	<div class="clearfix pull-right">
		<span><a href="" onclick="logOffReload('show_login'); return false;">(<@orcid.msg'confirm-oauth-access.notYou'/>?)</a></span>
	</div>
</div>