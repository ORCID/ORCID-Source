angular.module('orcidApp').controller('AffiliationCtrl', ['$scope', '$rootScope', '$compile', '$filter', 'affiliationsSrvc', 'workspaceSrvc', 'commonSrvc', 'emailSrvc', 'initialConfigService', function ($scope, $rootScope, $compile, $filter, affiliationsSrvc, workspaceSrvc, commonSrvc, emailSrvc, initialConfigService){
    $scope.affiliationsSrvc = affiliationsSrvc;
    $scope.editAffiliation;
    $scope.emailSrvc = emailSrvc;
    $scope.moreInfo = {};
    $scope.moreInfoCurKey = null;
    $scope.privacyHelp = {};
    $scope.privacyHelpCurKey = null;
    $scope.showElement = {};
    $scope.workspaceSrvc = workspaceSrvc;

    // ///////////////////// Begin of verified email logic for work
    var configuration = initialConfigService.getInitialConfiguration();
    var emailVerified = false;
    var emails = {};


    var showEmailVerificationModal = function(){
        $rootScope.$broadcast('emailVerifiedObj', {flag: emailVerified, emails: emails});
    };
    
    $scope.emailSrvc.getEmails(
        function(data) {
            emails = data.emails;
            if( $scope.emailSrvc.getEmailPrimary().verified == true ) {
                emailVerified = true;
            }
        }
    );
    // ///////////////////// End of verified email logic for work

    $scope.sortState = new ActSortState(GroupedActivities.AFFILIATION);
    $scope.sort = function(key) {       
        $scope.sortState.sortBy(key);
    };

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

    // remove once grouping is live
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

    // remove once grouping is live
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

    $scope.showDetailsMouseClick = function(key, $event) {
        $event.stopPropagation();
        $scope.moreInfo[key]=!$scope.moreInfo[key];
    };

    $scope.closeMoreInfo = function(key) {
        $scope.moreInfo[key]=false;
    };


    $scope.showAddModal = function(){
        var numOfResults = 25;
        $.colorbox({
            html: $compile($('#add-affiliation-modal').html())($scope),            
            onComplete: function() {
                // resize to insure content fits
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
                    $scope.disambiguatedAffiliation = data;
                    $scope.editAffiliation.orgDisambiguatedId.value = id;
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
        if ($scope.editAffiliation != undefined && $scope.editAffiliation.orgDisambiguatedId != undefined) delete $scope.editAffiliation.orgDisambiguatedId;
    };

    $scope.addAffiliationModal = function(type, affiliation){
        if(emailVerified === true || configuration.showModalManualEditVerificationEnabled == false){
            $scope.addAffType = type;
            if(affiliation === undefined) {
                $scope.removeDisambiguatedAffiliation();
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
            } else {
                $scope.editAffiliation = affiliation;
                if($scope.editAffiliation.orgDisambiguatedId != null)
                    $scope.getDisambiguatedAffiliation($scope.editAffiliation.orgDisambiguatedId.value);

                $scope.showAddModal();
            }
        }else{
            showEmailVerificationModal();
        }
    };

    $scope.addAffiliation = function(){
        if ($scope.addingAffiliation) return; // don't process if adding
                                                // affiliation
        $scope.addingAffiliation = true;
        $scope.editAffiliation.errors.length = 0;
        $.ajax({
            url: getBaseUri() + '/affiliations/affiliation.json',
            contentType: 'application/json;charset=UTF-8',
            dataType: 'json',
            success: function(data) {
                $scope.register = data;

                if ($scope.register.errors.length == 0) {
                    if ($scope.register.url != null) {
                        orcidGA.gaPush(['send', 'event', 'RegGrowth', 'New-Registration', 'Website']);
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
                // alert(angular.toJson(data));
                commonSrvc.copyErrorsLeft($scope.register, data);
                $scope.$apply();
            }
        }).fail(function() {
            // something bad is happening!
            console.log("RegistrationCtrl.postRegisterValidate() error");
        });
    };

    $scope.isValidClass = function (cur) {
        if (cur === undefined) return '';
        var valid = true;
        if (cur.required && (cur.value == null || cur.value.trim() == '')) valid = false;
        if (cur.errors !== undefined && cur.errors.length > 0) valid = false;
        return valid ? '' : 'text-error';
    };

    // init
    $scope.getClaim();
}]);