<script type="text/ng-template" id="resend-claim-ng2-template">    
    <div class="row">
        <div class="col-md-offset-3 col-md-9 col-sm-offset-3 col-sm-3 col-xs-12">
            <h2><@spring.message "resend_claim.title"/></h2>
            <p><small><@orcid.msg "resend_claim.resend_help"/> <a href="mailto:support@orcid.org"><@orcid.msg "resend_claim.labelorg" /></a>.</small></p>            
            <div name="emailAddress">                
            <span class="orcid-error" *ngIf="requestResendClaim.errors.length > 0">
                <div *ngFor="let error of requestResendClaim.errors" [innerHTML]="error"></div>
            </span>
            <div class="alert alert-success" *ngIf="requestResendClaim.successMessage != null">
                <strong><span [innerHTML]="requestResendClaim.successMessage"></span></strong>
            </div>
            <div class="control-group">
                <label for="givenNames" class="control-label"><@orcid.msg "resend_claim.labelEmailAddress" /> </label>
                <div class="controls">                      
                    <input id="email" type="text" [(ngModel)]="requestResendClaim.email" />
                    <span class="required">*</span>
                </div>
                <button class="btn btn-primary topBuffer" (click)="postResendClaimRequest()"><@orcid.msg "resend_claim.resend_claim_button_text" /></button>
            </div>            
        </div>
    </div>
</script>