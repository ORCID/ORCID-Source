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
                                        <li [ngClass]="{'checked':sortDisplayKeyEmployments=='startDate'}">                                         
                                            <a (click)="sort('employment', 'startDate', true);" class="action-option manage-button">
                                                <@orcid.msg 'manual_orcid_record_contents.sort_start_date'/>
                                                <span *ngIf="sortDisplayKeyEmployments=='startDate' && sortAscEmployments==false" [ngClass]="{'glyphicon glyphicon-sort-by-order-alt':sortDisplayKeyEmployments=='startDate'}"></span>
                                                <span *ngIf="sortDisplayKeyEmployments=='startDate' && sortAscEmployments==true" [ngClass]="{'glyphicon glyphicon-sort-by-order':sortDisplayKeyEmployments=='startDate'}"></span>
                                            </a>                                                                                    
                                        </li>
                                        <li [ngClass]="{'checked':sortDisplayKeyEmployments=='endDate'}">
                                            <a (click)="sort('employment', 'endDate', true);" class="action-option manage-button">
                                                <@orcid.msg 'manual_orcid_record_contents.sort_end_date'/>
                                                <span *ngIf="sortDisplayKeyEmployments=='endDate' && sortAscEmployments==false" [ngClass]="{'glyphicon glyphicon-sort-by-alphabet-alt':sortDisplayKeyEmployments=='endDate'}" ></span>
                                                <span *ngIf="sortDisplayKeyEmployments=='endDate' && sortAscEmployments==true" [ngClass]="{'glyphicon glyphicon-sort-by-alphabet':sortDisplayKeyEmployments=='endDate'}" ></span>
                                            </a>                                            
                                        </li>
                                        <li [ngClass]="{'checked':sortDisplayKeyEmployments=='title'}">                                            
                                            <a (click)="sort('employment', 'title', true);" class="action-option manage-button">
                                                <@orcid.msg 'manual_orcid_record_contents.sort_title'/>
                                                <span *ngIf="sortDisplayKeyEmployments=='title' && sortAscEmployments==false" [ngClass]="{'glyphicon glyphicon-sort-by-alphabet-alt':sortDisplayKeyEmployments=='title'}" ></span>
                                                <span *ngIf="sortDisplayKeyEmployments=='title' && sortAscEmployments==true" [ngClass]="{'glyphicon glyphicon-sort-by-alphabet':sortDisplayKeyEmployments=='title'}" ></span>
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
                    <li class="bottom-margin-small workspace-border-box affiliation-box card" *ngFor="let group of (employments | orderBy: sortKeyEmployments:sortAscEmployments)" [attr.employment-put-code]="group.activities[group?.activePutCode].putCode.value">
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
                                                <li [ngClass]="{'checked':sortDisplayKeyEducations=='startDate'}">                                         
                                                    <a (click)="sort('education', 'startDate', true);" class="action-option manage-button">
                                                        <@orcid.msg 'manual_orcid_record_contents.sort_start_date'/>
                                                        <span *ngIf="sortDisplayKeyEducations=='startDate' && sortAscEducations==false" [ngClass]="{'glyphicon glyphicon-sort-by-order-alt':sortDisplayKeyEducations=='startDate'}"></span>
                                                        <span *ngIf="sortDisplayKeyEducations=='startDate' && sortAscEducations==true" [ngClass]="{'glyphicon glyphicon-sort-by-order':sortDisplayKeyEducations=='startDate'}"></span>
                                                    </a>                                                                                    
                                                </li>
                                                <li [ngClass]="{'checked':sortDisplayKeyEducations=='endDate'}">
                                                    <a (click)="sort('education', 'endDate', true);" class="action-option manage-button">
                                                        <@orcid.msg 'manual_orcid_record_contents.sort_end_date'/>
                                                        <span *ngIf="sortDisplayKeyEducations=='endDate' && sortAscEducations==false" [ngClass]="{'glyphicon glyphicon-sort-by-alphabet-alt':sortDisplayKeyEducations=='endDate'}" ></span>
                                                        <span *ngIf="sortDisplayKeyEducations=='endDate' && sortAscEducations==true" [ngClass]="{'glyphicon glyphicon-sort-by-alphabet':sortDisplayKeyEducations=='endDate'}" ></span>
                                                    </a>                                            
                                                </li>
                                                <li [ngClass]="{'checked':sortDisplayKeyEducations=='title'}">                                            
                                                    <a (click)="sort('education', 'title', true);" class="action-option manage-button">
                                                        <@orcid.msg 'manual_orcid_record_contents.sort_title'/>
                                                        <span *ngIf="sortDisplayKeyEducations=='title' && sortAscEducations==false" [ngClass]="{'glyphicon glyphicon-sort-by-alphabet-alt':sortDisplayKeyEducations=='title'}" ></span>
                                                        <span *ngIf="sortDisplayKeyEducations=='title' && sortAscEducations==true" [ngClass]="{'glyphicon glyphicon-sort-by-alphabet':sortDisplayKeyEducations=='title'}" ></span>
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
                                            <li [ngClass]="{'checked':sortDisplayKeyEducations=='startDate'}">                                         
                                                <a (click)="sort('education', 'startDate', true);" class="action-option manage-button">
                                                    <@orcid.msg 'manual_orcid_record_contents.sort_start_date'/>
                                                    <span *ngIf="sortDisplayKeyEducations=='startDate' && sortAscEducations==false" [ngClass]="{'glyphicon glyphicon-sort-by-order-alt':sortDisplayKeyEducations=='startDate'}"></span>
                                                    <span *ngIf="sortDisplayKeyEducations=='startDate' && sortAscEducations==true" [ngClass]="{'glyphicon glyphicon-sort-by-order':sortDisplayKeyEducations=='startDate'}"></span>
                                                </a>                                                                                    
                                            </li>
                                            <li [ngClass]="{'checked':sortDisplayKeyEducations=='endDate'}">
                                                <a (click)="sort('education', 'endDate', true);" class="action-option manage-button">
                                                    <@orcid.msg 'manual_orcid_record_contents.sort_end_date'/>
                                                    <span *ngIf="sortDisplayKeyEducations=='endDate' && sortAscEducations==false" [ngClass]="{'glyphicon glyphicon-sort-by-alphabet-alt':sortDisplayKeyEducations=='endDate'}" ></span>
                                                    <span *ngIf="sortDisplayKeyEducations=='endDate' && sortAscEducations==true" [ngClass]="{'glyphicon glyphicon-sort-by-alphabet':sortDisplayKeyEducations=='endDate'}" ></span>
                                                </a>                                            
                                            </li>
                                            <li [ngClass]="{'checked':sortDisplayKeyEducations=='title'}">                                            
                                                <a (click)="sort('education', 'title', true);" class="action-option manage-button">
                                                    <@orcid.msg 'manual_orcid_record_contents.sort_title'/>
                                                    <span *ngIf="sortDisplayKeyEducations=='title' && sortAscEducations==false" [ngClass]="{'glyphicon glyphicon-sort-by-alphabet-alt':sortDisplayKeyEducations=='title'}" ></span>
                                                    <span *ngIf="sortDisplayKeyEducations=='title' && sortAscEducations==true" [ngClass]="{'glyphicon glyphicon-sort-by-alphabet':sortDisplayKeyEducations=='title'}" ></span>
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
                    <li class="bottom-margin-small workspace-border-box affiliation-box card ng-scope" *ngFor="let group of (sectionOneElements | orderBy: sortKeyEducations:sortAscEducations)" [attr.education-distinction-put-code]="group.activities[group?.activePutCode].putCode.value">
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
                                            <li [ngClass]="{'checked':sortDisplayKeyDistinctions=='startDate'}">                                         
                                                <a (click)="sort('distinction_invited_position', 'startDate', true);" class="action-option manage-button">
                                                    <@orcid.msg 'manual_orcid_record_contents.sort_start_date'/>
                                                    <span *ngIf="sortDisplayKeyDistinctions=='startDate' && sortAscDistinctions==false" [ngClass]="{'glyphicon glyphicon-sort-by-order-alt':sortDisplayKeyDistinctions=='startDate'}"></span>
                                                    <span *ngIf="sortDisplayKeyDistinctions=='startDate' && sortAscDistinctions==true" [ngClass]="{'glyphicon glyphicon-sort-by-order':sortDisplayKeyDistinctions=='startDate'}"></span>
                                                </a>                                                                                    
                                            </li>
                                            <li [ngClass]="{'checked':sortDisplayKeyDistinctions=='endDate'}">
                                                <a (click)="sort('distinction_invited_position', 'endDate', true);" class="action-option manage-button">
                                                    <@orcid.msg 'manual_orcid_record_contents.sort_end_date'/>
                                                    <span *ngIf="sortDisplayKeyDistinctions=='endDate' && sortAscDistinctions==false" [ngClass]="{'glyphicon glyphicon-sort-by-alphabet-alt':sortDisplayKeyDistinctions=='endDate'}" ></span>
                                                    <span *ngIf="sortDisplayKeyDistinctions=='endDate' && sortAscDistinctions==true" [ngClass]="{'glyphicon glyphicon-sort-by-alphabet':sortDisplayKeyDistinctions=='endDate'}" ></span>
                                                </a>                                            
                                            </li>
                                            <li [ngClass]="{'checked':sortDisplayKeyDistinctions=='title'}">                                            
                                                <a (click)="sort('distinction_invited_position', 'title', true);" class="action-option manage-button">
                                                    <@orcid.msg 'manual_orcid_record_contents.sort_title'/>
                                                    <span *ngIf="sortDisplayKeyDistinctions=='title' && sortAscDistinctions==false" [ngClass]="{'glyphicon glyphicon-sort-by-alphabet-alt':sortDisplayKeyDistinctions=='title'}" ></span>
                                                    <span *ngIf="sortDisplayKeyDistinctions=='title' && sortAscDistinctions==true" [ngClass]="{'glyphicon glyphicon-sort-by-alphabet':sortDisplayKeyDistinctions=='title'}" ></span>
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
                        <li class="bottom-margin-small workspace-border-box affiliation-box card" *ngFor="let group of (distinctionsAndInvitedPositions | orderBy: sortKeyDistinctions:sortAscDistinctions)" [attr.distinction-invited-position-put-code]="group.activities[group?.activePutCode].putCode.value">
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
                                            <li [ngClass]="{'checked':sortDisplayKeyMemberships=='startDate'}">                                         
                                                <a (click)="sort('membership_service', 'startDate', true);" class="action-option manage-button">
                                                    <@orcid.msg 'manual_orcid_record_contents.sort_start_date'/>
                                                    <span *ngIf="sortDisplayKeyMemberships=='startDate' && sortAscMemberships==false" [ngClass]="{'glyphicon glyphicon-sort-by-order-alt':sortDisplayKeyMemberships=='startDate'}"></span>
                                                    <span *ngIf="sortDisplayKeyMemberships=='startDate' && sortAscMemberships==true" [ngClass]="{'glyphicon glyphicon-sort-by-order':sortDisplayKeyMemberships=='startDate'}"></span>
                                                </a>                                                                                    
                                            </li>
                                            <li [ngClass]="{'checked':sortDisplayKeyMemberships=='endDate'}">
                                                <a (click)="sort('membership_service', 'endDate', true);" class="action-option manage-button">
                                                    <@orcid.msg 'manual_orcid_record_contents.sort_end_date'/>
                                                    <span *ngIf="sortDisplayKeyMemberships=='endDate' && sortAscMemberships==false" [ngClass]="{'glyphicon glyphicon-sort-by-alphabet-alt':sortDisplayKeyMemberships=='endDate'}" ></span>
                                                    <span *ngIf="sortDisplayKeyMemberships=='endDate' && sortAscMemberships==true" [ngClass]="{'glyphicon glyphicon-sort-by-alphabet':sortDisplayKeyMemberships=='endDate'}" ></span>
                                                </a>                                            
                                            </li>
                                            <li [ngClass]="{'checked':sortDisplayKeyMemberships=='title'}">                                            
                                                <a (click)="sort('membership_service', 'title', true);" class="action-option manage-button">
                                                    <@orcid.msg 'manual_orcid_record_contents.sort_title'/>
                                                    <span *ngIf="sortDisplayKeyMemberships=='title' && sortAscMemberships==false" [ngClass]="{'glyphicon glyphicon-sort-by-alphabet-alt':sortDisplayKeyMemberships=='title'}" ></span>
                                                    <span *ngIf="sortDisplayKeyMemberships=='title' && sortAscMemberships==true" [ngClass]="{'glyphicon glyphicon-sort-by-alphabet':sortDisplayKeyMemberships=='title'}" ></span>
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
                        <li class="bottom-margin-small workspace-border-box affiliation-box card" *ngFor="let group of (membershipsAndServices | orderBy: sortKeyMemberships:sortAscMemberships)" [attr.membership-service-put-code]="group.activities[group?.activePutCode].putCode.value">
                            <#include "affiliation-details-ng2.ftl"/>            
                        </li>
                    </ul>
                </div>                                                  
            </div>
        </div>
        
    </div>
</script>