<#--

    =============================================================================

    ORCID (R) Open Source
    http://orcid.org

    Copyright (c) 2012-2014 ORCID, Inc.
    Licensed under an MIT-Style License (MIT)
    http://orcid.org/open-source-license

    This copyright and license information (including a link to the full license)
    shall be included in its entirety in all copies or substantial portion of
    the software.

    =============================================================================

-->
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
      					<span>{{newMember.groupOrcid.value}}&nbsp;(<@orcid.msg 'admin.switch.click.1'/>&nbsp;<a href="<@orcid.msg 'admin.switch.click.link'/>{{newMember.groupOrcid.value}}"><@orcid.msg 'admin.switch.click.here'/></a>&nbsp;<@orcid.msg 'admin.switch.click.2'/>)</span>
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
		<#include "../includes/id_banner.ftl"/>
	</div>
	<!-- Right menu bar -->
	<div class="col-md-9 col-sm-12 col-xs-12 admin-options">	
		
		<!-- Add new client group -->
		<a name="add-client"></a>
		<div ng-controller="manageMembersCtrl" class="workspace-accordion-item" ng-cloak>			
			<p>
				<a ng-show="showAdminGroupsModal" ng-click="toggleGroupsModal()"><span class="glyphicon glyphicon-chevron-down blue"></span><@orcid.msg 'manage_groups.admin_groups_title'/></a>
				<a ng-hide="showAdminGroupsModal" ng-click="toggleGroupsModal()"><span class="glyphicon glyphicon-chevron-right blue"></span><@orcid.msg 'manage_groups.admin_groups_title'/></a>				
			</p>
			<div class="collapsible bottom-margin-small admin-modal" id="admin_groups_modal" style="display:none;">				
	    		<div class="view-items-link">							
					<a ng-click="showAddMemberModal()">
						<span  class="glyphicon glyphicon-plus-sign blue"></span>
						<@orcid.msg 'manage_groups.add_group_link'/>
					</a>
				</div>				
			</div>			
		</div>
		
		<!-- Find -->
		<a name="find"></a>
		<div ng-controller="manageMembersCtrl" class="workspace-accordion-item" ng-cloak>			
			<p>
				<a ng-show="showFindModal" ng-click="toggleFindModal()"><span class="glyphicon glyphicon-chevron-down blue"></span><@orcid.msg 'manage_members.find'/></a>
				<a ng-hide="showFindModal" ng-click="toggleFindModal()"><span class="glyphicon glyphicon-chevron-right blue"></span><@orcid.msg 'manage_members.find'/></a>				
			</p>
			<div class="collapsible bottom-margin-small admin-modal" id="find_edit_modal" style="display:none;">	
				<div class="form-group" ng-show="success_edit_member_message != null">
	    			<div ng-bind-html="success_edit_member_message" class="alert alert-success"></div>
	    		</div>
	    		<div class="form-group" ng-show="success_message != null">
	    			<div ng-bind-html="success_message" class="alert alert-success"></div>
	    		</div>
				<!-- Find -->
				<div class="form-group">
					<div>
						<label for="client_id"><@orcid.msg 'admin.edit_client.any_id' /></label>
						<input type="text" id="any_id" ng-enter="findAny()" ng-model="any_id" placeholder="<@orcid.msg 'admin.edit_client.any_id.placeholder' />" class="input-xlarge" />					
						<span class="orcid-error" ng-show="client.errors.length > 0">
							<div ng-repeat='error in client.errors' ng-bind-html="error"></div>						
						</span>		
						<span class="orcid-error" ng-show="member.errors.length > 0">
							<div ng-repeat='error in member.errors' ng-bind-html="error"></div>
						</span>
					</div>	
					<div class="controls save-btns pull-left">
						<span id="bottom-search" ng-click="findAny()" class="btn btn-primary"><@orcid.msg 'admin.edit_client.find'/></span>
					</div>	
				</div>
				<!-- Edit member -->
				<div ng-show="member.groupOrcid.value.length > 0" ng-cloak>
					<div class="admin-edit-client">
						<div class="row">
							<div class="col-md-12 col-sm-12 col-xs-12">
								<h3><@orcid.msg 'manage_groups.clients'/></h3>
							</div>
						</div>
						<!-- Clients -->						
						<div class="row" ng-show="member.clients.length > 0">
							<div>
								<div class="col-md-5 col-sm-5 col-xs-5">
									<strong><@orcid.msg 'admin.edit_client.client_id'/></strong>
								</div>
								<div class="col-md-7 col-sm-7 col-xs-7">
									<strong><@orcid.msg 'manage.developer_tools.group.display_name'/></strong>
								</div>
								<div ng-repeat="client in member.clients">
									<div class="col-md-5 col-sm-5 col-xs-5">
										{{client.clientId.value}}
									</div>
									<div class="col-md-7 col-sm-7 col-xs-7">
										{{client.displayName.value}}
									</div>
								</div> 									
							</div>
						</div>		
						<div class="row">
							<div class="col-md-12 col-sm-12 col-xs-12">
								<h3><@orcid.msg 'manage_member.edit_member.general'/></h3>
							</div>
						</div>						
						<!-- Name -->
						<div class="row">
							<div class="col-md-12 col-sm-12 col-xs-12">
								<span><@orcid.msg 'manage_groups.group_name'/></span><br />
								<input type="text" ng-model="member.groupName.value" class="full-width-input" />
								<span class="orcid-error" ng-show="member.groupName.errors.length > 0">
									<div ng-repeat='error in member.groupName.errors' ng-bind-html="error"></div>
								</span>	
							</div>
						</div>
						<!-- Salesforce ID -->
						<div class="row">
							<div class="col-md-12 col-sm-12 col-xs-12">
								<span><@orcid.msg 'manage_groups.salesforce_id'/></span><br />
								<input type="text" ng-model="member.salesforceId.value" class="full-width-input" />
								<span class="orcid-error" ng-show="member.salesforceId.errors.length > 0">
									<div ng-repeat='error in member.salesforceId.errors' ng-bind-html="error"></div>
								</span>	
							</div>
						</div>									
						<!-- email -->
						<div class="row">
							<div class="col-md-12 col-sm-12 col-xs-12">
								<span><@orcid.msg 'manage_groups.group_email'/></span><br />
								<input type="text" ng-model="member.email.value" class="full-width-input" />
								<span class="orcid-error" ng-show="member.email.errors.length > 0">
									<div ng-repeat='error in member.email.errors' ng-bind-html="error"></div>
								</span>	
							</div>
						</div>
						<!-- Member type -->
						<div class="control-group">
    						<label class="relative"><@orcid.msg 'manage_groups.group_type'/></label>
    						<div class="relative">					
      							<select id="groupType" name="groupType" class="input-xlarge" ng-model="member.type.value">			    		
									<#list groupTypes?keys as key>
										<option value="${key}">${groupTypes[key]}</option>
									</#list>
								</select> 
    						</div>
							<span class="orcid-error" ng-show="member.type.errors.length > 0">
								<div ng-repeat='error in member.type.errors' ng-bind-html="error"></div>
							</span>
  						</div>
						<!-- Buttons -->
						<div class="row">
							<div class="controls save-btns col-md-12 col-sm-12 col-xs-12">
			    				<span id="bottom-confirm-update-client" ng-click="confirmUpdateMember()" class="btn btn-primary"><@orcid.msg 'admin.edit_client.btn.update'/></span>
							</div>
						</div>						
					</div>
				</div>
				
				<!-- Edit client -->
				<div ng-show="client.clientId != null" ng-cloak>	
					<div class="admin-edit-client">
						<div class="row">
							<div class="col-md-12 col-sm-12 col-xs-12">
								<h3><@orcid.msg 'admin.edit_client.general'/></h3>
							</div>
						</div>
						<!-- Member id -->
						<div class="row">
							<div class="col-md-12 col-sm-12 col-xs-12">
								<span><@orcid.msg 'manage_groups.group_id'/></span><br />
								<i>{{client.memberId.value}}</i><br />								
							</div>
						</div>
						<!-- Member name -->
						<div class="row">
							<div class="col-md-12 col-sm-12 col-xs-12">
								<span><@orcid.msg 'manage_groups.group_name'/></span><br />
								<i>{{client.memberName.value}}</i><br />							
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
						<!-- IdP-->
						<div class="row">
							<div class="col-md-12 col-sm-12 col-xs-12">
								<span><@orcid.msg 'manage.developer_tools.client.idp'/></span><br />
								<input type="text" ng-model="client.authenticationProviderId.value" class="full-width-input" />
								<span class="orcid-error" ng-show="client.authenticationProviderId.errors.length > 0">
									<div ng-repeat='error in client.authenticationProviderId.errors' ng-bind-html="error"></div>
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
						<!-- Client secret -->
						<div class="row">
							<div class="col-md-12 col-sm-12 col-xs-12 dt-description">
								<span><@orcid.msg 'manage.developer_tools.group.secret'/></span><br />
								<input type="text" ng-model="client.clientSecret.value" class="full-width-input" readonly="readonly" ng-click="selectAll($event)"/>								
							</div>
						</div>
						<!-- Persistent tokens -->
						<div class="row">
							<div class="col-md-12 col-sm-12 col-xs-12">
								<input type="checkbox" name="persistentToken" class="small-element middle" ng-model="client.persistentTokenEnabled.value" />
								<span class="middle"><@orcid.msg 'manage_member.edit_client.use_pesistent_tokens'/></span>								
								<span class="orcid-error" ng-show="client.persistentTokenEnabled.errors.length > 0">
									<div ng-repeat='error in client.persistentTokenEnabled.errors' ng-bind-html="error"></div>
								</span>	
							</div>
						</div>
						
						<!-- Allow auto deprecate -->
						<div class="row">
							<div class="col-md-12 col-sm-12 col-xs-12">
								<input type="checkbox" name="persistentToken" class="small-element middle" ng-model="client.allowAutoDeprecate.value" />
								<span class="middle"><@orcid.msg 'manage.developer_tools.group.allow_auto_deprecate'/></span>
							</div>
						</div>
						
						<!-- Redirect uris -->
						<div class="row">			
							<div class="col-md-12 col-sm-12 col-xs-12">
								<h3><@orcid.msg 'admin.edit_client.redirect_uris'/></h3>
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
									<table class="edit-client-table">
										<tr>
											<td class="edit-client-table-col">
												<!-- Type -->
												<div class="col-md-6 col-sm-6 col-xs-12">
													<span class="edit-client-labels"><@orcid.msg 'manage_members.label.type'/></span><br>
													<select class="input-large input-xlarge-full" ng-model="rUri.type.value" ng-change="loadDefaultScopes(rUri)">
														<#list redirectUriTypes?keys as key>
															<option value="${key}">${redirectUriTypes[key]}</option>
														</#list>
													</select>
												</div>
											</td>
											<td class="edit-client-table-col">
												<!-- Scopes -->
												<div class="col-md-4 col-sm-4 col-xs-12">
													<div ng-show="rUri.type.value != 'default'">
														<span class="edit-client-labels"><@orcid.msg 'manage_members.label.scope'/></span><br>
														<multiselect multiple="true" ng-model="rUri.scopes" options="scope as scope for scope in availableRedirectScopes"></multiselect>
													</div>															
												</div>
											</td>
											<td>
												<!-- Delete button -->
												<div class="col-md-1 col-sm-1 col-xs-12">
													<br>
								    				<a href="" id="delete-redirect-uri" ng-click="deleteRedirectUri($index)" class="glyphicon glyphicon-trash grey"></a>
												</div>
											</td>
										</tr>
										<tr>
											<td class="edit-client-table-col">
												<!-- Activity Type-->
												<div class="col-md-6 col-sm-6 col-xs-12">
													<div ng-show="rUri.type.value == 'import-works-wizard'">
														<span class="edit-client-labels"><@orcid.msg 'manage_members.label.work_type'/></span><br>
														<multiselect multiple="true" ng-model="rUri.actType.value[rUri.type.value]" options="actType as actType for actType in importWorkWizard['actTypeList']"></multiselect>
													</div>
												</div>
											</td>
											<td class="edit-client-table-col">
												<!-- Geographical Area-->
												<div class="col-md-4 col-sm-4 col-xs-12">
													<div ng-show="rUri.type.value == 'import-works-wizard'">
														<span class="edit-client-labels"><@orcid.msg 'manage_members.label.geo_area'/></span><br>
														<multiselect multiple="true" ng-model="rUri.geoArea.value[rUri.type.value]" options="geoArea as geoArea for geoArea in importWorkWizard['geoAreaList']"></multiselect>
													</div>
												</div>
											</td>
											<td>
												<!-- Add button -->
												<div class="col-md-1 col-sm-1 col-xs-12">
								    				<a href="" id="load-empty-redirect-uri" ng-click="addRedirectUri()" class="glyphicon glyphicon-plus grey" ng-show="$last"></a>
												</div>
											</td>
										</tr>
									</table>
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

</@public >