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
    selector: 'websites-ng2',
    template:  scriptTmpl("websites-ng2-template")
})
export class WebsitesComponent implements AfterViewInit, OnDestroy, OnInit {
    private ngUnsubscribe: Subject<void> = new Subject<void>();
    private subscription: Subscription;

    formData: any;
    emails: any;
    emailSrvc: any;
    url_path: string;

    constructor( 
        private websitesService: GenericService,
        private emailService: EmailService,
        private modalService: ModalService
    ) {
        this.formData = {
            websites: null
        };
        this.emails = {};
        this.url_path = '/my-orcid/websitesForms.json';
    }

    getformData(): void {
        this.websitesService.getData( this.url_path )
        .takeUntil(this.ngUnsubscribe)
        .subscribe(
            data => {
                this.formData = data;

                let itemVisibility = null;
                let len = null;
                let websites = null;

                this.formData = data;
                //this.newElementDefaultVisibility = this.websitesForm.visibility.visibility;
                
                websites = this.formData.websites;
                len = websites.length;
                //Iterate over all elements to:
                // -> see if they have the same visibility, to set the default visibility element
                // -> set the default protocol when needed
                if(len > 0) {
                    while (len--) {
                        if(websites[len].url != null && websites[len].url.value != null) {
                            if (!websites[len].url.value.toLowerCase().startsWith('http')) {
                                websites[len].url.value = 'http://' + websites[len].url.value;
                            }                            
                        }     

                                     
                    }
                }

                //console.log('this.getForm websites', this.formData);
            },
            error => {
                //console.log('getWebsitesFormError', error);
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
                    this.modalService.notifyOther({action:'open', moduleId: 'modalWebsitesForm'});
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
        this.subscription = this.websitesService.notifyObservable$.subscribe(
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

