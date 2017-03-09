angular.module('orcidApp').controller('RequestPasswordResetCtrl', ['$scope', '$timeout', '$compile', function RequestPasswordResetCtrl($scope, $timeout, $compile) {

    var reEmailMatch = /^(([^<>()[\]\\.,;:\s@\"]+(\.[^<>()[\]\\.,;:\s@\"]+)*)|(\".+\"))@((\[[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\])|(([a-zA-Z\-0-9]+\.)+[a-zA-Z]{2,}))$/;

    //prefill reset form if email included in oauth url is an existing orcid user
    /*
    This doesn't update the view
    $scope.$on("loginHasUserId", function(event, options) {
        console.log(options.userName);
        if(reEmailMatch.test(options.userName)) {
            console.log(options.userName + " is an email");
                $scope.requestResetPassword = {
                    email:  options.userName
                } 
                console.log("email to prefill form is now: " + $scope.requestResetPassword.email);    
        } else {
            $scope.requestResetPassword = {
                email:  ""
            }
    }
    });

    This doesn't udpate the view either (neither does putting the expression inside $scope.apply)
    $scope.$on("loginHasUserId", function(event, options) {
        console.log(options.userName);//works fine
        if(reEmailMatch.test(options.userName)) {
            console.log(options.userName + " is an email");//works fine
                $scope.requestResetPassword = {
                    email:  options.userName
                } 
                console.log("email to prefill form is now: " + $scope.requestResetPassword.email);//works fine    
        } else {
            $scope.requestResetPassword = {
                email:  ""
            }
        $scope.$apply();
    }
    });*/
    //This updates the view, but timeout is not good
    $scope.$on("loginHasUserId", function(event, options) {
        console.log(options.userName);
        if(reEmailMatch.test(options.userName)) {
            $timeout(function(){
                console.log(options.userName + " is an email");
                $scope.requestResetPassword = {
                    email:  options.userName
                } 
                console.log("email to prefill form is now: " + $scope.requestResetPassword.email);
            });     
        } else {
            $scope.requestResetPassword = {
                email:  ""
            }
    }
    });

    //prefill reset form if email entered in login form
    $scope.$on("loginUserIdInputChanged", function(event, options) {
        if(reEmailMatch.test(options.newValue)) {
            $scope.requestResetPassword = {
                email:  options.newValue
            }
        } else {
            $scope.requestResetPassword = {
                email:  ""
            }
        }
    });


    $scope.toggleResetPassword = function() {
        $scope.showResetPassword = !$scope.showResetPassword;
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