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
    selector: 'affiliation-ng2',
    template:  scriptTmpl("affiliation-ng2-template"),
    providers: [CommonService]
})
export class AffiliationComponent implements AfterViewInit, OnDestroy, OnInit {
    private ngUnsubscribe: Subject<void> = new Subject<void>();
    private subscription: Subscription;

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
    //TODO: remove when new aff types is live and leave only educationsAndQualifications
    sectionOneElements: any;
    
    constructor(
        private affiliationService: AffiliationService,
        private emailService: EmailService,
        //private groupedActivitiesUtilService: GroupedActivitiesUtilService,
        private modalService: ModalService,
        private workspaceSrvc: WorkspaceService,
        private featuresService: FeaturesService,
        private commonSrvc: CommonService
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
        this.sortState = new ActSortState(GroupedActivities.NG2_AFFILIATION);   
        this.educationsAndQualifications = [];
        this.distinctionsAndInvitedPositions = [];
        this.membershipsAndServices = [];
        this.sectionOneElements = [];
        this.displayNewAffiliationTypesFeatureEnabled = this.featuresService.isFeatureEnabled('DISPLAY_NEW_AFFILIATION_TYPES');
        this.orgIdsFeatureEnabled = this.featuresService.isFeatureEnabled('SELF_SERVICE_ORG_IDS');
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
                //console.log('this.editAffiliation response', this.editAffiliation);
                this.addingAffiliation = false;
                this.close();
                //affiliationsSrvc.getAffiliations('affiliations/affiliationIds.json');
                if (data.errors.length > 0){
                    /*
                    $scope.editAffiliation = data;
                    commonSrvc.copyErrorsLeft($scope.editAffiliation, data);
                    $scope.addingAffiliation = false;
                    $scope.$apply();
                    */
                }
            },
            error => {
                //console.log('setBiographyFormError', error);
            } 
        );
    };

    addAffiliationModal(type, affiliation): void {
        this.emailService.getEmails()
        .takeUntil(this.ngUnsubscribe)
        .subscribe(
            data => {
                this.emails = data;
                if( this.emailService.getEmailPrimary().verified ){
                    console.log('ng2 affi', affiliation);
                    this.affiliationService.notifyOther({ affiliation:affiliation, type: type });
                    this.modalService.notifyOther({action:'open', moduleId: 'modalAffiliationForm'});
                }else{
                    this.modalService.notifyOther({action:'open', moduleId: 'modalemailunverified'});
                }
            },
            error => {
                //console.log('getEmails', error);
            } 
        );
        /*
        if(this.emailVerified === true || this.configuration.showModalManualEditVerificationEnabled == false){
            this.addAffType = type;
            if(affiliation === undefined) {
                this.removeDisambiguatedAffiliation();
                $.ajax({
                    url: getBaseUri() + '/affiliations/affiliation.json',
                    dataType: 'json',
                    success: function(data) {
                        this.editAffiliation = data;
                        if (type != null){
                            this.editAffiliation.affiliationType.value = type;
                        }
                        $scope.$apply(function() {
                            $scope.showAddModal();
                        });
                    }
                }).fail(function(e) {
                    //console.log("Error fetching affiliation: ", $scope.editAffiliation.affiliationType.value,  e);
                });
            } else {
                this.editAffiliation = affiliation;
                if(this.editAffiliation.orgDisambiguatedId != null){
                    this.getDisambiguatedAffiliation(this.editAffiliation.orgDisambiguatedId.value);
                }
                this.showAddModal();
            }
        }else{
            this.showEmailVerificationModal();
        }
        */
        
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
        $("#affiliationName").bind("typeahead:selected", function(obj, datum) {
            //$scope.selectAffiliation(datum);
            //$scope.$apply();
        });
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

    deleteAffiliation(affiliation): void {
        this.emailService.getEmails()
        .takeUntil(this.ngUnsubscribe)
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
        /*
        this.affiliationService.deleteAffiliation(affiliation)
            .takeUntil(this.ngUnsubscribe)
            .subscribe(data => {         
                if(data.errors.length == 0) {
                    if(affiliation.affiliationType != null && affiliation.affiliationType.value != null) {
                        if(affiliation.affiliationType.value == 'distinction' || affiliation.affiliationType.value == 'invited-position') {
                            this.removeFromArray(this.distinctionsAndInvitedPositions, affiliation.putCode.value);
                        } else if (affiliation.affiliationType.value == 'education' || affiliation.affiliationType.value == 'qualification'){
                            this.removeFromArray(this.educationsAndQualifications, affiliation.putCode.value);
                            if(affiliation.affiliationType.value == 'education') {
                                this.removeFromArray(this.educations, affiliation.putCode.value);
                            }                            
                        } else if (affiliation.affiliationType.value == 'employment'){
                            this.removeFromArray(this.employments, affiliation.putCode.value);                            
                        } else if(affiliation.affiliationType.value == 'membership' || affiliation.affiliationType.value == 'service') {
                            this.removeFromArray(this.membershipsAndServices, affiliation.putCode.value);                            
                        } 
                    }                    
                }                                
            });         
        */
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

    getAffiliationsById( affiliationIds ): void {
        this.affiliationService.getAffiliationsById( affiliationIds ).takeUntil(this.ngUnsubscribe)
            .subscribe(
                data => {
                    for (let i in data) {
                        if (data[i].affiliationType != null 
                            && data[i].affiliationType.value != null) {                            
                            if(data[i].affiliationType.value == 'distinction') {
                                this.distinctionsAndInvitedPositions.push( data[i] );
                            } else if(data[i].affiliationType.value == 'education'){
                                this.educations.push(data[i]);
                                this.educationsAndQualifications.push( data[i] );
                            } else if ( data[i].affiliationType.value == 'employment' ) {
                                this.employments.push( data[i] );
                            } else if(data[i].affiliationType.value == 'invited-position') {
                                this.distinctionsAndInvitedPositions.push( data[i] );
                            } else if(data[i].affiliationType.value == 'membership') {
                                this.membershipsAndServices.push( data[i] );
                            } else if (data[i].affiliationType.value == 'qualification') {
                                this.educationsAndQualifications.push(data[i]);                             
                            } else if(data[i].affiliationType.value == 'service') {
                                this.membershipsAndServices.push( data[i] );
                            }
                        }
                    };
                    
                    if(this.displayNewAffiliationTypesFeatureEnabled) {
                        this.sectionOneElements = this.educationsAndQualifications;
                    } else {
                        this.sectionOneElements = this.educations;
                    } 
                    
                    this.sort('endDate', true);
                },
                error => {
                    console.log('getAffiliationsById error', error);
                } 

        );
    };

    getAffiliationsId(): void {
        this.affiliationService.getAffiliationsId()
        .takeUntil(this.ngUnsubscribe)
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

    openEditAffiliation(affiliation): void {
        this.addAffiliationModal(affiliation.affiliationType.value, affiliation);
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
        /*
        $.colorbox({
            html: $compile($('#add-affiliation-modal').html())($scope),            
            onComplete: function() {
                // resize to insure content fits
                formColorBoxResize();
                $scope.bindTypeahead();
            }
        });
        */
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

    sort(key, reverse?): void {
        if( reverse ) {
            this.sortState.reverse = reverse;
        }
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
                console.log('res', res);
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
    }; 
}

/*
        '$scope', 
        '$rootScope', 
        '$compile', 
        '$filter', 
        'affiliationsSrvc', 
        'commonSrvc', 
        'emailSrvc', 
        'initialConfigService', 
        'workspaceSrvc', 
        function (
            $scope, 
            $rootScope, 
            $compile, 
            $filter, 
            affiliationsSrvc, 
            commonSrvc, 
            emailSrvc, 
            initialConfigService,
            workspaceSrvc
        ){
            
            // For resizing color box in case of error
            $scope.$watch(
                'addingAffiliation', 
                function() {
                    setTimeout(
                        function(){
                            $.colorbox.resize();;
                        }, 
                        50
                    );
                }
            );

            

            

            

            

            $scope.serverValidate = function (relativePath) {
                $.ajax({
                    url: getBaseUri() + '/' + relativePath,
                    type: 'POST',
                    data:  angular.toJson($scope.editAffiliation),
                    contentType: 'application/json;charset=UTF-8',
                    dataType: 'json',
                    success: function(data) {
                        commonSrvc.copyErrorsLeft($scope.editAffiliation, data);
                        $scope.$apply();
                    }
                }).fail(function() {
                    // something bad is happening!
                    //console.log("serverValidate() error");
                });
            };

            

            

            

            
        }
    ]

    */