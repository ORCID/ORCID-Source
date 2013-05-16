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
<#--
 * orcid.ftl
 *
 * This file consists of a collection of FreeMarker macros used in various places in the ORCID web interface.
 *
 * @author Will Simpson
 -->
 
<#macro workDetails work>
	<td class="summary_data">
		${springMacroRequestContext.getMessage("macros.orcid.Title")} ${(work.workTitle.title.content)!}
		<#if (work.workContributors.contributor)??>
			<br>
			${springMacroRequestContext.getMessage("macros.orcid.Author")}
			<#list work.workContributors.contributor as contributor>
				${(contributor.creditName.content)!} <#if contributor_has_next >, </#if>
			</#list>
		</#if>
		<br>
		<#if (work.workTitle.subtitle.content)??>
			${springMacroRequestContext.getMessage("macros.orcid.Subtitle")} <span class="data_bold">${work.workTitle.subtitle.content}</span>
		</#if>
		<#if work.volume??>
			${springMacroRequestContext.getMessage("macros.orcid.Volume")} <span class="data_bold">${work.volume.content}</span>
		</#if>
		<input type="hidden" value="" name="artifacts[0].volume">
		<#if work.pages??>
			${springMacroRequestContext.getMessage("macros.orcid.Pages")}
			<#if work.pages.start??><span class="data_bold">${work.pages.start}</span></#if><#if work.pages.end??><span class="data_bold">-${work.pages.end}</span></#if>
		</#if>
		<input type="hidden" value="" name="artifacts[0].bibPages">
		<#if work.publicationDate??>
			${springMacroRequestContext.getMessage("macros.orcid.Published")}
			<span class="data_bold">
				${work.publicationDate.year.value}<#if work.publicationDate.month??>-${work.publicationDate.month.value}</#if><#if work.publicationDate.day??>-${work.publicationDate.day.value}</#if>
			</span>
		</#if>
		<br>
		<#if work.workExternalIdentifiers??>
		    <#list work.workExternalIdentifiers.workExternalIdentifier as externalId>
		        ${externalId.workExternalIdentifierType}:
		        <#if externalId.workExternalIdentifierType = 'DOI'>
		            <a href="http://dx.doi.org/${externalId.workExternalIdentifierId.content}" target="_blank">${externalId.workExternalIdentifierId.content}</a>
		        <#else>
		            ${externalId.workExternalIdentifierId.content}
		        </#if>
		    </#list>
		</#if>
	</td>
	<#if showPrivacy!false>
	    <td class="summary_data">
	        <#if work.visibility! != 'public' >
	            ${springMacroRequestContext.getMessage("macros.orcid.Limitedaccess")}
	        <#else>
	            ${springMacroRequestContext.getMessage("manage.lipublic")}
            </#if>
        </td>
    </#if>
    <td class="summary_date">
        <#if work.addedToProfileDate??>
            ${springMacroRequestContext.getMessage("macros.orcid.added")}
            <br>
            ${work.addedToProfileDate.value.toGregorianCalendar().time?date}
        </#if>
    </td>
</#macro>

<#macro grantDetails grant>
    <li>
        <#if (grant.fundingAgency.agencyOrcid)??><div><b>${springMacroRequestContext.getMessage("macros.orcid.AgencyORCID")}</b>: ${grant.fundingAgency.agencyOrcid.content}</div></#if>
        <#if (grant.fundingAgency.agencyName)??><div><b>${springMacroRequestContext.getMessage("macros.orcid.Agencyname")}</b>: ${grant.fundingAgency.agencyName.content}</div></#if>
        <#if (grant.grantExternalIdentifier.grantExternalProgram)??><div><b>${springMacroRequestContext.getMessage("macros.orcid.Externalprogram")}</b>: ${grant.grantExternalIdentifier.grantExternalProgram.content}</div></#if>
        <#if (grant.grantExternalIdentifier.grantExternalId)??><div><b>${springMacroRequestContext.getMessage("macros.orcid.ExternalID")}</b>: ${grant.grantExternalIdentifier.grantExternalId.content}</div></#if>
        <#if grant.grantNumber??><div><b>${springMacroRequestContext.getMessage("macros.orcid.Grantnumber")}</b>: ${grant.grantNumber.content}</div></#if>
        <#if grant.shortDescription??><div><b>${springMacroRequestContext.getMessage("macros.orcid.Description")}</b>: ${grant.shortDescription}</div></#if>
        <#if (grant.grantContributors.contributor)??>
            <div>
                <b>${springMacroRequestContext.getMessage("macros.orcid.Contributor")}</b>: <@contributorList grant.grantContributors.contributor/>
            </div>
        </#if>
        <#if showPrivacy!false>
            <@privacy "" grant.visibility! />
        </#if>
    </li>
</#macro>

<#macro privacyToggle angularModel publicClick limitedClick privateClick>
	<div class="relative">
		<ul class="privacyToggle">
			<li class="publicActive" ng-class="{publicInActive: ${angularModel} != 'PUBLIC'}"><a href="" title="PUBLIC" ng-click="${publicClick}"></a></li>
			<li class="limitedActive" ng-class="{limitedInActive: ${angularModel} != 'LIMITED'}"><a href="" title="LIMITED" ng-click="${limitedClick}"></a></li>
			<li class="privateActive" ng-class="{privateInActive: ${angularModel} != 'PRIVATE'}"><a href="" title="PRIVATE" ng-click="${privateClick}"></a></li>
		</ul>
		<div class="popover-help-container" style="position: absolute; left: 110px; top: 5px;">
        	<a href="javascript:void(0);"><i class="icon-question-sign"></i></a>
            <div class="popover bottom">
		        <div class="arrow"></div>
		        <div class="popover-content">
		        	<strong>${springMacroRequestContext.getMessage("privacyToggle.help.who_can_see")}</strong>
			        <ul class="privacyHelp">
			        	<li class="public" style="color: #009900;">${springMacroRequestContext.getMessage("privacyToggle.help.everyone")}</li>
			        	<li class="limited"style="color: #ffb027;">${springMacroRequestContext.getMessage("privacyToggle.help.trusted_parties")}</li>
			        	<li class="private" style="color: #990000;">${springMacroRequestContext.getMessage("privacyToggle.help.only_me")}</li>
			        </ul>
			        <a href="http://support.orcid.org/knowledgebase/articles/124518-orcid-privacy-settings" target="_blank">${springMacroRequestContext.getMessage("privacyToggle.help.more_information")}</a>
		        </div>                
		    </div>
    	</div>				   					
	</div>
</#macro>


<#macro patentDetails patent>
    <li>
        <#if (patent.shortDescription)??><h4>${patent.shortDescription}</h4></#if>
        <#if (patent.country)??><div><b>${springMacroRequestContext.getMessage("macros.orcid.Country")}</b>: ${patent.country.content}</div></#if>
        <#if (patent.patentNumber)??><div><b>${springMacroRequestContext.getMessage("macros.orcid.Patentnumber")}</b>: ${patent.patentNumber.content}</div></#if>
        <#if (patent.shortDescription)??><div><b>${springMacroRequestContext.getMessage("macros.orcid.Description")}</b>: ${patent.shortDescription}</div></#if>
        <#if (patent.patentIssueDate)??><div><b>${springMacroRequestContext.getMessage("macros.orcid.Patentissuedate")}</b>: ${patent.patentIssueDate.value.toGregorianCalendar().time?date}</div></#if>
        <#if (patent.assignee)?? && patent.assignee?size &gt; 0>
            <div>
                <b>${springMacroRequestContext.getMessage("macros.orcid.Assignee")}</b>:
                <#list patent.assignee as assignee>
                    ${assignee} <#if assignee_has_next >, </#if>
                </#list>
            </div>
        </#if>
        <#if (patent.patentContributors.contributor)??>
            <div>
                <b>${springMacroRequestContext.getMessage("macros.orcid.Contributor")}</b>: <@contributorList patent.patentContributors.contributor/>
            </div>
        </#if>
        <#if showPrivacy!false>
            <@privacy "" patent.visibility! />
        </#if>
    </li>
</#macro>

<#macro itemDetails item="" field="" tag="div">
    <#if item != "">
        <${tag}><#if field != ""><b>${field}</b>: </#if>${item}</${tag}>
    </#if>
</#macro>

<#macro contributorList contributors>
    <#list contributors as contributor>
        ${(contributor.creditName.content)!} (${contributor.contributorAttributes.contributorRole?replace('_', ' ')?lower_case?cap_first}) <#if contributor_has_next >, </#if>
    </#list>    
</#macro>

<#macro closeTag>
    <#if xhtmlCompliant?exists && xhtmlCompliant>/><#else>></#if>
</#macro>

<#macro bind path>
    <#if htmlEscape?exists>
        <#assign status = springMacroRequestContext.getBindStatus(path, htmlEscape)>
    <#else>
        <#assign status = springMacroRequestContext.getBindStatus(path)>
    </#if>
<#-- assign a temporary value, forcing a string representation for any
kind of variable. This temp value is only used in this macro lib -->
    <#if status.value?exists && status.value?is_boolean>
        <#assign stringStatusValue=status.value?string>
    <#else>
        <#assign stringStatusValue=status.value?default("")>
    </#if>
</#macro>

<#macro orcidFormRadioButtons path options separator attributes="" labelAttributes="">
    <@bind path/>
    <#list options?keys as value>
        <#assign id="${status.expression}${value_index}">
    <label for="${id}" ${labelAttributes}>${options[value]?html}
            <input type="radio" id="${id}" name="${status.expression}" value="${value?html}"<#if stringStatusValue == value> checked="checked"</#if> ${attributes}<@closeTag/>
    </label>${separator}
    </#list>
</#macro>

<#macro orcidUrl orcid>
    ${request.scheme}://${request.serverName}<#if request.serverPort != 80 && request.serverPort != 443>:${request.serverPort?string.computer}</#if>${request.contextPath}/${orcid}
</#macro>

<#macro absUrl url>
	<#if (url.value)?? && !url.value?starts_with("http")>
  		http://${url.value}
	<#else>
	${url.value}	
	</#if>      
</#macro>

<#macro privacy thing selected="protected" btnContainerClass="btn-group privacy-group abs-left-top" helpLink="http://support.orcid.org/knowledgebase/articles/124518 ">
    <div class="privacy-tool">
        
        <div class="${btnContainerClass}">
            <#if selected == "" || selected == "public"><button class="btn btn-success dropdown-toggle privacy-toggle">${springMacroRequestContext.getMessage("manage.lipublic")} <span class="caret"></span></button></#if>
            <#if selected == "limited"><button class="btn btn-warning dropdown-toggle privacy-toggle">${springMacroRequestContext.getMessage("manage.lilimited")} <span class="caret"></span></button></#if>
            <#if selected == "private" || selected == "protected"><button class="btn btn-danger dropdown-toggle privacy-toggle">${springMacroRequestContext.getMessage("manage.liprivate")} <span class="caret"></span></button></#if>
            <ul class="dropdown-menu privacy-menu">
                <li><a class="btn btn-success btn-privacy" href="#public">${springMacroRequestContext.getMessage("manage.lipublic")} <span class="caret"></span></a></li>
                <li><a class="btn btn-warning btn-privacy" href="#limited">${springMacroRequestContext.getMessage("manage.lilimited")} <span class="caret"></span></a></li>
                <li><a class="btn btn-danger btn-privacy" href="#private">${springMacroRequestContext.getMessage("manage.liprivate")} <span class="caret"></span></a></li>	
                <li><a class="btn" href="${helpLink}" target="_blank">${springMacroRequestContext.getMessage("manage.lihelp")} <span class="caret"></span></a></li>
            </ul>
        </div>
    </div>
</#macro>

<#macro privacyLabel selected="private">
    <#if selected == "" || selected == "public"><span class="label label-success privacy-label">${springMacroRequestContext.getMessage("manage.lipublic")}</span></#if>
    <#if selected == "limited"><span class="label label-warning privacy-label">${springMacroRequestContext.getMessage("manage.lilimited")}</span></#if>
    <#if selected == "private" || selected == "protected"><span class="label label-important privacy-label">${springMacroRequestContext.getMessage("manage.liprivate")}</span></#if>
</#macro>

<#macro month number=0><#compress>
   <#attempt><#local months = ['Jan', 'Feb', 'Mar', 'Apr', 'May', 'Jun', 'July', 'Aug', 'Sep', 'Oct', 'Nov', 'Dec'] />
      ${months[number?number-1]}
   <#recover>
      ${number}
   </#attempt>
</#compress></#macro>

<#--
 * unescapedMessage
 *
 * Macro to translate a message code into a message,
 * but with html escaping
 -->
<#macro unescapedMessage code>${springMacroRequestContext.getMessage(code, null, false)}</#macro>

<#--
 * unescapedMessageText
 *
 * Macro to translate a message code into a message,
 * using the given default text if no message found,
 * but without html escaping
 -->
<#macro unescapedMessageText code, text>${springMacroRequestContext.getMessage(code, text, false)}</#macro>

<#--
 * unescapedMessageArgs
 *
 * Macro to translate a message code with arguments into a message,
 * but without html escaping
 -->
<#macro unescapedMessageArgs code, args>${springMacroRequestContext.getMessage(code, args, false)}</#macro>

<#--
 * unescapedMessageArgsText
 *
 * Macro to translate a message code with arguments into a message,
 * using the given default text if no message found.
 -->
<#macro unescapedMessageArgsText code, args, text>${springMacroRequestContext.getMessage(code, args, text, false)}</#macro>

<#--
 * showErrorsUnescaped
 *
 * Show validation errors for the currently bound field, but
 * without html escaping.
 -->
<#macro showErrorsUnescaped>
    <@showErrorsUnescapedForPath spring.status.path/> 
</#macro>

<#--
 * showErrorsUnescapedForPath
 *
 * Show validation errors for the specified binding path, but
 * without html escaping.
 -->
<#macro showErrorsUnescapedForPath path>
    <#list spring.status.errors.getFieldErrors(path)?sort as error> <span class="orcid-error">${springMacroRequestContext.getMessage(error, false)}</span><#if error_has_next><br/></#if></#list>
</#macro>

<#macro showGlobalErrorsUnescaped>
    <#list spring.status.errors.globalErrors?sort as error> <span class="orcid-error">${springMacroRequestContext.getMessage(error, false)}</span><#if error_has_next><br/></#if></#list>
</#macro>