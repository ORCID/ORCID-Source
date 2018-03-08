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
<#include "includes/funding/del_funding_inc.ftl"/>

<#include "includes/funding/add_funding_inc.ftl"/>

<@orcid.checkFeatureStatus 'ANGULAR2_DEV'> 
<funding-ng2></funding-ng2>

</@orcid.checkFeatureStatus>
<@orcid.checkFeatureStatus featureName='ANGULAR1_LEGACY' enabled=false>   
<div ng-controller="FundingCtrl">
    <!-- Funding -->
    <div id="workspace-fundings" class="workspace-accordion-item workspace-accordion-active" >
        <#include "includes/funding/funding_section_header_inc_v3.ftl" />
        
        <div ng-if="fundingImportWizard" class="funding-import-wizard" ng-cloak>
        	<#if ((fundingImportWizards)??)>
				<div class="ie7fix-inner">
					<div class="row">	
						<div class="col-md-12 col-sm-12 col-xs-12">
			           		<h1 class="lightbox-title wizard-header"><@orcid.msg 'workspace.link_funding'/></h1>
			           		<span ng-click="showFundingImportWizard()" class="close-wizard"><@orcid.msg 'workspace.LinkResearchActivities.hide_link_fundings'/></span>
						</div>
					</div>
					<div class="row">
						<div class="col-md-12 col-sm-12 col-xs-12">
			    	    	<div class="justify">
								<p><@orcid.msg 'workspace.LinkResearchActivities.description'/></p>
							</div>            	    	           	
		    		    	<#list fundingImportWizards?sort_by("name") as thirdPartyDetails>
			        	       	<#assign redirect = (thirdPartyDetails.redirectUri) >
		            	   		<#assign predefScopes = (thirdPartyDetails.scopes) >
		                   		<strong><a ng-click="openImportWizardUrl('<@orcid.rootPath '/oauth/authorize?client_id=${thirdPartyDetails.id}&response_type=code&scope=${predefScopes}&redirect_uri=${redirect}'/>')">${thirdPartyDetails.name}</a></strong><br />
		                 		<div class="justify">
									<p class="wizard-description" ng-class="{'ellipsis-on' : wizardDescExpanded[${thirdPartyDetails.id}] == false || wizardDescExpanded[${thirdPartyDetails.id}] == null}">
										${(thirdPartyDetails.description)!}
									<a ng-click="toggleWizardDesc(${thirdPartyDetails.id})" ng-if="wizardDescExpanded[${thirdPartyDetails.id}] == true"><span class="glyphicon glyphicon-chevron-down wizard-chevron"></span></a>
												</p>												
												<a ng-click="toggleWizardDesc(${thirdPartyDetails.id})" ng-if="wizardDescExpanded[${thirdPartyDetails.id}] == false || wizardDescExpanded[${thirdPartyDetails.id}] == null" class="toggle-wizard-desc"><span class="glyphicon glyphicon-chevron-right wizard-chevron"></span></a>
								</div>
		                   		<#if (thirdPartyDetails_has_next)>
			                      	<hr/>
								</#if>
		                		</#list>
						</div>
					</div>	
				</div>
			</#if>
        </div>
        <div ng-if="workspaceSrvc.displayFunding" class="workspace-accordion-content">
            <#include "includes/funding/body_funding_inc_v3.ftl" />
        </div>
    </div>
</div>
</@orcid.checkFeatureStatus>