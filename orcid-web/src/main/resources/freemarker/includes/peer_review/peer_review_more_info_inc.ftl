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

<div class="more-info" ng-show="group.activePutCode == peerReview.putCode.value && showDetails[group.groupId] == true">
	<div class="content">
		<!--  <span class="dotted-bar"></span> -->
		<div class="row">
			<div class="col-md-12">
				<div><span class="italic"></span></div>
			</div>
			<div class="peer-review-list-container">
				<ul class="sources-edit-list">
					<li class="source-active">
						<!-- Header -->
						<div class="sources-header">
                        	<div class="row">
	                        	<div class="col-md-3">
	                        		Review date
	                        	</div>
	                        	<div class="col-md-2">
	                        		Type
	                        	</div>
	                        	<div class="col-md-3">
	                        		Role
	                        	</div>
	                        	<div class="col-md-4">
	                        		<span class="pull-right">Actions</span>
	                        	</div>
                        	</div>
                        </div>
                        <!-- End of Header -->
					</li>
					
					<li ng-repeat="peerReview in group.activities">
						<!-- Active row -->
						<div class="row source-line">
							<div class="col-md-3">
								<span ng-show="peerReview.completionDate.year">{{peerReview.completionDate.year}}</span><span ng-show="peerReview.completionDate.month">-{{peerReview.completionDate.month}}</span><span ng-show="peerReview.completionDate.year">
							</div>
							<div class="col-md-2">
	                        	{{peerReview.type.value}}
                        	</div>
                        	<div class="col-md-3">
                        		{{peerReview.role.value}}
                        	</div>
                        	<div class="col-md-4">                        		
                        		<span class="pull-right">
                        			<a ng-click="showMoreDetails(peerReview.putCode.value); group.activePutCode = peerReview.putCode.value;" ng-hide="showPeerReviewDetails[peerReview.putCode.value] == true" ng-show="group.activePutCode != peerReview.putCode.value;">show details</a> 
                        			<a ng-click="hideMoreDetails(peerReview.putCode.value);" ng-show="showPeerReviewDetails[peerReview.putCode.value] == true" ng-hide="group.activePutCode != peerReview.putCode.value || showPeerReviewDetails[peerReview.putCode.value] == null">hide details</a> | 
                        			<a href=""><span>view</span></a> <a href="" ng-click="peerReviewSrvc.deletePeerReview(peerReview.putCode.value)"> | <span class="glyphicon glyphicon-trash"></span></a>
                        		</span>
                        	</div>
						</div>
						<!--
						<div class="row">
							<div class="col-md-12">
								  {{peerReview.subjectExternalIdentifier}}
							</div>
						</div>
						 -->
						<!-- Details row -->
						<div class="row" ng-show="showPeerReviewDetails[peerReview.putCode.value] == true && group.activePutCode == peerReview.putCode.value;">
							<div class="col-md-12" ng-show="peerReview.subjectExternalIdentifier[0].workExternalIdentifierId.value != null" ng-cloak>
								<span class="workspace-title">Review Identifiers: </span><br/>
								<span ng-repeat='ie in peerReview.subjectExternalIdentifier'><span
					             	ng-bind-html='ie | peerReviewExternalIdentifierHtml:$first:$last:peerReview.externalIdentifiers.length:showDetails[group.groupId]'></span>
					             	<!-- Filter requires adjustments -->
					            </span> | <span ng-show="peerReview.url != null"><a href="{{peerReview.url.value}}">{{peerReview.url.value}}</a></span>
						    </div>
						    <div class="col-md-12" ng-show="peerReview.orgName.value != null" ng-cloak>
								<span class="workspace-title">Convening organization: </span><span>{{peerReview.orgName.value}}</span><span> ({{peerReview.city.value}}, {{peerReview.countryForDisplay}})</span>
							</div>
							<div class="col-md-12">
								<span class="workspace-title">Review subject:</span> <span>{{peerReview.subjectName.value}}</span> <span>({{peerReview.subjectType.value}})</span> <span ng-show="peerReview.subjectContainerName != null">{{peerReview.subjectContainerName.value}}</span>
							</div>
							
						</div>
					</li>
				</ul>
			</div>
		</div>
	</div>
</div>