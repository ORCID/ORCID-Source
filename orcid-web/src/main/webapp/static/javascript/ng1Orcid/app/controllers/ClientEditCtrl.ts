//Migrated

declare var $: any;
declare var colorbox: any;
declare var getBaseUri: any;
declare var orcidVar: any;

import * as angular from 'angular';
import {NgModule} from '@angular/core';

// This is the Angular 1 part of the module
export const ClientEditCtrl = angular.module('orcidApp').controller(
    'ClientEditCtrl',
    [
        '$compile', 
        '$scope', 
        function (
            $compile,
            $scope
        ){
            $scope.authorizeUrlBase = getBaseUri() + '/oauth/authorize';
            $scope.authorizeURLTemplate = $scope.authorizeUrlBase + '?client_id=[CLIENT_ID]&response_type=code&redirect_uri=[REDIRECT_URI]&scope=[SCOPES]';
            $scope.availableRedirectScopes = [];
            $scope.clients = [];
            $scope.creating = false;
            $scope.editing = false;
            $scope.expanded = false;
            $scope.googleExampleLink = 'https://developers.google.com/oauthplayground/#step1&oauthEndpointSelect=Custom&oauthAuthEndpointValue=[BASE_URI_ENCODE]/oauth/authorize&oauthTokenEndpointValue=[BASE_URI_ENCODE]/oauth/token&oauthClientId=[CLIENT_ID]&oauthClientSecret=[CLIENT_SECRET]&accessTokenType=bearer&scope=[SCOPES]';
            $scope.googleUri = 'https://developers.google.com/oauthplayground';
            $scope.googleExampleLinkOpenID = 'https://developers.google.com/oauthplayground/#step1&scopes=openid&url=https%3A%2F%2F&content_type=application%2Fjson&http_method=GET&oauthEndpointSelect=Custom&oauthAuthEndpointValue=[BASE_URI_ENCODE]/oauth/authorize&includeCredentials=unchecked&accessTokenType=query&response_type=token&oauthClientId=[CLIENT_ID]';
            $scope.hideGoogleUri = true;
            $scope.hideSwaggerMemberUri = true;
            $scope.hideSwaggerUri = true;
            $scope.listing = true;
            $scope.newClient = null;
            $scope.playgroundExample = '';
            $scope.sampleAuthCurl = '';
            $scope.sampleAuthCurlTemplate = "curl -i -L -k -H 'Accept: application/json' --data 'client_id=[CLIENT_ID]&client_secret=[CLIENT_SECRET]&grant_type=authorization_code&redirect_uri=[REDIRECT_URI]&code=REPLACE WITH OAUTH CODE' [BASE_URI]/oauth/token";
            $scope.scopeSelectorOpen = false;
            $scope.selectedRedirectUri = "";
            $scope.selectedScope = "";
            $scope.selectedScopes = [];
            $scope.swaggerUri = orcidVar.pubBaseUri+ '/v2.0/';
            $scope.swaggerMemberUri = $scope.swaggerUri.replace("pub","api");
            $scope.tokenURL = getBaseUri() + '/oauth/token';
            $scope.viewing = false;
            $scope.selectedRedirectUriValue = null;
            
            $scope.sampleOpenId = '';
            $scope.sampleOpenIdTemplate = $scope.authorizeUrlBase + '?client_id=[CLIENT_ID]&response_type=token&scope=openid&redirect_uri=[REDIRECT_URI]';
           
            // Submits the new client request
            $scope.addClient = function(){
                // Check which redirect uris are empty strings and remove them from the
                // array
                for(var j = $scope.newClient.redirectUris.length - 1; j >= 0 ; j--){
                    if(!$scope.newClient.redirectUris[j].value){
                        $scope.newClient.redirectUris.splice(j, 1);
                    } else {
                        $scope.newClient.redirectUris[j].actType.value = JSON.stringify({"import-works-wizard" : ["Articles"]});
                        $scope.newClient.redirectUris[j].geoArea.value = JSON.stringify({"import-works-wizard" : ["Global"]});
                    }
                }

                // Submit the new client request
                $.ajax({
                    url: getBaseUri() + '/group/developer-tools/add-client.json',
                    type: 'POST',
                    data: angular.toJson($scope.newClient),
                    contentType: 'application/json;charset=UTF-8',
                    dataType: 'json',
                    success: function(data) {
                        if(data.errors != null && data.errors.length > 0){
                            $scope.newClient = data;
                            $scope.$apply();
                        } else {
                            // If everything worked fine, reload the list of clients
                            $scope.getClients();
                        }
                    }
                }).fail(function() {
                    console.log("Error creating client information.");
                });
            };
            
            $scope.addTestRedirectUri = function(type, edit) {
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
                
                if(edit == 'true') {
	        		if($scope.clientToEdit.redirectUris.length == 1 && $scope.clientToEdit.redirectUris[0].value.value == null) {
	                    $scope.clientToEdit.redirectUris[0].value.value = rUri;
	                } else {
	                 	$scope.addUriToExistingClientTable(rUri);   
	                }
                } else {
                	if($scope.newClient.redirectUris.length == 1 && $scope.newClient.redirectUris[0].value.value == null) {
	                    $scope.newClient.redirectUris[0].value.value = rUri;
	                } else {
	                 	$scope.addRedirectUriToNewClientTable(rUri);   
	                }
                }                    
            };

            // Add a new uri input field to a existing client
            $scope.addUriToExistingClientTable = function(defaultValue){
            	defaultValue = (typeof defaultValue != undefined && defaultValue != null) ? defaultValue : ''; 	
				$scope.clientToEdit.redirectUris.push({value: {value: defaultValue},type: {value: 'default'}, scopes: [], errors: [], actType: {value: ""}, geoArea: {value: ""}});
            };
            
            // Add a new uri input field to a new client
            $scope.addRedirectUriToNewClientTable = function(defaultValue){
            	defaultValue = (typeof defaultValue != undefined && defaultValue != null) ? defaultValue : '';				
                $scope.newClient.redirectUris.push({value: {value: defaultValue},type: {value: 'default'}, scopes: [], errors: [], actType: {value: ""}, geoArea: {value: ""}});
            };

            $scope.closeModal = function(){
                $.colorbox.close();
            };

            $scope.collapse = function(){
                $scope.expanded = false;
            };

            $scope.confirmResetClientSecret = function() {
                $scope.resetThisClient = $scope.clientToEdit;
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

            // Delete an uri input field
            $scope.deleteUriOnExistingClient = function(idx){
                $scope.clientToEdit.redirectUris.splice(idx, 1);
                $scope.hideGoogleUri = false;
                $scope.hideSwaggerUri = false;
                $scope.hideSwaggerMemberUri = false;
                if($scope.clientToEdit.redirectUris != null && $scope.clientToEdit.redirectUris.length > 0) {
                    for(var i = 0; i < $scope.clientToEdit.redirectUris.length; i++) {
                        if($scope.clientToEdit.redirectUris[i].value.value == $scope.googleUri) {
                            $scope.hideGoogleUri = true;
                        }else if ($scope.swaggerUri == $scope.clientToEdit.redirectUris[i].value.value){
                            $scope.hideSwaggerUri = true;
                        }else if ($scope.swaggerMemberUri == $scope.clientToEdit.redirectUris[i].value.value){
                            $scope.hideSwaggerMemberUri = true;
                        }
                    }
                }
            };

            // Delete an uri input field
            $scope.deleteUriOnNewClient = function(idx){
                $scope.newClient.redirectUris.splice(idx, 1);
                $scope.hideGoogleUri = false;
                $scope.hideSwaggerUri = false;
                $scope.hideSwaggerMemberUri = false;
                if($scope.newClient.redirectUris != null && $scope.newClient.redirectUris.length > 0) {
                    for(var i = 0; i < $scope.newClient.redirectUris.length; i++) {
                        if($scope.newClient.redirectUris[i].value.value == $scope.googleUri) {
                            $scope.hideGoogleUri = true;
                        }else if ($scope.swaggerUri == $scope.newClient.redirectUris[i].value.value){
                            $scope.hideSwaggerUri = true;
                        }else if ($scope.swaggerMemberUri == $scope.newClient.redirectUris[i].value.value){
                            $scope.hideSwaggerMemberUri = true;
                        }
                    }
                }
            };

            // Submits the updated client
            $scope.editClient = function() {
                // Check which redirect uris are empty strings and remove them from the
                // array
                for(var j = $scope.clientToEdit.redirectUris.length - 1; j >= 0 ; j--){
                    if(!$scope.clientToEdit.redirectUris[j].value){
                        $scope.clientToEdit.redirectUris.splice(j, 1);
                    } else if($scope.clientToEdit.redirectUris[j].actType.value == "") {
                        $scope.clientToEdit.redirectUris[j].actType.value = JSON.stringify({"import-works-wizard" : ["Articles"]});
                        $scope.clientToEdit.redirectUris[j].geoArea.value = JSON.stringify({"import-works-wizard" : ["Global"]});
                    }
                }
                // Submit the edited client
                $.ajax({
                    url: getBaseUri() + '/group/developer-tools/edit-client.json',
                    type: 'POST',
                    data: angular.toJson($scope.clientToEdit),
                    contentType: 'application/json;charset=UTF-8',
                    dataType: 'json',
                    success: function(data) {
                        if(data.errors != null && data.errors.length > 0){
                            $scope.clientToEdit = data;
                            $scope.$apply();
                        } else {
                            // If everything worked fine, reload the list of clients
                            $scope.getClients();
                        }
                    }
                }).fail(function() {
                    console.log("Error editing client information.");
                });
            };

            $scope.expand =  function(){
                $scope.expanded = true;
            };

            $scope.getAvailableRedirectScopes = function() {
                var toRemove = '/authenticate';
                var result = [];

                result = jQuery.grep($scope.availableRedirectScopes, function(value) {
                  return value != toRemove;
                });

                return result;
            };

            // Get the list of clients associated with this user
            $scope.getClients = function(){
                $.ajax({
                    url: getBaseUri() + '/group/developer-tools/get-clients.json',
                    dataType: 'json',
                    success: function(data) {
                        $scope.$apply(function(){
                            $scope.clients = data;
                            $scope.creating = false;
                            $scope.editing = false;
                            $scope.viewing = false;
                            $scope.listing = true;
                            $scope.hideGoogleUri = false;
                            $scope.hideSwaggerUri = false;
                            $scope.hideSwaggerMemberUri = false;

                        });
                    }
                }).fail(function() {
                    alert("Error fetching clients.");
                    console.log("Error fetching clients.");
                });
            };

            $scope.getClientUrl = function(client) {
                if(client != null) {
                    if(client.website != null){
                        if(client.website.value != null) {
                            if(client.website.value.lastIndexOf('http://') === -1 && client.website.value.lastIndexOf('https://') === -1) {
                                return '//' + client.website.value;
                            } else {
                                return client.website.value;
                            }
                        }
                    }
                }
                return '';
            };

            $scope.inputTextAreaSelectAll = function($event){
                $event.target.select();
            };

            // Checks if an item is selected
            $scope.isChecked = function (rUri) {
                var scope = this.scope;
                if (jQuery.inArray( scope, rUri.scopes ) != -1) {
                    return true;
                }
                return false;
            };

            // Checks if the scope checkbox should be disabled
            $scope.isDisabled = function (rUri) {
                if(rUri.type.value == 'grant-read-wizard') {
                    return true;
                }
                return false;
            };

            // Load the list of scopes for client redirect uris
            $scope.loadAvailableScopes = function(){
                $.ajax({
                    url: getBaseUri() + '/group/developer-tools/get-available-scopes.json',
                    type: 'GET',
                    contentType: 'application/json;charset=UTF-8',
                    dataType: 'json',
                    success: function(data) {
                        $scope.availableRedirectScopes = data;
                    }
                }).fail(function() {
                    console.log("Unable to fetch redirect uri scopes.");
                });
            };

            // Load the default scopes based n the redirect uri type selected
            $scope.loadDefaultScopes = function(rUri) {
                // Empty the scopes to update the default ones
                rUri.scopes = [];
                // Fill the scopes with the default scopes
                if(rUri.type.value == 'grant-read-wizard'){
                    rUri.scopes.push('/read-limited');
                } else if (rUri.type.value == 'import-works-wizard'){
                    rUri.scopes.push('/read-limited');
                    rUri.scopes.push('/activities/update');
                } else if (rUri.type.value == 'import-funding-wizard'){
                    rUri.scopes.push('/read-limited');
                    rUri.scopes.push('/activities/update');
                }
            };

            $scope.resetClientSecret = function() {
                $.ajax({
                    url: getBaseUri() + '/group/developer-tools/reset-client-secret.json',
                    type: 'POST',
                    data: $scope.resetThisClient.clientId.value,
                    contentType: 'application/json;charset=UTF-8',
                    dataType: 'text',
                    success: function(data) {
                        if(data) {
                            $scope.editing = false;
                            $scope.creating = false;
                            $scope.listing = true;
                            $scope.viewing = false;

                            $scope.closeModal();
                            $scope.getClients();
                        } else {
                            console.log('Unable to reset client secret');
                        }
                    }
                }).fail(function() {
                    console.log("Error resetting redirect uri");
                });
            };

            // Mark an item as selected
            $scope.setSelectedItem = function(rUri){
                var scope = this.scope;
                if (jQuery.inArray( scope, rUri.scopes ) == -1) {
                    rUri.scopes.push(scope);
                } else {
                    rUri.scopes = jQuery.grep(rUri.scopes, function(value) {
                        return value != scope;
                      });
                }
                return false;
            };

            // Get an empty modal to add
            $scope.showAddClient = function(){
                $.ajax({
                    url: getBaseUri() + '/group/developer-tools/client.json',
                    dataType: 'json',
                    success: function(data) {
                        $scope.$apply(function() {
                            $scope.newClient = data;
                            $scope.creating = true;
                            $scope.listing = false;
                            $scope.editing = false;
                            $scope.viewing = false;
                            $scope.hideGoogleUri = false;
                            $scope.hideSwaggerUri = false;
                            $scope.hideSwaggerMemberUri = false;
                        });
                    }
                }).fail(function() {
                    console.log("Error fetching client");
                });
            };

            // Display the modal to edit a client
            $scope.showEditClient = function(client) {
                // Copy the client to edit to a scope variable
                $scope.clientToEdit = client;
                $scope.editing = true;
                $scope.creating = false;
                $scope.listing = false;
                $scope.viewing = false;
                $scope.hideGoogleUri = false;
                $scope.hideSwaggerUri = false;
                $scope.hideSwaggerMemberUri = false;

                if($scope.clientToEdit.redirectUris != null && $scope.clientToEdit.redirectUris.length > 0) {
                    for(var i = 0; i < $scope.clientToEdit.redirectUris.length; i++) {
                        if($scope.clientToEdit.redirectUris[i].value.value == $scope.googleUri) {
                            $scope.hideGoogleUri = true;
                        }else if ($scope.swaggerUri == $scope.clientToEdit.redirectUris[i].value.value){
                            $scope.hideSwaggerUri = true;
                        }else if ($scope.swaggerMemberUri == $scope.clientToEdit.redirectUris[i].value.value){
                            $scope.hideSwaggerMemberUri = true;
                        }
                    }
                }
            };

            $scope.showViewLayout = function() {
                $scope.editing = false;
                $scope.creating = false;
                $scope.listing = true;
                $scope.viewing = false;
            };

            // Submits the client update request
            $scope.submitEditClient = function(){
                // Check which redirect uris are empty strings and remove them from the
                // array
                for(var j = $scope.clientToEdit.length - 1; j >= 0 ; j--)    {
                    if(!$scope.clientToEdit.redirectUris[j].value){
                        $scope.clientToEdit.redirectUris.splice(j, 1);
                    }
                }
                // Submit the update request
                $.ajax({
                    url: getBaseUri() + '/group/developer-tools/edit-client.json',
                    type: 'POST',
                    data: angular.toJson($scope.clientToEdit),
                    contentType: 'application/json;charset=UTF-8',
                    dataType: 'json',
                    success: function(data) {
                        if(data.errors != null && data.errors.length > 0){
                            $scope.clientToEdit = data;
                            $scope.$apply();
                        } else {
                            // If everything worked fine, reload the list of clients
                            $scope.getClients();
                            $.colorbox.close();
                        }
                    }
                }).fail(function() {
                    alert("An error occured updating the client");
                    console.log("Error updating client information.");
                });
            };

            $scope.updateSelectedRedirectUri = function() {
                var clientId = '';
                var example = null;
                var exampleOIDC = null;
                var sampleCurl = null;
                var sampleOIDC = null;
                var scope = $scope.selectedScope;
                var selectedClientSecret = '';
                $scope.playgroundExample = '';

                if ($scope.clientDetails != null){
                    clientId = $scope.clientDetails.clientId.value;
                    selectedClientSecret = $scope.clientDetails.clientSecret.value;
                }

                if($scope.selectedRedirectUri.length != 0) {
                    $scope.selectedRedirectUriValue = $scope.selectedRedirectUri.value.value;

                    if($scope.googleUri == $scope.selectedRedirectUriValue) {
                        example = $scope.googleExampleLink;
                        example = example.replace('[BASE_URI_ENCODE]', encodeURI(getBaseUri()));
                        example = example.replace('[CLIENT_ID]', clientId);
                        example = example.replace('[CLIENT_SECRET]', selectedClientSecret);
                        if(scope != '')
                            example = example.replace('[SCOPES]', scope);
                        $scope.playgroundExample = example.replace(/,/g,'%20');
                        
                        exampleOIDC = $scope.googleExampleLinkOpenID;
                        exampleOIDC = exampleOIDC.replace('[BASE_URI_ENCODE]', encodeURI(getBaseUri()));
                        exampleOIDC = exampleOIDC.replace('[CLIENT_ID]', clientId);
                        $scope.googleExampleLinkOpenID = exampleOIDC;        
                        
                    }else if($scope.swaggerUri == $scope.selectedRedirectUriValue) {
                        $scope.playgroundExample = $scope.swaggerUri;
                    }else if($scope.swaggerMemberUri == $scope.selectedRedirectUriValue) {
                        $scope.playgroundExample = $scope.swaggerMemberUri;
                    }

                    example = $scope.authorizeURLTemplate;
                    example = example.replace('[BASE_URI]', orcidVar.baseUri);
                    example = example.replace('[CLIENT_ID]', clientId);
                    example = example.replace('[REDIRECT_URI]', $scope.selectedRedirectUriValue);
                    if(scope != ''){
                        example = example.replace('[SCOPES]', scope);
                    }

                    $scope.authorizeURL = example.replace(/,/g,'%20');    // replacing
                                                                            // ,

                    // rebuild sample Auhtroization Curl
                    sampleCurl = $scope.sampleAuthCurlTemplate;
                    $scope.sampleAuthCurl = sampleCurl.replace('[CLIENT_ID]', clientId)
                        .replace('[CLIENT_SECRET]', selectedClientSecret)
                        .replace('[BASE_URI]', orcidVar.baseUri)
                        .replace('[REDIRECT_URI]', $scope.selectedRedirectUriValue);
                    
                    sampleOIDC = $scope.sampleOpenIdTemplate;
                    $scope.sampleOpenId = sampleOIDC
                      .replace('[CLIENT_ID]', clientId)
                      .replace('[REDIRECT_URI]', $scope.selectedRedirectUriValue);
                    console.log($scope.sampleOpenIdTemplate);
                    console.log($scope.sampleOpenId);
                }
            };

            // Display client details: Client ID and Client secret
            $scope.viewDetails = function(client) {
                // Set the client details
                $scope.clientDetails = client;
                // Set the first redirect uri selected
                if(client.redirectUris != null && client.redirectUris.length > 0) {
                    $scope.selectedRedirectUri = client.redirectUris[0];
                } else {
                    $scope.selectedRedirectUri = null;
                }

                $scope.editing = false;
                $scope.creating = false;
                $scope.listing = false;
                $scope.viewing = true;

                // Update the selected redirect uri
                if($scope.clientDetails != null){
                    $scope.updateSelectedRedirectUri();
                }
            };

            // init
            $scope.getClients();
            $scope.loadAvailableScopes();
        }
    ]
);

// This is the Angular 2 part of the module
@NgModule({})
export class ClientEditCtrlNg2Module {}