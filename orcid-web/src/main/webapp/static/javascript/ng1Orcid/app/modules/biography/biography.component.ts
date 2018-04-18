//Import all the angular components

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

import { BiographyService } 
    from '../../shared/biography.service.ts'; 

import { EmailService } 
    from '../../shared/email.service.ts';

import { ModalService } 
    from '../../shared/modal.service.ts'; 

@Component({
    selector: 'biography-ng2',
    template:  scriptTmpl("biography-ng2-template")
})
export class BiographyComponent implements AfterViewInit, OnDestroy, OnInit {
    private ngUnsubscribe: Subject<void> = new Subject<void>();

    formData: any;
    emails: any;
    emailSrvc: any;
    lengthError: any;
    showEdit: any;

    constructor(
        private biographyService: BiographyService,
        private emailService: EmailService,
        private modalService: ModalService
    ) {
        this.formData = {
            biography: {
                value: ''
            }
        };
        
        this.emails = {};
        this.lengthError = false;
        this.showEdit = false;
    }

    cancel(): void {
        this.getformData();
        this.showEdit = false;
    };

    checkLength(): any {
        if ( this.formData.biography.value.length > 5000 ) {
            this.lengthError = true;
        } else {
            this.lengthError = false;
        }

        return !this.lengthError; //Negating the error, if error is present will be true and return false to avoid user input
    };

    close(): void {
        this.showEdit = false;
    };

    getformData(): void {
        this.biographyService.getBiographyData()
        .takeUntil(this.ngUnsubscribe)
        .subscribe(
            data => {
                this.formData = data;
                ////console.log('this.formData', this.formData);

                if( this.formData.biography == null  ) {
                    this.formData.biography = {
                        errors: [],
                        getRequiredMessage: null,
                        required: true,
                        value: ''
                    }
                }
            },
            error => {
                //console.log('getformDataError', error);
            } 
        );
    };

    privacyChange( obj ): any {
        this.formData.visibility.visibility = obj;
        this.setformData();   
    };

    setformData(): any{
        if( this.checkLength() == false ){    
            return; // do nothing if there is a length error
        }
        this.biographyService.setBiographyData( this.formData )
        .takeUntil(this.ngUnsubscribe)
        .subscribe(
            data => {
                this.formData = data;
                ////console.log('this.formData response', this.formData);
                this.close();
            },
            error => {
                //console.log('setformDataError', error);
            } 
        );
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
    };

    ngOnDestroy() {
        this.ngUnsubscribe.next();
        this.ngUnsubscribe.complete();
    };

    ngOnInit() {
        this.getformData();
    }; 
}