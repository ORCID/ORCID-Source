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
<@public classes=['home'] nav="register">

    <@spring.bind "registrationForm.*" />
    <div class="row">
        <div class="span3"></div>
        <div class="span9">
            <h2>${springMacroRequestContext.getMessage("register.labelRegisterforanORCIDiD")}</h2>
            <p>${springMacroRequestContext.getMessage("register.labelORCIDprovides")}<br /><br /></p>
    		<#include "/includes/register_inc.ftl" />
        </div>
    </div>
</@public>