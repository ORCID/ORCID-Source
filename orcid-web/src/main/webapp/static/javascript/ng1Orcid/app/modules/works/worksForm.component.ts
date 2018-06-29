declare var ActSortState: any;
declare var GroupedActivities: any;
declare var om: any;
declare var openImportWizardUrl: any;

import { NgForOf, NgIf } 
    from '@angular/common'; 

import { AfterViewInit, ChangeDetectorRef, Component, OnDestroy, OnInit } 
    from '@angular/core';

import { Observable, Subject, Subscription } 
    from 'rxjs';
import { takeUntil } 
    from 'rxjs/operators';

import { CommonService } 
    from '../../shared/common.service.ts';

import { WorksService } 
    from '../../shared/works.service.ts';

import { EmailService } 
    from '../../shared/email.service.ts';

import { ModalService } 
    from '../../shared/modal.service.ts';

import { WorkspaceService } 
    from '../../shared/workspace.service.ts'; 

@Component({
    selector: 'works-form-ng2',
    template:  scriptTmpl("works-form-ng2-template")
})
export class WorksFormComponent implements AfterViewInit, OnDestroy, OnInit {
    private ngUnsubscribe: Subject<void> = new Subject<void>();
    private subscription: Subscription;
    private viewSubscription: Subscription;

    addingWork: boolean;
    bibtexExportError: boolean;
    bibtexLoading: boolean;
    bibtexParsingError: boolean;
    bibtextWork: boolean;
    bibtextWorkIndex: any;
    bulkChecked: any;
    bulkDeleteCount: number;
    bulkDeleteSubmit: boolean;
    bulkDisplayToggle: false;
    bulkEditMap: any;
    bulkEditShow: boolean;
    combineWork: any;
    delCountVerify: number;
    displayURLPopOver: any;
    editSources: any;
    editWork: any;
    emails: any;
    emailSrvc: any;
    formData: any;
    geoArea: any;
    loadingScripts: any;
    moreInfo: any;
    moreInfoOpen: boolean;
    noLinkFlag: boolean;
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
    contentCopy: any;
    badgesRequested: any; 
    bibtexGenerated: any;            
    bibtexURL: any;
    canReadFiles: any;
    combiningWorks: any;
    editTranslatedTitle: any;
    externalIDNamesToDescriptions: any;//caches name->description lookup so we can display the description not the name after selection
    externalIDTypeCache: any;//cache responses
    generatingBibtex: any;
    privacyHelp: any;
    types: any;

    constructor( 
        private cdr: ChangeDetectorRef,
        private commonService: CommonService,
        private emailService: EmailService,
        private modalService: ModalService,
        private workspaceSrvc: WorkspaceService,
        private worksService: WorksService
    ) {
        //console.log('works component init');
        this.contentCopy = {
            titleLabel: om.get("orcid.frontend.manual_work_form_contents.defaultTitle"),
            titlePlaceholder: om.get("orcid.frontend.manual_work_form_contents.defaultTitlePlaceholder")
        };
        this.addingWork = false;
        this.bibtexExportError = false;
        this.bibtexLoading = false;
        this.bibtexParsingError = false;
        this.bibtextWork = false;
        this.bibtextWorkIndex = null;
        this.bulkChecked = false;
        this.bulkDeleteCount = 0;
        this.bulkDeleteSubmit = false;
        this.bulkDisplayToggle = false;
        this.bulkEditMap = {};
        this.bulkEditShow = false;
        this.combineWork = null;
        this.delCountVerify = 0;
        this.displayURLPopOver = {};
        this.editSources = {};
        this.editWork = this.getEmptyWork();
        this.emails = {};
        this.formData = {
            works: null
        };
        this.geoArea = ['All'];
        this.loadingScripts = false;
        this.moreInfo = {};
        this.moreInfoOpen = false;
        this.noLinkFlag = true;
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
        this.badgesRequested = {};  
        this.bibtexGenerated = false;            
        this.bibtexURL = "";
        this.canReadFiles = false;
        this.combiningWorks = false;
        this.editTranslatedTitle = false;
        this.externalIDNamesToDescriptions = [];//caches name->description lookup so we can display the description not the name after selection
        this.externalIDTypeCache = [];//cache responses
        this.generatingBibtex = false;
        this.privacyHelp = {};
        this.types = null;
    }

    getEmptyWork(): any {
        return {
            citation: {
                citation: {
                    errors: {}, 
                    value: null
                },
                citationType: {
                    errors: {}, 
                    value: null
                }
            },
            contributors: {},
            countryCode: {
                errors: {}, 
                value: null
            },
            errors: {},
            journalTitle: {
                errors: {}, 
                value: null
            },
            languageCode: {
                errors: {}, 
                value: null
            },
            publicationDate: {
                errors: {}, 
                value: null
            },
            putCode: {
                value: null
            },
            shortDescription: {
                errors: {}, 
                value: null
            },
            subtitle: {
                errors: {}, 
                value: null
            },
            title: {
                errors: {}, 
                value: null
            },
            translatedTitle: {
                content: null,
                errors: {}, 
            },
            url: {
                errors: {}, 
                value: null
            },
            workCategory: {
                errors: {}, 
                value: null
            },
            workExternalIdentifiers: []
            ,
        };

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
                workExternalIdentifierId: {
                    value: ""
                }, 
                workExternalIdentifierType: {
                    value: ""
                } 
            }
        );
        console.log(this.editWork.workExternalIdentifiers);
    };

    applyLabelWorkType(): void {
        var obj = null;
        var that = this;
        setTimeout(
            function() {
                obj = that.worksService.getLabelMapping(that.editWork.workCategory.value, that.editWork.workType.value)
                that.contentCopy = obj;
            }, 
            100
        );

    };

    clearErrors(): void {
        this.editWork.workCategory.errors = [];
        this.editWork.workType.errors = [];
    };

    closeModal(): void {
        this.modalService.notifyOther({action:'close', moduleId: 'modalWorksForm'});
    };

    deleteContributor(obj): void {
        var index = this.editWork.contributors.indexOf(obj);
        this.editWork.contributors.splice(index,1);
    };

    deleteExternalIdentifier(obj): void {
        var index = this.editWork.workExternalIdentifiers.indexOf(obj);
        this.editWork.workExternalIdentifiers.splice(index,1);
    };

    isValidClass(cur): any {
        var valid = true;
        if (cur === undefined || cur == null) {
            return '';
        }
        if ( ( cur.required && (cur.value == null || cur.value.trim() == '') ) || ( cur.errors !== undefined && cur.errors.length > 0 ) ){
            valid = false;
        }
        return valid ? '' : 'text-error';
    };

    loadWorkTypes(): Observable<any>{
        var workCategory = "";
        if(this.editWork != null && this.editWork.workCategory != null && this.editWork.workCategory.value != null && this.editWork.workCategory.value != ""){
            workCategory = this.editWork.workCategory.value;
        }
        else{
            return; //do nothing if we have not types
        }
        this.worksService.loadWorkTypes(workCategory)
        .pipe(    
            takeUntil(this.ngUnsubscribe)
        )
        .subscribe(
            data => {
                this.types = data;
                if(this.editWork != null && this.editWork.workCategory != null) {
                    // if the edit works doesn't have a value that matches types
                    var hasType = false;
                    for (var idx in this.types){
                        if (this.types[idx].key == this.editWork.workType.value) hasType = true;
                    }
                    if(!hasType) {
                        switch (this.editWork.workCategory.value){
                        case "conference":
                            this.editWork.workType.value="conference-paper";
                            break;
                        case "intellectual_property":
                            this.editWork.workType.value="patent";
                            break;
                        case "other_output":
                            this.editWork.workType.value="data-set";
                            break;
                        case "publication":
                            this.editWork.workType.value="journal-article";
                            break;
                        }
                    }
                }
            },
            error => {
                console.log('loadWorkTypesError', error);
            } 
        );
    };

    /*addAffiliation(): void {
        if (this.addingAffiliation == true) {
            return; // don't process if adding affiliation
        }

        this.addingAffiliation = true;
        this.editAffiliation.errors.length = 0;
        this.affiliationService.setData( this.editAffiliation )
        .pipe(    
            takeUntil(this.ngUnsubscribe)
        )
        .subscribe(
            data => {
                this.editAffiliation = data;
                this.addingAffiliation = false;
                if (data.errors.length > 0){                    
                    this.editAffiliation = data;
                    this.commonService.copyErrorsLeft(this.editAffiliation, data);
                } else {
                    this.closeModal();
                    this.removeDisambiguatedAffiliation();
                    this.editAffiliation = this.getEmptyAffiliation();
                    this.affiliationService.notifyOther({action:'add', successful:true});
                }
            },
            error => {
                console.log('affiliationForm.component.ts addAffiliation Error', error);
            } 
        );
    };*/

    addWork(): any{
        console.log(this.editWork);
        this.addingWork = true;
        this.editWork.errors.length = 0;
        this.worksService.postWork( this.editWork)
        .pipe(    
            takeUntil(this.ngUnsubscribe)
        )
        .subscribe(
            data => {
                this.addingWork= false;
                if (data.errors.length > 0){                    
                    this.editWork = data;
                    this.commonService.copyErrorsLeft(this.editWork, data);
                } else {
                    if (this.bibtextWork == false){
                        this.closeModal();
                        this.addingWork = false;
                    } else {
                        this.worksFromBibtex.splice(this.bibtextWorkIndex, 1);
                        this.bibtextWork = false;
                        this.addingWork = false;
                        this.closeModal();
                    }
                    this.editWork = this.getEmptyWork();
                    this.worksService.notifyOther({action:'add', successful:true});
                }

            },
            error => {
                console.log('worksForm.component.ts addWorkError', error);
            } 
        );
    };

    serverValidate(relativePath): void {
        this.worksService.serverValidate(this.editWork, relativePath)
        .pipe(    
            takeUntil(this.ngUnsubscribe)
        )
        .subscribe(
            data => {
                console.log('data', data);
                if (data != null) {
                    this.commonService.copyErrorsLeft(this.editWork, data);
                }
            },
            error => {
            } 
        );
    }

    toggleTranslatedTitle(): void{
        this.editTranslatedTitle = !this.editTranslatedTitle;
        //$('#translatedTitle').toggle();
    };

    //Default init functions provided by Angular Core
    ngAfterViewInit() {
        //Fire functions AFTER the view inited. Useful when DOM is required or access children directives
        this.subscription = this.worksService.notifyObservable$.subscribe(
            (res) => {
                //this.bindTypeahead();
                if( res.work != undefined ) {
                    this.editWork = res.work;
                    this.loadWorkTypes();
                } else {
                    this.editWork = this.getEmptyWork();
                }
            }
        );
        
        this.viewSubscription = this.modalService.notifyObservable$.subscribe(
            (res) => {
                if(res.moduleId == "modalWorksForm'") {
                    if(res.action == "open" && res.edit == false) {
                        this.editWork = this.getEmptyWork();
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

    };
}