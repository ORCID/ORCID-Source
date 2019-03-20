<script type="text/ng-template" id="reset-password-ng2-template">
    <div>
        <form id="reg-form-password" autocomplete="off">
            <div class="control-group">
                <p><small>${springMacroRequestContext.getMessage("password_one_time_reset.label")}</small></p>
            </div>
            <div class="control-group">
                <span class="orcid-error" *ngIf="resetPasswordForm?.password?.errors?.length > 0">
                    <div *ngFor='let error of resetPasswordForm.password.errors'>{{error}}</div>
                </span>  
                <label for="passwordField" class="control-label">${springMacroRequestContext.getMessage("password_one_time_reset.pleaseenternewpassword")}</label>
                <div class="controls">
                    <input id="passwordField" type="password" name="password" class="input-xlarge" [(ngModel)]="resetPasswordForm && resetPasswordForm.password && resetPasswordForm.password.value" (onChange)="serverValidate()"/>
                    <span class="required">*</span>
                    <@orcid.passwordHelpPopup /> 
                </div>
            </div>
            <div class="control-group">
                <span class="orcid-error" *ngIf="resetPasswordForm?.retypedPassword?.errors?.length > 0">
                    <div *ngFor='let error of resetPasswordForm.retypedPassword.errors'>{{error}}</div>
                </span>  
                <label for="retypedPassword" class="control-label">${springMacroRequestContext.getMessage("password_one_time_reset.pleaseenternewpassword")}</label>
                <div class="controls">
                    <input id="retypedPassword" type="password" name="retypedPassword" value="${(oneTimeResetPasswordForm.retypedPassword)!}" class="input-xlarge" [(ngModel)]="resetPasswordForm && resetPasswordForm.password && resetPasswordForm.retypedPassword.value" (onChange)="validatePassword(); serverValidate()" />
                    <span class="required">*</span>
                </div>        
            </div>
            <div class="controls">
                <button class="btn btn-primary" (click)="postPasswordReset()">${springMacroRequestContext.getMessage("freemarker.btnsavechanges")}</button>      
            </div>    
        </form>
    </div>
</script>