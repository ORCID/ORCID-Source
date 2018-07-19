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
    initialOffset = "0";
    membershipsAndServices: any;
    moreInfo: any;
    moreInfoOpen: boolean;
    moreInfoCurKey: any;
    orgIdsFeatureEnabled: boolean;
    privacyHelp: any;
    privacyHelpCurKey: any;
    sectionOneElements: any;
    showElement: any;
    showResourceItemDetails: any;
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
        this.showResourceItemDetails = {};
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

    deleteResearchResourceConfirm(researchResource): void {
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
                        this.researchResourceService.loading = false;
                        this.researchResourceService.handleGroupData(data);
                        this.cdr.detectChanges();
                    },
                    error => {
                        this.researchResourceService.loading = false;
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
                        this.researchResourceService.loading = false;
                        this.researchResourceService.handleGroupData(data);
                        this.cdr.detectChanges();
                    },
                    error => {
                        this.researchResourceService.loading = false;
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

    makeDefault(group, researchResource, putCode): any {
        this.researchResourceService.updateToMaxDisplay(putCode)
        .pipe(    
            takeUntil(this.ngUnsubscribe)
        )
        .subscribe(
            data => {
                group.defaultActivity = researchResource;
                group.activePutCode = group.defaultActivity.putCode;  
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

    setGroupPrivacy = function(group, priv, $event): void {
        $event.preventDefault();
        var putCodes = new Array();
        for (var idx in group.researchResources) {
            putCodes.push(group.researchResources[idx].putCode);
            group.researchResources[idx].visibility = priv;
        }
        group.activeVisibility = priv;
        this.researchResourceService.updateVisibility(putCodes, priv)
        .pipe(    
            takeUntil(this.ngUnsubscribe)
        )
        .subscribe(
            data => {
                if (putCodes.length > 0) {
                    this.researchReourceService.updateVisibility(putCodes, priv);   
                }
                
            },
            error => {
                console.log('Error updating group visibility', error);
            } 
        );
    }

    showDetailsMouseClick = function(group, $event) {
        $event.stopPropagation();
        this.moreInfo[group.groupId] = !this.moreInfo[group.groupId];
        for (var idx in group.researchResources){
            this.loadDetails(group.researchResources[idx].putCode, $event);
        }
    };

    toggleResourceItemDetails = function(id, $event) {
        $event.stopPropagation();
        this.showResourceItemDetails[id] = !this.showResourceItemDetails[id];
    };

    showSources(group, $event): void {
        $event.stopPropagation();
        this.editSources[group.groupId] = true;
    };

    showTooltip(element): void{        
        this.showElement[element] = true;
    };
        
    sort(key): void {
        this.sortState.sortBy(key);
        this.researchResourceService.resetGroups();
        if(this.publicView === "true") {
            this.researchResourceService.getPublicResearchResourcePage(this.sortState.predicateKey, 
                !this.sortState.reverseKey[key]).pipe(    
            takeUntil(this.ngUnsubscribe)
            )
                .subscribe(
                    data => {
                        this.researchResourceService.loading = false;
                        this.researchResourceService.handleGroupData(data);
                        this.cdr.detectChanges();
                    },
                    error => {
                        this.researchResourceService.loading = false;
                        console.log('getPublicResearchPageError', error);
                    } 
            );
        } else {
            this.researchResourceService.getResearchResourcePage(this.sortState.predicateKey, 
                !this.sortState.reverseKey[key]).pipe(    
            takeUntil(this.ngUnsubscribe)
            )
                .subscribe(
                    data => {
                        this.researchResourceService.loading = false;
                        this.researchResourceService.handleGroupData(data);
                        this.cdr.detectChanges();
                    },
                    error => {
                        this.researchResourceService.loading = false;
                        console.log('getResearchResourceGroups error', error);
                    } 
            );
        
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

    //Default init functions provided by Angular Core
    ngAfterViewInit() {
        //Fire functions AFTER the view inited. Useful when DOM is required or access children directives

        this.subscription = this.researchResourceService.notifyObservable$.subscribe(
            (res) => {                
                if (res.action == 'cancel' || res.action == 'delete') {
                    if(res.successful == true) {
                        this.researchResourceService.resetGroups();
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