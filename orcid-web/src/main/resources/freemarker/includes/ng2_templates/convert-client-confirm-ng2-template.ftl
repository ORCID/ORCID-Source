<script type="text/ng-template" id="convert-client-confirm-ng2-template">
    <div class="colorbox-content">
        <div class="row">
            <div class="col-md-12 col-sm-12 col-xs-12"> 
                <h1><@orcid.msg 'admin.convert_client.confirmation.title'/></h1>
            </div>
        </div>
        <div class="row">
            <div class="col-md-12 col-sm-12 col-xs-12"> 
                <p><@orcid.msg 'admin.convert_client.confirmation.message'/></p>
            </div>
        </div>
        <div class="row">
            <div class="alert alert-success" *ngIf="clientConversionData.success">
                <@spring.message "admin.convert_client.success"/>
            </div>
            <div class="alert alert-success" *ngIf="clientConversionData.error">
                <p>{{clientConversionData.error}}</p>
            </div>
        </div>
        <div class="row" *ngIf="!clientConversionData.success && !clientConversionData.error">
            <div class="col-md-12 col-sm-12 col-xs-12"> 
                <p><strong>{{clientConversionData.clientId}}</strong> will be converted to type <strong>{{clientConversionData.targetClientType}}</strong> with group ID <strong>{{clientConversionData.groupId}}</strong>.</p>
            </div>
        </div>
        <div class="row">
            <div class="col-md-12 col-sm-12 col-xs-12"> 
                <div class="control-group">
                    <button (click)="confirmConvertClient()" class="btn btn-primary"><@orcid.msg 'admin.convert_client.confirmation.button.label'/></button>&nbsp;
                    <a class="cancel-action" (click)="closeModal()"><@orcid.msg 'freemarker.btnclose'/></a>
                </div>
            </div>                          
        </div>
    </div>
</script>