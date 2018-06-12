//migrated

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
        updateEmailFrequency: function() {
            console.log('updateEmailFrequency is deprecated and does not work anymore');
        }, 
        updateNotificationPreferences: function() {
            console.log('updateNotificationPreferences is deprecated and does not work anymore');
        },
        updateDefaultVisibility: function() {
        	$.ajax({
                url: getBaseUri() + '/account/default_visibility.json',
                type: 'POST',
                data: serv.prefs['default_visibility'],
                contentType: 'application/json;charset=UTF-8',
                dataType: 'text',
                success: function(data) {                    
                    serv.saved = true;
                    $rootScope.$apply();
                }
            }).fail(function(jqXHR, textStatus, errorThrown) {
            	console.log(textStatus);
            	console.log(errorThrown);
            	console.log(jqXHR);
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