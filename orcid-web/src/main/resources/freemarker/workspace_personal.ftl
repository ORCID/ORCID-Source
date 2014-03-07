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
<#escape x as x?html>
	<#if (profile.orcidBio.biography.content)?? && (profile.orcidBio.biography.content)?has_content>
   		<p>
   			<strong>${springMacroRequestContext.getMessage("manage_bio_settings.labelbiography")}</strong><br />
   			<div style="white-space: pre-wrap">${(profile.orcidBio.biography.content)}</div>
   		</p>
   	</#if>
</#escape>