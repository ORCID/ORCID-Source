angular.module('orcidApp').controller(
    'WorkCtrl', 
    [
        '$scope', 
        '$rootScope', 
        '$compile', 
        '$filter', 
        '$timeout', 
        '$q', 
        'actBulkSrvc', 
        'commonSrvc', 
        'emailSrvc', 
        'initialConfigService', 
        'utilsService', 
        'worksSrvc', 
        'workspaceSrvc',     
        function ($scope, $rootScope, $compile, $filter, $timeout, $q, actBulkSrvc, commonSrvc, emailSrvc, initialConfigService, utilsService, worksSrvc, workspaceSrvc ) {

            var utilsService = utilsService;

            actBulkSrvc.initScope($scope);
           
            $scope.badgesRequested = {};
            $scope.bibtexExportError = false;
            $scope.bibtexGenerated = false;
            $scope.bibtexParsingError = false;
            $scope.bibtexURL = "";
            $scope.bibtextWork = false;
            $scope.bibtextWorkIndex = null;
            $scope.bulkDeleteCount = 0;
            $scope.bulkDeleteSubmit = false;
            $scope.canReadFiles = false;
            $scope.combiningWorks = false;
            $scope.contentCopy = {
                titleLabel: om.get("orcid.frontend.manual_work_form_contents.defaultTitle"),
                titlePlaceholder: om.get("orcid.frontend.manual_work_form_contents.defaultTitlePlaceholder")
            };
            $scope.delCountVerify = 0;
            $scope.displayURLPopOver = {};
            $scope.editSources = {};
            $scope.editTranslatedTitle = false;
            $scope.emailSrvc = emailSrvc;
            $scope.generatingBibtex = false;
            $scope.geoArea = ['All'];
            $scope.moreInfo = {};
            $scope.moreInfoOpen = false;
            $scope.noLinkFlag = true;
            $scope.privacyHelp = {};
            $scope.scriptsLoaded = false;
            $scope.showBibtex = {};
            $scope.showBibtexExport = false;
            $scope.showBibtexImportWizard = false;
            $scope.showElement = {};
            $scope.sortState = new ActSortState(GroupedActivities.ABBR_WORK);
            $scope.textFiles = [];
            $scope.types = null;
            $scope.wizardDescExpanded = {};
            $scope.workImportWizard = false;
            $scope.worksFromBibtex = null;
            $scope.workspaceSrvc = workspaceSrvc;
            $scope.worksSrvc = worksSrvc;
            $scope.workType = ['All'];

            /////////////////////// Begin of verified email logic for work
            var configuration = initialConfigService.getInitialConfiguration();
            var emailVerified = false;
            var emails = {};

            var showEmailVerificationModal = function(){
                $rootScope.$broadcast('emailVerifiedObj', {flag: emailVerified, emails: emails});
            };
            
            // Check for the various File API support.
            if (window.File && window.FileReader && window.FileList && window.Blob) {
                $scope.canReadFiles = true;
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

            $scope.applyLabelWorkType = function() {
                var obj = null;
                $timeout(
                    function() {
                        obj = $scope.worksSrvc.getLabelMapping($scope.editWork.workCategory.value, $scope.editWork.workType.value)
                        $scope.contentCopy = obj;
                    }, 
                    100
                );
            };

            $scope.sort = function(key) {
                $scope.sortState.sortBy(key);
            };

            $scope.toggleBulkEdit = function() {

                if(emailVerified === true || configuration.showModalManualEditVerificationEnabled == false){
                    if (!$scope.bulkEditShow) {
                        $scope.bulkEditMap = {};
                        $scope.bulkChecked = false;
                        for (var idx in worksSrvc.groups){
                            $scope.bulkEditMap[worksSrvc.groups[idx].getActive().putCode.value] = false;
                        }
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
                for (var idx in worksSrvc.groups) {
                    if ($scope.bulkEditMap[worksSrvc.groups[idx].getActive().putCode.value]){
                        func(worksSrvc.groups[idx].getActive().putCode.value);
                    }
                }
            };

            $scope.swapbulkChangeAll = function() {
                $scope.bulkChecked = !$scope.bulkChecked;
                for (var idx in worksSrvc.groups){
                    $scope.bulkEditMap[worksSrvc.groups[idx].getActive().putCode.value] = $scope.bulkChecked;
                }
                $scope.bulkDisplayToggle = false;
            };

            $scope.bulkChangeAll = function(bool) {
                $scope.bulkChecked = bool;
                $scope.bulkDisplayToggle = false;
                for (var idx in worksSrvc.groups){
                    $scope.bulkEditMap[worksSrvc.groups[idx].getActive().putCode.value] = bool;
                }
            };

            $scope.setBulkGroupPrivacy = function(priv) {
                var putCodes = new Array();
                for (var idx in worksSrvc.groups){
                    if ($scope.bulkEditMap[worksSrvc.groups[idx].getActive().putCode.value]){    
                        for (var idj in worksSrvc.groups[idx].activities) {
                            putCodes.push(worksSrvc.groups[idx].activities[idj].putCode.value);
                            worksSrvc.groups[idx].activities[idj].visibility = priv;
                        }
                    }
                }
                worksSrvc.updateVisibility(putCodes, priv);
            };

            $scope.deleteBulk = function () {
                if ($scope.delCountVerify != parseInt($scope.bulkDeleteCount)) {
                    $scope.bulkDeleteSubmit = true;
                    return;
                }
                var delPuts = new Array();
                for (var idx in worksSrvc.groups){
                    if ($scope.bulkEditMap[worksSrvc.groups[idx].getActive().putCode.value]){
                        delPuts.push(worksSrvc.groups[idx].getActive().putCode.value);
                    }
                }
                worksSrvc.deleteGroupWorks(delPuts);
                $.colorbox.close();
                $scope.bulkEditShow = false;
            };


            $scope.deleteBulkConfirm = function(idx) {
                $scope.bulkDeleteCount = 0;
                $scope.bulkDeleteSubmit = false;        
                $scope.delCountVerify = 0;
                for (var idx in worksSrvc.groups){
                    if ($scope.bulkEditMap[worksSrvc.groups[idx].getActive().putCode.value]){
                        $scope.bulkDeleteCount++;
                    }
                }

                $scope.bulkDeleteFunction = $scope.deleteBulk;

                $.colorbox({
                    html: $compile($('#bulk-delete-modal').html())($scope)
                });
                $.colorbox.resize();
            };

            $scope.sortOtherLast = function(type) {
                if (type.key == 'other'){    
                    return 'ZZZZZ';
                } 
                return type.value;
            };

            $scope.loadBibtexJs = function() {
                try {
                    $scope.worksFromBibtex = new Array();
                    $.each(
                        $scope.textFiles, 
                        function (index, bibtex) {
                            var parsed = bibtexParse.toJSON(bibtex);
                            if (parsed.length == 0) {
                                throw "bibtex parse return nothing";
                            }
                            worksSrvc.getBlankWork(
                                function(blankWork) {
                                    var newWorks = new Array();
                                    while (parsed.length > 0) {
                                        var cur = parsed.shift();
                                        var bibtexEntry = cur.entryType.toLowerCase();
                                        if (bibtexEntry != 'preamble' && bibtexEntry != 'comment') {    
                                            //Filtering @PREAMBLE and @COMMENT
                                            newWorks.push( populateWorkAjaxForm( cur,JSON.parse( JSON.stringify(blankWork) ) ) );
                                        }
                                    };
                                    worksSrvc.worksValidate(
                                        newWorks, 
                                        function(data) {
                                            for (var i in data) {                           
                                                $scope.worksFromBibtex.push(data[i]);
                                            }
                                            $scope.$apply();
                                        }
                                    );
                                }
                            );
                        }
                    );
                    $scope.textFiles.length = 0;
                    $scope.bibtexParsingError = false;
                       
                } catch (err) {
                    $scope.bibtexParsingError = true;
                };
            };

            $scope.rmWorkFromBibtex = function(work) {
                var index = $scope.worksFromBibtex.indexOf(work);
                
                $scope.worksFromBibtex.splice(index, 1);
            };

            $scope.editWorkFromBibtex = function(work) {
                $scope.bibtextWork = true;
                $scope.bibtextWorkIndex = $scope.worksFromBibtex.indexOf(work);
                
                $scope.addWorkModal($scope.worksFromBibtex[$scope.bibtextWorkIndex]);        
            };
            
            $scope.addWorkFromBibtex = function(work) {
                $scope.bibtextWork = true;              
                $scope.bibtextWorkIndex = $scope.worksFromBibtex.indexOf(work);     
                $scope.editWork = $scope.worksFromBibtex[$scope.bibtextWorkIndex];
                
                $scope.putWork();        
            };
    
            $scope.saveAllFromBibtex = function(){
                var warksToSave =  new Array();
                angular.forEach($scope.worksFromBibtex, function( work, key ) {
                    if (work.errors.length == 0){
                        warksToSave.push(work);
                    } 
                });
                var numToSave = warksToSave.length;
                angular.forEach( warksToSave, function( work, key ) {
                    worksSrvc.putWork(work,function(data) {
                        index = $scope.worksFromBibtex.indexOf(work);
                        $scope.worksFromBibtex.splice(index, 1);
                        $scope.$apply();
                        numToSave--;
                        if (numToSave == 0){
                            $scope.worksSrvc.loadAbbrWorks(worksSrvc.constants.access_type.USER);
                        }
                    });
                });
            };

            $scope.openBibTextWizard = function () {
                if(emailVerified === true || configuration.showModalManualEditVerificationEnabled == false){
                    $scope.bibtexParsingError = false;
                    $scope.bulkEditShow = false;
                    $scope.showBibtexExport = false;
                    $scope.showBibtexImportWizard = !($scope.showBibtexImportWizard);
                    $scope.workImportWizard = false;
                    $scope.worksFromBibtex = null;
                }else{
                    showEmailVerificationModal();
                }
            };

            $scope.bibtextCancel = function(){
                $scope.worksFromBibtex = null;
            };    

            $scope.toggleClickPrivacyHelp = function(key) {
                if (!document.documentElement.className.contains('no-touch')){
                    $scope.privacyHelp[key]=!$scope.privacyHelp[key];
                }
            };

            $scope.addExternalIdentifier = function () {
                $scope.editWork.workExternalIdentifiers.push({workExternalIdentifierId: {value: ""}, workExternalIdentifierType: {value: ""}, relationship: {value: "self"}, url: {value: ""}});
            };

            $scope.deleteExternalIdentifier = function(obj) {
                var index = $scope.editWork.workExternalIdentifiers.indexOf(obj);
                
                $scope.editWork.workExternalIdentifiers.splice(index,1);
            };

            $scope.deleteContributor = function(obj) {
                var index = $scope.editWork.contributors.indexOf(obj);
                
                $scope.editWork.contributors.splice(index,1);
            };

            $scope.userIsSource = function(work) {
                if (work.source == orcidVar.orcidId){
                    return true;
                }
                return false;
            };

            $scope.hasCombineableEIs = function(work) {
                if (work.workExternalIdentifiers != null){
                    for (var idx in work.workExternalIdentifiers){
                        if (work.workExternalIdentifiers[idx].workExternalIdentifierType.value != 'issn'){
                            return true;
                        }
                    }
                }
                return false;
            };

            $scope.canBeCombined = function(work) {
                if ($scope.userIsSource(work)){
                    return true;
                }
                return $scope.hasCombineableEIs(work);
            };

            $scope.validCombineSel = function(selectedWork,work) {
                if ($scope.hasCombineableEIs(selectedWork)){
                    return $scope.userIsSource(work) || $scope.hasCombineableEIs(work);
                }
                else{
                    return $scope.hasCombineableEIs(work);
                }
            };


            $scope.combined = function(work1, work2) {
                // no duplicate request;
                var putWork;
                if ($scope.combiningWorks){
                    return;
                }
                $scope.combiningWorks = true;
                
                if ($scope.userIsSource(work1)) {
                    putWork = worksSrvc.copyEIs(work2, work1);
                } else if ($scope.userIsSource(work2)) {
                    putWork = worksSrvc.copyEIs(work1, work2);
                } else {
                    putWork = worksSrvc.createNew(work1);
                    putWork = worksSrvc.copyEIs(work1, work2);
                }
                worksSrvc.putWork(
                    putWork,
                    function(data){
                        $scope.combiningWorks = false;
                        $scope.closeModal();
                    },
                    function() {
                        $scope.combiningWorks = false;
                    }
                );
            };

            $scope.showCombineMatches = function( work1 ) {
                $scope.combineWork = work1;
                $.colorbox({
                    scrolling: true,
                    html: $compile($('#combine-work-template').html())($scope),
                    onLoad: function() {$('#cboxClose').remove();},
                    // start the colorbox off with the correct width
                    width: utilsService.formColorBoxResize(),
                    onComplete: function() {$.colorbox.resize();},
                    onClosed: function() {
                        $scope.closeAllMoreInfo();
                        $scope.worksSrvc.loadAbbrWorks(worksSrvc.constants.access_type.USER);
                    }
                });
                return false;
            };

            $scope.showAddWorkModal = function(){
                $scope.editTranslatedTitle = false;
                $.colorbox({
                    scrolling: true,
                    html: $compile($('#add-work-modal').html())($scope),
                    onLoad: function() {$('#cboxClose').remove();},
                    // start the colorbox off with the correct width
                    width: utilsService.formColorBoxResize(),
                    onComplete: function() {
                        //resize to insure content fits
                    },
                    onClosed: function() {
                        $scope.closeAllMoreInfo();
                        $scope.worksSrvc.loadAbbrWorks(worksSrvc.constants.access_type.USER);
                    }
                });
            };

            $scope.toggleTranslatedTitleModal = function(){
                $scope.editTranslatedTitle = !$scope.editTranslatedTitle;
                $('#translatedTitle').toggle();
                $.colorbox.resize();
            };

            $scope.bibtexShowToggle = function (putCode) {
                $scope.showBibtex[putCode] = !($scope.showBibtex[putCode]);
            };

            $scope.showWorkImportWizard =  function() {
                if(!$scope.workImportWizard) {
                    loadWorkImportWizardList();
                }
                $scope.workImportWizard = !$scope.workImportWizard;
            };   

            function loadWorkImportWizardList() {
                $.ajax({
                    url: getBaseUri() + '/workspace/retrieve-work-impor-wizards.json',
                    type: 'GET',
                    contentType: 'application/json;charset=UTF-8',
                    dataType: 'json',
                    success: function(data) {
                        if(data == null || data.length == 0) {
                            $scope.noLinkFlag = false;
                        }
                        
                        $scope.selectedWorkType = 'Articles';
                        $scope.selectedGeoArea = 'Global';
                        $scope.workImportWizardsOriginal = data;
                        $scope.bulkEditShow = false;
                        $scope.showBibtexImportWizard = false;
                        for(var i = 0; i < $scope.workImportWizardsOriginal.length; i ++) {
                            for(var j = 0; j < $scope.workImportWizardsOriginal[i].redirectUris.redirectUri.length; j ++) {
                                $scope.workImportWizardsOriginal[i].redirectUris.redirectUri[j].actType =  JSON.parse($scope.workImportWizardsOriginal[i].redirectUris.redirectUri[j].actType);
                                $scope.workImportWizardsOriginal[i].redirectUris.redirectUri[j].geoArea =  JSON.parse($scope.workImportWizardsOriginal[i].redirectUris.redirectUri[j].geoArea);
                                for(var k = 0; k < $scope.workImportWizardsOriginal[i].redirectUris.redirectUri[j].actType['import-works-wizard'].length; k ++) {
                                    if(!utilsService.contains($scope.workType, $scope.workImportWizardsOriginal[i].redirectUris.redirectUri[j].actType['import-works-wizard'][k])){
                                        $scope.workType.push($scope.workImportWizardsOriginal[i].redirectUris.redirectUri[j].actType['import-works-wizard'][k]);
                                    }
                                }
                                
                                for(var k = 0; k < $scope.workImportWizardsOriginal[i].redirectUris.redirectUri[j].geoArea['import-works-wizard'].length; k ++) {
                                    if(!utilsService.contains($scope.geoArea, $scope.workImportWizardsOriginal[i].redirectUris.redirectUri[j].geoArea['import-works-wizard'][k])){
                                        $scope.geoArea.push($scope.workImportWizardsOriginal[i].redirectUris.redirectUri[j].geoArea['import-works-wizard'][k]);
                                    }
                                }
                            }
                        }               
                        if( utilsService.getParameterByName('import_works_wizard') != 'true' ) {
                            $scope.selectedWorkType = 'All';
                            $scope.selectedGeoArea = 'All';
                        }
                        $scope.$apply();
                    }
                }).fail(function(e) {
                    // something bad is happening!
                    console.log("WorkImportWizardError");
                    logAjaxError(e);
                });
            }
    
            $scope.addWorkModal = function(data){
                if(emailVerified === true || configuration.showModalManualEditVerificationEnabled == false){
                    if (data == undefined) {
                        worksSrvc.getBlankWork(function(data) {
                            $scope.editWork = data;
                            $scope.$apply(function() {
                                $scope.loadWorkTypes();
                                $scope.showAddWorkModal();
                            });
                        });
                    } else {
                        $scope.editWork = data;            
                        $scope.loadWorkTypes();
                        $scope.showAddWorkModal();
                    }
                } else {
                    showEmailVerificationModal();
                }
            };

            $scope.openEditWork = function(putCode){
                worksSrvc.getEditable(putCode, function(data) {$scope.addWorkModal(data);});
            };       

            $scope.putWork = function(){
                if(emailVerified === true || configuration.showModalManualEditVerificationEnabled == false){
                    if ($scope.addingWork) {
                        return; // don't process if adding work
                    }
                    $scope.addingWork = true;
                    $scope.editWork.errors.length = 0;
                    worksSrvc.putWork($scope.editWork,
                        function(data){
                            if (data.errors.length == 0) {
                                if ($scope.bibtextWork == false){
                                    $.colorbox.close();
                                    $scope.addingWork = false;
                                } else {
                                    $scope.worksFromBibtex.splice($scope.bibtextWorkIndex, 1);
                                    $scope.bibtextWork = false;
                                    $scope.addingWork = false;
                                    $scope.$apply();
                                    $.colorbox.close();
                                    $scope.worksSrvc.loadAbbrWorks(worksSrvc.constants.access_type.USER);
                                }
                            } else {
                                $scope.editWork = data;                    
                                commonSrvc.copyErrorsLeft($scope.editWork, data);
                                
                                $scope.addingWork = false;
                                $scope.$apply();
                                // make sure colorbox is shown if there are errors
                                if (!($("#colorbox").css("display")=="block")) {
                                    $scope.addWorkModal(data);
                                }
                            }
                        },
                        function() {
                            // something bad is happening!
                            $scope.addingWork = false;
                        }
                    );
                } else {
                    showEmailVerificationModal();
                }
            };

            $scope.closeAllMoreInfo = function() {
                for (var idx in $scope.moreInfo){
                    $scope.moreInfo[idx]=false;
                }
            };

            $scope.validateCitation = function() {
                if ($scope.editWork.citation
                        && $scope.editWork.citation.citation.value
                        && $scope.editWork.citation.citation.value.length > 0
                        && $scope.editWork.citation.citationType.value == 'bibtex') {
                    try {
                        var parsed = bibtexParse.toJSON($scope.editWork.citation.citation.value);
                        var index = $scope.editWork.citation.citation.errors.indexOf(om.get('manualWork.bibtext.notValid'));
                        if (parsed.length == 0){
                            throw "bibtex parse return nothing";
                        } 
                        if (index > -1) {
                            $scope.editWork.citation.citation.errors.splice(index, 1);
                        }
                    } catch (err) {
                        $scope.editWork.citation.citation.errors.push(om.get('manualWork.bibtext.notValid'));
                    };
                };
            };

            $scope.renderTranslatedTitleInfo = function(putCode) {
                var info = null;

                if(putCode != null && $scope.worksSrvc.details[putCode] != null && $scope.worksSrvc.details[putCode].translatedTitle != null) {
                    info = $scope.worksSrvc.details[putCode].translatedTitle.content + ' - ' + $scope.worksSrvc.details[putCode].translatedTitle.languageName;
                }

                return info;
            };

            $scope.loadWorkTypes = function(){
                var workCategory = "";
                if($scope.editWork != null && $scope.editWork.workCategory != null && $scope.editWork.workCategory.value != null && $scope.editWork.workCategory.value != ""){
                    workCategory = $scope.editWork.workCategory.value;
                }
                else{
                    return; //do nothing if we have not types
                }
                $.ajax({
                    url: getBaseUri() + '/works/loadWorkTypes.json?workCategory=' + workCategory,
                    contentType: 'application/json;charset=UTF-8',
                    dataType: 'json',
                    success: function(data) {
                        $scope.$apply(function() {
                            $scope.types = data;
                            if($scope.editWork != null && $scope.editWork.workCategory != null) {
                                // if the edit works doesn't have a value that matches types
                                var hasType = false;
                                for (var idx in $scope.types){
                                    if ($scope.types[idx].key == $scope.editWork.workType.value) hasType = true;
                                }
                                if(!hasType) {
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
                                }
                            }
                        });
                    }
                }).fail(function() {
                    console.log("Error loading work types.");
                });
            };

            //--typeahead
            //populates the external id URL based on type and value.
            $scope.fillUrl = function(extId) {
                var url;
                if(extId != null) {
                    url = workIdLinkJs.getLink(extId.workExternalIdentifierId.value, extId.workExternalIdentifierType.value);
                    /* Code to fetch from DB...
                    if (extId.workExternalIdentifierType.value){
                        url = $scope.externalIDNamesToDescriptions[extId.workExternalIdentifierType.value].resolutionPrefix;
                        if (url && extId.workExternalIdentifierId.value)
                            url += extId.workExternalIdentifierId.value;
                    }*/
                    if(extId.url == null) {
                        extId.url = {value:url};
                    }else{
                        extId.url.value=url;                        
                    }
                }
            };
            
            //cache responses
            $scope.externalIDTypeCache = [];
            
            //Fetches an array of {name:"",description:"",resolutionPrefix:""} containing query.
            $scope.getExternalIDTypes = function(query){  
                var url = getBaseUri()+'/works/idTypes.json?query='+query;
                var ajax = $scope.externalIDTypeCache[query];
                if (!ajax){
                    ajax = $.ajax({
                        url: url,
                        dataType: 'json',
                        cache: true,
                      }).done(function(data) {
                          for (var key in data) {
                              $scope.externalIDNamesToDescriptions[data[key].name] = data[key];
                          }
                      });   
                    $scope.externalIDTypeCache[query] = ajax;
                }
                return ajax;
            };
            
            //caches name->description lookup so we can display the description not the name after selection
            $scope.externalIDNamesToDescriptions = [];
            $scope.formatExternalIDType = function(model) {
                if (!model)
                    return "";
                return $scope.externalIDNamesToDescriptions[model].description;
              }
            //--typeahead end
    
            //init
            $scope.worksSrvc.loadAbbrWorks(worksSrvc.constants.access_type.USER);
            loadWorkImportWizardList();

            // remove once grouping is live
            $scope.moreInfoClick = function(work, $event) {
                if (!document.documentElement.className.contains('no-touch')){
                    $scope.moreInfoOpen?$scope.closePopover():$scope.loadWorkInfo(work.putCode.value, $event);
                }
            };

            // remove once grouping is live
            $scope.moreInfoMouseEnter = function(work, $event) {
                $event.stopPropagation();
                if (document.documentElement.className.contains('no-touch')){
                    $scope.loadWorkInfo(work.putCode.value, $event);
                }
                else{
                    $scope.moreInfoOpen?$scope.closePopover():$scope.loadWorkInfo(work.putCode.value, $event);
                }
            };

            $scope.showDetailsMouseClick = function(group, $event) {
                $event.stopPropagation();
                $scope.moreInfo[group.groupId] = !$scope.moreInfo[group.groupId];
                for (var idx in group.activities){
                    $scope.loadDetails(group.activities[idx].putCode.value, $event);
                }
            };

            $scope.hideSources = function(group) {
                $scope.editSources[group.groupId] = false;
                group.activePutCode = group.defaultPutCode;
            };

            $scope.showSources = function(group) {
                $scope.editSources[group.groupId] = true;
            };

            $scope.loadDetails = function(putCode, event) {
                //Close any open popover
                $scope.closePopover(event);
                $scope.moreInfoOpen = true;
                //Display the popover
                $(event.target).next().css('display','inline');
                $scope.worksSrvc.getGroupDetails(putCode, worksSrvc.constants.access_type.USER);
            };


            $scope.loadWorkInfo = function(putCode, event) {
                //Close any open popover
                $scope.closePopover(event);
                $scope.moreInfoOpen = true;
                //Display the popover
                $(event.target).next().css('display','inline');
                if($scope.worksSrvc.details[putCode] == null) {
                    $scope.worksSrvc.getGroupDetails(putCode, worksSrvc.constants.access_type.USER);
                } else {
                    $(event.target).next().css('display','inline');
                }
            };

            $scope.closePopover = function(event) {
                $scope.moreInfoOpen = false;
                $('.work-more-info-container').css('display', 'none');
            };

            $scope.deleteWorkConfirm = function(putCode, deleteGroup) {
                var maxSize = 100;
                var work = worksSrvc.getWork(putCode);
                $scope.deletePutCode = putCode;
                $scope.deleteGroup = deleteGroup;
                if (work.title){
                    $scope.fixedTitle = work.title.value;
                }
                else {  
                    $scope.fixedTitle = '';
                } 
                if($scope.fixedTitle.length > maxSize){
                    $scope.fixedTitle = $scope.fixedTitle.substring(0, maxSize) + '...';
                }
                $.colorbox({
                    html : $compile($('#delete-work-modal').html())($scope),
                    onComplete: function() {$.colorbox.resize();}
                });
            };

            $scope.deleteByPutCode = function(putCode, deleteGroup) {
                if (deleteGroup) {
                   worksSrvc.deleteGroupWorks(putCode);
                }
                else {
                   worksSrvc.deleteWork(putCode);
                }
                $.colorbox.close();
            };

            $scope.closeModal = function() {
                $.colorbox.close();
            };

            $scope.openImportWizardUrl = function(url) {
                openImportWizardUrl(url);
            };
    
            $scope.openImportWizardUrlFilter = function(url, param) {
                url = url + '?client_id='+param.clientId+'&response_type=code&scope='+param.redirectUris.redirectUri[0].scopeAsSingleString+'&redirect_uri='+param.redirectUris.redirectUri[0].value;
                openImportWizardUrl(url);
            };

            $scope.setAddWorkPrivacy = function(priv, $event) {
                $event.preventDefault();
                $scope.editWork.visibility = priv;
            };

            $scope.serverValidate = function (relativePath) {
                $.ajax({
                    url: getBaseUri() + '/' + relativePath,
                    type: 'POST',
                    data:  angular.toJson($scope.editWork),
                    contentType: 'application/json;charset=UTF-8',
                    dataType: 'json',
                    success: function(data) {
                        commonSrvc.copyErrorsLeft($scope.editWork, data);
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

            $scope.isValidClass = function (cur) {
                var valid = true;
                if (cur === undefined || cur == null) {
                    return '';
                }
                if ( ( cur.required && (cur.value == null || cur.value.trim() == '') ) || ( cur.errors !== undefined && cur.errors.length > 0 ) ){
                    valid = false;
                }
                return valid ? '' : 'text-error';
            };

            $scope.clearErrors = function() {
                $scope.editWork.workCategory.errors = [];
                $scope.editWork.workType.errors = [];
            };
    
            $scope.showTooltip = function (key){        
                $scope.showElement[key] = true;     
            };
    
            $scope.hideTooltip = function (key){        
                $scope.showElement[key] = false;
            };
    
            $scope.openFileDialog = function(){
                $scope.textFiles = [];
                $scope.bibtexParsingError = false;
                $timeout(
                    function() { //To avoid '$apply already in progress' error
                        angular.element('#inputBibtex').trigger('click');
                    }, 
                    0
                );
            };
    
            $scope.toggleWizardDesc = function(id){
                $scope.wizardDescExpanded[id] = !$scope.wizardDescExpanded[id];
            };
            
            $scope.showURLPopOver = function(id){       
                $scope.displayURLPopOver[id] = true;
            }
            
            $scope.hideURLPopOver = function(id){       
                $scope.displayURLPopOver[id] = false;
            }
    
            $scope.showMozillaBadges = function(putCode){
                $scope.$watch(
                    function () { 
                        return document.getElementsByClassName('badge-container-' + putCode).length; 
                    },
                    function (newValue, oldValue) {
                          if (newValue !== oldValue) {
                              if ($scope.badgesRequested[putCode] == null){
                                var dois = worksSrvc.getUniqueDois(putCode);
                                var c = document.getElementsByClassName('badge-container-' + putCode);
                                for (var i = 0; i <= dois.length - 1; i++){
                                    var code = 'var conf={"article-doi": "' + dois[i].trim() + '", "container-class": "badge-container-' + putCode + '"};showBadges(conf);';
                                    var s = document.createElement('script');
                                    s.type = 'text/javascript';
                                    try {
                                        s.appendChild(document.createTextNode(code));
                                        c[0].appendChild(s);
                                    } catch (e) {
                                        s.text = code;
                                        c[0].appendChild(s);
                                    }
                                }
                                $scope.badgesRequested[putCode] = true;
                            }
                        }
                    }
                );
            };
    
            $scope.toggleBibtexExport = function(){
                $scope.bibtexExportError = false;
                $scope.bibtexLoading = false;
                $scope.bibtexParsingError = false;
                $scope.bulkEditShow = false;        
                $scope.loadingScripts = false;
                $scope.scriptsLoaded = false;
                $scope.showBibtexExport  = !$scope.showBibtexExport;
                $scope.showBibtexImportWizard = false;
                $scope.workImportWizard = false;
            }


            $scope.fetchBibtexExport = function(){
                $scope.bibtexLoading = true;
                $scope.bibtexExportError = false; 
                
                $.ajax({
                    url: getBaseUri() + '/' + 'works/works.bib',
                    type: 'GET',
                    success: function(data) {
                        $scope.bibtexLoading = false;
                        if(window.navigator.msSaveOrOpenBlob) {
                            var fileData = [data];
                            blobObject = new Blob(fileData, {type: 'text/plain'});
                            window.navigator.msSaveOrOpenBlob(blobObject, "works.bib");                              
                        } else {
                            var anchor = angular.element('<a/>');
                            anchor.css({display: 'none'});
                            angular.element(document.body).append(anchor);
                            anchor.attr({
                                href: 'data:text/x-bibtex;charset=utf-8,' + encodeURIComponent(data),
                                target: '_self',
                                download: 'works.bib'
                            })[0].click();
                            anchor.remove();
                        }
                    }
                }).fail(function() {
                    $scope.bibtexExportError = true;
                    console.log("bibtex export error");
                });        
            };
        }
    ]
);