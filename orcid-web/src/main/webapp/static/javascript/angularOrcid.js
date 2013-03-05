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
		$http.get($('body').data('baseurl') + 'account/emails.json').success(
				function(data) {
					$scope.emailsPojo = data;
		});		
	};
	
	$scope.initInputEmail = function () {
		$scope.inputEmail = {"value":"","primary":false,"current":true,"verified":false,"visibility":"PRIVATE","errors":[]};		
	};
	
	//init
	$scope.getEmails();
	$scope.initInputEmail();

	$scope.setPrimary = function(obj, $event) {
		
		for (i in $scope.emailsPojo.emails) {
			console.log($scope.emailsPojo.emails[i]);
			if (obj == $scope.emailsPojo.emails[i]) {
				$scope.emailsPojo.emails[i].primary = true;
			} else {
				$scope.emailsPojo.emails[i].primary = false;
			}
		}
		$scope.save();
	};
	
	$scope.toggleCurrent = function(obj, $event) {
		if (obj.current ==  true) {
			obj.current = false;
		} else {
			obj.current = true;
		}
		$scope.save();
	};
	
	$scope.toggleVisibility = function(obj, $event) {
		if (obj.visibility ==  "PRIVATE") {
			obj.visibility = "LIMITED";
		} else if (obj.visibility ==  "LIMITED") {
			obj.visibility = "PUBLIC";
		} else {
			obj.visibility = "PRIVATE";
		}
		$scope.save();
	};
	
	$scope.verifyEmail = function(obj, $event) {
	    alert( "we should send user to page to verify " + obj.value);  
	};


	$scope.save = function() {
		$http({
			url : $('body').data('baseurl') + 'account/emails.json',
			method : "POST",
			data : $scope.emailsPojo
		}).success(function(data, status, headers, config) {
			$scope.emailsPojo = data;
		}).error(function(data, status, headers, config) {
			$scope.status = status;
		});
	};

	$scope.add = function (obj, $event) {
		$http({
			url : $('body').data('baseurl') + 'account/addEmail.json',
			method : "POST",
			data : $scope.inputEmail
		}).success(function(data, status, headers, config) {
			$scope.inputEmail = data;
			if ($scope.inputEmail.errors.length == 0) {
				$scope.initInputEmail();
				$scope.getEmails();
			}
		}).error(function(data, status, headers, config) {
			//$scope.inputEmail = status;
			console.log("$EmailEdit.add() error "+ status + headers + data);
		});
	};
	
	$scope.deleteEmail = function(idx) {
		$scope.emailsPojo.emails.splice(idx, 1);
		$scope.save();
	};
	
	$scope.close = function() {
		$scope.$parent.toggleEmail();
	};
}
