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

<@public nav="developer-tools">
<div class="row">
	<div class="col-md-3 lhs override">
		
	</div>
	<div class="col-md-9 developer-tools">
		<!-- Developer public API Applications -->
		<div ng-controller="SSOPreferencesCtrl" class="sso-api">
			<!-- Top content, instructions -->
			<div class="row box">
				<div class="col-md-10 col-sm-10 col-xs-8">
					<h2><@orcid.msg 'manage.developer_tools.user.title' /></h2>					
				</div>				
				<div class="col-md-2 col-sm-2 col-xs-4" ng-hide="userCredentials.clientSecret.value">
					<a ng-click="createCredentialsModal()" class="pull-right"><span class="label btn-primary cboxElement"><@orcid.msg 'manage.developer_tools.button.register_now' /></span></a>
				</div>	
			</div>
			<div class="row">
				<div class="col-md-12 col-sm-12 col-xs-12">				
					<p class="developer-tools-instructions"><@orcid.msg 'manage.developer_tools.view.instructions'/></p>
				</div>
			</div>
			
			
			<!-- App details -->
			<div class="row" ng-show="userCredentials.clientSecret.value">
				<!-- 
				<div class="col-md-12  col-xs-12" ng-hide="userCredentials.clientSecret.value">
					<p><@orcid.msg 'manage.developer_tools.user.register_to.info.1'/>&nbsp;<a href="<@orcid.msg 'manage.developer_tools.user.register_to.info.link_url' />"><@orcid.msg 'manage.developer_tools.user.register_to.info.link_text'/></a><@orcid.msg 'manage.developer_tools.user.register_to.info.2'/>&nbsp;<@orcid.msg 'manage.developer_tools.user.register_to.info.3'/></p>
					<ul>
						<li><a href="<@orcid.msg 'manage.developer_tools.user.register_to.link.1.url'/>"><span class="glyphicon glyphicon-link"></span><@orcid.msg 'manage.developer_tools.user.register_to.link.1.text'/></a></li>
						<li><a href="<@orcid.msg 'manage.developer_tools.user.register_to.link.2.url'/>"><span class="glyphicon glyphicon-link"></span><@orcid.msg 'manage.developer_tools.user.register_to.link.2.text'/></a></li>
						<li><a href="<@orcid.msg 'manage.developer_tools.user.register_to.link.3.url'/>"><span class="glyphicon glyphicon-link"></span><@orcid.msg 'manage.developer_tools.user.register_to.link.3.text'/></a></li>
					</ul>
				</div>
				 -->
				<div class="col-md-9 col-sm-9 col-xs-8">
					<h4>{{userCredentials.clientName.value}}</span>				
				</div>
				<div class="col-md-3 col-sm-3 col-xs-4">				
					<ul class="sso-options pull-right">							
						<li><a href ng-click="showEditModal()"><span class="glyphicon glyphicon-pencil"></span></a></li>
						<li><a href ng-click="showRevokeModal()"><span class="glyphicon glyphicon-trash"></span></a></li>
					</ul>					
				</div>				
			</div>			
			<div class="row">
				<!-- Description -->
				<div class="col-md-12 col-sm-12 col-xs-12">
					<p>{{userCredentials.clientDescription.value}}</p>														
				</div>							
			</div>
			<div class="row slidebox">
				<!-- SLIDE BOX  -->
				<!-- Redirect URIS -->
				<div class="col-md-12 col-sm-12 col-xs-12">
					<h4><@orcid.msg 'manage.developer_tools.redirect_uri'/>:</h4>						
					<ul class="uris" ng-repeat='rUri in userCredentials.redirectUris'>
						<li><a href="{{rUri.value.value}}">{{rUri.value.value}}</a></li>									
					</ul>
				</div>	
				<!-- Client ID - Client Secret -->
				<div class="col-md-12 col-sm-12 col-xs-12">
					<div class="grey-box">
						<div class="table-responsive">
						  <table class="table">
						    <tr>
						    	<td><strong><@orcid.msg 'manage.developer_tools.view.secret'/></strong></td>
						    	<td>{{userCredentials.clientSecret.value}}</td>
						    </tr>
						    <tr>
						    	<td><strong><@orcid.msg 'manage.developer_tools.view.orcid'/></strong></td>
						    	<td>{{userCredentials.clientOrcid.value}}</td>
						    </tr>
						  </table>
						</div>									
					</div>
				</div>	 
			</div>
			<div class="row slide">
				<div class="col-md-12 col-sm-12 col-xs-12">
					<div class="tab-container">
						<a href="#" class="tab collapsed"><span class="glyphicon glyphicon-chevron-down"></span>Show Details</a>
						<a href="#" class="tab expanded"><span class="glyphicon glyphicon-chevron-up"></span>Hide Details</a>
					</div>
				</div>			
			</div>				
			<div class="row">
				<div class="col-md-12 col-sm-12 col-xs-12">
					<p><@orcid.msg 'manage.developer_tools.user.registered.info.1' />&nbsp;<a href="<@orcid.msg 'manage.developer_tools.user.register_to.info.link_url' />"><@orcid.msg 'manage.developer_tools.user.register_to.info.link_text'/></a>&nbsp;<@orcid.msg 'manage.developer_tools.user.registered.info.2' /></p>
					<ul class="sso-links">
						<li><a href="<@orcid.msg 'manage.developer_tools.user.register_to.link.1.url'/>"><span class="glyphicon glyphicon-link"></span><@orcid.msg 'manage.developer_tools.user.register_to.link.1.text'/></a></li>
						<li><a href="<@orcid.msg 'manage.developer_tools.user.register_to.link.2.url'/>"><span class="glyphicon glyphicon-link"></span><@orcid.msg 'manage.developer_tools.user.register_to.link.2.text'/></a></li>
						<li><a href="<@orcid.msg 'manage.developer_tools.user.register_to.link.3.url'/>"><span class="glyphicon glyphicon-link"></span><@orcid.msg 'manage.developer_tools.user.register_to.link.3.text'/></a></li>
					</ul>
				</div>			
			</div>
			
			<div class="row">
				<div class="col-md-12 col-xs-12">
					<span>
						<@orcid.msg 'manage.developer_tools.user.join.link.text.1'/>&nbsp;<a href="<@orcid.msg 'manage.developer_tools.user.join.link.url'/>" target="_blank"><@orcid.msg 'manage.developer_tools.user.join.link.text.2'/></a>						
					</span>
				</div>
			</div>
			
			
		</div>
	</div>				
</div>

<script type="text/ng-template" id="generate-sso-credentials-modal">
	<div style="margin: 20px;" class="sso-api">
		<h3><@orcid.msg 'manage.developer_tools.create.title'/></h3>
		<span><@orcid.msg 'manage.developer_tools.create.instructions'/></span><br />			
		
		<div class="sso-redirect_uris">
			<div class="row">
				<span class="col-xs-12 col-md-12"><strong><@orcid.msg 'manage.developer_tools.generate.name'/></strong></span>
				<span class="col-xs-12 col-md-12"><input type="text" placeholder="<@orcid.msg 'manage.developer_tools.generate.name.placeholder'/>" class="input-xlarge" ng-model="userCredentials.clientName.value"></span><br />
				<span class="col-xs-12 col-md-12 orcid-error" ng-show="userCredentials.clientName.errors.length > 0">
					<div ng-repeat='error in userCredentials.clientName.errors' ng-bind-html="error"></div>
				</span>	
			</div>

			<div class="row">
				<span class="col-xs-12 col-md-12"><strong><@orcid.msg 'manage.developer_tools.generate.description'/></strong></span>
				<span class="col-xs-12 col-md-12"><input type="text" placeholder="<@orcid.msg 'manage.developer_tools.generate.description.placeholder'/>" class="input-xlarge" ng-model="userCredentials.clientDescription.value"></span><br />
				<span class="col-xs-12 col-md-12 orcid-error" ng-show="userCredentials.clientDescription.errors.length > 0">
					<div ng-repeat='error in userCredentials.clientDescription.errors' ng-bind-html="error"></div>
				</span>	
			</div>


			<div class="row">
				<span class="col-xs-12 col-md-12"><strong><@orcid.msg 'manage.developer_tools.generate.website'/></strong></span>
				<span class="col-xs-12 col-md-12"><input type="text" placeholder="<@orcid.msg 'manage.developer_tools.generate.website.placeholder'/>" class="input-xlarge" ng-model="userCredentials.clientWebsite.value"></span><br />
				<span class="col-xs-12 col-md-12 orcid-error" ng-show="userCredentials.clientWebsite.errors.length > 0">
					<div ng-repeat='error in userCredentials.clientWebsite.errors' ng-bind-html="error"></div>
				</span>	
			</div>



			<div class="row">
				<span class="col-xs-12 col-md-12"><strong><@orcid.msg 'manage.developer_tools.redirect_uri'/>:</strong></span>
	    		<div class="col-xs-12 col-md-12" ng-repeat="rUri in userCredentials.redirectUris">										
					<input type="text" placeholder="<@orcid.msg 'manage.developer_tools.redirect_uri.placeholder'/>" class="input-xlarge" ng-model="rUri.value.value">
					<a href ng-click="deleteRedirectUri($index)" class="glyphicon glyphicon-trash grey"></a><br />					
					<span class="col-xs-12 col-md-12 orcid-error" ng-show="rUri.errors.length > 0">
						<div ng-repeat='error in rUri.errors' ng-bind-html="error"></div>
					</span>						
				</div>
			</div>
		
			<div class="row">
				<div class="col-xs-12 col-md-12" ng-show="!ssoCredentials.redirectUris.length">			
					<a href ng-click="addRedirectURI()"><span class="glyphicon glyphicon-plus blue"></span><@orcid.msg 'manage.developer_tools.create.add_redirect_uri'/></a>
				</div>
			</div>
	
			<div class="row">
				<span class="col-xs-12 col-md-12 small-row">
					<button class="btn btn-danger" ng-click="submit()"><@orcid.msg 'manage.developer_tools.create.generate'/></button>
					<a href="" ng-click="closeModal()"><@orcid.msg 'manage.developer_tools.create.cancel'/></a>
				</span>
			</div>
		</div>
	</div>
</script>

<script type="text/ng-template" id = "edit-sso-credentials-modal">
	<div style="padding: 20px;" class="sso-api">
		<h3><@orcid.msg 'manage.developer_tools.edit.title'/></h3>
		<span><@orcid.msg 'manage.developer_tools.create.instructions'/></span><br />

		<div class="sso-redirect_uris">
			<div class="row">
				<span class="col-xs-12 col-md-12"><strong><@orcid.msg 'manage.developer_tools.generate.name'/></strong></span>
				<span class="col-xs-12 col-md-12"><input type="text" placeholder="<@orcid.msg 'manage.developer_tools.generate.name.placeholder'/>" class="input-xlarge" ng-model="userCredentials.clientName.value"></span><br />
				<span class="col-xs-12 col-md-12 orcid-error" ng-show="userCredentials.clientName.errors.length > 0">
					<div ng-repeat='error in userCredentials.clientName.errors' ng-bind-html="error"></div>
				</span>	
			</div>

			<div class="row">
				<span class="col-xs-12 col-md-12"><strong><@orcid.msg 'manage.developer_tools.generate.description'/></strong></span>
				<span class="col-xs-12 col-md-12"><input type="text" placeholder="<@orcid.msg 'manage.developer_tools.generate.description.placeholder'/>" class="input-xlarge" ng-model="userCredentials.clientDescription.value"></span><br />
				<span class="col-xs-12 col-md-12 orcid-error" ng-show="userCredentials.clientDescription.errors.length > 0">
					<div ng-repeat='error in userCredentials.clientDescription.errors' ng-bind-html="error"></div>
				</span>	
			</div>

			<div class="row">
				<span class="col-xs-12 col-md-12"><strong><@orcid.msg 'manage.developer_tools.generate.website'/></strong></span>
				<span class="col-xs-12 col-md-12"><input type="text" placeholder="<@orcid.msg 'manage.developer_tools.generate.website.placeholder'/>" class="input-xlarge" ng-model="userCredentials.clientWebsite.value"></span><br />
				<span class="col-xs-12 col-md-12 orcid-error" ng-show="userCredentials.clientWebsite.errors.length > 0">
					<div ng-repeat='error in userCredentials.clientWebsite.errors' ng-bind-html="error"></div>
				</span>	
			</div>

			<div class="row">
				<span class="col-xs-12 col-md-12"><strong><@orcid.msg 'manage.developer_tools.redirect_uri'/>:</strong></span>
	    		<div class="col-xs-12 col-md-12" ng-repeat="rUri in userCredentials.redirectUris">										
					<input type="text" placeholder="<@orcid.msg 'manage.developer_tools.redirect_uri.placeholder'/>" class="input-xlarge" ng-model="rUri.value.value">					
					<a href ng-click="deleteRedirectUri($index)" class="glyphicon glyphicon-trash blue"></a>
					<span class="col-xs-12 col-md-12 orcid-error" ng-show="rUri.errors.length > 0">
						<div ng-repeat='error in rUri.errors' ng-bind-html-unsafe="error"></div>
					</span>							
				</div>
			</div>

			<div class="row">
				<div class="col-xs-12 col-md-12" ng-show="!ssoCredentials.redirectUris.length">					
					<a href ng-click="addRedirectURI()"><span class="glyphicon glyphicon-plus blue"></span><@orcid.msg 'manage.developer_tools.create.add_redirect_uri'/></a>
				</div>
			</div>

			<div class="row">
				<span class="col-xs-12 col-md-12 small-row">
					<button class="btn btn-danger" ng-click="editClientCredentials()"><@orcid.msg 'manage.developer_tools.update'/></button>
					<a href="" ng-click="closeModal()"><@orcid.msg 'manage.developer_tools.create.cancel'/></a>
				</span>
			</div>
		</div>
	</div>
</script>

<script type="text/ng-template" id="show-sso-credentials-modal">
	<div style="padding: 20px;" class="sso-api">
		<h3><@orcid.msg 'manage.developer_tools.view.title'/></h3>
		<span><@orcid.msg 'manage.developer_tools.view.instructions'/></span>
		<br />
		<br />
		<div class="row">
			<span class="col-xs-12 col-md-3"><strong><@orcid.msg 'manage.developer_tools.view.orcid'/></strong></span>
			<span class="col-xs-12 col-md-9">{{userCredentials.clientOrcid.value}}</span><br />
		</div>
		
		<div class="row">
			<span class="col-xs-12 col-md-3"><strong><@orcid.msg 'manage.developer_tools.view.name'/></strong></span>
			<span class="col-xs-12 col-md-9">{{userCredentials.clientName.value}}</span><br />
		</div>

		<div class="row">
			<span class="col-xs-12 col-md-3"><strong><@orcid.msg 'manage.developer_tools.view.description'/></strong></span>
			<span class="col-xs-12 col-md-9">{{userCredentials.clientDescription.value}}</span><br />
		</div>

		<div class="row">
			<span class="col-xs-12 col-md-3"><strong><@orcid.msg 'manage.developer_tools.view.website'/></strong></span>
			<span class="col-xs-12 col-md-9">{{userCredentials.clientWebsite.value}}</span><br />
		</div>

		<div class="row">
			<span class="col-xs-12 col-md-3"><strong><@orcid.msg 'manage.developer_tools.view.secret'/></strong></span>
			<span class="col-xs-12 col-md-9">{{userCredentials.clientSecret.value}}</span><br />
		</div>
		<br />		
		<div class="row">
			<span class="col-xs-12 col-md-12"><strong><@orcid.msg 'manage.developer_tools.view.redirect_uri'/>:</strong></span><br />						
			<div class="col-xs-12 col-md-12" ng-repeat='rUri in userCredentials.redirectUris'>
				<a href="{{rUri.value.value}}">{{rUri.value.value}}</a>									
			</div>
		</div>
		<br />
		<a href="" ng-click="closeModal()"><@orcid.msg 'manage.developer_tools.close'/></a>
	</div>
</script>	

<script type="text/ng-template" id="revoke-sso-credentials-modal">
	<div style="padding: 20px;" class="sso-api">
		<h3><@orcid.msg 'manage.developer_tools.revoke.title'/></h3>
		<span><@orcid.msg 'manage.developer_tools.revoke.instructions'/></span>
		<div style="padding-top: 5px;">
			<button class="btn btn-danger" ng-click="revoke()"><@orcid.msg 'manage.developer_tools.revoke.submit'/></button>
			<a href="" ng-click="closeModal()"><@orcid.msg 'manage.developer_tools.create.cancel'/></a>
		</div>
	</div>
</script>	

</@public>
