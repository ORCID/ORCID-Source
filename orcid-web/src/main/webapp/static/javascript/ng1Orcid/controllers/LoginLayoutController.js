angular.module('orcidApp').controller('LoginLayoutController',['$scope', function ($scope){
    
    $scope.personalLogin = true; //Flag to show or not Personal or Institution Account Login
    $scope.scriptsInjected = false; //Flag to show or not the spinner
    $scope.counter = 0; //To hide the spinner when the second script has been loaded, not the first one.
    $scope.showDeactivatedError = false;
    $scope.showReactivationSent = false;
    
    $scope.showPersonalLogin = function () {        
        $scope.personalLogin = true;        
    };
    
    $scope.showInstitutionLogin = function () {
        $scope.personalLogin = false; //Hide Personal Login
        
        if(!$scope.scriptsInjected){ //If shibboleth scripts haven't been loaded yet.            
            $scope.addScript('/static/javascript/shibboleth-embedded-ds/1.1.0/idpselect_config.js', function(){
                $scope.addScript('/static/javascript/shibboleth-embedded-ds/1.1.0/idpselect.js', function(){
                    $scope.scriptsInjected = true;
                    $scope.$apply();
                    addShibbolethGa($scope.gaString);
                });
            });
        };
    };
    
    $scope.addScript = function(url, onLoadFunction){        
        var head = document.getElementsByTagName('head')[0];
        var script = document.createElement('script');
        script.src = getBaseUri() + url + '?v=' + orcidVar.version;
        script.onload =  onLoadFunction;
        head.appendChild(script); //Inject the script
    };
    
    $scope.loginSocial = function(idp) {
        orcidGA.gaPush(['send', 'event', 'RegGrowth', 'Sign-In-Submit-Social', idp]);
        return false;
    };
    
    $scope.showDeactivationError = function() {
        $scope.showDeactivatedError = true;
        $scope.showReactivationSent = false;
        $scope.$apply();
    };

    $scope.sendReactivationEmail = function () {
       $scope.showDeactivatedError = false;
       $scope.showReactivationSent = true;
       $.ajax({
           url: getBaseUri() + '/sendReactivation.json',
           type: "POST",
           data: { email: $('#userId').val() },
           dataType: 'json',
       }).fail(function(){
       // something bad is happening!
           console.log("error sending reactivation email");
       });
   };
    
}]);