//WARNING DUPLICATE CONTROLLER
orcidNgModule.controller('resetPasswordCtrlModal',['$scope', '$compile', function ($scope,$compile) {
    $scope.showSection = false;
    $scope.params = {orcidOrEmail:'',password:''};
    $scope.result = '';

    $scope.toggleSection = function(){
        $scope.showSection = !$scope.showSection;
        $('#reset_password_section').toggle();
    };

    $scope.randomString = function() {
        $scope.result = '';
        $.ajax({
            url: getBaseUri()+'/admin-actions/generate-random-string.json',
            type: 'GET',
            dataType: 'text',
            success: function(data){
                $scope.$apply(function(){
                    $scope.params.password=data;
                });
            }
        }).fail(function(e) {
            // something bad is happening!
            console.log("Error generating random string");
            logAjaxError(e);
        });
    };

    $scope.resetPassword = function(){
        $scope.result = '';
        $.ajax({
            url: getBaseUri()+'/admin-actions/reset-password.json',
            type: 'POST',
            data: angular.toJson($scope.params),
            contentType: 'application/json;charset=UTF-8',
            dataType: 'text',
            success: function(data){
                $scope.$apply(function(){
                    $scope.result=data;
                    $scope.params.orcidOrEmail='';
                    $scope.params.password='';
                });
                $scope.closeModal();
            }
        }).fail(function(error) {
            // something bad is happening!
            console.log("Error generating random string");
        });
    };

    $scope.confirmResetPassword = function(){
        if($scope.params.orcidOrEmail != '' && $scope.params.password != '') {
            $.colorbox({
                html : $compile($('#confirm-reset-password').html())($scope),
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
        $scope.params.orcidOrEmail='';
        $scope.params.password='';
        $scope.result= '';
        $.colorbox.close();
    };
}]);