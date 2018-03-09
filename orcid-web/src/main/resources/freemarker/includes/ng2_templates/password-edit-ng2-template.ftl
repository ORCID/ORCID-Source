<script type="text/ng-template" id="password-edit-ng2-template">
    <span class="orcid-error"
        *ngIf="changePasswordPojo?.errors?.length > 0">
        <div *ngFor='let error of changePasswordPojo.errors'
            [innerHTML]="error"></div>
    </span>
    <div class="form-group">
        <label for="passwordField">${springMacroRequestContext.getMessage("change_password.oldpassword")}</label>
        <br />
        <input 
            id="passwordField" 
            type="password" 
            name="oldPassword" 
            (keyup.enter)="saveChangePassword()" 
            [(ngModel)]="changePasswordPojo.oldPassword" 
            class="input-xlarge" 
        />
        <span class="required">*</span>
    </div>
    <div class="form-group">
        <label for="passwordField">${springMacroRequestContext.getMessage("change_password.newpassword")}</label>
        <br />
        <input 
            id="password" 
            type="password" 
            name="password" 
            (keyup.enter)="saveChangePassword()" 
            [(ngModel)]="changePasswordPojo.password" 
            class="input-xlarge" 
        />
        <span class="required">*</span> <!-- orcid.passwordHelpPopup -->
    </div>
    <div class="form-group">
        <label for="retypedPassword">${springMacroRequestContext.getMessage("change_password.confirmnewpassword")}</label>
        <br />
        <input 
            id="retypedPassword" 
            type="password"
            name="retypedPassword"
            [(ngModel)]="changePasswordPojo.retypedPassword" 
            (keyup.enter)="saveChangePassword()" 
            class="input-xlarge" 
        />
        <span class="required">*</span>
    </div>
    <button id="bottom-submit-password-change"
        class="btn btn-primary" (click)="saveChangePassword()">${springMacroRequestContext.getMessage("freemarker.btnsavechanges")}</button>                                   
    <a class="cancel-option inner-row" (click)="getChangePassword()" id="bottom-clear-password-changes">${springMacroRequestContext.getMessage("freemarker.btncancel")}</a>
</script>