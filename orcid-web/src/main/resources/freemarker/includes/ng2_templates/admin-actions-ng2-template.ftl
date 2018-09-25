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
    <div class="form-group" *ngIf="showResetPassword">
        <div>
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
            <label class="orcid-error"><@orcid.msg 'admin.reset_password.confirm.message'/> {{resetPasswordParams.orcidOrEmail}}?</label>
            <span (click)="resetPassword()" class="btn btn-primary"><@orcid.msg 'change_password.confirmnewpassword'/></span> 
        </div>    
    </div>
</div>

<!-- Verify email -->
<div class="workspace-accordion-item" id="verify-email">
    <p>
        <a *ngIf="showVerifyEmail" (click)="showVerifyEmail = false"><span class="glyphicon glyphicon-chevron-down blue"></span><@orcid.msg 'admin.verify_email' /></a>
        <a *ngIf="!showVerifyEmail" (click)="showVerifyEmail = true"><span class="glyphicon glyphicon-chevron-right blue"></span><@orcid.msg 'admin.verify_email' /></a>
    </p>
    <div class="form-group" *ngIf="showVerifyEmail">
        <div>            
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

<!--  -->
<div class="workspace-accordion-item" id="">
    <p>
        <a *ngIf="show" (click)="show = false"><span class="glyphicon glyphicon-chevron-down blue"></span><@orcid.msg 'admin.' /></a>
        <a *ngIf="!show" (click)="show = true"><span class="glyphicon glyphicon-chevron-right blue"></span><@orcid.msg 'admin.' /></a>
    </p>
    <div class="form-group" *ngIf="show">
    </div>
</div>

</script>