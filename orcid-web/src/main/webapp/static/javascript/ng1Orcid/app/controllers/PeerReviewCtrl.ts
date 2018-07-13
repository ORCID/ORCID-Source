declare var $: any;
declare var ActSortState: any;
declare var colorbox: any;
declare var formColorBoxResize: any;
declare var getBaseUri: any;
declare var GroupedActivities: any;
declare var logAjaxError: any;
declare var openImportWizardUrl: any;
declare var orcidVar: any;
declare var workIdLinkJs: any;

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
            $scope.sortState = new ActSortState(GroupedActivities.PEER_REVIEW);
            $scope.wizardDescExpanded = {};
            $scope.workspaceSrvc = workspaceSrvc;
            
            $scope.addExternalIdentifier = function () {
                $scope.editPeerReview.externalIdentifiers.push({externalIdentifierId: {value: ""}, externalIdentifierType: {value: ""}, relationship: {value: "self"}, url: {value: ""}});
            };

            $scope.addAPeerReview = function() {
                if ($scope.addingPeerReview) {
                    return; 
                } 
                $scope.addingPeerReview = true;
                $scope.editPeerReview.errors.length = 0;
                peerReviewSrvc.postPeerReview($scope.editPeerReview,
                    function(data){             
                        if (data.errors.length == 0) {
                            $timeout(function(){
                                $scope.addingPeerReview = false;
                            });
                            $.colorbox.close();
                            $scope.peerReviewSrvc.loadPeerReviews(peerReviewSrvc.constants.access_type.USER);                    
                        } else {
                            $timeout(function(){
                                $scope.editPeerReview = data;
                                commonSrvc.copyErrorsLeft($scope.editPeerReview, data);
                                $scope.addingPeerReview = false;
                            });
                        }
                    },
                    function() {
                        // something bad is happening!
                        $scope.addingPeerReview = false;
                        console.log("error creating peer review");
                    }
                );
            };

            $scope.addPeerReviewModal = function(data){
                if (data == undefined) {
                    peerReviewSrvc.getBlankPeerReview(function(data) {
                        $scope.editPeerReview = data;
                        $timeout(function() {                    
                            $scope.showAddPeerReviewModal();
                            $scope.bindTypeaheadForOrgs();
                        });
                    });
                }else{
                    $scope.editPeerReview = data;
                    $scope.showAddPeerReviewModal();    
                }       
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
                    peerReviewSrvc.deletePeerReview(putCode);
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
                
                if (peerReview.subjectName){
                    $scope.fixedTitle = peerReview.subjectName.value;
                }
                else {
                    $scope.fixedTitle = '';
                }
                
                if($scope.fixedTitle.length > maxSize){
                    $scope.fixedTitle = $scope.fixedTitle.substring(0, maxSize) + '...';
                }
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

            $scope.openEditPeerReview = function(putCode){
                peerReviewSrvc.getEditable(putCode, function(data) {$scope.addPeerReviewModal(data);});        
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

            $scope.showAddPeerReviewModal = function(data){
                $scope.editTranslatedTitle = false;
                $.colorbox({
                    scrolling: true,
                    html: $compile($('#add-peer-review-modal').html())($scope),
                    onLoad: function() {$('#cboxClose').remove();},
                    // start the colorbox off with the correct width
                    width: formColorBoxResize(),
                    onComplete: function() {
                        // resize to insure content fits
                    },
                    onClosed: function() {
                        $scope.unbindTypeaheadForOrgs();
                        // $scope.closeAllMoreInfo();
                        $scope.peerReviewSrvc.loadPeerReviews(peerReviewSrvc.constants.access_type.USER);
                    }
                });
            };

            $scope.showDetailsMouseClick = function(groupId, $event){
                $event.stopPropagation();
                $scope.showDetails[groupId] = !$scope.showDetails[groupId];
            };

            $scope.showMoreDetails = function(putCode){
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

            $scope.showURLPopOver = function(id){
                $scope.displayURLPopOver[id] = true;
            };

            $scope.sort = function(key) {
                $scope.sortState.sortBy(key);
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
            $scope.peerReviewSrvc.loadPeerReviews(peerReviewSrvc.constants.access_type.USER);
            loadPeerReviewLinks();
        }
    ]
);

// This is the Angular 2 part of the module
@NgModule({})
export class PeerReviewCtrlNg2Module {}