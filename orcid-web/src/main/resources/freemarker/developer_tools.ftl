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
	<div class="row">
		<div class="col-md-3 lhs col-sm-12 col-xs-12 padding-fix">
			<#include "includes/id_banner.ftl"/>
		</div>
		<div class="col-md-9 col-sm-12 col-xs-12 developer-tools" ng-controller="SSOPreferencesCtrl">		
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
								<p><@orcid.msg 'developer_tools.client_types.description.oauth2' /></p>
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
									<li><a href="${knowledgeBaseUri}/articles/343182" target="developer_tools.public_member.read_more"><@orcid.msg 'developer_tools.public_member.read_more' /></a></li>
								</ul>
							</div>
						</div>													
					</div>
				</#if>										
			<#else>		
				Working on ...
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
					<span><@orcid.msg 'developer_tools.public_member.terms.description' /></span>				
				</div>				
			</div> 		
			<div class="row">
				<div class="col-md-8 col-xs-8 col-sm-12">
					<div class="row">
						<span class="col-md-1 col-xs-1 col-sm-1 vertical-align-middle"><input type="checkbox" name="accepted" ng-model="accepted" /></span>	
						<span class="col-md-11 col-xs-11 col-sm-11"><@orcid.msg 'developer_tools.public_member.terms.check' /></span>
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
