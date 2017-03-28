angular.module('orcidApp').controller('RequestPasswordResetCtrl', ['$scope', '$timeout', '$compile', 'utilsService', function RequestPasswordResetCtrl($scope, $timeout, $compile, utilsService) {

    $scope.toggleResetPassword = function() {
        $scope.showResetPassword = !$scope.showResetPassword;

        // pre-populate with email from signin form 
        if(typeof $scope.userId != "undefined" && $scope.userId && utilsService.isEmail($scope.userId)){
            $scope.requestResetPassword = {
                email:  $scope.userId
            } 
        } else if (typeof $scope.authorizationForm != "undefined" && $scope.authorizationForm.userName.value && utilsService.isEmail($scope.authorizationForm.userName.value)) {
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
    
    $scope.postPasswordResetRequest = function() {
        $.ajax({
            url: getBaseUri() + '/reset-password.json',
            dataType: 'json',
            type: 'POST',
            data:  angular.toJson($scope.requestResetPassword),
            contentType: 'application/json;charset=UTF-8',
            success: function(data) {
                $scope.requestResetPassword = data;
                $scope.$apply();
            }
        }).fail(function(){
            console.log("error posting to /reset-password.json");
        });  
    }
    
}]);