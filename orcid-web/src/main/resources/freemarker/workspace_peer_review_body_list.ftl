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
	<div class="workspace-accordion" id="workspace-accordion">
		<div id="workspace-peer-review" class="workspace-accordion-item workspace-accordion-active">
			<div class="workspace-accordion-header"><a name='workspace-peer-review'></a>
				<div class="row">
					<div class="col-md-5 col-sm-2 col-xs-12">
						 <div class="workspace-title">											
						    <a href="" ng-click="workspaceSrvc.togglePeerReview()" class="toggle-text">
						  		<i class="glyphicon-chevron-down glyphicon x075" ng-class="{'glyphicon-chevron-right':workspaceSrvc.displayPeerReview==false}"></i> Peer Review
						  			(<span class="ng-binding">0</span>)
						  		</a>						  		
						   	</a>							    
						</div>
					</div>
					<div class="col-md-7 col-sm-10 col-xs-12 action-button-bar">
						<div class="menu-container">
							<ul class="toggle-menu">
						        <li ng-class="{'green-bg' : showBibtexImportWizard == true}"> 
						            <span class="glyphicon glyphicon-plus"></span> Add Peer Reviews                                    
						            <ul class="menu-options works">
						                <!-- Search & Link -->
						                <li>
						                    <a class="action-option manage-button" ng-click="showWorkImportWizard()">
						                        <span class="glyphicon glyphicon-cloud-upload"></span> Search &amp; link
						                    </a>
						                </li>                
						                <!-- Add Manually -->
						                <li>
						                    <a class="action-option manage-button" ng-click="addPeerReviewModal()">
						                        <span class="glyphicon glyphicon-plus"></span> Add manually                                            
						                    </a>
						                </li>                                                                                
						            </ul>
						        </li>
						    </ul>
						</div>	
					</div>	
				</div>			
			</div>
			<div class="workspace-accordion-content" ng-show="1 == 1" >
			<!-- <div class="workspace-accordion-content" ng-show="workspaceSrvc.displayPeerReview" >  -->
				<#include "includes/peer_review/peer_review_body_inc.ftl" />
			</div>
			
		
		</div>
	</div>
</div>