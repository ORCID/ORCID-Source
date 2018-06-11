declare var ActSortState: any;
declare var GroupedActivities: any;
declare var om: any;
declare var openImportWizardUrl: any;

import { NgForOf, NgIf } 
    from '@angular/common'; 

import { AfterViewInit, Component, OnDestroy, OnInit } 
    from '@angular/core';

import { Observable, Subject, Subscription } 
    from 'rxjs';
import { takeUntil } 
    from 'rxjs/operators';

import { CommonService } 
    from '../../shared/common.service.ts';

import { WorksService } 
    from '../../shared/works.service.ts';

import { EmailService } 
    from '../../shared/email.service.ts';

import { ModalService } 
    from '../../shared/modal.service.ts';

import { WorkspaceService } 
    from '../../shared/workspace.service.ts'; 

@Component({
    selector: 'works-form-ng2',
    template:  scriptTmpl("works-form-ng2-template")
})
export class WorksFormComponent implements AfterViewInit, OnDestroy, OnInit {
    private ngUnsubscribe: Subject<void> = new Subject<void>();
    private subscription: Subscription;

    addingWork: boolean;
    bibtexExportError: boolean;
    bibtexLoading: boolean;
    bibtexParsingError: boolean;
    bibtextWork: boolean;
    bibtextWorkIndex: any;
    bulkChecked: any;
    bulkDeleteCount: number;
    bulkDeleteSubmit: boolean;
    bulkDisplayToggle: false;
    bulkEditMap: any;
    bulkEditShow: boolean;
    combineWork: any;
    delCountVerify: number;
    displayURLPopOver: any;
    editSources: any;
    editWork: any;
    emails: any;
    emailSrvc: any;
    formData: any;
    geoArea: any;
    loadingScripts: any;
    moreInfo: any;
    moreInfoOpen: boolean;
    noLinkFlag: boolean;
    scriptsLoaded: boolean;
    selectedGeoArea: any;
    selectedWorkType: any;
    showBibtex: any;
    showBibtexExport: boolean;
    showBibtexImportWizard: boolean;
    showElement: any;
    sortState: any;
    textFiles: any;
    wizardDescExpanded: any;
    workImportWizard: boolean;
    workImportWizardsOriginal: any;
    workType: any;
    worksFromBibtex: any;
    contentCopy: any;
    badgesRequested: any; 
    bibtexGenerated: any;            
    bibtexURL: any;
    canReadFiles: any;
    combiningWorks: any;
    editTranslatedTitle: any;
    externalIDNamesToDescriptions: any;//caches name->description lookup so we can display the description not the name after selection
    externalIDTypeCache: any;//cache responses
    generatingBibtex: any;
    privacyHelp: any;
    types: any;

    constructor( 
        private commonService: CommonService,
        private emailService: EmailService,
        private modalService: ModalService,
        private workspaceSrvc: WorkspaceService,
        private worksService: WorksService
    ) {
        //console.log('works component init');
        this.contentCopy = {
            titleLabel: om.get("orcid.frontend.manual_work_form_contents.defaultTitle"),
            titlePlaceholder: om.get("orcid.frontend.manual_work_form_contents.defaultTitlePlaceholder")
        };
        this.addingWork = false;
        this.bibtexExportError = false;
        this.bibtexLoading = false;
        this.bibtexParsingError = false;
        this.bibtextWork = false;
        this.bibtextWorkIndex = null;
        this.bulkChecked = false;
        this.bulkDeleteCount = 0;
        this.bulkDeleteSubmit = false;
        this.bulkDisplayToggle = false;
        this.bulkEditMap = {};
        this.bulkEditShow = false;
        this.combineWork = null;
        this.delCountVerify = 0;
        this.displayURLPopOver = {};
        this.editSources = {};
        this.editWork = {
            citation: {
                citation: {
                    errors: {}, 
                    value: null
                },
                citationType: {
                    errors: {}, 
                    value: null
                }
            },
            contributors: {},
            countryCode: {
                errors: {}, 
                value: null
            },
            errors: {},
            journalTitle: {
                errors: {}, 
                value: null
            },
            languageCode: {
                errors: {}, 
                value: null
            },
            publicationDate: {
                errors: {}, 
                value: null
            },
            putCode: {
                value: null
            },
            shortDescription: {
                errors: {}, 
                value: null
            },
            subtitle: {
                errors: {}, 
                value: null
            },
            title: {
                errors: {}, 
                value: null
            },
            translatedTitle: {
                content: null,
                errors: {}, 
            },
            url: {
                errors: {}, 
                value: null
            },
            workCategory: {
                errors: {}, 
                value: null
            },
            workExternalIdentifiers: []
            ,
        };
        this.emails = {};
        this.formData = {
            works: null
        };
        this.geoArea = ['All'];
        this.loadingScripts = false;
        this.moreInfo = {};
        this.moreInfoOpen = false;
        this.noLinkFlag = true;
        this.scriptsLoaded = false;
        this.selectedGeoArea = null;
        this.selectedWorkType = null;
        this.showBibtex = {};
        this.showBibtexExport = false;
        this.showBibtexImportWizard = false;
        this.showElement = {};
        this.sortState = new ActSortState(GroupedActivities.ABBR_WORK);
        this.textFiles = [];
        this.wizardDescExpanded = {};
        this.workImportWizard = false;
        this.workImportWizardsOriginal = null;
        this.workType = ['All'];
        this.worksFromBibtex = null;
        this.badgesRequested = {};  
        this.bibtexGenerated = false;            
        this.bibtexURL = "";
        this.canReadFiles = false;
        this.combiningWorks = false;
        this.editTranslatedTitle = false;
        this.externalIDNamesToDescriptions = [];//caches name->description lookup so we can display the description not the name after selection
        this.externalIDTypeCache = [];//cache responses
        this.generatingBibtex = false;
        this.privacyHelp = {};
        this.types = null;
    }

    addExternalIdentifier(): void {
        this.editWork.workExternalIdentifiers.push(
            {
                relationship: {
                    value: "self"
                }, 
                url: {
                    value: ""
                },
                workExternalIdentifierId: {
                    value: ""
                }, 
                workExternalIdentifierType: {
                    value: ""
                } 
            }
        );
    };

    addWorkFromBibtex(work): void {
        this.bibtextWork = true;              
        this.bibtextWorkIndex = this.worksFromBibtex.indexOf(work);     
        this.editWork = this.worksFromBibtex[this.bibtextWorkIndex];
        
        this.putWork();        
    };

    addWorkModal(data): void {
        /*
        if(emailVerified === true 
            || configuration.showModalManualEditVerificationEnabled == false
        ){
            if (data == undefined) {
                worksService.getBlankWork(function(data) {
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
        */
    };

    bibtexShowToggle(putCode): void {
        this.showBibtex[putCode] = !(this.showBibtex[putCode]);
    };

    bulkChangeAll(bool): void {
        this.bulkChecked = bool;
        this.bulkDisplayToggle = false;
        for (var idx in this.worksService.groups){
            this.bulkEditMap[this.worksService.groups[idx].activePutCode] = bool;
        }
    };

    canBeCombined(work): any {
        if (this.userIsSource(work)){
            return true;
        }
        return this.hasCombineableEIs(work);
    };

    closePopover(event): void {
        this.moreInfoOpen = false;
        $('.work-more-info-container').css('display', 'none');
    };

    deleteBulkConfirm(idx): void {
        var idx: any;
        this.bulkDeleteCount = 0;
        this.bulkDeleteSubmit = false;        
        this.delCountVerify = 0;
        for (idx in this.worksService.groups){
            if (this.bulkEditMap[this.worksService.groups[idx].activePutCode]){
                this.bulkDeleteCount++;
            }
        }

        /*
        this.bulkDeleteFunction = this.deleteBulk;

        $.colorbox({
            html: $compile($('#bulk-delete-modal').html())($scope)
        });
        $.colorbox.resize();
        */
    };

    deleteWorkConfirm(putCode, deleteGroup): void {
        let maxSize = 100;
        let work = this.worksService.getWork(putCode);
        /*
        this.deletePutCode = putCode;
        this.deleteGroup = deleteGroup;
        if (work.title){
            this.fixedTitle = work.title.value;
        }
        else {  
            this.fixedTitle = '';
        } 
        if(this.fixedTitle.length > maxSize){
            this.fixedTitle = this.fixedTitle.substring(0, maxSize) + '...';
        }

        $.colorbox({
            html : $compile($('#delete-work-modal').html())($scope),
            onComplete: function() {$.colorbox.resize();}
        });
        */
    };

    editWorkFromBibtex(work): void {
        this.bibtextWork = true;
        this.bibtextWorkIndex = this.worksFromBibtex.indexOf(work);
        
        this.addWorkModal(this.worksFromBibtex[this.bibtextWorkIndex]);        
    };

    fetchBibtexExport(){
        this.bibtexLoading = true;
        this.bibtexExportError = false; 
        
        /*
        $.ajax({
            url: getBaseUri() + '/' + 'works/works.bib',
            type: 'GET',
            success: function(data) {
                this.bibtexLoading = false;
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
            this.bibtexExportError = true;
            //console.log("bibtex export error");
        });  
        */      
    };

    getformData(): void {
        this.worksService.addAbbrWorksToScope( 
            this.sortState.predicateKey, 
            !this.sortState.reverseKey[this.sortState.predicateKey]
        )
        .takeUntil(this.ngUnsubscribe)
        .subscribe(
            data => {
                this.formData = data;

                //console.log('this.getForm works', this.formData);

                let itemVisibility = null;
                let len = null;

                this.formData = data;
                this.worksService.handleWorkGroupData( this.formData );
                //this.newElementDefaultVisibility = this.formData.visibility.visibility;
                this.worksService.loading = false;
            },
            error => {
                this.worksService.loading = false;
                //console.log('getWorksFormError', error);
            } 
        );
    };

    hasCombineableEIs(work): boolean {
        if (work.workExternalIdentifiers != null){
            for (var idx in work.workExternalIdentifiers){
                if (work.workExternalIdentifiers[idx].workExternalIdentifierType.value != 'issn'){
                    return true;
                }
            }
        }
        return false;
    };

    hideSources(group): void {
        this.editSources[group.groupId] = false;
    };

    hideTooltip(key): void {        
        this.showElement[key] = false;
    };

    hideURLPopOver(id): void {       
        this.displayURLPopOver[id] = false;
    };

    isValidClass(cur): any {
        var valid = true;
        if (cur === undefined || cur == null) {
            return '';
        }
        if ( ( cur.required && (cur.value == null || cur.value.trim() == '') ) || ( cur.errors !== undefined && cur.errors.length > 0 ) ){
            valid = false;
        }
        return valid ? '' : 'text-error';
    };

    loadDetails(putCode, event): void {
        //Close any open popover
        this.closePopover(event);
        this.moreInfoOpen = true;
        //Display the popover
        $(event.target).next().css('display','inline');
        this.worksService.getGroupDetails(
            putCode, 
            this.worksService.constants.access_type.USER
        );
    };

    loadMore(): void {
        this.worksService.addAbbrWorksToScope( 
            this.sortState.predicateKey, 
            !this.sortState.reverseKey[this.sortState.predicateKey]
        );
    };

    loadWorkImportWizardList(): void {
        this.worksService.loadWorkImportWizardList()
        .takeUntil(this.ngUnsubscribe)
        .subscribe(
            data => {
                //console.log('this.getForm works loadWorkImportWizardList', data);

                if(data == null || data.length == 0) {
                    this.noLinkFlag = false;
                }
                this.selectedWorkType = om.get('workspace.works.import_wizzard.all');
                this.selectedGeoArea = om.get('workspace.works.import_wizzard.all');
                this.workImportWizardsOriginal = data;
                this.bulkEditShow = false;
                this.showBibtexImportWizard = false;
                for(var idx in data) {                            
                    for(var i in data[idx].actTypes) {
                        if(!this.commonService.contains(this.workType, data[idx].actTypes[i])) {
                            this.workType.push(data[idx].actTypes[i]);
                        }                                
                    }
                    for(var j in data[idx].geoAreas) {
                        if(!this.commonService.contains(this.geoArea, data[idx].geoAreas[j])) {
                            this.geoArea.push(data[idx].geoAreas[j]);
                        }                                
                    }                            
                }
            },
            error => {
                //console.log('WorkImportWizardError', error);
            } 
        );
    };

    openBibTextWizard(): void {
        this.emailService.getEmails()
        .takeUntil(this.ngUnsubscribe)
        .subscribe(
            data => {
                this.emails = data;
                if( this.emailService.getEmailPrimary().verified ){
                    this.bibtexParsingError = false;
                    this.bulkEditShow = false;
                    this.showBibtexExport = false;
                    this.showBibtexImportWizard = !(this.showBibtexImportWizard);
                    this.workImportWizard = false;
                    this.worksFromBibtex = null;
                }else{
                    this.modalService.notifyOther({action:'open', moduleId: 'modalemailunverified'});
                }
            },
            error => {
                //console.log('getEmails', error);
            } 
        );
    };

    openFileDialog(): void{
        this.textFiles = [];
        this.bibtexParsingError = false;
        /*
        $timeout(
            function() { //To avoid '$apply already in progress' error
                angular.element('#inputBibtex').trigger('click');
            }, 
            0
        );
        */
    };

    openImportWizardUrl(url): void {
        openImportWizardUrl(url);
    };

    openImportWizardUrlFilter(url, client): void {
        url = url + '?client_id=' + client.id + '&response_type=code&scope=' + client.scopes + '&redirect_uri=' + client.redirectUri;
        openImportWizardUrl(url);
    };

    putWork(): any{
        this.emailService.getEmails()
        .takeUntil(this.ngUnsubscribe)
        .subscribe(
            data => {
                /*
                this.emails = data;
                if( this.emailService.getEmailPrimary().verified ){
                    if ( this.addingWork ) {
                        return; // don't process if adding work
                    }
                    this.addingWork = true;
                    this.editWork.errors.length = 0;
                    this.worksService.putWork(
                        this.editWork,
                        function(data){
                            if (data.errors.length == 0) {
                                if (this.bibtextWork == false){
                                    $.colorbox.close();
                                    $scope.addingWork = false;
                                } else {
                                    $timeout(function(){
                                        $scope.worksFromBibtex.splice($scope.bibtextWorkIndex, 1);
                                        $scope.bibtextWork = false;
                                        $scope.addingWork = false;
                                    });
                                    $.colorbox.close();
                                    $scope.worksService.addAbbrWorksToScope(worksService.constants.access_type.USER, $scope.sortState.predicateKey, !$scope.sortState.reverseKey[$scope.sortState.predicateKey]);
                                }
                            } else {
                                $timeout(function(){
                                    $scope.editWork = data;                    
                                    commonSrvc.copyErrorsLeft($scope.editWork, data);
                                    $scope.addingWork = false;
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
                }else{
                    this.modalService.notifyOther({action:'open', moduleId: 'modalemailunverified'});
                }
                */
            },
            error => {
                //console.log('getEmails', error);
            } 
        );
    };

    rmWorkFromBibtex(work): void {
        let index = this.worksFromBibtex.indexOf(work);
        
        this.worksFromBibtex.splice(index, 1);
    };

    saveAllFromBibtex(): any{
        var worksToSave = null;
        var numToSave = null;
        /*
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
                worksService.putWork(work,function(data) {
                    $timeout(function(){
                        var index = $scope.worksFromBibtex.indexOf(work);
                        $scope.worksFromBibtex.splice(index, 1);
                    });
                    numToSave--;
                    if (numToSave == 0){
                        $scope.closeAllMoreInfo();
                        $scope.worksService.refreshWorkGroups($scope.sortState.predicateKey, !$scope.sortState.reverseKey[$scope.sortState.predicateKey]);
                        savingBibtex = false;
                    }
                });
            });

        }
        */
    };

    showCombineMatches( work1 ): void {
        this.combineWork = work1;
        /*
        $.colorbox({
            scrolling: true,
            html: $compile($('#combine-work-template').html())($scope),
            onLoad: function() {$('#cboxClose').remove();},
            // start the colorbox off with the correct width
            width: this.commonService.formColorBoxResize(),
            onComplete: function() {$.colorbox.resize();},
            onClosed: function() {
                $scope.closeAllMoreInfo();
                $scope.worksService.refreshWorkGroups($scope.sortState.predicateKey, !$scope.sortState.reverseKey[$scope.sortState.predicateKey]);
            }
        });
        */
    };

    showDetailsMouseClick = function(group, $event) {
        //console.log('showDetailsMouseClick envent', $event);
        $event.stopPropagation();
        this.moreInfo[group.groupId] = !this.moreInfo[group.groupId];
        for (var idx in group.works){
            this.loadDetails(group.works[idx].putCode.value, $event);
        }
    };

    showMozillaBadges(putCode): any{
        /*
        $scope.$watch(
            function () { 
                return document.getElementsByClassName('badge-container-' + putCode).length; 
            },
            function (newValue, oldValue) {
                  if (newValue !== oldValue) {
                      if ($scope.badgesRequested[putCode] == null){
                        var dois = worksService.getUniqueDois(putCode);
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
        */
    };

    showSources(group): void {
        this.editSources[group.groupId] = true;
    };

    showTooltip(key): void{        
        this.showElement[key] = true;     
    };

    showURLPopOver(id): void{       
        this.displayURLPopOver[id] = true;
    };

    showWorkImportWizard(): void {
        if(!this.workImportWizard) {
            this.loadWorkImportWizardList();
        }
        this.workImportWizard = !this.workImportWizard;
    }; 

    sort = function(key) {
        this.sortState.sortBy(key);
        this.worksService.resetWorkGroups();
        this.worksService.addAbbrWorksToScope( 
            this.sortState.predicateKey, 
            !this.sortState.reverseKey[key]
        );
       
    };

    toggleBibtexExport(): void{
        this.bibtexExportError = false;
        this.bibtexLoading = false;
        this.bibtexParsingError = false;
        this.bulkEditShow = false;        
        this.loadingScripts = false;
        this.scriptsLoaded = false;
        this.showBibtexExport  = !this.showBibtexExport;
        this.showBibtexImportWizard = false;
        this.workImportWizard = false;
    };

    toggleBulkEdit(): void {
        this.emailService.getEmails()
        .takeUntil(this.ngUnsubscribe)
        .subscribe(
            data => {
                this.emails = data;
                if( this.emailService.getEmailPrimary().verified ){
                    this.worksService.loadAllWorkGroups(
                    this.sortState.predicateKey, 
                    !this.sortState.reverseKey[this.sortState.predicateKey], 
                    function() {
                        if (!this.bulkEditShow) {
                            
                            this.bulkChecked = false;
                            for (var idx in this.worksService.groups){
                                this.bulkEditMap[this.worksService.groups[idx].activePutCode] = false;
                            }
                        };
                        this.bulkEditShow = !this.bulkEditShow;
                        this.showBibtexImportWizard = false;
                        this.workImportWizard = false;
                        this.showBibtexExport = false;
                    }
                );
                }else{
                    this.modalService.notifyOther({action:'open', moduleId: 'modalemailunverified'});
                }
            },
            error => {
                //console.log('getEmails', error);
            } 
        );
    };

    toggleWizardDesc(id): void {
        this.wizardDescExpanded[id] = !this.wizardDescExpanded[id];
    };

    userIsSource(work): boolean {
        if (work.source == orcidVar.orcidId){
            return true;
        }
        return false;
    };

    //Default init functions provided by Angular Core
    ngAfterViewInit() {
        //Fire functions AFTER the view inited. Useful when DOM is required or access children directives
        this.subscription = this.worksService.notifyObservable$.subscribe(
            (res) => {
                this.getformData();
            }
        );
    };

    ngOnDestroy() {
        this.ngUnsubscribe.next();
        this.ngUnsubscribe.complete();
    };

    ngOnInit() {
        this.getformData();
        this.loadWorkImportWizardList();
    };
}

/*
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
        'initialConfigService', 
        'this.commonService', 
        'worksService', 
        'workspaceSrvc',     
        function ($scope, $rootScope, $compile, $filter, $timeout, $q, actBulkSrvc, commonSrvc, emailSrvc, initialConfigService, this.commonService, worksService, workspaceSrvc ) {

            var savingBibtex = false;
            var this.commonService = this.commonService;

            actBulkSrvc.initScope($scope);
           
            $scope.badgesRequested = {};
            
            $scope.bibtexGenerated = false;
            
            $scope.bibtexURL = "";
            
            
            
            
            $scope.canReadFiles = false;
            $scope.combiningWorks = false;
            
            
            
            
            
            $scope.editTranslatedTitle = false;
            $scope.emailSrvc = emailSrvc;
            $scope.externalIDNamesToDescriptions = [];//caches name->description lookup so we can display the description not the name after selection
            $scope.externalIDTypeCache = [];//cache responses
            $scope.generatingBibtex = false;
            
            
            
            
            $scope.privacyHelp = {};
            
            
            
            
            
            
            $scope.types = null;
            
            
            
            $scope.workspaceSrvc = workspaceSrvc;
            $scope.worksService = worksService;

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

            

            

            

            $scope.applyLabelWorkType = function() {
                var obj = null;
                $timeout(
                    function() {
                        obj = $scope.worksService.getLabelMapping($scope.editWork.workCategory.value, $scope.editWork.workType.value)
                        $scope.contentCopy = obj;
                    }, 
                    100
                );
            };

            $scope.bibtextCancel = function(){
                $scope.worksFromBibtex = null;
            };

            

            $scope.bulkApply = function(func) {
                for (var idx in worksService.groups) {
                    if ($scope.bulkEditMap[worksService.groups[idx].activePutCode]){
                        func(worksService.groups[idx].activePutCode);
                    }
                }
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

            

            $scope.combined = function(work1, work2) {
                // no duplicate request;
                var putWork;
                if ($scope.combiningWorks){
                    return;
                }
                $scope.combiningWorks = true;
                
                if ($scope.userIsSource(work1)) {
                    putWork = worksService.copyEIs(work2, work1);
                } else if ($scope.userIsSource(work2)) {
                    putWork = worksService.copyEIs(work1, work2);
                } else {
                    putWork = worksService.createNew(work1);
                    putWork = worksService.copyEIs(work1, work2);
                }
                worksService.putWork(
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
                for (var idx in worksService.groups){
                    if ($scope.bulkEditMap[worksService.groups[idx].activePutCode]){
                        delPuts.push(worksService.groups[idx].activePutCode);
                    }
                }
                worksService.deleteGroupWorks(delPuts, $scope.sortState.predicateKey, !$scope.sortState.reverseKey[$scope.sortState.predicateKey]);
                $.colorbox.close();
                $scope.bulkEditShow = false;
            };

            

            $scope.deleteByPutCode = function(putCode, deleteGroup) {
                $scope.closeAllMoreInfo();
                if (deleteGroup) {
                   worksService.deleteGroupWorks(putCode, $scope.sortState.predicateKey, !$scope.sortState.reverseKey[$scope.sortState.predicateKey]);
                }
                else {
                   worksService.deleteWork(putCode, $scope.sortState.predicateKey, !$scope.sortState.reverseKey[$scope.sortState.predicateKey]);
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
                    }* /
                    if(extId.url == null) {
                        extId.url = {value:url};
                    }else{
                        extId.url.value=url;                        
                    }
                }
            };

            
            //--typeahead end

            //Fetches an array of {name:"",description:"",resolutionPrefix:""} containing query.
            
            

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
                            worksService.getBlankWork(
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
                                    worksService.worksValidate(
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

            

            $scope.loadWorkInfo = function(putCode, event) {
                //Close any open popover
                $scope.closePopover(event);
                $scope.moreInfoOpen = true;
                //Display the popover
                $(event.target).next().css('display','inline');
                if($scope.worksService.details[putCode] == null) {
                    $scope.worksService.getGroupDetails(putCode, worksService.constants.access_type.USER);
                } else {
                    $(event.target).next().css('display','inline');
                }
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

            

            $scope.openEditWork = function(putCode){
                worksService.getEditable(putCode, function(data) {
                    $scope.addWorkModal(data);
                });
            };

            

            
    
            

            

            $scope.renderTranslatedTitleInfo = function(putCode) {
                var info = null;

                if(putCode != null && $scope.worksService.details[putCode] != null && $scope.worksService.details[putCode].translatedTitle != null) {
                    info = $scope.worksService.details[putCode].translatedTitle.content + ' - ' + $scope.worksService.details[putCode].translatedTitle.languageName;
                }

                return info;
            };

            

     
           

            

            $scope.setAddWorkPrivacy = function(priv, $event) {
                $event.preventDefault();
                $scope.editWork.visibility.visibility = priv;
            };

            $scope.setBulkGroupPrivacy = function(priv) {
                var putCodes = new Array();
                for (var idx in worksService.groups){
                    if ($scope.bulkEditMap[worksService.groups[idx].activePutCode]){  
                        for (var idj in worksService.groups[idx].works) {
                            putCodes.push(worksService.groups[idx].works[idj].putCode.value);
                            worksService.groups[idx].works[idj].visibility.visibility = priv;
                        }
                    }
                }
                
                if(putCodes.length > 0) {
                    worksService.updateVisibility(putCodes, priv);
                }                
            };

            $scope.showAddWorkModal = function(){
                $scope.editTranslatedTitle = false;
                $.colorbox({
                    scrolling: true,
                    html: $compile($('#add-work-modal').html())($scope),
                    onLoad: function() {$('#cboxClose').remove();},
                    // start the colorbox off with the correct width
                    width: this.commonService.formColorBoxResize(),
                    onComplete: function() {
                        //resize to insure content fits
                    },
                    onClosed: function() {
                        $scope.closeAllMoreInfo();
                        $scope.worksService.refreshWorkGroups($scope.sortState.predicateKey, !$scope.sortState.reverseKey[$scope.sortState.predicateKey]);
                    }
                });
            };

            


            $scope.sortOtherLast = function(type) {
                if (type.key == 'other'){    
                    return 'ZZZZZ';
                } 
                return type.value;
            };

            $scope.swapbulkChangeAll = function() {
                $scope.bulkChecked = !$scope.bulkChecked;
                for (var idx in worksService.groups){
                    $scope.bulkEditMap[worksService.groups[idx].activePutCode] = $scope.bulkChecked;
                }
                $scope.bulkDisplayToggle = false;
            };

            ;

            

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

        }
    ]
);

// This is the Angular 2 part of the module
@NgModule({})
export class WorkCtrlNg2Module {}
*/