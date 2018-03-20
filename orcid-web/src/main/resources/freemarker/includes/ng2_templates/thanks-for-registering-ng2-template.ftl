<script type="text/ng-template" id="thanks-for-registering-ng2-template">
    <div class="row">
        <div class="col-md-12 col-xs-12 col-sm-12">
            <@spring.message "orcid.frontend.web.thanks_for_registering"/>
            <div class="topBuffer">
                <button class="btn btn-primary" id="modal-close" (click)="verifyEmail()"><@orcid.msg 'orcid.frontend.workspace.send_verification'/></button>
            </div>
        </div>
    </div>
</script>