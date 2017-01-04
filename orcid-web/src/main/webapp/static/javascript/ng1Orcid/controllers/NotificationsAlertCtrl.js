// Controller to show alert for unread notifications
angular.module('orcidApp').controller('NotificationsAlertCtrl',['$scope', '$compile', 'notificationsSrvc', function ($scope, $compile, notificationsSrvc){
    $scope.getUnreadCount = notificationsSrvc.getUnreadCount;
    notificationsSrvc.retrieveUnreadCount();
}]);