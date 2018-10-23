<#include "/includes/ng2_templates/add-member-form-ng2-template.ftl">
<#include "/includes/ng2_templates/manage-member-consortium-ng2-template.ftl">
<#include "/includes/ng2_templates/manage-member-member-ng2-template.ftl">

<script type="text/ng-template" id="manage-member-ng2-template">
<!-- Add new client group -->
		<a name="add-client"></a>
		<div class="workspace-accordion-item">			
			<p >
				<a (click)="toggleCollapse('addMember')" *ngIf="collapseMenu.addMember"><span class="glyphicon glyphicon-chevron-down blue"></span><@orcid.msg 'manage_groups.admin_groups_title'/></a>
				<a (click)="toggleCollapse('addMember')" *ngIf="!collapseMenu.addMember"><span class="glyphicon glyphicon-chevron-right blue"></span><@orcid.msg 'manage_groups.admin_groups_title'/></a>				
			</p>
			<div *ngIf="collapseMenu.addMember" class="collapsible bottom-margin-small admin-modal" id="admin_groups_modal">				
	    		<div class="view-items-link">							
					<a (click)="showModal('addMember')">
						<span  class="glyphicon glyphicon-plus-sign blue"></span>
						<@orcid.msg 'manage_groups.add_group_link'/>
					</a>
				</div>				
			</div>			
		</div>
		
		<!-- Find -->
		<a name="find"></a>
			<div class="workspace-accordion-item">			
			<p>
				<a (click)="toggleCollapse('findMember')" *ngIf="collapseMenu.findMember"><span class="glyphicon glyphicon-chevron-down blue"></span><@orcid.msg 'manage_members.find_member'/></a>
				<a (click)="toggleCollapse('findMember')" *ngIf="!collapseMenu.findMember"><span class="glyphicon glyphicon-chevron-right blue"></span><@orcid.msg 'manage_members.find_member'/></a>				
			</p>
			<div *ngIf="collapseMenu.findMember" class="collapsible bottom-margin-small admin-modal" id="admin_groups_modal">
				<!--<div class="form-group" ng-show="success_edit_member_message != null">
	    			<div ng-bind-html="success_edit_member_message" class="alert alert-success"></div>
	    		</div>
	    		<div class="form-group" ng-show="success_message != null">
	    			<div ng-bind-html="success_message" class="alert alert-success"></div>
	    		</div>
				-->
				<!-- Find -->
				<div class="form-group">
					<div>
						<label for="client_id"><@orcid.msg 'admin.edit_client.any_id' /></label>
						<input type="text" id="any_id" ng-enter="findAny()" ng-model="any_id" placeholder="<@orcid.msg 'admin.edit_client.any_id.placeholder' />" class="input-xlarge" />					
						<span class="orcid-error"> <!-- ng-show="member.errors.length > 0"-->
							<!--<div ng-repeat='error in member.errors' ng-bind-html="error"></div>-->
						</span>
					</div>
					<div class="controls save-btns pull-left">
						<span id="bottom-search" ng-click="findAny()" class="btn btn-primary"><@orcid.msg 'admin.edit_client.find'/></span>
					</div>	
				</div>

				<manage-members-member-ng2> </manage-members-member-ng2>

			</div>			
		</div>

		<!-- Find consortium -->
        <a name="find-consortium"></a>
		<div class="workspace-accordion-item">		
			<p>
				<a (click)="toggleCollapse('findConsortium')" *ngIf="collapseMenu.findConsortium"><span class="glyphicon glyphicon-chevron-down blue"></span><@orcid.msg 'manage_members.manage_consortia'/></a>
				<a (click)="toggleCollapse('findConsortium')" *ngIf="!collapseMenu.findConsortium"><span class="glyphicon glyphicon-chevron-right blue"></span><@orcid.msg 'manage_members.manage_consortia'/></a>				
			</p>
			<div *ngIf="collapseMenu.findConsortium" class="collapsible bottom-margin-small admin-modal" id="admin_groups_modal">
					<!-- Find -->
					<div class="form-group">
						<div>
							<label for="salesForceId"><@orcid.msg 'manage_consortium.salesforce_id' /></label>
							<input type="text" id="salesForceId" ng-enter="findConsortium()" ng-model="salesForceId" placeholder="<@orcid.msg 'manage_consortium.salesforce_id' />" class="input-xlarge" />                   
						</div>  
						<span class="orcid-error"> <!--  ng-show="findConsortiumError" ng-cloak -->
							<!--<@spring.message "manage_consortium.salesforce_id_not_found"/> -->
						</span>
						<div class="controls save-btns pull-left">
							<span id="bottom-search" ng-click="findConsortium()" class="btn btn-primary"><@orcid.msg 'admin.edit_client.find'/></span>
						</div>  
					</div>
					<manage-members-consortium-ng2> </manage-members-consortium-ng2>
			</div>
		</div>
</script>
<modalngcomponent elementHeight="645" elementId="modalAddMember" elementWidth="820">
	<add-member-form-ng2></add-member-form-ng2>
</modalngcomponent><!-- Ng2 component -->


