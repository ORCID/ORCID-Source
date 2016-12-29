orcidNgModule.controller('ResetPasswordCtrl', ['$scope', '$compile', 'commonSrvc',function ($scope, $compile, commonSrvc) {
    $scope.getResetPasswordForm = function(){
        $.ajax({
            url: getBaseUri() + '/password-reset.json',
            dataType: 'json',
            success: function(data) {
                $scope.resetPasswordForm = data;
                $scope.$apply();
            }
        }).fail(function(){
        // something bad is happening!
            console.log("error fetching password-reset.json");
        });
    };

    $scope.serverValidate = function () {
        $.ajax({
            url: getBaseUri() + '/reset-password-form-validate.json',
            type: 'POST',
            data:  angular.toJson($scope.resetPasswordForm),
            contentType: 'application/json;charset=UTF-8',
            dataType: 'json',
            success: function(data) {
                commonSrvc.copyErrorsLeft($scope.resetPasswordForm, data);
                $scope.$apply();
            }
        }).fail(function() {
            // something bad is happening!
            console.log("ResetPasswordCtrl.serverValidate() error");
        });
    };

    //init
    $scope.getResetPasswordForm();
}]);