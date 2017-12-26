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

<script type="text/ng-template" id="works-ng2-template">
    <div id="workspace-publications" class="workspace-accordion-item workspace-accordion-active">
         
        <div class="workspace-accordion-header clearfix">
            <div class="row">
                <div class="col-md-4 col-sm-3 col-xs-12">
                    <div>
                        <a href="" (click)="workspaceSrvc.toggleWorks($event)" class="toggle-text">
                           <i class="glyphicon-chevron-down glyphicon x075" [ngClass]="{'glyphicon-chevron-right':workspaceSrvc.displayWorks==false}"></i>
                           <@orcid.msg 'workspace.Works'/> (<span>{{worksSrvc.groupsLabel}}</span>)
                        </a>
                        <#if !(isPublicProfile??)> 
                        <div class="popover-help-container">
                            <a href="javascript:void(0);"><i class="glyphicon glyphicon-question-sign"></i></a>
                            <div id="works-help" class="popover bottom">
                                <div class="arrow"></div>
                                <div class="popover-content">
                                    <p><@orcid.msg 'manage_works_settings.helpPopoverWorks'/> <a href="${knowledgeBaseUri}/articles/462032" target="manage_works_settings.helpPopoverWorks"><@orcid.msg 'common.learn_more'/></a></p>
                                </div>
                            </div>
                        </div> 
                        </#if>
                    </div>
                </div>
                <div class="col-md-8 col-sm-9 col-xs-12 action-button-bar" *ngIf="workspaceSrvc.displayWorks">
                    <div class="menu-container">                                     
                        <ul class="toggle-menu">
                            <li>
                                <span class="glyphicon glyphicon-sort"></span>                          
                                <@orcid.msg 'manual_orcid_record_contents.sort'/>
                                <ul class="menu-options sort">
                                    <li [ngClass]="{'checked':sortState.predicateKey=='endDate'}" *ngIf="sortState.type == 'affiliation'">                                         
                                        <a (click)="sort('endDate');" class="action-option manage-button">
                                            <@orcid.msg 'manual_orcid_record_contents.sort_end_date'/>
                                            <span *ngIf="sortState.reverseKey['endDate']" [ngClass]="{'glyphicon glyphicon-sort-by-order-alt':sortState.predicateKey=='endDate'}"></span>
                                            <span *ngIf="sortState.reverseKey['endDate'] == false" [ngClass]="{'glyphicon glyphicon-sort-by-order':sortState.predicateKey=='endDate'}"></span>
                                        </a>                                                                                    
                                    </li>
                                    <li [ngClass]="{'checked':sortState.predicateKey=='startDate'}" *ngIf="sortState.type == 'affiliation'">                                           
                                        <a (click)="sort('startDate');" class="action-option manage-button">
                                            <@orcid.msg 'manual_orcid_record_contents.sort_start_date'/>
                                            <span *ngIf="sortState.reverseKey['startDate']" [ngClass]="{'glyphicon glyphicon-sort-by-order-alt':sortState.predicateKey=='startDate'}"></span>
                                            <span *ngIf="sortState.reverseKey['startDate'] == false" [ngClass]="{'glyphicon glyphicon-sort-by-order':sortState.predicateKey=='startDate'}"></span>
                                        </a>                                                                                    
                                    </li>
                                    <li [ngClass]="{'checked':sortState.predicateKey=='date'}" ng-hide="sortHideOption || sortState.type == 'affiliation'">                                          
                                        <a (click)="sort('date');" class="action-option manage-button">
                                            <@orcid.msg 'manual_orcid_record_contents.sort_date'/>
                                            <span *ngIf="sortState.reverseKey['date']" [ngClass]="{'glyphicon glyphicon-sort-by-order-alt':sortState.predicateKey=='date'}"></span>
                                            <span *ngIf="sortState.reverseKey['date'] == false" [ngClass]="{'glyphicon glyphicon-sort-by-order':sortState.predicateKey=='date'}"></span>
                                        </a>                                                                                    
                                    </li>
                                    <li [ngClass]="{'checked':sortState.predicateKey=='groupName'}" ng-hide="sortHideOption == null">
                                        <a (click)="sort('groupName');" class="action-option manage-button">
                                            <@orcid.msg 'manual_orcid_record_contents.sort_title'/>
                                            <span *ngIf="sortState.reverseKey['groupName']" [ngClass]="{'glyphicon glyphicon-sort-by-alphabet-alt':sortState.predicateKey=='groupName'}" ></span>
                                            <span *ngIf="sortState.reverseKey['groupName'] == false" [ngClass]="{'glyphicon glyphicon-sort-by-alphabet':sortState.predicateKey=='groupName'}" ></span>
                                        </a>                                            
                                    </li>
                                    <li [ngClass]="{'checked':sortState.predicateKey=='title'}" ng-hide="sortHideOption">                                            
                                        <a (click)="sort('title');" class="action-option manage-button">
                                            <@orcid.msg 'manual_orcid_record_contents.sort_title'/>
                                            <span *ngIf="sortState.reverseKey['title']" [ngClass]="{'glyphicon glyphicon-sort-by-alphabet-alt':sortState.predicateKey=='title'}" ></span>
                                            <span *ngIf="sortState.reverseKey['title'] == false" [ngClass]="{'glyphicon glyphicon-sort-by-alphabet':sortState.predicateKey=='title'}" ></span>
                                        </a>                                            
                                    </li>
                                    <li [ngClass]="{'checked':sortState.predicateKey=='type'}" ng-hide="sortHideOption || sortState.type == 'affiliation'">                                          
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
                    <#if !(isPublicProfile??)>
                    <ul class="workspace-bar-menu">
                
                        <li *ngIf="worksSrvc.groups.length > 1" >
                            <a class="action-option works manage-button" [ngClass]="{'green-bg' : bulkEditShow == true}" (click)="toggleBulkEdit()">
                                <span class="glyphicon glyphicon-pencil"></span><@orcid.msg 'groups.common.bulk_edit'/>
                            </a>
                        </li>

                        <li *ngIf="worksSrvc.groups.length > 0" >
                            <a class="action-option works manage-button" [ngClass]="{'green-bg' : showBibtexExport}" (click)="toggleBibtexExport()">
                                <span class="glyphicon glyphicon-save"></span>
                                <@orcid.msg 'groups.common.export_works'/>
                            </a>
                        </li>

                        <li class="hidden-xs">
                            <div class="menu-container" id="add-work-container">
                                <ul class="toggle-menu">
                                    <li [ngClass]="{'green-bg' : showBibtexImportWizard == true || workImportWizard == true}"> 
                                        <span class="glyphicon glyphicon-plus"></span>
                                        <@orcid.msg 'groups.common.add_works'/>
                                        <ul class="menu-options works">
                                          
                                            <li *ngIf="noLinkFlag">
                                                <a *ngIf="noLinkFlag" class="action-option manage-button" (click)="showWorkImportWizard()">
                                                    <span class="glyphicon glyphicon-cloud-upload"></span>
                                                    <@orcid.msg 'manual_orcid_record_contents.search_link'/>
                                                </a>
                                            </li>
                                            
                                            <li>
                                                <a class="action-option manage-button" (click)="openBibTextWizard()">
                                                    <span class="glyphicons file_import bibtex-wizard"></span>
                                                    <@orcid.msg 'workspace.bibtexImporter.link_bibtex'/>
                                                </a>
                                            </li>
                                          
                                            <li>
                                                <a id="add-work" class="action-option manage-button" (click)="addWorkModal()">
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
                            <a *ngIf="noLinkFlag" class="action-option manage-button" (click)="showWorkImportWizard()">
                                <span class="glyphicon glyphicon-cloud-upload"></span>
                                <@orcid.msg 'manual_orcid_record_contents.search_link'/>
                            </a>
                        </li>
                  
                        <li class="hidden-md hidden-sm visible-xs-inline">
                            <a class="action-option manage-button" (click)="openBibTextWizard()">
                                <span class="glyphicons file_import bibtex-wizard"></span>
                                <@orcid.msg 'workspace.bibtexImporter.link_bibtex'/>
                            </a>
                        </li>
                    
                        <li class="hidden-md hidden-sm visible-xs-inline">
                            <a class="action-option manage-button" (click)="addWorkModal()">
                                <span class="glyphicon glyphicon-plus"></span>
                                <@orcid.msg 'manual_orcid_record_contents.link_manually'/>
                            </a>
                        </li>
                    </ul>
                    </#if>
                </div>
            </div>
        </div>

        <!-- Work Import Wizard -->
        <div *ngIf="workImportWizard" class="work-import-wizard">
            <div class="ie7fix-inner">
                <div class="row"> 
                    <div class="col-md-12 col-sm-12 col-xs-12">
                        <h1 class="lightbox-title wizard-header"><@orcid.msg 'workspace.link_works'/></h1>
                        <span (click)="showWorkImportWizard()" class="close-wizard"><@orcid.msg 'workspace.LinkResearchActivities.hide_link_works'/></span>
                    </div>
                </div>
                <div class="row">
                    <div class="col-md-12 col-sm-12 col-xs-12">
                        <p class="wizard-content">
                            <@orcid.msg 'workspace.LinkResearchActivities.description'/> <@orcid.msg 'workspace.LinkResearchActivities.description.more_info'/>
                        </p>
                    </div>
                </div>
                <div class="row">
                    <div id="workFilters">
                        <form class="form-inline">
                            <div class="col-md-5 col-sm-5 col-xs-12">
                                <div class="form-group">
                                    <label for="work-type"><@orcid.msg 'workspace.link_works.filter.worktype'/></label> 
                                     
                                    <select id="work-type" ng-options="wt as wt for wt in workType | orderBy: 'toString()'" ng-model="selectedWorkType"></select>    
                                                   
                                </div> 
                            </div>
                            <div class="col-md-7 col-sm-7 col-xs-12">
                                <div class="form-group geo-area-group">
                                    <label for="geo-area"><@orcid.msg 'workspace.link_works.filter.geographicalarea'/></label>  
                                    
                                    <select ng-options="ga as ga for ga in geoArea | orderBy: 'toString()'" ng-model="selectedGeoArea"></select>  
                                                       
                                </div>
                            </div>  
                        </form>
                        <hr />
                    </div>
                </div>         
                <div class="row wizards">               
                    <div class="col-md-12 col-sm-12 col-xs-12">
                        <div *ngFor="let wtw of workImportWizardsOriginal | orderBy: 'name' | filterImportWizards : selectedWorkType : selectedGeoArea">
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

        <!-- Bulk Edit --> 
            
        <div *ngIf="bulkEditShow && workspaceSrvc.displayWorks" >           
            <div class="bulk-edit">
                <div class="row">
                    <div class="col-md-7 col-sm-7 col-xs-12">
                        <h4><@orcid.msg 'workspace.bulkedit.title'/></h4><span class="hide-bulk" (click)="toggleBulkEdit()"><@orcid.msg 'workspace.bulkedit.hide'/></span>
                        <ol>
                            <li><@orcid.msg 'workspace.bulkedit.selectWorks'/></li>
                            <li><@orcid.msg 'workspace.bulkedit.selectAction'/></li>
                        </ol>
                    </div>
                    <div class="col-md-5 col-sm-5 col-xs-12">
                        <ul class="bulk-edit-toolbar">

                            <li class="bulk-edit-toolbar-item work-multiple-selector">
                                <label><@orcid.msg 'workspace.bulkedit.select'/></label>
                                <div id="custom-control-x">
                                    <div class="custom-control-x" > 
                                        <div class="dropdown-custom-menu" id="dropdown-custom-menu" (click)="toggleSelectMenu();$event.stopPropagation()">                   
                                            <span class="custom-checkbox-parent">
                                                <div class="custom-checkbox" id="custom-checkbox" (click)="swapbulkChangeAll();$event.stopPropagation();" [ngClass]="{'custom-checkbox-active':bulkChecked}"></div>
                                            </span>                   
                                            <div class="custom-control-arrow" (click)="toggleSelectMenu(); $event.stopPropagation()"></div>                            
                                        </div>
                                        <div>
                                            <ul class="dropdown-menu" role="menu" id="special-menu" [ngClass]="{'block': bulkDisplayToggle}">
                                                <li><a (click)="bulkChangeAll(true)"><@orcid.msg 'workspace.bulkedit.selected.all'/></a></li>
                                                <li><a (click)="bulkChangeAll(false)"><@orcid.msg 'workspace.bulkedit.selected.none'/></a></li>                                                
                                            </ul>     
                                        </div>
                                    </div>
                                </div>
                            </li>
                            <li class="bulk-edit-toolbar-item">
                                <label><@orcid.msg 'workspace.bulkedit.edit'/></label>
                                <div class="bulk-edit-privacy-control">
                         
                                    <@orcid.privacyToggle2 angularModel="groupPrivacy()" 
                                    questionClick=""
                                    clickedClassCheck=""
                                    publicClick="setBulkGroupPrivacy('PUBLIC', $event)" 
                                    limitedClick="setBulkGroupPrivacy('LIMITED', $event)" 
                                    privateClick="setBulkGroupPrivacy('PRIVATE', $event)"/>
                                  
                                </div>                      
                            </li>                   
                        </ul>
                        <div class="bulk-edit-delete">
                            <div class="centered">
                                <a (click)="deleteBulkConfirm()" class="ignore toolbar-button edit-item-button" ng-mouseenter="showTooltip('Bulk-Edit')" ng-mouseleave="hideTooltip('Bulk-Edit')">
                                    <span class="edit-option-toolbar glyphicon glyphicon-trash"></span>
                                </a>
                                <div class="popover popover-tooltip top bulk-edit-popover" *ngIf="showElement['Bulk-Edit']">
                                    <div class="arrow"></div>
                                    <div class="popover-content">
                                        <span><@orcid.msg 'workspace.bulkedit.delete'/></span>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>              
                </div>              
            </div>
        </div>
    

        <!-- BibTeX Export Layout -->        
        <div *ngIf="showBibtexExport && workspaceSrvc.displayWorks" class="bibtex-box">
            <div class="box-border" *ngIf="canReadFiles" >
                <h4><@orcid.msg 'workspace.bibtexExporter.export_bibtex'/></h4><span (click)="toggleBibtexExport()" class="hide-importer"><@orcid.msg 'workspace.bibtexExporter.hide'/></span>
                <div class="row full-height-row">
                    <div class="col-md-9 col-sm-9 col-xs-8">
                        <p>
                            <@orcid.msg 'workspace.bibtexExporter.intro'/>
                        </p> 
                    </div>
                    <div class="col-md-3 col-sm-3 col-xs-4">
                        <span class="bibtext-options">                                        
                            <a class="bibtex-cancel" (click)="toggleBibtexExport()"><@orcid.msg 'workspace.bibtexExporter.cancel'/></a>             
                            <span ng-hide="worksFromBibtex.length > 0" class="import-label" (click)="fetchBibtexExport()"><@orcid.msg 'workspace.bibtexExporter.export'/></span>                   
                        </span>                   
                    </div>
                </div>
            </div>

            <div class="bottomBuffer" *ngIf="bibtexLoading && !bibtexExportError" >
                <span class="dotted-bar"></span>
                <ul class="inline-list">
                    <li>
                        <@orcid.msg 'workspace.bibtexExporter.generating'/>
                    </li>
                    <li>
                        &nbsp;<span><i id="" class="glyphicon glyphicon-refresh spin x1 green"></i></span>    
                    </li>
                </ul>
            </div>
    
            <div class="alert alert-block" *ngIf="bibtexExportError">
                <strong><@orcid.msg 'workspace.bibtexExporter.error'/></strong>
            </div>
          
        </div>   

        <!-- Bibtex Importer Wizard -->
        <div *ngIf="showBibtexImportWizard && workspaceSrvc.displayWorks"  class="bibtex-box">
            <div class="box-border" *ngIf="canReadFiles" >
                <h4><@orcid.msg 'workspace.bibtexImporter.link_bibtex'/></h4><span (click)="openBibTextWizard()" class="hide-importer"><@orcid.msg 'workspace.bibtexImporter.hide_link_bibtex'/></span>
                <div class="row full-height-row">
                    <div class="col-md-9 col-sm-9 col-xs-8">
                        <p>
                            <@orcid.msg 'workspace.bibtexImporter.instructions'/>  <a href="${knowledgeBaseUri}/articles/390530#2import" target="workspace.bibtexImporter.learnMore"><@orcid.msg 'workspace.bibtexImporter.learnMore'/></a>.
                        </p> 
                    </div>
                    <div class="col-md-3 col-sm-3 col-xs-4">
                        <span class="bibtext-options">                                        
                            <a class="bibtex-cancel" (click)="openBibTextWizard()"><@orcid.msg 'workspace.bibtexImporter.cancel'/></a>            
                            <span ng-hide="worksFromBibtex.length > 0" class="import-label" (click)="openFileDialog()"><@orcid.msg 'workspace.bibtexImporter.fileUpload'/></span>
                            <span *ngIf="worksFromBibtex.length > 0" class="import-label" (click)="saveAllFromBibtex()"><@orcid.msg 'workspace.bibtexImporter.save_all'/></span>                                              
                            <input id="inputBibtex" type="file" class="upload-button" ng-model="textFiles" accept="*" update-fn="loadBibtexJs()"  app-file-text-reader multiple />
                        </span>                   
                    </div>
                </div>
            </div>            
            <div class="alert alert-block" *ngIf="bibtexParsingError">
                <strong><@orcid.msg 'workspace.bibtexImporter.parsingError'/></strong>
            </div>
            <span class="dotted-bar" *ngIf="worksFromBibtex.length > 0"></span>

            <div *ngFor="let work of worksFromBibtex"  class="bottomBuffer">             
                <div class="row full-height-row">   
                    <div class="col-md-9 col-sm-9 col-xs-7">
                        <h3 class="workspace-title" [ngClass]="work.title.value == null ? 'bibtex-content-missing' :  ''">
                            <span *ngIf="work.title.value != null">{{work.title.value}}</span>
                            <span *ngIf="work.title.value == null">&lt;<@orcid.msg 'workspace.bibtexImporter.work.title_missing' />&gt;</span>
                            <span class="journaltitle" *ngIf="work.journalTitle.value">{{work.journalTitle.value}}</span>
                        </h3>

                        <div class="info-detail">
                            <span *ngIf="work.publicationDate.year">{{work.publicationDate.year}}</span><span *ngIf="work.publicationDate.month">-{{work.publicationDate.month}}</span><span *ngIf="work.publicationDate.day">-</span><span *ngIf="work.publicationDate.day">{{work.publicationDate.day}}</span><span *ngIf="work.publicationDate.year"> | </span>

                  
                            <span class="capitalize" *ngIf="work.workType.value.length > 0">{{work.workType.value}}</span>
                            <span class="bibtex-content-missing small-missing-info" *ngIf="work.workType.value.length == 0">&lt;<@orcid.msg 'workspace.bibtexImporter.work.type_missing' />&gt;</span>

                     

                        </div>
                        <div class="row" *ngIf="group.activePutCode == work.putCode.value">
                            <div class="col-md-12 col-sm-12 bottomBuffer">
                                <ul class="id-details">
                                    <li class="url-work">
                                        <ul class="id-details">
                                            <li *ngFor='let ie of work.workExternalIdentifiers | orderBy:["-relationship.value", "workExternalIdentifierType.value"]' class="url-popover">

                                                <span *ngIf="work.workExternalIdentifiers[0].workExternalIdentifierId.value.length > 0" >bind-html-compile='ie | workExternalIdentifierHtml:$first:$last:work.workExternalIdentifiers.length:moreInfo[group.groupId]'</span>
                                            </li>
                                        </ul>                                   
                                    </li>

                                    <li *ngIf="work.url.value" class="url-popover url-work">
                                        <@orcid.msg 'common.url' />: <a href="{{work.url.value | urlProtocol}}" ng-mouseenter="showURLPopOver(work.putCode.value)" ng-mouseleave="hideURLPopOver(work.putCode.value)" [ngClass]="{'truncate-anchor' : moreInfo[group.groupId] == false || moreInfo[group.groupId] == undefined}" target="work.url.value">{{work.url.value}}</a>
                                        <div class="popover-pos">                                   
                                            <div class="popover-help-container">
                                                <div class="popover bottom" [ngClass]="{'block' : displayURLPopOver[work.putCode.value] == true}">
                                                    <div class="arrow"></div>
                                                    <div class="popover-content">
                                                        <a href="{{work.url.value}}" target="work.url.value">{{work.url.value}}</a>
                                                    </div>                
                                                </div>                              
                                            </div>
                                        </div>
                                    </li>
                                </ul>
                            </div>
                        </div>
                    </div>                          
                    <div class="col-md-3 col-sm-3 col-xs-3 bibtex-options-menu">                            
                        <ul>
                            <li><a (click)="rmWorkFromBibtex(work)" class="ignore glyphicon glyphicon-trash bibtex-button" title="Ignore"></a></li>
                            <li><a *ngIf="work.errors.length == 0" (click)="addWorkFromBibtex(work)" class="save glyphicon glyphicon-floppy-disk bibtex-button" title="Save"></a></li>
                            <li><a *ngIf="work.errors.length > 0" (click)="editWorkFromBibtex(work)" class="save glyphicon glyphicon-pencil bibtex-button" title="Edit"></a></li>
                            <li><span *ngIf="work.errors.length > 0"><a (click)="editWorkFromBibtex(work)"><i class="glyphicon glyphicon-exclamation-sign"></i><@orcid.msg 'workspace.bibtexImporter.work.warning' /></a></span></li>
                        </ul>
                    </div>
                </div>
            </div>
        </div>

        <div *ngIf="workspaceSrvc.displayWorks" class="workspace-accordion-content">
            include "includes/work/add_work_modal_inc.ftl"
            include "includes/work/del_work_modal_inc.ftl"
            include "includes/work/body_work_inc_v3.ftl"           
        </div>
    </div>
</script>