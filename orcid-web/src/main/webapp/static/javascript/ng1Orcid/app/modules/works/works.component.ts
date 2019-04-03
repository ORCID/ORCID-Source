declare var $: any;
declare var bibtexParse: any;
declare var blobObject: any;
declare var om: any;
declare var openImportWizardUrl: any;
declare var populateWorkAjaxForm: any;

import { AfterViewInit, ChangeDetectorRef, Component, ElementRef, Input, OnDestroy, OnInit } 
    from '@angular/core';

import { forkJoin, Subject, Subscription } 
    from 'rxjs';

import { takeUntil } 
    from 'rxjs/operators';

import { CommonService } 
    from '../../shared/common.service';

import { EmailService } 
    from '../../shared/email.service';

import { FeaturesService }
    from '../../shared/features.service';

import { ModalService } 
    from '../../shared/modal.service';

import { WorksService } 
    from '../../shared/works.service';

import { WorkspaceService } 
    from '../../shared/workspace.service'; 

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
    bibtexParsingErrorText: any;
    bibtexExportLoading: boolean;
    bibtexWork: boolean;
    bibtexWorkIndex: any;
    bulkChecked: any;
    bulkDeleteCount: number;
    bulkDisplayToggle: boolean;
    bulkEditMap: any;
    bulkEditShow: boolean;
    bulkSelectedCount: number;
    canReadFiles: boolean;
    deleteGroup: any;
    deletePutCode: any;
    displayURLPopOver: any;
    editSources: any;
    editWork: any;
    emails: any;
    fixedTitle: any;
    formData: any;
    geoArea: any;
    groupingSuggestionExtIdsPresent: boolean;
    groupingSuggestionPresent: boolean;
    groupingSuggestion: any;
    groupingSuggestionWorksToMerge: any;
    isPublicPage: boolean;
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
    showMergeWorksApiMissingExtIdsError: boolean;
    showMergeWorksExtIdsError: boolean;
    sortAsc: boolean;
    sortKey: string;
    textFiles: any;
    wizardDescExpanded: any;
    workImportWizard: boolean;
    workImportWizardsOriginal: any;
    worksToMerge: Array<any>;
    workType: any;
    worksFromBibtex: any;
    allSelected: boolean;
    bibTexIntervals: object;
    TOGGLZ_ADD_WORKS_WITH_EXTERNAL_ID: boolean;
    manualWorkGroupingEnabled: boolean;
    exIdResolverFeatureEnabled: boolean;
    groupingSuggestionFeatureEnabled: boolean;    
    
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
        this.bibtexParsingErrorText = "";
        this.bibtexWork = false;
        this.bibtexWorkIndex = null;
        this.bulkChecked = false;
        this.bulkDeleteCount = 0;
        this.bulkDisplayToggle = false;
        this.bulkEditMap = {};
        this.bulkEditShow = false;
        this.bulkSelectedCount = 0;
        this.canReadFiles = false;
        this.displayURLPopOver = {};
        this.editSources = {};
        this.editWork = null;
        this.emails = {};
        this.formData = {
            works: null
        };
        this.geoArea = ['All'];
        this.groupingSuggestionExtIdsPresent = false;
        this.groupingSuggestionPresent = false;
        this.isPublicPage = this.commonSrvc.isPublicPage;
        this.loadingScripts = false;
        this.moreInfo = {};
        this.moreInfoOpen = false;
        this.noLinkFlag = true;
        this.publicView = elementRef.nativeElement.getAttribute('publicView');
        this.printView = elementRef.nativeElement.getAttribute('printView');
        this.savingBibtex = false;
        this.scriptsLoaded = false;
        this.showBibtex = {};
        this.showBibtexExport = false;
        this.showBibtexImportWizard = false;
        this.showElement = {};
        this.showMergeWorksApiMissingExtIdsError = false;
        this.showMergeWorksExtIdsError = false;
        this.sortAsc = false;
        this.sortKey = "date";
        this.textFiles = [];
        this.wizardDescExpanded = {};
        this.workImportWizard = false;
        this.workImportWizardsOriginal = null;
        this.workType = ['All'];
        this.worksFromBibtex = null;
        this.TOGGLZ_ADD_WORKS_WITH_EXTERNAL_ID = this.featuresService.isFeatureEnabled('ADD_WORKS_WITH_EXTERNAL_ID');
        this.manualWorkGroupingEnabled = this.featuresService.isFeatureEnabled('MANUAL_WORK_GROUPING');
        this.exIdResolverFeatureEnabled = this.featuresService.isFeatureEnabled('EX_ID_RESOLVER');
        this.groupingSuggestionFeatureEnabled = this.featuresService.isFeatureEnabled('GROUPING_SUGGESTIONS');        
        om.process().then(() => { 
            this.selectedWorkType = om.get('workspace.works.import_wizzard.all');
            this.selectedGeoArea = om.get('workspace.works.import_wizzard.all');
        });        
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
                        if (!this.worksFromBibtex.length) {
                            this.openBibTextWizard()
                        }
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

    addWorkExternalIdModal(externalIdType): void {
        this.emailService.getEmails()
        .pipe(    
            takeUntil(this.ngUnsubscribe)
        )
        .subscribe(
            data => {
                this.emails = data;
                if( this.emailService.getEmailPrimary().verified ){
                        this.modalService.notifyOther({action:'open', moduleId: 'modalExternalIdForm', externalIdType: externalIdType});                
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
        this.showMergeWorksExtIdsError = false;
        this.showMergeWorksApiMissingExtIdsError = false;
        this.bulkSelectedCount = 0;
        this.bulkChecked = bool;
        this.bulkDisplayToggle = false;
        for (var idx in this.worksService.groups){
            this.bulkEditMap[this.worksService.groups[idx].activePutCode] = bool;
            if(this.bulkChecked == true){
                this.bulkSelectedCount ++;
            }
        }
    };

    bulkEditSelect(): void {
        this.showMergeWorksExtIdsError = false;
        this.showMergeWorksApiMissingExtIdsError = false;
        this.allSelected = false;
        this.bulkSelectedCount = 0;
        for (var idx in this.worksService.groups){
            if (this.bulkEditMap[this.worksService.groups[idx].activePutCode]){
                this.bulkSelectedCount++;
            }
        }
    }

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

    dismissError(error){
        if(error == "showMergeWorksExtIdsError"){
            this.showMergeWorksExtIdsError = false;
        }
        if(error == "showMergeWorksApiMissingExtIdsError"){
            this.showMergeWorksApiMissingExtIdsError = false;
        }
    }
    
    mergeConfirm(): void {
        this.worksToMerge = new Array();
        var idx: any;
        var groupCount = 0;
        var mergeCount = 0;       
        for (idx in this.worksService.groups){
            if (this.bulkEditMap[this.worksService.groups[idx].activePutCode]){
                groupCount++;
            }
        }
        if (groupCount > 1) {
            var apiWorkMissingExtIds = false;
            var apiWorkPresent = false;
            var externalIdsPresent = false; 
            for (var putCode in this.bulkEditMap) {
                if (this.bulkEditMap[putCode]) {
                    for (var i in this.worksService.groups) {
                        if(this.worksService.groups[i].activePutCode == putCode){
                            for (var j in this.worksService.groups[i].works) {
                                this.worksToMerge.push(this.worksService.getDetails(this.worksService.groups[i].works[j].putCode.value, this.worksService.constants.access_type.USER).pipe(takeUntil(this.ngUnsubscribe)));
                                mergeCount++;

                            }
                        
                        }
                    }
                }

            }       
            forkJoin(this.worksToMerge).subscribe(
                dataGroup => {
                    for(var i in dataGroup){
                        if(dataGroup[i].source != orcidVar.orcidId){
                            apiWorkPresent = true;
                            if(dataGroup[i].workExternalIdentifiers.length == 0){
                                apiWorkMissingExtIds = true;
                                break;
                            } else {
                                var currentApiWorkExtIds = 0;
                                for(var j in dataGroup[i].workExternalIdentifiers){ 
                                    if(dataGroup[i].workExternalIdentifiers[j].relationship.value == 'self'){
                                        currentApiWorkExtIds++;
                                    }
                                }
                                if(currentApiWorkExtIds == 0){
                                    apiWorkMissingExtIds = true;   
                                }
                            }
                        } else {
                            for(var j in dataGroup[i].workExternalIdentifiers){ 
                                if(dataGroup[i].workExternalIdentifiers[j].relationship.value == 'self'){
                                    externalIdsPresent = true;
                                }
                            }
                        }
                    }
                    if(apiWorkPresent && apiWorkMissingExtIds){
                        this.showMergeWorksApiMissingExtIdsError = true;
                    } else if(!apiWorkPresent && !externalIdsPresent){
                        this.showMergeWorksExtIdsError = true;
                    } else {
                        this.worksService.notifyOther({worksToMerge:dataGroup});       
                        this.worksService.notifyOther({mergeCount:mergeCount});
                        this.worksService.notifyOther({groupingSuggestion: false});
                        if(mergeCount < 20){
                            this.modalService.notifyOther({action:'open', moduleId: 'modalWorksMerge'});
                        } else {
                            this.modalService.notifyOther({action:'open', moduleId: 'modalWorksMerge', width: '600', height: '200'});
                        }
                    }   
                    
                },
                error => {
                    console.log('mergeConfirm', error);
                } 
            );
        }
    };

    mergeSuggestionConfirm(): void {
        this.groupingSuggestionWorksToMerge = []
        this.groupingSuggestion.suggestions.forEach(suggestionGroup => {
            const subList = []
            suggestionGroup.putCodes.forEach(putCode => {
                subList.push(this.worksService.getDetails(putCode, this.worksService.constants.access_type.USER).pipe(takeUntil(this.ngUnsubscribe)))
            });
            this.groupingSuggestionWorksToMerge.push (forkJoin(subList))
        });

        forkJoin(this.groupingSuggestionWorksToMerge).subscribe(
            dataGroup => {
                this.worksService.notifyOther({worksToMerge:false});
                this.worksService.notifyOther({orcid:this.groupingSuggestion.suggestions[0].orcid});
                this.worksService.notifyOther({groupingSuggestion:dataGroup});    
                this.worksService.notifyOther({mergeCount:this.groupingSuggestion.suggestions[0].putCodes.length});
                this.modalService.notifyOther({action:'open', moduleId: 'modalWorksMerge'});
            },
            error => {
                console.log('mergeSuggestionConfirm', error);
            } 
        );
        
        
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
                    this.worksService.notifyOther({fixedTitle:this.fixedTitle, putCode:putCode, deleteGroup:deleteGroup});
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
        this.worksFromBibtex = new Array();
        for (let bibtex of this.textFiles) {
            let reader = new FileReader();
            reader.readAsText(bibtex);
            reader.onloadend = function(e){
                var parsed = bibtexParse.toJSON(reader.result);
                if (typeof parsed == "string" && parsed.substring(0,5).toLowerCase().indexOf('error') > -1) {
                    this.bibtexParsingErrorText = parsed;
                    this.bibtexParsingError = true;
                } else {
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
                }    
            }.bind(this);
            this.bibtexParsingError = false; 
            this.bibtexParsingErrorText = "";  
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
                this.worksService.loadAllPublicWorkGroups(this.sortKey, 
                    this.sortAsc
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
                this.worksService.getWorksPage(this.worksService.constants.access_type.ANONYMOUS, this.sortKey, 
                    this.sortAsc
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
            this.worksService.getWorksPage(this.worksService.constants.access_type.USER, this.sortKey, 
                this.sortAsc
            )
            .pipe(    
                takeUntil(this.ngUnsubscribe)
            )
            .subscribe(
                data => {
                    this.formData = data;
                    this.worksService.handleWorkGroupData( this.formData );
                    this.worksService.loading = false;
                    if(this.groupingSuggestionFeatureEnabled){
                        this.loadGroupingSuggestions(false);
                    }
                },
                error => {
                    this.worksService.loading = false;
                    console.log('worksLoadMore', error);
                } 
            );
        }
    };
    
    loadGroupingSuggestions(openSuggestionsMenuAfterLoad: boolean): void {
        this.groupingSuggestionPresent = false;
        if(this.publicView != "true") {
            this.worksService.getWorksGroupingSuggestions(
            )
            .pipe(    
                takeUntil(this.ngUnsubscribe)
            )
            .subscribe(
                data => {
                    if (data && data.suggestions &&  data.suggestions.length) {
                        this.groupingSuggestionPresent = true;
                        this.groupingSuggestion = data;
                        if (openSuggestionsMenuAfterLoad) {
                            this.mergeSuggestionConfirm()
                        }
                    }
                },
                error => {
                    console.log('loadGroupingSuggestions', error);
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
                    this.bibtexImportLoading = false;
                    this.bibtexParsingErrorText = "";
                    this.bibtexParsingError = false;
                    this.bulkEditShow = false;
                    this.showBibtexExport = false;
                    this.showBibtexImportWizard = !(this.showBibtexImportWizard);
                    this.workImportWizard = false;
                    this.worksFromBibtex = null;
                    if (this.bibTexIntervals && this.savingBibtex) {
                        // THE UPLOAD WAS CANCELLED DURING THE PROCESS 
                        Object.keys(this.bibTexIntervals).forEach(interval => {
                            clearInterval(this.bibTexIntervals[interval])
                        });
                        this.closeAllMoreInfo();
                        this.refreshWorkGroups();
                        this.savingBibtex = false;
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

    openEditWork(putCode): void{
        this.getEditable(putCode, function(data) {
            this.addWorkModal(data);
        }.bind(this));
    };

    openFileDialog(): void{
        this.bibtexParsingErrorText = "";
        this.bibtexParsingError = false;
        this.textFiles = [];
    };

    openImportWizardUrl(url): void {
        openImportWizardUrl(url);
    };

    openImportWizardUrlFilter(url, client): void {
        url = url + '?client_id=' + client.id + '&response_type=code&scope=' + client.scopes + '&redirect_uri=' + client.redirectUri;
        openImportWizardUrl(url);
    };

    refreshWorkGroups(): void {
        this.worksService.refreshWorkGroups(this.sortKey, 
            this.sortAsc
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
        if (!this.worksFromBibtex.length) {
            this.openBibTextWizard() // CLOSE BIBTEX
        }
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
            const batchSize = 20;
            let numToSave = worksToSave.length;
            let  currentBatch = 0; 
            let  worksLeftOfCurrentBatch = batchSize; 
            this.bibTexIntervals = {}
            for (let work of worksToSave) {
                this.bibTexIntervals[worksToSave.indexOf(work)] =  setInterval ( (work) => {
                    let workIndex = worksToSave.indexOf(work)
                    let batchNumber = parseInt (workIndex/ batchSize +  '')
                    if (batchNumber == currentBatch){
                        // THIS WORK BELLOWS TO THE CURRENT BATCH TO UPLOAD
                        clearInterval(this.bibTexIntervals [workIndex])
                        this.worksService.postWork(work)
                        .pipe(    
                            takeUntil(this.ngUnsubscribe)
                        )
                        .subscribe(
                            data => {
                                if (this.savingBibtex) {
                                    // THE UPLOAD HAVEN'T BEEN CANCELLED
                                    var index = this.worksFromBibtex.indexOf(work);
                                    this.worksFromBibtex.splice(index, 1);
                                    numToSave--;
                                    worksLeftOfCurrentBatch--;
                                    if (worksLeftOfCurrentBatch === 0) {
                                        // UPLOAD BATCH FINISH 
                                        currentBatch++
                                        worksLeftOfCurrentBatch = batchSize;
                                    }
                                    if (numToSave === 0){
                                        // ALL WORKS UPLOADED
                                        this.closeAllMoreInfo();
                                        this.refreshWorkGroups();
                                        this.savingBibtex = false;
                                        this.openBibTextWizard(); // CLOSE BIBTEX
                                        
                                    }
                                }

                            },
                            error => {
                                console.log('worksForm.component.ts addWorkError', error);
                            } 
                        );
                    }
                },  (100 ), 
                work

                )
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
            this.callServerBulkPrivacyUpdate (putCodes, priv) 
        }
                   
    };

    callServerBulkPrivacyUpdate(putCodes, priv) {
            this.worksService.updateVisibility(putCodes.splice(0, 50), priv)
            .pipe(    
                takeUntil(this.ngUnsubscribe)
            )
            .subscribe(
                data => {
                    if (putCodes.length > 0) {
                        this.callServerBulkPrivacyUpdate(putCodes, priv);
                    }
                    //group.activeVisibility = priv;
                },
                error => {
                    console.log('Error updating group visibility', error);
                } 
            );  
    }

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
        if(key == this.sortKey){
            this.sortAsc = !this.sortAsc;
        } else {
            if(key=='title' || key=='type'){
                this.sortAsc = true;
            }
            this.sortKey = key;
        }
        this.worksService.resetWorkGroups();
        if(this.publicView === "true"){
            this.worksService.getWorksPage(
                this.worksService.constants.access_type.ANONYMOUS, 
                this.sortKey, 
                this.sortAsc
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
                this.sortKey, 
                this.sortAsc
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
                    this.worksService.loadAllWorkGroups(this.sortKey, 
                    this.sortAsc)
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
                        this.bulkEditSelect();
                        if (res.groupingSuggestion && res.groupingSuggestion.moreAvailable) {
                            setTimeout(()=>{this.loadGroupingSuggestions(true)}, 500)
                        }
                        else {
                            this.loadMore();
                        }
                    }
                    else {
                        this.loadMore();
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
    
    getBaseUri(): String {
        return getBaseUri();
    };
}