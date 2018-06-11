//Import all the angular components

import { NgForOf, NgIf } 
    from '@angular/common'; 

import { AfterViewInit, Component, OnDestroy, OnInit } 
    from '@angular/core';

import { Observable, Subject, Subscription } 
    from 'rxjs';
import 'rxjs/add/operator/takeUntil';

import { GenericService } 
    from '../../shared/generic.service.ts'; 

import { EmailService } 
    from '../../shared/email.service.ts';

@Component({
    selector: 'name-ng2',
    template:  scriptTmpl("name-ng2-template")
})
export class NameComponent implements AfterViewInit, OnDestroy, OnInit {
    private ngUnsubscribe: Subject<void> = new Subject<void>();

    emails: object;
    emailSrvc: any;
    emailVerified: boolean;
    lengthError: boolean;
    nameForm: any;
    originalData: object;
    showEdit: boolean;
    url_path: string;

    constructor(
        private nameService: GenericService,
        private emailService: EmailService
    ) {
        
        this.emails = {};
        this.emailVerified = false; //change to false once service is ready
        this.lengthError = false;
        this.nameForm = {};
        this.originalData = {};
        this.showEdit = false;
        this.url_path = '/account/nameForm.json';
    }

    cancel(): void {
        this.nameForm = this.originalData;
        this.getNameForm();
        this.showEdit = false;
    };

    close(): void {
        this.showEdit = false;
    };

    displayFullName(): boolean {
        let display = false;

        if(
            !(this.nameForm != null 
                && (this.nameForm.creditName == null 
                    || this.nameForm.givenNames.value.length == 0
                )
            )
        ){
            display = true;
        }

        return display;
    };

    displayPublishedName(): boolean {
        let display = false;

        if(
            this.nameForm != null 
            && (this.nameForm.creditName == null 
                || this.nameForm.creditName.value == null 
                || this.nameForm.creditName.value.length == 0)
        ){
            display = true;
        }

        return display;
    };

    getNameForm(): void {
        this.nameService.getData( this.url_path )
        .takeUntil(this.ngUnsubscribe)
        .subscribe(
            data => {
                this.nameForm = data;
                if( this.nameForm.creditName == null ) {
                    this.nameForm.creditName = { value: null };
                }
                if( this.nameForm.familyName == null ) {
                    this.nameForm.familyName = { value: null };
                }
                if( this.nameForm.givenNames == null ) {
                    this.nameForm.givenNames = { value: null };
                }
                this.originalData = this.nameForm;
                ////console.log('this.nameForm', this.nameForm);
            },
            error => {
                //console.log('getNameForm Error', error);
            } 
        );
    };

    privacyChange( obj ): any {
        this.nameForm.visibility.visibility = obj;
        this.setNameForm( false );   
    };

    setNameForm( closeAfterAction ): any {
        this.nameService.setData( this.nameForm, this.url_path )
        .takeUntil(this.ngUnsubscribe)
        .subscribe(
            data => {
                this.nameForm = data;
                ////console.log('this.nameForm response', this.nameForm);
                if( closeAfterAction == true 
                    && this.nameForm.errors.length == 0 
                ) {
                    this.close();
                }
            },
            error => {
                //console.log('setNameForm Error', error);
            } 
        );
    };

    setNameFormEnter( event ): any {
        if ( event.keyCode == "13"){
            this.setNameForm( true );
        }
    };
    
    toggleEdit(): void {
        this.showEdit = !this.showEdit;    
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
        this.getNameForm();
    }; 
}
