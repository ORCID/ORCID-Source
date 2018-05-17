<script type="text/ng-template" id="affiliation-ng2-template">
    <div>
        <!-- EMPLOYMENT -->
        <div id="workspace-employment" class="workspace-accordion-item workspace-accordion-active" [hidden]="publicView == 'true' && employments.length < 1">
            <div class="workspace-accordion-header clearfix">
                <div class="row">
                    <div class="col-md-6 col-sm-6 col-xs-12">
                        <h2 (click)="workspaceSrvc.toggleEmployment()" class="toggle-text">
                            <i class="glyphicon-chevron-down glyphicon x075" [ngClass]="{'glyphicon-chevron-right':workspaceSrvc.displayEmployment==false}"></i>
                            <@orcid.msg 'org.orcid.jaxb.model.message.AffiliationType.employment'/> (<span>{{employments.length}}</span>)
                        </h2>
                        <#if !(isPublicProfile??)> 
                        <div class="popover-help-container">
                            <i class="glyphicon glyphicon-question-sign"></i>
                            <div id="employment-help" class="popover bottom">
                                <div class="arrow"></div>
                                <div class="popover-content">
                                    <p><@orcid.msg 'manage_affiliations_settings.helpPopoverEmployment_new'/> <a href="${knowledgeBaseUri}/articles/115483" target="manage_affiliations_settings.helpPopoverEmployment"><@orcid.msg 'common.learn_more'/></a></p>
                                </div>
                            </div>
                        </div>
                        </#if>                     
                    </div>
                    <div class="col-md-6 col-sm-6 col-xs-12 action-button-bar" *ngIf="workspaceSrvc.displayEmployment">
                        <#escape x as x?html>                        
                        <div class="menu-container">                                     
                            <ul class="toggle-menu">
                                <li>
                                    <span class="glyphicon glyphicon-sort"></span>                          
                                    <@orcid.msg 'manual_orcid_record_contents.sort'/>
                                    <ul class="menu-options sort">
                                        <li [ngClass]="{'checked':sortState.predicateKey=='startDate'}">                                         
                                            <a (click)="sort('startDate');" class="action-option manage-button">
                                                <@orcid.msg 'manual_orcid_record_contents.sort_start_date'/>
                                                <span *ngIf="sortState.reverseKey['startDate']" [ngClass]="{'glyphicon glyphicon-sort-by-order-alt':sortState.predicateKey=='startDate'}"></span>
                                                <span *ngIf="sortState.reverseKey['startDate'] == false" [ngClass]="{'glyphicon glyphicon-sort-by-order':sortState.predicateKey=='startDate'}"></span>
                                            </a>                                                                                    
                                        </li>
                                        <li [ngClass]="{'checked':sortState.predicateKey=='endDate'}">
                                            <a (click)="sort('endDate');" class="action-option manage-button">
                                                <@orcid.msg 'manual_orcid_record_contents.sort_end_date'/>
                                                <span *ngIf="sortState.reverseKey['endDate']" [ngClass]="{'glyphicon glyphicon-sort-by-alphabet-alt':sortState.predicateKey=='endDate'}" ></span>
                                                <span *ngIf="sortState.reverseKey['endDate'] == false" [ngClass]="{'glyphicon glyphicon-sort-by-alphabet':sortState.predicateKey=='endDate'}" ></span>
                                            </a>                                            
                                        </li>
                                        <li [ngClass]="{'checked':sortState.predicateKey=='title'}">                                            
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
                        </#escape>
                        <#if !(isPublicProfile??)>
                        <ul class="workspace-bar-menu">                            
                            <li class="hidden-xs">                  
                                <div class="menu-container" id="add-employment-container">
                                    <ul class="toggle-menu">
                                        <li [ngClass]="{'green-bg' : showBibtexImportWizard == true}">       
                                            <span class="glyphicon glyphicon-plus"></span>
                                            <@orcid.msgCapFirst 'manual_affiliation_form_contents.add_employment' />    
                                            <ul class="menu-options employment">                                                
                                                <li>                            
                                                    <a id="add-employment" href="" class="action-option manage-button two-options" (click)="addAffiliationModal('employment')">
                                                        <span class="glyphicon glyphicon-plus"></span>
                                                        <@orcid.msg 'manual_orcid_record_contents.link_manually'/>
                                                    </a>            
                                                </li>
                                            </ul>
                                        </li>
                                    </ul>
                                </div>         
                            </li>                            
                            <li class="hidden-md hidden-sm visible-xs-inline">                     
                                <a href="" class="action-option manage-button two-options" (click)="addAffiliationModal('employment')">
                                    <span class="glyphicon glyphicon-plus"></span>
                                    <@orcid.msg 'manual_orcid_record_contents.link_manually'/>
                                </a>                
                            </li>
                        </ul>
                        </#if>                                   
                    </div>
                </div>
            </div>                        
            <div *ngIf="workspaceSrvc.displayEmployment" class="workspace-accordion-content">
                <div id="employments-empty" *ngIf="!employments?.length">
                    <strong>
                        <#if (publicProfile)?? && publicProfile == true>
                            <strong><@orcid.msg 'workspace_affiliations_body_list.Noemploymentddedyet' /></strong>
                        <#else>
                            <strong>                                
                            <@orcid.msg 'workspace_affiliations_body_list.havenotaddedany' />
                            <a href="" (click)="addAffiliationModal('employment')"><@orcid.msg 'workspace_affiliations_body_list.addemploymentnow' /></a>
                            <@orcid.msg 'common.now' />                                
                            </strong>
                        </#if>
                    </strong>
                </div>
                <ul id="employments-list" *ngIf="employments?.length > 0" class="workspace-affiliations workspace-body-list bottom-margin-medium">
                    <li class="bottom-margin-small workspace-border-box affiliation-box card" *ngFor="let group of employments | orderBy: sortState.predicate:sortState.reverse" [attr.employment-put-code]="group.activities[group?.activePutCode].putCode.value">
                        <#include "affiliation-details-ng2.ftl"/>                      
                    </li>
                </ul>
            </div>                                                  
        </div>        
        <!-- EDUCATION AND QUALIFICATION -->
        <div id="workspace-education" class="workspace-accordion-item workspace-accordion-active" [hidden]="publicView == 'true' && sectionOneElements.length < 1">
            <div class="workspace-accordion-header clearfix">
                <div class="row">                    
                    <div *ngIf="displayNewAffiliationTypesFeatureEnabled">
                        <div class="col-md-6 col-sm-6 col-xs-12">
                            <a name='workspace-educations-qualifications'></a>
                            <h2 (click)="workspaceSrvc.toggleEducation();workspaceSrvc.toggleEducationAndQualification();" class="toggle-text">
                                <i class="glyphicon-chevron-down glyphicon x075" [ngClass]="{ 'glyphicon-chevron-right': displayEducationAndQualification()==false }"></i>                               
                                <@orcid.msg 'org.orcid.jaxb.model.message.AffiliationType.education_qualification'/> (<span>{{sectionOneElements.length}}</span>)
                            </h2>    
                            <#if !(isPublicProfile??)> 
                                <div class="popover-help-container">
                                    <i class="glyphicon glyphicon-question-sign"></i>
                                    <div id="education-qualification-help" class="popover bottom">
                                        <div class="arrow"></div>
                                        <div class="popover-content">
                                            <p><@orcid.msg 'manage_affiliations_settings.helpPopoverEducationAndQualification'/> <a href="${knowledgeBaseUri}/articles/115483" target="manage_affiliations_settings.helpPopoverEducationAndQualification"><@orcid.msg 'common.learn_more'/></a></p>
                                        </div>
                                    </div>
                                </div>  
                            </#if>
                        </div>
                        <div class="col-md-6 col-sm-6 col-xs-12 action-button-bar" *ngIf="displayEducationAndQualification()">
                            <#escape x as x?html>
                                <div class="menu-container">   
                                    <ul class="toggle-menu">
                                        <li>
                                            <span class="glyphicon glyphicon-sort"></span>                          
                                            <@orcid.msg 'manual_orcid_record_contents.sort'/>
                                            <ul class="menu-options sort">
                                                <li [ngClass]="{'checked':sortState.predicateKey=='startDate'}">                                         
                                                    <a (click)="sort('startDate');" class="action-option manage-button">
                                                        <@orcid.msg 'manual_orcid_record_contents.sort_start_date'/>
                                                        <span *ngIf="sortState.reverseKey['startDate']" [ngClass]="{'glyphicon glyphicon-sort-by-order-alt':sortState.predicateKey=='startDate'}"></span>
                                                        <span *ngIf="sortState.reverseKey['startDate'] == false" [ngClass]="{'glyphicon glyphicon-sort-by-order':sortState.predicateKey=='startDate'}"></span>
                                                    </a>                                                                                    
                                                </li>
                                                <li [ngClass]="{'checked':sortState.predicateKey=='endDate'}">
                                                    <a (click)="sort('endDate');" class="action-option manage-button">
                                                        <@orcid.msg 'manual_orcid_record_contents.sort_end_date'/>
                                                        <span *ngIf="sortState.reverseKey['endDate']" [ngClass]="{'glyphicon glyphicon-sort-by-alphabet-alt':sortState.predicateKey=='endDate'}" ></span>
                                                        <span *ngIf="sortState.reverseKey['endDate'] == false" [ngClass]="{'glyphicon glyphicon-sort-by-alphabet':sortState.predicateKey=='endDate'}" ></span>
                                                    </a>                                            
                                                </li>
                                                <li [ngClass]="{'checked':sortState.predicateKey=='title'}">                                            
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
                            </#escape>               
                            <#if !(isPublicProfile??)>
                                <ul class="workspace-bar-menu">      
                                    <li class="hidden-xs">                  
                                        <div class="menu-container" id="add-education-container">
                                            <ul class="toggle-menu">
                                                <li [ngClass]="{'green-bg' : showBibtexImportWizard == true}">       
                                                    <span class="glyphicon glyphicon-plus"></span>
                                                    <@orcid.msgCapFirst 'manual_affiliation_form_contents.add_education'/>    
                                                    <ul class="menu-options education">
                                                        <li>          
                                                            <a id="add-education" href="" class="action-option manage-button two-options" (click)="addAffiliationModal('education')">
                                                                <span class="glyphicon glyphicon-plus"></span>
                                                                <@orcid.msg 'manual_orcid_record_contents.link_manually'/>
                                                            </a>
                                                        </li>
                                                    </ul>
                                                </li>
                                            </ul>
                                        </div>         
                                    </li>
                                    <li class="hidden-md hidden-sm visible-xs-inline">          
                                        <a href="" class="action-option manage-button two-options" (click)="addAffiliationModal('education')">
                                            <span class="glyphicon glyphicon-plus"></span>
                                            <@orcid.msg 'manual_orcid_record_contents.link_manually'/>
                                        </a>
                                    </li>
                                </ul>
                                <ul class="workspace-bar-menu">      
                                    <li class="hidden-xs">                  
                                        <div class="menu-container" id="add-qualification-container">
                                            <ul class="toggle-menu">
                                                <li [ngClass]="{'green-bg' : showBibtexImportWizard == true}">       
                                                    <span class="glyphicon glyphicon-plus"></span>
                                                    <@orcid.msgCapFirst 'manual_affiliation_form_contents.add_qualification'/>    
                                                    <ul class="menu-options qualification">
                                                        <li>          
                                                            <a id="add-qualification" href="" class="action-option manage-button two-options" (click)="addAffiliationModal('qualification')">
                                                                <span class="glyphicon glyphicon-plus"></span>
                                                                <@orcid.msg 'manual_orcid_record_contents.link_manually'/>
                                                            </a>
                                                        </li>
                                                    </ul>
                                                </li>
                                            </ul>
                                        </div>         
                                    </li>
                                    <li class="hidden-md hidden-sm visible-xs-inline">          
                                        <a href="" class="action-option manage-button two-options" (click)="addAffiliationModal('qualification')">
                                            <span class="glyphicon glyphicon-plus"></span>
                                            <@orcid.msg 'manual_orcid_record_contents.link_manually'/>
                                        </a>
                                    </li>
                                </ul>
                            </#if>
                        </div>
                    </div>
                    <div *ngIf="!displayNewAffiliationTypesFeatureEnabled">
                        <div class="col-md-3 col-sm-3 col-xs-12">
                            <a name='workspace-educations'></a>
                            <h2 (click)="toggleEducation()" class="toggle-text">
                                <i class="glyphicon-chevron-down glyphicon x075" [ngClass]="{ 'glyphicon-chevron-right': displayEducation()==false }"></i>                               
                                <@orcid.msg 'org.orcid.jaxb.model.message.AffiliationType.education'/> (<span>{{educations.length}}</span>)
                            </h2>    
                            <#if !(isPublicProfile??)> 
                                <div class="popover-help-container">
                                    <i class="glyphicon glyphicon-question-sign"></i>
                                    <div id="education-help" class="popover bottom">
                                        <div class="arrow"></div>
                                        <div class="popover-content">
                                            <p><@orcid.msg 'manage_affiliations_settings.helpPopoverEducation'/> <a href="${knowledgeBaseUri}/articles/1807522" target="manage_affiliations_settings.helpPopoverEducation"><@orcid.msg 'common.learn_more'/></a></p>
                                        </div>
                                    </div>
                                </div>  
                            </#if>
                        </div>
                        <div class="col-md-9 col-sm-9 col-xs-12 action-button-bar" *ngIf="displayEducationAndQualification()">
                            <#escape x as x?html>
                            <div class="menu-container">   
                                <ul class="toggle-menu">
                                    <li>
                                        <span class="glyphicon glyphicon-sort"></span>                          
                                        <@orcid.msg 'manual_orcid_record_contents.sort'/>
                                        <ul class="menu-options sort">
                                            <li [ngClass]="{'checked':sortState.predicateKey=='startDate'}">                                         
                                                <a (click)="sort('startDate', true);" class="action-option manage-button">
                                                    <@orcid.msg 'manual_orcid_record_contents.sort_start_date'/>
                                                    <span *ngIf="sortState.reverseKey['startDate']" [ngClass]="{'glyphicon glyphicon-sort-by-order-alt':sortState.predicateKey=='startDate'}"></span>
                                                    <span *ngIf="sortState.reverseKey['startDate'] == false" [ngClass]="{'glyphicon glyphicon-sort-by-order':sortState.predicateKey=='startDate'}"></span>
                                                </a>                                                                                    
                                            </li>
                                            <li [ngClass]="{'checked':sortState.predicateKey=='endDate'}">
                                                <a (click)="sort('endDate', false);" class="action-option manage-button">
                                                    <@orcid.msg 'manual_orcid_record_contents.sort_end_date'/>
                                                    <span *ngIf="sortState.reverseKey['endDate']" [ngClass]="{'glyphicon glyphicon-sort-by-alphabet-alt':sortState.predicateKey=='endDate'}" ></span>
                                                    <span *ngIf="sortState.reverseKey['endDate'] == false" [ngClass]="{'glyphicon glyphicon-sort-by-alphabet':sortState.predicateKey=='endDate'}" ></span>
                                                </a>                                            
                                            </li>
                                            <li [ngClass]="{'checked':sortState.predicateKey=='title'}">                                            
                                                <a (click)="sort('title', true);" class="action-option manage-button">
                                                    <@orcid.msg 'manual_orcid_record_contents.sort_title'/>
                                                    <span *ngIf="sortState.reverseKey['title']" [ngClass]="{'glyphicon glyphicon-sort-by-alphabet-alt':sortState.predicateKey=='title'}" ></span>
                                                    <span *ngIf="sortState.reverseKey['title'] == false" [ngClass]="{'glyphicon glyphicon-sort-by-alphabet':sortState.predicateKey=='title'}" ></span>
                                                </a>                                            
                                            </li>                                            
                                        </ul>
                                    </li>
                                </ul>                                   
                            </div>
                            </#escape>               
                            <#if !(isPublicProfile??)>
                                <ul class="workspace-bar-menu">      
                                    <li class="hidden-xs">                  
                                        <div class="menu-container" id="add-education-container">
                                            <ul class="toggle-menu">
                                                <li [ngClass]="{'green-bg' : showBibtexImportWizard == true}">       
                                                    <span class="glyphicon glyphicon-plus"></span>
                                                    <@orcid.msgCapFirst 'manual_affiliation_form_contents.add_education'/>    
                                                    <ul class="menu-options education">
                                                        <li>          
                                                            <a id="add-education" href="" class="action-option manage-button two-options" (click)="addAffiliationModal('education')">
                                                                <span class="glyphicon glyphicon-plus"></span>
                                                                <@orcid.msg 'manual_orcid_record_contents.link_manually'/>
                                                            </a>
                                                        </li>
                                                    </ul>
                                                </li>
                                            </ul>
                                        </div>         
                                    </li>
                                    <li class="hidden-md hidden-sm visible-xs-inline">          
                                        <a href="" class="action-option manage-button two-options" (click)="addAffiliationModal('education')">
                                            <span class="glyphicon glyphicon-plus"></span>
                                            <@orcid.msg 'manual_orcid_record_contents.link_manually'/>
                                        </a>
                                    </li>                                        
                                </ul>
                            </#if>
                        </div>
                    </div>
                </div>
            </div>            
            <div *ngIf="workspaceSrvc.displayEducation || workspaceSrvc.displayEducationAndQualification" class="workspace-accordion-content"> 
                <div id="educations-distinctions-empty" *ngIf="!sectionOneElements?.length && workspaceSrvc?.displayEducationAndQualification">
                    <strong>
                        <#if (publicProfile)?? && publicProfile == true>
                            <strong><@orcid.msg 'workspace_affiliations_body_list.Noeducationnorqualificationaddedyet' /></strong>
                        <#else>
                            <strong>                                
                            <@orcid.msg 'workspace_affiliations_body_list.havenotaddedany' />
                            <a href="" (click)="addAffiliationModal('education')"><@orcid.msg 'workspace_affiliations_body_list.addeducationnow' /></a>
                            <@orcid.msg 'common.or' />
                            <a href="" (click)="addAffiliationModal('qualification')"><@orcid.msg 'workspace_affiliations_body_list.addqualificationnow' /></a>
                            <@orcid.msg 'common.now' />                                
                            </strong>
                        </#if>
                    </strong>
                </div>
                <ul id="educations-distinctions-list" *ngIf="sectionOneElements?.length" class="workspace-affiliations workspace-body-list bottom-margin-medium">
                    <li class="bottom-margin-small workspace-border-box affiliation-box card ng-scope" *ngFor="let group of sectionOneElements | orderBy: sortState.predicate:sortState.reverse" [attr.education-distinction-put-code]="group.activities[group?.activePutCode].putCode.value">
                        <#include "affiliation-details-ng2.ftl"/> 
                    </li>
                </ul>
            </div>
        </div>
        <!-- INVITED POSITION AND DISTINCTION -->
        <div *ngIf="displayNewAffiliationTypesFeatureEnabled">
            <div id="workspace-distinction-invited-position" class="workspace-accordion-item workspace-accordion-active" [hidden]="publicView == 'true' && distinctionsAndInvitedPositions.length < 1">
                <div class="workspace-accordion-header clearfix">
                    <div class="row">
                        <div class="col-md-6 col-sm-6 col-xs-12">
                            <a name='workspace-distinction-invited-position'></a>
                            <h2 (click)="workspaceSrvc.toggleDistinctionAndInvitedPosition($event)" class="toggle-text">
                                <i class="glyphicon-chevron-down glyphicon x075" [ngClass]="{'glyphicon-chevron-right':workspaceSrvc.displayDistinctionAndInvitedPosition==false}"></i>
                                <@orcid.msg 'org.orcid.jaxb.model.message.AffiliationType.distinction_invited_position'/> (<span>{{distinctionsAndInvitedPositions.length}}</span>)
                            </h2>
                            <#if !(isPublicProfile??)> 
                            <div class="popover-help-container">
                                <i class="glyphicon glyphicon-question-sign"></i>
                                <div id="distinction-invited-position-help" class="popover bottom">
                                    <div class="arrow"></div>
                                    <div class="popover-content">
                                        <p><@orcid.msg 'manage_affiliations_settings.helpPopoverDistinctionAndInvitedPosition'/> <a href="${knowledgeBaseUri}/articles/115486" target="manage_affiliations_settings.helpPopoverDistinctionAndInvitedPosition"><@orcid.msg 'common.learn_more'/></a></p>
                                    </div>
                                </div>
                            </div>
                            </#if>                     
                        </div>
                        <div class="col-md-6 col-sm-6 col-xs-12 action-button-bar" *ngIf="workspaceSrvc.displayDistinctionAndInvitedPosition">
                            <#escape x as x?html>                            
                            <div class="menu-container">                                     
                                <ul class="toggle-menu">
                                    <li>
                                        <span class="glyphicon glyphicon-sort"></span>                          
                                        <@orcid.msg 'manual_orcid_record_contents.sort'/>
                                        <ul class="menu-options sort">
                                            <li [ngClass]="{'checked':sortState.predicateKey=='startDate'}">                                         
                                                <a (click)="sort('startDate');" class="action-option manage-button">
                                                    <@orcid.msg 'manual_orcid_record_contents.sort_start_date'/>
                                                    <span *ngIf="sortState.reverseKey['startDate']" [ngClass]="{'glyphicon glyphicon-sort-by-order-alt':sortState.predicateKey=='startDate'}"></span>
                                                    <span *ngIf="sortState.reverseKey['startDate'] == false" [ngClass]="{'glyphicon glyphicon-sort-by-order':sortState.predicateKey=='startDate'}"></span>
                                                </a>
                                            </li>
                                            <li [ngClass]="{'checked':sortState.predicateKey=='endDate'}">
                                                <a (click)="sort('endDate');" class="action-option manage-button">
                                                    <@orcid.msg 'manual_orcid_record_contents.sort_end_date'/>
                                                    <span *ngIf="sortState.reverseKey['endDate']" [ngClass]="{'glyphicon glyphicon-sort-by-alphabet-alt':sortState.predicateKey=='endDate'}" ></span>
                                                    <span *ngIf="sortState.reverseKey['endDate'] == false" [ngClass]="{'glyphicon glyphicon-sort-by-alphabet':sortState.predicateKey=='endDate'}" ></span>
                                                </a>                                            
                                            </li>
                                            <li [ngClass]="{'checked':sortState.predicateKey=='title'}">                                            
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
                            </#escape>                                           
                            <#if !(isPublicProfile??)>
                                <ul class="workspace-bar-menu">                                
                                    <li class="hidden-xs">                  
                                        <div class="menu-container" id="add-distinction-container">
                                            <ul class="toggle-menu">
                                                <li [ngClass]="{'green-bg' : showBibtexImportWizard == true}">       
                                                    <span class="glyphicon glyphicon-plus"></span>
                                                    <@orcid.msgCapFirst 'manual_affiliation_form_contents.add_distinction' />
                                                    <ul class="menu-options distinction">                                                    
                                                        <li>                            
                                                            <a id="add-distinction" href="" class="action-option manage-button two-options" (click)="addAffiliationModal('distinction')">
                                                                <span class="glyphicon glyphicon-plus"></span>
                                                                <@orcid.msg 'manual_orcid_record_contents.link_manually'/>
                                                            </a>            
                                                        </li>
                                                    </ul>
                                                </li>
                                            </ul>
                                        </div>
                                    </li>                                    
                                    <li class="hidden-md hidden-sm visible-xs-inline">                     
                                        <a href="" class="action-option manage-button two-options" (click)="addAffiliationModal('distinction')">
                                            <span class="glyphicon glyphicon-plus"></span>
                                            <@orcid.msg 'manual_orcid_record_contents.link_manually'/>
                                        </a>                
                                    </li>
                                </ul>
                                <ul class="workspace-bar-menu">                                
                                    <li class="hidden-xs">                  
                                        <div class="menu-container" id="add-invited-position-container">
                                            <ul class="toggle-menu">
                                                <li [ngClass]="{'green-bg' : showBibtexImportWizard == true}">       
                                                    <span class="glyphicon glyphicon-plus"></span>
                                                    <@orcid.msgCapFirst 'manual_affiliation_form_contents.add_invited_position' />
                                                    <ul class="menu-options invited-position">                                                    
                                                        <li>                            
                                                            <a id="add-distinction" href="" class="action-option manage-button two-options" (click)="addAffiliationModal('invited-position')">
                                                                <span class="glyphicon glyphicon-plus"></span>
                                                                <@orcid.msg 'manual_orcid_record_contents.link_manually'/>
                                                            </a>            
                                                        </li>
                                                    </ul>
                                                </li>
                                            </ul>
                                        </div>         
                                    </li>                                   
                                    <li class="hidden-md hidden-sm visible-xs-inline">                     
                                        <a href="" class="action-option manage-button two-options" (click)="addAffiliationModal('invited-position')">
                                            <span class="glyphicon glyphicon-plus"></span>
                                            <@orcid.msg 'manual_orcid_record_contents.link_manually'/>
                                        </a>                
                                    </li>
                                </ul>
                            </#if>                                        
                        </div>
                    </div>
                </div>                        
                <div *ngIf="workspaceSrvc.displayDistinctionAndInvitedPosition" class="workspace-accordion-content">
                    <div id="distinction-invited-position-empty" *ngIf="!distinctionsAndInvitedPositions?.length">
                        <strong>
                            <#if (publicProfile)?? && publicProfile == true>
                                <strong><@orcid.msg 'workspace_affiliations_body_list.Nodistinctionorinvitedpositionaddedyet' /></strong>
                            <#else>
                                <strong>                                
                                <@orcid.msg 'workspace_affiliations_body_list.havenotaddedany' />
                                <a href="" (click)="addAffiliationModal('distinction')"><@orcid.msg 'workspace_affiliations_body_list.adddistinctionnow' /></a>
                                <@orcid.msg 'common.or' />
                                <a href="" (click)="addAffiliationModal('invited-position')"><@orcid.msg 'workspace_affiliations_body_list.addinvitedpositionnow' /></a>
                                <@orcid.msg 'common.now' /> 
                                </strong>
                            </#if>
                        </strong>
                    </div>
                    <ul id="distinction-invited-position-list" *ngIf="distinctionsAndInvitedPositions?.length > 0" class="workspace-affiliations workspace-body-list bottom-margin-medium">
                        <li class="bottom-margin-small workspace-border-box affiliation-box card" *ngFor="let group of distinctionsAndInvitedPositions | orderBy: sortState.predicate:sortState.reverse" [attr.distinction-invited-position-put-code]="group.activities[group?.activePutCode].putCode.value">
                            <#include "affiliation-details-ng2.ftl"/>          
                        </li>
                    </ul>
                </div>                                                  
            </div>
        </div>        
        <!-- MEMBERSHIP AND SERVICE -->
        <div *ngIf="displayNewAffiliationTypesFeatureEnabled">
            <div id="workspace-membership-service" class="workspace-accordion-item workspace-accordion-active" [hidden]="publicView == 'true' && membershipsAndServices.length < 1">
                <div class="workspace-accordion-header clearfix">
                    <div class="row">
                        <div class="col-md-6 col-sm-6 col-xs-12">
                            <a name='workspace-membership-service'></a>
                            <h2 (click)="workspaceSrvc.toggleMembershipAndService()" class="toggle-text">
                                <i class="glyphicon-chevron-down glyphicon x075" [ngClass]="{'glyphicon-chevron-right':workspaceSrvc.displayMembershipAndService==false}"></i>
                                <@orcid.msg 'org.orcid.jaxb.model.message.AffiliationType.membership_service'/> (<span>{{membershipsAndServices.length}}</span>)
                            </h2>
                            <#if !(isPublicProfile??)> 
                            <div class="popover-help-container">
                                <i class="glyphicon glyphicon-question-sign"></i>
                                <div id="membership-service-help" class="popover bottom">
                                    <div class="arrow"></div>
                                    <div class="popover-content">
                                        <p><@orcid.msg 'manage_affiliations_settings.helpPopoverMembershipAndService'/> <a href="${knowledgeBaseUri}/articles/116752" target="manage_affiliations_settings.helpPopoverMembershipAndService"><@orcid.msg 'common.learn_more'/></a></p>
                                    </div>
                                </div>
                            </div>
                            </#if>                     
                        </div>
                        <div class="col-md-6 col-sm-6 col-xs-12 action-button-bar" *ngIf="workspaceSrvc.displayMembershipAndService">
                            <#escape x as x?html>                            
                            <div class="menu-container">                                     
                                <ul class="toggle-menu">
                                    <li>
                                        <span class="glyphicon glyphicon-sort"></span>                          
                                        <@orcid.msg 'manual_orcid_record_contents.sort'/>
                                        <ul class="menu-options sort">
                                            <li [ngClass]="{'checked':sortState.predicateKey=='startDate'}">                                         
                                                <a (click)="sort('startDate');" class="action-option manage-button">
                                                    <@orcid.msg 'manual_orcid_record_contents.sort_start_date'/>
                                                    <span *ngIf="sortState.reverseKey['startDate']" [ngClass]="{'glyphicon glyphicon-sort-by-order-alt':sortState.predicateKey=='startDate'}"></span>
                                                    <span *ngIf="sortState.reverseKey['startDate'] == false" [ngClass]="{'glyphicon glyphicon-sort-by-order':sortState.predicateKey=='startDate'}"></span>
                                                </a>                                                                                    
                                            </li>
                                            <li [ngClass]="{'checked':sortState.predicateKey=='endDate'}">
                                                <a (click)="sort('endDate');" class="action-option manage-button">
                                                    <@orcid.msg 'manual_orcid_record_contents.sort_end_date'/>
                                                    <span *ngIf="sortState.reverseKey['endDate']" [ngClass]="{'glyphicon glyphicon-sort-by-alphabet-alt':sortState.predicateKey=='endDate'}" ></span>
                                                    <span *ngIf="sortState.reverseKey['endDate'] == false" [ngClass]="{'glyphicon glyphicon-sort-by-alphabet':sortState.predicateKey=='endDate'}" ></span>
                                                </a>                                            
                                            </li>
                                            <li [ngClass]="{'checked':sortState.predicateKey=='title'}">                                            
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
                            </#escape>                                           
                            <#if !(isPublicProfile??)>
                                <ul class="workspace-bar-menu">                                
                                    <li class="hidden-xs">                  
                                        <div class="menu-container" id="add-membership-container">
                                            <ul class="toggle-menu">
                                                <li [ngClass]="{'green-bg' : showBibtexImportWizard == true}">       
                                                    <span class="glyphicon glyphicon-plus"></span>
                                                    <@orcid.msgCapFirst 'manual_affiliation_form_contents.add_membership' />
                                                    <ul class="menu-options distinction">                                                    
                                                        <li>                            
                                                            <a id="add-membership" href="" class="action-option manage-button two-options" (click)="addAffiliationModal('membership')">
                                                                <span class="glyphicon glyphicon-plus"></span>
                                                                <@orcid.msg 'manual_orcid_record_contents.link_manually'/>
                                                            </a>            
                                                        </li>
                                                    </ul>
                                                </li>
                                            </ul>
                                        </div>
                                    </li>                                    
                                    <li class="hidden-md hidden-sm visible-xs-inline">                     
                                        <a href="" class="action-option manage-button two-options" (click)="addAffiliationModal('membership')">
                                            <span class="glyphicon glyphicon-plus"></span>
                                            <@orcid.msg 'manual_orcid_record_contents.link_manually'/>
                                        </a>                
                                    </li>
                                </ul>
                                <ul class="workspace-bar-menu">                                
                                    <li class="hidden-xs">                  
                                        <div class="menu-container" id="add-service-container">
                                            <ul class="toggle-menu">
                                                <li [ngClass]="{'green-bg' : showBibtexImportWizard == true}">       
                                                    <span class="glyphicon glyphicon-plus"></span>
                                                    <@orcid.msgCapFirst 'manual_affiliation_form_contents.add_service' />
                                                    <ul class="menu-options invited-position">                                                    
                                                        <li>                            
                                                            <a id="add-service" href="" class="action-option manage-button two-options" (click)="addAffiliationModal('service')">
                                                                <span class="glyphicon glyphicon-plus"></span>
                                                                <@orcid.msg 'manual_orcid_record_contents.link_manually'/>
                                                            </a>            
                                                        </li>
                                                    </ul>
                                                </li>
                                            </ul>
                                        </div>         
                                    </li>                                   
                                    <li class="hidden-md hidden-sm visible-xs-inline">                     
                                        <a href="" class="action-option manage-button two-options" (click)="addAffiliationModal('service')">
                                            <span class="glyphicon glyphicon-plus"></span>
                                            <@orcid.msg 'manual_orcid_record_contents.link_manually'/>
                                        </a>                
                                    </li>
                                </ul>
                            </#if>                                        
                        </div>
                    </div>
                </div>                        
                <div *ngIf="workspaceSrvc.displayMembershipAndService" class="workspace-accordion-content">
                    <div id="membership-service-empty" *ngIf="!membershipsAndServices?.length">
                        <strong>
                            <#if (publicProfile)?? && publicProfile == true>
                                <strong><@orcid.msg 'workspace_affiliations_body_list.Nomembershiporserviceaddedyet' /></strong>
                            <#else>
                                <strong>                                
                                <@orcid.msg 'workspace_affiliations_body_list.havenotaddedany' />
                                <a href="" (click)="addAffiliationModal('membership')"><@orcid.msg 'workspace_affiliations_body_list.addmembershipnow' /></a>
                                <@orcid.msg 'common.or' />
                                <a href="" (click)="addAffiliationModal('service')"><@orcid.msg 'workspace_affiliations_body_list.addservicenow' /></a>
                                <@orcid.msg 'common.now' />                                
                                </strong>
                            </#if>
                        </strong>
                    </div>
                    <ul id="membership-service-list" *ngIf="membershipsAndServices?.length > 0" class="workspace-affiliations workspace-body-list bottom-margin-medium">
                        <li class="bottom-margin-small workspace-border-box affiliation-box card" *ngFor="let group of membershipsAndServices | orderBy: sortState.predicate:sortState.reverse" [attr.membership-service-put-code]="group.activities[group?.activePutCode].putCode.value">
                            <#include "affiliation-details-ng2.ftl"/>            
                        </li>
                    </ul>
                </div>                                                  
            </div>
        </div>
        
    </div>
</script>