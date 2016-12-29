orcidNgModule.controller('ReactivationCtrl', ['$scope', '$compile', 'commonSrvc', 'vcRecaptchaService', function ($scope, $compile, commonSrvc, vcRecaptchaService) {
    
    $scope.privacyHelp = {};

    $scope.toggleClickPrivacyHelp = function(key) {
        if (!document.documentElement.className.contains('no-touch'))
            $scope.privacyHelp[key]=!$scope.privacyHelp[key];
    };

    $scope.updateActivitiesVisibilityDefault = function(priv, $event) {
        $scope.register.activitiesVisibilityDefault.visibility = priv;
    };

    $scope.getReactivation = function(resetParams, linkFlag){
        $.ajax({
            url: getBaseUri() + '/register.json',
            dataType: 'json',
            success: function(data) {
               $scope.register = data;
               $scope.register.resetParams = resetParams;
               $scope.$apply();               
    
               $scope.$watch('register.givenNames.value', function() {
                   trimAjaxFormText($scope.register.givenNames);
               }); // initialize the watch
    
               $scope.$watch('register.familyNames.value', function() {
                    trimAjaxFormText($scope.register.familyNames);
               }); // initialize the watch
            }
        }).fail(function(){
        // something bad is happening!
            console.log("error fetching register.json");
        });
    };
    
    $scope.postReactivationConfirm = function () {
        $scope.register.valNumClient = $scope.register.valNumServer / 2;
        var baseUri = getBaseUri();
        if($scope.register.linkType === 'shibboleth'){
            baseUri += '/shibboleth';
        }
        $.ajax({
            url: baseUri + '/reactivationConfirm.json',
            type: 'POST',
            data:  angular.toJson($scope.register),
            contentType: 'application/json;charset=UTF-8',
            dataType: 'json',
            success: function(data) {
                if(data.errors.length == 0){
                    window.location.href = data.url;
                }
                else{
                    $scope.register = data;
                    $scope.$apply();
                }
            }
        }).fail(function() {
            // something bad is happening!
            console.log("ReactivationCtrl.postReactivationConfirm() error");
        });
    };

    $scope.serverValidate = function (field) {        
        if (field === undefined) field = '';
        $.ajax({
            url: getBaseUri() + '/register' + field + 'Validate.json',
            type: 'POST',
            data:  angular.toJson($scope.register),
            contentType: 'application/json;charset=UTF-8',
            dataType: 'json',
            success: function(data) {
                commonSrvc.copyErrorsLeft($scope.register, data);
                $scope.$apply();
            }
        }).fail(function() {
            // something bad is happening!
            console.log("RegistrationCtrl.serverValidate() error");
        });
    };

    $scope.isValidClass = function (cur) {
        if (cur === undefined) return '';
        var valid = true;
        if (cur.required && (cur.value == null || cur.value.trim() == '')) valid = false;
        if (cur.errors !== undefined && cur.errors.length > 0) valid = false;
        return valid ? '' : 'text-error';
    };
        
}]);