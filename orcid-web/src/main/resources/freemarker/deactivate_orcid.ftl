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
<div class="popover-frame">
     	<#if deactivateEmailSent??>
    		<div class="alert alert-success">
        		<strong>${deactivateEmailSent}</strong>
    		</div>	
    	</#if>
    
     	<div class="top-margin">${springMacroRequestContext.getMessage("deactivate_orcid.youmaydeactivateaccount")}</div>
     	<div class="top-margin">${springMacroRequestContext.getMessage("deactivate_orcid.soasnottoassignthesameidentifier")}</div>
     	<div class="top-margin">${springMacroRequestContext.getMessage("deactivate_orcid.Allotherdatainyourrecord")}</div>
     	<div class="top-margin">${springMacroRequestContext.getMessage("deactivate_orcid.youmaycontactORCID")}</div>
     	
     	<h3>${springMacroRequestContext.getMessage("deactivate_orcid.todeactivateyouraccount")}</h3>
     	<div class="top-margin">${springMacroRequestContext.getMessage("deactivate_orcid.clickonthesenddeactivation")}</div>
     	<div><a href="" onclick="orcidGA.gaPush(['_trackEvent', 'Disengagement', 'Deactivate_Initiate', 'Website']); orcidGA.windowLocationHrefDelay('<@spring.url '/account/start-deactivate-orcid-account'/>'); return false;">${springMacroRequestContext.getMessage("deactivate_orcid.deactivatemyOrcidaccount")}</a></div>
     	<div class="top-margin">${springMacroRequestContext.getMessage("deactivate_orcid.finddeactivationemail")}</div>
     	<div class="top-margin">${springMacroRequestContext.getMessage("deactivate_orcid.clickthelinkprovided")}</div>
     </div>
 
 
        
           
</@base>   