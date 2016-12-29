orcidNgModule.factory("initialConfigService", ['$rootScope', '$location', function ($rootScope, $location) {
    //location requires param after # example: https://localhost:8443/orcid-web/my-orcid#?flag Otherwise it doesn't found the param and returns an empty object
    var configValues = {
        modalManualEditVerificationEnabled: false
    };

    var locationObj = $location.search();

    var initialConfigService = {
        getInitialConfiguration: function(){
            return configValues;
        }
    };

    if( locationObj.verifyEdit ){
        if( locationObj.verifyEdit == true || locationObj.verifyEdit == "true" ){
            configValues.modalManualEditVerificationEnabled = true;
        } else {
            configValues.modalManualEditVerificationEnabled = false;
        }
    }

    return initialConfigService;
}]);