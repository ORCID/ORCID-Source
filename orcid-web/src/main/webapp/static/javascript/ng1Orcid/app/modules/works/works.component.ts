declare var $: any;
declare var ActSortState: any;
declare var bibtexParse: any;
declare var blobObject: any;
declare var GroupedActivities: any;
declare var om: any;
declare var openImportWizardUrl: any;
declare var populateWorkAjaxForm: any;
declare var workIdLinkJs: any;

import { NgForOf, NgIf } 
    from '@angular/common';

import { AfterViewInit, ChangeDetectorRef, Component, ElementRef, Input, OnDestroy, OnInit } 
    from '@angular/core';

import { Observable, Subject, Subscription } 
    from 'rxjs';

import { takeUntil } 
    from 'rxjs/operators';

import { CommonService } 
    from '../../shared/common.service.ts';

import { EmailService } 
    from '../../shared/email.service.ts';

import { FeaturesService }
    from '../../shared/features.service.ts';

import { ModalService } 
    from '../../shared/modal.service.ts';

import { WorksService } 
    from '../../shared/works.service.ts';

import { WorkspaceService } 
    from '../../shared/workspace.service.ts'; 

@Component({
    selector: 'works-ng2',
    template:  scriptTmpl("works-ng2-template")
})
export class WorksComponent implements AfterViewInit, OnDestroy, OnInit {
    @Input() publicView: any;
    @Input() printView: any;
    private ngUnsubscribe: Subject<void> = new Subject<void>();
    private subscription: Subscription;

    addingWork: boolean;
    bibtexExportError: boolean;
    bibtexImportLoading: boolean;
    bibtexParsingError: boolean;
    bibtexExportLoading: boolean;
    bibtexWork: boolean;
    bibtexWorkIndex: any;
    bulkChecked: any;
    bulkDeleteCount: number;
    bulkDisplayToggle: boolean;
    bulkEditMap: any;
    bulkEditShow: boolean;
    canReadFiles: boolean;
    deleteGroup: any;
    deletePutCode: any;
    displayURLPopOver: any;
    editSources: any;
    editWork: any;
    emails: any;
    exIdResolverFeatureEnabled = this.featuresService.isFeatureEnabled('EX_ID_RESOLVER');
    fixedTitle: any;
    formData: any;
    geoArea: any;
    loadingScripts: any;
    moreInfo: any;
    moreInfoOpen: boolean;
    noLinkFlag: boolean;
    savingBibtex: boolean;
    scriptsLoaded: boolean;
    selectedGeoArea: any;
    selectedWorkType: any;
    showBibtex: any;
    showBibtexExport: boolean;
    showBibtexImportWizard: boolean;
    showElement: any;
    sortState: any;
    textFiles: any;
    wizardDescExpanded: any;
    workImportWizard: boolean;
    workImportWizardsOriginal: any;
    workType: any;
    worksFromBibtex: any;
    allSelected: boolean;

    constructor( 
        private commonSrvc: CommonService,
        private cdr: ChangeDetectorRef,
        private elementRef: ElementRef,
        private emailService: EmailService,
        private featuresService: FeaturesService,
        private modalService: ModalService,
        private workspaceSrvc: WorkspaceService,
        private worksService: WorksService
    ) {

        this.addingWork = false;
        this.bibtexExportError = false;
        this.bibtexExportLoading = false;
        this.bibtexImportLoading = false;
        this.bibtexParsingError = false;
        this.bibtexWork = false;
        this.bibtexWorkIndex = null;
        this.bulkChecked = false;
        this.bulkDeleteCount = 0;
        this.bulkDisplayToggle = false;
        this.bulkEditMap = {};
        this.bulkEditShow = false;
        this.canReadFiles = false;
        this.displayURLPopOver = {};
        this.editSources = {};
        this.editWork = null;
        this.emails = {};
        this.formData = {
            works: null
        };
        this.geoArea = ['All'];
        this.loadingScripts = false;
        this.moreInfo = {};
        this.moreInfoOpen = false;
        this.noLinkFlag = true;
        this.publicView = elementRef.nativeElement.getAttribute('publicView');
        this.printView = elementRef.nativeElement.getAttribute('printView');
        this.savingBibtex = false;
        this.scriptsLoaded = false;
        this.selectedGeoArea = null;
        this.selectedWorkType = null;
        this.showBibtex = {};
        this.showBibtexExport = false;
        this.showBibtexImportWizard = false;
        this.showElement = {};
        this.sortState = new ActSortState(GroupedActivities.ABBR_WORK);
        this.textFiles = [];
        this.wizardDescExpanded = {};
        this.workImportWizard = false;
        this.workImportWizardsOriginal = null;
        this.workType = ['All'];
        this.worksFromBibtex = null;
    }

    addExternalIdentifier(): void {
        this.editWork.workExternalIdentifiers.push(
            {
                relationship: {
                    value: "self"
                }, 
                url: {
                    value: ""
                },
                externalIdentifierId: {
                    value: ""
                }, 
                externalIdentifierType: {
                    value: ""
                } 
            }
        );
    };

    addWorkFromBibtex(work): void {
        this.bibtexWork = true;              
        this.bibtexWorkIndex = this.worksFromBibtex.indexOf(work);     
        this.editWork = this.worksFromBibtex[this.bibtexWorkIndex];
        
        if ( this.addingWork ) {
            return; // don't process if adding work
        }
        this.addingWork = true;
        this.editWork.errors.length = 0;
        this.worksService.postWork( this.editWork)
        .pipe(    
            takeUntil(this.ngUnsubscribe)
        )
        .subscribe(
            data => {
                this.addingWork=false;
                if (data.errors.length > 0) {
                    this.editWork = data;                    
                    this.commonSrvc.copyErrorsLeft(this.editWork, data);
                    //TODO: resolve ext ids added via bibtex
                } else {
                    if (this.bibtexWork != false){
                        this.worksFromBibtex.splice(this.bibtexWorkIndex, 1);
                        this.bibtexWork = false;
                    }
                    this.refreshWorkGroups();
                }

            },
            error => {
                console.log('worksForm.component.ts addWorkError', error);
            } 
        );
    };      

    addWorkModal(work): void {
        this.emailService.getEmails()
        .pipe(    
            takeUntil(this.ngUnsubscribe)
        )
        .subscribe(
            data => {
                this.emails = data;
                if( this.emailService.getEmailPrimary().verified ){
                    this.worksService.notifyOther({ work:work });
                    if(work == undefined) {
                        this.modalService.notifyOther({action:'open', moduleId: 'modalWorksForm', edit: false, bibtexWork: this.bibtexWork});
                    } else {
                        this.modalService.notifyOther({action:'open', moduleId: 'modalWorksForm', edit: true, bibtexWork: this.bibtexWork});
                    }                    
                }else{
                    this.modalService.notifyOther({action:'open', moduleId: 'modalemailunverified'});
                }
            },
            error => {
                console.log('getEmails', error);
            } 
        );
    };

    bibtexShowToggle(putCode): void {
        this.showBibtex[putCode] = !(this.showBibtex[putCode]);
    };

    bulkChangeAll(bool): void {
        this.bulkChecked = bool;
        this.bulkDisplayToggle = false;
        for (var idx in this.worksService.groups){
            this.bulkEditMap[this.worksService.groups[idx].activePutCode] = bool;
        }
    };

    canBeCombined(work): any {
        if (this.userIsSource(work)){
            return true;
        }
        return this.hasCombineableEIs(work);
    };

    closeAllMoreInfo(): void {
        for (var idx in this.moreInfo){
            this.moreInfo[idx]=false;
        }
    };

    closePopover(event): void {
        this.moreInfoOpen = false;
        $('.work-more-info-container').css('display', 'none');
    };

    deleteBulkConfirm(idx): void {
        var idx: any;
        this.bulkDeleteCount = 0;       
        for (idx in this.worksService.groups){
            if (this.bulkEditMap[this.worksService.groups[idx].activePutCode]){
                this.bulkDeleteCount++;
            }
        }
        
        if (this.bulkDeleteCount > 0) {
            this.worksService.notifyOther({bulkDeleteCount:this.bulkDeleteCount, bulkEditMap:this.bulkEditMap});
            this.modalService.notifyOther({action:'open', moduleId: 'modalWorksBulkDelete'});
        }
    };
    
    mergeConfirm(): void {
        var idx: any;
        var mergeCount = 0;       
        for (idx in this.worksService.groups){
            if (this.bulkEditMap[this.worksService.groups[idx].activePutCode]){
                mergeCount++;
            }
        }
        
        if (mergeCount > 1) {
            var worksToMerge = new Array();
            var externalIdsPresent = false;
            for (var putCode in this.bulkEditMap) {
                if (this.bulkEditMap[putCode]) {
                    var work = this.worksService.getWork(putCode);
                    worksToMerge.push({ work: work, preferred: false});
                    if (work.workExternalIdentifiers.length > 0) {
                        externalIdsPresent = true;
                    }
                }
            }
            this.worksService.notifyOther({worksToMerge:worksToMerge});      
            this.worksService.notifyOther({externalIdsPresent:externalIdsPresent});     
            this.worksService.notifyOther({mergeCount:mergeCount});
            this.modalService.notifyOther({action:'open', moduleId: 'modalWorksMergeChoosePreferredVersion'});
        }
    };
    
    deleteWorkConfirm(putCode, deleteGroup): void {
        this.emailService.getEmails()
        .pipe(    
            takeUntil(this.ngUnsubscribe)
        )
        .subscribe(
            data => {
                this.emails = data;
                if( this.emailService.getEmailPrimary().verified ){
                    let maxSize = 100;
                    let work = this.worksService.getWork(putCode);
                    this.deletePutCode = putCode;
                    this.deleteGroup = deleteGroup;
                    if (work.title){
                        this.fixedTitle = work.title.value;
                    }
                    else {  
                        this.fixedTitle = '';
                    } 
                    if(this.fixedTitle.length > maxSize){
                        this.fixedTitle = this.fixedTitle.substring(0, maxSize) + '...';
                    }
                    this.worksService.notifyOther({fixedTitle:this.fixedTitle, putCode:putCode, deleteGroup:deleteGroup, sortState:this.sortState});
                    this.modalService.notifyOther({action:'open', moduleId: 'modalWorksDelete'});
                }else{
                    this.modalService.notifyOther({action:'open', moduleId: 'modalemailunverified'});
                }
            },
            error => {
                console.log('getEmails', error);
            } 
        );
    };

    editWorkFromBibtex(work): void {
        this.bibtexWork = true;
        this.bibtexWorkIndex = this.worksFromBibtex.indexOf(work);
        this.addWorkModal(this.worksFromBibtex[this.bibtexWorkIndex]);        
    };

    fetchBibtexExport(){
        this.bibtexExportLoading = true;
        this.bibtexExportError = false; 
        this.worksService.getBibtexExport()
        .pipe(    
            takeUntil(this.ngUnsubscribe)
        )
        .subscribe(
            data => {
                this.bibtexExportLoading = false;
                if(window.navigator.msSaveOrOpenBlob) {
                    var fileData = [data];
                    blobObject = new Blob(fileData, {type: 'text/plain'});
                    window.navigator.msSaveOrOpenBlob(blobObject, "works.bib");                              
                } else {
                    var anchor = document.createElement('a');
                    anchor.setAttribute('css', "{display: 'none'}");  
                    this.elementRef.nativeElement.append(anchor);
                    anchor.setAttribute('href', 'data:text/x-bibtex;charset=utf-8,' + encodeURIComponent(data));
                    anchor.setAttribute('target', '_self');
                    anchor.setAttribute('download', 'works.bib');
                    anchor.click();
                    anchor.remove();
                }
            },
            error => {
                this.bibtexExportError = true;
                console.log("bibtex export error");
            } 
        );   
    };

    getDetails(putCode, type, callback): void {
        if(this.worksService.details[putCode] == undefined) {
            this.worksService.getDetails(putCode, type)
            .pipe(    
                takeUntil(this.ngUnsubscribe)
            )
            .subscribe(
                data => {
                    this.worksService.removeBadContributors(data);
                    this.worksService.removeBadExternalIdentifiers(data);
                    this.worksService.addBibtexJson(data);
                    this.worksService.details[putCode] = data;
                    if (callback != undefined) {
                        callback(this.worksService.details[putCode]);
                    } 
                },
                error => {
                    console.log('getDetailsError', error);
                } 
            );
        } else {
            if (callback != undefined){
                callback(this.worksService.details[putCode]);
            }
        };
    }

    getEditable(putCode, callback): void {
        // first check if they are the current source
        var work = this.getDetails(
            putCode, this.worksService.constants.access_type.USER, 
            function(data) {
                if (data.source == orcidVar.orcidId){
                    callback(data);
                }
                else{
                    this.getGroupDetails(
                        putCode, 
                        this.worksService.constants.access_type.USER, 
                        function () {
                            // in this case we want to open their version
                            // if they don't have a version yet then copy
                            // the current one
                            var bestMatch = null;
                            for (var idx in this.worksService.details) {    
                                if (this.worksService.details[idx].source == orcidVar.orcidId) {
                                    bestMatch = this.worksService.details[idx];
                                    break;
                                }
                            }
                            if (bestMatch == null) {
                                bestMatch = this.worksService.createNew(this.worksService.details[putCode]);
                            }
                            
                            callback(bestMatch);
                        }.bind(this)
                    )
                }
            }.bind(this)
        );
    }

    getGroupDetails(putCode, type, callback?): void {
        let group = this.worksService.getGroup(putCode);
        let needsLoading =  new Array();
        let popFunct = function () {
            if (needsLoading.length > 0) {
                this.getDetails(needsLoading.pop(), type, popFunct); 
            }
            else if (callback != undefined) {
                callback();
            }
        }.bind(this);

        for (var idx in group.works) {
            needsLoading.push(group.works[idx].putCode.value)
        }

        popFunct();
    };

    hasCombineableEIs(work): boolean {
        if (work.workExternalIdentifiers != null){
            for (var idx in work.workExternalIdentifiers){
                if (work.workExternalIdentifiers[idx].externalIdentifierType.value != 'issn'){
                    return true;
                }
            }
        }
        return false;
    };

    hideAllTooltip(): void {
        for (var idx in this.showElement){
            this.showElement[idx]=false;
        }
    };

    hideSources(group): void {
        this.editSources[group.groupId] = false;
        group.activePutCode = group.defaultPutCode;
    };

    hideTooltip(key): void {        
        this.showElement[key] = false;
    };

    hideURLPopOver(id): void {       
        this.displayURLPopOver[id] = false;
    };

    loadBibtexJs($event): void {
        this.bibtexImportLoading = true;
        this.textFiles = $event.target.files; 
        try {
            this.worksFromBibtex = new Array();
            for (let bibtex of this.textFiles) {
                    let reader = new FileReader();
                    reader.readAsText(bibtex);
                    reader.onloadend = function(e){
                        try {
                            var parsed = bibtexParse.toJSON(reader.result);
                            if (parsed.length == 0) {
                                throw "bibtex parse return nothing";
                            }
                        } catch(err){
                            console.log("bibtexParse error");
                            this.bibtexParsingError = true;
                        }
                        this.worksService.getBlankWork()
                        .pipe(    
                            takeUntil(this.ngUnsubscribe)
                        )
                        .subscribe(
                            data => {
                                var blankWork = data;
                                var newWorks = new Array();
                                while (parsed.length > 0) {
                                    var cur = parsed.shift();
                                    var bibtexEntry = cur.entryType.toLowerCase();
                                    if (bibtexEntry != 'preamble' && bibtexEntry != 'comment') {    
                                        //Filtering @PREAMBLE and @COMMENT
                                        newWorks.push( populateWorkAjaxForm( cur,JSON.parse( JSON.stringify(blankWork) ) ) );
                                    }
                                };
                                this.worksService.worksValidate(newWorks)
                                .pipe(    
                                    takeUntil(this.ngUnsubscribe)
                                )
                                .subscribe(
                                    data => {
                                        for (var i in data) {                          
                                            this.worksFromBibtex.push(data[i]);
                                        }
                                        this.bibtexImportLoading = false; 
                                    },
                                    error => {
                                        console.log('worksValidateError', error);
                                    } 
                                );
                            },
                            error => {
                                console.log('parseBibtexError', error);
                            } 
                        );
                    }.bind(this);
                    
                    this.bibtexParsingError = false;  
            }  
        } catch (err) {
            this.bibtexImportLoading = false;
            this.bibtexParsingError = true;
            console.log('parseBibtexError', err);
        }
    };

    loadDetails(putCode, event): void {
        this.closePopover(event);
        this.moreInfoOpen = true;
        $(event.target).next().css('display','inline');
        if(this.publicView === "true"){
            this.getGroupDetails(
                putCode, 
                this.worksService.constants.access_type.ANONYMOUS
            );
        } else {
            this.getGroupDetails(
                putCode, 
                this.worksService.constants.access_type.USER
            );
        }
    };

    loadMore(): void {
        if(this.publicView === "true") {
            if(this.printView === "true") {
                this.worksService.loadAllPublicWorkGroups(this.sortState.predicateKey, 
                    !this.sortState.reverseKey[this.sortState.predicateKey]
                )
                .pipe(    
                    takeUntil(this.ngUnsubscribe)
                )
                .subscribe(
                    data => {
                        this.formData = data;
                        this.worksService.handleWorkGroupData( this.formData );
                        this.worksService.loading = false;
                    },
                    error => {
                        this.worksService.loading = false;
                        console.log('worksLoadMore', error);
                    } 
                );
            } else {
                this.worksService.getWorksPage(this.worksService.constants.access_type.ANONYMOUS, this.sortState.predicateKey, 
                    !this.sortState.reverseKey[this.sortState.predicateKey]
                )
                .pipe(    
                    takeUntil(this.ngUnsubscribe)
                )
                .subscribe(
                    data => {
                        this.formData = data;
                        this.worksService.handleWorkGroupData( this.formData );
                        this.worksService.loading = false;
                    },
                    error => {
                        this.worksService.loading = false;
                        console.log('worksLoadMore', error);
                    } 
                );
            }
        } else {
            this.worksService.getWorksPage(this.worksService.constants.access_type.USER, this.sortState.predicateKey, 
                !this.sortState.reverseKey[this.sortState.predicateKey]
            )
            .pipe(    
                takeUntil(this.ngUnsubscribe)
            )
            .subscribe(
                data => {
                    this.formData = data;
                    this.worksService.handleWorkGroupData( this.formData );
                    this.worksService.loading = false;
                    this.loadGroupingSuggestions();
                },
                error => {
                    this.worksService.loading = false;
                    console.log('worksLoadMore', error);
                } 
            );
        }
    };
    
    loadGroupingSuggestions(): void {
        if(this.publicView != "true") {
            this.worksService.getWorksGroupingSuggestions(
            )
            .pipe(    
                takeUntil(this.ngUnsubscribe)
            )
            .subscribe(
                data => {
                    if (data) {
                        var worksToMerge = new Array();
                        var externalIdsPresent = false;
                        for (var i in data.putCodes.workPutCodes) {
                            var workPutCode = data.putCodes.workPutCodes[i];
                            var work = this.worksService.getWork(workPutCode);
                            worksToMerge.push({ work: work, preferred: false});
                            if (work.workExternalIdentifiers.length > 0) {
                                externalIdsPresent = true;
                            }
                        }
                        this.worksService.notifyOther({suggestionId:data.id});
                        this.worksService.notifyOther({worksToMerge:worksToMerge});
                        this.worksService.notifyOther({externalIdsPresent:externalIdsPresent});     
                        this.worksService.notifyOther({mergeCount:worksToMerge.length});
                        this.modalService.notifyOther({action:'open', moduleId: 'modalWorksMergeSuggestions'});
                    }
                },
                error => {
                    this.worksService.loading = false;
                    console.log('worksLoadMore', error);
                } 
            );
        }
    };

    loadWorkImportWizardList(): void {        
        if(this.publicView != "true") {
            this.worksService.loadWorkImportWizardList()
            .pipe(    
                takeUntil(this.ngUnsubscribe)
            )
            .subscribe(
                data => {
                    if(data == null || data.length == 0) {
                        this.noLinkFlag = false;
                    }
                    this.selectedWorkType = om.get('workspace.works.import_wizzard.all');
                    this.selectedGeoArea = om.get('workspace.works.import_wizzard.all');
                    this.workImportWizardsOriginal = data;
                    this.bulkEditShow = false;
                    this.showBibtexImportWizard = false;
                    for(var idx in data) {                            
                        for(var i in data[idx].actTypes) {
                            if(!this.commonSrvc.contains(this.workType, data[idx].actTypes[i])) {
                                this.workType.push(data[idx].actTypes[i]);
                            }                                
                        }
                        for(var j in data[idx].geoAreas) {
                            if(!this.commonSrvc.contains(this.geoArea, data[idx].geoAreas[j])) {
                                this.geoArea.push(data[idx].geoAreas[j]);
                            }                                
                        }                            
                    }
                },
                error => {
                    console.log('WorkImportWizardError', error);
                } 
            );
        }                
    };

    makeDefault(group, putCode): any {
        this.worksService.updateToMaxDisplay(putCode)
        .pipe(    
            takeUntil(this.ngUnsubscribe)
        )
        .subscribe(
            data => {
                group.defaultPutCode = putCode;
                group.activePutCode = putCode;  
            },
            error => {
                console.log('makeDefault', error);
            } 
        );
    }

    openBibTextWizard(): void {
        this.emailService.getEmails()
        .pipe(    
            takeUntil(this.ngUnsubscribe)
        )
        .subscribe(
            data => {
                this.emails = data;
                if( this.emailService.getEmailPrimary().verified ){
                    this.bibtexParsingError = false;
                    this.bulkEditShow = false;
                    this.showBibtexExport = false;
                    this.showBibtexImportWizard = !(this.showBibtexImportWizard);
                    this.workImportWizard = false;
                    this.worksFromBibtex = null;
                }else{
                    this.modalService.notifyOther({action:'open', moduleId: 'modalemailunverified'});
                }
            },
            error => {
                console.log('getEmails', error);
            } 
        );
    };

    openEditWork(putCode): void{
        this.getEditable(putCode, function(data) {
            this.addWorkModal(data);
        }.bind(this));
    };

    openFileDialog(): void{
        this.textFiles = [];
        this.bibtexParsingError = false;
    };

    openImportWizardUrl(url): void {
        openImportWizardUrl(url);
    };

    openImportWizardUrlFilter(url, client): void {
        url = url + '?client_id=' + client.id + '&response_type=code&scope=' + client.scopes + '&redirect_uri=' + client.redirectUri;
        openImportWizardUrl(url);
    };

    refreshWorkGroups(): void {
        this.worksService.refreshWorkGroups(this.sortState.predicateKey, 
            !this.sortState.reverseKey[this.sortState.predicateKey]
        )
        .pipe(    
            takeUntil(this.ngUnsubscribe)
        )
        .subscribe(
            data => {
                this.formData = data;
                this.formData = data;
                this.worksService.handleWorkGroupData( this.formData );
                this.worksService.loading = false;
            },
            error => {
                this.worksService.loading = false;
                console.log('Error refreshing work groups', error);
            } 
        );
    };

    rmWorkFromBibtex(work): void {
        let index = this.worksFromBibtex.indexOf(work);
        
        this.worksFromBibtex.splice(index, 1);
    };

    saveAllFromBibtex(): any{
        var worksToSave = null;
        var numToSave = null;
        if( this.savingBibtex == false ){
            this.savingBibtex = true;
            worksToSave =  new Array();
            for(let work of this.worksFromBibtex){
                if (work.errors.length == 0){
                    worksToSave.push(work);
                } 
            }
            numToSave = worksToSave.length;
            for (let work of worksToSave) {
                this.worksService.postWork(work)
                .pipe(    
                    takeUntil(this.ngUnsubscribe)
                )
                .subscribe(
                    data => {
                        var index = this.worksFromBibtex.indexOf(work);
                        this.worksFromBibtex.splice(index, 1);
                        numToSave--;
                        if (numToSave == 0){
                            this.closeAllMoreInfo();
                            this.refreshWorkGroups();
                            this.savingBibtex = false;
                        }
                    },
                    error => {
                        console.log('worksForm.component.ts addWorkError', error);
                    } 
                );
            }
        }
    };

    setBulkGroupPrivacy(priv): void {
        var putCodes = new Array();
        for (var idx in this.worksService.groups){
            if (this.bulkEditMap[this.worksService.groups[idx].activePutCode]){  
                for (var idj in this.worksService.groups[idx].works) {
                    putCodes.push(this.worksService.groups[idx].works[idj].putCode.value);
                    this.worksService.groups[idx].works[idj].visibility.visibility = priv;
                }
            }
        }
        
        if(putCodes.length > 0) {
            this.worksService.updateVisibility(putCodes, priv)
            .pipe(    
                takeUntil(this.ngUnsubscribe)
            )
            .subscribe(
                data => {
                    if (putCodes.length > 0) {
                        this.worksService.updateVisibility(putCodes, priv);
                    }
                    //group.activeVisibility = priv;
                },
                error => {
                    console.log('Error updating group visibility', error);
                } 
            );
        }                
    };

    setGroupPrivacy(putCode, priv): void {
        var group = this.worksService.getGroup(putCode);
        var putCodes = new Array();
        for (var idx in group.works) {
            putCodes.push(group.works[idx].putCode.value);
            group.works[idx].visibility.visibility = priv;
        }
        this.worksService.updateVisibility(putCodes, priv)
        .pipe(    
            takeUntil(this.ngUnsubscribe)
        )
        .subscribe(
            data => {

                if (putCodes.length > 0) {
                    this.worksService.updateVisibility(putCodes, priv);
                }

                group.activeVisibility = priv;
            },
            error => {
                console.log('Error updating group visibility', error);
            } 
        );
    }

    setPrivacy(putCodes, priv): void {
        this.worksService.updateVisibility(putCodes, priv)
        .pipe(    
            takeUntil(this.ngUnsubscribe)
        )
        .subscribe(
            data => {
                if (putCodes.length > 0) {
                    this.worksService.updateVisibility(putCodes, priv);
                }
            },
            error => {
                console.log('Error updating work visibility', error);
            } 
        ); 
    }

    showDetailsMouseClick(group, $event): void {
        $event.stopPropagation();
        this.moreInfo[group.groupId] = !this.moreInfo[group.groupId];
        for (var idx in group.works){
            this.loadDetails(group.works[idx].putCode.value, $event);
        }
    };

    showSources(group, $event): void {
        $event.stopPropagation();
        this.editSources[group.groupId] = true;
        this.hideAllTooltip();
    };

    showTooltip(key): void{       
        this.showElement[key] = true;     
    };

    showURLPopOver(id): void{       
        this.displayURLPopOver[id] = true;
    };

    showWorkImportWizard(): void {
        if(!this.workImportWizard) {
            this.loadWorkImportWizardList();
        }
        this.workImportWizard = !this.workImportWizard;
    }; 

    sort(key): void {
        this.sortState.sortBy(key);
        this.worksService.resetWorkGroups();
        if(this.publicView === "true"){
            this.worksService.getWorksPage(
                this.worksService.constants.access_type.ANONYMOUS, 
                this.sortState.predicateKey, 
                !this.sortState.reverseKey[key]
            )
            .pipe(    
                takeUntil(this.ngUnsubscribe)
            )
            .subscribe(
                data => {
                    this.formData = data;
                    this.worksService.handleWorkGroupData( this.formData );
                    this.worksService.loading = false;
                },
                error => {
                    this.worksService.loading = false;
                    console.log('sortError', error);
                } 
            );

        } else {
            this.worksService.getWorksPage(
                this.worksService.constants.access_type.USER, 
                this.sortState.predicateKey, 
                !this.sortState.reverseKey[key]
            )
            .pipe(    
                takeUntil(this.ngUnsubscribe)
            )
            .subscribe(
                data => {
                    this.formData = data;
                    this.worksService.handleWorkGroupData( this.formData );
                    this.worksService.loading = false;
                },
                error => {
                    this.worksService.loading = false;
                    console.log('sortError', error);
                } 
            );
        }
       
    };

    swapbulkChangeAll(): void {
        this.bulkChecked = !this.bulkChecked;
        for (var idx in this.worksService.groups){
            this.bulkEditMap[this.worksService.groups[idx].activePutCode] = this.bulkChecked;
        }
        this.bulkDisplayToggle = false;
    };

    toggleBibtexExport(): void{
        this.bibtexExportError = false;
        this.bibtexExportLoading = false;
        this.bibtexParsingError = false;
        this.bulkEditShow = false;        
        this.loadingScripts = false;
        this.scriptsLoaded = false;
        this.showBibtexExport  = !this.showBibtexExport;
        this.showBibtexImportWizard = false;
        this.workImportWizard = false;
    };

    toggleBulkEdit(): void {
        this.emailService.getEmails()
        .pipe(    
            takeUntil(this.ngUnsubscribe)
        )
        .subscribe(
            data => {
                this.emails = data;
                if( this.emailService.getEmailPrimary().verified ){
                    this.worksService.loadAllWorkGroups(this.sortState.predicateKey, 
                    !this.sortState.reverseKey[this.sortState.predicateKey])
                    .pipe(    
                        takeUntil(this.ngUnsubscribe)
                    )
                    .subscribe(
                        data => {
                            this.formData = data;
                            this.worksService.handleWorkGroupData( this.formData );
                            this.worksService.loading = false;
                            if (!this.bulkEditShow) {
                                this.bulkEditMap = {};
                                this.bulkChecked = false;
                                for (var idx in this.worksService.groups){
                                    this.bulkEditMap[this.worksService.groups[idx].activePutCode] = false;
                                }
                            }
                            this.bulkEditShow = !this.bulkEditShow;
                            this.showBibtexImportWizard = false;
                            this.workImportWizard = false;
                            this.showBibtexExport = false;
                            this.cdr.detectChanges();
                        },
                        error => {
                            this.worksService.loading = false;
                            console.log('toggleBulkEditError', error);
                        }
                    );
                }else{
                    this.modalService.notifyOther({action:'open', moduleId: 'modalemailunverified'});
                }
            },
            error => {
                console.log('getEmails', error);
            } 
        );
    };

    toggleSectionDisplay($event): void {
        $event.stopPropagation();
        this.workspaceSrvc.displayWorks = !this.workspaceSrvc.displayWorks;
        if(this.workspaceSrvc.displayWorks==false){
            this.workImportWizard=false;
        }
    }

    toggleSelectMenu(): void {                   
        this.bulkDisplayToggle = !this.bulkDisplayToggle;                    
    };
    
    toggleSelectAll(): void {
        this.allSelected = !this.allSelected;
        this.bulkChangeAll(this.allSelected);
    }
    
    toggleWizardDesc(id): void {
        this.wizardDescExpanded[id] = !this.wizardDescExpanded[id];
    };

    userIsSource(work): boolean {
        if (work.source == orcidVar.orcidId){
            return true;
        }
        return false;
    };

    //Default init functions provided by Angular Core
    ngAfterViewInit() {
        //Fire functions AFTER the view inited. Useful when DOM is required or access children directivesload
        this.subscription = this.worksService.notifyObservable$.subscribe(
            (res) => {                
                if(res.action == 'delete') {
                    if(res.successful == true) {
                        this.closeAllMoreInfo();
                        this.refreshWorkGroups();
                        this.allSelected = false;
                        this.bulkEditMap = {};
                    }
                } 
                if(res.action == 'merge') {
                    if(res.successful == true) {
                        this.closeAllMoreInfo();
                        this.refreshWorkGroups();
                        this.allSelected = false;
                        this.bulkEditMap = {};
                    }
                } 
                if(res.action == 'deleteBulk') {
                    if(res.successful == true) {
                        this.bulkEditShow = false;
                        this.closeAllMoreInfo();
                        this.refreshWorkGroups();
                        this.allSelected = false;
                        this.bulkEditMap = {};
                    }
                } 
                if(res.action == 'add' || res.action == 'cancel') {
                    if(res.successful == true) {
                        this.closeAllMoreInfo();
                        this.refreshWorkGroups();
                        this.loadMore();
                        this.allSelected = false;
                        this.bulkEditMap = {};
                    }
                    if(res.bibtex==true){
                        this.worksFromBibtex.splice(this.bibtexWorkIndex, 1);
                        this.bibtexWork = false;
                    }
                }                
            }
        );
    };

    ngOnDestroy() {
        this.ngUnsubscribe.next();
        this.ngUnsubscribe.complete();
    };

    ngOnInit() {
        // Check for the various File API support.
        if ((<any>window).File != undefined && (<any>window).FileReader != undefined  && (<any>window).FileList != undefined  && (<any>window).Blob) {
            this.canReadFiles = true;
        };
        this.loadMore();
        this.loadWorkImportWizardList();
    };
}