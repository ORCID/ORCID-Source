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
<div class="row">		
        <form class="form-sign-in" id="loginForm" action="<@spring.url '/signin/auth'/>" method="post">
            <@spring.bind "loginForm" />
            <@spring.showErrors "<br/>" "error" />
            <#include "/common/browser-checks.ftl" />
            <#if (RequestParameters['alreadyClaimed'])??>
                <div class="alert offset3 span6"><@spring.message "orcid.frontend.security.already_claimed"/></div>
            </#if>
            <div class="control-group offset3 span6">
                <label for="userId" class="control-label">${springMacroRequestContext.getMessage("login.username")}</label>
                <div>
                   <input type="text" id="userId" name="userId" value="" placeholder="Email or ORCID" class="input-xlarge">
                </div>
            </div>
            <div class="control-group offset3 span6">
                <label for="password" class="control-label">${springMacroRequestContext.getMessage("login.password")}</label>
                <div>
                    <input type="password" id="password" name="password" value="" placeholder="Password" class="input-xlarge"    >
                </div>
            </div>
            <div class="control-group offset3 span6">
                <div>
                    <button id='form-sign-in-button' class="btn btn-primary" type="submit">${springMacroRequestContext.getMessage("login.signin")}</button>
                    <span id="ajax-loader" class="hide"><i id="ajax-loader" class="icon-spinner icon-large icon-spin green"></i></span>
                </div>
            </div>
            <div class="offset3 span3">
                <a href="<@spring.url '/reset-password'/>">${springMacroRequestContext.getMessage("login.reset")}</a>&nbsp;&nbsp;
            </div>
            <div class="span3">
               	<a class="reg" href="<@spring.url '/register'/>">${springMacroRequestContext.getMessage("login.register")}</a>
            <div>
        </form>  
    
</div>
</@public>