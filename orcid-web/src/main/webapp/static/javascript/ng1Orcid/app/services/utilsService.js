angular.module('orcidApp').factory(
    'utilsService', 
    function() {
        var utilsService = {
            formColorBoxResize: function() {
                if (isMobile()) {
                    $.colorbox.resize({width: formColorBoxWidth(), height: '100%'});
                }
                else {
                    // IE8 and below doesn't take auto height
                    // however the default div height
                    // is auto anyway
                    $.colorbox.resize({width:'800px'});
                    
                }
            }
        };
        return utilsService;
    }
);


var formColorBoxResize = function() {
    if (isMobile())
        $.colorbox.resize({width: formColorBoxWidth(), height: '100%'});
    else
        // IE8 and below doesn't take auto height
        // however the default div height
        // is auto anyway
        $.colorbox.resize({width:'800px'});
}

angular.module('orcidApp').factory("initialConfigService", ['$rootScope', '$location', function ($rootScope, $location) {
    //location requires param after # example: https://localhost:8443/orcid-web/my-orcid#?flag Otherwise it doesn't found the param and returns an empty object
    var configValues = {
        propertyManualEditVerificationEnabled: orcidVar.emailVerificationManualEditEnabled,
        showModalManualEditVerificationEnabled: true

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
        } else {
            configValues.showModalManualEditVerificationEnabled = false;
        }
    }

    return initialConfigService;
}]);