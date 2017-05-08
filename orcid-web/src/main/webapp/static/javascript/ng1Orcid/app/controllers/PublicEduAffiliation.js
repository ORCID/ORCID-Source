angular.module('orcidApp').controller('PublicEduAffiliation', ['$scope', '$compile', '$filter', '$location', 'workspaceSrvc', 'affiliationsSrvc', 'utilsService', function ($scope, $compile, $filter, $location, workspaceSrvc , affiliationsSrvc, utilsService ){
    $scope.workspaceSrvc = workspaceSrvc;
    $scope.affiliationsSrvc = affiliationsSrvc;
    $scope.utilsService = utilsService;
    $scope.moreInfo = {};
 
    $scope.printView =  utilsService.isPrintView(window.location.pathname);

    $scope.sortState = new ActSortState(GroupedActivities.AFFILIATION);
    $scope.sort = function(key) {       
        $scope.sortState.sortBy(key);
    };

    // remove once grouping is live
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

    $scope.showDetailsMouseClick = function(key, $event) {
        $event.stopPropagation();
        $scope.moreInfo[key] = !$scope.moreInfo[key];
    };

    $scope.closeMoreInfo = function(key) {
        $scope.moreInfo[key]=false;
    };

}]);