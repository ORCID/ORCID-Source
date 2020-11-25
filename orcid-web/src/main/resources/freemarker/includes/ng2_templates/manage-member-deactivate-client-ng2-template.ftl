<script type="text/ng-template" id="manage-member-deactivate-client-ng2-template">
	<div *ngIf="showDeactivateClientMessages">
        <div class="alert alert-success" *ngIf="!deactivateClientResults.error">
            <@spring.message "admin.deactivate_client.deactivated"/>
        </div>
        <div class="alert alert-success" *ngIf="deactivateClientResults.error">
            <br>{{deactivateClientResults.error}}
        </div>
    </div>
    <div class="form-group">
        <label for="client_to_deactivate"><@orcid.msg 'admin.deactivate_client.client_id' /></label>
        <input type="text" id="client_to_deactivate" (keyup.enter)="deactivateClient()" [(ngModel)]="clientToDeactivate" placeholder="<@orcid.msg 'admin.deactivate_client.client_id' />" class="input-xlarge" />
    </div>
    <div class="controls save-btns pull-left">
        <span id="bottom-confirm-deactivate-client" (click)="deactivateClient()" class="btn btn-primary"><@orcid.msg 'admin.deactivate_client.btn.deactivate'/></span>
    </div>     
</script>

