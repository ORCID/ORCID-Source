declare var $: any;
declare var colorbox: any;
declare var getBaseUri: any;

import * as angular from 'angular';
import {NgModule} from '@angular/core';

// This is the Angular 1 part of the module
export const findIdsCtrl = angular.module('orcidApp').controller(
    'findIdsCtrl',
    [
        '$compile', 
        '$scope',
        function findIdsCtrl(
            $compile,
            $scope
        ){
            $scope.emailIdsMap = {};
            $scope.emails = "";
            $scope.showSection = false;

            $scope.closeModal = function() {
                $.colorbox.close();
            };

            $scope.findIds = function() {
                $.ajax({
                    url: getBaseUri()+'/admin-actions/find-id.json',
                    type: 'POST',
                    dataType: 'json',
                    data: $scope.emails,
                    contentType: 'application/json;charset=UTF-8',
                    success: function(data){
                        $scope.$apply(function(){
                            if(!$.isEmptyObject(data)) {
                                $scope.profileList = data;
                            } else {
                                $scope.profileList = null;
                            }
                            $scope.emails='';
                            $scope.showEmailIdsModal();
                        });
                    }
                }).fail(function(error) {
                    // something bad is happening!
                    console.log("Error deprecating the account");
                });
            };

            $scope.showEmailIdsModal = function() {
                $.colorbox({
                    html : $compile($('#email-ids-modal').html())($scope),
                    scrolling: true,
                    onLoad: function() {
                        $('#cboxClose').remove();
                    }
                });

                setTimeout(function(){$.colorbox.resize({width:"575px"});},100);
            };

            $scope.toggleSection = function(){
                $scope.showSection = !$scope.showSection;
                $('#find_ids_section').toggle();
            };
        }
    ]
);

// This is the Angular 2 part of the module
@NgModule({})
export class findIdsCtrlNg2Module {}