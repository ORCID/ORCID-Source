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
<#-- @ftlvariable name="profile" type="org.orcid.jaxb.model.message.OrcidProfile" -->
<@protected nav="record">
<#escape x as x?html>
<#if emailVerified?? && emailVerified>
    <div class="alert alert-success">
        <strong><@spring.message "orcid.frontend.web.email_verified"/></strong>
    </div>
</#if>
<#if recordClaimed?? && recordClaimed>
    <div class="alert alert-success">
        <strong><@spring.message "orcid.frontend.web.record_claimed"/></strong>
    </div>
</#if>
<div class="row workspace-top public-profile">
    <div class="span3 lhs">
    	<div class="workspace-left workspace-profile">
            <h2 class="full-name">
                <#if (profile.orcidBio.personalDetails.creditName.content)??>
                    ${(profile.orcidBio.personalDetails.creditName.content)!}
                <#else>
                    ${(profile.orcidBio.personalDetails.givenNames.content)!} ${(profile.orcidBio.personalDetails.familyName.content)!}
                </#if>
            </h2>
            <p><small id="orcid-id" class="orcid-id">${(profile.orcid.value)!}</small></p>
	        <p><a href="<@spring.url "/" + (profile.orcid.value)!"my-orcid/public" />" class="label btn-primary">View Public ORCID Record</a></p>
	        <p><a href="<@spring.url '/account/manage-bio-settings'/>" class="btn-update">Update</a></p>
	        <#if ((profile.orcidBio.personalDetails.otherNames.otherName)?size != 0)>
	        	<p><strong>Also known as:</strong><br />
		       		<#list profile.orcidBio.personalDetails.otherNames.otherName as otherName>
		       			${otherName.content}<#if otherName_has_next><br /></#if>
		       		</#list></p>
	       	</#if>
            <#if (profile.orcidBio.contactDetails.address.country.content)??>
                <p><strong>Country:</strong>
                ${(profile.orcidBio.contactDetails.address.country.content)!}
                </p>
            </#if>
	       	<#if (profile.orcidBio.keywords)?? && (profile.orcidBio.keywords.keyword?size != 0)>
	        	<p><strong>Keywords:</strong> 
		       		<#list profile.orcidBio.keywords.keyword as keyword>
		       			${keyword.content}<#if keyword_has_next>,</#if>
		       		</#list></p>
	       	</#if>
	       	<#if (profile.orcidBio.researcherUrls)?? && (profile.orcidBio.researcherUrls.researcherUrl?size != 0)>
	        	<p><strong>Websites:</strong> <br/>
		       		<#list profile.orcidBio.researcherUrls.researcherUrl as url>		       		
		       		   <a href="<@orcid.absUrl url.url/>"><#if (url.urlName.content)! != "">${url.urlName.content}<#else>${url.url.value}</#if></a><#if url_has_next><br/></#if>
		       		</#list></p>
	       	</#if>
		    <#if (profile.orcidBio.externalIdentifiers)?? && (profile.orcidBio.externalIdentifiers.externalIdentifier)?size != 0>
		        <p><strong>Other IDs:</strong> <br />   
		        	<#list profile.orcidBio.externalIdentifiers.externalIdentifier as external>
		        		<#if (external.externalIdUrl.value)??>
		        		    <a href="${(external.externalIdUrl.value)!}">${(external.externalIdCommonName.content)!} ${(external.externalIdReference.content)!}</a>
		        		<#else>
		        		    ${(external.externalIdCommonName.content)!} ${(external.externalIdReference.content)!}
		        		</#if>
		        		<#if external_has_next><br /></#if>
		        	</#list>
		        </p>
		    </#if>
		    <#if ((thirdPartiesForImport)?? && (thirdPartiesForImport)?size &gt; 0)>
    	        <ul class="workspace-help">
    	        	<li><a href="#third-parties" class="colorbox-modal">Import Research Activities</a></li>
    	        </ul>
    	        <div class="inline-modal" id="third-parties">
    	           <#list thirdPartiesForImport as thirdPartyDetails>
                        <#if thirdPartyDetails_index != 0><br/></#if>
                        <#assign redirect = (thirdPartyDetails.redirectUris.redirectUri[0].value) >
                        <#assign predefScopes = (thirdPartyDetails.redirectUris.redirectUri[0].scopeAsSingleString) >
                        <a class="third-party-colorbox" href="<@spring.url '/oauth/authorize?client_id=${thirdPartyDetails.clientId}&response_type=code&scope=${predefScopes}&redirect_uri=${redirect}'/>">${thirdPartyDetails.displayName}</a>          
                    </#list>
    	        </div>
	        </#if>
        </div>
    </div>
    <div class="span9">
        <div class="workspace-right">
        	<div class="workspace-inner workspace-header">
                <div class="alert alert-info"><strong>Add information about you to help distinguish you from other researchers.</strong></div>
                <div class="workspace-overview">
                    <a href="#workspace-affiliations" class="overview-count">${(profile.orcidBio.affiliations?size)!0}</a>
                    <a href="#workspace-affiliations" class="overview-title">Affiliations</a>
                    <div><a target="_blank" href="http://support.orcid.org/forums/179657-coming-soon" class="btn-update no-icon">Coming Soon</a></div>
                </div>
        		<div class="workspace-overview">
        			<a href="#workspace-publications" class="overview-count">${(profile.orcidActivities.orcidWorks.orcidWork?size)!0}</a>
        			<a href="#workspace-publications" class="overview-title">Works</a>
                    <div><a href="<@spring.url '/works-update'/>" class="btn-update">Update</a></div>
        		</div>
        		<div class="workspace-overview">
        			<a href="#workspace-grants" class="overview-count">${(profile.orcidActivities.orcidGrants.orcidGrant?size)!0}</a>
        			<a href="#workspace-grants" class="overview-title">Grants</a>
        			<br />
        			<a target="_blank" href="http://support.orcid.org/forums/179657-coming-soon" class="btn-update no-icon">Coming Soon</a>
        		</div>
        		<div class="workspace-overview">
        			<a href="#workspace-patents" class="overview-count">${(profile.orcidActivities.orcidPatents.orcidPatent?size)!0}</a>
        			<a href="#workspace-patents" class="overview-title">Patents</a>
        			<br />
        			<a target="_blank" href="http://support.orcid.org/forums/179657-coming-soon" class="btn-update no-icon">Coming Soon</a>
        		</div>
        	</div>
        	<div class="workspace-accordion" id="workspace-accordion">
        	
        	   <div id="workspace-personal" class="workspace-accordion-item workspace-accordion-active">
        			<h3 class="workspace-accordion-header"><a href="#">${springMacroRequestContext.getMessage("workspace.personal_information")}</a> <a href="<@spring.url '/account/manage-bio-settings'/>" class="btn-update">Update</a></h3>
            		<div class="workspace-accordion-content">
            			<#include "workspace_personal.ftl"/>
        			</div>
            	</div>
            	
        		<div id="workspace-affiliations" class="workspace-accordion-item${(!(profile.orcidBio.affiliations)?? || (profile.orcidBio.affiliations?size = 0))?string(" workspace-accordion-active", "")}">
                    <h3 class="workspace-accordion-header"><a href="#">Affiliations</a> <#--<a href="#" class="btn-update">Update</a></h3>--><#--<a target="_blank" href="http://support.orcid.org/forums/179657-coming-soon" class="btn-update no-icon">Coming Soon</a></h3>
                    <div class="workspace-accordion-content">
                        <#include "workspace_affiliations.ftl"/>
                    </div>-->
                </div>
                
                <div id="workspace-publications" class="workspace-accordion-item workspace-accordion-active">
        			<h3 class="workspace-accordion-header"><a href="#">Works</a> <a href="<@spring.url '/works-update'/>" class="btn-update">Update</a></h3>
            		<div class="workspace-accordion-content">
            			<#include "workspace_works_body_list.ftl"/>
            		</div>
            	</div>
            	
        		<div id="workspace-grants" class="workspace-accordion-item<#--${(!(profile.orcidActivities.orcidGrants)??)?string(" workspace-accordion-active", "")}-->">
        			<h3 class="workspace-accordion-header"><a href="#">Grants</a> <#--<a target="_blank" href="http://support.orcid.org/forums/179657-coming-soon" class="btn-update no-icon">Coming Soon</a></h3>
            		<div class="workspace-accordion-content">
            			<#include "workspace_grants_body_list.ftl"/>
            		</div>-->
            	</div>
            	
        		<div id="workspace-patents" class="workspace-accordion-item<#--${(!(profile.orcidActivities.orcidPatents)??)?string(" workspace-accordion-active", "")}-->">
        			<h3 class="workspace-accordion-header"><a href="#">Patents</a> <#--<a target="_blank" href="http://support.orcid.org/forums/179657-coming-soon" class="btn-update no-icon">Coming Soon</a></h3>
            		<div class="workspace-accordion-content">
            			<#include "workspace_patents_body_list.ftl"/>
            		</div>-->
            	</div>
            	
            </div>
        </div>
    </div>
</div>
</#escape>
</@protected>
