angular.module('orcidApp').filter('uri', function() {
    return window.encodeURIComponent;
});