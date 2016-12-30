angular.module('orcidApp').controller('profileLockingCtrl', ['$scope', '$compile', function($scope, $compile){
    $scope.orcidToLock = '';
    $scope.orcidToUnlock = '';
    $scope.showLockModal = false;
    $scope.showUnlockModal = false;
    
    $scope.toggleLockModal = function(){
        $scope.showLockModal = !$scope.showLockModal;
        $('#lock_modal').toggle();
    };
    
    $scope.toggleUnlockModal = function(){
        $scope.showUnlockModal = !$scope.showUnlockModal;
        $('#unlock_modal').toggle();
    };
    
    $scope.lockAccount = function() {
        $.ajax({
            url: getBaseUri()+'/admin-actions/lock-accounts.json',
            type: 'POST',
            data: $scope.orcidToLock,
            contentType: 'application/json;charset=UTF-8',
            dataType: 'json',
            success: function(data){
                $scope.result = data;
                $scope.orcidToLock = '';
                $scope.$apply();
            }
        }).fail(function(error) {
            // something bad is happening!
            console.log("Error while locking account");
        });
    };
    
    $scope.unlockAccount = function() {
        $.ajax({
            url: getBaseUri()+'/admin-actions/unlock-accounts.json',
            type: 'POST',
            data: $scope.orcidToUnlock,
            contentType: 'application/json;charset=UTF-8',
            dataType: 'json',
            success: function(data){   
                $scope.result = data;               
                $scope.orcidToUnlock = '';
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