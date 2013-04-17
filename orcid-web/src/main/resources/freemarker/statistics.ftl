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
<#-- @ftlvariable name="statistics" type="java.util.Map" -->
<@public >
<#escape x as x?html>
<div id="statistics" class="row">
    <div class="span3">        
    </div>
    <div class="span9">
    	<span class="page-header">${springMacroRequestContext.getMessage("statistics.header")}</span>
    	<hr>

        <span class="stat-name">${springMacroRequestContext.getMessage("statistics.live_ids")}</span>
  		<div class="pull-right">${statistics['liveIds']}</div>
  		<hr>
        
        <span class="stat-name">${springMacroRequestContext.getMessage("statistics.ids_with_verified_emails")}</span>
  		<div class="pull-right">${statistics['idsWithVerifiedEmail']}</div>
  		<hr>
        
		<span class="stat-name">${springMacroRequestContext.getMessage("statistics.ids_with_works")}</span>
  		<div class="pull-right">${statistics['idsWithWorks']}</div>
  		<hr>
		
		<span class="stat-name">${springMacroRequestContext.getMessage("statistics.number_of_works")}</span>
  		<div class="pull-right">${statistics['works']}</div>
  		<hr>

		<span class="stat-name">${springMacroRequestContext.getMessage("statistics.number_of_works_with_dois")}</span>
  		<div class="pull-right">${statistics['worksWithDois']}</div>
  		<hr>
    </div>
</div>
</#escape>
</@public>