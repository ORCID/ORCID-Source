<script type="text/ng-template" id="admin-actions-ng2-template">
<!-- Switch user -->
<div class="workspace-accordion-item" id="switch-user">
    <p>
        <a *ngIf="showSwitchUser" (click)="showSwitchUser = false"><span class="glyphicon glyphicon-chevron-down blue"></span><@orcid.msg 'admin.switch_user' /></a>
        <a *ngIf="!showSwitchUser" (click)="showSwitchUser = true"><span class="glyphicon glyphicon-chevron-right blue"></span><@orcid.msg 'admin.switch_user' /></a>
    </p>
    <div class="collapsible bottom-margin-small admin-modal" id="switch_user_section" *ngIf="showSwitchUser">
        <div class="form-group">
            <label for="orcidOrEmail"><@orcid.msg 'admin.switch_user.orcid.label' /></label>
            <input type="text" id="orcidOrEmail" (keyup.enter)="switchUser()" [(ngModel)]="switchId" placeholder="<@orcid.msg 'admin.switch_user.orcid.placeholder' />" class="input-xlarge" />
            <span class="orcid-error" *ngIf="switchUserError">
                <@spring.message "orcid.frontend.web.invalid_switch_orcid"/>
            </span>    
        </div>
        <div class="controls save-btns pull-left">
            <span id="switch-user" (click)="switchUser(switchId)" class="btn btn-primary"><@orcid.msg 'admin.switch_user.button'/></span>                     
        </div>
    </div>  
</div>

<!-- Find ids -->
<div class="workspace-accordion-item" id="find-ids">    
    <p>
        <a *ngIf="showFindIds" (click)="showFindIds = false"><span class="glyphicon glyphicon-chevron-down blue"></span><@orcid.msg 'admin.find_ids' /></a>
        <a *ngIf="!showFindIds" (click)="showFindIds = true"><span class="glyphicon glyphicon-chevron-right blue"></span><@orcid.msg 'admin.find_ids' /></a>
    </p>                
    <div class="collapsible bottom-margin-small admin-modal" id="find_ids_section" *ngIf="showFindIds">
        <div class="form-group">
            <label for="emails"><@orcid.msg 'admin.find_ids.label' /></label>
            <input type="text" id="emails" (keyup.enter)="findIds()" [(ngModel)]="csvEmails" placeholder="<@orcid.msg 'admin.find_ids.placeholder' />" class="input-xlarge" />
        </div>
        <div class="controls save-btns pull-left">
            <span id="find-ids" (click)="findIds()" class="btn btn-primary"><@orcid.msg 'admin.find_ids.button'/></span>                       
        </div>
    </div>  
    <div class="form-group" *ngIf="showIds">
        <h3><@orcid.msg 'admin.find_ids.results'/></h3>
        <div *ngIf="profileList?.length > 0">
            <table class="table table-bordered table-hover">
                <tr>
                    <td><strong><@orcid.msg 'admin.email'/></strong></td>
                    <td><strong><@orcid.msg 'admin.orcid'/></strong></td>
                    <td><strong><@orcid.msg 'admin.account_status'/></strong></td>
                </tr>
                <tr *ngFor="let profile of profileList">
                    <td>{{profile.email}}</td>
                    <td><a href="<@orcid.msg 'admin.public_view.click.link'/>{{profile.orcid}}" target="profile.orcid">{{profile.orcid}}</a>&nbsp;(<@orcid.msg 'admin.switch.click.1'/>&nbsp;<a (click)="switchUser(profile.orcid)"><@orcid.msg 'admin.switch.click.here'/></a>&nbsp;<@orcid.msg 'admin.switch.click.2'/>)</td>
                    <td>{{profile.status}}</td>
                </tr>
            </table>
            <div class="controls save-btns pull-right bottom-margin-small">
                <a href="" class="cancel-action" (click)="showIds = false" (click)="csvEmails = ''"><@orcid.msg 'freemarker.btnclose'/></a>
            </div>
        </div>
        <div *ngIf="profileList?.length == 0">
            <span><@orcid.msg 'admin.find_ids.no_results'/></span>
        </div>
    </div>         
</div>

<!-- Reset password -->
<div class="workspace-accordion-item" id="reset-password">
    <p>
        <a *ngIf="showResetPassword" (click)="showResetPassword = false"><span class="glyphicon glyphicon-chevron-down blue"></span><@orcid.msg 'admin.reset_password' /></a>
        <a *ngIf="!showResetPassword" (click)="showResetPassword = true"><span class="glyphicon glyphicon-chevron-right blue"></span><@orcid.msg 'admin.reset_password' /></a>
    </p>
    <div class="collapsible bottom-margin-small admin-modal" *ngIf="showResetPassword">
        <div class="form-group">
            <label for="orcid"><@orcid.msg 'admin.reset_password.orcid.label' /></label>
            <input type="text" id="orcid" (keyup.enter)="confirmResetPassword()" [(ngModel)]="resetPasswordParams.orcidOrEmail" placeholder="<@orcid.msg 'admin.reset_password.orcid.placeholder' />" class="input-xlarge" />
            <label for="password"><@orcid.msg 'admin.reset_password.password.label' /></label>
            <input type="text" id="password" (keyup.enter)="confirmResetPassword()" [(ngModel)]="resetPasswordParams.password" placeholder="<@orcid.msg 'admin.reset_password.password.placeholder' />" class="input-xlarge" />
            <a (click)="randomString()" class="glyphicon glyphicon-random blue"></a>
            <div *ngIf="resetPasswordParams?.error != null">
                <span class="orcid-error">{{resetPasswordParams.error}}</span>
            </div>
            <div *ngIf="resetPasswordSuccess">
                <span class="orcid-error"><@orcid.msg 'deprecate_orcid_confirmation_modal.heading' /></span>
            </div>
        </div>
        <div class="controls save-btns pull-left" *ngIf="!showResetPasswordConfirm">
            <span (click)="confirmResetPassword()" class="btn btn-primary"><@orcid.msg 'admin.reset_password.button'/></span>                        
        </div>
        <div class="controls save-btns pull-left" *ngIf="showResetPasswordConfirm">
            <label class="orcid-error"><@orcid.msg 'admin.reset_password.confirm.message'/> {{resetPasswordParams.orcidOrEmail}}?</label><br>
            <span (click)="resetPassword()" class="btn btn-primary"><@orcid.msg 'change_password.confirmnewpassword'/></span>&nbsp; 
            <a href="" class="cancel-action" (click)="showResetPasswordConfirm = false" (click)="resetPasswordParams.orcidOrEmail = ''" (click)="resetPasswordParams.password = ''" (click)="showResetPassword = false"><@orcid.msg 'freemarker.btncancel'/></a>
        </div>    
    </div>
</div>

<!-- Verify email -->
<div class="workspace-accordion-item" id="verify-email">
    <p>
        <a *ngIf="showVerifyEmail" (click)="showVerifyEmail = false"><span class="glyphicon glyphicon-chevron-down blue"></span><@orcid.msg 'admin.verify_email' /></a>
        <a *ngIf="!showVerifyEmail" (click)="showVerifyEmail = true"><span class="glyphicon glyphicon-chevron-right blue"></span><@orcid.msg 'admin.verify_email' /></a>
    </p>
    <div class="collapsible bottom-margin-small admin-modal" *ngIf="showVerifyEmail">
        <div class="form-group">            
            <label for="email"><@orcid.msg 'admin.verify_email.title' /></label>
            <input type="text" (keyup.enter)="verifyEmail()" [(ngModel)]="emailToVerify" placeholder="<@orcid.msg 'admin.verify_email.placeholder' />" class="input-xlarge" />                                                                                    
        </div>
        <div *ngIf="verifyEmailMessage != null">
            <span class="orcid-error" [innerHTML]="verifyEmailMessage"></span>
        </div>
        <div class="controls save-btns pull-left">
            <span id="verify-email" (click)="verifyEmail()" class="btn btn-primary"><@orcid.msg 'admin.verify_email.btn'/></span>                      
        </div>        
    </div>
</div>

<!-- Admin delegates -->
<div class="workspace-accordion-item" id="add-delegates">
    <p>
        <a *ngIf="showAddDelegates" (click)="showAddDelegates = false"><span class="glyphicon glyphicon-chevron-down blue"></span><@orcid.msg 'admin.delegate' /></a>
        <a *ngIf="!showAddDelegates" (click)="showAddDelegates = true"><span class="glyphicon glyphicon-chevron-right blue"></span><@orcid.msg 'admin.delegate' /></a>
    </p>
    <div class="collapsible bottom-margin-small admin-modal" *ngIf="showAddDelegates">
        <!-- Managed -->
        <div class="form-group">
            <label for="managed"><@orcid.msg 'admin.delegate.managed.label' /></label>
            <input type="text" id="managed" [(ngModel)]="addDelegateParams.managed.value" (keyup.enter)="addDelegate()" placeholder="<@orcid.msg 'admin.delegate.managed.placeholder' />" class="input-xlarge">
            <div id="invalid-managed" *ngIf="addDelegateParams.managed.errors.length > 0">
                <span class="orcid-error" *ngFor='let error of addDelegateParams.managed.errors' [innerHTML]="error"></span><br />
            </div>                          
        </div>              
        <!-- Trusted -->
        <div class="form-group">
            <label for="trusted"><@orcid.msg 'admin.delegate.trusted.label' /></label>
            <input type="text" id="trusted" [(ngModel)]="addDelegateParams.trusted.value" (keyup.enter)="addDelegate()" placeholder="<@orcid.msg 'admin.delegate.trusted.placeholder' />" class="input-xlarge">
            <div id="invalid-trusted" *ngIf="addDelegateParams.trusted.errors.length > 0">
                <span class="orcid-error" *ngFor='let error of addDelegateParams.trusted.errors' [innerHTML]="error"></span><br />
            </div>                          
        </div>
        <div *ngIf="addDelegateParams.successMessage">
            <span class="orcid-error" [innerHTML]="addDelegateParams.successMessage"></span>
        </div>
        <div *ngIf="addDelegateParams.errors?.length > 0">
            <span class="orcid-error" *ngFor='let error of addDelegateParams.errors' [innerHTML]="error"></span><br />
        </div>
        <div class="controls save-btns pull-left">
            <span id="bottom-confirm-delegate-profile" (click)="addDelegate()" class="btn btn-primary"><@orcid.msg 'admin.delegate.button'/></span>
        </div>
    </div>
</div>

<!-- Remove security question -->
<div class="workspace-accordion-item" id="remove-security-question">
    <p>
        <a *ngIf="showRemoveSecurityQuestion" (click)="showRemoveSecurityQuestion = false"><span class="glyphicon glyphicon-chevron-down blue"></span><@orcid.msg 'admin.remove_security_question' /></a>
        <a *ngIf="!showRemoveSecurityQuestion" (click)="showRemoveSecurityQuestion = true"><span class="glyphicon glyphicon-chevron-right blue"></span><@orcid.msg 'admin.remove_security_question' /></a>
    </p>
    <div class="collapsible bottom-margin-small admin-modal" *ngIf="showRemoveSecurityQuestion">
        <div class="form-group">
            <label for="orcid"><@orcid.msg 'admin.remove_security_question.orcid.label' /></label>
            <input type="text" id="orcid" (keyup.enter)="confirmRemoveSecurityQuestion()" [(ngModel)]="orcidOrEmail" placeholder="<@orcid.msg 'admin.remove_security_question.orcid.placeholder' />" class="input-xlarge" />
            <div *ngIf="removeSecurityQuestionResult">
                <span class="orcid-error" [innerHTML]="removeSecurityQuestionResult"></span>
            </div>
        </div>
        <div class="controls save-btns pull-left" *ngIf="!showRemoveSecurityQuestionConfirm">
            <span (click)="confirmRemoveSecurityQuestion()" class="btn btn-primary"><@orcid.msg 'admin.remove_security_question.button'/></span>                     
        </div>
        <div class="controls save-btns pull-left" *ngIf="showRemoveSecurityQuestionConfirm">
            <label class="orcid-error"><@orcid.msg 'admin.remove_security_question.confirm.message'/> {{orcidOrEmail}}?</label><br>           
            <span (click)="removeSecurityQuestion()" class="btn btn-primary"><@orcid.msg 'admin.remove_security_question.confirm.button'/></span>&nbsp;                    
            <a href="" class="cancel-action" (click)="showRemoveSecurityQuestionConfirm = false" (click)="orcidOrEmail = ''" (click)="showRemoveSecurityQuestion = false"><@orcid.msg 'freemarker.btncancel'/></a>
        </div>
    </div>
</div>

<!-- Deprecate record -->
<div class="workspace-accordion-item" id="deprecate-record">
    <p>
        <a *ngIf="showDeprecateRecord" (click)="showDeprecateRecord = false"><span class="glyphicon glyphicon-chevron-down blue"></span><@orcid.msg 'admin.profile_deprecation' /></a>
        <a *ngIf="!showDeprecateRecord" (click)="showDeprecateRecord = true"><span class="glyphicon glyphicon-chevron-right blue"></span><@orcid.msg 'admin.profile_deprecation' /></a>
    </p>
    <div class="collapsible bottom-margin-small admin-modal" *ngIf="showDeprecateRecord">
        <!-- Deprecated -->
        <div class="form-group">
            <label for="deprecated"><@orcid.msg 'admin.profile_deprecation.to_deprecate' /></label>
            <input type="text" id="deprecated" [(ngModel)]="deprecateRecordParams.deprecatedAccount.orcid" (keyup.enter)="confirmDeprecate()" placeholder="<@orcid.msg 'admin.profile_deprecation.placeholder.account_to_deprecate' />" class="input-xlarge">                                   
            <div *ngIf="deprecateRecordParams.deprecatedAccount.errors?.length > 0">
                <span class="orcid-error" *ngFor='let error of deprecateRecordParams.deprecatedAccount.errors' [innerHTML]="error"></span><br />
            </div>
        </div>              
        <!-- Primary -->
        <div class="form-group">
            <label for="primary"><@orcid.msg 'admin.profile_deprecation.primary' /></label>
            <input type="text" id="primary" [(ngModel)]="deprecateRecordParams.primaryAccount.orcid" (keyup.enter)="confirmDeprecate()" placeholder="<@orcid.msg 'admin.profile_deprecation.placeholder.primary_account' />" class="input-xlarge">                                    
            <div *ngIf="deprecateRecordParams.primaryAccount.errors?.length > 0">
                <span class="orcid-error" *ngFor='let error of deprecateRecordParams.primaryAccount.errors' [innerHTML]="error"></span><br />
            </div>
        </div>        
        <div class="controls save-btns pull-left" *ngIf="!showDeprecateRecordConfirm">
            <span id="bottom-confirm-deprecate-record" (click)="confirmDeprecate()" class="btn btn-primary"><@orcid.msg 'admin.profile_deprecation.deprecate_account'/></span>
        </div>
        <br>
        <div class="form-group" *ngIf="deprecateRecordParams.successMessage != null && deprecateRecordParams.successMessage != ''">
            <h3><@orcid.msg 'admin.success'/></h3>
            <p id="success-message">{{deprecateRecordParams.successMessage}}</p>  
            <div class="control-group">
                <a href="" class="cancel-action" (click)="deprecateRecordReset()"><@orcid.msg 'freemarker.btnclose'/></a>
            </div>  
        </div>
        <div class="form-group" *ngIf="showDeprecateRecordConfirm">
            <h3><@orcid.msg 'admin.profile_deprecation.deprecate_account.confirm'/></h3>
            <div class="bottom-margin-small">
                <p><@orcid.msg 'admin.profile_deprecation.deprecate_account.confirm.message.1'/></p>
                <table border="0">
                    <tr>
                        <td><strong><@orcid.msg 'admin.profile_deprecation.orcid'/></strong></td>
                        <td>{{deprecateRecordParams.deprecatedAccount.orcid}}</td>
                    </tr>
                    <tr>
                        <td><strong><@orcid.msg 'admin.profile_deprecation.name'/></strong></td>
                        <td>{{deprecateRecordParams.deprecatedAccount.givenNames}}&nbsp;{{deprecateRecordParams.deprecatedAccount.familyName}}</td>
                    </tr>
                    <tr>
                        <td><strong><@orcid.msg 'admin.profile_deprecation.email'/></strong></td>
                        <td>{{deprecateRecordParams.deprecatedAccount.email}}</td>
                    </tr>           
                </table>
            </div>
            <div class="bottom-margin-small">       
                <p><@orcid.msg 'admin.profile_deprecation.deprecate_account.confirm.message.2'/></p>
                <table>
                    <tr>
                        <td><strong><@orcid.msg 'admin.profile_deprecation.orcid'/></strong></td>
                        <td>{{deprecateRecordParams.primaryAccount.orcid}}</td>
                    </tr>
                    <tr>
                        <td><strong><@orcid.msg 'admin.profile_deprecation.name'/></strong></td>
                        <td>{{deprecateRecordParams.primaryAccount.givenNames}}&nbsp;{{deprecateRecordParams.primaryAccount.familyName}}</td>
                    </tr>
                    <tr>
                        <td><strong><@orcid.msg 'admin.profile_deprecation.email'/></strong></td>
                        <td>{{deprecateRecordParams.primaryAccount.email}}</td>
                    </tr>       
                </table>
            </div>          
            <div class="control-group">
                <button class="btn btn-primary" id="bottom-deprecate-profile" (click)="deprecateRecord()"><@orcid.msg 'admin.profile_deprecation.deprecate_account'/></button>&nbsp;
                <a class="cancel-action" (click)="deprecateRecordReset();"><@orcid.msg 'freemarker.btnclose'/></a>
            </div>
        </div>        
    </div>
</div>





</script>