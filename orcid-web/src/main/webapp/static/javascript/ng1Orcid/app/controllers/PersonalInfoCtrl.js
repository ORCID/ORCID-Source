angular.module('orcidApp').controller('PersonalInfoCtrl', ['$scope', '$compile', 'workspaceSrvc', 'utilsService', function ($scope, $compile, workspaceSrvc, utilsService){
    $scope.displayInfo = workspaceSrvc.displayPersonalInfo;

    $scope.toggleDisplayInfo = function () {
        $scope.displayInfo = !$scope.displayInfo;
    };
    var lastModified = orcidVar.lastModified;
    var lastModifiedNoCommas = lastModified.replace(/,/g , "");
    $scope.lastModifiedDate = utilsService.formatTime(Number(lastModifiedNoCommas));
}]);