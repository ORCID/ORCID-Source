declare var $: any;
declare var colorbox: any;
declare var getBaseUri: any;
declare var logAjaxError: any;
declare var orcidVar: any;
declare var rUri: any;

import * as angular from 'angular';
import {NgModule} from '@angular/core';

// This is the Angular 1 part of the module
export const PublicClientCtrl = angular.module('orcidApp').controller(
    'PublicClientCtrl',
    [
        '$compile', 
        '$sce', 
        '$scope', 
        'emailSrvc', 
        function (
            $compile, 
            $sce, 
            $scope, 
            emailSrvc
        ) {
            $scope.accepted=false;
            $scope.authorizeURL = '';
            $scope.authorizeUrlBase = getBaseUri() + '/oauth/authorize';
            $scope.authorizeURLTemplate = $scope.authorizeUrlBase + '?client_id=[CLIENT_ID]&response_type=code&scope=/authenticate&redirect_uri=[REDIRECT_URI]';
            $scope.creating = false;
            $scope.editing = false;
            $scope.expanded = false;   
            $scope.viewing = false; 
            $scope.googleExampleLink = 'https://developers.google.com/oauthplayground/#step1&scopes=/authenticate&oauthEndpointSelect=Custom&oauthAuthEndpointValue=[BASE_URI_ENCODE]/oauth/authorize&oauthTokenEndpointValue=[BASE_URI_ENCODE]/oauth/token&oauthClientId=[CLIENT_ID]&oauthClientSecret=[CLIENT_SECRET]&accessTokenType=bearer';
            $scope.googleUri = 'https://developers.google.com/oauthplayground';
            $scope.googleExampleLinkOpenID = 'https://developers.google.com/oauthplayground/#step1&scopes=openid&url=https%3A%2F%2F&content_type=application%2Fjson&http_method=GET&oauthEndpointSelect=Custom&oauthAuthEndpointValue=[BASE_URI_ENCODE]/oauth/authorize&includeCredentials=unchecked&accessTokenType=query&response_type=token&oauthClientId=[CLIENT_ID]';
            $scope.hideGoogleUri = false;
            $scope.hideSwaggerMemberUri = false;
            $scope.hideSwaggerUri = false;
            $scope.noCredentialsYet = true;
            $scope.playgroundExample = '';
            $scope.sampleAuthCurl = '';
            $scope.sampleAuthCurlTemplate = "curl -i -L -k -H 'Accept: application/json' --data 'client_id=[CLIENT_ID]&client_secret=[CLIENT_SECRET]&grant_type=authorization_code&redirect_uri=[REDIRECT_URI]&code=REPLACE WITH OAUTH CODE' [BASE_URI]/oauth/token";
            $scope.swaggerUri = orcidVar.pubBaseUri +"/v2.0/";
            $scope.swaggerMemberUri = $scope.swaggerUri.replace("pub","api");
            $scope.tokenURL = getBaseUri() + '/oauth/token';
            $scope.userCredentials = null;
            $scope.selectedRedirectUri = '';
            $scope.emailSrvc = emailSrvc;
            $scope.nameToDisplay = '';
            $scope.descriptionToDisplay = '';
            $scope.verifyEmailSent=false;
            
            $scope.sampleOpenId = '';
            $scope.sampleOpenIdTemplate = $scope.authorizeUrlBase + '?client_id=[CLIENT_ID]&response_type=token&scope=openid&redirect_uri=[REDIRECT_URI]';
            
            $scope.addTestRedirectUri = function(type) {  
            	var rUri = null;
        		if(type == 'google'){
                    rUri = $scope.googleUri;
                    $scope.hideGoogleUri = true;
                } else if(type == 'swagger'){
                    rUri = $scope.swaggerUri;
                    $scope.hideSwaggerUri = true;
                } else if(type == 'swagger-member'){
                    rUri = $scope.swaggerMemberUri;
                    $scope.hideSwaggerMemberUri = true;
                }
                
        		if($scope.userCredentials.redirectUris.length == 1 && $scope.userCredentials.redirectUris[0].value.value == null) {
                    $scope.userCredentials.redirectUris[0].value.value = rUri;
                } else {
                 	$scope.addRedirectURI(rUri);   
                }                
            };

            $scope.closeModal = function() {
                $.colorbox.close();
            };

            $scope.collapse = function(){
                $scope.expanded = false;
            };

            $scope.confirmResetClientSecret = function() {
                $scope.clientSecretToReset = $scope.userCredentials.clientSecret;
                $.colorbox({
                    html : $compile($('#reset-client-secret-modal').html())($scope),
                    transition: 'fade',
                    onLoad: function() {
                        $('#cboxClose').remove();
                    },
                    scrolling: true
                });
                $.colorbox.resize({width:"415px" , height:"250px"});
            };

            $scope.deleteRedirectUri = function(idx) {
                $scope.userCredentials.redirectUris.splice(idx, 1);
                $scope.hideGoogleUri = false;
                $scope.hideSwaggerUri = false;
                $scope.hideSwaggerMemberUri = false;
                for(var i = 0; i < $scope.userCredentials.redirectUris.length; i++) {
                    if($scope.googleUri == $scope.userCredentials.redirectUris[i].value.value) {
                        $scope.hideGoogleUri = true;
                    }else if ($scope.swaggerUri == $scope.userCredentials.redirectUris[i].value.value){
                        $scope.hideSwaggerUri = true;
                    }else if ($scope.swaggerMemberUri == $scope.userCredentials.redirectUris[i].value.value){
                        $scope.hideSwaggerMemberUri = true;
                    }
                }
            };

            $scope.editClientCredentials = function() {
                $.ajax({
                    url: getBaseUri()+'/developer-tools/update-user-credentials.json',
                    contentType: 'application/json;charset=UTF-8',
                    type: 'POST',
                    dataType: 'json',
                    data: angular.toJson($scope.userCredentials),
                    success: function(data){
                        $scope.$apply(function(){
                            $scope.playgroundExample = '';
                            $scope.userCredentials = data;
                            if(data.errors.length != 0){
                                // SHOW ERROR
                            } else {
                                $scope.editing = false;
                                $scope.hideGoogleUri = false;
                                $scope.hideSwaggerUri = false;
                                $scope.hideSwaggerMemberUri = false;
                                $scope.selectedRedirectUri = $scope.userCredentials.redirectUris[0];
                                for(var i = 0; i < $scope.userCredentials.redirectUris.length; i++) {
                                    if($scope.googleUri == $scope.userCredentials.redirectUris[i].value.value) {
                                        $scope.hideGoogleUri = true;
                                    }else if ($scope.swaggerUri == $scope.userCredentials.redirectUris[i].value.value){
                                        $scope.hideSwaggerUri = true;
                                    }else if ($scope.swaggerMemberUri == $scope.userCredentials.redirectUris[i].value.value){
                                        $scope.hideSwaggerMemberUri = true;
                                    }

                                    if($scope.userCredentials.redirectUris[i].value.value < $scope.selectedRedirectUri.value.value) {
                                        $scope.selectedRedirectUri = $scope.userCredentials.redirectUris[i];
                                    }
                                }

                                $scope.updateSelectedRedirectUri();
                                $scope.setHtmlTrustedNameAndDescription();
                            }
                        });
                    }
                }).fail(function(error) {
                    // something bad is happening!
                    console.log("Error updating SSO credentials");
                });
            };            

            $scope.expand =  function(){
                $scope.expanded = true;
            };

            $scope.inputTextAreaSelectAll = function($event){
                $event.target.select();
            };

            $scope.resetClientSecret = function() {     
                $.ajax({
                    url: getBaseUri() + '/developer-tools/reset-client-secret.json',
                    type: 'POST',
                    data: $scope.userCredentials.clientId.value,
                    contentType: 'application/json;charset=UTF-8',
                    dataType: 'text',
                    success: function(data) {
                        if(data) {
                            $scope.editing = false;
                            $scope.closeModal();
                            $scope.getSSOCredentials();
                        } else
                            console.log('Unable to reset client secret');
                    }
                }).fail(function() {
                    console.log("Error resetting redirect uri");
                });
            };

            $scope.revoke = function() {
                $.ajax({
                    url: getBaseUri()+'/developer-tools/revoke-sso-credentials.json',
                    contentType: 'application/json;charset=UTF-8',
                    type: 'POST',
                    success: function(){
                        $scope.$apply(function(){
                            $scope.userCredentials = null;
                            $scope.closeModal();
                            $scope.showReg = true;
                        });
                    }
                }).fail(function(error) {
                    // something bad is happening!
                    console.log("Error revoking SSO credentials");
                });
            };

            $scope.showEditLayout = function() {
                // Hide the testing tools if they are already added
                for(var i = 0; i < $scope.userCredentials.redirectUris.length; i++) {
                    if($scope.googleUri == $scope.userCredentials.redirectUris[i].value.value) {
                        $scope.hideGoogleUri=true;
                    }else if ($scope.swaggerUri == $scope.userCredentials.redirectUris[i].value.value){
                        $scope.hideSwaggerUri = true;
                    }else if ($scope.swaggerMemberUri == $scope.userCredentials.redirectUris[i].value.value){
                        $scope.hideSwaggerMemberUri = true;
                    } 
                }
                $scope.viewing = false;                                
                $scope.editing = true;
                $('.developer-tools .slidebox').slideDown();
                $('.tab-container .collapsed').css('display', 'none');
                $('.tab-container .expanded').css('display', 'inline').parent().css('background','#EBEBEB');
            };

            $scope.showRevokeModal = function() {
                $.colorbox({
                    html : $compile($('#revoke-sso-credentials-modal').html())($scope),
                        onLoad: function() {
                        $('#cboxClose').remove();
                    }
                });

                $.colorbox.resize({width:"450px" , height:"230px"});
            };

            $scope.showViewLayout = function() {
                // Reset the credentials
                $scope.getSSOCredentials();
                $scope.editing = false;
                $scope.creating = false;
                $('.edit-details .slidebox').slideDown();
            };

            $scope.getSSOCredentials = function() {
                $.ajax({
                    url: getBaseUri()+'/developer-tools/get-client.json',
                    contentType: 'application/json;charset=UTF-8',
                    type: 'GET',
                    success: function(data){
                        $scope.$apply(function(){
                            if(data != null && data.clientSecret != null) {
                                $scope.userCredentials = data;
                                $scope.playgroundExample = '';
                                $scope.viewing = true;
                                $scope.hideGoogleUri = false;
                                $scope.hideSwaggerUri = false;
                                $scope.hideSwaggerMemberUri = false;
                                $scope.creating = false;
                                $scope.selectedRedirectUri = $scope.userCredentials.redirectUris[0];
                                for(var i = 0; i < $scope.userCredentials.redirectUris.length; i++) {
                                    if($scope.googleUri == $scope.userCredentials.redirectUris[i].value.value) {
                                        $scope.hideGoogleUri = true;
                                    }else if ($scope.swaggerUri == $scope.userCredentials.redirectUris[i].value.value){
                                        $scope.hideSwaggerUri = true;
                                    }else if ($scope.swaggerMemberUri == $scope.userCredentials.redirectUris[i].value.value){
                                        $scope.hideSwaggerMemberUri = true;
                                    }

                                    if($scope.userCredentials.redirectUris[i].value.value < $scope.selectedRedirectUri.value.value) {
                                        $scope.selectedRedirectUri = $scope.userCredentials.redirectUris[i];
                                    }
                                }
                                $scope.updateSelectedRedirectUri();
                                $scope.setHtmlTrustedNameAndDescription();
                            } else {
                            	$scope.createCredentialsLayout();                                
                            }
                        });
                    }
                }).fail(function(e) {
                    // something bad is happening!
                    console.log("Error obtaining SSO credentials");
                    logAjaxError(e);
                });
            };

            $scope.updateSelectedRedirectUri = function() {
                var clientId = $scope.userCredentials.clientId.value;
                var example = null;
                var exampleOIDC = null;
                var sampeleCurl = null;
                var sampleOIDC = null;
                var selectedRedirectUriValue = $scope.selectedRedirectUri.value.value;
                var selectedClientSecret = $scope.userCredentials.clientSecret.value;

                // Build the google playground url example
                $scope.playgroundExample = '';

                if($scope.googleUri == selectedRedirectUriValue) {
                    example = $scope.googleExampleLink;
                    example = example.replace('[BASE_URI_ENCODE]', encodeURI(getBaseUri()));
                    example = example.replace('[CLIENT_ID]', clientId);
                    example = example.replace('[CLIENT_SECRET]', selectedClientSecret);
                    $scope.playgroundExample = example;
                    exampleOIDC = $scope.googleExampleLinkOpenID;
                    exampleOIDC = exampleOIDC.replace('[BASE_URI_ENCODE]', encodeURI(getBaseUri()));
                    exampleOIDC = exampleOIDC.replace('[CLIENT_ID]', clientId);
                    $scope.googleExampleLinkOpenID = exampleOIDC;                    
                }else if($scope.swaggerUri == selectedRedirectUriValue) {
                    $scope.playgroundExample = $scope.swaggerUri;
                }else if($scope.swaggerMemberUri == selectedRedirectUriValue) {
                    $scope.playgroundExample = $scope.swaggerMemberUri;
                }
                
                example = $scope.authorizeURLTemplate;
                example = example.replace('[BASE_URI]', orcidVar.baseUri);
                example = example.replace('[CLIENT_ID]', clientId);
                example = example.replace('[REDIRECT_URI]', selectedRedirectUriValue);
                $scope.authorizeURL = example;

                // rebuild sampel Auhtroization Curl
                sampeleCurl = $scope.sampleAuthCurlTemplate;
                $scope.sampleAuthCurl = sampeleCurl.replace('[CLIENT_ID]', clientId)
                    .replace('[CLIENT_SECRET]', selectedClientSecret)
                    .replace('[BASE_URI]', orcidVar.baseUri)
                    .replace('[REDIRECT_URI]', selectedRedirectUriValue);
                
                sampleOIDC = $scope.sampleOpenIdTemplate;
                $scope.sampleOpenId = sampleOIDC
                  .replace('[CLIENT_ID]', clientId)
                  .replace('[REDIRECT_URI]', selectedRedirectUriValue);
                console.log($scope.sampleOpenIdTemplate);
                console.log($scope.sampleOpenId);
            };
            
            $scope.setHtmlTrustedNameAndDescription = function() {
                // Trust client name and description as html since it has been already
                // filtered
                $scope.nameToDisplay = $sce.trustAsHtml($scope.userCredentials.displayName.value);
                $scope.descriptionToDisplay = $sce.trustAsHtml($scope.userCredentials.shortDescription.value);
            };
            
            $scope.verifyEmail = function() {
                var funct = function() {
                    $scope.verifyEmailObject = emailSrvc.primaryEmail;
                    emailSrvc.verifyEmail(emailSrvc.primaryEmail,function(data) {
                        $scope.verifyEmailSent = true;    
                        $scope.$apply();                    
                   });            
                };
                if (emailSrvc.primaryEmail == null){
                    emailSrvc.getEmails(funct);
                }
                else {
                   funct();
                }
            };
            
            $scope.acceptTerms = function() {
                $scope.mustAcceptTerms = false;
                $scope.accepted = false;
                $.colorbox({
                    html : $compile($('#terms-and-conditions-modal').html())($scope),
                    scrolling: true,
                    onLoad: function() {
                        $('#cboxClose').remove();
                    }
                });

                $.colorbox.resize({width:"590px"});
            };
            
            $scope.enableDeveloperTools = function() {
                if($scope.accepted == true) {
                    $scope.mustAcceptTerms = false;
                    $.ajax({
                        url: getBaseUri()+'/developer-tools/enable-developer-tools.json',
                        contentType: 'application/json;charset=UTF-8',
                        type: 'POST',
                        success: function(data){
                            if(data == true){
                                window.location.href = getBaseUri()+'/developer-tools';
                            };
                        }
                    }).fail(function(error) {
                        // something bad is happening!
                        console.log("Error enabling developer tools");
                    });
                } else {
                    $scope.mustAcceptTerms = true;
                }        
            };
            
            $scope.getClientUrl = function(userCredentials) {
                if(typeof userCredentials != undefined 
                    && userCredentials != null 
                    && userCredentials.website != null 
                    && userCredentials.website.value != null) 
                {
                    if(userCredentials.website.value.lastIndexOf('http://') === -1 && userCredentials.website.value.lastIndexOf('https://') === -1) {
                        return '//' + userCredentials.website.value;
                    } else {
                        return userCredentials.website.value;
                    }
                }
                return '';
            };
            
            $scope.createCredentialsLayout = function(){
            	 $.ajax({
                     url: getBaseUri() + '/developer-tools/client.json',
                     dataType: 'json',
                     success: function(data) {
                         $scope.$apply(function(){
                             $scope.hideGoogleUri = false;
                             $scope.hideSwaggerUri = false;
                             $scope.hideSwaggerMemberUri = false;
                             $scope.creating = true;
                             $scope.noCredentialsYet = true;
                             $scope.userCredentials = data;
                         });
                     }
                 }).fail(function() {
                     console.log("Error fetching client");
                 });
             };
            
            $scope.submit = function() {
                $.ajax({
                    url: getBaseUri()+'/developer-tools/create-client.json',
                    contentType: 'application/json;charset=UTF-8',
                    type: 'POST',
                    dataType: 'json',
                    data: angular.toJson($scope.userCredentials),
                    success: function(data){
                        $scope.$apply(function(){
                            $scope.playgroundExample = '';
                            $scope.userCredentials = data;
                            if(data.errors.length != 0){
                                // SHOW ERROR
                                console.log(angular.toJson(data.errors));
                            } else {
                                $scope.hideGoogleUri = false;
                                $scope.hideSwaggerUri = false;
                                $scope.hideSwaggerMemberUri = false;
                                $scope.selectedRedirectUri = $scope.userCredentials.redirectUris[0];
                                for(var i = 0; i < $scope.userCredentials.redirectUris.length; i++) {
                                    if($scope.googleUri == $scope.userCredentials.redirectUris[i].value.value) {
                                        $scope.hideGoogleUri = true;
                                    }else if ($scope.swaggerUri == $scope.userCredentials.redirectUris[i].value.value){
                                        $scope.hideSwaggerUri = true;
                                    }else if ($scope.swaggerMemberUri == $scope.userCredentials.redirectUris[i].value.value){
                                        $scope.hideSwaggerMemberUri = true;
                                    }

                                    if($scope.userCredentials.redirectUris[i].value.value < $scope.selectedRedirectUri.value.value) {
                                        $scope.selectedRedirectUri = $scope.userCredentials.redirectUris[i];
                                    }
                                }
                                $scope.updateSelectedRedirectUri();
                                $scope.setHtmlTrustedNameAndDescription();
                                $scope.creating = false;
                                $scope.noCredentialsYet = false;
                                $scope.viewing = true;
                            }
                        });
                    }
                }).fail(function(error) {
                    // something bad is happening!
                    console.log("Error creating SSO credentials");
                });
            };
            
            $scope.addRedirectURI = function(defaultValue) {
            	defaultValue = (typeof defaultValue != undefined && defaultValue != null) ? defaultValue : '';            	
				$scope.userCredentials.redirectUris.push({value: {value: defaultValue}, type: {value: 'sso-authentication'}});                
            };            
            
            // init
            $scope.getSSOCredentials();
        }
    ]
);

// This is the Angular 2 part of the module
@NgModule({})
export class PublicClientCtrlNg2Module {}