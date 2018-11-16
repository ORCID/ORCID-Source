declare var ActSortState: any;
declare var GroupedActivities: any;
declare var om: any;
declare var openImportWizardUrl: any;
declare var typeahead: any;
declare var workIdLinkJs: any;

import { NgForOf, NgIf } 
    from '@angular/common'; 

import { AfterViewInit, ChangeDetectorRef, Component, OnDestroy, OnInit } 
    from '@angular/core';

import { Observable, of, Subject, Subscription } 
    from 'rxjs';

import { catchError, debounceTime, distinctUntilChanged, filter, map, switchMap, takeUntil, tap } 
    from 'rxjs/operators';

import { CommonService } 
    from '../../shared/common.service.ts';

import { WorksService } 
    from '../../shared/works.service.ts';

import { FeaturesService }
    from '../../shared/features.service.ts';

import { ModalService } 
    from '../../shared/modal.service.ts';

@Component({
    selector: 'works-form-ng2',
    template:  scriptTmpl("works-form-ng2-template")
})

export class WorksFormComponent implements AfterViewInit, OnDestroy, OnInit {
    private ngUnsubscribe: Subject<void> = new Subject<void>();
    private subscription: Subscription;
    private viewSubscription: Subscription;

    addingWork: boolean;
    bibtexWork: boolean;
    bibtexWorkIndex: any;
    editWork: any;
    exIdResolverFeatureEnabled = this.featuresService.isFeatureEnabled('EX_ID_RESOLVER');
    worksFromBibtex: any;
    contentCopy: any;
    editTranslatedTitle: any;
    externalIDNamesToDescriptions: any;
    externalIDTypeCache: any;
    types: any;
    togglzDialogPrivacyOption: boolean;
    sortedCountryNames: any;
    countryNamesToCountryCodes: any;

    constructor( 
        private cdr: ChangeDetectorRef,
        private commonService: CommonService,
        private featuresService: FeaturesService,
        private modalService: ModalService,
        private worksService: WorksService
    ) {
        this.contentCopy = {
            titleLabel: om.get("orcid.frontend.manual_work_form_contents.defaultTitle"),
            titlePlaceholder: om.get("orcid.frontend.manual_work_form_contents.defaultTitlePlaceholder")
        };
        this.addingWork = false;
        this.bibtexWork = false;
        this.bibtexWorkIndex = null;
        this.editWork = this.getEmptyWork();
        this.worksFromBibtex = null;  
        this.editTranslatedTitle = false;
        this.externalIDNamesToDescriptions = [];//caches name->description lookup so we can display the description not the name after selection
        this.externalIDTypeCache = [];//cache responses
        this.types = null;
        this.initCountries();
    }
    
    initCountries(): void {
        this.commonService.getCountryNamesMappedToCountryCodes().pipe(    
            takeUntil(this.ngUnsubscribe)
        )
        .subscribe(
            data => {
                this.countryNamesToCountryCodes = data;
                this.sortedCountryNames = [];
                for (var key in this.countryNamesToCountryCodes) {
                    this.sortedCountryNames.push(key);
                }
                this.sortedCountryNames.sort();
            },
            error => {
                console.log('error fetching country names to country codes map', error);
            } 
        );
    };

    search = (text$: Observable<string>) =>
    text$.pipe(
      debounceTime(300),
      distinctUntilChanged(),
      map(term => term === '' ? []
        : this.externalIDTypeCache.filter(v => v.name.toLowerCase().indexOf(term.toLowerCase()) > -1).slice(0, 10))
    );

    addExternalIdentifier(): void {
        this.editWork.workExternalIdentifiers.push({externalIdentifierId: {value: ""}, externalIdentifierType: {value: ""}, relationship: {value: "self"}, url: {value: ""}});
    };

    addWork(): any{
        this.addingWork = true;
        this.editWork.errors.length = 0;
        this.worksService.postWork( this.editWork)
        .pipe(    
            takeUntil(this.ngUnsubscribe)
        )
        .subscribe(
            data => {
                this.addingWork= false;
                if (data.errors.length > 0) {
                    this.editWork = data;                    
                    this.commonService.copyErrorsLeft(this.editWork, data);
                    //re-populate any id resolution errors.
                    //do it here because they're by-passable
                    if (this.exIdResolverFeatureEnabled == true){
                        for (var extId in this.editWork.workExternalIdentifiers){
                            this.fillUrl(this.editWork.workExternalIdentifiers[extId]);
                        }
                    }
                } else {
                    if (this.bibtexWork != false){
                        this.bibtexWork = false;
                        this.worksService.notifyOther({action:'add', successful:true, bibtex:true}); 
                    }
                    this.modalService.notifyOther({action:'close', moduleId: 'modalWorksForm'});
                    this.worksService.notifyOther({action:'add', successful:true, bibtex:false}); 
                }

            },
            error => {
                console.log('worksForm.component.ts addWorkError', error);
            } 
        );
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

    cancelEdit(): void {
        this.modalService.notifyOther({action:'close', moduleId: 'modalWorksForm'});
        this.worksService.notifyOther({action:'cancel', successful:true});
    };
    
    changeExtIdType(i, event): void {
        event.preventDefault();
        this.editWork.workExternalIdentifiers[i].externalIdentifierType.value = event.item.name;
        if (this.exIdResolverFeatureEnabled == true){
            if(this.editWork.workExternalIdentifiers[i].url == null) {
                this.editWork.workExternalIdentifiers[i].url = {value:""};
            }else{
                this.editWork.workExternalIdentifiers[i].url.value="";                        
            }
        }
        this.fillUrl(i);
    }

    deleteContributor(obj): void {
        var index = this.editWork.contributors.indexOf(obj);
        this.editWork.contributors.splice(index,1);
    };

    deleteExternalIdentifier(index): void {
        if (index > -1) {
            this.editWork.workExternalIdentifiers.splice(index,1);
            this.cdr.detectChanges();
        }
    };

    fillUrl(i): void {
        //if we have a value and type, generate URL.  If no URL, but attempted resolution, show warning.
        if (this.exIdResolverFeatureEnabled == true){
            if (this.editWork.workExternalIdentifiers[i] && this.editWork.workExternalIdentifiers[i].externalIdentifierId.value && this.editWork.workExternalIdentifiers[i].externalIdentifierType.value){
                this.editWork.workExternalIdentifiers[i].resolvingId = true;
                this.worksService.resolveExtId(this.editWork.workExternalIdentifiers[i])
                .pipe(    
                    takeUntil(this.ngUnsubscribe)
                )
                .subscribe(
                    data => {
                        this.editWork.workExternalIdentifiers[i].externalIdentifierId.errors = [];
                        if (data.generatedUrl){
                            if(this.editWork.workExternalIdentifiers[i].url == null) {
                                this.editWork.workExternalIdentifiers[i].url = {value:data.generatedUrl};
                            }else{
                                this.editWork.workExternalIdentifiers[i].url.value=data.generatedUrl;                        
                            }
                        } else if (!data.validFormat || (data.attemptedResolution && !data.resolved) ){
                            if(this.editWork.workExternalIdentifiers[i].url == null) {
                                this.editWork.workExternalIdentifiers[i].url = {value:""};
                            }else{
                                this.editWork.workExternalIdentifiers[i].url.value="";                        
                            }
                            this.editWork.workExternalIdentifiers[i].externalIdentifierId.errors.push(om.get('orcid.frontend.manual_work_form_errors.id_unresolvable'));
                        }
                        this.editWork.workExternalIdentifiers[i].resolvingId = false;
                    },
                    error => {
                        console.log("id resolver error");
                        this.editWork.workExternalIdentifiers[i].resolvingId = false;
                    } 
                );
            }
        } else{
            var url;
            if(this.editWork.workExternalIdentifiers[i] != null) {
                url = workIdLinkJs.getLink(this.editWork.workExternalIdentifiers[i].externalIdentifierId.value, this.editWork.workExternalIdentifiers[i].externalIdentifierType.value);
                if(this.editWork.workExternalIdentifiers[i].url == null) {
                    this.editWork.workExternalIdentifiers[i].url = {value:url};
                }else{
                    this.editWork.workExternalIdentifiers[i].url.value=url;                        
                }
            }
        }
    };

    formatExtIdTypeInput = function(input) {
        if (typeof(input)=='object' && input.name){
            return this.externalIDNamesToDescriptions[input.name].description; 
        } else if (typeof(input)=='string' && input != "")  {
            return this.externalIDNamesToDescriptions[input].description;
        } else {
            return "";
        }   
    }.bind(this);

    formatExtIdTypeResult = (result: {description: string}) => result.description;

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
            countryName: {
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
            languageName: {
                errors: {}, 
                value: null
            },
            publicationDate: {
                errors: {}, 
                month: "",
                day: "",
                year: "",
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
                value: ""
            },
            workType: {
                errors: {}, 
                value: ""
            },
            workExternalIdentifiers: [
                {
                    errors: {},
                    externalIdentifierId: {
                        errors: {},
                        value: null
                    },
                    externalIdentifierType: {
                        errors: {},
                        value: null
                    },
                    url: {
                        errors: {},
                        value: null
                    },
                    relationship: {
                        errors: {},
                        value: null
                    },
                }
            ]
            ,
        };

    }

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
                        if(this.types[idx].key == 'other'){
                            this.types.push(this.types.splice(idx, 1)[0]);
                        }
                    }
                    for (var idx in this.types){                        
                        if (this.types[idx].key == this.editWork.workType.value){
                            hasType = true;
                            break;
                        }                        
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

    serverValidate(relativePath): void {
        this.worksService.serverValidate(this.editWork, relativePath)
        .pipe(    
            takeUntil(this.ngUnsubscribe)
        )
        .subscribe(
            data => {
                if (data != null) {
                    this.commonService.copyErrorsLeft(this.editWork, data);
                }
            },
            error => {
                console.log('Error validating' + relativePath, error);
            } 
        );
    }

    toggleTranslatedTitle(): void{
        this.editTranslatedTitle = !this.editTranslatedTitle;
    };

    //Default init functions provided by Angular Core
    ngAfterViewInit() {
        //Fire functions AFTER the view inited. Useful when DOM is required or access children directives
        this.subscription = this.worksService.notifyObservable$.subscribe(
            (res) => {
                if( res.work != undefined ) {
                    this.editWork = res.work;
                    this.loadWorkTypes();
                    if( this.editWork.workExternalIdentifiers.length == 0 ){
                        this.addExternalIdentifier();
                    }  
                } else {
                    this.worksService.getBlankWork()
                    .pipe(    
                        takeUntil(this.ngUnsubscribe)
                    )
                    .subscribe(
                        data => {
                            this.editWork = data
                        },
                        error => {
                            console.log('Error getting blankwork', error);
                        } 
                    );
                }
            }
        );
        
        this.viewSubscription = this.modalService.notifyObservable$.subscribe(
            (res) => {
                this.bibtexWork = res.bibtexWork;
                if(res.moduleId == "modalWorksForm'") {
                    if(res.action == "open" && res.edit == false) {
                        this.worksService.getBlankWork()
                        .pipe(    
                            takeUntil(this.ngUnsubscribe)
                        )
                        .subscribe(
                            data => {
                                this.editWork = data
                            },
                            error => {
                                console.log('Error getting blankwork', error);
                            } 
                        );
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
        this.togglzDialogPrivacyOption = this.featuresService.isFeatureEnabled('DIALOG_PRIVACY_OPTION')
        this.worksService.getExternalIdTypes('')
        .pipe(    
            takeUntil(this.ngUnsubscribe)
        )
        .subscribe(
            data => {
                this.externalIDTypeCache = data;
                for (var key in data) {
                  this.externalIDNamesToDescriptions[data[key].name] = data[key];
                }
            },
            error => {
                console.log('Error getting external ID types', error);
            } 
        );
    };
}