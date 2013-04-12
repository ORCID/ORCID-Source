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

var orcidNgModule = angular.module('orcidApp', []);

orcidNgModule.filter('emailPrimaryFtr', function($filter) {
	return function(booleanValue) {
		return booleanValue ? OM.getInstance().get("manage.email.primary_email"): OM.getInstance().get("manage.email.set_primary");
	};
});

orcidNgModule.filter('emailVisibilityFtr', function($filter) {
	return function(strValue) {
		return strValue.substring(0,1) +  strValue.substring(1).toLowerCase() + " <span class='caret'></span>";
	};
});

orcidNgModule.filter('emailVisibilityBtnClassFtr', function($filter) {
	return function(strValue) {
		if (strValue == "PRIVATE") return "btn-danger";
		else if (strValue == "LIMITED") return "btn-warning";
		return "btn-success";
	};
});

orcidNgModule.filter('emailVerifiedFtr', function($filter) {
	return function(booleanValue) {
		return booleanValue ? OM.getInstance().get("manage.email.verifed") : OM.getInstance().get("manage.email.unverifed");
	};
});

orcidNgModule.filter('emailCurrentFtr', function($filter) {
	return function(booleanValue) {
		return booleanValue ? OM.getInstance().get("manage.email.active") : OM.getInstance().get("manage.email.inactive");
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

function PrivacyPreferences($scope, $http) {
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


function DeactivateAccount($scope, $http) {
	$scope.sendDeactivateEmail = function() {
		orcidGA.gaPush(['_trackEvent', 'Disengagement', 'Deactivate_Initiate', 'Website']);
		$.ajax({
	        url: $('body').data('baseurl') + 'account/send-deactivate-account.json',
	        dataType: 'json',
	        success: function(data) {
	    	    $.colorbox({
	    	        html : $('<div style="padding: 20px;" class="colorbox-modal"><h3>' + OM.getInstance().get("manage.deactivateSend") + data.value + '</h3>'
	    	            	+ '<div class="btn" id="modal-close">' + OM.getInstance().get("manage.deactivateSend.close") + '</div>')	            	
	    	    });
	    	    $.colorbox.resize();
	    	    $('#modal-close').click(function(e) {
	    	    	$.colorbox.close();
	    	    });
	        }
	    }).fail(function() { 
	    	// something bad is happening!
	    	console.log("error with change DeactivateAccount");
	    });

	};
}


function SecurityQuestionEdit($scope, $http) {
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
}

function PasswordEdit($scope, $http) {
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
}

function EmailEdit($scope, $http) {

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
		$.ajax({
	        url: $('body').data('baseurl') + 'account/verifyEmail.json',
	        type: 'get',
	        data:  { "email": $scope.emailsPojo.emails[idx].value },
	        contentType: 'application/json;charset=UTF-8',
	        dataType: 'json',
	        success: function(data) {
	        	//alert( "Verification Email Send To: " + $scope.emailsPojo.emails[idx].value); 	
	        }
	    }).fail(function() { 
	    	// something bad is happening!
	    	console.log("error with multi email");
	    });  
	    $.colorbox({
	        html : $('<div style="padding: 20px;" class="colorbox-modal"><h3>' + OM.getInstance().get("manage.email.verificationEmail") + ' ' + $scope.emailsPojo.emails[idx].value + '</h3>'
	            	+ '<div class="btn" id="modal-close">' + OM.getInstance().get("manage.email.verificationEmail.close") + '</div>')
	            	
	    });
	    $.colorbox.resize();
	    $('#modal-close').click(function(e) {
	    	$.colorbox.close();
	    });
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
	    	console.log("$EmailEdit.add() error");
	    });
	};
	
	$scope.deleteEmail = function(idx) {
        $.colorbox({
            html : $('<div style="padding: 20px;" class="colorbox-modal"><h3>'+ OM.getInstance().get("manage.email.pleaseConfirmDeletion") + ' ' + $scope.emailsPojo.emails[idx].value + '</h3>'
            	+ '<div class="btn btn-danger" id="modal-del-email">'
            	+ OM.getInstance().get("manage.email.deleteEmail") 
            	+ ' </div> <a href="" id="modal-cancel">' + OM.getInstance().get("manage.email.cancel") + '</a><div>')
            	
        });
        $.colorbox.resize();
        $('#modal-del-email').click(function(e) {
    		$scope.emailsPojo.emails.splice(idx, 1);
    		$scope.saveEmail();
    		$.colorbox.close();
        });
        $('#modal-cancel').click(function(e) {
        	e.preventDefault();
        	$.colorbox.close();
        });
	};
	
};

function ExternalIdentifierCtrl($scope, $http){		
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
        $.colorbox({        	
            html: function(){
            	var value = '';
            	if($scope.externalIdentifiersPojo.externalIdentifiers[idx].externalIdCommonName != null)
            		value = $scope.externalIdentifiersPojo.externalIdentifiers[idx].externalIdCommonName.content + ' ';
            	value += $scope.externalIdentifiersPojo.externalIdentifiers[idx].externalIdReference.content;
            	return '<div style="padding: 20px;" class="colorbox-modal"><h3>' + OM.getInstance().get("manage.deleteExternalIdentifier.pleaseConfirm") + ' ' + value + '</h3>'
	            	+ '<div class="btn btn-danger" id="modal-del-external-identifier">' + OM.getInstance().get("manage.deleteExternalIdentifier.delete") + '</div> <a href="" id="modal-cancel">' + OM.getInstance().get("manage.deleteExternalIdentifier.cancel") + '</a><div>'; 
            }
            	
        });
        $.colorbox.resize();
        $('#modal-del-external-identifier').click(function(e) {
    		$scope.externalIdentifiersPojo.externalIdentifiers.splice(idx, 1);
    		$scope.saveExternalIdentifier();
    		$.colorbox.close();
        });
        $('#modal-cancel').click(function(e) {
        	e.preventDefault();
        	$.colorbox.close();
        });
	};
	
	$scope.saveExternalIdentifier = function() {
		$.ajax({
	        url: $('body').data('baseurl') + 'my-orcid/externalIdentifiers.json',
	        type: 'POST',
	        data: angular.toJson($scope.externalIdentifiersPojo),
	        contentType: 'application/json;charset=UTF-8',
	        dataType: 'json',
	        success: function(data) {
	        	$scope.externalIdentifiersPojo = data;
	        	$scope.$apply();
	        }
	    }).fail(function() { 
	    	console.log("error with external identifiers");
	    });
	};
}
