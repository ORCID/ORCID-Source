<script type="text/ng-template" id="reactivation-ng2-template">
    <div *ngIf="reactivationData && reactivationData.linkExpired" class="row">
        <div class ="col-md-9 col-md-offset-3 col-sm-9 col-sm-offset-3 col-xs-12">
            <p *ngIf="!showReactivationSent"><@orcid.msg 'orcid.frontend.reset.password.resetLinkExpired_1' /><a (click)="sendReactivationEmail(reactivationData.email)"><@orcid.msg 'orcid.frontend.reset.password.resetLinkExpired_2' /></a></p>
            <p *ngIf="showReactivationSent"><@orcid.msg 'orcid.frontend.verify.reactivation_sent.1'/> <a href="https://orcid.org/help/contact-us"><@orcid.msg 'orcid.frontend.verify.reactivation_sent.2' /></a><@orcid.msg 'orcid.frontend.verify.reactivation_sent.3' /></p>                         
        </div>
    </div>

    <div *ngIf="reactivationData && !reactivationData.tokenValid" class="row">
        <div class ="col-md-9 col-md-offset-3 col-sm-9 col-sm-offset-3 col-xs-12">
            <p><@orcid.msg 'orcid.frontend.reset.password.resetLinkInvalid' /></p>
        </div>
    </div>

    <div *ngIf="reactivationData && reactivationData.tokenValid && !reactivationData.linkExpired" class="row">
        <div class="col-md-9 col-md-offset-3 col-sm-9 col-sm-offset-3 col-xs-12">
            <p><@orcid.msg "orcid.frontend.verify.reactivation.thank_you" /></p>
            <p><@orcid.msg "orcid.frontend.verify.reactivation.please_complete" /></p>
            <div>
                <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
                <!-- First name -->
                <div class="form-group clear-fix">
                    <label for="givenNames" class="control-label"><@orcid.msg "oauth_sign_up.labelfirstname" /></label>
                    <div class="bottomBuffer form-group clear-fix">
                        <input id="register-form-given-names" name="givenNames" type="text" tabindex="1" class="input-xlarge" [(ngModel)]="registrationForm.givenNames.value" (blur)="serverValidate('GivenNames')"/>
                        <span class="required" [ngClass]="isValidClass(registrationForm.givenNames)">*</span> 
                        <div class="popover-help-container">
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
                        <span class="orcid-error" *ngIf="registrationForm.givenNames.errors.length > 0">
                            <div *ngFor="let error of registrationForm.givenNames.errors" [innerHTML]="error"></div>
                        </span>
                    </div>
                </div>
                <!-- Last name -->
                <div class="form-group clear-fix">
                    <label class="control-label"><@orcid.msg 'oauth_sign_up.labellastname'/></label>
                    <div class="bottomBuffer">
                        <input id="register-form-family-name" name="familyNames" type="text" tabindex="2" class="input-xlarge" [(ngModel)]="registrationForm.familyNames.value" (blur)="serverValidate('FamilyNames')"/>
                        <span class="orcid-error" *ngIf="registrationForm.familyNames.errors.length > 0">
                            <div *ngFor="let error of registrationForm.familyNames.errors" [innerHTML]="error"></div>
                        </span>
                    </div>
                </div>
                <!-- Emails -->
                <div>
                    <div class="form-group clear-fix">
                        <label class="control-label">${springMacroRequestContext.getMessage("oauth_sign_up.labelemailprimary")}</label>
                        <div class="relative">          
                            <input name="emailprimary234" type="text" tabindex="3" class="input-xlarge" disabled="true" [(ngModel)]="registrationForm.email.value" (blur)="serverValidate('Email')"/>
                            <span class="required" [ngClass]="isValidClass(registrationForm.email)">*</span>                                                       
                        </div>
                    </div>                
                    <!-- Additional emails -->
                    <div class="form-group clear-fix" *ngFor="let emailAdditional of registrationForm.emailsAdditional;let i = index;trackBy:trackByIndex">
                        <label class="control-label">${springMacroRequestContext.getMessage("oauth_sign_up.labelemailadditional")}</label>
                        <div class="relative">
                            <input name="emailadditional234" type="text" tabindex="3" class="input-xlarge" [(ngModel)]="registrationForm.emailsAdditional[i].value" (blur)="serverValidate('EmailsAdditional')"/>
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
                                    <span class="orcid-error" *ngIf="error=='unavailable'">
                                        {{registrationForm.emailsAdditional[i].value}} <@orcid.msg 'oauth.registration.duplicate_email_1_ng2' /> <a href="{{getBaseUri()}}/signin?loginId={{registrationForm.emailsAdditional[i].value}}"><@orcid.msg 'oauth.registration.duplicate_email_2' /></a><@orcid.msg 'oauth.registration.duplicate_email_3_ng2' /> {{registrationForm.emailsAdditional[i].value}} <@orcid.msg 'oauth.registration.duplicate_email_4_ng2' />
                                    </span>
                                    <span class="orcid-error" *ngIf="error!='unavailable'" [innerHTML]="error"></span>
                                </div>
                            </span>
                        </div>
                    </div>
                    <button (click)="addEmailField()" class="left btn-white-no-border"><i class="glyphicon glyphicon-plus-sign"></i> ${springMacroRequestContext.getMessage("oauth_sign_up.buttonaddemail")}</button>  
                </div>
                
                <!--Password-->
                <div class="form-group clear-fix">
                    <label class="control-label"><@orcid.msg 'oauth_sign_up.labelpassword'/></label>
                    <div class="bottomBuffer">
                        <input id="register-form-password" type="password" name="password" tabindex="5" class="input-xlarge" [(ngModel)]="registrationForm.password.value" (ngModelChange)="serverValidate('Password')"/>
                        <span class="required" [ngClass]="isValidClass(registrationForm.password)">*</span>
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
                        <input id="register-form-confirm-password" type="password" name="confirmPassword" tabindex="6" class="input-xlarge" [(ngModel)]="registrationForm.passwordConfirm.value" (blur)="serverValidate('PasswordConfirm')"/>
                        <span class="required" [ngClass]="isValidClass(registrationForm.passwordConfirm)">*</span>                 
                        <span class="orcid-error" *ngIf="registrationForm.passwordConfirm.errors.length > 0">
                            <div *ngFor="let error of registrationForm.passwordConfirm.errors" [innerHTML]="error"></div>
                        </span>
                    </div>
                </div>
                <div> 
                    <!--Visibility default-->
                    <div class="form-group clear-fix popover-registry">  
                        <h4><@orcid.msg "register.privacy_settings" /></h4>         
                        <p><@orcid.msg "privacy_preferences.activitiesVisibilityDefault" /></p> 
                        <p><b><@orcid.msg "privacy_preferences.activitiesVisibilityDefault.who_can_see_this" /></b></p>
                        <div class="visibilityDefault">
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
                            <div class="popover-help-container">
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
                        <span class="orcid-error" *ngIf="registrationForm.activitiesVisibilityDefault.errors.length > 0">
                            <div *ngFor="let error of registrationForm.activitiesVisibilityDefault.errors" [innerHTML]="error"></div>
                        </span>
                    </div>
                    <!--Terms and conditions-->
                    <div class="form-group clear-fix bottomBuffer">
                        <h4><@orcid.msg 'register.labelTermsofUse'/>
                            <span class="required"  [ngClass]="{'text-error':registrationForm.termsOfUse.value == false}"></span></h4>  
                        <p>
                            <input id="register-form-term-box" type="checkbox" name="termsConditions" tabindex="9" name="acceptTermsAndConditions" [(ngModel)]="registrationForm.termsOfUse.value" (change)="serverValidate('TermsOfUse')" />
                            <@orcid.msg 'register.labelconsent'/> <a href="{{aboutUri}}/footer/privacy-policy" target="register.labelprivacypolicy"><@orcid.msg 'register.labelprivacypolicy'/></a>&nbsp;<@orcid.msg 'register.labeland'/>&nbsp;<@orcid.msg 'common.termsandconditions1'/><a href="{{aboutUri}}/content/orcid-terms-use" target="common.termsandconditions2"><@orcid.msg 'common.termsandconditions2'/></a>&nbsp;<@orcid.msg 'common.termsandconditions3'/>
                        </p>
                        <span class="orcid-error" *ngIf="registrationForm.termsOfUse.errors.length > 0">
                            <div *ngFor="let error of registrationForm.termsOfUse.errors" [innerHTML]="error"></div>
                        </span>
                    </div>
                </div>
                <div class="relative">
                    <button tabindex="10" class="btn btn-primary" (click)="postReactivationConfirm(null)"><@orcid.msg "orcid.frontend.reactivate" /></button>
                </div>
            </div>
        </div>
    </div>
</script>
