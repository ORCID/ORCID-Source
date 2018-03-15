declare var $: any;
declare var orcidSearchUrlJs;
declare var orcidVar: any;

import * as angular from 'angular';
import {NgModule} from '@angular/core';

// This is the Angular 1 part of the module
export const SearchCtrlV2 = angular.module('orcidApp').controller(
    'SearchCtrlV2',
    [
        '$compile', 
        '$scope', 
        function (
            $compile,
            $scope
        ){
            $scope.hasErrors = false;
            $scope.input = {};
            $scope.input.rows = 10;
            $scope.input.start = 0;
            $scope.input.text = $('#SearchCtrl').data('search-query');
            $scope.numFound = 0;
            $scope.results = new Array();
            $scope.resultsShowing = 0;

            $scope.areResults = function(){
                return $scope.results.length > 0;
            };

            $scope.concatPropertyValues = function(array, propertyName){
                if(typeof array === 'undefined'){
                    return '';
                }
                else{
                    return $.map(array, function(o){ return o[propertyName]; }).join(', ');
                }
            };

            $scope.getFirstResults = function(){
                $('#no-results-alert').hide();
                $scope.results = new Array();
                $scope.numFound = 0;
                $scope.input.start = 0;
                $scope.input.rows = 10;
                $scope.areMoreResults = false;
                if($scope.isValid()){
                    $scope.hasErrors = false;
                    $('#ajax-loader-search').show();
                    $scope.getResults();
                }
                else{
                    $scope.hasErrors = true;
                }
            };

            $scope.getMoreResults = function(){
                $('#ajax-loader-show-more').show();
                $scope.input.start += 10;
                $scope.getResults();
            };

            $scope.getResults = function(){
                $.ajax({
                    url: orcidSearchUrlJs.buildUrl($scope.input),
                    dataType: 'json',
                    headers: { Accept: 'application/json'},
                    success: function(data) {
                        var bottom = null;
                        var newSearchResults = null;
                        var newSearchResultsTop = null;
                        var showMoreButtonTop = null;
                        $('#ajax-loader-search').hide();
                        $('#ajax-loader-show-more').hide();
                        var orcidList = data['result'];
                        
                        $scope.numFound = data['num-found'];

                        $scope.results = $scope.results.concat(orcidList); 
                        
                        if(!$scope.numFound){
                            $('#no-results-alert').fadeIn(1200);
                        }
                        
                        $scope.areMoreResults = $scope.numFound > ($scope.input.start + $scope.input.rows);
                        
                        //if less than 10 results, show total number found
                        if($scope.numFound && $scope.numFound <= $scope.input.rows){
                            $scope.resultsShowing = $scope.numFound;
                        }

                        //if more than 10 results increment num found by 10
                        if($scope.numFound && $scope.numFound > $scope.input.rows){
                            if($scope.numFound > ($scope.input.start + $scope.input.rows)){
                                $scope.resultsShowing = $scope.input.start + $scope.input.rows;
                            } else {
                                $scope.resultsShowing = ($scope.input.start + $scope.input.rows) - ($scope.input.rows - ($scope.numFound % $scope.input.rows));
                            }
                        }

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
                    console.log("error doing search");
                    $('#ajax-loader-search').hide();
                    $('#search-error-alert').fadeIn(1200);

                });
            };

            $scope.getNames = function(result){
                if(!result['namesRequestSent']){
                    result['namesRequestSent'] = true;
                    var name="";
                    var orcid = result['orcid-identifier'].path;
                    var url = orcidVar.pubBaseUri + '/v2.1/' + orcid + '/person';
                    $.ajax({
                        url: url,
                        dataType: 'json',
                        headers: { Accept: 'application/json'},
                        success: function(data) {
                            if (data['name']['given-names']){
                                result['given-names'] = data['name']['given-names']['value'];
                            }
                            if(data['name']['family-name']){
                                result['family-name'] = data['name']['family-name']['value'];
                            }
                            if(data['other-names']['other-name']) {
                                result['other-name'] = data['other-names']['other-name'];
                            }
                        }
                    }).fail(function(){
                        // something bad is happening!
                        console.log("error getting name for " + orcid);
                    });  
                } 

                if (result['given-names']) {
                    name = result['given-names'];
                } else {
                    name = "";
                }             
                return name; 
            };

            $scope.getAffiliations = function(result){
                if(!result['affiliationsRequestSent']){
                    result['affiliationsRequestSent'] = true;
                    result['affiliations'] = [];
                    var orcid = result['orcid-identifier'].path;
                    var url = orcidVar.pubBaseUri + '/v2.1/' + orcid + '/activities';
                    $.ajax({
                        url: url,
                        dataType: 'json',
                        headers: { Accept: 'application/json'},
                        success: function(data) {
                            console.log(data.employments['employment-summary'].length);
                            console.log(data.educations['education-summary'].length);
                            if(data.employments){
                                for(var i in data.employments['employment-summary']){
                                    if (result['affiliations'].indexOf(data.employments['employment-summary'][i]['organization']['name']) < 0){
                                        result['affiliations'].push(data.employments['employment-summary'][i]['organization']['name']);
                                    }
                                }
                            }
                            if(data.educations){
                                for(var i in data.educations['education-summary']){
                                    if (result['affiliations'].indexOf(data.educations['education-summary'][i]['organization']['name']) < 0){
                                        result['affiliations'].push(data.educations['education-summary'][i]['organization']['name']);
                                    }
                                }
                            }
                        }
                    }).fail(function(){
                        // something bad is happening!
                        console.log("error getting name for " + orcid);
                    });  
                } 
                if(result['affiliations'].length > 0){
                    return result['affiliations'].join(", "); 
                } else {
                    return "";
                }
            };

            $scope.isValid = function(){
                return orcidSearchUrlJs.isValidInput($scope.input);
            };
            
            $scope.isValidOrcidId = function(){
                if(typeof $scope.input.text === 'undefined' || $scope.input.text === null || $scope.input.text === '' || orcidSearchUrlJs.isValidOrcidId($scope.input.text)){
                    return true;
                }
                return false;
            }

            // init
            if(typeof $scope.input.text !== 'undefined'){
                $('#ajax-loader-search').show();
                $scope.getResults();
            }
        }
    ]
);

// This is the Angular 2 part of the module
@NgModule({})
export class SearchCtrlV2Ng2Module {}