<script type="text/ng-template" id="reset-password-ng2-template">
    <div>
        <form id="reg-form-password" autocomplete="off">
            <div class="control-group">
                <p><small>${springMacroRequestContext.getMessage("password_one_time_reset.label")}</small></p>
            </div>
            <div class="control-group" *ngIf="resetPasswordForm">
                <label for="passwordField" class="control-label">${springMacroRequestContext.getMessage("password_one_time_reset.pleaseenternewpassword")}</label>
                <div class="controls">
                    <input id="passwordField" type="password" name="password" class="input-xlarge" [(ngModel)]="resetPasswordForm.password.value" (ngModelChange)="validatePassword(); serverValidate()"/>
                    <span class="required">*</span>
                    <@orcid.passwordHelpPopup /> 
                </div>
                <ng-container *ngIf="resetPasswordForm?.password?.errors?.length > 0">
                    <span role="alert" class="orcid-error" *ngIf="showPasswordPatterError(resetPasswordForm?.password?.errors)">
                        <div > 
                             <@spring.message 'Pattern.registrationForm.password'/> 
                        </div>
                    </span>
                    <ng-container *ngFor='let error of resetPasswordForm.password.errors'>
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
       
                <span class="pattern-errors">
                <div class="pattern-container">
                    <img *ngIf="resetPasswordForm?.password?.errors?.includes('Pattern.registrationForm.password.eigthCharacters')" src="${staticCdn}/img/mat-baseline-check_circle_outline.svg" width="20px" height="20px" alt="unmet">
                    <img *ngIf="!resetPasswordForm?.password?.errors?.includes('Pattern.registrationForm.password.eigthCharacters')" src="${staticCdn}/img/mat-baseline-check_circle.svg" width="20px" height="20px" alt="met">
                    <@spring.message 'Pattern.registrationForm.password.eigthCharacters'/>  
                </div>
                <div class="pattern-container">
                    <img *ngIf="resetPasswordForm?.password?.errors?.includes('Pattern.registrationForm.password.letterOrSymbol')" src="${staticCdn}/img/mat-baseline-check_circle_outline.svg" width="20px" height="20px" alt="unmet">
                    <img *ngIf="!resetPasswordForm?.password?.errors?.includes('Pattern.registrationForm.password.letterOrSymbol')" src="${staticCdn}/img/mat-baseline-check_circle.svg" width="20px" height="20px" alt="met">
                    <@spring.message 'Pattern.registrationForm.password.letterOrSymbol'/>
                </div>
                <div class="pattern-container">
                    <img *ngIf="resetPasswordForm?.password?.errors?.includes('Pattern.registrationForm.password.oneNumber')" src="${staticCdn}/img/mat-baseline-check_circle_outline.svg" width="20px" height="20px" alt="unmet">
                    <img *ngIf="!resetPasswordForm?.password?.errors?.includes('Pattern.registrationForm.password.oneNumber')" src="${staticCdn}/img/mat-baseline-check_circle.svg" width="20px" height="20px" alt="met">
                    <@spring.message 'Pattern.registrationForm.password.oneNumber'/>
                </div>
            </span>
            </div>
            <div class="control-group">
                <label for="retypedPassword" class="control-label">${springMacroRequestContext.getMessage("change_password.confirmnewpassword")}</label>
                <div class="controls">
                    <input id="retypedPassword" type="password" name="retypedPassword" value="${(oneTimeResetPasswordForm.retypedPassword)!}" class="input-xlarge" [(ngModel)]="resetPasswordForm && resetPasswordForm.password && resetPasswordForm.retypedPassword.value" (onChange)="validatePassword(); serverValidate()" />
                    <span class="required">*</span>
                </div>        
                <span class="orcid-error" *ngIf="resetPasswordForm?.retypedPassword?.errors?.length > 0">
                    <div *ngFor='let error of resetPasswordForm.retypedPassword.errors'>{{error}}</div>
                </span>  
            </div>
            <div class="controls">
                <button class="btn btn-primary" (click)="postPasswordReset()">${springMacroRequestContext.getMessage("freemarker.btnsavechanges")}</button>      
            </div>    
        </form>
    </div>
</script>