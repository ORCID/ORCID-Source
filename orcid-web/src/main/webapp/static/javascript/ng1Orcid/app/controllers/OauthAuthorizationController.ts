declare var $: any;
declare var addShibbolethGa: any;
declare var colorbox: any;
declare var getBaseUri: any;
declare var om: any;
declare var orcidGA: any;
declare var orcidVar: any;

import * as angular from 'angular';
import {NgModule} from '@angular/core';

// This is the Angular 1 part of the module
export const OauthAuthorizationController = angular.module('orcidApp').controller(
    'OauthAuthorizationController',
    [
        '$compile', 
        '$sce', 
        '$scope', 
        'commonSrvc', 
        'vcRecaptchaService', 
        function (
            $compile,
            $sce, 
            $scope, 
            commonSrvc, 
            vcRecaptchaService
        ){
            $scope.allowEmailAccess = true;
            $scope.authorizationForm = {};
            $scope.counter = 0;
            $scope.gaString = null;
            $scope.emailTrustAsHtmlErrors = [];
            $scope.enablePersistentToken = true;
            $scope.isOrcidPresent = false;
            $scope.model = {
                key: orcidVar.recaptchaKey
            };
            $scope.personalLogin = true;
            $scope.recaptchaWidgetId = null;
            $scope.recatchaResponse = null;
            $scope.requestInfoForm = null;    
            $scope.registrationForm = {};
            $scope.scriptsInjected = false;
            $scope.showBulletIcon = false;
            $scope.showClientDescription = false;
            $scope.showCreateIcon = false;
            $scope.showDeactivatedError = false;
            $scope.showLimitedIcon = false;    
            $scope.showLongDescription = {};
            $scope.showReactivationSent = false;
            $scope.showRegisterForm = false;
            $scope.showUpdateIcon = false;    
            
            $scope.addScript = function(url, onLoadFunction){        
                var head = document.getElementsByTagName('head')[0];
                var script = document.createElement('script');
                script.src = getBaseUri() + url + '?v=' + orcidVar.version;
                script.onload =  onLoadFunction;
                head.appendChild(script); // Inject the script
            }; 

			$scope.authorize = function() {
                $scope.authorizationForm.approved = true;
                $scope.authorizeRequest();
            };

            $scope.authorizeRequest = function() {
                var auth_scope_prefix = null;
                var is_authorize = null;

                auth_scope_prefix = 'Authorize_';
                if($scope.enablePersistentToken) {
                    $scope.authorizationForm.persistentTokenEnabled=true;
                    auth_scope_prefix = 'AuthorizeP_';
                }
                if($scope.allowEmailAccess) {
                    $scope.authorizationForm.emailAccessAllowed = true;
                }
                is_authorize = $scope.authorizationForm.approved;
                $.ajax({
                    url: getBaseUri() + '/oauth/custom/authorize.json',
                    type: 'POST',
                    data: angular.toJson($scope.authorizationForm),
                    contentType: 'application/json;charset=UTF-8',
                    dataType: 'json',
                    success: function(data) {
                        if(is_authorize) {
                            for(var i = 0; i < $scope.requestInfoForm.scopes.length; i++) {
                                orcidGA.gaPush(['send', 'event', 'RegGrowth', auth_scope_prefix + $scope.requestInfoForm.scopes[i].name, 'OAuth ' + $scope.gaString]);
                            }
                        }
                        orcidGA.windowLocationHrefDelay(data.redirectUrl);
                    }
                }).fail(function() {
                    console.log("An error occured authorizing the user.");
                });
            };

            $scope.deny = function() {
                $scope.authorizationForm.approved = false;
                orcidGA.gaPush(['send', 'event', 'Disengagement', 'Authorize_Deny', 'OAuth ' + $scope.gaString]);
                $scope.authorizeRequest();
            };

            $scope.getDuplicates = function(){
                $.ajax({
                    url: getBaseUri() + '/dupicateResearcher.json?familyNames=' + $scope.registrationForm.familyNames.value + '&givenNames=' + $scope.registrationForm.givenNames.value,
                    dataType: 'json',
                    success: function(data) {
                    	$scope.duplicates = data;
                        $scope.$apply();
                        if ($scope.duplicates.length > 0 ) {
                            $scope.showDuplicatesColorBox();
                        } else {
                        	if(orcidVar.originalOauth2Process) {
            					$scope.postRegisterConfirm();
            				} else {
            					$scope.oauth2ScreensPostRegisterConfirm();
            				}                         	
                        }
                    }
                }).fail(function(){
                    // something bad is happening!
                    console.log("error fetching dupicateResearcher.json");
                    // continue to registration, as solr dup lookup failed.
                    if(orcidVar.originalOauth2Process) {
    					$scope.postRegisterConfirm();
    				} else {
    					$scope.oauth2ScreensPostRegisterConfirm();
    				}
                });
            };

            $scope.loginAndAuthorize = function() {
                $scope.authorizationForm.approved = true;
                // Fire GA sign-in-submit
                orcidGA.gaPush(['send', 'event', 'RegGrowth', 'Sign-In-Submit' , 'OAuth ' + $scope.gaString]);
                $scope.submitLogin();
            };

            $scope.loadAndInitAuthorizationForm = function() {
                $.ajax({
                    url: getBaseUri() + '/oauth/custom/authorize/empty.json',
                    type: 'GET',
                    contentType: 'application/json;charset=UTF-8',
                    dataType: 'json',
                    success: function(data) {
                        $scope.authorizationForm = data;
                    }
                }).fail(function() {
                    console.log("An error occured initializing the form.");
                });
            };

            $scope.loadAndInitLoginForm = function() {
                $scope.isOrcidPresent = false;
                $scope.showVerificationCodeFor2FA = false;
                $.ajax({
                    url: getBaseUri() + '/oauth/custom/authorize/empty.json',
                    type: 'GET',
                    contentType: 'application/json;charset=UTF-8',
                    dataType: 'json',
                    success: function(data) {
                        $scope.authorizationForm = data;                                
                        if($scope.authorizationForm.userName.value) { 
                            $scope.isOrcidPresent = true;
                            $scope.showRegisterForm = false;   
                            $scope.$broadcast("loginHasUserId", { userName: $scope.authorizationForm.userName.value });                 
                        }
                        if(!$scope.isOrcidPresent){
                            $scope.showRegisterForm = !orcidVar.showLogin;                
                        }
                        
                        $scope.$apply();
                    }
                }).fail(function() {
                    console.log("An error occured initializing the form.");
                });
            };

            $scope.loadAndInitRegistrationForm = function() {
                $.ajax({
                    url: getBaseUri() + '/oauth/custom/register/empty.json',
                    type: 'GET',
                    contentType: 'application/json;charset=UTF-8',
                    dataType: 'json',
                    success: function(data) {
                        $scope.registrationForm = data;                            
                        if($scope.registrationForm.email.value && !$scope.isOrcidPresent){
                            $scope.showRegisterForm = true;
                        }
                        $scope.$apply();
                                        
                        // special handling of deactivation error
                        $scope.$watch('registrationForm.email.errors', function(newValue, oldValue) {
                            $scope.showDeactivatedError = ($.inArray('orcid.frontend.verify.deactivated_email', $scope.registrationForm.email.errors) != -1);
                            $scope.showReactivationSent = false;
                        }); // initialize the watch
                    }
                }).fail(function() {
                    console.log("An error occured initializing the registration form.");
                });
            };

            $scope.loadRequestInfoForm = function() {
                $.ajax({
                    url: getBaseUri() + '/oauth/custom/authorize/get_request_info_form.json',
                    type: 'GET',
                    contentType: 'application/json;charset=UTF-8',
                    dataType: 'json',
                    success: function(data) {
                        angular.forEach(data.scopes, function (scope) {
                            if (scope.value == "/email/read-private") {
                                $scope.emailRequested = true;
                            } else if(scope.value.endsWith('/create')) {
                                $scope.showCreateIcon = true;
                            } else if(scope.value.endsWith('/update')) {
                                $scope.showUpdateIcon = true;
                            } else if(scope.value.endsWith('/read-limited')) {
                                $scope.showLimitedIcon = true;
                            } else {
                                $scope.showBulletIcon = true;
                            }
                        })
                                                                                                                
                        $scope.requestInfoForm = data;              
                        $scope.gaString = orcidGA.buildClientString($scope.requestInfoForm.memberName, $scope.requestInfoForm.clientName);              
                        $scope.$apply();
                    }
                }).fail(function() {
                    console.log("An error occured initializing the form.");
                });
            };

            $scope.loginAndDeny = function() {
                $scope.authorizationForm.approved = false;
                $scope.submitLogin();
            };

            $scope.loginSocial = function(idp) {
                orcidGA.gaPush(['send', 'event', 'RegGrowth', 'Sign-In-Submit' , 'OAuth ' + $scope.gaString]);
                orcidGA.gaPush(['send', 'event', 'RegGrowth', 'Sign-In-Submit-Social', idp ]);
                return false;
            };

            $scope.postRegisterConfirm = function () {
                var auth_scope_prefix = 'Authorize_';
                if($scope.enablePersistentToken){
                    auth_scope_prefix = 'AuthorizeP_';
                }
                $scope.showProcessingColorBox();
                
                $.ajax({
                    url: getBaseUri() + '/oauth/custom/registerConfirm.json',
                    type: 'POST',
                    data:  angular.toJson($scope.registrationForm),
                    contentType: 'application/json;charset=UTF-8',
                    dataType: 'json',
                    success: function(data) {
                        $scope.requestInfoForm = data;
                        if($scope.requestInfoForm.errors.length > 0) {                                  
                            $scope.generalRegistrationError = $scope.requestInfoForm.errors[0];                    
                            $scope.$apply();
                            $.colorbox.close();
                        } else {
                            orcidGA.gaPush(['send', 'event', 'RegGrowth', 'New-Registration', 'OAuth '+ $scope.gaString]);
                            if(orcidVar.originalOauth2Process) {
                                if($scope.registrationForm.approved) {
                                    for(var i = 0; i < $scope.requestInfoForm.scopes.length; i++) {
                                        orcidGA.gaPush(['send', 'event', 'RegGrowth', auth_scope_prefix + $scope.requestInfoForm.scopes[i].name, 'OAuth ' + $scope.gaString]);
                                    }
                                } else {
                                    // Fire GA register deny
                                    orcidGA.gaPush(['send', 'event', 'Disengagement', 'Authorize_Deny', 'OAuth ' + $scope.gaString]);
                                }
                            }
                            orcidGA.windowLocationHrefDelay($scope.requestInfoForm.redirectUrl);
                        }                               
                    }
                }).fail(function() {
                    // something bad is happening!
                    console.log("OauthAuthorizationController.postRegister() error");
                });
            };

            $scope.register = function() {
                if($scope.enablePersistentToken) {
                    $scope.registrationForm.persistentTokenEnabled=true;
                }
            
                if ($scope.allowEmailAccess) {
                    $scope.registrationForm.allowEmailAccess = true;
                }
                
                // Adding the response to the register object
                $scope.registrationForm.grecaptcha.value = $scope.recatchaResponse; 
                $scope.registrationForm.grecaptchaWidgetId.value = $scope.recaptchaWidgetId;
                
                $.ajax({
                    url: getBaseUri() + '/oauth/custom/register.json',
                    type: 'POST',
                    data:  angular.toJson($scope.registrationForm),
                    contentType: 'application/json;charset=UTF-8',
                    dataType: 'json',
                    success: function(data) {
                        $scope.registrationForm = data;
                        if($scope.registrationForm.approved) {
                            if ($scope.registrationForm.errors == undefined 
                                || $scope.registrationForm.errors.length == 0) {
                                $scope.showProcessingColorBox();
                                $scope.getDuplicates();
                            } else {
                                if($scope.registrationForm.email.errors.length > 0) {
                                    for(var i = 0; i < $scope.registrationForm.email.errors.length; i++){
                                        $scope.emailTrustAsHtmlErrors[0] = $sce.trustAsHtml($scope.registrationForm.email.errors[i]);
                                    }
                                } else {
                                    $scope.emailTrustAsHtmlErrors = [];
                                }
                            }
                        } else {
                            // Fire GA register deny
                            orcidGA.gaPush(['send', 'event', 'Disengagement', 'Authorize_Deny', 'OAuth ' + $scope.gaString]);                    
                            orcidGA.windowLocationHrefDelay($scope.registrationForm.redirectUrl);
                        }

                        $scope.$apply();
                    }
                }).fail(function() {
                    // something bad is happening!
                    console.log("RegistrationCtrl.postRegister() error");
                });
            };

            $scope.registerAndAuthorize = function() {
                $scope.registrationForm.approved = true;
                orcidGA.gaPush(['send', 'event', 'RegGrowth', 'New-Registration-Submit' , 'OAuth ' + $scope.gaString]);
                $scope.register();
            };

            $scope.registerAndDeny = function() {
                $scope.registrationForm.approved = false;
                $scope.register();
            };

            $scope.sendReactivationEmail = function (email) {
                $scope.showDeactivatedError = false;
                $scope.showReactivationSent = true;
                $.ajax({
                    url: getBaseUri() + '/sendReactivation.json',
                    type: "POST",
                    data: { email: email },
                    dataType: 'json',
                }).fail(function(){
                // something bad is happening!
                    console.log("error sending reactivation email");
                });
            };

            $scope.serverValidate = function (field) {
                if (field === undefined) {
                    field = '';
                }
                $.ajax({
                    url: getBaseUri() + '/oauth/custom/register/validate' + field + '.json',
                    type: 'POST',
                    data:  angular.toJson($scope.registrationForm),
                    contentType: 'application/json;charset=UTF-8',
                    dataType: 'json',
                    success: function(data) {
                        commonSrvc.copyErrorsLeft($scope.registrationForm, data);
                        if(field == 'Email') {
                            if ($scope.registrationForm.email.errors.length > 0) {
                                for(var i = 0; i < $scope.registrationForm.email.errors.length; i++){
                                    $scope.emailTrustAsHtmlErrors[0] = $sce.trustAsHtml($scope.registrationForm.email.errors[i]);
                                }
                            } else {
                                $scope.emailTrustAsHtmlErrors = [];
                            }
                        }
                        $scope.$apply();
                    }
                }).fail(function() {
                    // something bad is happening!
                    console.log("OauthAuthorizationController.serverValidate() error");
                });
            };

            $scope.setRecatchaResponse = function (response) {
                $scope.recatchaResponse = response;        
            };

            $scope.setRecaptchaWidgetId = function (widgetId) {
                $scope.recaptchaWidgetId = widgetId;        
            };

            $scope.showDeactivationError = function() {
                $scope.showDeactivatedError = true;
                $scope.showReactivationSent = false;
                $scope.$apply();
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

            $scope.showInstitutionLogin = function () {
                $scope.personalLogin = false; // Hide Personal Login
                
                if(!$scope.scriptsInjected){ // If shibboleth scripts haven't been
                                                // loaded yet.
                    $scope.addScript('/static/javascript/shibboleth-embedded-ds/1.1.0/idpselect_config.js', function(){
                        $scope.addScript('/static/javascript/shibboleth-embedded-ds/1.1.0/idpselect.js', function(){
                            $scope.scriptsInjected = true;
                            $scope.$apply();
                            addShibbolethGa($scope.gaString);
                        });
                    });
                };
            };

            $scope.showPersonalLogin = function () {        
                $scope.personalLogin = true;
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

            $scope.showToLoginForm = function() {
                if (typeof($scope.authorizationForm.userName) != 'undefined'){
                    $scope.authorizationForm.userName.value=$scope.registrationForm.email.value;
                }
                $scope.showRegisterForm = false;
            };

            $scope.submitLogin = function() {
                var auth_scope_prefix = 'Authorize_';
                if($scope.enablePersistentToken) {
                    $scope.authorizationForm.persistentTokenEnabled=true;
                    auth_scope_prefix = 'AuthorizeP_';
                }        
                if($scope.allowEmailAccess) {
                    $scope.authorizationForm.emailAccessAllowed = true;
                }
                
                $.ajax({
                    url: getBaseUri() + '/oauth/custom/login.json',
                    type: 'POST',
                    data: angular.toJson($scope.authorizationForm),
                    contentType: 'application/json;charset=UTF-8',
                    dataType: 'json',
                    success: function(data) {
                        if(data) {
                            if(data.errors.length != 0) {
                                $scope.authorizationForm = data;
                                $scope.showDeactivatedError = ($.inArray('orcid.frontend.security.orcid_deactivated', $scope.authorizationForm.errors) != -1);
                                $scope.showReactivationSent = false;
                                $scope.$apply();
                            } else if (data.verificationCodeRequired) {
                                $scope.showVerificationCodeFor2FA = true;
                                $('#2FAInstructions').show();
                            } else {
                                // Fire google GA event
                                if($scope.authorizationForm.approved) {
                                    orcidGA.gaPush(['send', 'event', 'RegGrowth', 'Sign-In' , 'OAuth ' + $scope.gaString]);
                                    for(var i = 0; i < $scope.requestInfoForm.scopes.length; i++) {
                                        orcidGA.gaPush(['send', 'event', 'RegGrowth', auth_scope_prefix + $scope.requestInfoForm.scopes[i].name, 'OAuth ' + $scope.gaString]);
                                    }
                                } else {
                                    // Fire GA authorize-deny
                                    orcidGA.gaPush(['send', 'event', 'Disengagement', 'Authorize_Deny', 'OAuth ' + $scope.gaString]);
                                }
                                orcidGA.windowLocationHrefDelay(data.redirectUrl);
                            }
                        } else {
                            console.log("Error authenticating the user");
                        }

                    }
                }).fail(function() {
                    console.log("An error occured authenticating the user.");
                });
            };
            
            $('#enterRecoveryCode').click(function() {
                 $('#recoveryCodeSignin').show(); 
            });

            $scope.switchForm = function() {
                $scope.showRegisterForm = !$scope.showRegisterForm;
                if (!$scope.personalLogin) {
                    $scope.personalLogin = true;
                }
            };

            $scope.toggleClientDescription = function() {
                $scope.showClientDescription = !$scope.showClientDescription;
            };

            $scope.toggleLongDescription = function(orcid_scope) {              
                $scope.showLongDescription[orcid_scope] = !$scope.showLongDescription[orcid_scope];
            };

            $scope.updateActivitiesVisibilityDefault = function(priv, $event) {
                $scope.registrationForm.activitiesVisibilityDefault.visibility = priv;
            };
            
            window.onkeydown = function(e) {
                if (e.keyCode == 13) {     
                	if(orcidVar.originalOauth2Process) { 
	                	if (location.pathname.indexOf('/oauth/signin') !== -1){ 
	                        if ($scope.showRegisterForm == true){
	                            $scope.registerAndAuthorize();                  
	                        } else{
	                            $scope.loginAndAuthorize();                 
	                        }               
	                    } else{
	                    	$scope.authorize();
	                    }
                    }
                }
            }; 
            
            /////////////////////
            // Oauth 2 screens //
            /////////////////////
            $scope.oauth2ScreensLoadRegistrationForm = function() {
            	$.ajax({
                    url: getBaseUri() + '/register.json',
                    type: 'GET',
                    contentType: 'application/json;charset=UTF-8',
                    dataType: 'json',
                    success: function(data) {
                        $scope.registrationForm = data;                            
                        
                        $scope.$apply();
                                        
                        // special handling of deactivation error
                        $scope.$watch('registrationForm.email.errors', function(newValue, oldValue) {
                            $scope.showDeactivatedError = ($.inArray('orcid.frontend.verify.deactivated_email', $scope.registrationForm.email.errors) != -1);
                            $scope.showReactivationSent = false;
                        }); // initialize the watch
                    }
                }).fail(function() {
                    console.log("An error occured initializing the registration form.");
                });
            };
            
            $scope.oauth2ScreensRegister = function() {
                // Adding the response to the register object
                $scope.registrationForm.grecaptcha.value = $scope.recatchaResponse; 
                $scope.registrationForm.grecaptchaWidgetId.value = $scope.recaptchaWidgetId;
                
                $.ajax({
                    url: getBaseUri() + '/register.json',
                    type: 'POST',
                    data:  angular.toJson($scope.registrationForm),
                    contentType: 'application/json;charset=UTF-8',
                    dataType: 'json',
                    success: function(data) {
                    	$scope.registrationForm = data;
                        if ($scope.registrationForm.errors == undefined 
                            || $scope.registrationForm.errors.length == 0) {                            
                            $scope.showProcessingColorBox();                            
                            $scope.getDuplicates();
                        } else {
                            if($scope.registrationForm.email.errors.length > 0) {
                                for(var i = 0; i < $scope.registrationForm.email.errors.length; i++){
                                    $scope.emailTrustAsHtmlErrors[0] = $sce.trustAsHtml($scope.registrationForm.email.errors[i]);
                                }
                            } else {
                                $scope.emailTrustAsHtmlErrors = [];
                            }
                        }
                        

                        $scope.$apply();
                    }
                }).fail(function() {
                    // something bad is happening!
                    console.log("RegistrationCtrl.postRegister() error");
                });
            }; 
            
            $scope.oauth2ScreensPostRegisterConfirm = function() {
            	var baseUri = getBaseUri();
            	
            	if($scope.registrationForm.linkType === 'shibboleth'){
                    baseUri += '/shibboleth';
                }
            	
            	var auth_scope_prefix = 'Authorize_';
                if($scope.enablePersistentToken){
                    auth_scope_prefix = 'AuthorizeP_';
                }
                $scope.showProcessingColorBox();
                $scope.registrationForm.valNumClient = $scope.registrationForm.valNumServer / 2;
                
                $.ajax({
                    url: baseUri + '/registerConfirm.json',
                    type: 'POST',
                    data:  angular.toJson($scope.registrationForm),
                    contentType: 'application/json;charset=UTF-8',
                    dataType: 'json',
                    success: function(data) {
                        if(data != null && data.errors != null && data.errors.length > 0) {
                            //TODO: Display error in the page
                            $scope.generalRegistrationError = data.errors[0];
                            $scope.$apply();
                            $.colorbox.close();
                        } else {
                            //TODO: Add GA code
							orcidGA.windowLocationHrefDelay(data.url);
                        }                           
                    }
                }).fail(function() {
                    // something bad is happening!
                    console.log("OauthAuthorizationController.postRegister() error");
                });	
            };

            // Init
            if(orcidVar.oauth2Screens) {
                $scope.showRegisterForm = !orcidVar.showLogin;
                if(!$scope.showRegisterForm && orcidVar.oauthUserId){
                    $scope.authorizationForm = {
                        userName:  {value: orcidVar.oauthUserId}
                    } 
                }
            }
            if(orcidVar.originalOauth2Process) {                
                $scope.loadRequestInfoForm();
            }                     
        }
    ]
);

// This is the Angular 2 part of the module
@NgModule({})
export class OauthAuthorizationControllerNg2Module {}