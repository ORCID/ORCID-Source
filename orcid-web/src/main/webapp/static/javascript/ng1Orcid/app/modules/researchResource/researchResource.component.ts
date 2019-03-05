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
    from '../../shared/researchResource.service';

import { CommonService } 
    from '../../shared/common.service';

import { EmailService } 
    from '../../shared/email.service';

import { ModalService } 
    from '../../shared/modal.service'; 

import { WorkspaceService } 
    from '../../shared/workspace.service'; 

import { OrgDisambiguated } 
    from '../orgIdentifierPopover/orgDisambiguated';
    
@Component({
    selector: 'research-resource-ng2',
    template:  scriptTmpl("research-resource-ng2-template")
})

export class ResearchResourceComponent implements AfterViewInit, OnDestroy, OnInit {
    @Input() publicView: any;
    private ngUnsubscribe: Subject<void> = new Subject<void>();
    private subscription: Subscription;

    editSources: any;
    emails: any;
    isPublicPage: boolean;
    moreInfo: any;
    moreInfoOpen: boolean;
    orgDisambiguatedDetails: any;
    privacyHelp: any;
    privacyHelpCurKey: any;
    showElement: any;
    showResourceItemDetails: any;
    sortState: any;

    constructor(
        private researchResourceService: ResearchResourceService,
        private cdr: ChangeDetectorRef,
        private commonSrvc: CommonService,
        private emailService: EmailService,
        private modalService: ModalService,
        private workspaceSrvc: WorkspaceService,
        private elementRef: ElementRef
    ) {
        this.editSources = {};
        this.emails = {};
        this.isPublicPage = this.commonSrvc.isPublicPage;
        this.moreInfo = {};
        this.moreInfoOpen = false;
        this.orgDisambiguatedDetails = new Array();
        this.privacyHelp = {};
        this.privacyHelpCurKey = null;
        this.showElement = {};
        this.showResourceItemDetails = {};
        this.sortState = this.sortState = new ActSortState(GroupedActivities.NG2_AFFILIATION);
        this.publicView = elementRef.nativeElement.getAttribute('publicView');
    }

    closeAllMoreInfo(): void {
        for (var idx in this.moreInfo){
            this.moreInfo[idx]=false;
        }
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

    setGroupPrivacy = function(group, priv, $event): void {
        $event.preventDefault();
        var putCodes = new Array();
        for (var idx in group.researchResources) {
            putCodes.push(group.researchResources[idx].putCode);
            group.researchResources[idx].visibility.visibility = priv;
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
        if(this.moreInfo[group.groupId] == true){
            for (var idx in group.researchResources){
                this.loadDetails(group.researchResources[idx].putCode, $event);
            }
        } else {
            for (var idx in group.researchResources){
                console.log(group.researchResources[idx]);

                for(var idy in this.researchResourceService.details[group.researchResources[idx].putCode].items){
                    var id = group.researchResources[idx].putCode + 'resourceItem' + idy;
                    console.log(id);
                    console.log(this.showResourceItemDetails[id]);
                    this.showResourceItemDetails[id] = false;
                    console.log(this.showResourceItemDetails[id]);
                    this.cdr.detectChanges();
                }
            }
        }
    };

    showSources(group, $event): void {
        $event.stopPropagation();
        this.editSources[group.groupId] = true;
        this.hideAllTooltip();
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

    toggleResourceItemDetails = function(id, $event) {
        $event.stopPropagation();
        this.showResourceItemDetails[id] = !this.showResourceItemDetails[id];
    };

    //Default init functions provided by Angular Core
    ngAfterViewInit() {
        //Fire functions AFTER the view inited. Useful when DOM is required or access children directives

        this.subscription = this.researchResourceService.notifyObservable$.subscribe(
            (res) => {                
                if (res.action == 'cancel' || res.action == 'delete') {
                    if(res.successful == true) {
                        this.closeAllMoreInfo();
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