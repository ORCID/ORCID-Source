angular.module('orcidApp').controller('SecurityQuestionEditCtrl', ['$scope', '$compile', function ($scope, $compile) {
    $scope.errors = null;
    $scope.password = null;
    $scope.securityQuestions = [];

    $scope.getSecurityQuestion = function() {
        $.ajax({
            url: getBaseUri() + '/account/security-question.json',
            dataType: 'json',
            success: function(data) {               
                $scope.securityQuestionPojo = data;
                $scope.$apply();
            }
        }).fail(function() {
            // something bad is happening!
            console.log("error with security question.json");
        });
    };

    $scope.getSecurityQuestion();

    $scope.checkCredentials = function() {
        $scope.password=null;
        if(orcidVar.isPasswordConfirmationRequired){
            $.colorbox({
                html: $compile($('#check-password-modal').html())($scope)
            });
            $.colorbox.resize();
        }
        else{
            $scope.submitModal();
        }
    };

    $scope.submitModal = function() {
        $scope.securityQuestionPojo.password=$scope.password;
        $.ajax({
            url: getBaseUri() + '/account/security-question.json',
            type: 'POST',
            data: angular.toJson($scope.securityQuestionPojo),
            contentType: 'application/json;charset=UTF-8',
            dataType: 'json',
            success: function(data) {
                
                if(data.errors.length != 0) {
                    $scope.errors=data.errors;
                } else {
                    $scope.errors=null;
                }
                $scope.getSecurityQuestion();
                $scope.$apply();
            }
        }).fail(function() {
            // something bad is happening!
            console.log("error with security question");
        });
        $scope.password=null;
        $.colorbox.close();
    };

    $scope.closeModal = function() {
        $.colorbox.close();
    };
}]);