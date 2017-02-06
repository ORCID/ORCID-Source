angular.module('orcidApp').controller('languageCtrl',['$scope', '$cookies', 'widgetSrvc', function ($scope, $cookies, widgetSrvc) {
    var productionLangList =
        [
            {
                "value": "cs",
                "direction": "lr",
                "label": "čeština"
            },
            {
                "value": "en",
                "direction": "lr",
                "label": "English"
            },
            {
                "value": 'es',
                "direction": "lr",
                "label": 'Español'
            },
            {
                "value": 'fr',
                "direction": "lr",
                "label": 'Français'
            },
            {
                "value": 'it',
                "direction": "lr",
                "label": 'Italiano'
            },
            {
                "value": 'ja',
                "direction": "lr",
                "label": '日本語'
            },
            {
                "value": 'ko',
                "direction": "lr",
                "label": '한국어'
            },
            {
                "value": 'pt',
                "direction": "lr",
                "label": 'Português'
            },
            {
                "value": 'ru',
                "direction": "lr",
                "label": 'Русский'
            },
            {
                "value": 'zh_CN',
                "direction": "lr",
                "label": '简体中文'
            },
            {
                "value": 'zh_TW',
                "direction": "lr",
                "label": '繁體中文'
            }
        ];
    var testingLangList =
        [
            {
                "value": "ar",
                "direction": "rl",
                "label": "العربية"
            },
            {
                "value": "cs",
                "direction": "lr",
                "label": "čeština"
            },
            {
                "value": "en",
                "direction": "lr",
                "label": "English"
            },
            {
                "value": 'es',
                "direction": "rl",
                "label": 'Español'
            },
            {
                "value": 'fr',
                "direction": "lr",
                "label": 'Français'
            },
            {
                "value": 'it',
                "direction": "lr",
                "label": 'Italiano'
            },
            {
                "value": 'ja',
                "direction": "rl",
                "label": '日本語'
            },
            {
                "value": 'ko',
                "direction": "rl",
                "label": '한국어'
            },
            {
                "value": 'lr',
                "direction": "lr",
                "label": 'lr'
            },
            {
                "value": 'pt',
                "direction": "lr",
                "label": 'Português'
            },
            {
                "value": 'rl',
                "direction": "rl",
                "label": 'rl'
            },
            {
                "value": 'ru',
                "direction": "rl",
                "label": 'Русский'
            },
            {
                "value": 'xx',
                "direction": "lr",
                "label": 'X'
            },
            {
                "value": 'zh_CN',
                "direction": "lr",
                "label": '简体中文'
            },
            {
                "value": 'zh_TW',
                "direction": "rl",
                "label": '繁體中文'
            }
        ];
    
    $scope.widgetSrvc = widgetSrvc;

    if (location == parent.location && window.location.hostname.toLowerCase() != "orcid.org"){
        $scope.languages = testingLangList;
    }
    else{
        $scope.languages = productionLangList;
    }

    //Load Language that is set in the cookie or set default language to english
    $scope.getCurrentLanguage = function(){

        $scope.language = $scope.languages[0]; //Default
        typeof($cookies.get('locale_v3')) !== 'undefined' ? locale_v3 = $cookies.get('locale_v3') : locale_v3 = "en"; //If cookie exists we get the language value from it        
        angular.forEach($scope.languages, function(value, key){ //angular.forEach doesn't support break
            if (value.value == locale_v3){
                $scope.language = $scope.languages[key];
                $scope.widgetSrvc.locale = $scope.language.value; 
            }
        });
        
        document.body.className += " lang-" + $scope.language.direction;
        document.getElementsByTagName('html')[0].setAttribute('lang', $scope.language.value);
    };

    $scope.getCurrentLanguage(); //Checking for the current language value

    $scope.selectedLanguage = function(){
        $.ajax({
            url: getBaseUri()+'/lang.json?lang=' + $scope.language.value,
            type: 'GET',
            dataType: 'json',
            success: function(data){
                angular.forEach($scope.languages, function(value, key){
                    var params;
                    if(value.value == data.locale){
                        $scope.language = $scope.languages[key];                        
                        $scope.widgetSrvc.setLocale($scope.language.value);
                        //In case some parameters were sent via URL
                        params = window.location.href.split("?")[1];
                        if (typeof params != 'undefined'){
                            params = params.split("&");
                            //Removing language parameter (lang=[code]) if it exists
                            for ( var i = 0; i < params.length; i++ ){
                                if(params[i].indexOf("lang=") > -1){
                                    params.splice(i, 1);    
                                }
                            }
                            
                            if ( params.length > 0 ) {                                
                                window.location.href = window.location.href.split("?")[0] + '?' + params.join("&");
                            } else {
                                window.location.href = window.location.href.split("?")[0];
                            }
                            
                        }else{
                            window.location.reload(true);
                        }
                    }
                });
            }
        }).fail(function(error) {
            // something bad is happening!
            console.log("Error setting up language cookie");
        });
    };
    
    
}]);