<script type="text/ng-template" id="works-merge-warning-ng2-template"> 
    <div class="row">
        <div class="col-md-12 col-xs-12 col-sm-12">
            <h3><@orcid.msg 'manage.deleteWork.pleaseConfirm' /></h3>
            <p>{{fixedTitle}}</p>
            <button class="btn btn-danger" (click)="deleteByPutCode(putCode, deleteGroup)">
                <@orcid.msg 'freemarker.btnDelete' />
            </button>
            <button class="btn btn-white-no-border cancel-right" (click)="cancelEdit()"><@orcid.msg 'freemarker.btncancel' /></button>
        </div>
    </div>       
</script>