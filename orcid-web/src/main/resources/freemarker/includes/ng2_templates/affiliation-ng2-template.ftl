<#--

    =============================================================================

    ORCID (R) Open Source
    http://orcid.org

    Copyright (c) 2012-2014 ORCID, Inc.
    Licensed under an MIT-Style License (MIT)
    http://orcid.org/open-source-license

    This copyright and license information (including a link to the full license)
    shall be included in its entirety in all copies or substantial portion of
    the software.

    =============================================================================

-->

<script type="text/ng-template" id="affiliation-ng2-template">
    <div>
        <!-- EMPLOYMENT -->
        <div id="workspace-employment" class="workspace-accordion-item workspace-accordion-active" [hidden]="publicView == 'true' && employments.length < 1">
            <div class="workspace-accordion-header clearfix">
                <div class="row">
                    <div class="col-md-6 col-sm-6 col-xs-12">
                        <a name='workspace-employments'></a>
                        <a href="" (click)="workspaceSrvc.toggleEmployment($event)" class="toggle-text">
                            <i class="glyphicon-chevron-down glyphicon x075" [ngClass]="{'glyphicon-chevron-right':workspaceSrvc.displayEmployment==false}"></i>
                            <@orcid.msg 'org.orcid.jaxb.model.message.AffiliationType.employment'/> (<span>{{employments.length}}</span>)
                        </a>
                        <#if !(isPublicProfile??)> 
                        <div class="popover-help-container">
                            <a href="javascript:void(0);"><i class="glyphicon glyphicon-question-sign"></i></a>
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
                <div id="employments-empty" *ngIf="!employments.length">
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
                <ul id="employments-list" *ngIf="employments.length > 0" class="workspace-affiliations workspace-body-list bottom-margin-medium">
                    <li class="bottom-margin-small workspace-border-box affiliation-box card" *ngFor="let group of employments | orderBy: sortState.predicate:sortState.reverse" [attr.employment-put-code]="group.putCode.value">
                        <div class="row">                
                            <div class="col-md-9 col-sm-9 col-xs-7">
                                <h3 class="workspace-title">            
                                    <span>{{group.affiliationName.value}}</span>:
                                    <span>{{group.city.value}}</span><span *ngIf="group.region.value">, </span><span>{{group.region.value}}</span>, <span>{{group.countryForDisplay}}</span>                                               
                                </h3>
                                <div class="info-detail">
                                    <div class="info-date">                     
                                        <span class="affiliation-date" *ngIf="group.startDate">
                                            <span *ngIf="group.startDate.year">{{group.startDate.year}}</span><span *ngIf="group.startDate.month">-{{group.startDate.month}}</span><span *ngIf="group.startDate.day">-{{group.startDate.day}}</span>
                                            <span><@orcid.msg 'workspace_affiliations.dateSeparator'/></span>
                                            <span [hidden]="group.endDate && group.endDate.year"><@orcid.msg 'workspace_affiliations.present'/></span>
                                            <span *ngIf="group.endDate">
                                                <span *ngIf="group.endDate.year">{{group.endDate.year}}</span><span *ngIf="group.endDate.month">-{{group.endDate.month}}</span><span *ngIf="group.endDate.day">-{{group.endDate.day}}</span>
                                            </span>
                                        </span>
                                        <span class="affiliation-date" *ngIf="!group.startDate && group.endDate">
                                             <span  *ngIf="group.endDate.year">{{group.endDate.year}}</span><span *ngIf="group.endDate.month">-{{group.endDate.month}}</span><span *ngIf="group.endDate.day">-{{group.endDate.day}}</span>
                                        </span>
                                        <span *ngIf="(group.startDate || group.endDate) && (group.roleTitle.value || group.departmentName.value)"> | </span> <span *ngIf="group.roleTitle.value">{{group.roleTitle.value}}</span>        
                                        <span *ngIf="group.departmentName.value">
                                        <span *ngIf="group.roleTitle.value && !printView">&nbsp;</span>(<span>{{group.departmentName.value}}</span>)
                                        </span>
                                    </div>
                                </div>
                            </div>
                            <div class="col-md-3 col-sm-3 col-xs-5 padding-left-fix">          
                                <div class="workspace-toolbar">         
                                    <ul class="workspace-private-toolbar"> 
                                        <div *ngIf="orgIdsFeatureEnabled">
                                            <li class="works-details">
                                                <a (click)="showDetailsMouseClick(group,$event);showMozillaBadges(group.activePutCode)" (mouseenter)="showTooltip(group.groupId+'-showHideDetails')" (mouseleave)="hideTooltip(group.groupId+'-showHideDetails')">
                                                    <span [ngClass]="(moreInfo[group.groupId] == true) ? 'glyphicons collapse_top' : 'glyphicons expand'">
                                                    </span>
                                                </a>
                                                <div class="popover popover-tooltip top show-hide-details-popover" *ngIf="showElement[group.groupId+'-showHideDetails']">
                                                     <div class="arrow"></div>
                                                    <div class="popover-content">   
                                                        <span *ngIf="moreInfo[group.groupId] == false || moreInfo[group.groupId] == null"><@orcid.msg 'common.details.show_details'/></span>   
                                                        <span *ngIf="moreInfo[group.groupId]"><@orcid.msg 'common.details.hide_details'/></span>
                                                    </div>
                                                </div>
                                            </li>
                                        </div>
                                        <#if !(isPublicProfile??)> 
                                        <li>
                                            <@orcid.privacyToggle2Ng2  angularModel="group.visibility.visibility"
                                                elementId="group.putCode.value"
                                                questionClick="toggleClickPrivacyHelp(group.putCode.value)"
                                                clickedClassCheck="{'popover-help-container-show':privacyHelp[group.putCode.value]==true}" 
                                                publicClick="setPrivacy(group, 'PUBLIC', $event)" 
                                                limitedClick="setPrivacy(group, 'LIMITED', $event)" 
                                                privateClick="setPrivacy(group, 'PRIVATE', $event)" />
                                        </li>
                                        </#if>
                                    </ul>
                                </div>
                            </div>  
                        </div>
                        <div class="row" *ngIf="group.activePutCode == group.putCode.value">
                            <div class="col-md-12 col-sm-12 bottomBuffer">
                                <ul class="id-details">
                                    <li class="url-work">
                                        <ul class="id-details">
                                            <li *ngFor='let extID of group.affiliationExternalIdentifiers' class="url-popover">
                                                <span *ngIf="group.affiliationExternalIdentifiers[0].value.value.length > 0">{{extID}}</span>
                                            </li>
                                        </ul>                                   
                                    </li>
                                </ul>
                            </div>
                        </div>  
                        <div *ngIf="orgIdsFeatureEnabled">
                            <div class="more-info content" *ngIf="moreInfo[group.groupId]">
                                <div class="row bottomBuffer">
                                    <div class="col-md-12"></div>
                                </div>
                                <span class="dotted-bar"></span>    
                                <div class="row">
                                    <div class="org-ids" *ngIf="group.orgDisambiguatedId.value">
                                        <div class="col-md-12">   
                                            <strong><@orcid.msg 'workspace_affiliations.organization_id'/></strong><br>
                                            <span [innerHTML]='group.disambiguatedAffiliationSourceId.value | orgIdentifierHtml:group.disambiguationSource.value:group.putCode.value:group.disambiguationSource' class="url-popover"> 
                                            </span>
                                        </div>
                                        <div class="col-md-11 bottomBuffer info-detail leftBuffer clearfix">
                                            <span *ngIf="group.orgDisambiguatedName">{{group.orgDisambiguatedName}}</span><span *ngIf="group.orgDisambiguatedCity || group.orgDisambiguatedRegion || group.orgDisambiguatedCountry">: </span><span *ngIf="group.orgDisambiguatedCity">{{group.orgDisambiguatedCity}}</span><span *ngIf="group.orgDisambiguatedCity && group.orgDisambiguatedRegion">, </span><span *ngIf="group.orgDisambiguatedRegion">{{group.orgDisambiguatedRegion}}</span><span *ngIf="group.orgDisambiguatedCountry && (group.orgDisambiguatedCity || group.orgDisambiguatedRegion)">, </span><span *ngIf="group.orgDisambiguatedCountry">{{group.orgDisambiguatedCountry}}</span>
                                            <span *ngIf="group.orgDisambiguatedUrl"><br>
                                            <a href="{{group.orgDisambiguatedUrl}}" target="orgDisambiguatedUrl"><span>{{group.orgDisambiguatedUrl}}</span></a>
                                            </span>                                            
                                            <div *ngIf="group.orgDisambiguatedExternalIdentifiers">
                                                <strong><@orcid.msg 'workspace_affiliations.external_ids'/> {{group.disambiguationSource.value}}</strong><br>
                                                <ul class="reset">
                                                    <li *ngFor="let orgDisambiguatedExternalIdentifier of group.orgDisambiguatedExternalIdentifiers | orderBy:orgDisambiguatedExternalIdentifier.identifierType">
                                                        {{orgDisambiguatedExternalIdentifier.identifierType}}:  <span *ngIf="orgDisambiguatedExternalIdentifier.preferred">{{orgDisambiguatedExternalIdentifier.preferred}} <@orcid.msg 'workspace_affiliations.external_ids_preferred'/>, </span> <span *ngIf="orgDisambiguatedExternalIdentifier.all"><span *ngFor="let orgDisambiguatedExternalIdentifierAll of orgDisambiguatedExternalIdentifier.all">{{orgDisambiguatedExternalIdentifierAll}}{{$last ? '' : ', '}}</span></span></li>
                                                </ul>
                                            </div>
                                        </div>
                                    </div>
                                    <div class="col-md-6" *ngIf="group.url.value">
                                        <div class="bottomBuffer">
                                            <strong><@orcid.msg 'common.url'/></strong><br> 
                                            <a href="{{group.url.value}}" target="affiliation.url.value">{{group.url.value}}</a>
                                        </div>
                                    </div>  
                                    <div class="col-md-12">
                                        <div class="bottomBuffer">
                                            <strong><@orcid.msg 'groups.common.created'/></strong><br> 
                                            <span>{{group.createdDate}}</span>
                                        </div>
                                    </div>  
                                </div>
                            </div>
                        </div>
                        <div class="row source-line">
                            <div class="col-md-12 col-sm-12 col-xs-12">
                                <div class="sources-container-header">          
                                    <div class="row">
                                        <div class="col-md-7 col-sm-7 col-xs-12">
                                            <@orcid.msg 'groups.common.source'/>: {{(group.sourceName == null || group.sourceName == '') ? group.source : group.sourceName}}    
                                        </div>                            
                                        <div class="col-md-3 col-sm-3 col-xs-6">
                                            <@orcid.msg 'groups.common.created'/>: <span>{{group.createdDate | ajaxFormDateToISO8601}}</span>
                                        </div>                                                   
                                        <div class="col-md-2 col-sm-2 col-xs-6 pull-right">
                                            <ul class="sources-options">
                                                <#if !(isPublicProfile??)>
                                                <li *ngIf="group.source == '${effectiveUserOrcid}'">
                                                    <a (click)="openEditAffiliation(group)" (mouseenter)="showTooltip(group.putCode.value+'-edit')" (mouseleave)="hideTooltip(group.putCode.value+'-edit')">
                                                        <span class="glyphicon glyphicon-pencil"></span>
                                                    </a>
                                                    <div class="popover popover-tooltip top edit-source-popover" *ngIf="showElement[group.putCode.value+'-edit']"> 
                                                        <div class="arrow"></div>
                                                        <div class="popover-content">
                                                            <span ><@orcid.msg 'groups.common.edit_my'/></span>
                                                        </div>                
                                                    </div>  
                                                </li>   
                                                <li>
                                                    <a id="delete-affiliation_{{group.putCode.value}}" href="" (click)="deleteAffiliation(group)" (mouseenter)="showTooltip(group.putCode.value+'-delete')" (mouseleave)="hideTooltip(group.putCode.value+'-delete')" class="glyphicon glyphicon-trash"></a>
                                                    <div class="popover popover-tooltip top delete-source-popover" *ngIf="showElement[group.putCode.value+'-delete']"> 
                                                        <div class="arrow"></div>
                                                        <div class="popover-content">
                                                             <@orcid.msg 'groups.common.delete_this_source' />
                                                        </div>                
                                                    </div>
                                                </li>
                                                </#if>  
                                            </ul>
                                        </div>
                                    </div>                                    
                                </div>
                            </div>
                        </div>            
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
                            <a href="" (click)="toggleEducationAndQualification()" class="toggle-text">
                                <i class="glyphicon-chevron-down glyphicon x075" [ngClass]="{ 'glyphicon-chevron-right': displayEducationAndQualification()==false }"></i>                               
                                <@orcid.msg 'org.orcid.jaxb.model.message.AffiliationType.education_qualification'/> (<span>{{sectionOneElements.length}}</span>)
                            </a>    
                            <#if !(isPublicProfile??)> 
                                <div class="popover-help-container">
                                    <a href="javascript:void(0);"><i class="glyphicon glyphicon-question-sign"></i></a>
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
                            <a href="" (click)="toggleEducation()" class="toggle-text">
                                <i class="glyphicon-chevron-down glyphicon x075" [ngClass]="{ 'glyphicon-chevron-right': displayEducation()==false }"></i>                               
                                <@orcid.msg 'org.orcid.jaxb.model.message.AffiliationType.education'/> (<span>{{educations.length}}</span>)
                            </a>    
                            <#if !(isPublicProfile??)> 
                                <div class="popover-help-container">
                                    <a href="javascript:void(0);"><i class="glyphicon glyphicon-question-sign"></i></a>
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
                <div id="educations-distinctions-empty" *ngIf="!sectionOneElements.length && workspaceSrvc.displayEducationAndQualification">
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
                <ul id="educations-distinctions-list" *ngIf="sectionOneElements.length" class="workspace-affiliations workspace-body-list bottom-margin-medium">
                    <li class="bottom-margin-small workspace-border-box affiliation-box card ng-scope" *ngFor="let group of sectionOneElements | orderBy: sortState.predicate:sortState.reverse" [attr.education-distinction-put-code]="group.putCode.value">
                        <div class="row">                                 
                            <div class="col-md-9 col-sm-9 col-xs-7">
                                <h3 class="workspace-title">            
                                    <span>{{group.affiliationName.value}}</span>:
                                    <span>{{group.city.value}}</span><span *ngIf="group.region.value">, </span><span>{{group.region.value}}</span>, <span>{{group.countryForDisplay}}</span>                                               
                                </h3>
                                <div class="info-detail">
                                    <div class="info-date">                     
                                        <span class="affiliation-date" *ngIf="group.startDate">
                                            <span *ngIf="group.startDate.year">{{group.startDate.year}}</span><span *ngIf="group.startDate.month">-{{group.startDate.month}}</span><span *ngIf="group.startDate.day">-{{group.startDate.day}}</span>
                                            <span><@orcid.msg 'workspace_affiliations.dateSeparator'/></span>
                                            <span [hidden]="group.endDate && group.endDate.year"><@orcid.msg 'workspace_affiliations.present'/></span>
                                            <span *ngIf="group.endDate">
                                                <span *ngIf="group.endDate.year">{{group.endDate.year}}</span><span *ngIf="group.endDate.month">-{{group.endDate.month}}</span><span *ngIf="group.endDate.day">-{{group.endDate.day}}</span>
                                            </span>
                                        </span>
                                        <span class="affiliation-date" *ngIf="!group.startDate && group.endDate">
                                             <span *ngIf="group.endDate.year">{{group.endDate.year}}</span><span *ngIf="group.endDate.month">-{{group.endDate.month}}</span><span *ngIf="group.endDate.day">-{{group.endDate.day}}</span>
                                        </span>
                                        <span *ngIf="(group.startDate || group.endDate) && (group.roleTitle.value || group.departmentName.value)"> | </span> <span *ngIf="group.roleTitle.value">{{group.roleTitle.value}}</span>        
                                        <span *ngIf="group.departmentName.value">
                                            <span *ngIf="group.roleTitle.value && !printView">&nbsp;</span>(<span>{{group.departmentName.value}}</span>)
                                        </span>
                                    </div>
                                </div>
                            </div>                            
                            <div class="col-md-3 col-sm-3 col-xs-5 padding-left-fix">          
                                <div class="workspace-toolbar">         
                                    <ul class="workspace-private-toolbar"> 
                                        <div *ngIf="orgIdsFeatureEnabled">
                                            <li class="works-details">
                                                <a (click)="showDetailsMouseClick(group,$event);showMozillaBadges(group.activePutCode)" (mouseenter)="showTooltip(group.groupId+'-showHideDetails')" (mouseleave)="hideTooltip(group.groupId+'-showHideDetails')">
                                                    <span [ngClass]="(moreInfo[group.groupId] == true) ? 'glyphicons collapse_top' : 'glyphicons expand'">
                                                    </span>
                                                </a>
                                                <div class="popover popover-tooltip top show-hide-details-popover" *ngIf="showElement[group.groupId+'-showHideDetails']">
                                                     <div class="arrow"></div>
                                                    <div class="popover-content">   
                                                        <span *ngIf="moreInfo[group.groupId] == false || moreInfo[group.groupId] == null"><@orcid.msg 'common.details.show_details'/></span>   
                                                        <span *ngIf="moreInfo[group.groupId]"><@orcid.msg 'common.details.hide_details'/></span>
                                                    </div>
                                                </div>
                                            </li>                                        
                                        </div>
                                        <#if !(isPublicProfile??)> 
                                        <li>
                                            <@orcid.privacyToggle2Ng2  angularModel="group.visibility.visibility" 
                                                elementId="group.putCode.value"
                                                questionClick="toggleClickPrivacyHelp(group.putCode.value)" 
                                                clickedClassCheck="{'popover-help-container-show':privacyHelp[group.putCode.value]==true}" 
                                                publicClick="setPrivacy(group, 'PUBLIC', $event)" 
                                                limitedClick="setPrivacy(group, 'LIMITED', $event)" 
                                                privateClick="setPrivacy(group, 'PRIVATE', $event)" />
                                        </li>
                                        </#if>
                                    </ul>
                                </div>
                            </div>  
                        </div>
                        <div class="row" *ngIf="group.activePutCode == group.putCode.value">
                            <div class="col-md-12 col-sm-12 bottomBuffer">
                                <ul class="id-details">
                                    <li class="url-work">
                                        <ul class="id-details">
                                            <li *ngFor='let extID of group.affiliationExternalIdentifiers | orderBy:["-relationship.value", "type.value"]; trackBy: trackByFn' class="url-popover">
                                                <span *ngIf="group.affiliationExternalIdentifiers[0].value.value.length > 0">{{extID}}</span>                                                
                                            </li>
                                        </ul>                                   
                                    </li>
                                </ul>
                            </div>
                        </div>  
                        <div *ngIf="orgIdsFeatureEnabled">                     
                            <div class="more-info content" *ngIf="moreInfo[group.groupId]">
                                <div class="row bottomBuffer">
                                    <div class="col-md-12"></div>
                                </div>
                                <span class="dotted-bar"></span>    
                                <div class="row">
                                    <div class="org-ids" *ngIf="group.orgDisambiguatedId.value">
                                        <div class="col-md-12">   
                                            <strong><@orcid.msg 'workspace_affiliations.organization_id'/></strong><br>
                                            <span class="url-popover">{{group.disambiguatedAffiliationSourceId.value}}</span>

                                            <span [innerHTML]='group.disambiguatedAffiliationSourceId.value | orgIdentifierHtml:group.disambiguationSource.value:group.putCode.value:group.disambiguationSource' class="url-popover"> 
                                            </span>
                                        </div>
                                        <div class="col-md-11 bottomBuffer info-detail leftBuffer clearfix">
                                            <span *ngIf="group.orgDisambiguatedName">{{group.orgDisambiguatedName}}</span><span *ngIf="group.orgDisambiguatedCity || group.orgDisambiguatedRegion || group.orgDisambiguatedCountry">: </span><span *ngIf="group.orgDisambiguatedCity">{{group.orgDisambiguatedCity}}</span><span *ngIf="group.orgDisambiguatedCity && group.orgDisambiguatedRegion">, </span><span *ngIf="group.orgDisambiguatedRegion">{{group.orgDisambiguatedRegion}}</span><span *ngIf="group.orgDisambiguatedCountry && (group.orgDisambiguatedCity || group.orgDisambiguatedRegion)">, </span><span *ngIf="group.orgDisambiguatedCountry">{{group.orgDisambiguatedCountry}}</span>
                                            <span *ngIf="group.orgDisambiguatedUrl"><br>
                                            <a href="{{group.orgDisambiguatedUrl}}" target="orgDisambiguatedUrl"><span>{{group.orgDisambiguatedUrl}}</span></a>
                                            </span>
                                         
                                            <div *ngIf="group.orgDisambiguatedExternalIdentifiers">
                                                <strong><@orcid.msg 'workspace_affiliations.external_ids'/> {{group.disambiguationSource.value}}</strong><br>
                                                <ul class="reset">
                                                    <li *ngFor="let orgDisambiguatedExternalIdentifier of group.orgDisambiguatedExternalIdentifiers | orderBy:orgDisambiguatedExternalIdentifier.identifierType">
                                                        {{orgDisambiguatedExternalIdentifier.identifierType}}:  
                                                        <span *ngIf="orgDisambiguatedExternalIdentifier.preferred">{{orgDisambiguatedExternalIdentifier.preferred}} <@orcid.msg 'workspace_affiliations.external_ids_preferred'/>, </span> 
                                                        <span *ngIf="orgDisambiguatedExternalIdentifier.all">
                                                            <span *ngFor="let orgDisambiguatedExternalIdentifierAll of orgDisambiguatedExternalIdentifier.all">{{orgDisambiguatedExternalIdentifierAll}}{{$last ? '' : ', '}}</span>
                                                        </span>
                                                    </li>
                                                </ul>
                                            </div>
                                        </div>
                                    </div>
                                    <div class="col-md-6" *ngIf="group.url.value">
                                        <div class="bottomBuffer">
                                            <strong><@orcid.msg 'common.url'/></strong><br> 
                                            <a href="{{group.url.value}}" target="affiliation.url.value">{{group.url.value}}</a>
                                        </div>
                                    </div>  
                                    <div class="col-md-12">
                                        <div class="bottomBuffer">
                                            <strong><@orcid.msg 'groups.common.created'/></strong><br /> 
                                            <span>{{group.createdDate | ajaxFormDateToISO8601}}</span>
                                        </div>
                                    </div>  
                                </div>
                            </div>
                        </div>
                        <div class="row source-line">
                            <div class="col-md-12 col-sm-12 col-xs-12">
                                <div class="sources-container-header">          
                                    <div class="row">
                                        <div class="col-md-7 col-sm-7 col-xs-12">
                                            <@orcid.msg 'groups.common.source'/>: {{(group.sourceName == null || group.sourceName == '') ? group.source : group.sourceName}}    
                                        </div>
                                        
                                        <div class="col-md-3 col-sm-3 col-xs-6">
                                            <@orcid.msg 'groups.common.created'/>: <span>{{group.createdDate | ajaxFormDateToISO8601}}</span>
                                        </div>                                                   
                                        <div class="col-md-2 col-sm-2 col-xs-6 pull-right">
                                            <ul class="sources-options">
                                                <#if !(isPublicProfile??)>
                                                <li *ngIf="group.source == '${effectiveUserOrcid}'">
                                                    <a (click)="openEditAffiliation(group)" (mouseenter)="showTooltip(group.putCode.value+'-edit')" (mouseleave)="hideTooltip(group.putCode.value+'-edit')">
                                                        <span class="glyphicon glyphicon-pencil"></span>
                                                    </a>
                                                    <div class="popover popover-tooltip top edit-source-popover" *ngIf="showElement[group.putCode.value+'-edit']"> 
                                                        <div class="arrow"></div>
                                                        <div class="popover-content">
                                                            <span ><@orcid.msg 'groups.common.edit_my'/></span>
                                                        </div>                
                                                    </div>  
                                                </li>   
                                                <li>
                                                    <a id="delete-affiliation_{{group.putCode.value}}" href="" (click)="deleteAffiliation(group)" (mouseenter)="showTooltip(group.putCode.value+'-delete')" (mouseleave)="hideTooltip(group.putCode.value+'-delete')" class="glyphicon glyphicon-trash"></a>
                                                    <div class="popover popover-tooltip top delete-source-popover" *ngIf="showElement[group.putCode.value+'-delete']"> 
                                                        <div class="arrow"></div>
                                                        <div class="popover-content">
                                                             <@orcid.msg 'groups.common.delete_this_source' />
                                                        </div>                
                                                    </div>
                                                </li>
                                                </#if>  
                                            </ul>
                                        </div>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </li>
                </ul>
            </div>
        </div>
        <!-- INVITED POSITION AND DISTINCTION  -->
        <div *ngIf="displayNewAffiliationTypesFeatureEnabled">
            <div id="workspace-distinction-invited-position" class="workspace-accordion-item workspace-accordion-active" [hidden]="publicView == 'true' && distinctionsAndInvitedPositions.length < 1">
                <div class="workspace-accordion-header clearfix">
                    <div class="row">
                        <div class="col-md-6 col-sm-6 col-xs-12">
                            <a name='workspace-distinction-invited-position'></a>
                            <a href="" (click)="workspaceSrvc.toggleDistinctionAndInvitedPosition($event)" class="toggle-text">
                                <i class="glyphicon-chevron-down glyphicon x075" [ngClass]="{'glyphicon-chevron-right':workspaceSrvc.displayDistinctionAndInvitedPosition==false}"></i>
                                <@orcid.msg 'org.orcid.jaxb.model.message.AffiliationType.distinction_invited_position'/> (<span>{{distinctionsAndInvitedPositions.length}}</span>)
                            </a>
                            <#if !(isPublicProfile??)> 
                            <div class="popover-help-container">
                                <a href="javascript:void(0);"><i class="glyphicon glyphicon-question-sign"></i></a>
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
                    <div id="distinction-invited-position-empty" *ngIf="!distinctionsAndInvitedPositions.length">
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
                    <ul id="distinction-invited-position-list" *ngIf="distinctionsAndInvitedPositions.length > 0" class="workspace-affiliations workspace-body-list bottom-margin-medium">
                        <li class="bottom-margin-small workspace-border-box affiliation-box card" *ngFor="let group of distinctionsAndInvitedPositions | orderBy: sortState.predicate:sortState.reverse" [attr.distinction-invited-position-put-code]="group.putCode.value">
                            <div class="row">                
                                <div class="col-md-9 col-sm-9 col-xs-7">
                                    <h3 class="workspace-title">            
                                        <span>{{group.affiliationName.value}}</span>:
                                        <span>{{group.city.value}}</span><span *ngIf="group.region.value">, </span><span>{{group.region.value}}</span>, <span>{{group.countryForDisplay}}</span>                                               
                                    </h3>
                                    <div class="info-detail">
                                        <div class="info-date">                     
                                            <span class="affiliation-date" *ngIf="group.startDate">
                                                <span *ngIf="group.startDate.year">{{group.startDate.year}}</span><span *ngIf="group.startDate.month">-{{group.startDate.month}}</span><span *ngIf="group.startDate.day">-{{group.startDate.day}}</span>
                                                <span><@orcid.msg 'workspace_affiliations.dateSeparator'/></span>
                                                <span [hidden]="group.endDate && group.endDate.year"><@orcid.msg 'workspace_affiliations.present'/></span>
                                                <span *ngIf="group.endDate">
                                                    <span *ngIf="group.endDate.year">{{group.endDate.year}}</span><span *ngIf="group.endDate.month">-{{group.endDate.month}}</span><span *ngIf="group.endDate.day">-{{group.endDate.day}}</span>                                                
                                                </span>
                                            </span>
                                            <span class="affiliation-date" *ngIf="!group.startDate && group.endDate">
                                                 <span  *ngIf="group.endDate.year">{{group.endDate.year}}</span><span *ngIf="group.endDate.month">-{{group.endDate.month}}</span><span *ngIf="group.endDate.day">-{{group.endDate.day}}</span>
                                            </span>
                                            <span *ngIf="(group.startDate || group.endDate) && (group.roleTitle.value || group.departmentName.value)"> | </span> <span *ngIf="group.roleTitle.value">{{group.roleTitle.value}}</span>        
                                            <span *ngIf="group.departmentName.value">
                                            <span *ngIf="group.roleTitle.value && !printView">&nbsp;</span>(<span>{{group.departmentName.value}}</span>)
                                            </span>
                                        </div>
                                    </div>
                                </div>
                                <div class="col-md-3 col-sm-3 col-xs-5 padding-left-fix">          
                                    <div class="workspace-toolbar">         
                                        <ul class="workspace-private-toolbar"> 
                                            <div *ngIf="orgIdsFeatureEnabled">
                                                <li class="works-details">
                                                    <a (click)="showDetailsMouseClick(group,$event);showMozillaBadges(group.activePutCode)" (mouseenter)="showTooltip(group.groupId+'-showHideDetails')" (mouseleave)="hideTooltip(group.groupId+'-showHideDetails')">
                                                        <span [ngClass]="(moreInfo[group.groupId] == true) ? 'glyphicons collapse_top' : 'glyphicons expand'">
                                                        </span>
                                                    </a>
                                                    <div class="popover popover-tooltip top show-hide-details-popover" *ngIf="showElement[group.groupId+'-showHideDetails']">
                                                         <div class="arrow"></div>
                                                        <div class="popover-content">   
                                                            <span *ngIf="moreInfo[group.groupId] == false || moreInfo[group.groupId] == null"><@orcid.msg 'common.details.show_details'/></span>   
                                                            <span *ngIf="moreInfo[group.groupId]"><@orcid.msg 'common.details.hide_details'/></span>
                                                        </div>
                                                    </div>
                                                </li>
                                            </div>
                                            <#if !(isPublicProfile??)> 
                                            <li>
                                                <@orcid.privacyToggle2Ng2 angularModel="group.visibility.visibility"
                                                    elementId="group.putCode.value"
                                                    questionClick="toggleClickPrivacyHelp(group.putCode.value)"
                                                    clickedClassCheck="{'popover-help-container-show':privacyHelp[group.putCode.value]==true}" 
                                                    publicClick="setPrivacy(group, 'PUBLIC', $event)" 
                                                    limitedClick="setPrivacy(group, 'LIMITED', $event)" 
                                                    privateClick="setPrivacy(group, 'PRIVATE', $event)" />
                                            </li>
                                            </#if>
                                        </ul>
                                    </div>
                                </div>  
                            </div>
                            <div class="row" *ngIf="group.activePutCode == group.putCode.value">
                                <div class="col-md-12 col-sm-12 bottomBuffer">
                                    <ul class="id-details">
                                        <li class="url-work">
                                            <ul class="id-details">
                                                <li *ngFor='let extID of group.affiliationExternalIdentifiers | orderBy:["relationship.value", "type.value"]' class="url-popover">
                                                    <span *ngIf="group.affiliationExternalIdentifiers[0].value.value.length > 0">{{extID}}</span>
                                                </li>
                                            </ul>                                   
                                        </li>
                                    </ul>
                                </div>
                            </div>  
                            <div *ngIf="orgIdsFeatureEnabled">
                                <div class="more-info content" *ngIf="moreInfo[group.groupId]">
                                    <div class="row bottomBuffer">
                                        <div class="col-md-12"></div>
                                    </div>
                                    <span class="dotted-bar"></span>    
                                    <div class="row">
                                        <div class="org-ids" *ngIf="group.orgDisambiguatedId.value">
                                            <div class="col-md-12">   
                                                <strong><@orcid.msg 'workspace_affiliations.organization_id'/></strong><br>
                                                <span [innerHTML]='group.disambiguatedAffiliationSourceId.value | orgIdentifierHtml:group.disambiguationSource.value:group.putCode.value:group.disambiguationSource' class="url-popover"> 
                                                </span>
                                            </div>
                                            <div class="col-md-11 bottomBuffer info-detail leftBuffer clearfix">
                                                <span *ngIf="group.orgDisambiguatedName">{{group.orgDisambiguatedName}}</span><span *ngIf="group.orgDisambiguatedCity || group.orgDisambiguatedRegion || group.orgDisambiguatedCountry">: </span><span *ngIf="group.orgDisambiguatedCity">{{group.orgDisambiguatedCity}}</span><span *ngIf="group.orgDisambiguatedCity && group.orgDisambiguatedRegion">, </span><span *ngIf="group.orgDisambiguatedRegion">{{group.orgDisambiguatedRegion}}</span><span *ngIf="group.orgDisambiguatedCountry && (group.orgDisambiguatedCity || group.orgDisambiguatedRegion)">, </span><span *ngIf="group.orgDisambiguatedCountry">{{group.orgDisambiguatedCountry}}</span>
                                                <span *ngIf="group.orgDisambiguatedUrl"><br>
                                                <a href="{{group.orgDisambiguatedUrl}}" target="orgDisambiguatedUrl"><span>{{group.orgDisambiguatedUrl}}</span></a>
                                                </span>                                                
                                                <div *ngIf="group.orgDisambiguatedExternalIdentifiers">
                                                    <strong><@orcid.msg 'workspace_affiliations.external_ids'/> {{group.disambiguationSource.value}}</strong><br>
                                                    <ul class="reset">
                                                        <li *ngFor="let orgDisambiguatedExternalIdentifier of group.orgDisambiguatedExternalIdentifiers | orderBy:orgDisambiguatedExternalIdentifier.identifierType ">
                                                            {{orgDisambiguatedExternalIdentifier.identifierType}}:  <span *ngIf="orgDisambiguatedExternalIdentifier.preferred">{{orgDisambiguatedExternalIdentifier.preferred}} <@orcid.msg 'workspace_affiliations.external_ids_preferred'/>, </span> <span *ngIf="orgDisambiguatedExternalIdentifier.all"><span *ngFor="let orgDisambiguatedExternalIdentifierAll of orgDisambiguatedExternalIdentifier.all">{{orgDisambiguatedExternalIdentifierAll}}{{$last ? '' : ', '}}</span></span></li>
                                                    </ul>
                                                </div>
                                            </div>
                                        </div>
                                        <div class="col-md-6" *ngIf="group.url.value">
                                            <div class="bottomBuffer">
                                                <strong><@orcid.msg 'common.url'/></strong><br> 
                                                <a href="{{group.url.value}}" target="affiliation.url.value">{{group.url.value}}</a>
                                            </div>
                                        </div>  
                                        <div class="col-md-12">
                                            <div class="bottomBuffer">
                                                <strong><@orcid.msg 'groups.common.created'/></strong><br> 
                                                <span>{{group.createdDate}}</span>
                                            </div>
                                        </div>  
                                    </div>
                                </div>
                            </div>
                            <div class="row source-line">
                                <div class="col-md-12 col-sm-12 col-xs-12">
                                    <div class="sources-container-header">          
                                        <div class="row">
                                            <div class="col-md-7 col-sm-7 col-xs-12">
                                                <@orcid.msg 'groups.common.source'/>: {{(group.sourceName == null || group.sourceName == '') ? group.source : group.sourceName}}    
                                            </div>                            
                                            <div class="col-md-3 col-sm-3 col-xs-6">
                                                <@orcid.msg 'groups.common.created'/>: <span>{{group.createdDate | ajaxFormDateToISO8601}}</span>
                                            </div>                                                   
                                            <div class="col-md-2 col-sm-2 col-xs-6 pull-right">
                                                <ul class="sources-options">
                                                    <#if !(isPublicProfile??)>
                                                    <li *ngIf="group.source == '${effectiveUserOrcid}'">
                                                        <a (click)="openEditAffiliation(group)" (mouseenter)="showTooltip(group.putCode.value+'-edit')" (mouseleave)="hideTooltip(group.putCode.value+'-edit')">
                                                            <span class="glyphicon glyphicon-pencil"></span>
                                                        </a>
                                                        <div class="popover popover-tooltip top edit-source-popover" *ngIf="showElement[group.putCode.value+'-edit']"> 
                                                            <div class="arrow"></div>
                                                            <div class="popover-content">
                                                                <span ><@orcid.msg 'groups.common.edit_my'/></span>
                                                            </div>                
                                                        </div>  
                                                    </li>   
                                                    <li>
                                                        <a id="delete-affiliation_{{group.putCode.value}}" href="" (click)="deleteAffiliation(group)" (mouseenter)="showTooltip(group.putCode.value+'-delete')" (mouseleave)="hideTooltip(group.putCode.value+'-delete')" class="glyphicon glyphicon-trash"></a>
                                                        <div class="popover popover-tooltip top delete-source-popover" *ngIf="showElement[group.putCode.value+'-delete']"> 
                                                            <div class="arrow"></div>
                                                            <div class="popover-content">
                                                                 <@orcid.msg 'groups.common.delete_this_source' />
                                                            </div>                
                                                        </div>
                                                    </li>
                                                    </#if>  
                                                </ul>
                                            </div>
                                        </div>                                    
                                    </div>
                                </div>
                            </div>            
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
                            <a href="" (click)="workspaceSrvc.toggleMembershipAndService($event)" class="toggle-text">
                                <i class="glyphicon-chevron-down glyphicon x075" [ngClass]="{'glyphicon-chevron-right':workspaceSrvc.displayMembershipAndService==false}"></i>
                                <@orcid.msg 'org.orcid.jaxb.model.message.AffiliationType.membership_service'/> (<span>{{membershipsAndServices.length}}</span>)
                            </a>
                            <#if !(isPublicProfile??)> 
                            <div class="popover-help-container">
                                <a href="javascript:void(0);"><i class="glyphicon glyphicon-question-sign"></i></a>
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
                    <div id="membership-service-empty" *ngIf="!membershipsAndServices.length">
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
                    <ul id="membership-service-list" *ngIf="membershipsAndServices.length > 0" class="workspace-affiliations workspace-body-list bottom-margin-medium">
                        <li class="bottom-margin-small workspace-border-box affiliation-box card" *ngFor="let group of membershipsAndServices | orderBy: sortState.predicate:sortState.reverse" [attr.membership-service-put-code]="group.putCode.value">
                            <div class="row">                
                                <div class="col-md-9 col-sm-9 col-xs-7">
                                    <h3 class="workspace-title">            
                                        <span>{{group.affiliationName.value}}</span>:
                                        <span>{{group.city.value}}</span><span *ngIf="group.region.value">, </span><span>{{group.region.value}}</span>, <span>{{group.countryForDisplay}}</span>                                               
                                    </h3>
                                    <div class="info-detail">
                                        <div class="info-date">                     
                                            <span class="affiliation-date" *ngIf="group.startDate">
                                                <span *ngIf="group.startDate.year">{{group.startDate.year}}</span><span *ngIf="group.startDate.month">-{{group.startDate.month}}</span><span *ngIf="group.startDate.day">-{{group.startDate.day}}</span>
                                                <span><@orcid.msg 'workspace_affiliations.dateSeparator'/></span>
                                                <span [hidden]="group.endDate && group.endDate.year"><@orcid.msg 'workspace_affiliations.present'/></span>
                                                <span *ngIf="group.endDate">    
                                                    <span *ngIf="group.endDate.year">{{group.endDate.year}}</span><span *ngIf="group.endDate.month">-{{group.endDate.month}}</span><span *ngIf="group.endDate.day">-{{group.endDate.day}}</span>
                                                </span>
                                            </span>
                                            <span class="affiliation-date" *ngIf="!group.startDate && group.endDate">
                                                 <span  *ngIf="group.endDate.year">{{group.endDate.year}}</span><span *ngIf="group.endDate.month">-{{group.endDate.month}}</span><span *ngIf="group.endDate.day">-{{group.endDate.day}}</span>
                                            </span>
                                            <span *ngIf="(group.startDate || group.endDate) && (group.roleTitle.value || group.departmentName.value)"> | </span> <span *ngIf="group.roleTitle.value">{{group.roleTitle.value}}</span>        
                                            <span *ngIf="group.departmentName.value">
                                            <span *ngIf="group.roleTitle.value && !printView">&nbsp;</span>(<span>{{group.departmentName.value}}</span>)
                                            </span>
                                        </div>
                                    </div>
                                </div>
                                <div class="col-md-3 col-sm-3 col-xs-5 padding-left-fix">          
                                    <div class="workspace-toolbar">         
                                        <ul class="workspace-private-toolbar"> 
                                            <div *ngIf="orgIdsFeatureEnabled">
                                                <li class="works-details">
                                                    <a (click)="showDetailsMouseClick(group,$event);showMozillaBadges(group.activePutCode)" (mouseenter)="showTooltip(group.groupId+'-showHideDetails')" (mouseleave)="hideTooltip(group.groupId+'-showHideDetails')">
                                                        <span [ngClass]="(moreInfo[group.groupId] == true) ? 'glyphicons collapse_top' : 'glyphicons expand'">
                                                        </span>
                                                    </a>
                                                    <div class="popover popover-tooltip top show-hide-details-popover" *ngIf="showElement[group.groupId+'-showHideDetails']">
                                                         <div class="arrow"></div>
                                                        <div class="popover-content">   
                                                            <span *ngIf="moreInfo[group.groupId] == false || moreInfo[group.groupId] == null"><@orcid.msg 'common.details.show_details'/></span>   
                                                            <span *ngIf="moreInfo[group.groupId]"><@orcid.msg 'common.details.hide_details'/></span>
                                                        </div>
                                                    </div>
                                                </li>
                                            </div>
                                            <#if !(isPublicProfile??)> 
                                            <li>
                                                <@orcid.privacyToggle2Ng2 angularModel="group.visibility.visibility"
                                                    elementId="group.putCode.value"
                                                    questionClick="toggleClickPrivacyHelp(group.putCode.value)"
                                                    clickedClassCheck="{'popover-help-container-show':privacyHelp[group.putCode.value]==true}" 
                                                    publicClick="setPrivacy(group, 'PUBLIC', $event)" 
                                                    limitedClick="setPrivacy(group, 'LIMITED', $event)" 
                                                    privateClick="setPrivacy(group, 'PRIVATE', $event)" />
                                            </li>
                                            </#if>
                                        </ul>
                                    </div>
                                </div>  
                            </div>
                            <div class="row" *ngIf="group.activePutCode == group.putCode.value">
                                <div class="col-md-12 col-sm-12 bottomBuffer">
                                    <ul class="id-details">
                                        <li class="url-work">
                                            <ul class="id-details">
                                                <li *ngFor='let extID of group.affiliationExternalIdentifiers | orderBy:["-relationship.value", "type.value"]' class="url-popover">
                                                    <span *ngIf="group.affiliationExternalIdentifiers[0].value.value.length > 0">{{extID}}</span>
                                                </li>
                                            </ul>                                   
                                        </li>
                                    </ul>
                                </div>
                            </div>  
                            <div *ngIf="orgIdsFeatureEnabled">
                                <div class="more-info content" *ngIf="moreInfo[group.groupId]">
                                    <div class="row bottomBuffer">
                                        <div class="col-md-12"></div>
                                    </div>
                                    <span class="dotted-bar"></span>    
                                    <div class="row">
                                        <div class="org-ids" *ngIf="group.orgDisambiguatedId.value">
                                            <div class="col-md-12">   
                                                <strong><@orcid.msg 'workspace_affiliations.organization_id'/></strong><br>
                                                <span [innerHTML]='group.disambiguatedAffiliationSourceId.value | orgIdentifierHtml:group.disambiguationSource.value:group.putCode.value:group.disambiguationSource' class="url-popover"> 
                                                </span>
                                            </div>
                                            <div class="col-md-11 bottomBuffer info-detail leftBuffer clearfix">
                                                <span *ngIf="group.orgDisambiguatedName">{{group.orgDisambiguatedName}}</span><span *ngIf="group.orgDisambiguatedCity || group.orgDisambiguatedRegion || group.orgDisambiguatedCountry">: </span><span *ngIf="group.orgDisambiguatedCity">{{group.orgDisambiguatedCity}}</span><span *ngIf="group.orgDisambiguatedCity && group.orgDisambiguatedRegion">, </span><span *ngIf="group.orgDisambiguatedRegion">{{group.orgDisambiguatedRegion}}</span><span *ngIf="group.orgDisambiguatedCountry && (group.orgDisambiguatedCity || group.orgDisambiguatedRegion)">, </span><span *ngIf="group.orgDisambiguatedCountry">{{group.orgDisambiguatedCountry}}</span>
                                                <span *ngIf="group.orgDisambiguatedUrl"><br>
                                                <a href="{{group.orgDisambiguatedUrl}}" target="orgDisambiguatedUrl"><span>{{group.orgDisambiguatedUrl}}</span></a>
                                                </span>                                                
                                                <div *ngIf="group.orgDisambiguatedExternalIdentifiers">
                                                    <strong><@orcid.msg 'workspace_affiliations.external_ids'/> {{group.disambiguationSource.value}}</strong><br>
                                                    <ul class="reset">
                                                        <li *ngFor="let orgDisambiguatedExternalIdentifier of group.orgDisambiguatedExternalIdentifiers | orderBy:orgDisambiguatedExternalIdentifier.identifierType">
                                                            {{orgDisambiguatedExternalIdentifier.identifierType}}:  <span *ngIf="orgDisambiguatedExternalIdentifier.preferred">{{orgDisambiguatedExternalIdentifier.preferred}} <@orcid.msg 'workspace_affiliations.external_ids_preferred'/>, </span> <span *ngIf="orgDisambiguatedExternalIdentifier.all"><span *ngFor="let orgDisambiguatedExternalIdentifierAll of orgDisambiguatedExternalIdentifier.all">{{orgDisambiguatedExternalIdentifierAll}}{{$last ? '' : ', '}}</span></span></li>
                                                    </ul>
                                                </div>
                                            </div>
                                        </div>
                                        <div class="col-md-6" *ngIf="group.url.value">
                                            <div class="bottomBuffer">
                                                <strong><@orcid.msg 'common.url'/></strong><br> 
                                                <a href="{{group.url.value}}" target="affiliation.url.value">{{group.url.value}}</a>
                                            </div>
                                        </div>  
                                        <div class="col-md-12">
                                            <div class="bottomBuffer">
                                                <strong><@orcid.msg 'groups.common.created'/></strong><br> 
                                                <span>{{group.createdDate}}</span>
                                            </div>
                                        </div>  
                                    </div>
                                </div>
                            </div>
                            <div class="row source-line">
                                <div class="col-md-12 col-sm-12 col-xs-12">
                                    <div class="sources-container-header">          
                                        <div class="row">
                                            <div class="col-md-7 col-sm-7 col-xs-12">
                                                <@orcid.msg 'groups.common.source'/>: {{(group.sourceName == null || group.sourceName == '') ? group.source : group.sourceName}}    
                                            </div>                            
                                            <div class="col-md-3 col-sm-3 col-xs-6">
                                                <@orcid.msg 'groups.common.created'/>: <span>{{group.createdDate | ajaxFormDateToISO8601}}</span>
                                            </div>                                                   
                                            <div class="col-md-2 col-sm-2 col-xs-6 pull-right">
                                                <ul class="sources-options">
                                                    <#if !(isPublicProfile??)>
                                                    <li *ngIf="group.source == '${effectiveUserOrcid}'">
                                                        <a (click)="openEditAffiliation(group)" (mouseenter)="showTooltip(group.putCode.value+'-edit')" (mouseleave)="hideTooltip(group.putCode.value+'-edit')">
                                                            <span class="glyphicon glyphicon-pencil"></span>
                                                        </a>
                                                        <div class="popover popover-tooltip top edit-source-popover" *ngIf="showElement[group.putCode.value+'-edit']"> 
                                                            <div class="arrow"></div>
                                                            <div class="popover-content">
                                                                <span ><@orcid.msg 'groups.common.edit_my'/></span>
                                                            </div>                
                                                        </div>  
                                                    </li>   
                                                    <li>
                                                        <a id="delete-affiliation_{{group.putCode.value}}" href="" (click)="deleteAffiliation(group)" (mouseenter)="showTooltip(group.putCode.value+'-delete')" (mouseleave)="hideTooltip(group.putCode.value+'-delete')" class="glyphicon glyphicon-trash"></a>
                                                        <div class="popover popover-tooltip top delete-source-popover" *ngIf="showElement[group.putCode.value+'-delete']"> 
                                                            <div class="arrow"></div>
                                                            <div class="popover-content">
                                                                 <@orcid.msg 'groups.common.delete_this_source' />
                                                            </div>                
                                                        </div>
                                                    </li>
                                                    </#if>  
                                                </ul>
                                            </div>
                                        </div>                                    
                                    </div>
                                </div>
                            </div>            
                        </li>
                    </ul>
                </div>                                                  
            </div>
        </div>        
    </div>
</script>