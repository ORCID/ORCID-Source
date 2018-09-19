<script type="text/ng-template" id="peer-review-delete-ng2-template">
    <div class="row">
        <div class="col-md-12 col-xs-12 col-sm-12">
            <h3><@orcid.msg 'manage.deletePeerReview.pleaseConfirm' /></h3>
            <button class="btn btn-danger " (click)="deletePeerReview(peerReview.putCode.value)">
                <@orcid.msg 'freemarker.btnDelete' />
            </button>
            <button class="btn btn-white-no-border cancel-right" (click)="cancelEdit()"><@orcid.msg 'freemarker.btncancel' /></button>
        </div>
    </div>
</script>