/**
 * Fundings Controller
 * */
angular.module('orcidApp').controller('FundingCtrl',['$scope', '$rootScope', '$compile', '$filter', 'fundingSrvc', 'workspaceSrvc', 'commonSrvc', 'emailSrvc', 'initialConfigService', 'utilsService', function ($scope, $rootScope, $compile, $filter, fundingSrvc, workspaceSrvc, commonSrvc, emailSrvc, initialConfigService, utilsService) {
    $scope.addingFunding = false;
    $scope.disambiguatedFunding = null;
    $scope.displayURLPopOver = {};
    $scope.editFunding = null;
    $scope.editSources = {};
    $scope.editTranslatedTitle = false;
    $scope.emailSrvc = emailSrvc;
    $scope.fundingImportWizard = false;
    $scope.fundingSrvc = fundingSrvc;
    $scope.lastIndexedTerm = null;
    $scope.moreInfo = {};
    $scope.privacyHelp = {};
    $scope.showElement = {};
    $scope.wizardDescExpanded = {};
    $scope.workspaceSrvc = workspaceSrvc;
    
    /////////////////////// Begin of verified email logic for work
    var configuration = initialConfigService.getInitialConfiguration();
    var emailVerified = false;
    var emails = {};
    var utilsService = utilsService;


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
    /////////////////////// End of verified email logic for work

    $scope.sortState = new ActSortState(GroupedActivities.FUNDING);

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

    $scope.addFundingModal = function(data){
        if(emailVerified === true || configuration.showModalManualEditVerificationEnabled == false){
            if(data == undefined) {
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
            } else {
                $scope.editFunding = data;
                if($scope.editFunding.externalIdentifiers == null || $scope.editFunding.externalIdentifiers.length == 0) {
                    $scope.editFunding.externalIdentifiers.push($scope.getEmptyExtId());
                }            
                $scope.showAddModal();
            }
        }else{
            showEmailVerificationModal();
        }
    };

    $scope.closeAllMoreInfo = function() {
        for (var idx in $scope.moreInfo){
            $scope.moreInfo[idx]=false;
        }
    };

    $scope.closeMoreInfo = function(key) {
        $scope.moreInfo[key]=false;
    };

    $scope.getEmptyExtId = function() {
        return {
            "errors": [],
            "type": {
                "errors": [],
                "value": "award",
                "required": true,
                "getRequiredMessage": null
            },
            "value": {
                "errors": [],
                "value": "",
                "required": true,
                "getRequiredMessage": null
            },
            "url": {
                "errors": [],
                "value": "",
                "required": true,
                "getRequiredMessage": null
            },
            "putCode": null,
            "relationship": {
                "errors": [],
                "value": "self",
                "required": true,
                "getRequiredMessage": null
            }
        };
    };

    $scope.hideSources = function(group) {
        $scope.editSources[group.groupId] = false;
        group.activePutCode = group.defaultPutCode;
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

    $scope.putFunding = function(){
        if ($scope.addingFunding){    
            return; // don't process if adding funding
        } 
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
                } else {
                    $scope.editFunding = data;
                    if($scope.editFunding.externalIdentifiers.length == 0) {
                        $scope.addFundingExternalIdentifier();
                    }
                    commonSrvc.copyErrorsLeft($scope.editFunding, data);
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

    $scope.showAddModal = function(){
        $scope.editTranslatedTitle = false;
        $.colorbox({
            html: $compile($('#add-funding-modal').html())($scope),
            width: utilsService.formColorBoxResize(),
            onComplete: function() {
                //resize to insure content fits
                utilsService.formColorBoxResize();
                $scope.bindTypeaheadForOrgs();
                $scope.bindTypeaheadForSubTypes();
            },
            onClosed: function() {
                $scope.closeAllMoreInfo();
                fundingSrvc.getFundings('fundings/fundingIds.json');
            }
        });
    };

    $scope.showDetailsMouseClick = function(key, $event) {
        $event.stopPropagation();
        $scope.moreInfo[key] = !$scope.moreInfo[key];        
    };

    $scope.showSources = function(group) {
        $scope.editSources[group.groupId] = true;
    };

    $scope.sort = function(key) {
        $scope.sortState.sortBy(key);
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

    //Resizing window after error message is shown
    $scope.$watch('addingFunding', function() {
        setTimeout(
            function(){
                $.colorbox.resize();;
            }, 
            50
        );
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

    $scope.bindTypeaheadForOrgs = function () {
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
                if (datum.orgType != null && datum.orgType.trim() != ''){
                    forDisplay += ", " + datum.orgType;
                }
                forDisplay += '</span><hr />';

                return forDisplay;
            }
        });
        $("#fundingName").bind("typeahead:selected", function(obj, datum) {
            $scope.selectFunding(datum);
            $scope.$apply();
        });
    };

    $scope.bindTypeaheadForSubTypes = function() {
        var numOfResults = 20;
        $("#organizationDefinedType").typeahead({
            name: 'organizationDefinedType',
            limit: numOfResults,
            remote: {
                replace: function () {
                    var q = getBaseUri()+'/fundings/orgDefinedSubType/';
                    if ($('#organizationDefinedType').val()) {
                        q += encodeURIComponent($('#organizationDefinedType').val());
                    }
                    q += '?limit=' + numOfResults;
                    return q;
                }
            },
            template: function (datum) {
                var forDisplay =
                    '<span style=\'white-space: nowrap; font-weight: bold;\'>' + datum.value + '</span><hr />';
                return forDisplay;
            }
        });
        $("#organizationDefinedType").bind("typeahead:selected", function(obj, datum){
            $scope.selectOrgDefinedFundingSubType(datum);
            $scope.$apply();
        });
    };

    $scope.setSubTypeAsNotIndexed = function() {
        if($scope.lastIndexedTerm != $.trim($('#organizationDefinedType').val())) {
            console.log("value changed: " + $scope.lastIndexedTerm + " <-> " + $('#organizationDefinedType').val());
            $scope.editFunding.organizationDefinedFundingSubType.alreadyIndexed = false;
        }
    };

    $scope.selectOrgDefinedFundingSubType = function(subtype) {
        if (subtype != undefined && subtype != null) {
            $scope.editFunding.organizationDefinedFundingSubType.subtype.value = subtype.value;
            $scope.editFunding.organizationDefinedFundingSubType.alreadyIndexed = true;
            $scope.lastIndexedTerm = subtype.value;
            $scope.unbindTypeaheadForSubTypes();
        }
    };

    $scope.selectFunding = function(datum) {
        if (datum != undefined && datum != null) {
            $scope.editFunding.fundingName.value = datum.value;
            if(datum.value){
                $scope.editFunding.fundingName.errors = [];
            }
            $scope.editFunding.city.value = datum.city;
            if(datum.city){
                $scope.editFunding.city.errors = [];
            }
            $scope.editFunding.region.value = datum.region;

            if(datum.country != undefined && datum.country != null) {
                $scope.editFunding.country.value = datum.country;
                $scope.editFunding.country.errors = [];
            }

            if (datum.disambiguatedFundingIdentifier != undefined && datum.disambiguatedFundingIdentifier != null) {
                $scope.getDisambiguatedFunding(datum.disambiguatedFundingIdentifier);
                $scope.unbindTypeaheadForOrgs();
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

    $scope.deleteFundingConfirm = function(putCode, deleteGroup) {
        var funding = fundingSrvc.getFunding(putCode);
        var maxSize = 100;
        
        $scope.deletePutCode = putCode;
        $scope.deleteGroup = deleteGroup;
        
        if (funding.fundingTitle && funding.fundingTitle.title){
            $scope.fixedTitle = funding.fundingTitle.title.value;
        }
        else{
            $scope.fixedTitle = '';
        } 

        if($scope.fixedTitle.length > maxSize){
            $scope.fixedTitle = $scope.fixedTitle.substring(0, maxSize) + '...';
        }

        $.colorbox({
            html : $compile($('#delete-funding-modal').html())($scope),
            onComplete: function() {$.colorbox.resize();}
        });
    };

    $scope.deleteFundingByPut = function(putCode, deleteGroup) {
        if (deleteGroup){
            fundingSrvc.deleteGroupFunding(putCode);
        }
        else {
            fundingSrvc.deleteFunding(putCode);
        }
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
        $scope.bindTypeaheadForOrgs();
        if ($scope.disambiguatedFunding != undefined) {
            delete $scope.disambiguatedFunding;
        }
        if ($scope.editFunding != undefined && $scope.editFunding.disambiguatedFundingSourceId != undefined) {
            delete $scope.editFunding.disambiguatedFundingSourceId;
        }
    };

    $scope.isValidClass = function (cur) {
        var valid = true;

        if (cur === undefined){
            return '';
        } 
        if (cur.required && (cur.value == null || cur.value.trim() == '')) {
            valid = false;
        }
        if (cur.errors !== undefined && cur.errors.length > 0) {
            valid = false;
        }

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
                commonSrvc.copyErrorsLeft($scope.editFunding, data);
                $scope.$apply();
            }
        }).fail(function() {
            // something bad is happening!
            console.log("FundingCtrl.serverValidate() error");
        });
    };

    $scope.unbindTypeaheadForOrgs = function () {
        $('#fundingName').typeahead('destroy');
    };

    $scope.unbindTypeaheadForSubTypes = function () {
        $('#organizationDefinedType').typeahead('destroy');
    };

    $scope.addFundingExternalIdentifier = function () {
        $scope.editFunding.externalIdentifiers.push({type: {value: ""}, value: {value: ""}, url: {value: ""}, relationship: {value: "self"} });
    };

    $scope.deleteFundingExternalIdentifier = function(obj) {
        var index = $scope.editFunding.externalIdentifiers.indexOf(obj);
        $scope.editFunding.externalIdentifiers.splice(index,1);
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

    $scope.openEditFunding = function(putCode) {
        fundingSrvc.getEditable(putCode, function(bestMatch) {
            $scope.addFundingModal(bestMatch);
        });
    };    
    
    $scope.showFundingImportWizard =  function() {
        $scope.fundingImportWizard = !$scope.fundingImportWizard;               
    };
    
    $scope.toggleWizardDesc = function(id){
        $scope.wizardDescExpanded[id] = !$scope.wizardDescExpanded[id];
    }
    
    $scope.showTooltip = function (key){
        $scope.showElement[key] = true;
    };

    $scope.hideTooltip = function (key){
        $scope.showElement[key] = false;
    };
    
    $scope.userIsSource = function(funding) {
        if (funding.source == orcidVar.orcidId){
            return true;
        }
        return false;
    };
    
    $scope.hideURLPopOver = function(id){
        $scope.displayURLPopOver[id] = false;
    };
    
    $scope.showURLPopOver = function(id){
        $scope.displayURLPopOver[id] = true;
    };
    
    $scope.moreInfoActive = function(groupID){
        if ($scope.moreInfo[groupID] == true || $scope.moreInfo[groupID] != null) {
            return 'truncate-anchor';
        }
    }
    
}]);