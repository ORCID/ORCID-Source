<@public nav="developer-tools">
	<div class="row">
		<div class="col-md-3 lhs col-sm-12 col-xs-12 padding-fix">
			<#include "includes/id_banner.ftl"/>
		</div>
		<div class="col-md-9 col-sm-12 col-xs-12 developer-tools" ng-controller="PublicClientCtrl">		
			<#if developerToolsEnabled == false>
				<h1 id="manage-developer-tools">
					<span><@spring.message "manage.developer_tools.user.title"/></span>					
				</h1>
				<#if hideRegistration>
					<@orcid.msg 'developer_tools.unavailable' />
				<#else>									
					<div class="sso-api">						
						<div class="row">
							<div class="col-md-12 col-sm-12 col-xs-12">
								<p><i><@orcid.msg 'developer_tools.note' /> <a href="./my-orcid"><@orcid.msg 'developer_tools.note.link.text' /></a><@orcid.msg 'developer_tools.note.link.point' /></i></p>																
								<div class="centered bottomBuffer">
									<#if hasVerifiedEmail>
										<button class="btn btn-primary" ng-click="acceptTerms()" ><@orcid.msg 'developer_tools.public_member.turn_on' /></button>
									<#else>				
										<div ng-cloak>
											<button class="btn btn-primary bottomBuffer" ng-click="verifyEmail()"><@orcid.msg 'developer_tools.public_member.verify.button' /></button>		
											<div class="red" ng-show="verifyEmailSent">      							
										        <h4><@orcid.msg 'workspace.sent'/></h4>
										        <@orcid.msg 'workspace.check_your_email'/><br />
    										</div>																						
										</div>
									</#if>
								</div>																								
								<p><@orcid.msg 'developer_tools.client_types.description' /></p>
								<ul class="dotted">
									<li><@orcid.msg 'developer_tools.client_types.description.bullet.1' /></li>
									<li><@orcid.msg 'developer_tools.client_types.description.bullet.2' /></li>
									<li><@orcid.msg 'developer_tools.client_types.description.bullet.3' /></li>
									<li><@orcid.msg 'developer_tools.client_types.description.bullet.4' /></li>
								</ul>
								<p>
								    <@orcid.msg 'developer_tools.client_types.description.oauth2_1' /><a href="http://oauth.net/2/" target="oauth2"><@orcid.msg 'developer_tools.client_types.description.oauth2_2' /></a><@orcid.msg 'developer_tools.client_types.description.oauth2_3' />
								</p>
							</div>
						</div>
						<div class="row">
							<div class="col-md-12 col-sm-12 col-xs-12">
								<h3><@orcid.msg 'developer_tools.client_types.description.differences' /></h3>
								<p><a href="https://orcid.org/about/membership/comparison" target="developer_tools.client_types.description.differences.link"><@orcid.msg 'developer_tools.client_types.description.differences.link' /></a></p>
							</div>
						</div>																
						<div class="row">
							<div class="col-md-12 col-sm-12 col-xs-12">								
								<h3><@orcid.msg 'developer_tools.public_member.additional_resources' /></h3>																	
								<ul class="dotted">
									<#if !hasVerifiedEmail>
										<li><a href ng-click="verifyEmail()"><@orcid.msg 'developer_tools.public_member.verify.link' /></a> <@orcid.msg 'developer_tools.public_member.verify.description' /></li>
									</#if>
									<li><a href="<@orcid.msg 'common.kb_uri_default'/>360006897174" target="developer_tools.public_member.read_more"><@orcid.msg 'developer_tools.public_member.read_more' /></a></li>
								</ul>
							</div>
						</div>													
					</div>
				</#if>										
			<#else>		
				<div class="sso-api">
					<!-- Top content, instructions -->
					<div class="row">				
						<div class="col-md-10 col-sm-10 col-xs-8">					
							<h2><@orcid.msg 'manage.developer_tools.user.title' /></h2>										
						</div>					
					</div>
				
					<div class="row">
						<div class="col-md-12 col-sm-12 col-xs-12">	
							<p><i><@orcid.msg 'developer_tools.note' /> <a href="./my-orcid"><@orcid.msg 'developer_tools.note.link.text' /></a><@orcid.msg 'developer_tools.note.link.point' /></i></p>							
						</div>
					</div>
					<div class="row">
						<div class="col-md-12 col-sm-12 col-xs-12">
							<p class="reset"><strong><@orcid.msg 'developer_tools.public_member.enabled' /></strong></p>
							<p>
							    <@orcid.msg 'developer_tools.public_member.enabled.terms_1' />
							    <a href="http://orcid.org/content/orcid-public-client-terms-service" target="terms_of_service"><@orcid.msg 'developer_tools.public_member.enabled.terms_2' /></a><@orcid.msg 'developer_tools.public_member.enabled.terms_3' />
							</p>							
							<p class="developer-tools-instructions"></p>
						</div>
					</div>
					
					
					
					
					
					
					
					
					
					
					<!-- View client-->
					<div class="details" ng-show="userCredentials.clientSecret && userCredentials.clientSecret.value && !editing" ng-cloak>
						<!-- Name and Edit/Delete options -->
						<div class="row">					
							<div class="col-md-10 col-sm-10 col-xs-9">						
								<h4 ng-bind-html="nameToDisplay"></h4>													
							</div>
							<div class="col-md-2 col-sm-2 col-xs-3">				
								<ul class="sso-options-light pull-right">							
									<li><a href ng-click="showEditLayout()" class="edit" title="<@orcid.msg 'manage.developer_tools.tooltip.edit' />"><span class="glyphicon glyphicon-pencil"></span></a></li>							
								</ul>					
							</div>				
						</div>			
						<div class="row">
							<!-- Website -->
							<div class="col-md-12 col-sm-12 col-xs-12 dt-website">
								<p><a href="{{getClientUrl(userCredentials)}}" target="userCredentials.website.value">{{userCredentials.website.value}}</a></p>
							</div>							
						</div>
						<div class="row">
							<!-- Description -->
							<div class="col-md-12 col-sm-12 col-xs-12 dt-description">
								<p ng-bind-html="descriptionToDisplay"></p>														
							</div>							
						</div>
						<div class="slidebox" ng-show="expanded == true">
							<div class="row">
								<!-- SLIDE BOX  -->
								<!-- Redirect URIS -->
								<div class="col-md-12 col-sm-12 col-xs-12">
									<h4><@orcid.msg 'manage.developer_tools.redirect_uri'/>:</h4>																		
									<select ng-model="selectedRedirectUri" ng-options="rUri.value.value for rUri in userCredentials.redirectUris | orderBy:'value.value'" ng-change="updateSelectedRedirectUri()" class="input-xlarge-full input-xlarge">
									</select>														
								</div>
							</div>
							<div class="row">						
								<div class="col-md-12 col-sm-12 col-xs-12">
									<div class="grey-box">
										<!-- Client details-->
										<div class="row bottomBuffer">
											<div class="col-md-3 col-sm-3 col-xs-12">
												<strong><@orcid.msg 'manage.developer_tools.view.orcid'/></strong>									
											</div>
											<div class="col-md-9 col-sm-9 col-xs-12">
												{{userCredentials.clientId.value}}								
											</div>
										</div>
										
										<div class="row bottomBuffer">
											<div class="col-md-3 col-sm-3 col-xs-12">
												<strong><@orcid.msg 'manage.developer_tools.view.secret'/></strong>								
											</div>
											<div class="col-md-9 col-sm-9 col-xs-12">
												{{userCredentials.clientSecret.value}}							
											</div>
										</div>
										 <!-- Authorize URl and Token URL -->
										<div class="row bottomBuffer" ng-hide="playgroundExample != ''">
											<div class="col-md-3 col-sm-3 col-xs-12">
												<strong><@orcid.msg 'manage.developer_tools.view.example.authorize'/></strong>							
											</div>
											<div class="col-md-9 col-sm-9 col-xs-12">
												<div class="row">
									    			<div class="col-md-12 col-sm-12 col-xs-12">
									    				<strong><@orcid.msg 'manage.developer_tools.view.endpoint'/>&nbsp;</strong>{{authorizeUrlBase}}
									    			</div>
									    		</div>
									    		<div class="row">									    			
										    		<div class="col-md-12 col-sm-12 col-xs-12">
										  				<strong><@orcid.msg 'manage.developer_tools.view.available_scopes.authenticate.scope'/></strong>&nbsp;<@orcid.msg 'manage.developer_tools.view.available_scopes.authenticate'/><br/>
										  				<strong><@orcid.msg 'manage.developer_tools.view.available_scopes.authenticate.response_type'/></strong>&nbsp;<@orcid.msg 'manage.developer_tools.view.response_type.code'/><br/>
										  				<strong><@orcid.msg 'manage.developer_tools.view.description'/></strong>&nbsp;<@orcid.msg 'manage.developer_tools.view.available_scopes.authenticate.description'/>
										  			</div>
									    		</div>
									    		<div class="row">
									    			<div class="col-md-12 col-sm-12 col-xs-12">
									    				<textarea class="input-xlarge-full authorizeURL" ng-model="authorizeURL" readonly="readonly" ng-click="inputTextAreaSelectAll($event)"></textarea>
									    			</div>
									    		</div>								
											</div>
										</div>
										<div class="row bottomBuffer" ng-hide="playgroundExample != ''">
											<div class="col-md-3 col-sm-3 col-xs-12">
												<strong><@orcid.msg 'manage.developer_tools.view.example.token'/></strong>								
											</div>
											<div class="col-md-9 col-sm-9 col-xs-12">
													<strong><@orcid.msg 'manage.developer_tools.view.endpoint'/>&nbsp;</strong>{{tokenURL}}<br />
													<strong><@orcid.msg 'manage.developer_tools.view.available_scopes.authenticate.response_type'/></strong>&nbsp;<@orcid.msg 'manage.developer_tools.view.example.token.response_type'/><br/>
										  			<strong><@orcid.msg 'manage.developer_tools.view.description'/></strong>&nbsp;<@orcid.msg 'manage.developer_tools.view.example.token.description'/><br/>
											    	<@orcid.msg 'manage.developer_tools.view.example.curl' /><a href="<@orcid.msg 'manage.developer_tools.view.example.curl.url' />" target="curlWiki"><@orcid.msg 'manage.developer_tools.view.example.curl.text' /></a> 
											    	<textarea class="input-xlarge-full authorizeURL" ng-model="sampleAuthCurl" readonly="readonly" ng-click="inputTextAreaSelectAll($event)"></textarea>							
											</div>
										</div>
										
										<!-- Openid URL -->
										<div class="row bottomBuffer" ng-hide="playgroundExample != ''">
											<div class="col-md-3 col-sm-3 col-xs-12">
												<strong><@orcid.msg 'manage.developer_tools.view.example.openid'/></strong>							
											</div>
											<div class="col-md-9 col-sm-9 col-xs-12">
												<div class="row">
									    			<div class="col-md-12 col-sm-12 col-xs-12">
									    				<strong><@orcid.msg 'manage.developer_tools.view.endpoint'/>&nbsp;</strong>{{authorizeUrlBase}}
									    			</div>
									    		</div>
									    		<div class="row">									    			
										    		<div class="col-md-12 col-sm-12 col-xs-12">
										  				<strong><@orcid.msg 'manage.developer_tools.view.available_scopes.authenticate.scope'/></strong>&nbsp;<@orcid.msg 'manage.developer_tools.view.available_scopes.openid'/><br/>
										  				<strong><@orcid.msg 'manage.developer_tools.view.available_scopes.authenticate.response_type'/></strong>&nbsp;<@orcid.msg 'manage.developer_tools.view.response_type.token'/><br/>
										  				<strong><@orcid.msg 'manage.developer_tools.view.description'/></strong>&nbsp;<@orcid.msg 'manage.developer_tools.view.available_scopes.openid.description'/> (<a href="<@orcid.msg 'manage.developer_tools.view.example.opendid.url' />" target="openidWiki"><@orcid.msg 'manage.developer_tools.view.example.openid.text' /></a>)
										  			</div>
									    		</div>
									    		<div class="row">
									    			<div class="col-md-12 col-sm-12 col-xs-12">
									    				<textarea class="input-xlarge-full authorizeURL" ng-model="sampleOpenId" readonly="readonly" ng-click="inputTextAreaSelectAll($event)"></textarea>
									    			</div>
									    		</div>								
											</div>
										</div>
										
										<div class="row" ng-hide="playgroundExample == ''">
											<div class="col-md-3 col-sm-3 col-xs-12">
												<strong><@orcid.msg 'manage.developer_tools.view.example.title'/></strong>								
											</div>
											<div class="col-md-9 col-sm-9 col-xs-12">
												<a href="{{playgroundExample}}" target="'manage.developer_tools.view.example.google">
													<span ng-show="selectedRedirectUri.value.value == googleUri"><@orcid.msg 'manage.developer_tools.view.example.google'/></span>
													<span ng-show="selectedRedirectUri.value.value == swaggerUri"><@orcid.msg 'manage.developer_tools.view.example.swagger'/></span>
												</a>
												<br/>
												<span ng-show="selectedRedirectUri.value.value == googleUri">
													<a href="{{googleExampleLinkOpenID}}" target="'manage.developer_tools.view.example.google">
														<@orcid.msg 'manage.developer_tools.view.example.googleOIDC'/>
													</a>
												</span>
											</div>
										</div>												
									</div>
								</div>
							</div>
						</div>				
					</div>
					
					
					
					
					
					<!-- Create client -->
					<div class="create-client" ng-show="creating" ng-cloak>	
						<!-- Name -->
						<div class="row">					
							<div class="col-md-10 col-sm-10 col-xs-12">
									<span><strong><@orcid.msg 'manage.developer_tools.generate.name'/></strong></span>
									<input type="text" placeholder="<@orcid.msg 'manage.developer_tools.generate.name.placeholder'/>" class="full-width-input" ng-model="userCredentials.displayName.value">
									<span class="orcid-error" ng-show="userCredentials.displayName.errors.length > 0">
										<div ng-repeat='error in userCredentials.displayName.errors' ng-bind-html="error"></div>
									</span>
							</div>	
							<div class="col-md-2 col-sm-3"></div>											
						</div>
						<!-- Website -->
						<div class="row">					
							<div class="col-md-10 col-sm-10 col-xs-12 dt-website">
								<span><strong><@orcid.msg 'manage.developer_tools.generate.website'/></strong></span>
								<input type="text" placeholder="<@orcid.msg 'manage.developer_tools.generate.website.placeholder'/>" class="full-width-input" ng-model="userCredentials.website.value">
								<span class="orcid-error" ng-show="userCredentials.website.errors.length > 0">
									<div ng-repeat='error in userCredentials.website.errors' ng-bind-html="error"></div>
								</span>												
							</div>			
							<div class="col-md-2 col-sm-2"></div>									
						</div>
						<!-- Description -->						
						<div class="row">					
							<div class="col-md-10 col-sm-10 col-xs-12 dt-description">						
								<span><strong><@orcid.msg 'manage.developer_tools.generate.description'/></strong></span>
								<textarea placeholder="<@orcid.msg 'manage.developer_tools.generate.description.placeholder'/>" ng-model="userCredentials.shortDescription.value"></textarea>						
								<span class="orcid-error" ng-show="userCredentials.shortDescription.errors.length > 0">
									<div ng-repeat='error in userCredentials.shortDescription.errors' ng-bind-html="error"></div>
								</span>												
							</div>			
							<div class="col-md-2 col-sm-2"></div>									
						</div>
						<!-- Redirect URIS -->
						<div class="row">
							<!-- SLIDE BOX  -->					
							<div class="col-md-10 col-sm-10 col-xs-12">
								<div class="redirectUris">
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
						</div>
						<!-- Options -->
						<div class="row">
							<div class="col-md-9 col-sm-9 col-xs-9 add-options">
								<a href="" class="icon-href-bg" ng-click="addRedirectURI()"><span class="glyphicon glyphicon-plus"></span><@orcid.msg 'manage.developer_tools.edit.add_redirect_uri' /></a>
								<div class="add-options margin-bottom-box">								
									<div>
										<h4><@orcid.msg 'manage.developer_tools.test_redirect_uris.title' /></h4>
										<ul class="pullleft-list">
											<li ng-show="!hideGoogleUri" id="google-ruir"><a href="" class="icon-href" ng-click="addTestRedirectUri('google')"><span class="glyphicon glyphicon-plus"></span><@orcid.msg 'manage.developer_tools.edit.google'/></a></li>
											<li ng-show="!hideSwaggerUri" id="swagger-ruir"><a href="" class="icon-href" ng-click="addTestRedirectUri('swagger')"><span class="glyphicon glyphicon-plus"></span><@orcid.msg 'manage.developer_tools.edit.swagger'/></a></li>										
										</ul>								
									</div>
								</div>						
							</div>
							<div class="col-md-3 col-sm-3 col-xs-3">				
								<ul class="sso-options pull-right">									
									<li><a href ng-click="submit()" class="save" title="<@orcid.msg 'manage.developer_tools.tooltip.save' />"><span class="glyphicon glyphicon-floppy-disk"></span></a></li>																								
								</ul>					
							</div>	
						</div>				
					</div>
					<div class="row slide" ng-show="userCredentials.clientSecret && userCredentials.clientSecret.value && !editing" ng-cloak>
						<div class="col-md-12 col-sm-12 col-xs-12">
							<div class="tab-container" ng-class="{'expanded' : expanded == true}">
								<a class="tab" ng-click="expand()" ng-show="expanded == false"><span class="glyphicon glyphicon-chevron-down"></span><@orcid.msg 'common.details.show_details' /></a>
								<a class="tab" ng-click="collapse()" ng-show="expanded == true"><span class="glyphicon glyphicon-chevron-up"></span><@orcid.msg 'common.details.hide_details' /></a>
							</div>
						</div>			
					</div>
					
					
					
					
					
					
					
					
					
					
					
					
					
					
					
					<!-- Edit form -->
					<div class="edit-details" ng-show="userCredentials.clientSecret && userCredentials.clientSecret.value && editing" ng-cloak>			
						<!-- Name and Edit/Delete options -->
						<div class="row">					
							<div class="col-md-10 col-sm-10 col-xs-12">						
								<span><strong><@orcid.msg 'manage.developer_tools.generate.name'/></strong></span>
								<input type="text" placeholder="<@orcid.msg 'manage.developer_tools.generate.name.placeholder'/>" class="full-width-input" ng-model="userCredentials.displayName.value">
								<span class="orcid-error" ng-show="userCredentials.displayName.errors.length > 0">
									<div ng-repeat='error in userCredentials.displayName.errors' ng-bind-html="error"></div>
								</span>
							</div>	
							<div class="col-md-2 col-sm-2 col-xs-12"></div>											
						</div>
						<div class="row">
							<!-- Website -->
							<div class="col-md-10 col-sm-10 col-xs-12 dt-website">						
								<span><strong><@orcid.msg 'manage.developer_tools.generate.website'/></strong></span>
								<input type="text" placeholder="<@orcid.msg 'manage.developer_tools.generate.website.placeholder'/>" class="full-width-input" ng-model="userCredentials.website.value">
								<span class="orcid-error" ng-show="userCredentials.website.errors.length > 0">
									<div ng-repeat='error in userCredentials.website.errors' ng-bind-html="error"></div>
								</span>													
							</div>			
							<div class="col-md-2 col-sm-2"></div>									
						</div>
						<div class="row">
							<!-- Description -->
							<div class="col-md-10 col-sm-10 col-xs-12 dt-description">						
								<span><strong><@orcid.msg 'manage.developer_tools.generate.description'/></strong></span>
								<textarea placeholder="<@orcid.msg 'manage.developer_tools.generate.description.placeholder'/>" ng-model="userCredentials.shortDescription.value" class="full-width-input"></textarea>						
								<span class="orcid-error" ng-show="userCredentials.shortDescription.errors.length > 0">
									<div ng-repeat='error in userCredentials.shortDescription.errors' ng-bind-html="error"></div>
								</span>													
							</div>			
							<div class="col-md-2 col-sm-2"></div>									
						</div>				
						<div class="row">
							<!-- SLIDE BOX  -->
							<!-- Redirect URIS -->
							<div class="col-md-10 col-sm-10 col-xs-12">
								<div class="redirectUris">
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
								<div class="add-options margin-bottom-box" ng-show="!hideGoogleUri || !hideSwaggerUri">								
									<div>
										<h4><@orcid.msg 'manage.developer_tools.test_redirect_uris.title' /></h4>
										<ul class="pullleft-list">
											<li ng-show="!hideGoogleUri" id="google-ruir"><a href="" class="icon-href" ng-click="addTestRedirectUri('google')"><span class="glyphicon glyphicon-plus"></span><@orcid.msg 'manage.developer_tools.edit.google'/></a></li>
											<li ng-show="!hideSwaggerUri" id="swagger-ruir"><a href="" class="icon-href" ng-click="addTestRedirectUri('swagger')"><span class="glyphicon glyphicon-plus"></span><@orcid.msg 'manage.developer_tools.edit.swagger'/></a></li>										
										</ul>								
									</div>
								</div>
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
									<div class="grey-box">
										<div class="row bottomBuffer">
											<div class="col-md-3 col-sm-3 col-xs-12">
												<strong><@orcid.msg 'manage.developer_tools.view.orcid'/></strong>
											</div>
											<div class="col-md-9 col-sm-9 col-xs-12">
												{{userCredentials.clientId.value}}
											</div>
										</div>
										<div class="row bottomBuffer">
											<div class="col-md-3 col-sm-3 col-xs-12">
												<strong><@orcid.msg 'manage.developer_tools.view.secret'/></strong>
											</div>
											<div class="col-md-9 col-sm-9 col-xs-12">
												{{userCredentials.clientSecret.value}}
											</div>
										</div>
										<div class="row bottomBuffer">
											<div class="col-md-3 col-sm-3 col-xs-12">
											</div>
											<div class="col-md-9 col-sm-9 col-xs-12">
												<a href="" class="btn btn-primary" ng-click="confirmResetClientSecret()">
										    			<@orcid.msg 'manage.developer_tools.edit.reset_client_secret' />
												</a>
											</div>
										</div>
																			
									</div>
								</div>	 
							</div>					
						</div>				
					</div>
					
					
					
					
					
					
					
					
					
					
					
					
					
					
					
					
					
					
					
					
					
					<!-- Bottom instructions -->	
					<div class="row">
						<div class="col-md-12 col-sm-12 col-xs-12">
							<h3><@orcid.msg 'developer_tools.public_member.what_can_you_do' /></h3>
							<p><@orcid.msg 'developer_tools.public_member.what_can_you_do.description' /></p>
							<ul class="dotted">
								<li><@orcid.msg 'developer_tools.client_types.description.bullet.1' /></li>
								<li><@orcid.msg 'developer_tools.client_types.description.bullet.2' /></li>
								<li><@orcid.msg 'developer_tools.client_types.description.bullet.3' /></li>
								<li><@orcid.msg 'developer_tools.client_types.description.bullet.4' /></li>
							</ul>
							<p>
                                <@orcid.msg 'developer_tools.client_types.description.oauth2_1' /><a href="http://oauth.net/2/" target="oauth2"><@orcid.msg 'developer_tools.client_types.description.oauth2_2' /></a><@orcid.msg 'developer_tools.client_types.description.oauth2_3' />
                            </p>
						</div>
					</div>
					<div class="row">
						<div class="col-md-12 col-sm-12 col-xs-12">
							<h3><@orcid.msg 'developer_tools.client_types.description.differences' /></h3>
							<p><a href="https://orcid.org/about/membership/comparison" target="developer_tools.client_types.description.differences.link"><@orcid.msg 'developer_tools.client_types.description.differences.link' /></a></p>
						</div>
					</div>																
					<div class="row">
						<div class="col-md-12 col-sm-12 col-xs-12">								
							<h3><@orcid.msg 'developer_tools.public_member.additional_resources' /></h3>																	
							<ul class="dotted">
								<li><a href="http://members.orcid.org/api/introduction-orcid-public-api" target="developer_tools.public_member.read_more"><@orcid.msg 'developer_tools.public_member.read_more' /></a></li>
							</ul>
						</div>
					</div>
				</div>
			</#if>
		</div>				
	</div>		
	
<script type="text/ng-template" id="reset-client-secret-modal">
	<div class="lightbox-container">
		<div class="row">
			<div class="col-md-12 col-xs-12 col-sm-12">
				<h3><@orcid.msg 'manage.developer_tools.edit.reset_key.title' /></h3>				
				<p><strong>{{clientSecretToReset.value}}</strong></p>		
				<p><@orcid.msg 'manage.developer_tools.edit.reset_key.description' /></p>
    			<div class="btn btn-danger" ng-click="resetClientSecret()">
    				<@orcid.msg 'freemarker.btnReset' />
    			</div>
    			<a href="" ng-click="closeModal()"><@orcid.msg 'freemarker.btncancel' /></a>
			</div>
		</div>
    </div>
</script>

<script type="text/ng-template" id="terms-and-conditions-modal">
	<div class="lightbox-container">		
		<div class="col-md-12 col-xs-12 col-sm-12">			
			<div class="row bottomBuffer topBuffer">
				<div class="col-md-12 col-xs-12 col-sm-12">
					<h2 class="bottomBuffer"><@orcid.msg 'developer_tools.public_member.terms.title' /></h2>
				</div>
				<div class="col-md-12 col-xs-12 col-sm-12">
					<span>
					   <@orcid.msg 'developer_tools.public_member.terms.description_1' /><a href="http://orcid.org/content/orcid-public-client-terms-service" target="terms_of_service"><@orcid.msg 'developer_tools.public_member.terms.description_2' /></a><@orcid.msg 'developer_tools.public_member.terms.description_3' />
					</span>				
				</div>				
			</div> 		
			<div class="row">
				<div class="col-md-8 col-xs-8 col-sm-12">
					<div class="row">
						<span class="col-md-1 col-xs-1 col-sm-1 vertical-align-middle"><input type="checkbox" name="accepted" ng-model="accepted" /></span>	
						<span class="col-md-11 col-xs-11 col-sm-11">
						  <@orcid.msg 'developer_tools.public_member.terms.check_1' />
						  <a href="http://orcid.org/content/orcid-public-client-terms-service" target="terms_of_service">
						  <@orcid.msg 'developer_tools.public_member.terms.check_2' />
						  </a>
						</span>
					</div>
					<div class="row" ng-show="mustAcceptTerms">
						<span class="col-md-1 col-xs-1 col-sm-1">&nbsp;</span>	
						<span class="col-md-11 col-xs-11 col-sm-11 red"><@orcid.msg 'developer_tools.public_member.terms.must_accept' /></span>
					</div>
				</div>
				<div class="col-md-4 col-xs-4 col-sm-12">					
					<a href ng-click="closeModal()"><@orcid.msg 'freemarker.btncancel' /></a>
					<button class="btn btn-primary" ng-click="enableDeveloperTools()"><@orcid.msg 'freemarker.btncontinue' /></button>
				</div>
			</div>	
		</div>
	</div>
</script>

</@public>
