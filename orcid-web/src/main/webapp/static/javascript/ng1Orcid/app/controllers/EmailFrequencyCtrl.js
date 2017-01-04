angular.module('orcidApp').controller('EmailFrequencyCtrl',['$scope', '$compile', 'emailSrvc', 'prefsSrvc', function ($scope, $compile, emailSrvc, prefsSrvc) {
    $scope.prefsSrvc = prefsSrvc;
    $scope.emailSrvc = emailSrvc;
    
}]);