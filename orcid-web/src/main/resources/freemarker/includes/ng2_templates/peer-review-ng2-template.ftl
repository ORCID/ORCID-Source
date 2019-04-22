<script type="text/ng-template" id="peer-review-ng2-template">
    <div>
        <!-- PEER REVIEW -->
        <div id="workspace-peer-review" class="workspace-accordion-item workspace-accordion-active" [hidden]="publicView == 'true' && peerReviewService.groups.length < 1" *ngIf="noLinkFlag || peerReviewService?.groups?.length > 0">
            <div class="workspace-accordion-header clearfix">
                <div class="row">
                    <div class="col-md-6 col-sm-6 col-xs-12">
                        <a (click)="workspaceSrvc.togglePeerReview()" class="toggle-text">
                            <i class="glyphicon-chevron-down glyphicon x075" [ngClass]="{'glyphicon-chevron-right':workspaceSrvc.displayPeerReview==false}"></i>
                            <@orcid.msg 'workspace_peer_review_body_list.peerReview'/> (<span>{{peerReviewService.groups.length}}</span>)
                        </a>
                        <div *ngIf="!isPublicPage" class="popover-help-container">
                            <i class="glyphicon glyphicon-question-sign"></i>
                            <div id="peer-review-help" class="popover bottom">
                                <div class="arrow"></div>
                                <div class="popover-content">
                                    <p><@orcid.msg 'manage_peer_review_settings.helpPopoverPeerReview'/> <a href="<@orcid.msg 'common.kb_uri_default'/>360006971333" target="manage_peer_review_settings.helpPopoverPeerReview"><@orcid.msg 'common.learn_more'/></a></p>
                                </div>
                            </div>
                        </div>                    
                    </div>
                    <div class="col-md-6 col-sm-6 col-xs-12 action-button-bar" *ngIf="workspaceSrvc.displayPeerReview">
                        <#escape x as x?html>
                        <!--Sort menu-->
                        <div class="menu-container">      
                            <ul class="toggle-menu">
                                <li (click)="sort()">
                                    <span class="glyphicon glyphicon-sort"></span>
                                    <@orcid.msg 'manual_orcid_record_contents.sort'/>                                 
                                </li>
                            </ul>                                   
                        </div>
                        <!--End sort menu-->
                        <ul class="workspace-bar-menu" *ngIf="noLinkFlag && !isPublicPage">
                        <!--Add works-->    
                            <li class="hidden-xs">
                                <div class="menu-container" id="add-peer-review-container">
                                    <ul class="toggle-menu">
                                        <li [ngClass]="{'green-bg' : peerReviewImportWizard == true}"> 
                                            <span class="glyphicon glyphicon-plus"></span>
                                            <@orcid.msg 'groups.common.add_peer_review'/>
                                            <ul class="menu-options works">
                                            <!--Search & link-->
                                                <li>
                                                    <a class="action-option manage-button" (click)="showPeerReviewImportWizard()">
                                                        <span class="glyphicon glyphicon-cloud-upload"></span>
                                                        <@orcid.msg 'manual_orcid_record_contents.search_link'/>
                                                    </a>
                                                </li>
                                            </ul>
                                        </li>
                                    </ul>
                                </div>  
                            </li>  
                        </ul>         
                        </#escape>                                
                    </div>
                </div>
            </div>    
            <!-- Peer Review Import Wizard -->
            <div *ngIf="peerReviewImportWizard && workspaceSrvc.displayPeerReview" class="work-import-wizard">
                <div class="ie7fix-inner">
                    <div class="row"> 
                        <div class="col-md-12 col-sm-12 col-xs-12">
                            <h1 class="lightbox-title wizard-header"><@orcid.msg 'workspace.link_peer_reviews'/></h1>
                            <span (click)="showPeerReviewImportWizard()" class="close-wizard"><@orcid.msg 'workspace.LinkResearchActivities.hide_link_peer_review'/></span>
                        </div>
                    </div>
                    <div class="row">
                        <div class="col-md-12 col-sm-12 col-xs-12">
                            <p class="wizard-content">
                                <@orcid.msg 'workspace.LinkPeerReviewsActivities.description'/> <a href="https://support.orcid.org/hc/articles/360006971333" target="workspace.LinkPeerReviewsActivities.description"> <@orcid.msg 'workspace.LinkPeerReviewsActivities.description.more_info'/></a>
                            </p>
                        </div>
                    </div>
       
                    <div class="row wizards">               
                        <div class="col-md-12 col-sm-12 col-xs-12">
                            <div *ngFor="let wtw of peerReviewImportWizardList">
                                <strong><a (click)="openImportWizardUrlFilter(getBaseUri() + '/oauth/authorize', wtw)">{{wtw.name}}</a></strong>

                                <br />                                                                                    
                                <div class="justify">                       
                                    <p class="wizard-description" [ngClass]="{'ellipsis-on' : wizardDescExpanded[wtw.id] == false || wizardDescExpanded[wtw.id] == null}">
                                        {{wtw.description}}
                                        <a (click)="toggleWizardDesc(wtw.id)" *ngIf="wizardDescExpanded[wtw.id]"><span class="glyphicon glyphicon-chevron-right wizard-chevron"></span></a>
                                    </p>                        
                                    <a (click)="toggleWizardDesc(wtw.id)" *ngIf="wizardDescExpanded[wtw.id] == false || wizardDescExpanded[wtw.id] == null" class="toggle-wizard-desc"><span class="glyphicon glyphicon-chevron-down wizard-chevron"></span></a>
                                </div>
                                <hr/>
                            </div>
                        </div>
                    </div>
                </div>            
            </div>                   
            <div class="workspace-accordion-content">
                <ng-container  *ngIf="workspaceSrvc.displayPeerReview">
                    <ul *ngIf="peerReviewService?.groups?.length > 0" class="workspace-peer-review workspace-body-list bottom-margin-medium" id="peer-review-header">
                        <li class="bottom-margin-small workspace-border-box card" *ngFor="let group of peerReviewService.groups">
                            <#include "peer-review-details-ng2.ftl"/> 
                        </li>
                    </ul>
                    <div *ngIf="peerReviewService.loading == false && peerReviewService.groups.length == 0 && !peerReviewImportWizard">
                        <strong>
                            ${springMacroRequestContext.getMessage("workspace_peer_review_body_list.havenotaddedanypeerreviews")} 
                            <a *ngIf="noLinkFlag" (click)="showPeerReviewImportWizard()" class="no-wrap">
                            ${springMacroRequestContext.getMessage("workspace_peer_review_body_list.addsomenow")}
                            </a>
                        </strong> 
                    </div>
                </ng-container>
            </div>                                                   
        </div>      
    </div>
</script>