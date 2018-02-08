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
        this.showUnverifiedEmailSetPrimaryBox = false;
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
            if (!popup){
                /*
                $.colorbox({
                    html: $compile($('#check-password-modal').html())($scope)
                });
                $.colorbox.resize();
                */
            }else{
                this.showConfirmationBox = true;            
            }
        }else{
            this.submitModal();
        }
    };

    submitModal(obj?): void {
        
        this.emailService.inputEmail.password = this.password;

        this.emailService.addEmail()
        .takeUntil(this.ngUnsubscribe)
        .subscribe(
            data => {
            },
            error => {
                console.log('getEmailsFormError', error);
            } 
        );
        
    };

    getPrivacyPreferences(): void {
        this.prefsSrvc.getPrivacyPreferences()
        .takeUntil(this.ngUnsubscribe)
        .subscribe(
            data => {
                this.prefs = data;
            },
            error => {
                console.log('getEmailsFormError', error);
            } 
        );
    }

    updateEmailFrequency(): void {
        this.prefsSrvc.updateEmailFrequency( this.prefs.email_frequency )
        .takeUntil(this.ngUnsubscribe)
        .subscribe(
            data => {
            },
            error => {
                console.log('getEmailsFormError', error);
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
        /*
        $.colorbox({
            html : $compile($('#delete-email-modal').html())($scope)
        });
        $.colorbox.resize();
        */
        this.emailService.deleteEmail()
        .takeUntil(this.ngUnsubscribe)
        .subscribe(
            data => {
            },
            error => {
                console.log('getEmailsFormError', error);
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
            },
            error => {
                console.log('getEmailsFormError', error);
            } 
        );
        this.showDeleteBox = false;            
    };

    confirmDeleteEmailInline(email, $event): void {
        $event.preventDefault();
        this.showDeleteBox = true;
        this.emailService.delEmail = email;
        
        /*
        $scope.$watch(
            function () {
                return document.getElementsByClassName('delete-email-box').length; 
            },
            function (newValue, oldValue) {             
                $.colorbox.resize();
            }
        );
        */
    };

    setPrimary( email ): void {
        this.emailService.setPrimary( email )
        .takeUntil(this.ngUnsubscribe)
        .subscribe(
            data => {
                this.formDataBeforeChange = JSON.parse(JSON.stringify(data));
                this.formData = data;
                //this.newElementDefaultVisibility = this.formData.visibility.visibility;
                console.log('this.getForm emails', this.formData);
                if ( this.formData.emails.length == 0 ) {
                    this.addNew();
                }
                for( let i; i < data.length; i++ ){
                    if( data.primary == true ) {
                        this.primaryEmail = data.value;
                        if( data.primary == false ) {
                            this.showUnverifiedEmailSetPrimaryBox = true;
                        } else {
                            this.showUnverifiedEmailSetPrimaryBox = false;
                        }
                    }

                }
            },
            error => {
                console.log('getEmailsFormError', error);
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
                }else {
                    for( let i; i < data.length; i++ ){
                        if( data.primary == true ) {
                            this.primaryEmail = data.value;
                            if( data.primary == false ) {
                                this.showUnverifiedEmailSetPrimaryBox = true;
                            } else {
                                this.showUnverifiedEmailSetPrimaryBox = false;
                            }
                        }

                    }
                }
            },
            error => {
                console.log('getEmailsFormError', error);
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
                console.log('setEmailsKnownAs', error);
            } 
        ); 
    };

    saveEmail( closeAfterAction ): void {
        this.emailService.setData( this.formData )
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
                    console.log(this.formData.errors);
                }

            },
            error => {
                console.log('setEmailsKnownAs', error);
            } 
        );
        this.formData.visibility = null;
    }

    closeVerificationBox(): void {
        this.showEmailVerifBox = false;
    };

    verifyEmail(email, popup): void {
        this.verifyEmailObject = email;
        if( popup ){
            this.emailService.verifyEmail( email )
            .takeUntil(this.ngUnsubscribe)
            .subscribe(
                data => {
                },
                error => {
                    console.log('setEmailsKnownAs', error);
                } 
            );
            this.showEmailVerifBox = true;
    
        }else{
            this.modalService.notifyOther({action:'open', moduleId: 'emailSentConfirmation'});   
        }
        
    };

    updateDisplayIndex(): void{
        let idx: any;
        /*
        for (idx in this.formData.otherNames) {         
            this.formData.otherNames[idx]['displayIndex'] = this.formData.otherNames.length - idx;
        }
        */
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
        this.getformData();  
    };

}
