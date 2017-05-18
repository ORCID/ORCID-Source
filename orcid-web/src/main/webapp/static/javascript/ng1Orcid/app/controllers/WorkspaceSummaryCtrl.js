angular.module('orcidApp').controller('WorkspaceSummaryCtrl', ['$scope', '$compile', 'affiliationsSrvc', 'fundingSrvc', 'worksSrvc', 'peerReviewSrvc', 'workspaceSrvc',function ($scope, $compile, affiliationsSrvc, fundingSrvc, worksSrvc, peerReviewSrvc, workspaceSrvc){
    $scope.workspaceSrvc = workspaceSrvc;
    $scope.worksSrvc = worksSrvc;
    $scope.affiliationsSrvc = affiliationsSrvc;
    $scope.fundingSrvc = fundingSrvc;
    $scope.peerReviewSrvc = peerReviewSrvc;
    $scope.showAddAlert = function () {
        if (worksSrvc.loading == false && affiliationsSrvc.loading == false && peerReviewSrvc.loading == false
                && worksSrvc.groups.length == 0
                && affiliationsSrvc.educations.length == 0
                && affiliationsSrvc.employments.length == 0
                && fundingSrvc.groups.length == 0
                && peerReviewSrvc.groups.lenght == 0)
            return true;
        return false;
    };
}]);