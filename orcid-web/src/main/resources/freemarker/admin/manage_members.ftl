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
<@public nav="members">

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
    				<label class="relative"><@orcid.msg 'manage_groups.salesforce_id'/></label>
    					<div class="relative">
      						<input type="text" class="input-xlarge" id="groupSalesforceId" ng-model="newGroup.salesforceId.value" placeholder="<@orcid.msg 'manage_groups.salesforce_id'/>">
    				</div>
					<span class="orcid-error" ng-show="newGroup.salesforceId.errors.length > 0">
						<div ng-repeat='error in newGroup.salesforceId.errors' ng-bind-html="error"></div>
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
      					<span>{{newGroup.groupOrcid.value}}&nbsp;(<@orcid.msg 'admin.switch.click.1'/>&nbsp;<a href="<@orcid.msg 'admin.switch.click.link'/>{{newGroup.groupOrcid.value}}"><@orcid.msg 'admin.switch.click.here'/></a>&nbsp;<@orcid.msg 'admin.switch.click.2'/>)</span>
    				</div>
  				</div>
				<div class="control-group" ng-show="newGroup.salesforceId != null">
	    			<span><strong><@orcid.msg 'manage_groups.salesforce_id'/></strong></span>
    				<div class="relative">
      					<span>{{newGroup.salesforceId.value}}</span>
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
	<div class="col-md-3 col-sm-12 col-xs-12 lhs padding-fix">
		<#include "includes/id_banner.ftl"/>
	</div>
	<!-- Right menu bar -->
	<div class="col-md-9 col-sm-12 col-xs-12 admin-options">	
		
		<!-- Add new client group -->
		<a name="add-client"></a>
		<div ng-controller="adminGroupsCtrl" class="workspace-accordion-item" ng-cloak>
			<p>
				<a ng-show="showAdminGroupsModal" ng-click="toggleGroupsModal()"><span class="glyphicon glyphicon-chevron-down blue"></span><@orcid.msg 'manage_groups.admin_groups_title'/></a>
				<a ng-hide="showAdminGroupsModal" ng-click="toggleGroupsModal()"><span class="glyphicon glyphicon-chevron-right blue"></span><@orcid.msg 'manage_groups.admin_groups_title'/></a>				
			</p>
			<div class="collapsible bottom-margin-small admin-modal" id="admin_groups_modal" style="display:none;">				
	    		<div class="view-items-link">							
					<a ng-click="showAddGroupModal()">
						<span  class="glyphicon glyphicon-plus-sign blue"></span>
						<@orcid.msg 'manage_groups.add_group_link'/>
					</a>
				</div>				
			</div>			
		</div>
		
		<!-- Edit client -->
		<a name="edit-client"></a>
		<div ng-controller="adminEditClientCtrl" class="workspace-accordion-item" ng-cloak>
			<p>
				<a ng-show="showAdminGroupsModal" ng-click="toggleEditClientModal()"><span class="glyphicon glyphicon-chevron-down blue"></span><@orcid.msg 'admin.edit_client.title'/></a>
				<a ng-hide="showAdminGroupsModal" ng-click="toggleEditClientModal()"><span class="glyphicon glyphicon-chevron-right blue"></span><@orcid.msg 'admin.edit_client.title'/></a>				
			</p>
			<div class="collapsible bottom-margin-small admin-modal" id="edit_client_modal" style="display:none;">					    		
	    		<div class="form-group" ng-show="success_message != null">
	    			<div ng-bind-html="success_message" class="alert alert-success"></div>
	    		</div>
	    		<div class="form-group">
					<label for="client_id"><@orcid.msg 'admin.edit_client.client_id' /></label>
					<input type="text" id="client_id" ng-model="client_id" placeholder="<@orcid.msg 'admin.edit_client.client_id.placeholder' />" class="input-xlarge" />					
					<span class="orcid-error" ng-show="client.errors.length > 0 && client.clientId == null">
						<div ng-repeat='error in client.errors' ng-bind-html="error"></div>
					</span>		
				</div>
				<div class="controls save-btns pull-left">
					<span id="bottom-search-client" ng-click="search()" class="btn btn-primary"><@orcid.msg 'admin.edit_client.find'/></span>
				</div>
			</div>
			
			<div ng-show="client.clientId != null" ng-cloak>	
				<div class="admin-edit-client">
					<div class="row">
						<div class="col-md-12 col-sm-12 col-xs-12">
							<h4><@orcid.msg 'admin.edit_client.general'/></h4>
						</div>
					</div>
					<!-- Name -->
					<div class="row">
						<div class="col-md-12 col-sm-12 col-xs-12">
							<span><@orcid.msg 'manage.developer_tools.group.display_name'/></span><br />
							<input type="text" ng-model="client.displayName.value" class="full-width-input" />
							<span class="orcid-error" ng-show="client.displayName.errors.length > 0">
								<div ng-repeat='error in client.displayName.errors' ng-bind-html="error"></div>
							</span>	
						</div>
					</div>
					<!-- Website -->
					<div class="row">
						<div class="col-md-12 col-sm-12 col-xs-12">
							<span><@orcid.msg 'manage.developer_tools.group.website'/></span><br />
							<input type="text" ng-model="client.website.value" class="full-width-input" />
							<span class="orcid-error" ng-show="client.website.errors.length > 0">
								<div ng-repeat='error in client.website.errors' ng-bind-html="error"></div>
							</span>	
						</div>
					</div>
					<!-- Description -->
					<div class="row">
						<div class="col-md-12 col-sm-12 col-xs-12 dt-description">
							<span><@orcid.msg 'manage.developer_tools.group.description'/></span><br />
							<textarea ng-model="client.shortDescription.value"></textarea>
							<span class="orcid-error" ng-show="client.shortDescription.errors.length > 0">
								<div ng-repeat='error in client.shortDescription.errors' ng-bind-html="error"></div>
							</span>	
						</div>
					</div>
					<!-- Redirect uris -->
					<div class="row">			
						<div class="col-md-12 col-sm-12 col-xs-12">
							<h4><@orcid.msg 'admin.edit_client.redirect_uris'/></h4>
						</div>			
					</div>
					<div ng-repeat="rUri in client.redirectUris">
						<div class="admin-edit-client-redirect-uris">
							<div class="row">						
								<!-- URI -->
								<div class="col-md-12 col-sm-12 col-xs-12">
									<input type="text" ng-model="rUri.value.value" class="input-xlarge">
								</div>
							</div>
							<div class="row">
								<!-- Type -->						
								<div class="col-md-6 col-sm-6 col-xs-12">
									<select class="input-large input-xlarge-full" ng-model="rUri.type.value" ng-change="loadDefaultScopes(rUri)">
										<#list redirectUriTypes?keys as key>
											<option value="${key}">${redirectUriTypes[key]}</option>
										</#list>
									</select>
								</div>
								<!-- Scopes -->
								<div class="col-md-4 col-sm-4 col-xs-12">
									<div ng-show="rUri.type.value != 'default'">
										<multiselect multiple="true" ng-model="rUri.scopes" options="scope as scope for scope in availableRedirectScopes"></multiselect>
									</div>															
								</div>
								<!-- Delete button -->
								<div class="col-md-1 col-sm-1 col-xs-12">
				    				<a href="" id="delete-redirect-uri" ng-click="deleteRedirectUri($index)" class="glyphicon glyphicon-trash grey"></a>
								</div>
								<!-- Add button -->
								<div class="col-md-1 col-sm-1 col-xs-12">
				    				<a href="" id="load-empty-redirect-uri" ng-click="addRedirectUri()" class="glyphicon glyphicon-plus grey" ng-show="$last"></a>
								</div>
							</div>
							<div class="row">
								<!-- Errors -->
								<div class="col-md-12 col-sm-12 col-xs-12">
									<span class="orcid-error" ng-show="rUri.errors.length > 0">
										<div ng-repeat='error in rUri.errors' ng-bind-html="error"></div>
									</span>									
								</div>
							</div>
						</div>						
					</div>
					<div class="row" ng-show="client.redirectUris.length == 0 || client.redirectUris == null">
						<div class="controls save-btns col-md-12 col-sm-12 col-xs-12 margin-top-box margin-bottom-box">
							<a href="" ng-click="addRedirectUri()"><@orcid.msg 'manage.developer_tools.edit.add_redirect_uri' /></a>
						</div>
					</div>
					<div class="row">
						<div class="controls save-btns col-md-12 col-sm-12 col-xs-12">
		    				<span id="bottom-confirm-update-client" ng-click="confirmUpdateClient()" class="btn btn-primary"><@orcid.msg 'admin.edit_client.btn.update'/></span>
						</div>
					</div>					
				</div>							
			</div>			
		</div>						
						
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

<script type="text/ng-template" id="confirm-modal">
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

</@public >