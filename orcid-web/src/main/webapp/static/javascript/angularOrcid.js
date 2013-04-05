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
		return booleanValue ? 'Primary Email' : 'Set Primary';
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
		return booleanValue ? 'Verifed' : 'Unverfied';
	};
});

orcidNgModule.filter('emailCurrentFtr', function($filter) {
	return function(booleanValue) {
		return booleanValue ? 'Active' : 'Inactive';
	};
});

function EditTableCtrl($scope) {
	// email edit table
	$scope.showEditEmail = (window.location.hash === "#editEmail");
	
	$scope.emailToggleText = "Edit";
	$scope.toggleEmailEdit = function() {
		$scope.showEditEmail = !$scope.showEditEmail;
		if ($scope.emailToggleText == "Edit")
			$scope.emailToggleText = "Hide";
		else
			$scope.emailToggleText = "Edit";
	};
	
	// password edit table
	$scope.showEditPassword = (window.location.hash === "#editPassword");
	$scope.passwordToggleText = "Edit";
	$scope.togglePasswordEdit = function() {
		$scope.showEditPassword = !$scope.showEditPassword;
		if ($scope.passwordToggleText == "Edit")
			$scope.passwordToggleText = "Hide";
		else
			$scope.passwordToggleText = "Edit";
	};

};

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
	    	console.log("error with multi email");
	    });
	};
}

function EmailEdit($scope, $http) {

	$scope.getEmails = function() {
		$.ajax({
	        url: $('body').data('baseurl') + 'account/emails.json',	        
	        dataType: 'json',
	        success: function(data) {
	        	$scope.externalIdentifiersPojo = data;
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
	
//	$scope.current = function(idx) {
//		$scope.saveEmail();
//	};
//	
	
	// descoped delete after March 20
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
	        html : $('<div style="padding: 20px;" class="colorbox-modal"><h3>Verification email sent to ' + $scope.emailsPojo.emails[idx].value + '</h3>'
	            	+ '<div class="btn" id="modal-close">Close</div>')
	            	
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
            html : $('<div style="padding: 20px;" class="colorbox-modal"><h3>Please confirm deletion of ' + $scope.emailsPojo.emails[idx].value + '</h3>'
            	+ '<div class="btn btn-danger" id="modal-del-email">Delete Email</div> <a href="" id="modal-cancel">cancel</a><div>')
            	
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
            	var value = null;
            	if($scope.externalIdentifiersPojo.externalIdentifiers[idx].externalIdUrl != null)
            		value = $scope.externalIdentifiersPojo.externalIdentifiers[idx].externalIdUrl.value;
            	else
            		value = $scope.externalIdentifiersPojo.externalIdentifiers[idx].externalIdReference.content;
            	return '<div style="padding: 20px;" class="colorbox-modal"><h3>Please confirm deletion of ' + value + '</h3>'
	            	+ '<div class="btn btn-danger" id="modal-del-external-identifier">Delete</div> <a href="" id="modal-cancel">cancel</a><div>'; 
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