<script type="text/ng-template" id="funding-ng2-template">
    <#list fundingImportWizards?sort_by("name") as thirdPartyDetails>
        ${thirdPartyDetails.name}<br>
        ${thirdPartyDetails.id}<br>
        ${thirdPartyDetails.scopes}<br>
        ${thirdPartyDetails.redirectUri}<br>
    </#list>
    <div>
        <!-- Funding -->
        <div id="workspace-fundings" class="workspace-accordion-item workspace-accordion-active" [hidden]="publicView == 'true' && groups.length < 1">
            <div class="workspace-accordion-header clearfix">
                <div class="row">
                    <div class="col-md-4 col-sm-4 col-xs-12">
                        <a (click)="toggleSectionDisplay($event)" class="toggle-text">
                            <i class="glyphicon-chevron-down glyphicon x075" [ngClass]="{'glyphicon-chevron-right':workspaceSrvc.displayFunding==false}"></i>
                            <@orcid.msg 'workspace.Funding'/> (<span>{{groups.length}}</span>)
                        </a>
                        <#if !(isPublicProfile??)> 
                            <div class="popover-help-container">
                                <i class="glyphicon glyphicon-question-sign"></i>
                                <div id="funding-help" class="popover bottom">
                                    <div class="arrow"></div>
                                    <div class="popover-content">
                                        <p><@orcid.msg 'manage_funding_settings.helpPopoverFunding'/> <a href="<@orcid.msg 'common.kb_uri_default'/>360006897214" target="manage_funding_settings.helpPopoverFunding"><@orcid.msg 'common.learn_more'/></a></p>
                                    </div>
                                </div>
                            </div> 
                        </#if> 
                    </div>
                    <div class="col-md-8 col-sm-8 col-xs-12 action-button-bar" *ngIf="workspaceSrvc.displayFunding">
                        <!-- Sort -->                       
                        <div class="menu-container">                                     
                            <ul class="toggle-menu">
                                <li>
                                    <span class="glyphicon glyphicon-sort"></span>                          
                                    <@orcid.msg 'manual_orcid_record_contents.sort'/>
                                    <ul class="menu-options sort">
                                        <li [ngClass]="{'checked':sortState.predicateKey=='date'}" *ngIf="!sortHideOption">                                     
                                            <a (click)="sort('date');" class="action-option manage-button">
                                                <@orcid.msg 'manual_orcid_record_contents.sort_date'/>
                                                <span *ngIf="sortState.reverseKey['date']" [ngClass]="{'glyphicon glyphicon-sort-by-order-alt':sortState.predicateKey=='date'}"></span>
                                                <span *ngIf="sortState.reverseKey['date'] == false" [ngClass]="{'glyphicon glyphicon-sort-by-order':sortState.predicateKey=='date'}"></span>
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
                                        <li [ngClass]="{'checked':sortState.predicateKey=='type'}" *ngIf="!sortHideOption">                                          
                                            <a (click)="sort('type');" class="action-option manage-button">
                                                <@orcid.msg 'manual_orcid_record_contents.sort_type'/>
                                                <span *ngIf="sortState.reverseKey['type']" [ngClass]="{'glyphicon glyphicon-sort-by-alphabet-alt':sortState.predicateKey=='type'}"></span>
                                                <span *ngIf="sortState.reverseKey['type'] == false" [ngClass]="{'glyphicon glyphicon-sort-by-alphabet':sortState.predicateKey=='type'}"></span>
                                            </a>          
                                        </li>                                                                  
                                    </ul>                                        
                                </li>
                            </ul>                                   
                        </div>
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

            <!-- Funding Import Wizard -->
            <div *ngIf="fundingImportWizard && workspaceSrvc.displayFunding" class="work-import-wizard">
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
                                <strong><a (click)="openImportWizardUrlFilter('<@orcid.rootPath '/oauth/authorize'/>', wtw)">{{wtw.name}}</a></strong>

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
                        <#if (publicProfile)?? && publicProfile == true><@orcid.msg 'workspace_fundings_body_list.nograntaddedyet' /><#else><@orcid.msg 'workspace_fundings.havenotaddaffiliation' />
                            <#if fundingImportWizards?has_content>
                                <a (click)="showFundingImportWizard()"> <@orcid.msg 'workspace_fundings_body_list.addsomenow'/></a>
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