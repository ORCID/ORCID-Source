<div class="more-info" ng-if="showDetails[group.groupId] == true">
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
                    
                    <li ng-repeat="peerReviewDuplicateGroup in group.peerReviewDuplicateGroups" class="bottom-margin-small workspace-border-box card">
                        <div class="work-list-container">
                            <ul class="sources-edit-list">
                                <li ng-if="editSources[peerReviewDuplicateGroup.id]" class="source-header" ng-class="{'source-active' : editSources[peerReviewDuplicateGroup.id] == true}" ng-model="group.works">
                                    <div class="sources-header">
                                        <div class="row">
                                            <div class="col-md-7 col-sm-7 col-xs-6">
                                                <@orcid.msg 'groups.common.sources' /> <span class="hide-sources" ng-click="hideSources(peerReviewDuplicateGroup.id)"><@orcid.msg 'groups.common.close_sources' /></span>
                                            </div>
                                            
                                            <div class="col-md-2 col-sm-2 hidden-xs">
                                                <@orcid.msgCapFirst 'groups.common.preferred' />
                                            </div>
                                        </div>
                                    </div>
                                </li>
                                <li ng-repeat="peerReview in peerReviewDuplicateGroup.peerReviews" ng-hide="peerReview.putCode.value != peerReviewDuplicateGroup.activePutCode && editSources[peerReviewDuplicateGroup.id] != true" >
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
                                        <div class="col-md-12 info-detail" ng-if="peerReviewSrvc.details.externalIdentifiers[0].externalIdentifierId.value != null" ng-cloak>
                                            <span class="workspace-title"><@orcid.msg 'peer_review.review_identifiers' />&nbsp;</span> 
                                            <span ng-repeat='ie in peerReviewSrvc.details.externalIdentifiers'><span
                                                ng-bind-html='ie | peerReviewExternalIdentifierHtml:$first:$last:peerReviewSrvc.details.externalIdentifiers.length:showDetails[group.groupId]:false'></span>                            
                                            </span>                             
                                        </div>
                                        <div class="col-md-12 info-detail" ng-if="peerReviewSrvc.details.orgName.value != null" ng-cloak>
                                            <span class="workspace-title"><@orcid.msg 'peer_review.convening_organization' />&nbsp;</span><span ng-bind="peerReviewSrvc.details.orgName.value"></span>(<span ng-bind="peerReviewSrvc.details.city.value"></span><span ng-if="peerReviewSrvc.details.city.value">,</span> <span ng-bind="peerReviewSrvc.details.countryForDisplay"></span>)
                                        </div>
                                        <div class="col-md-12 info-detail">
                                            <span ng-if="peerReviewSrvc.details.subjectName.value != null">
                                                <span class="workspace-title">Review subject:&nbsp;</span>                                  
                                                <span ng-bind="peerReviewSrvc.details.subjectName.value"></span>
                                            </span>
                                            <span ng-if="peerReviewSrvc.details.subjectName.value != null" ng-bind="peerReviewSrvc.details.subjectType.value"></span>
                                            <span ng-if="peerReviewSrvc.details.subjectContainerName != null">
                                                {{peerReviewSrvc.details.subjectContainerName.value}}.
                                            </span>
                                            <span ng-if="peerReviewSrvc.details.subjectExternalIdentifier.externalIdentifierId.value != null" ng-cloak>
                                                <span ng-repeat='ie in peerReviewSrvc.details'><span
                                                    ng-bind-html='ie | peerReviewExternalIdentifierHtml:$first:$last:peerReviewSrvc.details.subjectExternalIdentifier.length:showDetails[group.groupId]:true'></span>                           
                                                </span>                             
                                            </span>                             
                                        </div>   
                                        <div class="col-md-12 sources-container-header">          
                                            <div class="row">
                                                <div class="col-md-3 col-sm-3 col-xs-6">
                                                    <@orcid.msg 'groups.common.created'/>: <span ng-bind="peerReviewSrvc.details.createdDate | ajaxFormDateToISO8601"></span>
                                                </div>              
                                            </div>
                                        </div>
                                    </div>
                                    <!-- Begin of source -->
                                    <div class="row source-line">
                                        <div class="col-md-12 sources-container-header">       
                                            <div class="row">
                                                <div class="col-md-7 col-sm-7 col-xs-12">
                                                    <@orcid.msg 'groups.common.source'/>: {{peerReview.sourceName}}    
                                                </div>
                                                <div class="col-md-3 col-sm-3 col-xs-9" ng-if="editSources[peerReviewDuplicateGroup.id] != true">
                                                      <span class="glyphicon glyphicon-check"></span><span> <@orcid.msg 'groups.common.preferred_source' /></span> <span ng-hide="peerReviewDuplicateGroup.peerReviews.length == 1">(</span><a ng-click="showSources(peerReviewDuplicateGroup.id)" ng-hide="peerReviewDuplicateGroup.peerReviews.length == 1" ng-mouseenter="showTooltip(peerReview.putCode.value+'-sources')" ng-mouseleave="hideTooltip(peerReview.putCode.value+'-sources')"><@orcid.msg 'groups.common.of'/> {{peerReviewDuplicateGroup.peerReviews.length}}</a><span ng-hide="peerReviewDuplicateGroup.peerReviews.length == 1">)</span>
                                                      <div class="popover popover-tooltip top sources-popover" ng-if="showElement[peerReview.putCode.value+'-sources']">
                                                           <div class="arrow"></div>
                                                           <div class="popover-content">
                                                               <@orcid.msg 'groups.common.sources.show_other_sources' />
                                                           </div>
                                                      </div>
                                                </div>              
                                                <div class="col-md-3 col-sm-3 col-xs-9" ng-if="editSources[peerReviewDuplicateGroup.id]">
                                                    <span class="glyphicon glyphicon-check" ng-if="peerReview.putCode.value == peerReviewDuplicateGroup.activePutCode"></span><span ng-if="peerReview.putCode.value == peerReviewDuplicateGroup.activePutCode"> <@orcid.msg 'groups.common.preferred_source' /></span>
                                                    <#if !(isPublicProfile??)>
                                                        <a ng-click="peerReviewSrvc.makeDefault(peerReviewDuplicateGroup, peerReview.putCode.value); " ng-if="peerReview.putCode.value != peerReviewDuplicateGroup.activePutCode">
                                                             <span class="glyphicon glyphicon-unchecked"></span> <@orcid.msg 'groups.common.make_preferred' />
                                                        </a>
                                                    </#if>
                                                </div>   
                                            </div>
                                        </div>
                                    </div>
                                    <!-- End of source -->  
                                </li>
                            </ul>
                        </div>
                    </li>
                </ul>
            </div>
        </div>
    </div>
</div>