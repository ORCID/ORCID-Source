angular.module('orcidApp').controller('RequestPasswordResetCtrl', ['$scope', '$timeout', '$compile', function RequestPasswordResetCtrl($scope, $timeout, $compile) {

    var reEmailMatch = /^(([^<>()[\]\\.,;:\s@\"]+(\.[^<>()[\]\\.,;:\s@\"]+)*)|(\".+\"))@((\[[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\])|(([a-zA-Z\-0-9]+\.)+[a-zA-Z]{2,}))$/;

    $scope.toggleResetPassword = function() {
        $scope.showResetPassword = !$scope.showResetPassword;

        // pre-populate with email from signin form 
        if(reEmailMatch.test($scope.userId)){
            $scope.requestResetPassword = {
                email:  options.userName
            } 
        } else if (reEmailMatch.test($scope.authorizationForm.userName.value)) {
            $scope.requestResetPassword = {
                email:  $scope.authorizationForm.userName.value
            } 
        } else {
            $scope.requestResetPassword = {
                email:  ""
            }
        }
    };

    // init reset password toggle text
    $scope.showResetPassword = (window.location.hash === "#resetPassword");
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