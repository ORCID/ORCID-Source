declare var $: any;
declare var ActSortState: any;
declare var GroupedActivities: any;
declare var groupedActivitiesUtil: any;
declare var sortState: any;
declare var typeahead: any;

//Import all the angular components
import { NgForOf, NgIf } 
    from '@angular/common'; 

import { AfterViewInit, Component, OnDestroy, OnInit } 
    from '@angular/core';

import { Observable, Subject, Subscription } 
    from 'rxjs';

import { takeUntil } 
    from 'rxjs/operators';

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
    template:  scriptTmpl("funding-ng2-template")
})
export class FundingComponent implements AfterViewInit, OnDestroy, OnInit {
    private ngUnsubscribe: Subject<void> = new Subject<void>();

    /*
    emailSrvc: any;
    workspaceSrvc: any;
    */
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
    fundingToAddIds: any;
    groups: any;
    moreInfo: any;
    moreInfoCurKey: any;
    privacyHelp: any;
    privacyHelpCurKey: any;
    showElement: any;
    sortHideOption: boolean;
    sortState: any;

    constructor(
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
        this.fundingToAddIds = {};
        this.groups = new Array();
        this.moreInfo = {};
        this.moreInfoCurKey = null;
        this.privacyHelp = {};
        this.privacyHelpCurKey = null;
        this.showElement = {};
        this.sortHideOption = false;
        this.sortState = new ActSortState(GroupedActivities.FUNDING);
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
                    this.modalService.notifyOther({action:'open', moduleId: 'modalFundingForm', edit: true}); 

                    if(obj == undefined){
                        this.removeDisambiguatedFunding();
                        this.fundingService.getFunding().pipe(    
                            takeUntil(this.ngUnsubscribe)
                        )
                        .subscribe(
                            data => {
                                this.fundingService.setFundingToEdit(data);
                                console.log('this.editFunding', this.editFunding);
                            }
                        );
                    } else {
                        this.fundingService.setFundingToEdit(obj);
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


    bindTypeahead(): void {

    };

    bindTypeaheadForOrgs(): void {
        (<any>$('#fundingName')).typeahead('destroy');
    };

    close(): void {
        //$.colorbox.close();
    };

    closeModal(): void {
        //$.colorbox.close();
    };

    closeMoreInfo(key): void {
        this.moreInfo[key]=false;
    };

    deleteFunding(delFunding): void {
        //this.fundingService.deleteData(delFunding);
        this.closeModal();
    };

    getFundingsById( ids ): any {
        console.log('getFundingsById', ids);
        this.fundingService.getFundingsById( ids ).pipe(    
            takeUntil(this.ngUnsubscribe)
        )
        .subscribe(
            data => {
                this.fundings = data;
                console.log('this.getFundingsById', data, this.fundings);

                for (let i in this.fundings) {
                    var funding = this.fundings[i];
                    groupedActivitiesUtil.group(funding,GroupedActivities.FUNDING, this.groups);
                };

                console.log('this.groups before', this.groups);
                
                for (let j in this.groups){
                    //let evilResponseProps = Object.keys(this.groups[j]);
                    //console.log('evilResponseProps', this.groups[j], this.groups[j]['activities'], Object.keys(this.groups[j]['activities']).length);
                    let goodResponse = [];
                    for(let k = 0; k < Object.keys(this.groups[j]['activities']).length; k++) {
                        let tmpObj = new Object();
                        tmpObj['key'] = Object.keys(this.groups[j]['activities'])[k];
                        tmpObj['value'] = this.groups[j]['activities'][Object.keys(this.groups[j]['activities'])[k]];

                        //console.log('bs', Object.keys(this.groups[j]['activities'])[k] );
                        console.log('tmpObj', tmpObj);
                        goodResponse.push(tmpObj);
                    }
                    this.groups[j]['activitiesObj'] = goodResponse; 
                    //console.log('good response', goodResponse, this.groups[j]);
                }
                console.log('this.groups after2', this.groups);
                
                if (this.fundings.length == 0) {
                    this.fundingService.loading = false;
                } else {
                    //this.getFundingsById( this.fundingToAddIds );//previously addFundingToScope();
                }
                console.log('groupedActivitiesUtil funding', this.groups);
                        

            },
            error => {
                //console.log('getBiographyFormError', error);
            } 
        );
    }

    getFundings(): any {
        this.fundingService.getFundingsId()
        .pipe(    
            takeUntil(this.ngUnsubscribe)
        )
        .subscribe(
            data => {
                
                this.fundingToAddIds = data;
                console.log('getFundingsIds', data, this.fundingToAddIds, this.fundingToAddIds.length);
                if( this.fundingToAddIds.length != 0 ) {
                    var fundingIds = this.fundingToAddIds.splice(0,20).join()
                    this.getFundingsById( fundingIds );
                }
                /*
                let funding = null;
                for (let i in data) {
                    funding = data[i];
                    groupedActivitiesUtil.group(funding,GroupedActivities.FUNDING,this.groups);
                };
                if (fundingSrvc.fundingToAddIds.length == 0) {
                    $timeout(function() {
                      fundingSrvc.loading = false;
                    });
                } else {
                    $timeout(function () {
                        fundingSrvc.addFundingToScope(path);
                    },50);
                }
                
                let ids = data.splice(0,20).join();
                */
            },
            error => {
                //console.log('getBiographyFormError', error);
            } 
        );
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

    removeDisambiguatedFunding(): void {
        this.bindTypeaheadForOrgs();
        if (this.disambiguatedFunding != undefined) {
            delete this.disambiguatedFunding;
        }
        if (this.editFunding != undefined && this.editFunding.disambiguatedFundingSourceId != undefined) {
            delete this.editFunding.disambiguatedFundingSourceId;
        }
    }


    setPrivacy(aff, priv, $event): void {
        $event.preventDefault();
        aff.visibility.visibility = priv;
        //this.affiliationService.updateProfileAffiliation(aff);
    };

    showAddModal(): void{
        let numOfResults = 25;

    };

    showDetailsMouseClick = function(group, $event) {
        $event.stopPropagation();
        this.moreInfo[group.groupId] = !this.moreInfo[group.groupId];
    };

    showSources(group): void {
        this.editSources[group.groupId] = true;
    };

    showTooltip(element): void{        
        this.showElement[element] = true;
    };

    sort(key): void {       
        this.sortState.sortBy(key);
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




    //Default init functions provided by Angular Core
    ngAfterViewInit() {
        //Fire functions AFTER the view inited. Useful when DOM is required or access children directives
    };

    ngOnDestroy() {
        this.ngUnsubscribe.next();
        this.ngUnsubscribe.complete();
    };

    ngOnInit() {
        //console.log('initi funding component');
        this.getFundings();
    }; 
}