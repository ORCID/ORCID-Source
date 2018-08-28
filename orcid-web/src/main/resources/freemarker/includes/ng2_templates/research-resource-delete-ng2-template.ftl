<script type="text/ng-template" id="research-resource-delete-ng2-template">
    <div class="row">
        <div class="col-md-12 col-xs-12 col-sm-12">
            <h3><@orcid.msg 'manage.deleteResearchResource.pleaseConfirm' /></h3>
            <p>{{researchResource?.title}}</p>
            <button class="btn btn-danger " (click)="deleteResearchResource(researchResource.putCode)">
                <@orcid.msg 'freemarker.btnDelete' />
            </button>
            <button class="btn btn-white-no-border cancel-right" (click)="cancelEdit()"><@orcid.msg 'freemarker.btncancel' /></button>
        </div>
    </div>
</script>