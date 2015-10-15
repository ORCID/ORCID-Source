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
    <form class="form-sign-in shibboleth" id="loginForm" ng-enter-submit action="<@orcid.rootPath '/signin/auth'/>" method="post">
        <div class="row">
        	<div class="col-md-offset-3 col-md-9 col-sm-offset-3 col-sm-9 col-xs-12">
	            <div class="">
		            <h4>Link your ${providerId} account to your ORCID account?</h4>
		            <p>
			            You are logged into <i>${providerId} </i> as <i>${emailId}</i>.<br />
			            Sign in to your ORCID account to complete the linkage.<br />
		            </p>
	            </div>
            </div>
        </div>
        <div class="row">
            <@spring.bind "loginForm" />             
            <@spring.showErrors "<br/>" "error" />             
            <#include "/common/browser-checks.ftl" />
            <div class="control-group col-md-offset-3 col-md-9 col-sm-9 col-sm-offset-3 col-xs-12">
                <label for="userId" class="control-label">${springMacroRequestContext.getMessage("login.username")}</label>
                <div>                
                    <input type="text" id="userId" name="userId" value="" placeholder="${springMacroRequestContext.getMessage("login.username")}">
                </div>                    
            </div>
            <div class="control-group col-md-offset-3 col-md-9 col-sm-9 col-sm-offset-3 col-xs-12 password">
                <label for="password" class="control-label">${springMacroRequestContext.getMessage("login.password")}</label>
                <div>
                    <input type="password" id="password" name="password" value="" placeholder="${springMacroRequestContext.getMessage("login.password")}">
                </div>
            </div>
            <div class="control-group col-md-offset-3 col-md-9 col-sm-9 col-sm-offset-3 col-xs-12 submit-login">                                    
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
            <div class="control-group col-md-offset-3 col-md-9 col-sm-9 col-sm-offset-3 col-xs-12"> 
                <div id="login-reset">
                    <a href="<@orcid.rootPath '/reset-password'/>">${springMacroRequestContext.getMessage("login.reset")}</a>&nbsp;&nbsp;
                </div>
                <div id="login-register">
                    <a class="reg" href="<@orcid.rootPath '/register'/>">${springMacroRequestContext.getMessage("login.register")}</a>
                </div>
            </div>            
        </div>
    </form>
</@public>