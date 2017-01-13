angular.module('orcidApp').factory("initialConfigService", ['$rootScope', '$location', function ($rootScope, $location) {
    //location requires param after # example: https://localhost:8443/orcid-web/my-orcid#?flag Otherwise it doesn't found the param and returns an empty object
    var configValues = {
        propertyManualEditVerificationEnabled: orcidVar.emailVerificationManualEditEnabled,
        showModalManualEditVerificationEnabled: false
    };

    var paramVerifyEditRegex = /.*\?(.*\&)*(verifyEdit){1}(=true){0,1}(?!=false)((\&){1}.+)*/g;
    var paramVerifyEdit = paramVerifyEditRegex.test( $location.absUrl() ); 

    var initialConfigService = {
        getInitialConfiguration: function(){
            console.log("configValues", configValues);
            return configValues;
        }
    };

    if( paramVerifyEdit == true ){
        configValues.showModalManualEditVerificationEnabled = true;
    } 

    return initialConfigService;
}]);