<script type="text/ng-template" id="request-password-reset-ng2-template">
    <#if springMacroRequestContext.requestUri?contains("/reset-password") >
        <div id="RequestPasswordResetCtr" class="row">
            <div class="col-md-9 col-md-offset-3 col-sm-12 col-xs-12">
                <h2>${springMacroRequestContext.getMessage("reset_password.h2ForgottenPassword")}</h2>
                <#if (tokenExpired)??>
                    <span class="orcid-error">${springMacroRequestContext.getMessage("orcid.frontend.reset.password.resetAgain")}</span>
                </#if>
                <p>
                    <small>
                    ${springMacroRequestContext.getMessage("reset_password.enterEmail_1")}<br />
                    ${springMacroRequestContext.getMessage("reset_password.enterEmail_2")}
                    <a href="mailto:support@orcid.org">${springMacroRequestContext.getMessage("resend_claim.labelorg")}</a>
                    </small>
                </p>            
                <form id="password-reset-form" name="emailAddressForm">
                    <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
                    <fieldset>
                        <span class="orcid-error" *ngIf="requestResetPassword?.errors?.length > 0">
                            <div *ngFor="let error of requestResetPassword.errors"
                                [innerHTML]="error"></div>
                        </span>
                        <div class="alert alert-success" *ngIf="requestResetPassword?.successMessage != null">
                            <strong><span [innerHTML]="requestResetPassword.successMessage"></span></strong>
                        </div>
                        <div class="control-group">
                            <label for="email" class="control-label">${springMacroRequestContext.getMessage("manage_bio_settings.h3email")} </label>
                            <div class="controls">                      
                                <input id="email" name="email" type="text" [(ngModel)]="requestResetPassword.email" />
                            </div>
                            <button class="btn btn-primary topBuffer" (click)="postPasswordResetRequest()">${springMacroRequestContext.getMessage("reset_password.sendResetLink")}</button>
                        </div>
                    </fieldset>
                </form>
            </div>       
        </div>
    <#else>
        <div id="RequestPasswordResetCtr" class="reset-password">
        <a name="resetPassword"></a>
        <a id="reset-password-toggle-text" (click)="toggleResetPassword()" [innerHTML]="resetPasswordToggleText"></a>
        <div *ngIf="showResetPassword">
            <p>
                 <small>
                 ${springMacroRequestContext.getMessage("reset_password.enterEmail_1")}<br />
                 ${springMacroRequestContext.getMessage("reset_password.enterEmail_2")}
                 <a href="mailto:support@orcid.org">${springMacroRequestContext.getMessage("resend_claim.labelorg")}</a>
                 </small>
            </p>
            <div id="password-reset" name="emailAddressForm">
                <span class="orcid-error" *ngIf="requestResetPassword.errors && requestResetPassword.errors.length > 0 && !showDeactivatedError &&  !showReactivationSent">
                   <div *ngFor='let error of requestResetPassword.errors' [innerHTML]="error"></div>
                </span>
                <div class="alert alert-success" *ngIf="requestResetPassword.successMessage != null">
                    <strong><span [innerHTML]="requestResetPassword.successMessage"></span></strong>
                </div>
                <div class="control-group">
                    <label for="email" class="control-label">${springMacroRequestContext.getMessage("manage_bio_settings.h3email")} </label>                       
                    <div class="controls"> 
                        <input id="email" name="email" type="text" class="form-control" [(ngModel)]="requestResetPassword.email" />
                    </div>
                    <span class="orcid-error" *ngIf="showDeactivatedError && !showReactivationSent">
                        ${springMacroRequestContext.getMessage("orcid.frontend.verify.deactivated_email.1")}<a (click)="sendReactivation(requestResetPassword.email)">${springMacroRequestContext.getMessage("orcid.frontend.verify.deactivated_email.2")}</a>${springMacroRequestContext.getMessage("orcid.frontend.verify.deactivated_email.3")}
                    </span>
                    <span class="orcid-error" *ngIf="showReactivationSent">
                        ${springMacroRequestContext.getMessage("orcid.frontend.verify.reactivation_sent.1")}<a href="mailto:support@orcid.org">${springMacroRequestContext.getMessage("orcid.frontend.verify.reactivation_sent.2")}</a>${springMacroRequestContext.getMessage("orcid.frontend.verify.reactivation_sent.3")}
                    </span>
                    <!--General error-->
                    <div style="margin-bottom: 15px;" *ngIf="showSendResetLinkError">
                        <span class="orcid-error">${springMacroRequestContext.getMessage("Email.resetPasswordForm.error")}</span>
                    </div>  
                    <button class="btn btn-primary" (click)="postPasswordResetRequest(requestResetPassword)">${springMacroRequestContext.getMessage("reset_password.sendResetLink")}</button>
                </div>
            </div>
        </div>
    </div>
</#if>                    
</script>