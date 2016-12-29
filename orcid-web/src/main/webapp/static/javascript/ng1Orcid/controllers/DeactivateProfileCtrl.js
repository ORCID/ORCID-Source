orcidNgModule.controller('DeactivateProfileCtrl', ['$scope', function ($scope) {
    $scope.orcidsToDeactivate = "";
    $scope.showSection = false;

    $scope.toggleSection = function(){
        $scope.showSection = !$scope.showSection;
        $('#deactivation_modal').toggle();
    };

    
    $scope.deactivateOrcids = function() {
        $.ajax({
            url: getBaseUri()+'/admin-actions/deactivate-profiles.json',
            type: 'POST',
            dataType: 'json',
            data: $scope.orcidsToDeactivate,
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