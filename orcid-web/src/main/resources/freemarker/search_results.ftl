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
<#-- @ftlvariable name="aboutUri" type="java.lang.String" -->
<#escape x as x?html>
<#if searchResults??>
        <h3 class="search-result-head">${springMacroRequestContext.getMessage("search_results.h3Searchresults")}</h3>
        <table class="table table-striped">
            <thead>
            <tr>
                <th>${springMacroRequestContext.getMessage("search_results.thRelevance")}</th>
                <th>${springMacroRequestContext.getMessage("search_results.thORCIDID")}</th>
                <th>${springMacroRequestContext.getMessage("search_results.thGivenname")}</th>
                <th>${springMacroRequestContext.getMessage("search_results.thFamilynames")}</th>
                <th>${springMacroRequestContext.getMessage("search_results.thOthernames")}</th>
                <th>${springMacroRequestContext.getMessage("search_results.thInstitutions")}</th>
            </tr>
            </thead>
            <tbody>
                <#list searchResults as searchResult>
                    <#assign profileInfo = searchResult.orcidProfile>
                <tr>
                    <td>${(searchResult.relevancyScore.value)!}</td>
                    <td><a href="<@orcid.orcidUrl (profileInfo.orcid.value)!/>">${(profileInfo.orcid.value)!}</td>
                     <#if (profileInfo.isDeactivated()) >
                    	 <td colspan="4">${springMacroRequestContext.getMessage("search_results.tdnologneractive")}</td>
					<#else>                     
                    	<td>${(profileInfo.orcidBio.personalDetails.givenNames.content)!}</td>
                    	<td>${(profileInfo.orcidBio.personalDetails.familyName.content)!}</td>
                    	<td title="<@otherNameTitle (profileInfo.orcidBio.personalDetails.otherNames.otherName)!/>">
                        	<@otherNameContent (profileInfo.orcidBio.personalDetails.otherNames.otherName)!/>
                    	</td>
                    	<td>${(profileInfo.orcidBio.primaryInstitution.primaryInstitutionName.content)!}</td>
                    </#if>
                </tr>
                </#list>
            </tbody>
        </table>
    </#if>
    <#if noResultsFound?? && noResultsFound>
        <div class="alert alert-error">
            <@spring.message "orcid.frontend.web.no_results"/>
        </div>
    </#if>
</#escape>

<#macro otherNameTitle elements>
 <#assign str = ""/>
    <#if elements?size &gt; 0 >
        <#list elements as name>
            <#assign str =(str + name.content) />
             <#if name_has_next>
             <#assign str= (str + ", ") />
             </#if>
        </#list>
    </#if>
${str}
</#macro>

<#macro otherNameContent elements>
    <#assign str = ""/>
    <#if elements?size &gt; 0 >
        <#list elements as name>
            <#assign str = (str + name.content) />
                <#if name_has_next>
                    <#assign str= (str + ", ") />
                </#if>            
        </#list>
    </#if>
${str}
</#macro>