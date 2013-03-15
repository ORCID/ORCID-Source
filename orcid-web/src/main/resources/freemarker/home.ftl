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
<@public classes=['home'] >

<#include "sandbox_warning.ftl"/>
<div class="page-header">
    <h1>${springMacroRequestContext.getMessage("home.Identifycommunicatecollaborate")}</h1>
</div>
<div class="row-fluid">
    <div class="span12">
        <p>${springMacroRequestContext.getMessage("home.ORCIDisagoal")}</p>
        <#if reducedFunctionalityMode>
            <p><strong><span>${springMacroRequestContext.getMessage("home.importantupdate")}</span></strong><br>
            ${springMacroRequestContext.getMessage("home.Inpreparationfortheupcoming")} <a href="http://dev.orcid.org/launch" target="_blank">${springMacroRequestContext.getMessage("home.launch")}</a> ${springMacroRequestContext.getMessage("home.havemadesomechanges")} <span>${springMacroRequestContext.getMessage("home.API")}</span>${springMacroRequestContext.getMessage("home.XMLdescriptionofwhich")} <a href="http://dev.orcid.org/resources" target="_blank">${springMacroRequestContext.getMessage("home.developersportal")}</a>.</p>
            <p>${springMacroRequestContext.getMessage("home.additiontoupdate")} <span>${springMacroRequestContext.getMessage("login")}</span>${springMacroRequestContext.getMessage("home.improvingouruserexperience")}<span>${springMacroRequestContext.getMessage("home.API")}</span>${springMacroRequestContext.getMessage("home.sandboxwillnothavefullyfunction")} <a href="http://dev.orcid.org/resources" target="_blank">${springMacroRequestContext.getMessage("home.resourcespage")}</a> ${springMacroRequestContext.getMessage("home.resourcespage")}</p>
            <p>${springMacroRequestContext.getMessage("thanksforyourinterest")}<span>${springMacroRequestContext.getMessage("ORCID")}</span>. ${springMacroRequestContext.getMessage("home.haveanyquestions")} <a hre="mailto:devsupport@orcid.org">${springMacroRequestContext.getMessage("home.sitedevsupportorcid")}</a>.</p>   
        <#else>
	        <form class="well form-horizontal" action="<@spring.url '/orcid-search/search-for-orcid'/>" method="get">
	            <h3>${springMacroRequestContext.getMessage("home.searchORCID")}</h3>
	            <div class="control-group">
	                <label for="search-field-given-name" class="control-label">${springMacroRequestContext.getMessage("home.Givenname")}</label>
	                <div class="controls">
	                   <input id="search-field-given-name" type="text" placeholder="Given name" class="span3" name="givenName"/>
	                </div>
	            </div>
	            <div class="control-group">
	                <label for="search-field-family-name" class="control-label">${springMacroRequestContext.getMessage("home.familyname")}</label>
	                <div class="controls">
	                   <input id="search-field-family-name" type="text" placeholder="Family name" class="span3" name="familyName"/>
	                </div>
	            </div>
	            <div class="control-group">
	                <div class="controls">
	                   <button class="btn" type="submit">${springMacroRequestContext.getMessage("home.search")}</button>
	                </div>
	            </div>
	        </form>
        </#if>
    </div>
    <#if !reducedFunctionalityMode>
	    <div id="top-keywords" class="span4 well">
	        <ul class="nav nav-list">
	            <li class="nav-header">${springMacroRequestContext.getMessage("home.top20keywords")}</li>
	        </ul>
	    </div>
    </#if>
</div>
</@public>