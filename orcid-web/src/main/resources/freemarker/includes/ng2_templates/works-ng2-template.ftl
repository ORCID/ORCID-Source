<script type="text/ng-template" id="works-ng2-template">
    <!--WORKS-->
    <div id="workspace-publications" class="workspace-accordion-item workspace-accordion-active">
        <!--Works section header--> 
        <div class="workspace-accordion-header clearfix">
            <div class="row">
                <div class="col-md-4 col-sm-3 col-xs-12">
                    <div>
                        <a (click)="workspaceSrvc.toggleWorks()" class="toggle-text">
                           <i class="glyphicon-chevron-down glyphicon x075" [ngClass]="{'glyphicon-chevron-right':workspaceSrvc.displayWorks==false}"></i>
                           <@orcid.msg 'workspace.Works'/> (<span>{{worksService.groupsLabel}}</span>)
                        </a>
                        <#if !(isPublicProfile??)> 
                        <div class="popover-help-container">
                            <i class="glyphicon glyphicon-question-sign"></i>
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
                    <!--Sort menu-->
                    <div class="menu-container">                                     
                        <ul class="toggle-menu">
                            <li>
                                <span class="glyphicon glyphicon-sort"></span>
                                <@orcid.msg 'manual_orcid_record_contents.sort'/>
                                <ul class="menu-options sort">
                                    <li [ngClass]="{'checked':sortState.predicateKey=='date'}" *ngIf="!(sortHideOption || sortState.type == 'affiliation')">                                          
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
                                    <li [ngClass]="{'checked':sortState.predicateKey=='type'}" *ngIf="!(sortHideOption || sortState.type == 'affiliation')">                                          
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
                    <!--End sort menu-->
                    <#if !(isPublicProfile??)>
                    <ul class="workspace-bar-menu">
                        <!--Bulk edit-->
                        <li *ngIf="worksService?.groups?.length > 1" >
                            <a class="action-option works manage-button" [ngClass]="{'green-bg' : bulkEditShow == true}" (click)="toggleBulkEdit()">
                                <span class="glyphicon glyphicon-pencil"></span><@orcid.msg 'groups.common.bulk_edit'/>
                            </a>
                        </li>
                        <!--Export bibtex-->
                        <li *ngIf="worksService?.groups?.length > 0" >
                            <a class="action-option works manage-button" [ngClass]="{'green-bg' : showBibtexExport}" (click)="toggleBibtexExport()">
                                <span class="glyphicon glyphicon-save"></span>
                                <@orcid.msg 'groups.common.export_works'/>
                            </a>
                        </li>
                        <!--Add works-->
                        <li class="hidden-xs">
                            <div class="menu-container" id="add-work-container">
                                <ul class="toggle-menu">
                                    <li [ngClass]="{'green-bg' : showBibtexImportWizard == true || workImportWizard == true}"> 
                                        <span class="glyphicon glyphicon-plus"></span>
                                        <@orcid.msg 'groups.common.add_works'/>
                                        <ul class="menu-options works">
                                          <!--Search & link-->
                                            <li *ngIf="noLinkFlag">
                                                <a *ngIf="noLinkFlag" class="action-option manage-button" (click)="showWorkImportWizard()">
                                                    <span class="glyphicon glyphicon-cloud-upload"></span>
                                                    <@orcid.msg 'manual_orcid_record_contents.search_link'/>
                                                </a>
                                            </li>
                                            <!--Bibtex-->
                                            <li>
                                                <a class="action-option manage-button" (click)="openBibTextWizard()">
                                                    <span class="glyphicons file_import bibtex-wizard"></span>
                                                    <@orcid.msg 'workspace.bibtexImporter.link_bibtex'/>
                                                </a>
                                            </li>
                                            <!--Add manually-->
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
                        <!--Search & link-->
                        <li class="hidden-md hidden-sm visible-xs-inline">
                            <a *ngIf="noLinkFlag" class="action-option manage-button" (click)="showWorkImportWizard()">
                                <span class="glyphicon glyphicon-cloud-upload"></span>
                                <@orcid.msg 'manual_orcid_record_contents.search_link'/>
                            </a>
                        </li>
                        <!--Bibtex-->
                        <li class="hidden-md hidden-sm visible-xs-inline">
                            <a class="action-option manage-button" (click)="openBibTextWizard()">
                                <span class="glyphicons file_import bibtex-wizard"></span>
                                <@orcid.msg 'workspace.bibtexImporter.link_bibtex'/>
                            </a>
                        </li>
                        <!--Mobile workaraound-->
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
        <!--End works section header-->
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
                                    
                                    <!-- ***
                                    <select id="work-type" ng-options="wt as wt for wt in workType | orderBy: 'toString()'" [(ngModel)]="selectedWorkType"></select>  
                                    -->  
                                                   
                                </div> 
                            </div>
                            <div class="col-md-7 col-sm-7 col-xs-12">
                                <div class="form-group geo-area-group">
                                    <label for="geo-area"><@orcid.msg 'workspace.link_works.filter.geographicalarea'/></label>  
                                    
                                    <!-- ***
                                    <select ng-options="ga as ga for ga in geoArea | orderBy: 'toString()'" [(ngModel)]="selectedGeoArea"></select>  
                                    -->
                                </div>
                            </div>  
                        </form>
                        <hr />
                    </div>
                </div>         
                <div class="row wizards">               
                    <div class="col-md-12 col-sm-12 col-xs-12">
                        <div *ngFor="let wtw of workImportWizardsOriginal | orderBy: 'name' | filterImportWizards : selectedWorkType : selectedGeoArea; let index = index; let first = first; let last = last;">
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
        <!--End import wizard-->
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
                                    <@orcid.privacyToggle2Ng2 angularModel="none" elementId="none" 
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
                                <a (click)="deleteBulkConfirm()" class="ignore toolbar-button edit-item-button" (mouseenter)="showTooltip('Bulk-Edit')" (mouseleave)="hideTooltip('Bulk-Edit')">
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
        <!--End bulk edit-->
        <!-- Bibtex export -->        
        <div *ngIf="showBibtexExport && workspaceSrvc.displayWorks" class="bibtex-box">
            <div *ngIf="canReadFiles" >
                <h4><@orcid.msg 'workspace.bibtexExporter.export_bibtex'/></h4><span (click)="toggleBibtexExport()" class="hide-importer"><@orcid.msg 'workspace.bibtexExporter.hide'/></span>
                <div class="row full-height-row">
                    <div class="col-md-9 col-sm-9 col-xs-8">
                        <p>
                            <@orcid.msg 'workspace.bibtexExporter.intro_1'/><a href="https://support.orcid.org/knowledgebase/articles/1807552-exporting-works-into-a-bibtex-file" target="exporting_bibtex" style="word-break\: normal;"><@orcid.msg 'workspace.bibtexExporter.intro_2'/></a><@orcid.msg 'workspace.bibtexExporter.intro_3'/>
                        </p> 
                    </div>
                    <div class="col-md-3 col-sm-3 col-xs-4">
                        <span class="bibtext-options">                                        
                            <a class="bibtex-cancel" (click)="toggleBibtexExport()"><@orcid.msg 'workspace.bibtexExporter.cancel'/></a>             
                            <span *ngIf="!(worksFromBibtex?.length > 0)" class="import-label" (click)="fetchBibtexExport()"><@orcid.msg 'workspace.bibtexExporter.export'/></span>                   
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
        <!--End bibtex export-->
        <!-- Bibtex Importer Wizard -->
        <div *ngIf="showBibtexImportWizard && workspaceSrvc.displayWorks"  class="bibtex-box">
            <div *ngIf="canReadFiles" >
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
                            <span *ngIf="!(worksFromBibtex?.length > 0)" class="import-label" (click)="openFileDialog()"><@orcid.msg 'workspace.bibtexImporter.fileUpload'/></span>
                            <span *ngIf="worksFromBibtex?.length > 0" class="import-label" (click)="saveAllFromBibtex()"><@orcid.msg 'workspace.bibtexImporter.save_all'/></span>                                              
                            <input id="inputBibtex" name="inputBibtex" type="file" class="upload-button" [(ngModel)]="textFiles" accept="*" /><!-- *** update-fn="loadBibtexJs()"  app-file-text-reader multiple  -->
                        </span>                   
                    </div>
                </div>
            </div>            
            <div class="alert alert-block" *ngIf="bibtexParsingError">
                <strong><@orcid.msg 'workspace.bibtexImporter.parsingError'/></strong>
            </div>
            <span class="dotted-bar" *ngIf="worksFromBibtex?.length > 0"></span>
            <!-- Bibtex Import Results List -->
            <div *ngFor="let work of worksFromBibtex; let index = index; let first = first; let last = last;" class="bottomBuffer">             
                <div class="row full-height-row">   
                    <div class="col-md-9 col-sm-9 col-xs-7">
                        <h3 class="workspace-title" [ngClass]="work.title?.value == null ? 'bibtex-content-missing' :  ''">
                            <span *ngIf="work.title?.value != null">{{work.title.value}}</span>
                            <span *ngIf="work.title?.value == null">&lt;<@orcid.msg 'workspace.bibtexImporter.work.title_missing' />&gt;</span>
                            <span class="journaltitle" *ngIf="work.journalTitle?.value">{{work.journalTitle.value}}</span>
                        </h3>

                        <div class="info-detail">
                            <span *ngIf="work.publicationDate.year">{{work.publicationDate.year}}</span><span *ngIf="work.publicationDate?.month">-{{work.publicationDate.month}}</span><span *ngIf="work.publicationDate?.day">-</span><span *ngIf="work.publicationDate?.day">{{work.publicationDate.day}}</span><span *ngIf="work.publicationDate.year"> | </span>
                  
                            <span class="capitalize" *ngIf="work.workType?.value?.length > 0">{{work.workType.value}}</span>
                            <span class="bibtex-content-missing small-missing-info" *ngIf="work.workType?.value.length == 0">&lt;<@orcid.msg 'workspace.bibtexImporter.work.type_missing' />&gt;</span>

                        </div>
                        <div class="row" *ngIf="group.activePutCode == work.putCode?.value">
                            <div class="col-md-12 col-sm-12 bottomBuffer">
                                <ul class="id-details">
                                    <li class="url-work">
                                        <ul class="id-details">
                                            <li *ngFor='let ie of work.workExternalIdentifiers | orderBy:["-relationship.value", "workExternalIdentifierType.value"]; let index = index; let first = first; let last = last;' class="url-popover">
                                                <!-- ***
                                                <span *ngIf="work.workExternalIdentifiers[0].workExternalIdentifierId.value.length > 0" >{{ie | workExternalIdentifierHtml:first:last:work.workExternalIdentifiers.length:moreInfo[group.groupId]}}</span>
                                                -->
                                            </li>
                                        </ul>                                   
                                    </li>

                                    <li *ngIf="work.url?.value" class="url-popover url-work">
                                        <@orcid.msg 'common.url' />: <a href="{{work.url.value | urlProtocol}}" (mouseenter)="showURLPopOver(work.putCode.value)" (mouseleave)="hideURLPopOver(work.putCode.value)" [ngClass]="{'truncate-anchor' : moreInfo[group.groupId] == false || moreInfo[group.groupId] == undefined}" target="work.url.value">{{work.url.value}}</a>
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
                            <li><a *ngIf="work?.errors?.length == 0" (click)="addWorkFromBibtex(work)" class="save glyphicon glyphicon-floppy-disk bibtex-button" title="Save"></a></li>
                            <li><a *ngIf="work?.errors?.length > 0" (click)="editWorkFromBibtex(work)" class="save glyphicon glyphicon-pencil bibtex-button" title="Edit"></a></li>
                            <li><span *ngIf="work?.errors?.length > 0"><a (click)="editWorkFromBibtex(work)"><i class="glyphicon glyphicon-exclamation-sign"></i><@orcid.msg 'workspace.bibtexImporter.work.warning' /></a></span></li>
                        </ul>
                    </div>
                </div>
            </div>
        </div>
        <!--End bibtex import-->
        <!--Works list-->
        <div *ngIf="workspaceSrvc.displayWorks" class="workspace-accordion-content">
            <ul *ngIf="worksService?.groups?.length" class="workspace-publications bottom-margin-medium" id="body-work-list">
                <li class="bottom-margin-small workspace-border-box card" *ngFor="let group of worksService.groups">
                    <div class="work-list-container">
                        <ul class="sources-edit-list">
                            <!--Edit sources-->
                            <li *ngIf="editSources[group.groupId]" class="source-header" [ngClass]="{'source-active' : editSources[group.groupId] == true}" [(ngModel)]="group.works">
                                <div class="sources-header">
                                    <div class="row">
                                        <div class="col-md-7 col-sm-7 col-xs-6">
                                            <@orcid.msg 'groups.common.sources' /> <span class="hide-sources" (click)="hideSources(group)"><@orcid.msg 'groups.common.close_sources' /></span>
                                        </div>
                                        
                                        <div class="col-md-2 col-sm-2 hidden-xs">
                                            <@orcid.msgCapFirst 'groups.common.preferred' />
                                        </div>
                                        
                                        <div class="col-md-3 col-sm-3 col-xs-6 right padding-left-fix">
                                            <div class="workspace-toolbar">
                                                <ul class="workspace-private-toolbar">
                                                    <#if !(isPublicProfile??)>
                                                    <li *ngIf="bulkEditShow">
                                                        <input type="checkbox" name="bulkEditSelectAll" [(ngModel)]="bulkEditMap[group.activePutCode]" class="bulk-edit-input-header">
                                                    </li>
                                                    </#if>                
                                                    <li class="works-details">
                                                        <a (click)="showDetailsMouseClick(group,$event);showMozillaBadges(group.activePutCode)" (mouseenter)="showTooltip(group?.groupId+'-showHideDetails')" (mouseleave)="hideTooltip(group?.groupId+'-showHideDetails')">
                                                            <span [ngClass]="(moreInfo[group?.groupId] == true) ? 'glyphicons collapse_top' : 'glyphicons expand'">
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
                                                    <#if !(isPublicProfile??)>
                                                    <li>
                                                        <!-- ***
                                                        <@orcid.privacyToggle2 angularModel="group.activeVisibility"
                                                            questionClick="toggleClickPrivacyHelp(group.activePutCode)"
                                                            clickedClassCheck="{'popover-help-container-show':privacyHelp[group.activePutCode]==true}"
                                                            publicClick="worksService.setGroupPrivacy(group.activePutCode, 'PUBLIC', $event)"
                                                            limitedClick="worksService.setGroupPrivacy(group.activePutCode, 'LIMITED', $event)"
                                                            privateClick="worksService.setGroupPrivacy(group.activePutCode, 'PRIVATE', $event)"/>
                                                        -->
                                                    </li>
                                                    </#if>
                                                </ul>
                                            </div>
                                        </div>
                                    </div>
                                </div>
                            </li>
                            <!--End edit sources-->
                            <!---Work info-->
                            <li *ngFor="let work of group.works; let index = index; let first = first; let last = last;"><!--  orcid-put-code="{{work.putCode.value}}"*** -->
                                <div *ngIf="group.activePutCode == work.putCode.value || editSources[group.groupId] == true">

                                    <div class="row" *ngIf="group.activePutCode == work.putCode?.value">
                                        <div class="col-md-9 col-sm-9 col-xs-7">
                                            <h3 class="workspace-title">
                                                <span>{{work.title.value}}</span>
                                                <span class="journaltitle" *ngIf="work.journalTitle?.value">{{work.journalTitle.value}}</span>                                
                                            </h3>                                                        
                                            <div class="info-detail">
                                                <span *ngIf="work.publicationDate?.year">{{work.publicationDate.year}}</span><span *ngIf="work.publicationDate?.month">-{{work.publicationDate.month}}</span><span *ngIf="work.publicationDate?.day">-</span><span *ngIf="work.publicationDate?.day">{{work.publicationDate.day}}</span><span *ngIf="work.publicationDate?.year"> | </span> <span class="capitalize">{{work.workType.value}}</span>
                                            </div>
                                        </div>


                                        <div class="col-md-3 col-sm-3 col-xs-5 workspace-toolbar">
                                            <ul class="workspace-private-toolbar" *ngIf="!editSources[group.groupId]"> 

                                                <!--Bulk edit checkbox-->                               
                                                <#if !(isPublicProfile??)>
                                                <li *ngIf="bulkEditShow" class="bulk-checkbox-item">
                                                    <input type="checkbox" name="bulkEditSelectAll" [(ngModel)]="bulkEditMap[work.putCode.value]" class="bulk-edit-input ng-pristine ng-valid pull-right">       
                                                </li>
                                                </#if> 
                                                <!--Show details toggle-->
                                                <li class="works-details" *ngIf="!editSources[group.groupId]">
                                                    <a (click)="showDetailsMouseClick(group,$event);showMozillaBadges(group.activePutCode)" (mouseenter)="showTooltip(group?.groupId+'-showHideDetails')" (mouseleave)="hideTooltip(group?.groupId+'-showHideDetails')">
                                                        <span [ngClass]="(moreInfo[group?.groupId] == true) ? 'glyphicons collapse_top' : 'glyphicons expand'">
                                                        </span>
                                                    </a>
                                                    <div class="popover popover-tooltip top show-hide-details-popover" *ngIf="showElement[group.groupId+'-showHideDetails']">
                                                        <div class="arrow"></div>
                                                        <div class="popover-content">
                                                            <span *ngIf="moreInfo[group.groupId] == false || moreInfo[group.groupId] == null"><@orcid.msg 'common.details.show_details' /></span>   
                                                            <span *ngIf="moreInfo[group.groupId]"><@orcid.msg 'common.details.hide_details' /></span>
                                                        </div>
                                                    </div>
                                                </li>
                                                <!--Visibility selector-->
                                                <#if !(isPublicProfile??)>
                                                <li>
                                                    <@orcid.privacyToggle2Ng2 angularModel="work.visibility.visibility"
                                                    elementId="group.groupId" questionClick="toggleClickPrivacyHelp(group.highestVis())" clickedClassCheck="{'popover-help-container-show':privacyHelp[work.putCode.value]==true}" publicClick="setGroupPrivacy(work.putCode.value, 'PUBLIC', $event)" limitedClick="setGroupPrivacy(work.putCode.value, 'LIMITED', $event)" privateClick="setGroupPrivacy(work.putCode.value, 'PRIVATE', $event)" />
                                                </li>
                                                </#if>
                                            </ul>
                                            <!--Inconsistent visibility warning-->  
                                            <#if !(isPublicProfile??)>
                                            <div *ngIf="!worksService.consistentVis(group) && !editSources[group.groupId]" class="vis-issue">
                                                <div class="popover-help-container">
                                                    <span class="glyphicons circle_exclamation_mark" (mouseleave)="hideTooltip('vis-issue')" (mouseenter)="showTooltip('vis-issue')"></span>
                                                    <div class="popover vis-popover bottom" *ngIf="showElement['vis-issue']">
                                                        <div class="arrow"></div>
                                                        <div class="popover-content">
                                                            <@orcid.msg 'groups.common.data_inconsistency' />                                            
                                                        </div>
                                                    </div>
                                                </div>                                    
                                            </div>
                                            </#if>
                                        </div>
                                    </div>
                                    <!--Identifiers-->
                                    <div class="row" *ngIf="group.activePutCode == work.putCode.value">
                                        <div class="col-md-12 col-sm-12 bottomBuffer">
                                            <ul class="id-details clearfix">
                                                <li class="url-work clearfix">
                                                    <ul class="id-details clearfix">
                                                        <li *ngFor='let extID of work?.workExternalIdentifiers;let i = index;trackBy:trackByIndex | orderBy:["-relationship.value", "type.value"]' class="url-popover">
                                                            <span *ngIf="work?.workExternalIdentifiers[0]?.workExternalIdentifierId?.value?.length > 0">
                                                                <ext-id-popover-ng2 [extID]="extID" [putCode]="work.putCode.value+i" [activityType]="'work'"></ext-id-popover-ng2>
                                                            </span>
                                                         </li>
                                                    </ul>                                   
                                                </li>
                                                <li *ngIf="work.url?.value" class="url-popover url-work">
                                                    <@orcid.msg 'common.url' />: <a href="{{work.url.value | urlProtocol}}" (mouseenter)="showURLPopOver(work.putCode.value)" (mouseleave)="hideURLPopOver(work.putCode.value)" [ngClass]="{'truncate-anchor' : moreInfo[group.groupId] == false || moreInfo[group.groupId] == undefined}" target="work.url.value">{{work.url.value}}</a>
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
 
                                    <div class="more-info" *ngIf="moreInfo[group?.groupId] && group.activePutCode == work.putCode.value">
                                        <div id="ajax-loader" *ngIf="worksService.details[work.putCode.value] == undefined">
                                            <span id="ajax-loader"><i id="ajax-loader" class="glyphicon glyphicon-refresh spin x4 green"></i></span>
                                        </div>
                                        
                                        <div class="content" *ngIf="worksService.details[work.putCode.value] != undefined">  
                                            
                                            <span class="dotted-bar"></span>
                                            <div class="row">       
                                                <div class="col-md-6" *ngIf="worksService.details[work.putCode.value].translatedTitle?.content" >
                                                    <div class="bottomBuffer">
                                                        <strong><@orcid.msg
                                                            'manual_work_form_contents.labeltranslatedtitle'/></strong> <span><i>({{worksService.details[work.putCode.value].translatedTitle.languageName}})</i></span>
                                                        <div>{{worksService.details[work.putCode.value].translatedTitle.content}}</div>                
                                                    </div>
                                                </div>
                                                <div class="col-md-6" *ngIf="worksService.details[work.putCode.value].languageCode?.value" >
                                                    <div class="bottomBuffer">                  
                                                        <strong><@orcid.msg
                                                            'manual_work_form_contents.labellanguage'/></strong>
                                                        <div>{{worksService.details[work.putCode.value].languageName.value}}</div>                  
                                                    </div>
                                                </div>
                                                <div class="col-md-6" *ngIf="worksService.details[work.putCode.value].subtitle?.value" >
                                                    <div class="bottomBuffer">
                                                        <strong> <@orcid.msg 'manual_work_form_contents.labelsubtitle'/> </strong>
                                                        <div>{{worksService.details[work.putCode.value].subtitle.value}}</div>
                                                    </div>
                                                </div>
                                                
                                                <div class="col-md-6" *ngIf="worksService.details[work.putCode.value].url?.value" >

                                                    <div class="bottomBuffer">
                                                        <strong>
                                                            <@orcid.msg
                                                            'common.url'/>
                                                        </strong>
                                                        <div>
                                                            <a href="{{worksService.details[work.putCode.value].url.value | urlProtocol}}" target="url.value">{{worksService.details[work.putCode.value].url.value}}</a>
                                                        </div>              
                                                    </div>
                                                </div>          
                                            </div>                  
                                            <div class="row bottomBuffer" *ngIf="worksService.details[work.putCode.value].citation?.citation?.value" >
                                                <div class="col-md-12">             
                                                    <strong><@orcid.msg 'manual_work_form_contents.labelcitation'/></strong> <span> (<span *ngIf="worksService.details[work.putCode.value].citation?.citationType?.value" ><i>{{worksService.details[work.putCode.value].citation.citationType.value}}</i></span>) 
                                                    </span>
                                                    <span *ngIf="showBibtex[work.putCode.value] && worksService.details[work.putCode.value].citation?.citationType?.value == 'bibtex'">
                                                        <a class="toggle-tag-option" (click)="bibtexShowToggle(work.putCode.value)">
                                                            [<@orcid.msg 'work.switch_view'/>]
                                                        </a>
                                                    </span>
                                                    <span *ngIf="(showBibtex[work.putCode.value] == null || showBibtex[work.putCode.value] == false) && worksService.details[work.putCode.value].citation?.citationType?.value == 'bibtex'">
                                                        <a class="toggle-tag-option" (click)="bibtexShowToggle(work.putCode.value)">
                                                            [<@orcid.msg 'work.switch_view'/>]
                                                        </a>
                                                    </span>
                                                    
                                                </div>
                                                <div class="col-md-12">
                                                
                                                    <div *ngIf="worksService.details[work.putCode.value].citation?.citationType?.value != 'bibtex'">
                                                        <span>
                                                            {{worksService.details[work.putCode.value].citation.citation.value}}
                                                        </span>
                                                    </div>
                                                    
                                                    <div *ngIf="(showBibtex[work.putCode.value] == null || showBibtex[work.putCode.value] == false) && worksService.details[work.putCode.value].citation?.citationType?.value == 'bibtex'" 
                                                         class="col-md-offset-1 col-md-11 col-sm-offset-1 col-sm-11 col-xs-12 citation-raw">{{worksService.details[work.putCode.value].citation.citation.value}}
                                                    </div>
                                                                        
                                                    <div class="row" *ngIf="showBibtex[work.putCode.value] && (worksService.bibtexJson[work.putCode.value]==null || worksService.bibtexJson[work.putCode.value].length==0)">
                                                        <div class="col-md-offset-1 col-md-6"><@orcid.msg 'work.unavailable_in_html'/></div>
                                                    </div>
                                                    
                                                    
                                                    <div class="row" *ngFor='let bibJSON of worksService.bibtexJson[work.putCode.value]; let index = index; let first = first; let last = last;'>    
                                                        <div *ngIf="showBibtex[work.putCode.value]">
                                                            <div class="col-md-offset-1 col-md-2 col-sm-offset-1 col-sm-1 col-xs-offset-1 col-xs-4">{{bibJSON.entryType}}</div>
                                                            <div class="col-md-8 col-sm-9 col-xs-offset-1 col-xs-6">{{bibJSON.citationKey}}</div>                               
                                                            <div *ngFor="let bibEntry of bibJSON.entryTags; let index = index; let first = first; let last = last;">
                                                                {{bibEntry | json}}
                                                                <!--****(entKey,entVal)
                                                                <div class="col-md-offset-1 col-md-2 col-sm-offset-1 col-sm-1 col-xs-offset-1 col-xs-4">{{entKey}}</div>
                                                                <div class="col-md-8 col-sm-9 col-xs-offset-1 col-xs-6">{{entVal | latex}}</div>
                                                                -->
                                                            </div>
                                                            
                                                        </div>                 
                                                    </div>                      
                                                </div>
                                            </div>
                                            <div class="row bottomBuffer" *ngIf="worksService.details[work.putCode.value].shortDescription?.value"
                                                >
                                                <div class="col-md-12">
                                                    <strong> <@orcid.msg
                                                        'manual_work_form_contents.labeldescription'/> </strong>
                                                    <div style="white-space: pre-wrap;">{{worksService.details[work.putCode.value].shortDescription.value}}</div>
                                                </div>
                                            </div>
                                            
                                            <div class="row bottomBuffer">
                                                <div class="col-md-6" *ngIf="worksService.details[work.putCode.value].countryCode?.value" >
              
                                                    <div class="bottomBuffer">
                                                        <strong><@orcid.msg
                                                            'manual_work_form_contents.labelcountry'/></strong>
                                                        <div>{{worksService.details[work.putCode.value].countryName.value}}</div>
                                                    </div>
                                                </div>          
                                                <div class="col-md-6" *ngIf="worksService.details[work.putCode.value].contributors?.length > 0" >
                                                    <div class="bottomBuffer">          
                                                        <strong> Contributor </strong>
                                                        <div *ngFor="let contributor of worksService.details[work.putCode.value].contributors; let index = index; let first = first; let last = last;">
                                                            {{contributor.creditName?.value}} <span>{{contributor | contributorFilter}}</span>
                                                        </div>
                                                    </div>                                      
                                                </div>
                                                <div class="col-md-6">
                                                    <div class="bottomBuffer">
                                                        <strong><@orcid.msg 'groups.common.created'/></strong><br />
                                                        <div>{{worksService.details[work.putCode.value].createdDate | ajaxFormDateToISO8601}}</div>
                                                    </div>      
                                                </div>
                                                <div class="col-md-12">
                                                    <div class="bottomBuffer">
                                                        <div class="badge-container-{{work.putCode.value}}"></div>
                                                    </div>                         
                                                </div>      
                                            </div>
                                            
                                        </div>  
                                    </div>

                                    <div class="row source-line" *ngIf="group.activePutCode == work.putCode.value">
                                        <!--Edit sources-->
                                        <div class="col-md-7 col-sm-7 col-xs-12" *ngIf="editSources[group.groupId]">
                                            {{(work.sourceName == null || work.sourceName == '') ? work.source : work.sourceName }}
                                        </div>
                                        <div class="col-md-3 col-sm-3 col-xs-10" *ngIf="editSources[group.groupId]">
                                            <div *ngIf="editSources[group.groupId]">
                                                <span class="glyphicon glyphicon-check" *ngIf="work.putCode.value == group.defaultWork.putCode.value"></span><span *ngIf="work.putCode.value == group.defaultWork.putCode.value"> <@orcid.msg 'groups.common.preferred_source' /></span>
                                                
                                                <#if !(isPublicProfile??)>
                                                <a (click)="makeDefault(group, work.putCode.value)" *ngIf="work.putCode.value != group.defaultWork.putCode.value">
                                                    <span class="glyphicon glyphicon-unchecked"></span> <@orcid.msg 'groups.common.make_preferred' />
                                                </a>
                                                </#if>
                                            </div>
                                        </div>
                                        <div class="col-md-2 col-sm-2 trash-source" *ngIf="editSources[group.groupId]">
                                            <div *ngIf="editSources[group.groupId]">
                                                <#if !(isPublicProfile??)>
                                                <ul class="sources-actions">
                                                    <#if RequestParameters['combine']??>
                                                    <li *ngIf="canBeCombined(work)">
                                                        <a class="glyphicons git_pull_request" (click)="showCombineMatches(group.defaultWork)" (mouseenter)="showTooltip(work.putCode.value+'-combineActiveDuplicates')" (mouseleave)="hideTooltip(work.putCode.value+'-combineActiveDuplicates')"></a>
                                                        <div class="popover popover-tooltip top combine-activeDuplicates-popover" *ngIf="showElement[work.putCode.value+'-combineActiveDuplicates']">
                                                            <div class="arrow"></div>
                                                            <div class="popover-content">
                                                                <@orcid.msg 'groups.common.combine_duplicates' />
                                                            </div>
                                                        </div>
                                                    </li>
                                                    </#if>
                                                    <li> 
                                                        <@orcid.editWorkIconNg2
                                                            activity="work"
                                                            click="openEditWork(work.putCode.value)"
                                                            toolTipSuffix="editToolTipSource"
                                                            toolTipClass="popover popover-tooltip top edit-activeSource-popover"
                                                         />
                                                        
                                                    </li>
                                                    <li>
                                                        <a 
                                                            (click)="deleteWorkConfirm(work.putCode.value, false)"  
                                                            title="<@orcid.msg 'freemarker.btnDelete' /> {{work.title.value}}" 
                                                            (mouseenter)="showTooltip(work.putCode.value+'-deleteActiveSource')" 
                                                            (mouseleave)="hideTooltip(work.putCode.value+'-deleteActiveSource')">
                                                            <span class="glyphicon glyphicon-trash"></span>
                                                        </a>

                                                        <div class="popover popover-tooltip top delete-activeSource-popover" *ngIf="showElement[work.putCode.value+'-deleteActiveSource']">
                                                            <div class="arrow"></div>
                                                            <div class="popover-content">
                                                                <@orcid.msg 'groups.common.delete_this_source' />
                                                            </div>
                                                        </div>
                                                    </li>
                                                </ul>
                                                </#if>
                                            </div>
                                        </div>
                                        <!--Edit sources-->
                                    </div>
                                    <div *ngIf="group.activePutCode != work.putCode.value" class="row source-line">
                                        <div class="col-md-7 col-sm-7 col-xs-12">
                                            <a (click)="group.activePutCode = work.putCode.value;showMozillaBadges(group.activePutCode);">                                
                                                {{(work.sourceName == null || work.sourceName == '') ? work.source : work.sourceName }}
                                            </a>
                                        </div>                                        
                                        <div class="col-md-3 col-sm-3 col-xs-10">
                                            <#if !(isPublicProfile??)>
                                            <span class="glyphicon glyphicon-check" *ngIf="work.putCode.value == group.defaultWork.putCode.value"></span><span *ngIf="work.putCode.value == group.defaultWork.putCode.value"> <@orcid.msg 'groups.common.preferred_source' /></span>
                                            <a (click)="makeDefault(group, work.putCode.value); " *ngIf="work.putCode.value != group.defaultWork.putCode.value">
                                                <span class="glyphicon glyphicon-unchecked"></span> <@orcid.msg 'groups.common.make_preferred' />
                                            </a>
                                            </#if>
                                        </div>
                                        <div class="col-md-2 col-sm-2 col-xs-2 trash-source">
                                            <#if !(isPublicProfile??)>
                                            <ul class="sources-actions">
                                                <#if RequestParameters['combine']??>
                                                <li *ngIf="canBeCombined(work)">
                                                    <a class="glyphicons git_pull_request" (click)="showCombineMatches(group.defaultWork)" (mouseenter)="showTooltip(work.putCode.value+'-combineInactiveDuplicates')" (mouseleave)="hideTooltip(work.putCode.value+'-combineInactiveDuplicates')"></a>

                                                    <div class="popover popover-tooltip top combine-inactiveDuplicates-popover" *ngIf="showElement[work.putCode.value+'-combineInactiveDuplicates'] == true">
                                                        <div class="arrow"></div>
                                                        <div class="popover-content">
                                                            <@orcid.msg 'groups.common.combine_duplicates' />
                                                        </div>
                                                    </div>
                                                </li>
                                                </#if>
                                                <li> 
                                                    <@orcid.editWorkIconNg2
                                                        activity="work"
                                                        click="openEditWork(work.putCode.value)"
                                                        toolTipSuffix="editToolTipSourceActions"
                                                        toolTipClass="popover popover-tooltip top edit-inactiveSource-popover"
                                                     />
                                                </li>
                                                <li>
                                                    <a (click)="deleteWorkConfirm(work.putCode.value, false)" (mouseenter)="showTooltip(work.putCode.value+'-deleteInactiveSource')" (mouseleave)="hideTooltip(work.putCode.value+'-deleteInactiveSource')">
                                                        <span class="glyphicon glyphicon-trash" title="<@orcid.msg 'freemarker.btnDelete'/> {{work.title.value}}"></span>
                                                    </a>

                                                    <div class="popover popover-tooltip top delete-inactiveSource-popover" *ngIf="showElement[work.putCode.value+'-deleteInactiveSource'] == true">
                                                        <div class="arrow"></div>
                                                        <div class="popover-content">
                                                           <@orcid.msg 'groups.common.delete_this_source' />
                                                        </div>
                                                    </div>
                                                </li>
                                            </ul>
                                            </#if>
                                        </div>
                                    </div> 
                                    <div class="row source-line" *ngIf="!editSources[group.groupId]">                        
                                        <div class="col-md-7 col-sm-7 col-xs-12">
                                            <@orcid.msg 'groups.common.source'/>: {{(work.sourceName == null || work.sourceName == '') ? work.source : work.sourceName }}
                                        </div>
                                        
                                        <div class="col-md-3 col-sm-3 col-xs-9">
                                            <span class="glyphicon glyphicon-check"></span><span> <@orcid.msg 'groups.common.preferred_source' /></span> <span *ngIf="group?.works?.length != 1"> (</span><a (click)="showSources(group, $event)" *ngIf="group?.works?.length != 1" (mouseenter)="showTooltip(group.groupId+'-sources')" (mouseleave)="hideTooltip(group.groupId+'-sources')"><@orcid.msg 'groups.common.of'/> {{group.works.length}}</a><span *ngIf="group?.works?.length != 1">)</span>

                                            <div class="popover popover-tooltip top sources-popover" *ngIf="showElement[group.groupId+'-sources']">
                                                <div class="arrow"></div>
                                                <div class="popover-content">
                                                    <@orcid.msg 'groups.common.sources.show_other_sources' />
                                                </div>
                                            </div>
                                        </div>

                                        <div class="col-md-2 col-sm-2 col-xs-3" *ngIf="group.activePutCode == work.putCode.value">
                                            <ul class="sources-options" >
                                                <#if !(isPublicProfile??)>
                                                <#if RequestParameters['combine']??>
                                                <li *ngIf="canBeCombined(work)">
                                                    <a (click)="showCombineMatches(group.defaultWork)" title="<@orcid.msg 'groups.common.combine_duplicates' />" (mouseenter)="showTooltip(group.groupId+'-combineDuplicates')" (mouseleave)="hideTooltip(group.groupId+'-combineDuplicates')">
                                                        <span class="glyphicons git_pull_request"></span>
                                                    </a>

                                                    <div class="popover popover-tooltip top combine-duplicates-popover" *ngIf="showElement[group.groupId+'-combineDuplicates']">
                                                        <div class="arrow"></div>
                                                        <div class="popover-content">
                                                            <@orcid.msg 'groups.common.combine_duplicates' />
                                                        </div>
                                                    </div>
                                                </li>
                                                </#if>

                                                <li>
                                                    <@orcid.editWorkIconNg2
                                                        activity="work"
                                                        click="openEditWork(work.putCode.value)"
                                                        toolTipSuffix="editToolTip"
                                                        toolTipClass="popover popover-tooltip top edit-source-popover"
                                                    />
                                                </li>

                                                <li *ngIf="!(editSources[group.groupId] || group?.works?.length == 1)">
                                                    <a (click)="showSources(group)" (mouseenter)="showTooltip(group.groupId+'-deleteGroup')" (mouseleave)="hideTooltip(group.groupId+'-deleteGroup')">
                                                        <span class="glyphicon glyphicon-trash"></span>
                                                    </a>
                                                    <div class="popover popover-tooltip top delete-group-popover" *ngIf="showElement[group.groupId+'-deleteGroup']">
                                                        <div class="arrow"></div>
                                                        <div class="popover-content">
                                                           <@orcid.msg 'groups.common.delete_this_source' />
                                                        </div>
                                                    </div>
                                                </li>

                                                <li *ngIf="group?.works?.length == 1">
                                                    <a (click)="deleteWorkConfirm(group.activePutCode, false)" (mouseenter)="showTooltip(group.groupId+'-deleteSource')" (mouseleave)="hideTooltip(group.groupId+'-deleteSource')">
                                                        <span class="glyphicon glyphicon-trash"></span>
                                                    </a>
                                                    <div class="popover popover-tooltip top delete-source-popover" *ngIf="showElement[group.groupId+'-deleteSource']">
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
                            </li>
                            <!--End work info-->
                        </ul>
                    </div>
                </li>
            </ul>
            <button *ngIf="worksService.showLoadMore" (click)="loadMore()" class="btn btn-primary">${springMacroRequestContext.getMessage("workspace.works.load_more")}</button>
            <div *ngIf="worksService?.loading" class="text-center" id="workSpinner">
                <i class="glyphicon glyphicon-refresh spin x4 green" id="spinner"></i>
            </div>
            <div *ngIf="worksService?.loading == false && worksService?.groups?.length == 0">
                <strong>
                    <#if (publicProfile)?? && publicProfile == true>${springMacroRequestContext.getMessage("workspace_works_body_list.Nopublicationsaddedyet")}<#else>${springMacroRequestContext.getMessage("workspace_works_body_list.havenotaddedanyworks")} 
                    <a *ngIf="noLinkFlag" (click)="showWorkImportWizard()">${springMacroRequestContext.getMessage("workspace_works_body_list.addsomenow")}</a>
                    <span *ngIf="noLinkFlag">${springMacroRequestContext.getMessage("workspace_works_body_list.addsomenow")}</span>
                    </#if>
                </strong>
            </div>          
        </div>
    </div>
</script>