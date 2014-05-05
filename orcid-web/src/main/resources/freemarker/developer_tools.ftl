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
				<div class="col-md-2 col-sm-2 col-xs-4" ng-show="showReg" ng-cloak>
					<a ng-click="createCredentialsLayout()" class="pull-right"><span class="label btn-primary"><@orcid.msg 'manage.developer_tools.button.register_now' /></span></a>
				</div>	
			</div>
			<div class="row">
				<div class="col-md-12 col-sm-12 col-xs-12">				
					<p class="developer-tools-instructions"><@orcid.msg 'manage.developer_tools.view.instructions'/></p>
				</div>
			</div>
						
			<!-- App details -->
			<div class="details" ng-show="userCredentials.clientSecrets && userCredentials.clientSecrets[0].value && !editing" ng-cloak>
			
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
									    	<td><select ng-model="selectedClientSecret" ng-options="secret.value for secret in userCredentials.clientSecrets | orderBy:'value'" ng-change="updateSelectedClientSecret()"></select></td>									    	
									    </tr>
									    <!-- Authorize URl and Token URL -->
									    <tr ng-hide="playgroundExample != ''">
									    	<td><strong><@orcid.msg 'manage.developer_tools.view.example.authorize'/></strong></td>
									    	<td>
									    		<strong><@orcid.msg 'manage.developer_tools.view.available_scopes.authenticate'/></strong>&nbsp;&nbsp;&nbsp;&nbsp;<@orcid.msg 'manage.developer_tools.view.available_scopes.authenticate.description'/><br/>
									    		<textarea class="input-xlarge selectable authorizeURL" ng-model="authorizeURL" readonly="readonly"></textarea>									    		
									    	</td>
									    </tr>
									    <tr  ng-hide="playgroundExample != ''" class="table-row-border-bottom">
									    	<td><strong><@orcid.msg 'manage.developer_tools.view.example.token'/></strong></td>
									    	<td>{{tokenURL}}<br />
									    	<@orcid.msg 'manage.developer_tools.view.example.curl' /><a href="<@orcid.msg 'manage.developer_tools.view.example.curl.url' />" target="curlWiki"><@orcid.msg 'manage.developer_tools.view.example.curl.text' /></a> 
									    	<textarea class="input-xlarge selectable authorizeURL" ng-model="sampleAuthCurl" readonly="readonly"></textarea>
									    	</td>
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
			
			<!-- Create form -->
			<div class="create-client" ng-show="creating" ng-cloak>	
				<!-- Name -->
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
					<div class="col-md-2 col-sm-3"></div>											
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
					<div class="col-md-9 col-sm-9 col-xs-9 add-options">
						<a href="" class="icon-href-bg" ng-click="addRedirectURI()"><span class="glyphicon glyphicon-plus"></span><@orcid.msg 'manage.developer_tools.edit.add_redirect_uri' /></a>
						<div class="add-options margin-bottom-box">								
							<div ng-show="!hideGoogleUri">
								<h4><@orcid.msg 'manage.developer_tools.test_redirect_uris.title' /></h4>
								<ul class="pullleft-list">
									<li id="google-ruir"><a href="" class="icon-href" ng-click="addTestRedirectUri('google')"><span class="glyphicon glyphicon-plus"></span><@orcid.msg 'manage.developer_tools.edit.google'/></a></li>										
								</ul>								
							</div>
						</div>						
					</div>
					<div class="col-md-3 col-sm-3 col-xs-3">				
						<ul class="sso-options pull-right">							
							<li><a href ng-click="showViewLayout()" class="back" title="<@orcid.msg 'manage.developer_tools.tooltip.back' />"><span class="glyphicon glyphicon-arrow-left"></span></a></li>
							<li><a href ng-click="submit()" class="save" title="<@orcid.msg 'manage.developer_tools.tooltip.save' />"><span class="glyphicon glyphicon-floppy-disk"></span></a></li>							
						</ul>					
					</div>	
				</div>				
			</div>
			
			<!-- Edit form -->
			<div class="edit-details" ng-show="userCredentials.clientSecrets && userCredentials.clientSecrets[0].value && editing" ng-cloak>			
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
					<div class="col-md-2 col-sm-3"></div>											
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
					<div class="col-md-9 col-sm-9 col-xs-9 add-options">
						<a href="" class="icon-href-bg" ng-click="addRedirectURI()"><span class="glyphicon glyphicon-plus"></span><@orcid.msg 'manage.developer_tools.edit.add_redirect_uri' /></a>
					</div>
					<div class="col-md-3 col-sm-3 col-xs-3">				
						<ul class="sso-options pull-right">							
							<li><a href ng-click="showViewLayout()" class="back" title="<@orcid.msg 'manage.developer_tools.tooltip.back' />"><span class="glyphicon glyphicon-arrow-left"></span></a></li>
							<li><a href ng-click="editClientCredentials()" class="save" title="<@orcid.msg 'manage.developer_tools.tooltip.save' />"><span class="glyphicon glyphicon-floppy-disk"></span></a></li>							
						</ul>					
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
								  <table class="table client_secret_table">
								    <tr>
								    	<td><strong><@orcid.msg 'manage.developer_tools.view.orcid'/></strong></td>
								    	<td colspan="3">{{userCredentials.clientOrcid.value}}</td>
								    </tr>
								    <tr ng-repeat="secret in userCredentials.clientSecrets">
								    	<td class="c1"><strong ng-show="$first"><@orcid.msg 'manage.developer_tools.view.secret'/></strong></td>
								    	<td class="c2">{{secret.value}}</td>	
								    	<td class="c3"><a href ng-click="confirmDeleteClientSecret($index)" class="delete" title="<@orcid.msg 'manage.developer_tools.client_secret.tooltip.delete' />" ng-show="userCredentials.clientSecrets.length > 1"><span class="glyphicon glyphicon-trash"></span></a></td>
								    	<td><a href ng-click="addClientSecret()" class="edit" title="<@orcid.msg 'manage.developer_tools.client_secret.tooltip.add' />" ng-show="$last"><span class="glyphicon glyphicon-plus-sign"></span></a></td>							    	   
								    </tr>
								  </table>
								</div>									
							</div>
						</div>	 
					</div>					
				</div>				
			</div>		
						
			<div class="row slide" ng-show="userCredentials.clientSecrets && userCredentials.clientSecrets[0].value" ng-cloak>
				<div class="col-md-12 col-sm-12 col-xs-12">
					<div class="tab-container">
						<a href="#" class="tab collapsed" data-tab="collapsed"><span class="glyphicon glyphicon-chevron-down"></span><@orcid.msg 'manage.developer_tools.show_details' /></a>
						<a href="#" class="tab expanded"><span class="glyphicon glyphicon-chevron-up"></span><@orcid.msg 'manage.developer_tools.hide_details' /></a>
					</div>
				</div>			
			</div>
			
			<div class="row">
				<div class="col-md-12 col-sm-12 col-xs-12">
					<p><strong><@orcid.msg 'manage.developer_tools.related_documentation' />:</p>
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

<script type="text/ng-template" id="delete-secret-modal">
	<div class="lightbox-container">
		<div class="row">
			<div class="col-md-12 col-xs-12 col-sm-12">
				<h3><@orcid.msg 'manage.developer_tools.edit.delete_key.title' /></h3>
				<p>{{clientSecretToDelete.value}}</p>		
    			<div class="btn btn-danger" ng-click="deleteClientSecret(clientSecretToDelete.value)">
    				<@orcid.msg 'freemarker.btnDelete' />
    			</div>
    			<a href="" ng-click="closeModal()"><@orcid.msg 'freemarker.btncancel' /></a>
			</div>
		</div>
    </div>
</script>

</@public>
