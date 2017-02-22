angular.module('orcidApp').controller('RequestPasswordResetCtrl', ['$scope', '$compile', function RequestPasswordResetCtrl($scope, $compile) {

    $scope.resetPasswordUpdateToggleText = function () {
        if ($scope.showResetPassword) $scope.resetPasswordToggleText = om.get("manage.editTable.hide");
        else $scope.resetPasswordToggleText = om.get("login.forgotten_password");
    };

    $scope.toggleResetPassword = function() {
        $scope.showResetPassword = !$scope.showResetPassword;
        //$scope.resetPasswordUpdateToggleText();
    };

    // init reset password
    $scope.showResetPassword = (window.location.hash === "#resetPassword");
    //$scope.resetPasswordUpdateToggleText();
    $scope.resetPasswordToggleText = om.get("login.forgotten_password");
    
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