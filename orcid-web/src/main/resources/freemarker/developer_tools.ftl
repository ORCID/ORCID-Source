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

<@protected classes=['developer-tools'] nav="developer-tools">
<div class="row">
	<div class="col-md-3 lhs override">
		<ul class="settings-nav">
			<li><a href="#account-settings">${springMacroRequestContext.getMessage("manage.accountsettings")}</a></li>
			<li><a href="#manage-permissions">${springMacroRequestContext.getMessage("manage.managepermission")}</a></li>
			<#if (profile.groupType)?? && ((profile.groupType) = "BASIC" ||
			(profile.groupType) = "PREMIUM" || (profile.groupType) =
			"BASIC_INSTITUTION" || (profile.groupType) = "PREMIUM_INSTITUTION")>
			<li><a href="<@spring.url "/manage-clients" />">${springMacroRequestContext.getMessage("workspace.ManageClientCredentials")}</a></li>
			</#if>
			<li></li>
		</ul>
	</div>
	<div class="col-md-9 developer-tools">
		<!-- Member API Applications -->
		<#if (profile.groupType)?? && ((profile.groupType) = "BASIC" || (profile.groupType) = "PREMIUM" || (profile.groupType) = "BASIC_INSTITUTION" || (profile.groupType) = "PREMIUM_INSTITUTION")>		
			<div class="row box">
				<div class="col-md-10">
					<h2>MEMBER API APPLICATIONS</h2>
				</div>
				<div class="col-md-2">
					<a href=""><span class="label btn-primary cboxElement">Register New</span></a>
				</div>	
			</div>		
			<div class="row">
				<div class="col-md-12">
					<p>These are the applications you have registered to use the <u>ORCID Member API</u>:</p>		
					<table class="table sub-table">
						<tbody>
							<tr>
								<td colspan="12" class="table-header-dt">
									GROUP ID: 0000-0000-0000-0000 (Premium Creator)
								</td>						
							</tr>	
							<tr>
								<td colspan="8">
									Laura's App (http://about.orcid.org)
								</td>												
								<td colspan="4" class="pull-right">
									<span class="label label-info-green">42 users</span>
								</td>									
							</tr>
							<tr>
								<td colspan="8">
									Fran's App (http://fran.orcid.org)
								</td>												
								<td colspan="4" class="pull-right">
									<span class="label label-info-green">42 users</span>
								</td>									
							</tr>
														
						</tbody>
					</table>	
					
					<table class="table">
						<tbody>
							<tr>
								<td colspan="12" class="table-header-dt">
									GROUP ID: 0000-0000-0000-0000 (Premium Creator)
								</td>						
							</tr>	
							<tr>
								<td colspan="8">
									Laura's App (http://about.orcid.org)
								</td>												
								<td colspan="4" class="pull-right">
									<span class="label label-info-green">42 users</span>
								</td>									
							</tr>
							<tr>
								<td colspan="8">
									Fran's App (http://fran.orcid.org)
								</td>												
								<td colspan="4" class="pull-right">
									<span class="label label-info-green">42 users</span>
								</td>									
							</tr>
														
						</tbody>
					</table>
				</div>			
			</div>	
						
						
						
						
						
						
						
						
						
						
						
						
						
						
						
						
						
						
						
						
						
			<#else>					
				<!-- Developer public API Applications -->
				<div ng-controller="SSOPreferencesCtrl" class="sso-api">
					<div class="row box">
						<div class="col-md-10">
							<h2><@orcid.msg 'manage.developer_tools.user.title' /></h2>
						</div>
						<div class="col-md-2" ng-hide="userCredentials.clientSecret.value">
							<a ng-click="createCredentialsModal()"><span class="label btn-primary cboxElement"><@orcid.msg 'manage.developer_tools.button.register_now' /></span></a>
						</div>	
					</div>
					<div class="row">
						<div class="col-md-12" ng-hide="userCredentials.clientSecret.value">
							<p><@orcid.msg 'manage.developer_tools.user.register_to.info.1'/>&nbsp;<a href='manage.developer_tools.user.register_to.info.link_url'><@orcid.msg 'manage.developer_tools.user.register_to.info.link_text'/></a><@orcid.msg 'manage.developer_tools.user.register_to.info.2'/>&nbsp;<@orcid.msg 'manage.developer_tools.user.register_to.info.3'/></p>
							<ul>
								<li><a href="<@orcid.msg 'manage.developer_tools.user.register_to.link.1.url'/>"><span class="glyphicon glyphicon-link"></span><@orcid.msg 'manage.developer_tools.user.register_to.link.1.text'/></a></li>
								<li><a href="<@orcid.msg 'manage.developer_tools.user.register_to.link.2.url'/>"><span class="glyphicon glyphicon-link"></span><@orcid.msg 'manage.developer_tools.user.register_to.link.2.text'/></a></li>
								<li><a href="<@orcid.msg 'manage.developer_tools.user.register_to.link.3.url'/>"><span class="glyphicon glyphicon-link"></span><@orcid.msg 'manage.developer_tools.user.register_to.link.2.text'/></a></li>
							</ul>
						</div>
						<div class="col-md-12" ng-show="userCredentials.clientSecret.value">
							<ul class="sso-options">
								<li><a href ng-click="showSSOCredentials()"><span class="glyphicon glyphicon-eye-open"></span><@orcid.msg 'manage.manage_sso_credentials.view_credentials_link' /></a></li>	
								<li><a href ng-click="showEditModal()"><span class="glyphicon glyphicon-pencil"></span><@orcid.msg 'manage.manage_sso_credentials.edit_credentials_link' /></a></li>
								<li><a href ng-click="showRevokeModal()"><span class="glyphicon glyphicon-remove"></span><@orcid.msg 'manage.manage_sso_credentials.revoke_credentials_link' /></a></li>
							</ul>
							<p><@orcid.msg 'manage.developer_tools.user.registered.info.1' />&nbsp;<a href='manage.developer_tools.user.register_to.info.link_url'><@orcid.msg 'manage.developer_tools.user.register_to.info.link_text'/></a>&nbsp;<@orcid.msg 'manage.developer_tools.user.registered.info.2' /></p>
							<ul class="sso-links">
								<li><a href="<@orcid.msg 'manage.developer_tools.user.register_to.link.1.url'/>"><span class="glyphicon glyphicon-link"></span><@orcid.msg 'manage.developer_tools.user.register_to.link.1.text'/></a></li>
								<li><a href="<@orcid.msg 'manage.developer_tools.user.register_to.link.2.url'/>"><span class="glyphicon glyphicon-link"></span><@orcid.msg 'manage.developer_tools.user.register_to.link.2.text'/></a></li>
								<li><a href="<@orcid.msg 'manage.developer_tools.user.register_to.link.3.url'/>"><span class="glyphicon glyphicon-link"></span><@orcid.msg 'manage.developer_tools.user.register_to.link.2.text'/></a></li>
							</ul>
						</div>
					</div>
				</div>				
			</#if>	
				
				
				
				
				
				
				
				
				
				
				
				
				
				
				
				
				
				
				
				
				
				
				
				
				
				
								
	</div>
</div>











<script type="text/ng-template" id="generate-sso-credentials-modal">
	<div style="padding: 20px;">
		<h3><@orcid.msg 'manage.developer_tools.create.title'/></h3>
		<span><@orcid.msg 'manage.developer_tools.create.instructions'/></span>
		<label class="control-label" style="margin-right:10px; text-align:left; width:90px"><@orcid.msg 'manage.developer_tools.redirect_uri'/>:</label>
		
		
		<div class="sso-redirect_uris">
	    	<div class="control-group" ng-repeat="rUri in userCredentials.redirectUris">										
				<input type="text" placeholder="<@orcid.msg 'manage.developer_tools.redirect_uri.placeholder'/>" class="input-xlarge" ng-model="rUri.value.value"><a href ng-click="deleteRedirectUri($index)" class="glyphicon glyphicon-trash grey"></a><br />					
				<span class="orcid-error" ng-show="rUri.errors.length > 0">
					<div ng-repeat='error in rUri.errors' ng-bind-html="error"></div>
				</span>						
			</div>
		</div>
		
		<div class="sso-add-more-redirect-uris" ng-show="!ssoCredentials.redirectUris.length">			
			<a href ng-click="addRedirectURI()"><span class="glyphicon glyphicon-plus blue"></span><@orcid.msg 'manage.developer_tools.create.add_redirect_uri'/></a>
		</div>
		
		
		<button class="btn btn-danger" ng-click="submit()"><@orcid.msg 'manage.developer_tools.create.generate'/></button>
		<a href="" ng-click="closeModal()"><@orcid.msg 'manage.developer_tools.create.cancel'/></a>
	</div>
</script>















<script type="text/ng-template" id="show-sso-credentials-modal">
	<div style="padding: 20px;">
		<h3><@orcid.msg 'manage.developer_tools.view.title'/></h3>
		<span><@orcid.msg 'manage.developer_tools.view.instructions'/></span>
		<br />
		<br />
		<span><strong><@orcid.msg 'manage.developer_tools.view.secret'/></strong></span>
		<span>{{userCredentials.clientSecret.value}}</span><br />
		<span><strong><@orcid.msg 'manage.developer_tools.view.redirect_uri'/>:</strong></span><br />
		<div class="control-group" ng-repeat='rUri in userCredentials.redirectUris'>									
			<div style="padding-left:10px;">
				{{rUri.value.value}}<br />									
			</div>
		</div>
		<a href="" ng-click="closeModal()"><@orcid.msg 'manage.developer_tools.close'/></a>
	</div>
</script>	

<script type="text/ng-template" id="revoke-sso-credentials-modal">
	<div style="padding: 20px;">
		<h3><@orcid.msg 'manage.developer_tools.revoke.title'/></h3>
		<span><@orcid.msg 'manage.developer_tools.revoke.instructions'/></span>
		<div style="padding-top: 5px;">
			<button class="btn btn-danger" ng-click="revoke()"><@orcid.msg 'manage.developer_tools.revoke.submit'/></button>
			<a href="" ng-click="closeModal()"><@orcid.msg 'manage.developer_tools.create.cancel'/></a>
		</div>
	</div>
</script>	

<script type="text/ng-template" id = "edit-sso-credentials-modal">
	<div style="padding: 20px;">
		<h3><@orcid.msg 'manage.developer_tools.edit.title'/></h3>
		<label class="control-label" style="margin-right:10px; text-align:left; width:90px"><@orcid.msg 'manage.developer_tools.redirect_uri'/>:</label>
		<div class="control-group" ng-repeat='rUri in userCredentials.redirectUris'>									
			<div class="relative">
				<input type="text" placeholder="<@orcid.msg 'manage.developer_tools.redirect_uri.placeholder'/>" class="input-xlarge" ng-model="rUri.value.value">
				<a href ng-click="deleteRedirectUri($index)" class="glyphicon glyphicon-trash blue"></a>
				<span class="orcid-error" ng-show="rUri.errors.length > 0">
					<div ng-repeat='error in rUri.errors' ng-bind-html-unsafe="error"></div>
				</span>						
			</div>
		</div>
		<div ng-show="!ssoCredentials.redirectUris.length">			
			<a href ng-click="addRedirectURI()" class="icon-plus-sign blue"><@orcid.msg 'manage.developer_tools.create.add_redirect_uri'/></a>
		</div>
		<button class="btn btn-danger" ng-click="editRedirectUris()"><@orcid.msg 'manage.developer_tools.update'/></button>
		<a href="" ng-click="closeModal()"><@orcid.msg 'manage.developer_tools.create.cancel'/></a>
	</div>
</script>

</@protected>
