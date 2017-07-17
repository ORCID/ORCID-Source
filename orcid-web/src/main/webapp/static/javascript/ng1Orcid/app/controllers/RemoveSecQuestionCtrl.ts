declare var $: any;
declare var colorbox: any;
declare var getBaseUri: any;

import * as angular from 'angular';
import {NgModule} from '@angular/core';

// This is the Angular 1 part of the module
export const removeSecQuestionCtrl = angular.module('orcidApp').controller(
    'removeSecQuestionCtrl',
    [
        '$compile', 
        '$scope',
        function (
            $compile,
            $scope
        ) {
            $scope.orcidOrEmail = '';
            $scope.result= '';
            $scope.showSection = false;

            $scope.closeModal = function() {
                $scope.orcidOrEmail = '';
                $scope.result= '';
                $.colorbox.close();
            };

            $scope.confirmRemoveSecurityQuestion = function(){
                if($scope.orcid != '') {
                    $.colorbox({
                        html : $compile($('#confirm-remove-security-question').html())($scope),
                        scrolling: true,
                        onLoad: function() {
                            $('#cboxClose').remove();
                        }
                    });

                    $.colorbox.resize({width:"450px" , height:"150px"});
                }
            };

            $scope.removeSecurityQuestion = function() {
                $.ajax({
                    url: getBaseUri()+'/admin-actions/remove-security-question.json',
                    type: 'POST',
                    data: $scope.orcidOrEmail,
                    contentType: 'application/json;charset=UTF-8',
                    dataType: 'text',
                    success: function(data){
                        $scope.$apply(function(){
                            $scope.result=data;
                            $scope.orcid = '';
                        });
                        $scope.closeModal();
                    }
                }).fail(function(error) {
                    // something bad is happening!
                    console.log("Error generating random string");
                });
            };

            $scope.toggleSection = function(){
                $scope.showSection = !$scope.showSection;
                $('#remove_security_question_section').toggle();
            };

        }
    ]
);

// This is the Angular 2 part of the module
@NgModule({})
export class removeSecQuestionCtrlNg2Module {}