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
    <form class="form-sign-in" id="loginForm" ng-enter-submit action="<@spring.url '/signin/auth'/>" method="post">
        <div class="row col-md-offset-3">
            <div>Congratulations!</div>
            <div>You have linked your Shibboleth account ${remoteUser}</div>
            <div>to your ORCID account ${effectiveUserOrcid}.</div>
            <div>You will now be able to signin to ORCID using Shibboleth.</div>
            <div>You can now <a href="<@spring.url '/my-orcid'/>">continue to your ORCID record</a>.</div>
        </div>
    </form>
</@public>