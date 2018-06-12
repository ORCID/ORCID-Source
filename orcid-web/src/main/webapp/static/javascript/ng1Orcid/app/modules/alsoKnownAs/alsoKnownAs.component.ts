import { NgForOf, NgIf } 
    from '@angular/common'; 

import { AfterViewInit, Component, OnDestroy, OnInit } 
    from '@angular/core';

import { Observable, Subject, Subscription } 
    from 'rxjs';
import { takeUntil } 
    from 'rxjs/operators';

import { GenericService } 
    from '../../shared/generic.service.ts';

import { EmailService } 
    from '../../shared/email.service.ts';

import { ModalService } 
    from '../../shared/modal.service.ts'; 

@Component({
    selector: 'also-known-as-ng2',
    template:  scriptTmpl("also-known-as-ng2-template")
})
export class AlsoKnownAsComponent implements AfterViewInit, OnDestroy, OnInit {
    private ngUnsubscribe: Subject<void> = new Subject<void>();
    private subscription: Subscription;

    formData: any;
    emails: any;
    emailSrvc: any;
    url_path: string;

    constructor( 
        private alsoKnownAsService: GenericService,
        private emailService: EmailService,
        private modalService: ModalService
    ) {
        this.formData = {
            otherNames: null
        };
        this.emails = {};
        this.url_path = '/my-orcid/otherNamesForms.json';
    }

    getformData(): void {
        this.alsoKnownAsService.getData( this.url_path, 'alsoKnownAs' )
        .pipe(    
            takeUntil(this.ngUnsubscribe)
        )
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

    openEditModal(): void{      
        this.emailService.getEmails()
        .pipe(    
            takeUntil(this.ngUnsubscribe)
        )
        .subscribe(
            data => {
                this.emails = data;
                if( this.emailService.getEmailPrimary().verified ){
                    this.modalService.notifyOther({action:'open', moduleId: 'modalAlsoKnownAsForm'});
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
        this.subscription = this.alsoKnownAsService.notifyObservable$.subscribe(
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
    };

}
