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
	
	<#include "sandbox_warning.ftl"/>
	<#include "/common/browser-checks.ftl" />
	<@spring.bind "loginForm" />
	<@spring.showErrors "<br/>" "error" />
	
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
					                    <input type="text" id="userId" name="userId" ng-model="userId" ng-change="loginUserIdInputChanged()" value="" class="form-control" placeholder="${springMacroRequestContext.getMessage("login.username")}">				                    
					                </div>
					                
					                <div class="form-group">
					                    <label for="password" class="control-label">${springMacroRequestContext.getMessage("login.password")}</label>					                    
					                    <input type="password" id="password" name="password" value="" class="form-control" placeholder="${springMacroRequestContext.getMessage("login.password")}">					                    					                    
					                    <#if !RequestParameters['ResetPassword']??>
						                    <div id="login-reset">
						                        <a href="<@orcid.rootPath '/reset-password'/>">${springMacroRequestContext.getMessage("login.reset")}</a>
						                    </div>
					                    </#if>
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
							<#if RequestParameters['ResetPassword']??>
				            	<div ng-controller="RequestPasswordResetCtrl" id="RequestPasswordResetCtr" ng-init="getRequestResetPassword()" class="reset-password">
				                	<a name="resetPassword"></a>
				                	<a href="" id="reset-password-toggle-text" ng-click="toggleResetPassword()" ng-bind="resetPasswordToggleText"></a>
				                	<div ng-show="showResetPassword" ng-cloak>
							            <p><small>${springMacroRequestContext.getMessage("reset_password.enterEmail")} <a href="mailto:support@orcid.org">${springMacroRequestContext.getMessage("resend_claim.labelorg")}</a>.</small></p>      		
							        	<form id="password-reset-form" name="emailAddressForm">
						        			<span class="orcid-error" ng-show="requestResetPassword.errors.length > 0">
									            <div ng-repeat='error in requestResetPassword.errors' ng-bind-html="error"></div>
									        </span>
									        <div class="alert alert-success" ng-show="requestResetPassword.successMessage != null">
									        	<strong><span ng-bind="requestResetPassword.successMessage"></span></strong>
									        </div>
						        			<div class="control-group">
						            			<label for="email" class="control-label">${springMacroRequestContext.getMessage("manage_bio_settings.h3email")} </label><span class="required">*</span>				           
						               			<div class="controls"> 
						               				<input id="email" type="text" class="form-control" ng-model="requestResetPassword.email" ng-change="validateRequestPasswordReset()" />
						               			</div>
						               			<button class="btn btn-primary" ng-click="postPasswordResetRequest()">${springMacroRequestContext.getMessage("reset_password.sendResetLink")}</button>
						        			</div>
							        	</form>
						        	</div>
								 </div>   
							</#if>
				            <!-- SOCIAL LOGIN -->					            
			                <div class="social-login">
			                    <div class="title">
			                        ${springMacroRequestContext.getMessage("login.signinwithasocialaccount")}
			                        <div class="popover-help-container">
                                        <a href="javascript:void(0);"><i class="glyphicon glyphicon-question-sign"></i></a>
                                        <div id="social-login-help" class="popover bottom">
                                            <div class="arrow"></div>
                                            <div class="popover-content">
                                                <p><@orcid.msg 'login.signinwithasocialaccount.help.1'/><a href="${knowledgeBaseUri}/articles/892920" target="_blank"><@orcid.msg 'login.signinwithasocialaccount.help.2'/></a><@orcid.msg 'login.signinwithasocialaccount.help.3'/></p>
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
                                                <p><@orcid.msg 'login.signinviayourinstitution.help.1'/><a href="${knowledgeBaseUri}/articles/892920" target="_blank"><@orcid.msg 'login.signinviayourinstitution.help.2'/></a><@orcid.msg 'login.signinviayourinstitution.help.3'/></p>
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
</@public>