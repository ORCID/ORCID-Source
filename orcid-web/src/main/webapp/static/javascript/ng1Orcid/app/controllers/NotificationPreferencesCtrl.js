angular.module('orcidApp').controller('NotificationPreferencesCtrl',['$scope', '$compile', 'emailSrvc', 'prefsSrvc', 'emailSrvc',function ($scope, $compile, emailSrvc, prefsSrvc, emailSrvc) {
    $scope.prefsSrvc = prefsSrvc;
    $scope.emailSrvc = emailSrvc;
}]);