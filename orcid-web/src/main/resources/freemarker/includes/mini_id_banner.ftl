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
<#if RequestParameters['delegates']??>
<div class="id-banner <#if inDelegationMode>delegation-mode</#if>">
	<div class="full-name">
	    <#if (profile.orcidBio.personalDetails.creditName.content)??>
	        ${(profile.orcidBio.personalDetails.creditName.content)!}
	    <#else>
	        ${(profile.orcidBio.personalDetails.givenNames.content)!} ${(profile.orcidBio.personalDetails.familyName.content)!}
	    </#if>                
	</div>
	<div class="oid">
	   <div ng-controller="SwitchUserCtrl" class="dropdown id-banner-container" ng-show="unfilteredLength" ng-cloak>
	       <a ng-click="openMenu($event)" class="id-banner-switch">
	           <div class="orcid-id-container">
	    	        ${baseUriHttp}/${(profile.orcidIdentifier.path)!}
	    	        <span class="glyphicon glyphicon-chevron-down"></span>
		       </div>
	       </a>
	       <ul class="dropdown-menu id-banner-dropdown" ng-show="isDroppedDown" ng-cloak>
	       	   <li>
				   <input id="delegators-search" type="text" ng-model="searchTerm" ng-change="search()" placeholder="<@orcid.msg 'manage_delegators.search.placeholder'/>"></input>
	           </li>
	           <li ng-show="me && !searchTerm">
	               <a  ng-click="switchUser(me.delegateSummary.orcidIdentifier.path)">
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
</div>
</#if>