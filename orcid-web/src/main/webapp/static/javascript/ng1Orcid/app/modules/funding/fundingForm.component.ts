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
                errors: {},
            },
            url: {
                errors: {},
                value: null
            }
        };
        this.editSources = {};
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

    addFunding(): void {
        if (this.addingFunding == true) {
            return; // don't process if adding affiliation
        }

        this.addingFunding = true;
        this.editFunding.errors.length = 0;
        
        /*
        this.fundingService.addFundingToScope( this.editFunding )
        .pipe(    
            takeUntil(this.ngUnsubscribe)
        )
        .subscribe(
            data => {
                this.editFunding = data;
                //console.log('this.editFunding response', this.editFunding);
                this.addingFunding = false;
                //this.close();

                if (data.errors.length > 0){

                }
            },
            error => {
                //console.log('setBiographyFormError', error);
            } 
        );
        */
        
    };

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
        this.modalService.notifyOther({action:'close', moduleId: 'modalemailunverified'});
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
                   this.fundingService.removeFunding(this.fundingService.groups[idx].activities[idj]);
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
        this.fundingService.removeFunding(rmFunding);
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

    setGroupPrivacy(putCode, priv): void {
        /*
        var group = this.getGroup(putCode);
        for (var idx in group.activities) {
            var curPutCode = group.activities[idx].putCode.value;
            this.fundingService.setPrivacy(curPutCode, priv);
        }
        */
    }

    setPrivacy(putCode, priv): void {
        /*
        var funding = this.getFunding(putCode);
        funding.visibility.visibility = priv;
        this.fundingService.updateProfileFunding(funding);
        */
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
                        this.editFunding = this.fundingService.getFundingToEdit();
                        console.log('form aff', this.editFunding);
                    }

                }
            }
        );

        

        /*
        this.fundingService.getFundingToEdit()
        .pipe(    
            takeUntil(this.ngUnsubscribe)
        )
        .subscribe(
            data => {
                this.editFunding = data;
                console.log('form aff', this.editFunding);
            },
            error => {
                //console.log('getEmails', error);
            } 
        );
        */
    }; 
}

