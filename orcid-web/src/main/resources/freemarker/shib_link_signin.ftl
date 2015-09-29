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
<@base>
	<#include "sandbox_warning.ftl"/>
	<div class="shibboleth-container top-green-border">		
		<div class="logo">
			<img src="${staticCdn}/img/orcid-logo.png" alt="ORCID logo" />
		</div>
	    <form class="form-sign-in shibboleth" id="loginForm" ng-enter-submit action="<@orcid.rootPath '/signin/auth'/>" method="post">
	        <div class="row">
	        	<div class="col-md-12">
		            <div class="alert">Keep your mock ${remoteUserHeader} and Shib-Identity-Provider as secret as your password if you are using mocked headers, because they can be used to get access to your account once they are linked.</div>
		            <div class="">
			            <h4>Link your Shibboleth account to your ORCID account?</h4>
			            <p>
				            You are logged into <i>Shibboleth</i> as <i>${remoteUser}</i>.<br />
				            Sign in to your ORCID account to complete the linkage.<br />
			            </p>
		            </div>
	            </div>
	        </div>
	        <div class="row">
	            <@spring.bind "loginForm" />             
	            <@spring.showErrors "<br/>" "error" />             
	            <#include "/common/browser-checks.ftl" />
	            <div class="col-md-12">
	                <label for="userId" class="control-label">${springMacroRequestContext.getMessage("login.username")}</label>
	                <div>                
	                    <input type="text" id="userId" name="userId" value="" placeholder="${springMacroRequestContext.getMessage("login.username")}">
	                </div>                    
	            </div>
	            <div class="password col-md-12">
	                <label for="password" class="control-label">${springMacroRequestContext.getMessage("login.password")}</label>
	                <div>
	                    <input type="password" id="password" name="password" value="" placeholder="${springMacroRequestContext.getMessage("login.password")}">
	                </div>
	            </div>
	            <div class="submit-login col-md-12">                                    
	                <button id='form-sign-in-button' class="btn btn-primary" type="submit">${springMacroRequestContext.getMessage("login.signin")}</button>                
	                <span id="ajax-loader" class="no-visible"><i id="ajax-loader" class="glyphicon glyphicon-refresh spin x2 green"></i></span>                
	                <#if (RequestParameters['alreadyClaimed'])??>
	                    <div class="alert"><@spring.message "orcid.frontend.security.already_claimed"/></div>
	                </#if>   
	                <#if (RequestParameters['invalidClaimUrl'])??>
	                    <div class="alert"><@spring.message "orcid.frontend.security.invalid_claim_url"/></div>
	                </#if>                
	            </div>                    
	        </div>
	        <div class="row">
	            <div class="col-md-12"> 
	                <div id="login-reset">
	                    <a href="<@orcid.rootPath '/reset-password'/>">${springMacroRequestContext.getMessage("login.reset")}</a>&nbsp;&nbsp;
	                </div>
	                <div id="login-register">
	                    <a class="reg" href="<@orcid.rootPath '/register'/>">${springMacroRequestContext.getMessage("login.register")}</a>
	                </div>
	            </div>            
	        </div>
	    </form>
    </div>
</@base>