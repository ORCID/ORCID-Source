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
<#-- @ftlvariable name="searchAndAddForm" type="org.orcid.frontend.web.forms.CurrentWorksForm" -->
<@base>

<div class="colorbox-content">
    <div class="row">
        <div class="span12">
            <h1 class="lightbox-title pull-left">${springMacroRequestContext.getMessage("works_update.UpdateWorks")}</h1><a class="btn pull-right close-button">X</a>
        </div>
    </div>
    <#if (searchAndAddForm.currentWorks)?? && (searchAndAddForm.currentWorks)?size &gt; 0>
        <div class="alert alert-block clearfix info">
            <#--<@spring.message "orcid.frontend.web.works_found"/>-->
            <div><strong>${springMacroRequestContext.getMessage("works_update.foundpublications")}</strong></div>
            <div><strong><a href="#" class="parent-import">${springMacroRequestContext.getMessage("works_update.alsoseeimport")}</a></strong></div>
        </div>
    <#elseif searchAndAddFormError??>
        <div class="alert alert-block clearfix info">
            ${springMacroRequestContext.getMessage("works_update.sorrycommunications")}
        </div>
    <#else>
    
        <div class="alert alert-block clearfix info">
            <@spring.message "orcid.frontend.web.no_works_found"/>
        </div>
    </#if>
    <div class="row">
        <div class="span6">
            <div id="left-selector-panel" data-work-count="${(currentWorksForm.currentWorks?size)!0}" class="selector-panel">
                <ul id="works-tabs" class="inline-list tabs">
                    <li class="selected"><a href="#search-and-add">${springMacroRequestContext.getMessage("works_update.Searchandadd")}</a></li>
                    <li><a href="#add-manually">${springMacroRequestContext.getMessage("works_update.Addmanually")}</a></li>
                </ul>
                <div class="tab-content open" id="search-and-add">
                    <h2 class="selector-title">${springMacroRequestContext.getMessage("works_update.Searchresults")}</h2>
                    <div class="selector-scroller search-scroller xref-search-results">
                        <#if (searchAndAddForm.currentWorks)?? && (searchAndAddForm.currentWorks)?size &gt; 0>
                            <@spring.bind "currentWorksForm.*" />
                            <form action="search-and-add-works" method="post">
                                <ul id="xref-search-results">
                                    <#list searchAndAddForm.currentWorks as work>
                                    <#-- @ftlvariable name="work" type="org.orcid.frontend.web.forms.CurrentWork" -->
                                        <li data-work-index="${work_index}">
                                            <h4>${(work.title)!}</h4>
                                            <label class="work-delete-lbl hide">
                                                <div class="delete-group">
                                                    <a href="#" class="delete-work">${springMacroRequestContext.getMessage("current_works_list.delete")}</a>
                                                    <span class="alert hide form-change-alert deleted-alert">
                                                        <a href="#" class="confirm-link">${springMacroRequestContext.getMessage("current_works_list.confirmrequiressave")} </a> |
                                                        <a href="#" class="deny-link">${springMacroRequestContext.getMessage("current_works_list.Abandon")}</a></span>
                                                    </span>
                                                </div>
                                            </label>
                                            <#if (work.subtitle)??>
                                                <div><span class="work-metadata">${work.subtitle}</span></div>
                                            <#else>
                                                <#if (work.citationForDisplay)??><div class="citation ${work.citationType}">${work.citationForDisplay}</div></#if>
                                            </#if>
                                            <@spring.formHiddenInput "searchAndAddForm.currentWorks[${work_index}].title"/>
                                            <@spring.formHiddenInput "searchAndAddForm.currentWorks[${work_index}].putCode"/>
                                            <@spring.formHiddenInput "searchAndAddForm.currentWorks[${work_index}].subtitle"/>
                                            <@spring.formHiddenInput "searchAndAddForm.currentWorks[${work_index}].citation"/>
                                            <@spring.formHiddenInput "searchAndAddForm.currentWorks[${work_index}].citationType"/>
                                            <@spring.formHiddenInput "searchAndAddForm.currentWorks[${work_index}].workType"/>
                                            <@spring.formHiddenInput "searchAndAddForm.currentWorks[${work_index}].day"/>
                                            <@spring.formHiddenInput "searchAndAddForm.currentWorks[${work_index}].month"/>
                                            <@spring.formHiddenInput "searchAndAddForm.currentWorks[${work_index}].year"/>
                                            <@spring.formHiddenInput "searchAndAddForm.currentWorks[${work_index}].url"/>
                                            <@spring.formHiddenInput "searchAndAddForm.currentWorks[${work_index}].description"/>
                                            <#if (work.currentWorkExternalIds)??>
                                                <#list work.currentWorkExternalIds as ei>
                                                    <@spring.formHiddenInput "searchAndAddForm.currentWorks[${work_index}].currentWorkExternalIds[${ei_index}].id"/>
                                                    <@spring.formHiddenInput "searchAndAddForm.currentWorks[${work_index}].currentWorkExternalIds[${ei_index}].type"/>
                                                    <#if (ei.type = 'doi') && (ei.id)??>
                                                        <span class="work-metadata">DOI: ${ei.id}</span>
                                                        <img onclick="javascript:window.open(&quot;http://dx.doi.org/${ei.id}&quot;)" style="cursor:pointer;" src="${staticCdn}/img/view_full_text.gif"><input type="hidden" value="null" name="artifacts[0].destApp"><input type="hidden" value="JOUR" name="artifacts[0].type"><input type="hidden" value="W" name="artifacts[0].uploadedBy">
                                                     </#if>
                                                </#list>
                                            </#if>
                                            <#if (work.currentWorkContributors)??>
                                                <#list work.currentWorkContributors as contributor>
                                                    <@spring.formHiddenInput "searchAndAddForm.currentWorks[${work_index}].currentWorkContributors[${contributor_index}].orcid"/>
                                                    <@spring.formHiddenInput "searchAndAddForm.currentWorks[${work_index}].currentWorkContributors[${contributor_index}].creditName"/>
                                                    <@spring.formHiddenInput "searchAndAddForm.currentWorks[${work_index}].currentWorkContributors[${contributor_index}].role"/>
                                                    <@spring.formHiddenInput "searchAndAddForm.currentWorks[${work_index}].currentWorkContributors[${contributor_index}].sequence"/>
                                                </#list>
                                            </#if>
                                            <@spring.formCheckbox "searchAndAddForm.currentWorks[${work_index}].selected" 'class="no-js"'/>
                                        </li>
                                    </#list>
                                    <li class="no-js">
                                        <input type="submit" value="${springMacroRequestContext.getMessage('manual_work_form_contents.btnaddtolist')}"></input>
                                        <input type="reset" class="btn" value="${springMacroRequestContext.getMessage('freemarker.btncancel')}"></input>
                                    </li>
                                </ul>
                            </form>
                            <#elseif searchAndAddFormError??>
                             <p>${springMacroRequestContext.getMessage("works_update.sorrycommunications")}</p>
                            <#else>
                            <p>${springMacroRequestContext.getMessage("works_update.noresults")}</p>
                        </#if>
                    </div>
                </div>
                <div class="tab-content" id="add-manually">
                    <h2 class="selector-title">${springMacroRequestContext.getMessage("works_update.Addmanually")}</h2>
                    <div class="selector-scroller">
                        <form action="save-work-manually" method="post" id="save-work-manually" class="">
                            <#include "manual_work_form_contents.ftl"/>
                        </form>
                    </div>
                </div>
            </div>
        </div>
        <div class="span6">
            <div class="selector-panel">
                <h2 class="selector-title no-tabs">${springMacroRequestContext.getMessage("works_update.WorksinyourORCIDrecord")}</h2>
                <div class="selector-scroller works-scroller">
                    <@spring.bind "currentWorksForm.*" />
                    <form id="save-works-form" action="save-current-works" method="post">
                        <ul id="current-work-list">
                            <#include "current_works_list.ftl"/>
                            <li class="no-js">
                                <input type="submit" value="${springMacroRequestContext.getMessage('works_update.Savecurrentpublicationslist')}"></input>
                                <input type="reset" value="${springMacroRequestContext.getMessage('freemarker.btncancel')}"></input>
                            </li>
                        </ul>
                    </form>
                </div>
            </div>
        </div>
    </div>
    <div class="control-bar">
          <div class="pull-right">
              <span id="changed-alert" class="hide alert form-change-alert">${springMacroRequestContext.getMessage("works_update.youmadechanges")} </span>
              <a href="#" class="btn btn-primary close-and-reload" id="works-list-save">${springMacroRequestContext.getMessage("freemarker.btnsavechanges")}</a>
              <a href="#" class="btn close-button" id="works-list-reset" type="reset">${springMacroRequestContext.getMessage("freemarker.btncancel")}</a>
          </div>
          ${springMacroRequestContext.getMessage("works_update.Poweredby")} <img src="${staticCdn}/img/xref.png" alt="${springMacroRequestContext.getMessage('works_update.altcross')}" />
    </div>
</div>
<div class="hide">
    <label id="privacy-template" class="privacy-toggle-lbl">
        <select class="works-visibility" name="currentWorks[0].visibility" id="currentWorks[0].visibility">
            <#if workVisibilityDefault == "public"><option value="public" selected="selected">${springMacroRequestContext.getMessage("manage.lipublic")}</option><#else><option value="public">${springMacroRequestContext.getMessage("manage.lipublic")}</option></#if>
            <#if workVisibilityDefault == "limited"><option value="limited" selected="selected">${springMacroRequestContext.getMessage("manage.lilimited")}</option><#else><option value="limited">${springMacroRequestContext.getMessage("manage.lilimited")}</option></#if>
            <#if workVisibilityDefault == "private"><option value="private" selected="selected">${springMacroRequestContext.getMessage("manage.liprivate")}</option><#else><option value="private">${springMacroRequestContext.getMessage("manage.liprivate")}</option></#if>
        </select>
        <div class="privacy-tool">
            <div class="btn-group privacy-group abs-left-top">
	            <#if workVisibilityDefault == "" || workVisibilityDefault == "public"><button class="btn btn-success dropdown-toggle privacy-toggle">${springMacroRequestContext.getMessage("manage.lipublic")} <span class="caret"></span></button></#if>
	            <#if workVisibilityDefault == "limited"><button class="btn btn-warning dropdown-toggle privacy-toggle">${springMacroRequestContext.getMessage("manage.lilimited")} <span class="caret"></span></button></#if>
	            <#if workVisibilityDefault == "private" || workVisibilityDefault == "protected"><button class="btn btn-danger dropdown-toggle privacy-toggle">${springMacroRequestContext.getMessage("manage.liprivate")} <span class="caret"></span></button></#if>
	            <ul class="dropdown-menu privacy-menu">
	                <li><a class="btn btn-success btn-privacy" href="#public">${springMacroRequestContext.getMessage("manage.lipublic")} <span class="caret"></span></a></li>
	                <li><a class="btn btn-warning btn-privacy" href="#limited">${springMacroRequestContext.getMessage("manage.lilimited")} <span class="caret"></span></a></li>
	                <li><a class="btn btn-danger btn-privacy" href="#private">${springMacroRequestContext.getMessage("manage.liprivate")} <span class="caret"></span></a></li>	
	                <li><a class="btn" href="http://support.orcid.org/knowledgebase/articles/124518" target="_blank">${springMacroRequestContext.getMessage("manage.lihelp")} <span class="caret"></span></a></li>
	            </ul>
            </div>
        </div>
    </label>
</div>
</@base>
