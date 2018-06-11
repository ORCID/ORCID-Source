declare var $: any;
declare var colorbox: any;
declare var formColorBoxResize: any;
declare var getBaseUri: any;
declare var logAjaxError: any;
declare var orcidVar: any;

//Import all the angular components

import { NgForOf, NgIf } 
    from '@angular/common'; 

import { AfterViewInit, Component, OnDestroy, OnInit } 
    from '@angular/core';

import { Observable, Subject, Subscription } 
    from 'rxjs';
import 'rxjs/add/operator/takeUntil';

import { ExternalIdentifiersService } 
    from '../../shared/externalIdentifiers.service.ts'; 

import { PreferencesService } 
    from '../../shared/preferences.service.ts'; 


@Component({
    selector: 'external-identifiers-ng2',
    template:  scriptTmpl("external-identifiers-ng2-template")
})
export class ExternalIdentifiersComponent implements AfterViewInit, OnDestroy, OnInit {
    private ngUnsubscribe: Subject<void> = new Subject<void>();

    externalIdentifiersForm: any;
    orcidId: any;
    primary: any;
    scrollTop: any;
    showElement: any;
    bioModel: any;
    bulkEditShow: any;
    bulkEditMap: any;
    bulkChecked: any;
    bulkDisplayToggle: any;
    removeExternalIdentifierIndex: any;
    removeExternalModalText: any;
   
    constructor(
        private externalIdentifiersService: ExternalIdentifiersService,
        private prefsSrvc: PreferencesService
    ) {

        //bioBulkSrvc.initScope($scope):
        this.bioModel = null; //Dummy model to avoid bulk privacy selector fail
        this.bulkEditShow = false;
        this.bulkEditMap = {};
        this.bulkChecked = false;
        this.bulkDisplayToggle = false;
        //

        this.externalIdentifiersForm = null;
        this.orcidId = orcidVar.orcidId;
        this.primary = true;
        this.scrollTop = 0;
        this.showElement = [];
        this.removeExternalIdentifierIndex = null;
        this.removeExternalModalText = '';
    }

    toggleSelectMenu(): void {               
        this.bulkDisplayToggle = !this.bulkDisplayToggle;                    
    };

     closeEditModal(): void {
       $.colorbox.close();
    };

    // Person 2
    deleteExternalIdentifier(externalIdentifier): void {
        var externalIdentifiers = this.externalIdentifiersForm.externalIdentifiers;
        var len = externalIdentifiers.length;
        while (len--) {
            if (externalIdentifiers[len] == externalIdentifier){
                externalIdentifiers.splice(len,1);
                this.externalIdentifiersForm.externalIdentifiers = externalIdentifiers;
            }       
        }
    };
    
    deleteExternalIdentifierConfirmation(idx): void{
        this.removeExternalIdentifierIndex = idx;
        this.removeExternalModalText = this.externalIdentifiersForm.externalIdentifiers[idx].reference;
        if (this.externalIdentifiersForm.externalIdentifiers[idx].commonName != null) {
            this.removeExternalModalText = this.externalIdentifiersForm.externalIdentifiers[idx].commonName + ' ' + this.removeExternalModalText;
        }
        /*
        $.colorbox({
            html: $compile($('#delete-external-id-modal').html())($scope)
        });
        $.colorbox.resize();
        */
    };

    getExternalIdentifiersForm(): void {
        this.externalIdentifiersService.getExternalIdentifiersForm()
        .takeUntil(this.ngUnsubscribe)
        .subscribe(
            data => {
                this.externalIdentifiersForm = data;
                this.displayIndexInit();
            },
            error => {
                //console.log('getAlsoKnownAsFormError', error);
            } 
        );
    };

    openEditModal(): void {      
        this.bulkEditShow = false;
        /*
        $.colorbox({
            scrolling: true,
            html: $compile($('#edit-external-identifiers').html())($scope),
            onLoad: function() {
                $('#cboxClose').remove();
            },
            width: formColorBoxResize(),
            onComplete: function() {

            },
            onClosed: function() {
                this.getExternalIdentifiersForm();
            }
        });
        */
        //$.colorbox.resize();
    };

    removeExternalIdentifier(): void {
        var externalIdentifier = this.externalIdentifiersForm.externalIdentifiers[this.removeExternalIdentifierIndex];

        this.externalIdentifiersService.removeExternalIdentifier( externalIdentifier )
        .takeUntil(this.ngUnsubscribe)
        .subscribe(
            data => {
                if(data['errors'].length != 0){
                    //console.log("Unable to delete external identifier.");
                } else {
                    this.externalIdentifiersForm.externalIdentifiers.splice(this.removeExternalIdentifierIndex, 1);
                    this.removeExternalIdentifierIndex = null;
                }
            },
            error => {
                //console.log('getAlsoKnownAsFormError', error);
            } 
        );
    };

    // To fix displayIndex values that comes with -1
    displayIndexInit(): void {
        var idx = null;
        for (idx in this.externalIdentifiersForm.externalIdentifiers) {            
           this.externalIdentifiersForm.externalIdentifiers[idx]['displayIndex'] = this.externalIdentifiersForm.externalIdentifiers.length - idx;
        }       
    };

    setBulkGroupPrivacy(priv): void {
        var idx = null;
        for (idx in this.externalIdentifiersForm.externalIdentifiers){
            this.externalIdentifiersForm.externalIdentifiers[idx].visibility.visibility = priv;    
        }
    };

    setExternalIdentifiersForm(): void {     
        this.externalIdentifiersForm.visibility = null;

        this.externalIdentifiersService.setExternalIdentifiersForm( this.externalIdentifiersForm )
        .takeUntil(this.ngUnsubscribe)
        .subscribe(
            data => {
                this.externalIdentifiersForm = data;
                if (this.externalIdentifiersForm.errors.length == 0){                    
                    this.getExternalIdentifiersForm();                
                    this.closeEditModal();
                }else{
                    //console.log(this.externalIdentifiersForm.errors);
                }
            },
            error => {
                //console.log('getAlsoKnownAsFormError', error);
            } 
        );
    };
    
    setPrivacy(priv, $event): void {
        $event.preventDefault();
        this.externalIdentifiersForm.visibility.visibility = priv;
    };
    
    setPrivacyModal(priv, $event, externalIdentifier): void {        
        var externalIdentifiers = this.externalIdentifiersForm.externalIdentifiers;
        var len = externalIdentifiers.length;

        $event.preventDefault();        
                        
        while (len--) {
            if (externalIdentifiers[len] == externalIdentifier) {
                externalIdentifiers[len].visibility.visibility = priv;        
            }
        }
    };

    swapDown(index): void {
        var temp = null;
        var tempDisplayIndex = null;
        if (index < this.externalIdentifiersForm.externalIdentifiers.length - 1) {
            temp = this.externalIdentifiersForm.externalIdentifiers[index];
            tempDisplayIndex = this.externalIdentifiersForm.externalIdentifiers[index]['displayIndex'];
            temp['displayIndex'] = this.externalIdentifiersForm.externalIdentifiers[index + 1]['displayIndex']
            this.externalIdentifiersForm.externalIdentifiers[index] = this.externalIdentifiersForm.externalIdentifiers[index + 1];
            this.externalIdentifiersForm.externalIdentifiers[index]['displayIndex'] = tempDisplayIndex;
            this.externalIdentifiersForm.externalIdentifiers[index + 1] = temp;
        }
    };  

    swapUp(index): void {
        var temp = null;
        var tempDisplayIndex = null;
        if (index > 0) {
            temp = this.externalIdentifiersForm.externalIdentifiers[index];
            tempDisplayIndex = this.externalIdentifiersForm.externalIdentifiers[index]['displayIndex'];

            temp['displayIndex'] = this.externalIdentifiersForm.externalIdentifiers[index - 1]['displayIndex']
            this.externalIdentifiersForm.externalIdentifiers[index] = this.externalIdentifiersForm.externalIdentifiers[index - 1];
            this.externalIdentifiersForm.externalIdentifiers[index]['displayIndex'] = tempDisplayIndex;
            this.externalIdentifiersForm.externalIdentifiers[index - 1] = temp;
        }
    };

    

    //Default init functions provided by Angular Core
    ngAfterViewInit() {
        //Fire functions AFTER the view inited. Useful when DOM is required or access children directives
    };

    ngOnDestroy() {
        this.ngUnsubscribe.next();
        this.ngUnsubscribe.complete();
    };

    ngOnInit() {
        this.getExternalIdentifiersForm();  
    }; 
}