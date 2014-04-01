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
<div class="id-banner <#if inDelegationMode>delegation-mode</#if>">
    <#if inDelegationMode><span class="delegation-mode-warning">${springMacroRequestContext.getMessage("delegate.managing_record")}</span></#if>
	<h2 class="full-name">
	    <#if (profile.orcidBio.personalDetails.creditName.content)??>
	        ${(profile.orcidBio.personalDetails.creditName.content)!}
	    <#else>
	        ${(profile.orcidBio.personalDetails.givenNames.content)!} ${(profile.orcidBio.personalDetails.familyName.content)!}
	    </#if>                
	</h2>
	<div class="oid">
		<p class="orcid-id-container">
	    	<span class="mini-orcid-icon"></span>
	    	<a href="${baseUriHttp}/${(profile.orcidIdentifier.path)!}" id="orcid-id" class="orcid-id" title="Click for public view of ORCID iD">${baseUriHttp}/${(profile.orcidIdentifier.path)!}</a>
		</p>
		<#if RequestParameters['delegates']??>
	   <div ng-controller="SwitchUserCtrl" class="dropdown id-banner-container" ng-show="unfilteredLength" ng-cloak>
	       <a ng-click="openMenu($event)" class="id-banner-switch"><@orcid.msg 'public-layout.manage_proxy_account'/><span class="glyphicon glyphicon-chevron-right"></span></a>
	       <ul class="dropdown-menu id-banner-dropdown" ng-show="isDroppedDown" ng-cloak>
	       	   <li>
				   <input id="delegators-search" type="text" ng-model="searchTerm" ng-change="search()" placeholder="ORCID or names"></input>
	           </li>
	           <li ng-show="me && !searchTerm">
	               <a href="<@spring.url '/switch-user?j_username='/>{{me.delegateSummary.orcidIdentifier.path}}">
					   <ul>
						   <li>{{me.delegateSummary.creditName.content}} (me)</li>
						   <li>{{me.delegateSummary.orcidIdentifier.uri}}</li>
					   </ul>
	               </a>
	           </li>
	           <li ng-repeat="delegationDetails in delegators.delegationDetails | orderBy:'delegateSummary.creditName.content' | limitTo:10">
	               <a href="<@spring.url '/switch-user?j_username='/>{{delegationDetails.delegateSummary.orcidIdentifier.path}}">
	               	   <ul>
	               	   	 <li>{{delegationDetails.delegateSummary.creditName.content}}</li>
	               	   	 <li>{{delegationDetails.delegateSummary.orcidIdentifier.uri}}</li>
	               	   </ul>
	               </a>
	           </li>
	           <li><a href="<@spring.url '/delegators?delegates'/>">More...</a></li>
	       </ul>
	    </div>
	</#if>
	</div>	
</div>