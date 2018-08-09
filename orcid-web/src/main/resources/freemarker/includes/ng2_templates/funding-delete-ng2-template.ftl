<script type="text/ng-template" id="funding-delete-ng2-template">
    <div class="row">
        <div class="col-md-12 col-xs-12 col-sm-12">
            <h3><@orcid.msg 'manage.deleteFunding.pleaseConfirm' /></h3>
            <p>{{deleteObj.fundingTitle.title.value}}</p>       
            <button id="confirm-delete-funding" class="btn btn-danger" (click)="deleteFunding()">
                <@orcid.msg 'freemarker.btnDelete' />
            </button>
            <button class="btn btn-white-no-border cancel-right" (click)="cancelEdit()"><@orcid.msg 'freemarker.btncancel' /></button>
        </div>
    </div>
</script>