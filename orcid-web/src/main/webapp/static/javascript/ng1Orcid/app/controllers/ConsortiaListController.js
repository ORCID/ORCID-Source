angular.module('orcidApp').controller('ConsortiaListController',['$scope', '$sce', 'membersListSrvc', 'clearMemberListFilterSrvc', function ($scope, $sce, membersListSrvc, clearMemberListFilterSrvc){
    $scope.membersListSrvc = membersListSrvc;
    $scope.displayMoreDetails = {};
    
    $scope.toggleDisplayMoreDetails = function(memberId, consortiumLeadId){
        membersListSrvc.getDetails(memberId, consortiumLeadId);
        $scope.displayMoreDetails[memberId] = !$scope.displayMoreDetails[memberId];
    }
    
    //render html from salesforce data
    $scope.renderHtml = function (htmlCode) {
        return $sce.trustAsHtml(htmlCode);
    };
    
    //create alphabetical list for filter
    var alphaStr = "abcdefghijklmnopqrstuvwxyz";
    $scope.alphabet = alphaStr.toUpperCase().split("");
    $scope.activeLetter = '';
    $scope.activateLetter = function(letter) {
      $scope.activeLetter = letter
    };
    
    //clear filters
    $scope.clearFilters = function(){
        return clearMemberListFilterSrvc.clearFilters($scope);
    }
        
    // populate the consortia feed
    membersListSrvc.getConsortiaList();    
    
}]);