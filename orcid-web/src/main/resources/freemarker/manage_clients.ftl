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

<script type="text/ng-template" id="view-details-modal">
	<div style="padding: 20px;" class="colorbox-modal">
		<h1><@orcid.msg 'manage_clients.client_information'/></h1><a id="cboxClose" class="btn pull-right close-button">X</a>
		<div id="client-information">
			<span><strong><@orcid.msg 'manage_clients.client_id'/>:</strong>{{clientDetails.clientId}}</span><br />
			<span><strong><@orcid.msg 'manage_clients.client_secret'/>:</strong>{{clientDetails.clientSecret}}</span><br />
		</div>
	</div>
</script>

<script type="text/ng-template" id="edit-client-modal">
	<div style="padding: 20px;" class="colorbox-modal">
		<h1><@orcid.msg 'manage_clients.edit_client'/></h1><a id="cboxClose" class="btn pull-right close-button">X</a>		
		<h3><@orcid.msg 'manage_clients.client_details'/></h3>
		
		<@orcid.msg 'manage_clients.client_type'/>:&nbsp;<span>{{clientToEdit.type}}</span>		
		<br />		
    	<@orcid.msg 'manage_clients.display_name'/><input type="text" ng-model="clientToEdit.displayName" required />
    	<br />
    	<@orcid.msg 'manage_clients.website'/><input type="url" ng-model="clientToEdit.website" required />
    	<br />
    	<@orcid.msg 'manage_clients.description'/><input type="text" ng-model="clientToEdit.shortDescription" required />
    	<br />
    	    	
    	<div ng-show="clientToEdit.redirectUris.redirectUri.length">
	    	<table id="edit-client-table">
	    		<tr ng-repeat='rUri in clientToEdit.redirectUris.redirectUri'>
					<td><@orcid.msg 'manage_clients.redirect_uri'/>:</td>
					<td><input type="url" placeholder="Redirect Uri" class="input-xlarge" ng-model="rUri.value"></td>			 		
					<td>
						<a href ng-click="deleteUri($index)" class="icon-trash blue"></a>
						<a ng-show="$last" href ng-click="addUriToExistingClientTable()" class="icon-plus-sign blue"></a>
					</td>
				</tr>			
	    	</table>
    	</div>
		<div ng-show="!clientToEdit.redirectUris.redirectUri.length">			
			<a href ng-click="addUriToExistingClientTable()" class="icon-plus-sign blue"></a>
		</div>
		    	     	 
    	   	
		<div class="controls save-btns pull-left bottom-margin-small">
			<span id="bottom-submit-update-credential-request" ng-click="submitEditClient($index)" class="btn btn-primary"><@orcid.msg 'manage_clients.update'/></span>				
		</div> 	    	
    <div> 
</script>

<script type="text/ng-template" id="new-client-modal">
	<div style="padding: 20px;" class="colorbox-modal">
		<h1>Add new client</h1><a id="cboxClose" class="btn pull-right close-button">X</a>		
		<div id="errors" ng-repeat="error in errors">
			<ul>
				<li>{{error}}</li>				
			</ul>
		</div>
		<table id="client-table">
			<tbody>
			<tr>
				<td><@orcid.msg 'manage_clients.client_type'/>:</td>
				<td>						
					<@security.authorize ifAnyGranted="ROLE_PREMIUM_GROUP">
						<select ng-model="newClient.type" ng-options='option.value as option.name for option in premiumClientOptions' required>								
						</select>
					</@security.authorize>
					<@security.authorize ifAnyGranted="ROLE_GROUP">
						<select ng-model="newClient.type" ng-options='option.value as option.name for option in clientOptions' required>
						</select>
					</@security.authorize>						
				</td>
			</tr>
			<tr>
				<td><@orcid.msg 'manage_clients.display_name'/>:</td>
				<td><input type="text" placeholder="Display name" class="input-xlarge" ng-model="newClient.displayName"></td>
				<td>&nbsp;</td>
			</tr>
		 	<tr>
		 		<td><@orcid.msg 'manage_clients.website'/>:</td>
		 		<td><input type="url" placeholder="Website" class="input-xlarge" ng-model="newClient.website" required></td>
		 		<td>&nbsp;</td>
		 	</tr>
		 	<tr>
		 		<td><@orcid.msg 'manage_clients.description'/>:</td>
		 		<td><input type="text" placeholder="Description" class="input-xlarge" ng-model="newClient.shortDescription"></td>
		 		<td>&nbsp;</td>
		 	</tr>
		 	<tr ng-repeat='rUri in newClient.redirectUris.redirectUri'>
		 		<td><@orcid.msg 'manage_clients.redirect_uri'/>:</td>
		 		<td><input type="url" placeholder="Redirect Uri" class="input-xlarge" ng-model="rUri.value" required></td>			 		
		 		<td><span id="add-uri" ng-click="addUriToNewClientTable()" class="btn btn-primary"><@orcid.msg 'manage_clients.add'/></span></td>
		 	</tr>
		 	</tbody>			 	
		</table>
		<div class="controls save-btns pull-left bottom-margin-small">
			<span id="bottom-submit-credential-request" ng-click="submitAddClient()" class="btn btn-primary"><@orcid.msg 'manage_clients.submit'/></span>				
		</div>
	</div>
</script>


<div class="row">
	<div class="span3 lhs override">
		<ul class="settings-nav">
			<li><a href="#account-settings"><@orcid.msg 'manage.accountsettings'/></a></li>
			<li><a href="#manage-permissions"><@orcid.msg 'manage.managepermission'/></a></li>
		</ul>
	</div>
	<div class="span9">			
		<div ng-controller="ClientEditCtrl"> 
			<div ng-show="!clients.length" ng-cloak>
				<span><@orcid.msg 'manage_clients.no_clients'/></span>
			</div>	
			<div ng-hide="!clients.length" ng-cloak>
				<ul>
					<li class="bottom-margin-small" ng-repeat='client in clients'>
						<div class="pull-right" style="right: 145px; top: 20px; width: 15px; margin-right:5px;"><a href="#" ng-click="viewDetails($index)" class="icon-zoom-in blue"></a></div>
						<div class="pull-right" style="right: 145px; top: 20px; width: 15px; margin-right:5px;"><a href="#" ng-click="editClient($index)" class="icon-pencil  blue"></a></div>
						<div style="width: 530px;">
							<span><strong><@orcid.msg 'manage_clients.client_id'/>:</strong>{{client.clientId}}</span><br />
			                <span><strong><@orcid.msg 'manage_clients.display_name'/>:</strong>{{client.displayName}}</span><br />	                
			                <span><strong><@orcid.msg 'manage_clients.website'/>:</strong>{{client.website}}</span><br />
			                <span><strong><@orcid.msg 'manage_clients.type'/>:</strong>{{client.type}}</span><br />
			                <span><strong><@orcid.msg 'manage_clients.description'/>:</strong>{{client.shortDescription}}</span><br />	                
		                </div>
		                <div class="bottom-margin-small" ng-repeat='rUri in client.redirectUris.redirectUri'>
		                	<span><strong><@orcid.msg 'manage_clients.type'/></strong>:{{rUri.type}}</span>
		                	<span><strong><@orcid.msg 'manage_clients.redirect_uri'/></strong>:{{rUri.value}}</span>
						</div>
					</li>
				</ul>
			</div>
			
			<@security.authorize ifAnyGranted="ROLE_PREMIUM_GROUP">
				<div class="controls save-btns pull-left">
					<span id="bottom-create-new-client-premium" ng-click="addClient()" class="btn btn-primary"><@orcid.msg 'manage_clients.add'/></span>				
				</div>
			</@security.authorize>
			<@security.authorize ifAnyGranted="ROLE_GROUP">
				<#if (group)?? && (group.orcidClient)?? && !(group.orcidClient?has_content)> 
					<div class="controls save-btns pull-left">
						<span id="bottom-create-new-client" ng-click="addClient()" class="btn btn-primary"><@orcid.msg 'manage_clients.add'/></span>				
					</div>
				</#if>
			</@security.authorize>
		</div>						
	</div>
</div>
</@public >