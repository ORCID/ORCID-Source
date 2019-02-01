<script type="text/ng-template" id="works-delete-ng2-template">
    <div class="row">
        <div class="col-md-12 col-xs-12 col-sm-12">
            <h3><@orcid.msg 'groups.merge.confirm.header'/></h3>
            <p><@orcid.msg 'groups.merge.confirm.you_are_attempting'/> {{mergeCount}} <@orcid.msg 'common.works.lower'/></p><p class="orcid-error"><b><@orcid.msg 'groups.merge.confirm.cannot_undo'/> <@orcid.msg 'groups.merge.confirm.do_you_really'/></b></p>
            <button class="btn btn-danger" (click)="merge()">
                <@orcid.msg 'freemarker.btncontinue' />
            </button>
            <button class="btn btn-white-no-border cancel-right" (click)="cancelEdit()"><@orcid.msg 'freemarker.btncancel' /></button>
        </div>
    </div>
</script>