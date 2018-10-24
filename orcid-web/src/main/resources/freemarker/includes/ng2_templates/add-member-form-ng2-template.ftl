<script type="text/ng-template" id="add-member-form-ng2-template">
<!-- Add new client group -->
	<div class="colorbox-content">
		<div class="row">
			<div class="col-md-12 col-sm-12 col-xs-12">	
    			<h1><@orcid.msg 'manage_groups.add_new_group'/></h1>
			</div>
		</div>
		<div class="row">
			<div class="col-md-12 col-sm-12 col-xs-12">
				<form *ngIf="memberData" >	
					<div class="control-group">
						<label class="relative"><@orcid.msg 'manage_groups.group_name'/></label>
						<div class="relative">
							<input type="text" class="input-xlarge" [(ngModel)]="memberData.groupName.value" name="groupName" placeholder="<@orcid.msg 'manage_groups.name'/>">
						</div>
						<span class="orcid-error" *ngIf="memberData.groupName.errors.length > 0">
							<div *ngFor='let error of memberData.groupName.errors'>{{error}}</div>
						</span>
					</div>
					<div class="control-group">
						<label class="relative"><@orcid.msg 'manage_groups.group_email'/></label>
							<div class="relative">
								<input type="text" class="input-xlarge"  [(ngModel)]="memberData.email.value" name="email" id="groupEmail" placeholder="<@orcid.msg 'manage_groups.email'/>">
						</div>
						<span class="orcid-error" *ngIf="memberData.email.errors.length > 0">
							<div *ngFor='let error of memberData.email.errors'>{{error}}</div>
						</span>
					</div>
					<div class="control-group">
						<label class="relative"><@orcid.msg 'manage_groups.salesforce_id'/></label>
							<div class="relative">
								<input type="text" class="input-xlarge" [(ngModel)]="memberData.salesforceId.value" name="salesforceId" id="groupSalesforceId" placeholder="<@orcid.msg 'manage_groups.salesforce_id'/>">
						</div>
						<span class="orcid-error" *ngIf="memberData.salesforceId.errors.length > 0">
							<div *ngFor='let error of memberData.salesforceId.errors'>{{error}}</div>
						</span>
					</div>
					<div class="control-group">
						<label class="relative"><@orcid.msg 'manage_groups.group_type'/></label>
						<div class="relative">					
							<select [(ngModel)]="memberData.type.value" id="groupType" name="groupType" class="input-xlarge" >			    		
								<#list groupTypes?keys as key>
									<option [attr.value]="${groupTypes[key]}">${groupTypes[key]}</option>
								</#list>
							</select> 
						</div>
						<span class="orcid-error" *ngIf="memberData.type.errors.length > 0">
							<div *ngFor='let error of memberData.type.errors'>{{error}}</div>
						</span>
					</div>
					<div class="control-group">
						<button (click)="sendForm()" class="btn btn-primary"><@orcid.msg 'manage_groups.btnadd'/></button>
						<a href="" class="cancel-action" (click)="closeModal()"><@orcid.msg 'freemarker.btnclose'/></a>
					</div>
				</form>
			</div>							
		</div>
	</div>
</script>