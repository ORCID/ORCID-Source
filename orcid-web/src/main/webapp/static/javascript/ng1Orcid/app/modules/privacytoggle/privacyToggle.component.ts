//Import all the angular components

import { NgFor } 
    from '@angular/common'; 

import { AfterViewInit, Component, OnDestroy, OnInit } 
    from '@angular/core';

import { Observable } 
    from 'rxjs/Rx';

import { Subject } 
    from 'rxjs/Subject';

import { Subscription }
    from 'rxjs/Subscription';

import { BiographyService } 
    from '../../shared/biographyService.ts'; 

import { ConfigurationService } 
    from '../../shared/configurationService.ts';

import { EmailService } 
    from '../../shared/emailService.ts';

import { ModalService } 
    from '../../shared/modalService.ts'; 

@Component({
    selector: 'privacy-toggle-ng2',
    template:  scriptTmpl("privacy-toggle-ng2-template")
})
export class PrivacytoggleComponent implements AfterViewInit, OnDestroy, OnInit {
    private ngUnsubscribe: Subject<void> = new Subject<void>();

    biographyForm: any;
    configuration: any;
    emails: any;
    emailSrvc: any;
    emailVerified: any;
    lengthError: any;
    showEdit: any;
    showElement: any;

    constructor(
        private biographyService: BiographyService,
        private configurationService: ConfigurationService,
        private emailService: EmailService,
        private modalService: ModalService
    ) {
        this.biographyForm = {
            biography: {
                value: ''
            }
        };
        
        this.emails = {};
        this.emailVerified = false; //change to false once service is ready
        this.lengthError = false;
        this.showEdit = false;
        this.showElement = {};
    }

    //Default init functions provided by Angular Core
    ngAfterViewInit() {
        //Fire functions AFTER the view inited. Useful when DOM is required or access children directives
    };

    ngOnDestroy() {
        this.ngUnsubscribe.next();
        this.ngUnsubscribe.complete();
    };

    ngOnInit() {
        //this.getBiographyForm();
        //this.configuration = this.configurationService.getInitialConfiguration();
    }; 
}