angular.module('orcidApp').factory("widgetSrvc", ['$rootScope', function ($rootScope) {
    var widgetSrvc = {
        locale: 'en',
        setLocale: function (locale) {
            widgetSrvc.locale = locale;
        }
    };
    return widgetSrvc;
}]);