declare var orcidGA: any;
declare var orcidVar: any;
declare var getBaseUri: any;

//Import all the angular components

import { NgForOf, NgIf } 
    from '@angular/common'; 


import { AfterViewInit, Component, EventEmitter, Input, OnChanges, OnDestroy, OnInit, Output } 
    from '@angular/core';

import { Observable, Subject, Subscription } 
    from 'rxjs';
import { takeUntil } 
    from 'rxjs/operators';

import { EmailService } 
    from '../../shared/email.service.ts'; 

import { CommonService } 
    from '../../shared/common.service.ts'; 

/*
<custom-email-ng2 elementId="${client_id}"></custom-email-ng2>
*/
@Component({
    selector: 'custom-email-ng2',
    template:  scriptTmpl("custom-email-ng2-template")
})
export class CustomEmailComponent implements AfterViewInit, OnDestroy, OnInit {
    private ngUnsubscribe: Subject<void> = new Subject<void>();

    @Input() elementId: string;
    
    clientId: any;
    customEmail: any;
    customEmailList: any;
    editedCustomEmail: any;
    showCreateButton: any;
    showCreateForm: any;
    showEditForm: any;
    showEmailList: any;
    toDelete: any;

    constructor(
        private emailService: EmailService,
        private commonService: CommonService
    ) {
        this.clientId = null;
        this.customEmail = null;
        this.customEmailList = [];
        this.editedCustomEmail = null;
        this.showCreateButton = false;
        this.showCreateForm = false;
        this.showEditForm = false;
        this.showEmailList = false;
        this.toDelete = null;
    }

    closeModal(): void {
        //$.colorbox.close();
    };

    confirmDeleteCustomEmail(index): void {
        this.toDelete = this.customEmailList[index];
        /*
        $.colorbox({
            html : $compile($('#delete-custom-email').html())($scope),
            scrolling: true,
            onLoad: function() {
                $('#cboxClose').remove();
            }
        });

        $.colorbox.resize({width:"415px" , height:"175px"});
        */
    };

    deleteCustomEmail(index): void {
        this.emailService.deleteCustomEmail( this.toDelete )
        .takeUntil(this.ngUnsubscribe)
        .subscribe(
            data => {
                if(data){
                    // If everything worked fine, reload the list of clients
                    this.getCustomEmails();
                    this.closeModal();
                } else {
                    //console.log("Error deleting custom email");
                }
            },
            error => {
                //console.log('setformDataError', error);
            } 
        );
    };

    displayCreateForm(): void {
        this.emailService.displayCreateForm( this.clientId )
        .takeUntil(this.ngUnsubscribe)
        .subscribe(
            data => {
                if(data.errors == null || data.errors.length == 0){
                    this.customEmail = data;
                    this.showCreateForm = true;
                    this.showEditForm = false;
                    this.showCreateButton = false;
                    this.showEmailList = false;
                }
            },
            error => {
                //console.log('setformDataError', error);
            } 
        );
      
    };

    editCustomEmail(): void {

        this.emailService.editCustomEmail( this.editedCustomEmail )
        .takeUntil(this.ngUnsubscribe)
        .subscribe(
            data => {
                if(data.errors != null && data.errors.length > 0){
                    this.editedCustomEmail = data;
                } else {
                    // If everything worked fine, reload the list of clients
                    this.getCustomEmails();
                }
            },
            error => {
                //console.log('setformDataError', error);
            } 
        );
    };

    getCustomEmails(): void {
        this.emailService.getCustomEmails( this.clientId )
        .takeUntil(this.ngUnsubscribe)
        .subscribe(
            data => {
                this.customEmailList = [];
                this.showEmailList = false;
                this.showCreateForm = false;
                this.showEditForm = false;
                this.customEmail = null;
                this.editedCustomEmail = null;
                if(data != null && data.length > 0){
                    this.customEmailList = data;
                    this.showCreateForm = false;
                    this.showEditForm = false;
                    this.showEmailList = true;
                    this.showCreateButton = false;
                }  else {
                    this.showCreateButton = true;
                }
            },
            error => {
                //console.log('setformDataError', error);
            } 
        );
      
    };

    init(client_id): void {
        this.clientId = client_id;
        this.getCustomEmails();
    };

    saveCustomEmail(): void {

        this.emailService.saveCustomEmail( this.customEmail )
        .takeUntil(this.ngUnsubscribe)
        .subscribe(
            data => {
                if(data.errors != null && data.errors.length > 0){
                    this.customEmail = data;
                } else {
                    // If everything worked fine, reload the list of clients
                    this.getCustomEmails();
                }
            },
            error => {
                //console.log('setformDataError', error);
            } 
        );
    };

    showEditLayout(index): void {
        this.showCreateForm = false;
        this.showEditForm = true;
        this.showCreateButton = false;
        this.showEmailList = false;
        this.editedCustomEmail = this.customEmailList[index];
    };
    
    showViewLayout(): void {
        this.getCustomEmails();
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
        this.init(this.elementId);
    }; 
}