//Migrated

declare var $: any;
declare var om: any;
declare var ActSortState: any;
declare var GroupedActivities: any;
declare var colorbox: any;
declare var orcidVar: any;
declare var getBaseUri: any;
declare var logAjaxError: any;
declare var workIdLinkJs: any;

declare var bibtexParse: any;
declare var populateWorkAjaxForm: any;
declare var index: any;
declare var ajax: any;
declare var openImportWizardUrl: any;
declare var blobObject: any;

import * as angular from 'angular';
import {NgModule} from '@angular/core';

// This is the Angular 1 part of the module
export const WorkCtrl = angular.module('orcidApp').controller(
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
        'featuresService', 
        'initialConfigService', 
        'utilsService', 
        'worksSrvc', 
        'workspaceSrvc',     
        function ($scope, $rootScope, $compile, $filter, $timeout, $q, actBulkSrvc, commonSrvc, emailSrvc, featuresService, initialConfigService, utilsService, worksSrvc, workspaceSrvc ) {

            var savingBibtex = false;
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
            $scope.exIdResolverFeatureEnabled = featuresService.isFeatureEnabled('EX_ID_RESOLVER');
            $scope.externalIDNamesToDescriptions = [];//caches name->description lookup so we can display the description not the name after selection
            $scope.externalIDTypeCache = [];//cache responses
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
            if ((<any>window).File != undefined && (<any>window).FileReader != undefined  && (<any>window).FileList != undefined  && (<any>window).Blob) {
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

            $scope.addExternalIdentifier = function () {
                $scope.editWork.workExternalIdentifiers.push({workExternalIdentifierId: {value: ""}, workExternalIdentifierType: {value: ""}, relationship: {value: "self"}, url: {value: ""}});
            };

            $scope.addWorkFromBibtex = function(work) {
                $scope.bibtextWork = true;              
                $scope.bibtextWorkIndex = $scope.worksFromBibtex.indexOf(work);     
                $scope.editWork = $scope.worksFromBibtex[$scope.bibtextWorkIndex];
                
                $scope.putWork();        
            };

            $scope.addWorkModal = function(data){
                if(emailVerified === true 
                    || configuration.showModalManualEditVerificationEnabled == false
                ){
                    if (data == undefined) {
                        worksSrvc.getBlankWork(function(data) {
                            $scope.editWork = data;
                            $timeout(function(){
                                $scope.loadWorkTypes();
                                $scope.showAddWorkModal();
                            });
                        });
                    } else {
                        $scope.editWork = data;
                        if( $scope.editWork.workExternalIdentifiers.length == 0 ){
                            $scope.addExternalIdentifier();
                        }        
                        $scope.loadWorkTypes();
                        $scope.showAddWorkModal();
                    }
                } else {
                    showEmailVerificationModal();
                }
            };

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

            $scope.bibtextCancel = function(){
                $scope.worksFromBibtex = null;
            };

            $scope.bibtexShowToggle = function (putCode) {
                $scope.showBibtex[putCode] = !($scope.showBibtex[putCode]);
            };

            $scope.bulkApply = function(func) {
                for (var idx in worksSrvc.groups) {
                    if ($scope.bulkEditMap[worksSrvc.groups[idx].activePutCode]){
                        func(worksSrvc.groups[idx].activePutCode);
                    }
                }
            };

            $scope.bulkChangeAll = function(bool) {
                $scope.bulkChecked = bool;
                $scope.bulkDisplayToggle = false;
                for (var idx in worksSrvc.groups){
                    $scope.bulkEditMap[worksSrvc.groups[idx].activePutCode] = bool;
                }
            };

            $scope.canBeCombined = function(work) {
                if ($scope.userIsSource(work)){
                    return true;
                }
                return $scope.hasCombineableEIs(work);
            };

            $scope.clearErrors = function() {
                $scope.editWork.workCategory.errors = [];
                $scope.editWork.workType.errors = [];
            };

            $scope.closeAllMoreInfo = function() {
                for (var idx in $scope.moreInfo){
                    $scope.moreInfo[idx]=false;
                }
            };

            $scope.closeModal = function() {
                $.colorbox.close();
            };

            $scope.closePopover = function(event) {
                $scope.moreInfoOpen = false;
                $('.work-more-info-container').css('display', 'none');
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

            $scope.deleteBulk = function () {
                if ($scope.delCountVerify != parseInt($scope.bulkDeleteCount)) {
                    $scope.bulkDeleteSubmit = true;
                    return;
                }
                var delPuts = new Array();
                for (var idx in worksSrvc.groups){
                    if ($scope.bulkEditMap[worksSrvc.groups[idx].activePutCode]){
                        delPuts.push(worksSrvc.groups[idx].activePutCode);
                    }
                }
                worksSrvc.deleteGroupWorks(delPuts, $scope.sortState.predicateKey, !$scope.sortState.reverseKey[$scope.sortState.predicateKey]);
                $.colorbox.close();
                $scope.bulkEditShow = false;
            };

            $scope.deleteBulkConfirm = function(idx) {
                var idx: any;
                $scope.bulkDeleteCount = 0;
                $scope.bulkDeleteSubmit = false;        
                $scope.delCountVerify = 0;
                for (idx in worksSrvc.groups){
                    if ($scope.bulkEditMap[worksSrvc.groups[idx].activePutCode]){
                        $scope.bulkDeleteCount++;
                    }
                }

                $scope.bulkDeleteFunction = $scope.deleteBulk;

                $.colorbox({
                    html: $compile($('#bulk-delete-modal').html())($scope)
                });
                $.colorbox.resize();
            };

            $scope.deleteByPutCode = function(putCode, deleteGroup) {
                $scope.closeAllMoreInfo();
                if (deleteGroup) {
                   worksSrvc.deleteGroupWorks(putCode, $scope.sortState.predicateKey, !$scope.sortState.reverseKey[$scope.sortState.predicateKey]);
                }
                else {
                   worksSrvc.deleteWork(putCode, $scope.sortState.predicateKey, !$scope.sortState.reverseKey[$scope.sortState.predicateKey]);
                }
                $.colorbox.close();
            };

            $scope.deleteContributor = function(obj) {
                var index = $scope.editWork.contributors.indexOf(obj);
                
                $scope.editWork.contributors.splice(index,1);
            };

            $scope.deleteExternalIdentifier = function(obj) {
                var index = $scope.editWork.workExternalIdentifiers.indexOf(obj);
                
                $scope.editWork.workExternalIdentifiers.splice(index,1);
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

            $scope.editWorkFromBibtex = function(work) {
                $scope.bibtextWork = true;
                $scope.bibtextWorkIndex = $scope.worksFromBibtex.indexOf(work);
                
                $scope.addWorkModal($scope.worksFromBibtex[$scope.bibtextWorkIndex]);        
            };

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

            $scope.changeIdType = function(extId){
                if ($scope.exIdResolverFeatureEnabled == true){
                    if(extId.url == null) {
                        extId.url = {value:""};
                    }else{
                        extId.url.value="";                        
                    }
                }
                $scope.fillUrl(extId);
            }
            
            //--typeahead
            //populates the external id URL based on type and value.
            $scope.fillUrl = function(extId) {
                //if we have a value and type, generate URL.  If no URL, but attempted resolution, show warning.
                if ($scope.exIdResolverFeatureEnabled == true){
                    if (extId && extId.workExternalIdentifierId.value && extId.workExternalIdentifierType.value){
                        $timeout(function() {
                            extId.resolvingId = true;
                            $.ajax({
                                url: getBaseUri() + '/works/id/'+extId.workExternalIdentifierType.value,
                                type: 'GET',
                                data:{value:extId.workExternalIdentifierId.value},
                                success: function(data) {
                                    extId.workExternalIdentifierId.errors = [];
                                    if (data.generatedUrl){
                                        if(extId.url == null) {
                                            extId.url = {value:data.generatedUrl};
                                        }else{
                                            extId.url.value=data.generatedUrl;                        
                                        }
                                    } else if (!data.validFormat || (data.attemptedResolution && !data.resolved) ){
                                        if(extId.url == null) {
                                            extId.url = {value:""};
                                        }else{
                                            extId.url.value="";                        
                                        }
                                        extId.workExternalIdentifierId.errors.push(om.get('orcid.frontend.manual_work_form_errors.id_unresolvable'));
                                    }
                                    extId.resolvingId = false;
                                }
                            }).fail(function() {
                                console.log("id resolve error");
                                extId.resolvingId = false;
                            });
                        });
                    }
                }else{
                    var url;
                    if(extId != null) {
                        url = workIdLinkJs.getLink(extId.workExternalIdentifierId.value, extId.workExternalIdentifierType.value);
                        if(extId.url == null) {
                            extId.url = {value:url};
                        }else{
                            extId.url.value=url;                        
                        }
                    }
                }
            };

            $scope.formatExternalIDType = function(model) {
                if (!model){
                    return "";
                }
                if ($scope.externalIDNamesToDescriptions[model]){
                    return $scope.externalIDNamesToDescriptions[model].description;
                }
                //not loaded any descriptions yet, fetch them
                var url = getBaseUri()+'/works/idTypes.json?query='+model;
                var ajax = $.ajax({
                    url: url,
                    dataType: 'json',
                    cache: true,
                    async: false,
                  }).done(function(data) {
                      for (var key in data) {
                          $scope.externalIDNamesToDescriptions[data[key].name] = data[key];
                      }
                  });   
                $scope.externalIDTypeCache[model] = ajax;
                return $scope.externalIDNamesToDescriptions[model].description;   
            };
            //--typeahead end

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

            $scope.hideSources = function(group) {
                $scope.editSources[group.groupId] = false;
            };

            $scope.hideTooltip = function (key){        
                $scope.showElement[key] = false;
            };

            $scope.hideURLPopOver = function(id){       
                $scope.displayURLPopOver[id] = false;
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
                                            $timeout(function(){
                                                for (var i in data) {                           
                                                    $scope.worksFromBibtex.push(data[i]);
                                                }   
                                            });
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
                        $timeout(function() {
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

            // remove once grouping is live
            $scope.moreInfoClick = function(work, $event) {
                if (document.documentElement.className.indexOf('no-touch') == -1){
                    $scope.moreInfoOpen?$scope.closePopover():$scope.loadWorkInfo(work.putCode.value, $event);
                }
            };

            // remove once grouping is live
            $scope.moreInfoMouseEnter = function(work, $event) {
                $event.stopPropagation();
                if (document.documentElement.className.indexOf('no-touch') > -1){
                    $scope.loadWorkInfo(work.putCode.value, $event);
                }
                else{
                    $scope.moreInfoOpen?$scope.closePopover():$scope.loadWorkInfo(work.putCode.value, $event);
                }
            };

            $scope.openBibTextWizard = function () {
                if(emailVerified === true 
                    || configuration.showModalManualEditVerificationEnabled == false)
                {
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

            $scope.openEditWork = function(putCode){
                worksSrvc.getEditable(putCode, function(data) {
                    $scope.addWorkModal(data);
                });
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

            $scope.openImportWizardUrl = function(url) {
                openImportWizardUrl(url);
            };
    
            $scope.openImportWizardUrlFilter = function(url, client) {
                url = url + '?client_id='+client.id+'&response_type=code&scope='+client.scopes+'&redirect_uri='+client.redirectUri;
                openImportWizardUrl(url);
            };

            $scope.putWork = function(){
                if(emailVerified === true 
                    || configuration.showModalManualEditVerificationEnabled == false
                ){
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
                                    $timeout(function(){
                                        $scope.worksFromBibtex.splice($scope.bibtextWorkIndex, 1);
                                        $scope.bibtextWork = false;
                                        $scope.addingWork = false;
                                    });
                                    $.colorbox.close();
                                    $scope.worksSrvc.addAbbrWorksToScope(worksSrvc.constants.access_type.USER, $scope.sortState.predicateKey, !$scope.sortState.reverseKey[$scope.sortState.predicateKey]);
                                }
                            } else {
                                $timeout(function(){
                                    $scope.editWork = data;                    
                                    commonSrvc.copyErrorsLeft($scope.editWork, data);
                                    $scope.addingWork = false;
                                    //re-populate any id resolution errors.
                                    //do it here because they're by-passable
                                    if ($scope.exIdResolverFeatureEnabled == true){
                                        for (var extId in $scope.editWork.workExternalIdentifiers){
                                            $scope.fillUrl($scope.editWork.workExternalIdentifiers[extId]);                                                                                    
                                        }
                                    }
                                });
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

            $scope.renderTranslatedTitleInfo = function(putCode) {
                var info = null;

                if(putCode != null && $scope.worksSrvc.details[putCode] != null && $scope.worksSrvc.details[putCode].translatedTitle != null) {
                    info = $scope.worksSrvc.details[putCode].translatedTitle.content + ' - ' + $scope.worksSrvc.details[putCode].translatedTitle.languageName;
                }

                return info;
            };

            $scope.rmWorkFromBibtex = function(work) {
                var index = $scope.worksFromBibtex.indexOf(work);
                
                $scope.worksFromBibtex.splice(index, 1);
            };

            $scope.saveAllFromBibtex = function(){
                var worksToSave = null;
                var numToSave = null;

                if( savingBibtex == false ){
                    savingBibtex = true;

                    worksToSave =  new Array();
                    angular.forEach($scope.worksFromBibtex, function( work, key ) {
                        if (work.errors.length == 0){
                            worksToSave.push(work);
                        } 
                    });
                    
                    numToSave = worksToSave.length;
                    angular.forEach( worksToSave, function( work, key ) {
                        worksSrvc.putWork(work,function(data) {
                            $timeout(function(){
                                var index = $scope.worksFromBibtex.indexOf(work);
                                $scope.worksFromBibtex.splice(index, 1);
                            });
                            numToSave--;
                            if (numToSave == 0){
                                $scope.closeAllMoreInfo();
                                $scope.worksSrvc.refreshWorkGroups($scope.sortState.predicateKey, !$scope.sortState.reverseKey[$scope.sortState.predicateKey]);
                                savingBibtex = false;
                            }
                        });
                    });

                }
                
            };
            
            $scope.loadMore = function() {
                $scope.worksSrvc.addAbbrWorksToScope(worksSrvc.constants.access_type.USER, $scope.sortState.predicateKey, !$scope.sortState.reverseKey[$scope.sortState.predicateKey]);
            }

            $scope.serverValidate = function (relativePath) {
                console.log(getBaseUri() + '/' + relativePath);
                $.ajax({
                    url: getBaseUri() + '/' + relativePath,
                    type: 'POST',
                    data:  angular.toJson($scope.editWork),
                    contentType: 'application/json;charset=UTF-8',
                    dataType: 'json',
                    success: function(data) {
                        $timeout(function(){
                            commonSrvc.copyErrorsLeft($scope.editWork, data);
                            if ( relativePath == 'works/work/citationValidate.json') {
                                $scope.validateCitation();
                            }
                        });
                    }
                }).fail(function() {
                    // something bad is happening!
                    console.log("WorkCtrl.serverValidate() error");
                });
            };

            $scope.setAddWorkPrivacy = function(priv, $event) {
                $event.preventDefault();
                $scope.editWork.visibility.visibility = priv;
            };

            $scope.setBulkGroupPrivacy = function(priv) {
            	var putCodes = new Array();
                for (var idx in worksSrvc.groups){
                	if ($scope.bulkEditMap[worksSrvc.groups[idx].activePutCode]){  
                        for (var idj in worksSrvc.groups[idx].works) {
                        	putCodes.push(worksSrvc.groups[idx].works[idj].putCode.value);
                            worksSrvc.groups[idx].works[idj].visibility.visibility = priv;
                        }
                    }
                }
                
                if(putCodes.length > 0) {
                	worksSrvc.updateVisibility(putCodes, priv);
                }                
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
                        $scope.worksSrvc.refreshWorkGroups($scope.sortState.predicateKey, !$scope.sortState.reverseKey[$scope.sortState.predicateKey]);
                    }
                });
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
                        $scope.worksSrvc.refreshWorkGroups($scope.sortState.predicateKey, !$scope.sortState.reverseKey[$scope.sortState.predicateKey]);
                    }
                });
                return false;
            };

            $scope.showDetailsMouseClick = function(group, $event) {
                $event.stopPropagation();
                $scope.moreInfo[group.groupId] = !$scope.moreInfo[group.groupId];
                for (var idx in group.works){
                    $scope.loadDetails(group.works[idx].putCode.value, $event);
                }
            };

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

            $scope.showSources = function(group) {
                $scope.editSources[group.groupId] = true;
            };

            $scope.showTooltip = function (key){        
                $scope.showElement[key] = true;     
            };

            $scope.showURLPopOver = function(id){       
                $scope.displayURLPopOver[id] = true;
            };

            $scope.showWorkImportWizard =  function() {
                if(!$scope.workImportWizard) {
                    loadWorkImportWizardList();
                }
                $scope.workImportWizard = !$scope.workImportWizard;
            };  

            $scope.sort = function(key) {
                $scope.sortState.sortBy(key);
                worksSrvc.resetWorkGroups();
                worksSrvc.addAbbrWorksToScope(worksSrvc.constants.access_type.USER, $scope.sortState.predicateKey, !$scope.sortState.reverseKey[key]);
               
            };

            $scope.sortOtherLast = function(type) {
                if (type.key == 'other'){    
                    return 'ZZZZZ';
                } 
                return type.value;
            };

            $scope.swapbulkChangeAll = function() {
                $scope.bulkChecked = !$scope.bulkChecked;
                for (var idx in worksSrvc.groups){
                    $scope.bulkEditMap[worksSrvc.groups[idx].activePutCode] = $scope.bulkChecked;
                }
                $scope.bulkDisplayToggle = false;
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
            };

            $scope.toggleBulkEdit = function() {
                worksSrvc.loadAllWorkGroups($scope.sortState.predicateKey, !$scope.sortState.reverseKey[$scope.sortState.predicateKey], function() {
                    if(emailVerified === true || configuration.showModalManualEditVerificationEnabled == false){
                        if (!$scope.bulkEditShow) {
                            $scope.bulkEditMap = {};
                            $scope.bulkChecked = false;
                            for (var idx in worksSrvc.groups){
                                $scope.bulkEditMap[worksSrvc.groups[idx].activePutCode] = false;
                            }
                        };
                        $scope.bulkEditShow = !$scope.bulkEditShow;
                        $scope.showBibtexImportWizard = false;
                        $scope.workImportWizard = false;
                        $scope.showBibtexExport = false;
                    }else{
                        showEmailVerificationModal();
                    }
                });
            };

            $scope.toggleClickPrivacyHelp = function(key) {
                if ( document.documentElement.className.indexOf('no-touch') == -1 ){
                    $scope.privacyHelp[key]=!$scope.privacyHelp[key];
                }
            };

            $scope.toggleTranslatedTitleModal = function(){
                $scope.editTranslatedTitle = !$scope.editTranslatedTitle;
                $('#translatedTitle').toggle();
                $.colorbox.resize();
            };

            $scope.toggleWizardDesc = function(id){
                $scope.wizardDescExpanded[id] = !$scope.wizardDescExpanded[id];
            };

            $scope.userIsSource = function(work) {
                if (work.source == orcidVar.orcidId){
                    return true;
                }
                return false;
            };

            $scope.validateCitation = function() {
                if ( $scope.editWork.citation ){
                    if ($scope.editWork.citation.citation
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
                    
                }
            };

            $scope.validCombineSel = function(selectedWork,work) {
                if ($scope.hasCombineableEIs(selectedWork)){
                    return $scope.userIsSource(work) || $scope.hasCombineableEIs(work);
                }
                else{
                    return $scope.hasCombineableEIs(work);
                }
            };

            function loadWorkImportWizardList() {
                $.ajax({
                    url: getBaseUri() + '/workspace/retrieve-work-import-wizards.json',
                    type: 'GET',
                    contentType: 'application/json;charset=UTF-8',
                    dataType: 'json',
                    success: function(data) {                    	                     
                        $timeout(function(){
                            //console.log('crtl works loadWorkImportWizardList', data);
	                        if(data == null || data.length == 0) {
	                            $scope.noLinkFlag = false;
	                        }
	                        $scope.selectedWorkType = om.get('workspace.works.import_wizzard.all');
	                        $scope.selectedGeoArea = om.get('workspace.works.import_wizzard.all');
	                        $scope.workImportWizardsOriginal = data;
	                        $scope.bulkEditShow = false;
	                        $scope.showBibtexImportWizard = false;
	                        for(var idx in data) {                        	
	                        	for(var i in data[idx].actTypes) {
	                        		if(!utilsService.contains($scope.workType, data[idx].actTypes[i])) {
	                        			$scope.workType.push(data[idx].actTypes[i]);
	                        		}                        		
	                        	}
	                        	for(var j in data[idx].geoAreas) {
	                        		if(!utilsService.contains($scope.geoArea, data[idx].geoAreas[j])) {
	                        			$scope.geoArea.push(data[idx].geoAreas[j]);
	                        		}                        		
	                        	}							
	                        }
                        });                                                
                    }
                }).fail(function(e) {
                    // something bad is happening!
                    console.log("WorkImportWizardError");
                    logAjaxError(e);
                });
            }
    
            //init
            $scope.loadMore();
            loadWorkImportWizardList();

        }
    ]
);

// This is the Angular 2 part of the module
@NgModule({})
export class WorkCtrlNg2Module {}