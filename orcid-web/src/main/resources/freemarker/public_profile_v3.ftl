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
<#-- @ftlvariable name="profile" type="org.orcid.jaxb.model.message.OrcidProfile" -->
<@public >
<#escape x as x?html>
<div class="row workspace-top public-profile">
    <div class="col-md-3 left-aside">
        <div class="workspace-left workspace-profile">
        	<div class="id-banner">
	            <h2 class="full-name">
	            	<#if (locked)?? && locked>
	            		<@orcid.msg 'public_profile.deactivated.given_names' /> <@orcid.msg 'public_profile.deactivated.family_name' />
	            	<#else>
		                <#if (profile.orcidBio.personalDetails.creditName.content)??>
		                    ${(profile.orcidBio.personalDetails.creditName.content)!}
		                <#else>
		                    ${(profile.orcidBio.personalDetails.givenNames.content)!} ${(profile.orcidBio.personalDetails.familyName.content)!}
		                </#if>
	                </#if>
	            </h2>	            	            
	            
	            <div class="oid">
					<div class="id-banner-header">
						<span><@orcid.msg 'common.orcid_id' /></span>
					</div>
					<div class="orcid-id-container">
						<div class="orcid-id-info">
					    	<span class="mini-orcid-icon"></span>
					    	<!-- Reference: orcid.js:removeProtocolString() -->
				       		<span id="orcid-id" class="orcid-id shortURI">${baseDomainRmProtocall}/${(profile.orcidIdentifier.path)!}</span>
						</div>
						<@security.authorize ifAnyGranted="ROLE_USER, ROLE_ADMIN, ROLE_BASIC, ROLE_PREMIUM, ROLE_BASIC_INSTITUTION, ROLE_PREMIUM_INSTITUTION">
							<div class="orcid-id-options">
								<a href="<@spring.url '/my-orcid'/>" class="gray-button"><@orcid.msg 'public-layout.return' /></a>
							</div>
						</@security.authorize>
					</div>
				</div>
				
				<#if (locked)?? && !locked>
		            <#if (profile.orcidBio.personalDetails.otherNames)?? && (profile.orcidBio.personalDetails.otherNames.otherName?size != 0)>
		            	<div class="workspace-section">
		            		<div class="workspace-section-header">
		                		<span class="workspace-section-title">${springMacroRequestContext.getMessage("public_profile.labelAlsoknownas")}</span>
		                	</div>
		                	<div>
				                <#list profile.orcidBio.personalDetails.otherNames.otherName as otherName>
				                	${otherName.content}<#if otherName_has_next></#if>
				                </#list>
			                </div>
		                </div>
		            </#if>	            	            	           
		            <#if (countryName)??>
		            	<div class="workspace-section">
		            		<div class="workspace-section-header">
		                		<span class="workspace-section-title"><@orcid.msg 'public_profile.labelCountry'/></span>
		                		<div>
		                			${(countryName)!}
		                		</div>
		                	</div>
		                </div>
		            </#if>
		            <#if (profile.orcidBio.keywords)?? && (profile.orcidBio.keywords.keyword?size != 0)>
			            <div class="workspace-section">
		            		<div class="workspace-section-header">
		                		<span class="workspace-section-title">${springMacroRequestContext.getMessage("public_profile.labelKeywords")}</span>
		                		<div> 
		                    		<#list profile.orcidBio.keywords.keyword as keyword>
		                        		${keyword.content}<#if keyword_has_next>,</#if>	                    
	                    			</#list>
	                        	</div>
	                        </div>
	                    </div>	                   
		            </#if>	            	            
		            <#if (profile.orcidBio.researcherUrls)?? && (profile.orcidBio.researcherUrls.researcherUrl?size != 0)>
		           		<div class="workspace-section">
		            		<div class="workspace-section-header">            
				                <span class="workspace-section-title">${springMacroRequestContext.getMessage("public_profile.labelWebsites")}</span>
				                <div>
				                    <#list profile.orcidBio.researcherUrls.researcherUrl as url>
				                        <a href="<@orcid.absUrl url.url/>" target="_blank" rel="nofollow"><#if (url.urlName.content)! != "">${url.urlName.content}<#else>${url.url.value}</#if></a><#if url_has_next><br/></#if>
				                    </#list>
			                    </div>
			                </div>
	                    </div>
		            </#if>	            	            
		            <#if (profile.orcidBio.externalIdentifiers)?? && (profile.orcidBio.externalIdentifiers.externalIdentifier?size != 0)>
						<div class="workspace-section">
		            		<div class="workspace-section-header">            
				                <span class="workspace-section-title">${springMacroRequestContext.getMessage("public_profile.labelOtherIDs")}</span>
				                <div>
				                    <#list profile.orcidBio.externalIdentifiers.externalIdentifier as external>
				                        <#if (external.externalIdUrl.value)??>
				                            <a href="${external.externalIdUrl.value}" target="_blank">${(external.externalIdCommonName.content)!}: ${(external.externalIdReference.content)!}</a><#if external_has_next><br/></#if>
				                        <#else>
				                            ${(external.externalIdCommonName.content)!}: ${(external.externalIdReference.content)!}<#if external_has_next><br/></#if>
				                        </#if>    
				                    </#list>
			                    </div>
			                 </div>
			             </div>	                
		            </#if>
				</#if>		           
	        </div>
        </div>
    </div>
    
    <div class="col-md-9 right-aside">
        <div class="workspace-right" ng-controller="PersonalInfoCtrl">
        	<#if (locked)?? && locked>
        		<div class="alert alert-error readme">
		        	<p><b id="error_locked"><@orcid.msg 'public-layout.locked'/></b></p>
		        </div>        		
        	<#else>
	        	<#if (deprecated)??>
		        	<div class="alert alert-error readme">
		        		<p><b><@orcid.msg 'public_profile.deprecated_account.1'/>&nbsp;<a href="${baseUriHttp}/${primaryRecord}">${baseUriHttp}/${primaryRecord}</a>&nbsp;<@orcid.msg 'public_profile.deprecated_account.2'/></b></p>
		        	</div>
	        	</#if>
	        	<div class="workspace-inner-public workspace-public workspace-accordion">
	        		<#if (isProfileEmpty)?? && isProfileEmpty>
	        			<p class="margin-top-box"><b><@orcid.msg 'public_profile.empty_profile'/></b></p>
	        		<#else>	            
		                <#if (profile.orcidBio.biography.content)?? && (profile.orcidBio.biography.content)?has_content>		                	        			
		        			<div class="workspace-accordion-content" ng-show="displayInfo">
		        				<div class="row bottomBuffer">
		        					<div class="col-md-12 col-sm-12 col-xs-12">
		        						<h3 class="workspace-title">${springMacroRequestContext.getMessage("public_profile.labelBiography")}</h3>
		        					</div>
		        				</div>	        
		        				<div class="row bottomBuffer">					
			        				<div class="col-md-12 col-sm-12 col-xs-12">
			        					${(profile.orcidBio.biography.content)!}
			        				</div>
			        			</div>	        				
		        			</div>
		                </#if>
		                <#assign publicProfile = true />
		                <#include "workspace_preview_activities_v3.ftl"/>	                    	
	        		</#if>
	        	</div>
	        </#if>            
        </div>
    </div>
</div>
</#escape>
</@public>