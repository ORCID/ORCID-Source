orcidNgModule.controller('lookupIdOrEmailCtrl',['$scope','$compile', function findIdsCtrl($scope,$compile){
    $scope.idOrEmails = "";
    $scope.emailIdsMap = {};
    $scope.showSection = false;

    $scope.toggleSection = function(){
        $scope.showSection = !$scope.showSection;
        $('#lookup_ids_section').toggle();
    };

    $scope.lookupIdOrEmails = function() {
        $.ajax({
            url: getBaseUri()+'/admin-actions/lookup-id-or-emails.json',
            type: 'POST',
            dataType: 'text',
            data: $scope.idOrEmails,
            contentType: 'application/json;charset=UTF-8',
            success: function(data){
                $scope.$apply(function(){
                    console.log(data);
                    $scope.result = data;
                    $scope.idOrEmails='';
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
            html : $compile($('#lookup-email-ids-modal').html())($scope),
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