<script type="text/ng-template" id="research-resource-ng2-template">
    <div>
        <!-- RESEARCH RESOURCE -->
        <div id="workspace-employment" class="workspace-accordion-item workspace-accordion-active" [hidden]="publicView == 'true' && researchResourceService.groups.length < 1">
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
                        <div class="menu-container">                                     
                            <ul class="toggle-menu">
                                <li>
                                    <span class="glyphicon glyphicon-sort"></span>                          
                                    <@orcid.msg 'manual_orcid_record_contents.sort'/>
                                    <ul class="menu-options sort">
                                        <li [ngClass]="{'checked':sortDisplayKey=='startDate'}">                                         
                                            <a (click)="sort('startDate', true);" class="action-option manage-button">
                                                <@orcid.msg 'manual_orcid_record_contents.sort_start_date'/>
                                                <span *ngIf="sortDisplayKey=='startDate' && sortAsc==false" [ngClass]="{'glyphicon glyphicon-sort-by-order-alt':sortDisplayKeyResearch=='startDate'}"></span>
                                                <span *ngIf="sortDisplayKey=='startDate' && sortAsc==true" [ngClass]="{'glyphicon glyphicon-sort-by-order':sortDisplayKey=='startDate'}"></span>
                                            </a>                                                                                    
                                        </li>
                                        <li [ngClass]="{'checked':sortDisplayKey=='endDate'}">
                                            <a (click)="sort('employment', 'endDate', true);" class="action-option manage-button">
                                                <@orcid.msg 'manual_orcid_record_contents.sort_end_date'/>
                                                <span *ngIf="sortDisplayKeyResearchResources=='endDate' && sortAscResearchResources==false" [ngClass]="{'glyphicon glyphicon-sort-by-alphabet-alt':sortDisplayKeyResearchResources=='endDate'}" ></span>
                                                <span *ngIf="sortDisplayKeyResearchResources=='endDate' && sortAscResearchResources==true" [ngClass]="{'glyphicon glyphicon-sort-by-alphabet':sortDisplayKeyResearchResources=='endDate'}" ></span>
                                            </a>                                            
                                        </li>
                                        <li [ngClass]="{'checked':sortDisplayKeyResearchResources=='title'}">                                            
                                            <a (click)="sort('employment', 'title', true);" class="action-option manage-button">
                                                <@orcid.msg 'manual_orcid_record_contents.sort_title'/>
                                                <span *ngIf="sortDisplayKeyResearchResources=='title' && sortAscResearchResources==false" [ngClass]="{'glyphicon glyphicon-sort-by-alphabet-alt':sortDisplayKeyResearchResources=='title'}" ></span>
                                                <span *ngIf="sortDisplayKeyResearchResources=='title' && sortAscResearchResources==true" [ngClass]="{'glyphicon glyphicon-sort-by-alphabet':sortDisplayKeyResearchResources=='title'}" ></span>
                                            </a>                                            
                                        </li>                                            
                                    </ul>                                        
                                </li>
                            </ul>                                   
                        </div>
                        </#escape>                                
                    </div>
                </div>
            </div>                        
            <div *ngIf="workspaceSrvc.displayResearchResource" class="workspace-accordion-content">
                <ul id="groups-list" *ngIf="researchResourceService.groups?.length > 0" class="workspace-affiliations workspace-body-list bottom-margin-medium">
                    <li class="bottom-margin-small workspace-border-box affiliation-box card" *ngFor="let group of researchResourceService.groups">
                        <#include "research-resource-details-ng2.ftl"/>                     
                    </li>
                </ul>
            </div>                                                   
        </div>      
    </div>
</script>