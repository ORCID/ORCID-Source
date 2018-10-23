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
				<div update-fn="addMember()">	
				<div class="control-group">
	    			<label class="relative"><@orcid.msg 'manage_groups.group_name'/></label>
    				<div class="relative">
      					<input type="text" class="input-xlarge" id="groupName" ng-model="newMember.groupName.value" placeholder="<@orcid.msg 'manage_groups.name'/>">
    				</div>
					<span class="orcid-error" ng-show="newMember.groupName.errors.length > 0">
						<div ng-repeat='error in newMember.groupName.errors' ng-bind-html="error"></div>
					</span>
	  			</div>
				<div class="control-group">
    				<label class="relative"><@orcid.msg 'manage_groups.group_email'/></label>
    					<div class="relative">
      						<input type="text" class="input-xlarge" id="groupEmail" ng-model="newMember.email.value" placeholder="<@orcid.msg 'manage_groups.email'/>">
    				</div>
					<span class="orcid-error" ng-show="newMember.email.errors.length > 0">
						<div ng-repeat='error in newMember.email.errors' ng-bind-html="error"></div>
					</span>
	  			</div>
				<div class="control-group">
    				<label class="relative"><@orcid.msg 'manage_groups.salesforce_id'/></label>
    					<div class="relative">
      						<input type="text" class="input-xlarge" id="groupSalesforceId" ng-model="newMember.salesforceId.value" placeholder="<@orcid.msg 'manage_groups.salesforce_id'/>">
    				</div>
					<span class="orcid-error" ng-show="newMember.salesforceId.errors.length > 0">
						<div ng-repeat='error in newMember.salesforceId.errors' ng-bind-html="error"></div>
					</span>
	  			</div>
				<div class="control-group">
    				<label class="relative"><@orcid.msg 'manage_groups.group_type'/></label>
    				<div class="relative">					
      					<select id="groupType" name="groupType" class="input-xlarge" ng-model="newMember.type.value">			    		
							<#list groupTypes?keys as key>
								<option value="${key}">${groupTypes[key]}</option>
							</#list>
						</select> 
    				</div>
					<span class="orcid-error" ng-show="newMember.type.errors.length > 0">
						<div ng-repeat='error in newMember.type.errors' ng-bind-html="error"></div>
					</span>
  				</div>
				<div class="control-group">
					<button class="btn btn-primary" ng-click="addMember()"><@orcid.msg 'manage_groups.btnadd'/></button>
					<a href="" class="cancel-action" ng-click="closeModal()"><@orcid.msg 'freemarker.btnclose'/></a>
				</div>
				</div>
			</div>							
		</div>
	</div>
</script>