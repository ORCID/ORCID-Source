<@public nav="members">

<script type="text/ng-template" id="add-new-member">
	<div class="colorbox-content">
		<div class="row">
			<div class="col-md-12 col-sm-12 col-xs-12">	
    			<h1><@orcid.msg 'manage_groups.add_new_group'/></h1>
			</div>
		</div>
		<div class="row">
			<div class="col-md-12 col-sm-12 col-xs-12">
				<fn-form update-fn="addMember()">	
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
				</fn-form>
			</div>							
		</div>
	</div>
</script>

<script type="text/ng-template" id="new-group-info">
	<div class="colorbox-content">
		<div class="row">
			<div class="col-md-12 col-sm-12 col-xs-12">	
    			<h1><@orcid.msg 'manage_groups.new_group_info'/></h1>
			</div>
		</div>
		<div class="row">
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
      					<span>{{newMember.groupOrcid.value}}&nbsp;(<@orcid.msg 'admin.switch.click.1'/>&nbsp;<a href="${baseUri}/switch-user?username={{newMember.groupOrcid.value}}"><@orcid.msg 'admin.switch.click.here'/></a>&nbsp;<@orcid.msg 'admin.switch.click.2'/>)</span>
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
					<a href="" class="cancel-action" ng-click="closeModal()"><@orcid.msg 'freemarker.btnclose'/></a>
  				</div>
			<div>
		</div>
	</div>
</script>

<!-- Admin main Layout -->
<div class="row">
	<!-- Left menu bar -->	
	<div class="col-md-3 col-sm-12 col-xs-12 lhs padding-fix">
	    <#include "/includes/ng2_templates/id-banner-ng2-template.ftl"/>
	    <id-banner-ng2> </id-banner-ng2>
	</div>


	<!-- Right menu bar -->
	<#include "../includes/ng2_templates/manage-member-ng2-template.ftl">
	

	<div class="col-md-9 col-sm-12 col-xs-12 admin-options">
		<manage-members-ng2></manage-members-ng2>
	</div>
</div>

<script type="text/ng-template" id="multiselect">
	<div class="btn-group">
  		<button type="button" class="btn btn-default dropdown-toggle" ng-click="toggleSelect()" ng-disabled="disabled" ng-class="{'error': !valid()}">
    	{{header}} <span class="caret"></span>
  		</button>  
  		<ul class="dropdown-menu">  	
		    <li>
	      		<input class="form-control input-sm" type="text" ng-model="searchText.label" autofocus="autofocus" placeholder="Filter" />
	    	</li>	    
	    	<li ng-show="multiple" role="presentation" class="">
	      		<button class="btn btn-link btn-xs" ng-click="checkAll()" type="button"><i class="glyphicon glyphicon-ok"></i> Check all</button>
	      		<button class="btn btn-link btn-xs" ng-click="uncheckAll()" type="button"><i class="glyphicon glyphicon-remove"></i> Uncheck all</button>
	    	</li>
			<div class="dropdown-menu-list">
		    	<li ng-repeat="i in items | filter:searchText">
		      		<a ng-click="select(i); focus()">
		        	<i class='glyphicon' ng-class="{'glyphicon-ok': i.checked, 'empty': !i.checked}"></i> {{i.label}}</a>
		    	</li>
	    	</div>    
  		</ul>
	</div>
</script>

<script type="text/ng-template" id="confirm-modal-client">
	<div class="lightbox-container">
		<div class="row">
			<div class="col-md-12 col-xs-12 col-sm-12">
				<h3><@orcid.msg 'admin.edit_client.confirm_update.title' /></h3>	
				<p><@orcid.msg 'admin.edit_client.confirm_update.text' /></p>			
				<p><strong>{{client.displayName.value}}</strong></p>						
    			<div class="btn btn-danger" ng-click="updateClient()">
    				<@orcid.msg 'admin.edit_client.btn.update' />
    			</div>
    			<a href="" ng-click="closeModal()"><@orcid.msg 'freemarker.btncancel' /></a>
			</div>
		</div>
    </div>
</script>

<script type="text/ng-template" id="confirm-modal-member">
	<div class="lightbox-container">
		<div class="row">
			<div class="col-md-12 col-xs-12 col-sm-12">
				<h3><@orcid.msg 'manage_member.edit_member.confirm_update.title' /></h3>	
				<p><@orcid.msg 'manage_member.edit_memeber.confirm_update.text' /></p>			
				<p><strong>{{member.groupName.value}}</strong></p>						
    			<div class="btn btn-danger" ng-click="updateMember()">
    				<@orcid.msg 'manage_member.edit_member.btn.update' />
    			</div>
    			<a href="" ng-click="closeModal()"><@orcid.msg 'freemarker.btncancel' /></a>
			</div>
		</div>
    </div>
</script>

<script type="text/ng-template" id="confirm-modal-consortium">
    <div class="lightbox-container">
        <div class="row">
            <div class="col-md-12 col-xs-12 col-sm-12">
                <h3><@orcid.msg 'manage_member.edit_consortium.confirm_update.title' /></h3>    
                <p><@orcid.msg 'manage_member.edit_consortium.confirm_update.text' /></p>          
                <p><strong>{{member.groupName.value}}</strong></p>                      
                <div class="btn btn-danger" ng-click="updateConsortium()">
                    <@orcid.msg 'manage_member.edit_member.btn.update' />
                </div>
                <a href="" ng-click="closeModal()"><@orcid.msg 'freemarker.btncancel' /></a>
            </div>
        </div>
    </div>
</script>

</@public >