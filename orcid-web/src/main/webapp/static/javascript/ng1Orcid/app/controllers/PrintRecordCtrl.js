angular.module('orcidApp').controller('PrintRecordCtrl',['$scope', '$compile', '$window', function ($scope, $compile, $window) {

    $scope.printRecord = function(url){
        //open window
        printWindow = $window.open(url);  
    }

}]);