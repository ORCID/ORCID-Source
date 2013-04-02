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
<@base>
<div class="colorbox-content">
<div class="row">

    <div class="span12">
        <@security.authorize ifAnyGranted="ROLE_USER">
        <#if justRegistered?? && justRegistered>
            <div class="alert alert-success">
                <strong><@spring.message "orcid.frontend.web.just_registered"/></strong>
            </div>
        </#if>
        
        <aside class="logo">
            <h1><img src="<@spring.url "/static/img/orcid-logo.png" />" alt="ORCID logo"></h1>
            <p>${springMacroRequestContext.getMessage("confirm-oauth-access.connectingresearchandresearchers")}</p>
        </aside>
        
        <br />
        <h2 class="oauth-title">${springMacroRequestContext.getMessage("confirm-oauth-access.connecting")}<br /> <span>${clientProfile.orcidBio.personalDetails.creditName.content}</span><br /> ${springMacroRequestContext.getMessage("confirm-oauth-access.withyourrecord")}</h2>
        
        <hr />
        
    </div>
    
    <div class="span6">
         <h3>${clientProfile.orcidBio.personalDetails.creditName.content}</h3>
         <p>${springMacroRequestContext.getMessage("confirm-oauth-access.hasaskedforthefollowing")}</p>
         <#list scopes as scope>
             <div class="alert">
                 <@spring.message "${scope.declaringClass.name}.${scope.name()}"/>
             </div>
         </#list>
         <p><@spring.message "orcid.frontend.web.oauth_is_secure"/></p>
         <div class="row">
         <#assign authOnClick = "">
         <#list scopes as scope>
            <#assign authOnClick = authOnClick + " orcidGA.gaPush(['_trackEvent', 'RegGrowth', 'Authorize_" + scope.name()?replace("ORCID_", "") + "', 'OAuth " + clientProfile.orcidBio.personalDetails.creditName.content + "']);">     
         </#list>

    	 <#assign denyOnClick = " orcidGA.gaPush(['_trackEvent', 'Disengagement', 'Authorize_Deny', 'OAuth " + clientProfile.orcidBio.personalDetails.creditName.content + "']);">     
             <span class="span">
                <form id="denialForm" class="form-inline" name="denialForm" action="<@spring.url '/oauth/authorize'/>" onsubmit="${denyOnClick} orcidGA.gaFormSumbitDelay(this); return false;" method="post">
                    <input name="user_oauth_approval" value="false" type="hidden"/>
                    <input class="btn btn-success" name="deny" value="${springMacroRequestContext.getMessage('confirm-oauth-access.Deny')}" type="submit">
                </form>        
            </span>
            <span class="span">
                <form id="confirmationForm" class="form-inline" name="confirmationForm" action="<@spring.url '/oauth/authorize'/>" onsubmit="${authOnClick} orcidGA.gaFormSumbitDelay(this); return false;" method="post">
                    <input name="user_oauth_approval" value="true" type="hidden"/>
                    <input class="btn btn-success" name="authorize" value="${springMacroRequestContext.getMessage('confirm-oauth-access.Authorize')}" type="submit">
                </form>
            </span>
        </div>
    </div>
    
    <div class="span6">
         <h5><#if (clientProfile.orcidBio.researcherUrls.url[0].value)??><a href="${(clientProfile.orcidBio.researcherUrls.url[0].value)!}" target="_blank"></#if>${(clientProfile.orcidBio.personalDetails.creditName.content)!}<#if (clientProfile.orcidBio.researcherUrls.url[0].value)??></a></#if></h5>
         ${(clientProfile.orcidBio.biography.content)!}
    </div>

</div>
</@security.authorize>
</div>
</@base>