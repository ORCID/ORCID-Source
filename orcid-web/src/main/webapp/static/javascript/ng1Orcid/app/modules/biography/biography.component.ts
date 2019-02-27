//Import all the angular components

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
    
import { CommonService } 
    from '../../shared/common.service.ts';

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
    url_path: any;
    userInfo: any;
    
    constructor(
        private biographyService: GenericService,
        private emailService: EmailService,
        private modalService: ModalService,
        private commonSrvc: CommonService
    ) {
        this.formData = {
            biography: {
                value: ''
            }
        };
        
        this.emails = {};
        this.lengthError = false;
        this.showEdit = false;
        this.url_path = '/account/biographyForm.json';
                
        if(!this.commonSrvc.isPublicPage) {                        
            this.userInfo = this.commonSrvc.userInfo$
            .subscribe(
                data => {
                    this.userInfo = data;                
                },
                error => {
                    console.log('header.component.ts: unable to fetch userInfo', error);
                    this.userInfo = {};
                } 
            );
        }      
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
        this.biographyService.getData( this.url_path )
        .pipe(    
            takeUntil(this.ngUnsubscribe)
        )
        .subscribe(
            data => {
                this.formData = data;

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
        this.biographyService.setData( this.formData, this.url_path )
        .pipe(    
            takeUntil(this.ngUnsubscribe)
        )
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
        console.log('edit bio clicked')
        this.emailService.getEmails()
        .pipe(    
            takeUntil(this.ngUnsubscribe)
        )
        .subscribe(
            data => {
                console.log('email data bio', data, this.emailService.getEmailPrimary());
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