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
    
     	<div class="top-margin"You may deactivate an ORCID account at any time</div>
     	<div class="top-margin">So as not to assign the same identifier to another person, ORCID will maintain in a private data file your name and email address.</div>
     	<div class="top-margin">All other data in your record will be removed.</div>
     	<div class="top-margin">You may contact ORCID if you later wish to re-claim your identifier and reactivate your account</p></div>
     	
     	<h3>To deactivate your account</h3>
     	<div class="top-margin">Click on the Send Deactivation link below:</div>
     	<div><a href="<@spring.url '/account/start-deactivate-orcid-account'/>">Deactivate My Orcid Account...</a></div>
     	<div class="top-margin">Find the deactivation email that will be sent to the address currently in your account.</div>
     	<div class="top-margin">Click the link provided in the email to confirm the deactivation.</div>
     </div>
 
 
        
           
</@base>   