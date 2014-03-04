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

<script type="text/ng-template" id="email-ids-modal">
	<div style="padding:20px">
		<h1><@orcid.msg 'admin.find_ids.results'/></h1>
		<div ng-show="emailIdsMap">
			<table border="0">
				<tr>
					<td><strong><@orcid.msg 'admin.email'/></strong></td>
					<td><strong><@orcid.msg 'admin.orcid'/></strong></td>
				</tr>
				<tr ng-repeat="(email, orcid) in emailIdsMap">
					<td>{{email}}</td>
					<td>{{orcid}}</td>
				</tr>
			</table>
			<div class="controls save-btns pull-right bottom-margin-small">
				<a href="" class="cancel-action" ng-click="closeModal()"><@orcid.msg 'freemarker.btnclose'/></a>
			</div>
		</div>
		<div ng-show="!emailIdsMap">
			<span><@orcid.msg 'admin.find_ids.no_results'/></span>
		</div>
	</div>
</script>

<script type="text/ng-template" id="confirm-deprecation-modal">
  <div style="padding:20px">
    <a id="cboxClose" class="btn pull-right close-button" ng-click="closeModal()">X</a>
    <h1><@orcid.msg 'admin.profile_deprecation.deprecate_account.confirm'/></h1>
    
    <div ng-show="errors.length">
		<span class="orcid-error" ng-repeat='error in errors' ng-bind-html="error"></span><br />
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
    <div class="controls save-btns pull-left bottom-margin-small">
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
		<div class="row">
			<div class="col-md-12 col-sm-12 col-xs-12">	
    			<h1><@orcid.msg 'manage_groups.add_new_group'/></h1>
			</div>
		</div>
		<div class="row">
			<div class="col-md-12 col-sm-12 col-xs-12">	
				<div class="control-group">
	    			<label class="relative"><@orcid.msg 'manage_groups.group_name'/></label>
    				<div class="relative">
      					<input type="text" class="input-xlarge" id="groupName" ng-model="newGroup.groupName.value" placeholder="<@orcid.msg 'manage_groups.name'/>">
    				</div>
					<span class="orcid-error" ng-show="newGroup.groupName.errors.length > 0">
						<div ng-repeat='error in newGroup.groupName.errors' ng-bind-html="error"></div>
					</span>
	  			</div>
				<div class="control-group">
    				<label class="relative"><@orcid.msg 'manage_groups.group_email'/></label>
    					<div class="relative">
      						<input type="text" class="input-xlarge" id="groupEmail" ng-model="newGroup.email.value" placeholder="<@orcid.msg 'manage_groups.email'/>">
    				</div>
					<span class="orcid-error" ng-show="newGroup.email.errors.length > 0">
						<div ng-repeat='error in newGroup.email.errors' ng-bind-html="error"></div>
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
						<div ng-repeat='error in newGroup.type.errors' ng-bind-html="error"></div>
					</span>
  				</div>
				<div class="control-group">
					<button class="btn btn-primary" ng-click="addGroup()"><@orcid.msg 'manage_groups.btnadd'/></button>
					<a href="" class="cancel-action" ng-click="closeModal()"><@orcid.msg 'freemarker.btnclose'/></a>
				</div>
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
					<a href="" class="cancel-action" ng-click="closeModal()"><@orcid.msg 'freemarker.btnclose'/></a>
  				</div>
			<div>
		</div>
	</div>
</script>

<script type="text/ng-template" id="list-groups">
	<div class="colorbox-content">
		<a id="cboxClose" class="btn pull-right close-button" ng-click="closeModal()">X</a>
    	<h1><@orcid.msg 'manage_groups.group_list_title'/></h1>
		<div>
			<div class="relative" ng-show="groups.length">				
				<table class="table table-bordered">
					<tr>
      					<th><@orcid.msg 'manage_groups.orcid'/></th>
						<th><@orcid.msg 'manage_groups.name'/></th>
						<th><@orcid.msg 'manage_groups.email'/></th>
						<th><@orcid.msg 'manage_groups.type'/></th>
					</tr>
					<tr ng-repeat="group in groups">
						<td>{{group.groupOrcid.value}}</td>
						<td>{{group.groupName.value}}</td>
						<td>{{group.email.value}}</td>
						<td>{{group.type.value}}</td>
					</tr>
				</table>
    		</div>
			<div ng-show="!groups.length">
				<span><@orcid.msg 'manage_groups.no_groups'/></span>
			</div>
		</div>
	</div>
</script>


<!-- Admin main Layout -->
<div class="row">
	<!-- Left menu bar -->	
	<div class="col-md-3 col-sm-3 col-xs-12 lhs override">
		<ul class="settings-nav">
			<!-- 
			<li><a href="<@spring.url "/account" />#account-settings">${springMacroRequestContext.getMessage("manage.accountsettings")}</a></li>
			<li><a href="<@spring.url "/account" />#manage-permissions">${springMacroRequestContext.getMessage("manage.managepermission")}</a></li>
			 -->
			<li class="admin-option">
				<a href="admin-actions#add-client"><@orcid.msg 'manage_groups.admin_groups_title'/></a>	
			</li>
			<li class="admin-option">
				<a href="admin-actions#deprecate-profile"><@orcid.msg 'admin.profile_deprecation' /></a>
			</li>
			<li class="admin-option" >
				<a href="admin-actions#deactivate-profile"><@orcid.msg 'admin.profile_deactivation' /></a>
			</li>
			<li class="admin-option">
				<a href="admin-actions#reactivate-profile"><@orcid.msg 'admin.profile_reactivation' /></a>
			</li>					
		</ul>
	</div>
	<!-- Right menu bar -->
	<div class="col-md-9 col-sm-9 col-xs-12 admin-options">	
		<!-- Add new client group -->
		<a name="add-client"></a>
		<div ng-controller="adminGroupsCtrl" class="workspace-accordion-item" ng-cloak>
			<p>
				<a ng-show="showAdminGroupsModal" ng-click="toggleReactivationModal()"><span class="glyphicon glyphicon-chevron-down blue"></span><@orcid.msg 'manage_groups.admin_groups_title'/></a>
				<a ng-hide="showAdminGroupsModal" ng-click="toggleReactivationModal()"><span class="glyphicon glyphicon-chevron-right blue"></span><@orcid.msg 'manage_groups.admin_groups_title'/></a>				
			</p>
			<div class="collapsible bottom-margin-small admin-modal" id="admin_groups_modal" style="display:none;">				
	    		<div class="view-items-link">							
					<a ng-click="showAddGroupModal()">
						<span  class="glyphicon glyphicon-plus-sign blue"></span>
						<@orcid.msg 'manage_groups.add_group_link'/>
					</a>
				</div>
				<!--  
					<div class="view-items-link">
						<a ng-click="listGroups()">
							<span class="glyphicon glyphicon-list-alt blue"></span>
							<@orcid.msg 'manage_groups.view_all_link'/>
						</a>
					</div>
				-->		
			</div>			
		</div>
		
		<!-- Deprecate Profile -->
		<a name="deprecate-profile"></a>
		<div ng-controller="profileDeprecationCtrl" class="workspace-accordion-item" ng-cloak>
			<p>				
				<a ng-hide="showModal" ng-click="toggleDeprecationModal()"><span class="glyphicon glyphicon-chevron-right blue"></span><@orcid.msg 'admin.profile_deprecation' /></a>
				<a ng-show="showModal" ng-click="toggleDeprecationModal()"><span class="glyphicon glyphicon-chevron-down blue"></span><@orcid.msg 'admin.profile_deprecation' /></a>
			</p>		
			<div class="collapsible bottom-margin-small admin-modal" id="deprecation_modal" style="display:none;">		    	
				<div class="form-group">
					<label for="deprecated_orcid"><@orcid.msg 'admin.profile_deprecation.to_deprecate' /></label>
					<input type="text" id="deprecated_orcid" placeholder="<@orcid.msg 'admin.profile_deprecation.placeholder.account_to_deprecate' />" class="form-control" ng-model="deprecatedAccount.orcid" ng-change="findAccountDetails('deprecated')">
					<a href class="glyphicon glyphicon-ok green" ng-show="deprecated_verified"></a>					
					<div id="invalid-regex-deprecated" ng-show="invalid_regex_deprecated" ng-cloak>
						<span class="orcid-error"><@orcid.msg 'admin.profile_deprecation.errors.invalid_regex' /></span>
					</div>
					<div ng-show="deprecatedAccount.errors.length">
						<span class="orcid-error" ng-repeat='error in deprecatedAccount.errors' ng-bind-html="error"></span>	
					</div>
				</div>
				<div class="form-group">
					<label for="deprecated_orcid"><@orcid.msg 'admin.profile_deprecation.primary' /></label>
					<input type="text" id="primary_orcid" placeholder="<@orcid.msg 'admin.profile_deprecation.placeholder.primary_account' />" class="form-control" ng-model="primaryAccount.orcid" ng-change="findAccountDetails('primary')">				
					<a href class="glyphicon glyphicon-ok green" ng-show="primary_verified"></a>					
					<div id="invalid-regex-primary" ng-show="invalid_regex_primary" ng-cloak>
						<span class="orcid-error"><@orcid.msg 'admin.profile_deprecation.errors.invalid_regex' /></span><br />
					</div>
					<div ng-show="primaryAccount.errors.length">
						<span class="orcid-error" ng-repeat='error in primaryAccount.errors' ng-bind-html="error"></span><br />
					</div>			
				</div>				
				<div class="controls save-btns pull-left">
		    		<span id="bottom-confirm-deprecate-profile" ng-click="confirmDeprecateAccount()" class="btn btn-primary"><@orcid.msg 'admin.profile_deprecation.deprecate_account'/></span>
				</div>
			</div>
		</div>
				
		<!-- Deactivate Profile -->
		<a name="deactivate-profile"></a>			  
		<div ng-controller="profileDeactivationAndReactivationCtrl" class="workspace-accordion-item" ng-cloak>
			<p>
				<a  ng-show="showDeactivateModal" ng-click="toggleDeactivationModal()"><span class="glyphicon glyphicon-chevron-down blue"></span></span><@orcid.msg 'admin.profile_deactivation' /></a>
				<a  ng-hide="showDeactivateModal" ng-click="toggleDeactivationModal()"><span class="glyphicon glyphicon-chevron-right blue"></span></span><@orcid.msg 'admin.profile_deactivation' /></a>
			</p>			  	
			<div class="collapsible bottom-margin-small admin-modal" id="deactivation_modal" style="display:none;">					    		
	    		<div class="form-group">
					<label for="orcid_to_deactivate"><@orcid.msg 'admin.profile_deactivation.to_deactivate' /></label>
					<input type="text" id="orcid_to_deactivate" ng-model="orcidToDeactivate" placeholder="<@orcid.msg 'admin.profile_deactivation.placeholder.to_deactivate' />" class="form-control" />					
					<div ng-show="deactivatedAccount.errors.length">
						<span class="orcid-error" ng-repeat='error in deactivatedAccount.errors' ng-bind-html="error"></span><br />
					</div>		
				</div>
				<div class="controls save-btns pull-left">
					<span id="bottom-confirm-deactivate-profile" ng-click="confirmDeactivateAccount()" class="btn btn-primary"><@orcid.msg 'admin.profile_deactivation.deactivate_account'/></span>
				</div>
			</div>
		</div>
		
		<!-- Reactivate Profile -->
		<a name="reactivate-profile"></a>
		<div ng-controller="profileDeactivationAndReactivationCtrl" class="workspace-accordion-item" ng-cloak>
			<p>				
				<a ng-show="showReactivateModal" ng-click="toggleReactivationModal()"><span class="glyphicon glyphicon-chevron-down blue"></span><@orcid.msg 'admin.profile_reactivation' /></a>
				<a ng-hide="showReactivateModal" ng-click="toggleReactivationModal()"><span class="glyphicon glyphicon-chevron-right blue"></span><@orcid.msg 'admin.profile_reactivation' /></a>
			</p>
			<div class="collapsible bottom-margin-small admin-modal" id="reactivation_modal" style="display:none;">					    		
	    		<div class="form-group">
					<label for="orcid_to_reactivate"><@orcid.msg 'admin.profile_reactivation.to_reactivate' /></label>
					<input type="text" id="orcid_to_reactivate" ng-model="orcidToReactivate" placeholder="<@orcid.msg 'admin.profile_reactivation.placeholder.to_reactivate' />" class="form-control" />
					<div ng-show="reactivatedAccount.errors.length">
						<span class="orcid-error" ng-repeat='error in reactivatedAccount.errors' ng-bind-html="error"></span><br />
					</div>
				</div>
				<div class="controls save-btns pull-left">
					<span id="bottom-confirm-reactivate-profile" ng-click="confirmReactivateAccount()" class="btn btn-primary"><@orcid.msg 'admin.profile_reactivation.reactivate_account'/></span>		
				</div>
			</div>
		</div>
		
		
		
		
		
		
		
		
		
		
		
		<!-- Find Ids by email -->
		<a name="find-ids"></a>
		<div ng-controller="findIdsCtrl" class="workspace-accordion-item" ng-cloak>
			<p>
				<a  ng-show="showSection" ng-click="toggleSection()"><span class="glyphicon glyphicon-chevron-down blue"></span></span><@orcid.msg 'admin.find_ids' /></a>
				<a  ng-hide="showSection" ng-click="toggleSection()"><span class="glyphicon glyphicon-chevron-right blue"></span></span><@orcid.msg 'admin.find_ids' /></a>
			</p>			  	
			<div class="collapsible bottom-margin-small admin-modal" id="find_ids_section" style="display:none;">
				<div class="form-group">
					<label for="emails"><@orcid.msg 'admin.find_ids.label' /></label>
					<input type="text" id="emails" ng-model="emails" placeholder="<@orcid.msg 'admin.find_ids.placeholder' />" class="input-xlarge" />
				</div>
				<div class="controls save-btns pull-left">
					<span id="find-ids" ng-click="findIds()" class="btn btn-primary"><@orcid.msg 'admin.find_ids.button'/></span>						
				</div>
			</div>	
		</div>
		
		
		<!-- Reset password -->
		<a name="reset-password"></a>
		<div ng-controller="resetPasswordCtrl" class="workspace-accordion-item" ng-cloak>
			<p>
				<a  ng-show="showSection" ng-click="toggleSection()"><span class="glyphicon glyphicon-chevron-down blue"></span></span><@orcid.msg 'admin.reset_password' /></a>
				<a  ng-hide="showSection" ng-click="toggleSection()"><span class="glyphicon glyphicon-chevron-right blue"></span></span><@orcid.msg 'admin.reset_password' /></a>
			</p>
			<div class="collapsible bottom-margin-small admin-modal" id="reset_password_section" style="display:none;">			
				<div class="form-group">
					<label for="orcid"><@orcid.msg 'admin.reset_password.orcid.label' /></label>
					<input type="text" id="orcid" ng-model="params.orcid" placeholder="<@orcid.msg 'admin.reset_password.orcid.placeholder' />" class="input-xlarge" />
					<label for="password"><@orcid.msg 'admin.reset_password.password.label' /></label>
					<input type="text" id="password" ng-model="params.password" placeholder="<@orcid.msg 'admin.reset_password.password.placeholder' />" class="input-xlarge" />
					<a href ng-click="randomString()" class="glyphicon glyphicon-random blue"><@orcid.msg 'admin.reset_password.password.random'/></a>
					<div ng-show="result != ''">
						<span class="orcid-error" ng-bind-html="result"></span><br />
					</div>
				</div>
				<div class="controls save-btns pull-left">
					<span id="find-ids" ng-click="resetPassword()" class="btn btn-primary"><@orcid.msg 'admin.reset_password.button'/></span>						
				</div>
			
			
			</div>
		</div>
		
		
		
		
	</div>
		
</div>
</@public >