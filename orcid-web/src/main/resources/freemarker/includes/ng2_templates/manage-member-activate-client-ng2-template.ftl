<script type="text/ng-template" id="manage-member-activate-client-ng2-template">
	<div *ngIf="showActivateClientMessages">
        <div class="alert alert-success" *ngIf="!activateClientResults.error">
            <@spring.message "admin.activate_client.activated"/>
        </div>
        <div class="alert alert-success" *ngIf="activateClientResults.error">
            <br>{{activateClientResults.error}}
        </div>
    </div>
    <div class="form-group">
        <label for="client_to_activate"><@orcid.msg 'admin.activate_client.client_id' /></label>
        <input type="text" id="client_to_activate" (keyup.enter)="activateClient()" [(ngModel)]="clientToActivate" placeholder="<@orcid.msg 'admin.activate_client.client_id' />" class="input-xlarge" />
    </div>
    <div class="controls save-btns pull-left">
        <span id="bottom-confirm-activate-client" (click)="activateClient()" class="btn btn-primary"><@orcid.msg 'admin.activate_client.btn.activate'/></span>
    </div>  
</script>

