declare var $: any;
declare var ActSortState: any;
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

import { ResearchResourceService } 
    from '../../shared/researchResource.service.ts';

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
    selector: 'research-resource-ng2',
    template:  scriptTmpl("research-resource-ng2-template"),
    providers: [CommonService]
})
export class ResearchResourceComponent implements AfterViewInit, OnDestroy, OnInit {
    @Input() publicView: any;
    private ngUnsubscribe: Subject<void> = new Subject<void>();
    private subscription: Subscription;

    disambiguatedResearchResource: any;
    displayNewResearchResourceTypesFeatureEnabled: boolean;
    distinctionsAndInvitedPositions: any;
    editResearchResource: any;
    editSources: any;
    educations: any;
    educationsAndQualifications: any;
    emails: any;
    employments: any;
    membershipsAndServices: any;
    moreInfo: any;
    moreInfoOpen: boolean;
    moreInfoCurKey: any;
    orgIdsFeatureEnabled: boolean;
    privacyHelp: any;
    privacyHelpCurKey: any;
    sectionOneElements: any;
    showElement: any;
    sortState: any;

    constructor(
        private researchResourceService: ResearchResourceService,
        private cdr: ChangeDetectorRef,
        private emailService: EmailService,
        private modalService: ModalService,
        private workspaceSrvc: WorkspaceService,
        private featuresService: FeaturesService,
        private commonSrvc: CommonService,
        private elementRef: ElementRef
    ) {
        this.disambiguatedResearchResource = null;
        this.editResearchResource = {};
        this.editSources = {};
        this.educations = [];
        this.emails = {};
        this.employments = [];
        this.moreInfo = {};
        this.moreInfoCurKey = null;
        this.moreInfoOpen = false;
        this.privacyHelp = {};
        this.privacyHelpCurKey = null;
        this.showElement = {};
        this.sortState = this.sortState = new ActSortState(GroupedActivities.ABBR_WORK);
        /*this.sortAsc = false;
        this.sortDisplayKey = 'endDate';
        this.sortKey = ['endDate', 'title'];*/
        this.publicView = elementRef.nativeElement.getAttribute('publicView');
    }

    addResearchResourceModal(type, researchResource): void {
        this.emailService.getEmails()
        .pipe(    
            takeUntil(this.ngUnsubscribe)
        )
        .subscribe(
            data => {
                this.emails = data;
                if( this.emailService.getEmailPrimary().verified ){
                    this.researchResourceService.notifyOther({ researchResource:researchResource, type: type });
                    if(researchResource == undefined) {
                        this.modalService.notifyOther({action:'open', moduleId: 'modalResearchResourceForm', edit: false});
                    } else {
                        this.modalService.notifyOther({action:'open', moduleId: 'modalResearchResourceForm', edit: true});
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

    closePopover(event): void {
        this.moreInfoOpen = false;
        $('.work-more-info-container').css('display', 'none');
    };


    deleteResearchResource(researchResource): void {
        this.emailService.getEmails()
        .pipe(    
            takeUntil(this.ngUnsubscribe)
        )
        .subscribe(
            data => {
                this.emails = data;
                if( this.emailService.getEmailPrimary().verified ){
                    this.researchResourceService.notifyOther({researchResource:researchResource});
                    this.modalService.notifyOther({action:'open', moduleId: 'modalResearchResourceDelete'});
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

    getResearchResourceGroups(): void {
        if(this.publicView === "true") {
            this.researchResourceService.getPublicResearchResourcePage(this.sortState.predicateKey, 
                !this.sortState.reverseKey[this.sortState.predicateKey]).pipe(    
            takeUntil(this.ngUnsubscribe)
            )
                .subscribe(
                    data => {
                        console.log(data);
                        this.researchResourceService.handleGroupData(data);
                        this.cdr.detectChanges();
                    },
                    error => {
                        console.log('getPublicResearchPageError', error);
                    } 
            );
        } else {
            this.researchResourceService.getResearchResourcePage(this.sortState.predicateKey, 
                !this.sortState.reverseKey[this.sortState.predicateKey]).pipe(    
            takeUntil(this.ngUnsubscribe)
            )
                .subscribe(
                    data => {
                        console.log(data);
                        this.researchResourceService.handleGroupData(data);
                        this.cdr.detectChanges();
                    },
                    error => {
                        console.log('getResearchResourceGroups error', error);
                    } 
            );
        
        }
    };

    getDisambiguatedResearchResource = function(id) {
        this.researchResourceService.getDisambiguatedResearchResource(id)
        .pipe(    
            takeUntil(this.ngUnsubscribe)
        )
        .subscribe(
            data => {
                if (data != null) {
                    this.disambiguatedResearchResource = data;
                    this.editResearchResource.orgDisambiguatedId.value = id;
                    this.editResearchResource.disambiguatedResearchResourceSourceId = data.sourceId;
                    this.editResearchResource.disambiguationSource = data.sourceType;
                }
            },
            error => {
                console.log("getResearchResourcesId", id, error);
            } 
        );
    };

    getDetails(putCode): void {
        if(this.publicView === "true"){
            this.researchResourceService.getPublicResearchResourceById(putCode)
            .pipe(    
                takeUntil(this.ngUnsubscribe)
            )
            .subscribe(
                data => {
                    //this.researchResourceService.removeBadExternalIdentifiers(data);
                    this.researchResourceService.details[putCode] = data;
                    console.log(putCode);
                    console.log(this.researchResourceService.details[putCode]);
                },
                error => {
                    console.log('getDetailsError', error);
                } 
            );
        } else {
            this.researchResourceService.getResearchResourceById(putCode)
            .pipe(    
                takeUntil(this.ngUnsubscribe)
            )
            .subscribe(
                data => {
                    //this.researchResourceService.removeBadExternalIdentifiers(data);
                    this.researchResourceService.details[putCode] = data;
                    console.log(this.researchResourceService.details[putCode]);
                },
                error => {
                    console.log('getDetailsError', error);
                } 
            );
        }
    }

    hideSources(group): void {
        this.editSources[group.groupId] = false;
    };

    hideTooltip(element): void{        
        this.showElement[element] = false;
    };

    loadDetails(putCode, event): void {
        this.closePopover(event);
        this.moreInfoOpen = true;
        $(event.target).next().css('display','inline');
        if(this.researchResourceService.details[putCode] == undefined){
            this.getDetails(putCode);
        }
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

    openEditResearchResource(researchResource): void {
        this.addResearchResourceModal(researchResource.researchResourceType.value, researchResource);
    };

    selectResearchResource(datum): void {
        if (datum != undefined && datum != null) {
            this.editResearchResource.researchResourceName.value = datum.value;
            this.editResearchResource.city.value = datum.city;
            
            if(datum.city) {
                this.editResearchResource.city.errors = [];
            }

            this.editResearchResource.region.value = datum.region;
            
            if(datum.region){
                this.editResearchResource.region.errors = [];
            }
            
            if(datum.country != undefined && datum.country != null) {
                this.editResearchResource.country.value = datum.country;
                this.editResearchResource.country.errors = [];
            }

            if (datum.disambiguatedResearchResourceIdentifier != undefined && datum.disambiguatedResearchResourceIdentifier != null) {
                this.getDisambiguatedResearchResource(datum.disambiguatedResearchResourceIdentifier);
            }
        }
    };

    setAddResearchResourcePrivacy(priv, $event): void {
        $event.preventDefault();
        this.editResearchResource.visibility.visibility = priv;
    };

    setPrivacy(obj, priv, $event): void {
        $event.preventDefault();
        obj.visibility.visibility = priv;                
        this.researchResourceService.updateVisibility(obj.putCode, priv)
            .pipe(    
            takeUntil(this.ngUnsubscribe)
        )
            .subscribe(data => {});
    };

    showDetailsMouseClick = function(group, $event) {
        $event.stopPropagation();
        this.moreInfo[group.groupId] = !this.moreInfo[group.groupId];
        for (var idx in group.researchResources){
            this.loadDetails(group.researchResources[idx].putCode, $event);
        }
    };

    showSources(group, $event): void {
        $event.stopPropagation();
        this.editSources[group.groupId] = true;
    };

    showTooltip(element): void{        
        this.showElement[element] = true;
    };

    sort(type, displayKey, reverse): void {
        /*var sortKey;

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
        }*/ 
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

    //Default init functions provided by Angular Core
    ngAfterViewInit() {
        //Fire functions AFTER the view inited. Useful when DOM is required or access children directives

        this.subscription = this.researchResourceService.notifyObservable$.subscribe(
            (res) => {                
                if (res.action == 'cancel' || res.action == 'delete') {
                    if(res.successful == true) {
                        this.getResearchResourceGroups();
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
        this.getResearchResourceGroups();
    };
}