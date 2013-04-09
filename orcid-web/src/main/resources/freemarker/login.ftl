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
    <div class="offset3 span9">
        <form class="form-sign-in" id="loginForm" action="<@spring.url '/signin/auth'/>" method="post">
            <@spring.bind "loginForm" />
            <@spring.showErrors "<br/>" "error" />
            <#include "/common/browser-checks.ftl" />
            <#if alreadyClaimed!false>
                <div class="alert"><@spring.message "orcid.frontend.security.already_claimed"/></div>
            </#if>
            <div class="control-group">
                <label for="userId" class="control-label">${springMacroRequestContext.getMessage("login.username")}</label>
                <div>
                   <input type="text" id="userId" name="userId" value="" placeholder="Email or ORCID" class="input-xlarge">
                </div>
            </div>
            <div class="control-group">
                <label for="password" class="control-label">${springMacroRequestContext.getMessage("login.password")}</label>
                <div>
                    <input type="password" id="password" name="password" value="" placeholder="Password" class="input-xlarge"    >
                </div>
            </div>
            <div class="control-group">
                <div>
                    <button id='form-sign-in-button' class="btn btn-primary" type="submit">${springMacroRequestContext.getMessage("login.signin")}</button>
                    <span id="ajax-loader" class="hide"><i id="ajax-loader" class="icon-spinner icon-large icon-spin green"></i></span>
                </div>
                <div class="control-links">
                	<a href="<@spring.url '/reset-password'/>">${springMacroRequestContext.getMessage("login.reset")}</a>&nbsp;&nbsp;
                	<a class="reg" href="<@spring.url '/register'/>">${springMacroRequestContext.getMessage("login.register")}</a>
                </div>
            </div>
            <#if Session.SPRING_SECURITY_LAST_EXCEPTION?? && Session.SPRING_SECURITY_LAST_EXCEPTION.message?has_content>
                <div class="alert alert-error pagination-centered input-xxlarge">
                    <p><@spring.message "orcid.frontend.security.bad_credentials"/></p>
                </div>
            </#if>
        </form>
    </div>
</div>
</@public>