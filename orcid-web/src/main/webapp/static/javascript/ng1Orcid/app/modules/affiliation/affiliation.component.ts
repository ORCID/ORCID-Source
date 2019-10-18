declare var $: any;
declare var orcidVar: any;

//Import all the angular components
import { NgForOf, NgIf } 
    from '@angular/common'; 

import { AfterViewInit, ChangeDetectorRef, Component, OnDestroy, OnInit, ElementRef, Input, Output } 
    from '@angular/core';

import { Observable, Subject, Subscription } 
    from 'rxjs';
import { takeUntil } 
    from 'rxjs/operators';

import { AffiliationService } 
    from '../../shared/affiliation.service';

import { EmailService } 
    from '../../shared/email.service';

import { ModalService } 
    from '../../shared/modal.service'; 

import { WorkspaceService } 
    from '../../shared/workspace.service'; 

import { FeaturesService }
    from '../../shared/features.service' 
    
import { CommonService } 
    from '../../shared/common.service';
    
@Component({
    selector: 'affiliation-ng2',
    template:  scriptTmpl("affiliation-ng2-template")
})
export class AffiliationComponent implements AfterViewInit, OnDestroy, OnInit {
    @Input() publicView: any;
    private ngUnsubscribe: Subject<void> = new Subject<void>();
    private subscription: Subscription;

    disambiguatedAffiliation: any;
    distinctionsAndInvitedPositions: any;
    editAffiliation: any;
    editSources: any;
    educations: any;
    educationsAndQualifications: any;
    emails: any;
    employments: any;
    isPublicPage: boolean;
    membershipsAndServices: any;
    moreInfo: any;
    moreInfoCurKey: any;
    orgIdsFeatureEnabled: boolean;
    privacyHelp: any;
    privacyHelpCurKey: any;
    showElement: any;
    sortAscDistinctions: boolean;
    sortAscEducations: boolean;
    sortAscEmployments: boolean;
    sortAscMemberships: boolean;
    sortDisplayKeyDistinctions: any;
    sortDisplayKeyEducations: any;
    sortDisplayKeyEmployments: any;
    sortDisplayKeyMemberships: any;
    sortKeyDistinctions: any;
    sortKeyEducations: any;
    sortKeyEmployments: any;
    sortKeyMemberships: any;
    recordLocked: boolean;

    constructor(
        private affiliationService: AffiliationService,
        private cdr: ChangeDetectorRef,
        private emailService: EmailService,
        private modalService: ModalService,
        private workspaceSrvc: WorkspaceService,
        private featuresService: FeaturesService,
        private commonSrvc: CommonService,
        private elementRef: ElementRef
    ) {
        this.disambiguatedAffiliation = null;
        this.editAffiliation = {};
        this.editSources = {};
        this.educations = [];
        this.emails = {};
        this.employments = [];
        this.isPublicPage = this.commonSrvc.isPublicPage;
        this.moreInfo = {};
        this.moreInfoCurKey = null;
        this.privacyHelp = {};
        this.privacyHelpCurKey = null;
        this.showElement = {};
        this.sortAscDistinctions = false;
        this.sortDisplayKeyDistinctions = 'endDate';
        this.sortKeyDistinctions = ['endDate', 'title'];
        this.sortAscEducations = false;
        this.sortDisplayKeyEducations = 'endDate';
        this.sortKeyEducations = ['endDate', 'title'];
        this.sortAscEmployments = false;
        this.sortDisplayKeyEmployments = 'endDate';
        this.sortKeyEmployments = ['endDate', 'title'];
        this.sortAscMemberships = false;
        this.sortDisplayKeyMemberships = 'endDate';
        this.sortKeyMemberships = ['endDate', 'title'];
        this.educationsAndQualifications = [];
        this.distinctionsAndInvitedPositions = [];
        this.membershipsAndServices = [];
        this.orgIdsFeatureEnabled = this.featuresService.isFeatureEnabled('AFFILIATION_ORG_ID');
        this.publicView = elementRef.nativeElement.getAttribute('publicView');
    }

    addAffiliationModal(type, affiliation): void {
        this.emailService.getEmails()
        .pipe(    
            takeUntil(this.ngUnsubscribe)
        )
        .subscribe(
            data => {
                this.emails = data;
                if( this.emailService.getEmailPrimary().verified ){
                    this.affiliationService.notifyOther({ affiliation:affiliation, type: type });
                    if(affiliation == undefined) {
                        this.modalService.notifyOther({action:'open', moduleId: 'modalAffiliationForm', edit: false});
                    } else {
                        this.modalService.notifyOther({action:'open', moduleId: 'modalAffiliationForm', edit: true});
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

    deleteAffiliation(affiliation): void {
        this.emailService.getEmails()
        .pipe(    
            takeUntil(this.ngUnsubscribe)
        )
        .subscribe(
            data => {
                this.emails = data;
                if( this.emailService.getEmailPrimary().verified ){
                    this.affiliationService.notifyOther({affiliation:affiliation});
                    this.modalService.notifyOther({action:'open', moduleId: 'modalAffiliationDelete'});
                }else{
                    this.modalService.notifyOther({action:'open', moduleId: 'modalemailunverified'});
                }
            },
            error => {
                //console.log('getEmails', error);
            } 
        );
    };

    displayEducation(): boolean {
        return this.workspaceSrvc.displayEducation;
    };
    
    displayEducationAndQualification(): boolean {
        return this.workspaceSrvc.displayEducationAndQualification;
    };

    getAffiliationGroups(): void {
        if(this.publicView === "true") {
            this.affiliationService.getPublicAffiliationGroups().pipe(    
            takeUntil(this.ngUnsubscribe)
            )
                .subscribe(
                    data => {
                        this.parseAffiliationGroups(data);
                        this.affiliationService.loading = false; 
                        this.cdr.detectChanges();
                    },
                    error => {
                        this.affiliationService.loading = false; 
                        console.log('getPublicAffiliationGroups error', error);
                    } 
            );
        } else {
            this.affiliationService.getAffiliationGroups().pipe(    
            takeUntil(this.ngUnsubscribe)
            )
                .subscribe(
                    data => {
                        this.parseAffiliationGroups(data);
                        this.affiliationService.loading = false; 
                        this.cdr.detectChanges();
                    },
                    error => {
                        this.affiliationService.loading = false; 
                        console.log('getAffiliationGroups error', error);
                    } 
            );
        
        }
    };

    getDetails(putCode, type): void {
        if(this.publicView === "true"){
            this.affiliationService.getPublicAffiliationDetails(putCode, type)
            .pipe(    
                takeUntil(this.ngUnsubscribe)
            )
            .subscribe(
                data => {
                    this.affiliationService.loading = false;
                    this.affiliationService.details[putCode] = data;  
                },
                error => {
                    this.affiliationService.loading = false;
                    console.log('getDetailsError', error);
                } 
            );
        } else {
            this.affiliationService.getAffiliationDetails(putCode, type)
            .pipe(    
                takeUntil(this.ngUnsubscribe)
            )
            .subscribe(
                data => {
                    this.affiliationService.loading = false;
                    this.affiliationService.details[putCode] = data;  
                },
                error => {
                    this.affiliationService.loading = false;
                    console.log('getDetailsError', error);
                } 
            );
        }
    }

    getDisambiguatedAffiliation = function(id) {
        this.affiliationService.getDisambiguatedAffiliation(id)
        .pipe(    
            takeUntil(this.ngUnsubscribe)
        )
        .subscribe(
            data => {
                if (data != null) {
                    this.disambiguatedAffiliation = data;
                    this.editAffiliation.orgDisambiguatedId.value = id;
                    this.editAffiliation.disambiguatedAffiliationSourceId = data.sourceId;
                    this.editAffiliation.disambiguationSource = data.sourceType;
                }
            },
            error => {
                console.log("getAffiliationsId", id, error);
            } 
        );
    };

    hideAllTooltip(): void {
        for (var idx in this.showElement){
            this.showElement[idx]=false;
        }
    };

    hideSources(group): void {
        this.editSources[group.activePutCode] = false;
    };

    hideTooltip(element): void{        
        this.showElement[element] = false;
    };

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

    openEditAffiliation(affiliation, group): void {
        var bestMatchPutCode = null;
        //check for user source version
        if(affiliation.source == orcidVar.orcidId){
            bestMatchPutCode = affiliation.putCode.value;
        } else {
            for (var idx in group.affiliations) {    
                if (group.affiliations[idx].source == orcidVar.orcidId) {
                    bestMatchPutCode = group.affiliations[idx].putCode.value;
                    break;
                }
            }
        }
        if(bestMatchPutCode != null){
            if(this.affiliationService.details[bestMatchPutCode] == undefined){
                this.affiliationService.getAffiliationDetails(bestMatchPutCode, affiliation.affiliationType.value)
                .pipe(    
                    takeUntil(this.ngUnsubscribe)
                )
                .subscribe(
                    data => {
                            this.addAffiliationModal(data.affiliationType.value, data);
                    },
                    error => {
                        console.log('openEditAffiliationError', error);
                    } 
                );
            } else {
                this.addAffiliationModal(affiliation.affiliationType.value, this.affiliationService.details[bestMatchPutCode]);
            }
        } else {
            //otherwise make a copy 
            if(this.affiliationService.details[affiliation.putCode.value] == undefined){
                this.affiliationService.getAffiliationDetails(affiliation.putCode.value, affiliation.affiliationType.value)
                    .pipe(    
                        takeUntil(this.ngUnsubscribe)
                    )
                    .subscribe(
                        data => {
                                this.addAffiliationModal(affiliation.affiliationType.value, this.affiliationService.createNew(data));
                        },
                        error => {
                            console.log('openEditAffiliationError', error);
                        } 
                );
            } else {
                this.addAffiliationModal(affiliation.affiliationType.value,  this.affiliationService.createNew(this.affiliationService.details[affiliation.putCode.value]));
            }
        }
    };

    parseAffiliationGroups(data): void {
        this.distinctionsAndInvitedPositions = new Array();
        this.educations = new Array();
        this.educationsAndQualifications = new Array();
        this.employments = new Array();
        this.membershipsAndServices = new Array();
 
        this.distinctionsAndInvitedPositions = this.distinctionsAndInvitedPositions.concat(data.affiliationGroups.DISTINCTION);
        this.distinctionsAndInvitedPositions = this.distinctionsAndInvitedPositions.concat(data.affiliationGroups.INVITED_POSITION);
        
        this.educations = this.educations.concat(data.affiliationGroups.EDUCATION);
        
        this.educationsAndQualifications = this.educationsAndQualifications.concat(data.affiliationGroups.EDUCATION);
        this.educationsAndQualifications = this.educationsAndQualifications.concat(data.affiliationGroups.QUALIFICATION);

        this.employments = this.employments.concat(data.affiliationGroups.EMPLOYMENT);
        
        this.membershipsAndServices = this.membershipsAndServices.concat(data.affiliationGroups.MEMBERSHIP);
        this.membershipsAndServices = this.membershipsAndServices.concat(data.affiliationGroups.SERVICE);
    }

    selectAffiliation(datum): void {
        if (datum != undefined && datum != null) {
            this.editAffiliation.affiliationName.value = datum.value;
            this.editAffiliation.city.value = datum.city;
            
            if(datum.city) {
                this.editAffiliation.city.errors = [];
            }

            this.editAffiliation.region.value = datum.region;
            
            if(datum.region){
                this.editAffiliation.region.errors = [];
            }
            
            if(datum.country != undefined && datum.country != null) {
                this.editAffiliation.country.value = datum.country;
                this.editAffiliation.country.errors = [];
            }

            if (datum.disambiguatedAffiliationIdentifier != undefined && datum.disambiguatedAffiliationIdentifier != null) {
                this.getDisambiguatedAffiliation(datum.disambiguatedAffiliationIdentifier);
            }
        }
    };

    setAddAffiliationPrivacy(priv, $event): void {
        $event.preventDefault();
        this.editAffiliation.visibility.visibility = priv;
    };

    setGroupPrivacy = function(group, priv, $event): void {
        $event.preventDefault();
        var putCodes = new Array();
        for (var idx in group.affiliations) {
            putCodes.push(group.affiliations[idx].putCode.value);
            group.affiliations[idx].visibility.visibility = priv;
        }
        group.activeVisibility = priv;
        this.affiliationService.updateVisibility(putCodes, priv)
        .pipe(    
            takeUntil(this.ngUnsubscribe)
        )
        .subscribe(
            data => {
                if (putCodes.length > 0) {
                    this.affiliationService.updateVisibility(putCodes, priv);   
                }
                
            },
            error => {
                console.log('Error updating group visibility', error);
            } 
        );
    }

    showDetailsMouseClick(group,$event): void {
        $event.stopPropagation();
        this.moreInfo[group.activePutCode] = !this.moreInfo[group.activePutCode];
        if(this.moreInfo[group.activePutCode] == true){
            for (var idx in group.affiliations){
                $($event.target).next().css('display','inline');
                if(this.affiliationService.details[group.affiliations[idx].putCode.value] == undefined){
                    this.getDetails(group.affiliations[idx].putCode.value, group.affiliations[idx].affiliationType.value);
                }
            }
        }
    };


    showSources(group, $event): void {
        $event.stopPropagation();        
        this.editSources[group.activePutCode] = true;
        this.hideAllTooltip();
    };

    showTooltip(element): void{        
        this.showElement[element] = true;
    };

    sort(type, displayKey, reverse): void {
        var sortKey;

        switch(displayKey) {
            case 'endDate':
                sortKey = ['endDate', 'title'];
                break;
            case 'startDate':
                sortKey = ['startDate', 'title'];
                break;
            case 'title':
                sortKey = ['title', 'endDate'];
                break;
        }
        
        switch (type) {
            case 'distinction_invited_position':
                if (this.sortDisplayKeyDistinctions == displayKey) {
                    this.sortAscDistinctions = !this.sortAscDistinctions;
                } else {
                    this.sortAscDistinctions = reverse;
                }
                this.sortKeyDistinctions = sortKey;
                this.sortDisplayKeyDistinctions = displayKey;
                break;
            case 'education':
                if (this.sortDisplayKeyEducations == displayKey) {
                    this.sortAscEducations = !this.sortAscEducations;
                } else {
                    this.sortAscEducations = reverse;
                }
                this.sortKeyEducations = sortKey;
                this.sortDisplayKeyEducations = displayKey;
                break;
            case 'employment':
                if (this.sortDisplayKeyEmployments == displayKey) {
                    this.sortAscEmployments = !this.sortAscEmployments;
                } else {
                    this.sortAscEmployments = reverse;
                }
                this.sortKeyEmployments = sortKey;
                this.sortDisplayKeyEmployments = displayKey;
                break;
            case 'membership_service':
                if (this.sortDisplayKeyMemberships == displayKey) {
                    this.sortAscMemberships = !this.sortAscMemberships;
                } else {
                    this.sortAscMemberships = reverse;
                }
                this.sortKeyMemberships = sortKey;
                this.sortDisplayKeyMemberships = displayKey;
                break;
        }  
    };

    swapSources(group, putCode): void{
        group.activePutCode = putCode;
        this.editSources[group.activePutCode] = true;
    };

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

    userIsSource(affiliation): boolean {
        if (affiliation.source == orcidVar.orcidId){
            return true;
        }
        return false;
    };

    makeDefault(group, affiliation, putCode): any {
        this.affiliationService.updateToMaxDisplay(putCode)
        .pipe(    
            takeUntil(this.ngUnsubscribe)
        )
        .subscribe(
            data => {                
                group.defaultAffiliation = affiliation;                
                group.activePutCode = group.defaultAffiliation.putCode.value;                 
            },
            error => {
                console.log('makeDefault', error);
            } 
        );
    }    
    
    //Default init functions provided by Angular Core
    ngAfterViewInit() {
        //Fire functions AFTER the view inited. Useful when DOM is required or access children directives

        this.subscription = this.affiliationService.notifyObservable$.subscribe(
            (res) => {                
                if(res.action == 'add' || res.action == 'cancel' || res.action == 'delete') {
                    if(res.successful == true) {
                        this.getAffiliationGroups();
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
        if(this.isPublicPage) {
            this.commonSrvc.publicUserInfo$
            .subscribe(
                userInfo => {
                    this.recordLocked = !userInfo || userInfo.IS_LOCKED === 'true' || userInfo.IS_DEACTIVATED === 'true';
                    if (!this.recordLocked) {
                        this.getAffiliationGroups();
                    }
                },
                error => {
                    console.log('affiliation.component.ts: unable to fetch publicUserInfo', error);                    
                } 
            );
        } else {
            this.getAffiliationGroups();
        }
    };
}