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
			<table id="client-table">
				<tbody>
				<tr>
					<td>Display name:</td>
					<td><input type="text" placeholder="Display name" class="input-xlarge" ng-model="client.displayName"></td>
					<td>&nbsp;</td>
				</tr>
			 	<tr>
			 		<td>Website:</td>
			 		<td><input type="text" placeholder="Website" class="input-xlarge" ng-model="client.website"></td>
			 		<td>&nbsp;</td>
			 	</tr>
			 	<tr>
			 		<td>Description:</td>
			 		<td><input type="text" placeholder="Description" class="input-xlarge" ng-model="client.shortDescription"></td>
			 		<td>&nbsp;</td>
			 	</tr>
			 	<tr ng-repeat='rUri in client.redirectUri'>
			 		<td>Redirect URL's:</td>
			 		<td><input type="text" placeholder="Redirect Uri" class="input-xlarge" ng-model="rUri.value"></td>
			 		<td><span id="add-uri" ng-click="addRowToClientTable()" class="btn btn-primary">${springMacroRequestContext.getMessage("manage.spanadd")}</span></td>
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