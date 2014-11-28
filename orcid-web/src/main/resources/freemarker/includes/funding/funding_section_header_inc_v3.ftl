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
<div class="workspace-accordion-header">
    <div class="row">
        
        <div class="col-md-3 col-sm-2 col-xs-12">
            <a href="" ng-click="workspaceSrvc.toggleFunding($event)" class="toggle-text">
                  <i class="glyphicon-chevron-down glyphicon x075" ng-class="{'glyphicon-chevron-right':workspaceSrvc.displayFunding==false}"></i>
                  <@orcid.msg 'workspace.Funding'/> (<span ng-bind="fundingSrvc.groups.length"></span>)
               </a>
        </div>
        
        
        <div class="col-md-9 col-sm-10 col-xs-12 action-button-bar" ng-show="workspaceSrvc.displayFunding">
        		<!-- Sort -->
            	<#include "../workspace/workspace_act_sort_menu.ftl"/>
                <#if !(isPublicProfile??)>
                    <ul class="workspace-bar-menu">
                        <!-- Link Manually -->
                        <li>
                        	<div class="menu-container">
		                    	<ul class="toggle-menu">
		                    		<li ng-class="{'green-bg' : showBibtexImportWizard == true}">       
				                    	<span class="glyphicon glyphicon-plus"></span>
					                    Add funding    
					                    <ul class="menu-options funding">	                    	
						                    <!-- Add Manually -->
						                    <li>
					                            <a class="action-option manage-button" ng-click="addFundingModal()">
					                                <span class="glyphicon glyphicon-plus"></span>
					                                <@orcid.msg 'manual_orcid_record_contents.link_manually'/>
					                            </a>
					                        </li>
				                    	</ul>
			                    	</li>
		                    	</ul>
		                    </div>
                        </li>
                        <!-- Search & Link -->
                        <li>
                            <#if fundingImportWizards?size != 0>
                                <a class="action-option manage-button" ng-click="showTemplateInModal('import-funding-modal')">
                                    <span class="glyphicon glyphicon-cloud-upload"></span>
                                    <@orcid.msg 'manual_orcid_record_contents.search_link'/>
                                </a>
                            </#if>
                        </li>
                    </ul>
                </#if>
        </div>
    </div>
</div>
