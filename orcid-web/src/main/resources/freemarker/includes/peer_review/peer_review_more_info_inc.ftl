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

<div class="more-info" ng-if="group.activePutCode == peerReview.putCode.value && showDetails[group.groupId] == true">
	<div class="content">
		<!--  <span class="dotted-bar"></span> -->
		<div class="row">
			<div class="col-md-12">
				<div class="group-summary-details">
					<span class="italic" ng-if="group.groupType" ng-bind="group.groupType"></span><span ng-if="group.groupDescription">, </span><span ng-if="group.groupDescription" ng-bind="group.groupDescription"></span>
				</div>				
			</div>
			<div class="peer-review-list-container">
				<ul class="sources-edit-list">
					<li class="source-active">
						<!-- Header -->
						<div class="sources-header">
                        	<div class="row">
	                        	<div class="col-md-3 col-sm-3 col-xs-3">
	                        		Review date
	                        	</div>
	                        	<div class="col-md-2 col-sm-2 col-xs-2">
	                        		Type
	                        	</div>
	                        	<div class="col-md-3 col-sm-3 col-xs-3">
	                        		Role
	                        	</div>
	                        	<div class="col-md-4 col-sm-4 col-xs-4">
	                        		<span class="pull-right">Actions</span>
	                        	</div>
                        	</div>
                        </div>
                        <!-- End of Header -->
					</li>
					
					<li ng-repeat="peerReview in group.activities">
						<!-- Active row -->
						<div class="row source-line-peer-review">
							<div class="col-md-3 col-sm-3 col-xs-3">
								<span ng-if="peerReview.completionDate.year" ng-bind="peerReview.completionDate.year"></span><span ng-if="peerReview.completionDate.month">-</span><span ng-if="peerReview.completionDate.month" ng-bind="peerReview.completionDate.month"></span>
							</div>
							<div class="col-md-2 col-sm-2 col-xs-2" ng-bind="peerReview.type.value"></div>
                        	<div class="col-md-3 col-sm-3 col-xs-3" ng-bind="peerReview.role.value"></div>
                        	<div class="col-md-4 col-sm-4 col-xs-4">                        		
                        		<span class="pull-right"> 
                        			<a ng-click="showMoreDetails(peerReview.putCode.value); group.activePutCode = peerReview.putCode.value;" ng-hide="showPeerReviewDetails[peerReview.putCode.value]" ng-show="group.activePutCode != peerReview.putCode.value || showPeerReviewDetails[peerReview.putCode.value] == null">
                        				<span class="glyphicons expand"></span>
                        				show details
                        			</a> 
                        			<a ng-click="hideMoreDetails(peerReview.putCode.value);" ng-show="showPeerReviewDetails[peerReview.putCode.value] == true" ng-hide="group.activePutCode != peerReview.putCode.value || showPeerReviewDetails[peerReview.putCode.value] == null">
                        				<span class="glyphicons collapse_top"></span>
                        				hide details
                        			</a> | 
                        			<a href="{{peerReview.url.value}}" ng-if="peerReview.url != null" target="_blank"><span>view</span></a><span ng-if="peerReview.url == null">view</span>
                        			 <#if !(isPublicProfile??)>
                        				<a ng-click="deletePeerReviewConfirm(group.getActive().putCode.value, false)"> | <span class="glyphicon glyphicon-trash"></span></a>
                        			 </#if>
                        		</span>
                        	</div>
						</div>						
						 
						<!-- Details row -->
						<div class="row" ng-if="showPeerReviewDetails[peerReview.putCode.value] == true && group.activePutCode == peerReview.putCode.value;">
							<div class="col-md-12 info-detail" ng-if="peerReview.externalIdentifiers[0].workExternalIdentifierId.value != null" ng-cloak>
								<span class="workspace-title">Review identifier(s):&nbsp;</span> 
								<span ng-repeat='ie in peerReview.externalIdentifiers'><span
					             	ng-bind-html='ie | peerReviewExternalIdentifierHtml:$first:$last:peerReview.externalIdentifiers.length:showDetails[group.groupId]:false'></span>					        
					            </span>					            
						    </div>
						    <div class="col-md-12 info-detail" ng-if="peerReview.orgName.value != null" ng-cloak>
								<span class="workspace-title">Convening organization:&nbsp;</span><span ng-bind="peerReview.orgName.value"></span>(<span ng-bind="peerReview.city.value"></span><span ng-if="peerReview.city.value">,</span> <span ng-bind="peerReview.countryForDisplay"></span>)
							</div>
							<div class="col-md-12 info-detail">
								<span ng-if="peerReview.subjectName.value != null">
									<span class="workspace-title">Review subject:&nbsp;</span>									
									<span ng-bind="peerReview.subjectName.value"></span>
								</span>
								<span ng-if="peerReview.subjectName.value != null" ng-bind="peerReview.subjectType.value"></span>
								<span ng-if="peerReview.subjectContainerName != null">
									{{peerReview.subjectContainerName.value}}.
								</span><span ng-if="peerReview.subjectExternalIdentifier.workExternalIdentifierId.value != null" ng-cloak>
									<span ng-repeat='ie in peerReview'><span
						             	ng-bind-html='ie | peerReviewExternalIdentifierHtml:$first:$last:peerReview.subjectExternalIdentifier.length:showDetails[group.groupId]:true'></span>					        
						            </span>					            
							    </span>							    
							</div>							
						</div>
					</li>
				</ul>
			</div>
		</div>
	</div>
</div>