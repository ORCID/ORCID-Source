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
	<div style="padding:20px">
		<a id="cboxClose" class="btn pull-right close-button" ng-click="closeColorBox()">X</a>
		<h1><@orcid.msg 'manage_clients.client_information'/></h1>
		<table class="table table-bordered">
			<tr>
				<td><@orcid.msg 'manage_clients.client_id'/></td>
				<td>{{clientDetails.clientId}}</td>
			</tr>
			<tr>
				<td><@orcid.msg 'manage_clients.client_secret'/></td>
				<td>{{clientDetails.clientSecret}}</td>
			</tr>
		</table>		
	</div>
</script>

<script type="text/ng-template" id="edit-client-modal">
	<div style="padding: 20px;" class="colorbox-modal">
		<a id="cboxClose" class="btn pull-right close-button" ng-click="closeColorBox()">X</a>
		<h1><@orcid.msg 'manage_clients.edit_client'/></h1>
		<div id="errors" ng-repeat="error in errors" class="alert">
			<ul>
				<li>{{error}}</li>
			</ul>
		</div>		
		<form class="form-horizontal">
			<div class="control-group">
				<label class="control-label" for="clientname" style="margin-right:10px; text-align:left; width:90px"><@orcid.msg 'manage_clients.display_name'/>: </label>
		    	<input id="clientname" type="text" ng-model="clientToEdit.displayName" required />
		    </div>
		    <div class="control-group">
				<label class="control-label" for="website" style="margin-right:10px; text-align:left; width:90px"><@orcid.msg 'manage_clients.website'/>: </label>
		    	<input id="website" type="url" ng-model="clientToEdit.website" required />	
		    </div>
		    <div class="control-group">
				<label class="control-label" for="description" style="margin-right:10px; text-align:left; width:90px"><@orcid.msg 'manage_clients.description'/>: </label>
		    	<input id="description" type="text" ng-model="clientToEdit.shortDescription" required />	
		    </div>		    	    	    	
	    	<div ng-show="clientToEdit.redirectUris.redirectUri.length">
	    		<div id="edit-client-table">
		    		<div class="control-group" ng-repeat='rUri in clientToEdit.redirectUris.redirectUri'>						
						<label class="control-label" style="margin-right:10px; text-align:left; width:90px"><@orcid.msg 'manage_clients.redirect_uri'/>:</label>
						<input type="url" placeholder="Redirect Uri"  ng-model="rUri.value">						
						<a href ng-click="deleteUri($index)" class="icon-trash blue"></a>
						<a ng-show="$last" href ng-click="addUriToExistingClientTable()" class="icon-plus-sign blue"></a>						
					</div>
		    	</div>
	    	</div>
	    	<div ng-show="!clientToEdit.redirectUris.redirectUri.length">			
				<a href ng-click="addUriToExistingClientTable()" class="icon-plus-sign blue"></a>
			</div>
			<div class="controls save-btns pull-right bottom-margin-small">
				<span id="bottom-submit-update-credential-request" ng-click="submitEditClient($index)" class="btn btn-primary"><@orcid.msg 'manage_clients.update'/></span>				
			</div> 	    	
		</form>
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
		
		
		<@security.authorize ifAnyGranted="ROLE_BASIC">
			<input type="hidden" id="client_type" value="UPDATER" />
		</@security.authorize>
		
		<@security.authorize ifAnyGranted="ROLE_BASIC_INSTITUTION">
			<input type="hidden" id="client_type" value="CREATOR" />
		</@security.authorize>
		
		<@security.authorize ifAnyGranted="ROLE_PREMIUM">
			<input type="hidden" id="client_type" value="UPDATER" />
		</@security.authorize>
		
		<@security.authorize ifAnyGranted="ROLE_PREMIUM_INSTITUTION">
			<input type="hidden" id="client_type" value="CREATOR" />
		</@security.authorize>
		
		<table id="client-table">
			<tbody>			
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
		<div ng-controller="ClientEditCtrl" class="clients"> 
			<div ng-show="!clients.length" ng-cloak>
				<span><@orcid.msg 'manage_clients.no_clients'/></span>
			</div>
			<!--
			<div ng-hide="!clients.length" ng-cloak>
					<table class="table table-bordered" ng-repeat='client in clients'>
						<caption>
							<@orcid.msg 'manage_clients.client_id'/>: {{client.clientId}}
							<div class="pull-right"><a href="#" ng-click="viewDetails($index)" class="icon-zoom-in blue"></a></div>
							<div class="pull-right"><a href="#" ng-click="editClient($index)" class="icon-pencil  blue"></a></div>
						</caption>
						<tr>
							<td><@orcid.msg 'manage_clients.display_name'/></td>
							<td>{{client.displayName}}</td>							
						</tr>
						<tr>
							<td><@orcid.msg 'manage_clients.website'/></td>
							<td><a href="{{client.website}}" target="_blank">{{client.website}}</a></td>	
						</tr>
						<tr>
							<td><@orcid.msg 'manage_clients.description'/></td>
							<td>{{client.shortDescription}}</td>
						</tr>
						<tr ng-repeat='rUri in client.redirectUris.redirectUri'>
							<td><@orcid.msg 'manage_clients.redirect_uri'/></td>
							<td><a href="{{rUri.value}}" target="_blank">{{rUri.value}}</td>   	
						</tr>	
					</table>
			-->						
			
			<div ng-hide="!clients.length" ng-cloak>				
					<div class="bottom-margin-small" ng-repeat='client in clients'>
						<div class="pull-right"><a href="#" ng-click="viewDetails($index)" class="icon-zoom-in blue"></a></div>
						<div class="pull-right"><a href="#" ng-click="editClient($index)" class="icon-pencil  blue"></a></div>
						<div>							
							<h4><@orcid.msg 'manage_clients.client_id'/>: {{client.clientId}}</h4>
							<ul>
								<li><span><@orcid.msg 'manage_clients.display_name'/></span>: {{client.displayName}}</li>
								<li><span><@orcid.msg 'manage_clients.website'/>:</span> <a href="{{client.website}}" target="_blank">{{client.website}}</a></li>
								<li><span><@orcid.msg 'manage_clients.description'/>:</span> {{client.shortDescription}}</li>
								<li>
									<div ng-repeat='rUri in client.redirectUris.redirectUri'>			                	
					                	<span><@orcid.msg 'manage_clients.redirect_uri'/></span>: <a href="{{rUri.value}}" target="_blank">{{rUri.value}}</a>
									</div>
								</li>
							</ul>
		                </div>
					</div>
			</div>
			
			<@security.authorize ifAnyGranted="ROLE_PREMIUM_INSTITUTION, ROLE_PREMIUM">
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
