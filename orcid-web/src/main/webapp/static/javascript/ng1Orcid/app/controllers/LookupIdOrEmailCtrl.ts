declare var $: any;
declare var colorbox: any;
declare var getBaseUri: any;

import * as angular from 'angular';
import {NgModule} from '@angular/core';

// This is the Angular 1 part of the module
export const lookupIdOrEmailCtrl = angular.module('orcidApp').controller(
    'lookupIdOrEmailCtrl',
    [
        '$compile', 
        '$scope',
        function findIdsCtrl(
            $compile,
            $scope
        ){
            $scope.emailIdsMap = {};
            $scope.idOrEmails = "";
            $scope.showSection = false;

            $scope.closeModal = function() {
                $.colorbox.close();
            };

            $scope.lookupIdOrEmails = function() {
                $.ajax({
                    url: getBaseUri()+'/admin-actions/lookup-id-or-emails.json',
                    type: 'POST',
                    dataType: 'text',
                    data: $scope.idOrEmails,
                    contentType: 'application/json;charset=UTF-8',
                    success: function(data){
                        $scope.$apply(function(){
                            console.log(data);
                            $scope.result = data;
                            $scope.idOrEmails='';
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
                    html : $compile($('#lookup-email-ids-modal').html())($scope),
                    scrolling: true,
                    onLoad: function() {
                        $('#cboxClose').remove();
                    }
                });

                setTimeout(function(){$.colorbox.resize({width:"575px"});},100);
            };

            $scope.toggleSection = function(){
                $scope.showSection = !$scope.showSection;
                $('#lookup_ids_section').toggle();
            };
        }
    ]
);

// This is the Angular 2 part of the module
@NgModule({})
export class lookupIdOrEmailCtrlNg2Module {}