//Migrated - Not in use?

declare var $: any;
declare var colorbox: any;
declare var getBaseUri: any;
declare var orcidGA: any;

import * as angular from 'angular';
import {NgModule} from '@angular/core';

// This is the Angular 1 part of the module

export const DeactivateAccountCtrl = angular.module('orcidApp').controller(
    'DeactivateAccountCtrl', 
    [
        '$compile', 
        '$scope', 
        function (
            $compile,
            $scope
        ) {
            $scope.closeModal = function() {
                $.colorbox.close();
            };

            $scope.sendDeactivateEmail = function() {
                orcidGA.gaPush(['send', 'event', 'Disengagement', 'Deactivate_Initiate', 'Website']);
                $.ajax({
                    url: getBaseUri() + '/account/send-deactivate-account.json',
                    dataType: 'text',
                    success: function(data) {
                        $scope.primaryEmail = data;
                        $.colorbox({
                            html : $compile($('#deactivate-account-modal').html())($scope)
                        });
                        $scope.$apply();
                        $.colorbox.resize();
                    }
                }).fail(function() {
                    // something bad is happening!
                    console.log("error with change DeactivateAccount");
                });
            };
        }
    ]
);

// This is the Angular 2 part of the module
@NgModule({})
export class DeactivateAccountCtrlNg2Module {}
