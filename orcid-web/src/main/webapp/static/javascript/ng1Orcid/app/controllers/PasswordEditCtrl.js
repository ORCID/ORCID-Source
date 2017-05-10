angular.module('orcidApp').controller('PasswordEditCtrl', ['$scope', '$http', function ($scope, $http) {
    $scope.getChangePassword = function() {
        $.ajax({
            url: getBaseUri() + '/account/change-password.json',
            dataType: 'json',
            success: function(data) {
                $scope.changePasswordPojo = data;
                $scope.$apply();
                $scope.zIndexfixIE7();
            }
        }).fail(function() {
            // something bad is happening!
            console.log("error with change password");
        });
    };

    $scope.getChangePassword();

    $scope.zIndexfixIE7 = function(){
        fixZindexIE7('#password-edit', 999999);
        fixZindexIE7('#password-edit .relative', 99999);
    };

    $scope.saveChangePassword = function() {
        $.ajax({
            url: getBaseUri() + '/account/change-password.json',
            type: 'POST',
            data: angular.toJson($scope.changePasswordPojo),
            contentType: 'application/json;charset=UTF-8',
            dataType: 'json',
            success: function(data) {
                $scope.changePasswordPojo = data;
                $scope.$apply();
            }
        }).fail(function() {
            // something bad is happening!
            console.log("error with edit password");
        });
    };
}]);