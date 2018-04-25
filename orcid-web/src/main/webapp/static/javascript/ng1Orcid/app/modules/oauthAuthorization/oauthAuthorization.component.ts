declare var $: any; //delete
declare var OrcidCookie: any;
declare var orcidVar: any;
declare var orcidGA: any;
declare var addShibbolethGa: any;
declare var getBaseUri: any;
declare var getStaticCdnPath: any;
declare var orcidGA: any;
declare var orcidVar: any;

import { NgForOf, NgIf } 
    from '@angular/common'; 

import { AfterViewInit, Component, OnDestroy, OnInit, ChangeDetectorRef, ViewChild, NgZone  } 
    from '@angular/core';

import { ReCaptchaComponent } 
    from 'angular2-recaptcha';

import { Observable } 
    from 'rxjs/Rx';

import { Subject } 
    from 'rxjs/Subject';

import { Subscription }
    from 'rxjs/Subscription';

import { CommonNg2Module }
    from './../common/common.ts';

import { CommonService } 
    from '../../shared/common.service.ts';

import { FeaturesService }
    from '../../shared/features.service.ts'

import { ModalService } 
    from '../../shared/modal.service.ts'; 

import { OauthService } 
    from '../../shared/oauth.service.ts';


@Component({
    selector: 'oauth-authorization-ng2',
    template:  scriptTmpl("oauth-authorization-ng2-template")
})
export class OauthAuthorizationComponent implements AfterViewInit, OnDestroy, OnInit {
    @ViewChild(ReCaptchaComponent) captcha: ReCaptchaComponent;

    private ngUnsubscribe: Subject<void> = new Subject<void>();
    private subscription: Subscription;

    res: any;

    allowEmailAccess: any;
    authorizationForm: any;
    counter: any;
    currentLanguage: any;
    duplicates: any;
    gaString: any;
    focusIndex: any;
    enablePersistentToken: any;
    errorEmail: any;
    errorEmailsAdditional: any;
    isOrcidPresent: any;
    oauthSignin: any;
    personalLogin: any;
    recaptchaWidgetId: any;
    recatchaResponse: any;
    requestInfoForm: any;
    registrationForm: any;
    scriptsInjected: any;
    site_key: any;
    showBulletIcon: any;
    showClientDescription: any;
    showDeactivatedError: any;
    showDuplicateEmailError: any;
    showEmailsAdditionalDeactivatedError: any;
    showEmailsAdditionalDuplicateEmailError: any;
    showEmailsAdditionalReactivationSent: any;
    showGeneralRegistrationError: any;
    showLimitedIcon: any;
    showLongDescription: any;
    showReactivationSent: any;
    showRecaptcha: any
    showRegisterForm: any;
    showRegisterProcessing: any;
    showUpdateIcon: any;
    socialSignInForm: any;
    loadTime: any;
    generalRegistrationError: any;
    //registration form togglz features
    regMultiEmailFeatureEnabled: boolean = this.featuresService.isFeatureEnabled('REG_MULTI_EMAIL');
    gdprUiFeatureEnabled: boolean = this.featuresService.isFeatureEnabled('GDPR_UI');
    disableRecaptchaFeatureEnabled: boolean = this.featuresService.isFeatureEnabled('DISABLE_RECAPTCHA');
    gdprEmailNotifications: boolean = this.featuresService.isFeatureEnabled('GDPR_EMAIL_NOTIFICATIONS');
    
    constructor(
        private zone:NgZone,
        private cdr:ChangeDetectorRef,
        private commonSrvc: CommonService,
        private featuresService: FeaturesService,
        private modalService: ModalService,
        private oauthService: OauthService
    ) {
        window['angularComponentReference'] = {
            zone: this.zone,
            showDeactivationError: () => this.showDeactivationError(),
            component: this,
        };

        this.allowEmailAccess = true;
        this.authorizationForm = {
            userName: {
                value: ""
            }
        };
        this.counter = 0;
        this.currentLanguage = OrcidCookie.getCookie('locale_v3');
        this.duplicates = {};
        this.gaString = null;
        this.enablePersistentToken = true;
        this.errorEmail = null;
        this.errorEmailsAdditional = [];
        this.focusIndex = null;
        this.isOrcidPresent = false;
        this.site_key = orcidVar.recaptchaKey;
        this.oauthSignin = false;
        this.personalLogin = true;
        this.recaptchaWidgetId = null;
        this.recatchaResponse = null;
        this.requestInfoForm = null;    
        this.registrationForm = {};
        this.scriptsInjected = false;
        this.showBulletIcon = false;
        this.showClientDescription = false;
        this.showDeactivatedError = false;
        this.showDuplicateEmailError = false;
        this.showEmailsAdditionalDeactivatedError = [false];
        this.showEmailsAdditionalDuplicateEmailError = [false];
        this.showEmailsAdditionalReactivationSent = [false];
        this.showGeneralRegistrationError = false
        this.showLimitedIcon = false;    
        this.showLongDescription = {};
        this.showRecaptcha = true;
        this.showReactivationSent = false;
        this.showRegisterForm = false;
        this.showRegisterProcessing = false;
        this.showUpdateIcon = false;
        this.socialSignInForm = {};
        this.loadTime = 0;
        this.generalRegistrationError = null;
    }

    addScript(url, onLoadFunction): void {      
        let head = document.getElementsByTagName('head')[0];
        let script = document.createElement('script');
        script.src = getStaticCdnPath() + url;
        script.onload =  onLoadFunction;
        head.appendChild(script); // Inject the script
    }; 

    authorize(): void {
        this.authorizationForm.approved = true;
        this.authorizeRequest();
    };

    deny(): void {
        this.authorizationForm.approved = false;
        orcidGA.gaPush(['send', 'event', 'Disengagement', 'Authorize_Deny', 'OAuth ' + this.gaString]);
        this.authorizeRequest();
    };

    isValidClass(cur): any {
        var valid;
        if (cur === undefined) {
            return '';
        } 
        valid = true;
        if (cur.required && (cur.value == null || cur.value.trim() == '')) {
            valid = false;
        }
        if (cur.errors !== undefined && cur.errors.length > 0) {
            valid = false;
        }
        return valid ? '' : 'text-error';
    };

    loginSocial(idp): void {
        if(this.gaString){
            orcidGA.gaPush(['send', 'event', 'RegGrowth', 'Sign-In-Submit' , 'OAuth ' + this.gaString]);
        } else {
            orcidGA.gaPush(['send', 'event', 'RegGrowth', 'Sign-In-Submit' , 'Website']);
        }
        orcidGA.gaPush(['send', 'event', 'RegGrowth', 'Sign-In-Submit-Social', idp ]);
    };


    showDeactivationError(): void {
        this.showDeactivatedError = true;
        this.showReactivationSent = false;
    };

    showInstitutionLogin(): void  {

        this.personalLogin = false; // Hide Personal Login
        
        if(!this.scriptsInjected){ // If shibboleth scripts haven't been
                                        // loaded yet.

            let scriptInjectedCallback = function () {
                    this.scriptsInjected = true;
                    addShibbolethGa(this.gaString);
                };

            this.addScript('/javascript/shibboleth-embedded-ds/1.1.0/idpselect_config.js', this.addScript.bind(this, '/javascript/shibboleth-embedded-ds/1.1.0/idpselect.js', scriptInjectedCallback.bind(this)));
        };
    };


    showPersonalLogin(): void {        
        this.personalLogin = true;
    };

    switchForm(): void {
        var re = new RegExp("(/register)(.*)?$");
        if (this.registrationForm.linkType=="social") {
            window.location.href = getBaseUri() + "/social/access";
        } else if (this.registrationForm.linkType=="shibboleth") {
            window.location.href = getBaseUri() + "/shibboleth/signin";
        } else if(re.test(window.location.pathname)){
            window.location.href = getBaseUri() + "/signin";
        } else {
            this.showRegisterForm = !this.showRegisterForm;
            if (!this.personalLogin) {
                this.personalLogin = true;
            }
        }
        this.cdr.detectChanges();
    };

    toggleClientDescription(): void {
        this.showClientDescription = !this.showClientDescription;
    };

    toggleLongDescription(orcid_scope): void {              
        this.showLongDescription[orcid_scope] = !this.showLongDescription[orcid_scope];
    };

    updateActivitiesVisibilityDefault(priv, $event): void {
        this.registrationForm.activitiesVisibilityDefault.visibility = priv;
    };

    addEmailField(): void {
        this.registrationForm.emailsAdditional.push({value: ''});
        this.focusIndex = this.registrationForm.emailsAdditional.length-1;
    };  

    removeEmailField(index): void {
        this.registrationForm.emailsAdditional.splice(index, 1);
        this.cdr.detectChanges();
    };

    authorizeRequest(): void {
        let auth_scope_prefix = null;
        let is_authorize = null;

        auth_scope_prefix = 'Authorize_';
        if(this.enablePersistentToken) {
            this.authorizationForm.persistentTokenEnabled=true;
            auth_scope_prefix = 'AuthorizeP_';
        }
        if(this.allowEmailAccess) {
            this.authorizationForm.emailAccessAllowed = true;
        }
        is_authorize = this.authorizationForm.approved;

        this.oauthService.authorizeRequest( this.authorizationForm )
        .takeUntil(this.ngUnsubscribe)
        .subscribe(
            data => {
                if(is_authorize) {
                    for(var i = 0; i < this.requestInfoForm.scopes.length; i++) {
                        orcidGA.gaPush(['send', 'event', 'RegGrowth', auth_scope_prefix + this.requestInfoForm.scopes[i].name, 'OAuth ' + this.gaString]);
                    }
                }
                orcidGA.windowLocationHrefDelay(data.redirectUrl);

            },
            error => {
                console.log("An error occured authorizing the user.");
            } 
        );
    };

    getAffiliations( dup ): void{
        if(!dup['affiliationsRequestSent']){
            dup['affiliationsRequestSent'] = true;
            dup['institution'] = [];
            var orcid = dup.orcid;
            var url = orcidVar.pubBaseUri + '/v2.1/' + orcid + '/activities';

            this.oauthService.getAffiliations( url )
            .takeUntil(this.ngUnsubscribe)
            .subscribe(
                data => {
                    if(data.employments){
                        for(var i in data.employments['employment-summary']){
                            if (dup['institution'].indexOf(data.employments['employment-summary'][i]['organization']['name']) < 0){
                                dup['institution'].push(data.employments['employment-summary'][i]['organization']['name']);
                            }
                        }
                    }
                    if(data.educations){
                        for(var i in data.educations['education-summary']){
                            if (dup['institution'].indexOf(data.educations['education-summary'][i]['organization']['name']) < 0){
                                dup['institution'].push(data.educations['education-summary'][i]['organization']['name']);
                            }
                        }
                    }

                },
                error => {
                    console.log("Error getting affiliations");
                } 
            );
        }
    };

    loadAndInitAuthorizationForm(): void{

        this.oauthService.loadAndInitAuthorizationForm( )
        .takeUntil(this.ngUnsubscribe)
        .subscribe(
            data => {
                this.authorizationForm = data;

            },
            error => {
                console.log("An error occured initializing the authorization form.");
            } 
        );

    };  

    loadRequestInfoForm(): void{
        this.oauthService.loadRequestInfoForm( )
        .takeUntil(this.ngUnsubscribe)
        .subscribe(
            data => {
                if(data){
                    this.requestInfoForm = JSON.parse(data._body); 
                    this.requestInfoForm.scopes.forEach((scope) => {
                        if (scope.value.endsWith('/update')) {
                            this.showUpdateIcon = true;
                        } else if(scope.value.endsWith('/read-limited')) {
                            this.showLimitedIcon = true;
                        } else {
                            this.showBulletIcon = true;
                        }
                    });
          
                    this.gaString = orcidGA.buildClientString(this.requestInfoForm.memberName, this.requestInfoForm.clientName);
                }

            },
            error => {
                console.log("An error occured initializing the request info form.");
            } 
        );

    };

    oauth2ScreensLoadRegistrationForm(givenName, familyName, email, linkFlag): void{

        this.oauthService.oauth2ScreensLoadRegistrationForm( )
        .takeUntil(this.ngUnsubscribe)
        .subscribe(
            data => {
                this.registrationForm = data;

                if(givenName || familyName || email || linkFlag){
                    this.registrationForm.givenNames.value=givenName;
                    this.registrationForm.familyNames.value=familyName;
                    this.registrationForm.email.value=email;
                    this.registrationForm.linkType=linkFlag; 
                }

                if (this.gdprUiFeatureEnabled == true){
                    this.registrationForm.activitiesVisibilityDefault.visibility = null;
                }

                this.registrationForm.emailsAdditional=[{errors: [], getRequiredMessage: null, required: false, value: '',  }];                          
                
                this.showDeactivatedError = ($.inArray('orcid.frontend.verify.deactivated_email', this.registrationForm.email.errors) != -1);
                this.showReactivationSent = false;

                for (var index in this.registrationForm.emailsAdditional) {
                    this.showEmailsAdditionalDeactivatedError.splice(index, 1, ($.inArray('orcid.frontend.verify.deactivated_email', this.registrationForm.emailsAdditional[index].errors) != -1));
                    this.showEmailsAdditionalReactivationSent.splice(index, 1, false);
                } 
                this.cdr.detectChanges(); 
            },
            error => {
                console.log("An error occured initializing the registration form.");
            } 
        );

    };

    getDuplicates(): void{
        let url = getBaseUri() + '/dupicateResearcher.json?familyNames=' + this.registrationForm.familyNames.value + '&givenNames=' + this.registrationForm.givenNames.value;
        this.oauthService.getDuplicates( url )
        .takeUntil(this.ngUnsubscribe)
        .subscribe(
            data => {
                var diffDate = new Date();
                this.duplicates = data;
                // reg was filled out to fast reload the page
                if (this.loadTime + 5000 > diffDate.getTime()) {
                    window.location.reload();
                    return;
                }
                if (this.duplicates.length > 0 ) {
                    this.showRegisterProcessing = false;
                    this.modalService.notifyOther({action:'open', moduleId: 'modalRegisterDuplicates', duplicates: this.duplicates});
                } else {
                    this.oauth2ScreensPostRegisterConfirm();                          
                }

            },
            error => {
                // something bad is happening!
                console.log("error fetching dupicateResearcher.json");
                // continue to registration, as solr dup lookup failed.
                this.oauth2ScreensPostRegisterConfirm();
        } 
        );

    };

    sendReactivationEmail(email): void {
        this.oauthService.sendReactivationEmail(email)
        .takeUntil(this.ngUnsubscribe)
        .subscribe(
            data => {
                this.showDeactivatedError = false;
                this.showReactivationSent = true;
                this.cdr.detectChanges();
            },
            error => {
                console.log("error sending reactivation email");
            } 
        );
    };

    sendEmailsAdditionalReactivationEmail(index): void {
        this.showEmailsAdditionalDeactivatedError.splice(index, 1, false);
        this.showEmailsAdditionalReactivationSent.splice(index, 1, true);

        this.oauthService.sendEmailsAdditionalReactivationEmail(this.registrationForm.emailsAdditional[index].value)
        .takeUntil(this.ngUnsubscribe)
        .subscribe(
            data => {
            },
            error => {
                console.log("error sending reactivation email");
            } 
        );
    };

    oauth2ScreensPostRegisterConfirm(): void {
        var baseUri = getBaseUri();  
        if(this.registrationForm.linkType === 'shibboleth'){
            baseUri += '/shibboleth';
        }
        this.registrationForm.valNumClient = this.registrationForm.valNumServer / 2;
        this.oauthService.oauth2ScreensPostRegisterConfirm(this.registrationForm)
        .takeUntil(this.ngUnsubscribe)
        .subscribe(
            data => {
                if(data != null && data.errors != null && data.errors.length > 0) {
                    this.generalRegistrationError = data.errors[0];
                    this.showGeneralRegistrationError = true;
                    this.showRegisterProcessing = false;
                    this.modalService.notifyOther({action:'close', moduleId: 'modalRegisterDuplicates'});
                    this.cdr.detectChanges();
                } else {
                    if (this.gaString){
                        orcidGA.gaPush(['send', 'event', 'RegGrowth', 'New-Registration', 'OAuth ' + this.gaString]);
                    } else {
                        orcidGA.gaPush(['send', 'event', 'RegGrowth', 'New-Registration', 'Website']);
                    } 
                    orcidGA.windowLocationHrefDelay(data.url);
                }   
            },
            error => {
                // something bad is happening!
                console.log("OauthAuthorizationController.postRegister() error");
            } 
        );
    };

    oauth2ScreensRegister(linkFlag): void {
        if (this.gaString) {
            this.registrationForm.referredBy = this.requestInfoForm.clientId;
            this.registrationForm.creationType.value = "Member-referred";
            orcidGA.gaPush(['send', 'event', 'RegGrowth', 'New-Registration-Submit' , 'OAuth ' + this.gaString]);
        } else {
            orcidGA.gaPush(['send', 'event', 'RegGrowth', 'New-Registration-Submit', 'Website']);
            this.registrationForm.creationType.value = "Direct";
        } 

        // Adding the response to the register object
        this.registrationForm.grecaptcha.value = this.recatchaResponse; 
        this.registrationForm.grecaptchaWidgetId.value = this.recaptchaWidgetId;
        
        this.registrationForm.linkType = linkFlag;

        this.oauthService.oauth2ScreensRegister(this.registrationForm)
        .takeUntil(this.ngUnsubscribe)
        .subscribe(
            data => {
                this.registrationForm = data;
                if (this.registrationForm.errors == undefined 
                    || this.registrationForm.errors.length == 0) {                                 
                    this.getDuplicates();
                } else {
                    if(this.registrationForm.email.errors.length > 0) {
                        this.errorEmail = data.email.value;
                        //deactivated error
                        this.showDeactivatedError = ($.inArray('orcid.frontend.verify.deactivated_email', this.registrationForm.email.errors) != -1);
                        this.showReactivationSent = false;
                        //duplicate email error
                        if($.inArray('orcid.frontend.verify.duplicate_email', this.registrationForm.email.errors) != -1){ 
                            this.showDuplicateEmailError = true;
                            this.authorizationForm.userName.value = this.registrationForm.email.value;
                        } 
                    } else {
                        this.showDeactivatedError = false;
                        this.showDuplicateEmailError = false;
                    }

                    for (var index in this.registrationForm.emailsAdditional) {
                        if (this.registrationForm.emailsAdditional[index].errors.length > 0) {      
                            this.errorEmailsAdditional[index] = data.emailsAdditional[index].value;     
                            //deactivated error
                            this.showEmailsAdditionalDeactivatedError.splice(index, 1, ($.inArray('orcid.frontend.verify.deactivated_email', this.registrationForm.emailsAdditional[index].errors) != -1));
                            this.showEmailsAdditionalReactivationSent.splice(index, 1, false);
                            //duplicate email error
                            this.showEmailsAdditionalDuplicateEmailError.splice(index, 1, ($.inArray('orcid.frontend.verify.duplicate_email', this.registrationForm.emailsAdditional[index].errors) != -1));
                            this.authorizationForm.userName.value = this.registrationForm.emailsAdditional[index].value;
                        } else {
                            this.showEmailsAdditionalDeactivatedError[index] = false;
                            this.showEmailsAdditionalDuplicateEmailError[index] = false;
                        } 
                    }

                    if (this.registrationForm.grecaptcha.errors.length == 0) {
                        this.showRecaptcha = false;
                    }
                }
                this.cdr.detectChanges();
            },
            error => {
                // something bad is happening!
                console.log("oauth2ScreensRegister() error");
            } 
        );
    };

    serverValidate(field): void {
        this.oauthService.serverValidate(this.registrationForm, field)
        .takeUntil(this.ngUnsubscribe)
        .subscribe(
            data => {
                this.commonSrvc.copyErrorsLeft(this.registrationForm, data);
                if(field == 'Email') {
                    if (this.registrationForm.email.errors.length > 0) {
                        this.errorEmail = data.email.value;
                        //deactivated error
                        this.showDeactivatedError = ($.inArray('orcid.frontend.verify.deactivated_email', this.registrationForm.email.errors) != -1);
                        this.showReactivationSent = false;
                        //duplicate email error
                        if($.inArray('orcid.frontend.verify.duplicate_email', this.registrationForm.email.errors) != -1){ 
                            this.showDuplicateEmailError = true;
                            this.authorizationForm.userName.value = this.registrationForm.email.value;
                        } 
                    } else {
                        this.showDeactivatedError = false;
                        this.showDuplicateEmailError = false;
                    } 
                }
                if(field == 'EmailsAdditional') {
                    for (var index in this.registrationForm.emailsAdditional) {
                        if (this.registrationForm.emailsAdditional[index].errors.length > 0) {      
                            this.errorEmailsAdditional[index] = data.emailsAdditional[index].value;     
                            //deactivated error
                            this.showEmailsAdditionalDeactivatedError.splice(index, 1, ($.inArray('orcid.frontend.verify.deactivated_email', this.registrationForm.emailsAdditional[index].errors) != -1));
                            this.showEmailsAdditionalReactivationSent.splice(index, 1, false);
                            //duplicate email error
                            this.showEmailsAdditionalDuplicateEmailError.splice(index, 1, ($.inArray('orcid.frontend.verify.duplicate_email', this.registrationForm.emailsAdditional[index].errors) != -1));
                            this.authorizationForm.userName.value = this.registrationForm.emailsAdditional[index].value;
                        } else {
                            this.showEmailsAdditionalDeactivatedError[index] = false;
                            this.showEmailsAdditionalDuplicateEmailError[index] = false;
                        }
                    }                          
                }
                this.cdr.detectChanges();
                
            },
            error => {
                // something bad is happening!
                console.log("serverValidate() error");
            } 
        );
    };

    showDuplicatesColorBox(): void {
        this.modalService.notifyOther({action:'open', moduleId: 'modalRegisterDuplicates'});
    };

    handleCaptchaResponse($event): void {
        this.recaptchaWidgetId = this.captcha.widgetId;
        this.recatchaResponse = this.captcha.getResponse();
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

        this.authorizationForm = {
            userName:  {value: ""},
            givenNames:  {value: ""},
            familyNames:  {value: ""},
            email:  {value: ""},
            linkType:  {value: null},
        }

        //if oauth request load request info form
        if(orcidVar.oauth2Screens || orcidVar.originalOauth2Process){
            this.loadRequestInfoForm();
        }
        
        if(orcidVar.oauth2Screens) {
            if(orcidVar.oauthUserId && orcidVar.showLogin){
                this.showRegisterForm = false;
                this.authorizationForm = {
                    userName:  {value: orcidVar.oauthUserId}
                } 
            } else{
                this.showRegisterForm = !orcidVar.showLogin;  
            }
        } else {
            if(orcidVar.showLogin){
                this.showRegisterForm = false;
            } else{
                this.showRegisterForm = !orcidVar.showLogin;  
            }
        } 

        window.onkeydown = function(e) {
            if (e.keyCode == 13) {     
                if(orcidVar.originalOauth2Process) { 
                    //this.authorize();
                }
            }
        };

        $('#enterRecoveryCode').click(function() {
            $('#recoveryCodeSignin').show(); 
        });

        if (orcidVar.firstName || orcidVar.lastName || orcidVar.emailId || orcidVar.linkRequest) {
            this.oauth2ScreensLoadRegistrationForm(orcidVar.firstName, orcidVar.lastName, orcidVar.emailId, orcidVar.linkRequest);
        } else {
            this.oauth2ScreensLoadRegistrationForm('', '', '', '');
        }

        this.subscription = this.oauthService.notifyObservable$.subscribe(
            (res) => {
                if(res !== "undefined" && res.action === "confirm" && res.moduleId === "registerDuplicates"){
                    this.oauth2ScreensPostRegisterConfirm();
                }
            }
        );
    };
}