
<script type="text/ng-template" id="manage-member-find-ng2-template">
	
<div class="collapsible bottom-margin-small admin-modal" id="admin_groups_modal">
	<div class="form-group" *ngIf="success_edit_member_message">
		<div class="alert alert-success">{{success_edit_member_message}}</div>
	</div>
	<div class="form-group" *ngIf="updateMessage">
		<div class="alert alert-success">{{updateMessage}}</div>
	</div>
		
	<div class="form-group">
		<div>
			<label for="client_id"><@orcid.msg 'admin.edit_client.any_id' /></label>
			<input type="text" id="any_id" [(ngModel)]="searchId" placeholder="<@orcid.msg 'admin.edit_client.any_id.placeholder' />" class="input-xlarge" />					
			<span class="orcid-error" *ngIf="consortium?.errors?.length > 0">
				<div *ngFor='let error of consortium.errors'>{{error}}</div>
			</span>
		</div>
		<div class="controls save-btns pull-left">
			<span id="bottom-search" (click)="find(searchId)" class="btn btn-primary"><@orcid.msg 'admin.edit_client.find'/></span>
		</div>	
	</div>
	
	<manage-members-find-member-ng2 *ngIf="memberObject" [member]="memberObject" (update)="update($event)"> </manage-members-find-member-ng2>
	<manage-members-find-client-ng2 *ngIf="clientObject" [client]="clientObject" (update)="update($event)"> </manage-members-find-client-ng2>
</div>
</script>

