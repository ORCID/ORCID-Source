
    <ul class="sources-edit-list">
        <li class="peer-review-group">
            <!-- active row summary info -->
            <div class="row">
                <div class="col-md-9 col-sm-9 col-xs-8">
                    <div>
                        <span class="title" (click)="showDetailsMouseClick(group,$event);"><span [ngClass]="{'glyphicon x075 glyphicon-chevron-right': moreInfo[group.groupId] ==false, 'glyphicon x075 glyphicon-chevron-down': moreInfo[group.groupId]}"></span> <span><@orcid.msg 'peer_review.review_activity_for' /> </span><span class="peer-review-title"><span>{{group.name}}</span>({{group?.peerReviewDuplicateGroups?.length}})</span></span>
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
            <!--more info-->
        </li>
    </ul>
