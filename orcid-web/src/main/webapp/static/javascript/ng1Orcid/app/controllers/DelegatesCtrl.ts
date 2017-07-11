declare var $: any;
declare var colorbox: any;
declare var delegateEmail: any;
declare var getBaseUri: any;
declare var isEmail: any;
declare var orcidVar: any;
declare var orcidSearchUrlJs: any;

// Controller for delegate permissions that have been granted BY the current
// user
import * as angular from 'angular';
import {NgModule} from '@angular/core';

// This is the Angular 1 part of the module
export const DelegatesCtrl = angular.module('orcidApp').controller(
    'DelegatesCtrl',
    [
        '$compile', 
        '$scope', 
        function DelegatesCtrl(
            $compile,
            $scope
        ){
            $scope.effectiveUserOrcid = orcidVar.orcidId;
            $scope.input = {};
            $scope.input.rows = 10;
            $scope.input.start = 0;
            $scope.numFound = 0;
            $scope.realUserOrcid = orcidVar.realOrcidId;
            $scope.results = new Array();
            $scope.showInitLoader = true;
            $scope.showLoader = false;
            $scope.sort = {
                column: 'delegateSummary.creditName.content',
                descending: false
            };

            $scope.addDelegate = function() {
                var addDelegate = {
                    delegateToManage: null,
                    password: null
                };
                addDelegate.delegateToManage = $scope.delegateToAdd;
                addDelegate.password = $scope.password;
                $.ajax({
                    url: getBaseUri() + '/account/addDelegate.json',
                    type: 'POST',
                    data: angular.toJson(addDelegate),
                    contentType: 'application/json;charset=UTF-8',
                    success: function(data) {
                        if(data.errors.length === 0){
                            $scope.getDelegates();
                            $scope.results.splice($scope.delegateIdx, 1);
                            $scope.$apply();
                            $scope.closeModal();
                        }
                        else{
                            $scope.errors = data.errors;
                            $scope.$apply();
                        }
                    }
                }).fail(function() {
                    console.log("Error adding delegate.");
                });
            };

            $scope.addDelegateByEmail = function(delegateEmail) {
                var addDelegate = {
                    delegateEmail: null,
                    password: null
                };
                
                $scope.errors = [];
                
                addDelegate.delegateEmail = $scope.input.text;
                addDelegate.password = $scope.password;
                
                $.ajax({
                    url: $('body').data('baseurl') + 'account/addDelegateByEmail.json',
                    type: 'POST',
                    data: angular.toJson(addDelegate),
                    contentType: 'application/json;charset=UTF-8',
                    success: function(data) {
                        if(data.errors.length === 0){
                            $scope.getDelegates();
                            $scope.$apply();
                            $scope.closeModal();
                        }
                        else{
                            $scope.errors = data.errors;
                            $scope.$apply();
                        }
                    }
                }).fail(function() {
                    console.log("Error adding delegate.");
                });
            };

            $scope.areResults = function(){
                return $scope.numFound != 0;
            };
            
            $scope.isPasswordConfirmationRequired = orcidVar.isPasswordConfirmationRequired;

            $scope.changeSorting = function(column) {
                var sort = $scope.sort;
                if (sort.column === column) {
                    sort.descending = !sort.descending;
                } else {
                    sort.column = column;
                    sort.descending = false;
                }
            };

            $scope.closeModal = function() {
                $.colorbox.close();
            };

            $scope.concatPropertyValues = function(array, propertyName){
                if(typeof array === 'undefined'){
                    return '';
                }
                else{
                    return $.map(array, function(o){ return o[propertyName]; }).join(', ');
                }
            };

            $scope.confirmAddDelegate = function(delegateName, delegateId, delegateIdx){
                $scope.errors = [];
                $scope.delegateNameToAdd = delegateName;
                $scope.delegateToAdd = delegateId;
                $scope.delegateIdx = delegateIdx;
                $.colorbox({
                    html : $compile($('#confirm-add-delegate-modal').html())($scope),
                    transition: 'fade',
                    close: '',
                    onLoad: function() {
                        $('#cboxClose').remove();
                    },
                    onComplete: function() {$.colorbox.resize();},
                    scrolling: true
                });
            };

            $scope.confirmAddDelegateByEmail = function(emailSearchResult){
                $scope.errors = [];
                $scope.emailSearchResult = emailSearchResult;
                $.colorbox({
                    html : $compile($('#confirm-add-delegate-by-email-modal').html())($scope),
                    transition: 'fade',
                    close: '',
                    onLoad: function() {
                        $('#cboxClose').remove();
                    },
                    onComplete: function() {$.colorbox.resize();},
                    scrolling: true
                });
            };

            $scope.confirmRevoke = function(delegateName, delegateId) {
                $scope.errors = [];
                $scope.delegateNameToRevoke = delegateName;
                $scope.delegateToRevoke = delegateId;
                $.colorbox({
                    html : $compile($('#revoke-delegate-modal').html())($scope)

                });
                $.colorbox.resize();
            };

            $scope.getDelegates = function() {
                $.ajax({
                    url: getBaseUri() + '/account/delegates.json',
                    dataType: 'json',
                    success: function(data) {
                        $scope.delegatesByOrcid = {};
                        $scope.delegation = data;
                        if(data != null){
                            for(var i=0; i < data.length; i++){
                                var delegate = data[i];
                                $scope.delegatesByOrcid[delegate.receiverOrcid.value] = delegate;
                            }
                        }
                        $scope.showInitLoader = false;
                        $scope.$apply();
                    }
                }).fail(function() {
                    $scope.showInitLoader = false;
                    // something bad is happening!
                    console.log("error with delegates");
                });
            };

            $scope.getDisplayName = function(result){
                var personalDetails = result['orcid-profile']['orcid-bio']['personal-details'];
                var name = "";
                if(personalDetails != null) {
                    var creditName = personalDetails['credit-name'];
                    if(creditName != null){
                        return creditName.value;
                    }
                    name = personalDetails['given-names'].value;
                    if(personalDetails['family-name'] != null) {
                        name = name + ' ' + personalDetails['family-name'].value;
                    }
                }
                return name;
            };

            $scope.getResults = function(rows){
                $.ajax({
                    url: orcidSearchUrlJs.buildUrl($scope.input)+'&callback=?',
                    dataType: 'json',
                    headers: { Accept: 'application/json'},
                    success: function(data) {
                        var bottom = null;
                        var newSearchResults = null;
                        var newSearchResultsTop = null;
                        var resultsContainer = data['orcid-search-results'];
                        var showMoreButtonTop = null;
                        var tempResults = null;

                        $scope.numFound = resultsContainer['num-found'];
                        if(resultsContainer['orcid-search-result']){
                            $scope.numFound = resultsContainer['num-found'];
                            $scope.results = $scope.results.concat(resultsContainer['orcid-search-result']);
                        }
                        
                        tempResults = $scope.results;
                        for(var index = 0; index < tempResults.length; index ++) {
                            if($scope.results[index]['orcid-profile']['orcid-bio']['personal-details'] == null) {
                                $scope.results.splice(index, 1);
                            } 
                        }
                        $scope.numFound = $scope.results.length;
                        
                        if(!$scope.numFound){
                            $('#no-results-alert').fadeIn(1200);
                        }
                        
                        $scope.areMoreResults = $scope.numFound >= ($scope.start + $scope.rows);
                        $scope.showLoader = false;
                        $scope.$apply();
                        
                        newSearchResults = $('.new-search-result');
                        
                        if(newSearchResults.length > 0){
                            newSearchResults.fadeIn(1200);
                            newSearchResults.removeClass('new-search-result');
                            newSearchResultsTop = newSearchResults.offset().top;
                            showMoreButtonTop = $('#show-more-button-container').offset().top;
                            bottom = $(window).height();
                            if(showMoreButtonTop > bottom){
                                $('html, body').animate(
                                    {
                                        scrollTop: newSearchResultsTop
                                    },
                                    1000,
                                    'easeOutQuint'
                                );
                            }
                        }
                    }
                }).fail(function(){
                    // something bad is happening!
                    console.log("error doing search for delegates");
                });
            };

            $scope.getMoreResults = function(){
                $scope.showLoader = true;
                $scope.start += 10;
                $scope.getResults();
            };

            $scope.revoke = function () {
                var revokeDelegate = {
                    delegateToManage: null,
                    password: null
                };
                revokeDelegate.delegateToManage = $scope.delegateToRevoke;
                revokeDelegate.password = $scope.password;
                $.ajax({
                    url: getBaseUri() + '/account/revokeDelegate.json',
                    type: 'POST',
                    data:  angular.toJson(revokeDelegate),
                    contentType: 'application/json;charset=UTF-8',
                    success: function(data) {
                        if(data.errors.length === 0){
                            $scope.getDelegates();
                            $scope.$apply();
                            $scope.closeModal();
                        }
                        else{
                            $scope.errors = data.errors;
                            $scope.$apply();
                        }
                    }
                }).fail(function() {
                    // something bad is happening!
                    console.log("$DelegateCtrl.revoke() error");
                });
            };

            $scope.search = function(){
                $scope.results = new Array();
                $scope.showLoader = true;
                $('#no-results-alert').hide();
                if(isEmail($scope.input.text)){
                    $scope.numFound = 0;
                    $scope.start = 0;
                    $scope.areMoreResults = 0;
                    $scope.searchByEmail();
                }
                else{
                    $scope.getResults();
                }
            };

            $scope.searchByEmail = function(){
                $.ajax({
                    url: $('body').data('baseurl') + "manage/search-for-delegate-by-email/" + encodeURIComponent($scope.input.text) + '/',
                    dataType: 'json',
                    headers: { Accept: 'application/json'},
                    success: function(data) {
                        $scope.confirmAddDelegateByEmail(data);
                        $scope.showLoader = false;
                        $scope.$apply();
                    }
                }).fail(function(){
                    // something bad is happening!
                    console.log("error doing search for delegate by email");
                });

            };

            // init
            $scope.getDelegates();

        }
    ]
);

// This is the Angular 2 part of the module
@NgModule({})
export class DelegatesCtrlNg2Module {}