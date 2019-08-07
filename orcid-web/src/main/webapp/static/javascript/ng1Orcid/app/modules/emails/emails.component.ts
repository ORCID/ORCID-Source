import { NgForOf, NgIf } 
    from '@angular/common'; 

import { AfterViewInit, Component, OnDestroy, OnInit } 
    from '@angular/core';

import { Observable, Subject, Subscription } 
    from 'rxjs';
import { takeUntil } 
    from 'rxjs/operators';

import { EmailService } 
    from '../../shared/email.service';

import { ModalService } 
    from '../../shared/modal.service'; 

@Component({
    selector: 'emails-ng2',
    template:  scriptTmpl("emails-ng2-template")
})
export class EmailsComponent implements AfterViewInit, OnDestroy, OnInit {
    private ngUnsubscribe: Subject<void> = new Subject<void>();
    private subscription: Subscription;

    formData: any;
    emails: any;
    emailSrvc: any;

    constructor( 
        private emailService: EmailService,
        private modalService: ModalService
    ) {
        this.formData = {
            emails: null
        };
        this.emails = {};
    }

    deleteOtherName(otherName): void{
        let otherNames = this.formData.otherNames;
        let len = otherNames.length;
        while (len--) {            
            if (otherNames[len] == otherName){                
                otherNames.splice(len,1);
            }
        }        
    };

    getformData(): void {
        this.emailService.getData()
        .pipe(    
            takeUntil(this.ngUnsubscribe)
        )
        .subscribe(
            data => {
                this.formData = data;

                ////console.log('this.getForm', this.formData);
            },
            error => {
                //console.log('getAlsoKnownAsFormError', error);
            } 
        );
    };

    openEditModal(): void{
        this.modalService.notifyOther({action:'open', moduleId: 'modalEmails'});
    };

    //Default init functions provided by Angular Core
    ngAfterViewInit() {
        //Fire functions AFTER the view inited. Useful when DOM is required or access children directives
        this.subscription = this.emailService.notifyObservable$.subscribe(
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