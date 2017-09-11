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
		<#include "/common/browser-checks.ftl" />
		<@spring.bind "loginForm" />
		<@spring.showErrors "<br/>" "error" />								
		<@orcid.checkFeatureStatus featureName='OAUTH_2SCREENS' enabled=false>
			<div class="login" ng-controller="LoginLayoutController">
			   <#if shibbolethEnabled>
			   <div class="row">
			      <div class="col-md-12">
			         <p class="title">${springMacroRequestContext.getMessage("login.signinusingyour")}</p>
			      </div>
			   </div>
			   </#if>
			   <div class="row">
			      <div class="col-md-offset-3 col-md-6">
			         <#if shibbolethEnabled>
			         <div class="btn-group btn-group-justified" role="group">
			            <a ng-click="showPersonalLogin()" class="btn btn-default" ng-class="{active: personalLogin == true}" role="button"><span class="glyphicon glyphicon-user"></span> ${springMacroRequestContext.getMessage("login.personalaccount")}</a>
			            <a ng-click="showInstitutionLogin()" class="btn btn-default" ng-class="{active: personalLogin == false}" role="button"><span class="glyphicons bank"></span> ${springMacroRequestContext.getMessage("login.institutionaccount")}</a>
			         </div>
			         </#if>				
			         <div class="row personal-login" ng-hide="personalLogin == false" ng-cloak>
			            <div class="col-md-12">
			               <div class="login-box">
			                  <!-- ORCID ACCOUNT LOGIN -->
			                  <div class="personal-account-login">
			                     <p class="title">${springMacroRequestContext.getMessage("login.signinwithyourorcidaccount")}</p>
			                     <form class="form-sign-in" id="loginForm" action="<@orcid.rootPath '/signin/auth'/>" method="post">
			                        <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
			                        <div class="form-group">
			                           <label for="userId" class="control-label">${springMacroRequestContext.getMessage("login.username")}</label>				                                   
			                           <input type="text" id="userId" name="userId" ng-model="userId" value="" class="form-control" placeholder="${springMacroRequestContext.getMessage("login.username")}">				                    
			                        </div>
			                        <div class="form-group">
			                           <label for="password" class="control-label">${springMacroRequestContext.getMessage("login.password")}</label>					                    
			                           <input type="password" id="password" name="password" value="" class="form-control" placeholder="${springMacroRequestContext.getMessage("login.password")}">					                    					                    
			                        </div>
			                        <div class="form-group" id="verificationCodeFor2FA">
                                        ${springMacroRequestContext.getMessage("orcid.frontend.security.2fa.heading")}
                                        <label for="verificationCode" class="control-label">${springMacroRequestContext.getMessage("orcid.frontend.security.2fa.label")}</label>                                       
                                        <input id="verificationCode" name="verificationCode" value="" class="form-control" placeholder="${springMacroRequestContext.getMessage("orcid.frontend.security.2fa.label")}">     
                                    </div>
			                        <div class="form-group">
			                           <button id='form-sign-in-button' class="btn btn-primary" type="submit" class="form-control">${springMacroRequestContext.getMessage("login.signin")}</button>					                    
			                           <span id="ajax-loader" class="no-visible"><i id="ajax-loader-icon" class="glyphicon glyphicon-refresh spin x2 green"></i></span>					                    
			                           <#if (RequestParameters['alreadyClaimed'])??>
			                           <div class="alert"><@spring.message "orcid.frontend.security.already_claimed"/></div>
			                           </#if>   
			                           <#if (RequestParameters['invalidClaimUrl'])??>
			                           <div class="alert"><@spring.message "orcid.frontend.security.invalid_claim_url"/></div>
			                           </#if>
			                           <div id="2FAInstructions" style="display:none">
                                            <p>${springMacroRequestContext.getMessage("orcid.frontend.security.2fa.instructions")}</p>
                                            <p>${springMacroRequestContext.getMessage("orcid.frontend.security.2fa.no_device")}</p>
										</div>
										<div id="recoveryCodeSignin" class="form-group" style="display:none">
										    <label for="recoveryCode" class="control-label">${springMacroRequestContext.getMessage("orcid.frontend.security.2fa.recoveryCode")}</label>                                       
										    <input id="recoveryCode" name="recoveryCode" value="" class="form-control" placeholder="${springMacroRequestContext.getMessage("orcid.frontend.security.2fa.recoveryCode")}">        
										    <button class="btn btn-primary" type="submit" class="form-control">${springMacroRequestContext.getMessage("orcid.frontend.security.2fa.authenticate")}</button>                                       
                                        </div>
			                        </div>
			                        <div id="login-deactivated-error" class="orcid-error" style="display:none">
			                           <span ng-show="showDeactivatedError">
			                           ${springMacroRequestContext.getMessage("orcid.frontend.verify.deactivated_email.1")}<a href="" ng-click="sendReactivationEmail()">${springMacroRequestContext.getMessage("orcid.frontend.verify.deactivated_email.2")}</a>${springMacroRequestContext.getMessage("orcid.frontend.verify.deactivated_email.3")}
			                           </span>
			                           <span ng-show="showReactivationSent">
			                           ${springMacroRequestContext.getMessage("orcid.frontend.verify.reactivation_sent.1")}<a href="mailto:support@orcid.org">${springMacroRequestContext.getMessage("orcid.frontend.verify.reactivation_sent.2")}</a>${springMacroRequestContext.getMessage("orcid.frontend.verify.reactivation_sent.3")}
			                           </span>
			                        </div>
			                     </form>
			                  </div>
			                  <!-- RESET PASSWORD -->
			                  <div ng-controller="RequestPasswordResetCtrl" id="RequestPasswordResetCtr" ng-init="getRequestResetPassword()" class="reset-password">
			                     <a name="resetPassword"></a>
			                     <a href="" id="reset-password-toggle-text" ng-click="toggleResetPassword()" ng-bind="resetPasswordToggleText"></a>
			                     <div ng-show="showResetPassword" ng-cloak>
			                        <p><small>${springMacroRequestContext.getMessage("reset_password.enterEmail")} <a href="mailto:support@orcid.org">${springMacroRequestContext.getMessage("resend_claim.labelorg")}</a>.</small></p>
			                        <form id="password-reset-form" name="emailAddressForm">
			                           <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
			                           <span class="orcid-error" ng-show="requestResetPassword.errors.length > 0">
			                              <div ng-repeat='error in requestResetPassword.errors' ng-bind-html="error"></div>
			                           </span>
			                           <div class="alert alert-success" ng-show="requestResetPassword.successMessage != null">
			                              <strong><span ng-bind="requestResetPassword.successMessage"></span></strong>
			                           </div>
			                           <div class="control-group">
			                              <label for="email" class="control-label">${springMacroRequestContext.getMessage("manage_bio_settings.h3email")} </label>			           
			                              <div class="controls"> 
			                                 <input id="email" type="text" class="form-control" ng-model="requestResetPassword.email" />
			                              </div>
			                              <button class="btn btn-primary" ng-click="postPasswordResetRequest()">${springMacroRequestContext.getMessage("reset_password.sendResetLink")}</button>
			                           </div>
			                        </form>
			                     </div>
			                  </div>
			                  <!-- SOCIAL LOGIN -->					            
			                  <div class="social-login">
			                     <div class="title">
			                        ${springMacroRequestContext.getMessage("login.signinwithasocialaccount")}
			                        <div class="popover-help-container">
			                           <a href="javascript:void(0);"><i class="glyphicon glyphicon-question-sign"></i></a>
			                           <div id="social-login-help" class="popover bottom">
			                              <div class="arrow"></div>
			                              <div class="popover-content">
			                                 <p><@orcid.msg 'login.signinwithasocialaccount.help.1'/><a href="${knowledgeBaseUri}/articles/892920" target="login.signinwithasocialaccount.help.2"><@orcid.msg 'login.signinwithasocialaccount.help.2'/></a><@orcid.msg 'login.signinwithasocialaccount.help.3'/></p>
			                              </div>
			                           </div>
			                        </div>
			                     </div>
			                     <ul class="social-icons">
			                        <li>
			                           <form action="<@orcid.rootPath '/signin/facebook'/>" method="POST" ng-submit="loginSocial('facebook')">
			                              <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
			                              <button type="submit" class="btn btn-social-icon btn-facebook"></button>
			                              <input type="hidden" name="scope" value="email" />
			                              <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
			                           </form>
			                        </li>
			                        <!-- 
			                           <li>
			                               <form action="<@orcid.rootPath '/signin/twitter'/>" method="POST" ng-submit="loginSocial('twitter')">
			                                   <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
			                                   <button type="submit" class="btn btn-social-icon btn-twitter"></button>
			                                   <input type="hidden" name="scope" value="email" />
			                                   <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
			                               </form>
			                           </li>
			                            -->
			                        <li>
			                           <form action="<@orcid.rootPath '/signin/google'/>" method="POST" ng-submit="loginSocial('google')">
			                              <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
			                              <button type="submit" class="btn btn-social-icon btn-google"></button>
			                              <input type="hidden" name="scope" value="email" />
			                              <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
			                           </form>
			                        </li>
			                     </ul>
			                  </div>
			               </div>
			            </div>
			         </div>
			         <!-- SHIBBOLETH -->
			         <div class="row institution-login" ng-show="personalLogin == false"  ng-cloak>
			            <div class="col-md-12">
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
			                  </div>
			               </div>
			            </div>
			         </div>
			         <div class="row">
			            <div class="title">
			               ${springMacroRequestContext.getMessage("social.link.dont_have_orcid")} <a class="reg" href="<@orcid.rootPath '/register'/>">${springMacroRequestContext.getMessage("login.register")}</a>
			            </div>
			         </div>
			      </div>
			   </div>
			   <div class="col-md-3"></div>
			</div>
		</@orcid.checkFeatureStatus>
		<@orcid.checkFeatureStatus 'OAUTH_2SCREENS'>
			<div class="col-md-6 col-md-offset-3" ng-controller="OauthAuthorizationController">
				<div class="login">			
					<p class="title" ng-show="!showRegisterForm" ng-cloak>${springMacroRequestContext.getMessage("login.signin")} ${springMacroRequestContext.getMessage("login.or")} <a href="#" id="switch-to-register-form" ng-click="switchForm()">${springMacroRequestContext.getMessage("login.register")}</a></p>
					<p class="title" ng-show="showRegisterForm" ng-cloak>Already have an ORCID iD? <a href="#" id = "switch-to-login-form" ng-click="switchForm()">Sign In</a></p>
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
										    <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
										    <div class="form-group">
										        <label for="userId" class="control-label">${springMacroRequestContext.getMessage("login.username")}</label>				  					<input type="text" id="userId" name="userId" ng-model="authorizationForm.userName.value" value="" class="form-control" placeholder="${springMacroRequestContext.getMessage("login.username")}">
										    </div>
										    <div class="form-group">
										        <label for="password" class="control-label">${springMacroRequestContext.getMessage("login.password")}</label>					
										        <input type="password" id="password" name="password" value="" class="form-control" placeholder="${springMacroRequestContext.getMessage("login.password")}">
										    </div>
										    <div class="form-group" id="verificationCodeFor2FA">
		                                        ${springMacroRequestContext.getMessage("orcid.frontend.security.2fa.heading")}
		                                        <label for="verificationCode" class="control-label">${springMacroRequestContext.getMessage("orcid.frontend.security.2fa.label")}</label>                                       
		                                        <input id="verificationCode" name="verificationCode" value="" class="form-control" placeholder="${springMacroRequestContext.getMessage("orcid.frontend.security.2fa.label")}">     
		                                    </div>
										    <div class="form-group">
										        <button id='form-sign-in-button' class="btn btn-primary" type="submit" class="form-control">${springMacroRequestContext.getMessage("login.signin")}</button>					                    
										        <span id="ajax-loader" class="no-visible"><i id="ajax-loader-icon" class="glyphicon glyphicon-refresh spin x2 green"></i></span>					                    
										        <#if (RequestParameters['alreadyClaimed'])??>
										        <div class="alert"><@spring.message "orcid.frontend.security.already_claimed"/></div>
										        </#if>   
										        <#if (RequestParameters['invalidClaimUrl'])??>
										        <div class="alert"><@spring.message "orcid.frontend.security.invalid_claim_url"/></div>
										        </#if>
										        <div id="2FAInstructions" style="display:none">
		                                            <p>${springMacroRequestContext.getMessage("orcid.frontend.security.2fa.instructions")}</p>
		                                            <p>${springMacroRequestContext.getMessage("orcid.frontend.security.2fa.no_device")}</p>
												</div>
												<div id="recoveryCodeSignin" class="form-group" style="display:none">
												    <label for="recoveryCode" class="control-label">${springMacroRequestContext.getMessage("orcid.frontend.security.2fa.recoveryCode")}</label>                                       
												    <input id="recoveryCode" name="recoveryCode" value="" class="form-control" placeholder="${springMacroRequestContext.getMessage("orcid.frontend.security.2fa.recoveryCode")}">        
												    <button class="btn btn-primary" type="submit" class="form-control">${springMacroRequestContext.getMessage("orcid.frontend.security.2fa.authenticate")}</button>                                    
                                        		</div>
										    </div>
										    <div id="login-deactivated-error" class="orcid-error" style="display:none">
										        <span ng-show="showDeactivatedError">
										        ${springMacroRequestContext.getMessage("orcid.frontend.verify.deactivated_email.1")}<a href="" ng-click="sendReactivationEmail()">${springMacroRequestContext.getMessage("orcid.frontend.verify.deactivated_email.2")}</a>${springMacroRequestContext.getMessage("orcid.frontend.verify.deactivated_email.3")}
										        </span>
										        <span ng-show="showReactivationSent">
										        ${springMacroRequestContext.getMessage("orcid.frontend.verify.reactivation_sent.1")}<a href="mailto:support@orcid.org">${springMacroRequestContext.getMessage("orcid.frontend.verify.reactivation_sent.2")}</a>${springMacroRequestContext.getMessage("orcid.frontend.verify.reactivation_sent.3")}
										        </span>
										    </div>
									  	</form>
									</div>
									<!-- RESET PASSWORD -->
									<div ng-controller="RequestPasswordResetCtrl" id="RequestPasswordResetCtr" ng-init="getRequestResetPassword()" class="reset-password">
										<a name="resetPassword"></a>
										<a href="" id="reset-password-toggle-text" ng-click="toggleResetPassword()" ng-bind="resetPasswordToggleText"></a>
										<div ng-show="showResetPassword" ng-cloak>
										 	<p><small>${springMacroRequestContext.getMessage("reset_password.enterEmail")} <a href="mailto:support@orcid.org">${springMacroRequestContext.getMessage("resend_claim.labelorg")}</a>.</small></p>
											<form id="password-reset-form" name="emailAddressForm">
											    <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
												<span class="orcid-error" ng-show="requestResetPassword.errors.length > 0">
												   <div ng-repeat='error in requestResetPassword.errors' ng-bind-html="error"></div>
												</span>
												<div class="alert alert-success" ng-show="requestResetPassword.successMessage != null">
												   	<strong><span ng-bind="requestResetPassword.successMessage"></span></strong>
												</div>
												<div class="control-group">
													<label for="email" class="control-label">${springMacroRequestContext.getMessage("manage_bio_settings.h3email")} </label>			           
													<div class="controls"> 
														<input id="email" type="text" class="form-control" ng-model="requestResetPassword.email" />
													</div>
													<button class="btn btn-primary" ng-click="postPasswordResetRequest()">${springMacroRequestContext.getMessage("reset_password.sendResetLink")}</button>
												</div>
											</form>
									  	</div>
									</div>
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
					<div class="personal-account-login" ng-show="personalLogin == true && showRegisterForm" ng-init="oauth2ScreensLoadRegistrationForm()" ng-cloak>
						<div id="register" class="oauth-registration">
							<p>${springMacroRequestContext.getMessage("register.labelClause")}</p>
							<!-- First name -->
							<div class="form-group clear-fix">
								<label for="givelNames" class="control-label"><@orcid.msg 'oauth_sign_up.labelfirstname'/></label>
								<div class="bottomBuffer">
									<input id="register-form-given-names" name="givenNames" type="text" tabindex="1" class="" ng-model="registrationForm.givenNames.value" ng-model-onblur ng-change="serverValidate('GivenNames')"/>                         
									<span class="required" ng-class="isValidClass(registrationForm.givenNames)">*</span>            
									<div class="popover-help-container">
										<a href="javascript:void(0);"><i class="glyphicon glyphicon-question-sign"></i></a>
										<div id="name-help" class="popover bottom">
											<div class="arrow"></div>
											<div class="popover-content">
												<p><@orcid.msg 'orcid.frontend.register.help.first_name'/></p>
												<p><@orcid.msg 'orcid.frontend.register.help.last_name'/></p>
												<p><@orcid.msg 'orcid.frontend.register.help.update_names'/></p>
												<a href="<@orcid.msg 'orcid.frontend.register.help.more_info.link.url'/>" target="orcid.frontend.register.help.more_info.link.text"><@orcid.msg 'orcid.frontend.register.help.more_info.link.text'/></a>
											</div>
										</div>
									</div>
									<span class="orcid-error" ng-show="registrationForm.givenNames.errors.length > 0">
										<div ng-repeat='error in registrationForm.givenNames.errors' ng-bind-html="error"></div>
									</span>
								</div>
							</div>
							<!-- Last name -->
							<div class="form-group clear-fix">
								<label class="control-label"><@orcid.msg 'oauth_sign_up.labellastname'/></label>
								<div class="bottomBuffer">
									<input id="register-form-family-name" name="familyNames" type="text" tabindex="2" class=""  ng-model="registrationForm.familyNames.value" ng-model-onblur/>
									<span class="orcid-error" ng-show="registrationForm.familyNames.errors.length > 0">
										<div ng-repeat='error in registrationForm.familyNames.errors' ng-bind-html="error"></div>
									</span>
								</div>
							</div>
							<!-- Email -->                  
							<div class="form-group clear-fix">
								<label class="control-label"><@orcid.msg 'oauth_sign_up.labelemail'/></label>
								<div class="bottomBuffer">
									<input id="register-form-email" name="email" type="email" tabindex="3" class="" ng-model="registrationForm.email.value" ng-model-onblur ng-change="serverValidate('Email')" />
									<span class="required" ng-class="isValidClass(registrationForm.email)">*</span> <span class="orcid-error" ng-show="emailTrustAsHtmlErrors.length > 0 && !showDeactivatedError && !showReactivationSent">
										<div ng-repeat='error in emailTrustAsHtmlErrors' ng-bind-html="error" compile="html"></div>
									</span>
									<span class="orcid-error" ng-show="showDeactivatedError" ng-cloak>
										${springMacroRequestContext.getMessage("orcid.frontend.verify.deactivated_email.1")}<a href="" ng-click="sendReactivationEmail(registrationForm.email.value)">${springMacroRequestContext.getMessage("orcid.frontend.verify.deactivated_email.2")}</a>${springMacroRequestContext.getMessage("orcid.frontend.verify.deactivated_email.3")}
									</span>
									<span class="orcid-error" ng-show="showReactivationSent" ng-cloak>
										${springMacroRequestContext.getMessage("orcid.frontend.verify.reactivation_sent.1")}<a href="mailto:support@orcid.org">${springMacroRequestContext.getMessage("orcid.frontend.verify.reactivation_sent.2")}</a>${springMacroRequestContext.getMessage("orcid.frontend.verify.reactivation_sent.3")}
									</span>	                            	            
								</div>
							</div>
							<!--Re-enter email-->
							<div class="form-group clear-fix">
								<label class="control-label"><@orcid.msg 'oauth_sign_up.labelreenteremail'/></label>
								<div class="bottomBuffer">
									<input id="register-form-confirm-email" name="confirmedEmail" type="email" tabindex="4" class="" ng-model="registrationForm.emailConfirm.value" ng-model-onblur ng-change="serverValidate('EmailConfirm')" />
									<span class="required" ng-class="isValidClass(registrationForm.emailConfirm)">*</span>                  
									<span class="orcid-error" ng-show="registrationForm.emailConfirm.errors.length > 0 && !showDeactivatedError && !showReactivationSent">
										<div ng-repeat='error in registrationForm.emailConfirm.errors' ng-bind-html="error"></div>
									</span>
								</div>
							</div>
							<!--Password-->
							<div class="form-group clear-fix">
								<label class="control-label"><@orcid.msg 'oauth_sign_up.labelpassword'/></label>
								<div class="bottomBuffer">
									<input id="register-form-password" type="password" name="password" tabindex="5" class="" ng-model="registrationForm.password.value" ng-change="serverValidate('Password')"/>
									<span class="required" ng-class="isValidClass(registrationForm.password)">*</span>
									<@orcid.passwordHelpPopup />
									<span class="orcid-error" ng-show="registrationForm.password.errors.length > 0">
										<div ng-repeat='error in registrationForm.password.errors' ng-bind-html="error"></div>
									</span>
								</div>
							</div>
							<!--Confirm password-->
							<div class="form-group clear-fix">
								<label class="control-label"><@orcid.msg 'password_one_time_reset.labelconfirmpassword'/></label>
								<div class="bottomBuffer">
									<input id="register-form-confirm-password" type="password" name="confirmPassword" tabindex="6" class="" ng-model="registrationForm.passwordConfirm.value" ng-change="serverValidate('PasswordConfirm')"/>
									<span class="required" ng-class="isValidClass(registrationForm.passwordConfirm)">*</span>                 
									<span class="orcid-error" ng-show="registrationForm.passwordConfirm.errors.length > 0">
										<div ng-repeat='error in registrationForm.passwordConfirm.errors' ng-bind-html="error"></div>
									</span>
								</div>
							</div>
							<!--Visibility default-->
							<div class="form-group clear-fix">
								<div class="oauth-privacy">                      
									<label class="privacy-toggle-lbl">${springMacroRequestContext.getMessage("privacy_preferences.activitiesVisibilityDefault")}</label> 
									<label class="privacy-toggle-lbl">${springMacroRequestContext.getMessage("privacy_preferences.activitiesVisibilityDefault.who_can_see_this")}</label>
									<@orcid.privacyToggle 
										angularModel="registrationForm.activitiesVisibilityDefault.visibility" 
										questionClick="toggleClickPrivacyHelp('workPrivHelp')"
										clickedClassCheck="{'popover-help-container-show':privacyHelp['workPrivHelp']==true}" 
										publicClick="updateActivitiesVisibilityDefault('PUBLIC', $event)"
										limitedClick="updateActivitiesVisibilityDefault('LIMITED', $event)"
										privateClick="updateActivitiesVisibilityDefault('PRIVATE', $event)" />
								</div>
							</div>
							<!--Email frequency-->
							<div>
								<div class="relative">              
									<@orcid.registrationEmailFrequencySelector angularElementName="registrationForm" />
								</div>
							</div>
							<!--Recaptcha-->
							<div>
								<div class="bottomBuffer relative recaptcha"  id="recaptcha">
									<div vc-recaptcha
									theme="'light'"
									key="model.key"
									on-create="setRecaptchaWidgetId(widgetId)"
									on-success="setRecatchaResponse(response)"></div>
										<span class="orcid-error" ng-show="registrationForm.grecaptcha.errors.length > 0">
											<div ng-repeat='error in registrationForm.grecaptcha.errors track by $index' ng-bind-html="error"></div>
										</span>
								</div>
							</div>
							<!--Terms and conditions-->
							<div class="bottomBuffer">
								<label for="termsConditions">
									<@orcid.msg 'register.labelTermsofUse'/>
									<span class="required"  ng-class="{'text-error':register.termsOfUse.value == false}">*</span>
								</label>
								<p>
									<input id="register-form-term-box" type="checkbox" name="termsConditions" tabindex="9" name="acceptTermsAndConditions" ng-model="registrationForm.termsOfUse.value" ng-change="serverValidate('TermsOfUse')" />
									<@orcid.msg 'register.labelconsent'/> <a href="${aboutUri}/footer/privacy-policy" target="register.labelprivacypolicy"><@orcid.msg 'register.labelprivacypolicy'/></a>&nbsp;<@orcid.msg 'register.labeland'/>&nbsp;<@orcid.msg 'common.termsandconditions1'/><a href="${aboutUri}/content/orcid-terms-use" target="common.termsandconditions2"><@orcid.msg 'common.termsandconditions2'/></a>&nbsp;<@orcid.msg 'common.termsandconditions3'/>
								</p>
								<span class="orcid-error" ng-show="registrationForm.termsOfUse.errors.length > 0">
									<div ng-repeat='error in registrationForm.termsOfUse.errors' ng-bind-html="error"></div>
								</span>
							</div>
							<!--Registration error-->
							<div style="margin-bottom: 15px;" ng-show="generalRegistrationError != null">
								<span class="orcid-error" ng-bind-html="generalRegistrationError"></span>
							</div>
							<!-- Buttons  -->
							<div class="bottomBuffer col-xs-12 col-sm-3">
								<button id="register-authorize-button" class="btn btn-primary" name="authorize" value="<@orcid.msg 'confirm-oauth-access.Authorize'/>" ng-click="oauth2ScreensRegister()">
									<@orcid.msg 'header.register' />
								</button>   
							</div><!--buttons-->
						</div><!--register-->
					</div><!--personal-account-login-->
			   		<!-- END -->
			   	</div><!--login-->
			</div><!--col-md-offset-3-->
			<div class="col-md-3"></div>
			<script type="text/ng-template" id="duplicates">
			    <div class="lightbox-container" id="duplicates-records">
			        <div class="row margin-top-box">      
			            <div class="col-md-6 col-sm-6 col-xs-12">
			                <h4><@orcid.msg 'duplicate_researcher.wefoundfollowingrecords'/>
			                    <@orcid.msg 'duplicate_researcher.to_access.1'/><a href="<@orcid.rootPath "/signin" />" target="signin"><@orcid.msg 'duplicate_researcher.to_access.2'/></a><@orcid.msg 'duplicate_researcher.to_access.3'/>
			                </h4>
			            </div>
			            <div class="col-md-6 col-sm-6 col-xs-12 right margin-top-box">
			                <button class="btn btn-primary" ng-click="oauth2ScreensPostRegisterConfirm()"><@orcid.msg 'duplicate_researcher.btncontinuetoregistration'/></button>
			            </div>
			        </div>        
			        <div class="row">
			            <div class="col-sm-12">
			                <div class="table-container">
			                    <table class="table">
			                        <thead>
			                            <tr>                      
			                                <th><@orcid.msg 'search_results.thORCIDID'/></th>
			                                <th><@orcid.msg 'duplicate_researcher.thEmail'/></th>
			                                <th><@orcid.msg 'duplicate_researcher.thgivennames'/></th>
			                                <th><@orcid.msg 'duplicate_researcher.thFamilyName'/></th>
			                                <th><@orcid.msg 'duplicate_researcher.thInstitution'/></th>                       
			                            </tr>
			                        </thead>
			                        <tbody>
			                            <tr ng-repeat='dup in duplicates'>
			                                <td><a href="<@orcid.rootPath '/'/>{{dup.orcid}}" target="dup.orcid">{{dup.orcid}}</a></td>
			                                <td>{{dup.email}}</td>
			                                <td>{{dup.givenNames}}</td>
			                                <td>{{dup.familyNames}}</td>
			                                <td>{{dup.institution}}</td>
			                            </tr>
			                        </tbody>
			                    </table>
			                </div>
			            </div>
			        </div>  
			        <div class="row margin-top-box">
			            <div class="col-md-12 col-sm-12 col-xs-12 right">
			                <button class="btn btn-primary" ng-click="oauth2ScreensPostRegisterConfirm()"><@orcid.msg 'duplicate_researcher.btncontinuetoregistration'/></button>
			            </div>
			        </div>
			    </div>
			</script> 			
		</@orcid.checkFeatureStatus>											
	</div><!--row-->
</@public>