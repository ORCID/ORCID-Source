import { NgForOf, NgIf } 
    from '@angular/common'; 

import { AfterViewInit, Component, ElementRef, EventEmitter, Input, OnChanges, OnDestroy, OnInit, Output } 
    from '@angular/core';

import { Observable, Subject, Subscription } 
    from 'rxjs';
import { takeUntil } 
    from 'rxjs/operators';

import { EmailService } 
    from '../../shared/email.service';

import { PreferencesService }
    from '../../shared/preferences.service';

import { CommonService } 
    from '../../shared/common.service';

import { ModalService } 
    from '../../shared/modal.service'; 

import { FeaturesService }
    from '../../shared/features.service';

import { EmailFrequencyService }
    from '../../shared/emailFrequency.service';    
    
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
    MAX_EMAIL_COUNT: number = 30;

    TOGGLZ_HIDE_UNVERIFIED_EMAILS: boolean;
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
    emailCurrentLabel: string;
    emailPastLabel: string;
    emailsEditText: string;
    showUnverifiedEmailSetPrimaryBox: boolean;
    primaryEmail: string;
    verifyEmailObject: any;
    showEmailVerifBox: boolean;
    showEmailVerifBoxNewsTips: boolean;
    isPassConfReq: any;
    baseUri: any;
    curPrivToggle: any; 
    password: any;
    showConfirmationBox: any;
    showDeleteBox: any;
    position: any;
    inputEmail: any;
    prefs: any;        
    sendChangeNotifications: string;
    sendAdministrativeChangeNotifications: string;
    sendMemberUpdateRequestsNotifications: string;
    sendQuarterlyTips: boolean;
    aboutUri: String;
    emailFrequencyOptions: any;
    
    constructor( 
        private elementRef: ElementRef, 
        private emailService: EmailService,
        private commonSrvc: CommonService,
        private modalService: ModalService,
        private featuresService: FeaturesService,
        private prefsSrvc: PreferencesService,
        private emailFrequencyService: EmailFrequencyService
    ) {
        this.TOGGLZ_HIDE_UNVERIFIED_EMAILS = this.featuresService.isFeatureEnabled('HIDE_UNVERIFIED_EMAILS');
        this.verifyEmailObject = {};
        this.showEmailVerifBox = false;
        this.showEmailVerifBoxNewsTips = false;
        this.baseUri = getBaseUri();
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
        this.showUnverifiedEmailSetPrimaryBox = false;
        this.primaryEmail = '';
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
        this.commonSrvc.configInfo$
        .subscribe(
            data => {                
                this.aboutUri = data.messages['ABOUT_URI'];
            },
            error => {
                console.log('emailsForm.component.ts: unable to fetch configInfo', error);
            } 
        );
        
        om.process().then(() => {
            this.emailCurrentLabel = om.get("manage.email.current");
            this.emailPastLabel = om.get("manage.email.past");
            this.emailsEditText = om.get("manage.edit.emails");
            this.emailStatusOptions = [
                {
                    label: this.emailCurrentLabel,
                    val:true
                },
                {
                    label: this.emailPastLabel,
                    val:false
                }
            ];
        });
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
            .pipe(    
            takeUntil(this.ngUnsubscribe)
        )
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
        .pipe(    
            takeUntil(this.ngUnsubscribe)
        )
        .subscribe(
            data => {
                this.prefs = data;
            },
            error => {
                ////console.log('getEmailsFormError', error);
            } 
        );
    }

    getEmailFrequencies(): void {
        this.emailFrequencyService.getEmailFrequencies()
        .pipe(    
            takeUntil(this.ngUnsubscribe)
        )
        .subscribe(
            data => {                
                this.sendChangeNotifications = data['send_change_notifications']
                this.sendAdministrativeChangeNotifications = data['send_administrative_change_notifications']
                this.sendMemberUpdateRequestsNotifications = data['send_member_update_requests']
                this.sendQuarterlyTips = data['send_quarterly_tips']  == "true";                
            },
            error => {
                ////console.log('getEmailsFormError', error);
            } 
        );
    }
    
    updateChangeNotificationsFrequency(): void {
        this.emailFrequencyService.updateFrequency('send_change_notifications', this.sendChangeNotifications)
        .pipe(    
            takeUntil(this.ngUnsubscribe)
        ).subscribe(data => {}, error => {console.log('Error changing frequency', error)});
    }
    
    updateAdministrativeChangeNotificationsFrequency(): void {
        this.emailFrequencyService.updateFrequency('send_administrative_change_notifications', this.sendAdministrativeChangeNotifications)
        .pipe(    
            takeUntil(this.ngUnsubscribe)
        ).subscribe(data => {}, error => {console.log('Error changing frequency', error)});
    }
    
    updateMemberUpdateRequestsFrequency(): void {
        this.emailFrequencyService.updateFrequency('send_member_update_requests', this.sendMemberUpdateRequestsNotifications)
        .pipe(    
            takeUntil(this.ngUnsubscribe)
        ).subscribe(data => {}, error => {console.log('Error changing frequency', error)});
    }
    
    updateSendQuarterlyTips(): void {
        this.emailFrequencyService.updateFrequency('send_quarterly_tips', this.sendQuarterlyTips)
        .pipe(    
            takeUntil(this.ngUnsubscribe)
        ).subscribe(data => {}, error => {console.log('Error changing frequency', error)});
    }
    
    updateEmailFrequency(): void {
        this.prefsSrvc.updateEmailFrequency( this.prefs )
        .pipe(    
            takeUntil(this.ngUnsubscribe)
        )
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
        .pipe(    
            takeUntil(this.ngUnsubscribe)
        )
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
        .pipe(    
            takeUntil(this.ngUnsubscribe)
        )
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
        .pipe(    
            takeUntil(this.ngUnsubscribe)
        )
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
        .pipe(    
            takeUntil(this.ngUnsubscribe)
        )
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
        .pipe(    
            takeUntil(this.ngUnsubscribe)
        )
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
        .pipe(    
            takeUntil(this.ngUnsubscribe)
        )
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

    closeVerificationBox(location?): void {
        if(location){
            if(location == "newsTips"){
                this.showEmailVerifBoxNewsTips = false;

            }
        } else {
            this.showEmailVerifBox = false;
        }
    };

    verifyEmail(email, popup, location?): void {

        this.verifyEmailObject = email;
        
        this.emailService.verifyEmail( email )
        .pipe(    
            takeUntil(this.ngUnsubscribe)
        )
        .subscribe(
            data => {
            },
            error => {
                ////console.log('setEmailsKnownAs', error);
            } 
        );
        if(location){
            if(location == "newsTips"){
                this.showEmailVerifBoxNewsTips = true;

            }
        } else {
            this.showEmailVerifBox = true;
        }

        if( !popup ){
            this.modalService.notifyOther(
                {
                    action:'open', 
                    moduleId: 'emailSentConfirmation', 
                    data: {
                        email: email
                    }
                }
            );   
        }
        
    };
    
    getEmailFrequencyOptions(): void {
        this.commonSrvc.getEmailFrequencyOptions()
        .pipe(    
            takeUntil(this.ngUnsubscribe)
        )
        .subscribe(
            data => {
                this.emailFrequencyOptions = data;
            },
            error => {
                console.log('error getting email frequency options');
            } 
        );
    }

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
        this.getEmailFrequencyOptions();
        this.getPrivacyPreferences();
        this.getformData();          
        this.getEmailFrequencies(); 

        //Subscribe to emailChange event and 
        //update data when emails changed by another component
        this.emailService.emailsChange.subscribe(emailListUpdated => {
            if (emailListUpdated == true){
                this.getformData(); 
            }
        });       
    };

}
