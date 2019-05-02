<script type="text/ng-template" id="manage-member-find-member-ng2-template">
	<!-- Edit member -->
	<div *ngIf="member.groupOrcid.value.length > 0">
		<div class="admin-edit-client">
			<div class="row">
				<div class="col-md-12 col-sm-12 col-xs-12">
					<h3><@orcid.msg 'manage_groups.clients'/></h3>
				</div>
			</div>
			<!-- Clients -->						
			<div class="row" *ngIf="member.clients.length > 0">
				<div>
					<div class="col-md-5 col-sm-5 col-xs-5">
						<strong><@orcid.msg 'admin.edit_client.client_id'/></strong>
					</div>
					<div class="col-md-5 col-sm-5 col-xs-5">
						<strong><@orcid.msg 'manage.developer_tools.group.display_name'/></strong>
					</div>
					<div class="col-md-2 col-sm-2 col-xs-2">
						<strong><@orcid.msg 'manage_member.edit_client.use_OBO'/></strong>
					</div>
					<div *ngFor="let client of member.clients">
						<div class="col-md-5 col-sm-5 col-xs-5">
							{{client.clientId.value}}
						</div>
						<div class="col-md-5 col-sm-5 col-xs-5">
							{{client.displayName.value}}
						</div>
						<div class="col-md-2 col-sm-2 col-xs-2 obo-member-flag">
							<input type="checkbox" name="persistentToken" class="middle" [(ngModel)]="client.userOBOEnabled.value" />
						</div>
					</div>						
				</div>
			</div>		
			<div class="row">
				<div class="col-md-12 col-sm-12 col-xs-12">
					<h3><@orcid.msg 'manage_member.edit_member.general'/></h3>
				</div>
			</div>						
			<!-- Name -->
			<div class="row">
				<div class="col-md-12 col-sm-12 col-xs-12">
					<span><@orcid.msg 'manage_groups.group_name'/></span><br />
					<input type="text" [(ngModel)]="member.groupName.value" name="groupName" class="full-width-input" />
					<span class="orcid-error" *ngIf="member.groupName.errors.length > 0">
						<div *ngFor='let error of member.groupName.errors'>{{error}}</div>
					</span>	
				</div>
			</div>
			<!-- Salesforce ID -->
			<div class="row">
				<div class="col-md-12 col-sm-12 col-xs-12">
					<span><@orcid.msg 'manage_groups.salesforce_id'/></span><br />
					<input type="text" [(ngModel)]="member.salesforceId.value" name="salesforceId" class="full-width-input" />
					<span class="orcid-error" *ngIf="member.salesforceId.errors.length > 0">
						<div *ngFor='let error of member.salesforceId.errors'>{{error}}</div>
					</span>	
				</div>
			</div>									
			<!-- email -->
			<div class="row">
				<div class="col-md-12 col-sm-12 col-xs-12">
					<span><@orcid.msg 'manage_groups.group_email'/></span><br />
					<input type="text" [(ngModel)]="member.email.value" name="email" class="full-width-input" />
					<span class="orcid-error" *ngIf="member.email.errors.length > 0">
						<div *ngFor='let error of member.email.errors'>{{error}}</div>
					</span>	
				</div>
			</div>
			<!-- Member type -->
			<div class="control-group">
				<label class="relative"><@orcid.msg 'manage_groups.group_type'/></label>
				<div class="relative">					
					<select id="groupType" name="groupType" class="input-xlarge" [(ngModel)]="member.type.value">			    		
						<#list groupTypes?keys as key>
							<option [attr.value]="${groupTypes[key]}">${groupTypes[key]}</option>
						</#list>
					</select> 
				</div>
				<span class="orcid-error" *ngIf="member.type.errors.length > 0">
					<div *ngFor='let error of member.type.errors'>{{error}}</div>
				</span>
			</div>
			<!-- Buttons -->
			<div class="row">
				<div class="controls save-btns col-md-12 col-sm-12 col-xs-12">
					<span id="bottom-confirm-update-client" (click)="confirmUpdateMember()" class="btn btn-primary"><@orcid.msg 'admin.edit_client.btn.update'/></span>
				</div>
			</div>						
		</div>
	</div>
</script>

