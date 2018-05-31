import { NgForOf, NgIf } 
    from '@angular/common'; 

import { AfterViewInit, Component, OnDestroy, OnInit } 
    from '@angular/core';

import { Observable } 
    from 'rxjs/Rx';

import { Subject } 
    from 'rxjs/Subject';

import { Subscription }
    from 'rxjs/Subscription';

import { GenericService } 
    from '../../shared/generic.service.ts'; 

import { EmailService } 
    from '../../shared/email.service.ts';

import { ModalService } 
    from '../../shared/modal.service.ts'; 

@Component({
    selector: 'country-ng2',
    template:  scriptTmpl("country-ng2-template")
})
export class CountryComponent implements AfterViewInit, OnDestroy, OnInit {
    private ngUnsubscribe: Subject<void> = new Subject<void>();
    private subscription: Subscription;

    formData: any;
    formDataAddresses: any;
    emails: any;
    emailSrvc: any;
    url_path: any;

    constructor( 
        private countryService: GenericService,
        private emailService: EmailService,
        private modalService: ModalService
    ) {

        this.formData = {
            addresses: {
            }
        };
        this.formDataAddresses = [];
        this.emails = {};
        this.url_path = '/account/countryForm.json';
    }

    getformData(): void {
        this.countryService.getData( this.url_path )
        .takeUntil(this.ngUnsubscribe)
        .subscribe(
            data => {
                this.formData = data;
                this.formDataAddresses = this.formData.addresses;
                ////console.log('this.formData', this.formData);
            },
            error => {
                //console.log('getformDataError', error);
            } 
        );
    };

    openEditModal(): void{      
        this.emailService.getEmails()
        .takeUntil(this.ngUnsubscribe)
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
                //console.log('getEmails', error);
            } 
        );
    };


    //Default init functions provided by Angular Core
    ngAfterViewInit() {
        //Fire functions AFTER the view inited. Useful when DOM is required or access children directives
        this.subscription = this.countryService.notifyObservable$.subscribe(
            (res) => {
                this.getformData();
                //console.log('notified', res);
            }
        );
    };

    ngOnDestroy() {
        this.ngUnsubscribe.next();
        this.ngUnsubscribe.complete();
    };

    ngOnInit() {
        this.getformData();
    };

}
