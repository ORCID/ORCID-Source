//Unused
//Import all the angular components

import { NgForOf, NgIf } 
    from '@angular/common'; 

import {  AfterViewInit, Component, EventEmitter, Input, OnChanges, OnDestroy, OnInit, Output } 
    from '@angular/core';

import { Observable } 
    from 'rxjs/Rx';

import { Subject } 
    from 'rxjs/Subject';

import { Subscription }
    from 'rxjs/Subscription';

import { EmailService } 
    from '../../shared/email.service.ts'; 

import { PreferencesService } 
    from '../../shared/preferences.service.ts'; 


@Component({
    selector: 'email-frecuency-ng2',
    template:  scriptTmpl("email-frecuency-ng2-template")
})
export class EmailFrecuencyComponent implements AfterViewInit, OnDestroy, OnInit {
    private ngUnsubscribe: Subject<void> = new Subject<void>();
   
    constructor(
        private emailSrvc: EmailService,
        private prefsSrvc: PreferencesService
    ) {
    }
    
    /*
    getformData(): void {
        this.emailService.getData()
        .takeUntil(this.ngUnsubscribe)
        .subscribe(
            data => {
                this.formData = data;

                if( this.formData.otherNames == null ) {
                    this.formData.otherNames = { value: null };
                }

                ////console.log('this.getForm', this.formData);
            },
            error => {
                //console.log('getAlsoKnownAsFormError', error);
            } 
        );
    };
    */

    //Default init functions provided by Angular Core
    ngAfterViewInit() {
        //Fire functions AFTER the view inited. Useful when DOM is required or access children directives
    };

    ngOnDestroy() {
        this.ngUnsubscribe.next();
        this.ngUnsubscribe.complete();
    };

    ngOnInit() {
    }; 
}