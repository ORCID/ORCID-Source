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
		<#if (peerReviews)??>
			<ul class="workspace-publications workspace-body-list">
		        <#list peerReviews as peerReview>		        			        	
		    		<div>
				        <h3 class="work-title">
				            <#if peerReview.subjectForm.title?? && peerReview.subjectForm.title.value??>
				        	    <#if peerReview.subjectForm.journalTitle?? && peerReview.subjectForm.journalTitle.value??>
				        	        <strong>${peerReview.subjectForm.title.value}</strong>: <span class="work-subtitle">${peerReview.subjectForm.journalTitle.value}</span>
				        	    <#else>
				        	        <strong>${peerReview.subjectForm.title.value}</strong>
				        	    </#if>
				        	    <#if peerReview.completionDate??>
				        	    	<#assign date = '' >
				        	    	<#if peerReview.completionDate.year??>
				        	       		<#assign date = date + peerReview.completionDate.year>
				        	    	</#if>
				        	    	<#if work.completionDate.month??>
				        	    		 <#if peerReview.completionDate.day??>
				        	       			<#assign date = date + '-' + peerReview.completionDate.day >
				        	    		</#if>
				        	       		<#assign date = date + '-' + peerReview.completionDate.month >
				        	        </#if>				        	        
				        	    	${date}
				        	    </#if>
				        	</#if>
				        </h3>				        
				    </div>
		        </#list>
			</ul>
		</#if>
	</noscript>
	<div>
		<#include "peer_review_body_inc.ftl"/>
	</div>
</#escape>