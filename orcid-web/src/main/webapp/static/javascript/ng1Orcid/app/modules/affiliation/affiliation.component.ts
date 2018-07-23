declare var $: any;
declare var orcidVar: any;
declare var GroupedActivities: any;

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
    from '../../shared/affiliation.service.ts';

import { EmailService } 
    from '../../shared/email.service.ts';

import { ModalService } 
    from '../../shared/modal.service.ts'; 

import { WorkspaceService } 
    from '../../shared/workspace.service.ts'; 

import { FeaturesService }
    from '../../shared/features.service.ts' 
    
import { CommonService } 
    from '../../shared/common.service.ts';
    
@Component({
    selector: 'affiliation-ng2',
    template:  scriptTmpl("affiliation-ng2-template"),
    providers: [CommonService]
})
export class AffiliationComponent implements AfterViewInit, OnDestroy, OnInit {
    @Input() publicView: any;
    private ngUnsubscribe: Subject<void> = new Subject<void>();
    private subscription: Subscription;

    disambiguatedAffiliation: any;
    displayNewAffiliationTypesFeatureEnabled: boolean;
    distinctionsAndInvitedPositions: any;
    editAffiliation: any;
    editSources: any;
    educations: any;
    educationsAndQualifications: any;
    emails: any;
    employments: any;
    membershipsAndServices: any;
    moreInfo: any;
    moreInfoCurKey: any;
    orgIdsFeatureEnabled: boolean;
    privacyHelp: any;
    privacyHelpCurKey: any;
    sectionOneElements: any;
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
        this.sectionOneElements = [];
        this.displayNewAffiliationTypesFeatureEnabled = this.featuresService.isFeatureEnabled('DISPLAY_NEW_AFFILIATION_TYPES');
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
                        this.cdr.detectChanges();
                    },
                    error => {
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
                        this.cdr.detectChanges();
                    },
                    error => {
                        console.log('getAffiliationGroups error', error);
                    } 
            );
        
        }
    };

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
        this.editSources[group.groupId] = false;
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
        // first check if user is current source
        if (affiliation.source == orcidVar.orcidId){
            this.addAffiliationModal(affiliation.affiliationType.value, affiliation);
        } else{
            // in this case we want to open their version
            // if they don't have a version yet then copy
            // the current one
            var bestMatch = null;
            for (var idx in group) {    
                if (group[idx].source == orcidVar.orcidId) {
                    bestMatch = group[idx];
                    break;
                }
            }
            if (bestMatch == null) {
                bestMatch = this.affiliationService.createNew(affiliation);
            }
            this.addAffiliationModal(bestMatch.affiliationType.value, bestMatch);
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

        if(this.displayNewAffiliationTypesFeatureEnabled) {
            this.sectionOneElements = this.educationsAndQualifications;
        } else {
            this.sectionOneElements = this.educations;
        } 
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
        for (var idx in group.researchResources) {
            putCodes.push(group.researchResources[idx].putCode);
            group.affiliations[idx].visibility = priv;
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

    showDetailsMouseClick(group, $event): void {
        $event.stopPropagation();
        this.moreInfo[group.groupId] = !this.moreInfo[group.groupId];
    };


    showSources(group, $event): void {
        $event.stopPropagation();
        this.editSources[group.groupId] = true;
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

    userIsSource(affiliation): boolean {
        if (affiliation.source == orcidVar.orcidId){
            return true;
        }
        return false;
    };


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
        this.getAffiliationGroups();
    };
}