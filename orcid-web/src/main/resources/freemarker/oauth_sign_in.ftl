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
    <#include "/common/browser-checks.ftl" />
    <div class="span6">
    <div class="page-header">
	    <h3>${springMacroRequestContext.getMessage("oauth_sign_in.h3signin")}</h3>
	</div>
        <form id="loginForm" action="<@spring.url '/signin/auth'/>" method="post">
            
            <@spring.bind "loginForm" />
            <@spring.showErrors "<br/>" "error" />

            <div>
                <label for="userId">${springMacroRequestContext.getMessage("oauth_sign_in.labelemailorID")}</label>
                <div class="relative">
                   <@spring.formInput "loginForm.userId" 'placeholder="Email or ORCID" class="input-xlarge"' />
                </div>
            </div>
            <div id="passwordField">
                <label for="password">${springMacroRequestContext.getMessage("oauth_sign_in.labelpassword")}</label>
                <div class="relative">
                   <@spring.formPasswordInput "loginForm.password" 'placeholder="Password" class="input-xlarge"' />
                </div>
            </div>
            <div id="buttons">
                <div class="relative">
                    <button class="btn btn-primary" type="submit">${springMacroRequestContext.getMessage("oauth_sign_in.h3signin")}</button>
                    <span id="ajax-loader" class="hide"><i id="ajax-loader" class="icon-spinner icon-large icon-spin green"></i></span>
                </div>
                <div class="relative">
                	<a href="<@spring.url '/reset-password'/>">${springMacroRequestContext.getMessage("oauth_sign_in.forgottenpassword")}</a>
                </div>
            </div>
        </form>
    </div>
