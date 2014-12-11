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
        <div class="col-md-3 col-sm-3 col-xs-12">
            <a name='workspace-employments' />
            <a href="" ng-click="workspaceSrvc.toggleEmployment($event)" ng-click="workspaceSrvc.toggleEmployment($event)" class="toggle-text">
                <i class="glyphicon-chevron-down glyphicon x075" ng-class="{'glyphicon-chevron-right':workspaceSrvc.displayEmployment==false}"></i>
                <@orcid.msg 'org.orcid.jaxb.model.message.AffiliationType.employment'/> (<span ng-bind="affiliationsSrvc.employments.length"></span>)
            </a>                    
        </div>
        <div class="col-md-9 col-sm-9 col-xs-12 action-button-bar" ng-show="workspaceSrvc.displayEmployment">
            <#include "../workspace/workspace_act_sort_menu.ftl"/>  
            <#if !(isPublicProfile??)>
                <ul class="workspace-bar-menu">                         
                    <!-- Link Manually -->
                    <li class="hidden-xs">                  
                    	<div class="menu-container">
	                    	<ul class="toggle-menu">
	                    		<li ng-class="{'green-bg' : showBibtexImportWizard == true}">       
			                    	<span class="glyphicon glyphicon-plus"></span>
				                    <@orcid.msgCapFirst 'manual_affiliation_form_contents.add_employment' />    
				                    <ul class="menu-options employment">	                    	
					                    <!-- Add Manually -->
					                    <li>                            
					                        <a href="" class="action-option manage-button two-options" ng-click="addAffiliationModal('employment')">
					                            <span class="glyphicon glyphicon-plus"></span>
					                            <@orcid.msg 'manual_orcid_record_contents.link_manually'/>
					                        </a>                
                   						 </li>
					                </ul>
					             </li>
					      </ul>
					    </div>         
                    </li>
                    <!-- Mobile workaound -->
                    <li class="hidden-md hidden-sm visible-xs-inline">                            
                        <a href="" class="action-option manage-button two-options" ng-click="addAffiliationModal('employment')">
                            <span class="glyphicon glyphicon-plus"></span>
                            <@orcid.msg 'manual_orcid_record_contents.link_manually'/>
                        </a>                
              		</li>
                    
                    
                    
                    
                    
                </ul>
            </#if>
        </div>
    </div>
</div>
