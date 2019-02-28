declare var orcidGA: any;
declare var orcidVar: any;

//Import all the angular components

import { NgForOf, NgIf } 
    from '@angular/common'; 

import { AfterViewInit, ChangeDetectorRef, Component, OnDestroy, OnInit, EventEmitter, Output } 
    from '@angular/core';

import { Observable, Subject, Subscription } 
    from 'rxjs';
import { takeUntil } 
    from 'rxjs/operators';

import { OauthService } 
    from '../../shared/oauth.service.ts'; 

import { CommonService } 
    from '../../shared/common.service.ts';

import { FeaturesService } 
    from '../../shared/features.service.ts'; 

import { ReactivationService } 
    from '../../shared/reactivation.service.ts'; 

@Component({
    selector: 'reactivation-ng2',
    template:  scriptTmpl("reactivation-ng2-template")
})
export class ReactivationComponent implements AfterViewInit, OnDestroy, OnInit {    
    private ngUnsubscribe: Subject<void> = new Subject<void>();

    privacyHelp: any;
    registrationForm: any;
    showReactivationSent: boolean = false;
    showDeactivatedError: boolean = false;
    showDuplicateEmailError: boolean = false;
    errorEmailsAdditional: any;
    showEmailsAdditionalDeactivatedError: any;
    showEmailsAdditionalReactivationSent: any;
    showEmailsAdditionalDuplicateEmailError: any;
    reactivationData: any;
    
    constructor(
        private oauthService: OauthService,
        private commonSrvc: CommonService,
        private featuresService: FeaturesService,
        private reactivationService: ReactivationService,
        private cdr:ChangeDetectorRef
    ) {
        this.privacyHelp = {};
        this.errorEmailsAdditional = [false];        
        this.showEmailsAdditionalDuplicateEmailError = [false];
        this.showEmailsAdditionalReactivationSent = [false]
        this.showEmailsAdditionalDeactivatedError = [false];
        this.registrationForm = {
                "activitiesVisibilityDefault": {
                    "value": null,
                    "errors": []
                },
                "errors": [],
                "familyNames": {
                    "value": "",
                    "errors": []
                },
                "givenNames": {
                    "value": "",
                    "errors": []
                },
                "email": {
                    "value": "",
                    "errors": []
                },
                "emailsAdditional": [{
                    "errors": [],
                    "value": null,
                    "required": false,
                    "getRequiredMessage": null
                }],
                "password": {
                    "value": "",
                    "errors": []
                },
                "passwordConfirm": {
                    "value": "",
                    "errors": []
                },
                "termsOfUse": {
                    "value": false, 
                    "errors": []
                }                                                
            }; 
    }

    isValidClass(cur) : string {
        return this.commonSrvc.isValidClass(cur);
    };
    
    getReactivation(resetParams, linkFlag): void {
        this.oauthService.oauth2ScreensLoadRegistrationForm( )
        .pipe(    
            takeUntil(this.ngUnsubscribe)
        )
        .subscribe(
            data => {
                this.registrationForm = data;
                this.registrationForm.resetParams = resetParams;
                this.registrationForm.activitiesVisibilityDefault.visibility = null;
                this.cdr.detectChanges();              
            },
            error => {
                // something bad is happening!
                console.log("error fetching register.json");
            } 
        );
    };
    
     getReactivationData(resetParams): void {
        this.reactivationService.getReactivationData(resetParams)
        .pipe(    
            takeUntil(this.ngUnsubscribe)
        )
        .subscribe(
            data => {
                this.reactivationData = data;
                this.registrationForm.email.value = this.reactivationData.email;
            },
            error => {
                // something bad is happening!
                console.log("error fetching reactivation data");
            } 
        );
    };

    postReactivationConfirm(): void {
        this.registrationForm.valNumClient = this.registrationForm.valNumServer / 2;
        var baseUri = getBaseUri();                
        if(this.registrationForm.linkType === 'shibboleth'){
            baseUri += '/shibboleth';
        }
        this.reactivationService.postReactivationConfirm(this.registrationForm)
        .pipe(    
            takeUntil(this.ngUnsubscribe)
        )
        .subscribe(
            data => {
                if(data.errors.length == 0){
                    window.location.href = data.url;
                }
                else{
                    this.registrationForm = data;
                    for (var index in this.registrationForm.emailsAdditional) {
                        if (this.registrationForm.emailsAdditional[index].errors.length > 0) {      
                            this.errorEmailsAdditional[index] = data.emailsAdditional[index].value;     
                            //deactivated error
                            this.showEmailsAdditionalDeactivatedError.splice(index, 1, ($.inArray('orcid.frontend.verify.deactivated_email', this.registrationForm.emailsAdditional[index].errors) != -1));
                            this.showEmailsAdditionalReactivationSent.splice(index, 1, false);
                            //duplicate email error
                            this.showEmailsAdditionalDuplicateEmailError.splice(index, 1, ($.inArray('orcid.frontend.verify.duplicate_email', this.registrationForm.emailsAdditional[index].errors) != -1));                            
                        } else {
                            this.showEmailsAdditionalDeactivatedError[index] = false;
                            this.showEmailsAdditionalDuplicateEmailError[index] = false;
                        } 
                    }
                    this.cdr.detectChanges();
                } 
            },
            error => {
                // something bad is happening!
                console.log("ReactivationCtrl.postReactivationConfirm() error");
            } 
        );
    };

    serverValidate(field): void {
        if (field === undefined) {
            field = '';
        }
        this.reactivationService.serverValidate(this.registrationForm, field)
        .pipe(    
            takeUntil(this.ngUnsubscribe)
        )
        .subscribe(
            data => {
                this.commonSrvc.copyErrorsLeft(this.registrationForm, data);
            },
            error => {
                // something bad is happening!
                console.log("serverValidate() error");
            } 
        );
    };

    trimAjaxFormText(pojoMember): void {
        this.commonSrvc.trimAjaxFormText(pojoMember);
    }

    updateActivitiesVisibilityDefault(priv, $event): void {
        this.registrationForm.activitiesVisibilityDefault.visibility = priv;
    };
    
    sendReactivationEmail(email): void {        
        this.oauthService.sendReactivationEmail(email)
        .pipe(    
            takeUntil(this.ngUnsubscribe)
        )
        .subscribe(
            data => {
                this.showReactivationSent = true;
                this.cdr.detectChanges();
            },
            error => {
                console.log("error sending reactivation email");
            } 
        );
    };
    
    addEmailField(): void {
        this.registrationForm.emailsAdditional.push({value: ''});
        this.cdr.detectChanges();       
    }; 
    
    removeEmailField(index): void {
        this.registrationForm.emailsAdditional.splice(index, 1);
        this.cdr.detectChanges();
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
        var segments = window.location.href.split('/');
        var resetParams = segments[segments.length - 1];
        this.getReactivation(resetParams, '');
        this.getReactivationData(resetParams);
    };


}