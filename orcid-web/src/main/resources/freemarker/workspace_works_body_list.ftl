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
            	<div class="pull-right">             		
					<div class="relative">
						<ul class="privacyToggle">
							<li class="publicActive" ng-class="{publicInActive: work.visibility != 'PUBLIC'}"><a href="" title="PUBLIC" ng-click="setPrivacy($index, 'PUBLIC', $event)"></a></li>
							<li class="limitedActive" ng-class="{limitedInActive: work.visibility != 'LIMITED'}"><a href="" title="LIMITED" ng-click="setPrivacy($index, 'LIMITED', $event)"></a></li>
							<li class="privateActive" ng-class="{privateInActive: work.visibility != 'PRIVATE'}"><a href="" title="PRIVATE" ng-click="setPrivacy($index, 'PRIVATE', $event)"></a></li>
						</ul>					   				
					</div>
					<div class="popover-help-container" style="position: absolute; left: 100px; top: 5px;">
        				<a href="javascript:void(0);"><i class="icon-question-sign"></i></a>
            			<div class="popover bottom">
		        			<div class="arrow"></div>
		        			<div class="popover-content">
		        				<strong>${springMacroRequestContext.getMessage("privacyToggle.help.who_can_see")}</strong>
			        			<ul class="privacyHelp">
			        				<li class="public" style="color: #009900;">${springMacroRequestContext.getMessage("privacyToggle.help.everyone")}</li>
			        				<li class="limited"style="color: #ffb027;">${springMacroRequestContext.getMessage("privacyToggle.help.trusted_parties")}</li>
			        				<li class="private" style="color: #990000;">${springMacroRequestContext.getMessage("privacyToggle.help.only_me")}</li>
			        			</ul>
			        			<a href="http://support.orcid.org/knowledgebase/articles/124518-orcid-privacy-settings" target="_blank">${springMacroRequestContext.getMessage("privacyToggle.help.more_information")}</a>
		        			</div>                
		    			</div>
    				</div>		             		             		    				
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