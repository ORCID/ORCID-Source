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
				<div><span>{{peerReview.orgName.value}}</span>, <span>{{peerReview.city.value}}</span>, {{peerReview.countryForDisplay}}</span> <span>({{peerReview.type.value}})</span></div>
			</div>
			<div class="peer-review-list-container">
				<ul class="sources-edit-list">
					<li class="source-active">
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
					</li>
					<li ng-repeat="peerReview in group.activities">
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
                        			<a href=""><span>show details</span></a> <a href=""><span>view</span></a> <a href=""><span class="glyphicon glyphicon-trash"></span></a>
                        		</span>
                        	</div>
						</div>
					</li>
				</ul>
			
			</div>
		</div>
		
		
		
		
		<!-- Old code below 
		
		<div class="row">
			
			<div class="col-md-6" ng-show="peerReview.role.value" ng-cloak>
				<div class="bottomBuffer">
					<strong><@orcid.msg 'workspace_peer_review_body_list.role'/></strong>
					<div>{{peerReview.role.value.toLowerCase()}}</div>				
				</div>
			</div>
			
			<div class="col-md-6" ng-show="peerReview.type.value" ng-cloak>
				<div class="bottomBuffer">
					<strong><@orcid.msg 'workspace_peer_review_body_list.type'/></strong>
					<div>{{peerReview.type.value.toLowerCase()}}</div>				
				</div>
			</div>
			
			<div class="col-md-6" ng-show="peerReview.orgName.value" ng-cloak>
				<div class="bottomBuffer">
					<strong><@orcid.msg 'workspace_peer_review_body_list.institution'/></strong>
					<div>{{peerReview.orgName.value}}</div>				
				</div>
			</div>
			
			<div class="col-md-6" ng-show="peerReview.city.value" ng-cloak>
				<div class="bottomBuffer">
					<strong><@orcid.msg 'workspace_peer_review_body_list.city'/></strong>
					<div>{{peerReview.city.value}}</div>				
				</div>
			</div>
			
			<div class="col-md-6" ng-show="peerReview.region.value" ng-cloak>
				<div class="bottomBuffer">
					<strong><@orcid.msg 'workspace_peer_review_body_list.region'/></strong>
					<div>{{peerReview.region.value}}</div>				
				</div>
			</div>
			
			<div class="col-md-6" ng-show="peerReview.country.value" ng-cloak>
				<div class="bottomBuffer">
					<strong><@orcid.msg 'workspace_peer_review_body_list.country'/></strong>
					<div>{{peerReview.countryForDisplay}}</div>				
				</div>
			</div>
			
			<div class="col-md-6" ng-show="peerReview.subjectForm.workExternalIdentifiers[0].workExternalIdentifierId.value != null" ng-cloak>
				<div class="bottomBuffer">
					<strong><@orcid.msg 'workspace_peer_review_body_list.subject.externalIdentifiers'/></strong><br/>
					<span ng-repeat='ie in peerReview.subjectForm.workExternalIdentifiers'><span
		             	ng-bind-html='ie | workExternalIdentifierHtml:$first:$last:peerReview.subjectForm.workExternalIdentifiers.length'></span>
		            </span>
		        </div>
		   </div>
		   <
			<div class="col-md-6" ng-show="peerReview.subjectForm.url.value" ng-cloak>
				<div class="bottomBuffer">
					<strong><@orcid.msg 'workspace_peer_review_body_list.subject.url'/></strong><br/>
					<div><a href="{{peerReview.subjectForm.url.value}}" target="_blank">{{peerReview.subjectForm.url.value}}</a></div>			
		        </div>
		   </div>
		   
		   -->
		</div>		
	</div>
</div>