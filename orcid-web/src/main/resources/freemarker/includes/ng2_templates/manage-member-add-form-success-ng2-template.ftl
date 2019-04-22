<script type="text/ng-template" id="manage-member-add-form-success-ng2-template">
	<div class="colorbox-content">
		<div class="row">
			<div class="col-md-12 col-sm-12 col-xs-12">	
    			<h1><@orcid.msg 'manage_groups.new_group_info'/></h1>
			</div>
		</div>
		<div class="row" *ngIf="newMember">
			<div class="col-md-12 col-sm-12 col-xs-12">
				<div class="control-group">
    				<span><strong><@orcid.msg 'manage_groups.group_name'/></strong></span>
    				<div class="relative">
      					<span>{{newMember.groupName.value}}</span>
    				</div>
	  			</div>
				<div class="control-group">
    				<span><strong><@orcid.msg 'manage_groups.group_email'/></strong></span>
    				<div class="relative">
      					<span>{{newMember.email.value}}</span>
    				</div>
  				</div>
				<div class="control-group">
	    			<span><strong><@orcid.msg 'manage_groups.group_orcid'/></strong></span>
    				<div class="relative">
      					<span>{{newMember.groupOrcid.value}}&nbsp;(<@orcid.msg 'admin.switch.click.1'/>&nbsp;<a href="{{getBaseUri()}}/switch-user?username={{newMember.groupOrcid.value}}"><@orcid.msg 'admin.switch.click.here'/></a>&nbsp;<@orcid.msg 'admin.switch.click.2'/>)</span>
    				</div>
  				</div>
				<div class="control-group" ng-show="newMember.salesforceId != null">
	    			<span><strong><@orcid.msg 'manage_groups.salesforce_id'/></strong></span>
    				<div class="relative">
      					<span>{{newMember.salesforceId.value}}</span>
    				</div>
  				</div>
				<div class="control-group">
    				<span><strong><@orcid.msg 'manage_groups.instructions_title'/></strong></span>
    				<div class="relative">
						<ul>
      						<li><@orcid.msg 'manage_groups.instructions.1'/></li>
							<li><@orcid.msg 'manage_groups.instructions.2'/></li>
							<li><@orcid.msg 'manage_groups.instructions.3'/></li>
						</ul>
    				</div>
					<a class="cancel-action" (click)="closeModal()"><@orcid.msg 'freemarker.btnclose'/></a>
  				</div>
			<div>
		</div>
	</div>
</script>