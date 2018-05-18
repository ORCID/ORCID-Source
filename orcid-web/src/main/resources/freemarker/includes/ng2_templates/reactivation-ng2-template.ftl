<#--

    =============================================================================

    ORCID (R) Open Source
    http://orcid.org

    Copyright (c) 2012-2014 ORCID, Inc.
    Licensed under an MIT-Style License (MIT)
    http://orcid.org/open-source-license

    This copyright and license information (including a link to the full license)
    shall be included in its entirety in all copies or substantial portion of
    the software.

    =============================================================================

-->
<script type="text/ng-template" id="reactivation-ng2-template">
    
        <div class ="col-md-9 col-md-offset-3 col-sm-9 col-sm-offset-3 col-xs-12 oauth-registration">
            <p>${springMacroRequestContext.getMessage("orcid.frontend.verify.reactivation.thank_you")}</p>
            <p>${springMacroRequestContext.getMessage("orcid.frontend.verify.reactivation.please_complete")}</p>
            <div>
                <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
                <!-- First name -->
                <div class="form-group clear-fix">
                    <label for="givenNames" class="control-label">${springMacroRequestContext.getMessage("oauth_sign_up.labelfirstname")}
                    </label>
                    <div class="bottomBuffer form-group clear-fix">
                        <input id="register-form-given-names" name="givenNames" type="text" tabindex="1" [(ngModel)]="registrationForm.givenNames.value" (blur)="serverValidate('GivenNames')"/>
                        <span class="required" [ngClass]="isValidClass(registrationForm.givenNames)">*</span> 
                        <div class="popover-help-container">
                            <i class="glyphicon glyphicon-question-sign"></i>
                            <div id="name-help" class="popover bottom">
                                <div class="arrow"></div>
                                <div class="popover-content">
                                    <p><@orcid.msg ''/></p>
                                    <p><@orcid.msg 'orcid.frontend.register.help.last_name'/></p>
                                    <p><@orcid.msg 'orcid.frontend.register.help.update_names'/></p>
                                    <a href="${knowledgeBaseUri}/articles/142948-names-in-the-orcid-registry" target="orcid.frontend.register.help.more_info.link.text"><@orcid.msg 'orcid.frontend.register.help.more_info.link.text'/></a>
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
                        <input id="register-form-family-name" name="familyNames" type="text" tabindex="2" class=""  [(ngModel)]="registrationForm.familyNames.value"/>
                        <span class="orcid-error" *ngIf="registrationForm.familyNames.errors.length > 0">
                            <div *ngFor="let error of registrationForm.familyNames.errors" [innerHTML]="error"></div>
                        </span>
                    </div>
                </div>
                <!--Password-->
                <div class="form-group clear-fix">
                    <label class="control-label"><@orcid.msg 'oauth_sign_up.labelpassword'/></label>
                    <div class="bottomBuffer">
                        <input id="register-form-password" type="password" name="password" tabindex="5" class="" [(ngModel)]="registrationForm.password.value" (blur)="serverValidate('Password')"/>
                        <span class="required" [ngClass]="isValidClass(registrationForm.password)">*</span>
                        <@orcid.passwordHelpPopup />
                        <span class="orcid-error" *ngIf="registrationForm.password.errors.length > 0">
                            <div *ngFor="let error of registrationForm.password.errors" [innerHTML]="error"></div>
                        </span>
                    </div>
                </div>
                <!--Confirm password-->
                <div class="form-group clear-fix">
                    <label class="control-label"><@orcid.msg 'password_one_time_reset.labelconfirmpassword'/></label>
                    <div class="bottomBuffer">
                        <input id="register-form-confirm-password" type="password" name="confirmPassword" tabindex="6" class="" [(ngModel)]="registrationForm.passwordConfirm.value" (blur)="serverValidate('PasswordConfirm')"/>
                        <span class="required" [ngClass]="isValidClass(registrationForm.passwordConfirm)">*</span>                 
                        <span class="orcid-error" *ngIf="registrationForm.passwordConfirm.errors.length > 0">
                            <div *ngFor="let error of registrationForm.passwordConfirm.errors" [innerHTML]="error"></div>
                        </span>
                    </div>
                </div>
                <div *ngIf="gdprUiFeatureEnabled"> 
                    <!--Visibility default-->
                    <div class="form-group clear-fix popover-registry">  
                        <h4>${springMacroRequestContext.getMessage("register.privacy_settings")}</h4>         
                        <p>${springMacroRequestContext.getMessage("privacy_preferences.activitiesVisibilityDefault")}</p> 
                        <p><b>${springMacroRequestContext.getMessage("privacy_preferences.activitiesVisibilityDefault.who_can_see_this")}</b></p>
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
                                        <strong>${springMacroRequestContext.getMessage("privacyToggle.help.who_can_see")}</strong>
                                        <ul class="privacyHelp">
                                            <li class="public" style="color: #009900;">${springMacroRequestContext.getMessage("privacyToggle.help.everyone")}</li>
                                            <li class="limited" style="color: #ffb027;">${springMacroRequestContext.getMessage("privacyToggle.help.trusted_parties")}</li>
                                            <li class="private" style="color: #990000;">${springMacroRequestContext.getMessage("privacyToggle.help.only_me")}</li>
                                        </ul>
                                        <a href="${knowledgeBaseUri}/articles/124518-orcid-privacy-settings" target="privacyToggle.help.more_information">${springMacroRequestContext.getMessage("privacyToggle.help.more_information")}</a>
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
                            <@orcid.msg 'register.labelconsent'/> <a href="${aboutUri}/footer/privacy-policy" target="register.labelprivacypolicy"><@orcid.msg 'register.labelprivacypolicy'/></a>&nbsp;<@orcid.msg 'register.labeland'/>&nbsp;<@orcid.msg 'common.termsandconditions1'/><a href="${aboutUri}/content/orcid-terms-use" target="common.termsandconditions2"><@orcid.msg 'common.termsandconditions2'/></a>&nbsp;<@orcid.msg 'common.termsandconditions3'/>
                        </p>
                        <span class="orcid-error" *ngIf="registrationForm.termsOfUse.errors.length > 0">
                            <div *ngFor="let error of registrationForm.termsOfUse.errors" [innerHTML]="error"></div>
                        </span>
                    </div>
                </div>
                <div *ngIf="!gdprUiFeatureEnabled">
                    <!--Visibility default-->
                    <div class="form-group clear-fix popover-registry">
                        <div class="oauth-privacy">                   
                            <label class="privacy-toggle-lbl">${springMacroRequestContext.getMessage("privacy_preferences.activitiesVisibilityDefault")}</label> 
                            <label class="privacy-toggle-lbl">${springMacroRequestContext.getMessage("privacy_preferences.activitiesVisibilityDefault.who_can_see_this")}</label>
                            <@orcid.privacyToggle3Ng2
                                angularModel="registrationForm.activitiesVisibilityDefault.visibility"
                                publicClick="updateActivitiesVisibilityDefault('PUBLIC', $event)" 
                                limitedClick="updateActivitiesVisibilityDefault('LIMITED', $event)" 
                                privateClick="updateActivitiesVisibilityDefault('PRIVATE', $event)" 
                                elementId="workPrivHelp"
                                position="bottom" /> 
                        </div>
                    </div>
                    <!--Terms and conditions-->
                    <div class="form-group clear-fix bottomBuffer">
                        <label for="termsConditions">
                            <@orcid.msg 'register.labelTermsofUse'/>
                            <span class="required"  [ngClass]="{'text-error':registrationForm.termsOfUse.value == false}">*</span>
                        </label>
                        <p>
                            <input id="register-form-term-box" type="checkbox" name="termsConditions" tabindex="9" name="acceptTermsAndConditions" [(ngModel)]="registrationForm.termsOfUse.value" (change)="serverValidate('TermsOfUse')" />
                            <@orcid.msg 'register.labelconsent'/> <a href="${aboutUri}/footer/privacy-policy" target="register.labelprivacypolicy"><@orcid.msg 'register.labelprivacypolicy'/></a>&nbsp;<@orcid.msg 'register.labeland'/>&nbsp;<@orcid.msg 'common.termsandconditions1'/><a href="${aboutUri}/content/orcid-terms-use" target="common.termsandconditions2"><@orcid.msg 'common.termsandconditions2'/></a>&nbsp;<@orcid.msg 'common.termsandconditions3'/>
                        </p>
                        <span class="orcid-error" *ngIf="registrationForm.termsOfUse.errors.length > 0">
                            <div *ngFor="let error of registrationForm.termsOfUse.errors" [innerHTML]="error"></div>
                        </span>
                    </div>
                </div>
                <div class="relative">
                    <button tabindex="10" class="btn btn-primary" (click)="postReactivationConfirm(null)">${springMacroRequestContext.getMessage("orcid.frontend.reactivate")}</button>
                </div>
            </div>
        </div>
</script>
