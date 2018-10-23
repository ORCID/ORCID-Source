<script type="text/ng-template" id="manage-member-consortium-ng2-template">
	<div *ngIf="consortium != null" ng-cloak>
		<div class="admin-edit-consortium">
			<div class="row">
				<h3><@orcid.msg 'manage_consortium.consortium_lead'/></h3>
			</div>                      
			<div class="row">
				<!-- Name -->
				<p>{{consortium.name.value}}</p>
			</div>
			<div class="row">
			<!-- Contacts -->
				<h4><@orcid.msg 'manage_consortium.contacts_heading'/></h4>
				<table>
					<tr>
						<th>Name</th><th>Email</th><th>Role</th><th>ORCID iD</th><th>Self-service enabled</th>
					</tr>
					<tr ng-repeat="contact in consortium.contactsList">
						<td>{{contact.name}}</td><td>{{contact.email}}</td><td>{{consortium.roleMap[contact.role.roleType]}}</td><td>{{contact.orcid}}</td><td>{{contact.selfServiceEnabled}}</td>
					</tr>
				</table>
			</div>
			<!-- Buttons -->
			<div class="row">
				<div class="controls save-btns col-md-12 col-sm-12 col-xs-12">
					<span id="bottom-confirm-update-consortium" ng-click="confirmUpdateConsortium()" class="btn btn-primary"><@orcid.msg 'manage_member.edit_member.btn.update'/></span>
				</div>
			</div>
			<div class="form-group" ng-show="success_edit_member_message != null">
				<div ng-bind-html="success_edit_member_message" class="alert alert-success"></div>
			</div>                      
		</div>
	</div>
</script>

