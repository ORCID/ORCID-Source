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
		<h1><@orcid.msg 'manage_clients.client_information'/></h1>
		<table class="table table-bordered">
			<tr>
				<td><@orcid.msg 'manage_clients.client_id'/></td>
				<td>{{clientDetails.clientId.value}}</td>
			</tr>
			<tr>
				<td><@orcid.msg 'manage_clients.client_secret'/></td>
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
		<h1><@orcid.msg 'manage_clients.edit_client'/></h1>			
		<form class="form-horizontal">
			<div class="control-group">
				<label class="control-label" for="clientname" style="margin-right:10px; text-align:left; width:90px"><@orcid.msg 'manage_clients.display_name'/>: </label>
		    	<div class="relative">
		    		<input id="clientname" class="input-xlarge" type="text" ng-model="clientToEdit.displayName.value" required />
					<span class="orcid-error" ng-show="clientToEdit.displayName.errors.length > 0">
						<div ng-repeat='error in clientToEdit.displayName.errors' ng-bind-html="error"></div>
					</span>
				</div>
		    </div>
		    <div class="control-group">
				<label class="control-label" for="website" style="margin-right:10px; text-align:left; width:90px"><@orcid.msg 'manage_clients.website'/>: </label>
		    	<div class="relative">
					<input id="website" class="input-xlarge" type="text" ng-model="clientToEdit.website.value" required />
					<span class="orcid-error" ng-show="clientToEdit.website.errors.length > 0">
						<div ng-repeat='error in clientToEdit.website.errors' ng-bind-html="error"></div>
					</span>
				</div>	
		    </div>
		    <div class="control-group">
				<label class="control-label" for="description" style="margin-right:10px; text-align:left; width:90px"><@orcid.msg 'manage_clients.description'/>: </label>
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
						<label class="control-label" style="margin-right:10px; text-align:left; width:90px"><@orcid.msg 'manage_clients.redirect_uri'/>:</label>
						<a href ng-click="deleteUri($index)" class="glyphicon glyphicon-trash blue"></a>
						<div class="relative">
							<@orcid.msg 'manage_clients.redirect_uri.value'/>:<input type="text" class="input-xlarge" ng-model="rUri.value.value">
							<@orcid.msg 'manage_clients.redirect_uri.type'/>:
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
				<a href ng-click="addUriToExistingClientTable()"><span class="glyphicon glyphicon-plus blue"></span><@orcid.msg 'manage_clients.add_redirect_uri'/></a>							
			</div>
			<div class="controls save-btns pull-left bottom-margin-small">
				<button class="btn btn-primary" ng-click="submitEditClient($index)"><@orcid.msg 'manage_clients.update'/></button>
				<a href="" class="cancel-action" ng-click="closeModal()"><@orcid.msg 'freemarker.btnclose'/></a>				
			</div>  			  
		</form>		
    <div> 
</script>

<#-- New client modal -->
<script type="text/ng-template" id="new-client-modal">
	<div style="padding: 20px;">
		<h1><@orcid.msg 'manage_clients.add_new'/></h1>
		
		<div class="form-horizontal">
			<div class="control-group">
				<label class="control-label" for="clientname" style="margin-right:10px; text-align:left; width:90px"><@orcid.msg 'manage_clients.display_name'/>: </label>
				<div class="relative">
					<input id="clientname" type="text" placeholder="<@orcid.msg 'manage_clients.display_name_placeholder'/>" class="input-xlarge" ng-model="newClient.displayName.value" required />
					<span class="orcid-error" ng-show="newClient.displayName.errors.length > 0">
						<div ng-repeat='error in newClient.displayName.errors' ng-bind-html="error"></div>
					</span>
				</div>								
			</div>									
		 	<div class="control-group">
		 		<label class="control-label" for="website" style="margin-right:10px; text-align:left; width:90px"><@orcid.msg 'manage_clients.website'/>: </label>
				<div class="relative">
		 			<input id="website" type="text" placeholder="<@orcid.msg 'manage_clients.website_placeholder'/>" class="input-xlarge" ng-model="newClient.website.value" required />
					<span class="orcid-error" ng-show="newClient.website.errors.length > 0">
						<div ng-repeat='error in newClient.website.errors' ng-bind-html="error"></div>
					</span>
				</div>	 		
		 	</div>
			<div class="control-group">
				<label class="control-label" for="description" style="margin-right:10px; text-align:left; width:90px"><@orcid.msg 'manage_clients.description'/>: </label>
		    	<div class="relative">
					<input id="description" type="text" placeholder="<@orcid.msg 'manage_clients.description_placeholder'/>" class="input-xlarge" ng-model="newClient.shortDescription.value" required />
					<span class="orcid-error" ng-show="newClient.shortDescription.errors.length > 0">
						<div ng-repeat='error in newClient.shortDescription.errors' ng-bind-html="error"></div>
					</span>
				</div>	
			</div>			
	    	<div id="new-client-table">
		    	<div class="control-group" ng-repeat="rUri in newClient.redirectUris">						
					<label class="control-label" style="margin-right:10px; text-align:left; width:90px"><@orcid.msg 'manage_clients.redirect_uri'/>:</label>
					<a href ng-click="deleteJustCreatedUri($index)" class="glyphicon glyphicon-trash grey"></a>										
					<div class="relative">
						<@orcid.msg 'manage_clients.redirect_uri.value'/>:<input type="text" placeholder="<@orcid.msg 'manage_clients.redirect_uri_placeholder'/>" class="input-xlarge" ng-model="rUri.value.value"><br />
						<@orcid.msg 'manage_clients.redirect_uri.type'/>:<select class="input-xlarge" ng-model="rUri.type.value">
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
			<a href ng-click="addUriToNewClientTable()"><span class="glyphicon glyphicon-plus blue"></span><@orcid.msg 'manage_clients.add_redirect_uri'/></a>
		</div>
		<div class="controls save-btns pull-left bottom-margin-small">			
			<button class="btn btn-primary" ng-click="submitAddClient()"><@orcid.msg 'manage_clients.submit'/></button>
			<a href="" class="cancel-action" ng-click="closeModal()"><@orcid.msg 'freemarker.btnclose'/></a>				
		</div>		
	</div>
</script>


<div class="row">
	<div class="col-md-3 col-sm-3 col-xs-12">		
		<!-- <#include "admin_menu.ftl"/> -->
	</div>
	<div class="col-md-9 col-sm-9 col-xs-12">			
		<div ng-controller="ClientEditCtrl" class="clients">			
			<div ng-show="!clients.length" ng-cloak>
				<span><@orcid.msg 'manage_clients.no_clients'/></span>
			</div>							
			
			<div ng-hide="!clients.length" ng-cloak>				
					<div class="bottom-margin-small" ng-repeat='client in clients'>
						<div class="pull-right"><a href="#" ng-click="viewDetails($index)" class="glyphicon glyphicon-zoom-in blue"></a></div>
						<div class="pull-right"><a href="#" ng-click="editClient($index)" class="glyphicon glyphicon-pencil  blue"></a></div>
						<div>							
							<h4><@orcid.msg 'manage_clients.client_id'/>: {{client.clientId.value}}</h4>
							<ul>
								<li><span><@orcid.msg 'manage_clients.display_name'/></span>: {{client.displayName.value}}</li>
								<li><span><@orcid.msg 'manage_clients.website'/>:</span> <a href="{{client.website}}" target="_blank">{{client.website.value}}</a></li>
								<li><span><@orcid.msg 'manage_clients.description'/>:</span> {{client.shortDescription.value}}</li>
								<li>
									<div ng-repeat='rUri in client.redirectUris'>			                	
					                	<span><@orcid.msg 'manage_clients.redirect_uri'/></span>: <a href="{{rUri.value.value}}" target="_blank">{{rUri.value.value}}</a>
									</div>
								</li>
							</ul>
		                </div>
					</div>
			</div>
			
			<@security.authorize ifAnyGranted="ROLE_PREMIUM_INSTITUTION, ROLE_PREMIUM, ROLE_ADMIN">
				<div class="controls save-btns pull-left">
					<span id="bottom-create-new-client-premium" ng-click="addClient()" class="btn btn-primary"><@orcid.msg 'manage_clients.add'/></span>				
				</div>
			</@security.authorize>
			<@security.authorize ifAnyGranted="ROLE_BASIC_INSTITUTION, ROLE_BASIC">
				<#if (group)?? && (group.orcidClient)?? && !(group.orcidClient?has_content)> 
					<div class="controls save-btns pull-left" ng-show="!clients.length">
						<span id="bottom-create-new-client" ng-click="addClient()" class="btn btn-primary"><@orcid.msg 'manage_clients.add'/></span>				
					</div>
				</#if>
			</@security.authorize>
		</div>						
	</div>
</div>
</@public >
