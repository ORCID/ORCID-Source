angular.module('orcidApp').factory("featuresService", ['$rootScope', function ($rootScope) {

    var features = orcidVar.features;

    var featuresService = {
        isFeatureEnabled: function(featureName){
            if (features[featureName]) {
                orcidGA.gaPush(['send', 'event', 'feature', featureName, 'enabled']);
                return true;
            } else {
                orcidGA.gaPush(['send', 'event', 'feature', featureName, 'disabled']);
                return false;
            }
        }
    };

    return featuresService;
}]);