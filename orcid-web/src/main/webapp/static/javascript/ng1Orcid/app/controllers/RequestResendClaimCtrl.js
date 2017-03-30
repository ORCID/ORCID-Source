angular.module('orcidApp').controller('RequestResendClaimCtrl', ['$scope', '$compile', function RequestResendClaimCtrl($scope, $compile) {
    
    $scope.getRequestResendClaim = function() {
        $.ajax({
            url: getBaseUri() + '/resend-claim.json',
            dataType: 'json',
            success: function(data) {
                $scope.requestResendClaim = data;
                $scope.requestResendClaim.email = getParameterByName("email");
                $scope.$apply();
            }
        }).fail(function(e) {
            console.log("error getting resend-claim.json");
            logAjaxError(e);
        });  
    };
    
    $scope.validateRequestResendClaim = function() {
        $.ajax({
            url: getBaseUri() + '/validate-resend-claim.json',
            dataType: 'json',
            type: 'POST',
            data:  angular.toJson($scope.requestResendClaim),
            contentType: 'application/json;charset=UTF-8',
            success: function(data) {
                $scope.requestResendClaim = data;
                $scope.requestResendClaim.successMessage = null;
                $scope.$apply();
            }
        }).fail(function() {
            console.log("error validating validate-resend-claim.json");
        });  
    };
    
    $scope.postResendClaimRequest = function() {
        $.ajax({
            url: getBaseUri() + '/resend-claim.json',
            dataType: 'json',
            type: 'POST',
            data:  angular.toJson($scope.requestResendClaim),
            contentType: 'application/json;charset=UTF-8',
            success: function(data) {
                $scope.requestResendClaim = data;
                $scope.requestResendClaim.email = "";
                $scope.$apply();
            }
        }).fail(function(){
            console.log("error posting to /resend-claim.json");
        });  
    }
    
    var getParameterByName = function(name) {
        var url = window.location.href;
        name = name.replace(/[\[\]]/g, "\\$&");
        var regex = new RegExp("[?&]" + name + "(=([^&#]*)|&|#|$)"),
            results = regex.exec(url);
        if (!results) return null;
        if (!results[2]) return '';
        return decodeURIComponent(results[2].replace(/\+/g, " "));
    };
    
}]);