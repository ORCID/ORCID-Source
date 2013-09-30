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
<#if (currentWorks)??>

	<ul class="workspace-publications workspace-body-list">
        <#list currentWorks as work>
        <#-- @ftlvariable name="work" type="org.orcid.frontend.web.forms.CurrentWork" -->
            <li id="work_${work.putCode}">
             	<span class="pull-right"><@orcid.privacyLabel work.visibility /></span>             
                <h3 class="work-title"><b>${(work.title)!}</b><#if (work.subtitle)??>: <span class="work-subtitle">${(work.subtitle)!""}</span></#if><#if (work.year)??> <#if (work.month)??><@orcid.month work.month />-</#if>${work.year}</#if></h3>
                <#if (work.currentWorkExternalIds)??>
                	<#assign eiSize = work.currentWorkExternalIds?size />
                    <#list work.currentWorkExternalIds as ei>
                        <span class="work-metadata">
                        ${ei.type?upper_case}: 
                        <span wiJs-data="${ei.type}">${ei.id}</span>
                    </#list>
                </#if>
                <#if (work.url)??>
                	<#assign curUrl = work.url> 
                	<#if !work.url?starts_with('http')>
                		<#assign curUrl = 'http://' + work.url>
                	</#if>
                    <div><a href="${curUrl}" target="_blank">${work.url}</a></div>
                </#if>
                <#if (work.description)?? && work.description?has_content>
                    <div style="white-space: pre-wrap;">${work.description}</div>
                </#if>
                <#if (work.citationForDisplay)??><div class="citation ${work.citationType}">${work.citationForDisplay}</div></#if>
            </li>           
        </#list>
	</ul>
	
	<#--<#if profile.orcidActivities.orcidWorks.orcidWork?size &gt; 10>
		<p><a href="<@spring.url '/publications/manage'/>" class="btn btn-primary">${springMacroRequestContext.getMessage("workspace_works_body_list.Viewmore")} <span class="icon-arrow-right"></span></a></p>
	</#if>-->

<#else>
    <div class="alert alert-info">
        <strong><#if (publicProfile)?? && publicProfile == true>${springMacroRequestContext.getMessage("workspace_works_body_list.Nopublicationsaddedyet")}<#else>${springMacroRequestContext.getMessage("workspace_works_body_list.havenotaddedanyworks")} <a href="<@spring.url '/works-update'/>" class="update">${springMacroRequestContext.getMessage("workspace_works_body_list.addsomenow")}</a></#if></strong>
    </div>
</#if>
</#escape>