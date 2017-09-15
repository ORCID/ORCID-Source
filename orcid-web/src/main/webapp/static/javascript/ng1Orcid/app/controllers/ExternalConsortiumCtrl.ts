declare var $: any;
declare var colorbox: any;
declare var getBaseUri: any;
declare var om: any;
declare var orcidVar: any;

import * as angular from 'angular';
import {NgModule} from '@angular/core';

// This is the Angular 1 part of the module

export const externalConsortiumCtrl = angular.module('orcidApp').controller(
    'externalConsortiumCtrl',
    [
        '$compile', 
        '$scope',
        'commonSrvc', 
        'membersListSrvc', 
        'utilsService', 
        function manageConsortiumCtrl(
            $compile, 
            $scope, 
            commonSrvc,
            membersListSrvc,
            utilsService
        ) { 
            $scope.addContactDisabled = false;
            $scope.addSubMemberDisabled = false;
            $scope.addSubMemberShowLoader = false;
            $scope.contacts = null;
            $scope.effectiveUserOrcid = orcidVar.orcidId;
            $scope.input = {};
            $scope.memberDetails = null;
            $scope.membersListSrvc = membersListSrvc;
            $scope.newSubMember = {website: {errors: [], getRequiredMessage: null, required: false, value: ''}, name: {errors: [], getRequiredMessage: null, required: false, value: ''}};
            $scope.realUserOrcid = orcidVar.realOrcidId;
            $scope.showInitLoader = true;
            $scope.updateContactsDisabled = false;
            $scope.updateContactsShowLoader = false;
            $scope.updateMemberDetailsDisabled = false;
            $scope.updateMemberDetailsShowLoader = false;
            
            $scope.addContactByEmail = function(contactEmail) {
                let addContact = <any>{};
                
                $scope.addContactDisabled = true;
                $scope.errors = [];
                
                addContact.email = $scope.input.text;
                addContact.accountId = $scope.accountId;
                
                $.ajax({
                    url: getBaseUri() + '/self-service/add-contact-by-email.json',
                    type: 'POST',
                    data: angular.toJson(addContact),
                    contentType: 'application/json;charset=UTF-8',
                    success: function(data) {
                        $scope.getContacts();
                        $scope.addContactDisabled = false;
                        $scope.input.text = "";
                        $scope.$apply();
                        $scope.closeModal();
                    }
                }).fail(function() {
                    console.log("Error adding contact.");
                });
            };

            $scope.isValidClass = function (cur) {
                var valid;
                if (cur === undefined) {
                    return '';
                }
                valid = true;
                if (cur.required && (cur.value == null || cur.value.trim() == '')) {
                    valid = false;
                }
                if (cur.errors !== undefined && cur.errors.length > 0) {
                    valid = false;
                }
                return valid ? '' : 'text-error';
            };

            $scope.validateSubMemberField = function(fieldname) {
                 $.ajax({
                      url: getBaseUri()+'/self-service/validate-sub-member-' + fieldname + '.json',
                      contentType: 'application/json;charset=UTF-8',
                      type: 'POST',
                      dataType: 'json',
                      data: angular.toJson($scope.newSubMember),
                      success: function(data){
                        $scope.newSubMember = data
                        $scope.$apply();
                    }
                 }).fail(function(error) {
                      // something bad is happening!
                      console.log("Error validating new submember");
                 });
            };

            $scope.validateSubMember = function () {
                $scope.newSubMember.parentAccountId = $scope.accountId;
                $.ajax({
                    url: getBaseUri()+'/self-service/validate-sub-member.json',
                    contentType: 'application/json;charset=UTF-8',
                    type: 'POST',
                    dataType: 'json',
                    data: angular.toJson($scope.newSubMember),
                    success: function(data) {
                        $scope.newSubMember = data
                        $scope.$apply();                
                        if ($scope.newSubMember.errors == undefined || $scope.newSubMember.errors.length == 0) {
                            $scope.addSubMemberShowLoader = true;
                            $scope.checkExistingSubMember();
                        }
                    }
                }).fail(function() {
                    // something bad is happening!
                    console.log("validate submember error");
                });
            };

            $scope.checkExistingSubMember = function () {
                $scope.addSubMemberShowLoader = true;
                $.ajax({
                    url: getBaseUri()+'/self-service/check-existing-sub-member.json',
                    contentType: 'application/json;charset=UTF-8',
                    type: 'POST',
                    dataType: 'json',
                    data: angular.toJson($scope.newSubMember),
                    success: function(data) {
                        if(data){
                            $scope.newSubMemberExistingOrg = data
                            $scope.$apply();
                            console.log($scope.newSubMemberExistingOrg);
                            $scope.showExistingOrgColorBox();  
                        } else {
                            $scope.addSubMember();
                        }        
                    }
                }).fail(function(){
                    // something bad is happening!
                    console.log("error adding submember");
                    // continue to add submember
                    $scope.addSubMember();
                });
            };

            $scope.showExistingOrgColorBox = function () {
                $.colorbox({
                    html : $compile($('#add-sub-member-existing-org-modal').html())($scope),
                    escKey:false,
                    overlayClose:false,
                    transition: 'fade',
                    close: '',
                    scrolling: true
                    });
                $scope.$apply();
                $.colorbox.resize({width:"500px"});
            };

            $scope.addSubMember = function() {
                $scope.addSubMemberDisabled = true;
                $scope.addSubMemberShowLoader = true;
                $scope.newSubMember.parentAccountId = $scope.accountId;
                $.ajax({
                    url: getBaseUri() + '/self-service/add-sub-member.json',
                    type: 'POST',
                    data: angular.toJson($scope.newSubMember),
                    contentType: 'application/json;charset=UTF-8',
                    success: function(data) {
                        if(data.errors.length === 0){
                            $scope.getMemberDetails();
                            $scope.addSubMemberShowLoader = false;
                            $scope.addSubMemberDisabled = false;
                            $scope.newSubMember.name.value = "";
                            $scope.newSubMember.website.value = "";
                            $.colorbox.close();
                            $scope.$apply();
                        }
                        else{
                            $scope.errors = data.errors;
                            $scope.$apply();
                        }
                    }
                }).fail(function() {
                    console.log("Error adding submember.");
                });
            };

            $scope.buildOrcidUri = function(orcid){
                return orcidVar.baseUri + '/' + orcid;
            };

            $scope.closeModal = function() {
                 $.colorbox.close();
            };

            $scope.closeModalReload = function() {
                 $.colorbox.close();
                 window.location.reload();
            };

            $scope.confirmAddContactByEmail = function(emailSearchResult){
                $scope.errors = [];
                $scope.emailSearchResult = emailSearchResult;
                $.colorbox({
                    html : $compile($('#confirm-add-contact-by-email-modal').html())($scope),
                    transition: 'fade',
                    close: '',
                    onLoad: function() {
                        $('#cboxClose').remove();
                    },
                    onComplete: function() {$.colorbox.resize({width: "500px"});},
                    scrolling: true
                });
            };

            $scope.confirmRemoveSubMember = function(subMember) {
                $scope.subMemberToRemove = subMember;
                $.colorbox({
                    html : $compile($('#remove-sub-member-modal').html())($scope),
                    transition: 'fade',
                    close: '',
                    onLoad: function() {
                        $('#cboxClose').remove();
                    },
                    onComplete: function() {$.colorbox.resize();},
                    scrolling: true

                });
                $.colorbox.resize();
            };

            $scope.confirmRevoke = function(contact) {
                $scope.contactToRevoke = contact;
                $.colorbox({
                    html : $compile($('#revoke-contact-modal').html())($scope),
                    transition: 'fade',
                    close: '',
                    onLoad: function() {
                        $('#cboxClose').remove();
                    },
                    onComplete: function() {$.colorbox.resize();},
                    scrolling: true

                });
                $.colorbox.resize();
            };

            $scope.getAccountIdFromPath = function(){
                var pathname = window.location.pathname;
                var basepath = '/self-service/';
                return pathname.substring(pathname.indexOf(basepath) + basepath.length);
            };

            $scope.getContacts = function() {
                 $.ajax({
                      url: getBaseUri()+'/self-service/get-contacts.json?accountId=' + $scope.accountId,
                      type: 'GET',
                      dataType: 'json',
                      success: function(data){
                            $scope.contacts = data;
                            $scope.$apply();
                      }
                 }).fail(function(error) {
                      // something bad is happening!
                      console.log("Error getting the contacts");
                 });
            };

            $scope.getMemberDetails = function() {
                 $.ajax({
                      url: getBaseUri()+'/self-service/get-member-details.json?accountId=' + $scope.accountId,
                      type: 'GET',
                      dataType: 'json',
                      success: function(data){
                            $scope.memberDetails = data;
                            $scope.$apply();
                      }
                 }).fail(function(error) {
                      // something bad is happening!
                      console.log("Error getting the member details");
                 });
            };

            $scope.removeSubMember = function () {
                $scope.subMemberToRemove.parentAccountId = $scope.accountId;
                $.ajax({
                    url: getBaseUri() + '/self-service/remove-sub-member.json',
                    type: 'POST',
                    data:  angular.toJson($scope.subMemberToRemove),
                    contentType: 'application/json;charset=UTF-8',
                    success: function(data) {
                        $scope.getMemberDetails();
                        $scope.$apply();
                        $scope.closeModal();
                    }
                }).fail(function() {
                    // something bad is happening!
                    console.log("Problem removing sub member");
                });
            };

            $scope.revoke = function () {
                $scope.contactToRevoke.accountId = $scope.accountId;
                $.ajax({
                    url: getBaseUri() + '/self-service/remove-contact.json',
                    type: 'POST',
                    data:  angular.toJson($scope.contactToRevoke),
                    contentType: 'application/json;charset=UTF-8',
                    success: function(data) {
                        $scope.getContacts();
                        $scope.$apply();
                        $scope.closeModal();
                    }
                }).fail(function() {
                    // something bad is happening!
                    console.log("$ContactCtrl.revoke() error");
                });
            };

            $scope.search = function(){
                $('#invalid-email-alert').hide();
                if(utilsService.isEmail($scope.input.text)){
                    $scope.searchByEmail();
                }
                else{
                    $('#invalid-email-alert').show();
                }
            };

            $scope.searchByEmail = function(){
                $.ajax({
                    url: getBaseUri() + '/manage/search-for-delegate-by-email/' + encodeURIComponent($scope.input.text) + '/',
                    dataType: 'json',
                    headers: { Accept: 'application/json'},
                    success: function(data) {
                        $scope.confirmAddContactByEmail(data);
                        $scope.$apply();
                    }
                }).fail(function(){
                    // something bad is happening!
                    console.log("error doing search for contact by email");
                });

            };

            $scope.toggleFindConsortiumModal = function() {
                 $scope.showFindModal = !$scope.showFindModal;
            };

            $scope.update = function (contact) {
                var done = false;
                var nextContact = null;
                var other = null;
                if(contact.mainContact){
                    for(var i in $scope.memberDetails.contactsList){
                        other = $scope.memberDetails.contactsList[i];
                        if(other.id !== contact.id && other.mainContact){
                            other.mainContact = false;
                            other.role = null;
                            nextContact = contact;
                            $scope.updateCall(other, function() {  $scope.updateCall(nextContact); })
                            done = true;
                        }
                    }
                }
                if(!done){
                    $scope.updateCall(contact);
                }
                
            };

            $scope.updateCall = function(contact, nextFunction){
                $.ajax({
                    url: getBaseUri() + '/self-service/update-contact.json',
                    type: 'POST',
                    data:  angular.toJson(contact),
                    contentType: 'application/json;charset=UTF-8',
                    success: function(data) {
                        contact.role.id = data.role.id;
                        $scope.$apply();
                        if(nextFunction){
                            nextFunction();
                        }
                    }
                }).fail(function() {
                    // something bad is happening!
                    console.log("$ContactCtrl.update() error");
                });
            };

            $scope.validateMemberDetailsField = function(fieldname) {
                 $.ajax({
                      url: getBaseUri()+'/self-service/validate-member-details-' + fieldname + '.json',
                      contentType: 'application/json;charset=UTF-8',
                      type: 'POST',
                      dataType: 'json',
                      data: angular.toJson($scope.memberDetails),
                      success: function(data){
                        $scope.memberDetails = data
                        $scope.$apply();
                    }
                 }).fail(function(error) {
                      // something bad is happening!
                      console.log("Error validating member details field");
                 });
            };

            $scope.validateMemberDetails = function () {
                $.ajax({
                    url: getBaseUri()+'/self-service/validate-member-details.json',
                    contentType: 'application/json;charset=UTF-8',
                    type: 'POST',
                    dataType: 'json',
                    data: angular.toJson($scope.memberDetails),
                    success: function(data) {
                        $scope.memberDetails = data
                        $scope.$apply();                
                        if ($scope.memberDetails.errors == undefined || $scope.memberDetails.errors.length == 0) {
                            $scope.updateMemberDetailsShowLoader = true;
                            $scope.updateMemberDetails();
                        }
                    }
                }).fail(function() {
                    // something bad is happening!
                    console.log("validate member details error");
                });
            };

            $scope.updateMemberDetails = function() {
                $scope.updateMemberDetailsShowLoader = true;
                $scope.updateMemberDetailsDisabled = true;
                 $.ajax({
                      url: getBaseUri()+'/self-service/update-member-details.json',
                      contentType: 'application/json;charset=UTF-8',
                      type: 'POST',
                      dataType: 'json',
                      data: angular.toJson($scope.memberDetails),
                      success: function(data){
                            $scope.updateMemberDetailsShowLoader = false;
                            $scope.updateMemberDetailsDisabled = false;
                            $scope.$apply(function(){
                                 if(data.errors.length == 0){
                                      $scope.success_edit_member_message = om.get('manage_member.edit_member.success');
                                 } else {
                                      $scope.memberDetails = data;
                                 }
                            });
                      }
                 }).fail(function(error) {
                      // something bad is happening!
                      console.log("Error updating the consortium");
                 });
            };
  
            $scope.updateContacts = function() {
                $scope.updateContactsShowLoader = true;
                $scope.updateContactsDisabled = true;
                $scope.contacts.accountId = $scope.accountId;
                $.ajax({
                    url: getBaseUri()+'/self-service/update-contacts.json',
                    contentType: 'application/json;charset=UTF-8',
                    type: 'POST',
                    dataType: 'json',
                    data: angular.toJson($scope.contacts),
                    success: function(data){
                        $scope.updateContactsShowLoader = false;
                        $scope.updateContactsDisabled = false;
                        $scope.$apply(function(){
                            if(data.errors.length == 0){
                                $scope.success_edit_member_message = om.get('manage_member.edit_member.success');
                            }
                            $scope.contacts = data;
                        });
                    }
                }).fail(function(error) {
                    // something bad is happening!
                    console.log("Error updating the contacts");
                });
            };

            $scope.validateContacts = function() {
                 $.ajax({
                      url: getBaseUri()+'/self-service/validate-contacts.json',
                      contentType: 'application/json;charset=UTF-8',
                      type: 'POST',
                      dataType: 'json',
                      data: angular.toJson($scope.contacts),
                      success: function(data){
                            $scope.$apply(function(){
                                $scope.contacts.errors = data.errors;
                            });
                      }
                 }).fail(function(error) {
                      // something bad is happening!
                      console.log("Error validating the contacts");
                 });
            };

            // Init
            $scope.accountId = $scope.getAccountIdFromPath();
            $scope.getMemberDetails();
            $scope.getContacts();
            
        }
    ]
);

// This is the Angular 2 part of the module
@NgModule({})
export class externalConsortiumCtrlNg2Module {}