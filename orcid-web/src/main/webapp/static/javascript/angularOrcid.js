/*
 * =============================================================================
 *
 * ORCID (R) Open Source
 * http://orcid.org
 *
 * Copyright (c) 2012-2013 ORCID, Inc.
 * Licensed under an MIT-Style License (MIT)
 * http://orcid.org/open-source-license
 *
 * This copyright and license information (including a link to the full license)
 * shall be included in its entirety in all copies or substantial portion of
 * the software.
 *
 * =============================================================================
 */

var orcidNgModule = angular.module('orcidApp', []).directive('ngModelOnblur', function() {
    return {
        restrict: 'A',
        require: 'ngModel',
        link: function(scope, elm, attr, ngModelCtrl) {
            if (attr.type === 'radio' || attr.type === 'checkbox') return;

            elm.unbind('input').unbind('keydown').unbind('change');
            
            elm.bind("keydown keypress", function(event) {
                if (event.which === 13) {
                    scope.$apply(function() {
                        ngModelCtrl.$setViewValue(elm.val());
                    });
                }
            });

            elm.bind('blur', function() {
                scope.$apply(function() {
                    ngModelCtrl.$setViewValue(elm.val());
                });
            });
        }
    };
});


function EditTableCtrl($scope) {
	
	// email edit row
	$scope.emailUpdateToggleText = function () {
		if ($scope.showEditEmail) $scope.emailToggleText = OM.getInstance().get("manage.editTable.hide");
		else $scope.emailToggleText = OM.getInstance().get("manage.editTable.edit");		
	};
	
	$scope.toggleEmailEdit = function() {
		$scope.showEditEmail = !$scope.showEditEmail;
		$scope.emailUpdateToggleText();
	};
	
	// init email edit row
	$scope.showEditEmail = (window.location.hash === "#editEmail");
	$scope.emailUpdateToggleText();
	

	// password edit row
	$scope.passwordUpdateToggleText = function () {
		if ($scope.showEditPassword) $scope.passwordToggleText = OM.getInstance().get("manage.editTable.hide");
		else $scope.passwordToggleText = OM.getInstance().get("manage.editTable.edit");		
	};
	
	$scope.togglePasswordEdit = function() {
		$scope.showEditPassword = !$scope.showEditPassword;
		$scope.passwordUpdateToggleText();
	};

	// init password row
	$scope.showEditPassword = (window.location.hash === "#editPassword");
	$scope.passwordUpdateToggleText();
	
	// deactivate edit row
	$scope.deactivateUpdateToggleText = function () {
		if ($scope.showEditDeactivate) $scope.deactivateToggleText = OM.getInstance().get("manage.editTable.hide");
		else $scope.deactivateToggleText = OM.getInstance().get("manage.editTable.deactivateRecord");		
	};

	$scope.toggleDeactivateEdit = function() {
		$scope.showEditDeactivate = !$scope.showEditDeactivate;
		$scope.deactivateUpdateToggleText();
	};
	
	// init deactivate
	$scope.showEditDeactivate = (window.location.hash === "#editDeactivate");
	$scope.deactivateUpdateToggleText();
	
	// privacy preferences edit row
	$scope.privacyPreferencesUpdateToggleText = function () {
		if ($scope.showEditPrivacyPreferences) $scope.privacyPreferencesToggleText = OM.getInstance().get("manage.editTable.hide");
		else $scope.privacyPreferencesToggleText = OM.getInstance().get("manage.editTable.edit");		
	};

	$scope.togglePrivacyPreferencesEdit = function() {
		$scope.showEditPrivacyPreferences = !$scope.showEditPrivacyPreferences;
		$scope.privacyPreferencesUpdateToggleText();
	};
	
	// init privacy preferences
	$scope.showEditPrivacyPreferences = (window.location.hash === "#editPrivacyPreferences");
	$scope.privacyPreferencesUpdateToggleText();

	// email preferences edit row
	$scope.emailPreferencesUpdateToggleText = function () {
		if ($scope.showEditEmailPreferences) $scope.emailPreferencesToggleText = OM.getInstance().get("manage.editTable.hide");
		else $scope.emailPreferencesToggleText = OM.getInstance().get("manage.editTable.edit");		
	};

	$scope.toggleEmailPreferencesEdit = function() {
		$scope.showEditEmailPreferences = !$scope.showEditEmailPreferences;
		$scope.emailPreferencesUpdateToggleText();
	};
	
	// init privacy preferences
	$scope.showEditEmailPreferences = (window.location.hash === "#editEmailPreferences");
	$scope.emailPreferencesUpdateToggleText();	

	// security question edit row
	$scope.securityQuestionUpdateToggleText = function () {
		if ($scope.showEditSecurityQuestion) $scope.securityQuestionToggleText = OM.getInstance().get("manage.editTable.hide");
		else $scope.securityQuestionToggleText = OM.getInstance().get("manage.editTable.edit");		
	};

	$scope.toggleSecurityQuestionEdit = function() {
		$scope.showEditSecurityQuestion = !$scope.showEditSecurityQuestion;
		$scope.securityQuestionUpdateToggleText();
	};
	
	// init security question
	$scope.showEditSecurityQuestion = (window.location.hash === "#editSecurityQuestion");
	$scope.securityQuestionUpdateToggleText();	
	
};

function PrivacyPreferencesCtrl($scope, $http) {
	$scope.getPrivacyPreferences = function() {	
		$.ajax({
	        url: $('body').data('baseurl') + 'account/default-privacy-preferences.json',
	        dataType: 'json',
	        success: function(data) {
	        	$scope.privacyPreferences = data;
	        	$scope.$apply();
	        	//alert($scope.privacyPreferences.workVisibilityDefault.value);
	        }
	    }).fail(function() { 
	    	// something bad is happening!
	    	console.log("error with multi email");
	    });
	};
	
	$scope.savePrivacyPreferences = function() {
		$.ajax({
	        url: $('body').data('baseurl') + 'account/default-privacy-preferences.json',
	        type: 'POST',
	        data: angular.toJson($scope.privacyPreferences),
	        contentType: 'application/json;charset=UTF-8',
	        dataType: 'json',
	        success: function(data) {
	        	$scope.privacyPreferences = data;
	        	$scope.$apply();
	         	//alert($scope.privacyPreferences.workVisibilityDefault.value);
	 	       
	        }
	    }).fail(function() { 
	    	// something bad is happening!
	    	console.log("error with multi email");
	    });
	};

	$scope.updateWorkVisibilityDefault = function(priv, $event) {
		$scope.privacyPreferences.workVisibilityDefault.value = priv;
		$scope.savePrivacyPreferences();
	};
	
	//init
	$scope.privacyPreferences = $scope.getPrivacyPreferences();
	
};


function DeactivateAccountCtrl($scope, $compile) {
	$scope.sendDeactivateEmail = function() {
		orcidGA.gaPush(['_trackEvent', 'Disengagement', 'Deactivate_Initiate', 'Website']);
		$.ajax({
	        url: $('body').data('baseurl') + 'account/send-deactivate-account.json',
	        dataType: 'json',
	        success: function(data) {
	        	$scope.primaryEmail = data.value;
	    	    $.colorbox({
	    	        html : $compile($('#deactivate-account-modal').html())($scope)	            	
	    	    });
	    	    $scope.$apply();
	    	    $.colorbox.resize();
	        }
	    }).fail(function() { 
	    	// something bad is happening!
	    	console.log("error with change DeactivateAccount");
	    });
	};
	
	$scope.closeModal = function() {
		$.colorbox.close();
	};
};


function SecurityQuestionEditCtrl($scope, $http) {
	$scope.getSecurityQuestion = function() {
		$.ajax({
	        url: $('body').data('baseurl') + 'account/security-question.json',
	        dataType: 'json',
	        success: function(data) {
	        	$scope.securityQuestionPojo = data;
	        	$scope.$apply();
	        }
	    }).fail(function() { 
	    	// something bad is happening!
	    	console.log("error with security question.json");
	    });
	};
	
	$scope.getSecurityQuestion();
	
	$scope.saveSecurityQuestion = function() {
		$.ajax({
	        url: $('body').data('baseurl') + 'account/security-question.json',
	        type: 'POST',
	        data: angular.toJson($scope.securityQuestionPojo),
	        contentType: 'application/json;charset=UTF-8',
	        dataType: 'json',
	        success: function(data) {
	        	//alert(angular.toJson($scope.securityQuestionPojo));
	        	$scope.securityQuestionPojo = data;
	        	$scope.$apply();
	        }
	    }).fail(function() { 
	    	// something bad is happening!
	    	console.log("error with security question");
	    });
	};
};


function PasswordEditCtrl($scope, $http) {
	$scope.getChangePassword = function() {
		$.ajax({
	        url: $('body').data('baseurl') + 'account/change-password.json',
	        dataType: 'json',
	        success: function(data) {
	        	$scope.changePasswordPojo = data;
	        	$scope.$apply();
	        }
	    }).fail(function() { 
	    	// something bad is happening!
	    	console.log("error with change password");
	    });
	};
	
	$scope.getChangePassword();
	
	$scope.saveChangePassword = function() {
		$.ajax({
	        url: $('body').data('baseurl') + 'account/change-password.json',
	        type: 'POST',
	        data: angular.toJson($scope.changePasswordPojo),
	        contentType: 'application/json;charset=UTF-8',
	        dataType: 'json',
	        success: function(data) {
	        	$scope.changePasswordPojo = data;
	        	$scope.$apply();
	        }
	    }).fail(function() { 
	    	// something bad is happening!
	    	console.log("error with edit password");
	    });
	};
};

function EmailEditCtrl($scope, $compile) {
	$scope.getEmails = function() {
		$.ajax({
	        url: $('body').data('baseurl') + 'account/emails.json',
	        //type: 'POST',
	        //data: $scope.emailsPojo, 
	        dataType: 'json',
	        success: function(data) {
	        	$scope.emailsPojo = data;
	        	$scope.$apply();
	        }
	    }).fail(function() { 
	    	// something bad is happening!
	    	console.log("error with multi email");
	    });
	};
	
	$scope.initInputEmail = function () {
		$scope.inputEmail = {"value":"","primary":false,"current":true,"verified":false,"visibility":"PRIVATE","errors":[]};		
	};
	
	//init
	$scope.curPrivToggle = null;
	$scope.getEmails();
	$scope.initInputEmail();

	$scope.setPrimary = function(idx) {
		for (i in $scope.emailsPojo.emails) {
			console.log($scope.emailsPojo.emails[idx]);
			if (i == idx) {
				$scope.emailsPojo.emails[i].primary = true;
			} else {
				$scope.emailsPojo.emails[i].primary = false;
			}
		}
		$scope.saveEmail();
	};
	
	$scope.toggleVisibility = function(idx) {
		if ($scope.emailsPojo.emails[idx].visibility ==  "PRIVATE") {
			$scope.emailsPojo.emails[idx].visibility = "LIMITED";
		} else if ($scope.emailsPojo.emails[idx].visibility ==  "LIMITED") {
			$scope.emailsPojo.emails[idx].visibility = "PUBLIC";
		} else {
			$scope.emailsPojo.emails[idx].visibility = "PRIVATE";
		}
		$scope.saveEmail();
	};
	
	$scope.togglePrivacySelect = function(idx) {
		var curEmail = $scope.emailsPojo.emails[idx].value;
		if ($scope.curPrivToggle == null) $scope.curPrivToggle = curEmail;
		else $scope.curPrivToggle = null;
	};
	
	$scope.setPrivacy = function(idx, priv, $event) {
		$event.preventDefault();
		$scope.emailsPojo.emails[idx].visibility = priv;
		$scope.curPrivToggle = null;
		$scope.saveEmail();
	};
	
	$scope.verifyEmail = function(idx) {
		$scope.verifyEmailIdx = idx;
		$.ajax({
	        url: $('body').data('baseurl') + 'account/verifyEmail.json',
	        type: 'get',
	        data:  { "email": $scope.emailsPojo.emails[idx].value },
	        contentType: 'application/json;charset=UTF-8',
	        dataType: 'json',
	        success: function(data) {
	    	    $.colorbox({
	    	        html : $compile($('#verify-email-modal').html())($scope)
	    	    });
	    	    $scope.$apply();
	    	    $.colorbox.resize();
	        }
	    }).fail(function() { 
	    	// something bad is happening!
	    	console.log("error with multi email");
	    });  
	};
	
	$scope.closeModal = function() {
		$.colorbox.close();
	};


	$scope.saveEmail = function() {
		$.ajax({
	        url: $('body').data('baseurl') + 'account/emails.json',
	        type: 'POST',
	        data: angular.toJson($scope.emailsPojo),
	        contentType: 'application/json;charset=UTF-8',
	        dataType: 'json',
	        success: function(data) {
	        	$scope.emailsPojo = data;
	        	$scope.$apply();
	        }
	    }).fail(function() { 
	    	// something bad is happening!
	    	console.log("error with multi email");
	    });
	};

	$scope.addEmail = function (obj, $event) {
		$.ajax({
	        url: $('body').data('baseurl') + 'account/addEmail.json',
	        type: 'POST',
	        data:  angular.toJson($scope.inputEmail),
	        contentType: 'application/json;charset=UTF-8',
	        dataType: 'json',
	        success: function(data) {
	        	$scope.inputEmail = data;
	        	//alert($scope.inputEmail.errors.length);
	        	if ($scope.inputEmail.errors.length == 0) {
					$scope.initInputEmail();
					$scope.getEmails();
				}
	        	$scope.$apply();
	        }
	    }).fail(function() { 
	    	// something bad is happening!
	    	console.log("$EmailEditCtrl.addEmail() error");
	    });
	};
	
	$scope.confirmDeleteEmail = function(idx) {
		    $scope.deleteEmailIdx = idx;
            $.colorbox({
                html : $compile($('#delete-email-modal').html())($scope)
                	
            });
            $.colorbox.resize();
	};
	
	$scope.deleteEmail = function () {
		var email = $scope.emailsPojo.emails[$scope.deleteEmailIdx];
		$scope.deleteEmailIdx = null;
		$.ajax({
	        url: $('body').data('baseurl') + 'account/deleteEmail.json',
	        type: 'DELETE',
	        data:  angular.toJson(email),
	        contentType: 'application/json;charset=UTF-8',
	        dataType: 'json',
	        success: function(data) {
	        	$scope.inputEmail = data;
	        	//alert($scope.inputEmail.errors.length);
	        	if ($scope.inputEmail.errors.length == 0) {
					$scope.initInputEmail();
					$scope.getEmails();
				}
	        	$scope.$apply();
	        	$scope.closeModal();
	        }
	    }).fail(function() { 
	    	// something bad is happening!
	    	console.log("$EmailEditCtrl.deleteEmail() error");
	    });
	};
	
};


function ExternalIdentifierCtrl($scope, $compile){		
	$scope.getExternalIdentifiers = function(){
		$.ajax({
			url: $('body').data('baseurl') + 'my-orcid/externalIdentifiers.json',	        
	        dataType: 'json',
	        success: function(data) {
	        	$scope.externalIdentifiersPojo = data;
	        	$scope.$apply();
	        }
		}).fail(function(){
			// something bad is happening!
	    	console.log("error fetching external identifiers");
		});
	};
	
	//init
	$scope.getExternalIdentifiers();
	
	$scope.deleteExternalIdentifier = function(idx) {
		$scope.removeExternalIdentifierIndex = idx;
		$scope.removeExternalModalText = $scope.externalIdentifiersPojo.externalIdentifiers[idx].externalIdReference.content;
		if ($scope.externalIdentifiersPojo.externalIdentifiers[idx].externalIdCommonName != null)
			$scope.removeExternalModalText = $scope.externalIdentifiersPojo.externalIdentifiers[idx].externalIdCommonName.content + ' ' + $scope.removeExternalModalText;
        $.colorbox({        	
            html: $compile($('#delete-external-id-modal').html())($scope)
            	
        });
        $.colorbox.resize();
	};
	
	$scope.removeExternalIdentifier = function() {
		var externalIdentifier = $scope.externalIdentifiersPojo.externalIdentifiers[$scope.removeExternalIdentifierIndex];
		$scope.externalIdentifiersPojo.externalIdentifiers.splice($scope.removeExternalIdentifierIndex, 1);
		$scope.removeExternalIdentifierIndex = null;
		$.ajax({
	        url: $('body').data('baseurl') + 'my-orcid/externalIdentifiers.json',
	        type: 'DELETE',
	        data: angular.toJson(externalIdentifier),
	        contentType: 'application/json;charset=UTF-8',
	        dataType: 'json',
	        success: function(data) {	        	
	        	if(data.errors.length != 0){
	        		console.log("Unable to delete external identifier.");
	        	} 
	        }
	    }).fail(function() { 
	    	console.log("Error deleting external identifier.");
	    });
		$scope.closeModal();
	};
	
	$scope.closeModal = function() {
		$.colorbox.close();
	};
	
};	


function RegistrationCtrl($scope, $compile) {
	$scope.getRegister = function(){
		$.ajax({
			url: $('body').data('baseurl') + 'register.json',	        
	        dataType: 'json',
	        success: function(data) {
	       	$scope.register = data;
	        $scope.$apply();
	        }
		}).fail(function(){
		// something bad is happening!
			console.log("error fetching register.json");
		});
	};
	
	$scope.getDuplicates = function(){
		$.ajax({
			//url: $('body').data('baseurl') + 'dupicateResearcher.json?familyNames=test&givenNames=test',	        
			url: $('body').data('baseurl') + 'dupicateResearcher.json?familyNames=' + $scope.register.familyNames.value + '&givenNames=' + $scope.register.givenNames.value,	        
	        dataType: 'json',
	        success: function(data) {
		       	$scope.duplicates = data;
		        $scope.$apply();
		        if ($scope.duplicates.length > 0 ) {
		        	$scope.showDuplicatesColorBox();
		        } else {
		        	$scope.postRegisterConfirm();
		        }
	        }
		}).fail(function(){
		// something bad is happening!
			console.log("error fetching register.json");
		});
	};

	
	$scope.updateWorkVisibilityDefault = function(priv, $event) {
		$scope.register.workVisibilityDefault.visibility = priv;
	};
	
	$scope.postRegister = function () {
		if (basePath.startsWith(baseUrl + 'oauth')) 
		    orcidGA.gaPush(['_trackEvent', 'RegGrowth', 'New-Registration-Submit', 'OAuth']);
	    else
	    	orcidGA.gaPush(['_trackEvent', 'RegGrowth', 'New-Registration-Submit', 'Website']);	
		$.ajax({
	        url: $('body').data('baseurl') + 'register.json',
	        type: 'POST',
	        data:  angular.toJson($scope.register),
	        contentType: 'application/json;charset=UTF-8',
	        dataType: 'json',
	        success: function(data) {
	        	$scope.register = data;
	        	$scope.$apply();
	        	if ($scope.register.errors.length == 0) {
	        		$scope.showProcessingColorBox();
	        		$scope.getDuplicates();
	        	}
	        }
	    }).fail(function() { 
	    	// something bad is happening!
	    	console.log("RegistrationCtrl.postRegister() error");
	    });
	};
	
	$scope.postRegisterConfirm = function () {
		$scope.showProcessingColorBox();
		$.ajax({
	        url: $('body').data('baseurl') + 'registerConfirm.json',
	        type: 'POST',
	        data:  angular.toJson($scope.register),
	        contentType: 'application/json;charset=UTF-8',
	        dataType: 'json',
	        success: function(data) {
	    		if (basePath.startsWith(baseUrl + 'oauth')) 
	    		    orcidGA.gaPush(['_trackEvent', 'RegGrowth', 'New-Registration', 'OAuth']);
	    	    else
	    	    	orcidGA.gaPush(['_trackEvent', 'RegGrowth', 'New-Registration', 'Website']);
	    		orcidGA.windowLocationHrefDelay(data.url);
	        }
	    }).fail(function() { 
	    	// something bad is happening!
	    	console.log("RegistrationCtrl.postRegister() error");
	    });
	};

	

	$scope.serverValidate = function (field) {
		if (field === undefined) field = '';
		$.ajax({
	        url: $('body').data('baseurl') + 'register' + field + 'Validate.json',
	        type: 'POST',
	        data:  angular.toJson($scope.register),
	        contentType: 'application/json;charset=UTF-8',
	        dataType: 'json',
	        success: function(data) {
	        	$scope.copyErrorsLeft($scope.register, data);
	        	$scope.$apply();
	        }
	    }).fail(function() { 
	    	// something bad is happening!
	    	console.log("RegistrationCtrl.serverValidate() error");
	    });
	};

	// in the case of slow network connection
	// we don't want to overwrite  values while
	// user is typing
	$scope.copyErrorsLeft = function (data1, data2) {
		for (var key in data1) {
			if (key == 'errors') {
				data1.errors = data2.errors;
			} else {
				if (data1[key].errors !== undefined)
				data1[key].errors = data2[key].errors;
			};
		};
	};
	
	$scope.showProcessingColorBox = function () {
	    $.colorbox({
	        html : $('<div style="font-size: 50px; line-height: 60px; padding: 20px; text-align:center">Processing <i id="ajax-loader" class="icon-spinner icon-spin green"></i></div>'),
	        width: '400px', 
	        close: '',
	        escKey:false, 
	        overlayClose:false
	    });
	    $.colorbox.resize({width:"400px" , height:"100px"});
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
	
	//init
	$scope.getRegister();	
	//$scope.getDuplicates();
		 
};
		

function ClaimCtrl($scope, $compile) {
	$scope.getClaim = function(){
		$.ajax({
			url: window.location + '.json',	        
	        dataType: 'json',
	        success: function(data) {
	       	$scope.register = data;
	        $scope.$apply();
	        }
		}).fail(function(){
		// something bad is happening!
			console.log("error fetching register.json");
		});
	};
		
	$scope.postClaim = function () {
		$.ajax({
	        url: window.location + '.json',
	        type: 'POST',
	        data:  angular.toJson($scope.register),
	        contentType: 'application/json;charset=UTF-8',
	        dataType: 'json',
	        success: function(data) {
	        	$scope.register = data;
	        	$scope.$apply();
	        	if ($scope.register.errors.length == 0) {
	        		if ($scope.register.url != null) {
		    	    	orcidGA.gaPush(['_trackEvent', 'RegGrowth', 'New-Registration', 'Website']);
			    		orcidGA.windowLocationHrefDelay($scope.register.url);
	        		}
	        	}
	        }
	    }).fail(function() { 
	    	// something bad is happening!
	    	console.log("RegistrationCtrl.postRegister() error");
	    });
	};
	
	
	$scope.updateWorkVisibilityDefault = function(priv, $event) {
		$scope.register.workVisibilityDefault.visibility = priv;
	};

	$scope.serverValidate = function (field) {
		if (field === undefined) field = '';
		$.ajax({
	        url: $('body').data('baseurl') + 'claim' + field + 'Validate.json',
	        type: 'POST',
	        data:  angular.toJson($scope.register),
	        contentType: 'application/json;charset=UTF-8',
	        dataType: 'json',
	        success: function(data) {
	        	//alert(angular.toJson(data));
	        	$scope.copyErrorsLeft($scope.register, data);
	        	$scope.$apply();
	        }
	    }).fail(function() { 
	    	// something bad is happening!
	    	console.log("RegistrationCtrl.postRegisterValidate() error");
	    });
	};

	// in the case of slow network connection
	// we don't want to overwrite  values while
	// user is typing
	$scope.copyErrorsLeft = function (data1, data2) {
		for (var key in data1) {
			if (key == 'errors') {
				data1.errors = data2.errors;
			} else {
				if (data1[key] != null && data1[key].errors !== undefined)
				data1[key].errors = data2[key].errors;
			};
		};
	};
	
	$scope.isValidClass = function (cur) {
		if (cur === undefined) return '';
		var valid = true;
		if (cur.required && (cur.value == null || cur.value.trim() == '')) valid = false;
		if (cur.errors !== undefined && cur.errors.length > 0) valid = false;
		return valid ? '' : 'text-error';
	};
	
	//init
	$scope.getClaim();	
	//$scope.getDuplicates();
		 
};


function VerifyEmailCtrl($scope, $compile) {
	$scope.getEmails = function() {
		$.ajax({
	        url: $('body').data('baseurl') + 'account/emails.json',
	        //type: 'POST',
	        //data: $scope.emailsPojo, 
	        dataType: 'json',
	        success: function(data) {
	        	$scope.emailsPojo = data;
	        	$scope.$apply();
	        	var primeVerified = false;
	        	for (i in $scope.emailsPojo.emails) {
	        		if ($scope.emailsPojo.emails[i].primary) {
	        			$scope.primaryEmail = $scope.emailsPojo.emails[i].value;
	        			if ($scope.emailsPojo.emails[i].verified) primeVerified = true;
	        		};
	        	};
	        	if (!primeVerified) {
	        		var colorboxHtml = $compile($('#verify-email-modal').html())($scope);
	        		$scope.$apply();
	        	    
	        		$.colorbox({
	        	        html : colorboxHtml,
	        	        escKey:false, 
	        	        overlayClose:false,
	        	        transition: 'fade',
	        	        close: '',
	        	        //height: '200px',
	        	        //width: '500px',
	        	        scrolling: false
	        	        	    });
	        	        $.colorbox.resize();	        		
	        	};
	        }
	    }).fail(function() { 
	    	// something bad is happening!
	    	console.log("error with multi email");
	    });
	};
	
	$scope.verifyEmail = function() {
		$.ajax({
	        url: $('body').data('baseurl') + 'account/verifyEmail.json',
	        type: 'get',
	        data:  { "email": $scope.primaryEmail },
	        contentType: 'application/json;charset=UTF-8',
	        dataType: 'json',
	        success: function(data) {
	        	//alert( "Verification Email Send To: " + $scope.emailsPojo.emails[idx].value); 	
	        }
	    }).fail(function() { 
	    	// something bad is happening!
	    	console.log("error with multi email");
	    });  		
		var colorboxHtml = $compile($('#verify-email-modal-sent').html())($scope);
		
		$scope.emailSent = true;
		$.colorbox({
	        html : colorboxHtml,
	        escKey: true, 
	        overlayClose: true,
	        transition: 'fade',
	        close: '',
	        scrolling: false
	        	    });
	    $.colorbox.resize();
		
	};
	
	
	$scope.closeColorBox = function() {		
		$.ajax({
	        url: $('body').data('baseurl') + 'account/delayVerifyEmail.json',
	        type: 'get',
	        contentType: 'application/json;charset=UTF-8',
	        success: function(data) {
	        	//alert( "Verification Email Send To: " + $scope.emailsPojo.emails[idx].value); 	
	        }
	    }).fail(function() { 
	    	// something bad is happening!
	    	console.log("error with multi email");
	    });
		$.colorbox.close();
	};
	
	$scope.emailSent = false;
	$scope.getEmails();
};


function ClaimThanks($scope, $compile) {
	$scope.showThanks = function () {
		var colorboxHtml; 
			if ($scope.sourceGrantReadWizard.url == null)  
				colorboxHtml = $compile($('#claimed-record-thanks').html())($scope);
			else 
				colorboxHtml = $compile($('#claimed-record-thanks-source-grand-read').html())($scope);
		$.colorbox({
	        html : colorboxHtml,
	        escKey: true, 
	        overlayClose: true,
	        transition: 'fade',
	        close: '',
	        scrolling: false
	        	    });
		$scope.$apply(); // this seems to make sure angular renders in the colorbox
	    $.colorbox.resize();
	};
	
	$scope.getSourceGrantReadWizard = function(){
		$.ajax({
			url: $('body').data('baseurl') + 'my-orcid/sourceGrantReadWizard.json',	        
	        dataType: 'json',
	        success: function(data) {
	        	$scope.sourceGrantReadWizard = data;
	        	//console.log(angular.toJson(data))
	        	$scope.$apply();
	        	$scope.showThanks();
	        }
		}).fail(function(){
			// something bad is happening!
	    	console.log("error fetching external identifiers");
		});
		
	};
	
	$scope.yes = function () {
		$.colorbox.close();
		var newWin = window.open($scope.sourceGrantReadWizard.url);
		if (!newWin) window.location.href = $scope.sourceGrantReadWizard.url;
		else newWin.focus();
	};
	
	$scope.close = function () {
		$.colorbox.close();
	};
	
	$scope.getSourceGrantReadWizard();
	
};

function WorkCtrl($scope, $compile){
	$scope.works = new Array();
	
	$scope.addWorkToScope = function(){
		if($scope.worksToAddIds.length != 0 ) {
			var workIds = $scope.worksToAddIds.splice(0,20).join();
			$.ajax({
				url: $('body').data('baseurl') + 'my-orcid/work.json?workIds=' + workIds,
				dataType: 'json',
				success: function(data) {
					$scope.$apply(function(){ 
						for (i in data)
						$scope.works.push(data[i]);
					});
					setTimeout(function () {$scope.addWorkToScope();},50);
				}
			}).fail(function() { 
		    	console.log("Error fetching work: " + value);
		    });
		}
	}; 
	
	
	$scope.getWorks = function(){
		$.ajax({
			url: $('body').data('baseurl') + 'my-orcid/works.json',	        
	        dataType: 'json',
	        success: function(data) {
	        	$scope.worksToAddIds = data;
	        	if (data.length > 0 ) $scope.addWorkToScope();	        	
	        }
		}).fail(function(){
			// something bad is happening!
	    	console.log("error fetching works");
		});
	};
	
	//init
	$scope.getWorks();
	
	$scope.deleteWork = function(idx) {		
		$scope.deleteIndex = idx;
		if ($scope.works[idx].workTitle) 
			$scope.fixedTitle = $scope.works[idx].workTitle.title.content;
		else $scope.fixedTitle = ''
        var maxSize = 100;
        if($scope.fixedTitle.length > maxSize)
        	$scope.fixedTitle = $scope.fixedTitle.substring(0, maxSize) + '...';
		$.colorbox({        	            
            html : $compile($('#delete-work-modal').html())($scope)           
        });
		$scope.$apply(); 
        $.colorbox.resize();        
	};
	
	$scope.deleteByIndex = function() {		
		var work = $scope.works[$scope.deleteIndex];
    	// remove work on server
		$scope.removeWork(work);
		// remove the work from the UI
    	$scope.works.splice($scope.deleteIndex, 1);
    	// apply changes on scope
		$scope.$apply();
		// close box
		$.colorbox.close(); 
	};
	
	$scope.closeModal = function() {
		$.colorbox.close();
	};
	
	$scope.removeWork = function(work) {
		$.ajax({
	        url: $('body').data('baseurl') + 'my-orcid/works.json',
	        type: 'DELETE',
	        data: angular.toJson(work),
	        contentType: 'application/json;charset=UTF-8',
	        dataType: 'json',
	        success: function(data) {	        	
	        	if(data.errors.length != 0){
	        		console.log("Unable to delete work.");
	        	} 
	        }
	    }).fail(function() { 
	    	console.log("Error deleting work.");
	    });
	};
		
	$scope.setPrivacy = function(idx, priv, $event) {
		$event.preventDefault();
		$scope.works[idx].visibility.visibility = priv;
		$scope.curPrivToggle = null;
		$scope.updateProfileWork(idx);
	};
	
	$scope.updateProfileWork = function(idx) {
		var work = $scope.works[idx];
		$.ajax({
	        url: $('body').data('baseurl') + 'my-orcid/profileWork.json',
	        type: 'PUT',
	        data: angular.toJson(work),
	        contentType: 'application/json;charset=UTF-8',
	        dataType: 'json',
	        success: function(data) {	        	
	        	if(data.errors.length != 0){
	        		console.log("Unable to update profile work.");
	        	} 
	        }
	    }).fail(function() { 
	    	console.log("Error updating profile work.");
	    });
	};		
}

