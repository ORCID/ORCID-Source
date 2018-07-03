declare var $: any;
declare var ActSortState: any;
declare var GroupedActivities: any;
declare var om: any;
declare var openImportWizardUrl: any;

import { NgForOf, NgIf } 
    from '@angular/common'; 

import { AfterViewInit, ChangeDetectorRef, Component, OnDestroy, OnInit } 
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
    selector: 'works-ng2',
    template:  scriptTmpl("works-ng2-template")
})
export class WorksComponent implements AfterViewInit, OnDestroy, OnInit {
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
    bulkDisplayToggle: boolean;
    bulkEditMap: any;
    bulkEditShow: boolean;
    combineWork: any;
    deleteGroup: any;
    deletePutCode: any;
    displayURLPopOver: any;
    editSources: any;
    editWork: any;
    emails: any;
    emailSrvc: any;
    fixedTitle: any;
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

    constructor( 
        private commonSrvc: CommonService,
        private cdr: ChangeDetectorRef,
        private emailService: EmailService,
        private modalService: ModalService,
        private workspaceSrvc: WorkspaceService,
        private worksService: WorksService
    ) {
        //console.log('works component init');

        this.addingWork = false;
        this.bibtexExportError = false;
        this.bibtexLoading = false;
        this.bibtexParsingError = false;
        this.bibtextWork = false;
        this.bibtextWorkIndex = null;
        this.bulkChecked = false;
        this.bulkDeleteCount = 0;
        this.bulkDisplayToggle = false;
        this.bulkEditMap = {};
        this.bulkEditShow = false;
        this.combineWork = null;
        this.displayURLPopOver = {};
        this.editSources = {};
        this.editWork = null;
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

    addWorkModal(work): void {
        this.emailService.getEmails()
        .pipe(    
            takeUntil(this.ngUnsubscribe)
        )
        .subscribe(
            data => {
                this.emails = data;
                if( this.emailService.getEmailPrimary().verified ){
                    this.worksService.notifyOther({ work:work });
                    if(work == undefined) {
                        this.modalService.notifyOther({action:'open', moduleId: 'modalWorksForm', edit: false});
                    } else {
                        this.modalService.notifyOther({action:'open', moduleId: 'modalWorksForm', edit: true});
                    }                    
                }else{
                    this.modalService.notifyOther({action:'open', moduleId: 'modalemailunverified'});
                }
            },
            error => {
                //console.log('getEmails', error);
            } 
        );
    };

    /*addWorkModal(work): void{      
        this.emailService.getEmails()
        .pipe(    
            takeUntil(this.ngUnsubscribe)
        )
        .subscribe(
            data => {
                this.emails = data;
                if( this.emailService.getEmailPrimary().verified ){
                    this.editWork = work;
                    this.genericService.open('modalWorksForm');
                }else{
                    this.modalService.notifyOther({action:'open', moduleId: 'modalemailunverified'});
                }
            },
            error => {
                console.log('error getting email data: ', error);
            } 
        );
    };*/

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
                this.editWork = data;
                if( this.editWork.workExternalIdentifiers.length == 0 ){
                    this.addExternalIdentifier();
                }        
                this.loadWorkTypes();
                this.showAddWorkModal();
            }
        } else {
            showEmailVerificationModal();
        }
        */

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
        for (idx in this.worksService.groups){
            if (this.bulkEditMap[this.worksService.groups[idx].activePutCode]){
                this.bulkDeleteCount++;
            }
        }

        this.worksService.notifyOther({bulkDeleteCount:this.bulkDeleteCount, bulkEditMap:this.bulkEditMap});
        this.modalService.notifyOther({action:'open', moduleId: 'modalWorksBulkDelete'});
    };

    deleteWorkConfirm(putCode, deleteGroup): void {
        this.emailService.getEmails()
        .pipe(    
            takeUntil(this.ngUnsubscribe)
        )
        .subscribe(
            data => {
                this.emails = data;
                if( this.emailService.getEmailPrimary().verified ){
                    let maxSize = 100;
                    let work = this.worksService.getWork(putCode);
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
                    this.worksService.notifyOther({fixedTitle:this.fixedTitle, putCode:putCode, deleteGroup:deleteGroup, sortState:this.sortState});
                    this.modalService.notifyOther({action:'open', moduleId: 'modalWorksDelete'});
                }else{
                    this.modalService.notifyOther({action:'open', moduleId: 'modalemailunverified'});
                }
            },
            error => {
                //console.log('getEmails', error);
            } 
        );
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

    loadDetails(putCode, event): void {
        console.log("load details");
        //Close any open popover
        this.closePopover(event);
        this.moreInfoOpen = true;
        //Display the popover
        $(event.target).next().css('display','inline');
        this.getGroupDetails(
            putCode, 
            this.worksService.constants.access_type.USER
        );
    };

    getDetails(putCode, type, callback): void {
        console.log("get details");
        if(this.worksService.details[putCode] == undefined) {
             this.worksService.getDetails(putCode, type)
            .pipe(    
                takeUntil(this.ngUnsubscribe)
            )
            .subscribe(
                data => {
                    this.worksService.removeBadContributors(data);
                    this.worksService.removeBadExternalIdentifiers(data);
                    this.worksService.addBibtexJson(data);
                    this.worksService.details[putCode] = data;
                    if (callback != undefined) {
                        callback(this.worksService.details[putCode]);
                    } 
                    console.log("worksService details[]");
                    console.log(this.worksService.details[putCode]);
                },
                error => {
                    console.log('error getting work details', error);
                } 
            );
        } else {
            if (callback != undefined){
                callback(this.worksService.details[putCode]);
            }
        };
    }

    getEditable(putCode, callback): void {
        // first check if they are the current source
        var work = this.getDetails(
            putCode, this.worksService.constants.access_type.USER, 
            function(data) {
                if (data.source == orcidVar.orcidId){
                    callback(data);
                }
                else{
                    this.getGroupDetails(
                        putCode, 
                        this.worksService.constants.access_type.USER, 
                        function () {
                            // in this case we want to open their version
                            // if they don't have a version yet then copy
                            // the current one
                            var bestMatch = null;
                            for (var idx in this.worksService.details) {    
                                if (this.worksService.details[idx].source == orcidVar.orcidId) {
                                    bestMatch = this.worksService.details[idx];
                                    break;
                                }
                            }
                            if (bestMatch == null) {
                                bestMatch = this.worksService.createNew(this.worksService.details[putCode]);
                            }
                            
                            callback(bestMatch);
                        }
                    );
                }
            }
        );
    }

    getGroupDetails(putCode, type, callback?): void {
        console.log("get group details");
        let group = this.worksService.getGroup(putCode);
        let needsLoading =  new Array();
        let popFunct = function () {
            if (needsLoading.length > 0) {
                this.getDetails(needsLoading.pop(), type, popFunct); 
            }
            else if (callback != undefined) {
                callback();
            }
        }.bind(this);

        for (var idx in group.works) {
            needsLoading.push(group.works[idx].putCode.value)
        }

        popFunct();
    }

    loadMore(): void {
        this.worksService.addAbbrWorksToScope(this.worksService.constants.access_type.USER, this.sortState.predicateKey, 
            !this.sortState.reverseKey[this.sortState.predicateKey]
        )
        .pipe(    
            takeUntil(this.ngUnsubscribe)
        )
        .subscribe(
            data => {
                this.formData = data;

                console.log('this.getForm works', this.formData);

                //let itemVisibility = null;
                //let len = null;

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

    loadWorkImportWizardList(): void {
        this.worksService.loadWorkImportWizardList()
        .pipe(    
            takeUntil(this.ngUnsubscribe)
        )
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
                        if(!this.commonSrvc.contains(this.workType, data[idx].actTypes[i])) {
                            this.workType.push(data[idx].actTypes[i]);
                        }                                
                    }
                    for(var j in data[idx].geoAreas) {
                        if(!this.commonSrvc.contains(this.geoArea, data[idx].geoAreas[j])) {
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
        .pipe(    
            takeUntil(this.ngUnsubscribe)
        )
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

    openEditWork(putCode): void{
        this.getEditable(putCode, function(data) {
            this.addWorkModal(data);
        }.bind(this));
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
        .pipe(    
            takeUntil(this.ngUnsubscribe)
        )
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

    refreshWorkGroups(): void {
        this.worksService.refreshWorkGroups(this.sortState.predicateKey, 
            !this.sortState.reverseKey[this.sortState.predicateKey]
        )
        .pipe(    
            takeUntil(this.ngUnsubscribe)
        )
        .subscribe(
            data => {
                this.formData = data;
                this.formData = data;
                this.worksService.handleWorkGroupData( this.formData );
                this.worksService.loading = false;
            },
            error => {
                this.worksService.loading = false;
                console.log('Error refreshing work groups', error);
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
                this.worksService.putWork(work,function(data) {
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

    setBulkGroupPrivacy(priv): void {
        var putCodes = new Array();
        for (var idx in this.worksService.groups){
            if (this.bulkEditMap[this.worksService.groups[idx].activePutCode]){  
                for (var idj in this.worksService.groups[idx].works) {
                    putCodes.push(this.worksService.groups[idx].works[idj].putCode.value);
                    this.worksService.groups[idx].works[idj].visibility.visibility = priv;
                }
            }
        }
        
        if(putCodes.length > 0) {
            this.worksService.updateVisibility(putCodes, priv)
            .pipe(    
                takeUntil(this.ngUnsubscribe)
            )
            .subscribe(
                data => {
                    if (putCodes.length > 0) {
                        this.worksService.updateVisibility(putCodes, priv);
                    }
                    //group.activeVisibility = priv;
                },
                error => {
                    console.log('Error updating group visibility', error);
                } 
            );
        }                
    };

    setGroupPrivacy(putCode, priv): void {
        var group = this.worksService.getGroup(putCode);
        var putCodes = new Array();
        for (var idx in group.works) {
            putCodes.push(group.works[idx].putCode.value);
            group.works[idx].visibility.visibility = priv;
        }
        this.worksService.updateVisibility(putCodes, priv)
        .pipe(    
            takeUntil(this.ngUnsubscribe)
        )
        .subscribe(
            data => {

                if (putCodes.length > 0) {
                    this.worksService.updateVisibility(putCodes, priv);
                }

                group.activeVisibility = priv;
            },
            error => {
                console.log('Error updating group visibility', error);
            } 
        );
    }

    setPrivacy(putCodes, priv): void {
        this.worksService.updateVisibility(putCodes, priv)
        .pipe(    
            takeUntil(this.ngUnsubscribe)
        )
        .subscribe(
            data => {
                if (putCodes.length > 0) {
                    this.worksService.updateVisibility(putCodes, priv);
                }
            },
            error => {
                console.log('Error updating work visibility', error);
            } 
        ); 
    }

    showCombineMatches( work1 ): void {
        this.combineWork = work1;
        /*
        $.colorbox({
            scrolling: true,
            html: $compile($('#combine-work-template').html())($scope),
            onLoad: function() {$('#cboxClose').remove();},
            // start the colorbox off with the correct width
            width: this.commonSrvc.formColorBoxResize(),
            onComplete: function() {$.colorbox.resize();},
            onClosed: function() {
                $scope.closeAllMoreInfo();
                $scope.worksService.refreshWorkGroups($scope.sortState.predicateKey, !$scope.sortState.reverseKey[$scope.sortState.predicateKey]);
            }
        });
        */
    };

    showDetailsMouseClick = function(group, $event) {
        console.log("show details");
        $event.stopPropagation();
        this.moreInfo[group.groupId] = !this.moreInfo[group.groupId];
        //this.cdr.detectChanges();
        console.log(group);
        for (var idx in group.works){
            console.log("group works i");
            console.log(group.works[idx]);
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

    swapbulkChangeAll(): void {
        this.bulkChecked = !this.bulkChecked;
        for (var idx in this.worksService.groups){
            this.bulkEditMap[this.worksService.groups[idx].activePutCode] = this.bulkChecked;
        }
        this.bulkDisplayToggle = false;
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
        console.log("toggle bulk edit");
        this.emailService.getEmails()
        .pipe(    
            takeUntil(this.ngUnsubscribe)
        )
        .subscribe(
            data => {
                this.emails = data;
                if( this.emailService.getEmailPrimary().verified ){
                    this.worksService.loadAllWorkGroups(this.sortState.predicateKey, 
                    !this.sortState.reverseKey[this.sortState.predicateKey])
                    .pipe(    
                        takeUntil(this.ngUnsubscribe)
                    )
                    .subscribe(
                        data => {
                            this.formData = data;
                            this.worksService.handleWorkGroupData( this.formData );
                            this.worksService.loading = false;
                            if (!this.bulkEditShow) {
                                this.bulkEditMap = {};
                                this.bulkChecked = false;
                                console.log(this.worksService.groups);
                                for (var idx in this.worksService.groups){
                                    this.bulkEditMap[this.worksService.groups[idx].activePutCode] = false;
                                }
                            }
                            this.bulkEditShow = !this.bulkEditShow;
                            this.showBibtexImportWizard = false;
                            this.workImportWizard = false;
                            this.showBibtexExport = false;
                            this.cdr.detectChanges();
                        },
                        error => {
                            this.worksService.loading = false;
                            //console.log('getWorksFormError', error);
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

    toggleSelectMenu(): void {                   
        this.bulkDisplayToggle = !this.bulkDisplayToggle;                    
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
        /*this.subscription = this.worksService.notifyObservable$.subscribe(
            (res) => {
                this.loadMore();
            }
        );*/
        this.subscription = this.worksService.notifyObservable$.subscribe(
            (res) => {                
                if(res.action == 'delete') {
                    if(res.successful == true) {
                        this.refreshWorkGroups();
                    }
                } 
                if(res.action == 'deleteBulk') {
                    if(res.successful == true) {
                        this.bulkEditShow = false;
                        this.refreshWorkGroups();
                    }
                } 
                if(res.action == 'add' || res.action == 'cancel') {
                    if(res.successful == true) {
                        this.refreshWorkGroups();
                        this.loadMore();
                    }
                }                
            }
        );
    };

    ngOnDestroy() {
        this.ngUnsubscribe.next();
        this.ngUnsubscribe.complete();
    };

    ngOnInit() {
        this.loadMore();
        this.loadWorkImportWizardList();
    };
}