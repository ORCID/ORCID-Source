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
<@public nav="developer">

<div class="row developer-tools">
	<div class="col-md-3 col-sm-12 col-xs-12">
		<#include "includes/id_banner.ftl"/>
	</div>
	<div class="col-md-9 col-sm-12 col-xs-12 margin-top-box-mobile">		
		<div ng-controller="ClientEditCtrl">	
			<!-- Header -->
			
			<div class="row">
				<div class="col-md-9 col-sm-10 col-xs-10">
					<h2><@orcid.msg 'manage.developer_tools.group.title'/></h2>
				</div>
				
				<div class="col-md-3 col-sm-2 col-xs-2">				
					<@security.authorize ifAnyGranted="ROLE_PREMIUM_INSTITUTION, ROLE_PREMIUM, ROLE_ADMIN">						
						<a href="" class="pull-right"><span id="label btn-primary cboxElement" ng-click="showAddClient()" class="btn btn-primary"><@orcid.msg 'manage.developer_tools.group.add'/></span></a>										
					</@security.authorize>
					<@security.authorize ifAnyGranted="ROLE_BASIC_INSTITUTION, ROLE_BASIC">
						<#if (group)?? && (group.orcidClient)?? && !(group.orcidClient?has_content)> 							
							<a href="" ng-hide="clients.length > 0"><span id="label btn-primary cboxElement" ng-click="showAddClient()" class="btn btn-primary"><@orcid.msg 'manage.developer_tools.group.add'/></span></a>				
						</#if>
					</@security.authorize>
				</div>				
			</div>
			<div class="row">
				<div class="col-md-12 col-sm-12 col-xs-12">				
					<p class="developer-tools-instructions"><@orcid.msg 'manage.developer_tools.header' /></p>
				</div>
			</div>		
			
			
			
			<!-- View existing credentials -->
			<div class="listing-clients" ng-show="listing" ng-cloack>
				<div class="row">
					<div class="col-md-12 client-api">
						<p><@orcid.msg 'manage.developer_tools.group.description.1' />&nbsp;<a href="<@orcid.msg 'manage.developer_tools.group.description.link.url' />"><@orcid.msg 'manage.developer_tools.group.description.link.text' /></a><@orcid.msg 'manage.developer_tools.group.description.2' /></p>		
						<div ng-show="clients.length == 0" ng-cloak>
							<span><@orcid.msg 'manage.developer_tools.group.no_clients'/></span><br />
							<span><@orcid.msg 'manage.developer_tools.group.register_now'/>&nbsp;<a href="" ng-click="showAddClient()"><@orcid.msg 'manage.developer_tools.group.add'/></a></span>
						</div>	
						<div ng-show="clients.length > 0" ng-cloak>
							<table class="table sub-table">
								<tbody>
									<tr>
										<td colspan="12" class="table-header-dt">
											<@orcid.msg 'manage.developer_tools.group.group.id'/> ${(group.groupOrcid)!} (${(group.type)!})
										</td>						
									</tr>	
									<tr ng-repeat="client in clients">
										<td colspan="8">
											{{client.displayName.value}} (<a href="{{client.website.value}}" target="_blank">{{client.website.value}}</a>)
										</td>												
										<td colspan="4" class="pull-right">										
											<ul class="client-options">
												<li><a href ng-click="viewDetails($index)"><span class="glyphicon glyphicon-eye-open"></span><@orcid.msg 'manage.developer_tools.group.view_credentials_link' /></a></li>	
												<li><a href ng-click="editClient($index)"><span class="glyphicon glyphicon-pencil"></span><@orcid.msg 'manage.developer_tools.group.edit_credentials_link' /></a></li>												
											</ul>										
										</td>									
									</tr>												
								</tbody>
							</table>
						</div>																
					</div>			
				</div>	
			</div>
			
			
			
			
			
			
			
			
			
			<!-- Create new credentials -->
			<div class="create-client" ng-show="creating" ng-cloak>	
				<!-- Name -->
				<div class="row">					
					<div class="col-md-12 col-sm-12 col-xs-12">
						<div class="inner-row">
							<span><strong><@orcid.msg 'manage.developer_tools.group.display_name'/></strong></span>
							<input type="text" placeholder="<@orcid.msg 'manage.developer_tools.group.display_name_placeholder'/>" class="input-xlarge" ng-model="newClient.displayName.value" />
							<span class="orcid-error" ng-show="newClient.displayName.errors.length > 0">
								<div ng-repeat='error in newClient.displayName.errors' ng-bind-html="error"></div>
							</span>					
						</div>		
					</div>																
				</div>
				<!-- Website -->
				<div class="row">	
					<div class="col-md-12 col-sm-12 col-xs-12">
						<div class="inner-row">
							<span><strong><@orcid.msg 'manage.developer_tools.group.website'/></strong></span>
							<input type="text" placeholder="<@orcid.msg 'manage.developer_tools.group.website_placeholder'/>" class="input-xlarge" ng-model="newClient.website.value" />
							<span class="orcid-error" ng-show="newClient.website.errors.length > 0">
								<div ng-repeat='error in newClient.website.errors' ng-bind-html="error"></div>
							</span>					
						</div>		
					</div>	
				</div>
				<!-- Description -->
				<div class="row">					
					<div class="col-md-12 col-sm-12 col-xs-12 dt-description">
						<div class="inner-row">
							<span><strong><@orcid.msg 'manage.developer_tools.group.description'/></strong></span>
							<textarea class="input-xlarge selectable" placeholder="<@orcid.msg 'manage.developer_tools.group.description_placeholder'/>" ng-model="newClient.shortDescription.value"></textarea>						
							<span class="orcid-error" ng-show="newClient.shortDescription.errors.length > 0">
								<div ng-repeat='error in newClient.shortDescription.errors' ng-bind-html="error"></div>
							</span>
						</div>															
					</div>			
				</div>
				<!-- Redirect Uris -->				
				<div ng-repeat="rUri in newClient.redirectUris" class="margin-bottom-box">
					<!-- Header -->
					<div class="row" ng-show="$first">
						<div class="col-md-12 col-sm-12 col-xs-12">
							<div class="inner-row">					
								<h4><@orcid.msg 'manage.developer_tools.redirect_uri'/></h4>
							</div>
						</div>
					</div>
					<!-- Value -->
					<div class="grey-box">
						<div class="row">						
							<div class="col-md-12 col-sm-12 col-xs-12">
								<div class="inner-row">							
									<input type="text" placeholder="<@orcid.msg 'manage.developer_tools.group.redirect_uri_placeholder'/>" class="input-xlarge" ng-model="rUri.value.value" />						
									<span class="orcid-error" ng-show="rUri.value.errors.length > 0">
										<div ng-repeat='error in rUri.value.errors' ng-bind-html="error"></div>
									</span>
								</div>															
							</div>	
						</div>
						<!-- Type -->
						<div class="row">						
							<div class="col-md-12 col-sm-12 col-xs-12">
								<div class="inner-row">
									<select class="input-xlarge" ng-model="rUri.type.value" ng-change="loadDefaultScopes(rUri)">
										<#list redirectUriTypes?keys as key>
											<option value="${key}">${redirectUriTypes[key]}</option>
										</#list>
									</select>
								</div>															
							</div>
						</div>
						<!-- Scopes -->
						<div class="row">						
							<div class="col-md-12 col-sm-12 col-xs-12" ng-show="rUri.type.value != 'default'">
								<div class="inner-row">
									<div class="scope_item" ng-repeat="scope in availableRedirectScopes">
										<div class="small-box">
											<div class="checkbox">										
												<label>
													<input type="checkbox" ng-checked="isChecked(rUri)" ng-click="setSelectedItem(rUri)"/>{{scope}}
												</label>
											</div>										
										</div>								
									</div>
								</div>
							</div>
						</div>
					</div>
				</div>		
				<div class="row">
					<!-- Add redirect uris -->
					<div class="col-md-9 col-sm-9 col-xs-9 add-options">
						<a href="" class="icon-href-bg" ng-click="addRedirectUriToNewClientTable()"><span class="glyphicon glyphicon-plus"></span><@orcid.msg 'manage.developer_tools.edit.add_redirect_uri' /></a>
						<div class="add-options margin-bottom-box">								
							<div ng-show="!hideGoogleUri">
								<h4><@orcid.msg 'manage.developer_tools.test_redirect_uris.title' /></h4>
								<ul class="pullleft-list">
									<li id="google-ruir"><a href="" class="icon-href" ng-click="addTestRedirectUri('google','false')"><span class="glyphicon glyphicon-plus"></span><@orcid.msg 'manage.developer_tools.edit.google'/></a></li>										
								</ul>								
							</div>
						</div>						
					</div>
					<div class="col-md-3 col-sm-3 col-xs-3 sso-api">				
						<ul class="sso-options pull-right">							
							<li><a href ng-click="showViewLayout()" class="back" title="<@orcid.msg 'manage.developer_tools.tooltip.back' />"><span class="glyphicon glyphicon-arrow-left"></span></a></li>
							<li><a href ng-click="addClient()" class="save" title="<@orcid.msg 'manage.developer_tools.tooltip.save' />"><span class="glyphicon glyphicon-floppy-disk"></span></a></li>							
						</ul>					
					</div>		
				</div>		
			</div>
			
			
			
			
			
			
			
			
			
			<!-- Edit credentials -->
			<div class="row">				
			</div>
			
			
			
			
		</div>
	</div>
</div>



<#-- View details modal -->
<script type="text/ng-template" id="view-details-modal">
	<div style="padding: 20px;">
		<h1><@orcid.msg 'manage.developer_tools.group.client_information'/></h1>
		<table class="table table-bordered">
			<tr>
				<td><@orcid.msg 'manage.developer_tools.group.client_id'/></td>
				<td>{{clientDetails.clientId.value}}</td>
			</tr>
			<tr>
				<td><@orcid.msg 'manage.developer_tools.group.client_secret'/></td>
				<td>{{clientDetails.clientSecret.value}}</td>
			</tr>
		</table>
		<p><@orcid.msg 'manage.developer_tools.group.view.redirect_uris' /></p>
		<table class="table">
			<tr ng-repeat="redirect in clientDetails.redirectUris">
				<td><a href="{{redirect.value.value}}" target="_blank">{{redirect.value.value}}</a></td>
				<td><@orcid.msg 'manage.developer_tools.group.type' />:{{redirect.type.value}}</td>
			</tr>
		</table>
		<div class="controls pull-left bottom-margin-small">			
			<a href="" class="cancel-action" ng-click="closeModal()"><@orcid.msg 'freemarker.btnclose'/></a>				
		</div>  		
	</div>
</script>

<#-- Edit client modal -->
<script type="text/ng-template" id="edit-client-modal">
	<div style="padding: 20px;">		
		<h1><@orcid.msg 'manage.developer_tools.group.edit_client'/></h1>			
		<form class="form-horizontal">
			<div class="control-group">
				<label class="control-label" for="clientname" style="margin-right:10px; text-align:left; width:90px"><@orcid.msg 'manage.developer_tools.group.display_name'/>: </label>
		    	<div class="relative">
		    		<input id="clientname" class="input-xlarge" type="text" ng-model="clientToEdit.displayName.value" required />
					<span class="orcid-error" ng-show="clientToEdit.displayName.errors.length > 0">
						<div ng-repeat='error in clientToEdit.displayName.errors' ng-bind-html="error"></div>
					</span>
				</div>
		    </div>
		    <div class="control-group">
				<label class="control-label" for="website" style="margin-right:10px; text-align:left; width:90px"><@orcid.msg 'manage.developer_tools.group.website'/>: </label>
		    	<div class="relative">
					<input id="website" class="input-xlarge" type="text" ng-model="clientToEdit.website.value" required />
					<span class="orcid-error" ng-show="clientToEdit.website.errors.length > 0">
						<div ng-repeat='error in clientToEdit.website.errors' ng-bind-html="error"></div>
					</span>
				</div>	
		    </div>
		    <div class="control-group">
				<label class="control-label" for="description" style="margin-right:10px; text-align:left; width:90px"><@orcid.msg 'manage.developer_tools.group.description'/>: </label>
		    	<div class="relative">
					<input id="description" class="input-xlarge" type="text" ng-model="clientToEdit.shortDescription.value" required />
					<span class="orcid-error" ng-show="clientToEdit.shortDescription.errors.length > 0">
						<div ng-repeat='error in clientToEdit.shortDescription.errors' ng-bind-html="error"></div>
					</span>
				</div>	
		    </div>		    	    	    	
	    	<div ng-show="clientToEdit.redirectUris.length">
	    		<div id="edit-client-table">
		    		<div class="control-group" ng-repeat='rUri in clientToEdit.redirectUris'>						
						<label class="control-label" style="margin-right:10px; text-align:left; width:90px"><@orcid.msg 'manage.developer_tools.group.redirect_uri'/>:</label>
						<a href ng-click="deleteUri($index)" class="glyphicon glyphicon-trash blue"></a>
						<div class="relative">
							<@orcid.msg 'manage.developer_tools.group.redirect_uri.value'/>:<input type="text" class="input-xlarge" ng-model="rUri.value.value">
							<@orcid.msg 'manage.developer_tools.group.redirect_uri.type'/>:
							<select class="input-xlarge" ng-model="rUri.type.value" ng-change="loadDefaultScopes(rUri)">
								<#list redirectUriTypes?keys as key>
									<option value="${key}">${redirectUriTypes[key]}</option>
								</#list>
							</select>							
							
							<div ng-show="rUri.type.value != 'default'">
								<@orcid.msg 'manage_clients.redirect_uri.scope' />:<br />
								<div class='btn-group multiple-select'>
									<button class='btn btn-small'><@orcid.msg 'manage_clients.redirect_uri.scopes.label'/></button>
									<button class='btn btn-small dropdown-toggle' ng-click='scopeSelectorOpen=!scopeSelectorOpen;openDropdown(rUri, true)'><span class='caret'></span></button>
									<div class="scrollable-list" ng-show="scopeSelectorOpen">
										<ul class="dropdown-menu">		
											<li ng-repeat='scope in availableRedirectScopes'><a ng-click='setSelectedItem(rUri)'>{{scope}}<span ng-class='isChecked(rUri)'></span></a></li>
										</ul>
									</div>
								</div>
							</div>															
							
							<span class="orcid-error" ng-show="rUri.errors.length > 0">
								<div ng-repeat='error in rUri.errors' ng-bind-html="error"></div>
							</span>
						</div>	
					</div>
		    	</div>
	    	</div>			
	    	<div>
				<a href ng-click="addUriToExistingClientTable()"><span class="glyphicon glyphicon-plus blue"></span><@orcid.msg 'manage.developer_tools.group.add_redirect_uri'/></a>							
			</div>
			<div class="controls save-btns pull-left bottom-margin-small">
				<button class="btn btn-primary" ng-click="submitEditClient($index)"><@orcid.msg 'manage.developer_tools.group.update'/></button>
				<a href="" class="cancel-action" ng-click="closeModal()"><@orcid.msg 'freemarker.btnclose'/></a>				
			</div>  			  
		</form>		
    <div> 
</script>


</@public >







