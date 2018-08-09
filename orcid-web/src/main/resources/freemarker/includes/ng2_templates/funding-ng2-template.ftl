<script type="text/ng-template" id="funding-ng2-template">
    <div>
        <!-- Funding -->
        <div id="workspace-fundings" class="workspace-accordion-item workspace-accordion-active" [hidden]="publicView == 'true' && groups.length < 1">
            <div class="workspace-accordion-header clearfix">
                <div class="row">
                    <div class="col-md-4 col-sm-4 col-xs-12">
                        <a href="" (click)="workspaceSrvc.toggleFunding($event)" class="toggle-text">
                            <i class="glyphicon-chevron-down glyphicon x075" [ngClass]="{'glyphicon-chevron-right':workspaceSrvc.displayFunding==false}"></i>
                            <@orcid.msg 'workspace.Funding'/> (<span>{{groups.length}}</span>)
                        </a>
                        <#if !(isPublicProfile??)> 
                            <div class="popover-help-container">
                                <i class="glyphicon glyphicon-question-sign"></i>
                                <div id="funding-help" class="popover bottom">
                                    <div class="arrow"></div>
                                    <div class="popover-content">
                                        <p><@orcid.msg 'manage_funding_settings.helpPopoverFunding'/> <a href="${knowledgeBaseUri}/articles/326033" target="manage_funding_settings.helpPopoverFunding"><@orcid.msg 'common.learn_more'/></a></p>
                                    </div>
                                </div>
                            </div> 
                        </#if> 
                    </div>
                    <div class="col-md-8 col-sm-8 col-xs-12 action-button-bar" *ngIf="workspaceSrvc.displayFunding">
                        <!-- Sort -->
                        <#escape x as x?html>                        
                        <div class="menu-container">                                     
                            <ul class="toggle-menu">
                                <li>
                                    <span class="glyphicon glyphicon-sort"></span>                          
                                    <@orcid.msg 'manual_orcid_record_contents.sort'/>
                                    <ul class="menu-options sort">
                                        <li [ngClass]="{'checked':sortDisplayKeyFundings=='startDate'}">                                         
                                            <a (click)="sort('funding', 'startDate', true);" class="action-option manage-button">
                                                <@orcid.msg 'manual_orcid_record_contents.sort_start_date'/>
                                                <span *ngIf="sortDisplayKeyFundings=='startDate' && sortAscFundings==false" [ngClass]="{'glyphicon glyphicon-sort-by-order-alt':sortDisplayKeyFundings=='startDate'}"></span>
                                                <span *ngIf="sortDisplayKeyFundings=='startDate' && sortAscFundings==true" [ngClass]="{'glyphicon glyphicon-sort-by-order':sortDisplayKeyFundings=='startDate'}"></span>
                                            </a>                                                                                    
                                        </li>
                                        <li [ngClass]="{'checked':sortDisplayKeyFundings=='endDate'}">
                                            <a (click)="sort('funding', 'endDate', true);" class="action-option manage-button">
                                                <@orcid.msg 'manual_orcid_record_contents.sort_end_date'/>
                                                <span *ngIf="sortDisplayKeyFundings=='endDate' && sortAscFundings==false" [ngClass]="{'glyphicon glyphicon-sort-by-alphabet-alt':sortDisplayKeyFundings=='endDate'}" ></span>
                                                <span *ngIf="sortDisplayKeyFundings=='endDate' && sortAscFundings==true" [ngClass]="{'glyphicon glyphicon-sort-by-alphabet':sortDisplayKeyFundings=='endDate'}" ></span>
                                            </a>                                            
                                        </li>
                                        <li [ngClass]="{'checked':sortDisplayKeyFundings=='title'}">                                            
                                            <a (click)="sort('funding', 'title', true);" class="action-option manage-button">
                                                <@orcid.msg 'manual_orcid_record_contents.sort_title'/>
                                                <span *ngIf="sortDisplayKeyFundings=='title' && sortAscFundings==false" [ngClass]="{'glyphicon glyphicon-sort-by-alphabet-alt':sortDisplayKeyFundings=='title'}" ></span>
                                                <span *ngIf="sortDisplayKeyFundings=='title' && sortAscFundings==true" [ngClass]="{'glyphicon glyphicon-sort-by-alphabet':sortDisplayKeyFundings=='title'}" ></span>
                                            </a>                                            
                                        </li>                                            
                                    </ul>                                        
                                </li>
                            </ul>                                   
                        </div>
                        </#escape>
                        <!--Add funding-->
                        <#if !(isPublicProfile??)>
                            <ul class="workspace-bar-menu">
                                <li class="hidden-xs">
                                    <div class="menu-container" id="add-funding-container">
                                        <ul class="toggle-menu">
                                            <li [ngClass]="{'green-bg' : fundingImportWizard == true}">   
                                                <span class="glyphicon glyphicon-plus"></span>
                                                <@orcid.msgCapFirst 'manual_funding_form_contents.add_grant' />    
                                                <ul class="menu-options funding">
                                                    <!-- Search & Link -->
                                                    <#if fundingImportWizards?has_content>
                                                        <li>                                                
                                                            <a class="action-option manage-button" (click)="showFundingImportWizard()">
                                                                <span class="glyphicon glyphicon-cloud-upload"></span>
                                                                <@orcid.msg 'manual_orcid_record_contents.search_link'/>
                                                            </a>
                                                        </li>
                                                    </#if>                      
                                                    <!-- Add Manually -->
                                                    <li>
                                                        <a id="add-funding" class="action-option manage-button" (click)="addFundingModal()">
                                                            <span class="glyphicon glyphicon-plus"></span>
                                                            <@orcid.msg 'manual_orcid_record_contents.link_manually'/>
                                                        </a>
                                                    </li>
                                                </ul>
                                            </li>                                   
                                        </ul>
                                    </div>
                                </li>
                                <!-- Mobile Version Workaround -->
                                <!-- Search & Link -->
                                <#if fundingImportWizards?has_content>
                                    <li class="hidden-md hidden-sm visible-xs-inline">                                               
                                        <a class="action-option manage-button action-funding-mobile" (click)="showFundingImportWizard()">
                                            <span class="glyphicon glyphicon-cloud-upload"></span>
                                            <@orcid.msg 'manual_orcid_record_contents.search_link'/>
                                        </a>
                                    </li>
                                </#if>                      
                                <!-- Add Manually -->
                                <li class="hidden-md hidden-sm visible-xs-inline">
                                    <a class="action-option manage-button action-funding-mobile" (click)="addFundingModal()">
                                        <span class="glyphicon glyphicon-plus"></span>
                                        <@orcid.msg 'manual_orcid_record_contents.link_manually'/>
                                    </a>
                                </li>
                            </ul>
                        </#if>
                    </div>
                </div>
            </div>
            
            <div *ngIf="fundingImportWizard" class="funding-import-wizard" >
                <#if ((fundingImportWizards)??)>
                    <div class="ie7fix-inner">
                        <div class="row">   
                            <div class="col-md-12 col-sm-12 col-xs-12">
                                <h1 class="lightbox-title wizard-header"><@orcid.msg 'workspace.link_funding'/></h1>
                                <span (click)="showFundingImportWizard()" class="close-wizard"><@orcid.msg 'workspace.LinkResearchActivities.hide_link_fundings'/></span>
                            </div>
                        </div>
                        <div class="row">
                            <div class="col-md-12 col-sm-12 col-xs-12">
                                <div class="justify">
                                    <p><@orcid.msg 'workspace.LinkResearchActivities.description'/></p>
                                </div>                                  
                                <#list fundingImportWizards?sort_by("name") as thirdPartyDetails>
                                    <#assign redirect = (thirdPartyDetails.redirectUri) >
                                    <#assign predefScopes = (thirdPartyDetails.scopes) >
                                    <strong><a (click)="openImportWizardUrl('<@orcid.rootPath '/oauth/authorize?client_id=${thirdPartyDetails.id}&response_type=code&scope=${predefScopes}&redirect_uri=${redirect}'/>')">${thirdPartyDetails.name}</a></strong><br />
                                    <div class="justify">
                                        <p class="wizard-description" [ngClass]="{'ellipsis-on' : wizardDescExpanded[${thirdPartyDetails.id}] == false || wizardDescExpanded[${thirdPartyDetails.id}] == null}">
                                            ${(thirdPartyDetails.description)!}
                                        <a (click)="toggleWizardDesc(${thirdPartyDetails.id})" *ngIf="wizardDescExpanded[${thirdPartyDetails.id}] == true"><span class="glyphicon glyphicon-chevron-down wizard-chevron"></span></a>
                                                    </p>                                                
                                                    <a (click)="toggleWizardDesc(${thirdPartyDetails.id})" *ngIf="wizardDescExpanded[${thirdPartyDetails.id}] == false || wizardDescExpanded[${thirdPartyDetails.id}] == null" class="toggle-wizard-desc"><span class="glyphicon glyphicon-chevron-right wizard-chevron"></span></a>
                                    </div>
                                    <#if (thirdPartyDetails_has_next)>
                                        <hr/>
                                    </#if>
                                    </#list>
                            </div>
                        </div>  
                    </div>
                </#if>
            </div>
            <div *ngIf="workspaceSrvc.displayFunding" class="workspace-accordion-content">
                <ul *ngIf="groups.length > 0" class="workspace-fundings workspace-body-list bottom-margin-medium" >
                    <li class="bottom-margin-small workspace-border-box card ng-scope" *ngFor="let group of groups | orderBy: 'sortState.predicate:sortState.reverse'">
                        <#include "funding-details-ng2.ftl"/>                    
                    </li>
                </ul>
                <div *ngIf="loading" class="text-center">
                    <i class="glyphicon glyphicon-refresh spin x4 green" id="spinner"></i>
                </div>
                <div *ngIf="loading == false && groups.length == 0" class="" >
                    <strong>
                        <#if (publicProfile)?? && publicProfile == true><@orcid.msg 'workspace_fundings_body_list.nograntaddedyet' /><#else><@orcid.msg 'workspace_fundings.havenotaddaffiliation' />
                            <#if fundingImportWizards?has_content>
                                <a (click)="showTemplateInModal('import-funding-modal')"> <@orcid.msg 'workspace_fundings_body_list.addsomenow'/></a>
                            <#else>
                                <span><@orcid.msg 'workspace_fundings_body_list.addsomenow'/></span>
                            </#if>
                        </#if>
                    </strong>
                </div>
            </div>
        </div>
    </div>
</script>