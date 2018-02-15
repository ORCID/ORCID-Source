declare var $: any; //delete
declare var orcidVar: any;
declare var orcidGA: any;
declare var addShibbolethGa: any;

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

import { CommonService } 
    from '../../shared/common.service.ts';

import { OauthService } 
    from '../../shared/oauth.service.ts';


@Component({
    selector: 'oauth-authorization-ng2',
    template:  scriptTmpl("oauth-authorization-ng2-template"),
    providers: [OauthService]

})
export class OauthAuthorizationComponent implements AfterViewInit, OnDestroy, OnInit {
    private ngUnsubscribe: Subject<void> = new Subject<void>();
    private subscription: Subscription;

    allowEmailAccess: any;
    authorizationForm: any;
    counter: any;
    duplicates: any;
    gaString: any;
    focusIndex: any;
    emailTrustAsHtmlErrors: any;
    enablePersistentToken: any;
    isOrcidPresent: any;
    model: any;
    oauthSignin: any;
    personalLogin: any;
    recaptchaWidgetId: any;
    recatchaResponse: any;
    requestInfoForm: any;
    registrationForm: any;
    scriptsInjected: any;
    showBulletIcon: any;
    showClientDescription: any;
    showCreateIcon: any;
    showDeactivatedError: any;
    showEmailsAdditionalDeactivatedError: any;
    showEmailsAdditionalReactivationSent: any;
    showLimitedIcon: any;
    showLongDescription: any;
    showReactivationSent: any;
    showRegisterForm: any;
    showUpdateIcon: any;
    socialSignInForm: any;
    loadTime: any;
    generalRegistrationError: any;

    constructor(
        private commonSrvc: CommonService,
        private oauthService: OauthService
    ) {
        this.allowEmailAccess = true;
        this.authorizationForm = {};
        this.counter = 0;
        this.duplicates = {};
        this.gaString = null;
        this.emailTrustAsHtmlErrors = [];
        this.enablePersistentToken = true;
        this.focusIndex = null;
        this.isOrcidPresent = false;
        this.model = {
            key: orcidVar.recaptchaKey
        };
        this.oauthSignin = false;
        this.personalLogin = true;
        this.recaptchaWidgetId = null;
        this.recatchaResponse = null;
        this.requestInfoForm = null;    
        this.registrationForm = {};
        this.scriptsInjected = false;
        this.showBulletIcon = false;
        this.showClientDescription = false;
        this.showCreateIcon = false;
        this.showDeactivatedError = false;
        this.showEmailsAdditionalDeactivatedError = [false];
        this.showEmailsAdditionalReactivationSent = [false];
        this.showLimitedIcon = false;    
        this.showLongDescription = {};
        this.showReactivationSent = false;
        this.showRegisterForm = false;
        this.showUpdateIcon = false;
        this.socialSignInForm = {};
        this.loadTime = 0;
        this.generalRegistrationError = null;
    }

    addScript(url, onLoadFunction): void {        
        let head = document.getElementsByTagName('head')[0];
        let script = document.createElement('script');
        script.src = getBaseUri() + url + '?v=' + orcidVar.version;
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
        console.log("login social");
        if(this.gaString){
            orcidGA.gaPush(['send', 'event', 'RegGrowth', 'Sign-In-Submit' , 'OAuth ' + this.gaString]);
        } else {
            orcidGA.gaPush(['send', 'event', 'RegGrowth', 'Sign-In-Submit' , 'Website']);
        }
        orcidGA.gaPush(['send', 'event', 'RegGrowth', 'Sign-In-Submit-Social', idp ]);
    };

    setRecaptchaWidgetId(widgetId): void {
        this.recaptchaWidgetId = widgetId;        
    };

    setRecatchaResponse(response): void {
        this.recatchaResponse = response;        
    };

    showDeactivationError(): void {
        this.showDeactivatedError = true;
        this.showReactivationSent = false;
    };

    showInstitutionLogin(): void  {
        this.personalLogin = false; // Hide Personal Login
        
        if(!this.scriptsInjected){ // If shibboleth scripts haven't been
                                        // loaded yet.
            this.addScript('/static/javascript/shibboleth-embedded-ds/1.1.0/idpselect_config.js', function(){
                this.addScript('/static/javascript/shibboleth-embedded-ds/1.1.0/idpselect.js', function(){
                    this.scriptsInjected = true;
                    addShibbolethGa(this.gaString);
                });
            });
        };
    };

    showPersonalLogin(): void {        
        this.personalLogin = true;
    };

    switchForm(): void {
        this.showRegisterForm = !this.showRegisterForm;
        if (!this.personalLogin) {
            this.personalLogin = true;
        }
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
                ////console.log('setBiographyFormError', error);
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
                    //console.log('getformDataError', error);
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
                //console.log('getformDataError', error);
            } 
        );

    };  

    loadRequestInfoForm(): void{

        this.oauthService.loadRequestInfoForm( )
        .takeUntil(this.ngUnsubscribe)
        .subscribe(
            data => {
                if(data){
                    /*
                    angular.forEach(data.scopes, function (scope) {
                        if (scope.value == "/email/read-private") {
                            this.emailRequested = true;
                        } else if(scope.value.endsWith('/create')) {
                            this.showCreateIcon = true;
                        } else if(scope.value.endsWith('/update')) {
                            this.showUpdateIcon = true;
                        } else if(scope.value.endsWith('/read-limited')) {
                            this.showLimitedIcon = true;
                        } else {
                            this.showBulletIcon = true;
                        }
                    })
                    */
                                                                                                            
                    this.requestInfoForm = data;              
                    this.gaString = orcidGA.buildClientString(this.requestInfoForm.memberName, this.requestInfoForm.clientName);
                }

            },
            error => {
                //console.log('getformDataError', error);
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
                }

                this.registrationForm.emailsAdditional=[{errors: [], getRequiredMessage: null, required: false, value: '',  }];                          
                this.registrationForm.linkType=linkFlag;
                
                /*            
                // special handling of deactivation error for primary email
                $scope.$watch('registrationForm.email.errors', function(newValue, oldValue) {
                    $scope.showDeactivatedError = ($.inArray('orcid.frontend.verify.deactivated_email', $scope.registrationForm.email.errors) != -1);
                    $scope.showReactivationSent = false;
                }); // initialize the watch

                // special handling of deactivation error for additional emails
                $scope.$watch('registrationForm.emailsAdditional', function(newValue, oldValue) {
                    for (var index in $scope.registrationForm.emailsAdditional) {
                        $scope.showEmailsAdditionalDeactivatedError.splice(index, 1, ($.inArray('orcid.frontend.verify.deactivated_email', $scope.registrationForm.emailsAdditional[index].errors) != -1));
                        $scope.showEmailsAdditionalReactivationSent.splice(index, 1, false);
                    }                              
                }, true); // initialize the watch
                */

            },
            error => {
                //console.log('getformDataError', error);
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
                    this.showDuplicatesColorBox();
                } else {
                    this.oauth2ScreensPostRegisterConfirm();                          
                }

            },
            error => {
                //console.log('getformDataError', error);
                this.oauth2ScreensPostRegisterConfirm();
            } 
        );

    };

    sendReactivationEmail(email): void {
        

        this.oauthService.sendReactivationEmail( { email: email })
        .takeUntil(this.ngUnsubscribe)
        .subscribe(
            data => {
            },
            error => {
                ////console.log('setBiographyFormError', error);
            } 
        );
    };

    sendEmailsAdditionalReactivationEmail(index): void {
        this.showEmailsAdditionalDeactivatedError.splice(index, 1, false);
        this.showEmailsAdditionalReactivationSent.splice(index, 1, true);

        this.oauthService.sendEmailsAdditionalReactivationEmail({ email: this.registrationForm.emailsAdditional[index].value })
        .takeUntil(this.ngUnsubscribe)
        .subscribe(
            data => {
            },
            error => {
                ////console.log('setBiographyFormError', error);
            } 
        );
    };

    oauth2ScreensPostRegisterConfirm(): void {
         var baseUri = getBaseUri();
                
        if(this.registrationForm.linkType === 'shibboleth'){
            baseUri += '/shibboleth';
        }
        
        this.showProcessingColorBox();
        this.registrationForm.valNumClient = this.registrationForm.valNumServer / 2;

        this.oauthService.oauth2ScreensPostRegisterConfirm(this.registrationForm)
        .takeUntil(this.ngUnsubscribe)
        .subscribe(
            data => {
                if(data != null && data.errors != null && data.errors.length > 0) {
                    //TODO: Display error in the page
                    this.generalRegistrationError = data.errors[0];
                    $.colorbox.close();
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
                ////console.log('setBiographyFormError', error);
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
                    this.showProcessingColorBox();                            
                    this.getDuplicates();
                } else {
                    if(this.registrationForm.email.errors.length > 0) {
                        for(var i = 0; i < this.registrationForm.email.errors.length; i++){
                            //this.emailTrustAsHtmlErrors[0] = $sce.trustAsHtml(this.registrationForm.email.errors[i]);
                        }
                    } else {
                        this.emailTrustAsHtmlErrors = [];
                    }
                    if (this.registrationForm.grecaptcha.errors.length == 0) {
                        //angular.element(document.querySelector('#recaptcha')).remove();
                    }
                }
            },
            error => {
                ////console.log('setBiographyFormError', error);
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
                        for(var i = 0; i < this.registrationForm.email.errors.length; i++){
                            //this.emailTrustAsHtmlErrors[0] = $sce.trustAsHtml(this.registrationForm.email.errors[i]);
                        }
                    } else {
                        this.emailTrustAsHtmlErrors = [];
                    }
                }
            },
            error => {
                ////console.log('setBiographyFormError', error);
            } 
        );
    };

    showDuplicatesColorBox(): void {
        /*
        $.colorbox({
            html : $compile($('#duplicates').html())(this),
            escKey:false,
            overlayClose:false,
            transition: 'fade',
            close: '',
            scrolling: true
        });
        $.colorbox.resize({width:"780px" , height:"400px"});
        */
    };

   
    showProcessingColorBox(): void  {
        /*
        $.colorbox({
            html : $('<div style="font-size: 50px; line-height: 60px; padding: 20px; text-align:center">' + om.get('common.processing') + '&nbsp;<i id="ajax-loader" class="glyphicon glyphicon-refresh spin green"></i></div>'),
            width: '400px',
            height:"100px",
            close: '',
            escKey:false,
            overlayClose:false,
            onComplete: function() {
                $.colorbox.resize({width:"400px" , height:"100px"});
            }
        });
        */
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

        this.loadRequestInfoForm();
        if(orcidVar.oauth2Screens) {
            if(orcidVar.oauthUserId && orcidVar.showLogin){
                this.showRegisterForm = false;
                this.authorizationForm = {
                    userName:  {value: orcidVar.oauthUserId}
                } 
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

        this.oauth2ScreensLoadRegistrationForm('', '', '', '');
    };
}