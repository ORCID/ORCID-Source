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

import { ModalService } 
    from '../../shared/modal.service.ts'; 

@Component({
    selector: 'keywords-ng2',
    template:  scriptTmpl("keywords-ng2-template")
})
export class KeywordsComponent implements AfterViewInit, OnDestroy, OnInit {
    private ngUnsubscribe: Subject<void> = new Subject<void>();
    private subscription: Subscription;

    formData: any;
    emails: any;
    emailSrvc: any;
    url_path: string;

    constructor( 
        private keywordsService: GenericService,
        private emailService: EmailService,
        private modalService: ModalService
    ) {

        this.formData = {
        };

        this.emails = {};
        this.url_path = '/my-orcid/keywordsForms.json';
    }

    getData(): void {
        this.keywordsService.getData( this.url_path )
        .takeUntil(this.ngUnsubscribe)
        .subscribe(
            data => {
                this.formData = data;
                ////console.log('this.keywords', this.form);
            },
            error => {
                //console.log('getKeywordsFormError', error);
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
                    this.modalService.notifyOther({action:'open', moduleId: 'modalKeywordsForm'});
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
        this.subscription = this.keywordsService.notifyObservable$.subscribe(
            (res) => {
                this.getData();
            }
        );
    };

    ngOnDestroy() {
        this.ngUnsubscribe.next();
        this.ngUnsubscribe.complete();
    };

    ngOnInit() {
        this.getData();
    };

}

