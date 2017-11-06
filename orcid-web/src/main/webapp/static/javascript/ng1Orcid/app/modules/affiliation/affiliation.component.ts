declare var $: any;
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
    from '../../shared/affiliationService.ts';

import { EmailService } 
    from '../../shared/emailService.ts';

import { ModalService } 
    from '../../shared/modalService.ts'; 

@Component({
    selector: 'affiliation-ng2',
    template:  scriptTmpl("affiliation-ng2-template")
})
export class AffiliationComponent implements AfterViewInit, OnDestroy, OnInit {
    private ngUnsubscribe: Subject<void> = new Subject<void>();

    /*
    emailSrvc: any;
    moreInfoCurKey: any;
    workspaceSrvc: any;
    */
    addingAffiliation: boolean;
    displayAffiliationExtIdPopOver: any;
    displayURLPopOver: any;
    editAffiliation: any;
    emails: any;
    moreInfo: any;
    privacyHelp: any;
    privacyHelpCurKey: any;
    showElement: any;

    constructor(
        private affiliationService: AffiliationService,
        private emailService: EmailService,
        private modalService: ModalService
    ) {
        /*
        this.emailSrvc = emailSrvc;
        this.moreInfoCurKey = null;
        this.workspaceSrvc = workspaceSrvc;
        */
        this.addingAffiliation = false;
        this.displayAffiliationExtIdPopOver = {};
        this.displayURLPopOver = {};
        this.editAffiliation = {};
        this.emails = {};
        this.moreInfo = {};
        this.privacyHelp = {};
        this.privacyHelpCurKey = null;
        this.showElement = {};
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
                console.log('this.editAffiliation response', this.editAffiliation);
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
                console.log('setBiographyFormError', error);
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

    showURLPopOver(id): void {
        this.displayURLPopOver[id] = true;
    };

    showAffiliationExtIdPopOver(id): void{
        this.displayAffiliationExtIdPopOver[id] = true;
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

    unbindTypeahead(): void {
        $('#affiliationName').typeahead('destroy');
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
                console.log('getEmails', error);
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
        //affiliationService.getAffiliations('affiliations/affiliationIds.json');
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

            

            $scope.addAffiliationModal = function(type, affiliation){
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
                            console.log("Error fetching affiliation: ", $scope.editAffiliation.affiliationType.value,  e);
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
            };

            

            

            $scope.deleteAff = function(delAff) {
                affiliationsSrvc.deleteAffiliation(delAff);
                $.colorbox.close();
            };

            $scope.deleteAffiliation = function(aff) {
                var maxSize = 100;
                
                $scope.deleAff = aff;

                if (aff.affiliationName && aff.affiliationName.value){
                    $scope.fixedTitle = aff.affiliationName.value;
                }
                else {
                    $scope.fixedTitle = '';
                }

                if($scope.fixedTitle.length > maxSize){
                    $scope.fixedTitle = $scope.fixedTitle.substring(0, maxSize) + '...';
                }

                $.colorbox({
                    html : $compile($('#delete-affiliation-modal').html())($scope),
                    onComplete: function() {
                        $.colorbox.resize();
                    }
                });
            };

            $scope.getDisambiguatedAffiliation = function(id) {
                $.ajax({
                    url: getBaseUri() + '/affiliations/disambiguated/id/' + id,
                    dataType: 'json',
                    type: 'GET',
                    success: function(data) {
                        if (data != null) {
                            $scope.disambiguatedAffiliation = data;
                            $scope.editAffiliation.orgDisambiguatedId.value = id;
                            $scope.editAffiliation.disambiguatedAffiliationSourceId = data.sourceId;
                            $scope.editAffiliation.disambiguationSource = data.sourceType;
                            $scope.$apply();
                        }
                    }
                }).fail(function(){
                    console.log("error getDisambiguatedAffiliation(id)");
                });
            };

            


            

            // remove once grouping is live
            $scope.moreInfoMouseEnter = function(key, $event) {
                $event.stopPropagation();
                if ( document.documentElement.className.indexOf('no-touch') > -1 ) {
                    if ($scope.moreInfoCurKey != null
                        && $scope.moreInfoCurKey != key) {
                        $scope.privacyHelp[$scope.moreInfoCurKey]=false;
                    }
                    $scope.moreInfoCurKey = key;
                    $scope.moreInfo[key]=true;
                }
            };

            $scope.openEditAffiliation = function(affiliation) {
                $scope.addAffiliationModal(affiliation.affiliationType.value, affiliation);
            };

            $scope.removeDisambiguatedAffiliation = function() {
                $scope.bindTypeahead();
                
                if ($scope.disambiguatedAffiliation != undefined) {
                    delete $scope.disambiguatedAffiliation;
                }
                
                if ($scope.editAffiliation != undefined && $scope.editAffiliation.disambiguatedAffiliationSourceId != undefined) {
                    delete $scope.editAffiliation.disambiguatedAffiliationSourceId;
                }
                
                if ($scope.editAffiliation != undefined && $scope.editAffiliation.orgDisambiguatedId != undefined) {delete $scope.editAffiliation.orgDisambiguatedId;
                }
            };

            $scope.selectAffiliation = function(datum) {
                if (datum != undefined && datum != null) {
                    $scope.editAffiliation.affiliationName.value = datum.value;
                    $scope.editAffiliation.city.value = datum.city;
                    
                    if(datum.city) {
                        $scope.editAffiliation.city.errors = [];
                    }

                    $scope.editAffiliation.region.value = datum.region;
                    
                    if(datum.region){
                        $scope.editAffiliation.region.errors = [];
                    }
                    
                    if(datum.country != undefined && datum.country != null) {
                        $scope.editAffiliation.country.value = datum.country;
                        $scope.editAffiliation.country.errors = [];
                    }

                    if (datum.disambiguatedAffiliationIdentifier != undefined && datum.disambiguatedAffiliationIdentifier != null) {
                        $scope.getDisambiguatedAffiliation(datum.disambiguatedAffiliationIdentifier);
                        $scope.unbindTypeahead();
                    }
                }
            };

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
                    console.log("RegistrationCtrl.serverValidate() error");
                });
            };

            $scope.setAddAffiliationPrivacy = function(priv, $event) {
                $event.preventDefault();
                $scope.editAffiliation.visibility.visibility = priv;
            };

            $scope.setPrivacy = function(aff, priv, $event) {
                $event.preventDefault();
                aff.visibility.visibility = priv;
                affiliationsSrvc.updateProfileAffiliation(aff);
            };

            $scope.showAddModal = function(){
                var numOfResults = 25;
                $.colorbox({
                    html: $compile($('#add-affiliation-modal').html())($scope),            
                    onComplete: function() {
                        // resize to insure content fits
                        formColorBoxResize();
                        $scope.bindTypeahead();
                    }
                });
            };

            $scope.showDetailsMouseClick = function(group, $event) {
                $event.stopPropagation();
                $scope.moreInfo[group.groupId] = !$scope.moreInfo[group.groupId];
            };

            $scope.showTooltip = function (element){        
                $scope.showElement[element] = true;
            };

            $scope.sortState = new ActSortState(GroupedActivities.AFFILIATION);

            $scope.sort = function(key) {       
                $scope.sortState.sortBy(key);
            };

            // remove once grouping is live
            $scope.toggleClickMoreInfo = function(key) {
                if ( document.documentElement.className.indexOf('no-touch') == -1 ) {
                    if ($scope.moreInfoCurKey != null
                            && $scope.moreInfoCurKey != key) {
                        $scope.moreInfo[$scope.moreInfoCurKey]=false;
                    }
                    $scope.moreInfoCurKey = key;
                    $scope.moreInfo[key]=!$scope.moreInfo[key];
                }
            };

            
        }
    ]

    */