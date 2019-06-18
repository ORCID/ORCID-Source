declare var $: any;
declare var openImportWizardUrl: any;

//Import all the angular components

import { AfterViewInit, ChangeDetectorRef, Component, OnDestroy, OnInit, ElementRef, Input, Output } 
    from '@angular/core';

import { Subject, Subscription } 
    from 'rxjs';

import { takeUntil } 
    from 'rxjs/operators';

import { CommonService } 
    from '../../shared/common.service';

import { EmailService } 
    from '../../shared/email.service';

import { FundingService } 
    from '../../shared/funding.service';

import { ModalService } 
    from '../../shared/modal.service'; 

import { WorkspaceService } 
    from '../../shared/workspace.service'; 

@Component({
    selector: 'funding-ng2',
    template:  scriptTmpl("funding-ng2-template")
})
export class FundingComponent implements AfterViewInit, OnDestroy, OnInit {
    @Input() publicView: any;

    private ngUnsubscribe: Subject<void> = new Subject<void>();
    private subscription: Subscription;
    private viewSubscription: Subscription;
    
    displayURLPopOver: any;
    editSources: any;
    emails: any;
    fundingImportWizard: boolean;
    fundingImportWizardList: any;
    groups: any;
    isPublicPage: boolean;
    moreInfo: any;
    noLinkFlag: boolean;
    privacyHelp: any;
    privacyHelpCurKey: any;
    showElement: any;
    sortAsc: boolean;
    sortKey: string;
    fundingMoreInfo: any;
    wizardDescExpanded: any;
    recordLocked: boolean;

    constructor(
        private elementRef: ElementRef,
        private cdr: ChangeDetectorRef,
        private commonSrvc: CommonService,
        private fundingService: FundingService,
        private emailService: EmailService,
        private modalService: ModalService,
        private workspaceSrvc: WorkspaceService
    ) {

        this.displayURLPopOver = {};
        this.editSources = {};
        this.emails = {};
        this.fundingImportWizard = false;
        this.groups = new Array();
        this.isPublicPage = this.commonSrvc.isPublicPage;
        this.moreInfo = {};
        this.noLinkFlag = true;
        this.privacyHelp = {};
        this.privacyHelpCurKey = null;
        this.publicView = elementRef.nativeElement.getAttribute('publicView');
        this.showElement = {};
        this.sortAsc = false;
        this.sortKey = 'date';
        this.fundingMoreInfo = {};
        this.wizardDescExpanded = {};
    }

    addFundingModal(funding?): void {
        this.emailService.getEmails()
        .pipe(    
            takeUntil(this.ngUnsubscribe)
        )
        .subscribe(
            data => {
                if( this.emailService.getEmailPrimary().verified ){
                    this.fundingService.notifyOther({ funding:funding });
                    if(funding == undefined) {
                        this.modalService.notifyOther({action:'open', moduleId: 'modalFundingForm', edit: false});
                    } else {
                        this.modalService.notifyOther({action:'open', moduleId: 'modalFundingForm', edit: true});
                    } 
                } else{
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

    getFundingGroups(): any {
        this.groups = new Array();
        if(this.publicView === "true") {
            this.fundingService.getPublicFundingGroups(this.sortKey, this.sortAsc).pipe(    
                takeUntil(this.ngUnsubscribe)
            )
            .subscribe(
                data => {
                    this.fundingService.loading = false;
                    this.groups = data;
                },
                error => {
                    this.fundingService.loading = false;
                    console.log('getFundingGroups', error);
                } 
            );
        } else {
            this.fundingService.getFundingGroups(this.sortKey, this.sortAsc).pipe(    
                takeUntil(this.ngUnsubscribe)
            )
            .subscribe(
                data => {
                    this.fundingService.loading = false;
                    this.groups = data;
                },
                error => {
                    this.fundingService.loading = false;
                    console.log('getFundingGroups', error);
                } 
            );
        }
    }
    
    getFundingDetails(putCode): any {
        if(this.publicView === "true") {
            this.fundingService.getPublicFundingDetails(putCode).pipe(    
            takeUntil(this.ngUnsubscribe)
            )
            .subscribe(
                data => {
                    this.fundingService.loading = false;
                    this.fundingService.details[putCode] = data; 
                },
                error => {
                    this.fundingService.loading = false;
                    console.log('getFundingsByIdError', error);
                } 
            );
        } else {
            this.fundingService.getFundingDetails(putCode).pipe(    
            takeUntil(this.ngUnsubscribe)
            )
            .subscribe(
                data => {
                    this.fundingService.loading = false;
                    this.fundingService.details[putCode] = data; 
                },
                error => {
                    this.fundingService.loading = false;
                    console.log('getFundingsByIdError', error);
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

    loadFundingImportWizards(): void {
        this.fundingService.getFundingImportWizardList()
            .pipe(    
                takeUntil(this.ngUnsubscribe)
            )
            .subscribe(
                data => {
                    this.fundingImportWizardList = data;
                    if(data == null || data.length == 0) {
                        this.noLinkFlag = false;
                    }
                },
                error => {
                    console.log('getFundingImportWizardsError', error);
                } 
            );
    }


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

    openEditFunding(funding, group): void {
        var bestMatchPutCode = null;
        //check for user source version
        if(funding.source == orcidVar.orcidId){
            bestMatchPutCode = funding.putCode.value;
        } else {
            for (var idx in group.fundings) {    
                if (group.fundings[idx].source == orcidVar.orcidId) {
                    bestMatchPutCode = group.fundings[idx].putCode.value;
                    break;
                }
            }
        }
        if(bestMatchPutCode != null){
            if(this.fundingService.details[bestMatchPutCode] == undefined){
                this.fundingService.getFundingDetails(bestMatchPutCode)
                .pipe(    
                    takeUntil(this.ngUnsubscribe)
                )
                .subscribe(
                    data => {
                            this.addFundingModal(data);
                    },
                    error => {
                        console.log('openEditFundingError', error);
                    } 
                );
            } else {
                this.addFundingModal(this.fundingService.details[bestMatchPutCode]);
            }
        } else {
            //otherwise make a copy 
            if(this.fundingService.details[funding.putCode.value] == undefined){
                this.fundingService.getFundingDetails(funding.putCode.value)
                    .pipe(    
                        takeUntil(this.ngUnsubscribe)
                    )
                    .subscribe(
                        data => {
                                this.addFundingModal(this.fundingService.createNew(data));
                        },
                        error => {
                            console.log('openEditFundingError', error);
                        } 
                );
            } else {
                this.addFundingModal(this.fundingService.createNew(this.fundingService.details[funding.putCode.value]));
            }
        }
    }

    openImportWizardUrl(url): void {
        openImportWizardUrl(url);
    };

    openImportWizardUrlFilter(url, client): void {
        url = url + '?client_id=' + client.id + '&response_type=code&scope=' + client.scopes + '&redirect_uri=' + client.redirectUri;
        openImportWizardUrl(url);
    };

    setGroupPrivacy(group, priv, $event): void {
        $event.preventDefault();
        var putCodes = new Array();
        for (var idx in group.fundings) {
            putCodes.push(group.fundings[idx].putCode.value);
            group.fundings[idx].visibility.visibility = priv;
        }
        group.activeVisibility = priv;
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

    showDetailsMouseClick(group, $event): void {
        $event.stopPropagation();
        this.moreInfo[group.groupId] = !this.moreInfo[group.groupId];
        if(this.moreInfo[group.groupId] == true){
            for (var idx in group.fundings){
                if(this.fundingService.details[group.fundings[idx].putCode.value] == undefined){
                    this.getFundingDetails(group.fundings[idx].putCode.value);
                }
            }
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
        if(key == this.sortKey){
            this.sortAsc = !this.sortAsc;
        } else {
            if(key=='title' || key=='type'){
                this.sortAsc = true;
            }
            this.sortKey = key;
        }
        this.getFundingGroups();
    };

    showURLPopOver(id): void {
        this.displayURLPopOver[id] = true;
    };

    swapSources(group, putCode): void{
        group.activePutCode = putCode;
        this.editSources[group.activePutCode] = true;
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

    toggleWizardDesc(id): void{
        this.wizardDescExpanded[id] = !this.wizardDescExpanded[id];
    };

    toggleSectionDisplay($event): void {
        $event.stopPropagation();
        this.workspaceSrvc.displayFunding = !this.workspaceSrvc.displayFunding;
        if(this.workspaceSrvc.displayFunding==false){
            this.fundingImportWizard=false;
        }
    }

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
                        this.getFundingGroups();
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
                        this.getFundingGroups();
                    }
                },
                error => {
                    console.log('affiliation.component.ts: unable to fetch publicUserInfo', error);                    
                } 
            );
        } else {
            this.getFundingGroups();
            this.loadFundingImportWizards();
        }
    }; 
    
    getBaseUri(): String {
        return getBaseUri();
    };
}