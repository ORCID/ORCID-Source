declare var $: any;
declare var ActSortState: any;
declare var GroupedActivities: any;
declare var groupedActivitiesUtil: any;
declare var openImportWizardUrl: any;
declare var sortState: any;
declare var typeahead: any;

//Import all the angular components
import { NgForOf, NgIf } 
    from '@angular/common'; 

import { AfterViewInit, ChangeDetectorRef, Component, OnDestroy, OnInit, ElementRef, Input, Output } 
    from '@angular/core';

import { Observable, Subject, Subscription } 
    from 'rxjs';

import { takeUntil } 
    from 'rxjs/operators';

import { CommonService } 
    from '../../shared/common.service.ts';

import { EmailService } 
    from '../../shared/email.service.ts';

import { FundingService } 
    from '../../shared/funding.service.ts';

import { ModalService } 
    from '../../shared/modal.service.ts'; 

import { WorkspaceService } 
    from '../../shared/workspace.service.ts'; 

@Component({
    selector: 'funding-ng2',
    template:  scriptTmpl("funding-ng2-template"),
    providers: [CommonService]
})
export class FundingComponent implements AfterViewInit, OnDestroy, OnInit {
    @Input() publicView: any;

    private ngUnsubscribe: Subject<void> = new Subject<void>();
    private subscription: Subscription;
    private viewSubscription: Subscription;
    
    addingFunding: boolean;
    defaultFunding: any;
    deleFunding: any;
    disambiguatedFunding: any;
    displayFundingxtIdPopOver: any;
    displayURLPopOver: any;
    editFunding: any;
    editSources: any;
    educations: any;
    emails: any;
    employments: any;
    fixedTitle: string;
    fundings: any;
    fundingImportWizard: boolean;
    fundingToAddIds: any;
    groups: any;
    moreInfo: any;
    moreInfoCurKey: any;
    privacyHelp: any;
    privacyHelpCurKey: any;
    showElement: any;
    sortHideOption: boolean;
    sortState: any;
    fundingMoreInfo: any;
    wizardDescExpanded: any;

    constructor(
        private elementRef: ElementRef,
        private cdr: ChangeDetectorRef,
        private commonSrvc: CommonService,
        private fundingService: FundingService,
        private emailService: EmailService,
        private modalService: ModalService,
        private workspaceSrvc: WorkspaceService
    ) {

        this.addingFunding = false;
        this.defaultFunding = {
            amount: {
                errors: {},
                value: null
            },
            city: {
                errors: {},
                value: null
            },
            country: {
                errors: {},
                value: null
            },
            currencyCode: {
                errors: {}
            },
            description: {
                errors: {},
                value: null
            },
            endDate: {
                errors: {},
            },
            errors: {},
            fundingName: {
                errors: {},
                value: null
            },
            fundingTitle: {
                title: {
                    errors: {},
                    value: null
                },
                translatedTitle: {
                    errors: {}
                }
            },
            fundingType: {
                errors: {},
                value: null
            },
            organizationDefinedFundingSubType: {
                subtype: {
                    errors: {},
                    value: null
                }
            },
            putCode: {
                value: null
            },
            region: {
                errors: {},
                value: null
            },
            startDate: {
                errors: {},
            },
            url: {
                errors: {},
                value: null
            }
        };
        this.deleFunding = null;
        this.displayURLPopOver = {};
        this.editFunding = {};
        this.editSources = {};
        this.emails = {};
        this.fixedTitle = '';
        this.fundings = new Array();
        this.fundingImportWizard = false;
        this.fundingToAddIds = {};
        this.groups = new Array();
        this.moreInfo = {};
        this.moreInfoCurKey = null;
        this.privacyHelp = {};
        this.privacyHelpCurKey = null;
        this.publicView = elementRef.nativeElement.getAttribute('publicView');
        this.showElement = {};
        this.sortState = new ActSortState(GroupedActivities.FUNDING);
        this.fundingMoreInfo = {};
        this.wizardDescExpanded = {};
    }

    addFunding(): void {
        if (this.addingFunding == true) {
            return; 
        }

        this.addingFunding = true;
        this.editFunding.errors.length = 0;
    };

    addFundingModal(obj?): void {
        this.emailService.getEmails()
        .pipe(    
            takeUntil(this.ngUnsubscribe)
        )
        .subscribe(
            data => {
                if( this.emailService.getEmailPrimary().verified ){

                    if(obj == undefined){
                        this.removeDisambiguatedFunding();
                        this.fundingService.getFundingEmpty().pipe(    
                            takeUntil(this.ngUnsubscribe)
                        )
                        .subscribe(
                            data => {
                                this.editFunding = data;
                                this.fundingService.setFundingToEdit(data);
                                this.modalService.notifyOther({action:'open', moduleId: 'modalFundingForm', edit: false});
                            }
                        );
                    } else {
                        this.fundingService.setFundingToEdit(obj);
                        this.modalService.notifyOther({action:'open', moduleId: 'modalFundingForm', edit: true}); 
                    }

                }else{
                    this.modalService.notifyOther({action:'open', moduleId: 'modalemailunverified'});
                }
            },
            error => {
                //console.log('getEmails', error);
            } 
        );
    };

    closeMoreInfo(key): void {
        this.moreInfo[key]=false;
    };

    deleteFunding(funding): void {
        this.emailService.getEmails()
        .pipe(    
            takeUntil(this.ngUnsubscribe)
        )
        .subscribe(
            data => {
                this.emails = data;
                if( this.emailService.getEmailPrimary().verified ){
                    this.fundingService.notifyOther({funding:funding});
                    this.modalService.notifyOther({action:'open', moduleId: 'modalFundingDelete'});
                }else{
                    this.modalService.notifyOther({action:'open', moduleId: 'modalemailunverified'});
                }
            },
            error => {
                //console.log('getEmails', error);
            } 
        );
    };

    getFundings(): any {
        this.groups = new Array();
        if(this.publicView === "true") {
            this.fundingService.getPublicFundings(this.sortState.predicateKey, !this.sortState.reverseKey[this.sortState.predicateKey]).pipe(    
                takeUntil(this.ngUnsubscribe)
            )
            .subscribe(
                data => {
                   this.groups = data;
                },
                error => {
                    console.log('getFundingsByIdError', error);
                } 
            );
        } else {
            this.fundingService.getFundings(this.sortState.predicateKey, !this.sortState.reverseKey[this.sortState.predicateKey]).pipe(    
                takeUntil(this.ngUnsubscribe)
            )
            .subscribe(
                data => {
                   this.groups = data;
                },
                error => {
                    console.log('getFundingsByIdError', error);
                } 
            );
        }
    }
    
    getFundingMoreInfo(putCode): any {
        this.fundingService.getFullFunding(putCode).pipe(    
            takeUntil(this.ngUnsubscribe)
        )
        .subscribe(
            data => {
               this.fundingMoreInfo = data;
            },
            error => {
                console.log('getFundingsByIdError', error);
            } 
        );
    }
    
    consistentVis(group): boolean {
        var vis = group.fundings[0].visibility.visibility;
        for(let i = 0; i < group.fundings.length; i++) {
            if (group.fundings[i].visibility.visibility != vis) {
                return false;
            }
        }
        return true;
    }

    hideAllTooltip(): void {
        for (var idx in this.showElement){
            this.showElement[idx]=false;
        }
    };

    hideSources(group): void {
        this.editSources[group.groupId] = false;
        group.activePutCode = group.defaultPutCode;
    };


    hideTooltip(element): void{        
        this.showElement[element] = false;
    };

    hideURLPopOver(id): void{
        this.displayURLPopOver[id] = false;
    };

    isValidClass(cur): any {
        let valid = true;

        if (cur === undefined) {
            return '';
        }
        if ( 
            ( cur.required && (cur.value == null || cur.value.trim() == '') ) 
            || 
            ( cur.errors !== undefined && cur.errors.length > 0 ) 
        ) {
            valid = false;
        }

        return valid ? '' : 'text-error';
    };

    isValidStartDate(start): any {
        if (start === undefined) {
            return '';
        }
        
        if (start.errors !== undefined && start.errors.length > 0) {
            return 'text-error';
        }
        
        return '';
    };

    makeDefault(group, putCode): any {
        this.fundingService.updateToMaxDisplay(group, putCode)
        .pipe(    
            takeUntil(this.ngUnsubscribe)
        )
        .subscribe(
            data => {
                group.activePutCode = putCode;   
                group.defaultPutCode = putCode;
            },
            error => {
                console.log('makeDefault', error);
            } 
        );
    }

    moreInfoMouseEnter(key, $event): void {
        $event.stopPropagation();
        if ( document.documentElement.className.indexOf('no-touch') > -1 ) {
            if (this.moreInfoCurKey != null
                && this.moreInfoCurKey != key) {
                this.privacyHelp[this.moreInfoCurKey]=false;
            }
            this.moreInfoCurKey = key;
            this.moreInfo[key]=true;
        }
    };

    openEditFunding( putCode ): void {
        let data = this.fundingService.getEditable(putCode, this.groups)
        this.addFundingModal(data);
    }

    openImportWizardUrl(url): void {
        openImportWizardUrl(url);
    };

    removeDisambiguatedFunding(): void {
        if (this.disambiguatedFunding != undefined) {
            delete this.disambiguatedFunding;
        }
        if (this.editFunding != undefined && this.editFunding.disambiguatedFundingSourceId != undefined) {
            delete this.editFunding.disambiguatedFundingSourceId;
        }
    }

    setGroupPrivacy = function(group, priv, $event): void {
        $event.preventDefault();
        var putCodes = new Array();
        for (var idx in group.activities) {
            putCodes.push(group.activities[idx].putCode.value);
            group.activities[idx].visibility.visibility = priv;
        }
        this.fundingService.updateVisibility(putCodes, priv)
        .pipe(    
            takeUntil(this.ngUnsubscribe)
        )
        .subscribe(
            data => {
                if (putCodes.length > 0) {
                    this.fundingService.updateVisibility(putCodes, priv);   
                }
                
            },
            error => {
                console.log('Error updating group visibility', error);
            } 
        );
    }

    setPrivacy( obj, priv, $event ): void {

        $event.preventDefault();
        obj.visibility.visibility = priv;

        if (this.addingFunding){    
            return; // don't process if adding funding
        } 
        this.addingFunding = true;
        this.fundingService.putFunding( obj )
        .pipe(    
            takeUntil(this.ngUnsubscribe)
        )
        .subscribe(
            data => {
                this.addingFunding = false;
                this.getFundings();
            },
            error => {
                console.log('setFundingPrivacyError', error);
            } 
        );
    };

    showDetailsMouseClick = function(group, $event) {
        var showing = this.moreInfo[group.groupId];
        this.moreInfo = {}; 
        this.fundingMoreInfo = {};
        if (!showing) {
            this.getFundingMoreInfo(group.activePutCode);
            $event.stopPropagation();
            this.moreInfo[group.groupId] = true;
        }
    };

    showFundingImportWizard(): void {
        this.fundingImportWizard = !this.fundingImportWizard;               
    };

    showSources(group,$event): void {
        $event.stopPropagation();
        this.editSources[group.groupId] = true;
        this.hideAllTooltip();
    };

    showTooltip(element): void{        
        this.showElement[element] = true;
    };

    sort(key): void {       
        this.sortState.sortBy(key);
        this.getFundings();
    };

    showURLPopOver(id): void {
        this.displayURLPopOver[id] = true;
    };

    // remove once grouping is live
    toggleClickMoreInfo(key): void {
        if ( document.documentElement.className.indexOf('no-touch') == -1 ) {
            if (this.moreInfoCurKey != null
                    && this.moreInfoCurKey != key) {
                this.moreInfo[this.moreInfoCurKey]=false;
            }
            this.moreInfoCurKey = key;
            this.moreInfo[key]=!this.moreInfo[key];
        }
    };

    toggleClickPrivacyHelp(key): void {
        if ( document.documentElement.className.indexOf('no-touch') == -1 ) {
            if (
                this.privacyHelpCurKey != null
                && this.privacyHelpCurKey != key) {
                this.privacyHelp[this.privacyHelpCurKey]=false;
            }
            this.privacyHelpCurKey = key;
            this.privacyHelp[key]=!this.privacyHelp[key];
        }

    };

    toggleEdit(): void {
        this.emailService.getEmails()
        .pipe(    
            takeUntil(this.ngUnsubscribe)
        )
        .subscribe(
            data => {
                this.emails = data;
                if( this.emailService.getEmailPrimary().verified ){
                    //this.showEdit = !this.showEdit;
                }else{
                    this.modalService.notifyOther({action:'open', moduleId: 'modalemailunverified'});
                }
            },
            error => {
                //console.log('getEmails', error);
            } 
        );
    };

    toggleWizardDesc(id): void{
        this.wizardDescExpanded[id] = !this.wizardDescExpanded[id];
    };

    userIsSource(funding): boolean {
        if (funding.source == orcidVar.orcidId){
            return true;
        }
        return false;
    };

    //Default init functions provided by Angular Core
    ngAfterViewInit() {
        //Fire functions AFTER the view inited. Useful when DOM is required or access children directives

        this.subscription = this.fundingService.notifyObservable$.subscribe(
            (res) => {                
                if(res.action == 'add' || res.action == 'cancel' || res.action == 'delete') {
                    if(res.successful == true) {
                        this.getFundings();
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
        //console.log('init funding component');
        this.getFundings();
        
        this.viewSubscription = this.modalService.notifyObservable$.subscribe(
            (res) => {

                if(res.moduleId == "modalFundingForm") {
                    if(res.action == "close") {
                        this.editFunding = this.getFundings();
                    }
                }
            }
        );

    }; 
}