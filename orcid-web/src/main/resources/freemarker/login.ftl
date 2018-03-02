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
<@public classes=['home'] nav="signin">
	<div class="row">
		<#include "sandbox_warning.ftl"/>
		<!--<#include "/common/browser-checks.ftl" />-->
		<@spring.bind "loginForm" />
		<@spring.showErrors "<br/>" "error" />
		<@orcid.checkFeatureStatus 'ANGULAR2_QA'>
			<#include "/includes/ng2_templates/modal-ng2-template.ftl">
			<#include "/includes/ng2_templates/register-duplicates-ng2-template.ftl">
			
            <oauth-authorization-ng2></oauth-authorization-ng2>

            <modalngcomponent elementHeight="400" elementId="modalRegisterDuplicates" elementWidth="780">
			    <register-duplicates-ng2></register-duplicates-ng2>
			</modalngcomponent><!-- Ng2 component --> 

        </@orcid.checkFeatureStatus>
		<@orcid.checkFeatureStatus featureName='ANGULAR1_LEGACY' enabled=false>
			<div class="col-md-6 col-md-offset-3" ng-controller="OauthAuthorizationController">
				<div class="login">			
					<p class="title" ng-show="!showRegisterForm" ng-cloak>${springMacroRequestContext.getMessage("login.signin")} ${springMacroRequestContext.getMessage("login.or")} <a href="javascript:void(0);" id="switch-to-register-form" ng-click="switchForm()">${springMacroRequestContext.getMessage("login.register")}</a></p>
					<p class="title" ng-show="showRegisterForm" ng-cloak>Already have an ORCID iD? <a href="javascript:void(0);" id = "switch-to-login-form" ng-click="switchForm()">Sign In</a></p>
					<div ng-show="!showRegisterForm">
						<div class="personal-login" ng-cloak>
							<#if shibbolethEnabled>
								<div class="btn-group btn-group-justified" role="group">
									<a ng-click="showPersonalLogin()" class="btn btn-default" ng-class="{active: personalLogin == true}" role="button"><span class="glyphicon glyphicon-user"></span> ${springMacroRequestContext.getMessage("login.personalaccount")}</a>
									<a ng-click="showInstitutionLogin()" class="btn btn-default" ng-class="{active: personalLogin == false}" role="button"><span class="glyphicons bank"></span> ${springMacroRequestContext.getMessage("login.institutionaccount")}</a>
								</div>
							</#if>	
							<div ng-show="personalLogin == true">
								<div class="login-box">
									<!-- ORCID ACCOUNT LOGIN -->
									<div class="personal-account-login">
										<p class="title">${springMacroRequestContext.getMessage("login.signinwithyourorcidaccount")}</p>
										<form class="form-sign-in" id="loginForm" action="<@orcid.rootPath '/signin/auth'/>" method="post">
										    <#include "/includes/login_personal_fields_inc.ftl"/>
									  	</form>
									</div>
									<!-- RESET PASSWORD -->
									<#include "/includes/login_reset_password_inc.ftl"/>
							       	<!-- SOCIAL LOGIN -->					            
							       	<div class="social-login">
										<div class="title">
										 	${springMacroRequestContext.getMessage("login.signinwithasocialaccount")}
											<div class="popover-help-container">
												<a href="javascript:void(0);"><i class="glyphicon glyphicon-question-sign"></i></a>
												<div id="social-login-help" class="popover bottom">
													<div class="arrow"></div>
													<div class="popover-content">
														<p><@orcid.msg 'login.signinwithasocialaccount.help.1'/><a href="${knowledgeBaseUri}/articles/892920" target="login.signinviayourinstitution.help.2"><@orcid.msg 'login.signinwithasocialaccount.help.2'/></a><@orcid.msg 'login.signinwithasocialaccount.help.3'/></p>
													</div>
												</div>
											</div>
										</div>
										<ul class="social-icons">
											<!--FACEBOOK-->
											<li>
												<form action="<@orcid.rootPath '/signin/facebook'/>" method="POST" ng-submit="loginSocial('facebook')">
													<input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
													<button type="submit" class="btn btn-social-icon btn-facebook"></button>
													<input type="hidden" name="scope" value="email" />
													<input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
												</form>
											</li>
											<!--TWITTER (NOT USED)
											<li>
												<form action="<@orcid.rootPath '/signin/twitter'/>" method="POST" ng-submit="loginSocial('twitter')">
													<input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
													<button type="submit" class="btn btn-social-icon btn-twitter"></button>
													<input type="hidden" name="scope" value="email" />
													<input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
												</form>
											</li>
											-->
											<!--GOOGLE-->
											<li>
												<form action="<@orcid.rootPath '/signin/google'/>" method="POST" ng-submit="loginSocial('google')">
													<input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
													<button type="submit" class="btn btn-social-icon btn-google"></button>
													<input type="hidden" name="scope" value="email" />
													<input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
												</form>
											</li>
										</ul>
									</div><!--social login-->
								</div><!--login box-->
							</div><!--ng-show personal login-->
						</div><!--personal login-->			      
						<!-- SHIBBOLETH -->
						<div class="institution-login" ng-show="personalLogin == false"  ng-cloak>
							<div class="login-box">
								<div class="institution-login">
									<div class="title">
									${springMacroRequestContext.getMessage('login.signinviayourinstitution')}
										<div class="popover-help-container">
											<a href="javascript:void(0);"><i class="glyphicon glyphicon-question-sign"></i></a>
											<div id="institution-login-help" class="popover bottom">
												<div class="arrow"></div>
												<div class="popover-content">
													<p><@orcid.msg 'login.signinviayourinstitution.help.1'/><a href="${knowledgeBaseUri}/articles/892920" target="login.signinviayourinstitution.help.2"><@orcid.msg 'login.signinviayourinstitution.help.2'/></a><@orcid.msg 'login.signinviayourinstitution.help.3'/></p>
												</div>
											</div>
										</div>
									</div>
									<div id="idpSelectContainer">
										<div id="idpSelectInner">
											<div ng-show="scriptsInjected == false;" class="text-center" ng-cloak>
												<i class="glyphicon glyphicon-refresh spin x4 green" id="spinner"></i>
											</div>
											<!-- Where the widget is going to be injected -->
											<div id="idpSelect"></div>
										</div>
									</div>
								</div><!--institution login-->
							</div><!--login box-->
						</div><!--institution login-->
					</div><!--ng show !registion form-->
				   	<!-- REGISTRATION FORM-->
					<div class="personal-account-login" id="RegistrationForm" ng-show="personalLogin == true && showRegisterForm" ng-init="oauth2ScreensLoadRegistrationForm('', '', '', '')" ng-cloak>
						<p>
						${springMacroRequestContext.getMessage("register.labelClause_1")}
						<a href="http://orcid.org/content/orcid-terms-use" target="terms_and_conditions">
						${springMacroRequestContext.getMessage("register.labelClause_2")}
						</a>
						${springMacroRequestContext.getMessage("register.labelClause_3")}
						</p>
						<#include "/includes/register_inc.ftl" />
					</div><!--personal-account-login-->
			   		<!-- END -->
			   	</div><!--login-->
			</div><!--col-md-offset-3-->
		</@orcid.checkFeatureStatus>
		<div class="col-md-3"></div>
	</div><!--row-->
</@public>