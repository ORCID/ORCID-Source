//Import all the angular components

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

import { BiographyService } 
    from '../../shared/biographyService.ts'; 

import { ConfigurationService } 
    from '../../shared/configurationService.ts';

import { EmailService } 
    from '../../shared/emailService.ts';

import { ModalService } 
    from '../../shared/modalService.ts'; 

@Component({
    selector: 'biography-ng2',
    template:  scriptTmpl("biography-ng2-template")
})
export class BiographyComponent implements AfterViewInit, OnDestroy, OnInit {
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

    cancel(): void {
        this.getBiographyForm();
        this.showEdit = false;
    };

    checkLength(): any {
        if ( this.biographyForm.biography.value.length > 5000 ) {
            this.lengthError = true;
        } else {
            this.lengthError = false;
        }

        return !this.lengthError; //Negating the error, if error is present will be true and return false to avoid user input
    };

    close(): void {
        this.showEdit = false;
    };

    getBiographyForm(): void{
        this.biographyService.getBiographyData()
        .takeUntil(this.ngUnsubscribe)
        .subscribe(
            data => {
                this.biographyForm = data;
                //console.log('this.biographyForm', this.biographyForm);

                if( this.biographyForm.biography == null  ) {
                    this.biographyForm.biography = {
                        errors: [],
                        getRequiredMessage: null,
                        required: true,
                        value: ''
                    }
                }
                //console.log('this.biographyForm 2', this.biographyForm);
            },
            error => {
                console.log('getBiographyFormError', error);
            } 
        );
    };

    hideTooltip(tp): void{
        this.showElement[tp] = false;
    };

    setBiographyForm(): any{
        if( this.checkLength() == false ){    
            return; // do nothing if there is a length error
        }
        this.biographyService.setBiographyData( this.biographyForm )
        .takeUntil(this.ngUnsubscribe)
        .subscribe(
            data => {
                this.biographyForm = data;
                //console.log('this.biographyForm response', this.biographyForm);
                this.close();
            },
            error => {
                console.log('setBiographyFormError', error);
            } 
        );
    };

    setPrivacy(priv, $event:any): void {
        $event.preventDefault();
        this.biographyForm.visiblity.visibility = priv;
        this.setBiographyForm();        
    };

    showTooltip(tp): void{
        this.showElement[tp] = true;
    };
    
    toggleEdit(): void {

        this.emailService.getEmails()
        .takeUntil(this.ngUnsubscribe)
        .subscribe(
            data => {
                this.emails = data;
                if( this.emailService.getEmailPrimary().verified ){
                    this.showEdit = !this.showEdit;
                }else{
                    this.modalService.notifyOther('open');
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
    };

    ngOnDestroy() {
        this.ngUnsubscribe.next();
        this.ngUnsubscribe.complete();
    };

    ngOnInit() {
        this.getBiographyForm();
        this.configuration = this.configurationService.getInitialConfiguration();
    }; 
}