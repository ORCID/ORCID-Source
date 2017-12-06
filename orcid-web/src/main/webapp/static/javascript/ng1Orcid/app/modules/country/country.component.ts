import { NgFor, NgIf } 
    from '@angular/common'; 

import { AfterViewInit, Component, OnDestroy, OnInit } 
    from '@angular/core';

import { Observable } 
    from 'rxjs/Rx';

import { Subject } 
    from 'rxjs/Subject';

import { Subscription }
    from 'rxjs/Subscription';

import { CountryService } 
    from '../../shared/countryService.ts';

import { EmailService } 
    from '../../shared/emailService.ts';

import { ModalService } 
    from '../../shared/modalService.ts'; 

@Component({
    selector: 'country-ng2',
    template:  scriptTmpl("country-ng2-template")
})
export class CountryComponent implements AfterViewInit, OnDestroy, OnInit {
    private ngUnsubscribe: Subject<void> = new Subject<void>();
    private subscription: Subscription;

    countryForm: any;
    countryFormAddresses: any;
    emails: any;
    emailSrvc: any;

    constructor( 
        private countryService: CountryService,
        private emailService: EmailService,
        private modalService: ModalService
    ) {

        this.countryForm = {
            addresses: {
            }
        };
        this.countryFormAddresses = [];
        this.emails = {};
    }

    getCountryForm(): void {
        this.countryService.getCountryData()
        //.takeUntil(this.ngUnsubscribe)
        .subscribe(
            data => {
                this.countryForm = data;
                this.countryFormAddresses = this.countryForm.addresses;
                //console.log('this.countryForm', this.countryForm);
            },
            error => {
                console.log('getCountryFormError', error);
            } 
        );
    };

    openEditModal(): void{      
        this.emailService.getEmails()
        //.takeUntil(this.ngUnsubscribe)
        .subscribe(
            data => {
                this.emails = data;
                if( this.emailService.getEmailPrimary().verified ){
                    this.modalService.notifyOther({action:'open', moduleId: 'modalCountryForm'});
                }else{
                    this.modalService.notifyOther({action:'open', moduleId: 'modalemailunverified'});
                }
            },
            error => {
                console.log('getEmails', error);
            } 
        );
    };


    //Default init functions provided by Angular Core
    ngAfterViewInit() {
        //Fire functions AFTER the view inited. Useful when DOM is required or access children directives
        this.subscription = this.countryService.notifyObservable$.subscribe(
            (res) => {
                this.getCountryForm();
                console.log('notified', res);
            }
        );
    };

    ngOnDestroy() {
        this.ngUnsubscribe.next();
        this.ngUnsubscribe.complete();
    };

    ngOnInit() {
        this.getCountryForm();
    };

}
