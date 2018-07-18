<script type="text/ng-template" id="works-delete-ng2-template">
    <div class="row">
        <div class="col-md-12 col-xs-12 col-sm-12">
            <h3><@orcid.msg 'manage.deleteWork.pleaseConfirm' /></h3>
            {{fixedTitle}}<br />
            <div class="btn btn-danger" (click)="deleteByPutCode(putCode, deleteGroup)">
                <@orcid.msg 'freemarker.btnDelete' />
            </div>
            <a href="" (click)="cancelEdit()"><@orcid.msg 'freemarker.btncancel' /></a>
        </div>
    </div>
</script>