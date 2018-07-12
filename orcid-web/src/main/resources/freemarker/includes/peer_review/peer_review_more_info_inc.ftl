<div class="more-info" ng-if="showDetails[group.id] == true">
    <div class="content">       
        <div class="row">
            <div class="col-md-12">
                <div class="group-summary-details">
                    <span class="italic" ng-if="group.type" ng-bind="group.type"></span><span ng-if="group.description">, </span><span ng-if="group.description" ng-bind="group.description"></span>
                </div>              
            </div>
            <div class="peer-review-list-container">
                <ul class="sources-edit-list">
                    <li class="source-active">
                        <!-- Header -->
                        <div class="sources-header">
                            <div class="row">
                                <div class="col-md-3 col-sm-3 col-xs-3">
                                    <@orcid.msg 'peer_review.review_date' />
                                </div>
                                <div class="col-md-2 col-sm-2 col-xs-2">
                                    <@orcid.msg 'peer_review.type' />
                                </div>
                                <div class="col-md-3 col-sm-3 col-xs-3">
                                    <@orcid.msg 'peer_review.role' />
                                </div>
                                <div class="col-md-4 col-sm-4 col-xs-4">
                                    <span class="pull-right"><@orcid.msg 'peer_review.actions' /></span>
                                </div>
                            </div>
                        </div>
                        <!-- End of Header -->
                    </li>
                    
                    <li ng-repeat="peerReview in group.peerReviews">
                        <!-- Active row -->
                        <div class="row source-line-peer-review">
                            <div class="col-md-3 col-sm-3 col-xs-3">
                                <span ng-if="peerReview.completionDate.year" ng-bind="peerReview.completionDate.year" ng-cloak></span><span ng-if="peerReview.completionDate.month">-</span><span ng-if="peerReview.completionDate.month" ng-bind="peerReview.completionDate.month"></span>
                            </div>
                            <div class="col-md-2 col-sm-2 col-xs-2" ng-bind="peerReview.type.value" ng-cloak></div>
                            <div class="col-md-3 col-sm-3 col-xs-3" ng-bind="peerReview.role.value" ng-cloak></div>
                            <div class="col-md-4 col-sm-4 col-xs-4">                                
                                <span class="pull-right"> 
                                    <a ng-click="showMoreDetails(peerReview.putCode.value)" ng-hide="showPeerReviewDetails[peerReview.putCode.value]" ng-show="showPeerReviewDetails[peerReview.putCode.value] == null">
                                        <span class="glyphicons expand"></span>
                                        <span class="hidden-xs"><@orcid.msg 'common.details.show_details_lc' /></span>
                                    </a> 
                                    <a ng-click="hideMoreDetails(peerReview.putCode.value);" ng-show="showPeerReviewDetails[peerReview.putCode.value]" ng-hide="showPeerReviewDetails[peerReview.putCode.value] == null">
                                        <span class="glyphicons collapse_top"></span>                                       
                                        <span class="hidden-xs"><@orcid.msg 'common.details.hide_details_lc' /></span>
                                    </a> | 
                                    <a href="{{peerReview.url.value}}" ng-if="peerReview.url != null" target="peer_review.view"><span><@orcid.msg 'peer_review.view' /></span></a><span ng-if="peerReview.url == null"><@orcid.msg 'peer_review.view' /></span>
                                     <#if !(isPublicProfile??)>
                                        <div ng-click="deletePeerReviewConfirm(peerReview.putCode.value, false)" class="peer-review-delete"> | <span class="glyphicon glyphicon-trash"></span>
                                            <div class="popover popover-tooltip top">
                                                <div class="arrow"></div>
                                                <div class="popover-content">
                                                    <span><@orcid.msg 'groups.common.delete_this_source'/></span>
                                                </div>                
                                            </div>
                                                                                    
                                        </div>
                                     </#if>
                                </span>
                            </div>
                        </div>                      
                         
                        <!-- Details row -->
                        <div class="row" ng-if="showPeerReviewDetails[peerReview.putCode.value] == true">
                            <div class="col-md-12 info-detail" ng-if="peerReview.externalIdentifiers[0].workExternalIdentifierId.value != null" ng-cloak>
                                <span class="workspace-title"><@orcid.msg 'peer_review.review_identifiers' />&nbsp;</span> 
                                <span ng-repeat='ie in peerReview.externalIdentifiers'><span
                                    ng-bind-html='ie | peerReviewExternalIdentifierHtml:$first:$last:peerReview.externalIdentifiers.length:showDetails[group.groupId]:false'></span>                            
                                </span>                             
                            </div>
                            <div class="col-md-12 info-detail" ng-if="peerReview.orgName.value != null" ng-cloak>
                                <span class="workspace-title"><@orcid.msg 'peer_review.convening_organization' />&nbsp;</span><span ng-bind="peerReview.orgName.value"></span>(<span ng-bind="peerReview.city.value"></span><span ng-if="peerReview.city.value">,</span> <span ng-bind="peerReview.countryForDisplay"></span>)
                            </div>
                            <div class="col-md-12 info-detail">
                                <span ng-if="peerReview.subjectName.value != null">
                                    <span class="workspace-title">Review subject:&nbsp;</span>                                  
                                    <span ng-bind="peerReview.subjectName.value"></span>
                                </span>
                                <span ng-if="peerReview.subjectName.value != null" ng-bind="peerReview.subjectType.value"></span>
                                <span ng-if="peerReview.subjectContainerName != null">
                                    {{peerReview.subjectContainerName.value}}.
                                </span>
                                <span ng-if="peerReview.subjectExternalIdentifier.workExternalIdentifierId.value != null" ng-cloak>
                                    <span ng-repeat='ie in peerReview'><span
                                        ng-bind-html='ie | peerReviewExternalIdentifierHtml:$first:$last:peerReview.subjectExternalIdentifier.length:showDetails[group.groupId]:true'></span>                           
                                    </span>                             
                                </span>                             
                            </div>   

                            <!-- Begin of source -->
                            <div class="col-md-12 sources-container-header">          
                                <div class="row">
                                    <div class="col-md-7 col-sm-7 col-xs-12">
                                        <@orcid.msg 'groups.common.source'/>: {{peerReview.source}}    
                                    </div>
                                    <div class="col-md-3 col-sm-3 col-xs-6">
                                        <@orcid.msg 'groups.common.created'/>: <span ng-bind="peerReview.createdDate | ajaxFormDateToISO8601"></span>
                                    </div>              
                                </div>
                            </div>
                            <!-- End of source -->  

                        </div>
                    </li>
                </ul>
            </div>
        </div>
    </div>
</div>