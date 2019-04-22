<script type="text/ng-template" id="delegates-add-ng2-template">
    <div *ngIf="emailSearchResult" class="lightbox-container">
        <div *ngIf="emailSearchResult.isSelf">
            <p class="topBuffer"><@orcid.msg 'manage_delegation.sorrynoaccount1'/><@orcid.msg 'manage_delegation.youcantaddyourself'/></p>
            <button class="btn btn-white-no-border" (click)="closeModal()"><@orcid.msg 'freemarker.btnclose'/></button>
        </div>
        <div *ngIf="!emailSearchResult.found">
            <p class="topBuffer"><@orcid.msg 'manage_delegation.sorrynoaccount1'/><b>{{input.text}}</b><@orcid.msg 'manage_delegation.sorrynoaccount2'/></p>
            <p><@orcid.msg 'manage_delegation.musthaveanaccount'/></p>
            <button class="btn btn-white-no-border" (click)="closeModal()"><@orcid.msg 'freemarker.btnclose'/></button>
        </div>
        <div *ngIf="!emailSearchResult.isSelf && emailSearchResult.found">
            <h3><@orcid.msg 'manage_delegation.addtrustedindividual'/></h3>
            <p>{{input.text}}</p>
            <form (ngSubmit)="addDelegateByEmail(input.text)">
                <div *ngIf="isPasswordConfirmationRequired">
                    <h3><@orcid.msg 'check_password_modal.confirm_password' /></h3>
                    <label for="confirm_add_delegate_modal.password"><@orcid.msg 'check_password_modal.password' /></label>
                    <input id="confirm_add_delegate_modal.password" type="password" name="confirm_add_delegate_modal.password" [(ngModel)]"password" class="input-large"/> <span class="required">*</span>
                    <span class="orcid-error" *ngIf="errors?.length > 0">
                        <span *ngFor="let error of errors" [innerHTML]="error"></span>
                    </span>
                </div>
                <button type="submit" class="btn btn-primary"><@orcid.msg 'manage.spanadd'/></button>
                <button type="button" class="btn btn-white-no-border cancel-right" (click)="closeModal()"><@orcid.msg 'freemarker.btnclose'/></button>
            </form>
        </div>
    </div>
    <div *ngIf="!emailSearchResult" class="lightbox-container">
       <h3><@orcid.msg 'manage_delegation.addtrustedindividual'/></h3>
       <div *ngIf="effectiveUserOrcid === delegateToAdd">
          <p class="alert alert-error"><@orcid.msg 'manage_delegation.youcantaddyourself'/></p>
          <button class="btn btn-white-no-border" (click)="closeModal()"><@orcid.msg 'freemarker.btnclose'/></button>
       </div>
       <div *ngIf="!(effectiveUserOrcid === delegateToAdd)">
          <p>{{delegateNameToAdd}} (<a href="{{getBaseUri()}}/{{delegateToAdd}}" target="delegateToAdd">{{getBaseUri()}}/{{delegateToAdd}}</a>)</p>
          <form (ngSubmit)="addDelegate()">
              <div *ngIf="isPasswordConfirmationRequired">
                  <h3><@orcid.msg 'check_password_modal.confirm_password' /></h3>
                  <label for="confirm_add_delegate_modal.password"><@orcid.msg 'check_password_modal.password' /></label>
                  <input id="confirm_add_delegate_modal.password" type="password" name="confirm_add_delegate_modal.password" [(ngModel)]="password" class="input-large"/> <span class="required">*</span>
                  <span class="orcid-error" *ngIf="errors?.length > 0">
                      <span *ngFor="let error of errors" [innerHTML]="error"></span>
                  </span>
              </div>
              <button type="submit" class="btn btn-primary"><@orcid.msg 'manage.spanadd'/></button>
              <button type="button" class="btn btn-white-no-border cancel-right" (click)="closeModal()"><@orcid.msg 'freemarker.btnclose'/></button>
          </form>
       </div>
    </div>
</script>