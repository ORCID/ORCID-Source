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
<@public nav="developer">

<div class="row developer-tools">
	<div class="col-md-3 col-sm-12 col-xs-12">
		<#include "includes/id_banner.ftl"/>
	</div>
	<div class="col-md-9 col-sm-12 col-xs-12 margin-top-box-mobile">		
		<div ng-controller="ClientEditCtrl">	
			<!-- Header -->
			
			<div class="row">
				<div class="col-md-9 col-sm-10 col-xs-10">
					<h2><@orcid.msg 'manage.developer_tools.group.title'/></h2>
				</div>
				
				<div class="col-md-3 col-sm-2 col-xs-2">				
					<@security.authorize ifAnyGranted="ROLE_PREMIUM_INSTITUTION, ROLE_PREMIUM, ROLE_ADMIN">						
						<a href="" class="pull-right"><span id="label btn-primary cboxElement" ng-click="showAddClient()" class="btn btn-primary"><@orcid.msg 'manage.developer_tools.group.add'/></span></a>										
					</@security.authorize>
					<@security.authorize ifAnyGranted="ROLE_BASIC_INSTITUTION, ROLE_BASIC">
						<#if (group)?? && (group.orcidClient)?? && !(group.orcidClient?has_content)> 							
							<a href="" ng-hide="clients.length > 0"><span id="label btn-primary cboxElement" ng-click="showAddClient()" class="btn btn-primary"><@orcid.msg 'manage.developer_tools.group.add'/></span></a>				
						</#if>
					</@security.authorize>
				</div>				
			</div>
			<div class="row">
				<div class="col-md-12 col-sm-12 col-xs-12">				
					<p class="developer-tools-instructions"><@orcid.msg 'manage.developer_tools.header' /></p>
				</div>
			</div>		
			<!-- View existing credentials -->
			<div class="listing-clients" ng-show="listing" ng-cloack>
				<div class="row">
					<div class="col-md-12 client-api">
						<p><@orcid.msg 'manage.developer_tools.group.description.1' />&nbsp;<a href="<@orcid.msg 'manage.developer_tools.group.description.link.url' />"><@orcid.msg 'manage.developer_tools.group.description.link.text' /></a><@orcid.msg 'manage.developer_tools.group.description.2' /></p>		
						<div ng-show="clients.length == 0" ng-cloak>
							<span><@orcid.msg 'manage.developer_tools.group.no_clients'/></span><br />
							<span><@orcid.msg 'manage.developer_tools.group.register_now'/>&nbsp;<a href="" ng-click="showAddClient()"><@orcid.msg 'manage.developer_tools.group.add'/></a></span>
						</div>	
						<div ng-show="clients.length > 0" ng-cloak>
							<table class="table sub-table">
								<tbody>
									<tr>
										<td colspan="12" class="table-header-dt">
											<@orcid.msg 'manage.developer_tools.group.group.id'/> ${(group.groupOrcid)!} (${(group.type)!})
										</td>						
									</tr>	
									<tr ng-repeat="client in clients">
										<td colspan="8">
											{{client.displayName.value}} (<a href="{{client.website.value}}" target="_blank">{{client.website.value}}</a>)
										</td>												
										<td colspan="4" class="pull-right">										
											<ul class="client-options">
												<li><a href ng-click="viewDetails(client)"><span class="glyphicon glyphicon-eye-open"></span><@orcid.msg 'manage.developer_tools.group.view_credentials_link' /></a></li>	
												<li><a href ng-click="showEditClient(client)"><span class="glyphicon glyphicon-pencil"></span><@orcid.msg 'manage.developer_tools.group.edit_credentials_link' /></a></li>												
											</ul>										
										</td>									
									</tr>												
								</tbody>
							</table>
						</div>																
					</div>			
				</div>	
			</div>
			<!-- ---------------------- -->
			<!-- Create new credentials -->
			<!-- ---------------------- -->
			<div class="create-client" ng-show="creating" ng-cloak>	
				<!-- Name -->
				<div class="row">					
					<div class="col-md-12 col-sm-12 col-xs-12">
						<div class="inner-row margin-left-fix">
							<span><strong><@orcid.msg 'manage.developer_tools.group.display_name'/></strong></span>
							<input type="text" placeholder="<@orcid.msg 'manage.developer_tools.group.display_name_placeholder'/>" class="input-xlarge" ng-model="newClient.displayName.value" />
							<span class="orcid-error" ng-show="newClient.displayName.errors.length > 0">
								<div ng-repeat='error in newClient.displayName.errors' ng-bind-html="error"></div>
							</span>					
						</div>		
					</div>																
				</div>
				<!-- Website -->
				<div class="row">	
					<div class="col-md-12 col-sm-12 col-xs-12">
						<div class="inner-row margin-left-fix">
							<span><strong><@orcid.msg 'manage.developer_tools.group.website'/></strong></span>
							<input type="text" placeholder="<@orcid.msg 'manage.developer_tools.group.website_placeholder'/>" class="input-xlarge" ng-model="newClient.website.value" />
							<span class="orcid-error" ng-show="newClient.website.errors.length > 0">
								<div ng-repeat='error in newClient.website.errors' ng-bind-html="error"></div>
							</span>					
						</div>		
					</div>	
				</div>
				<!-- Description -->
				<div class="row">					
					<div class="col-md-12 col-sm-12 col-xs-12 dt-description">
						<div class="inner-row margin-left-fix">
							<span><strong><@orcid.msg 'manage.developer_tools.group.description'/></strong></span>
							<textarea class="input-xlarge selectable" placeholder="<@orcid.msg 'manage.developer_tools.group.description_placeholder'/>" ng-model="newClient.shortDescription.value"></textarea>						
							<span class="orcid-error" ng-show="newClient.shortDescription.errors.length > 0">
								<div ng-repeat='error in newClient.shortDescription.errors' ng-bind-html="error"></div>
							</span>
						</div>															
					</div>			
				</div>
				<!-- Redirect Uris -->				
				<div ng-repeat="rUri in newClient.redirectUris" class="margin-bottom-box">
					<!-- Header -->
					<div class="row" ng-show="$first">
						<div class="col-md-12 col-sm-12 col-xs-12">
							<div class="inner-row margin-left-fix">					
								<h4><@orcid.msg 'manage.developer_tools.redirect_uri'/></h4>
							</div>
						</div>
					</div>
					<!-- Value -->
					<div class="grey-box">
						<div class="row">						
							<div class="col-md-12 col-sm-12 col-xs-12">
								<div class="inner-row margin-left-fix">							
									<input type="text" placeholder="<@orcid.msg 'manage.developer_tools.group.redirect_uri_placeholder'/>" class="input-xlarge" ng-model="rUri.value.value" />															
									<span class="orcid-error" ng-show="rUri.value.errors.length > 0">
										<div ng-repeat='error in rUri.value.errors' ng-bind-html="error"></div>
									</span>
								</div>											
							</div>	
						</div>
						<!-- Type -->
						<div class="row">						
							<div class="col-md-12 col-sm-12 col-xs-12">
								<div class="inner-row margin-left-fix">
									<select class="input-large input-xlarge-full" ng-model="rUri.type.value" ng-change="loadDefaultScopes(rUri)">
										<#list redirectUriTypes?keys as key>
											<option value="${key}">${redirectUriTypes[key]}</option>
										</#list>
									</select>
								</div>															
							</div>							
						</div>						
						<!-- Scopes -->
						<div class="row">						
							<div class="col-md-12 col-sm-12 col-xs-12" ng-show="rUri.type.value != 'default'">
								<div class="inner-row margin-left-fix">
									<div class="scope_item" ng-repeat="scope in availableRedirectScopes">
										<div class="small-box">
											<div class="checkbox">										
												<label>
													<input type="checkbox" ng-checked="isChecked(rUri)" ng-click="setSelectedItem(rUri)"/>{{scope}}
												</label>
											</div>										
										</div>								
									</div>
								</div>
							</div>
						</div>
						<div class="row">
							<div class="col-md-12 col-sm-12 col-xs-12">
								<a href ng-click="deleteUriOnNewClient($index)" class="glyphicon glyphicon-trash grey pull-right"></a>
							</div>
						</div>
					</div>
				</div>		
				<div class="row">
					<!-- Add redirect uris -->
					<div class="col-md-9 col-sm-9 col-xs-9 add-options">
						<a href="" class="icon-href-bg" ng-click="addRedirectUriToNewClientTable()"><span class="glyphicon glyphicon-plus"></span><@orcid.msg 'manage.developer_tools.edit.add_redirect_uri' /></a>
						<div class="add-options margin-bottom-box">								
							<div ng-show="!hideGoogleUri">
								<h4><@orcid.msg 'manage.developer_tools.test_redirect_uris.title' /></h4>
								<ul class="pullleft-list">
									<li id="google-ruir"><a href="" class="icon-href" ng-click="addTestRedirectUri('google','false')"><span class="glyphicon glyphicon-plus"></span><@orcid.msg 'manage.developer_tools.edit.google'/></a></li>										
								</ul>								
							</div>
						</div>						
					</div>
					<div class="col-md-3 col-sm-3 col-xs-3 sso-api">				
						<ul class="sso-options pull-right">							
							<li><a href ng-click="showViewLayout()" class="back" title="<@orcid.msg 'manage.developer_tools.tooltip.back' />"><span class="glyphicon glyphicon-arrow-left"></span></a></li>
							<li><a href ng-click="addClient()" class="save" title="<@orcid.msg 'manage.developer_tools.tooltip.save' />"><span class="glyphicon glyphicon-floppy-disk"></span></a></li>							
						</ul>					
					</div>		
				</div>		
			</div>

			<!-- View credentials -->
			<div class="view-client" ng-show="viewing" ng-cloak>		
				<!-- Client name -->
				<div class="row">
					<div class="col-md-3 col-sm-3 col-xs-12">
						<span><strong><@orcid.msg 'manage.developer_tools.group.display_name'/></strong></span>
					</div>					
					<div class="col-md-7 col-sm-7 col-xs-7">
						<h4>{{clientDetails.displayName.value}}</h4>												
					</div>
					<div class="col-md-2 col-sm-2 col-xs-3">				
						<ul class="sso-options pull-right">	
							<li><a href ng-click="showViewLayout()" class="back" title="<@orcid.msg 'manage.developer_tools.tooltip.back' />"><span class="glyphicon glyphicon-arrow-left"></span></a></li>						
							<li><a href ng-click="showEditClient(clientDetails)" class="edit" title="<@orcid.msg 'manage.developer_tools.tooltip.edit' />"><span class="glyphicon glyphicon-pencil"></span></a></li>							
						</ul>					
					</div>				
				</div>			
				<div class="row">
					<!-- Website -->
					<div class="col-md-3 col-sm-3 col-xs-12">
						<span><strong><@orcid.msg 'manage.developer_tools.group.website'/></strong></span>
					</div>
					<div class="col-md-9 col-sm-9 col-xs-12 dt-website">
						<p><a href="{{clientDetails.clientWebsite.value}}">{{clientDetails.website.value}}</a></p>														
					</div>							
				</div>
				<div class="row">
					<!-- Description -->
					<div class="col-md-3 col-sm-3 col-xs-12">
						<span><strong><@orcid.msg 'manage.developer_tools.group.description'/></strong></span>
					</div>
					<div class="col-md-9 col-sm-9 col-xs-12 dt-description">
						<p>{{clientDetails.shortDescription.value}}</p>														
					</div>							
				</div>

				<div class="row">
					<span class="col-md-3 col-sm-3 col-xs-12">
						<strong><@orcid.msg 'manage.developer_tools.view.orcid'/></strong>
					</span>
					<span class="col-md-9 col-sm-9 col-xs-12">{{clientDetails.clientId.value}}</span>
				</div>
				<div class="row">
					<span class="col-md-3 col-sm-3 col-xs-12">
						<strong><@orcid.msg 'manage.developer_tools.view.secret'/></strong>
					</span>
					<span class="col-md-9 col-sm-9 col-xs-12">{{clientDetails.clientSecret.value}}</span>
				</div>

				<!-- Slidebox -->
				<div class="slidebox">
					<div class="row">
						<!-- Redirect URIS -->						
						<div  class="col-md-12 col-sm-12 col-xs-12">
							<h4><@orcid.msg 'manage.developer_tools.redirect_uri'/>:</h4>
							<select ng-model="selectedRedirectUri" ng-options="rUri.value.value for rUri in clientDetails.redirectUris | orderBy:'value.value'" ng-change="updateSelectedRedirectUri()"></select>
						</div>						
					</div>
					<div class="row">
						<div class="col-md-12 col-sm-12 col-xs-12">
							<h4><@orcid.msg 'manage.developer_tools.view.scope' />:</h4>
							<select ng-model="selectedScope" ng-options="scope as scope for scope in availableRedirectScopes" ng-change="updateSelectedRedirectUri()"></select>
						</div>
					</div>
					<!-- Examples -->
					<div ng-hide="playgroundExample != ''">																					
						<div class="row">
							<span class="col-md-3 col-sm-3 col-xs-12"><strong><@orcid.msg 'manage.developer_tools.view.example.authorize'/></strong></span>
							<span class="col-md-9 col-sm-9 col-xs-12">{{authorizeUrlBase}}</span>
						</div>
						<div class="row">
							<span class="col-md-3 col-sm-3 col-xs-12"></span>
							<span class="col-md-9 col-sm-9 col-xs-12">
								<textarea class="input-xlarge selectable authorizeURL" ng-model="authorizeURL" readonly="readonly"></textarea>
							</span>
						</div>
						<div class="row">
							<span class="col-md-3 col-sm-3 col-xs-12"><strong><@orcid.msg 'manage.developer_tools.view.example.token'/></strong></span>
							<span class="col-md-9 col-sm-9 col-xs-12">
								{{tokenURL}}<br />
								<@orcid.msg 'manage.developer_tools.view.example.curl' /><a href="<@orcid.msg 'manage.developer_tools.view.example.curl.url' />" target="curlWiki"><@orcid.msg 'manage.developer_tools.view.example.curl.text' /></a>
							</span>
						</div>
						<div class="row">
							<span class="col-md-3 col-sm-3 col-xs-12"></span>
							<span class="col-md-9 col-sm-9 col-xs-12">
								<textarea class="input-xlarge selectable authorizeURL" ng-model="sampleAuthCurl" readonly="readonly"></textarea>
							</span>
						</div>
					</div>
				</div>
			</div>			
			<!-- Slide button -->
			<div class="row slide" ng-show="viewing" ng-cloak>
				<div class="col-md-12 col-sm-12 col-xs-12">
					<div class="tab-container">
						<a href="#" class="tab collapsed" data-tab="collapsed"><span class="glyphicon glyphicon-chevron-down"></span><@orcid.msg 'manage.developer_tools.show_details' /></a>
						<a href="#" class="tab expanded"><span class="glyphicon glyphicon-chevron-up"></span><@orcid.msg 'manage.developer_tools.hide_details' /></a>
					</div>
				</div>			
			</div>
			
			
			
			
			
			
			
			
			
			
			
			
			
			
			<!-- ---------------- -->
			<!-- Edit credentials -->
			<!-- ---------------- -->
			<div class="edit-client" ng-show="editing" ng-cloak>	
				<!-- Name -->
				<div class="row">					
					<div class="col-md-12 col-sm-12 col-xs-12">
						<div class="inner-row margin-left-fix">
							<span><strong><@orcid.msg 'manage.developer_tools.group.display_name'/></strong></span>
							<input type="text" class="input-xlarge" ng-model="clientToEdit.displayName.value" placeholder="<@orcid.msg 'manage.developer_tools.group.display_name_placeholder'/>"/>
							<span class="orcid-error" ng-show="clientToEdit.displayName.errors.length > 0">
								<div ng-repeat='error in clientToEdit.displayName.errors' ng-bind-html="error"></div>
							</span>					
						</div>		
					</div>																
				</div>
				<!-- Website -->
				<div class="row">	
					<div class="col-md-12 col-sm-12 col-xs-12">
						<div class="inner-row margin-left-fix">
							<span><strong><@orcid.msg 'manage.developer_tools.group.website'/></strong></span>
							<input type="text" class="input-xlarge" ng-model="clientToEdit.website.value" placeholder="<@orcid.msg 'manage.developer_tools.group.website_placeholder'/>"/>
							<span class="orcid-error" ng-show="clientToEdit.website.errors.length > 0">
								<div ng-repeat='error in clientToEdit.website.errors' ng-bind-html="error"></div>
							</span>					
						</div>		
					</div>	
				</div>
				<!-- Description -->
				<div class="row">					
					<div class="col-md-12 col-sm-12 col-xs-12 dt-description">
						<div class="inner-row margin-left-fix">
							<span><strong><@orcid.msg 'manage.developer_tools.group.description'/></strong></span>
							<textarea class="input-xlarge selectable" ng-model="clientToEdit.shortDescription.value" placeholder="<@orcid.msg 'manage.developer_tools.group.description_placeholder'/>"></textarea>						
							<span class="orcid-error" ng-show="clientToEdit.shortDescription.errors.length > 0">
								<div ng-repeat='error in clientToEdit.shortDescription.errors' ng-bind-html="error"></div>
							</span>
						</div>															
					</div>			
				</div>
				<!-- Client secret -->
				<div class="row">
					<div class="col-md-3 col-sm-3 col-xs-4">
						<span><strong><@orcid.msg 'manage.developer_tools.view.secret'/></strong></span>
					</div>
					<div class="col-md-9 col-sm-9 col-xs-8">
						<span>{{clientToEdit.clientSecret.value}}</span>
					</div>					
				</div>	
				<!-- Reset client secret button -->
				<div class="row">
					<div class="col-md-3 col-sm-3 col-xs-4">
						<span></span>
					</div>
					<div class="col-md-9 col-sm-9 col-xs-8">
						<a href="" class="btn btn-danger" ng-click="confirmResetClientSecret()">								    		
							<@orcid.msg 'manage.developer_tools.edit.reset_client_secret' />
						</a>
					</div>
				</div>
				<!-- Redirect Uris -->				
				<div ng-repeat="rUri in clientToEdit.redirectUris" class="margin-bottom-box">
					<!-- Header -->
					<div class="row" ng-show="$first">
						<div class="col-md-12 col-sm-12 col-xs-12">
							<div class="inner-row margin-left-fix">					
								<h4><@orcid.msg 'manage.developer_tools.redirect_uri'/></h4>
							</div>
						</div>
					</div>
					<!-- Value -->
					<div class="grey-box">
						<div class="row">						
							<div class="col-md-12 col-sm-12 col-xs-12">
								<div class="inner-row margin-left-fix">							
									<input type="text" class="input-xlarge" ng-model="rUri.value.value" placeholder="<@orcid.msg 'manage.developer_tools.group.redirect_uri_placeholder'/>"/>															
									<span class="orcid-error" ng-show="rUri.value.errors.length > 0">
										<div ng-repeat='error in rUri.value.errors' ng-bind-html="error"></div>
									</span>
								</div>											
							</div>	
						</div>
						<!-- Type -->
						<div class="row">						
							<div class="col-md-12 col-sm-12 col-xs-12">
								<div class="inner-row margin-left-fix">
									<select class="input-large input-xlarge-full" ng-model="rUri.type.value" ng-change="loadDefaultScopes(rUri)">
										<#list redirectUriTypes?keys as key>
											<option value="${key}">${redirectUriTypes[key]}</option>
										</#list>
									</select>
								</div>															
							</div>							
						</div>						
						<!-- Scopes -->
						<div class="row">						
							<div class="col-md-12 col-sm-12 col-xs-12" ng-show="rUri.type.value != 'default'">
								<div class="inner-row margin-left-fix">
									<div class="scope_item" ng-repeat="scope in availableRedirectScopes">
										<div class="small-box">
											<div class="checkbox">										
												<label>
													<input type="checkbox" ng-checked="isChecked(rUri)" ng-click="setSelectedItem(rUri)"/>{{scope}}
												</label>
											</div>										
										</div>								
									</div>
								</div>
							</div>
						</div>
						<div class="row">
							<div class="col-md-12 col-sm-12 col-xs-12">
								<a href ng-click="deleteUriOnExistingClient($index)" class="glyphicon glyphicon-trash grey pull-right"></a>
							</div>
						</div>
					</div>
				</div>					
				<div class="row">
					<!-- Add redirect uris -->
					<div class="col-md-9 col-sm-9 col-xs-9 add-options">
						<a href="" class="icon-href-bg" ng-click="addUriToExistingClientTable()"><span class="glyphicon glyphicon-plus"></span><@orcid.msg 'manage.developer_tools.edit.add_redirect_uri' /></a>
						<div class="add-options margin-bottom-box">								
							<div ng-show="!hideGoogleUri">
								<h4><@orcid.msg 'manage.developer_tools.test_redirect_uris.title' /></h4>
								<ul class="pullleft-list">
									<li id="google-ruir"><a href="" class="icon-href" ng-click="addTestRedirectUri('google','true')"><span class="glyphicon glyphicon-plus"></span><@orcid.msg 'manage.developer_tools.edit.google'/></a></li>										
								</ul>								
							</div>
						</div>						
					</div>
					<div class="col-md-3 col-sm-3 col-xs-3 sso-api">				
						<ul class="sso-options pull-right">							
							<li><a href ng-click="showViewLayout()" class="back" title="<@orcid.msg 'manage.developer_tools.tooltip.back' />"><span class="glyphicon glyphicon-arrow-left"></span></a></li>
							<li><a href ng-click="editClient()" class="save" title="<@orcid.msg 'manage.developer_tools.tooltip.save' />"><span class="glyphicon glyphicon-floppy-disk"></span></a></li>							
						</ul>					
					</div>		
				</div>		
			</div>
		</div>
	</div>
</div>



<script type="text/ng-template" id="reset-client-secret-modal">
	<div class="lightbox-container">
		<div class="row">
			<div class="col-md-12 col-xs-12 col-sm-12">
				<h3><@orcid.msg 'manage.developer_tools.edit.reset_key.title' /></h3>				
				<p><strong>{{resetThisClient.clientSecret.value}}</strong></p>		
				<p><@orcid.msg 'manage.developer_tools.edit.reset_key.description' /></p>
    			<div class="btn btn-danger" ng-click="resetClientSecret()">
    				<@orcid.msg 'freemarker.btnReset' />
    			</div>
    			<a href="" ng-click="closeModal()"><@orcid.msg 'freemarker.btncancel' /></a>
			</div>
		</div>
    </div>
</script>


</@public >







