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

<script type="text/ng-template" id="edit-client-modal">
	<div style="padding: 20px;" class="colorbox-modal">
		<h3 style="margin-bottom: 0px;"><@orcid.msg 'manage_clients.edit_client'/></h3>
		<br />
		<br />
		<strong><@orcid.msg 'manage_clients.client_information'/></strong>
		<span><@orcid.msg 'manage_clients.client_id'/>{{clientToEdit.clientId}}</span>
    	<span><@orcid.msg 'manage_clients.client_secret'/>{{clientToEdit.clientSecret}}</span>
		<br />
		<br />
		<strong><@orcid.msg 'manage_clients.client_details'/></strong>
		<div ng-switch on="clientToEdit.type">
			<select ng-switch-when="ROLE_GROUP">
				<option value="CREATOR">CREATOR</option>
				<option value="UPDATER">UPDATER</option>
			</select>
			<select ng-switch-when="ROLE_PREMIUM_GROUP">
				<option value="PREMIUM_CREATOR">PREMIUM_CREATOR</option>
				<option value="PREMIUM_UPDATER">PREMIUM_UPDATER</option>
			</select>
		</div>		
    	<input type="text" ng-model="clientToEdit.displayName" required />
    	<input type="text" ng-model="clientToEdit.website" required />
    	<input type="text" ng-model="clientToEdit.shortDescription" required />
    	
    	<table id="edit-client-table">
    		<tr ng-repeat='rUri in clientToEdit.redirectUris.redirectUri'>
				<td><@orcid.msg 'manage_clients.redirect_uri'/>:</td>
				<td><input type="text" placeholder="Redirect Uri" class="input-xlarge" ng-model="rUri.value"></td>			 		
				<td><span id="add-uri" ng-click="addRowToClientTable('client-table')" class="btn btn-primary">${springMacroRequestContext.getMessage("manage.spanadd")}</span></td>
			</tr>
    	</table>
    	
    	<a href="" ng-click="closeModal()">${springMacroRequestContext.getMessage("manage.deleteWork.cancel")}</a>
    <div>; 
</script>

<div class="row">
	<div class="span3 lhs override">
		<ul class="settings-nav">
			<li><a href="#account-settings">${springMacroRequestContext.getMessage("manage.accountsettings")}</a></li>
			<li><a href="#manage-permissions">${springMacroRequestContext.getMessage("manage.managepermission")}</a></li>
		</ul>
	</div>
	<div class="span9" id="ng-app" ng-app="orcidApp">		
		<@security.authorize ifAnyGranted="ROLE_PREMIUM_GROUP">
		</@security.authorize>
		<@security.authorize ifAnyGranted="ROLE_GROUP">
		</@security.authorize>
		
		<div ng-controller="ClientEditCtrl">			
			<table>
				<tr>
					<th><@orcid.msg 'manage_clients.client_id'/></th>
					<th><@orcid.msg 'manage_clients.display_name'/></th>										
					<th></th>					
				</tr>
				<tr ng-repeat="client in clients">
					<td>{{client.clientId}}</td>
					<td>{{client.displayName}}</td>										
					<td><a href="#" ng-click="editClient($index)"><@orcid.msg 'manage_clients.details'/></a></td>
				</tr>
			</table>
		</div>
		
		<br />
		<br />
		
		<div ng-controller="ClientEditCtrl">
			<table id="client-table">
				<tbody>
				<tr>
					<td><@orcid.msg 'manage_clients.client_type'/>:</td>
					<td>
						<select ng-model="client.type">
							<@security.authorize ifAnyGranted="ROLE_PREMIUM_GROUP">
								<option value="PREMIUM_CREATOR">PREMIUM_CREATOR</option>
								<option value="PREMIUM_UPDATER">PREMIUM_UPDATER</option>
							</@security.authorize>
							<@security.authorize ifAnyGranted="ROLE_GROUP">
								<option value="CREATOR">CREATOR</option>
								<option value="UPDATER">UPDATER</option>
							</@security.authorize>
						</select>
					</td>
				</tr>
				<tr>
					<td><@orcid.msg 'manage_clients.display_name'/>:</td>
					<td><input type="text" placeholder="Display name" class="input-xlarge" ng-model="newClient.displayName"></td>
					<td>&nbsp;</td>
				</tr>
			 	<tr>
			 		<td><@orcid.msg 'manage_clients.website'/>:</td>
			 		<td><input type="text" placeholder="Website" class="input-xlarge" ng-model="newClient.website"></td>
			 		<td>&nbsp;</td>
			 	</tr>
			 	<tr>
			 		<td><@orcid.msg 'manage_clients.description'/>:</td>
			 		<td><input type="text" placeholder="Description" class="input-xlarge" ng-model="newClient.shortDescription"></td>
			 		<td>&nbsp;</td>
			 	</tr>
			 	<tr ng-repeat='rUri in newClient.redirectUris.redirectUri'>
			 		<td><@orcid.msg 'manage_clients.redirect_uri'/>:</td>
			 		<td><input type="text" placeholder="Redirect Uri" class="input-xlarge" ng-model="rUri.value"></td>			 		
			 		<td><span id="add-uri" ng-click="addRowToClientTable('client-table')" class="btn btn-primary">${springMacroRequestContext.getMessage("manage.spanadd")}</span></td>
			 	</tr>
			 	</tbody>			 	
			</table>
			<div class="controls save-btns pull-left">
				<span id="bottom-submit-credential-request" ng-click="submitCredentials()" class="btn btn-primary">${springMacroRequestContext.getMessage("manage_clients.spansubmit")}</span>				
			</div>
		</div>
		
	</div>
</div>
</@public >