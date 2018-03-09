<script type="text/ng-template" id="thanks-for-verifying-ng2-template">
    <div class="row">
        <div class="col-md-12 col-xs-12 col-sm-12">
            <@spring.message "orcid.frontend.web.primary_email_unverified"/>
            <div class="topBuffer">
                <button class="btn btn-primary" id="modal-close" (click)="verifyEmail()"><@orcid.msg 'orcid.frontend.workspace.send_verification'/></button>
            </div>
        </div>
    </div>
</script>