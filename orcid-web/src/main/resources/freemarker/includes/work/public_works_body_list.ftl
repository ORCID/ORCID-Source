<#--

    =============================================================================

    ORCID (R) Open Source
    http://orcid.org

    Copyright (c) 2012-2014 ORCID, Inc.
    Licensed under an MIT-Style License (MIT)
    http://orcid.org/open-source-license

    This copyright and license information (including a link to the full license)
    shall be included in its entirety in all copies or substantial portion of
    the software.

    =============================================================================

-->
<#escape x as x?html>
	<#-- noscript is for search engines -->
	<noscript ng-non-bindable>
		<#if (works)??>
			<ul class="workspace-publications workspace-body-list">
		        <#list works as work>
		    		<div>
				        <h3 class="work-title">
				            <#if work.workTitle?? && work.workTitle.title?? && work.workTitle.title.value??>
				        	    <#if work.workTitle.subtitle?? && work.workTitle.subtitle.value??>
				        	        <strong>${work.workTitle.title.value}</strong>: <span class="work-subtitle">${work.workTitle.subtitle.value}</span>
				        	    <#else>
				        	        <strong>${work.workTitle.title.value}</strong>
				        	    </#if>
				        	    <#if work.publicationDate??>
				        	    	<#assign date = '' >
				        	    	<#if work.publicationDate.year??>
				        	       		<#assign date = date + work.publicationDate.year>
				        	    	</#if>
				        	    	<#if work.publicationDate.month??>
				        	    		 <#if work.publicationDate.day??>
				        	       			<#assign date = date + '-' + work.publicationDate.day >
				        	    		</#if>
				        	       		<#assign date = date + '-' + work.publicationDate.month >
				        	        </#if>				        	        
				        	    	${date}
				        	    </#if>
				        	</#if>
				        </h3>
				        <#if work.shortDescription??>
				           <#if work.shortDescription.value??>
				           <div>${work.shortDescription.value}</div>
				           </#if>
				        </#if>
				    </div>
		        </#list>
			</ul>
		</#if>
	</noscript>
	<div ng-controller="PublicWorkCtrl">
		  <#if RequestParameters['v']??>	
	      	<#include "body_work_inc_v3.ftl"/>
	      <#else>
	      	 <#include "body_work_inc.ftl"/>
	      </#if>
	</div>
</#escape>