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

@Component({
    selector: 'affiliation-ng2',
    template:  scriptTmpl("affiliation-ng2-template")
})
export class AffiliationComponent implements AfterViewInit, OnDestroy, OnInit {
    private ngUnsubscribe: Subject<void> = new Subject<void>();

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

    constructor(
        private affiliationService: AffiliationService,
        private emailService: EmailService,
        //private groupedActivitiesUtilService: GroupedActivitiesUtilService,
        private modalService: ModalService,
        private workspaceSrvc: WorkspaceService
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
        this.sortState = new ActSortState(GroupedActivities.AFFILIATION);
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
        /*
        if(emailVerified === true || configuration.showModalManualEditVerificationEnabled == false){
            $scope.addAffType = type;
            if(affiliation === undefined) {
                $scope.removeDisambiguatedAffiliation();
                $.ajax({
                    url: getBaseUri() + '/affiliations/affiliation.json',
                    dataType: 'json',
                    success: function(data) {
                        $scope.editAffiliation = data;
                        if (type != null){
                            $scope.editAffiliation.affiliationType.value = type;
                        }
                        $scope.$apply(function() {
                            $scope.showAddModal();
                        });
                    }
                }).fail(function(e) {
                    //console.log("Error fetching affiliation: ", $scope.editAffiliation.affiliationType.value,  e);
                });
            } else {
                $scope.editAffiliation = affiliation;
                if($scope.editAffiliation.orgDisambiguatedId != null){
                    $scope.getDisambiguatedAffiliation($scope.editAffiliation.orgDisambiguatedId.value);
                }
                $scope.showAddModal();
            }
        }else{
            showEmailVerificationModal();
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

    deleteAff(delAff): void {
        this.affiliationService.deleteData(delAff);
        this.closeModal();
    };

    deleteAffiliation(aff): void {
        var maxSize = 100;
        
        this.deleAff = aff;

        if (aff.affiliationName && aff.affiliationName.value){
            this.fixedTitle = aff.affiliationName.value;
        }
        else {
            this.fixedTitle = '';
        }

        if(this.fixedTitle.length > maxSize){
            this.fixedTitle = this.fixedTitle.substring(0, maxSize) + '...';
        }

        /*
        $.colorbox({
            html : $compile($('#delete-affiliation-modal').html())($scope),
            onComplete: function() {
                $.colorbox.resize();
            }
        });
        */
    };

    displayEducation(): boolean {
        return this.workspaceSrvc.displayEducation;
    };

    getAffiliationsById( affiliationIds ): void {
        this.affiliationService.getAffiliationsById( affiliationIds ).takeUntil(this.ngUnsubscribe)
            .subscribe(
                data => {

                    //console.log('this.getAffiliationsById', data);
                    for (let i in data) {
                        if (data[i].affiliationType != null 
                            && data[i].affiliationType.value != null
                        ){
                            if(data[i].affiliationType.value == 'education'){
                                this.educations.push(data[i]);
                                /*
                                groupedActivitiesUtil.group(
                                    data[i],
                                    GroupedActivities.AFFILIATION, 
                                    this.affiliationService.educations
                                );
                                */
                                
                            } else if ( data[i].affiliationType.value == 'employment' ) {
                                this.employments.push( data[i] );
                                 /*
                                groupedActivitiesUtil.group(
                                    data[i],
                                    GroupedActivities.AFFILIATION,
                                    this.affiliationService.employments
                                );
                                */
                            }
                        }

                    };
                    //console.log('educations', this.educations);
                    //console.log('employments', this.employments);
                    /*
                    if (this.affiliationService.affiliationsToAddIds.length == 0) {
                        this.affiliationService.loading = false;
                        //$rootScope.$apply();
                    } else {
                        //$rootScope.$apply();
                        setTimeout(
                            function () {
                                //this.affiliationService.getAffiliationsById(path);
                            },
                            50
                        );
                    }
                    */

                },
                error => {
                    //console.log('getBiographyFormError', error);
                } 
                /*

        addAffiliationToScope: function(path) {
            if( serv.affiliationsToAddIds.length != 0 ) {
                var affiliationIds = serv.affiliationsToAddIds.splice(0,20).join();
                var url = getBaseUri() + '/' + path + '?affiliationIds=' + affiliationIds;                
                $.ajax({
                    url: url,                        
                    headers : {'Content-Type': 'application/json'},
                    method: 'GET',
                    success: function(data) {
                        for (i in data) {
                            if (data[i].affiliationType != null && data[i].affiliationType.value != null
                                    && data[i].affiliationType.value == 'education'){
                                groupedActivitiesUtil.group(data[i],GroupedActivities.AFFILIATION,serv.educations);
                            }
                            else if (data[i].affiliationType != null && data[i].affiliationType.value != null
                                    && data[i].affiliationType.value == 'employment'){
                                groupedActivitiesUtil.group(data[i],GroupedActivities.AFFILIATION,serv.employments);
                            }
                        };
                        if (serv.affiliationsToAddIds.length == 0) {
                            serv.loading = false;
                            $rootScope.$apply();
                        } else {
                            $rootScope.$apply();
                            setTimeout(
                                function () {
                                    serv.addAffiliationToScope(path);
                                },
                                50
                            );
                        }
                    }
                }).fail(function(e) {
                    //console.log("Error adding affiliations to scope")
                    logAjaxError(e);
                });
            } else {
                serv.loading = false;
            };
        }
        */
        );
    };

    getAffiliationsId(): void {
        this.affiliationService.getAffiliationsId()
        .takeUntil(this.ngUnsubscribe)
        .subscribe(
            data => {
                //console.log('getAffiliationsIds', data);

                if( data.length != 0 ) {
                    let affiliationIds = data.splice(0,20).join();
                    this.getAffiliationsById( affiliationIds );
                    
                }
            },
            error => {
                //console.log('getBiographyFormError', error);
            } 
        );
    };

    getDisambiguatedAffiliation = function(id) {
        this.affiliationService.getDisambiguatedAffiliation(id)
        .takeUntil(this.ngUnsubscribe)
        .subscribe(
            data => {
                ////console.log('this.getDisambiguatedAffiliation', data);

                if (data != null) {
                    this.disambiguatedAffiliation = data;
                    this.editAffiliation.orgDisambiguatedId.value = id;
                    this.editAffiliation.disambiguatedAffiliationSourceId = data.sourceId;
                    this.editAffiliation.disambiguationSource = data.sourceType;
                }
            },
            error => {
                //console.log("error getDisambiguatedAffiliation(id)", id, error);
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
        
        if (this.editAffiliation != undefined && this.editAffiliation.orgDisambiguatedId != undefined) {delete this.editAffiliation.orgDisambiguatedId;
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
        //this.affiliationService.updateProfileAffiliation(aff);
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

    trackByFn(index, item): any {
        return index; // or item.id
    };

    unbindTypeahead(): void {
        $('#affiliationName').typeahead('destroy');
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
        //console.log('initi affiliation component');
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