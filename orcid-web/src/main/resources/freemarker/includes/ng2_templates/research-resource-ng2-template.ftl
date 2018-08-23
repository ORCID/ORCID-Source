<script type="text/ng-template" id="research-resource-ng2-template">
    <div *ngIf="researchResourceService.groups?.length > 0">
        <!-- RESEARCH RESOURCE -->
        <div class="workspace-accordion-item workspace-accordion-active" [hidden]="publicView == 'true' && researchResourceService.groups.length < 1">
            <div class="workspace-accordion-header clearfix">
                <div class="row">
                    <div class="col-md-6 col-sm-6 col-xs-12">
                        <h2 (click)="workspaceSrvc.toggleResearchResource()" class="toggle-text">
                            <i class="glyphicon-chevron-down glyphicon x075" [ngClass]="{'glyphicon-chevron-right':workspaceSrvc.displayResearchResource==false}"></i>
                            <@orcid.msg 'manage.research_resources'/> (<span>{{researchResourceService.groups.length}}</span>)
                        </h2>
                        <#if !(isPublicProfile??)> 
                        <div class="popover-help-container">
                            <i class="glyphicon glyphicon-question-sign"></i>
                            <div id="research-resource-help" class="popover bottom">
                                <div class="arrow"></div>
                                <div class="popover-content">
                                    <p><strong><@orcid.msg 'manage.research_resources'/></strong> <@orcid.msg 'manage.research_resources.helpPopover'/> <a href="${knowledgeBaseUri}/articles" target="manage.research_resources.helpPopover"><@orcid.msg 'common.learn_more'/></a></p>
                                </div>
                            </div>
                        </div>
                        </#if>                     
                    </div>
                    <div class="col-md-6 col-sm-6 col-xs-12 action-button-bar" *ngIf="workspaceSrvc.displayResearchResource">
                        <#escape x as x?html>                        
                        <!--Sort menu-->
                        <div class="menu-container">                                     
                            <ul class="toggle-menu">
                                <li>
                                    <span class="glyphicon glyphicon-sort"></span>
                                    <@orcid.msg 'manual_orcid_record_contents.sort'/>
                                    <ul class="menu-options sort">
                                        <li [ngClass]="{'checked':sortState.predicateKey=='startDate'}" *ngIf="!(sortHideOption || sortState.type == 'affiliation')">                                          
                                            <a (click)="sort('startDate');" class="action-option manage-button">
                                                <@orcid.msg 'manual_orcid_record_contents.sort_start_date'/>
                                                <span *ngIf="sortState.reverseKey['startDate']" [ngClass]="{'glyphicon glyphicon-sort-by-order-alt':sortState.predicateKey=='startDate'}"></span>
                                                <span *ngIf="sortState.reverseKey['startDate'] == false" [ngClass]="{'glyphicon glyphicon-sort-by-order':sortState.predicateKey=='startDate'}"></span>
                                            </a>                                                                                    
                                        </li>
                                        <li [ngClass]="{'checked':sortState.predicateKey=='endDate'}" *ngIf="!(sortHideOption || sortState.type == 'affiliation')">                                          
                                            <a (click)="sort('endDate');" class="action-option manage-button">
                                                <@orcid.msg 'manual_orcid_record_contents.sort_end_date'/>
                                                <span *ngIf="sortState.reverseKey['endDate']" [ngClass]="{'glyphicon glyphicon-sort-by-order-alt':sortState.predicateKey=='endDate'}"></span>
                                                <span *ngIf="sortState.reverseKey['endDate'] == false" [ngClass]="{'glyphicon glyphicon-sort-by-order':sortState.predicateKey=='endDate'}"></span>
                                            </a>                                                                                    
                                        </li>
                                        <li [ngClass]="{'checked':sortState.predicateKey=='groupName'}" *ngIf="sortHideOption != null">
                                            <a (click)="sort('groupName');" class="action-option manage-button">
                                                <@orcid.msg 'manual_orcid_record_contents.sort_title'/>
                                                <span *ngIf="sortState.reverseKey['groupName']" [ngClass]="{'glyphicon glyphicon-sort-by-alphabet-alt':sortState.predicateKey=='groupName'}" ></span>
                                                <span *ngIf="sortState.reverseKey['groupName'] == false" [ngClass]="{'glyphicon glyphicon-sort-by-alphabet':sortState.predicateKey=='groupName'}" ></span>
                                            </a>                                            
                                        </li>
                                        <li [ngClass]="{'checked':sortState.predicateKey=='title'}" *ngIf="!sortHideOption">                                            
                                            <a (click)="sort('title');" class="action-option manage-button">
                                                <@orcid.msg 'manual_orcid_record_contents.sort_title'/>
                                                <span *ngIf="sortState.reverseKey['title']" [ngClass]="{'glyphicon glyphicon-sort-by-alphabet-alt':sortState.predicateKey=='title'}" ></span>
                                                <span *ngIf="sortState.reverseKey['title'] == false" [ngClass]="{'glyphicon glyphicon-sort-by-alphabet':sortState.predicateKey=='title'}" ></span>
                                            </a>                                            
                                        </li>
                                    </ul>                                           
                                </li>
                            </ul>                                   
                        </div>
                        <!--End sort menu-->
                        </#escape>                                
                    </div>
                </div>
            </div>                        
            <div *ngIf="workspaceSrvc.displayResearchResource" class="workspace-accordion-content">
                <ul id="groups-list" class="workspace-research-resource workspace-body-list bottom-margin-medium">
                    <li class="bottom-margin-small workspace-border-box card" *ngFor="let group of researchResourceService.groups">
                        <#include "research-resource-details-ng2.ftl"/>                     
                    </li>
                </ul>
                <button *ngIf="researchResourceService.showLoadMore" (click)="getResearchResourceGroups()" class="btn btn-primary">${springMacroRequestContext.getMessage("workspace.works.load_more")}</button>
                <div *ngIf="researchResourceService?.loading" class="text-center" id="workSpinner">
                    <i class="glyphicon glyphicon-refresh spin x4 green" id="spinner"></i>
                </div>
            </div>                                                   
        </div>      
    </div>
</script>