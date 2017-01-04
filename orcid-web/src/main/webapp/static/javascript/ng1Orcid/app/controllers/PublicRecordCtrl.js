angular.module('orcidApp').controller('PublicRecordCtrl',['$scope', '$compile',function ($scope, $compile) {
    $scope.showSources = new Array();
    $scope.showPopover = new Array();
    $scope.toggleSourcesDisplay = function(section){        
        $scope.showSources[section] = !$scope.showSources[section];     
    }
    
    $scope.showPopover = function(section){
        $scope.showPopover[section] = true;
    }   
    
    $scope.hidePopover = function(section){
        $scope.showPopover[section] = false;    
    }
}]);