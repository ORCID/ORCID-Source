orcidNgModule.controller('ResendClaimCtrl', ['$scope', function ($scope) {
    $scope.emailIds = "";
    $scope.showSection = false;

    $scope.toggleSection = function(){
        $scope.showSection = !$scope.showSection;
        $('#batch_resend_section').toggle();
    };

    
    $scope.resendClaimEmails = function() {
        $.ajax({
            url: getBaseUri()+'/admin-actions/resend-claim.json',
            type: 'POST',
            dataType: 'json',
            data: $scope.emailIds,
            contentType: 'application/json;charset=UTF-8',
            success: function(data){
                $scope.$apply(function(){
                    $scope.result = data;
                });
            }
        }).fail(function(error) {
            // something bad is happening!
            console.log("Error re-sending claim emails");
        });
    }
}]);