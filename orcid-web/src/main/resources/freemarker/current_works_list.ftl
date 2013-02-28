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
<#if (currentWorksForm.currentWorks)??>
<#list currentWorksForm.currentWorks as work>
<#-- @ftlvariable name="work" type="org.orcid.frontend.web.forms.CurrentWork" -->
    <li>
        <h3 class="work-title"><b>${(work.title)!}</b><#if (work.subtitle)??>: <span class="work-subtitle">${(work.subtitle)!""}</span></#if><#if (work.year)??> <#if (work.month)?? && work.month?has_content><@orcid.month work.month />-</#if>${work.year}</#if></h3>
        <label class="work-delete-lbl">
            <div class="delete-group">
                <a href="#" class="delete-work">delete</a>
                <span class="alert hide form-change-alert deleted-alert">
                    <a href="#" class="confirm-link">Confirm (requires save) </a> |
                    <a href="#" class="deny-link">Abandon</a></span>
                </span>
            </div>
        </label>
        <#if (work.currentWorkExternalIds)??>
            <#list work.currentWorkExternalIds as ei>
            <#-- @ftlvariable name="ei" type="org.orcid.frontend.web.forms.CurrentWorkExternalId" -->
                <#if (ei.type = 'doi') && (ei.id)??>
                    <span class="work-metadata">DOI: <a href="http://dx.doi.org/${ei.id}">${ei.id}</a></span>
                    <img onclick="javascript:window.open(&quot;http://dx.doi.org/${ei.id}&quot;)" style="cursor:pointer;" src="<@spring.url '/static/img/view_full_text.gif'/>"><input type="hidden" value="null" name="artifacts[0].destApp"><input type="hidden" value="JOUR" name="artifacts[0].type"><input type="hidden" value="W" name="artifacts[0].uploadedBy">
                </#if>
            </#list>
        </#if>
        <#if (work.url)??>
            <div><a href="${work.url}">${work.url}</a></div>
        </#if>
        <#if (work.description)?? && work.description?has_content>
            <div>${work.description}</div>
        <#else>
            <#if (work.citationForDisplay)??><div class="citation ${work.citationType}">${work.citationForDisplay}</div></#if>
        </#if>
        
        <@spring.formHiddenInput "currentWorksForm.currentWorks[${work_index?string.computer}].title"/>
        <@spring.formHiddenInput "currentWorksForm.currentWorks[${work_index?string.computer}].putCode"/>
        <@spring.formHiddenInput "currentWorksForm.currentWorks[${work_index?string.computer}].subtitle"/>
        <@spring.formHiddenInput "currentWorksForm.currentWorks[${work_index?string.computer}].citation"/>
        <@spring.formHiddenInput "currentWorksForm.currentWorks[${work_index?string.computer}].citationType"/>
        <@spring.formHiddenInput "currentWorksForm.currentWorks[${work_index?string.computer}].workType"/>
        <@spring.formHiddenInput "currentWorksForm.currentWorks[${work_index?string.computer}].day"/>
        <@spring.formHiddenInput "currentWorksForm.currentWorks[${work_index?string.computer}].month"/>
        <@spring.formHiddenInput "currentWorksForm.currentWorks[${work_index?string.computer}].year"/>
        <@spring.formHiddenInput "currentWorksForm.currentWorks[${work_index?string.computer}].url"/>
        <@spring.formHiddenInput "currentWorksForm.currentWorks[${work_index?string.computer}].description"/>
        <#if (work.currentWorkExternalIds)??>
            <#list work.currentWorkExternalIds as ei>
                <@spring.formHiddenInput "currentWorksForm.currentWorks[${work_index?string.computer}].currentWorkExternalIds[${ei_index}].id"/>
                <#if ei.type??>
                    <@spring.formHiddenInput "currentWorksForm.currentWorks[${work_index?string.computer}].currentWorkExternalIds[${ei_index}].type"/>
                </#if>
            </#list>
        </#if>
        <#if (work.currentWorkContributors)??>
            <#list work.currentWorkContributors as contributor>
                <@spring.formHiddenInput "currentWorksForm.currentWorks[${work_index?string.computer}].currentWorkContributors[${contributor_index}].orcid"/>
                <@spring.formHiddenInput "currentWorksForm.currentWorks[${work_index?string.computer}].currentWorkContributors[${contributor_index}].creditName"/>
                <@spring.formHiddenInput "currentWorksForm.currentWorks[${work_index?string.computer}].currentWorkContributors[${contributor_index}].role"/>
                <@spring.formHiddenInput "currentWorksForm.currentWorks[${work_index?string.computer}].currentWorkContributors[${contributor_index}].sequence"/>
            </#list>
        </#if>
        <#if showPrivacy!true>
            <label class="privacy-toggle-lbl">
                <@spring.formSingleSelect "currentWorksForm.currentWorks[${work_index?string.computer}].visibility", visibilities, "class='works-visibility'" />
                <@orcid.privacy "test" currentWorksForm.currentWorks[work_index].visibility/> 
            </label>
        </#if>
    </li>
</#list>
</#if>
