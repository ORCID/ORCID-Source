<@public >
<#escape x as x?html>
<#setting date_format="yyyy-MM-dd">
<div class="row workspace-top public-profile print">
    <div class="col-md-12">
        <div class="workspace-left workspace-profile">
        	<div class="id-banner">
	            <h2 class="full-name">	            	
					${(displayName)!}	                
	            </h2>	            	            
	            
	            <div class="oid">
					<div class="id-banner-header">
						<span><@orcid.msg 'common.orcid_id' /></span>
					</div>
					<div class="orcid-id-container">
						<div class="orcid-id-info">
	                        <span class="mini-orcid-icon"></span>
	                        <!-- Reference: orcid.js:removeProtocolString() -->
	                        <span id="orcid-id" class="orcid-id">${baseUri}/${(effectiveUserOrcid)!}</span>	
						</div>				
					</div>
				</div>
	        </div>
	        <!--Person sections-->
            <#include "/includes/ng2_templates/public-record-ng2-template.ftl">
            <public-record-ng2></public-record-ng2>
        </div>
    </div>   
    <div class="col-md-12">
        <div class="workspace-right">
    		<div class="workspace-inner-public workspace-public workspace-accordion">
        		<#if (isProfileEmpty)?? && isProfileEmpty>
        			<p class="margin-top-box"><b><@orcid.msg 'public_profile.empty_profile'/></b></p>
        		<#else>	            
	                <#if (biography.content)?? && (biography.content)?has_content>		                	        			
	        			<div class="workspace-accordion-content">
	        				<div class="row bottomBuffer">
	        					<div class="col-md-12 col-sm-12 col-xs-12">
	        						<h3 class="workspace-title">${springMacroRequestContext.getMessage("public_profile.labelBiography")}</h3>
	        					</div>
	        				</div>	        
	        				<div class="row bottomBuffer">					
		        				<div class="col-md-12 col-sm-12 col-xs-12">
									<div class="bio-content">${(biography.content)!}</div>		        					
		        				</div>
		        			</div>	        				
	        			</div>
	                </#if>
	                <#assign publicProfile = true />
	                <#if !(affiliationsEmpty)??>
                        <#include "/includes/ng2_templates/affiliation-ng2-template.ftl">
                        <affiliation-ng2  publicView="true"></affiliation-ng2>
                    </#if>
                    <!-- Funding -->
                    <#if !(fundingEmpty)??>     
                        <#include "/includes/ng2_templates/funding-ng2-template.ftl">
                        <funding-ng2  publicView="true"></funding-ng2>
                    </#if>
                    <@orcid.checkFeatureStatus 'RESEARCH_RESOURCE'>
	                    <#if !(researchResourcesEmpty)??>  
	                        <!-- Research resources -->
	                        <#include "/includes/ng2_templates/research-resource-ng2-template.ftl">
	                        <research-resource-ng2  publicView="true"></research-resource-ng2>
	                    </#if>
                    </@orcid.checkFeatureStatus>
                    <!-- Works -->
                    <#if !(worksEmpty)??> 
	                    <#include "/includes/ng2_templates/works-ng2-template.ftl">
	                    <works-ng2 publicView="true" printView="true"></works-ng2>
                    </#if>
                    <!-- Peer Review -->
                    <#if !(peerReviewEmpty)??> 
	                    <#include "/includes/ng2_templates/peer-review-ng2-template.ftl">
	                    <peer-review-ng2 publicView="true"></peer-review-ng2>
                    </#if>                    	
        		</#if>
        		<div id="public-last-modified">
                    <p class="small italic">${springMacroRequestContext.getMessage("public_profile.labelLastModified")} ${(lastModifiedTime?datetime)!}</p>
                </div>  
        	</div>	                   
        </div>
    </div>
</div>
</#escape>
</@public>