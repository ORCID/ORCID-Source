angular.module('orcidApp').controller('SSOPreferencesCtrl',['$scope', '$compile', '$sce', 'emailSrvc', function ($scope, $compile, $sce, emailSrvc) {
    $scope.noCredentialsYet = true;
    $scope.userCredentials = null;
    $scope.editing = false;
    $scope.hideGoogleUri = false;
    $scope.hideRunscopeUri = false;
    $scope.googleUri = 'https://developers.google.com/oauthplayground';
    $scope.runscopeUri = 'https://www.runscope.com/oauth_tool/callback';
    $scope.playgroundExample = '';
    $scope.googleExampleLink = 'https://developers.google.com/oauthplayground/#step1&scopes=/authenticate&oauthEndpointSelect=Custom&oauthAuthEndpointValue=[BASE_URI_ENCODE]/oauth/authorize&oauthTokenEndpointValue=[BASE_URI_ENCODE]/oauth/token&oauthClientId=[CLIENT_ID]&oauthClientSecret=[CLIENT_SECRET]&accessTokenType=bearer';
    $scope.sampleAuthCurl = '';
    $scope.sampleAuthCurlTemplate = "curl -i -L -k -H 'Accept: application/json' --data 'client_id=[CLIENT_ID]&client_secret=[CLIENT_SECRET]&grant_type=authorization_code&redirect_uri=[REDIRECT_URI]&code=REPLACE WITH OAUTH CODE' [BASE_URI]/oauth/token";
    $scope.runscopeExample = '';
    $scope.runscopeExampleLink = 'https://www.runscope.com/oauth2_tool';
    $scope.authorizeUrlBase = getBaseUri() + '/oauth/authorize';
    $scope.authorizeURLTemplate = $scope.authorizeUrlBase + '?client_id=[CLIENT_ID]&response_type=code&scope=/authenticate&redirect_uri=[REDIRECT_URI]';
    $scope.tokenURL = orcidVar.pubBaseUri + '/oauth/token';
    $scope.authorizeURL = '';
    $scope.selectedRedirectUri = '';
    $scope.creating = false;
    $scope.emailSrvc = emailSrvc;
    $scope.nameToDisplay = '';
    $scope.descriptionToDisplay = '';
    $scope.verifyEmailSent=false;
    $scope.accepted=false;
    $scope.expanded = false;    
    
    $scope.verifyEmail = function() {
        var funct = function() {
            $scope.verifyEmailObject = emailSrvc.primaryEmail;
            emailSrvc.verifyEmail(emailSrvc.primaryEmail,function(data) {
                $scope.verifyEmailSent = true;    
                $scope.$apply();                    
           });            
       };
       if (emailSrvc.primaryEmail == null)
              emailSrvc.getEmails(funct);
       else
           funct();
    };

    $scope.closeModal = function() {
        $.colorbox.close();
    };

    $scope.acceptTerms = function() {
        $scope.mustAcceptTerms = false;
        $scope.accepted = false;
        $.colorbox({
            html : $compile($('#terms-and-conditions-modal').html())($scope),
                scrolling: true,
                onLoad: function() {
                $('#cboxClose').remove();
            },
            scrolling: true
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

    $scope.confirmDisableDeveloperTools = function() {
        $.colorbox({
            html : $compile($('#confirm-disable-developer-tools').html())($scope),
                onLoad: function() {
                $('#cboxClose').remove();
            }
        });
    };

    $scope.disableDeveloperTools = function() {
        $.ajax({
            url: getBaseUri()+'/developer-tools/disable-developer-tools.json',
            contentType: 'application/json;charset=UTF-8',
            type: 'POST',
            success: function(data){
                if(data == true){
                    window.location.href = getBaseUri()+'/account';
                };
            }
        }).fail(function(error) {
            // something bad is happening!
            console.log("Error enabling developer tools");
        });
    };

    $scope.getSSOCredentials = function() {
        $.ajax({
            url: getBaseUri()+'/developer-tools/get-sso-credentials.json',
            contentType: 'application/json;charset=UTF-8',
            type: 'GET',
            success: function(data){
                $scope.$apply(function(){
                    if(data != null && data.clientSecret != null) {
                        $scope.playgroundExample = '';
                        $scope.userCredentials = data;
                        $scope.hideGoogleUri = false;                        
                        $scope.selectedRedirectUri = $scope.userCredentials.redirectUris[0];
                        for(var i = 0; i < $scope.userCredentials.redirectUris.length; i++) {
                            if($scope.googleUri == $scope.userCredentials.redirectUris[i].value.value) {
                                $scope.hideGoogleUri = true;
                            }

                            if($scope.userCredentials.redirectUris[i].value.value < $scope.selectedRedirectUri.value.value) {
                                $scope.selectedRedirectUri = $scope.userCredentials.redirectUris[i];
                            }
                        }
                        $scope.updateSelectedRedirectUri();
                        $scope.setHtmlTrustedNameAndDescription();
                    } else {
                        $scope.createCredentialsLayout();
                        $scope.noCredentialsYet = true;
                    }
                });
            }
        }).fail(function(e) {
            // something bad is happening!
            console.log("Error obtaining SSO credentials");
            logAjaxError(e);
        });
    };

    // Get an empty modal to add
    $scope.createCredentialsLayout = function(){
        $.ajax({
            url: getBaseUri() + '/developer-tools/get-empty-sso-credential.json',
            dataType: 'json',
            success: function(data) {
                $scope.$apply(function(){
                    $scope.hideGoogleUri = false;
                    $scope.creating = true;
                    $scope.userCredentials = data;
                });
            }
        }).fail(function() {
            console.log("Error fetching client");
        });
    };

    $scope.addRedirectURI = function() {
        $scope.userCredentials.redirectUris.push({value: '',type: 'default'});
        $scope.hideGoogleUri = false;
        for(var i = 0; i < $scope.userCredentials.redirectUris.length; i++) {
            if($scope.googleUri == $scope.userCredentials.redirectUris[i].value.value) {
                $scope.hideGoogleUri = true;
            }
        }
    };

    $scope.submit = function() {
        $.ajax({
            url: getBaseUri()+'/developer-tools/generate-sso-credentials.json',
            contentType: 'application/json;charset=UTF-8',
            type: 'POST',
            dataType: 'json',
            data: angular.toJson($scope.userCredentials),
            success: function(data){
                $scope.$apply(function(){
                    $scope.playgroundExample = '';
                    $scope.userCredentials = data;
                    if(data.errors.length != 0){
                        //SHOW ERROR
                    } else {
                        $scope.hideGoogleUri = false;
                        $scope.selectedRedirectUri = $scope.userCredentials.redirectUris[0];
                        for(var i = 0; i < $scope.userCredentials.redirectUris.length; i++) {
                            if($scope.googleUri == $scope.userCredentials.redirectUris[i].value.value) {
                                $scope.hideGoogleUri = true;
                            }

                            if($scope.userCredentials.redirectUris[i].value.value < $scope.selectedRedirectUri.value.value) {
                                $scope.selectedRedirectUri = $scope.userCredentials.redirectUris[i];
                            }
                        }
                        $scope.updateSelectedRedirectUri();
                        $scope.setHtmlTrustedNameAndDescription();
                        $scope.creating = false;
                        $scope.noCredentialsYet = false;
                    }
                });
            }
        }).fail(function(error) {
            // something bad is happening!
            console.log("Error creating SSO credentials");
        });
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
        //Hide the testing tools if they are already added
        for(var i = 0; i < $scope.userCredentials.redirectUris.length; i++) {
            if($scope.googleUri == $scope.userCredentials.redirectUris[i].value.value) {
                $scope.hideGoogleUri=true;
            } else if($scope.runscopeUri == $scope.userCredentials.redirectUris[i].value.value) {
                $scope.hideRunscopeUri=true;
            }
        }
        $scope.editing = true;
        $('.developer-tools .slidebox').slideDown();
        $('.tab-container .collapsed').css('display', 'none');
        $('.tab-container .expanded').css('display', 'inline').parent().css('background','#EBEBEB');
    };

    $scope.showViewLayout = function() {
        //Reset the credentials
        $scope.getSSOCredentials();
        $scope.editing = false;
        $scope.creating = false;
        $('.edit-details .slidebox').slideDown();
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
                        //SHOW ERROR
                    } else {
                        $scope.editing = false;
                        $scope.hideGoogleUri = false;
                        $scope.selectedRedirectUri = $scope.userCredentials.redirectUris[0];
                        for(var i = 0; i < $scope.userCredentials.redirectUris.length; i++) {
                            if($scope.googleUri == $scope.userCredentials.redirectUris[i].value.value) {
                                $scope.hideGoogleUri = true;
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

    $scope.deleteRedirectUri = function(idx) {
        $scope.userCredentials.redirectUris.splice(idx, 1);
        $scope.hideGoogleUri = false;
        for(var i = 0; i < $scope.userCredentials.redirectUris.length; i++) {
            if($scope.googleUri == $scope.userCredentials.redirectUris[i].value.value) {
                $scope.hideGoogleUri = true;
            }
        }
    };

    $scope.addTestRedirectUri = function(type) {
        var rUri = $scope.runscopeUri;
        if(type == 'google'){
            rUri = $scope.googleUri;
        }

        $.ajax({
            url: getBaseUri() + '/developer-tools/get-empty-redirect-uri.json',
            dataType: 'json',
            success: function(data) {
                data.value.value=rUri;
                $scope.$apply(function(){
                    if($scope.userCredentials.redirectUris.length == 1 && $scope.userCredentials.redirectUris[0].value.value == null) {
                        $scope.userCredentials.redirectUris[0].value.value = rUri;
                    } else {
                        $scope.userCredentials.redirectUris.push(data);
                    }
                    if(type == 'google') {
                        $scope.hideGoogleUri = true;
                    }
                });
            }
        }).fail(function() {
            console.log("Error fetching empty redirect uri");
        });
    };

    $scope.updateSelectedRedirectUri = function() {
        var clientId = $scope.userCredentials.clientOrcid.value;
        var selectedRedirectUriValue = $scope.selectedRedirectUri.value.value;
        var selectedClientSecret = $scope.userCredentials.clientSecret.value;

        //Build the google playground url example
        $scope.playgroundExample = '';

        if($scope.googleUri == selectedRedirectUriValue) {
            var example = $scope.googleExampleLink;
            example = example.replace('[BASE_URI_ENCODE]', encodeURI(getBaseUri()));
            example = example.replace('[CLIENT_ID]', clientId);
            example = example.replace('[CLIENT_SECRET]', selectedClientSecret);
            $scope.playgroundExample = example;
        }

        var example = $scope.authorizeURLTemplate;
        example = example.replace('BASE_URI]', orcidVar.baseUri);
        example = example.replace('[CLIENT_ID]', clientId);
        example = example.replace('[REDIRECT_URI]', selectedRedirectUriValue);
        $scope.authorizeURL = example;

        // rebuild sampel Auhtroization Curl
        var sampeleCurl = $scope.sampleAuthCurlTemplate;
        $scope.sampleAuthCurl = sampeleCurl.replace('[CLIENT_ID]', clientId)
            .replace('[CLIENT_SECRET]', selectedClientSecret)
            .replace('[BASE_URI]', orcidVar.baseUri)
            .replace('[REDIRECT_URI]', selectedRedirectUriValue);
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

    $scope.resetClientSecret = function() {     
        $.ajax({
            url: getBaseUri() + '/developer-tools/reset-client-secret.json',
            type: 'POST',
            data: $scope.userCredentials.clientOrcid.value,
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

    $scope.closeModal = function(){
        $.colorbox.close();
    };

    //init
    $scope.getSSOCredentials();

    $scope.setHtmlTrustedNameAndDescription = function() {
        //Trust client name and description as html since it has been already filtered
        $scope.nameToDisplay = $sce.trustAsHtml($scope.userCredentials.clientName.value);
        $scope.descriptionToDisplay = $sce.trustAsHtml($scope.userCredentials.clientDescription.value);
    };
    
    $scope.inputTextAreaSelectAll = function($event){
        $event.target.select();
    }
    
    $scope.expand =  function(){
        $scope.expanded = true;
    }
    
    $scope.collapse = function(){
        $scope.expanded = false;
    }
    
    $scope.getClientUrl = function(userCredentials) {
        if(typeof userCredentials != undefined && userCredentials != null && userCredentials.clientWebsite != null && userCredentials.clientWebsite.value != null) {
            if(userCredentials.clientWebsite.value.lastIndexOf('http://') === -1 && userCredentials.clientWebsite.value.lastIndexOf('https://') === -1) {
                return '//' + userCredentials.clientWebsite.value;
            } else {
                return userCredentials.clientWebsite.value;
            }
        }
        return '';
    }
    
}]);