angular.module('orcidApp').controller('MemberPageController',['$scope', '$sce', 'membersListSrvc', function ($scope, $sce, membersListSrvc){
    $scope.membersListSrvc = membersListSrvc;
    
    $scope.renderHtml = function (htmlCode) {
        return $sce.trustAsHtml(htmlCode);
    };
    
}]);