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
	
	<div class="login">
		<div class="row">
			<!-- ORCID login form -->
			<div class="col-md-offset-1 col-md-5 login-left-column">
				<form class="form-sign-in" id="loginForm" action="<@orcid.rootPath '/signin/auth'/>" method="post">
			        <div class="control-group">
			            <label for="userId" class="control-label">${springMacroRequestContext.getMessage("login.username")}</label>
			            <div>               
			                <input type="text" id="userId" name="userId" value="" placeholder="${springMacroRequestContext.getMessage("login.username")}">
			            </div>
			        </div>
			        
			        <div class="control-group password">
			            <label for="password" class="control-label">${springMacroRequestContext.getMessage("login.password")}</label>
			            <div>
			                <input type="password" id="password" name="password" value="" placeholder="${springMacroRequestContext.getMessage("login.password")}">
			            </div>
			        </div>
			        
			        <div class="control-group submit-login">				            
                        <button id='form-sign-in-button' class="btn btn-primary" type="submit">${springMacroRequestContext.getMessage("login.signin")}</button>
                        <span id="ajax-loader" class="no-visible"><i id="ajax-loader" class="glyphicon glyphicon-refresh spin x2 green"></i></span>
			            
			            <#if (RequestParameters['alreadyClaimed'])??>
			            	<div class="alert"><@spring.message "orcid.frontend.security.already_claimed"/></div>
			            </#if>   
			            <#if (RequestParameters['invalidClaimUrl'])??>
			            	<div class="alert"><@spring.message "orcid.frontend.security.invalid_claim_url"/></div>
			            </#if>              
			        </div>
			        				 
			        <div class="control-group">
			            <div id="login-reset">
			                <a href="<@orcid.rootPath '/reset-password'/>">${springMacroRequestContext.getMessage("login.reset")}</a>
			            </div>			            
			        </div>				    
				</form>
			</div>			
			<!-- Shibboleth and Social Login -->			
			<div class="col-md-offset-1 col-md-5 login-right-column">
				<#if (RequestParameters['shibboleth'])??>
					<div id="idpSelectContainer">				   
					    <div id="idpSelectInner">
					        <!-- Where the widget is going to be injected -->
					        <div id="idpSelect"></div>
					    </div>				    
					</div>
				</#if>
				<#if (RequestParameters['social'])??>
				    <div class="social-login">				        				        
	                    <form action="<@orcid.rootPath '/signin/facebook'/>" method="POST">
	                        <button type="submit" class="btn btn-social-icon btn-facebook"><i class="fa fa-facebook"></i></button>
	                        <input type="hidden" name="scope" value="email" />
	                        <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
	                    </form>
	        
	                    <form action="<@orcid.rootPath '/signin/google'/>" method="POST">
	                        <button type="submit" class="btn btn-social-icon btn-google"><i class="fa fa-google"></i></button>
	                        <input type="hidden" name="scope" value="email" />
	                        <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
	                    </form>				        
				        
				    </div>
				</#if>
					
			</div>		
		</div>
		<div class="login-messages">
			<div class="alert">
				Message
			</div>
		</div>
		<div class="row">			
			<div class="login-register">
				Do you have an ORCID iD yet? <a class="reg" href="<@orcid.rootPath '/register'/>">${springMacroRequestContext.getMessage("login.register")}</a>
			</div>
		</div>
	</div>
</@public>