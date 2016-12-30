angular.module('orcidApp').controller('EmailFrequencyLinkCtrl',['$scope','$rootScope', function ($scope, $rootScope) {
    $scope.getEmailFrequencies = function() {
        $.ajax({
            url: window.location.href + '/email-frequencies.json',
            type: 'GET',
            dataType: 'json',
            success: function(data) {
                $scope.emailFrequency = data;
                $rootScope.$apply();
            }
        }).fail(function() {
            console.log("error with frequency");
        });
    };
    
    $scope.saveEmailFrequencies = function() {
        $.ajax({
            url: window.location.href + '/email-frequencies.json',
            type: 'POST',
            data: angular.toJson($scope.emailFrequency),
            contentType: 'application/json;charset=UTF-8',
            dataType: 'json',
            success: function(data) {
                $scope.emailFrequency = data;
                $rootScope.$apply();
            }
        }).fail(function() {
            console.log("error with frequency");
        });
    };
    
    $scope.getEmailFrequencies();
}]);