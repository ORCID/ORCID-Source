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

    /* Bulk Funtions */
    
    $scope.bulkDeleteCount = 0;
    $scope.bulkDeleteSubmit = false;

    $scope.toggleBulkEdit = function() {
        emailVerified = true;  //Remove this line

        if(emailVerified === true || configuration.showModalManualEditVerificationEnabled == false){
            if (!$scope.bulkEditShow) {
                $scope.bulkEditMap = {};
                $scope.bulkChecked = false;
                /*for (var idx in worksSrvc.groups){
                    $scope.bulkEditMap[worksSrvc.groups[idx].getActive().putCode.value] = false;
                }*/
            };
            $scope.bulkEditShow = !$scope.bulkEditShow;
            $scope.showBibtexImportWizard = false;
            $scope.workImportWizard = false;
            $scope.showBibtexExport = false;
        }else{
            showEmailVerificationModal();
        }
    };


    $scope.bulkApply = function(func) {
        /*for (var idx in worksSrvc.groups) {
            if ($scope.bulkEditMap[worksSrvc.groups[idx].getActive().putCode.value]){
                func(worksSrvc.groups[idx].getActive().putCode.value);
            }
        }*/
    };

    $scope.swapbulkChangeAll = function() {
        $scope.bulkChecked = !$scope.bulkChecked;
        /*for (var idx in worksSrvc.groups){
            $scope.bulkEditMap[worksSrvc.groups[idx].getActive().putCode.value] = $scope.bulkChecked;
        }*/
        $scope.bulkDisplayToggle = false;
    };

    $scope.bulkChangeAll = function(bool) {
        $scope.bulkChecked = bool;
        $scope.bulkDisplayToggle = false;
        /*for (var idx in worksSrvc.groups){
            $scope.bulkEditMap[worksSrvc.groups[idx].getActive().putCode.value] = bool;
        }*/
    };

    $scope.setBulkGroupPrivacy = function(priv) {
        var putCodes = new Array();
        /*for (var idx in worksSrvc.groups){
            if ($scope.bulkEditMap[worksSrvc.groups[idx].getActive().putCode.value]){    
                for (var idj in worksSrvc.groups[idx].activities) {
                    putCodes.push(worksSrvc.groups[idx].activities[idj].putCode.value);
                    worksSrvc.groups[idx].activities[idj].visibility = priv;
                }
            }
        }
        worksSrvc.updateVisibility(putCodes, priv);
        */
    };

    $scope.deleteBulk = function () {
        var delPuts = new Array();
        if ($scope.delCountVerify != parseInt($scope.bulkDeleteCount)) {
            $scope.bulkDeleteSubmit = true;
            return;
        }
        /*
        for (var idx in worksSrvc.groups){
            if ($scope.bulkEditMap[worksSrvc.groups[idx].getActive().putCode.value]){
                delPuts.push(worksSrvc.groups[idx].getActive().putCode.value);
            }
        }
        worksSrvc.deleteGroupWorks(delPuts);
        */
        $.colorbox.close();
        $scope.bulkEditShow = false;
    };


    $scope.deleteBulkConfirm = function(idx) {
        $scope.bulkDeleteCount = 0;
        $scope.bulkDeleteSubmit = false;        
        $scope.delCountVerify = 0;
        /*for (var idx in worksSrvc.groups){
            if ($scope.bulkEditMap[worksSrvc.groups[idx].getActive().putCode.value]){
                $scope.bulkDeleteCount++;
            }
        }*/

        $scope.bulkDeleteFunction = $scope.deleteBulk;

        $.colorbox({
            html: $compile($('#bulk-delete-modal').html())($scope)
        });
        $.colorbox.resize();
    };
    /* Bulk functions end */ 

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
            type: 'POST',
            data:  angular.toJson($scope.editAffiliation),
            success: function(data) {
                if (data.errors.length == 0){
                    $.colorbox.close();
                    $scope.addingAffiliation = false;
                    affiliationsSrvc.getAffiliations('affiliations/affiliationIds.json');
                } else {
                    $scope.editAffiliation = data;
                    commonSrvc.copyErrorsLeft($scope.editAffiliation, data);
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

    // For resizing color box in case of error
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
                commonSrvc.copyErrorsLeft($scope.editAffiliation, data);
                $scope.$apply();
            }
        }).fail(function() {
            // something bad is happening!
            console.log("RegistrationCtrl.serverValidate() error");
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
    affiliationsSrvc.getAffiliations('affiliations/affiliationIds.json');

    $scope.openEditAffiliation = function(affiliation) {
        $scope.addAffiliationModal(affiliation.affiliationType.value, affiliation);
    };
    
    $scope.showTooltip = function (element){        
        $scope.showElement[element] = true;
    };

    $scope.hideTooltip = function (element){        
        $scope.showElement[element] = false;
    };
}]);