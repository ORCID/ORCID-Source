<#--

    =============================================================================

    ORCID (R) Open Source
    http://orcid.org

    Copyright (c) 2012-2014 ORCID, Inc.
    Licensed under an MIT-Style License (MIT)
    http://orcid.org/open-source-license

    This copyright and license information (including a link to the full license)
    shall be included in its entirety in all copies or substantial portion of
    the software.

    =============================================================================

-->
<@public nav="developer-tools">
<@security.authorize ifNotGranted="ROLE_GROUP,ROLE_BASIC,ROLE_PREMIUM,ROLE_BASIC_INSTITUTION,ROLE_PREMIUM_INSTITUTION,ROLE_CREATOR,ROLE_PREMIUM_CREATOR,ROLE_UPDATER,ROLE_PREMIUM_UPDATER">
	<div class="row">
		<div class="col-md-3 lhs col-sm-12 col-xs-12 padding-fix">
			<#include "includes/id_banner.ftl"/>
		</div>
		<div class="col-md-9 col-sm-12 col-xs-12 developer-tools">
		
			<#if profile.orcidInternal?? && profile.orcidInternal.preferences.developerToolsEnabled?? && profile.orcidInternal.preferences.developerToolsEnabled.value == false>
				<h1 id="manage-developer-tools">
					<span><@spring.message "manage.developer_tools.user.title"/></span>					
				</h1>
				<#if !inDelegationMode || isDelegatedByAdmin>
					<div class="sso-api" ng-controller="SSOPreferencesCtrl">						
						<div class="row">
							<div class="col-md-12 col-sm-12 col-xs-12">
								<p><i><@orcid.msg 'developer_tools.note' /> <a href="./my-orcid"><@orcid.msg 'developer_tools.note.link.text' /></a><@orcid.msg 'developer_tools.note.link.point' /></i></p>																
								<div class="centered bottomBuffer">
									<#if hasVerifiedEmail>
										<button class="btn btn-primary" ng-click="enableDeveloperTools()" ><@orcid.msg 'developer_tools.public_member.turn_on' /></button>
									<#else>
										<button class="btn btn-primary" ng-click="verifyEmail()" ><@orcid.msg 'developer_tools.public_member.verify.button' /></button>
									</#if>
								</div>																								
								<p><@orcid.msg 'developer_tools.client_types.description' /></p>
								<ul class="dotted">
									<li><@orcid.msg 'developer_tools.client_types.description.bullet.1' /></li>
									<li><@orcid.msg 'developer_tools.client_types.description.bullet.2' /></li>
									<li><@orcid.msg 'developer_tools.client_types.description.bullet.3' /></li>
									<li><@orcid.msg 'developer_tools.client_types.description.bullet.4' /></li>
								</ul>
								<p><@orcid.msg 'developer_tools.client_types.description.oauth2' /></p>
							</div>
						</div>
						<div class="row">
							<div class="col-md-12 col-sm-12 col-xs-12">
								<h3><@orcid.msg 'developer_tools.client_types.description.differences' /></h3>
								<p><a href="https://orcid.org/about/membership/comparison" target="_blank"><@orcid.msg 'developer_tools.client_types.description.differences.link' /></a></p>
							</div>
						</div>																
						<div class="row">
							<div class="col-md-12 col-sm-12 col-xs-12">								
								<h3><@orcid.msg 'developer_tools.public_member.additional_resources' /></h3>																	
								<ul class="dotted">
									<#if !hasVerifiedEmail>
										<li><a href ng-click="verifyEmail()"><@orcid.msg 'developer_tools.public_member.verify.link' /></a> <@orcid.msg 'developer_tools.public_member.verify.description' /></li>
									</#if>
									<li><a href="http://support.orcid.org/knowledgebase/articles/335483" target="_blank"><@orcid.msg 'developer_tools.public_member.read_more' /></a></li>
								</ul>
							</div>
						</div>													
					</div>
				<#else>
					<@orcid.msg 'developer_tools.unavailable' />
				</#if>						
			<#else>		
				<!-- Developer public API Applications -->
				<div ng-controller="SSOPreferencesCtrl" class="sso-api">
					<!-- Top content, instructions -->
					<div class="row">				
						<div class="col-md-10 col-sm-10 col-xs-8">					
							<h2><@orcid.msg 'manage.developer_tools.user.title' /></h2>										
						</div>
						<div class="col-md-2 col-sm-2 col-xs-4" ng-show="showReg" ng-cloak>
							<a ng-click="createCredentialsLayout()" class="pull-right"><span class="label btn-primary"><@orcid.msg 'manage.developer_tools.button.register_now' /></span></a>
						</div>	
					</div>
					<div class="row">
						<div class="col-md-12 col-sm-12 col-xs-12">	
							<p><i><@orcid.msg 'developer_tools.note' /> <a href="./my-orcid"><@orcid.msg 'developer_tools.note.link.text' /></a><@orcid.msg 'developer_tools.note.link.point' /></i></p>
							<p class="developer-tools-instructions"></p>
						</div>
					</div>
					<!-- App details -->
					<div class="details" ng-show="userCredentials.clientSecret && userCredentials.clientSecret.value && !editing" ng-cloak>
					
						<!-- Name and Edit/Delete options -->
						<div class="row">					
							<div class="col-md-10 col-sm-10 col-xs-9">						
								<h4 ng-bind-html="nameToDisplay"></h4>													
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
								<p ng-bind-html="descriptionToDisplay"></p>														
							</div>							
						</div>
						<div class="slidebox">
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
												{{userCredentials.clientOrcid.value}}								
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
									    				{{authorizeUrlBase}}
									    			</div>
									    		</div>
									    		<div class="row">									    			
										    		<div class="col-md-5 col-sm-5 col-xs-12">
										  				<strong><@orcid.msg 'manage.developer_tools.view.available_scopes.authenticate.scope'/>&nbsp;<@orcid.msg 'manage.developer_tools.view.available_scopes.authenticate'/></strong>
										  			</div>
										  			<div class="col-md-7 col-sm-7 col-xs-12 no-wrap">
										  				<@orcid.msg 'manage.developer_tools.view.available_scopes.authenticate.description'/>
										  			</div>
									    		</div>
									    		<div class="row">
									    			<div class="col-md-12 col-sm-12 col-xs-12">
									    				<textarea class="input-xlarge-full selectable authorizeURL" ng-model="authorizeURL" readonly="readonly"></textarea>
									    			</div>
									    		</div>								
											</div>
										</div>
										
										<div class="row bottomBuffer" ng-hide="playgroundExample != ''">
											<div class="col-md-3 col-sm-3 col-xs-12">
												<strong><@orcid.msg 'manage.developer_tools.view.example.token'/></strong>								
											</div>
											<div class="col-md-9 col-sm-9 col-xs-12">
													{{tokenURL}}<br />
											    	<@orcid.msg 'manage.developer_tools.view.example.curl' /><a href="<@orcid.msg 'manage.developer_tools.view.example.curl.url' />" target="curlWiki"><@orcid.msg 'manage.developer_tools.view.example.curl.text' /></a> 
											    	<textarea class="input-xlarge-full selectable authorizeURL" ng-model="sampleAuthCurl" readonly="readonly"></textarea>							
											</div>
										</div>
										
										<div class="row" ng-hide="playgroundExample == ''">
											<div class="col-md-3 col-sm-3 col-xs-12">
												<strong><@orcid.msg 'manage.developer_tools.view.example.title'/></strong>								
											</div>
											<div class="col-md-9 col-sm-9 col-xs-12">
												<a href="{{playgroundExample}}" target="_blank"><@orcid.msg 'manage.developer_tools.view.example.google'/></a>							
											</div>
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
							<div class="col-md-10 col-sm-10 col-xs-12">
									<span><strong><@orcid.msg 'manage.developer_tools.generate.name'/></strong></span>
									<input type="text" placeholder="<@orcid.msg 'manage.developer_tools.generate.name.placeholder'/>" class="full-width-input" ng-model="userCredentials.clientName.value">
									<span class="orcid-error" ng-show="userCredentials.clientName.errors.length > 0">
										<div ng-repeat='error in userCredentials.clientName.errors' ng-bind-html="error"></div>
									</span>
							</div>	
							<div class="col-md-2 col-sm-3"></div>											
						</div>
									
						<!-- Website -->
						<div class="row">					
							<div class="col-md-10 col-sm-10 col-xs-12 dt-website">
								<span><strong><@orcid.msg 'manage.developer_tools.generate.website'/></strong></span>
								<input type="text" placeholder="<@orcid.msg 'manage.developer_tools.generate.website.placeholder'/>" class="full-width-input" ng-model="userCredentials.clientWebsite.value">
								<span class="orcid-error" ng-show="userCredentials.clientWebsite.errors.length > 0">
									<div ng-repeat='error in userCredentials.clientWebsite.errors' ng-bind-html="error"></div>
								</span>												
							</div>			
							<div class="col-md-2 col-sm-2"></div>									
						</div>
										
						<!-- Description -->						
						<div class="row">					
							<div class="col-md-10 col-sm-10 col-xs-12 dt-description">						
								<span><strong><@orcid.msg 'manage.developer_tools.generate.description'/></strong></span>
								<textarea placeholder="<@orcid.msg 'manage.developer_tools.generate.description.placeholder'/>" ng-model="userCredentials.clientDescription.value"></textarea>						
								<span class="orcid-error" ng-show="userCredentials.clientDescription.errors.length > 0">
									<div ng-repeat='error in userCredentials.clientDescription.errors' ng-bind-html="error"></div>
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
					<div class="edit-details" ng-show="userCredentials.clientSecret && userCredentials.clientSecret.value && editing" ng-cloak>			
						<!-- Name and Edit/Delete options -->
						<div class="row">					
							<div class="col-md-10 col-sm-10 col-xs-12">						
								<span><strong><@orcid.msg 'manage.developer_tools.generate.name'/></strong></span>
								<input type="text" placeholder="<@orcid.msg 'manage.developer_tools.generate.name.placeholder'/>" class="full-width-input" ng-model="userCredentials.clientName.value">
								<span class="orcid-error" ng-show="userCredentials.clientName.errors.length > 0">
									<div ng-repeat='error in userCredentials.clientName.errors' ng-bind-html="error"></div>
								</span>
							</div>	
							<div class="col-md-2 col-sm-2 col-xs-12"></div>											
						</div>
									
						<div class="row">
							<!-- Website -->
							<div class="col-md-10 col-sm-10 col-xs-12 dt-website">						
								<span><strong><@orcid.msg 'manage.developer_tools.generate.website'/></strong></span>
								<input type="text" placeholder="<@orcid.msg 'manage.developer_tools.generate.website.placeholder'/>" class="full-width-input" ng-model="userCredentials.clientWebsite.value">
								<span class="orcid-error" ng-show="userCredentials.clientWebsite.errors.length > 0">
									<div ng-repeat='error in userCredentials.clientWebsite.errors' ng-bind-html="error"></div>
								</span>													
							</div>			
							<div class="col-md-2 col-sm-2"></div>									
						</div>
																
						<div class="row">
							<!-- Description -->
							<div class="col-md-10 col-sm-10 col-xs-12 dt-description">						
								<span><strong><@orcid.msg 'manage.developer_tools.generate.description'/></strong></span>
								<textarea placeholder="<@orcid.msg 'manage.developer_tools.generate.description.placeholder'/>" ng-model="userCredentials.clientDescription.value" class="full-width-input"></textarea>						
								<span class="orcid-error" ng-show="userCredentials.clientDescription.errors.length > 0">
									<div ng-repeat='error in userCredentials.clientDescription.errors' ng-bind-html="error"></div>
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
										<div class="row bottomBuffer">
											<div class="col-md-3 col-sm-3 col-xs-12">
												<strong><@orcid.msg 'manage.developer_tools.view.orcid'/></strong>
											</div>
											<div class="col-md-9 col-sm-9 col-xs-12">
												{{userCredentials.clientOrcid.value}}
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
												<a href="" class="btn btn-danger" ng-click="confirmResetClientSecret()">								    		
										    			<@orcid.msg 'manage.developer_tools.edit.reset_client_secret' />
										    		</a>
											</div>
										</div>
																			
									</div>
								</div>	 
							</div>					
						</div>				
					</div>										
					<div class="row slide" ng-show="userCredentials.clientSecret && userCredentials.clientSecret.value" ng-cloak>
						<div class="col-md-12 col-sm-12 col-xs-12">
							<div class="tab-container">
								<a href="#" class="tab collapsed" data-tab="collapsed"><span class="glyphicon glyphicon-chevron-down"></span><@orcid.msg 'manage.developer_tools.show_details' /></a>
								<a href="#" class="tab expanded"><span class="glyphicon glyphicon-chevron-up"></span><@orcid.msg 'manage.developer_tools.hide_details' /></a>
							</div>
						</div>			
					</div>
					<div class="row">
						<div class="col-md-12 col-sm-12 col-xs-12">
							<h3><@orcid.msg 'developer_tools.public_member.what_can_you_do' /></h3>
							<p><@orcid.msg 'developer_tools.public_member.what_can_you_do.description' /></p>
								<ul class="dotted">
									<li><@orcid.msg 'developer_tools.public_member.what_can_you_do.bullet.1' /></li>
									<li><@orcid.msg 'developer_tools.public_member.what_can_you_do.bullet.2' /></li>
									<li><@orcid.msg 'developer_tools.public_member.what_can_you_do.bullet.3' /></li>
									<li><@orcid.msg 'developer_tools.public_member.what_can_you_do.bullet.4' /></li>
								</ul>
								<p><@orcid.msg 'developer_tools.client_types.description.oauth2' /></p>
						</div>
					</div>
					<div class="row">
						<div class="col-md-12 col-sm-12 col-xs-12">
							<h3><@orcid.msg 'developer_tools.client_types.description.differences' /></h3>
							<p><a href="https://orcid.org/about/membership/comparison" target="_blank"><@orcid.msg 'developer_tools.client_types.description.differences.link' /></a></p>
						</div>
					</div>																
					<div class="row">
						<div class="col-md-12 col-sm-12 col-xs-12">								
							<h3><@orcid.msg 'developer_tools.public_member.additional_resources' /></h3>																	
							<ul class="dotted">
								<li><a href="http://support.orcid.org/knowledgebase/articles/335483" target="_blank"><@orcid.msg 'developer_tools.public_member.read_more' /></a></li>
							</ul>
						</div>
					</div>
				</div>
			</#if>
		</div>				
	</div>		
</@security.authorize>
<@security.authorize ifAnyGranted="ROLE_GROUP,ROLE_BASIC,ROLE_PREMIUM,ROLE_BASIC_INSTITUTION,ROLE_PREMIUM_INSTITUTION,ROLE_CREATOR,ROLE_PREMIUM_CREATOR,ROLE_UPDATER,ROLE_PREMIUM_UPDATER">
	<@orcid.msg 'developer_tools.invalid_page'/>
</@security.authorize>

<script type="text/ng-template" id="verify-email-modal">
	<div style="padding: 20px;"><h3>${springMacroRequestContext.getMessage("manage.email.verificationEmail")} {{verifyEmailObject.value}}</h3>
	<button class="btn" ng-click="closeModal()">${springMacroRequestContext.getMessage("manage.email.verificationEmail.close")}</button>
</script>

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

</@public>
