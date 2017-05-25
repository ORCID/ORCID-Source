/*import * as angular from 'angular';
import {NgModule} from '@angular/core';

// This is the Angular 1 part of the module

export const NameCtrl = angular.module('orcidApp').controller('NameCtrl', ['$scope', '$compile',function NameCtrl($scope, $compile) {
    $scope.nameForm = null;
    $scope.privacyHelp = false;
    $scope.showEdit = false;

    $scope.toggleEdit = function() {
        $scope.showEdit = !$scope.showEdit;
    };

    $scope.close = function() {
        $scope.getNameForm();
        $scope.showEdit = false;
    };

    $scope.getNameForm = function(){
        $.ajax({
            url: getBaseUri() + '/account/nameForm.json',
            dataType: 'json',
            success: function(data) {
                $scope.nameForm = data;
                $scope.$apply();
            }
        }).fail(function(){
            // something bad is happening!
            console.log("error fetching otherNames");
        });
    };

    $scope.setNameForm = function(){
        $.ajax({
            url: getBaseUri() + '/account/nameForm.json',
            type: 'POST',
            data:  angular.toJson($scope.nameForm),
            contentType: 'application/json;charset=UTF-8',
            dataType: 'json',
            success: function(data) {
                $scope.nameForm = data;
                if(data.errors.length == 0)
                   $scope.close();
                $scope.$apply();
            }
        }).fail(function() {
            // something bad is happening!
            console.log("OtherNames.serverValidate() error");
        });
    };

    $scope.setNamesVisibility = function(priv, $event) {
        $event.preventDefault();
        $scope.nameForm.namesVisibility.visibility = priv;
    };

    $scope.getNameForm();

    function getBaseUri() {
        return 'https:' == document.location.protocol ? orcidVar.baseUri
                : orcidVar.baseUriHttp;
    }
}]);

// This is the Angular 2 part of the module
@NgModule({})
export class NameCtrlNg2Module {}
*/