declare var $: any;
declare var ActSortState: any;
declare var GroupedActivities: any;
declare var groupedActivitiesUtil: any;
declare var sortState: any;
declare var typeahead: any;

//Import all the angular components
import { NgFor, NgIf } 
    from '@angular/common'; 

import { AfterViewInit, Component, OnDestroy, OnInit } 
    from '@angular/core';

import { Observable } 
    from 'rxjs/Rx';

import { Subject } 
    from 'rxjs/Subject';

import { Subscription }
    from 'rxjs/Subscription';


import { AffiliationService } 
    from '../../shared/affiliation.service.ts';

import { EmailService } 
    from '../../shared/email.service.ts';

import { GroupedActivitiesUtilService } 
    from '../../shared/groupedActivities.service.ts';

import { ModalService } 
    from '../../shared/modal.service.ts'; 

import { WorkspaceService } 
    from '../../shared/workspace.service.ts'; 

import { FeaturesService }
    from '../../shared/features.service.ts' 
    
import { CommonService } 
    from '../../shared/common.service.ts';

@Component({
    selector: 'affiliation-form-ng2',
    template:  scriptTmpl("affiliation-form-ng2-template")
})
export class AffiliationFormComponent implements AfterViewInit, OnDestroy, OnInit {
    private ngUnsubscribe: Subject<void> = new Subject<void>();
    private subscription: Subscription;
    private viewSubscription: Subscription;

    /*
    emailSrvc: any;
    workspaceSrvc: any;
    */
    addingAffiliation: boolean;
    deleAff: any;
    disambiguatedAffiliation: any;
    displayAffiliationExtIdPopOver: any;
    displayURLPopOver: any;
    editAffiliation: any;
    educations: any;
    emails: any;
    employments: any;
    fixedTitle: string;
    moreInfo: any;
    moreInfoCurKey: any;
    privacyHelp: any;
    privacyHelpCurKey: any;
    showElement: any;
    sortHideOption: boolean;
    sortState: any;
    educationsAndQualifications: any;
    distinctionsAndInvitedPositions: any;
    membershipsAndServices: any;
    orgIdsFeatureEnabled: boolean;
    displayNewAffiliationTypesFeatureEnabled: boolean;
    //: remove when new aff types is live and leave only educationsAndQualifications
    sectionOneElements: any;
    addAffType: any;
    

    constructor(
        private affiliationService: AffiliationService,
        private emailService: EmailService,
        //private groupedActivitiesUtilService: GroupedActivitiesUtilService,
        private modalService: ModalService,
        private workspaceSrvc: WorkspaceService,
        private featuresService: FeaturesService,
        private commonSrvc: CommonService,
    ) {
        /*
        this.emailSrvc = emailSrvc;
        this.workspaceSrvc = workspaceSrvc;
        */
        this.addingAffiliation = false;
        this.deleAff = null;
        this.disambiguatedAffiliation = null;
        this.displayAffiliationExtIdPopOver = {};
        this.displayURLPopOver = {};
        this.editAffiliation = this.getEmptyAffiliation();
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
        this.sortState = new ActSortState(GroupedActivities.AFFILIATION);   
        this.educationsAndQualifications = [];
        this.distinctionsAndInvitedPositions = [];
        this.membershipsAndServices = [];
        this.sectionOneElements = [];
        this.displayNewAffiliationTypesFeatureEnabled = this.featuresService.isFeatureEnabled('DISPLAY_NEW_AFFILIATION_TYPES');
        this.orgIdsFeatureEnabled = this.featuresService.isFeatureEnabled('SELF_SERVICE_ORG_IDS');
        this.addAffType = null;
    }

    getEmptyAffiliation(): any {
        return {
            affiliationType: {
                errors: [],
                value: ""
            },
            affiliationName: {
                errors: [],
                value: ""
            },
            city: {
                errors: [],
                value: ""
            },
            country: {
                errors: [],
                value: ""
            },
            departmentName: {
                errors: [],
                value: ""
            },
            disambiguatedAffiliationSourceId: "",
            disambiguationSource: "",
            endDate: {
                errors: [],
                value: ""
            },
            errors: [],
            orgDisambiguatedId: {
                value: ""
            },
            putCode: {
                value: null
            },
            region: {
                errors: [],
                value: ""
            },
            roleTitle: {
                errors: [],
                value: ""
            },
            startDate: {
                errors: [],
                value: ""
            },
            url: {
                errors: [],
                value: ""
            },
        };
    }
    
     addAffiliation(): void {
        if (this.addingAffiliation == true) {
            return; // don't process if adding affiliation
        }

        this.addingAffiliation = true;
        this.editAffiliation.errors.length = 0;
        this.affiliationService.setData( this.editAffiliation )
        .takeUntil(this.ngUnsubscribe)
        .subscribe(
            data => {
                this.editAffiliation = data;
                this.addingAffiliation = false;
                if (data.errors.length > 0){                    
                    this.editAffiliation = data;
                    this.commonSrvc.copyErrorsLeft(this.editAffiliation, data);
                } else {
                    this.closeModal();
                    this.removeDisambiguatedAffiliation();
                    this.editAffiliation = this.getEmptyAffiliation();
                    this.affiliationService.notifyOther({action:'add', successful:true});
                }
            },
            error => {
                console.log('affiliationForm.component.ts addAffiliation Error', error);
            } 
        );
    };

    bindTypeahead(): void {
        let numOfResults = 100;

        $("#affiliationName").typeahead({
            name: 'affiliationName',
            limit: numOfResults,
            remote: {
                url: getBaseUri()+'/affiliations/disambiguated/name/%QUERY?limit=' + numOfResults
            },
            template: function (datum) {
                var forDisplay =
                    '<span style=\'white-space: nowrap; font-weight: bold;\'>' + datum.value+ '</span>'
                    +'<span style=\'font-size: 80%;\'>'
                    + ' <br />' + datum.city;
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

        $('#affiliationName').bind(
            "typeahead:selected", 
            (
                function(obj, datum) {
                    console.log('typeahead', obj, datum, this);
                    this.selectAffiliation(datum);
                }
            ).bind(this)
        );
    };

    closeModal(): void {
        this.modalService.notifyOther({action:'close', moduleId: 'modalAffiliationForm'});
    };

    closeMoreInfo(key): void {
        this.moreInfo[key]=false;
    };
    
    removeFromArray(affArray, putCode): void {
        console.log("putCode: " + putCode);
        console.log(affArray);
        for(let idx in affArray) {
            if(affArray[idx].putCode.value == putCode) {
                affArray.splice(idx, 1);
                break;
            }
        }
    };

    displayEducation(): boolean {
        return this.workspaceSrvc.displayEducation;
    };
    
    displayEducationAndQualification(): boolean {
        return this.workspaceSrvc.displayEducationAndQualification;
    };

    getDisambiguatedAffiliation = function(id) {
        this.affiliationService.getDisambiguatedAffiliation(id)
        .takeUntil(this.ngUnsubscribe)
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

    hideTooltip(element): void{        
        this.showElement[element] = false;
    };

    hideURLPopOver(id): void{
        this.displayURLPopOver[id] = false;
    };

    hideAffiliationExtIdPopOver(id): void{
        this.displayAffiliationExtIdPopOver[id] = false;
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

    removeDisambiguatedAffiliation(): void {
        this.bindTypeahead();
        
        if (this.disambiguatedAffiliation != undefined) {
            delete this.disambiguatedAffiliation;
        }
        
        if (this.editAffiliation != undefined && this.editAffiliation.disambiguatedAffiliationSourceId != undefined) {
            delete this.editAffiliation.disambiguatedAffiliationSourceId;
        }
        
        if (this.editAffiliation != undefined && this.editAffiliation.orgDisambiguatedId != undefined) {
            delete this.editAffiliation.orgDisambiguatedId;
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
                this.unbindTypeahead();
            }
        }
    };

    serverValidate(relativePath): void {
        this.affiliationService.serverValidate(this.editAffiliation, relativePath)
        .takeUntil(this.ngUnsubscribe)
        .subscribe(
            data => {
                console.log('data', data);
                if (data != null) {
                    this.commonSrvc.copyErrorsLeft(this.editAffiliation, data);
                }
            },
            error => {
            } 
        );
    }

    setAddAffiliationPrivacy(priv, $event): void {
        $event.preventDefault();
        this.editAffiliation.visibility.visibility = priv;
    };

    setPrivacy(aff, priv, $event): void {
        $event.preventDefault();
        aff.visibility.visibility = priv;                
        this.affiliationService.updateVisibility(aff)
            .takeUntil(this.ngUnsubscribe)
            .subscribe(data => {});
    };

    showAddModal(): void{
        let numOfResults = 25;
    };

    showAffiliationExtIdPopOver(id): void{
        this.displayAffiliationExtIdPopOver[id] = true;
    };

    showDetailsMouseClick = function(group, $event) {
        $event.stopPropagation();
        this.moreInfo[group.groupId] = !this.moreInfo[group.groupId];
    };

    showTooltip(element): void{        
        this.showElement[element] = true;
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
        .takeUntil(this.ngUnsubscribe)
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

    toggleEducation(): void {
        this.workspaceSrvc.toggleEducation();
    };

    toggleEducationAndQualification(): void {
        this.workspaceSrvc.toggleEducationAndQualification();
    };

    trackByFn(index, item): any {
        return index; // or item.id
    };

    unbindTypeahead(): void {
        $('#affiliationName').typeahead('destroy');
    };

    //Default init functions provided by Angular Core
    ngAfterViewInit() {
        //Fire functions AFTER the view inited. Useful when DOM is required or access children directives
        this.subscription = this.affiliationService.notifyObservable$.subscribe(
            (res) => {
                this.bindTypeahead();
                this.addAffType = res.type;
                if( res.affiliation != undefined ) {
                    this.editAffiliation = res.affiliation;
                } else {
                    this.editAffiliation = this.getEmptyAffiliation();
                    this.editAffiliation.affiliationType.value = this.addAffType;
                }
            }
        );
        
        this.viewSubscription = this.modalService.notifyObservable$.subscribe(
                (res) => {
                    console.log(JSON.stringify(res));
                    if(res.moduleId = "modalAffiliationForm") {
                        if(res.action == "open" && res.edit == false) {
                            this.editAffiliation = this.getEmptyAffiliation();
                            this.editAffiliation.affiliationType.value = this.addAffType;
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
        if( this.affiliationService.affiliation ){
        } else {
            this.addAffType = this.affiliationService.type;     
        }
    }; 
}
