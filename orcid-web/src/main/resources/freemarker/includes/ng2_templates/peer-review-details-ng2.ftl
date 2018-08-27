
    <ul class="sources-edit-list">
        <li class="peer-review-group">
            <!-- active row summary info -->
            <div class="row">
                <div class="col-md-9 col-sm-9 col-xs-8">
                    <div>
                        <span class="title" (click)="showDetailsMouseClick(group,$event);"><span [ngClass]="(moreInfo[group?.groupId] == true) ? 'glyphicon glyphicon-chevron-down' : 'glyphicon glyphicon-chevron-right'"></span> <span><@orcid.msg 'peer_review.review_activity_for' /> </span><span class="peer-review-title"><span>{{group.name}}</span>({{group?.peerReviewDuplicateGroups?.length}})</span></span>
                    </div>
                </div>
                <div class="col-md-3 col-sm-3 col-xs-4 workspace-toolbar">
                    <ul class="workspace-private-toolbar">
                        <#if !(isPublicProfile??)>
                        <!-- Privacy -->
                        <li>
                            <@orcid.privacyToggle2Ng2 angularModel="group.peerReviewDuplicateGroups[0].peerReviews[0].visibility.visibility"
                            elementId="group.activePutCode" 
                            questionClick=""
                            clickedClassCheck="{'popover-help-container-show':privacyHelp[group.peerReviewDuplicateGroups[0].peerReviews[0].putCode.value]==true}"
                            publicClick="setGroupPrivacy(group, 'PUBLIC', $event)"
                            limitedClick="setGroupPrivacy(group, 'LIMITED', $event)"
                            privateClick="setGroupPrivacy(group, 'PRIVATE', $event)"/>
                        </li>
                        </#if>
                    </ul>

                    <#if !(isPublicProfile??)>
                    <div *ngIf="!peerReviewService.consistentVis(group)" class="vis-issue">
                        <div class="popover-help-container">
                            <span class="glyphicons circle_exclamation_mark" (mouseleave)="hideTooltip('vis-issue')" (mouseenter)="showTooltip('vis-issue')"></span>
                            <div class="popover vis-popover bottom" *ngIf="showElement['vis-issue'] == true">
                                <div class="arrow"></div>
                                <div class="popover-content">
                                    <@orcid.msg 'groups.common.data_inconsistency' />                                            
                                </div>
                            </div>
                        </div>                                    
                    </div>
                    </#if> 

                </div>
            </div>
            <!--More info-->
            <div class="more-info" *ngIf="moreInfo[group.groupId] == true">
                <div class="content">       
                    <div class="row">
                        <div class="col-md-12">
                            <div class="group-summary-details">
                                <span class="italic" *ngIf="group.type">{{group.type}}</span><span *ngIf="group.description">, {{group.description}}></span>
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
                                <li *ngFor="let peerReviewDuplicateGroup of group.peerReviewDuplicateGroups" class="bottom-margin-small workspace-border-box card">
                                    <div class="work-list-container">
                                        <ul class="sources-edit-list">
                                            <li *ngIf="editSources[peerReviewDuplicateGroup.id]" class="source-header" [ngClass]="{'source-active' : editSources[peerReviewDuplicateGroup.id] == true}">
                                                <div class="sources-header">
                                                    <div class="row">
                                                        <div class="col-md-7 col-sm-7 col-xs-6">
                                                            <@orcid.msg 'groups.common.sources' /> <span class="hide-sources" (click)="hideSources(peerReviewDuplicateGroup.id)"><@orcid.msg 'groups.common.close_sources' /></span>
                                                        </div>
                                                        
                                                        <div class="col-md-2 col-sm-2 hidden-xs">
                                                            <@orcid.msgCapFirst 'groups.common.preferred' />
                                                        </div>
                                                    </div>
                                                </div>
                                            </li>
                                            <ng-container *ngFor="let peerReview of peerReviewDuplicateGroup.peerReviews">
                                                <li *ngIf="peerReview.putCode.value == peerReviewDuplicateGroup.activePutCode || editSources[peerReviewDuplicateGroup.id] == true">
                                                    <!-- Active row -->
                                                    <div class="row source-line-peer-review">
                                                        <div class="col-md-3 col-sm-3 col-xs-3">
                                                            <span *ngIf="peerReview.completionDate.year">{{peerReview.completionDate.year}}</span><span *ngIf="peerReview.completionDate.month">-{{peerReview.completionDate.month}}</span><span *ngIf="peerReview.completionDate.day">-{{peerReview.completionDate.month}}</span>
                                                        </div>
                                                        <div *ngIf="peerReview?.type?.value" class="col-md-2 col-sm-2 col-xs-2">{{peerReview.type.value}}</div>
                                                        <div *ngIf="peerReview?.role?.value" class="col-md-3 col-sm-3 col-xs-3">{{peerReview.role.value}}</div>
                                                        <div class="col-md-4 col-sm-4 col-xs-4">    <span class="pull-right"> 
                                                                <a (click)="showMoreDetails(peerReview.putCode.value,$event)" *ngIf="!showPeerReviewDetails[peerReview.putCode.value]">
                                                                    <span class="glyphicons expand"></span>
                                                                    <span class="hidden-xs"><@orcid.msg 'common.details.show_details_lc' /></span>
                                                                </a> 
                                                                <a (click)="hideMoreDetails(peerReview.putCode.value);" *ngIf="showPeerReviewDetails[peerReview.putCode.value]">
                                                                    <span class="glyphicons collapse_top"></span>       <span class="hidden-xs"><@orcid.msg 'common.details.hide_details_lc' /></span>
                                                                </a> | 
                                                                <a href="{{peerReview.url.value}}" *ngIf="peerReview.url != null" target="peer_review.view"><span><@orcid.msg 'peer_review.view' /></span></a><span *ngIf="peerReview.url == null"><@orcid.msg 'peer_review.view' /></span>
                                                                 <#if !(isPublicProfile??)>
                                                                    <div (click)="deletePeerReviewConfirm(peerReview)" class="peer-review-delete"> | <span class="glyphicon glyphicon-trash"></span>
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
                                                    <div class="row" *ngIf="showPeerReviewDetails[peerReview.putCode.value]">
                                                        <!--Ext ids-->
                                                        <div class="col-md-12 info-detail" *ngIf="peerReview?.externalIdentifiers?.length > 0">
                                                            <ul class="id-details clearfix">
                                                                <li *ngFor='let extID of peerReview?.externalIdentifiers;let i = index;trackBy:trackByIndex | orderBy:["-relationship.value", "type.value"]' class="url-popover">
                                                                    <span *ngIf="peerReview?.externalIdentifiers[0]?.externalIdentifierId?.value?.length > 0">
                                                                        <ext-id-popover-ng2 [extID]="extID" [putCode]="peerReview.putCode+'resourceItem'+index" [activityType]="'peerReview'"></ext-id-popover-ng2>
                                                                    </span>
                                                                 </li>
                                                            </ul> 
                                                        </div>
                                                        <div class="col-md-12 info-detail" *ngIf="peerReviewSrvc.details.orgName.value != null">
                                                            <span class="workspace-title"><@orcid.msg 'peer_review.convening_organization' />&nbsp;</span><span ng-bind="peerReviewSrvc.details.orgName.value"></span>(<span ng-bind="peerReviewSrvc.details.city.value"></span><span *ngIf="peerReviewSrvc.details.city.value">,</span> <span ng-bind="peerReviewSrvc.details.countryForDisplay"></span>)
                                                        </div>
                                                        <div class="col-md-12 info-detail">
                                                            <span *ngIf="peerReviewSrvc.details.subjectName.value != null">
                                                                <span class="workspace-title">Review subject:&nbsp;</span>                                  
                                                                <span ng-bind="peerReviewSrvc.details.subjectName.value"></span>
                                                            </span>
                                                            <span *ngIf="peerReviewSrvc.details.subjectName.value != null" ng-bind="peerReviewSrvc.details.subjectType.value"></span>
                                                            <span *ngIf="peerReviewSrvc.details.subjectContainerName != null">
                                                                {{peerReviewSrvc.details.subjectContainerName.value}}.
                                                            </span>
                                                            <span *ngIf="peerReviewSrvc.details.subjectExternalIdentifier.externalIdentifierId.value != null">
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
                                                                <div class="col-md-3 col-sm-3 col-xs-9" *ngIf="editSources[peerReviewDuplicateGroup.id] != true">
                                                                      <span class="glyphicon glyphicon-check"></span><span> <@orcid.msg 'groups.common.preferred_source' /></span> <span ng-hide="peerReviewDuplicateGroup.peerReviews.length == 1">(</span><a (click)="showSources(peerReviewDuplicateGroup.id)" ng-hide="peerReviewDuplicateGroup.peerReviews.length == 1" (mouseenter)="showTooltip(peerReview.putCode.value+'-sources')" (mouseleave)="hideTooltip(peerReview.putCode.value+'-sources')"><@orcid.msg 'groups.common.of'/> {{peerReviewDuplicateGroup.peerReviews.length}}</a><span ng-hide="peerReviewDuplicateGroup.peerReviews.length == 1">)</span>
                                                                      <div class="popover popover-tooltip top sources-popover" *ngIf="showElement[peerReview.putCode.value+'-sources']">
                                                                           <div class="arrow"></div>
                                                                           <div class="popover-content">
                                                                               <@orcid.msg 'groups.common.sources.show_other_sources' />
                                                                           </div>
                                                                      </div>
                                                                </div>              
                                                                <div class="col-md-3 col-sm-3 col-xs-9" *ngIf="editSources[peerReviewDuplicateGroup.id]">
                                                                    <span class="glyphicon glyphicon-check" *ngIf="peerReview.putCode.value == peerReviewDuplicateGroup.activePutCode"></span><span *ngIf="peerReview.putCode.value == peerReviewDuplicateGroup.activePutCode"> <@orcid.msg 'groups.common.preferred_source' /></span>
                                                                    <#if !(isPublicProfile??)>
                                                                        <a (click)="peerReviewSrvc.makeDefault(peerReviewDuplicateGroup, peerReview.putCode.value); " *ngIf="peerReview.putCode.value != peerReviewDuplicateGroup.activePutCode">
                                                                             <span class="glyphicon glyphicon-unchecked"></span> <@orcid.msg 'groups.common.make_preferred' />
                                                                        </a>
                                                                    </#if>
                                                                </div>   
                                                            </div>
                                                        </div>
                                                    </div>
                                                    <!-- End of source -->  
                                                </li>
                                            </ng-container>
                                        </ul>
                                    </div>
                                </li>
                            </ul>
                        </div>
                    </div>
                </div>
            </div>
            <!--End more info-->
        </li>
    </ul>
