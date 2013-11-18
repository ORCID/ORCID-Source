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
<@public classes=['home'] nav="signin">
<#include "sandbox_warning.ftl"/>
	<form class="form-sign-in" id="loginForm" action="<@spring.url '/signin/auth'/>" method="post">
		<div class="row">
		    <@spring.bind "loginForm" />
		    <@spring.showErrors "<br/>" "error" />
		    <#include "/common/browser-checks.ftl" />
		    <#if (RequestParameters['alreadyClaimed'])??>
		        <div class="alert col-md-offset-3 col-md-9 col-sm-12 col-xs-12"><@spring.message "orcid.frontend.security.already_claimed"/></div>
		    </#if>            
		    <div class="control-group col-md-offset-3 col-md-9 col-sm-9 col-sm-offset-3 col-xs-12">
		        <label for="userId" class="control-label">${springMacroRequestContext.getMessage("login.username")}</label>
		        <div>
		           <input type="text" id="userId" name="userId" value="" placeholder="${springMacroRequestContext.getMessage("login.username")}">
		        </div>
		    </div>
		    <div class="control-group col-md-offset-3 col-md-9 col-sm-9 col-sm-offset-3 col-xs-12">
		        <label for="password" class="control-label">${springMacroRequestContext.getMessage("login.password")}</label>
		        <div>
		            <input type="password" id="password" name="password" value="" placeholder="${springMacroRequestContext.getMessage("login.password")}">
		        </div>
		    </div>
		    <div class="control-group col-md-offset-3 col-md-9 col-sm-9 col-sm-offset-3 col-xs-12">
		        <div>		        	
		            <button id='form-sign-in-button' class="btn btn-primary" type="submit">${springMacroRequestContext.getMessage("login.signin")}</button>
		            <span id="ajax-loader" class="no-visible"><i id="ajax-loader" class="glyphicon glyphicon-refresh spin x2 green"></i></span>
		        </div>
		    </div>
		</div>
		<div class="row">
		    <div class="col-md-offset-3 col-md-3 col-sm-9 col-sm-offset-3 col-xs-12" id="login-reset">
		        <a href="<@spring.url '/reset-password'/>">${springMacroRequestContext.getMessage("login.reset")}</a>&nbsp;&nbsp;
		    </div>
		    <div class="col-md-6 col-sm-9 col-sm-offset-3"  id="login-register">
		       	<a class="reg" href="<@spring.url '/register'/>">${springMacroRequestContext.getMessage("login.register")}</a>
		    </div>
	    </div>
	</form>
</@public>