orcidNgModule.controller('profileReviewCtrl', ['$scope', '$compile', function($scope, $compile){
    $scope.orcidToReview = '';
    $scope.orcidToUnreview = '';
    $scope.showReviewModal = false;
    $scope.showUnreviewModal = false;
    
    $scope.toggleReviewModal = function(){
        $scope.showReviewModal = !$scope.showReviewModal;
        $('#review_modal').toggle();
    };
    
    $scope.toggleUnreviewModal = function(){
        $scope.showUnreviewModal = !$scope.showUnreviewModal;
        $('#unreview_modal').toggle();
    };
    
    $scope.reviewAccount = function() {
        $.ajax({
            url: getBaseUri()+'/admin-actions/review-accounts.json',
            type: 'POST',
            data: $scope.orcidToReview,
            contentType: 'application/json;charset=UTF-8',
            dataType: 'json',
            success: function(data){   
                $scope.result = data;               
                $scope.orcidToReview = '';
                $scope.$apply();
            }
        }).fail(function(error) {
            // something bad is happening!
            console.log("Error while reviewing account");
        });
    };
    
    $scope.unreviewAccount = function() {
        $.ajax({
            url: getBaseUri()+'/admin-actions/unreview-accounts.json',
            type: 'POST',
            data: $scope.orcidToUnreview,
            contentType: 'application/json;charset=UTF-8',
            dataType: 'json',
            success: function(data){   
                $scope.result = data;               
                $scope.orcidToUnreview = '';
                $scope.$apply();
            }
        }).fail(function(error) {
            // something bad is happening!
            console.log("Error while unlocking account");
        });
    };
    
    $scope.closeModal = function() {        
        $.colorbox.close();
    };
}]);