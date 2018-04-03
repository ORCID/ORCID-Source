//Migrated

declare var $: any;
declare var basePath: any;
declare var baseUrl: any;
declare var addShibbolethGa: any;
declare var colorbox: any;
declare var getBaseUri: any;
declare var getStaticCdnPath: any;
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
        '$timeout', 
        'commonSrvc', 
        'vcRecaptchaService',
        'utilsService', 
        function (
            $compile,
            $sce, 
            $scope,
            $timeout, 
            commonSrvc, 
            vcRecaptchaService,
            utilsService
        ){
            $scope.allowEmailAccess = true;
            $scope.authorizationForm = {};
            $scope.counter = 0;
            $scope.gaString = null;
            $scope.emailTrustAsHtmlErrors = [];
            $scope.enablePersistentToken = true;
            $scope.errorEmail = null;
            $scope.errorEmailsAdditional = [];
            $scope.isOrcidPresent = false;
            $scope.model = {
                key: orcidVar.recaptchaKey
            };
            $scope.oauthSignin = false;
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
            $scope.showDuplicateEmailError = false;
            $scope.showEmailsAdditionalDeactivatedError = [false];
            $scope.showEmailsAdditionalDuplicateEmailError = [false];
            $scope.showEmailsAdditionalReactivationSent = [false];
            $scope.showLimitedIcon = false;    
            $scope.showLongDescription = {};
            $scope.showReactivationSent = false;
            $scope.showRegisterForm = false;
            $scope.showUpdateIcon = false;    
            
            $scope.addScript = function(url, onLoadFunction){        
                var head = document.getElementsByTagName('head')[0];
                var script = document.createElement('script');
                script.src = getStaticCdnPath() + url;
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

            $scope.getAffiliations = function(dup){
                if(!dup['affiliationsRequestSent']){
                    dup['affiliationsRequestSent'] = true;
                    dup['institution'] = [];
                    var orcid = dup.orcid;
                    var url = orcidVar.pubBaseUri + '/v2.1/' + orcid + '/activities';
                    $.ajax({
                        url: url,
                        dataType: 'json',
                        headers: { Accept: 'application/json'},
                        success: function(data) {
                            if(data.employments){
                                for(var i in data.employments['employment-summary']){
                                    if (dup['institution'].indexOf(data.employments['employment-summary'][i]['organization']['name']) < 0){
                                        dup['institution'].push(data.employments['employment-summary'][i]['organization']['name']);
                                    }
                                }
                            }
                            if(data.educations){
                                for(var i in data.educations['education-summary']){
                                    if (dup['institution'].indexOf(data.educations['education-summary'][i]['organization']['name']) < 0){
                                        dup['institution'].push(data.educations['education-summary'][i]['organization']['name']);
                                    }
                                }
                            }
                        }
                    }).fail(function(){
                        // something bad is happening!
                        console.log("error getting name for " + orcid);
                    });  
                } 
                return dup['institution'].join(", "); 
            };

            $scope.getDuplicates = function(){
                $.ajax({
                    url: getBaseUri() + '/dupicateResearcher.json?familyNames=' + $scope.registrationForm.familyNames.value + '&givenNames=' + $scope.registrationForm.givenNames.value,
                    dataType: 'json',
                    success: function(data) {
                    	var diffDate = new Date();
                        $scope.duplicates = data;
                        $scope.$apply();
                        // reg was filled out to fast reload the page
                        if ($scope.loadTime + 5000 > diffDate.getTime()) {
                            window.location.reload();
                            return;
                        }
                        if ($scope.duplicates.length > 0 ) {
                            $scope.showDuplicatesColorBox();
                        } else {
            				$scope.oauth2ScreensPostRegisterConfirm();                        	
                        }
                    }
                }).fail(function(){
                    // something bad is happening!
                    console.log("error fetching dupicateResearcher.json");
                    // continue to registration, as solr dup lookup failed.
    				$scope.oauth2ScreensPostRegisterConfirm();
                });
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

            $scope.loadRequestInfoForm = function() {
                $.ajax({
                    url: getBaseUri() + '/oauth/custom/authorize/get_request_info_form.json',
                    type: 'GET',
                    contentType: 'application/json;charset=UTF-8',
                    dataType: 'json',
                    success: function(data) {
                        $timeout(function() {
                            if(data){
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
                            }
                        });
                    }
                }).fail(function() {
                    console.log("An error occured initializing the form.");
                });
            };

            $scope.loginSocial = function(idp) {
                if($scope.gaString){
                    orcidGA.gaPush(['send', 'event', 'RegGrowth', 'Sign-In-Submit' , 'OAuth ' + $scope.gaString]);
                } else {
                    orcidGA.gaPush(['send', 'event', 'RegGrowth', 'Sign-In-Submit' , 'Website']);
                }
                orcidGA.gaPush(['send', 'event', 'RegGrowth', 'Sign-In-Submit-Social', idp ]);
                return false;
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

            $scope.sendEmailsAdditionalReactivationEmail = function (index) {
                $scope.showEmailsAdditionalDeactivatedError.splice(index, 1, false);
                $scope.showEmailsAdditionalReactivationSent.splice(index, 1, true);
                $.ajax({
                    url: getBaseUri() + '/sendReactivation.json',
                    type: "POST",
                    data: { email: $scope.registrationForm.emailsAdditional[index].value },
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
                                $scope.errorEmail = data.email.value;
                                for(var i = 0; i < $scope.registrationForm.email.errors.length; i++){
                                    $scope.emailTrustAsHtmlErrors[0] = $sce.trustAsHtml($scope.registrationForm.email.errors[i]);
                                }
                            } else {
                                $scope.emailTrustAsHtmlErrors = [];
                            }
                        }
                        if(field == 'EmailsAdditional') {
                            for (var index in $scope.registrationForm.emailsAdditional) {
                                if ($scope.registrationForm.emailsAdditional[index].errors.length > 0) {
                                    $scope.errorEmailsAdditional[index] = data.emailsAdditional[index].value;
                                }
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
                    $scope.addScript('/javascript/shibboleth-embedded-ds/1.1.0/idpselect_config.js', function(){
                        $scope.addScript('/javascript/shibboleth-embedded-ds/1.1.0/idpselect.js', function(){
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

            $scope.addEmailField = function () {
                $scope.registrationForm.emailsAdditional.push({value: ''});
                $scope.focusIndex = $scope.registrationForm.emailsAdditional.length-1;
            };  

            $scope.removeEmailField = function (index) {
                $scope.registrationForm.emailsAdditional.splice(index, 1);
            }; 
            
            window.onkeydown = function(e) {
                if (e.keyCode == 13) {     
                	if(orcidVar.originalOauth2Process) { 
	                    $scope.authorize();
                    }
                }
            }; 
        
            $scope.oauth2ScreensLoadRegistrationForm = function(givenName, familyName, email, linkFlag) {
            	$.ajax({
                    url: getBaseUri() + '/register.json',
                    type: 'GET',
                    contentType: 'application/json;charset=UTF-8',
                    dataType: 'json',
                    success: function(data) {
                        $scope.registrationForm = data;
                        if (orcidVar.features['GDPR_UI'] == true){
                            $scope.registrationForm.activitiesVisibilityDefault.visibility = null;
                        }
                        if(givenName || familyName || email || linkFlag){
                            $scope.registrationForm.givenNames.value=givenName;
                            $scope.registrationForm.familyNames.value=familyName;
                            $scope.registrationForm.email.value=email; 
                        }                         
                        $scope.registrationForm.linkType=linkFlag;
                        $scope.$apply();
                                        
                        // special handling of deactivate and duplicate errors for primary email
                        $scope.$watch('registrationForm.email.errors', function(newValue, oldValue) {
                            $scope.showDeactivatedError = ($.inArray('orcid.frontend.verify.deactivated_email', $scope.registrationForm.email.errors) != -1);
                            $scope.showReactivationSent = false;
                            $scope.showDuplicateEmailError = ($.inArray('orcid.frontend.verify.duplicate_email', $scope.registrationForm.email.errors) != -1);
                            if($scope.showDuplicateEmailError==true){
                                $scope.authorizationForm.userName.value = $scope.registrationForm.email.value;
                            }
                        }); // initialize the watch

                        // special handling of duplicate error for additional emails
                        $scope.$watch('registrationForm.emailsAdditional', function(newValue, oldValue) {
                            for (var index in $scope.registrationForm.emailsAdditional) {
                                $scope.showEmailsAdditionalDeactivatedError.splice(index, 1, ($.inArray('orcid.frontend.verify.deactivated_email', $scope.registrationForm.emailsAdditional[index].errors) != -1));
                                $scope.showEmailsAdditionalReactivationSent.splice(index, 1, false);
                                $scope.showEmailsAdditionalDuplicateEmailError.splice(index, 1, ($.inArray('orcid.frontend.verify.duplicate_email', $scope.registrationForm.emailsAdditional[index].errors) != -1));
                                if($scope.showEmailsAdditionalDuplicateEmailError[index]==true){
                                    $scope.authorizationForm.userName.value = $scope.registrationForm.emailsAdditional[index].value;
                                }
                            }                              
                        }, true); // initialize the watch
                    }
                }).fail(function() {
                    console.log("An error occured initializing the registration form.");
                });
            };
            
            $scope.oauth2ScreensRegister = function(linkFlag) {
                var baseUri = getBaseUri();

                if ($scope.gaString) {
                    $scope.registrationForm.referredBy = $scope.requestInfoForm.clientId;
                    $scope.registrationForm.creationType.value = "Member-referred";
                    orcidGA.gaPush(['send', 'event', 'RegGrowth', 'New-Registration-Submit' , 'OAuth ' + $scope.gaString]);
                } else {
                    orcidGA.gaPush(['send', 'event', 'RegGrowth', 'New-Registration-Submit', 'Website']);
                    $scope.registrationForm.creationType.value = "Direct";
                } 

                // Adding the response to the register object
                $scope.registrationForm.grecaptcha.value = $scope.recatchaResponse; 
                $scope.registrationForm.grecaptchaWidgetId.value = $scope.recaptchaWidgetId;
                
                $scope.registrationForm.linkType = linkFlag;
                $.ajax({
                    url: baseUri + '/register.json',
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
                            if ($scope.registrationForm.grecaptcha.errors.length == 0) {
                                angular.element(document.querySelector('#recaptcha')).remove();
                            }
                        }
   
                        $scope.$apply();
                    }
                }).fail(function() {
                    // something bad is happening!
                    console.log("oauth2ScreensRegister() error");
                });
            }; 
            
            $scope.oauth2ScreensPostRegisterConfirm = function() {
            	var baseUri = getBaseUri();
            	
            	if($scope.registrationForm.linkType === 'shibboleth'){
                    baseUri += '/shibboleth';
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
                            if ($scope.gaString){
                                orcidGA.gaPush(['send', 'event', 'RegGrowth', 'New-Registration', 'OAuth ' + $scope.gaString]);
                            } else {
                                orcidGA.gaPush(['send', 'event', 'RegGrowth', 'New-Registration', 'Website']);
                            } 
							orcidGA.windowLocationHrefDelay(data.url);
                        }                           
                    }
                }).fail(function() {
                    // something bad is happening!
                    console.log("OauthAuthorizationController.postRegister() error");
                });	
            };

            // Init
            $scope.loadRequestInfoForm();
            if(orcidVar.oauth2Screens) {
                if(orcidVar.oauthUserId && orcidVar.showLogin){
                    $scope.showRegisterForm = false;
                    $scope.authorizationForm = {
                        userName:  {value: orcidVar.oauthUserId}
                    } 
                } else{
                    $scope.showRegisterForm = !orcidVar.showLogin;
                    $scope.authorizationForm = {
                        userName:  {value: ""}
                    } 
                }
            } else {
                if(orcidVar.showLogin){
                    $scope.showRegisterForm = false;
                } else{
                    $scope.showRegisterForm = !orcidVar.showLogin;  
                }
                $scope.authorizationForm = {
                    userName:  {value: ""}
                } 
            }               
        }
    ]
);

// This is the Angular 2 part of the module
@NgModule({})
export class OauthAuthorizationControllerNg2Module {}