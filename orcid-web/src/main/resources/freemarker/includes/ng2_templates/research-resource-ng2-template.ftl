<script type="text/ng-template" id="research-resource-ng2-template">
    <div *ngIf="researchResourceService.groups?.length > 0">
        <!-- RESEARCH RESOURCE -->
        <div class="workspace-accordion-item workspace-accordion-active" [hidden]="publicView == 'true' && researchResourceService.groups.length < 1">
            <div class="workspace-accordion-header clearfix">
                <div class="row">
                    <div class="col-md-6 col-sm-6 col-xs-12">
                        <a (click)="workspaceSrvc.toggleResearchResource()" class="toggle-text">
                            <i class="glyphicon-chevron-down glyphicon x075" [ngClass]="{'glyphicon-chevron-right':workspaceSrvc.displayResearchResource==false}"></i>
                            <h2><@orcid.msg 'manage.research_resources'/> (<span>{{researchResourceService.groupsLabel}}</span>)</h2>
                        </a>
                        <div *ngIf="!isPublicPage" class="popover-help-container">
                            <i class="glyphicon glyphicon-question-sign"></i>
                            <div id="research-resource-help" class="popover bottom">
                                <div class="arrow"></div>
                                <div class="popover-content">
                                    <p><strong><@orcid.msg 'manage.research_resources'/></strong> <@orcid.msg 'manage.research_resources.helpPopover'/> <a href="<@orcid.msg 'common.kb_uri_default'/>" target="manage.research_resources.helpPopover"><@orcid.msg 'common.learn_more'/></a></p>
                                </div>
                            </div>
                        </div>                  
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
                                        <li [ngClass]="{'checked':sortKey=='startDate'}">
                                            <a (click)="sort('startDate');" class="action-option manage-button">
                                                <@orcid.msg 'manual_orcid_record_contents.sort_start_date'/>
                                                <span *ngIf="!sortAsc" [ngClass]="{'glyphicon glyphicon-sort-by-order-alt':sortKey=='startDate'}"></span>
                                                <span *ngIf="sortAsc" [ngClass]="{'glyphicon glyphicon-sort-by-order':sortKey=='startDate'}"></span>
                                            </a>                                                                                    
                                        </li>
                                        <li [ngClass]="{'checked':sortKey=='endDate'}"> 
                                            <a (click)="sort('endDate');" class="action-option manage-button">
                                                <@orcid.msg 'manual_orcid_record_contents.sort_end_date'/>
                                                <span *ngIf="!sortAsc" [ngClass]="{'glyphicon glyphicon-sort-by-order-alt':sortKey=='endDate'}"></span>
                                                <span *ngIf="sortAsc" [ngClass]="{'glyphicon glyphicon-sort-by-order':sortKey=='endDate'}"></span>
                                            </a>        
                                        </li>
                                        <li [ngClass]="{'checked':sortKey=='title'}">                                            
                                            <a (click)="sort('title');" class="action-option manage-button">
                                                <@orcid.msg 'manual_orcid_record_contents.sort_title'/>
                                                <span *ngIf="!sortAsc" [ngClass]="{'glyphicon glyphicon-sort-by-alphabet-alt':sortKey=='title'}" ></span>
                                                <span *ngIf="sortAsc" [ngClass]="{'glyphicon glyphicon-sort-by-alphabet':sortKey=='title'}" ></span>
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