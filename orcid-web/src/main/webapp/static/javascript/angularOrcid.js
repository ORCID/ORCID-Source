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

	function openImportWizardUrl(url) {
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
	    		if( serv.affiliationsToAddIds.length != 0 ) {
	    			var affiliationIds = serv.affiliationsToAddIds.splice(0,20).join();
	    			$.ajax({
	    				url: getBaseUri() + '/' + path + '?affiliationIds=' + affiliationIds,
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
	    						if (serv.affiliationsToAddIds.length == 0) {
	    							serv.loading = false;
	    							$rootScope.$apply();
	    						} else {
	    							$rootScope.$apply();
	    					    	setTimeout(function () {
	    					    		serv.addAffiliationToScope(path);
	    					    	},50);	    							
	    						}
	    				}
	    			}).fail(function() { 
	    		    	console.log("Error fetching affiliation: " + value);
	    		    });
	    		} else {
	    			serv.loading = false;
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
	    			url: getBaseUri() + '/' + path,	        
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
	    	        url: getBaseUri() + '/affiliations/affiliation.json',
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
	    	        url: getBaseUri() + '/affiliations/affiliations.json',
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

orcidNgModule.factory("workspaceSrvc", ['$rootScope', function ($rootScope) {
	var serv = {
			displayAffiliations: true,
			displayEducation: true,
			displayEmployment: true,
			displayFunding: true, 
			displayPersonalInfo: true,
			displayWorks: true,
			toggleAffiliations: function() {
				displayAffiliations = !displayAffiliations;
			},
			toggleEducation: function() {
				serv.displayEducation = !serv.displayEducation;
			},
			toggleEmployment: function() {
				serv.displayEmployment = !serv.displayEmployment;
			},
			toggleFunding: function() {
				serv.displayFunding = !serv.displayFunding;
			},
			togglePersonalInfo: function() {
				serv.displayPersonalInfo = !serv.displayPersonalInfo;
			},
			toggleWorks: function() {
				serv.displayWorks = !serv.displayWorks;
			},
			openAffiliations: function() {
				serv.displayAffiliations = true;
			},
			openEducation: function() {
				serv.displayEducation = true;
			},
			openFunding: function() {
				serv.displayFunding = true;
			},
			openEmployment: function() {
				serv.displayEmployment = true;
			},
			openPersonalInfo: function() {
				serv.displayPersonalInfo = true;
			},
			openWorks: function() {
				serv.displayWorks = true;
			}
	}; 
	return serv;
}]);

/**
 * Fundings Service 
 * */
orcidNgModule.factory("fundingSrvc", ['$rootScope', function ($rootScope) {
	var serv = {
			fundings: new Array(),
			loading: false,
			fundingToAddIds: null,
			addFundingToScope: function(path) {
	    		if( serv.fundingToAddIds.length != 0 ) {
	    			var fundingIds = serv.fundingToAddIds.splice(0,20).join();
	    			$.ajax({
	    				url: getBaseUri() + '/' + path + '?fundingIds=' + fundingIds,
	    				dataType: 'json',
	    				success: function(data) {
	    						for (i in data) {	    							
	    							serv.fundings.push(data[i]);
	    						};
	    						if (serv.fundingToAddIds.length == 0) {
	    							serv.loading = false;
	    							$rootScope.$apply();
	    						} else {
	    							$rootScope.$apply();
	    					    	setTimeout(function () {
	    					    		serv.addFundingToScope(path);
	    					    	},50);	    							
	    						}
	    				}
	    			}).fail(function() { 
	    		    	console.log("Error fetching fundings: " + value);
	    		    });
	    		} else {
	    			serv.loading = false;
	    		};
	    	},
	    	setIdsToAdd: function(ids) {
	    		serv.fundingToAddIds = ids;
	    	},
	    	getFundings: function(path) {
	    		//clear out current fundings
	    		serv.loading = true;
	    		serv.fundingToAddIds = null;
	    		serv.fundings.length = 0;
	    		//get funding ids
	    		$.ajax({
	    			url: getBaseUri() + '/'  + path,	        
	    	        dataType: 'json',
	    	        success: function(data) {
	    	        	serv.fundingToAddIds = data;
	    	        	serv.addFundingToScope('fundings/fundings.json');
	    	        	$rootScope.$apply();
	    	        }
	    		}).fail(function(){
	    			// something bad is happening!
	    	    	console.log("error fetching fundings");
	    		});
	    	},
	    	updateProfileFunding: function(funding) {
	    		$.ajax({
	    	        url: getBaseUri() + '/fundings/funding.json',
	    	        type: 'PUT',
	    	        data: angular.toJson(funding),
	    	        contentType: 'application/json;charset=UTF-8',
	    	        dataType: 'json',
	    	        success: function(data) {	        	
	    	        	if(data.errors.length != 0){
	    	        		console.log("Unable to update profile funding.");
	    	        	}
	    	        	$rootScope.$apply();
	    	        }
	    	    }).fail(function() { 
	    	    	console.log("Error updating profile funding.");
	    	    });
	    	},
	    	deleteFunding: function(funding) {	
	    		$.ajax({
	    	        url: getBaseUri() + '/fundings/funding.json',
	    	        type: 'DELETE',
	    	        data: angular.toJson(funding),
	    	        contentType: 'application/json;charset=UTF-8',
	    	        dataType: 'json',
	    	        success: function(data) {	        	
	    	        	if(data.errors.length != 0){
	    	        		console.log("Unable to delete funding.");
	    	        	} else {
	    	        		var arr = serv.fundings;				
	    					var idx;
	    					for (idx in arr) {
	    						if (arr[idx].putCode.value == funding.putCode.value) {
	    							break;
	    						}
	    					}
	    					arr.splice(idx, 1);
	    	        	}
	    	        	$rootScope.$apply();
	    	        }
	    	    }).fail(function() { 
	    	    	console.log("Error deleting funding.");
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
			        url: getBaseUri() + '/account/preferences.json',
			        dataType: 'json',
			        success: function(data) {
			        	serv.prefs = data;
			        	$rootScope.$apply();
			        }
			    }).fail(function() { 
			    	// something bad is happening!
			    	console.log("error with multi email");
			    });
			},
			savePrivacyPreferences: function() {
				$.ajax({
			        url: getBaseUri() + '/account/preferences.json',
			        type: 'POST',
			        data: angular.toJson(serv.prefs),
			        contentType: 'application/json;charset=UTF-8',
			        dataType: 'json',
			        success: function(data) {
			        	serv.prefs = data;
			        	$rootScope.$apply();
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

function formColorBoxWidth() {
	return isMobile()? '100%': '800px';
}

function formColorBoxResize() {
    if (isMobile())
    	$.colorbox.resize({width: formColorBoxWidth(), height: '100%'});
    else
    	// IE8 and below doesn't take auto height
    	// however the default div height
    	// is auto anyway
    	$.colorbox.resize({width:'800px'});
}

function fixZindexIE7(target, zindex){
	if(isIE() == 7){
		$(target).each(function(){
			$(this).css('z-index', zindex);		
			--zindex;    			    		
		});
	}
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

//We should merge this one with workExternalIdentifierHtml
orcidNgModule.filter('externalIdentifierHtml', function(){
	return function(externalIdentifier, first, last, length){
		var output = '';
		
		if (externalIdentifier == null) return output;
		var type = externalIdentifier.type.value;;		
		if (type != null) output = output + type.toUpperCase() + ": ";
		var value = null;
		if(externalIdentifier.value != null)
			value = externalIdentifier.value.value;
		var link = null;
		if(externalIdentifier.url != null)
			link = externalIdentifier.url.value;
		
		if (link != null && value != null) 
		    output = output + "<a href='" + link + "' target='_blank'>" + value + "</a>";
		else if(value != null)
			output = output + " " + value;
		else if(link != null)
			output = output + "<a href='" + link + "' target='_blank'>" + link + "</a>";
		if (length > 1 && !last) output = output + ',';
	    return output;
	};
});

function addBibtexCitation($scope, dw) {
	if (dw.citation && dw.citation.citationType && dw.citation.citationType.value == 'bibtex') {
		try {
			$scope.bibtexCitations[dw.putCode.value] = bibtexParse.toJSON(dw.citation.citation.value);
		} catch (err) {
			$scope.bibtexCitations[dw.putCode.value] = null;
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
			&& dw.contributors[idx].creditNameVisibility == null) {
				dw.contributors.splice(idx,1);
			}
	}
}

function isEmail(email) {
	var re = /\S+@\S+\.\S+/;
	return re.test(email);
}

function EditTableCtrl($scope) {
	
	// email edit row
	$scope.emailUpdateToggleText = function () {
		if ($scope.showEditEmail) $scope.emailToggleText = om.get("manage.editTable.hide");
		else $scope.emailToggleText = om.get("manage.editTable.edit");		
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
		if ($scope.showEditPassword) $scope.passwordToggleText = om.get("manage.editTable.hide");
		else $scope.passwordToggleText = om.get("manage.editTable.edit");		
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
		if ($scope.showEditDeactivate) $scope.deactivateToggleText = om.get("manage.editTable.hide");
		else $scope.deactivateToggleText = om.get("manage.editTable.deactivateRecord");		
	};

	$scope.toggleDeactivateEdit = function() {
		$scope.showEditDeactivate = !$scope.showEditDeactivate;
		$scope.deactivateUpdateToggleText();
	};
	
	$scope.fixIE7zIndexes = function() {		
		fixZindexIE7('tr', 999999);
		fixZindexIE7('#privacy-settings', 5000);
	};
	
	// init deactivate and Z-Indexes Fix
	$scope.showEditDeactivate = (window.location.hash === "#editDeactivate");
	$scope.deactivateUpdateToggleText();
	$scope.fixIE7zIndexes();
	
	// privacy preferences edit row
	$scope.privacyPreferencesUpdateToggleText = function () {
		if ($scope.showEditPrivacyPreferences) $scope.privacyPreferencesToggleText = om.get("manage.editTable.hide");
		else $scope.privacyPreferencesToggleText = om.get("manage.editTable.edit");		
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
		if ($scope.showEditEmailPreferences) $scope.emailPreferencesToggleText = om.get("manage.editTable.hide");
		else $scope.emailPreferencesToggleText = om.get("manage.editTable.edit");		
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
		if ($scope.showEditSecurityQuestion) $scope.securityQuestionToggleText = om.get("manage.editTable.hide");
		else $scope.securityQuestionToggleText = om.get("manage.editTable.edit");		
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
	$scope.privacyHelp = {};
	
	$scope.toggleClickPrivacyHelp = function(key) {
		if (!document.documentElement.className.contains('no-touch'))
			$scope.privacyHelp[key]=!$scope.privacyHelp[key];
	};
	
	$scope.updateActivitiesVisibilityDefault = function(priv, $event) {
		$scope.prefsSrvc.prefs.activitiesVisibilityDefault.value = priv;
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
	        url: getBaseUri() + '/account/send-deactivate-account.json',
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
	        url: getBaseUri() + '/account/security-question.json',
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
	        url: getBaseUri() + '/account/security-question.json',
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
	        url: getBaseUri() + '/account/change-password.json',
	        dataType: 'json',
	        success: function(data) {
	        	$scope.changePasswordPojo = data;
	        	$scope.$apply();
	        	$scope.zIndexfixIE7();
	        }
	    }).fail(function() { 
	    	// something bad is happening!
	    	console.log("error with change password");
	    });
	};
	
	$scope.getChangePassword();
	
	$scope.zIndexfixIE7 = function(){		
		fixZindexIE7('#password-edit', 999999);
		fixZindexIE7('#password-edit .relative', 99999);		
	};
	
	$scope.saveChangePassword = function() {
		$.ajax({
	        url: getBaseUri() + '/account/change-password.json',
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
	$scope.privacyHelp = {};
	
	$scope.toggleClickPrivacyHelp = function(key) {
		if (!document.documentElement.className.contains('no-touch'))
			$scope.privacyHelp[key]=!$scope.privacyHelp[key];
	};
	
	$scope.getEmails = function() {
		$.ajax({
	        url: getBaseUri() + '/account/emails.json',
	        //type: 'POST',
	        //data: $scope.emailsPojo, 
	        dataType: 'json',
	        success: function(data) {
	        	$scope.emailsPojo = data;
	        	$scope.$apply();
	        	if(isIE() == 7) $scope.fixZindexesIE7();
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
	
	$scope.fixZindexesIE7 =  function(){		
	    fixZindexIE7('.popover',2000);
	    fixZindexIE7('.popover-help-container',3000);
	    fixZindexIE7('#privacy-bar',500);
	    fixZindexIE7('.emailVisibility',5000);
	    fixZindexIE7('.col-md-3', 6000);
	    fixZindexIE7('.row', 7000);	
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
	
	$scope.setPrivacy = function(email, priv, $event) {
		$event.preventDefault();
		email.visibility = priv;
		$scope.curPrivToggle = null;
		$scope.saveEmail();
	};
	
	$scope.verifyEmail = function(idx) {
		$scope.verifyEmailIdx = idx;
		$.ajax({
	        url: getBaseUri() + '/account/verifyEmail.json',
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
	        url: getBaseUri() + '/account/emails.json',
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
	        url: getBaseUri() + '/account/addEmail.json',
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
	        url: getBaseUri() + '/account/deleteEmail.json',
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

function WebsitesCtrl($scope, $compile) {
    $scope.showEdit = false;
    $scope.websitesForm = null;
    $scope.privacyHelp = false;
    
	$scope.toggleEdit = function() {
		$scope.showEdit = !$scope.showEdit;
	};

	$scope.close = function() {
		$scope.getWebsitesForm();
		$scope.showEdit = false;
	};
	
	$scope.addNew = function() {
		$scope.websitesForm.websites.push({ name: {value: ""}, url: {value: ""} });
	};
    
    $scope.getWebsitesForm = function(){
		$.ajax({
			url: getBaseUri() + '/my-orcid/websitesForms.json',	        
	        dataType: 'json',
	        success: function(data) {
	        	$scope.websitesForm = data;
	        	var websites = $scope.websitesForm.websites;
	        	var len = websites.length;
	        	while (len--) {
	        		if (!websites[len].url.value.toLowerCase().startsWith('http'))
	        			websites[len].url.value = 'http://' + websites[len].url.value;
	        	}
	        	$scope.$apply();
	        }
		}).fail(function(){
			// something bad is happening!
	    	console.log("error fetching websites");
		});
	};

	$scope.deleteWebsite = function(website){
		var websites = $scope.websitesForm.websites;
		var websites = $scope.websitesForm.websites;
    	var len = websites.length;
    	while (len--) {
    		if (websites[len] == website)
    			websites.splice(len,1);
    	}
	}
	
	$scope.setWebsitesForm = function(){
		var websites = $scope.websitesForm.websites;
    	var len = websites.length;
    	while (len--) {
    		if (websites[len].url.value == null || websites[len].url.value.trim() == '')
    			websites.splice(len,1);
    	}
		$.ajax({
	        url: getBaseUri() + '/my-orcid/websitesForms.json',
	        type: 'POST',
	        data:  angular.toJson($scope.websitesForm),
	        contentType: 'application/json;charset=UTF-8',
	        dataType: 'json',
	        success: function(data) {
	        	$scope.websitesForm = data;
	        	if(data.errors.length == 0)
	        	   $scope.close();
	        	$scope.$apply();
	        }
	    }).fail(function() { 
	    	// something bad is happening!
	    	console.log("WebsiteCtrl.serverValidate() error");
	    });
	};
	
	$scope.setPrivacy = function(priv, $event) {
		$event.preventDefault();
		$scope.websitesForm.visibility.visibility = priv;
	};

	$scope.getWebsitesForm();
};

function BiographyCtrl($scope, $compile) {
    $scope.showEdit = false;
	$scope.biographyForm = null;

	$scope.toggleEdit = function() {
		$scope.showEdit = !$scope.showEdit;
	};

	$scope.close = function() {
		$scope.showEdit = false;
	};

	$scope.cancel = function() {
		$scope.getBiographyForm();
		$scope.showEdit = false;
	};

	
	$scope.getBiographyForm = function(){
		$.ajax({
			url: getBaseUri() + '/account/biographyForm.json',	        
	        dataType: 'json',
	        success: function(data) {
	        	$scope.biographyForm = data;
	        	$scope.$apply();
	        }
		}).fail(function(){
			// something bad is happening!
	    	console.log("error fetching BiographyForm");
		});
	};
	
	$scope.setBiographyForm = function(){
		
		$.ajax({
	        url: getBaseUri() + '/account/biographyForm.json',
	        type: 'POST',
	        data:  angular.toJson($scope.biographyForm),
	        contentType: 'application/json;charset=UTF-8',
	        dataType: 'json',
	        success: function(data) {
	        	$scope.biographyForm = data;
	        	if(data.errors.length == 0)
	        	    $scope.close();
	        	$scope.$apply();
	        }
	    }).fail(function() { 
	    	// something bad is happening!
	    	console.log("BiographyCtrl.serverValidate() error");
	    });
	};

		
	$scope.getBiographyForm();

};


function CountryCtrl($scope, $compile) {
    $scope.showEdit = false;
	$scope.countryForm = null;
	$scope.privacyHelp = false;
	
	$scope.toggleEdit = function() {
		$scope.showEdit = !$scope.showEdit;
	};

	$scope.close = function() {
		$scope.showEdit = false;
	};

	
	$scope.getCountryForm = function(){
		$.ajax({
			url: getBaseUri() + '/account/countryForm.json',	        
	        dataType: 'json',
	        success: function(data) {
	        	$scope.countryForm = data;
	        	$scope.$apply();
	        }
		}).fail(function(){
			// something bad is happening!
	    	console.log("error fetching external identifiers");
		});
	};
	
	$scope.toggleClickPrivacyHelp = function() {
		if (!document.documentElement.className.contains('no-touch'))
			$scope.privacyHelp=!$scope.privacyHelp;
	};
	
	$scope.setCountryForm = function(){
		
		if ($scope.countryForm.iso2Country.value == '')
		   $scope.countryForm.iso2Country = null;
		$.ajax({
	        url: getBaseUri() + '/account/countryForm.json',
	        type: 'POST',
	        data:  angular.toJson($scope.countryForm),
	        contentType: 'application/json;charset=UTF-8',
	        dataType: 'json',
	        success: function(data) {
	        	$scope.countryForm = data;
	        	$scope.close();
	        	$scope.$apply();
	        }
	    }).fail(function() { 
	    	// something bad is happening!
	    	console.log("CountryCtrl.serverValidate() error");
	    });
	};

	
	$scope.setPrivacy = function(priv, $event) {
		$event.preventDefault();
		$scope.countryForm.profileAddressVisibility.visibility = priv;
		$scope.setCountryForm();
	};
	
	$scope.getCountryForm();

};


function ExternalIdentifierCtrl($scope, $compile){		
	$scope.getExternalIdentifiers = function(){
		$.ajax({
			url: getBaseUri() + '/my-orcid/externalIdentifiers.json',	        
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
		$.ajax({
	        url: getBaseUri() + '/my-orcid/externalIdentifiers.json',
	        type: 'DELETE',
	        data: angular.toJson(externalIdentifier),
	        contentType: 'application/json;charset=UTF-8',
	        dataType: 'json',
	        success: function(data) {	        	
	        	if(data.errors.length != 0){
	        		console.log("Unable to delete external identifier.");
	        	} else {
	    	    	$scope.externalIdentifiersPojo.externalIdentifiers.splice($scope.removeExternalIdentifierIndex, 1);
	    		    $scope.removeExternalIdentifierIndex = null;
	    		    $scope.$apply();
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
			url: getBaseUri() + '/password-reset.json',	        
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
	        url: getBaseUri() + '/reset-password-form-validate.json',
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
	$scope.privacyHelp = {};
	
	$scope.toggleClickPrivacyHelp = function(key) {
		if (!document.documentElement.className.contains('no-touch'))
			$scope.privacyHelp[key]=!$scope.privacyHelp[key];
	};
		
	$scope.getRegister = function(){
		$.ajax({
			url: getBaseUri() + '/register.json',	        
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
			//url: getBaseUri() + 'dupicateResearcher.json?familyNames=test&givenNames=test',	        
			url: getBaseUri() + '/dupicateResearcher.json?familyNames=' + $scope.register.familyNames.value + '&givenNames=' + $scope.register.givenNames.value,	        
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

	
	$scope.updateActivitiesVisibilityDefault = function(priv, $event) {
		$scope.register.activitiesVisibilityDefault.visibility = priv;
	};
	
	$scope.postRegister = function () {
		if (basePath.startsWith(baseUrl + 'oauth')) { 
			var clientName = $('div#RegistrationCtr input[name="client_name"]').val();
			$scope.register.referredBy = $('div#RegistrationCtr input[name="client_id"]').val();
			var clientGroupName = $('div#RegistrationCtr input[name="client_group_name"]').val();
		    orcidGA.gaPush(['_trackEvent', 'RegGrowth', 'New-Registration-Submit' , 'OAuth ' + orcidGA.buildClientString(clientGroupName, clientName)]);
		    $scope.register.creationType.value = "Member-referred";
		} else {
	    	orcidGA.gaPush(['_trackEvent', 'RegGrowth', 'New-Registration-Submit', 'Website']);
	    	$scope.register.creationType.value = "Direct";
		}
	    $.ajax({
	        url: getBaseUri() + '/register.json',
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
	        url: getBaseUri() + '/registerConfirm.json',
	        type: 'POST',
	        data:  angular.toJson($scope.register),
	        contentType: 'application/json;charset=UTF-8',
	        dataType: 'json',
	        success: function(data) {
	    		if (basePath.startsWith(baseUrl + 'oauth')) {
	    			var clientName = $('div#RegistrationCtr input[name="client_name"]').val();
	    			var clientGroupName = $('div#RegistrationCtr input[name="client_group_name"]').val();
	    		    orcidGA.gaPush(['_trackEvent', 'RegGrowth', 'New-Registration', 'OAuth '+ orcidGA.buildClientString(clientGroupName, clientName)]);
	    		}
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
	        url: getBaseUri() + '/register' + field + 'Validate.json',
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
	
	$scope.updateActivitiesVisibilityDefault = function(priv, $event) {
		$scope.register.activitiesVisibilityDefault.visibility = priv;
	};

	$scope.serverValidate = function (field) {
		if (field === undefined) field = '';
		$.ajax({
	        url: getBaseUri() + '/claim' + field + 'Validate.json',
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
	        url: getBaseUri() + '/account/emails.json',
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
	        url: getBaseUri() + '/account/verifyEmail.json',
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
	        url: getBaseUri() + '/account/delayVerifyEmail.json',
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
			url: getBaseUri() + '/my-orcid/sourceGrantReadWizard.json',	        
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

function PersonalInfoCtrl($scope, $compile, workspaceSrvc){
	$scope.displayInfo = workspaceSrvc.displayPersonalInfo;
	$scope.toggleDisplayInfo = function () {
		$scope.displayInfo = !$scope.displayInfo;
	};
};

function WorkspaceSummaryCtrl($scope, $compile, affiliationsSrvc, fundingSrvc, worksSrvc, workspaceSrvc){
	$scope.workspaceSrvc = workspaceSrvc;
	$scope.worksSrvc = worksSrvc;
	$scope.affiliationsSrvc = affiliationsSrvc;
	$scope.fundingSrvc = fundingSrvc;
	$scope.showAddAlert = function () {
		if (worksSrvc.loading == false && affiliationsSrvc.loading == false
				&& worksSrvc.works.length == 0 
				&& affiliationsSrvc.educations.length == 0
				&& affiliationsSrvc.employments.length == 0
				&& fundingSrvc.fundings.length == 0)
			return true;
		return false;
	};	
}

function PublicEduAffiliation($scope, $compile, $filter, affiliationsSrvc){
	$scope.affiliationsSrvc = affiliationsSrvc;
	$scope.moreInfo = {};
	
	$scope.toggleClickMoreInfo = function(key) {
		if (!document.documentElement.className.contains('no-touch'))
			$scope.moreInfo[key]=!$scope.moreInfo[key];
	};
	
	$scope.moreInfoMouseEnter = function(key, $event) {
		$event.stopPropagation();
		if (document.documentElement.className.contains('no-touch'))
			$scope.moreInfo[key]=true;
	};

	$scope.closeMoreInfo = function(key) {
		$scope.moreInfo[key]=false;
	};

}

function PublicEmpAffiliation($scope, $compile, $filter, affiliationsSrvc){
	$scope.affiliationsSrvc = affiliationsSrvc;
	$scope.moreInfo = {};
	
	$scope.toggleClickMoreInfo = function(key) {
		if (!document.documentElement.className.contains('no-touch'))
			$scope.moreInfo[key]=!$scope.moreInfo[key];
	};
	
	$scope.moreInfoMouseEnter = function(key, $event) {
		$event.stopPropagation();
		if (document.documentElement.className.contains('no-touch'))
			$scope.moreInfo[key]=true;
	};

	$scope.closeMoreInfo = function(key) {
		$scope.moreInfo[key]=false;
	};

	affiliationsSrvc.setIdsToAdd(orcidVar.affiliationIdsJson);
	affiliationsSrvc.addAffiliationToScope(orcidVar.orcidId +'/affiliations.json');
}


function AffiliationCtrl($scope, $compile, $filter, affiliationsSrvc, workspaceSrvc){
	$scope.affiliationsSrvc = affiliationsSrvc;
	$scope.workspaceSrvc = workspaceSrvc;
	$scope.editAffiliation;
	$scope.privacyHelp = {};
	$scope.privacyHelpCurKey = null;
	$scope.moreInfo = {};
	$scope.moreInfoCurKey = null;	
	
	$scope.toggleClickPrivacyHelp = function(key) {
		if (!document.documentElement.className.contains('no-touch')) {
			if ($scope.privacyHelpCurKey != null 
					&& $scope.privacyHelpCurKey != key) {
				$scope.privacyHelp[$scope.privacyHelpCurKey]=false;
			}
			$scope.privacyHelpCurKey = key;
			$scope.privacyHelp[key]=!$scope.privacyHelp[key];
		}
			
	};

	$scope.toggleClickMoreInfo = function(key) {
		if (!document.documentElement.className.contains('no-touch')) {
			if ($scope.moreInfoCurKey != null 
					&& $scope.moreInfoCurKey != key) {
				$scope.moreInfo[$scope.moreInfoCurKey]=false;
			}
			$scope.moreInfoCurKey = key;
			$scope.moreInfo[key]=!$scope.moreInfo[key];
		}
	};
	
	$scope.moreInfoMouseEnter = function(key, $event) {
		$event.stopPropagation();
		if (document.documentElement.className.contains('no-touch')) {
			if ($scope.moreInfoCurKey != null 
					&& $scope.moreInfoCurKey != key) {
				$scope.privacyHelp[$scope.moreInfoCurKey]=false;
			}
			$scope.moreInfoCurKey = key;
			$scope.moreInfo[key]=true;
		}
	};

	$scope.closeMoreInfo = function(key) {
		$scope.moreInfo[key]=false;
	};

	$scope.showAddModal = function(){
		var numOfResults = 25;
		$.colorbox({        	
			html: $compile($('#add-affiliation-modal').html())($scope),
			// start the colorbox off with the correct width
			width: formColorBoxResize(),
			onComplete: function() {
				//resize to insure content fits
				formColorBoxResize();
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
				url: getBaseUri()+'/affiliations/disambiguated/name/%QUERY?limit=' + numOfResults
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
		if (datum != undefined && datum != null) {
			$scope.editAffiliation.affiliationName.value = datum.value;
			$scope.editAffiliation.city.value = datum.city;
			if(datum.city)
				$scope.editAffiliation.city.errors = [];
			$scope.editAffiliation.region.value = datum.region;
			if(datum.region)
				$scope.editAffiliation.region.errors = [];	
			if(datum.country != undefined && datum.country != null) {
				$scope.editAffiliation.country.value = datum.country;
				$scope.editAffiliation.country.errors = [];
			}
				
			if (datum.disambiguatedAffiliationIdentifier != undefined && datum.disambiguatedAffiliationIdentifier != null) {
				$scope.getDisambiguatedAffiliation(datum.disambiguatedAffiliationIdentifier);
				$scope.unbindTypeahead();
			}
		}
	};
	
	$scope.getDisambiguatedAffiliation = function(id) {
		$.ajax({
			url: getBaseUri() + '/affiliations/disambiguated/id/' + id,
	        dataType: 'json',
	        type: 'GET',
	        success: function(data) {
	        	if (data != null) {
	        		console.log(data.sourceId);
			        $scope.disambiguatedAffiliation = data;
			        $scope.editAffiliation.disambiguatedAffiliationSourceId = data.sourceId;
			        $scope.editAffiliation.disambiguationSource = data.sourceType;
			        $scope.$apply();
	        	}
	        }
		}).fail(function(){
	    	console.log("error getDisambiguatedAffiliation(id)");
		});
	};
	
	$scope.removeDisambiguatedAffiliation = function() {
		$scope.bindTypeahead();
		if ($scope.disambiguatedAffiliation != undefined) delete $scope.disambiguatedAffiliation;
		if ($scope.editAffiliation != undefined && $scope.editAffiliation.disambiguatedAffiliationSourceId != undefined) delete $scope.editAffiliation.disambiguatedAffiliationSourceId;
	};

	$scope.addAffiliationModal = function(type){
		$scope.removeDisambiguatedAffiliation();
		$scope.addAffType = type;
		$.ajax({
			url: getBaseUri() + '/affiliations/affiliation.json',
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
			url: getBaseUri() + '/affiliations/affiliation.json',
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
	
	//For resizing color box in case of error
	$scope.$watch('addingAffiliation', function() {
		 setTimeout(function(){
			 $.colorbox.resize();; 
	     }, 50);		
	});
	
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
	        url: getBaseUri() + '/' + relativePath,
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

/**
 * Fundings Controller 
 * */
function FundingCtrl($scope, $compile, $filter, fundingSrvc, workspaceSrvc) {	
	$scope.workspaceSrvc = workspaceSrvc;
	$scope.fundingSrvc = fundingSrvc;
	$scope.addingFunding = false;
	$scope.editFunding = null;
	$scope.disambiguatedFunding = null;
	$scope.moreInfo = {};
	$scope.privacyHelp = {};
	$scope.editTranslatedTitle = false; 	
	
	$scope.toggleClickMoreInfo = function(key) {
		if (!document.documentElement.className.contains('no-touch')) {
			if ($scope.moreInfoCurKey != null 
					&& $scope.moreInfoCurKey != key) {
				$scope.moreInfo[$scope.moreInfoCurKey]=false;
			}
			$scope.moreInfoCurKey = key;
			$scope.moreInfo[key]=!$scope.moreInfo[key];
		}
	};
	
	$scope.moreInfoMouseEnter = function(key, $event) {
		$event.stopPropagation();
		if (document.documentElement.className.contains('no-touch')) {
			if ($scope.moreInfoCurKey != null 
					&& $scope.moreInfoCurKey != key) {
				$scope.privacyHelp[$scope.moreInfoCurKey]=false;
			}
			$scope.moreInfoCurKey = key;
			$scope.moreInfo[key]=true;
		}
	};
	
	$scope.closeMoreInfo = function(key) {
		$scope.moreInfo[key]=false;
	};
	
	$scope.addFundingModal = function(type){
		$scope.removeDisambiguatedFunding();
		$.ajax({
			url: getBaseUri() + '/fundings/funding.json',
			dataType: 'json',
			success: function(data) {						
				$scope.$apply(function() {
					$scope.editFunding = data;
					$scope.showAddModal();
				});
			}
		}).fail(function() { 
	    	console.log("Error fetching funding: " + value);
	    });
	};
	
	$scope.showAddModal = function(){
		$scope.editTranslatedTitle = false;		
		$.colorbox({        	
			html: $compile($('#add-funding-modal').html())($scope),			
			width: formColorBoxResize(),
			onComplete: function() {
				//resize to insure content fits
				formColorBoxResize();
				$scope.bindTypeahead();
			}
	    });
	};
	
	$scope.addFunding = function(){
		if ($scope.addingFunding) return; // don't process if adding funding
		$scope.addingFunding = true;		
		$scope.editFunding.errors.length = 0;
		$.ajax({
			url: getBaseUri() + '/fundings/funding.json',
	        contentType: 'application/json;charset=UTF-8',
	        dataType: 'json',
	        type: 'POST',
	        data:  angular.toJson($scope.editFunding),
	        success: function(data) {	        		        	
	        	if (data.errors.length == 0){
	        		$.colorbox.close(); 	        		
	        		fundingSrvc.getFundings('fundings/fundingIds.json');
	        	} else {
		        	$scope.editFunding = data;
		        	if($scope.editFunding.externalIdentifiers.length == 0) {
		        		$scope.addExternalIdentifier();
		        	}
		        	$scope.copyErrorsLeft($scope.editFunding, data);		        	
	        	}
	        	$scope.addingFunding = false;
	        	$scope.$apply();	        	
	        }
		}).fail(function(){
			// something bad is happening!
			$scope.addingFunding = false;						
	    	console.log("error adding fundings");
		});
	};
	
	//Resizing window after error message is shown
	$scope.$watch('addingFunding', function() {
		 setTimeout(function(){
			 $.colorbox.resize();; 
         }, 50);		
	 });
	
	$scope.showTemplateInModal = function(templateId) {
		$.colorbox({        	            
            html : $compile($('#'+templateId).html())($scope),
            onComplete: function() {$.colorbox.resize();}
        });
	};
	
	$scope.openImportWizardUrl = function(url) {
		openImportWizardUrl(url);
	};


	$scope.bindTypeahead = function () {
		var numOfResults = 100;
		$("#fundingName").typeahead({
			name: 'fundingName',
			limit: numOfResults,
			remote: {
				replace: function () {
                    var q = getBaseUri()+'/fundings/disambiguated/name/';
                    if ($('#fundingName').val()) {
                        q += encodeURIComponent($('#fundingName').val());
                    }
                    q += '?limit=' + numOfResults + '&funders-only=true';
                    return q;
                }
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
		$("#fundingName").bind("typeahead:selected", function(obj, datum) {        
			$scope.selectFunding(datum);
			$scope.$apply();
		});		
	};
	
	$scope.selectFunding = function(datum) {
		console.log(angular.toJson(datum));
		if (datum != undefined && datum != null) {
			$scope.editFunding.fundingName.value = datum.value;
			if(datum.value)
				$scope.editFunding.fundingName.errors = [];
			$scope.editFunding.city.value = datum.city;
			if(datum.city)
				$scope.editFunding.city.errors = [];			
			$scope.editFunding.region.value = datum.region;
			
			if(datum.country != undefined && datum.country != null) {
				$scope.editFunding.country.value = datum.country;
				$scope.editFunding.country.errors = [];
			}
			
			if (datum.disambiguatedFundingIdentifier != undefined && datum.disambiguatedFundingIdentifier != null) {
				$scope.getDisambiguatedFunding(datum.disambiguatedFundingIdentifier);
				$scope.unbindTypeahead();
			}
		}
	};
	
	$scope.getDisambiguatedFunding = function(id) {
		$.ajax({
			url: getBaseUri() + '/fundings/disambiguated/id/' + id,
	        dataType: 'json',
	        type: 'GET',
	        success: function(data) {
	        	if (data != null) {
	        		console.log(data.sourceId);
			        $scope.disambiguatedFunding = data;
			        $scope.editFunding.disambiguatedFundingSourceId = data.sourceId;
			        $scope.editFunding.disambiguationSource = data.sourceType;
			        $scope.$apply();
	        	}
	        }
		}).fail(function(){
	    	console.log("error getDisambiguatedFunding(id)");
		});
	};
	
	$scope.deleteFunding = function(funding) {
		$scope.delFunding = funding;
				
		$.colorbox({        	            
            html : $compile($('#delete-funding-modal').html())($scope),
            onComplete: function() {$.colorbox.resize();}
        });
	};
	
	$scope.confirmDeleteFunding = function(delFunding) {	
		fundingSrvc.deleteFunding(delFunding);
		$.colorbox.close(); 
	};
	
	//init
	fundingSrvc.getFundings('fundings/fundingIds.json');
	
	$scope.closeModal = function() {
		$.colorbox.close();
	};
	
	// Add privacy for new fundings
	$scope.setAddFundingPrivacy = function(priv, $event) {
		$event.preventDefault();
		$scope.editFunding.visibility.visibility = priv;
	};
	
	// Update privacy of an existing funding
	$scope.setPrivacy = function(funding, priv, $event) {
		$event.preventDefault();
		funding.visibility.visibility = priv;
		fundingSrvc.updateProfileFunding(funding);
	};
	
	$scope.removeDisambiguatedFunding = function() {
		$scope.bindTypeahead();
		if ($scope.disambiguatedFunding != undefined) delete $scope.disambiguatedFunding;
		if ($scope.editFunding != undefined && $scope.editFunding.disambiguatedFundingSourceId != undefined) delete $scope.editFunding.disambiguatedFundingSourceId;
	};
	
	$scope.isValidClass = function (cur) {
		if (cur === undefined) return '';
		var valid = true;
		if (cur.required && (cur.value == null || cur.value.trim() == '')) valid = false;
		if (cur.errors !== undefined && cur.errors.length > 0) valid = false;
		return valid ? '' : 'text-error';
	};			
	
	// Server validations
	$scope.serverValidate = function (relativePath) {		
		$.ajax({
	        url: getBaseUri() + '/' + relativePath,
	        type: 'POST',
	        data:  angular.toJson($scope.editFunding),
	        contentType: 'application/json;charset=UTF-8',
	        dataType: 'json',
	        success: function(data) {
	        	$scope.copyErrorsLeft($scope.editFunding, data);
	        	$scope.$apply();
	        }
	    }).fail(function() { 
	    	// something bad is happening!
	    	console.log("FundingCtrl.serverValidate() error");
	    });
	};
	
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
	
	$scope.unbindTypeahead = function () {
		$('#fundingName').typeahead('destroy');
	};
	
	$scope.addExternalIdentifier = function () {
		$scope.editFunding.externalIdentifiers.push({type: {value: ""}, value: {value: ""}, url: {value: ""} });
	};
	
	$scope.toggleTranslatedTitleModal = function(){
		$scope.editTranslatedTitle = !$scope.editTranslatedTitle;
    	$('#translatedTitle').toggle();
    	$.colorbox.resize();
	};
	$scope.renderTranslatedTitleInfo = function(funding) {		
		var info = null; 
		if(funding != null && funding.fundingTitle != null && funding.fundingTitle.translatedTitle != null) {
			info = funding.fundingTitle.translatedTitle.content + ' - ' + funding.fundingTitle.translatedTitle.languageName;										
		}				
		return info;
	};
	
	$scope.typeChanged = function() {
		var selectedType = $scope.editFunding.fundingType.value;
		switch (selectedType){
		case 'award':
			$("#funding-ext-ids-title").text(om.get("funding.add.external_id.title.award"));
			$("#funding-ext-ids-value-label").text(om.get("funding.add.external_id.value.label.award"));
			$("#funding-ext-ids-value-input").attr("placeholder", om.get("funding.add.external_id.value.placeholder.award"));
			$("#funding-ext-ids-url-label").text(om.get("funding.add.external_id.url.label.award"));
			$("#funding-ext-ids-url-input").attr("placeholder", om.get("funding.add.external_id.url.placeholder.award"));
			break;
		case 'contract':
			$("#funding-ext-ids-title").text(om.get("funding.add.external_id.title.contract"));
			$("#funding-ext-ids-value-label").text(om.get("funding.add.external_id.value.label.contract"));
			$("#funding-ext-ids-value-input").attr("placeholder", om.get("funding.add.external_id.value.placeholder.contract"));
			$("#funding-ext-ids-url-label").text(om.get("funding.add.external_id.url.label.contract"));
			$("#funding-ext-ids-url-input").attr("placeholder", om.get("funding.add.external_id.url.placeholder.contract"));
			break;
		case 'grant':
			$("#funding-ext-ids-title").text(om.get("funding.add.external_id.title.grant"));
			$("#funding-ext-ids-value-label").text(om.get("funding.add.external_id.value.label.grant"));
			$("#funding-ext-ids-value-input").attr("placeholder", om.get("funding.add.external_id.value.placeholder.grant"));
			$("#funding-ext-ids-url-label").text(om.get("funding.add.external_id.url.label.grant"));
			$("#funding-ext-ids-url-input").attr("placeholder", om.get("funding.add.external_id.url.placeholder.grant"));
			break;
		case 'salary-award':
			$("#funding-ext-ids-value-label").text(om.get("funding.add.external_id.value.label.award"));
			$("#funding-ext-ids-value-input").attr("placeholder", om.get("funding.add.external_id.value.placeholder.award"));
			$("#funding-ext-ids-url-label").text(om.get("funding.add.external_id.url.label.award"));
			$("#funding-ext-ids-url-input").attr("placeholder", om.get("funding.add.external_id.url.placeholder.award"));
			$("#funding-ext-ids-title").text(om.get("funding.add.external_id.title.award"));
			break;
		default:
			$("#funding-ext-ids-title").text(om.get("funding.add.external_id.title.grant"));
			$("#funding-ext-ids-value-label").text(om.get("funding.add.external_id.value.label.grant"));
			$("#funding-ext-ids-value-input").attr("placeholder", om.get("funding.add.external_id.value.placeholder.grant"));
			$("#funding-ext-ids-url-label").text(om.get("funding.add.external_id.url.label.grant"));
			$("#funding-ext-ids-url-input").attr("placeholder", om.get("funding.add.external_id.url.placeholder.grant"));
			break;
		}
	};
}

/**
 * Public Funding Controller 
 * */
function PublicFundingCtrl($scope, $compile, $filter, fundingSrvc){
	$scope.fundingSrvc = fundingSrvc;
	$scope.moreInfo = {};
	
	$scope.toggleClickMoreInfo = function(key) {
		if (!document.documentElement.className.contains('no-touch'))
			$scope.moreInfo[key]=!$scope.moreInfo[key];
	};
	
	$scope.moreInfoMouseEnter = function(key, $event) {
		$event.stopPropagation();
		if (document.documentElement.className.contains('no-touch'))
			$scope.moreInfo[key]=true;
	};

	$scope.closeMoreInfo = function(key) {
		$scope.moreInfo[key]=false;
	};

	fundingSrvc.setIdsToAdd(orcidVar.fundingIdsJson);
	fundingSrvc.addFundingToScope(orcidVar.orcidId +'/fundings.json');
	
	$scope.renderTranslatedTitleInfo = function(funding) {		
		var info = null; 
		if(funding != null && funding.fundingTitle != null && funding.fundingTitle.translatedTitle != null) {
			info = funding.fundingTitle.translatedTitle.content + ' - ' + funding.fundingTitle.translatedTitle.languageName;										
		}				
		return info;
	};
}

function PublicWorkCtrl($scope, $compile, worksSrvc) {
	$scope.works = worksSrvc.works;
	$scope.worksSrvc = worksSrvc;
	$scope.showBibtex = true;
	$scope.loadingInfo = false;
	$scope.bibtexCitations = {};
	$scope.worksInfo = {};
	$scope.moreInfoOpen = false;

    $scope.bibtexShowToggle = function () {
    	$scope.showBibtex = !($scope.showBibtex);
    };   

    $scope.addWorkToScope = function() {
		if($scope.worksToAddIds.length != 0 ) {
			$scope.worksSrvc.loading = true;
			var workIds = $scope.worksToAddIds.splice(0,20).join();
			$.ajax({
				url: getBaseUri() + '/' + orcidVar.orcidId +'/works.json?workIds=' + workIds,
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
					if($scope.worksToAddIds.length == 0 ) {
						$scope.worksSrvc.loading = false;
						$scope.$apply();					
						fixZindexIE7('.workspace-public workspace-body-list li',99999);
						fixZindexIE7('.workspace-toolbar',9999);						
					} else {
						$scope.$apply();					
						setTimeout(function(){
							$scope.addWorkToScope();
						},50);
					}
				}
			}).fail(function() { 
				$scope.$apply(function() {
					$scope.worksSrvc.loading = false;
				});
		    	console.log("Error fetching works: " + workIds);
		    });
		} else {
			$scope.worksSrvc.loading = false;
		}
	};     
	  
	$scope.renderTranslatedTitleInfo = function(putCode) {		
		var info = null; 
		
		if(putCode != null && $scope.worksInfo[putCode] != null && $scope.worksInfo[putCode].workTitle != null && $scope.worksInfo[putCode].workTitle.translatedTitle != null) {
			info = $scope.worksInfo[putCode].workTitle.translatedTitle.content + ' - ' + $scope.worksInfo[putCode].workTitle.translatedTitle.languageName;										
		}		
		
		return info;
	};
		
	$scope.worksToAddIds = orcidVar.workIds;	
	$scope.addWorkToScope();
	

	$scope.moreInfoClick = function(work, $event) {
		if (!document.documentElement.className.contains('no-touch'))
			$scope.moreInfoOpen?$scope.closePopover():$scope.loadWorkInfo(work.putCode.value, $event);
	};
	
	$scope.moreInfoMouseEnter = function(work, $event) {
		$event.stopPropagation();
		if (document.documentElement.className.contains('no-touch'))
			$scope.loadWorkInfo(work.putCode.value, $event);
		else
			$scope.moreInfoOpen?$scope.closePopover():$scope.loadWorkInfo(work.putCode.value, $event);
	};
	
	$scope.loadWorkInfo = function(putCode, event) {
		//Close any open popover
		$scope.closePopover(event);
		$scope.moreInfoOpen = true;
		//Display the popover
		$scope.loadingInfo = true;
		$(event.target).next().css('display','inline');		
		if($scope.worksInfo[putCode] == null) {		
			$.ajax({
				url: getBaseUri() + '/' + orcidVar.orcidId + '/getWorkInfo.json?workId=' + putCode,	        
		        dataType: 'json',
		        success: function(data) {		        	
		        	$scope.$apply(function () {
		        		removeBadContributors(data);
						addBibtexCitation($scope,data);
						$scope.worksInfo[putCode] = data;
						$scope.loadingInfo = false;
		        	});		        	
		        }
			}).fail(function(){
				// something bad is happening!
		    	console.log("error fetching works");
		    	$(event.target).next().css('display','none');	
		    	$scope.loadingInfo = false;
			});
		} else {
			$(event.target).next().css('display','inline');
			$scope.loadingInfo = false;
		}
	};			
	
	$scope.closePopover = function(event) {
		$scope.moreInfoOpen = false;
		$('.work-more-info-container').css('display', 'none');
	};
}

function WorkCtrl($scope, $compile, worksSrvc, workspaceSrvc) {
	$scope.workspaceSrvc = workspaceSrvc;
	$scope.worksSrvc = worksSrvc;
	$scope.works = worksSrvc.works;
	$scope.showBibtex = true;
	$scope.loadingInfo = false;
	$scope.bibtexCitations = {};
	$scope.editTranslatedTitle = false;
	$scope.types = null;
	$scope.worksInfo = {};
	$scope.privacyHelp = {};
	$scope.moreInfoOpen = false;
	
	$scope.toggleClickPrivacyHelp = function(key) {
		if (!document.documentElement.className.contains('no-touch'))
			$scope.privacyHelp[key]=!$scope.privacyHelp[key];
	};

	$scope.addExternalIdentifier = function () {
		$scope.editWork.workExternalIdentifiers.push({workExternalIdentifierId: { value: ""}, workExternalIdentifierType: {value: ""} });
	};
	
	$scope.showAddModal = function(){;
		$scope.editTranslatedTitle = false;
		$scope.types = null;
	    $.colorbox({	    	
	    	scrolling: true,
	        html: $compile($('#add-work-modal').html())($scope),	        
	        onLoad: function() {$('#cboxClose').remove();},
			// start the colorbox off with the correct width
			width: formColorBoxResize(),
			onComplete: function() {
				//resize to insure content fits
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
    
	$scope.showWorkImportWizard =  function() {
		$.colorbox({        	            
            html : $compile($('#import-wizard-modal').html())($scope),
            onComplete: function() {$.colorbox.resize();}
        });
	};
	
	$scope.addWorkModal = function(){
		$.ajax({
			url: getBaseUri() + '/works/work.json',
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
			url: getBaseUri() + '/works/work.json',	        
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
		if ($scope.editWork.citation
				&& $scope.editWork.citation.citation.value
				&& $scope.editWork.citation.citation.value.length > 0
				&& $scope.editWork.citation.citationType.value == 'bibtex') {
			try {
				var parsed = bibtexParse.toJSON($scope.editWork.citation.citation.value);
				console.log(parsed);
				if (parsed.length == 0) throw "bibtex parse return nothing";
				var index = $scope.editWork.citation.citation.errors.indexOf(om.get('manualWork.bibtext.notValid'));
				if (index > -1) {
					$scope.editWork.citation.citation.errors.splice(index, 1);
				}
			} catch (err) {
				$scope.editWork.citation.citation.errors.push(om.get('manualWork.bibtext.notValid'));
			};
		};
	};
		
	$scope.addWorkToScope = function() {
		if($scope.worksToAddIds.length != 0 ) {
			$scope.worksSrvc.loading = true;
			var workIds = $scope.worksToAddIds.splice(0,20).join();
			$.ajax({
				url: getBaseUri() + '/works/works.json?workIds=' + workIds,
				dataType: 'json',
				success: function(data) {
					$scope.$apply(function(){ 
						for (i in data) {
							var dw = data[i];													
							$scope.works.push(dw);
						}
					});
					if($scope.worksToAddIds.length == 0 ) {
						$scope.worksSrvc.loading = false;
						$scope.$apply();
						fixZindexIE7('.workspace-toolbar', 999999);
						fixZindexIE7('.workspace-private-toolbar', 500);
						fixZindexIE7('#privacy-bar', 400);						
					} else {
						$scope.$apply();
						setTimeout(function () {
							$scope.addWorkToScope(); 
						},50);
					}
				}
			}).fail(function() { 
		    	console.log("Error fetching work: " + value);
		    });
		} else {
			$scope.worksSrvc.loading = false;
		}
	}; 	

	$scope.getWorks = function() {
		//clear out current works
		$scope.worksToAddIds = null;
		$scope.worksSrvc.loading = true;
		$scope.works.length = 0;
		//get work ids
		$.ajax({
			url: getBaseUri() + '/works/workIds.json',	        
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
		
	
	$scope.renderTranslatedTitleInfo = function(putCode) {		
		var info = null; 
		
		if(putCode != null && $scope.worksInfo[putCode] != null && $scope.worksInfo[putCode].workTitle != null && $scope.worksInfo[putCode].workTitle.translatedTitle != null) {
			info = $scope.worksInfo[putCode].workTitle.translatedTitle.content + ' - ' + $scope.worksInfo[putCode].workTitle.translatedTitle.languageName;										
		}		
		
		return info;
	};
		
	//init
	$scope.getWorks();	
	
	$scope.moreInfoClick = function(work, $event) {
		if (!document.documentElement.className.contains('no-touch'))
			$scope.moreInfoOpen?$scope.closePopover():$scope.loadWorkInfo(work.putCode.value, $event);
	};
	
	$scope.moreInfoMouseEnter = function(work, $event) {
		$event.stopPropagation();
		if (document.documentElement.className.contains('no-touch'))
			$scope.loadWorkInfo(work.putCode.value, $event);
		else
			$scope.moreInfoOpen?$scope.closePopover():$scope.loadWorkInfo(work.putCode.value, $event);
	};
	
	$scope.loadWorkInfo = function(putCode, event) {
		//Close any open popover
		$scope.closePopover(event);
		$scope.moreInfoOpen = true;
		//Display the popover
		$scope.loadingInfo = true;		
		$(event.target).next().css('display','inline');	
		if($scope.worksInfo[putCode] == null) {		
			$.ajax({
				url: getBaseUri() + '/works/getWorkInfo.json?workId=' + putCode,	        
		        dataType: 'json',
		        success: function(data) {
		        	
		        	$scope.$apply(function () {
		        		removeBadContributors(data);
						addBibtexCitation($scope,data);
						$scope.worksInfo[putCode] = data;
						$scope.loadingInfo = false;
		        	});		        	
		        }
			}).fail(function(){
				// something bad is happening!
		    	console.log("error fetching works");
		    	$scope.loadingInfo = false;
			});
		} else {
			$(event.target).next().css('display','inline');
			$scope.loadingInfo = false;
		}
	};			
	
	$scope.closePopover = function(event) {
		$scope.moreInfoOpen = false;
		$('.work-more-info-container').css('display', 'none');
	};
	
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
		openImportWizardUrl(url);
	};

	
	$scope.removeWork = function(work) {
		$.ajax({
	        url: getBaseUri() + '/works/works.json',
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
		$scope.editWork.visibility = priv;
	};
			
	$scope.setPrivacy = function(putCode, priv, $event) {
		$event.preventDefault();
		var idx;
		for (idx in $scope.works) {
			if ($scope.works[idx].putCode.value == putCode)
				break;
		}
		$scope.works[idx].visibility = priv;
		$scope.curPrivToggle = null;
		$scope.updateProfileWork(putCode);
	};
	
	$scope.serverValidate = function (relativePath) {
		$.ajax({
	        url: getBaseUri() + '/' + relativePath,
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
	        url: getBaseUri() + '/works/profileWork.json',
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
		        url: getBaseUri() + '/works/loadWorkTypes.json?workCategory=' + $scope.editWork.workCategory.value,
		        type: 'POST',	        
		        contentType: 'application/json;charset=UTF-8',
		        dataType: 'json',
		        success: function(data) {
		        	
		        	$scope.$apply(function() {
			        	$scope.types = data;		        	
			        	switch ($scope.editWork.workCategory.value){
			                case "conference":
			                	$scope.editWork.workType.value="conference-paper";		                	
			                    break;
			                case "intellectual_property":
			                	$scope.editWork.workType.value="patent";
			                    break;
			                case "other_output":
			                	$scope.editWork.workType.value="data-set";
			                    break;
			                case "publication":
			                	$scope.editWork.workType.value="journal-article";
			                    break;
			        	}
			        	console.log($scope.editWork.workType.value);
		        	});
		        	
		        }
		    }).fail(function() { 
		    	console.log("Error loading work types.");
		    });
		} else {
			$scope.types = null;
		}
	};
	
	$scope.clearErrors = function() {
		$scope.editWork.workCategory.errors = [];
		$scope.editWork.workType.errors = [];
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
				$('#ajax-loader').hide();
				var resultsContainer = data['orcid-search-results'];
				$scope.numFound = resultsContainer['num-found'];
				if(resultsContainer['orcid-search-result']){
					$scope.results = $scope.results.concat(resultsContainer['orcid-search-result']);
				}
				if(!$scope.numFound){
					$('#no-results-alert').fadeIn(1200);
				}
				$scope.areMoreResults = $scope.numFound > ($scope.start + $scope.rows);
				$scope.$apply();
				var newSearchResults = $('.new-search-result');
				if(newSearchResults.length > 0){
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
		return $scope.results.length > 0;
	};
	
	// init
	$scope.getResults(10);
};

// Controller for delegate permissions that have been granted BY the current user
function DelegatesCtrl($scope, $compile){
	$scope.results = new Array();
	$scope.numFound = 0;
	$scope.start = 0;
	$scope.rows = 10;
	$scope.showLoader = false;
	$scope.effectiveUserOrcid = $('body').data('effective-user-orcid');
	
	$scope.sort = {
		column: 'delegateSummary.creditName.content',
		descending: false
	};
	
	$scope.changeSorting = function(column) {
		var sort = $scope.sort;
		if (sort.column === column) {
			sort.descending = !sort.descending;
		} else {
			sort.column = column;
			sort.descending = false;
		}
	};
	
	$scope.search = function(){
		$scope.results = new Array();
		$scope.showLoader = true;
		$('#no-results-alert').hide();
		if(isEmail($scope.userQuery)){
			$scope.numFound = 0;
			$scope.start = 0;
			$scope.areMoreResults = 0;
			$scope.searchByEmail();
		}
		else{
			$scope.getResults();
		}
	};
	
	$scope.searchByEmail = function(){
		$.ajax({
			url: $('body').data('baseurl') + "manage/search-for-delegate-by-email/" + encodeURIComponent($scope.userQuery) + '/',      
			dataType: 'json',
			headers: { Accept: 'application/json'},
			success: function(data) {
				$scope.confirmAddDelegateByEmail(data);
				$scope.showLoader = false;
				$scope.$apply();
			}
		}).fail(function(){
			// something bad is happening!
			console.log("error doing search for delegate by email");
		});
		
	};
	
	$scope.getResults = function(rows){
		var query = "{!edismax qf='given-and-family-names^50.0 family-name^10.0 given-names^5.0 credit-name^10.0 other-names^5.0 text^1.0' pf='given-and-family-names^50.0' mm=1}" + $scope.userQuery;
		var orcidRegex = new RegExp("(\\d{4}-){3,}\\d{3}[\\dX]");
		var regexResult = orcidRegex.exec($scope.userQuery);
		if(regexResult){
			query = "orcid:" + regexResult[0];
		}
		$.ajax({
			url: $('#DelegatesCtrl').data('search-query-url') + query + '&start=' + $scope.start + '&rows=' + $scope.rows,      
			dataType: 'json',
			headers: { Accept: 'application/json'},
			success: function(data) {
				var resultsContainer = data['orcid-search-results'];
				$scope.numFound = resultsContainer['num-found'];
				if(resultsContainer['orcid-search-result']){
					$scope.numFound = resultsContainer['num-found'];
					$scope.results = $scope.results.concat(resultsContainer['orcid-search-result']);
				}
				if(!$scope.numFound){
					$('#no-results-alert').fadeIn(1200);
				}
				$scope.areMoreResults = $scope.numFound >= ($scope.start + $scope.rows);
				$scope.showLoader = false;
				$scope.$apply();
				var newSearchResults = $('.new-search-result');
				if(newSearchResults.length > 0){
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
			}
		}).fail(function(){
			// something bad is happening!
			console.log("error doing search for delegates");
		});
	};
	
	$scope.getMoreResults = function(){
		$scope.showLoader = true;
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
	
	$scope.getDisplayName = function(result){
		var personalDetails = result['orcid-profile']['orcid-bio']['personal-details'];
		var creditName = personalDetails['credit-name'];
		if(creditName !== undefined){
			return creditName.value;
		}
		return personalDetails['given-names'].value + ' ' + personalDetails['family-name'].value;
	};
	
	$scope.confirmAddDelegateByEmail = function(emailSearchResult){
		$scope.emailSearchResult = emailSearchResult;
		$.colorbox({                      
			html : $compile($('#confirm-add-delegate-by-email-modal').html())($scope),
			transition: 'fade',
			close: '',
			onLoad: function() {
				$('#cboxClose').remove();
			},
			onComplete: function() {$.colorbox.resize();},
			scrolling: true
		});
	};
	
	$scope.confirmAddDelegate = function(delegateName, delegateId, delegateIdx){
		$scope.delegateNameToAdd = delegateName;
		$scope.delegateToAdd = delegateId;
		$scope.delegateIdx = delegateIdx;
		$.colorbox({                      
			html : $compile($('#confirm-add-delegate-modal').html())($scope),
			transition: 'fade',
			close: '',
			onLoad: function() {
				$('#cboxClose').remove();
			},
			onComplete: function() {$.colorbox.resize();},
			scrolling: true
		});
	};
	
	$scope.addDelegateByEmail = function(delegateEmail) {
		$.ajax({
	        url: $('body').data('baseurl') + 'account/addDelegateByEmail.json',
	        type: 'POST',
	        data: delegateEmail,
	        contentType: 'application/json;charset=UTF-8',
	        success: function(data) {
	        	$scope.getDelegates();
	        	$scope.$apply();
	        	$scope.closeModal();
	        }
	    }).fail(function() { 
	    	console.log("Error adding delegate.");
	    });
	};
	
	$scope.addDelegate = function() {
		$scope.results.splice($scope.delegateIdx, 1);
		$.ajax({
	        url: getBaseUri() + '/account/addDelegate.json',
	        type: 'POST',
	        data: $scope.delegateToAdd,
	        contentType: 'application/json;charset=UTF-8',
	        success: function(data) {
	        	$scope.getDelegates();
	        	$scope.$apply();
	        	$scope.closeModal();
	        }
	    }).fail(function() { 
	    	console.log("Error adding delegate.");
	    });
	};
	
	$scope.confirmRevoke = function(delegateName, delegateId) {
	    $scope.delegateNameToRevoke = delegateName;
	    $scope.delegateToRevoke = delegateId;
        $.colorbox({
            html : $compile($('#revoke-delegate-modal').html())($scope)
            	
        });
        $.colorbox.resize();
	};

	$scope.revoke = function () {
		$.ajax({
	        url: getBaseUri() + '/account/revokeDelegate.json',
	        type: 'DELETE',
	        data:  $scope.delegateToRevoke,
	        contentType: 'application/json;charset=UTF-8',
	        success: function(data) {
				$scope.getDelegates();
				$scope.$apply();
	        	$scope.closeModal();
	        }
	    }).fail(function() { 
	    	// something bad is happening!
	    	console.log("$DelegateCtrl.revoke() error");
	    });
	};
	
	$scope.getDelegates = function() {
		$.ajax({
	        url: getBaseUri() + '/account/delegates.json',
	        dataType: 'json',
	        success: function(data) {
	        	$scope.delegatesByOrcid = {};
	        	$scope.delegation = data;
	        	if(data.givenPermissionTo != null){
		        	for(var i=0; i < data.givenPermissionTo.delegationDetails.length; i++){
		        		var delegate = data.givenPermissionTo.delegationDetails[i];
		        		$scope.delegatesByOrcid[delegate.delegateSummary.orcidIdentifier.path] = delegate;
		        	}
	        	}
	        	$scope.$apply();
	        }
	    }).fail(function() { 
	    	// something bad is happening!
	    	console.log("error with delegates");
	    });
	};
	
	$scope.closeModal = function() {
		$.colorbox.close();
	};
	
	// init
	$scope.getDelegates();
	
};

// Controller for delegate permissions that have been granted TO the current user
function DelegatorsCtrl($scope, $compile){
	
	$scope.sort = {
			column: 'delegateSummary.creditName.content',
			descending: false
	};
	
	$scope.changeSorting = function(column) {
		var sort = $scope.sort;
		if (sort.column === column) {
			sort.descending = !sort.descending;
		} else {
			sort.column = column;
			sort.descending = false;
		}
	};
	
	$scope.getDelegators = function() {
		$.ajax({
	        url: getBaseUri() + '/delegators/delegators-and-me.json',
	        dataType: 'json',
	        success: function(data) {
	        	$scope.delegators = data.delegators;
	        	$scope.$apply();
	        }
	    }).fail(function() { 
	    	// something bad is happening!
	    	console.log("error with delegates");
	    });
	};
	
	$scope.selectDelegator = function(datum) {
		window.location.href = getBaseUri() + '/switch-user?j_username=' + datum.orcid;
	};
	
	$("#delegatorsSearch").typeahead({
		name: 'delegatorsSearch',
		remote: {
			url: getBaseUri()+'/delegators/search-for-data/%QUERY?limit=' + 10
		},
		template: function (datum) {
			var forDisplay;
			if(datum.noResults){
				forDisplay = "<span class=\'no-delegator-matches\'>" + om.get('delegators.nomatches') + "</span>";
			}
			else{
				forDisplay = 
					'<span style=\'white-space: nowrap; font-weight: bold;\'>' + datum.value + '</span>'
					+'<span style=\'font-size: 80%;\'> (' + datum.orcid + ')</span>';
			}
			return forDisplay;
		}
	});
	$("#delegatorsSearch").bind("typeahead:selected", function(obj, datum) {        
		if(!datum.noResults){
			$scope.selectDelegator(datum);
		}
		$scope.$apply();
	});
	
	// init
	$scope.getDelegators();
	
};

function SwitchUserCtrl($scope, $compile, $document){
	$scope.isDroppedDown = false;
	$scope.searchResultsCache = new Object();
	
	$scope.openMenu = function(event){
		$scope.isDroppedDown = true;
		event.stopPropagation();
	};
	
	$scope.getDelegates = function() {
		$.ajax({
	        url: getBaseUri() + '/delegators/delegators-and-me.json',
	        dataType: 'json',
	        success: function(data) {
	        	$scope.delegators = data.delegators;
				$scope.searchResultsCache[''] = $scope.delegators;
	        	$scope.me = data.me;
	        	$scope.unfilteredLength = $scope.delegators != null ? $scope.delegators.delegationDetails.length : 0;
	        	$scope.$apply();
	        }
	    }).fail(function() { 
	    	// something bad is happening!
	    	console.log("error with delegates");
	    });
	};
	
	$scope.search = function() {
		if($scope.searchResultsCache[$scope.searchTerm] === undefined) {
			if($scope.searchTerm === ''){
				$scope.getDelegates();
				$scope.searchResultsCache[$scope.searchTerm] = $scope.delegators;
			}
			else {
				$.ajax({
			        url: getBaseUri() + '/delegators/search/' + encodeURIComponent($scope.searchTerm) + '?limit=10',
			        dataType: 'json',
			        success: function(data) {
			        	$scope.delegators = data;
			        	$scope.searchResultsCache[$scope.searchTerm] = $scope.delegators;
			        	$scope.$apply();
			        }
			    }).fail(function() { 
			    	// something bad is happening!
			    	console.log("error searching for delegates");
			    });
			}
		} else {
			$scope.delegators = $scope.searchResultsCache[$scope.searchTerm];
		}
	};
	
	$scope.switchUser = function(targetOrcid){
		$.ajax({
	        url: getBaseUri() + '/switch-user?j_username=' + targetOrcid,
	        dataType: 'json',
	        complete: function(data) {
	        	window.location.reload();
	        }
	    });
	};
	
	$document.bind('click',
		function(event){
			if(event.target.id !== "delegators-search"){
				$scope.isDroppedDown = false;
				$scope.searchTerm = '';
				$scope.$apply();
			}
		});
	
	// init
	$scope.getDelegates();
};

function statisticCtrl($scope){	
	$scope.liveIds = 0;	
	$scope.getLiveIds = function(){
		$.ajax({
	        url: getBaseUri()+'/statistics/liveids.json',	        
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

function languageCtrl($scope, $cookies) {
	var productionLangList = 
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
	        	"value": 'ko',
	    		"label": '한국어'
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
	var testingLangList = 
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
	        	"value": 'ko',
	    		"label": '한국어'
	        },
	        {
	        	"value": 'pt',
	    		"label": 'Português'
	        },	        
	        {
	        	"value": 'ru',
	    		"label": 'Русский'
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

	if (location == parent.location && window.location.hostname.toLowerCase() != "orcid.org") 
		$scope.languages = testingLangList;
	else
		$scope.languages = productionLangList;
	
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
	        url: getBaseUri()+'/lang.json?lang=' + $scope.language.value + "&callback=?",	        
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
	$scope.deactivateMessage = om.get('admin.profile_deactivation.success');
	$scope.reactivateMessage = om.get('admin.profile_reactivation.success');
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
	        url: getBaseUri()+'/admin-actions/deactivate-profile?orcid=' + $scope.orcidToDeactivate,	        
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
	        url: getBaseUri()+'/admin-actions/reactivate-profile?orcid=' + $scope.orcidToReactivate,	        
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
	        url: getBaseUri()+'/admin-actions/deactivate-profile/check-orcid.json?orcid=' + $scope.orcidToDeactivate,	        
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
	        url: getBaseUri()+'/admin-actions/deprecate-profile/check-orcid.json?orcid=' + orcid,	        
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
	        url: getBaseUri()+'/admin-actions/deprecate-profile/deprecate-profile.json?deprecated=' + deprecatedOrcid + '&primary=' + primaryOrcid,	        
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
		console.log(om.get('admin.profile_deprecation.deprecate_account.success_message'));
		$scope.successMessage = om.get('admin.profile_deprecation.deprecate_account.success_message').replace("{{0}}", deprecated).replace("{{1}}", primary);
		
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
	$scope.confirmRevoke = function(appName, appGroupName, appIndex){
		$scope.appName = appName;
		$scope.appIndex = appIndex;
		$scope.appGroupName = appGroupName;
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
		orcidGA.gaPush(['_trackEvent', 'Disengagement', 'Revoke_Access', 'OAuth '+ orcidGA.buildClientString($scope.appGroupName, $scope.appName)]);
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
		$scope.getGroup();
		$.colorbox({                      
			html : $compile($('#add-new-group').html())($scope),				
				onLoad: function() {
				$('#cboxClose').remove();
			}
		});
		
		$.colorbox.resize({width:"400px" , height:"390px"});
	};
	
	$scope.closeModal = function() {
		$.colorbox.close();
	};
	
	$scope.listGroups = function() {
		$.ajax({
	        url: getBaseUri()+'/admin-actions/list-groups.json',	        
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
	        url: getBaseUri()+'/admin-actions/group.json',	        
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
	        url: getBaseUri()+'/admin-actions/create-group.json',	        
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

function findIdsCtrl($scope,$compile){
	$scope.emails = "";
	$scope.emailIdsMap = {};
	$scope.showSection = false;
	
	$scope.toggleSection = function(){
		$scope.showSection = !$scope.showSection;
    	$('#find_ids_section').toggle();
	};
	
	$scope.findIds = function() {
		$.ajax({
	        url: getBaseUri()+'/admin-actions/find-id?csvEmails=' + $scope.emails,	        
	        type: 'GET',
	        dataType: 'json',
	        success: function(data){
	        	$scope.$apply(function(){ 
	        		if(!$.isEmptyObject(data)) {	        			
	        			$scope.emailIdsMap = data;	        			
	        		} else {
	        			$scope.emailIdsMap = null;
	        		}
	        		$scope.emails='';
	        		$scope.showEmailIdsModal();
				});
	        }
	    }).fail(function(error) { 
	    	// something bad is happening!	    	
	    	console.log("Error deprecating the account");	    	
	    });	
	};
	
	$scope.showEmailIdsModal = function() {
		$.colorbox({                      
			html : $compile($('#email-ids-modal').html())($scope),
				scrolling: true,
				onLoad: function() {
				$('#cboxClose').remove();
			},
			scrolling: true
		});	

		setTimeout(function(){$.colorbox.resize({width:"450px"});},100);		
	};	
	
	$scope.closeModal = function() {
		$.colorbox.close();
	};
};

function resetPasswordCtrl($scope,$compile) {
	$scope.showSection = false;
	$scope.params = {orcidOrEmail:'',password:''};
	$scope.result = '';
	
	$scope.toggleSection = function(){
		$scope.showSection = !$scope.showSection;
    	$('#reset_password_section').toggle();
	};
	
	$scope.randomString = function() {
		$scope.result = '';
		$.ajax({
	        url: getBaseUri()+'/admin-actions/generate-random-string.json',	        
	        type: 'GET',
	        dataType: 'text',
	        success: function(data){
	        	$scope.$apply(function(){ 
	        		$scope.params.password=data;
				});
	        }
	    }).fail(function(error) { 
	    	console.log(error);
	    	// something bad is happening!	    	
	    	console.log("Error generating random string");	    	
	    });	
	};
	
	$scope.resetPassword = function(){
		$scope.result = '';		
		$.ajax({
	        url: getBaseUri()+'/admin-actions/reset-password.json',	        
	        type: 'POST',
	        data: angular.toJson($scope.params),
	        contentType: 'application/json;charset=UTF-8',
	        dataType: 'text',
	        success: function(data){	      	        	
		        $scope.$apply(function(){ 
		        	$scope.result=data;
		        	$scope.params.orcidOrEmail='';
		        	$scope.params.password='';
				});	        	
		        $scope.closeModal();
	        }
	    }).fail(function(error) { 
	    	// something bad is happening!	    	
	    	console.log("Error generating random string");	    	
	    });	
		
	};
	
	$scope.confirmResetPassword = function(){
		if($scope.params.orcidOrEmail != '' && $scope.params.password != '') {
			$.colorbox({                      
				html : $compile($('#confirm-reset-password').html())($scope),
					scrolling: true,
					onLoad: function() {
					$('#cboxClose').remove();
				},
				scrolling: true
			});
			
			$.colorbox.resize({width:"450px" , height:"150px"});
		}
	};
	
	$scope.closeModal = function() {
		$scope.params.orcidOrEmail='';
    	$scope.params.password='';
    	$scope.result= '';
		$.colorbox.close();
	};
};

function removeSecQuestionCtrl($scope,$compile) {
	$scope.showSection = false;
	$scope.orcidOrEmail = '';
	$scope.result= '';
	
	$scope.toggleSection = function(){
		$scope.showSection = !$scope.showSection;
    	$('#remove_security_question_section').toggle();
	};
	
	$scope.removeSecurityQuestion = function() {		
		$.ajax({
	        url: getBaseUri()+'/admin-actions/remove-security-question.json',	        
	        type: 'POST',
	        data: $scope.orcidOrEmail,
	        contentType: 'application/json;charset=UTF-8',
	        dataType: 'text',
	        success: function(data){	        	
		        $scope.$apply(function(){ 
		        	$scope.result=data;
		        	$scope.orcid = '';
				});	     
		        $scope.closeModal();
	        }
	    }).fail(function(error) { 
	    	// something bad is happening!	    	
	    	console.log("Error generating random string");	    	
	    });	
	};
	
	$scope.confirmRemoveSecurityQuestion = function(){
		if($scope.orcid != '') {
			$.colorbox({                      
				html : $compile($('#confirm-remove-security-question').html())($scope),
					scrolling: true,
					onLoad: function() {
					$('#cboxClose').remove();
				},
				scrolling: true
			});
			
			$.colorbox.resize({width:"450px" , height:"150px"});
		}
	};
	
	$scope.closeModal = function() {
		$scope.orcidOrEmail = '';
		$scope.result= '';
		$.colorbox.close();
	};	
};

function SSOPreferencesCtrl($scope, $compile) {
	$scope.showReg = false;
	$scope.userCredentials = null;	
	$scope.editing = false;
	$scope.hideGoogleUri = false;
	$scope.hideRunscopeUri = false;
	$scope.googleUri = 'https://developers.google.com/oauthplayground';
	$scope.runscopeUri = 'https://www.runscope.com/oauth_tool/callback';
	$scope.playgroundExample = '';
	$scope.googleExampleLink = 'https://developers.google.com/oauthplayground/#step1&scopes=/authenticate&oauthEndpointSelect=Custom&oauthAuthEndpointValue=[BASE_URI_ENCODE]/oauth/authorize&oauthTokenEndpointValue=[PUB_BASE_URI_ENCODE]/oauth/token&oauthClientId=[CLIENT_ID]&oauthClientSecret=[CLIENT_SECRET]&accessTokenType=bearer';
	$scope.sampleAuthCurl = '';
	$scope.sampleAuthCurlTemplate = "curl -i -L -k -H 'Accept: application/json' --data 'client_id=[CLIENT_ID]&client_secret=[CLIENT_SECRET]&grant_type=authorization_code&redirect_uri=[REDIRECT_URI]&code=REPLACE WITH OAUTH CODE' [PUB_BASE_URI]/oauth/token";
	$scope.runscopeExample = '';
	$scope.runscopeExampleLink = 'https://www.runscope.com/oauth2_tool';
	$scope.authorizeURLTemplate = getBaseUri() + '/oauth/authorize?client_id=[CLIENT_ID]&response_type=code&scope=/authenticate&redirect_uri=[REDIRECT_URI]';
	$scope.tokenURL = orcidVar.pubBaseUri + '/oauth/token';
	$scope.authorizeURL = '';
	$scope.selectedRedirectUri = '';
	$scope.selectedClientSecret = null;
	$scope.creating = false;
	
	$scope.enableDeveloperTools = function() {
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
	        type: 'POST',	                	      
	        success: function(data){	        	
	        	$scope.$apply(function(){ 
	        		if(data != null && data.clientSecrets != null) {
	        			$scope.playgroundExample = '';
	        			$scope.userCredentials = data;	
	        			$scope.hideGoogleUri = false;	
	        			$scope.selectedRedirectUri = $scope.userCredentials.redirectUris[0];
	        			$scope.selectedClientSecret = $scope.userCredentials.clientSecrets[0];
	        			for(var i = 0; i < $scope.userCredentials.redirectUris.length; i++) {
	        				if($scope.googleUri == $scope.userCredentials.redirectUris[i].value.value) {
	        					$scope.hideGoogleUri = true;
	        				}
	        				
	        				if($scope.userCredentials.redirectUris[i].value.value < $scope.selectedRedirectUri.value.value) {	        					
	        					$scope.selectedRedirectUri = $scope.userCredentials.redirectUris[i];
	        				}
	        			}
	        			$scope.updateSelectedRedirectUri();
	        		} else {
	        			$scope.showReg = true;
	        		}        					        	
				});
	        }
	    }).fail(function(error) { 
	    	// something bad is happening!	   	    	
	    	console.log("Error obtaining SSO credentials");
	    	console.log(error);
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
	        			$scope.creating = false;	        			
	        			$scope.showReg = false;
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
		//Initialize client secret if needed
		if($scope.selectedClientSecret == null) {
			$scope.selectedClientSecret = $scope.userCredentials.clientSecrets[0];
		}
		var clientId = $scope.userCredentials.clientOrcid.value;
		var selectedRedirectUriValue = $scope.selectedRedirectUri.value.value;
		var selectedClientSecret = $scope.selectedClientSecret.value;
		
		console.log("Selected secret: " + angular.toJson($scope.selectedClientSecret));
		
		//Build the google playground url example
		$scope.playgroundExample = '';
		
		if($scope.googleUri == selectedRedirectUriValue) {
			var example = $scope.googleExampleLink;
			example = example.replace('[PUB_BASE_URI_ENCODE]', encodeURI(orcidVar.pubBaseUri));
			example = example.replace('[BASE_URI_ENCODE]', encodeURI(getBaseUri()));
			example = example.replace('[CLIENT_ID]', clientId);
			example = example.replace('[CLIENT_SECRET]', selectedClientSecret);	        					
			$scope.playgroundExample = example;
		}		
				
		var example = $scope.authorizeURLTemplate;
		example = example.replace('[PUB_BASE_URI]', orcidVar.pubBaseUri);
		example = example.replace('[CLIENT_ID]', clientId);
		example = example.replace('[REDIRECT_URI]', selectedRedirectUriValue);
		$scope.authorizeURL = example;
		
		// rebuild sampel Auhtroization Curl
		var sampeleCurl = $scope.sampleAuthCurlTemplate;
		$scope.sampleAuthCurl = sampeleCurl.replace('[CLIENT_ID]', clientId)
		    .replace('[CLIENT_SECRET]', selectedClientSecret)
		    .replace('[PUB_BASE_URI]', orcidVar.pubBaseUri)
		    .replace('[REDIRECT_URI]', selectedRedirectUriValue);
       
	};
	
	$scope.updateSelectedClientSecret = function(){
		$scope.updateSelectedRedirectUri();
	};
	
	$scope.confirmDeleteClientSecret = function(index) {
		$scope.clientSecretToDelete = $scope.userCredentials.clientSecrets[index];
		$.colorbox({        	            
            html : $compile($('#delete-secret-modal').html())($scope), 
            transition: 'fade',
            onLoad: function() {
			    $('#cboxClose').remove();
			},
	        scrolling: true
        });
        $.colorbox.resize({width:"400px" , height:"175px"});
	};
	
	$scope.deleteClientSecret = function(secretToDelete) {		
		$.ajax({
			url: getBaseUri() + '/developer-tools/delete-client-secret.json',
			type: 'POST',
			dataType: 'text',
			data: 'clientSecret=' + secretToDelete, 
			success: function(data) {
				if(data) {
					$scope.getSSOCredentials();
					$scope.closeModal();
				} else
					console.log('Unable to delete client secret');
			}
		}).fail(function() { 
	    	console.log("Error fetching empty redirect uri");
	    });
		
		
	};
	
	$scope.addClientSecret = function() {
		$.ajax({
			url: getBaseUri() + '/developer-tools/add-client-secret.json',
			type: 'POST',			
			success: function(data) {
				if(data)
					$scope.getSSOCredentials();
				else
					console.log('Unable to delete client secret');
			}
		}).fail(function() { 
	    	console.log("Error fetching empty redirect uri");
	    });
	};
	
	$scope.closeModal = function(){
		$.colorbox.close();	
	};
	
	//init
	$scope.getSSOCredentials();	
};

function ClientEditCtrl($scope, $compile){	
	$scope.clients = [];
	$scope.newClient = null;	
	$scope.scopeSelectorOpen = false;		
	$scope.selectedScopes = [];
	$scope.availableRedirectScopes = [];
	
	// Get the list of clients associated with this user
	$scope.getClients = function(){
		$.ajax({
	        url: getBaseUri() + '/group/developer-tools/get-clients.json',
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
			url: getBaseUri() + '/group/developer-tools/client.json',
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
        $.colorbox.resize({width:"400px" , height:"450px"});
	};
	
	// Add a new uri input field to a new client
	$scope.addUriToNewClientTable = function(){		
		$scope.newClient.redirectUris.push({value: {value: ''},type: {value: 'default'}, scopes: []});	
	};
	
	// Add a new uri input field to a existing client
	$scope.addUriToExistingClientTable = function(){
		$scope.clientToEdit.redirectUris.push({value: {value: ''},type: {value: 'default'}, scopes: []});
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
        $.colorbox.resize({width:"400px" , height:"450px"});   
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
		
        $.colorbox.resize({width:"560px" , height:"275px"});
        
	};
	
	$scope.closeModal = function(){
		$.colorbox.close();	
	};
	
	
	// Delete an uri input field 
	$scope.deleteUri = function(idx){
		$scope.clientToEdit.redirectUris.splice(idx, 1);
	};		
	
	// Delete an uri input field 
	$scope.deleteJustCreatedUri = function(idx){
		$scope.newClient.redirectUris.splice(idx, 1);
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
	        		//If everything worked fine, reload the list of clients
	        		$scope.getClients();
	        		$.colorbox.close();
	        	}
	        }
	    }).fail(function() { 
	    	console.log("Error creating client information.");
	    });		
	};
	
	//Load the list of scopes for client redirect uris 
	$scope.loadAvailableScopes = function(){
		console.log("looking for available scopes");
		$.ajax({
	        url: getBaseUri() + '/group/developer-tools/get-available-scopes.json',
	        type: 'GET',
	        contentType: 'application/json;charset=UTF-8',
	        dataType: 'json',
	        success: function(data) {	        	
	        	$scope.availableRedirectScopes = data;
	        	console.log($scope.availableRedirectScopes);
	        }
	    }).fail(function() { 
	    	console.log("Unable to fetch redirect uri scopes.");
	    });		
	};
			
	//Load the default scopes based n the redirect uri type selected
	$scope.loadDefaultScopes = function(rUri) {
		//If the scopes are empty, fill it with the default scopes
		if(rUri.scopes.length == 0) {
			if(rUri.type.value == 'grant-read-wizard'){
				rUri.scopes.push('/orcid-profile/read-limited');
			} else if (rUri.type.value == 'import-works-wizard'){
				rUri.scopes.push('/orcid-profile/read-limited');
				rUri.scopes.push('/orcid-works/create');
			} else if (rUri.type.value == 'import-funding-wizard'){
				rUri.scopes.push('/orcid-profile/read-limited');
				rUri.scopes.push('/funding/create');
			}  
		}
	};		

	//Mark an item as selected
	$scope.setSelectedItem = function(rUri){
	    var scope = this.scope;
	    if (jQuery.inArray( scope, rUri.scopes ) == -1) {
	    	rUri.scopes.push(scope);
	    } else {
	    	rUri.scopes = jQuery.grep(rUri.scopes, function(value) {
	            return value != scope;
	          });
	    }
	    console.log(rUri.scopes);
	    return false;
	};
	
	//Checks if an item is selected, if so, returns the css classes that might 
	//be applied to the object
	$scope.isChecked = function (rUri) { 
		var scope = this.scope;
		if (jQuery.inArray( scope, rUri.scopes ) != -1) {
	        return 'glyphicon glyphicon-ok pull-right';
	    }
	    return false;
	};
	
	//init
	$scope.getClients();
	$scope.loadAvailableScopes();
	
};
