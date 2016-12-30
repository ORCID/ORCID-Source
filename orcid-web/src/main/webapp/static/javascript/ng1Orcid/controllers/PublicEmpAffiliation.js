angular.module('orcidApp').controller('PublicEmpAffiliation', ['$scope', '$compile', '$filter', 'workspaceSrvc', 'affiliationsSrvc', function ($scope, $compile, $filter, workspaceSrvc, affiliationsSrvc){
    $scope.workspaceSrvc = workspaceSrvc;
    $scope.affiliationsSrvc = affiliationsSrvc;
    $scope.moreInfo = {};

    $scope.sortState = new ActSortState(GroupedActivities.AFFILIATION);
    $scope.sort = function(key) {
        $scope.sortState.sortBy(key);
    };

    $scope.toggleClickMoreInfo = function(key) {
        if (!document.documentElement.className.contains('no-touch'))
            $scope.moreInfo[key]=!$scope.moreInfo[key];
    };

    // remove once grouping is live
    $scope.moreInfoMouseEnter = function(key, $event) {
        $event.stopPropagation();
        if (document.documentElement.className.contains('no-touch'))
            $scope.moreInfo[key]=true;
    };

    // remove once grouping is live
    $scope.showDetailsMouseClick = function(key, $event) {
        $event.stopPropagation();
        $scope.moreInfo[key]=!$scope.moreInfo[key];
    };

    $scope.closeMoreInfo = function(key) {
        $scope.moreInfo[key]=false;
    };

    affiliationsSrvc.setIdsToAdd(orcidVar.affiliationIdsJson);
    affiliationsSrvc.addAffiliationToScope(orcidVar.orcidId +'/affiliations.json');
}]);