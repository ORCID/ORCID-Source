<#escape x as x?html>
	 <#-- noscript is for search engines -->
    <noscript ng-non-bindable>
     
        <#if (peerReviews)??>
        	<ul class="workspace-publications workspace-body-list">
        		<#-- need changes          
	                <#list peerReviews as peerReview>
	                	                                    
	                    <div>
	                    	<h3 class="work-title">
	                            <#if peerReview.subject.title?? && peerReview.subject.title.title.content??>
	                                <#if peerReview.subject.journalTitle?? && peerReview.subject.journalTitle.content??>
	                                    <strong>${peerReview.subject.title.title.content}</strong>: <span class="work-subtitle">${peerReview.subject.journalTitle.content}</span>
	                                <#else>
	                                    <strong>${peerReview.subject.title.title.content}</strong>
	                                </#if>                       
	                            </#if>
	                        </h3>
	                    </div>
	        		</#list>
        		 -->
           </ul>
        </#if>         
    </noscript>
    <div>       
        <#include "peer_review_body_inc.ftl"/>
    </div>
</#escape>