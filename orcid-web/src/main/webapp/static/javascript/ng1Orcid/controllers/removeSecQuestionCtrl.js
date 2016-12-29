orcidNgModule.controller('removeSecQuestionCtrl',['$scope','$compile', function ($scope,$compile) {
    $scope.showSection = false;
    $scope.orcidOrEmail = '';
    $scope.result= '';

    $scope.toggleSection = function(){
        $scope.showSection = !$scope.showSection;
        $('#remove_security_question_section').toggle();
    };

    $scope.removeSecurityQuestion = function() {
        $.ajax({
            url: getBaseUri()+'/admin-actions/remove-security-question.json',
            type: 'POST',
            data: $scope.orcidOrEmail,
            contentType: 'application/json;charset=UTF-8',
            dataType: 'text',
            success: function(data){
                $scope.$apply(function(){
                    $scope.result=data;
                    $scope.orcid = '';
                });
                $scope.closeModal();
            }
        }).fail(function(error) {
            // something bad is happening!
            console.log("Error generating random string");
        });
    };

    $scope.confirmRemoveSecurityQuestion = function(){
        if($scope.orcid != '') {
            $.colorbox({
                html : $compile($('#confirm-remove-security-question').html())($scope),
                    scrolling: true,
                    onLoad: function() {
                    $('#cboxClose').remove();
                },
                scrolling: true
            });

            $.colorbox.resize({width:"450px" , height:"150px"});
        }
    };

    $scope.closeModal = function() {
        $scope.orcidOrEmail = '';
        $scope.result= '';
        $.colorbox.close();
    };
}]);