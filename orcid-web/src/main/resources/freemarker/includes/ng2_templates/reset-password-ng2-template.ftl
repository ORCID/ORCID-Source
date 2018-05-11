<script type="text/ng-template" id="reset-password-ng2-template">
    <div>
        <form id="reg-form-password" autocomplete="off">
            <div class="control-group">
                <p><small>${springMacroRequestContext.getMessage("password_one_time_reset_optional_security_questions.label")}</small></p>
            </div>
            <span class="orcid-error" *ngIf="resetPasswordForm?.errors?.length > 0">
                <div *ngFor='let error of resetPasswordForm.errors'>{{error}}</div>
            </span>   
            <div class="control-group">
                <label for="passwordField" class="control-label">${springMacroRequestContext.getMessage("password_one_time_reset_optional_security_questions.pleaseenternewpassword")}</label>
                <div class="controls">
                    <input id="passwordField" type="password" name="password" class="input-xlarge" [(ngModel)]="resetPasswordForm.password" (onChange)="serverValidate()"/>
                    <span class="required">*</span>
                    <@orcid.passwordHelpPopup /> 
                </div>
            </div>
            <div class="control-group">
                <label for="retypedPassword" class="control-label">${springMacroRequestContext.getMessage("password_one_time_reset_optional_security_questions.confirmyournewpassword")}</label>
                <div class="controls">
                    <input id="retypedPassword" type="password" name="retypedPassword" value="${(oneTimeResetPasswordForm.retypedPassword)!}" class="input-xlarge" [(ngModel)]="resetPasswordForm.retypedPassword" (onChange)="serverValidate()" />
                    <span class="required">*</span>
                </div>        
            </div>
            <p><small>${springMacroRequestContext.getMessage("password_one_time_reset_optional_security_questions.optionalconsidersetting")}</small></p>         
            <div class="control-group">
                <label for="securityQuestionId" class="control-label">${springMacroRequestContext.getMessage("password_one_time_reset_optional_security_questions.challengequestion")}</label>                 
                <div class="controls">                          
                    <select id="securityQuestionId" [(ngModel)]="resetPasswordForm.securityQuestionId" name="securityQuestionId" class="input-xlarge">
                        <#list securityQuestions?keys as key>
                           <option value="${key}">${securityQuestions[key]}</option>
                        </#list>
                    </select>                       
                </div>
                <label for="securityQuestionAnswer" class="control-label">${springMacroRequestContext.getMessage("password_one_time_reset_optional_security_questions.challengeanswer")}</label>
                <div class="controls">                                          
                    <input [(ngModel)]="resetPasswordForm.securityQuestionAnswer" name="securityQuestionAnswer" class="input-xlarge" />
                </div>
            </div>
            <div class="controls">
                <button class="btn btn-primary" (click)="postPasswordReset()">${springMacroRequestContext.getMessage("freemarker.btnsavechanges")}</button>      
            </div>    
        </form>
    </div>
</script>