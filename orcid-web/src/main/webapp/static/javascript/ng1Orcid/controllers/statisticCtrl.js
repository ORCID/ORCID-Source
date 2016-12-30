angular.module('orcidApp').controller('statisticCtrl',['$scope', function ($scope){
    $scope.liveIds = 0;
    $scope.getLiveIds = function(){
        $.ajax({
            url: getBaseUri()+'/statistics/liveids.json',
            type: 'GET',
            dataType: 'html',
            success: function(data){
                $scope.liveIds = data;
                $scope.$apply($scope.liveIds);
            }
        }).fail(function(e) {
            // something bad is happening!
            console.log("Error getting statistics Live iDs total amount");
            logAjaxError(e);
        });
    };

    $scope.getLiveIds();
}]);