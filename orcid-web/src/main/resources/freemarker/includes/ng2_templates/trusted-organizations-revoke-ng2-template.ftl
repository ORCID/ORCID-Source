<script type="text/ng-template" id="trusted-organizations-revoke-ng2-template">
    <div class="lightbox-container confirm-revoke-access-modal">        
        <div class="row">
            <div class="col-md-12 col-sm-12 col-xs-12 bottomBuffer">        
                <h2><@orcid.msg 'manage.application_access.revoke.confirm_title' /></h2>         
                <p><@orcid.msg 'manage.application_access.revoke.confirm_copy_1' /></p>             
                <p><@orcid.msg 'manage.application_access.revoke.confirm_copy_2' /> {{applicationSummary?.name}} (<@orcid.msg 'manage.application_access.revoke.access' /><span *ngFor="let scope of applicationSummary?.scopePaths | keys; let last = last;">{{scope.value}}<span *ngIf="!last">, </span></span>
                )</p>
            </div>          
        </div>
        <div class="row">
            <div class="col-md-12 col-sm-12 col-xs-12">
                    <button class="btn btn-danger" (click)="revokeAccess()">
                        <@orcid.msg 'manage.application_access.revoke.remove' />
                    </button>
                    <button class="btn btn-white-no-border cancel-right" (click)="closeModal()">
                        <@orcid.msg 'manage.application_access.revoke.confirm_close' />
                    </button>
            </div>
        </div>
    </div>
</script>