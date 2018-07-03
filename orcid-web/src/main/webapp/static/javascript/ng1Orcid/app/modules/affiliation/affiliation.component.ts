declare var $: any;
declare var GroupedActivities: any;
declare var groupedActivitiesUtil: any;
declare var scriptTmpl: any;
declare var typeahead: any;

//Import all the angular components
import { NgForOf, NgIf } 
    from '@angular/common'; 

import { AfterViewInit, Component, OnDestroy, OnInit, ElementRef, Input, Output } 
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

    /*
    emailSrvc: any;
    workspaceSrvc: any;
    */
    addingAffiliation: boolean;
    deleAff: any;
    disambiguatedAffiliation: any;
    displayNewAffiliationTypesFeatureEnabled: boolean;
    distinctionsAndInvitedPositions: any;
    editAffiliation: any;
    educations: any;
    educationsAndQualifications: any;
    emails: any;
    employments: any;
    fixedTitle: string;
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
    sortHideOption: boolean;
    sortKeyDistinctions: any;
    sortKeyEducations: any;
    sortKeyEmployments: any;
    sortKeyMemberships: any;

    constructor(
        private affiliationService: AffiliationService,
        private emailService: EmailService,
        private modalService: ModalService,
        private workspaceSrvc: WorkspaceService,
        private featuresService: FeaturesService,
        private commonSrvc: CommonService,
        private elementRef: ElementRef
    ) {
        /*
        this.emailSrvc = emailSrvc;
        this.workspaceSrvc = workspaceSrvc;
        */
        this.addingAffiliation = false;
        this.deleAff = null;
        this.disambiguatedAffiliation = null;
        this.editAffiliation = {};
        this.educations = [];
        this.emails = {};
        this.employments = [];
        this.fixedTitle = '';
        this.moreInfo = {};
        this.moreInfoCurKey = null;
        this.privacyHelp = {};
        this.privacyHelpCurKey = null;
        this.showElement = {};
        this.sortHideOption = false;
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
                //console.log('open add aff modal');
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

    getAffiliationsById( affiliationIds ): void {
        this.affiliationService.getAffiliationsById( affiliationIds ).pipe(    
            takeUntil(this.ngUnsubscribe)
        )
            .subscribe(
                data => {
                    this.parseAffiliations(data);
                },
                error => {
                    console.log('getAffiliationsById error', error);
                } 
        );
    };

    getAffiliationsId(): void {
        //Be sure all arrays are empty
        this.distinctionsAndInvitedPositions.length = 0;
        this.educationsAndQualifications.length = 0;
        this.educations.length = 0;
        this.employments.length = 0;
        this.membershipsAndServices.length = 0; 
        
        if(this.publicView === "true") {
            this.getPublicAffiliationsById( orcidVar.affiliationIdsJson );
        } else {
            this.affiliationService.getAffiliationsId()
            .pipe(    
            takeUntil(this.ngUnsubscribe)
        )
            .subscribe(
                data => {
                    if( data.length != 0 ) {
                        let affiliationIds = data.splice(0,20).join();
                        this.getAffiliationsById( affiliationIds );
                    }
                },
                error => {
                    console.log('getAffiliationsId', error);
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

    getPublicAffiliationsById( affiliationIds ): void {
        this.affiliationService.getPublicAffiliationsById( affiliationIds ).pipe(    
            takeUntil(this.ngUnsubscribe)
        )
            .subscribe(
                data => {
                    this.parseAffiliations(data);
                },
                error => {
                    console.log('getPublicAffiliationsById error', error);
                } 
        );
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

    openEditAffiliation(affiliation): void {
        this.addAffiliationModal(affiliation.affiliationType.value, affiliation);
    };

    parseAffiliations( data ): void {
        for (let i in data) {
            //console.log(JSON.stringify(data[i]));
            if (data[i].affiliationType != null 
                && data[i].affiliationType.value != null) {                            
                if(data[i].affiliationType.value == 'distinction') {
                    groupedActivitiesUtil.group(data[i],GroupedActivities.AFFILIATION,this.distinctionsAndInvitedPositions);
                } else if(data[i].affiliationType.value == 'education'){
                    groupedActivitiesUtil.group(data[i],GroupedActivities.AFFILIATION,this.educations);
                    groupedActivitiesUtil.group(data[i],GroupedActivities.AFFILIATION,this.educationsAndQualifications);
                } else if ( data[i].affiliationType.value == 'employment' ) {
                    groupedActivitiesUtil.group(data[i],GroupedActivities.AFFILIATION,this.employments);
                } else if(data[i].affiliationType.value == 'invited-position') {
                    groupedActivitiesUtil.group(data[i],GroupedActivities.AFFILIATION,this.distinctionsAndInvitedPositions);
                } else if(data[i].affiliationType.value == 'membership') {
                    groupedActivitiesUtil.group(data[i],GroupedActivities.AFFILIATION,this.membershipsAndServices);
                } else if (data[i].affiliationType.value == 'qualification') {
                    groupedActivitiesUtil.group(data[i],GroupedActivities.AFFILIATION,this.educationsAndQualifications);                            
                } else if(data[i].affiliationType.value == 'service') {
                    groupedActivitiesUtil.group(data[i],GroupedActivities.AFFILIATION,this.membershipsAndServices);   
                }
                console.log('groupedActivitiesUtil affiliation', groupedActivitiesUtil);
            }
        };
        
        if(this.displayNewAffiliationTypesFeatureEnabled) {
            this.sectionOneElements = this.educationsAndQualifications;
        } else {
            this.sectionOneElements = this.educations;
        } 
    };
       
    removeFromArray(affArray, putCode): void {
        for(let idx in affArray) {
            if(affArray[idx].activePutCode == putCode) {
                affArray.splice(idx, 1);
                break;
            }
        }
    };

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

    setPrivacy(aff, priv, $event): void {
        $event.preventDefault();
        aff.visibility.visibility = priv;                
        this.affiliationService.updateVisibility(aff)
            .pipe(    
            takeUntil(this.ngUnsubscribe)
        )
            .subscribe(data => {});
    };


    showDetailsMouseClick = function(group, $event) {
        $event.stopPropagation();
        this.moreInfo[group.groupId] = !this.moreInfo[group.groupId];
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
                console.log("sort asc: " + this.sortAscEmployments);
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
    

    //Default init functions provided by Angular Core
    ngAfterViewInit() {
        //Fire functions AFTER the view inited. Useful when DOM is required or access children directives

        this.subscription = this.affiliationService.notifyObservable$.subscribe(
            (res) => {                
                if(res.action == 'delete') {
                    if(res.deleteAffiliationObj.affiliationType != null && res.deleteAffiliationObj.affiliationType.value != null) {
                        if(res.deleteAffiliationObj.affiliationType.value == 'distinction' || res.deleteAffiliationObj.affiliationType.value == 'invited-position') {
                            this.removeFromArray(this.distinctionsAndInvitedPositions, res.deleteAffiliationObj.putCode.value);
                        } else if (res.deleteAffiliationObj.affiliationType.value == 'education' || res.deleteAffiliationObj.affiliationType.value == 'qualification'){
                            this.removeFromArray(this.educationsAndQualifications, res.deleteAffiliationObj.putCode.value);
                            if(res.deleteAffiliationObj.affiliationType.value == 'education') {
                                this.removeFromArray(this.educations, res.deleteAffiliationObj.putCode.value);
                            }                            
                        } else if (res.deleteAffiliationObj.affiliationType.value == 'employment'){
                            this.removeFromArray(this.employments, res.deleteAffiliationObj.putCode.value);                            
                        } else if(res.deleteAffiliationObj.affiliationType.value == 'membership' || res.deleteAffiliationObj.affiliationType.value == 'service') {
                            this.removeFromArray(this.membershipsAndServices, res.deleteAffiliationObj.putCode.value);                            
                        } 
                    }  
                } else if(res.action == 'add') {
                    if(res.successful == true) {
                        console.log("Fetching affiliations data");
                        this.getAffiliationsId();
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
        this.getAffiliationsId();
;    }; 
}