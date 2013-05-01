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
<@spring.bind "oAuthRegistrationForm.*" />
<#if spring.status.errors.globalErrors?? && spring.status.errors.globalErrors?size &gt; 0>
<div class="errorBox">
    <div class="errorHead">${springMacroRequestContext.getMessage("oauth_sign_up.notice")}</div>
    <div class="errorText">
        <@orcid.showGlobalErrorsUnescaped/>
    </div>
</div>
</#if>
    <div class="span6">
    <div class="page-header">
	    <h3>${springMacroRequestContext.getMessage("oauth_sign_up.h3donothaveid")}</h3>
	</div>
    <#include "/includes/register_inc.ftl" />
    <br />
    <br />	

