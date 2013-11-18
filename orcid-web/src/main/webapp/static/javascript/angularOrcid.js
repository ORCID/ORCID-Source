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

var orcidNgModule = angular.module('orcidApp', ['ngCookies','ngSanitize']);

orcidNgModule.directive('ngModelOnblur', function() {
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

orcidNgModule.factory("affiliationsSrvc", ['$rootScope', function ($rootScope) {
	var serv = {
			affiliations: new Array(),
			educations: new Array(),
			employments: new Array(),
			loading: false,
			affiliationsToAddIds: null,
	    	addAffiliationToScope: function(path) {
	    		if(serv.affiliationsToAddIds.length != 0 ) {
	    			var affiliationIds = serv.affiliationsToAddIds.splice(0,20).join();
	    			$.ajax({
	    				url: $('body').data('baseurl') + path + '?affiliationIds=' + affiliationIds,
	    				dataType: 'json',
	    				success: function(data) {
	    						for (i in data) {
	    							if (data[i].affiliationType != null && data[i].affiliationType.value != null
	    									&& data[i].affiliationType.value == 'education')
	    								serv.educations.push(data[i]);
	    							else if (data[i].affiliationType != null && data[i].affiliationType.value != null
	    									&& data[i].affiliationType.value == 'employment')
	    								serv.employments.push(data[i]);
	    							else
	    								serv.affiliations.push(data[i]);
	    						};
	    					setTimeout(function () {serv.addAffiliationToScope(path);},50);
	    				}
	    			}).fail(function() { 
	    		    	console.log("Error fetching affiliation: " + value);
	    		    });
	    		} else {
	    			$rootScope.$apply( function() {
	    				serv.loading = false;
	    			});
	    		};
	    	},
	    	setIdsToAdd: function(ids) {
	    		serv.affiliationsToAddIds = ids;
	    	},
	    	getAffiliations: function(path) {
	    		//clear out current affiliations
	    		serv.loading = true;
	    		serv.affiliationsToAddIds = null;
	    		serv.affiliations.length = 0;
	    		serv.educations.length = 0;
	    		serv.employments.length = 0;
	    		//get affiliation ids
	    		$.ajax({
	    			url: $('body').data('baseurl') + path,	        
	    	        dataType: 'json',
	    	        success: function(data) {
	    	        	serv.affiliationsToAddIds = data;
	    	        	serv.addAffiliationToScope('affiliations/affiliations.json');
	    	        	$rootScope.$apply();
	    	        }
	    		}).fail(function(){
	    			// something bad is happening!
	    	    	console.log("error fetching affiliations");
	    		});
	    	},
	    	updateProfileAffiliation: function(aff) {
	    		$.ajax({
	    	        url: $('body').data('baseurl') + 'affiliations/affiliation.json',
	    	        type: 'PUT',
	    	        data: angular.toJson(aff),
	    	        contentType: 'application/json;charset=UTF-8',
	    	        dataType: 'json',
	    	        success: function(data) {	        	
	    	        	if(data.errors.length != 0){
	    	        		console.log("Unable to update profile affiliation.");
	    	        	}
	    	        	$rootScope.$apply();
	    	        }
	    	    }).fail(function() { 
	    	    	console.log("Error updating profile affiliation.");
	    	    });
	    	},
	    	deleteAffiliation: function(affiliation) {
				var arr = serv.affiliations;
				if (affiliation.affiliationType != null && affiliation.affiliationType.value != null
						&& affiliation.affiliationType.value == 'education')
					arr = serv.educations;
				if (affiliation.affiliationType != null && affiliation.affiliationType.value != null
						&& affiliation.affiliationType.value == 'employment')
					arr = serv.employments;
				var idx;
				for (idx in arr) {
					if (arr[idx].putCode.value == affiliation.putCode.value) {
						break;
					}
				}
				arr.splice(idx, 1);
	    		$.ajax({
	    	        url: $('body').data('baseurl') + 'affiliations/affiliations.json',
	    	        type: 'DELETE',
	    	        data: angular.toJson(affiliation),
	    	        contentType: 'application/json;charset=UTF-8',
	    	        dataType: 'json',
	    	        success: function(data) {	        	
	    	        	if(data.errors.length != 0){
	    	        		console.log("Unable to delete affiliation.");
	    	        	}
	    	        	$rootScope.$apply();
	    	        }
	    	    }).fail(function() { 
	    	    	console.log("Error deleting affiliation.");
	    	    });
	    	}  
	}; 
	return serv;
}]);

orcidNgModule.factory("worksSrvc", function () {
	var serv = {
		    loading: false,
			works: new Array()
	}; 
	return serv;
});

orcidNgModule.factory("prefsSrvc", function ($rootScope) {
	var serv = {
			prefs: null,
			getPrivacyPreferences: function() {
				$.ajax({
			        url: $('body').data('baseurl') + 'account/preferences.json',
			        dataType: 'json',
			        success: function(data) {
			        	serv.prefs = data;
			        	$rootScope.$apply;
			        }
			    }).fail(function() { 
			    	// something bad is happening!
			    	console.log("error with multi email");
			    });
			},
			savePrivacyPreferences: function() {
				$.ajax({
			        url: $('body').data('baseurl') + 'account/preferences.json',
			        type: 'POST',
			        data: angular.toJson(serv.prefs),
			        contentType: 'application/json;charset=UTF-8',
			        dataType: 'json',
			        success: function(data) {
			        	serv.prefs = data;
			        	$rootScope.$apply;
			        }
			    }).fail(function() { 
			    	// something bad is happening!
			    	console.log("error with multi email");
			    });
			}
		};
	    
	    // populate the prefs
		serv.getPrivacyPreferences();

	return serv; 
});


orcidNgModule.filter('urlWithHttp', function(){
	return function(input){
		if (input == null) return input;
		if (!input.startsWith('http')) return 'http://' + input; 
	    return input;
	};
});

function fixZindexIE7(target, zindex){
	$(target).each(function(){
		$(this).css('z-index', zindex);    			
		--zindex;    			    		
	});
}

function emptyTextField(field) {
	if (field != null 
			&& field.value != null
			&& field.value.trim() != '') return false;
	return true;
}

function addComma(str) {
	if (str.length > 0) return str + ', ';
	return str;
}

orcidNgModule.filter('contributorFilter', function(){
	return function(ctrb){
		var out = '';
		if (!emptyTextField(ctrb.contributorRole)) out = out + ctrb.contributorRole.value;
		if (!emptyTextField(ctrb.contributorSequence)) out = addComma(out) + ctrb.contributorSequence.value; 
		if (!emptyTextField(ctrb.orcid)) out = addComma(out) + ctrb.orcid.value; 
		if (!emptyTextField(ctrb.email)) out = addComma(out) + ctrb.email.value;
		if (out.length > 0) out = '(' + out + ')';
	    return out;
	};
});


orcidNgModule.filter('workExternalIdentifierHtml', function(){
	return function(workExternalIdentifier, first, last, length){
		var output = '';
		
		if (workExternalIdentifier == null) return output;
		var id = workExternalIdentifier.workExternalIdentifierId.value;
		var type;
		if (workExternalIdentifier.workExternalIdentifierType != null)
			type = workExternalIdentifier.workExternalIdentifierType.value;
		if (type != null) output = output + type.toUpperCase() + ": ";
		var link = workIdLinkJs.getLink(id,type);
		if (link != null) 
		    output = output + "<a href='" + link + "' target='_blank'>" + id + "</a>";
		else
			output = output + id;
		
		if (length > 1 && !last) output = output + ',';
	    return output;
	};
});


function addBibtexCitation($scope, dw) {
	if (dw.citation && dw.citation.citationType.value == 'bibtex') {
		try {
			$scope.bibtexCitations[dw.putCode.value] = bibtexParse.toJSON(dw.citation.citation.value);
		} catch (err) {
			$scope.bibtexCitations[dw.putCode.value] = 'Error Parsing Bibtex';
			console.log("couldn't parse bibtex: " + dw.citation.citation.value);
		}
	}
}

function removeBadContributors(dw) {
	for (idx in dw.contributors) {
		if (dw.contributors[idx].contributorSequence == null
			&& dw.contributors[idx].email == null
			&& dw.contributors[idx].orcid == null
			&& dw.contributors[idx].creditName == null
			&& dw.contributors[idx].contributorRole == null
			&& dw.contributors[idx].creditNameVisibility == null)
			delete dw.contributors.splice(idx,1);
	}
}

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


function WorksPrivacyPreferencesCtrl($scope, prefsSrvc) {
	$scope.prefsSrvc = prefsSrvc;
	
	$scope.updateWorkVisibilityDefault = function(priv, $event) {
		$scope.prefsSrvc.prefs.workVisibilityDefault.value = priv;
		$scope.prefsSrvc.savePrivacyPreferences();
	};	
};


function EmailPreferencesCtrl($scope, prefsSrvc) {
	$scope.prefsSrvc = prefsSrvc;
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


function SecurityQuestionEditCtrl($scope, $compile) {
	$scope.errors=null;
	$scope.password=null;
	
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
	
	$scope.checkCredentials = function() {
		$scope.password=null;
		$.colorbox({        	
			html: $compile($('#check-password-modal').html())($scope)
		});
		$.colorbox.resize();
	};
	
	$scope.submitModal = function() {	
		$scope.securityQuestionPojo.password=$scope.password;		
		console.log(angular.toJson($scope.securityQuestionPojo));		
		$.ajax({
	        url: $('body').data('baseurl') + 'account/security-question.json',
	        type: 'POST',
	        data: angular.toJson($scope.securityQuestionPojo),
	        contentType: 'application/json;charset=UTF-8',
	        dataType: 'json',
	        success: function(data) {
	        	//alert(angular.toJson($scope.securityQuestionPojo));
	        	if(data.errors.length != 0) {
	        		$scope.errors=data.errors;
	        	} else {
	        		$scope.errors=null;
	        	}
	        	$scope.getSecurityQuestion();	        	
	        	$scope.$apply();
	        }
	    }).fail(function() { 
	    	// something bad is happening!
	    	console.log("error with security question");
	    });
		$scope.password=null;
		$.colorbox.close();
	};
	
	$scope.closeModal = function() {
		$.colorbox.close();
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
	$scope.password = null;
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

	$scope.submitModal = function (obj, $event) {
		$scope.inputEmail.password = $scope.password;
		
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
		
		$.colorbox.close();
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
	
	$scope.checkCredentials = function() {
		$scope.password=null;
		$.colorbox({        	
			html: $compile($('#check-password-modal').html())($scope)
		});
		$.colorbox.resize();
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

function ResetPasswordCtrl($scope, $compile) {
	$scope.getResetPasswordForm = function(){
		$.ajax({
			url: $('body').data('baseurl') + 'password-reset.json',	        
	        dataType: 'json',
	        success: function(data) {
	        	console.log(angular.toJson(data));
	        	$scope.resetPasswordForm = data;
	        	$scope.$apply();
	        }
		}).fail(function(){
		// something bad is happening!
			console.log("error fetching password-reset.json");
		});		
	};
				
	$scope.serverValidate = function () {
		$.ajax({
	        url: $('body').data('baseurl') + 'reset-password-form-validate.json',
	        type: 'POST',
	        data:  angular.toJson($scope.resetPasswordForm),
	        contentType: 'application/json;charset=UTF-8',
	        dataType: 'json',
	        success: function(data) {
	        	$scope.copyErrorsLeft($scope.resetPasswordForm, data);
	        	$scope.$apply();
	        }
	    }).fail(function() { 
	    	// something bad is happening!
	    	console.log("ResetPasswordCtrl.serverValidate() error");
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
	
	//init
	$scope.getResetPasswordForm();	
}

function RegistrationCtrl($scope, $compile) {
		
	$scope.getRegister = function(){
		$.ajax({
			url: $('body').data('baseurl') + 'register.json',	        
	        dataType: 'json',
	        success: function(data) {
	       	$scope.register = data;
	        $scope.$apply();
	        
	        // make sure inputs stayed trimmed
	    	$scope.$watch('register.email.value', function() {
	    		trimAjaxFormText($scope.register.email);
	    		$scope.serverValidate('Email');
	    	}); // initialize the watch
	    	
	    	// make sure email is trimmed
	    	$scope.$watch('register.emailConfirm.value', function() {
	    		 trimAjaxFormText($scope.register.emailConfirm);
	    		 $scope.serverValidate('EmailConfirm');
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
	        html : $('<div style="font-size: 50px; line-height: 60px; padding: 20px; text-align:center">' + OM.getInstance().get('common.processing') + '&nbsp;<i id="ajax-loader" class="glyphicon glyphicon-refresh spin green"></i></div>'),
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
	
	//init
	$scope.getRegister();	
	//$scope.getDuplicates();
		 
};
		

function ClaimCtrl($scope, $compile) {
	$scope.postingClaim = false;
	$scope.getClaim = function(){
		$.ajax({
			url: $scope.getClaimAjaxUrl(),	        
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
		if ($scope.postingClaim) return;
		$scope.postingClaim = true;
		$.ajax({
	        url: $scope.getClaimAjaxUrl(),
	        type: 'POST',
	        data:  angular.toJson($scope.register),
	        contentType: 'application/json;charset=UTF-8',
	        dataType: 'json',
	        success: function(data) {
	        	$scope.register = data;
	        	
	        	if ($scope.register.errors.length == 0) {
	        		if ($scope.register.url != null) {
		    	    	orcidGA.gaPush(['_trackEvent', 'RegGrowth', 'New-Registration', 'Website']);
			    		orcidGA.windowLocationHrefDelay($scope.register.url);
	        		}
	        	}
	        	$scope.postingClaim = false;
	        	$scope.$apply();
	        }
	    }).fail(function() { 
	    	// something bad is happening!
	    	console.log("RegistrationCtrl.postRegister() error");
	    	$scope.postingClaim = false;
	    });
	};
	
	
	$scope.getClaimAjaxUrl = function () {
		return window.location.href.split("?")[0]+".json";
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
	    $.colorbox.resize({width:"500px", height:"200px"});
		
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

function PersonalInfoCtrl($scope, $compile){
	$scope.displayInfo = true;
	$scope.toggleDisplayInfo = function () {
		$scope.displayInfo = !$scope.displayInfo;
	};
};

function WorkspaceSummaryCtrl($scope, $compile, affiliationsSrvc, worksSrvc){
	$scope.worksSrvc = worksSrvc;
	$scope.affiliationsSrvc = affiliationsSrvc;
	$scope.showAddAlert = function () {
		if (worksSrvc.loading == false && affiliationsSrvc.loading == false
				&& worksSrvc.works.length == 0 
				&& affiliationsSrvc.educations.length == 0
				&& affiliationsSrvc.employments.length == 0)
			return true;
		return false;
	};
}

function PublicEduAffiliation($scope, $compile, $filter, affiliationsSrvc){
	$scope.affiliationsSrvc = affiliationsSrvc;
}

function PublicEmpAffiliation($scope, $compile, $filter, affiliationsSrvc){
	$scope.affiliationsSrvc = affiliationsSrvc;
	affiliationsSrvc.setIdsToAdd(orcidVar.affiliationIdsJson);
	affiliationsSrvc.addAffiliationToScope(orcidVar.orcidId +'/affiliations.json');
}


function AffiliationCtrl($scope, $compile, $filter, affiliationsSrvc){
	$scope.affiliationsSrvc = affiliationsSrvc;
	$scope.displayAffiliations = true;
	$scope.displayEducation = true;
	$scope.displayEmployment = true;
	
	$scope.toggleDisplayEducation = function () {
		$scope.displayEducation = !$scope.displayEducation;
	};	

	$scope.toggleDisplayEmployment = function () {
		$scope.displayEmployment = !$scope.displayEmployment;
	};	

	$scope.toggleDisplayAffiliations = function () {
		$scope.displayAffiliations = !$scope.displayAffiliations;
	};	

	$scope.showAddModal = function(){
		isMobile() ? w = '100%' : w = '800px';
		isMobile() ? h = '100%' : h = 'auto';
		var numOfResults = 25;
		$.colorbox({        	
			html: $compile($('#add-affiliation-modal').html())($scope),
			onComplete: function() {
							$.colorbox.resize({width:w, height:h});
							$scope.bindTypeahead();
			}
	    });
	};
	
	$scope.bindTypeahead = function () {
		var numOfResults = 100;
		
		$("#affiliationName").typeahead({
			name: 'affiliationName',
			limit: numOfResults,
			remote: {
				url: $('body').data('baseurl')+'affiliations/disambiguated/name/%QUERY?limit=' + numOfResults
			},
			template: function (datum) {
				   var forDisplay = 
				       '<span style=\'white-space: nowrap; font-weight: bold;\'>' + datum.value+ '</span>'
				      +'<span style=\'font-size: 80%;\'>'
				      + ' <br />' + datum.city;
				   if(datum.region){
					   forDisplay += ", " + datum.region;
				   }
				   if (datum.orgType != null && datum.orgType.trim() != '')
				      forDisplay += ", " + datum.orgType;
				   forDisplay += '</span><hr />';
				   return forDisplay;
			}
		});
		$("#affiliationName").bind("typeahead:selected", function(obj, datum) {        
			$scope.selectAffiliation(datum);
			$scope.$apply();
		});		
	};
	
	$scope.unbindTypeahead = function () {
		$('#affiliationName').typeahead('destroy');
	};
	
	$scope.selectAffiliation = function(datum) {
		$scope.editAffiliation.affiliationName.value = datum.value;
		$scope.editAffiliation.city.value = datum.city;
		$scope.editAffiliation.region.value = datum.region;
		$scope.editAffiliation.country.value = datum.country;
		if (datum.disambiguatedAffiliationIdentifier != undefined && datum.disambiguatedAffiliationIdentifier != null) {
			$scope.getDisambiguatedAffiliation(datum.disambiguatedAffiliationIdentifier);
			$scope.unbindTypeahead();
		}
	};
	
	$scope.getDisambiguatedAffiliation = function(id) {
		$.ajax({
			url: $('body').data('baseurl') + 'affiliations/disambiguated/id/' + id,
	        dataType: 'json',
	        type: 'GET',
	        success: function(data) {
	        	console.log(data.sourceId);
		        $scope.disambiguatedAffiliation = data;
		        $scope.editAffiliation.disambiguatedAffiliationSourceId = data.sourceId;
		        $scope.editAffiliation.disambiguationSource = data.sourceType;
		        $scope.$apply();
	        }
		}).fail(function(){
	    	console.log("error getDisambiguatedAffiliation(id)");
		});
	};
	
	$scope.removeDisambiguatedAffiliation = function() {
		$scope.bindTypeahead();
		delete $scope.disambiguatedAffiliation;
		delete $scope.editAffiliation.disambiguatedAffiliationSourceId;
	};

	$scope.addAffiliationModal = function(type){
		$scope.addAffType = type;
		$.ajax({
			url: $('body').data('baseurl') + 'affiliations/affiliation.json',
			dataType: 'json',
			success: function(data) {
				$scope.editAffiliation = data;
				if (type != null) 
					$scope.editAffiliation.affiliationType.value = type;
				$scope.$apply(function() {
					$scope.showAddModal();
				});
			}
		}).fail(function() { 
	    	console.log("Error fetching affiliation: " + value);
	    });
	};

	$scope.addAffiliation = function(){
		if ($scope.addingAffiliation) return; // don't process if adding affiliation
		$scope.addingAffiliation = true;
		$scope.editAffiliation.errors.length = 0;
		$.ajax({
			url: $('body').data('baseurl') + 'affiliations/affiliation.json',
	        contentType: 'application/json;charset=UTF-8',
	        dataType: 'json',
	        type: 'POST',
	        data:  angular.toJson($scope.editAffiliation),
	        success: function(data) {
	        	if (data.errors.length == 0){
	        		$.colorbox.close(); 
	        		$scope.addingAffiliation = false;
	        		affiliationsSrvc.getAffiliations('affiliations/affiliationIds.json');
	        	} else {
		        	$scope.editAffiliation = data;
		        	$scope.copyErrorsLeft($scope.editAffiliation, data);
		        	$scope.addingAffiliation = false;
		        	$scope.$apply();
	        	}
	        }
		}).fail(function(){
			// something bad is happening!
			$scope.addingAffiliation = false;
	    	console.log("error adding affiliations");
		});
	};
	
	$scope.deleteAffiliation = function(aff) {
		$scope.deleAff = aff;
		
		if (aff.affiliationName && aff.affiliationName.value) 
			$scope.fixedTitle = aff.affiliationName.value;
		else $scope.fixedTitle = '';
        var maxSize = 100;
        if($scope.fixedTitle.length > maxSize)
        	$scope.fixedTitle = $scope.fixedTitle.substring(0, maxSize) + '...';
		$.colorbox({        	            
            html : $compile($('#delete-affiliation-modal').html())($scope),
            onComplete: function() {$.colorbox.resize();}
        });
	};
	
	$scope.deleteAff = function(delAff) {		
		affiliationsSrvc.deleteAffiliation(delAff);
		$.colorbox.close(); 
	};
	
	$scope.closeModal = function() {
		$.colorbox.close();
	};
	
	$scope.setAddAffiliationPrivacy = function(priv, $event) {
		$event.preventDefault();
		$scope.editAffiliation.visibility.visibility = priv;
	};
		
	$scope.setPrivacy = function(aff, priv, $event) {
		$event.preventDefault();
		aff.visibility.visibility = priv;
		affiliationsSrvc.updateProfileAffiliation(aff);
	};
	
	$scope.serverValidate = function (relativePath) {
		$.ajax({
	        url: $('body').data('baseurl') + relativePath,
	        type: 'POST',
	        data:  angular.toJson($scope.editAffiliation),
	        contentType: 'application/json;charset=UTF-8',
	        dataType: 'json',
	        success: function(data) {
	        	$scope.copyErrorsLeft($scope.editAffiliation, data);
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
			if (key == null) continue;
			if (key == 'errors') {
				data1.errors = data2.errors;
			} else {
				if (typeof(data1[key])=="object") {
					$scope.copyErrorsLeft(data1[key], data2[key]);
				}
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
	affiliationsSrvc.getAffiliations('affiliations/affiliationIds.json');
}

function PublicWorkCtrl($scope, $compile, worksSrvc) {
	$scope.works = worksSrvc.works;
	$scope.worksSrvc = worksSrvc;
	$scope.showBibtex = true;
	$scope.bibtexCitations = {};

    $scope.bibtexShowToggle = function () {
    	$scope.showBibtex = !($scope.showBibtex);
    };   

    $scope.addWorkToScope = function() {
		if($scope.worksToAddIds.length != 0 ) {
			$scope.worksSrvc.loading = true;
			var workIds = $scope.worksToAddIds.splice(0,20).join();
			$.ajax({
				url: $('body').data('baseurl') + orcidVar.orcidId +'/works.json?workIds=' + workIds,
				dataType: 'json',
				success: function(data) {
					$scope.$apply(function(){ 
						for (i in data) {
							var dw = data[i];                            
							removeBadContributors(dw);							
							addBibtexCitation($scope,dw);							
							$scope.works.push(dw);
						}
					});
					setTimeout(function () {$scope.addWorkToScope();},50);
				}
			}).fail(function() { 
				$scope.$apply(function() {
					$scope.worksSrvc.loading = false;
				});
		    	console.log("Error fetching works: " + workIds);
		    });
		} else {
			$scope.$apply(function () {
				$scope.worksSrvc.loading = false;
			});
		}
	};     
	  
	$scope.renderTranslatedTitleInfo = function(workIdx) {
		var info = null; 
		
		if($scope.works[workIdx].workTitle != null && $scope.works[workIdx].workTitle.translatedTitle != null) {
			info = $scope.works[workIdx].workTitle.translatedTitle.content + ' - ' + $scope.works[workIdx].workTitle.translatedTitle.languageName;										
		}
		
		return info;
	};
		
	$scope.worksToAddIds = orcidVar.workIds;	
	$scope.addWorkToScope();	
}

function WorkCtrl($scope, $compile, worksSrvc) {
	$scope.displayWorks = true;
	$scope.worksSrvc = worksSrvc;
	$scope.works = worksSrvc.works;
	$scope.showBibtex = true;
	$scope.bibtexCitations = {};
	$scope.editTranslatedTitle = false;
	$scope.types = null;
	
	$scope.toggleDisplayWorks = function () {
		$scope.displayWorks = !$scope.displayWorks;
	};	
	
	$scope.addExternalIdentifier = function () {
		$scope.editWork.workExternalIdentifiers.push({workExternalIdentifierId: { value: ""}, workExternalIdentifierType: {value: ""} });
	};
	
	$scope.showAddModal = function(){;
		$scope.editTranslatedTitle = false;
		isMobile() ? w = '100%' : w = '800px';
		isMobile() ? h = '100%' : h = 'auto';		
	    $.colorbox({	    	
	    	scrolling: true,
	        html: $compile($('#add-work-modal').html())($scope),	        
	        onLoad: function() {$('#cboxClose').remove();},
	        onComplete: function() {
	        		$.colorbox.resize({width:w, height:h});
	        		}
	    });
	};

	$scope.toggleTranslatedTitleModal = function(){
		$scope.editTranslatedTitle = !$scope.editTranslatedTitle;
    	$('#translatedTitle').toggle();
    	$.colorbox.resize();
	};		
    
    $scope.bibtexShowToggle = function () {
    	$scope.showBibtex = !($scope.showBibtex);
    };
    
    //setTimeout(function() {$scope.showDetailModal(0); $scope.$apply();}, 1000);

	$scope.showWorkImportWizard =  function() {
		$.colorbox({        	            
            html : $compile($('#import-wizard-modal').html())($scope),
            onComplete: function() {$.colorbox.resize();}
        });
	};
	
	$scope.addWorkModal = function(){
		$.ajax({
			url: $('body').data('baseurl') + 'works/work.json',
			dataType: 'json',
			success: function(data) {
				$scope.editWork = data;
				$scope.$apply(function() {
					$scope.showAddModal();
				});
			}
		}).fail(function() { 
	    	console.log("Error fetching work: " + value);
	    });
	};


	$scope.addWork = function(){
		if ($scope.addingWork) return; // don't process if adding work
		$scope.addingWork = true;
		$scope.editWork.errors.length = 0;
		$.ajax({
			url: $('body').data('baseurl') + 'works/work.json',	        
	        contentType: 'application/json;charset=UTF-8',
	        dataType: 'json',
	        type: 'POST',
	        data:  angular.toJson($scope.editWork),
	        success: function(data) {
	        	if (data.errors.length == 0){
	        		$.colorbox.close(); 
	        		$scope.addingWork = false;
	        		$scope.getWorks();
	        	} else {
		        	$scope.editWork = data;
		        	$scope.copyErrorsLeft($scope.editWork, data);
		        	$scope.addingWork = false;
		        	$scope.$apply();
	        	}
	        }
		}).fail(function(){
			// something bad is happening!
			$scope.addingWork = false;
	    	console.log("error fetching works");
		});
	};
	
	$scope.validateCitation = function() {
		if ($scope.editWork.citation && $scope.editWork.citation.citationType.value == 'bibtex') {
			try {
				var parsed = bibtexParse.toJSON($scope.editWork.citation.citation.value);
				console.log(parsed);
				if (parsed.length == 0) throw "bibtex parse returne nothing";
				var index = $scope.editWork.citation.citation.errors.indexOf(OM.getInstance().get('manualWork.bibtext.notValid'));
				if (index > -1) {
					$scope.editWork.citation.citation.errors.splice(index, 1);
				}
			} catch (err) {
				$scope.editWork.citation.citation.errors.push(OM.getInstance().get('manualWork.bibtext.notValid'));
			};
		};
	};
		
	$scope.addWorkToScope = function() {
		if($scope.worksToAddIds.length != 0 ) {
			$scope.worksSrvc.loading = true;
			var workIds = $scope.worksToAddIds.splice(0,20).join();
			$.ajax({
				url: $('body').data('baseurl') + 'works/works.json?workIds=' + workIds,
				dataType: 'json',
				success: function(data) {
					$scope.$apply(function(){ 
						for (i in data) {
							var dw = data[i];
                            
							removeBadContributors(dw);
							
							addBibtexCitation($scope,dw);
							
							$scope.works.push(dw);
						}
					});
					setTimeout(function () {$scope.addWorkToScope();},50);
				}
			}).fail(function() { 
		    	console.log("Error fetching work: " + value);
		    });
		}else {
			$scope.$apply(function() {
				$scope.worksSrvc.loading = false;
			});
			if(isIE() == 7){
				fixZindexIE7('.workspace-toolbar', 999999);
				fixZindexIE7('.workspace-private-toolbar', 500);
				fixZindexIE7('#privacy-bar', 400);
			}
		}
	}; 	

	$scope.getWorks = function() {
		//clear out current works
		$scope.worksToAddIds = null;
		$scope.worksSrvc.loading = true;
		$scope.works.length = 0;
		//get work ids
		$.ajax({
			url: $('body').data('baseurl') + 'works/workIds.json',	        
	        dataType: 'json',
	        success: function(data) {
	        	$scope.worksToAddIds = data;
	        	$scope.addWorkToScope();
	        	$scope.$apply();
	        }
		}).fail(function(){
			// something bad is happening!
	    	console.log("error fetching works");
		});
	};
		
	
	$scope.renderTranslatedTitleInfo = function(workIdx) {		
		var info = null; 
		
		if($scope.works[workIdx].workTitle != null && $scope.works[workIdx].workTitle.translatedTitle != null) {
			info = $scope.works[workIdx].workTitle.translatedTitle.content + ' - ' + $scope.works[workIdx].workTitle.translatedTitle.languageName;										
		}		
		
		return info;
	};
		
	//init
	$scope.getWorks();	
	
	$scope.deleteWork = function(putCode) {
		$scope.deletePutCode = putCode;
		var work;
		for (idx in $scope.works) {
			if ($scope.works[idx].putCode.value == putCode) {
				work = $scope.works[idx];
				break;
			}
		}
		if (work.workTitle && work.workTitle.title) 
			$scope.fixedTitle = work.workTitle.title.value;
		else $scope.fixedTitle = '';
        var maxSize = 100;
        if($scope.fixedTitle.length > maxSize)
        	$scope.fixedTitle = $scope.fixedTitle.substring(0, maxSize) + '...';
		$.colorbox({        	            
            html : $compile($('#delete-work-modal').html())($scope),
            onComplete: function() {$.colorbox.resize();}
        });
	};
	
	$scope.deleteByPutCode = function() {		
		var work;
		var idx;
		for (idx in $scope.works) {
			if ($scope.works[idx].putCode.value == $scope.deletePutCode) {
				work = $scope.works[idx];
				break;
			}
		}
		// remove work on server
		$scope.removeWork(work);
		// remove the work from the UI
    	$scope.works.splice(idx, 1);
    	// apply changes on scope
		// close box
		$.colorbox.close(); 
	};
	
	$scope.closeModal = function() {
		$.colorbox.close();
	};
	

	$scope.openImportWizardUrl = function(url) {
		var win = window.open(url, "_target"); 
		setTimeout( function() {
		    if(!win || win.outerHeight === 0) {
		        //First Checking Condition Works For IE & Firefox
		        //Second Checking Condition Works For Chrome
		        window.location.href = url;
		    } 
		}, 250);
		$.colorbox.close();
		
	};

	
	$scope.removeWork = function(work) {
		$.ajax({
	        url: $('body').data('baseurl') + 'works/works.json',
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

	$scope.setAddWorkPrivacy = function(priv, $event) {
		$event.preventDefault();
		$scope.editWork.visibility.visibility = priv;
	};
			
	$scope.setPrivacy = function(putCode, priv, $event) {
		$event.preventDefault();
		var idx;
		for (idx in $scope.works) {
			if ($scope.works[idx].putCode.value == putCode)
				break;
		}
		$scope.works[idx].visibility.visibility = priv;
		$scope.curPrivToggle = null;
		$scope.updateProfileWork(putCode);
	};
	
	$scope.serverValidate = function (relativePath) {
		$.ajax({
	        url: $('body').data('baseurl') + relativePath,
	        type: 'POST',
	        data:  angular.toJson($scope.editWork),
	        contentType: 'application/json;charset=UTF-8',
	        dataType: 'json',
	        success: function(data) {
	        	$scope.copyErrorsLeft($scope.editWork, data);
	        	if ( relativePath == 'works/work/citationValidate.json') {
	        		$scope.validateCitation();
	        	}
	        	$scope.$apply();
	        }
	    }).fail(function() { 
	    	// something bad is happening!
	    	console.log("WorkCtrl.serverValidate() error");
	    });
	};
	
	// in the case of slow network connection
	// we don't want to overwrite  values while
	// user is typing
	$scope.copyErrorsLeft = function (data1, data2) {
		for (var key in data1) {
			if (key == null) continue;
			if (key == 'errors') {
				data1.errors = data2.errors;
			} else {
				if (typeof(data1[key])=="object") {
					$scope.copyErrorsLeft(data1[key], data2[key]);
				}
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

	
	$scope.updateProfileWork = function(putCode) {
		var work;
		for (idx in $scope.works) {
			if ($scope.works[idx].putCode.value == putCode) {
				work = $scope.works[idx];
				break;
			}
		}
		$.ajax({
	        url: $('body').data('baseurl') + 'works/profileWork.json',
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
	
	
	$scope.loadWorkTypes = function(){			
		if($scope.editWork.workCategory.value != null && $scope.editWork.workCategory.value != ""){
			$.ajax({
		        url: $('body').data('baseurl') + 'works/loadWorkTypes.json?workCategory=' + $scope.editWork.workCategory.value,
		        type: 'POST',	        
		        contentType: 'application/json;charset=UTF-8',
		        dataType: 'json',
		        success: function(data) {
		        	console.log(data);
		        	$scope.types = data;
		        	$scope.$apply();
		        }
		    }).fail(function() { 
		    	console.log("Error loading work types.");
		    });
		} else {
			$scope.types = null;
		}
	};
	
}

function QuickSearchCtrl($scope, $compile){
	$scope.results = new Array();
	$scope.numFound = 0;
	$scope.start = 0;
	$scope.rows = 10;
	
	$scope.getResults = function(rows){
		$.ajax({
			url: $('#QuickSearchCtrl').data('search-query-url') + '&start=' + $scope.start + '&rows=' + $scope.rows,      
			dataType: 'json',
			headers: { Accept: 'application/json'},
			success: function(data) {
				var resultsContainer = data['orcid-search-results']; 
				if(typeof resultsContainer !== 'undefined'){
					$scope.numFound = resultsContainer['num-found'];
					$scope.results = $scope.results.concat(resultsContainer['orcid-search-result']);
				}
				else{
					$('#no-results-alert').fadeIn(1200);
				}
				$scope.areMoreResults = $scope.numFound >= ($scope.start + $scope.rows);
				$scope.$apply();
				$('#ajax-loader').hide();
				var newSearchResults = $('.new-search-result');
				newSearchResults.fadeIn(1200);
				newSearchResults.removeClass('new-search-result');
				var newSearchResultsTop = newSearchResults.offset().top;
				var showMoreButtonTop = $('#show-more-button-container').offset().top;
				var bottom = $(window).height();
				if(showMoreButtonTop > bottom){
					$('html, body').animate(
						{ 
							scrollTop: newSearchResultsTop
						},
						1000, 
						'easeOutQuint'
					);
				}
			}
		}).fail(function(){
			// something bad is happening!
			console.log("error doing quick search");
		});
	};
	
	$scope.getMoreResults = function(){
		$('#ajax-loader').show();
		$scope.start += 10;
		$scope.getResults();
	};
	
	$scope.concatPropertyValues = function(array, propertyName){
		if(typeof array === 'undefined'){
			return '';
		}
		else{
			return $.map(array, function(o){ return o[propertyName]; }).join(', ');
		}
	};
	
	$scope.areResults = function(){
		return $scope.numFound != 0;
	};
	
	// init
	$scope.getResults(10);
};

function ClientEditCtrl($scope, $compile){	
	$scope.clients = [];
	$scope.newClient = null;
			
	// Get the list of clients associated with this user
	$scope.getClients = function(){
		$.ajax({
	        url: $('body').data('baseurl') + 'manage-clients/get-clients.json',
	        dataType: 'json',
	        success: function(data) {	        	        					
				$scope.$apply(function(){
					$scope.clients = data;      		
				});
	        }
	    }).fail(function() { 
	    	alert("Error fetching clients.");
	    	console.log("Error fetching clients.");
	    });				
	};		
	
	// Get an empty modal to add
	$scope.addClient = function(){		
		$.ajax({
			url: $('body').data('baseurl') + 'manage-clients/client.json',
			dataType: 'json',
			success: function(data) {
				$scope.newClient = data;
				console.log(data);
				$scope.$apply(function() {
					$scope.showNewClientModal();
				});
			}
		}).fail(function() { 
	    	console.log("Error fetching client");
	    });
	};
	
	// Display the modal to add a new client
	$scope.showNewClientModal = function(){
		$.colorbox({        	            
            html : $compile($('#new-client-modal').html())($scope), 
            transition: 'fade',
            onLoad: function() {
			    $('#cboxClose').remove();
			},
	        scrolling: true
        });
        $.colorbox.resize({width:"580px" , height:"380px"});
	};
	
	// Add a new uri input field to a new client
	$scope.addUriToNewClientTable = function(){		
		$scope.newClient.redirectUris.push({value: '',type: 'default'});
	};
	
	// Add a new uri input field to a existing client
	$scope.addUriToExistingClientTable = function(){
		$scope.clientToEdit.redirectUris.push({value: '',type: 'default'});
	};
	
	// Display the modal to edit a client
	$scope.editClient = function(idx) {		
		// Copy the client to edit to a scope variable 
		$scope.clientToEdit = angular.copy($scope.clients[idx]);		
		$.colorbox({        	            
            html : $compile($('#edit-client-modal').html())($scope), 
            transition: 'fade',            
	        onLoad: function() {
			    $('#cboxClose').remove();
			},
	        scrolling: true
        });		
        $.colorbox.resize({width:"450px" , height:"420px"});   
	};		
	
	// Display client details: Client ID and Client secret
	$scope.viewDetails = function(idx){
		$scope.clientDetails = $scope.clients[idx];
		$.colorbox({        	            
            html : $compile($('#view-details-modal').html())($scope),
	        scrolling: true,
	        onLoad: function() {
			    $('#cboxClose').remove();
			},
			scrolling: true
        });
		
        $.colorbox.resize({width:"550px" , height:"200px"});
        
	};
	
	$scope.closeColorBox = function(){
		$.colorbox.close();	
	};
	
	
	// Delete an uri input field 
	$scope.deleteUri = function(idx){
		$scope.clientToEdit.redirectUris.splice(idx, 1);
	};
	
	//Submits the client update request
	$scope.submitEditClient = function(){				
		// Check which redirect uris are empty strings and remove them from the array
		for(var j = $scope.clientToEdit.length - 1; j >= 0 ; j--)	{
			if(!$scope.clientToEdit.redirectUris[j].value){
				$scope.clientToEdit.redirectUris.splice(j, 1);
			}
		}				
		
		//Submit the update request
		$.ajax({
	        url: $('body').data('baseurl') + 'manage-clients/edit-client.json',
	        type: 'POST',
	        data: angular.toJson($scope.clientToEdit),
	        contentType: 'application/json;charset=UTF-8',
	        dataType: 'json',
	        success: function(data) {
	        	if(data.errors != null && data.errors.length > 0){
	        		$scope.clientToEdit = data;
	        		$scope.$apply();
	        	} else {
	        		//If everything worked fine, reload the list of clients
        			$scope.getClients();
        			$.colorbox.close();
	        	} 
	        }
	    }).fail(function() { 
	    	alert("An error occured updating the client");
	    	console.log("Error updating client information.");
	    });				
	};
	
	//Submits the new client request
	$scope.submitAddClient = function(){		
		// Check which redirect uris are empty strings and remove them from the array
		for(var j = $scope.newClient.redirectUris.length - 1; j >= 0 ; j--)	{
			if(!$scope.newClient.redirectUris[j].value){
				$scope.newClient.redirectUris.splice(j, 1);
			}
		}
		
		//Submit the new client request
		$.ajax({
	        url: $('body').data('baseurl') + 'manage-clients/add-client.json',
	        type: 'POST',
	        data: angular.toJson($scope.newClient),
	        contentType: 'application/json;charset=UTF-8',
	        dataType: 'json',
	        success: function(data) {	        	
	        	if(data.errors != null && data.errors.length > 0){
	        		$scope.newClient = data;
	        		$scope.$apply();
	        	} else {
	        		//If everything worked fine, reload the list of clients
	        		$scope.getClients();
	        		$.colorbox.close();
	        	}
	        }
	    }).fail(function() { 
	    	console.log("Error creating client information.");
	    });		
	};
	    
	//init
	$scope.getClients();
};

function statisticCtrl($scope){	
	$scope.liveIds = 0;	
	$scope.getLiveIds = function(){
		$.ajax({
	        url: $('body').data('baseurl')+'statistics/liveids.json',	        
	        type: 'GET',
	        dataType: 'html',
	        success: function(data){
	        	$scope.liveIds = data;
	        	$scope.$apply($scope.liveIds);	        		        	
	        }
	    }).fail(function(error) { 
	    	// something bad is happening!	    	
	    	console.log("Error getting statistics Live iDs total amount");	    	
	    });
	};

	$scope.getLiveIds();
};

function languageCtrl($scope, $cookies){		
	$scope.languages = 
	    [
	        {	            
	            "value": "en",
	            "label": "English"
	        },
	        {
	        	"value": 'es',
	    		"label": 'Español'
	        },
	        {
	        	"value": 'fr',
	    		"label": 'Français'
	        },	        
	        {
		        "value": 'zh_CN',
			    "label": '简体中文'
	        },
	        {
		        "value": 'zh_TW',
			    "label": '繁體中文'
	        }	        
	    ];	

	//Load Language that is set in the cookie or set default language to english
	$scope.getCurrentLanguage = function(){
		$scope.language = $scope.languages[0]; //Default
		typeof($cookies.locale_v3) !== 'undefined' ? locale_v3 = $cookies.locale_v3 : locale_v3 = "en"; //If cookie exists we get the language value from it		
    	angular.forEach($scope.languages, function(value, key){ //angular.forEach doesn't support break
    		if (value.value == locale_v3) $scope.language = $scope.languages[key];    		
    	});
	};

	$scope.getCurrentLanguage(); //Checking for the current language value


	$scope.selectedLanguage = function(){		
		$.ajax({
	        url: orcidVar.baseUri+'/lang.json?lang=' + $scope.language.value + "&callback=?",	        
	        type: 'GET',
	        dataType: 'json',
	        success: function(data){
	        	angular.forEach($scope.languages, function(value, key){
	        		if(value.value == data.locale){
	        			$scope.language = $scope.languages[key];
	        			window.location.reload(true);
	        		}
	        	});	        
	        }
	    }).fail(function(error) { 
	    	// something bad is happening!	    	
	    	console.log("Error setting up language cookie");	    	
	    });		
	};
};

function profileDeactivationAndReactivationCtrl($scope,$compile){
	$scope.orcidToDeactivate = null;
	$scope.orcidToReactivate = null;
	$scope.deactivatedAccount = null;
	$scope.reactivatedAccount = null;
	$scope.successMessage = null;
	$scope.deactivateMessage = OM.getInstance().get('admin.profile_deactivation.success');
	$scope.reactivateMessage = OM.getInstance().get('admin.profile_reactivation.success');
	$scope.showDeactivateModal = false;
	$scope.showReactivateModal = false;

	$scope.toggleDeactivationModal = function(){
		$scope.showDeactivateModal = !$scope.showDeactivateModal;
    	$('#deactivation_modal').toggle();
	};
	
	$scope.toggleReactivationModal = function(){
		$scope.showReactivateModal = !$scope.showReactivateModal;
    	$('#reactivation_modal').toggle();
	};
	
	$scope.deactivateAccount = function() {
		$.ajax({
	        url: orcidVar.baseUri+'/admin-actions/deactivate-profile?orcid=' + $scope.orcidToDeactivate,	        
	        type: 'GET',
	        dataType: 'json',
	        success: function(data){
	        	$scope.$apply(function(){ 
	        		$scope.deactivatedAccount = data;
	        		if($scope.deactivatedAccount.errors != null && $scope.deactivatedAccount.errors.length != 0){	        				        			
	        			console.log($scope.deactivatedAccount.errors);
	        			$scope.closeModal();
	        		} else {
	        			$scope.orcidToDeactivate = null;
	        			$scope.showSuccessMessage($scope.deactivateMessage);
	        		}	        		
				});
	        }
	    }).fail(function(error) { 
	    	// something bad is happening!	    	
	    	console.log("Error deprecating the account");	    	
	    });		
	};
	
	
	$scope.reactivateAccount = function() {
		$.ajax({
	        url: orcidVar.baseUri+'/admin-actions/reactivate-profile?orcid=' + $scope.orcidToReactivate,	        
	        type: 'GET',
	        dataType: 'json',
	        success: function(data){
	        	$scope.$apply(function(){ 
	        		$scope.reactivatedAccount = data;
	        		if($scope.reactivatedAccount.errors != null && $scope.reactivatedAccount.errors.length != 0){	        				        			
	        			console.log($scope.reactivatedAccount.errors);
	        			$scope.closeModal();
	        		} else {	        			
	        			$scope.orcidToReactivate = null;
	        			$scope.showSuccessMessage($scope.reactivateMessage);
	        		}
				});
	        }
	    }).fail(function(error) { 
	    	// something bad is happening!	    	
	    	console.log("Error reactivating the account");	    	
	    });		
	};
	
	$scope.confirmDeactivateAccount = function() {		
		$.ajax({
	        url: orcidVar.baseUri+'/admin-actions/deactivate-profile/check-orcid.json?orcid=' + $scope.orcidToDeactivate,	        
	        type: 'GET',
	        dataType: 'json',
	        success: function(data){
	        	$scope.deactivatedAccount = data;
	        	if($scope.deactivatedAccount.errors != null && $scope.deactivatedAccount.errors.length != 0){
	        		console.log($scope.deactivatedAccount.errors);	        		
	        	} else {
	        		$scope.showConfirmModal();
	        	}
	        	$scope.$apply();
	        }
	        }).fail(function(error) { 
		    	// something bad is happening!	    	
		    	console.log("Error deactivating the account");	    	
		    });
	};
	
	$scope.confirmReactivateAccount = function() {		
		$.colorbox({                      
			html : $compile($('#confirm-reactivation-modal').html())($scope),
				scrolling: true,
				onLoad: function() {
				$('#cboxClose').remove();
			},
			scrolling: true
		});
		
		$.colorbox.resize({width:"450px" , height:"225px"});
	};
	
	$scope.showConfirmModal = function() {
		$.colorbox({                      
			html : $compile($('#confirm-deactivation-modal').html())($scope),
				scrolling: true,
				onLoad: function() {
				$('#cboxClose').remove();
			},
			scrolling: true
		});
		
		$.colorbox.resize({width:"525px" , height:"275px"});
	};		
	
	$scope.showSuccessMessage = function(message){		
		console.log(message);
		$scope.successMessage = message;		
		$.colorbox({                      
			html : $compile($('#success-modal').html())($scope),
				scrolling: true,
				onLoad: function() {
				$('#cboxClose').remove();
			},
			scrolling: true
		});
		
		$.colorbox.resize({width:"425px" , height:"225px"});
	};
	
	$scope.closeModal = function() {
		$.colorbox.close();
	};
};

function profileDeprecationCtrl($scope,$compile){	
	$scope.deprecated_verified = false;
	$scope.primary_verified = false;
	$scope.deprecatedAccount = null;
	$scope.primaryAccount = null;
	$scope.showModal = false;
	
	$scope.toggleDeprecationModal = function(){
		$scope.showModal = !$scope.showModal;
    	$('#deprecation_modal').toggle();
	};
	
	$scope.cleanup = function(orcid_type){
		$("#deprecated_orcid").removeClass("orcid-red-background-input");
		$("#primary_orcid").removeClass("orcid-red-background-input");
		if(orcid_type == 'deprecated'){
			if($scope.deprecated_verified == false)
				$("#deprecated_orcid").addClass("error");
			else 
				$("#deprecated_orcid").removeClass("error");
		} else {					
			if($scope.primary_verified == false)
				$("#primary_orcid").addClass("error");
			else 
				$("#primary_orcid").removeClass("error");
		}
	};
	
	$scope.getAccountDetails = function (orcid, callback){
		$.ajax({
	        url: orcidVar.baseUri+'/admin-actions/deprecate-profile/check-orcid.json?orcid=' + orcid,	        
	        type: 'GET',
	        dataType: 'json',
	        success: function(data){
	        	console.log(data);
	        	callback(data);
	        	$scope.$apply();
	        	}
	        }).fail(function(error) { 
		    	// something bad is happening!	    	
		    	console.log("Error getting account details for: " + orcid);	    	
		    });
	};
				
	$scope.findAccountDetails = function(orcid_type){						
		var orcid;
		var orcidRegex=new RegExp("(\\d{4}-){3,}\\d{3}[\\dX]");
		if(orcid_type == 'deprecated') {
			orcid = $scope.deprecatedAccount.orcid;					
		} else { 
			orcid = $scope.primaryAccount.orcid;			
		}
		//Reset styles
		$scope.cleanup(orcid_type);
		if(orcidRegex.test(orcid)){			
			$scope.getAccountDetails(orcid, function(data){				
				if(orcid_type == 'deprecated') {
					$scope.invalid_regex_deprecated = false;
	    			if(data.errors.length != 0){
	    				$scope.deprecatedAccount.errors = data.errors;
	    				$scope.deprecatedAccount.givenNames = null;
	    				$scope.deprecatedAccount.familyName = null;
	    				$scope.deprecatedAccount.primaryEmail = null;
	    				$scope.deprecated_verified = false;	    				
	    			} else {
	    				$scope.deprecatedAccount.errors = null;
	    				$scope.deprecatedAccount.givenNames = data.givenNames;
	    				$scope.deprecatedAccount.familyName = data.familyName;
	    				$scope.deprecatedAccount.primaryEmail = data.email;
	    				$scope.deprecated_verified = true;
	    				$scope.cleanup(orcid_type);
	    			}
	    		} else {
	    			$scope.invalid_regex_primary = false;
	    			if(data.errors.length != 0){
	    				$scope.primaryAccount.errors = data.errors;
	    				$scope.primaryAccount.givenNames = null;
	    				$scope.primaryAccount.familyName = null;
	    				$scope.primaryAccount.primaryEmail = null;
	    				$scope.primary_verified = false;
	    			} else {
	    				$scope.primaryAccount.errors = null;
	    				$scope.primaryAccount.givenNames = data.givenNames;
	    				$scope.primaryAccount.familyName = data.familyName;
	    				$scope.primaryAccount.primaryEmail = data.email;
	    				$scope.primary_verified = true;
	    				$scope.cleanup(orcid_type);
	    			}
	        	}
			});				
		} else {
			console.log("Orcid: " + orcid + " doesnt match regex");			
			if(orcid_type == 'deprecated') {
				if(!($scope.deprecatedAccount === undefined)){					
					$scope.invalid_regex_deprecated = true;
					$scope.deprecatedAccount.errors = null;
					$scope.deprecatedAccount.givenNames = null;
					$scope.deprecatedAccount.familyName = null;
					$scope.deprecatedAccount.primaryEmail = null;
					$scope.deprecated_verified = false;
				}
			} else {
				if(!($scope.primaryAccount === undefined)){
					$scope.invalid_regex_primary = true;
					$scope.primaryAccount.errors = null;
					$scope.primaryAccount.givenNames = null;
					$scope.primaryAccount.familyName = null;
					$scope.primaryAccount.primaryEmail = null;
					$scope.primary_verified = false;
				}
			}
		}		
	};		
	
	$scope.confirmDeprecateAccount = function(){
		var isOk = true;
		$scope.errors = null;
		if($scope.deprecated_verified === undefined || $scope.deprecated_verified == false){
			$("#deprecated_orcid").addClass("error");
			$("#deprecated_orcid").addClass("orcid-red-background-input");
			isOk = false;
		} 
		
		if($scope.primary_verified === undefined || $scope.primary_verified == false){
			$("#primary_orcid").addClass("error");
			$("#primary_orcid").addClass("orcid-red-background-input");
			isOk = false;
		}
		
		if(isOk){
			$.colorbox({                      
				html : $compile($('#confirm-deprecation-modal').html())($scope),
					scrolling: true,
					onLoad: function() {
					$('#cboxClose').remove();
				},
				scrolling: true
			});
			
			$.colorbox.resize({width:"625px" , height:"400px"});
		}
	};
	
	$scope.deprecateAccount = function(){
		var deprecatedOrcid = $scope.deprecatedAccount.orcid;
		var primaryOrcid = $scope.primaryAccount.orcid;		
		$.ajax({
	        url: orcidVar.baseUri+'/admin-actions/deprecate-profile/deprecate-profile.json?deprecated=' + deprecatedOrcid + '&primary=' + primaryOrcid,	        
	        type: 'GET',
	        dataType: 'json',
	        success: function(data){
	        	$scope.$apply(function(){ 
	        		if(data.errors.length != 0){
	        			$scope.errors = data.errors;
	        		} else {
	        			$scope.showSuccessModal(deprecatedOrcid, primaryOrcid);
	        		}
				});
	        }
	    }).fail(function(error) { 
	    	// something bad is happening!	    	
	    	console.log("Error deprecating the account");	    	
	    });			
	};
	
	$scope.showSuccessModal = function(deprecated, primary){
		console.log(OM.getInstance().get('admin.profile_deprecation.deprecate_account.success_message'));
		$scope.successMessage = OM.getInstance().get('admin.profile_deprecation.deprecate_account.success_message').replace("{{0}}", deprecated).replace("{{1}}", primary);
		
		//Clean fields
		$scope.deprecated_verified = false;
		$scope.primary_verified = false;
		$scope.deprecatedAccount = null;
		$scope.primaryAccount = null;
		
		$.colorbox({                      
			html : $compile($('#success-modal').html())($scope),
				scrolling: true,
				onLoad: function() {
				$('#cboxClose').remove();
			},
			scrolling: true
		});
		
		$.colorbox.resize({width:"450px" , height:"150px"});
	};	
	
	$scope.closeModal = function() {
		$.colorbox.close();
	};
};

function revokeApplicationFormCtrl($scope,$compile){
	
	$scope.confirmRevoke = function(appName, appIndex){
		$scope.appName = appName;
		$scope.appIndex = appIndex;
		$.colorbox({                      
			html : $compile($('#confirm-revoke-access-modal').html())($scope),
			transition: 'fade',
			close: '',
			onLoad: function() {
				$('#cboxClose').remove();
			},
			onComplete: function() {$.colorbox.resize();},
			scrolling: true
		});
	};
	
	$scope.revokeAccess = function(){
		orcidGA.gaPush(['_trackEvent', 'Disengagement', 'Revoke_Access', 'OAuth ' + $scope.appName]);
		orcidGA.gaFormSumbitDelay($('#revokeApplicationForm' + $scope.appIndex));
	};
	
	$scope.closeModal = function() {
		$.colorbox.close();
	};
};

function adminGroupsCtrl($scope,$compile){
	$scope.showAdminGroupsModal = false;
	$scope.newGroup = null;
	$scope.groups = [];
	
	$scope.toggleReactivationModal = function() {
		$scope.showAdminGroupsModal = !$scope.showAdminGroupsModal;
    	$('#admin_groups_modal').toggle();
	};
	
	$scope.showAddGroupModal = function() {
		$.colorbox({                      
			html : $compile($('#add-new-group').html())($scope),				
				onLoad: function() {
				$('#cboxClose').remove();
			}
		});
		
		$.colorbox.resize({width:"450px" , height:"360px"});
	};
	
	$scope.closeModal = function() {
		$.colorbox.close();
	};
	
	$scope.listGroups = function() {
		$.ajax({
	        url: orcidVar.baseUri+'/admin-actions/list-groups.json',	        
	        type: 'GET',
	        dataType: 'json',	        
	        success: function(data){
	        	$scope.$apply(function(){
	        		console.log(data);
	        		$scope.groups = data;
	        		$scope.showGroupList();
				});	        	
	        }
	    }).fail(function(error) { 
	    	// something bad is happening!	    	
	    	console.log("Error getting existing groups");	    	
	    });					
	};
	
	$scope.showGroupList = function() {
		$.colorbox({                      
			html : $compile($('#list-groups').html())($scope),				
				onLoad: function() {
				$('#cboxClose').remove();
			}
		});
		
		$.colorbox.resize({width:"750px" , height:"360px"});
	};
	
	$scope.getGroup = function() { 
		$.ajax({
	        url: orcidVar.baseUri+'/admin-actions/group.json',	        
	        type: 'GET',
	        dataType: 'json',	        
	        success: function(data){
	        	$scope.$apply(function(){ 	
	        		$scope.newGroup = data;
				});
	        }
	    }).fail(function(error) { 
	    	// something bad is happening!	    	
	    	console.log("Error getting emtpy group");	    	
	    });		
	};
	
	$scope.addGroup = function() {
		$.ajax({
	        url: orcidVar.baseUri+'/admin-actions/create-group.json',	        
	        contentType: 'application/json;charset=UTF-8',
	        type: 'POST',
	        dataType: 'json',
	        data: angular.toJson($scope.newGroup),	        	       
	        success: function(data){
	        	console.log(data);
	        	$scope.$apply(function(){ 
	        		$scope.newGroup = data;
	        		if(data.errors.length != 0){
	        			
	        		} else {	        			
	        			$scope.showSuccessModal();
	        		}
				});
	        }
	    }).fail(function(error) { 
	    	// something bad is happening!	    	
	    	console.log("Error deprecating the account");	    	
	    });		
	};
	
	$scope.showSuccessModal = function() {
		$.colorbox({                      
			html : $compile($('#new-group-info').html())($scope),				
				onLoad: function() {
				$('#cboxClose').remove();
			}
		});
		
		$.colorbox.resize({width:"500px" , height:"450px"});
	};
	
	//init 
	$scope.getGroup();
};

