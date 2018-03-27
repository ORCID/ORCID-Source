import { NgFor, NgIf } 
    from '@angular/common'; 

import { AfterViewInit, Component, ElementRef, EventEmitter, Input, OnChanges, OnDestroy, OnInit, Output } 
    from '@angular/core';

import { Observable } 
    from 'rxjs/Rx';

import { Subject } 
    from 'rxjs/Subject';

import { Subscription }
    from 'rxjs/Subscription';

import { EmailService } 
    from '../../shared/email.service.ts';

import { PreferencesService }
    from '../../shared/preferences.service.ts'

import { CommonService } 
    from '../../shared/common.service.ts';

import { ModalService } 
    from '../../shared/modal.service.ts'; 

@Component({
    selector: 'emails-form-ng2',
    template:  scriptTmpl("emails-form-ng2-template")
})
export class EmailsFormComponent implements AfterViewInit, OnDestroy, OnInit {
    private ngUnsubscribe: Subject<void> = new Subject<void>();
    private subscription: Subscription;

    /* On the template:
    <td ng-init="emailStatusOptions = [{label:'<@orcid.msg "manage.email.current.true" />',val:true},{label:'<@orcid.msg "manage.email.current.false" />',val:false}];">
    */
    ///account/email/visibility and /account/email/setPrimary
    @Input() popUp: any;

    defaultVisibility: any;
    emails: any;
    emailStatusOptions: any;
    formData: any;
    formDataBeforeChange: any;
    newElementDefaultVisibility: any;
    orcidId: any;
    privacyHelp: any;
    scrollTop: any;
    showEdit: any;
    showElement: any;
    showEditEmail: boolean;
    emailsEditText: string;
    //popUp: boolean;
    showUnverifiedEmailSetPrimaryBox: boolean;
    primaryEmail: string;
    verifyEmailObject: any;
    showEmailVerifBox: boolean;
    isPassConfReq: any;
    baseUri: any;
    curPrivToggle: any;
    
    password: any;
  
    showConfirmationBox: any;
    showDeleteBox: any;

    position: any;
    inputEmail: any;
    prefs: any;
    email_frequency: any;

    constructor( 
        private elementRef: ElementRef, 
        private emailService: EmailService,
        private commonSrvc: CommonService,
        private modalService: ModalService,
        private prefsSrvc: PreferencesService
    ) {
        this.verifyEmailObject = {};
        this.showEmailVerifBox = false;
        this.baseUri = orcidVar.baseUri;
        this.curPrivToggle = null;
        this.isPassConfReq = orcidVar.isPasswordConfirmationRequired;
        this.password = null;
        this.privacyHelp = {};
        this.scrollTop = 0;
        this.showConfirmationBox = false;
        this.showDeleteBox = false;
        this.showElement = {};
        this.showEmailVerifBox = false;
        this.verifyEmailObject = {};
        this.position = 0;

        this.isPassConfReq = orcidVar.isPasswordConfirmationRequired;
        this.defaultVisibility = null;
        this.emails = {};
        this.emailStatusOptions = null;
        this.formData = {
            emails: null,
            visibility: {
                visibility: this.defaultVisibility
            }
        };
        this.formDataBeforeChange = {};
        this.newElementDefaultVisibility = 'PRIVATE';
        this.orcidId = orcidVar.orcidId; 
        this.privacyHelp = false;
        this.scrollTop = 0;
        this.showEdit = false;
        this.showElement = {};
        this.showEditEmail = (window.location.hash === "#editEmail")
        this.emailsEditText = om.get("manage.edit.emails");
        //this.popUp = true;
        this.showUnverifiedEmailSetPrimaryBox = false;
        this.primaryEmail = '';
        this.emailStatusOptions = [
            {
                label:'Current',
                val:true
            },
            {
                label:'Past',
                val:false
            }
        ];
        this.inputEmail = {
            "current":true,
            "errors":[],
            "primary":false,
            "value":"",
            "verified":false,
            "visibility":"PRIVATE"
        };
        this.prefs = {};
        this.popUp = elementRef.nativeElement.getAttribute('popUp');
        //this.email_frequency = null;

    }

    addNew(): void {
        let tmpObj = {
            "errors":[],
            "url":null,
            "urlName":null,
            "putCode":null,
            "visibility":{
                "errors":[],
                "required":true,
                "getRequiredMessage":null,
                "visibility": this.newElementDefaultVisibility
            },
            "source":this.orcidId,
            "sourceName":"", 
            "displayIndex": 1
        };        
        this.formData.emails.push(tmpObj);        
        this.updateDisplayIndex();    
    };

    checkCredentials(popup): void {
        this.password = null;
        if(orcidVar.isPasswordConfirmationRequired){            
            this.showConfirmationBox = true;            
        }else{
            this.submitModal(this.inputEmail);
        }
    };

    initInputEmail(): void {
        this.inputEmail = {
            "current":true,
            "errors":[],
            "primary":false,
            "value":"",
            "verified":false,
            "visibility":"PRIVATE"
        };
    }

    submitModal(obj?): void {
        
        if( orcidVar.isPasswordConfirmationRequired == true ){
            this.inputEmail.password = this.password;
        }
        if( obj.value ) {
            this.emailService.addEmail( obj )
            .takeUntil(this.ngUnsubscribe)
            .subscribe(
                data => {
                    this.getformData();
                    this.inputEmail = data;
                    this.emailService.notifyOther();

                    if (this.inputEmail.errors.length == 0) {
                        this.initInputEmail();
                    }
                },
                error => {
                    ////console.log('getEmailsFormError', error);
                } 
            );
            this.inputEmail.value = "";
        }
        
    };

    getPrivacyPreferences(): void {
        this.prefsSrvc.getPrivacyPreferences()
        .takeUntil(this.ngUnsubscribe)
        .subscribe(
            data => {
                this.prefs = data;
            },
            error => {
                ////console.log('getEmailsFormError', error);
            } 
        );
    }

    updateEmailFrequency(): void {
        this.prefsSrvc.updateEmailFrequency( this.prefs )
        .takeUntil(this.ngUnsubscribe)
        .subscribe(
            data => {
            },
            error => {
                ////console.log('getEmailsFormError', error);
            } 
        );
    };

    clearMessage(): void {
        this.prefsSrvc.clearMessage();
    }

    closeEditModal(): void {
        this.formData = this.formDataBeforeChange;
        this.modalService.notifyOther({action:'close', moduleId: 'modalEmails'});
    };

    closeUnverifiedEmailSetPrimaryBox(): void{
        this.showUnverifiedEmailSetPrimaryBox = false;
    };

    confirmDeleteEmail(email): void {
        this.emailService.delEmail = email;        
        this.emailService.deleteEmail()
        .takeUntil(this.ngUnsubscribe)
        .subscribe(
            data => {
                this.getformData();
                this.emailService.notifyOther();
            },
            error => {
                ////console.log('getEmailsFormError', error);
            } 
        );
    };

    closeDeleteBox(): void {
        this.showDeleteBox = false;
    };

    deleteEmailInline(): void {
        this.emailService.deleteEmail()
        .takeUntil(this.ngUnsubscribe)
        .subscribe(
            data => {
                this.getformData();
                //this.inputEmail.value = "";
                this.emailService.notifyOther();
            },
            error => {
                ////console.log('getEmailsFormError', error);
            } 
        );
        this.showDeleteBox = false;            
    };

    confirmDeleteEmailInline(email, $event): void {
        $event.preventDefault();
        this.showDeleteBox = true;
        this.emailService.delEmail = email;                
    };

    setPrimary( email ): void {
        this.emailService.setPrimary( email )
        .takeUntil(this.ngUnsubscribe)
        .subscribe(
            data => {
                let tempData = null;
                this.formDataBeforeChange = JSON.parse(JSON.stringify(data));
                
                this.getformData();

                if ( data.verified == false ) {
                    this.showUnverifiedEmailSetPrimaryBox = true;
                } else {
                    this.showUnverifiedEmailSetPrimaryBox = false;
                }
            },
            error => {
                ////console.log('getEmailsFormError', error);
            } 
        );
    };

    getformData(): void {
        this.emailService.getData()
        .takeUntil(this.ngUnsubscribe)
        .subscribe(
            data => {
                this.formDataBeforeChange = JSON.parse(JSON.stringify(data));
                this.formData = data;

                if ( this.formData.emails.length == 0 ) {
                    this.addNew();
                } else {
                    for( let i = 0; i < data.emails.length; i++ ){
                        if( data.emails[i].primary == true ) {
                            this.primaryEmail = data.emails[i].value;
                        }

                    }
                }

            },
            error => {
                ////console.log('getEmailsFormError', error);
            } 
        );
    };

    privacyChange( $event, obj ): any {

        this.emailService.setEmailPrivacy( obj )
        .takeUntil(this.ngUnsubscribe)
        .subscribe(
            data => {
            },
            error => {
                ////console.log('setEmailsKnownAs', error);
            } 
        ); 
    };

    saveEmail( closeAfterAction ): void {
        this.emailService.saveEmail( this.formData )
        .takeUntil(this.ngUnsubscribe)
        .subscribe(
            data => {
                this.formData = data;
                if (this.formData.errors.length == 0){
                    this.getformData();
                    this.emailService.notifyOther();
                    if( closeAfterAction == true ) {
                        this.closeEditModal();
                    }
                }else{
                    ////console.log(this.formData.errors);
                }

            },
            error => {
                ////console.log('setEmailsKnownAs', error);
            } 
        );
        this.formData.visibility = null;
    }

    closeVerificationBox(): void {
        this.showEmailVerifBox = false;
    };

    verifyEmail(email, popup): void {

        this.verifyEmailObject = email;
        
        this.emailService.verifyEmail( email )
        .takeUntil(this.ngUnsubscribe)
        .subscribe(
            data => {
            },
            error => {
                ////console.log('setEmailsKnownAs', error);
            } 
        );
        this.showEmailVerifBox = true;

        if( !popup ){
            this.modalService.notifyOther({action:'open', moduleId: 'emailSentConfirmation'});   
        }
        
    };

    updateDisplayIndex(): void{
        let idx: any;
    };

    //Default init functions provided by Angular Core
    ngAfterViewInit() {
        if(this.popUp == "true" ){
            this.popUp = true;
        } else {
            this.popUp = false;
        }
    };

    ngOnDestroy() {
        this.ngUnsubscribe.next();
        this.ngUnsubscribe.complete();
    };

    ngOnInit() {
        this.getPrivacyPreferences();
        //this.getformData();  
    };

}
