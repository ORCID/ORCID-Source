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

<script type="text/ng-template" id="request-password-reset-ng2-template">
    <div id="RequestPasswordResetCtr" class="reset-password">
    <a name="resetPassword"></a>
    <a href="javascript:void(0);" id="reset-password-toggle-text" (click)="toggleResetPassword()" [innerHTML]="resetPasswordToggleText"></a>
    <div *ngIf="showResetPassword">
        <p>
             <small>
             ${springMacroRequestContext.getMessage("reset_password.enterEmail_1")}<br />
             ${springMacroRequestContext.getMessage("reset_password.enterEmail_2")}
             <a href="mailto:support@orcid.org">${springMacroRequestContext.getMessage("resend_claim.labelorg")}</a>
             </small>
        </p>
        <div id="password-reset" name="emailAddressForm">
            <span class="orcid-error" *ngIf="requestResetPassword.errors && requestResetPassword.errors.length > 0">
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
                <!--General error-->
                <div style="margin-bottom: 15px;" *ngIf="showSendResetLinkError">
                    <span class="orcid-error">${springMacroRequestContext.getMessage("Email.resetPasswordForm.error")}</span>
                </div>  
                <button class="btn btn-primary" (click)="postPasswordResetRequest(requestResetPassword)">${springMacroRequestContext.getMessage("reset_password.sendResetLink")}</button>
            </div>
        </div>
    </div>
</div>                     
</script>