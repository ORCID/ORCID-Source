declare var $: any;
declare var ActSortState: any;
declare var getBaseUri: any;
declare var logAjaxError: any;
declare var openImportWizardUrl: any;
declare var orcidVar: any;
declare var workIdLinkJs: any;
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

import { PeerReviewService } 
    from '../../shared/peerReview.service.ts';

import { CommonService } 
    from '../../shared/common.service.ts';

import { EmailService } 
    from '../../shared/email.service.ts';

import { ModalService } 
    from '../../shared/modal.service.ts'; 

import { WorkspaceService } 
    from '../../shared/workspace.service.ts'; 

import { OrgDisambiguated } 
    from '../orgIdentifierPopover/orgDisambiguated.ts';
    
@Component({
    selector: 'peer-review-ng2',
    template:  scriptTmpl("peer-review-ng2-template")
})

export class PeerReviewComponent implements AfterViewInit, OnDestroy, OnInit {
    @Input() publicView: any;
    private ngUnsubscribe: Subject<void> = new Subject<void>();
    private subscription: Subscription;

    editSources: any;
    emails: any;
    isPublicPage: boolean;
    moreInfo: any;
    moreInfoOpen: boolean;
    noLinkFlag: boolean;
    orgDisambiguatedDetails: any;
    peerReviewImportWizardList: any;
    privacyHelp: any;
    privacyHelpCurKey: any;
    showElement: any;
    showPeerReviewDetails: any;
    showResourceItemDetails: any;
    sortState: any;
    peerReviewImportWizard: boolean = false;
    wizardDescExpanded: any = {};

    constructor(
        private peerReviewService: PeerReviewService,
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
        this.noLinkFlag = true;
        this.orgDisambiguatedDetails = new Array();
        this.privacyHelp = {};
        this.privacyHelpCurKey = null;
        this.showElement = {};
        this.showPeerReviewDetails = new Array();
        this.showResourceItemDetails = {};
        this.sortState = this.sortState = new ActSortState(GroupedActivities.PEER_REVIEW);
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

    deletePeerReviewConfirm(peerReview): void {
        this.emailService.getEmails()
        .pipe(    
            takeUntil(this.ngUnsubscribe)
        )
        .subscribe(
            data => {
                this.emails = data;
                if( this.emailService.getEmailPrimary().verified ){
                    this.peerReviewService.notifyOther({peerReview:peerReview});
                    this.modalService.notifyOther({action:'open', moduleId: 'modalPeerReviewDelete'});
                }else{
                    this.modalService.notifyOther({action:'open', moduleId: 'modalemailunverified'});
                }
            },
            error => {
                //console.log('getEmails', error);
            } 
        );
    };

    getPeerReviewGroups(): void {
        if(this.publicView === "true") {
            this.peerReviewService.getPublicPeerReviewGroups(!this.sortState.reverseKey[this.sortState.predicateKey]).pipe(    
            takeUntil(this.ngUnsubscribe)
            )
                .subscribe(
                    data => {
                        this.peerReviewService.loading = false;
                        this.peerReviewService.handleGroupData(data);
                        this.cdr.detectChanges();
                    },
                    error => {
                        this.peerReviewService.loading = false;
                        console.log('getPublicResearchPageError', error);
                    } 
            );
        } else {
            this.peerReviewService.getPeerReviewGroups(!this.sortState.reverseKey[this.sortState.predicateKey]).pipe(    
            takeUntil(this.ngUnsubscribe)
            )
                .subscribe(
                    data => {
                        this.peerReviewService.loading = false;
                        this.peerReviewService.handleGroupData(data);
                        this.cdr.detectChanges();
                    },
                    error => {
                        this.peerReviewService.loading = false;
                        console.log('getPeerReviewGroups error', error);
                    } 
            );
        
        }
    };

    loadPeerReviewImportWizards(): void {
        this.peerReviewService.getPeerReviewImportWizardList()
            .pipe(    
                takeUntil(this.ngUnsubscribe)
            )
            .subscribe(
                data => {
                    this.peerReviewImportWizardList = data;
                    if(data == null || data.length == 0) {
                        this.noLinkFlag = false;
                    }
                },
                error => {
                    console.log('getDetailsError', error);
                } 
            );
    }

    getDetails(putCode): void {
        if(this.publicView === "true"){
            this.peerReviewService.getPublicPeerReviewById(putCode)
            .pipe(    
                takeUntil(this.ngUnsubscribe)
            )
            .subscribe(
                data => {
                    //this.peerReviewService.removeBadExternalIdentifiers(data);
                    this.peerReviewService.details[putCode] = data;   
                },
                error => {
                    console.log('getDetailsError', error);
                } 
            );
        } else {
            this.peerReviewService.getPeerReviewById(putCode)
            .pipe(    
                takeUntil(this.ngUnsubscribe)
            )
            .subscribe(
                data => {
                    //this.peerReviewService.removeBadExternalIdentifiers(data);
                    this.peerReviewService.details[putCode] = data;   
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

    hideSources(groupId): void {
        this.editSources[groupId] = false;
    };

    hideTooltip(element): void{        
        this.showElement[element] = false;
    };

    makeDefault(group, peerReview, putCode): any {
        this.peerReviewService.updateToMaxDisplay(putCode)
        .pipe(    
            takeUntil(this.ngUnsubscribe)
        )
        .subscribe(
            data => {
                //group.defaultActivity = peerReview;
                group.activePutCode = putCode;  
            },
            error => {
                console.log('makeDefault', error);
            } 
        );
    }

    setGroupPrivacy(group, priv, $event): void {
        $event.preventDefault();
        var putCodes = new Array();
        for (var x in group.peerReviewDuplicateGroups) {
            var duplicateGroup = group.peerReviewDuplicateGroups[x];
            for (var y in duplicateGroup.peerReviews) {
                var peerReview = duplicateGroup.peerReviews[y];
                putCodes.push(peerReview.putCode.value);
                peerReview.visibility.visibility = priv;
            }
        }
        group.activeVisibility = priv;
        this.peerReviewService.updateVisibility(putCodes, priv)
        .pipe(    
            takeUntil(this.ngUnsubscribe)
        )
        .subscribe(
            data => {
                if (putCodes.length > 0) {
                    this.peerReviewService.updateVisibility(putCodes, priv);   
                }
                
            },
            error => {
                console.log('Error updating group visibility', error);
            } 
        );
    }

    showDetailsMouseClick(group, $event): void{
        $event.stopPropagation(group.groupId);
        this.moreInfo[group.groupId] = !this.moreInfo[group.groupId];
    };

    showMoreDetails(putCode, $event) {
        $event.stopPropagation();
        this.showPeerReviewDetails[putCode] = !this.showPeerReviewDetails[putCode];
        if(this.showPeerReviewDetails[putCode] == true){
            this.closePopover($event);
            this.moreInfoOpen = true;
            $($event.target).next().css('display','inline');
            if(this.peerReviewService.details[putCode] == undefined){
                this.getDetails(putCode);
            }
        }
    };

    showSources(groupId,$event): void {
        $event.stopPropagation();
        this.editSources[groupId] = true;
        this.hideAllTooltip();
    };

    showTooltip(element): void{        
        this.showElement[element] = true;
    };
        
    sort(key): void {
        this.sortState.sortBy(key);
        this.peerReviewService.resetGroups();
        if(this.publicView === "true") {
            this.peerReviewService.getPublicPeerReviewGroups(!this.sortState.reverseKey[key]).pipe(    
            takeUntil(this.ngUnsubscribe)
            )
                .subscribe(
                    data => {
                        this.peerReviewService.loading = false;
                        this.peerReviewService.handleGroupData(data);
                        this.cdr.detectChanges();
                    },
                    error => {
                        this.peerReviewService.loading = false;
                        console.log('getPublicResearchPageError', error);
                    } 
            );
        } else {
            this.peerReviewService.getPeerReviewGroups(!this.sortState.reverseKey[key]).pipe(    
            takeUntil(this.ngUnsubscribe)
            )
                .subscribe(
                    data => {
                        this.peerReviewService.loading = false;
                        this.peerReviewService.handleGroupData(data);
                        this.cdr.detectChanges();
                    },
                    error => {
                        this.peerReviewService.loading = false;
                        console.log('getPeerReviewGroups error', error);
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

        this.subscription = this.peerReviewService.notifyObservable$.subscribe(
            (res) => {                
                if (res.action == 'cancel' || res.action == 'delete') {
                    if(res.successful == true) {
                        this.closeAllMoreInfo();
                        this.peerReviewService.resetGroups();
                        this.getPeerReviewGroups();
                    }
                }                
            }
        );
    };

    showPeerReviewImportWizard () {
        this.peerReviewImportWizard = !this.peerReviewImportWizard;
    }

    toggleWizardDesc(id): void{
        this.wizardDescExpanded[id] = !this.wizardDescExpanded[id];
    };

    openImportWizardUrlFilter(url, client): void {
        url = url + '?client_id=' + client.id + '&response_type=code&scope=' + client.scopes + '&redirect_uri=' + client.redirectUri;
        openImportWizardUrl(url);
    };

    ngOnDestroy() {
        this.ngUnsubscribe.next();
        this.ngUnsubscribe.complete();
    };

    ngOnInit() {
        this.getPeerReviewGroups();
        this.loadPeerReviewImportWizards();
    };
    
    getBaseUri(): String {
        return getBaseUri();
    };
}