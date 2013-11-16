<#--

    =============================================================================

    ORCID (R) Open Source
    http://orcid.org

    Copyright (c) 2012-2013 ORCID, Inc.
    Licensed under an MIT-Style License (MIT)
    http://orcid.org/open-source-license

    This copyright and license information (including a link to the full license)
    shall be included in its entirety in all copies or substantial portion of
    the software.

    =============================================================================

-->
<@public >
<script type="text/ng-template" id="success-modal">
  <div style="padding:20px">
    <a id="cboxClose" class="btn pull-right close-button" ng-click="closeModal()">X</a>
    <h1><@orcid.msg 'admin.success'/></h1>
    <p id="success-message">{{successMessage}}</p>    
  </div>
</script>

<script type="text/ng-template" id="confirm-deprecation-modal">
  <div style="padding:20px">
    <a id="cboxClose" class="btn pull-right close-button" ng-click="closeModal()">X</a>
    <h1><@orcid.msg 'admin.profile_deprecation.deprecate_account.confirm'/></h1>
    
    <div ng-show="errors.length">
		<span class="orcid-error" ng-repeat='error in errors' ng-bind-html-unsafe="error"></span><br />
	</div>	    	    
    <div class="bottom-margin-small">
	    <p><@orcid.msg 'admin.profile_deprecation.deprecate_account.confirm.message.1'/></p>
	    <table border="0">
		    <tr>
		    	<td><strong><@orcid.msg 'admin.profile_deprecation.orcid'/></strong></td>
		    	<td>{{deprecatedAccount.orcid}}</td>
		    </tr>
		    <tr>
		    	<td><strong><@orcid.msg 'admin.profile_deprecation.name'/></strong></td>
		    	<td>{{deprecatedAccount.givenNames}}&nbsp;{{deprecatedAccount.familyName}}</td>
		    </tr>
		    <tr>
		    	<td><strong><@orcid.msg 'admin.profile_deprecation.email'/></strong></td>
		    	<td>{{deprecatedAccount.primaryEmail}}</td>
		    </tr>		    
	    </table>
	</div>
	<div class="bottom-margin-small">	    
	    <p><@orcid.msg 'admin.profile_deprecation.deprecate_account.confirm.message.2'/></p>
	    <table>
		    <tr>
		    	<td><strong><@orcid.msg 'admin.profile_deprecation.orcid'/></strong></td>
		    	<td>{{primaryAccount.orcid}}</td>
		    </tr>
		    <tr>
		    	<td><strong><@orcid.msg 'admin.profile_deprecation.name'/></strong></td>
		    	<td>{{primaryAccount.givenNames}}&nbsp;{{primaryAccount.familyName}}</td>
		    </tr>
		    <tr>
		    	<td><strong><@orcid.msg 'admin.profile_deprecation.email'/></strong></td>
		    	<td>{{primaryAccount.primaryEmail}}</td>
		    </tr>	    
	    </table>
	</div>		   	
    <div class="controls save-btns pull-right bottom-margin-small">
    	<span id="bottom-deprecate-profile" ng-click="deprecateAccount()" class="btn btn-primary"><@orcid.msg 'admin.profile_deprecation.deprecate_account'/></span>
	</div>
  </div>
</script>

<script type="text/ng-template" id="confirm-deactivation-modal">
	<div style="padding:20px">
    	<a id="cboxClose" class="btn pull-right close-button" ng-click="closeModal()">X</a>
    	<h1><@orcid.msg 'admin.profile_deactivation.confirm'/></h1>
		<div class="bottom-margin-small">
	    	<p><@orcid.msg 'admin.profile_deactivation.confirm.message'/></p>
	    	<table border="0">
		    	<tr>
			    	<td><strong><@orcid.msg 'admin.profile_deprecation.orcid'/></strong></td>
			    	<td>{{deactivatedAccount.orcid}}</td>
			    </tr>
		    	<tr>
			    	<td><strong><@orcid.msg 'admin.profile_deprecation.name'/></strong></td>
			    	<td>{{deactivatedAccount.givenNames}}&nbsp;{{deactivatedAccount.familyName}}</td>
			    </tr>
		    	<tr>
			    	<td><strong><@orcid.msg 'admin.profile_deprecation.email'/></strong></td>
			    	<td>{{deactivatedAccount.email}}</td>
			    </tr>		    
	    	</table>
		</div>
		<div class="controls save-btns pull-right bottom-margin-small">
	    	<span id="bottom-deactivate-profile" ng-click="deactivateAccount()" class="btn btn-primary"><@orcid.msg 'admin.profile_deactivation.deactivate_account'/></span>
		</div>
	</div>
</script>

<script type="text/ng-template" id="confirm-reactivation-modal">
	<div style="padding:20px">
    	<a id="cboxClose" class="btn pull-right close-button" ng-click="closeModal()">X</a>
    	<h1><@orcid.msg 'admin.profile_reactivation.confirm'/></h1>
		<div class="bottom-margin-small">
	    	<span><@orcid.msg 'admin.profile_reactivation.confirm.message'/></span>
			<br />
			<span>{{orcidToReactivate}}</span>				    	
		</div>
		<div class="controls save-btns pull-right bottom-margin-small">
	    	<span id="bottom-deactivate-profile" ng-click="reactivateAccount()" class="btn btn-primary"><@orcid.msg 'admin.profile_reactivation.reactivate_account'/></span>
		</div>
	</div>
</script>

<script type="text/ng-template" id="add-new-group">
	<div class="colorbox-content">
		<a id="cboxClose" class="btn pull-right close-button" ng-click="closeModal()">X</a>
    	<h1><@orcid.msg 'manage_groups.add_new_group'/></h1>
		<div>
			<div class="control-group">
    			<label class="relative"><@orcid.msg 'manage_groups.group_name'/></label>
    			<div class="relative">
      				<input type="text" class="input-xlarge" id="groupName" ng-model="newGroup.groupName.value" placeholder="<@orcid.msg 'manage_groups.name'/>">
    			</div>
				<span class="orcid-error" ng-show="newGroup.groupName.errors.length > 0">
					<div ng-repeat='error in newGroup.groupName.errors' ng-bind-html-unsafe="error"></div>
				</span>
  			</div>
			<div class="control-group">
    			<label class="relative"><@orcid.msg 'manage_groups.group_email'/></label>
    			<div class="relative">
      				<input type="text" class="input-xlarge" id="groupEmail" ng-model="newGroup.email.value" placeholder="<@orcid.msg 'manage_groups.email'/>">
    			</div>
				<span class="orcid-error" ng-show="newGroup.email.errors.length > 0">
					<div ng-repeat='error in newGroup.email.errors' ng-bind-html-unsafe="error"></div>
				</span>
  			</div>
			<div class="control-group">
    			<label class="relative"><@orcid.msg 'manage_groups.group_type'/></label>
    			<div class="relative">					
      				<select id="groupType" name="groupType" class="input-xlarge" ng-model="newGroup.type.value">			    		
						<#list groupTypes?keys as key>
							<option value="${key}">${groupTypes[key]}</option>
						</#list>
					</select> 
    			</div>
				<span class="orcid-error" ng-show="newGroup.type.errors.length > 0">
					<div ng-repeat='error in newGroup.type.errors' ng-bind-html-unsafe="error"></div>
				</span>
  			</div>
			<div class="control-group">
				<button class="btn btn-primary" ng-click="addGroup()"><@orcid.msg 'manage_groups.btnadd'/></button>
			</div>				
		</div>
	</div>
</script>

<script type="text/ng-template" id="new-group-info">
	<div class="colorbox-content">
		<a id="cboxClose" class="btn pull-right close-button" ng-click="closeModal()">X</a>
    	<h1><@orcid.msg 'manage_groups.new_group_info'/></h1>
		<div>
			<div class="control-group">
    			<span><strong><@orcid.msg 'manage_groups.group_name'/></strong></span>
    			<div class="relative">
      				<span>{{newGroup.groupName.value}}</span>
    			</div>
  			</div>
			<div class="control-group">
    			<span><strong><@orcid.msg 'manage_groups.group_email'/></strong></span>
    			<div class="relative">
      				<span>{{newGroup.email.value}}</span>
    			</div>
  			</div>
			<div class="control-group">
    			<span><strong><@orcid.msg 'manage_groups.group_orcid'/></strong></span>
    			<div class="relative">
      				<span>{{newGroup.groupOrcid.value}}</span>
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
  			</div>
		</div>
	</div>
</script>

<script type="text/ng-template" id="list-groups">
	<div class="colorbox-content">
		<a id="cboxClose" class="btn pull-right close-button" ng-click="closeModal()">X</a>
    	<h1><@orcid.msg 'manage_groups.group_list_title'/></h1>
		<div>
			<div class="relative" ng-show="groups.length > 0">				
				<ul>
      				<li><@orcid.msg 'manage_groups.orcid'/></li>
					<li><@orcid.msg 'manage_groups.name'/></li>
					<li><@orcid.msg 'manage_groups.email'/></li>
					<li><@orcid.msg 'manage_groups.type'/></li>
				</ul>
				<ul ng-repeat="group in groups">
					<li>{{group.groupOrcid}}</li>
					<li>{{group.groupName}}</li>
					<li>{{group.email}}</li>
					<li>{{group.type}}</li>
				</ul>
    		</div>
			<div ng-show="groups.length == 0">
				<span><@orcid.msg 'manage_groups.no_groups'/></span>
			</div>
		</div>
	</div>
</script>

<div class="row">
	<div class="span3 lhs override">
		<ul class="settings-nav">
			<li><a href="<@spring.url "/account" />#account-settings">${springMacroRequestContext.getMessage("manage.accountsettings")}</a></li>
			<li><a href="<@spring.url "/account" />#manage-permissions">${springMacroRequestContext.getMessage("manage.managepermission")}</a></li>
			<@security.authorize ifAnyGranted="ROLE_ADMIN">
				<li><a href="<@spring.url "/admin-actions" />"><@orcid.msg 'admin.workspace_link' /></a></li>
			</@security.authorize>					
		</ul>
	</div>
	<div class="span9">
	
		<div ng-controller="adminGroupsCtrl" class="workspace-accordion-item" ng-cloak>
			<p>
				<@orcid.msg 'manage_groups.add_new_group' />&nbsp;
				<a ng-show="showAdminGroupsModal" ng-click="toggleReactivationModal()" class="icon-minus-sign blue"></a>
				<a ng-hide="showAdminGroupsModal" ng-click="toggleReactivationModal()" class="icon-plus-sign blue"></a>				
			</p>
			<div class="collapsible bottom-margin-small" id="admin_groups_modal" style="display:none;">
				<h2><@orcid.msg 'manage_groups.admin_groups_title'/></h2>
	    		<br />
	    		<div>
	    			<div class="view-items-link">							
							<span><a ng-click="showAddGroupModal()" class="glyphicon glyphicon-plus-sign blue"><@orcid.msg 'manage_groups.add_group_link'/></a></span>
					</div>
					<div class="add-item-link">
							<span><a ng-click="showAllGroupsModal()" class="glyphicon glyphicon-zoom-in blue"><@orcid.msg 'manage_groups.view_all_link'/></a></span>
					</div>		
				</div>
			</div>
		</div>	
		<br />
		<div ng-controller="profileDeprecationCtrl" class="workspace-accordion-item" ng-cloak>
			<p>
				<@orcid.msg 'admin.profile_deprecation' />&nbsp;
				<a ng-hide="showModal" ng-click="toggleDeprecationModal()" class="icon-plus-sign blue"></a>
				<a ng-show="showModal" ng-click="toggleDeprecationModal()" class="icon-minus-sign blue"></a>
			</p>		
			<div class="collapsible bottom-margin-small" id="deprecation_modal" style="display:none;">
		    	<h2><@orcid.msg 'admin.profile_deprecation.deprecate_account.title'/></h2>
		    	<br />
				<div>
					<label for="deprecated_orcid"><@orcid.msg 'admin.profile_deprecation.to_deprecate' /></label>
					<input type="text" id="deprecated_orcid" placeholder="<@orcid.msg 'admin.profile_deprecation.placeholder.account_to_deprecate' />" class="input-xlarge" ng-model="deprecatedAccount.orcid" ng-change="findAccountDetails('deprecated')">
					<a href class="icon-ok green" ng-show="deprecated_verified"></a>
					<a href class="icon-remove red" ng-show="deprecated_verified == false"></a>
					<div id="invalid-regex-deprecated" ng-show="invalid_regex_deprecated" ng-cloak>
						<span class="orcid-error"><@orcid.msg 'admin.profile_deprecation.errors.invalid_regex' /></span><br />
					</div>
					<div ng-show="deprecatedAccount.errors.length">
						<span class="orcid-error" ng-repeat='error in deprecatedAccount.errors' ng-bind-html-unsafe="error"></span><br />	
					</div>
				</div>
				<div>
					<label for="deprecated_orcid"><@orcid.msg 'admin.profile_deprecation.primary' /></label>
					<input type="text" id="primary_orcid" placeholder="<@orcid.msg 'admin.profile_deprecation.placeholder.primary_account' />" class="input-xlarge" ng-model="primaryAccount.orcid" ng-change="findAccountDetails('primary')">				
					<a href class="icon-ok green" ng-show="primary_verified"></a>
					<a href class="icon-remove red" ng-show="primary_verified == false"></a>
					<div id="invalid-regex-primary" ng-show="invalid_regex_primary" ng-cloak>
						<span class="orcid-error"><@orcid.msg 'admin.profile_deprecation.errors.invalid_regex' /></span><br />
					</div>
					<div ng-show="primaryAccount.errors.length">
						<span class="orcid-error" ng-repeat='error in primaryAccount.errors' ng-bind-html-unsafe="error"></span><br />
					</div>				
				</div>
				<div class="controls save-btns pull-left">
		    		<span id="bottom-confirm-deprecate-profile" ng-click="confirmDeprecateAccount()" class="btn btn-primary"><@orcid.msg 'admin.profile_deprecation.deprecate_account'/></span>
				</div>
			</div>
		</div>		
		<br />			  
		<div ng-controller="profileDeactivationAndReactivationCtrl" class="workspace-accordion-item" ng-cloak>
			<p>
				<@orcid.msg 'admin.profile_deactivation' />&nbsp;
				<a  ng-show="showDeactivateModal" ng-click="toggleDeactivationModal()" class="icon-minus-sign blue"></a>
				<a  ng-hide="showDeactivateModal" ng-click="toggleDeactivationModal()" class="icon-plus-sign blue"></a>
			</p>			  	
			<div class="collapsible bottom-margin-small" id="deactivation_modal" style="display:none;">
				<h2><@orcid.msg 'admin.profile_deactivation.deactivate_account.title'/></h2>
	    		<br />
	    		<div>
					<label for="orcid_to_deactivate"><@orcid.msg 'admin.profile_deactivation.to_deactivate' /></label>
					<input type="text" id="orcid_to_deactivate" ng-model="orcidToDeactivate" placeholder="<@orcid.msg 'admin.profile_deactivation.placeholder.to_deactivate' />" class="input-xlarge" />
					<span id="bottom-confirm-deactivate-profile" ng-click="confirmDeactivateAccount()" class="btn btn-primary"><@orcid.msg 'admin.profile_deactivation.deactivate_account'/></span>				
					<div ng-show="deactivatedAccount.errors.length">
						<span class="orcid-error" ng-repeat='error in deactivatedAccount.errors' ng-bind-html-unsafe="error"></span><br />
					</div>		
				</div>
			</div>
		</div>
		<br />
		<div ng-controller="profileDeactivationAndReactivationCtrl" class="workspace-accordion-item" ng-cloak>
			<p>
				<@orcid.msg 'admin.profile_reactivation' />&nbsp;
				<a ng-show="showReactivateModal" ng-click="toggleReactivationModal()" class="icon-minus-sign blue"></a>
				<a ng-hide="showReactivateModal" ng-click="toggleReactivationModal()" class="icon-plus-sign blue"></a>
			</p>
			<div class="collapsible bottom-margin-small" id="reactivation_modal" style="display:none;">
				<h2><@orcid.msg 'admin.profile_reactivation.reactivate_account.title'/></h2>
	    		<br />
	    		<div>
					<label for="orcid_to_reactivate"><@orcid.msg 'admin.profile_reactivation.to_reactivate' /></label>
					<input type="text" id="orcid_to_reactivate" ng-model="orcidToReactivate" placeholder="<@orcid.msg 'admin.profile_reactivation.placeholder.to_reactivate' />" class="input-xlarge" />
					<span id="bottom-confirm-reactivate-profile" ng-click="confirmReactivateAccount()" class="btn btn-primary"><@orcid.msg 'admin.profile_reactivation.reactivate_account'/></span>			
					<div ng-show="reactivatedAccount.errors.length">
						<span class="orcid-error" ng-repeat='error in reactivatedAccount.errors' ng-bind-html-unsafe="error"></span><br />
					</div>		
				</div>
			</div>
		</div>
	</div>
		
</div>
</@public >