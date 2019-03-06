<script type="text/ng-template" id="delegates-revoke-ng2-template">
    <div class="lightbox-container">
        <h3><@orcid.msg 'manage_delegation.confirmrevoketrustedindividual'/></h3>
        <p> {{delegateNameToRevoke}} (<a href="{{getBaseUri()}}/{{delegateToRevoke}}" target="delegateToRevoke">{{getBaseUri()}}/{{delegateToRevoke}}</a>)</p>
        <form (ngSubmit)="revoke()">
            <div *ngIf="isPasswordConfirmationRequired" ng-cloak>
                <h3><@orcid.msg 'check_password_modal.confirm_password' /></h3>
                <label for="confirm_add_delegate_modal.password" class=""><@orcid.msg 'check_password_modal.password' /></label>
                <input id="confirm_add_delegate_modal.password" type="password" name="confirm_add_delegate_modal.password" ng-model="password" class="input-large"/> <span class="required">*</span>
                <span class="orcid-error" *ngIf="errors?.length > 0">
                    <span *ngFor="let error of errors" [innerHTML]="error"></span>
                </span>
            </div>
            <button type="submit" class="btn btn-danger"><@orcid.msg 'manage_delegation.btnrevokeaccess'/></button>
            <button type="button" class="btn btn-white-no-border cancel-right" (click)="closeModal()"><@orcid.msg 'freemarker.btnclose'/></button>
        </form>
        <div *ngIf="errors?.length === 0">
        </div>
    </div>
</script>