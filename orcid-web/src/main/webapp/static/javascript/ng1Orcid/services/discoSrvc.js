orcidNgModule.factory("discoSrvc", ['$rootScope', 'widgetSrvc', function ($rootScope, widgetSrvc) {
    var serv = {
        feed: null,
        getDiscoFeed: function() {
            $.ajax({
                url: getBaseUri() + '/Shibboleth.sso/DiscoFeed',
                dataType: 'json',
                cache: true,
                success: function(data) {
                    serv.feed = data;
                    $rootScope.$apply();
                }
            }).fail(function(e) {
                // something bad is happening!
                console.log("error with disco feed");
                logAjaxError(e);
                serv.feed = [];
                $rootScope.$apply();
            });
        },
        getIdPName: function(entityId) {
            var locale = widgetSrvc.locale != null ? widgetSrvc.locale : "en";
            for(i in serv.feed) {
                var idp = serv.feed[i];
                if(entityId === idp.entityID) {
                    var name = idp.DisplayNames[0].value;
                    for(j in idp.DisplayNames){
                        var displayName = idp.DisplayNames[j];
                        if(locale === displayName.lang){
                            name = displayName.value;
                        }
                    }
                    return name;
                }
            }
            if(entityId === "facebook" || entityId === "google"){
                return entityId.charAt(0).toUpperCase() + entityId.slice(1);
            }
            return entityId;
        }
    };

    // populate the disco feed
    serv.getDiscoFeed();
    return serv; 
}]);