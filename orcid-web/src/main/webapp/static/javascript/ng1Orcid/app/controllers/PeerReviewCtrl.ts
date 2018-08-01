declare var $: any;
declare var ActSortState: any;
declare var colorbox: any;
declare var formColorBoxResize: any;
declare var getBaseUri: any;
declare var logAjaxError: any;
declare var openImportWizardUrl: any;
declare var orcidVar: any;
declare var workIdLinkJs: any;
declare var GroupedActivities: any;

import * as angular from 'angular';
import {NgModule} from '@angular/core';

// This is the Angular 1 part of the module

export const PeerReviewCtrl = angular.module('orcidApp').controller(
    'PeerReviewCtrl', 
    [
        '$compile', 
        '$filter', 
        '$scope', 
        '$timeout',
        'commonSrvc', 
        'peerReviewSrvc', 
        'workspaceSrvc', 
        function (
            $compile, 
            $filter, 
            $scope, 
            $timeout,
            commonSrvc, 
            peerReviewSrvc,
            workspaceSrvc
        ){
            $scope.addingPeerReview = false;
            $scope.disambiguatedOrganization = null;
            $scope.displayURLPopOver = {};
            $scope.editPeerReview = null;
            $scope.editSources = {};
            $scope.editTranslatedTitle = false;
            $scope.noLinkFlag = true;
            $scope.peerReviewImportWizard = false;
            $scope.peerReviewSrvc = peerReviewSrvc;
            $scope.showDetails = {};
            $scope.showElement = {};
            $scope.sortHideOption = true;
            $scope.showPeerReviewDetails = new Array();
            $scope.wizardDescExpanded = {};
            $scope.workspaceSrvc = workspaceSrvc;
            $scope.sortState = new ActSortState(GroupedActivities.PEER_REVIEW);
            
            $scope.addExternalIdentifier = function () {
                $scope.editPeerReview.externalIdentifiers.push({externalIdentifierId: {value: ""}, externalIdentifierType: {value: ""}, relationship: {value: "self"}, url: {value: ""}});
            };

            $scope.addSubjectExternalIdentifier = function () {
                $scope.editPeerReview.subjectForm.workExternalIdentifiers.push({externalIdentifierId: {value: ""}, externalIdentifierType: {value: ""}, relationship: {value: "self"}, url: {value: ""}});
            };

            $scope.bindTypeaheadForOrgs = function () {
                var numOfResults = 100;
                (<any>$("#organizationName")).typeahead({
                    name: 'organizationName',
                    limit: numOfResults,
                    remote: {
                        replace: function () {
                            var q = getBaseUri()+'/peer-reviews/disambiguated/name/';
                            if ($('#organizationName').val()) {
                                q += encodeURIComponent($('#organizationName').val());
                            }
                            q += '?limit=' + numOfResults;
                            return q;
                        }
                    },
                    template: function (datum) {
                        var forDisplay =
                            '<span style=\'white-space: nowrap; font-weight: bold;\'>' + datum.value
                            + '</span>'
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
                $("#organizationName").bind("typeahead:selected", function(obj, datum) {
                    $timeout(function(){
                        $scope.selectOrganization(datum);
                    });
                });
            };

            $scope.closeModal = function() {
                $.colorbox.close();
            };

            $scope.deleteByPutCode = function(putCode, deleteGroup) {
                if (deleteGroup){
                   peerReviewSrvc.deleteGroupPeerReview(putCode);
                }
                else{
                    peerReviewSrvc.deletePeerReview(putCode, $scope.sortState.reverseKey['groupName']);
                }
                $.colorbox.close();
            };

            $scope.deleteExternalIdentifier = function(obj) {
                var index = $scope.editPeerReview.externalIdentifiers.indexOf(obj);
                $scope.editPeerReview.externalIdentifiers.splice(index,1);
            };

            $scope.deletePeerReviewConfirm = function(putCode, deleteGroup) {
                var peerReview = peerReviewSrvc.getPeerReview(putCode);
                var maxSize = 100;
                
                $scope.deletePutCode = putCode;
                $scope.deleteGroup = deleteGroup;
                
                $.colorbox({
                    html : $compile($('#delete-peer-review-modal').html())($scope),
                    onComplete: function() {$.colorbox.resize();}
                });
            };

            $scope.deleteSubjectExternalIdentifier = function(obj) {
                var index = $scope.editPeerReview.subjectForm.workExternalIdentifiers.indexOf(obj);
                $scope.editPeerReview.subjectForm.workExternalIdentifiers.splice(index,1);        
            };

            $scope.fillUrl = function(extId) {
                var url = null;
                if(extId != null) {
                    url = workIdLinkJs.getLink(extId.externalIdentifierId.value, extId.externalIdentifierType.value);           
                    if(extId.url == null) {
                        extId.url = {value:""};
                    }
                    extId.url.value=url;
                }
            };

            $scope.getDisambiguatedOrganization = function(id) {
                $.ajax({
                    url: getBaseUri() + '/peer-reviews/disambiguated/id/' + id,
                    dataType: 'json',
                    type: 'GET',
                    success: function(data) {
                        $timeout(function(){
                            if (data != null) {
                                $scope.disambiguatedOrganization = data;
                                $scope.editPeerReview.disambiguatedOrganizationSourceId = data.sourceId;
                                $scope.editPeerReview.disambiguationSource = data.sourceType;
                            }
                        }); 
                    }
                }).fail(function(){
                    console.log("error getDisambiguatedOrganization(id)");
                });
            };

            $scope.hideMoreDetails = function(putCode){
                $scope.showPeerReviewDetails.length = 0;
                $scope.showPeerReviewDetails[putCode] = false;
            };

            $scope.hideTooltip = function (element){        
                $scope.showElement[element] = false;
            };

            $scope.hideURLPopOver = function(id){
                $scope.displayURLPopOver[id] = false;
            };

            $scope.moreInfoActive = function(groupID){
                if ($scope.moreInfo[groupID] == true || $scope.moreInfo[groupID] != null) {
                    return 'truncate-anchor';
                }
            };

            $scope.removeDisambiguatedOrganization = function() {
                $scope.bindTypeaheadForOrgs();
                if ($scope.disambiguatedOrganization != undefined) {
                    delete $scope.disambiguatedOrganization;
                }
                if ($scope.editPeerReview != undefined && $scope.editPeerReview.disambiguatedOrganizationSourceId != undefined) {
                    delete $scope.editPeerReview.disambiguatedOrganizationSourceId;
                }
            };

            $scope.openImportWizardUrlFilter = function(url, client) {
                url = url + '?client_id='+client.id+'&response_type=code&scope='+client.scopes+'&redirect_uri='+client.redirectUri;
                openImportWizardUrl(url);
            };

            $scope.selectOrganization = function(datum) {
                if (datum != undefined && datum != null) {
                    $scope.editPeerReview.orgName.value = datum.value;
                    if(datum.value){
                        $scope.editPeerReview.orgName.errors = [];
                    }
                    $scope.editPeerReview.city.value = datum.city;
                    if(datum.city){
                        $scope.editPeerReview.city.errors = [];
                    }
                    if(datum.region){
                        $scope.editPeerReview.region.value = datum.region;
                    }

                    if(datum.country != undefined && datum.country != null) {
                        $scope.editPeerReview.country.value = datum.country;
                        $scope.editPeerReview.country.errors = [];
                    }

                    if (datum.disambiguatedOrganizationIdentifier != undefined 
                        && datum.disambiguatedOrganizationIdentifier != null) {
                        $scope.getDisambiguatedOrganization(datum.disambiguatedOrganizationIdentifier);
                    }
                }
            };

            $scope.serverValidate = function (relativePath) {
                $.ajax({
                    url: getBaseUri() + '/' + relativePath,
                    type: 'POST',
                    data:  angular.toJson($scope.editPeerReview),
                    contentType: 'application/json;charset=UTF-8',
                    dataType: 'json',
                    success: function(data) {
                        $timeout(function(){
                            commonSrvc.copyErrorsLeft($scope.editPeerReview, data);    
                        });
                    }
                }).fail(function() {
                    // something bad is happening!
                    console.log("PeerReviewCtrl.serverValidate() error");
                });
            };

            $scope.showDetailsMouseClick = function(groupId, $event){
                $event.stopPropagation();
                $scope.showDetails[groupId] = !$scope.showDetails[groupId];
            };

            $scope.showMoreDetails = function(putCode) {
                peerReviewSrvc.fetchPeerReviewDetails(putCode);
                $scope.showPeerReviewDetails.length = 0;
                $scope.showPeerReviewDetails[putCode] = true;   
            };

            $scope.showPeerReviewImportWizard = function(){
                if(!$scope.peerReviewImportWizard) {
                    loadPeerReviewLinks();
                }
                $scope.peerReviewImportWizard = !$scope.peerReviewImportWizard;
            };

            $scope.showTooltip = function (element){
                $scope.showElement[element] = true;
            };
            
            $scope.showSources = function(groupId) {
                $scope.editSources[groupId] = true;
            };
            
            $scope.hideSources = function(id) {
                $scope.editSources[id] = false;
            };

            $scope.showURLPopOver = function(id){
                $scope.displayURLPopOver[id] = true;
            };

            $scope.sort = function(key) {
                $scope.sortState.sortBy(key);
                peerReviewSrvc.getPeerReviews(!$scope.sortState.reverseKey[key]);
            };
            
            $scope.toggleTranslatedTitleModal = function(){
                $scope.editTranslatedTitle = !$scope.editTranslatedTitle;
                $('#translatedTitle').toggle();
                $.colorbox.resize();
            };

            $scope.toggleWizardDesc = function(id){
                $scope.wizardDescExpanded[id] = !$scope.wizardDescExpanded[id];
            };
            
            $scope.unbindTypeaheadForOrgs = function () {
                (<any>$('#organizationName')).typeahead('destroy');
            };

            $scope.userIsSource = function(peerReview) {
                if (peerReview.source == orcidVar.orcidId){
                    return true;
                }
                return false;
            };

            function loadPeerReviewLinks() {
                $.ajax({
                    url: getBaseUri() + '/workspace/retrieve-peer-review-import-wizards.json',
                    type: 'GET',
                    contentType: 'application/json;charset=UTF-8',
                    dataType: 'json',
                    success: function(data) {
						$timeout(function(){
	                        $scope.peerReviewImportWizardList = data;
	                        if(data == null || data.length == 0) {
	                            $scope.noLinkFlag = false;
	                        }
                        });                        
                    }
                }).fail(function(e) {
                    // something bad is happening!
                    console.log("PeerReviewImportWizardError");
                    logAjaxError(e);
                });
            }
            
            // Init
            $scope.peerReviewSrvc.getPeerReviews(true);
            loadPeerReviewLinks();
        }
    ]
);

// This is the Angular 2 part of the module
@NgModule({})
export class PeerReviewCtrlNg2Module {}