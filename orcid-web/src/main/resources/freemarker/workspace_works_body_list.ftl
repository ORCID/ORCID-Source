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
	<ul ng-controller="WorkCtrl" ng-hide="!worksPojo.works.length" class="workspace-publications workspace-body-list" ng-cloak>        
            <li ng-repeat='work in worksPojo.works'>            	
            	<div class="pull-right" ng-switch on="work.visibility">             		
             		<span class="label label-success privacy-label" ng-switch-when="PUBLIC">${springMacroRequestContext.getMessage("manage.lipublic")}</span>
             		<span class="label label-warning privacy-label" ng-switch-when="LIMITED">${springMacroRequestContext.getMessage("manage.lilimited")}</span>
             		<span class="label label-important privacy-label" ng-switch-when="PRIVATE">${springMacroRequestContext.getMessage("manage.liprivate")}</span>
             		<span class="label label-important privacy-label" ng-switch-when="PROTECTED">${springMacroRequestContext.getMessage("manage.liprivate")}</span>
             		<span class="label label-success privacy-label" ng-switch-default>${springMacroRequestContext.getMessage("manage.lipublic")}</span>             		    				
             	</div>             
                <h3 class="work-title">
                	<b ng-bind-html-unsafe="work.workTitle.title.content"></b>&nbsp;
                	<span class="work-subtitle" ng-hide="!work.workTitle.subtitle.content" ng-bind-html-unsafe="{: {work.workTitle.subtitle.content}}"></span>
                	<span ng-hide="!work.publicationDate.month.value">{{work.publicationDate.month.value}}-</span><span ng-hide="!work.publicationDate.year.value">{{work.publicationDate.year.value}}</span>
                </h3>
                <div ng-repeat='ie in work.workExternalIdentifiers.workExternalIdentifier'>
                	<div ng-show="ie.workExternalIdentifierType=='DOI' && ie.workExternalIdentifierId.content">
                		<span class="work-metadata">${springMacroRequestContext.getMessage("workspace_works_body_list.DOI")} <a href="http://dx.doi.org/{{ie.workExternalIdentifierId.content}}">{{ie.workExternalIdentifierId.content}}</a></span>
                		<img onclick="javascript:window.open(&quot;http://dx.doi.org/{{ie.workExternalIdentifierId.content}}&quot;)" style="cursor:pointer;" src="${staticCdn}/img/view_full_text.gif"><input type="hidden" value="null" name="artifacts[0].destApp"><input type="hidden" value="JOUR" name="artifacts[0].type"><input type="hidden" value="W" name="artifacts[0].uploadedBy">
                	</div>                	             
                </div>
                <div ng-show="work.url.value"><a href="{{work.url.value}}">{{work.url.value}}</a></div>
                <div ng-show="work.shortDescription" ng-bind-html-unsafe="work.shortDescription"></div>
                <div ng-show="work.workCitation" class="citation {{work.workCitation.workCitationType}}" ng-bind-html-unsafe="work.workCitation.citation"></div>
                <span class="pull-right-level-two"><a href ng-click="deleteWork($index)" class="icon-trash grey"></a></span>
            </li>           
	</ul>
	
    <div ng-controller="WorkCtrl" ng-hide="worksPojo.works.length" class="alert alert-info">
        <strong><#if (publicProfile)?? && publicProfile == true>${springMacroRequestContext.getMessage("workspace_works_body_list.Nopublicationsaddedyet")}<#else>${springMacroRequestContext.getMessage("workspace_works_body_list.havenotaddedanyworks")} <a href="<@spring.url '/works-update'/>" class="update">${springMacroRequestContext.getMessage("workspace_works_body_list.addsomenow")}</a></#if></strong>
    </div>