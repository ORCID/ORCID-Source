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
<div>
    <#if inDelegationMode>managing record</#if>
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
		<p>
	</div>
</div>
<#if RequestParameters['delegates']??>
   <div ng-controller="SwitchUserCtrl" class="dropdown" ng-show="unfilteredLength" ng-cloak>
       <a ng-click="openMenu($event)" ><@orcid.msg 'public-layout.manage_proxy_account'/></a>
       <ul class="dropdown-menu" ng-show="isDroppedDown" ng-cloak>
           <input id="delegators-search" type="text" ng-model="searchTerm" ng-change="search()" placeholder="ORCID or names"></input>
           <li ng-show="me && !searchTerm">
               <a href="<@spring.url '/switch-user?j_username='/>{{me.delegateSummary.orcidIdentifier.path}}">
                   <div>{{me.delegateSummary.creditName.content}} (me)</div>
                   <div>{{me.delegateSummary.orcidIdentifier.uri}}</div>
               </a>
           </li>
           <li ng-repeat="delegationDetails in delegators.delegationDetails | orderBy:'delegateSummary.creditName.content' | limitTo:10">
               <a href="<@spring.url '/switch-user?j_username='/>{{delegationDetails.delegateSummary.orcidIdentifier.path}}">
                   <div>{{delegationDetails.delegateSummary.creditName.content}}</div>
                   <div>{{delegationDetails.delegateSummary.orcidIdentifier.uri}}</div>
               </a>
           </li>
           <li><a href="<@spring.url '/delegators?delegates'/>">More...</a></li>
       </ul>
    </div>
</#if>