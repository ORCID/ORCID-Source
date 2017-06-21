declare var $: any;
declare var ActSortState: any;
declare var colorbox: any;
declare var formColorBoxResize: any;
declare var getBaseUri: any;
declare var GroupedActivities: any;

import * as angular from 'angular';
import {NgModule} from '@angular/core';

// This is the Angular 1 part of the module
export const AffiliationCtrl = angular.module('orcidApp').controller(
    'AffiliationCtrl', 
    [
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
            $scope.affiliationsSrvc = affiliationsSrvc;
            $scope.editAffiliation;
            $scope.emailSrvc = emailSrvc;
            $scope.moreInfo = {};
            $scope.moreInfoCurKey = null;
            $scope.privacyHelp = {};
            $scope.privacyHelpCurKey = null;
            $scope.showElement = {};
            $scope.workspaceSrvc = workspaceSrvc;

            // init
            affiliationsSrvc.getAffiliations('affiliations/affiliationIds.json');

            // ///////////////////// Begin of verified email logic for work
            var configuration = initialConfigService.getInitialConfiguration();
            var emailVerified = false;
            var emails = {};


            var showEmailVerificationModal = function(){
                $rootScope.$broadcast('emailVerifiedObj', {flag: emailVerified, emails: emails});
            };
            
            $scope.emailSrvc.getEmails(
                function(data) {
                    emails = data.emails;
                    if( $scope.emailSrvc.getEmailPrimary().verified == true ) {
                        emailVerified = true;
                    }
                }
            );
            // ///////////////////// End of verified email logic for work

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

            $scope.addAffiliation = function(){
                if ($scope.addingAffiliation) {
                    return; // don't process if adding affiliation
                }

                $scope.addingAffiliation = true;
                $scope.editAffiliation.errors.length = 0;
                
                $.ajax({
                    url: getBaseUri() + '/affiliations/affiliation.json',
                    contentType: 'application/json;charset=UTF-8',
                    data:  angular.toJson($scope.editAffiliation),
                    dataType: 'json',
                    type: 'POST',
                    success: function(data) {
                        if (data.errors.length == 0){
                            $.colorbox.close();
                            $scope.addingAffiliation = false;
                            affiliationsSrvc.getAffiliations('affiliations/affiliationIds.json');
                        } else {
                            $scope.editAffiliation = data;
                            commonSrvc.copyErrorsLeft($scope.editAffiliation, data);
                            $scope.addingAffiliation = false;
                            $scope.$apply();
                        }
                    }
                }).fail(function(){
                    // something bad is happening!
                    $scope.addingAffiliation = false;
                    console.log("error adding affiliations");
                });
            };

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

            $scope.bindTypeahead = function () {
                var numOfResults = 100;

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
                    $scope.selectAffiliation(datum);
                    $scope.$apply();
                });
            };

            $scope.closeModal = function() {
                $.colorbox.close();
            };

            $scope.closeMoreInfo = function(key) {
                $scope.moreInfo[key]=false;
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

            $scope.hideTooltip = function (element){        
                $scope.showElement[element] = false;
            };

            $scope.isValidClass = function (cur) {
                var valid = true;

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

            $scope.showDetailsMouseClick = function(key, $event) {
                $event.stopPropagation();
                $scope.moreInfo[key]=!$scope.moreInfo[key];
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

            $scope.toggleClickPrivacyHelp = function(key) {
                if ( document.documentElement.className.indexOf('no-touch') == -1 ) {
                    if ($scope.privacyHelpCurKey != null
                            && $scope.privacyHelpCurKey != key) {
                        $scope.privacyHelp[$scope.privacyHelpCurKey]=false;
                    }
                    $scope.privacyHelpCurKey = key;
                    $scope.privacyHelp[key]=!$scope.privacyHelp[key];
                }

            };

            $scope.unbindTypeahead = function () {
                $('#affiliationName').typeahead('destroy');
            };
        }
    ]
);

// This is the Angular 2 part of the module
@NgModule({})
export class AffiliationCtrlNg2Module {}