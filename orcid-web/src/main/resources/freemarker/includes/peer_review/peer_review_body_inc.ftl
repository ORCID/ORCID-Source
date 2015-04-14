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
<ul ng-hide="!peerReviewSrvc.groups.length" class="workspace-peer-review workspace-body-list bottom-margin-medium" ng-cloak>	
	

	
</ul>
<div ng-show="" class="text-center">
    <i class="glyphicon glyphicon-refresh spin x4 green" id="spinner"></i>
    <!--[if lt IE 8]>    
    	<img src="${staticCdn}/img/spin-big.gif" width="85" height ="85"/>
    <![endif]-->
</div>

<div ng-show="peerReviewSrvc.loading == false && peerReviewSrvc.groups.length == 0" class="" ng-cloak>
<!--  <div ng-show="worksSrvc.loading == false && worksSrvc.groups.length == 0" class="" ng-cloak> -->
    <strong><#if (publicProfile)?? && publicProfile == true>${springMacroRequestContext.getMessage("workspace_peer_review_body_list.Nopublicationsaddedyet")}<#else>${springMacroRequestContext.getMessage("workspace_peer_review_body_list.havenotaddedanypeerreviews")} <a ng-click="addPeerReviewModal()">${springMacroRequestContext.getMessage("workspace_peer_review_body_list.addsomenow")}</a></#if></strong>
</div>