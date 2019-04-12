<script type="text/ng-template" id="funding-ng2-template">
    <div>
        <!-- Funding -->
        <div id="workspace-fundings" class="workspace-accordion-item workspace-accordion-active" [hidden]="publicView == 'true' && groups.length < 1">
            <div class="workspace-accordion-header clearfix">
                <div class="row">
                    <div class="col-md-4 col-sm-4 col-xs-12">
                        <a (click)="toggleSectionDisplay($event)" class="toggle-text">
                            <i class="glyphicon-chevron-down glyphicon x075" [ngClass]="{'glyphicon-chevron-right':workspaceSrvc.displayFunding==false}"></i>
                            <h2><@orcid.msg 'workspace.Funding'/> (<span>{{groups.length}}</span>)</h2>
                        </a>
                        <div *ngIf="!isPublicPage" class="popover-help-container">
                            <i class="glyphicon glyphicon-question-sign"></i>
                            <div id="funding-help" class="popover bottom">
                                <div class="arrow"></div>
                                <div class="popover-content">
                                    <p><@orcid.msg 'manage_funding_settings.helpPopoverFunding'/> <a href="<@orcid.msg 'common.kb_uri_default'/>360006897214" target="manage_funding_settings.helpPopoverFunding"><@orcid.msg 'common.learn_more'/></a></p>
                                </div>
                            </div>
                        </div> 
                    </div>
                    <div class="col-md-8 col-sm-8 col-xs-12 action-button-bar" *ngIf="workspaceSrvc.displayFunding">
                        <!-- Sort -->                       
                        <div class="menu-container">                                     
                            <ul class="toggle-menu">
                                <li>
                                    <span class="glyphicon glyphicon-sort"></span>                          
                                    <@orcid.msg 'manual_orcid_record_contents.sort'/>
                                    <ul class="menu-options sort">
                                        <li [ngClass]="{'checked':sortKey=='date'}">        
                                            <a (click)="sort('date');" class="action-option manage-button">
                                                <@orcid.msg 'manual_orcid_record_contents.sort_date'/>
                                                <span *ngIf="!sortAsc" [ngClass]="{'glyphicon glyphicon-sort-by-order-alt':sortKey=='date'}"></span>
                                                <span *ngIf="sortAsc" [ngClass]="{'glyphicon glyphicon-sort-by-order':sortKey=='date'}"></span>
                                            </a>        
                                        </li>
                                        <li [ngClass]="{'checked':sortKey=='title'}">   
                                            <a (click)="sort('title');" class="action-option manage-button">
                                                <@orcid.msg 'manual_orcid_record_contents.sort_title'/>
                                                <span *ngIf="!sortAsc" [ngClass]="{'glyphicon glyphicon-sort-by-alphabet-alt':sortKey=='title'}" ></span>
                                                <span *ngIf="sortAsc" [ngClass]="{'glyphicon glyphicon-sort-by-alphabet':sortKey=='title'}" ></span>
                                            </a>      
                                        </li>
                                        <li [ngClass]="{'checked':sortKey=='type'}" *ngIf="!sortHideOption"> 
                                            <a (click)="sort('type');" class="action-option manage-button">
                                                <@orcid.msg 'manual_orcid_record_contents.sort_type'/>
                                                <span *ngIf="!sortAsc" [ngClass]="{'glyphicon glyphicon-sort-by-alphabet-alt':sortKey=='type'}"></span>
                                                <span *ngIf="sortAsc" [ngClass]="{'glyphicon glyphicon-sort-by-alphabet':sortKey=='type'}"></span>
                                            </a>          
                                        </li>                         
                                    </ul>                                 
                                </li>
                            </ul>                                   
                        </div>
                        <!--Add funding-->
                        <ul *ngIf="!isPublicPage" class="workspace-bar-menu">
                            <li class="hidden-xs">
                                <div class="menu-container" id="add-funding-container">
                                    <ul class="toggle-menu">
                                         <li *ngIf="!noLinkFlag" [ngClass]="{'green-bg' : fundingImportWizard == true}" (click)="addFundingModal()">   
                                            <span class="glyphicon glyphicon-plus"></span>
                                            <@orcid.msgCapFirst 'manual_funding_form_contents.add_grant' />    
                                        </li>
                                        <li *ngIf="noLinkFlag" [ngClass]="{'green-bg' : fundingImportWizard == true}">   
                                            <span class="glyphicon glyphicon-plus"></span>
                                            <@orcid.msgCapFirst 'manual_funding_form_contents.add_grant' />    
                                            <ul class="menu-options funding">
                                                <!-- Search & Link -->
                                                    <li *ngIf="noLinkFlag">
                                                        <a class="action-option manage-button" (click)="showFundingImportWizard()">
                                                            <span class="glyphicon glyphicon-cloud-upload"></span>
                                                            <@orcid.msg 'manual_orcid_record_contents.search_link'/>
                                                        </a>
                                                    </li>                     
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
                            <li *ngIf="noLinkFlag" class="hidden-md hidden-sm visible-xs-inline">                                               
                                <a class="action-option manage-button action-funding-mobile" (click)="showFundingImportWizard()">
                                    <span class="glyphicon glyphicon-cloud-upload"></span>
                                    <@orcid.msg 'manual_orcid_record_contents.search_link'/>
                                </a>
                            </li>                     
                            <!-- Add Manually -->
                            <li class="hidden-md hidden-sm visible-xs-inline">
                                <a class="action-option manage-button action-funding-mobile" (click)="addFundingModal()">
                                    <span class="glyphicon glyphicon-plus"></span>
                                    <@orcid.msg 'manual_orcid_record_contents.link_manually'/>
                                </a>
                            </li>
                        </ul>
                    </div>
                </div>
            </div>
            <!-- Funding Import Wizard -->
            <div *ngIf="fundingImportWizard && workspaceSrvc.displayFunding && !isPublicPage" class="work-import-wizard">
                <div class="ie7fix-inner">
                    <div class="row"> 
                        <div class="col-md-12 col-sm-12 col-xs-12">
                            <h1 class="lightbox-title wizard-header"><@orcid.msg 'workspace.link_funding'/></h1>
                            <span (click)="showFundingImportWizard()" class="close-wizard"><@orcid.msg 'workspace.LinkResearchActivities.hide_link_fundings'/></span>
                        </div>
                    </div>
                    <div class="row">
                        <div class="col-md-12 col-sm-12 col-xs-12">
                            <p class="wizard-content">
                                <@orcid.msg 'workspace.LinkResearchActivities.description'/><
                            </p>
                        </div>
                    </div>
                    <div class="row wizards">               
                        <div class="col-md-12 col-sm-12 col-xs-12">
                            <div *ngFor="let wtw of fundingImportWizardList">
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
            <div *ngIf="workspaceSrvc.displayFunding" class="workspace-accordion-content">
                <ul *ngIf="groups.length > 0" class="workspace-fundings workspace-body-list bottom-margin-medium" >
                    <li class="bottom-margin-small workspace-border-box card ng-scope" *ngFor="let group of groups">
                        <#include "funding-details-ng2.ftl"/>                    
                    </li>
                </ul>
                <div *ngIf="loading" class="text-center">
                    <i class="glyphicon glyphicon-refresh spin x4 green" id="spinner"></i>
                </div>
                <div *ngIf="fundingService?.loading == false && groups.length == 0">
                    <strong>
                        <@orcid.msg 'workspace_fundings.havenotaddaffiliation' />
                        <a *ngIf="noLinkFlag" (click)="showFundingImportWizard()"> <@orcid.msg 'workspace_fundings_body_list.addsomenow'/></a>
                        <span *ngIf="!noLinkFlag"><@orcid.msg 'workspace_fundings_body_list.addsomenow'/></span>
                    </strong>
                </div>
            </div>
        </div>
    </div>
</script>