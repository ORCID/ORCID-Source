orcidNgModule.controller('profileDeactivationAndReactivationCtrl',['$scope', '$compile', function ($scope,$compile){
    $scope.orcidToDeactivate = null;
    $scope.orcidToReactivate = null;
    $scope.deactivatedAccount = null;
    $scope.reactivatedAccount = null;
    $scope.successMessage = null;
    $scope.deactivateMessage = om.get('admin.profile_deactivation.success');
    $scope.reactivateMessage = om.get('admin.profile_reactivation.success');
    $scope.showDeactivateModal = false;
    $scope.showReactivateModal = false;

    $scope.toggleDeactivationModal = function(){
        $scope.showDeactivateModal = !$scope.showDeactivateModal;
        $('#deactivation_modal').toggle();
    };

    $scope.toggleReactivationModal = function(){
        $scope.showReactivateModal = !$scope.showReactivateModal;
        $('#reactivation_modal').toggle();
    };

    $scope.deactivateAccount = function() {
        $.ajax({
            url: getBaseUri()+'/admin-actions/deactivate-profile?orcid=' + $scope.orcidToDeactivate,
            type: 'GET',
            dataType: 'json',
            success: function(data){
                $scope.$apply(function(){
                    $scope.deactivatedAccount = data;
                    if($scope.deactivatedAccount.errors != null && $scope.deactivatedAccount.errors.length != 0){
                        $scope.closeModal();
                    } else {
                        $scope.orcidToDeactivate = null;
                        $scope.showSuccessMessage($scope.deactivateMessage);
                    }
                });
            }
        }).fail(function(error) {
            // something bad is happening!
            console.log("Error deprecating the account");
        });
    };

    $scope.reactivateAccount = function() {
        $.ajax({
            url: getBaseUri()+'/admin-actions/reactivate-profile?orcid=' + $scope.orcidToReactivate,
            type: 'GET',
            dataType: 'json',
            success: function(data){
                $scope.$apply(function(){
                    $scope.reactivatedAccount = data;
                    if($scope.reactivatedAccount.errors != null && $scope.reactivatedAccount.errors.length != 0){
                        $scope.closeModal();
                    } else {
                        $scope.orcidToReactivate = null;
                        $scope.showSuccessMessage($scope.reactivateMessage);
                    }
                });
            }
        }).fail(function(error) {
            // something bad is happening!
            console.log("Error reactivating the account");
        });
    };

    $scope.confirmDeactivateAccount = function() {
        $.ajax({
            url: getBaseUri()+'/admin-actions/deactivate-profile/check-orcid.json?orcid=' + $scope.orcidToDeactivate,
            type: 'GET',
            dataType: 'json',
            success: function(data){
                $scope.deactivatedAccount = data;
                if($scope.deactivatedAccount.errors != null && $scope.deactivatedAccount.errors.length != 0){
                    console.log($scope.deactivatedAccount.errors);
                } else {
                    $scope.showConfirmModal();
                }
                $scope.$apply();
            }
            }).fail(function(error) {
                // something bad is happening!
                console.log("Error deactivating the account");
            });
    };

    $scope.confirmReactivateAccount = function() {
        $.colorbox({
            html : $compile($('#confirm-reactivation-modal').html())($scope),
                scrolling: true,
                onLoad: function() {
                $('#cboxClose').remove();
            },
            scrolling: true
        });

        $.colorbox.resize({width:"450px" , height:"225px"});
    };

    $scope.showConfirmModal = function() {
        $.colorbox({
            html : $compile($('#confirm-deactivation-modal').html())($scope),
                scrolling: true,
                onLoad: function() {
                $('#cboxClose').remove();
            },
            scrolling: true
        });

        $.colorbox.resize({width:"525px" , height:"275px"});
    };

    $scope.showSuccessMessage = function(message){
        $scope.successMessage = message;
        $.colorbox({
            html : $compile($('#success-modal').html())($scope),
                scrolling: true,
                onLoad: function() {
                $('#cboxClose').remove();
            },
            scrolling: true
        });

        $.colorbox.resize({width:"425px" , height:"225px"});
    };

    $scope.closeModal = function() {
        $.colorbox.close();
    };
}]);