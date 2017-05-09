angular.module('orcidApp').controller('VerifyEmailCtrl', ['$scope', '$compile', 'emailSrvc', 'initialConfigService', function ($scope, $compile, emailSrvc, initialConfigService) {
    $scope.loading = true;
    $scope.getEmails = function() {
        $.ajax({
            url: getBaseUri() + '/account/emails.json',
            // type: 'POST',
            // data: $scope.emailsPojo,
            dataType: 'json',
            success: function(data) {
                var configuration = initialConfigService.getInitialConfiguration();
                var primeVerified = false;

                $scope.verifiedModalEnabled = configuration.showModalManualEditVerificationEnabled;
                $scope.emailsPojo = data;
                $scope.$apply();
                for (i in $scope.emailsPojo.emails) {
                    if ($scope.emailsPojo.emails[i].primary  == true) {
                        $scope.primaryEmail = $scope.emailsPojo.emails[i].value;
                        if ($scope.emailsPojo.emails[i].verified) {
                            primeVerified = true;
                        }
                    };
                };
                if (!primeVerified && !getBaseUri().contains("sandbox")) {
                    var colorboxHtml = $compile($('#verify-email-modal').html())($scope);
                    $scope.$apply();
                    $.colorbox({
                        html : colorboxHtml,
                        escKey:false,
                        overlayClose:false,
                        transition: 'fade',
                        close: '',
                        scrolling: false
                    });
                    $.colorbox.resize({width:"500px"});
                };
                $scope.loading = false;
                $scope.$apply();
            }
        }).fail(function() {
            // something bad is happening!
            console.log("error with multi email");
        });
    };

    $scope.verifyEmail = function() {
        var colorboxHtml = null;
        $.ajax({
            url: getBaseUri() + '/account/verifyEmail.json',
            type: 'get',
            data:  { "email": $scope.primaryEmail },
            contentType: 'application/json;charset=UTF-8',
            dataType: 'json',
            success: function(data) {
                // alert( "Verification Email Send To: " +
                // $scope.emailsPojo.emails[idx].value);
            }
        }).fail(function() {
            // something bad is happening!
            console.log("error with multi email");
        });
        
        colorboxHtml = $compile($('#verify-email-modal-sent').html())($scope);

        $scope.emailSent = true;
        $.colorbox({
            html : colorboxHtml,
            escKey: true,
            overlayClose: true,
            transition: 'fade',
            close: '',
            scrolling: false
                    });
        $.colorbox.resize({height:"200px", width:"500px"});
    };

    $scope.closeColorBox = function() {
        $.ajax({
            url: getBaseUri() + '/account/delayVerifyEmail.json',
            type: 'get',
            contentType: 'application/json;charset=UTF-8',
            success: function(data) {
                // alert( "Verification Email Send To: " +
                // $scope.emailsPojo.emails[idx].value);
            }
        }).fail(function() {
            // something bad is happening!
            console.log("error with multi email");
        });
        $.colorbox.close();
    };

    $scope.emailSent = false;
    $scope.getEmails();
}]);