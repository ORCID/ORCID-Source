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
		<div ng-controller="manageMembersCtrl" class="workspace-accordion-item">			
            AWESOME2
		</div>
		
		<!-- Find consortium -->
        <a name="find-consortium"></a>
        <div ng-controller="internalConsortiumCtrl" class="workspace-accordion-item">           
            AWESOME3
        </div>
</script>

<modalngcomponent elementHeight="645" elementId="addMemberModal" elementWidth="820">
    <add-member-form-ng2></add-member-form-ng2>
</modalngcomponent><!-- Ng2 component -->