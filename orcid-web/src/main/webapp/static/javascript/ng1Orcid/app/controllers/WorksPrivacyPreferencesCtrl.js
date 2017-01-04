angular.module('orcidApp').controller('WorksPrivacyPreferencesCtrl',['$scope', 'prefsSrvc', 'commonSrvc', function ($scope, prefsSrvc, commonSrvc) {
    $scope.prefsSrvc = prefsSrvc;
    $scope.privacyHelp = {};
    $scope.showElement = {};
    $scope.commonSrvc = commonSrvc;

    $scope.toggleClickPrivacyHelp = function(key) {
        if (!document.documentElement.className.contains('no-touch'))
            $scope.privacyHelp[key]=!$scope.privacyHelp[key];
    };

    $scope.updateActivitiesVisibilityDefault = function(priv, $event) {
        $scope.prefsSrvc.prefs.activitiesVisibilityDefault.value = priv;
        $scope.prefsSrvc.savePrivacyPreferences();
    };
    
    $scope.showTooltip = function(el){
        $scope.showElement[el] = true;
    };
    
    $scope.hideTooltip = function(el){
        $scope.showElement[el] = false;
    };
}]);