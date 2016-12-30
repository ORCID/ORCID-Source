angular.module('orcidApp').factory("prefsSrvc", function ($rootScope) {
    var serv = {
        prefs: null,
        saved: false,
        getPrivacyPreferences: function() {
            $.ajax({
                url: getBaseUri() + '/account/preferences.json',
                dataType: 'json',
                success: function(data) {
                    serv.prefs = data;
                    $rootScope.$apply();
                }
            }).fail(function() {
                // something bad is happening!
                console.log("error with prefs");
            });
        },
        savePrivacyPreferences: function() {
            $.ajax({
                url: getBaseUri() + '/account/preferences.json',
                type: 'POST',
                data: angular.toJson(serv.prefs),
                contentType: 'application/json;charset=UTF-8',
                dataType: 'json',
                success: function(data) {
                    serv.prefs = data;
                    serv.saved = true;
                    $rootScope.$apply();
                }
            }).fail(function() {
                // something bad is happening!
                console.log("error with prefs");
            });
        },
        clearMessage: function(){
            serv.saved = false;
        }
    };

    // populate the prefs
    serv.getPrivacyPreferences();

    return serv; 
});