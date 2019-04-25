<div id="register" class="oauth-registration">
    <!-- First name -->
    <div class="form-group clear-fix">
        <label for="givenNames" class="control-label"><@orcid.msg 'oauth_sign_up.labelfirstname'/></label>
        <div class="bottomBuffer">
            <input id="register-form-given-names" name="givenNames" type="text" tabindex="1" [(ngModel)]="registrationForm.givenNames.value" (blur)="serverValidate('GivenNames')"/>                             
            <div class="popover-help-container">
                <i class="glyphicon glyphicon-question-sign"></i>
                <div id="name-help" class="popover bottom">
                    <div class="arrow"></div>
                    <div class="popover-content">
                        <p><@orcid.msg 'orcid.frontend.register.help.first_name'/></p>
                        <p><@orcid.msg 'orcid.frontend.register.help.last_name'/></p>
                        <p><@orcid.msg 'orcid.frontend.register.help.update_names'/></p>
                        <a href="<@orcid.msg 'orcid.frontend.register.help.more_info.link.url'/>" target="orcid.frontend.register.help.more_info.link.text"><@orcid.msg 'orcid.frontend.register.help.more_info.link.text'/></a>
                    </div>
                </div>
            </div>
            <span class="orcid-error" *ngIf="registrationForm?.givenNames?.errors?.length > 0">
                <div *ngFor="let error of registrationForm.givenNames.errors" [innerHTML]="error"></div>
            </span>
        </div>
    </div>
    <!-- Last name -->
    <div class="form-group clear-fix">
        <label class="control-label"><@orcid.msg 'oauth_sign_up.labellastname'/> <i><@orcid.msg 'register.optional'/></i></label>
        <div class="bottomBuffer">
            <input id="register-form-family-name" name="familyNames" type="text" tabindex="2" class=""  [(ngModel)]="registrationForm.familyNames.value" (blur)="serverValidate('FamilyNames')"/>
            <span class="orcid-error" *ngIf="registrationForm?.familyNames?.errors?.length > 0">
                <div *ngFor="let error of registrationForm.familyNames.errors" [innerHTML]="error"></div>
            </span>
        </div>
    </div>
    <div>
        <!-- Primary email -->
        <div class="form-group clear-fix">
            <label class="control-label">${springMacroRequestContext.getMessage("oauth_sign_up.labelemailprimary")}</label>
            <div class="relative">          
                <input name="emailprimary234" type="text" tabindex="3" class="input-xlarge" [(ngModel)]="registrationForm.email.value" (blur)="serverValidate('Email')"/>
                <span class="orcid-error" *ngIf="registrationForm?.email?.errors?.length > 0 && !showDeactivatedError && !showReactivationSent && !showDuplicateEmailError">
                    <div *ngFor="let error of registrationForm.email.errors" [innerHTML]="error"></div>
                </span>
                <span class="orcid-error" *ngIf="showDuplicateEmailError">{{errorEmail}} 
                    ${springMacroRequestContext.getMessage("oauth.registration.duplicate_email_1_ng2")} <a (click)="switchForm(errorEmail)">${springMacroRequestContext.getMessage("oauth.registration.duplicate_email_2")}</a>${springMacroRequestContext.getMessage("oauth.registration.duplicate_email_3_ng2")} {{errorEmail}}
                    ${springMacroRequestContext.getMessage("oauth.registration.duplicate_email_4_ng2")}
                </span>
                <span class="orcid-error" *ngIf="showDeactivatedError">
                    <@spring.message 'orcid.frontend.verify.deactivated_email.1'/><a (click)="sendReactivationEmail(registrationForm.email.value)"><@spring.message 'orcid.frontend.verify.deactivated_email.2' /></a><@spring.message 'orcid.frontend.verify.deactivated_email.3'/>
                </span>
                <span class="orcid-error" *ngIf="showReactivationSent">
                    <@spring.message 'orcid.frontend.verify.reactivation_sent.1'/> <a href="https://orcid.org/help/contact-us">${springMacroRequestContext.getMessage("orcid.frontend.verify.reactivation_sent.2")}</a>${springMacroRequestContext.getMessage("orcid.frontend.verify.reactivation_sent.3")}
                </span>
            </div>
        </div>
        <!-- Additional emails -->
        <div class="form-group clear-fix" *ngFor="let emailAdditional of registrationForm.emailsAdditional;let i = index;trackBy:trackByIndex">
            <label class="control-label">${springMacroRequestContext.getMessage("oauth_sign_up.labelemailadditional")} <i><@orcid.msg 'register.optional'/></i></label>
            <div class="relative">
                <input name="emailadditional234" type="text" tabindex="3" class="input-xlarge" [(ngModel)]="registrationForm.emailsAdditional[i].value" [focusMe]="newInput" (blur)="serverValidate('EmailsAdditional')"/>
                <div *ngIf="i == 0" class="popover-help-container leftBuffer">
                    <i class="glyphicon glyphicon-question-sign"></i>
                    <div id="email-additional-help" class="popover bottom">
                        <div class="arrow"></div>
                        <div class="popover-content">
                            <p><@orcid.msg ''/></p>
                            <p><@orcid.msg 'orcid.frontend.register.help.email_additional'/></p>
                        </div>
                    </div>
                </div>
                <div *ngIf="i != 0" class="popover-help-container leftBuffer">
                    <a class="btn-white-no-border" (click)="removeEmailField(i)"><i class="glyphicon glyphicon-remove-sign"></i></a>
                </div>
                <span class="orcid-error" *ngIf="registrationForm?.emailsAdditional[i]?.errors && registrationForm?.emailsAdditional[i]?.errors?.length > 0 && !showEmailsAdditionalDeactivatedError[i] && !showEmailsAdditionalReactivationSent[i] && !showEmailsAdditionalDuplicateEmailError[i]">
                    <div *ngFor="let error of registrationForm.emailsAdditional[i].errors;let i = index;trackBy:trackByIndex" [innerHTML]="error"></div>
                </span>
                <span class="orcid-error" *ngIf="showEmailsAdditionalDuplicateEmailError[i]">{{errorEmailsAdditional[i]}} 
                    ${springMacroRequestContext.getMessage("oauth.registration.duplicate_email_1_ng2")} <a (click)="switchForm(errorEmailsAdditional[i])">${springMacroRequestContext.getMessage("oauth.registration.duplicate_email_2")}</a>${springMacroRequestContext.getMessage("oauth.registration.duplicate_email_3_ng2")} {{errorEmailsAdditional[i]}} ${springMacroRequestContext.getMessage("oauth.registration.duplicate_email_4_ng2")}
                </span>
                <span class="orcid-error" *ngIf="showEmailsAdditionalDeactivatedError[i]">                
                    ${springMacroRequestContext.getMessage("orcid.frontend.verify.deactivated_email.1")}<a (click)="sendEmailsAdditionalReactivationEmail(i)">${springMacroRequestContext.getMessage("orcid.frontend.verify.deactivated_email.2")}</a>${springMacroRequestContext.getMessage("orcid.frontend.verify.deactivated_email.3")}
                </span>
                <span class="orcid-error" *ngIf="showEmailsAdditionalReactivationSent[i]">
                    <@spring.message 'orcid.frontend.verify.reactivation_sent.1'/> <a href="https://orcid.org/help/contact-us">${springMacroRequestContext.getMessage("orcid.frontend.verify.reactivation_sent.2")}</a>${springMacroRequestContext.getMessage("orcid.frontend.verify.reactivation_sent.3")}
                </span>
            </div>
        </div>
        <button *ngIf="registrationForm?.emailsAdditional?.length < (MAX_EMAIL_COUNT -1)" (click)="addEmailField()" class="left btn-white-no-border"><i class="glyphicon glyphicon-plus-sign"></i> ${springMacroRequestContext.getMessage("oauth_sign_up.buttonaddemail")}</button>  
    </div>
    <!--Password-->
    <div class="form-group clear-fix">
        <label class="control-label"><@orcid.msg 'oauth_sign_up.labelpassword'/></label>
        <div class="bottomBuffer">
            <input id="register-form-password" type="password" name="password" tabindex="5" class="" [(ngModel)]="registrationForm.password.value" (ngModelChange)="serverValidate('Password')"/>
            <@orcid.passwordHelpPopup />
            <span class="pattern-errors">
                <div class="pattern-container">
                    <img *ngIf="registrationForm?.password?.errors?.includes('Pattern.registrationForm.password.eigthCharacters')" src="${staticCdn}/img/mat-baseline-check_circle_outline.svg" width="20px" height="20px" alt="unmet">
                    <img *ngIf="!registrationForm?.password?.errors?.includes('Pattern.registrationForm.password.eigthCharacters')" src="${staticCdn}/img/mat-baseline-check_circle.svg" width="20px" height="20px" alt="met">
                    <@spring.message 'Pattern.registrationForm.password.eigthCharacters'/>
                </div>
                <div class="pattern-container">
                    <img *ngIf="registrationForm?.password?.errors?.includes('Pattern.registrationForm.password.letterOrSymbol')" src="${staticCdn}/img/mat-baseline-check_circle_outline.svg" width="20px" height="20px" alt="unmet">
                    <img *ngIf="!registrationForm?.password?.errors?.includes('Pattern.registrationForm.password.letterOrSymbol')" src="${staticCdn}/img/mat-baseline-check_circle.svg" width="20px" height="20px" alt="met">
                    <@spring.message 'Pattern.registrationForm.password.letterOrSymbol'/>
                </div>
                <div class="pattern-container">
                    <img *ngIf="registrationForm?.password?.errors?.includes('Pattern.registrationForm.password.oneNumber')" src="${staticCdn}/img/mat-baseline-check_circle_outline.svg" width="20px" height="20px" alt="unmet">
                    <img *ngIf="!registrationForm?.password?.errors?.includes('Pattern.registrationForm.password.oneNumber')" src="${staticCdn}/img/mat-baseline-check_circle.svg" width="20px" height="20px" alt="met">
                    <@spring.message 'Pattern.registrationForm.password.oneNumber'/>
                </div>
            </span>
            <span class="orcid-error" *ngIf="registrationForm?.password?.errors?.length > 0">
                    <div *ngFor='let error of registrationForm.password.errors'>
                     <ng-container *ngIf="error.indexOf('Pattern.') < 0">{{error}} </ng-container>
                    </div>
            </span>  
        </div>
    </div>
    <!--Confirm password-->
    <div class="form-group clear-fix">
        <label class="control-label"><@orcid.msg 'password_one_time_reset.labelconfirmpassword'/></label>
        <div class="bottomBuffer">
            <input id="register-form-confirm-password" type="password" name="confirmPassword" tabindex="6" class="" [(ngModel)]="registrationForm.passwordConfirm.value" (blur)="serverValidate('PasswordConfirm')"/>
            <span class="orcid-error" *ngIf="registrationForm?.passwordConfirm?.errors?.length > 0">
                <div *ngFor="let error of registrationForm.passwordConfirm.errors" [innerHTML]="error"></div>
            </span>
        </div>
    </div>
    <div>
        <!--Visibility default-->
        <div class="form-group clear-fix popover-registry">  
            <h4>${springMacroRequestContext.getMessage("register.privacy_settings")}</h4>         
            <p>${springMacroRequestContext.getMessage("privacy_preferences.activitiesVisibilityDefault")} <a href="<@orcid.msg 'common.kb_uri_default'/>360006897614" target="privacyToggle.help.more_information">${springMacroRequestContext.getMessage("privacyToggle.help.more_information")}</a>.</p> 
            <p><b>${springMacroRequestContext.getMessage("privacy_preferences.activitiesVisibilityDefault.who_can_see_this")}</b></p>
            <div class="visibilityDefault">
                <div class="radio">
                  <label><input type="radio" name="defaultVisibility" [(ngModel)]="registrationForm.activitiesVisibilityDefault.visibility" value="PUBLIC" (blur)="serverValidate('ActivitiesVisibilityDefault')"><span class="public"></span><span class="defaultVisLabel"><b><@orcid.msg 'manage.lipublic'/></b> <@orcid.msg 'register.privacy_everyone_text'/></span></label>
                </div>
                <div class="radio">
                  <label><input type="radio" name="defaultVisibility" [(ngModel)]="registrationForm.activitiesVisibilityDefault.visibility" value="LIMITED" (blur)="serverValidate('ActivitiesVisibilityDefault')"><span class="limited"></span><span class="defaultVisLabel"><b><@orcid.msg 'manage.lilimited'/></b> <@orcid.msg 'register.privacy_limited_text'/></span></label>
                </div>
                <div class="radio">
                  <label><input type="radio" name="defaultVisibility" [(ngModel)]="registrationForm.activitiesVisibilityDefault.visibility" value="PRIVATE" (blur)="serverValidate('ActivitiesVisibilityDefault')"><span class="private"></span><span class="defaultVisLabel"><b><@orcid.msg 'manage.liprivate'/></b> <@orcid.msg 'register.privacy_private_text'/></span></label>
                </div>
            </div>
            <span class="orcid-error" *ngIf="registrationForm?.activitiesVisibilityDefault?.errors?.length > 0">
                <div *ngFor="let error of registrationForm.activitiesVisibilityDefault.errors" [innerHTML]="error"></div>
            </span>
        </div>
        <div>
            <!--Notifications settings -->
            <div id="notificationSettings" class="form-group clear-fix">  
                <h4 class="dark-label"><@orcid.msg 'register.label.notification_settings' /></h4>                
                <p><@orcid.msg 'register.paragraph.1' /></p>
                <p><@orcid.msg 'register.paragraph.2' /></p>
                <div class="control-group">
                    <input id="send-orcid-news" type="checkbox" name="sendOrcidNews" tabindex="9" [(ngModel)]="registrationForm.sendOrcidNews.value" />
                    <label for="send-orcid-news"><@orcid.msg 'manage.email.email_frequency.notifications.news.checkbox.label' /></label>
                    <p class="italic"><@orcid.msg 'register.paragraph.3' /></p>
                </div>
                <p><@orcid.msg 'register.paragraph.4' /></p>
            </div>
        </div>        
        <!--Terms and conditions-->
        <div class="clearfix bottomBuffer">
            <h4><@orcid.msg 'register.labelTermsofUse'/></h4>  
            <p>
                <input id="register-form-term-box" type="checkbox" name="termsConditions" tabindex="9" name="acceptTermsAndConditions" [(ngModel)]="registrationForm.termsOfUse.value" (change)="serverValidate('TermsOfUse')" />
                <@orcid.msg 'register.labelconsent'/> <a href="{{aboutUri}}/footer/privacy-policy" target="register.labelprivacypolicy"><@orcid.msg 'register.labelprivacypolicy'/></a>&nbsp;<@orcid.msg 'register.labeland'/>&nbsp;<@orcid.msg 'common.termsandconditions1'/><a href="{{aboutUri}}/content/orcid-terms-use" target="common.termsandconditions2"><@orcid.msg 'common.termsandconditions2'/></a>&nbsp;<@orcid.msg 'common.termsandconditions3'/>
            </p>
            <span class="orcid-error" *ngIf="registrationForm?.termsOfUse?.errors?.length > 0">
                <div *ngFor="let error of registrationForm.termsOfUse.errors" [innerHTML]="error"></div>
            </span>
        </div>
        <!--Recaptcha-->
        <div *ngIf="!disableRecaptchaFeatureEnabled">  
            <div *ngIf="showRecaptcha" class="bottomBuffer relative recaptcha"  id="recaptcha">
                <re-captcha (captchaResponse)="handleCaptchaResponse($event)" site_key="{{site_key}}" theme="light" language={{currentLanguage}}></re-captcha>
                <span class="orcid-error" *ngIf="registrationForm?.grecaptcha?.errors?.length > 0">
                    <div *ngFor="let error of registrationForm.grecaptcha.errors;trackBy:$index" [innerHTML]="error"></div>
                </span>
            </div>
        </div>
    </div>
    <!--Registration error-->
    <div style="margin-bottom: 15px;" *ngIf="showGeneralRegistrationError">
        <span class="orcid-error" [innerHtml]="generalRegistrationError"></span>
    </div>
    <!-- Buttons  -->
    <div class="bottomBuffer col-xs-12 col-sm-3">
            <button *ngIf="isLinkRequest" id="register-authorize-button" class="btn btn-primary" name="authorize" value="<@orcid.msg 'confirm-oauth-access.Authorize'/>" (click)="oauth2ScreensRegister(linkType)">${springMacroRequestContext.getMessage("header.register")}</button>
            <button *ngIf="!isLinkRequest" id="register-authorize-button" class="btn btn-primary" name="authorize" value="<@orcid.msg 'confirm-oauth-access.Authorize'/>" (click)="oauth2ScreensRegister(null)">${springMacroRequestContext.getMessage("header.register")}</button>
    </div>   
</div>  