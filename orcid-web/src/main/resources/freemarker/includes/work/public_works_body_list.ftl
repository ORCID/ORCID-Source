<#escape x as x?html>
	<#-- noscript is for search engines -->
	<noscript ng-non-bindable>
		<#if (works)??>
			<ul class="workspace-publications workspace-body-list">
		        <#list works as work>
		    		<div>
				        <h3 class="work-title">
				            <#if work.title?? && work.title.value??>
				        	    <#if work.subtitle?? && work.subtitle.value??>
				        	        <strong>${work.title.value}</strong>: <span class="work-subtitle">${work.subtitle.value}</span>
				        	    <#else>
				        	        <strong>${work.title.value}</strong>
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
	<div>
    <#include "body_work_inc_v3.ftl"/>
	</div>
</#escape>