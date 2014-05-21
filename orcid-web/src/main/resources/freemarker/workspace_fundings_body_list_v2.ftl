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

<#include "includes/funding/del_funding_inc.ftl"/>

<#include "includes/funding/add_funding_inc.ftl"/>
<div ng-controller="FundingCtrl">
	<!-- Funding -->
	<div id="workspace-fundings" class="workspace-accordion-item workspace-accordion-active" >
		<div class="workspace-accordion-header">
			<div class="row">
				<div class="col-md-3 col-sm-3 col-xs-12">
					<a name='workspace-fundings' />
				    <a href="" ng-click="workspaceSrvc.toggleFunding($event)" class="toggle-text">
				  		<i class="glyphicon-chevron-down glyphicon x075" ng-class="{'glyphicon-chevron-right':workspaceSrvc.displayFunding==false}"></i>
				  		<@orcid.msg 'workspace.Funding'/> (<span ng-bind="fundingSrvc.fundings.length"></span>)
				   	</a>
				</div>
				<div class="col-md-9 col-sm-9 col-xs-12 action-button-bar" ng-show="workspaceSrvc.displayFunding">
					<!-- Sort -->
	       				<div class="sort-menu-container">			       					 
		       				<a class="action-option manage-button sort-menu" ng-click="">
								<span class="glyphicon glyphicon-sort"></span>							
								<@orcid.msg 'manual_orcid_record_contents.sort'/>
							</a>
							<ul class="sort-menu-options">
								<li><a href="" ng-click=""><@orcid.msg 'manual_orcid_record_contents.sort_title'/> <span class=""></span></a></li>
								<li><a href="" ng-click="" class="checked"><@orcid.msg 'manual_orcid_record_contents.sort_data'/> <span class="glyphicon glyphicon-ok pull-right"></span></a></li>
								<li><a href="" ng-click=""><@orcid.msg 'manual_orcid_record_contents.sort_type'/> <span class=""></span></a></li>
								<li><a href="" ng-click=""><@orcid.msg 'manual_orcid_record_contents.sort_source'/> <span class=""></span></a></li>
							</ul>
						</div>
						
                		<ul class="workspace-bar-menu">
                			<!-- Manage view -->		                			
	        				<li>
	        					<a href="" class="action-option manage-button" ng-click="">
									<span class="glyphicon glyphicon-cog"></span>
									<@orcid.msg 'manual_orcid_record_contents.manage_view'/>
								</a>	        				
	        				</li>
	        				<!-- Link Manually -->
	        				<li>
		        				<#if fundingImportWizards?size != 0>
									<a class="action-option manage-button" ng-click="showTemplateInModal('import-funding-modal')">
										<span class="glyphicon glyphicon-plus"></span>
										<@orcid.msg 'workspace.link_funding'/>
									</a>
								</#if>			
								<a href="" class="action-option manage-button" ng-click="addFundingModal()">
									<span class="glyphicon glyphicon-plus"></span>
									<@orcid.msg 'manual_orcid_record_contents.link_manually'/>				
								</a>
	        				</li>
	        				<!-- Search & Link -->
	        				<li>
		        				<a class="action-option manage-button" ng-click="showWorkImportWizard()">
									<span class="glyphicon glyphicon-cloud-upload"></span>							
									<@orcid.msg 'manual_orcid_record_contents.search_link'/>
								</a>	        				
	        				</li>
						</ul>
				
				
								
					
				</div>
			</div>			
		</div>				
			<div ng-show="workspaceSrvc.displayFunding" class="workspace-accordion-content">							
				<#include "includes/funding/body_funding_inc_v2.ftl" />				
			</div>		
		</div>
	</div>		
</div>