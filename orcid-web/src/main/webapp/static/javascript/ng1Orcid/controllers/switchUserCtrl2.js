//WARNING DUPLICATED CONTROLLER
orcidNgModule.controller('switchUserCtrl',['$scope','$compile',function ($scope,$compile){
    $scope.emails = "";
    $scope.orcidOrEmail = "";
    $scope.showSection = false;

    $scope.toggleSection = function(){
        $scope.showSection = !$scope.showSection;
        $('#switch_user_section').toggle();
    };
    
    $scope.switchUserAdmin = function() {
        $.ajax({
            url: getBaseUri()+'/admin-actions/admin-switch-user?orcidOrEmail=' + $scope.orcidOrEmail,
            type: 'GET',
            dataType: 'json',
            success: function(data){
                $scope.$apply(function(){
                    if(!$.isEmptyObject(data)) {
                        if(!$.isEmptyObject(data.errorMessg)) {
                            $scope.orcidMap = data;
                            $scope.showSwitchErrorModal();
                        } else {
                            window.location.replace("./account/admin-switch-user?orcid\=" + data.orcid);
                        }
                    } else {
                        $scope.showSwitchInvalidModal();
                    }
                    $scope.orcidOrEmail='';
                });
            }
        }).fail(function(error) {
            // something bad is happening!
            console.log("Error deprecating the account");
        });
    };
    
    $scope.showSwitchInvalidModal = function() {
    $.colorbox({
        html : $compile($('#switch-imvalid-modal').html())($scope),
            scrolling: false,
            onLoad: function() {
            $('#cboxClose').remove();
        },
        scrolling: false
    });

        setTimeout(function(){$.colorbox.resize({width:"575px"});},100);
    };
    
    $scope.showSwitchErrorModal = function() {
        $.colorbox({
            html : $compile($('#switch-error-modal').html())($scope),
                scrolling: false,
                onLoad: function() {
                $('#cboxClose').remove();
            },
            scrolling: false
        });

        setTimeout(function(){$.colorbox.resize({width:"575px"});},100);
    };

    $scope.closeModal = function() {
        $.colorbox.close();
    };

}]);