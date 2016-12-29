orcidNgModule.controller('PersonalInfoCtrl', ['$scope', '$compile', 'workspaceSrvc',function ($scope, $compile, workspaceSrvc){
    $scope.displayInfo = workspaceSrvc.displayPersonalInfo;
    $scope.toggleDisplayInfo = function () {
        $scope.displayInfo = !$scope.displayInfo;
    };
}]);