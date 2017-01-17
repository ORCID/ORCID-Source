angular.module('orcidApp').factory("actBulkSrvc", ['$rootScope', function ($rootScope) {
    var actBulkSrvc = {
        initScope: function($scope) {
            $scope.bulkEditShow = false;
            $scope.bulkEditMap = {};
            $scope.bulkChecked = false;
            $scope.bulkDisplayToggle = false;
            $scope.toggleSelectMenu = function(){                   
                $scope.bulkDisplayToggle = !$scope.bulkDisplayToggle;                    
            };
        }
    };
    return actBulkSrvc;
}]);