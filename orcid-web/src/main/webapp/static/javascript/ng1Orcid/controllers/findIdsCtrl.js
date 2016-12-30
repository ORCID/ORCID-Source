angular.module('orcidApp').controller('findIdsCtrl',['$scope','$compile', function findIdsCtrl($scope,$compile){
    $scope.emails = "";
    $scope.emailIdsMap = {};
    $scope.showSection = false;

    $scope.toggleSection = function(){
        $scope.showSection = !$scope.showSection;
        $('#find_ids_section').toggle();
    };

    $scope.findIds = function() {
        $.ajax({
            url: getBaseUri()+'/admin-actions/find-id.json',
            type: 'POST',
            dataType: 'json',
            data: $scope.emails,
            contentType: 'application/json;charset=UTF-8',
            success: function(data){
                $scope.$apply(function(){
                    if(!$.isEmptyObject(data)) {
                        $scope.profileList = data;
                    } else {
                        $scope.profileList = null;
                    }
                    $scope.emails='';
                    $scope.showEmailIdsModal();
                });
            }
        }).fail(function(error) {
            // something bad is happening!
            console.log("Error deprecating the account");
        });
    };

    $scope.showEmailIdsModal = function() {
        $.colorbox({
            html : $compile($('#email-ids-modal').html())($scope),
                scrolling: true,
                onLoad: function() {
                $('#cboxClose').remove();
            },
            scrolling: true
        });

        setTimeout(function(){$.colorbox.resize({width:"575px"});},100);
    };

    $scope.closeModal = function() {
        $.colorbox.close();
    };
}]);