<@public >
<#escape x as x?html>
<#setting date_format="yyyy-MM-dd">
<div class="row workspace-top public-profile print">
    <div class="col-md-12">
        <div class="workspace-left workspace-profile">

			<#include "/includes/ng2_templates/print-id-banner-ng2-template.ftl">
			<print-id-banner-ng2></print-id-banner-ng2>


	        <!--Person sections-->
            <#include "/includes/ng2_templates/public-record-ng2-template.ftl">
            <public-record-ng2></public-record-ng2>
        </div>
    </div>   
    <div class="col-md-12">
        <div class="workspace-right">
    		<div class="workspace-inner-public workspace-public workspace-accordion">

				<#include "/includes/ng2_templates/bio-ng2-template.ftl">
                <bio-ng2></bio-ng2>
        		<#if !((peerReviewEmpty)?? && (affiliationsEmpty)?? && (fundingEmpty)?? && (researchResourcesEmpty)?? && (worksEmpty)??)>	  
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