var orcidNgModule = angular.module('orcidApp', []);

orcidNgModule.filter('emailPrimaryFtr', function($filter) {
	return function(booleanValue) {
		return booleanValue ? 'Primary Email' : 'Set Primary';
	};
});


orcidNgModule.filter('emailVisibilityFtr', function($filter) {
	return function(strValue) {
		return strValue.substring(0,1) +  strValue.substring(1).toLowerCase();
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
	$scope.showEditEmail = false;
	$scope.toggleText = "Edit";
	$scope.toggleEmail = function() {
		$scope.showEditEmail = !$scope.showEditEmail;
		if ($scope.toggleText == "Edit")
			$scope.toggleText = "Hide";
		else
			$scope.toggleText = "Edit";
	};
};

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
	$scope.getEmails();
	$scope.initInputEmail();

	$scope.setPrimary = function(idx, $event) {
		for (i in $scope.emailsPojo.emails) {
			console.log($scope.emailsPojo.emails[i]);
			if (i == idx) {
				$scope.emailsPojo.emails[i].primary = true;
			} else {
				$scope.emailsPojo.emails[i].primary = false;
			}
		}
		$scope.save();
	};
	
	$scope.toggleCurrent = function(idx, $event) {
		
		if ($scope.emailsPojo.emails[idx].current ==  true) {
			$scope.emailsPojo.emails[idx].current = false;
		} else {
			$scope.emailsPojo.emails[idx].current = true;
		}
		$scope.save();
	};
	
	$scope.toggleVisibility = function(idx, $event) {
		if ($scope.emailsPojo.emails[idx].visibility ==  "PRIVATE") {
			$scope.emailsPojo.emails[idx].visibility = "LIMITED";
		} else if ($scope.emailsPojo.emails[idx].visibility ==  "LIMITED") {
			$scope.emailsPojo.emails[idx].visibility = "PUBLIC";
		} else {
			$scope.emailsPojo.emails[idx].visibility = "PRIVATE";
		}
		$scope.save();
	};
	
	$scope.verifyEmail = function(idx, $event) {
	    alert( "we should send user to page to verify " + $scope.emailsPojo.emails[idx].value);  
	};

	$scope.save = function() {
		$.ajax({
	        url: $('body').data('baseurl') + 'account/emails.json',
	        type: 'POST',
	        data:  angular.toJson($scope.emailsPojo),
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

	$scope.add = function (obj, $event) {
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
		$scope.emailsPojo.emails.splice(idx, 1);
		$scope.save();
	};
	
	$scope.close = function() {
		$scope.$parent.toggleEmail();
	};
};
