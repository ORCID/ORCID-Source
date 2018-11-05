<script type="text/ng-template" id="alt-signin-accounts-revoke-ng2-template">
    <div class="lightbox-container revoke-social">
        <div class="row">
            <div class="col-md-12 col-sm-12 col-xs-12">
                <h3><@orcid.msg 'social.revoke'/></h3>
                <p><@orcid.msg 'social.revoke.body.1'/>{{socialAccount?.idpName}}<@orcid.msg 'social.revoke.body.2'/>{{socialAccount?.accountIdForDisplay}}<@orcid.msg 'social.revoke.body.3'/></p>
                <button class="btn btn-danger" (click)="revoke()"><@orcid.msg 'social.revoke.button'/></button>
                <button class="btn btn-white-no-border cancel-right" (click)="closeModal()">
                    <@orcid.msg 'manage.application_access.revoke.confirm_close' />
                </button>
            </div>
        </div>        
    </div>
</script>