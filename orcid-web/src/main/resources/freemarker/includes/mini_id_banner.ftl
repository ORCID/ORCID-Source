<div class="id-banner <#if inDelegationMode>delegation-mode</#if>">
	<div class="full-name pull-right" ng-if="requestInfoForm.userName != null" ng-cloak>	
		{{requestInfoForm.userName}}		
	</div>
	<div class="oid">
		<#if (locked)?? && !locked>
			<div ng-controller="SwitchUserCtrl">
				<div class="dropdown id-banner-container" ng-show="unfilteredLength"
					ng-cloak>
					<a ng-click="openMenu($event)" class="id-banner-switch">
						<div class="orcid-id-container" ng-cloak>
							<@orcid.checkFeatureStatus featureName='HTTPS_IDS'>
								${baseUri}/{{requestInfoForm.userOrcid}}
							</@orcid.checkFeatureStatus>
							<@orcid.checkFeatureStatus featureName='HTTPS_IDS' enabled=false>
                    			${baseUriHttp}/{{requestInfoForm.userOrcid}}
               				</@orcid.checkFeatureStatus>
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
								<a ng-click="switchUser(me.giverOrcid.path)">
									<ul>
										<li><@orcid.msg 'id_banner.switchbacktome'/></li>										
										<li>{{me.giverOrcid.uri}}</li>															
									</ul>
								</a>
							</li>
							<li ng-repeat="delegationDetails in delegators | orderBy:'giverName.value' | limitTo:10">
								<a ng-click="switchUser(delegationDetails.giverOrcid.path)">
									<ul>
										<li>{{delegationDetails.giverName.value}}</li>
										<li>{{delegationDetails.giverOrcid.uri}}</li>										
									</ul>
								</a>
							</li>
						</ul>
					</div>
				</div>
				<div ng-hide="me || unfilteredLength > 0" ng-cloak>
					<div class="pull-right">
						<@orcid.checkFeatureStatus featureName='HTTPS_IDS'>
							<a href="${baseUri}/{{requestInfoForm.userOrcid}}" target="userOrcid">${baseUri}/{{requestInfoForm.userOrcid}}</a>
						</@orcid.checkFeatureStatus>
						<@orcid.checkFeatureStatus featureName='HTTPS_IDS' enabled=false>
                			${baseUriHttp}/{{requestInfoForm.userOrcid}}
           				</@orcid.checkFeatureStatus>
					</div>
				</div>
			</div>
		</#if>
	</div>
	<div class="clearfix pull-right">
		<span><a href="" onclick="logOffReload('show_login'); return false;">(<@orcid.msg'confirm-oauth-access.notYou'/>?)</a></span>
	</div>
</div>