orcidNgModule.controller('PublicPeerReviewCtrl',['$scope', '$compile', '$filter', 'workspaceSrvc', 'peerReviewSrvc', function ($scope, $compile, $filter, workspaceSrvc, peerReviewSrvc) {
    $scope.peerReviewSrvc = peerReviewSrvc;
    $scope.workspaceSrvc  = workspaceSrvc;
    $scope.showDetails = {};
    $scope.showElement = {};
    $scope.showPeerReviewDetails = new Array();
    $scope.sortHideOption = true;

    $scope.sortState = new ActSortState(GroupedActivities.PEER_REVIEW);

    $scope.sort = function(key) {
      $scope.sortState.sortBy(key);
    };

    $scope.showDetailsMouseClick = function(groupId, $event){
      $event.stopPropagation();
      $scope.showDetails[groupId] = !$scope.showDetails[groupId];
    };
    
    $scope.showTooltip = function (element){        
        $scope.showElement[element] = true;
    };

    $scope.hideTooltip = function (element){        
        $scope.showElement[element] = false;
    };
    
    
    $scope.showMoreDetails = function(putCode){  
        $scope.showPeerReviewDetails.length = 0;
        $scope.showPeerReviewDetails[putCode] = true;   
    };
    
    $scope.hideMoreDetails = function(putCode){
        $scope.showPeerReviewDetails.length = 0;
        $scope.showPeerReviewDetails[putCode] = false;
    };
    
    //Init
    $scope.peerReviewSrvc.loadPeerReviews(peerReviewSrvc.constants.access_type.ANONYMOUS);       
}]);