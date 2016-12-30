angular.module('orcidApp').controller('EmailPreferencesCtrl',['$scope', 'prefsSrvc', function ($scope, prefsSrvc) {
    $scope.prefsSrvc = prefsSrvc;
}]);