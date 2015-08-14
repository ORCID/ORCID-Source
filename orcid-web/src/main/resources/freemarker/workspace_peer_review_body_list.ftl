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
<#include "includes/peer_review/del_peer_review_inc.ftl"/>

<#include "includes/peer_review/add_peer_review_inc.ftl"/>
<div ng-controller="PeerReviewCtrl">
	<div class="workspace-accordion accordion-peer-review" id="workspace-accordion">
		<div id="workspace-peer-review" class="workspace-accordion-item workspace-accordion-active">
			<div class="workspace-accordion-header"><a name='workspace-peer-review'></a>
				<div class="row">
					<div class="col-md-5 col-sm-2 col-xs-12">
						 <div class="workspace-title">											
						    <a ng-click="workspaceSrvc.togglePeerReview()" class="toggle-text">
						  		<i class="glyphicon-chevron-down glyphicon x075" ng-class="{'glyphicon-chevron-right':workspaceSrvc.displayPeerReview==false}"></i> <@orcid.msg 'workspace_peer_review_body_list.peerReview'/> (<span ng-bind="peerReviewSrvc.peerReviewCount()"></span>)
						   	</a>							    
						</div>
					</div>
					<div class="col-md-7 col-sm-10 col-xs-12 action-button-bar">
						<#include "includes/workspace/workspace_act_sort_menu.ftl"/>
						<#if !(isPublicProfile??)>
                			<ul class="workspace-bar-menu">
                			  	<li class="hidden-xs">
									<div class="menu-container">
										<ul class="toggle-menu">
									        <li ng-class="{'green-bg' : showBibtexImportWizard == true}"> 
									            <span class="glyphicon glyphicon-plus"></span> <span ng-click="addPeerReviewModal()"><@orcid.msg 'workspace_peer_review_body_list.addPeerReview'/></span>
									        </li>									        
									    </ul>									    
									</div>	
								</li>
								<li class="hidden-md hidden-sm visible-xs-inline">
							       	<a class="action-option manage-button" ng-click="addPeerReviewModal()">
							           	<span class="glyphicon glyphicon-plus"></span> <@orcid.msg 'workspace_peer_review_body_list.addManually'/>                                            
							        </a>
							    </li>
							</ul>
						</#if>
					</div>	
				</div>			
			</div>
			<div class="workspace-accordion-content" ng-show="workspaceSrvc.displayPeerReview == true" >
				<#include "includes/peer_review/peer_review_body_inc.ftl" />
			</div>
		</div>
	</div>
</div>