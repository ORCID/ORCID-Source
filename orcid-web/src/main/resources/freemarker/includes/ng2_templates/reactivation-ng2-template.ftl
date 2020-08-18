<script type="text/ng-template" id="reactivation-ng2-template">
    <div *ngIf="reactivationData && reactivationData.linkExpired" class="row">
        <div class ="col-md-9 col-md-offset-3 col-sm-9 col-sm-offset-3 col-xs-12">
            <p *ngIf="!showReactivationSent"><@orcid.msg 'orcid.frontend.reset.password.resetLinkExpired_1' /><a (click)="sendReactivationEmail(reactivationData.email)"><@orcid.msg 'orcid.frontend.reset.password.resetLinkExpired_2' /></a></p>
            <p *ngIf="showReactivationSent"><@orcid.msg 'orcid.frontend.verify.reactivation_sent.1'/> <a href="https://support.orcid.org/hc/en-us/requests/new"><@orcid.msg 'orcid.frontend.verify.reactivation_sent.2' /></a><@orcid.msg 'orcid.frontend.verify.reactivation_sent.3' /></p>                         
        </div>
    </div>

    <div *ngIf="reactivationData && !reactivationData.tokenValid" class="row">
        <div class ="col-md-9 col-md-offset-3 col-sm-9 col-sm-offset-3 col-xs-12">
            <p><@orcid.msg 'orcid.frontend.reset.password.resetLinkInvalid' /></p>
        </div>
    </div>

    <div *ngIf="reactivationData && reactivationData.tokenValid && !reactivationData.linkExpired" class="row accessible-urls accessible-errors">
        <div class="col-md-9 col-md-offset-3 col-sm-9 col-sm-offset-3 col-xs-12">
            <p><@orcid.msg "orcid.frontend.verify.reactivation.thank_you" /></p>
            <p><@orcid.msg "orcid.frontend.verify.reactivation.please_complete" /></p>
            <div>
                <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
                <!-- First name -->
                <div class="form-group clear-fix">
                    <label id="label-register-form-given-names" for="givenNames" class="control-label"><@orcid.msg "oauth_sign_up.labelfirstname" /></label>
                    <div class="bottomBuffer form-group clear-fix">
                        <input required aria-labelledby="label-register-form-given-names" id="register-form-given-names" name="givenNames" type="text" class="input-xlarge" [(ngModel)]="registrationForm.givenNames.value" (blur)="serverValidate('GivenNames')"/>
                        <span class="required" [ngClass]="isValidClass(registrationForm.givenNames)">*</span> 
                        <div class="popover-help-container  leftBuffer">
                            <i class="glyphicon glyphicon-question-sign"></i>
                            <div id="name-help" class="popover bottom">
                                <div class="arrow"></div>
                                <div class="popover-content">
                                    <p><@orcid.msg ''/></p>
                                    <p><@orcid.msg 'orcid.frontend.register.help.last_name'/></p>
                                    <p><@orcid.msg 'orcid.frontend.register.help.update_names'/></p>
                                    <a href="<@orcid.msg 'common.kb_uri_default'/>360006973853" target="orcid.frontend.register.help.more_info.link.text"><@orcid.msg 'orcid.frontend.register.help.more_info.link.text'/></a>
                                </div>
                            </div>
                        </div>
                        <span role="alert" class="orcid-error" *ngIf="registrationForm.givenNames.errors.length > 0">
                            <div *ngFor="let error of registrationForm.givenNames.errors" [innerHTML]="error"></div>
                        </span>
                    </div>
                </div>
                <!-- Last name -->
                <div class="form-group clear-fix">
                    <label id="label-register-form-family-name" class="control-label"><@orcid.msg 'oauth_sign_up.labellastname'/></label>
                    <div class="bottomBuffer">
                        <input aria-labelledby="label-register-form-family-name" id="register-form-family-name" name="familyNames" type="text" class="input-xlarge" [(ngModel)]="registrationForm.familyNames.value" (blur)="serverValidate('FamilyNames')"/>
                        <span  role="alert"  class="orcid-error" *ngIf="registrationForm.familyNames.errors.length > 0">
                            <div *ngFor="let error of registrationForm.familyNames.errors" [innerHTML]="error"></div>
                        </span>
                    </div>
                </div>
                <!-- Emails -->
                <div>
                    <div class="form-group clear-fix">
                        <label id="label-email" class="control-label">${springMacroRequestContext.getMessage("oauth_sign_up.labelemailprimary")}</label>
                        <div class="relative">          
                            <input required aria-labelledby="label-email"  name="emailprimary234" type="text" class="input-xlarge" disabled="true" [(ngModel)]="registrationForm.email.value" (blur)="serverValidate('Email')"/>
                            <span class="required" [ngClass]="isValidClass(registrationForm.email)">*</span>                                                       
                        </div>
                    </div>                
                    <!-- Additional emails -->
                    <div class="form-group clear-fix" *ngFor="let emailAdditional of registrationForm.emailsAdditional;let i = index;trackBy:trackByIndex">
                        <label id="label-additional-email" class="control-label">${springMacroRequestContext.getMessage("oauth_sign_up.labelemailadditional")}</label>
                        <div class="relative">
                            <input aria-labelledby="label-additional-email" name="emailadditional234" type="text" class="input-xlarge" [(ngModel)]="registrationForm.emailsAdditional[i].value" (blur)="serverValidate('EmailsAdditional')"/>
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
                            <span class="orcid-error" *ngIf="registrationForm?.emailsAdditional[i]?.errors && registrationForm?.emailsAdditional[i]?.errors?.length > 0">
                                <div *ngFor="let error of registrationForm.emailsAdditional[i].errors;let i = index;trackBy:trackByIndex">
                                    <span  role="alert"  class="orcid-error" *ngIf="error=='unavailable'">
                                        {{registrationForm.emailsAdditional[i].value}} <@orcid.msg 'oauth.registration.duplicate_email_1_ng2' /> <a href="{{getBaseUri()}}/signin?loginId={{registrationForm.emailsAdditional[i].value}}"><@orcid.msg 'oauth.registration.duplicate_email_2' /></a><@orcid.msg 'oauth.registration.duplicate_email_3_ng2' /> {{registrationForm.emailsAdditional[i].value}} <@orcid.msg 'oauth.registration.duplicate_email_4_ng2' />
                                    </span>
                                    <span   role="alert" class="orcid-error" *ngIf="error!='unavailable'" [innerHTML]="error"></span>
                                </div>
                            </span>
                        </div>
                    </div>
                    <button (click)="addEmailField()" class="left btn-white-no-border"><i class="glyphicon glyphicon-plus-sign"></i> ${springMacroRequestContext.getMessage("oauth_sign_up.buttonaddemail")}</button>  
                </div>
                
                <!--Password-->
                <div class="form-group clear-fix">
                    <label id="label-register-form-password" class="control-label"><@orcid.msg 'oauth_sign_up.labelpassword'/></label>
                    <div class="bottomBuffer">
                        <input aria-labelledby="label-register-form-password" required id="register-form-password" type="password" name="password" class="input-xlarge" [(ngModel)]="registrationForm.password.value" (ngModelChange)="serverValidate('Password')"/>
                        <ng-container *ngIf="registrationForm?.password?.errors?.length > 0">
                            <span role="alert" class="orcid-error" *ngIf="showPasswordPatterError(registrationForm?.password?.errors)">
                                <div > 
                                    <@spring.message 'Pattern.registrationForm.password'/> 
                                </div>
                            </span>
                            <ng-container *ngFor='let error of registrationForm.password.errors'>
                                        <ng-container *ngIf="error.indexOf('Pattern.') < 0">
                                            <span role="alert" class="orcid-error">
                                                <div> 
                                                    {{error}} 
                                                </div>
                                            </span>
                                        </ng-container>
                                        <ng-container *ngIf="error.indexOf('containsEmail') >= 0">
                                        <span role="alert" class="orcid-error">
                                                <div> 
                                                <@spring.message 'Pattern.registrationForm.password.containsEmail'/> 
                                                </div>
                                            </span>
                                        </ng-container>
                            </ng-container>
                        </ng-container>
                        <span class="required" [ngClass]="isValidClass(registrationForm.password)">*</span>
                        <@orcid.passwordHelpPopup />
                        <span class="pattern-errors" aria-live="polite" >
                            <div class="pattern-container flex" aria-labelledby="eigthCharacters-status eigthCharacters" >
                                <img aria-hidden="true" tabindex="-1" id="eigthCharacters-status" *ngIf="registrationForm?.password?.errors?.includes('Pattern.registrationForm.password.eigthCharacters')" src="${staticCdn}/img/mat-baseline-check_circle_outline.svg" width="20px" height="20px" alt="unmet" aria-label="${springMacroRequestContext.getMessage("a11y.registry.password.unmet")}" >
                                <img aria-hidden="true" tabindex="-1" id="eigthCharacters-status" *ngIf="!registrationForm?.password?.errors?.includes('Pattern.registrationForm.password.eigthCharacters')" src="${staticCdn}/img/mat-baseline-check_circle.svg" width="20px" height="20px" alt="met" aria-label="${springMacroRequestContext.getMessage("a11y.registry.password.met")}" >
                                <div aria-hidden="true" tabindex="-1" id="eigthCharacters"><@spring.message 'Pattern.registrationForm.password.eigthCharacters'/></div>
                            </div>
                            <div class="pattern-container flex"  aria-labelledby="letterOrSymbol-status letterOrSymbol">
                                <img aria-hidden="true" tabindex="-1" id="letterOrSymbol-status" *ngIf="registrationForm?.password?.errors?.includes('Pattern.registrationForm.password.letterOrSymbol')" src="${staticCdn}/img/mat-baseline-check_circle_outline.svg" width="20px" height="20px" aria-label="${springMacroRequestContext.getMessage("a11y.registry.password.unmet")}" >
                                <img aria-hidden="true" tabindex="-1" id="letterOrSymbol-status" *ngIf="!registrationForm?.password?.errors?.includes('Pattern.registrationForm.password.letterOrSymbol')" src="${staticCdn}/img/mat-baseline-check_circle.svg" width="20px" height="20px" aria-label="${springMacroRequestContext.getMessage("a11y.registry.password.met")} " >
                                <div aria-hidden="true" tabindex="-1" id="letterOrSymbol" ><@spring.message 'Pattern.registrationForm.password.letterOrSymbol'/></div>
                            </div>
                            <div class="pattern-container flex"  aria-labelledby="oneNumber-status oneNumber">
                                <img aria-hidden="true" tabindex="-1" id="oneNumber-status" *ngIf="registrationForm?.password?.errors?.includes('Pattern.registrationForm.password.oneNumber')" src="${staticCdn}/img/mat-baseline-check_circle_outline.svg" width="20px" height="20px" aria-label="${springMacroRequestContext.getMessage("a11y.registry.password.unmet")}" >
                                <img aria-hidden="true" tabindex="-1" id="oneNumber-status"*ngIf="!registrationForm?.password?.errors?.includes('Pattern.registrationForm.password.oneNumber')" src="${staticCdn}/img/mat-baseline-check_circle.svg" width="20px" height="20px" aria-label="${springMacroRequestContext.getMessage("a11y.registry.password.met")}" >
                                <div aria-hidden="true" tabindex="-1" id="oneNumber"><@spring.message 'Pattern.registrationForm.password.oneNumber'/></div>
                            </div>
                        </span>
                    </div>
                </div>
                <!--Confirm password-->
                <div class="form-group clear-fix">
                    <label id="label-labelconfirmpassword" class="control-label"><@orcid.msg 'password_one_time_reset.labelconfirmpassword'/></label>
                    <div class="bottomBuffer">
                        <input required aria-labelledby="label-labelconfirmpassword" id="register-form-confirm-password" type="password" name="confirmPassword" class="input-xlarge" [(ngModel)]="registrationForm.passwordConfirm.value" (blur)="serverValidate('PasswordConfirm')"/>
                        <span class="required" [ngClass]="isValidClass(registrationForm.passwordConfirm)">*</span>                 
                        <span  role="alert"  class="orcid-error" *ngIf="registrationForm.passwordConfirm.errors.length > 0">
                            <div *ngFor="let error of registrationForm.passwordConfirm.errors" [innerHTML]="error"></div>
                        </span>
                    </div>
                </div>
                <div> 
                    <!--Visibility default-->
                    <div class="form-group clear-fix popover-registry">  
                        <h4  id="label-privacySettings"><@orcid.msg "register.privacy_settings" /></h4>         
                        <p><@orcid.msg "privacy_preferences.activitiesVisibilityDefault" /></p> 
                        <p><b><@orcid.msg "privacy_preferences.activitiesVisibilityDefault.who_can_see_this" /></b></p>
                        <div class="visibilityDefault"   role="radiogroup"  aria-labelledby="label-privacySettings">
                            <div class="radio">
                              <label><input type="radio" name="defaultVisibility" [(ngModel)]="registrationForm.activitiesVisibilityDefault.visibility" value="PUBLIC" (blur)="serverValidate('ActivitiesVisibilityDefault')"><span class="public"></span><span class="defaultVisLabel"><b><@orcid.msg 'manage.lipublic'/></b> <@orcid.msg 'register.privacy_everyone_text'/></span></label>
                            </div>
                            <div class="radio">
                              <label><input type="radio" name="defaultVisibility" [(ngModel)]="registrationForm.activitiesVisibilityDefault.visibility" value="LIMITED" (blur)="serversValidate('ActivitiesVisibilityDefault')"><span class="limited"></span><span class="defaultVisLabel"><b><@orcid.msg 'manage.lilimited'/></b> <@orcid.msg 'register.privacy_limited_text'/></span></label>
                            </div>
                            <div class="radio">
                              <label><input type="radio" name="defaultVisibility" [(ngModel)]="registrationForm.activitiesVisibilityDefault.visibility" value="PRIVATE" (blur)="serverValidate('ActivitiesVisibilityDefault')"><span class="private"></span><span class="defaultVisLabel"><b><@orcid.msg 'manage.liprivate'/></b> <@orcid.msg 'register.privacy_private_text'/></span></label>
                            </div>
                        </div>
                        <div class="visibilityHelp">
                            <span class="required" [ngClass]="isValidClass(registrationForm.activitiesDefaultVisibility)">*</span>
                            <div class="popover-help-container leftBuffer">
                                <i class="glyphicon glyphicon-question-sign"></i>
                                <div id="name-help" class="popover bottom">
                                    <div class="arrow"></div>
                                    <div class="popover-content">
                                        <strong><@orcid.msg "privacyToggle.help.who_can_see" /></strong>
                                        <ul class="privacyHelp">
                                            <li class="public" style="color: #009900;"><@orcid.msg "privacyToggle.help.everyone" /></li>
                                            <li class="limited" style="color: #ffb027;"><@orcid.msg "privacyToggle.help.trusted_parties" /></li>
                                            <li class="private" style="color: #990000;"><@orcid.msg "privacyToggle.help.only_me" /></li>
                                        </ul>
                                        <a href="<@orcid.msg 'common.kb_uri_default'/>360006897614" target="privacyToggle.help.more_information"><@orcid.msg "privacyToggle.help.more_information" /></a>
                                    </div>
                                </div>
                            </div>
                        </div>
                        <span  role="alert" class="orcid-error" *ngIf="registrationForm.activitiesVisibilityDefault.errors.length > 0">
                            <div *ngFor="let error of registrationForm.activitiesVisibilityDefault.errors" [innerHTML]="error"></div>
                        </span>
                    </div>
                    <!--Terms and conditions-->
                    <div class="form-group clear-fix bottomBuffer">
                        <h4 id="label-labelTermsofUse"><@orcid.msg 'register.labelTermsofUse'/>
                            <span class="required"  [ngClass]="{'text-error':registrationForm.termsOfUse.value == false}"></span></h4>  
                        <p>
                            <input aria-labelledby="label-labelTermsofUse" id="register-form-term-box" type="checkbox" name="termsConditions"name="acceptTermsAndConditions" [(ngModel)]="registrationForm.termsOfUse.value" (change)="serverValidate('TermsOfUse')" />
                            <@orcid.msg 'register.labelconsent'/> <a href="{{aboutUri}}/footer/privacy-policy" target="register.labelprivacypolicy"><@orcid.msg 'register.labelprivacypolicy'/></a>&nbsp;<@orcid.msg 'register.labeland'/>&nbsp;<@orcid.msg 'common.termsandconditions1'/><a href="{{aboutUri}}/content/orcid-terms-use" target="common.termsandconditions2"><@orcid.msg 'common.termsandconditions2'/></a>&nbsp;<@orcid.msg 'common.termsandconditions3'/>
                        </p>
                        <span  role="alert"  class="orcid-error" *ngIf="registrationForm.termsOfUse.errors.length > 0">
                            <div *ngFor="let error of registrationForm.termsOfUse.errors" [innerHTML]="error"></div>
                        </span>
                    </div>
                </div>
                    <#--  registry errors   -->
                <div style="margin-bottom: 15px;" *ngIf="showFormHasError()">
                    <span role="alert" class="orcid-error" >${springMacroRequestContext.getMessage("common.please_fix_errors")}</span>
                </div>
                <div class="relative">
                    <button class="btn btn-primary" (click)="postReactivationConfirm(null)"><@orcid.msg "orcid.frontend.reactivate" /></button>
                </div>
            </div>
        </div>
    </div>
</script>
