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

<script type="text/ng-template" id="new-client-modal">
	<div style="padding: 20px;">		
	</div>
</script>


<div class="row">
	<div class="span3 lhs override">
		<ul class="settings-nav">
			<li><a href="<@spring.url '/account' />#account-settings"><@orcid.msg 'manage.accountsettings'/></a></li>
			<li><a href="<@spring.url '/account' />#manage-permissions"><@orcid.msg 'manage.managepermission'/></a></li>
		</ul>
	</div>
	<div class="span9">			
		<div ng-controller="ClientEditCtrl" class="clients">
			<div id="errors" ng-show="error" class="alert" ng-cloak>
				<ul>
					<li>{{error}}</li>
				</ul>
			</div> 
			<div ng-show="!clients.length" ng-cloak>
				<span><@orcid.msg 'manage_clients.no_clients'/></span>
			</div>							
			
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
			
			<@security.authorize ifAnyGranted="ROLE_PREMIUM_INSTITUTION, ROLE_PREMIUM, ROLE_ADMIN">
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
