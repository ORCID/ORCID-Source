// Controller for notifications
angular.module('orcidApp').controller('NotificationAlertsCtrl',['$scope', '$compile', 'notificationsSrvc', function ($scope, $compile, notificationsSrvc){
    $scope.notificationsSrvc = notificationsSrvc;
    notificationsSrvc.getNotificationAlerts();
}]);