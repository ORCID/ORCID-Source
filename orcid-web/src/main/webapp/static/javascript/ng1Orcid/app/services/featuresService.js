angular.module('orcidApp').factory("featuresService", ['$rootScope', function ($rootScope) {

    var features = orcidVar.features;

    var featuresService = {
        isFeatureEnabled: function(featureName){
            if (features[featureName]) {
                return true;
            } else {
                return false;
            }
        }
    };

    return featuresService;
}]);