// Controller to show alert for unread notifications
angular.module('orcidApp').controller('NotificationsCountCtrl',['$scope', '$compile', 'notificationsSrvc', function ($scope, $compile, notificationsSrvc){
    
    $scope.isCurrentPage = function(path){
        return window.location.href.startsWith(orcidVar.baseUri + '/' + path);
    }
    
    $scope.getUnreadCount = notificationsSrvc.getUnreadCount;
    // Pages that load notifications will get the unread count themselves
    if(!($scope.isCurrentPage('my-orcid') || $scope.isCurrentPage('inbox'))){
        notificationsSrvc.retrieveUnreadCount();
    }
    
}]);