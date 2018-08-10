declare var $: any;
declare var ActSortState: any;
declare var GroupedActivities: any;
declare var groupedActivitiesUtil: any;
declare var sortState: any;
declare var typeahead: any;

//Import all the angular components
import { NgForOf, NgIf } 
    from '@angular/common'; 

import { AfterViewInit, ChangeDetectorRef, Component, OnDestroy, OnInit } 
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
    selector: 'funding-form-ng2',
    template:  scriptTmpl("funding-form-ng2-template")
})
export class FundingFormComponent implements AfterViewInit, OnDestroy, OnInit {
    private ngUnsubscribe: Subject<void> = new Subject<void>();

    /*
    emailSrvc: any;
    workspaceSrvc: any;
    */
    addingFunding: boolean;
    deleFunding: any;
    deleteGroup: any;
    deletePutCode: any;
    disambiguatedFunding: any;
    displayFundingxtIdPopOver: any;
    displayURLPopOver: any;
    editFunding: any;
    editSources: any;
    editTranslatedTitle: any;
    educations: any;
    emails: any;
    employments: any;
    fixedTitle: string;
    fundingToAddIds: any;
    fundings: any;
    groups: any;
    loading: boolean;
    moreInfo: any;
    moreInfoCurKey: any;
    privacyHelp: any;
    privacyHelpCurKey: any;
    putCode: any;
    selectOrgDefinedFundingSubType: any;
    showElement: any;
    sortHideOption: boolean;
    sortState: any;

    constructor(
        private cdr: ChangeDetectorRef,
        private commonService: CommonService,
        private fundingService: FundingService,
        private emailService: EmailService,
        private modalService: ModalService,
        private workspaceSrvc: WorkspaceService
    ) {
        /*
        this.emailSrvc = emailSrvc;
        this.workspaceSrvc = workspaceSrvc;
        */
        this.addingFunding = false;
        this.deleFunding = null;
        this.deleteGroup = null;
        this.deletePutCode = null;
        this.disambiguatedFunding = {};
        this.displayURLPopOver = {};
        this.editFunding = {
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
                day: "",
                month: "",
                year: "",
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
                    content: null,
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
                day: "",
                month: "",
                year: "",
                errors: {},
            },
            url: {
                errors: {},
                value: null
            }
        };
        this.editSources = {};
        this.editTranslatedTitle = false;
        this.emails = {};
        this.fixedTitle = '';
        this.fundings = new Array();
        this.fundingToAddIds = {};
        this.groups = new Array();
        this.loading = false;    
        this.moreInfo = {};
        this.moreInfoCurKey = null;
        this.privacyHelp = {};
        this.privacyHelpCurKey = null;
        this.putCode = null;
        this.selectOrgDefinedFundingSubType = {};
        this.showElement = {};
        this.sortHideOption = false;
        this.sortState = new ActSortState(GroupedActivities.FUNDING);
    }

    addFundingExternalIdentifier(): void {
        this.editFunding.externalIdentifiers.push(
            {
                type: {
                    value: ""
                }, 
                value: {
                    value: ""
                }, 
                url: {
                    value: ""
                }, 
                relationship: {
                    value: "self"
                } 
            }
        );
    };

    bindTypeaheadForOrgs(): void {
        var numOfResults = 100;
        (<any>$("#fundingName")).typeahead({
            name: 'fundingName',
            limit: numOfResults,
            remote: {
                replace: function () {
                    var q = getBaseUri()+'/fundings/disambiguated/name/';
                    if ($('#fundingName').val()) {
                        q += encodeURIComponent($('#fundingName').val());
                    }
                    q += '?limit=' + numOfResults + '&funders-only=true';
                    return q;
                }
            },
            template: function (datum) {
                var forDisplay =
                    '<span style=\'white-space: nowrap; font-weight: bold;\'>' + datum.value+ '</span>'
                    +'<span style=\'font-size: 80%;\'>'
                    + ' <br />';
                if(datum.city){
                    forDisplay += datum.city;
                }
                if(datum.region){
                    forDisplay += ", " + datum.region;
                }
                if (datum.orgType != null && datum.orgType.trim() != ''){
                    forDisplay += ", " + datum.orgType;
                }
                forDisplay += '</span><hr />';

                return forDisplay;
            }
        });
        $("#fundingName").bind("typeahead:selected", function(obj, datum) {
            this.selectFunding(datum);
            
        });
    };

    bindTypeaheadForSubTypes(): void {
        var numOfResults = 20;
        (<any>$("#organizationDefinedType")).typeahead({
            name: 'organizationDefinedType',
            limit: numOfResults,
            remote: {
                replace: function () {
                    var q = getBaseUri()+'/fundings/orgDefinedSubType/';
                    if ($('#organizationDefinedType').val()) {
                        q += encodeURIComponent($('#organizationDefinedType').val());
                    }
                    q += '?limit=' + numOfResults;
                    return q;
                }
            },
            template: function (datum) {
                var forDisplay =
                    '<span style=\'white-space: nowrap; font-weight: bold;\'>' + datum.value + '</span><hr />';
                return forDisplay;
            }
        });
        $("#organizationDefinedType").bind("typeahead:selected", function(obj, datum){
            this.selectOrgDefinedFundingSubType(datum);
            
        });
    };


    closeModal(): void {
        this.modalService.notifyOther(
            {
                action:'close', 
                moduleId: 'modalFundingForm'
            }
        );
    };

    closeMoreInfo(key): void {
        this.moreInfo[key]=false;
    };

    createNew(work): void {
        var cloneF = JSON.parse(JSON.stringify(work));
        cloneF.source = null;
        cloneF.putCode = null;
        for (var idx in cloneF.externalIdentifiers){
            cloneF.externalIdentifiers[idx].putCode = null;
        }
        return cloneF;
    }

    deleteGroupFunding(putCode): void {
        let rmWorks;
        for (let idx in this.fundingService.groups) {
            if (this.fundingService.groups[idx].hasPut(putCode)) {
               for (var idj in this.fundingService.groups[idx].activities) {
                   this.fundingService.deleteFunding(this.fundingService.groups[idx].activities[idj]);
                }
                this.fundingService.groups.splice(idx,1);
                break;
            }
        }
    }

    deleteFunding(delFunding): void {
        let rmFunding;
        for (var idx in this.fundingService.groups) {
            if (this.fundingService.groups[idx].hasPut(this.putCode)) {
                rmFunding = this.fundingService.groups[idx].getByPut(this.putCode);
                break;
            };
        };
        // remove work on server
        this.fundingService.deleteFunding(rmFunding);
        this.closeModal();
    };


    deleteFundingByPut(putCode, deleteGroup): void {
        if (deleteGroup){
            //this.this.fundingService.deleteGroupFunding(putCode);
        }
        else {
            //this.this.fundingService.deleteFunding(putCode);
        }
        //$.colorbox.close();
    };

    deleteFundingConfirm(putCode, deleteGroup): void {
        //var funding = this.getFunding(putCode);
        var funding = {
            fundingTitle: {
                title: {
                    value: null
                }
            }
        };

        var maxSize = 100;
        
        this.deletePutCode = putCode;
        this.deleteGroup = deleteGroup;
        
        if (funding.fundingTitle && funding.fundingTitle.title){
            this.fixedTitle = funding.fundingTitle.title.value;
        }
        else{
            this.fixedTitle = '';
        } 

        if(this.fixedTitle.length > maxSize){
            this.fixedTitle = this.fixedTitle.substring(0, maxSize) + '...';
        }

        /*
        $.colorbox({
            html : $compile($('#delete-funding-modal').html())($scope),
            onComplete: function() {$.colorbox.resize();}
        });
        */
    };

    fundingCount(): Number {
        var count = 0;
        for (var idx in this.fundingService.groups) {
            count += this.fundingService.groups[idx].activitiesCount;
        }
        return count;
    }

    getEmptyExtId(): any {
        return {
            "errors": [],
            "type": {
                "errors": [],
                "value": "award",
                "required": true,
                "getRequiredMessage": null
            },
            "value": {
                "errors": [],
                "value": "",
                "required": true,
                "getRequiredMessage": null
            },
            "url": {
                "errors": [],
                "value": "",
                "required": true,
                "getRequiredMessage": null
            },
            "putCode": null,
            "relationship": {
                "errors": [],
                "value": "self",
                "required": true,
                "getRequiredMessage": null
            }
        };
    }

    getFunding(putCode): any {
        for (var idx in this.fundingService.groups) {
            if (this.fundingService.groups[idx].hasPut(putCode)){
                return this.fundingService.groups[idx].getByPut(putCode);
            }
        }
        return null;
    }

    getFundingsById( ids ): any {
        this.fundingService.getFundingsById( ids ).pipe(    
            takeUntil(this.ngUnsubscribe)
        )
        .subscribe(
            data => {

                //console.log('this.getFundingsById', data);
                for (let i in data) {
                    this.fundings.push(data[i]);
                };

            },
            error => {
                //console.log('getBiographyFormError', error);
            } 
        );
    }

    getGroup(putCode): any {
        for (var idx in this.fundingService.groups) {
            if (this.fundingService.groups[idx].hasPut(putCode)){
                return this.fundingService.groups[idx];
            }
        }
        return null;
    }


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

    putFunding(): void {
        if (this.addingFunding){    
            return; // don't process if adding funding
        } 
        this.addingFunding = true;
        this.editFunding.errors.length = 0;

        this.fundingService.putFunding( this.editFunding )
        .pipe(    
            takeUntil(this.ngUnsubscribe)
        )
        .subscribe(
            data => {
                this.editFunding = data;
                //console.log('this.editFunding response', this.editFunding);
                this.addingFunding = false;

                if (data['errors'].length == 0){
                    this.closeModal();
                    
                } else {
                    this.editFunding = data;
                    if(this.editFunding.externalIdentifiers.length == 0) {
                        this.addFundingExternalIdentifier();
                    }
                }
                this.addingFunding = false;
            },
            error => {
                //console.log('setFundingFormError', error);
            } 
        );
    };

    serverValidate(relativePath): void {
        if( relativePath == 'fundings/funding/datesValidate.json' ){
            if( this.editFunding.startDate.month == "" 
                || this.editFunding.startDate.day == ""
                || this.editFunding.startDate.year == ""
                || this.editFunding.endDate.month == "" 
                || this.editFunding.endDate.day == ""
                || this.editFunding.endDate.year == ""
                || this.editFunding.startDate.month == null 
                || this.editFunding.startDate.day == null
                || this.editFunding.startDate.year == null
                || this.editFunding.endDate.month == null 
                || this.editFunding.endDate.day == null
                || this.editFunding.endDate.year == null  ){
                return;
            }
        }
        this.fundingService.serverValidate(this.editFunding, relativePath)
        .pipe(    
            takeUntil(this.ngUnsubscribe)
        )
        .subscribe(
            data => {
                if (data != null) {
                    this.commonService.copyErrorsLeft(this.editFunding, data);
                }
            },
            error => {
            } 
        );
    }


    setIdsToAdd(ids): void {
        this.fundingToAddIds = ids;
    }

    showAddModal(): void{
        let numOfResults = 25;

    };

    showDetailsMouseClick = function(group, $event) {
        $event.stopPropagation();
        this.moreInfo[group.groupId] = !this.moreInfo[group.groupId];
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

    typeChanged(): void {
        var selectedType = this.editFunding.fundingType.value;
        switch (selectedType){
        case 'award':
            $("#funding-ext-ids-title").text(om.get("funding.add.external_id.title.award"));
            $("#funding-ext-ids-value-label").text(om.get("funding.add.external_id.value.label.award"));
            $("#funding-ext-ids-value-input").attr("placeholder", om.get("funding.add.external_id.value.placeholder.award"));
            $("#funding-ext-ids-url-label").text(om.get("funding.add.external_id.url.label.award"));
            $("#funding-ext-ids-url-input").attr("placeholder", om.get("funding.add.external_id.url.placeholder.award"));
            break;
        case 'contract':
            $("#funding-ext-ids-title").text(om.get("funding.add.external_id.title.contract"));
            $("#funding-ext-ids-value-label").text(om.get("funding.add.external_id.value.label.contract"));
            $("#funding-ext-ids-value-input").attr("placeholder", om.get("funding.add.external_id.value.placeholder.contract"));
            $("#funding-ext-ids-url-label").text(om.get("funding.add.external_id.url.label.contract"));
            $("#funding-ext-ids-url-input").attr("placeholder", om.get("funding.add.external_id.url.placeholder.contract"));
            break;
        case 'grant':
            $("#funding-ext-ids-title").text(om.get("funding.add.external_id.title.grant"));
            $("#funding-ext-ids-value-label").text(om.get("funding.add.external_id.value.label.grant"));
            $("#funding-ext-ids-value-input").attr("placeholder", om.get("funding.add.external_id.value.placeholder.grant"));
            $("#funding-ext-ids-url-label").text(om.get("funding.add.external_id.url.label.grant"));
            $("#funding-ext-ids-url-input").attr("placeholder", om.get("funding.add.external_id.url.placeholder.grant"));
            break;
        case 'salary-award':
            $("#funding-ext-ids-value-label").text(om.get("funding.add.external_id.value.label.award"));
            $("#funding-ext-ids-value-input").attr("placeholder", om.get("funding.add.external_id.value.placeholder.award"));
            $("#funding-ext-ids-url-label").text(om.get("funding.add.external_id.url.label.award"));
            $("#funding-ext-ids-url-input").attr("placeholder", om.get("funding.add.external_id.url.placeholder.award"));
            $("#funding-ext-ids-title").text(om.get("funding.add.external_id.title.award"));
            break;
        default:
            $("#funding-ext-ids-title").text(om.get("funding.add.external_id.title.grant"));
            $("#funding-ext-ids-value-label").text(om.get("funding.add.external_id.value.label.grant"));
            $("#funding-ext-ids-value-input").attr("placeholder", om.get("funding.add.external_id.value.placeholder.grant"));
            $("#funding-ext-ids-url-label").text(om.get("funding.add.external_id.url.label.grant"));
            $("#funding-ext-ids-url-input").attr("placeholder", om.get("funding.add.external_id.url.placeholder.grant"));
            break;
        }
    };

    toggleTranslatedTitle(): void{
        this.editTranslatedTitle = !this.editTranslatedTitle;
        console.log(this.editTranslatedTitle);
    };

    unbindTypeahead(): void {
        $('#fundingName').typeahead('destroy');
    };



    //Default init functions provided by Angular Core
    ngAfterViewInit() {
        //Fire functions AFTER the view inited. Useful when DOM is required or access children directives
    };

    ngOnDestroy() {
        (<any>$('#fundingName')).typeahead('destroy');
        (<any>$('#organizationDefinedType')).typeahead('destroy');
  
        this.ngUnsubscribe.next();
        this.ngUnsubscribe.complete();
    };

    ngOnInit() {
        //console.log('initi funding component')

        this.modalService.notifyObservable$.subscribe(
            (res) => {
                //console.log('res.value',res, this.elementId);
                if ( res.moduleId == 'modalFundingForm' ) {

                    if ( res.action === "open") {
                        this.bindTypeaheadForOrgs();
                        this.editFunding = this.fundingService.getFundingToEdit();
                        
                        if (this.editFunding.putCode == null) {
                            this.editFunding.putCode = {
                                'value': null
                            };
                        }

                        if (this.editFunding.fundingTitle == null) {
                            this.editFunding.fundingTitle = {
                                'translatedTitle': {
                                    'content': null,
                                    'languageCode': null
                                }
                            };
                        }

                        if (this.editFunding.fundingTitle.translatedTitle == null) {
                            this.editFunding.fundingTitle.translatedTitle = {

                                'content': null,
                                'languageCode': null
                            };
                        }

                        if (this.editFunding.startDate == null) {
                            this.editFunding.startDate = {
                                'month': "",
                                'year': ""
                            };
                        }

                        if (this.editFunding.endDate == null) {
                            this.editFunding.endDate = {
                                'month': "",
                                'year': ""
                            };
                        }
                    }

                }
            }
        );

    }; 
}

