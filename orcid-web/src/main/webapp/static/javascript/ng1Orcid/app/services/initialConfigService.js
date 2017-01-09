angular.module('orcidApp').factory("initialConfigService", ['$rootScope', '$location', function ($rootScope, $location) {
    //location requires param after # example: https://localhost:8443/orcid-web/my-orcid#?flag Otherwise it doesn't found the param and returns an empty object
    var configValues = {
        propertyManualEditVerificationEnabled: orcidVar.emailVerificationManualEditEnabled,
        showModalManualEditVerificationEnabled: false

    };

    var locationObj = $location.search();

    var initialConfigService = {
        getInitialConfiguration: function(){
            return configValues;
        }
    };

    if( locationObj.verifyEdit ){
        if( locationObj.verifyEdit == true || locationObj.verifyEdit == "true" ){
            configValues.showModalManualEditVerificationEnabled = true;
        }
    }

    return initialConfigService;
}]);