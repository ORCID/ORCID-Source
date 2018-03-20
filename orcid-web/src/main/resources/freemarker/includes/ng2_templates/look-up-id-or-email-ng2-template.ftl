<script type="text/ng-template" id="look-up-id-or-email-ng2-template">
    <div class="workspace-accordion-item">
        <p>
            <a  *ngIf="showSection" (click)="toggleSection()"><span class="glyphicon glyphicon-chevron-down blue"></span></span><@orcid.msg 'admin.lookup_id_email' /></a>
            <a  *ngIf="!showSection" (click)="toggleSection()"><span class="glyphicon glyphicon-chevron-right blue"></span></span><@orcid.msg 'admin.lookup_id_email' /></a>
        </p>                
        <div class="collapsible bottom-margin-small admin-modal" id="lookup_ids_section" style="display:none;">
            <div class="form-group">
                <label for="idOrEmails"><@orcid.msg 'admin.lookup_id_email' /></label>
                <input type="text" id="idOrEmails" (keyup.enter)="lookupIdOrEmails()" [(ngModel)]="idOrEmails" placeholder="<@orcid.msg 'admin.lookup_id_email.placeholder' />" class="input-xlarge" />
            </div>
            <div class="controls save-btns pull-left">
                <span id="lookup-ids" (click)="lookupIdOrEmails()" class="btn btn-primary"><@orcid.msg 'admin.lookup_id_email.button'/></span>                     
            </div>
        </div>  
    </div>
</script>