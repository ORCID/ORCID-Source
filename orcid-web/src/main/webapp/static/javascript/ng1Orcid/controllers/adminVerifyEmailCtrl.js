orcidNgModule.controller('adminVerifyEmailCtrl',['$scope','$compile', function ($scope,$compile){
    $scope.showSection = false;

    $scope.toggleSection = function(){
        $scope.showSection = !$scope.showSection;
        $('#verify_email_section').toggle();
    };

    $scope.verifyEmail = function(){
        $.ajax({
            url: getBaseUri()+'/admin-actions/admin-verify-email.json',
            type: 'POST',
            dataType: 'text',
            data: $scope.email,
            contentType: 'application/json;charset=UTF-8',
            success: function(data){
                $scope.$apply(function(){
                    $scope.result = data;
                });
            }
        }).fail(function(error) {
            // something bad is happening!
            console.log("Error verifying the email address");
        });
    };
}]);