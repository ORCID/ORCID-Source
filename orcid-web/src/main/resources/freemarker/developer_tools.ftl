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
	<div class="col-md-3 lhs col-sm-12 col-xs-12 padding-fix">
		<#include "includes/id_banner.ftl"/>
	</div>
	<div class="col-md-9 col-sm-12 col-xs-12 developer-tools">
		<!-- Developer public API Applications -->
		<div ng-controller="SSOPreferencesCtrl" class="sso-api">
			<!-- Top content, instructions -->
			<div class="row">				
				<div class="col-md-10 col-sm-10 col-xs-8">
					<div class="inner-row">
						<h2><@orcid.msg 'manage.developer_tools.user.title' /></h2>
					</div>					
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
			<div class="details" ng-show="userCredentials.clientSecret.value && !editing" ng-cloak>
			
				<!-- Name and Edit/Delete options -->
				<div class="row">					
					<div class="col-md-10 col-sm-10 col-xs-9">
						<div class="inner-row">
							<h4>{{userCredentials.clientName.value}}</h4>
						</div>							
					</div>
					<div class="col-md-2 col-sm-2 col-xs-3">				
						<ul class="sso-options pull-right">							
							<li><a href ng-click="showEditLayout()" class="edit" title="<@orcid.msg 'manage.developer_tools.tooltip.edit' />"><span class="glyphicon glyphicon-pencil"></span></a></li>							
						</ul>					
					</div>				
				</div>			
				<div class="row">
					<!-- Website -->
					<div class="col-md-12 col-sm-12 col-xs-12 dt-website">
						<p><a href="{{userCredentials.clientWebsite.value}}">{{userCredentials.clientWebsite.value}}</a></p>														
					</div>							
				</div>
				<div class="row">
					<!-- Description -->
					<div class="col-md-12 col-sm-12 col-xs-12 dt-description">
						<p>{{userCredentials.clientDescription.value}}</p>														
					</div>							
				</div>
				<div class="slidebox">
					<div class="row">
						<!-- SLIDE BOX  -->
						<!-- Redirect URIS -->
						<div class="col-md-12 col-sm-12 col-xs-12">
							<h4><@orcid.msg 'manage.developer_tools.redirect_uri'/>:</h4>																		
							<select ng-model="selectedRedirectUri" ng-options="rUri.value.value for rUri in userCredentials.redirectUris | orderBy:'value.value'" ng-change="updateSelectedRedirectUri()">
							</select>														
						</div>
					</div>
					<div class="row">						
						<div class="col-md-12 col-sm-12 col-xs-12">
							<div class="grey-box">
								<div class="table-responsive">
								  <!-- Client ID - Client Secret -->
								  <table class="table">								  										   
									    <!-- Client details-->
									    <tr>
									    	<td><strong><@orcid.msg 'manage.developer_tools.view.orcid'/></strong></td>
									    	<td>{{userCredentials.clientOrcid.value}}</td>									    	
									    </tr>
									    <tr class="table-row-border-bottom">
									    	<td><strong><@orcid.msg 'manage.developer_tools.view.secret'/></strong></td>
									    	<td>{{userCredentials.clientSecret.value}}</td>
									    </tr>
									    <!-- Authorize URl and Token URL -->
									    <tr>
									    	<td><strong><@orcid.msg 'manage.developer_tools.view.example.authorize'/></strong></td>
									    	<td>
									    		<strong><@orcid.msg 'manage.developer_tools.view.available_scopes.authenticate'/></strong>&nbsp;&nbsp;&nbsp;&nbsp;<@orcid.msg 'manage.developer_tools.view.available_scopes.authenticate.description'/><br/>
									    		<textarea class="input-xlarge selectable authorizeURL" ng-model="authorizeURL" readonly="readonly"></textarea>									    		
									    	</td>
									    </tr>
									    <tr class="table-row-border-bottom">
									    	<td><strong><@orcid.msg 'manage.developer_tools.view.example.token'/></strong></td>
									    	<td>{{tokenURL}}</td>
									    </tr>									    
									    <!-- Testing tools -->
									    <tr ng-hide="playgroundExample == ''">
									    		<td><strong><@orcid.msg 'manage.developer_tools.view.example.title'/></strong></td>
									    		<td><a href="{{playgroundExample}}" target="_blank"><@orcid.msg 'manage.developer_tools.view.example.google'/></a></td>
									    </tr>
								   </table>
								</div>									
							</div>
						</div>
					</div>
				</div>				
			</div>
			
			<!-- Edit form -->
			<div class="edit-details" ng-show="userCredentials.clientSecret.value && editing" ng-cloak>			
				<!-- Name and Edit/Delete options -->
				<div class="row">					
					<div class="col-md-10 col-sm-10 col-xs-9">
						<div class="inner-row">
							<span><strong><@orcid.msg 'manage.developer_tools.generate.name'/></strong></span>
							<input type="text" placeholder="<@orcid.msg 'manage.developer_tools.generate.name.placeholder'/>" class="input-xlarge" ng-model="userCredentials.clientName.value">
							<span class="orcid-error" ng-show="userCredentials.clientName.errors.length > 0">
								<div ng-repeat='error in userCredentials.clientName.errors' ng-bind-html="error"></div>
							</span>					
						</div>		
					</div>
					<div class="col-md-2 col-sm-2 col-xs-3">				
						<ul class="sso-options pull-right">							
							<li><a href ng-click="showViewLayout()" class="back" title="<@orcid.msg 'manage.developer_tools.tooltip.back' />"><span class="glyphicon glyphicon-arrow-left"></span></a></li>
							<li><a href ng-click="editClientCredentials()" class="save" title="<@orcid.msg 'manage.developer_tools.tooltip.save' />"><span class="glyphicon glyphicon-floppy-disk"></span></a></li>							
						</ul>					
					</div>				
				</div>
							
				<div class="row">
					<!-- Website -->
					<div class="col-md-10 col-sm-10 col-xs-12 dt-website">
						<div class="inner-row">
							<span><strong><@orcid.msg 'manage.developer_tools.generate.website'/></strong></span>
							<input type="text" placeholder="<@orcid.msg 'manage.developer_tools.generate.website.placeholder'/>" class="input-xlarge" ng-model="userCredentials.clientWebsite.value">
							<span class="orcid-error" ng-show="userCredentials.clientWebsite.errors.length > 0">
								<div ng-repeat='error in userCredentials.clientWebsite.errors' ng-bind-html="error"></div>
							</span>
						</div>															
					</div>			
					<div class="col-md-2 col-sm-2"></div>									
				</div>
														
				<div class="row">
					<!-- Description -->
					<div class="col-md-10 col-sm-10 col-xs-12 dt-description">
						<div class="inner-row">
							<span><strong><@orcid.msg 'manage.developer_tools.generate.description'/></strong></span>
							<textarea placeholder="<@orcid.msg 'manage.developer_tools.generate.description.placeholder'/>" ng-model="userCredentials.clientDescription.value"></textarea>						
							<span class="orcid-error" ng-show="userCredentials.clientDescription.errors.length > 0">
								<div ng-repeat='error in userCredentials.clientDescription.errors' ng-bind-html="error"></div>
							</span>
						</div>															
					</div>			
					<div class="col-md-2 col-sm-2"></div>									
				</div>				
				<div class="row">
					<!-- SLIDE BOX  -->
					<!-- Redirect URIS -->
					<div class="col-md-10 col-sm-10 col-xs-12">
						<div class="inner-row redirectUris">
							<h4><@orcid.msg 'manage.developer_tools.redirect_uri'/></h4>						
							<div ng-repeat="rUri in userCredentials.redirectUris">										
								<input type="text" placeholder="<@orcid.msg 'manage.developer_tools.redirect_uri.placeholder'/>" ng-model="rUri.value.value">					
								<a href ng-click="deleteRedirectUri($index)" class="glyphicon glyphicon-trash blue"></a>
								<span class="orcid-error" ng-show="rUri.errors.length > 0">
									<div ng-repeat='error in rUri.errors' ng-bind-html="error"></div>
								</span>	
							</div>
							<span class="orcid-error" ng-show="userCredentials.redirectUris.length == 0">
								<div><@orcid.msg 'manage.developer_tools.at_least_one' /></div>
							</span>
						</div>
					</div>	
					<div class="col-md-2 col-sm-2"></div>
					<!-- Client ID - Client Secret -->
				</div>
				<div class="row">
					<div class="col-md-12 col-sm-12 col-xs-12 add-options">
						<a href="" class="icon-href-bg" ng-click="addRedirectURI()"><span class="glyphicon glyphicon-plus"></span><@orcid.msg 'manage.developer_tools.edit.add_redirect_uri' /></a>
					</div>
				</div>
				<div class="slidebox">
					<div class="row">
						<div class="col-md-12 col-sm-12 col-xs-12">
							<div class="add-options">								
								<div ng-show="!hideGoogleUri">
									<h4><@orcid.msg 'manage.developer_tools.test_redirect_uris.title' /></h4>
									<ul class="pullleft-list">
										<li id="google-ruir"><a href="" class="icon-href" ng-click="addTestRedirectUri('google')"><span class="glyphicon glyphicon-plus"></span><@orcid.msg 'manage.developer_tools.edit.google'/></a></li>										
									</ul>								
								</div>
							</div>
						</div>					
					</div>					
					<div class="row">
						<div class="col-md-12 col-sm-12 col-xs-12">						
							<div class="grey-box">
								<div class="table-responsive">
								  <table class="table">
								    <tr>
								    	<td><strong><@orcid.msg 'manage.developer_tools.view.orcid'/></strong></td>
								    	<td>{{userCredentials.clientOrcid.value}}</td>
								    </tr>
								    <tr>
								    	<td><strong><@orcid.msg 'manage.developer_tools.view.secret'/></strong></td>
								    	<td>{{userCredentials.clientSecret.value}}</td>								    	
								    </tr>								    
								  </table>
								</div>									
							</div>
						</div>	 
					</div>					
				</div>				
			</div>		
						
			<div class="row slide" ng-show="userCredentials.clientSecret.value" ng-cloak>
				<div class="col-md-12 col-sm-12 col-xs-12">
					<div class="tab-container">
						<a href="#" class="tab collapsed" data-tab="collapsed"><span class="glyphicon glyphicon-chevron-down"></span><@orcid.msg 'manage.developer_tools.show_details' /></a>
						<a href="#" class="tab expanded"><span class="glyphicon glyphicon-chevron-up"></span><@orcid.msg 'manage.developer_tools.hide_details' /></a>
					</div>
				</div>			
			</div>
			
			<div class="row">
				<div class="col-md-12 col-sm-12 col-xs-12">
					<p><strong><@orcid.msg 'manage.developer_tools.user.registered.info.1' />&nbsp;<a href="<@orcid.msg 'manage.developer_tools.user.register_to.info.link_url' />"><@orcid.msg 'manage.developer_tools.user.register_to.info.link_text'/></a>&nbsp;<@orcid.msg 'manage.developer_tools.user.registered.info.2' /></strong></p>
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
		
		<div class="generate-public-client">
			<div class="row">
				<span class="col-xs-12 col-md-12"><strong><@orcid.msg 'manage.developer_tools.generate.name'/></strong></span>
				<span class="col-xs-12 col-md-12"><input type="text" placeholder="<@orcid.msg 'manage.developer_tools.generate.name.placeholder'/>" class="input-xlarge" ng-model="userCredentials.clientName.value"></span><br />
				<span class="col-xs-12 col-md-12 orcid-error" ng-show="userCredentials.clientName.errors.length > 0">
					<div ng-repeat='error in userCredentials.clientName.errors' ng-bind-html="error"></div>
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
				<span class="col-xs-12 col-md-12"><strong><@orcid.msg 'manage.developer_tools.generate.description'/></strong></span>
				<span class="col-xs-12 col-md-12"><textarea placeholder="<@orcid.msg 'manage.developer_tools.generate.description.placeholder'/>" class="input-xlarge client-description" ng-model="userCredentials.clientDescription.value" /></span><br />
				<span class="col-xs-12 col-md-12 orcid-error" ng-show="userCredentials.clientDescription.errors.length > 0">
					<div ng-repeat='error in userCredentials.clientDescription.errors' ng-bind-html="error"></div>
				</span>	
			</div>

			<div class="row">
				<span class="col-xs-12 col-md-12"><strong><@orcid.msg 'manage.developer_tools.redirect_uri'/>:</strong></span>
	    		<div class="col-xs-12 col-md-12" ng-repeat="rUri in userCredentials.redirectUris">										
					<input type="text" placeholder="<@orcid.msg 'manage.developer_tools.redirect_uri.placeholder'/>" class="input-xlarge" ng-model="rUri.value.value">
					<a href ng-click="deleteRedirectUri($index)" class="glyphicon glyphicon-trash grey"></a><br />					
					<span class="orcid-error" ng-show="rUri.errors.length > 0">
						<div ng-repeat='error in rUri.errors' ng-bind-html="error"></div>
					</span>							
				</div>
				<span class="col-xs-12 col-md-12 orcid-error" ng-show="userCredentials.redirectUris.length == 0">
					<div><@orcid.msg 'manage.developer_tools.at_least_one' /></div>
				</span>		
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

</@public>
