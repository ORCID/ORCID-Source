declare var $: any;
declare var getBaseUri: any;
declare var logAjaxError: any;
declare var om: any;

// Controller for delegate permissions that have been granted TO the current user
import * as angular from 'angular';
import {NgModule} from '@angular/core';

// This is the Angular 1 part of the module
export const DelegatorsCtrl = angular.module('orcidApp').controller(
    'DelegatorsCtrl',
    [
        '$compile', 
        '$scope', 
        function (
            $compile,
            $scope 
        ){
            $scope.changeSorting = function(column) {
                var sort = $scope.sort;
                if (sort.column === column) {
                    sort.descending = !sort.descending;
                } else {
                    sort.column = column;
                    sort.descending = false;
                }
            };

            $scope.getDelegators = function() {
                $.ajax({
                    url: getBaseUri() + '/delegators/delegators-and-me.json',
                    dataType: 'json',
                    success: function(data) {
                        $scope.delegators = data.delegators;
                        $scope.$apply();
                    }
                }).fail(function(e) {
                    // something bad is happening!
                    console.log("error with delegates");
                    logAjaxError(e);
                });
            };

            $scope.selectDelegator = function(datum) {
                window.location.href = getBaseUri() + '/switch-user?j_username=' + datum.orcid;
            };

            $scope.sort = {
                column: 'delegateSummary.creditName.content',
                descending: false
            };

            (<any>$("#delegatorsSearch")).typeahead({
                name: 'delegatorsSearch',
                remote: {
                    url: getBaseUri()+'/delegators/search-for-data/%QUERY?limit=' + 10
                },
                template: function (datum) {
                    var forDisplay;
                    if(datum.noResults){
                        forDisplay = "<span class=\'no-delegator-matches\'>" + om.get('delegators.nomatches') + "</span>";
                    }
                    else{
                        forDisplay =
                            '<span style=\'white-space: nowrap; font-weight: bold;\'>' + datum.value + '</span>'
                            +'<span style=\'font-size: 80%;\'> (' + datum.orcid + ')</span>';
                    }
                    return forDisplay;
                }
            });

            $("#delegatorsSearch").bind(
                "typeahead:selected", 
                function(datum) {
                    if(!(<any>(datum)).noResults){
                        $scope.selectDelegator(datum);
                    }
                    $scope.$apply();
                    return true;
                }
            );

            // init
            $scope.getDelegators();
        }
    ]
);

// This is the Angular 2 part of the module
@NgModule({})
export class DelegatorsCtrlNg2Module {}