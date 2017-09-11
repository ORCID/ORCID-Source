angular.module('orcidApp').directive('resize', function ($window) {
    return function ($scope, element) {
        var w = angular.element($window);
        /*
         * Only used for detecting window resizing, the value returned by
         * w.width() is not accurate, please refer to getWindowWidth()
         */
        $scope.getWindowWidth = function () {
            return { 'w': getWindowWidth() };
        };
        $scope.$watch($scope.getWindowWidth, function (newValue, oldValue) {            
            
            $scope.windowWidth = newValue.w;
            
            
            if($scope.windowWidth > 767){ /* Desktop view */
                $scope.menuVisible = true;
                $scope.searchVisible = true;
                $scope.settingsVisible = true;
            }else{
                $scope.menuVisible = false;
                $scope.searchVisible = false;
                $scope.settingsVisible = false;
            }
            
        }, true);
    
        w.bind('resize', function () {
            $scope.$apply();
        });
    }
});