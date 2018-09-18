<script type="text/ng-template" id="works-ng2-template">
    <!--WORKS-->
    <div id="workspace-publications" class="workspace-accordion-item workspace-accordion-active">
        <!--Works section header--> 
        <div class="workspace-accordion-header clearfix">
            <div class="row">
                <div class="col-md-3 col-sm-3 col-xs-12 no-padding-right">
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
                                    <p><@orcid.msg 'manage_works_settings.helpPopoverWorks'/> <a href="<@orcid.msg 'common.kb_uri_default'/>360006973133" target="manage_works_settings.helpPopoverWorks"><@orcid.msg 'common.learn_more'/></a></p>
                                </div>
                            </div>
                        </div> 
                        </#if>
                    </div>
                </div>
                <div class="col-md-9 col-sm-9 col-xs-12 action-button-bar" *ngIf="workspaceSrvc.displayWorks">
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
                        <@orcid.checkFeatureStatus featureName='MANUAL_WORK_GROUPING' enabled=false>
                            <li *ngIf="worksService?.groups?.length > 1" >
                                <a class="action-option works manage-button" [ngClass]="{'green-bg' : bulkEditShow == true}" (click)="toggleBulkEdit()">
                                    <span class="glyphicon glyphicon-pencil"></span><@orcid.msg 'groups.common.bulk_edit'/>
                                </a>
                            </li>
                        </@orcid.checkFeatureStatus>
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
                                            <!--Add from Bibtex-->
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
                        <!--Import Search & link-->
                        <li class="hidden-md hidden-sm visible-xs-inline">
                            <a *ngIf="noLinkFlag" class="action-option manage-button" (click)="showWorkImportWizard()">
                                <span class="glyphicon glyphicon-cloud-upload"></span>
                                <@orcid.msg 'manual_orcid_record_contents.search_link'/>
                            </a>
                        </li>
                        <!--Import Bibtex-->
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
                                    <select id="work-type" name="work-type" [(ngModel)]="selectedWorkType">
                                        <option *ngFor="let wt of workType">{{wt}}</option>
                                    </select> 
                                </div> 
                            </div>
                            <div class="col-md-7 col-sm-7 col-xs-12">
                                <div class="form-group geo-area-group">
                                    <label for="geo-area"><@orcid.msg 'workspace.link_works.filter.geographicalarea'/></label>
                                    <select id="geo-area" name="geo-area" [(ngModel)]="selectedGeoArea">
                                        <option *ngFor="let ga of geoArea">{{ga}}</option>
                                    </select>   
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
                            <@orcid.msg 'workspace.bibtexExporter.intro_1'/><a href="<@orcid.msg 'common.kb_uri_default'/>360006971453" target="exporting_bibtex" style="word-break\: normal;"><@orcid.msg 'workspace.bibtexExporter.intro_2'/></a><@orcid.msg 'workspace.bibtexExporter.intro_3'/>
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
            <div class="bottomBuffer" *ngIf="bibtexExportLoading && !bibtexExportError" >
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
                            <@orcid.msg 'workspace.bibtexImporter.instructions'/>  <a href="<@orcid.msg 'common.kb_uri_default'/>360006973353" target="workspace.bibtexImporter.learnMore"><@orcid.msg 'workspace.bibtexImporter.learnMore'/></a>.
                        </p> 
                    </div>
                    <div class="col-md-3 col-sm-3 col-xs-4">
                        <span class="bibtext-options">                                        
                            <a class="bibtex-cancel" (click)="openBibTextWizard()"><@orcid.msg 'workspace.bibtexImporter.cancel'/></a>            
                            <label for="inputBibtex" *ngIf="!(worksFromBibtex?.length > 0)" class="import-label" ><@orcid.msg 'workspace.bibtexImporter.fileUpload'/></label>
                            <span *ngIf="worksFromBibtex?.length > 0" class="import-label" (click)="saveAllFromBibtex()"><@orcid.msg 'workspace.bibtexImporter.save_all'/></span>                                     
                            <input id="inputBibtex" name="textFiles" type="file" class="upload-button" accept="*" (change)="loadBibtexJs($event)" app-file-text-reader multiple/>

                        </span>                   
                    </div>
                </div>
            </div> 
            <div class="bottomBuffer text-center" *ngIf="bibtexImportLoading && !bibtexParsingError" >
                <i class="glyphicon glyphicon-refresh spin x4 green" id="spinner"></i>
            </div>          
            <div class="alert alert-block" *ngIf="bibtexParsingError">
                <strong><@orcid.msg 'workspace.bibtexImporter.parsingError'/></strong>
            </div>
            <span class="dotted-bar" *ngIf="worksFromBibtex?.length > 0"></span>
            <!-- Bibtex Import Results List -->
            <ng-container *ngIf="!bibtexImportLoading">
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
                            <div class="row" *ngIf="work?.workExternalIdentifiers[0]?.externalIdentifierId?.value">
                                <div class="col-md-12 col-sm-12 bottomBuffer">
                                    <ul class="id-details">
                                        <li class="url-work">
                                            <ul class="id-details">
                                                <li *ngFor='let extID of work.workExternalIdentifiers | orderBy:["relationship.value", "externalIdentifierType.value"]; let index = index; let first = first; let last = last;' class="url-popover">
                                                    <span *ngIf="work?.workExternalIdentifiers[0]?.externalIdentifierId?.value?.length > 0">
                                                        <ext-id-popover-ng2 [extID]="extID" [putCode]="'bibtexWork'+i" [activityType]="'work'"></ext-id-popover-ng2>
                                                    </span>
                                                </li>
                                            </ul>                                   
                                        </li>

                                        <li *ngIf="work.url?.value" class="url-popover url-work">
                                            <@orcid.msg 'common.url' />: <a href="{{work.url.value | urlProtocol}}" (mouseenter)="showURLPopOver('bibtexWork'+index)" (mouseleave)="hideURLPopOver('bibtexWork'+index)" [ngClass]="{'truncate-anchor' : moreInfo[group?.groupId] == false || moreInfo[group?.groupId] == undefined}" target="work.url.value">{{work.url.value}}</a>
                                            <div class="popover-pos">                                   
                                                <div class="popover-help-container">
                                                    <div class="popover bottom" [ngClass]="{'block' : displayURLPopOver['bibtexWork'+index] == true}">
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
                                <li><a (click)="rmWorkFromBibtex(work)" class="ignore glyphicon glyphicon-trash bibtex-button" title="<@orcid.msg 'common.ignore' />"></a></li>
                                <li><a *ngIf="work?.errors?.length == 0" (click)="addWorkFromBibtex(work)" class="save glyphicon glyphicon-floppy-disk bibtex-button" title="<@orcid.msg 'common.save' />"></a></li>
                                <li><a *ngIf="work?.errors?.length > 0" (click)="editWorkFromBibtex(work)" class="save glyphicon glyphicon-pencil bibtex-button" title="<@orcid.msg 'common.edit' />"></a></li>
                                <li><span *ngIf="work?.errors?.length > 0"><a (click)="editWorkFromBibtex(work)"><i class="glyphicon glyphicon-exclamation-sign"></i><@orcid.msg 'workspace.bibtexImporter.work.warning' /></a></span></li>
                            </ul>
                        </div>
                    </div>
                </div>
            </ng-container>
        </div>
        <!--End bibtex import wizard-->
        <!--Works list-->
        <div *ngIf="workspaceSrvc.displayWorks" class="workspace-accordion-content">
            <@orcid.checkFeatureStatus featureName='MANUAL_WORK_GROUPING'>
                <#if !(isPublicProfile??)>
                    <div class="work-bulk-actions row" *ngIf="worksService?.groups?.length">
                        <div class="col-md-12 col-sm-12 col-xs-12">
                            <ul class="sources-actions">
                                <li>
                                    <div class="left">
                                        <input type="checkbox" [value]="allSelected" (click)="toggleSelectAll()" />
                                    </div>
                                </li>
                                <li>
                                    <div class="left leftBuffer">
                                        <a (click)="mergeConfirm()">
                                            <span class="edit-option-toolbar glyphicon glyphicon-resize-small"></span>
                                            <span><@orcid.msg 'workspace.bulkedit.merge'/></span>
                                        </a>
                                    </div>
                                </li>
                                <li>
                                    <div class="left leftBuffer">
                                        <a (click)="deleteBulkConfirm()">
                                            <span class="edit-option-toolbar glyphicon glyphicon-trash"></span>
                                            <span><@orcid.msg 'workspace.bulkedit.delete'/></span>
                                        </a>
                                    </div>
                                </li>
                                <li>
                                    <div class="bulk-edit-privacy-control left leftBuffer">
                                        <@orcid.privacyToggleBulkWorksNg2 angularModel="none" elementId="none" 
                                        questionClick=""
                                        clickedClassCheck=""
                                        publicClick="setBulkGroupPrivacy('PUBLIC', $event)" 
                                        limitedClick="setBulkGroupPrivacy('LIMITED', $event)" 
                                        privateClick="setBulkGroupPrivacy('PRIVATE', $event)"/>
                                    </div>
                                </li>
                                <li>
                                    <div class="popover-help-container">
                                        <i class="glyphicon glyphicon-question-sign"></i>
                                        <div class="bulk-actions-popover popover bottom">
                                            <div class="arrow"></div>
                                            <div class="popover-content">
                                                <p>
                                                    <span class="helpPopoverMergeHeading"><@orcid.msg 'groups.merge.helpPopoverMergeHeading'/></span><@orcid.msg 'groups.merge.helpPopoverMerge'/><br />
                                                    <span class="helpPopoverDeleteBulkEditHeading"><@orcid.msg 'groups.merge.helpPopoverDeleteBulkEditHeading'/></span><@orcid.msg 'groups.merge.helpPopoverDeleteBulkEdit'/>
                                                </p>
                                            </div>
                                        </div>
                                    </div> 
                                </li>
                            </ul>
                        </div>
                    </div>  
                </#if>            
            </@orcid.checkFeatureStatus>
            <ul *ngIf="worksService?.groups?.length" class="workspace-publications bottom-margin-medium" id="body-work-list">
                <li class="bottom-margin-small workspace-border-box card" *ngFor="let group of worksService.groups">
                    <#include "work-details-ng2.ftl"/>  
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
                    <span *ngIf="!noLinkFlag">${springMacroRequestContext.getMessage("workspace_works_body_list.addsomenow")}</span>
                    </#if>
                </strong>
            </div>          
        </div>
    </div>
</script>