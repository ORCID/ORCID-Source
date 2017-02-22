angular.module('orcidApp').controller('RequestPasswordResetCtrl', ['$scope', '$compile', function RequestPasswordResetCtrl($scope, $compile) {
    
    $scope.getRequestResetPassword = function() {
        $.ajax({
            url: getBaseUri() + '/reset-password.json',
            dataType: 'json',
            success: function(data) {
                $scope.requestResetPassword = data;
                $scope.$apply();
            }
        }).fail(function(){
            console.log("error getting reset-password.json");
        });  
    };
    
    $scope.validateRequestPasswordReset = function() {
        $.ajax({
            url: getBaseUri() + '/validate-reset-password.json',
            dataType: 'json',
            type: 'POST',
            data:  angular.toJson($scope.requestResetPassword),
            contentType: 'application/json;charset=UTF-8',
            success: function(data) {
                $scope.requestResetPassword = data;
                $scope.requestResetPassword.successMessage = null;
                $scope.$apply();
            }
        }).fail(function() {
            console.log("error validating validate-reset-password.json");
        });  
    };
    
    $scope.postPasswordResetRequest = function() {
        $.ajax({
            url: getBaseUri() + '/reset-password.json',
            dataType: 'json',
            type: 'POST',
            data:  angular.toJson($scope.requestResetPassword),
            contentType: 'application/json;charset=UTF-8',
            success: function(data) {
                $scope.requestResetPassword = data;
                $scope.requestResetPassword.email = "";
                $scope.$apply();
            }
        }).fail(function(){
            console.log("error posting to /reset-password.json");
        });  
    }
    
}]);