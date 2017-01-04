angular.module('orcidApp').factory("bioBulkSrvc", ['$rootScope', function ($rootScope) {
    var bioBulkSrvc = {
        initScope: function($scope) {
            $scope.bioModel = null; //Dummy model to avoid bulk privacy selector fail
            $scope.bulkEditShow = false;
            $scope.bulkEditMap = {};
            $scope.bulkChecked = false;
            $scope.bulkDisplayToggle = false;
            $scope.toggleSelectMenu = function(){               
                $scope.bulkDisplayToggle = !$scope.bulkDisplayToggle;                    
            };
        }
    };
    return bioBulkSrvc;
}]);