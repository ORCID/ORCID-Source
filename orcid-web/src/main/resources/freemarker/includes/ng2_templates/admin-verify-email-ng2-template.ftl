<script type="text/ng-template" id="two-fa-state-ng2-template">
    <div class="workspace-accordion-item" >
        <p>
            <a  *ngIf="showSection" (click)="toggleSection()"><span class="glyphicon glyphicon-chevron-down blue"></span></span><@orcid.msg 'admin.verify_email' /></a>
            <a  *ngIf="!showSection" (click)="toggleSection()"><span class="glyphicon glyphicon-chevron-right blue"></span></span><@orcid.msg 'admin.verify_email' /></a>
        </p>
        <div class="collapsible bottom-margin-small admin-modal" id="verify_email_section" style="display:none;">
            <div class="form-group">                
                <div *ngIf="result">
                    <span class="orcid-error" [innerHTML]="result"></span><br />
                </div>
                <label for="email"><@orcid.msg 'admin.verify_email.title' /></label>
                <input type="text" id="name" (keyup.enter)="verifyEmail()" [(ngModel)]="email" placeholder="<@orcid.msg 'admin.verify_email.placeholder' />" class="input-xlarge" />                                                                                    
            </div>
            <div class="controls save-btns pull-left">
                <span id="verify-email" (click)="verifyEmail()" class="btn btn-primary"><@orcid.msg 'admin.verify_email.btn'/></span>                      
            </div>
        </div>
    </div>
</script>