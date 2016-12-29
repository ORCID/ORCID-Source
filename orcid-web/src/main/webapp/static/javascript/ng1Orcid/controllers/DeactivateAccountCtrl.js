orcidNgModule.controller('DeactivateAccountCtrl', ['$scope', '$compile', function ($scope, $compile) {
    $scope.sendDeactivateEmail = function() {
        orcidGA.gaPush(['send', 'event', 'Disengagement', 'Deactivate_Initiate', 'Website']);
        $.ajax({
            url: getBaseUri() + '/account/send-deactivate-account.json',
            dataType: 'json',
            success: function(data) {
                $scope.primaryEmail = data.value;
                $.colorbox({
                    html : $compile($('#deactivate-account-modal').html())($scope)
                });
                $scope.$apply();
                $.colorbox.resize();
            }
        }).fail(function() {
            // something bad is happening!
            console.log("error with change DeactivateAccount");
        });
    };

    $scope.closeModal = function() {
        $.colorbox.close();
    };
}]);