<script type="text/ng-template" id="request-password-reset-ng2-template">
    <#if springMacroRequestContext.requestUri?contains("/reset-password") >
        <div id="RequestPasswordResetCtr" class="row">
            <div class="col-md-9 col-md-offset-3 col-sm-12 col-xs-12">
                <h2>${springMacroRequestContext.getMessage("reset_password.h2ForgottenPassword")}</h2>
                <span role="alert" *ngIf="tokenExpired" class="orcid-error">${springMacroRequestContext.getMessage("orcid.frontend.reset.password.resetAgain")}</span>
                <p>
                     ${springMacroRequestContext.getMessage("reset_password.enterEmail_1")}<br />
                     ${springMacroRequestContext.getMessage("reset_password.enterEmail_2")}
                     <a href="https://support.orcid.org/hc/en-us/requests/new">${springMacroRequestContext.getMessage("resend_claim.labelorg")}</a>
                </p>           
                <div id="password-reset" name="emailAddressForm">
                    <span role="alert" class="orcid-error" *ngIf="requestResetPassword.errors && requestResetPassword.errors.length > 0 && !showDeactivatedError &&  !showReactivationSent">
                       <div *ngFor='let error of requestResetPassword.errors' [innerHTML]="error"></div>
                    </span>
                    <div role="alert" class="alert alert-success" *ngIf="requestResetPassword?.successMessage != null && (!resetPasswordEmailFeatureEnabled)">
                        <strong><span [innerHTML]="requestResetPassword.successMessage"></span>
                        </strong>
                    </div>
                    <div role="alert" class="alert alert-success" *ngIf="requestResetPassword?.successMessage != null && resetPasswordEmailFeatureEnabled">
                        <strong>${springMacroRequestContext.getMessage("orcid.frontend.reset.password.email_success_1")} {{successEmailSentTo}}
                        ${springMacroRequestContext.getMessage("orcid.frontend.reset.password.email_success_2")}</strong><br>
                        ${springMacroRequestContext.getMessage("orcid.frontend.reset.password.email_success_3")}
                        <a href='${springMacroRequestContext.getMessage("common.contact_us.uri")}' target="common.contact_us.uri">${springMacroRequestContext.getMessage("orcid.frontend.reset.password.email_success_4")}</a>${springMacroRequestContext.getMessage("common.period")}
                    </div>
                    <div class="control-group col-md-6 reset">
                        <label for="email" class="control-label">${springMacroRequestContext.getMessage("manage_bio_settings.h3email")} </label>                       
                        <div class="controls"> 
                            <input id="email" name="email" type="text" class="form-control" [(ngModel)]="requestResetPassword.email" />
                        </div>
                        <span role="alert" class="orcid-error" *ngIf="showDeactivatedError && !showReactivationSent">
                            ${springMacroRequestContext.getMessage("orcid.frontend.verify.deactivated_email.1")}<a role="button" tabindex="0"  (keydown.Enter)="sendReactivationResetPasswordPage($event, requestResetPassword.email)" (keydown.Space)="sendReactivationResetPasswordPage($event, requestResetPassword.email)" (click)="sendReactivationResetPasswordPage($event, requestResetPassword.email)" >${springMacroRequestContext.getMessage("orcid.frontend.verify.deactivated_email.2")}</a>${springMacroRequestContext.getMessage("orcid.frontend.verify.deactivated_email.3")}
                        </span>
                        <span role="alert" class="orcid-error" *ngIf="showReactivationSent">
                            <@orcid.msg 'orcid.frontend.verify.reactivation_sent.1'/> <a href="https://support.orcid.org/hc/en-us/requests/new">${springMacroRequestContext.getMessage("orcid.frontend.verify.reactivation_sent.2")}</a>${springMacroRequestContext.getMessage("orcid.frontend.verify.reactivation_sent.3")}
                        </span>
                        <!--General error-->
                        <div style="margin-bottom: 15px;" *ngIf="showSendResetLinkError">
                            <span role="alert" class="orcid-error">${springMacroRequestContext.getMessage("Email.resetPasswordForm.error")}</span>
                        </div>  
                        <button class="btn btn-primary topBuffer" (click)="postPasswordResetRequest(requestResetPassword)">${springMacroRequestContext.getMessage("reset_password.sendResetLink")}</button>
                    </div>
                </div>
            </div>       
        </div>
    <#else>
        <div id="RequestPasswordResetCtr" class="reset-password">
        <a name="resetPassword"></a>
        <a role="button" tabindex="0"  (keydown.Enter)="toggleResetPassword($event)" (keydown.Space)="toggleResetPassword($event)" (click)="toggleResetPassword($event)" id="reset-password-toggle-text" [innerHTML]="resetPasswordToggleText"></a>
        <div *ngIf="showResetPassword">
            <p>
                 <small role="alert">
                 ${springMacroRequestContext.getMessage("reset_password.enterEmail_1")}<br />
                 ${springMacroRequestContext.getMessage("reset_password.enterEmail_2")}
                 <a href="https://support.orcid.org/hc/en-us/requests/new">${springMacroRequestContext.getMessage("resend_claim.labelorg")}</a>
                 </small>
            </p>
            <div id="password-reset" name="emailAddressForm">
                <span role="alert" class="orcid-error" *ngIf="requestResetPassword.errors && requestResetPassword.errors.length > 0 && !showDeactivatedError &&  !showReactivationSent">
                   <div *ngFor='let error of requestResetPassword.errors' [innerHTML]="error"></div>
                </span>
                <div role="alert" class="alert alert-success" *ngIf="requestResetPassword?.successMessage != null && (!resetPasswordEmailFeatureEnabled)">
                    <strong><span [innerHTML]="requestResetPassword.successMessage"></span>
                    </strong>
                </div>
                <div role="alert"class="alert alert-success" *ngIf="requestResetPassword?.successMessage != null && resetPasswordEmailFeatureEnabled">
                    <strong>${springMacroRequestContext.getMessage("orcid.frontend.reset.password.email_success_1")} {{successEmailSentTo}}
                    ${springMacroRequestContext.getMessage("orcid.frontend.reset.password.email_success_2")}</strong><br>
                    ${springMacroRequestContext.getMessage("orcid.frontend.reset.password.email_success_3")}
                    <a href='${springMacroRequestContext.getMessage("common.contact_us.uri")}' target="common.contact_us.uri">${springMacroRequestContext.getMessage("orcid.frontend.reset.password.email_success_4")}</a>${springMacroRequestContext.getMessage("common.period")}
                </div>
                <div class="control-group">
                    <label for="email" class="control-label">${springMacroRequestContext.getMessage("manage_bio_settings.h3email")} </label>                       
                    <div class="controls"> 
                        <input id="email" name="email" type="text" class="form-control" [(ngModel)]="requestResetPassword.email" />
                    </div>
                    <span role="alert" class="orcid-error" *ngIf="showDeactivatedError && !showReactivationSent">
                        ${springMacroRequestContext.getMessage("orcid.frontend.verify.deactivated_email.1")}<a role="button" tabindex="0" (keydown.Enter)="sendReactivation($event, requestResetPassword.email)" (keydown.Space)="sendReactivation($event, requestResetPassword.email)" (click)="sendReactivation($event, requestResetPassword.email)">${springMacroRequestContext.getMessage("orcid.frontend.verify.deactivated_email.2")}</a>${springMacroRequestContext.getMessage("orcid.frontend.verify.deactivated_email.3")}
                    </span> 
                    <span role="alert" class="orcid-error" *ngIf="showReactivationSent">
                        <@orcid.msg 'orcid.frontend.verify.reactivation_sent.1'/> <a href="https://support.orcid.org/hc/en-us/requests/new">${springMacroRequestContext.getMessage("orcid.frontend.verify.reactivation_sent.2")}</a>${springMacroRequestContext.getMessage("orcid.frontend.verify.reactivation_sent.3")}
                    </span>
                    <!--General error-->
                    <div style="margin-bottom: 15px;" *ngIf="showSendResetLinkError">
                        <span role="alert" class="orcid-error">${springMacroRequestContext.getMessage("Email.resetPasswordForm.error")}</span>
                    </div>  
                    <button class="btn btn-primary" (click)="postPasswordResetRequest(requestResetPassword)">${springMacroRequestContext.getMessage("reset_password.sendResetLink")}</button>
                </div>
            </div>
        </div>
    </div>
</#if>                    
</script>
