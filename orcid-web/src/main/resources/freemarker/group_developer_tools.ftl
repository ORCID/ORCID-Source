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
							<select class="input-xlarge" ng-model="rUri.type.value">
								<#list redirectUriTypes?keys as key>
									<option value="${key}">${redirectUriTypes[key]}</option>
								</#list>
							</select>							
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

<#-- New client modal -->
<script type="text/ng-template" id="new-client-modal">
	<div style="padding: 20px;">
		<h1><@orcid.msg 'manage.developer_tools.group.add_new'/></h1>
		
		<div class="form-horizontal">
			<div class="control-group">
				<label class="control-label" for="clientname" style="margin-right:10px; text-align:left; width:90px"><@orcid.msg 'manage.developer_tools.group.display_name'/>: </label>
				<div class="relative">
					<input id="clientname" type="text" placeholder="<@orcid.msg 'manage.developer_tools.group.display_name_placeholder'/>" class="input-xlarge" ng-model="newClient.displayName.value" required />
					<span class="orcid-error" ng-show="newClient.displayName.errors.length > 0">
						<div ng-repeat='error in newClient.displayName.errors' ng-bind-html="error"></div>
					</span>
				</div>								
			</div>									
		 	<div class="control-group">
		 		<label class="control-label" for="website" style="margin-right:10px; text-align:left; width:90px"><@orcid.msg 'manage.developer_tools.group.website'/>: </label>
				<div class="relative">
		 			<input id="website" type="text" placeholder="<@orcid.msg 'manage.developer_tools.group.website_placeholder'/>" class="input-xlarge" ng-model="newClient.website.value" required />
					<span class="orcid-error" ng-show="newClient.website.errors.length > 0">
						<div ng-repeat='error in newClient.website.errors' ng-bind-html="error"></div>
					</span>
				</div>	 		
		 	</div>
			<div class="control-group">
				<label class="control-label" for="description" style="margin-right:10px; text-align:left; width:90px"><@orcid.msg 'manage.developer_tools.group.description'/>: </label>
		    	<div class="relative">
					<input id="description" type="text" placeholder="<@orcid.msg 'manage.developer_tools.group.description_placeholder'/>" class="input-xlarge" ng-model="newClient.shortDescription.value" required />
					<span class="orcid-error" ng-show="newClient.shortDescription.errors.length > 0">
						<div ng-repeat='error in newClient.shortDescription.errors' ng-bind-html="error"></div>
					</span>
				</div>	
			</div>			
	    	<div id="new-client-table">
		    	<div class="control-group" ng-repeat="rUri in newClient.redirectUris">						
					<label class="control-label" style="margin-right:10px; text-align:left; width:90px"><@orcid.msg 'manage.developer_tools.group.redirect_uri'/>:</label>
					<a href ng-click="deleteJustCreatedUri($index)" class="glyphicon glyphicon-trash grey"></a>										
					<div class="relative">
						<@orcid.msg 'manage.developer_tools.group.redirect_uri.value'/>:<input type="text" placeholder="<@orcid.msg 'manage.developer_tools.group.redirect_uri_placeholder'/>" class="input-xlarge" ng-model="rUri.value.value"><br />
						<@orcid.msg 'manage.developer_tools.group.redirect_uri.type'/>:<select class="input-xlarge" ng-model="rUri.type.value">
							<#list redirectUriTypes?keys as key>
								<option value="${key}">${redirectUriTypes[key]}</option>
							</#list>
						</select>
						<span class="orcid-error" ng-show="rUri.errors.length > 0">
							<div ng-repeat='error in rUri.errors' ng-bind-html="error"></div>
						</span>						
					</div>
				</div>
		    </div>
		</div>		
		<div>
			<a href ng-click="addUriToNewClientTable()"><span class="glyphicon glyphicon-plus blue"></span><@orcid.msg 'manage.developer_tools.group.add_redirect_uri'/></a>
		</div>
		<div class="controls save-btns pull-left bottom-margin-small">			
			<button class="btn btn-primary" ng-click="submitAddClient()"><@orcid.msg 'manage.developer_tools.group.submit'/></button>
			<a href="" class="cancel-action" ng-click="closeModal()"><@orcid.msg 'freemarker.btnclose'/></a>				
		</div>		
	</div>
</script>

<div class="row">
	<div class="col-md-3 lhs override">
		
	</div>
	<div class="col-md-9 developer-tools">
		<div ng-controller="ClientEditCtrl">	
			<div class="row box">
				<div class="col-md-10">
					<h2><@orcid.msg 'manage.developer_tools.group.title'/></h2>
				</div>
				<div class="col-md-2">				
					<@security.authorize ifAnyGranted="ROLE_PREMIUM_INSTITUTION, ROLE_PREMIUM, ROLE_ADMIN">						
						<a href=""><span id="label btn-primary cboxElement" ng-click="addClient()" class="btn btn-primary"><@orcid.msg 'manage.developer_tools.group.add'/></span></a>										
					</@security.authorize>
					<@security.authorize ifAnyGranted="ROLE_BASIC_INSTITUTION, ROLE_BASIC">
						<#if (group)?? && (group.orcidClient)?? && !(group.orcidClient?has_content)> 
							<a href=""><span id="label btn-primary cboxElement" ng-click="addClient()" class="btn btn-primary"><@orcid.msg 'manage.developer_tools.group.add'/></span></a>				
						</#if>
					</@security.authorize>
				</div>	
			</div>		
			<div class="row">
				<div class="col-md-12 client-api">
					<p><@orcid.msg 'manage.developer_tools.group.description.1' />&nbsp;<a href="<@orcid.msg 'manage.developer_tools.group.description.link.url' />"><@orcid.msg 'manage.developer_tools.group.description.link.text' /></a><@orcid.msg 'manage.developer_tools.group.description.2' /></p>		
					<div ng-show="clients.length == 0" ng-cloak>
						<span><@orcid.msg 'manage.developer_tools.group.no_clients'/></span>
						<span><@orcid.msg 'manage.developer_tools.group.register_now'/>&nbsp;<a href=""><span id="label btn-primary cboxElement" ng-click="addClient()" class="btn btn-primary"><@orcid.msg 'manage.developer_tools.group.add'/></span></a></span>
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
	</div>
</div>

</@public >
