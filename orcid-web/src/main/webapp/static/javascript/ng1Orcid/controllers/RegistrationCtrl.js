orcidNgModule.controller('RegistrationCtrl', ['$scope', '$compile', 'commonSrvc', 'vcRecaptchaService', function ($scope, $compile, commonSrvc, vcRecaptchaService) {
    $scope.privacyHelp = {};
    $scope.recaptchaWidgetId = null;
    $scope.recatchaResponse = null;
    $scope.showDeactivatedError = false;
    $scope.showReactivationSent = false;
    
    $scope.model = {
        key: orcidVar.recaptchaKey
    };
    
    var loadDate = new Date();
    $scope.loadTime = loadDate.getTime();

    $scope.toggleClickPrivacyHelp = function(key) {
        if (!document.documentElement.className.contains('no-touch'))
            $scope.privacyHelp[key]=!$scope.privacyHelp[key];
    };
    
    $scope.getRegister = function(givenName, familyName, email, linkFlag){
        $.ajax({
            url: getBaseUri() + '/register.json',
            dataType: 'json',
            success: function(data) {
               $scope.register = data;
               $scope.register.givenNames.value=givenName;
               $scope.register.familyNames.value=familyName;
               $scope.register.email.value=email;
               $scope.register.linkType=linkFlag;
               $scope.$apply();               
    
                // make sure inputs stayed trimmed
                $scope.$watch('register.email.value', function(newValue, oldValue) {
                    if(newValue !== oldValue) {
                        trimAjaxFormText($scope.register.email);
                    }
                }); // initialize the watch
                
                // special handling of deactivation error
                $scope.$watch('register.email.errors', function(newValue, oldValue) {
                        $scope.showDeactivatedError = ($.inArray('orcid.frontend.verify.deactivated_email', $scope.register.email.errors) != -1);
                        $scope.showReactivationSent = false;
                }); // initialize the watch     
                
                // make sure email is trimmed
                $scope.$watch('register.emailConfirm.value', function(newValue, oldValue) {
                    if(newValue !== oldValue){
                        trimAjaxFormText($scope.register.emailConfirm);
                        $scope.serverValidate('EmailConfirm');
                    }
                }); // initialize the watch
    
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
    
    $scope.getDuplicates = function(){
        $.ajax({
            //url: getBaseUri() + 'dupicateResearcher.json?familyNames=test&givenNames=test',
            url: getBaseUri() + '/dupicateResearcher.json?familyNames=' + $scope.register.familyNames.value + '&givenNames=' + $scope.register.givenNames.value,
            dataType: 'json',
            success: function(data) {
                $scope.duplicates = data;
                $scope.$apply();
                var diffDate = new Date();
                // reg was filled out to fast reload the page
                if ($scope.loadTime + 5000 > diffDate.getTime()) {
                    window.location.reload();
                    return;
                }
                if ($scope.duplicates.length > 0 ) {
                    $scope.showDuplicatesColorBox();
                } else {
                    $scope.postRegisterConfirm();
                }
            }
        }).fail(function(){
            // something bad is happening!
            console.log("error fetching dupicateResearcher.json");
            // continue to registration, as solr dup lookup failed.
            $scope.postRegisterConfirm();
        });
    };


    $scope.updateActivitiesVisibilityDefault = function(priv, $event) {
        $scope.register.activitiesVisibilityDefault.visibility = priv;
    };

    $scope.postRegister = function (linkFlag) {
        if (basePath.startsWith(baseUrl + 'oauth')) {
            var clientName = $('div#RegistrationCtr input[name="client_name"]').val();
            $scope.register.referredBy = $('div#RegistrationCtr input[name="client_id"]').val();
            var clientGroupName = $('div#RegistrationCtr input[name="client_group_name"]').val();
            orcidGA.gaPush(['send', 'event', 'RegGrowth', 'New-Registration-Submit' , 'OAuth ' + orcidGA.buildClientString(clientGroupName, clientName)]);
            $scope.register.creationType.value = "Member-referred";
        } else {
            orcidGA.gaPush(['send', 'event', 'RegGrowth', 'New-Registration-Submit', 'Website']);
            $scope.register.creationType.value = "Direct";
        }        
        
        $scope.register.grecaptcha.value = $scope.recatchaResponse; //Adding the response to the register object
        $scope.register.grecaptchaWidgetId.value = $scope.recaptchaWidgetId;
        console.log('link flag is : '+ linkFlag);
        $scope.register.linkType = linkFlag;
        $.ajax({
            url: getBaseUri() + '/register.json',
            type: 'POST',
            data:  angular.toJson($scope.register),
            contentType: 'application/json;charset=UTF-8',
            dataType: 'json',
            success: function(data) {
                console.log(data);
                $scope.register = data;             
                $scope.$apply();                
                if ($scope.register.errors == undefined || $scope.register.errors == undefined || $scope.register.errors.length == 0) {
                    if ($scope.register.errors.length == 0) {
                        
                        $scope.showProcessingColorBox();
                        $scope.getDuplicates();
                    }
                } else {
                    if ($scope.register.grecaptcha.errors.length == 0) angular.element(document.querySelector('#recaptcha')).remove();
                }
            }
        }).fail(function() {
            // something bad is happening!
            console.log("RegistrationCtrl.postRegister() error");
        });
    };

    $scope.postRegisterConfirm = function () {
        $scope.showProcessingColorBox();
        $scope.register.valNumClient = $scope.register.valNumServer / 2;
        var baseUri = getBaseUri();
        if($scope.register.linkType === 'shibboleth'){
            baseUri += '/shibboleth';
        }
        $.ajax({
            url: baseUri + '/registerConfirm.json',
            type: 'POST',
            data:  angular.toJson($scope.register),
            contentType: 'application/json;charset=UTF-8',
            dataType: 'json',
            success: function(data) {
                if(data != null && data.errors != null && data.errors.length > 0) {
                    $scope.generalRegistrationError = data.errors[0];
                    console.log($scope.generalRegistrationError);
                    $scope.$apply();
                    $.colorbox.close();
                } else {
                    if (basePath.startsWith(baseUrl + 'oauth')) {
                        var clientName = $('div#RegistrationCtr input[name="client_name"]').val();
                        var clientGroupName = $('div#RegistrationCtr input[name="client_group_name"]').val();
                        orcidGA.gaPush(['send', 'event', 'RegGrowth', 'New-Registration', 'OAuth '+ orcidGA.buildClientString(clientGroupName, clientName)]);
                    } else {
                        orcidGA.gaPush(['send', 'event', 'RegGrowth', 'New-Registration', 'Website']);
                    }                        
                    orcidGA.windowLocationHrefDelay(data.url);
                }                
            }
        }).fail(function() {
            // something bad is happening!
            console.log("RegistrationCtrl.postRegister() error");
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

    $scope.showProcessingColorBox = function () {
        $.colorbox({
            html : $('<div style="font-size: 50px; line-height: 60px; padding: 20px; text-align:center">' + om.get('common.processing') + '&nbsp;<i id="ajax-loader" class="glyphicon glyphicon-refresh spin green"></i></div>'),
            width: '400px',
            height:"100px",
            close: '',
            escKey:false,
            overlayClose:false,
            onComplete: function() {
                $.colorbox.resize({width:"400px" , height:"100px"});
            }
        });
    };

    $scope.showDuplicatesColorBox = function () {
        $.colorbox({
            html : $compile($('#duplicates').html())($scope),
            escKey:false,
            overlayClose:false,
            transition: 'fade',
            close: '',
            scrolling: true
            });
        $scope.$apply();
        $.colorbox.resize({width:"780px" , height:"400px"});
    };

    $scope.hideProcessingColorBox = function () {
        $.colorbox.close();
    };

    $scope.isValidClass = function (cur) {
        if (cur === undefined) return '';
        var valid = true;
        if (cur.required && (cur.value == null || cur.value.trim() == '')) valid = false;
        if (cur.errors !== undefined && cur.errors.length > 0) valid = false;
        return valid ? '' : 'text-error';
    };
        
    $scope.setRecaptchaWidgetId = function (widgetId) {  
        $scope.recaptchaWidgetId = widgetId;
    };

    $scope.setRecatchaResponse = function (response) {
        $scope.recatchaResponse = response;
    };
    
    $scope.sendReactivationEmail = function () {
        $scope.showDeactivatedError = false;
        $scope.showReactivationSent = true;
        $.ajax({
            url: getBaseUri() + '/sendReactivation.json',
            type: "POST",
            data: { email: $scope.register.email.value },
            dataType: 'json',
        }).fail(function(){
        // something bad is happening!
            console.log("error sending reactivation email");
        });
    };
    
}]);