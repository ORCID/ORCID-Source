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

	<script type="text/ng-template" id="delete-work-modal">
		<div style="padding: 20px;" class="colorbox-modal">
			<h3 style="margin-bottom: 0px;">${springMacroRequestContext.getMessage("manage.deleteWork.pleaseConfirm")}</h3>
			{{fixedTitle}}<br />
			<br />
	    	<div class="btn btn-danger" ng-click="deleteByIndex()">
	    		${springMacroRequestContext.getMessage("manage.deleteWork.delete")}
	    	</div>
	    	<a href="" ng-click="closeModal()">${springMacroRequestContext.getMessage("manage.deleteWork.cancel")}</a>
	    <div>; 
	</script>
	
	<div ng-controller="WorkCtrl">
	<#if RequestParameters['showWorkCards']??> 
	<div ng-repeat='work in works' style="background-color: #ffffff; border: 1px solid #ddd; -webkit-border-radius: 16px;
	    -moz-border-radius: 8px; border-radius: 8px; margin-bottom: 20px;">
		<div style=" padding: 5px; height: 30px;">
				<div class="pull-right" style="width: 130px;">
				<@orcid.privacyToggle "work.visibility" "setPrivacy($index, 'PUBLIC', $event)" 
		                    	  "setPrivacy($index, 'LIMITED', $event)" "setPrivacy($index, 'PRIVATE', $event)" />
				</div>
				<div style="position: relative; left: 522px; top: 4px; width: 15px;"><a href ng-click="deleteWork($index)" class="icon-trash grey"></a></div>
		</div>
		<table style="border-top: solid 1px #dddddd; width: 100%;">
			<tr style="vertical-align: middle;">
				<td style=" padding: 5px;"><span class="label"><strong>Title<strong></span></td>
				<td style=" padding: 5px;"><strong>{{work.workTitle.title.content}}</strong></td>
			</tr>
			<tr ng-show="work.workTitle.subtitle.content">
				<td style=" padding: 5px;"><span class="label"><strong>Subtitle</strong></span></td>
				<td style=" padding: 5px;" ng-bind-html-unsafe="work.workTitle.subtitle.content"></td>
			</tr>
			<tr ng-show="work.url.value">
				<td style=" padding: 5px;" ><span class="label"><strong>Url</strong></span></td>
				<td style=" padding: 5px;"><a href="{work.url.value}" target="_blank" ng-bind-html-unsafe="work.url.value"></td>
			</tr>
			<tr ng-repeat="ie in work.workExternalIdentifiers.workExternalIdentifier">
				<td style=" padding: 5px;"><span class="label"><strong>External Id</strong></span></td>
				<td style=" padding: 5px;"><strong>ID:</strong> <span ng-bind-html-unsafe="ie.workExternalIdentifierId.content"></span> 
					 <strong>TYPE:</strong> <span ng-bind="ie.workExternalIdentifierType"></span> 
				</td>
			</tr>
			<tr>
			   <td colspan="2" style="text-align: center;  padding: 5px;"><a href="#"><i class="icon-caret-down"></i> show all details <i class="icon-caret-down"></i> </a></td>
			</tr>
			
		</table>
	</div>
	</#if>

	 
	<ul ng-hide="!works.length" class="workspace-publications workspace-body-list bottom-margin-medium" ng-cloak>        
            <li class="bottom-margin-small" ng-repeat='work in works'>            	
                <div class="pull-right" style="right: 145px; top: 20px; width: 15px;"><a href ng-click="deleteWork($index)" class="icon-trash grey"></a></div>
				<div style="width: 530px;">
                <h3 class="work-title">
                	<strong ng-bind="work.workTitle.title.content"></strong><span class="work-subtitle" ng-show="work.workTitle.subtitle.content" ng-bind-html-unsafe="':&nbsp;'.concat(work.workTitle.subtitle.content)"></span>
                	<span ng-show="work.publicationDate.month.value">{{work.publicationDate.month.value}}-</span><span ng-show="work.publicationDate.year.value">{{work.publicationDate.year.value}}</span>
                </h3>
                </div>
                <div class="pull-right" style="width: 130px;">
				<@orcid.privacyToggle "work.visibility" "setPrivacy($index, 'PUBLIC', $event)" 
		                    	  "setPrivacy($index, 'LIMITED', $event)" "setPrivacy($index, 'PRIVATE', $event)" />
				</div>
				<div  style="width: 680px;" class="work-metadata">
	                <span ng-repeat='ie in work.workExternalIdentifiers.workExternalIdentifier'>
	                	<span ng-show="ie.workExternalIdentifierType=='DOI' && ie.workExternalIdentifierId.content">
	                		<span>${springMacroRequestContext.getMessage("workspace_works_body_list.DOI")} <a href="http://dx.doi.org/{{ie.workExternalIdentifierId.content.replace('http://dx.doi.org/','')}}">{{ie.workExternalIdentifierId.content}}</a></span>	          
	                	</span>                	             
	                </span>
	                <span ng-show="work.url.value" style=" display: inline-block;">URL: <a href="{{work.url.value}}">{{work.url.value}}</a></span>
	            </div>
                
                <div ng-show="work.shortDescription" ng-bind-html-unsafe="work.shortDescription" style="width: 680px;"></div>
                <div ng-show="work.citationForDisplay" class="citation {{work.workCitation.workCitationType.toLowerCase()}}" ng-bind-html-unsafe="work.citationForDisplay" style="width: 680px;"></div>
            </li>           
	</ul>
	
    <div ng-hide="works.length" class="alert alert-info">
        <strong><#if (publicProfile)?? && publicProfile == true>${springMacroRequestContext.getMessage("workspace_works_body_list.Nopublicationsaddedyet")}<#else>${springMacroRequestContext.getMessage("workspace_works_body_list.havenotaddedanyworks")} <a href="<@spring.url '/works-update'/>" class="update">${springMacroRequestContext.getMessage("workspace_works_body_list.addsomenow")}</a></#if></strong>
    </div>
    
    </div>
