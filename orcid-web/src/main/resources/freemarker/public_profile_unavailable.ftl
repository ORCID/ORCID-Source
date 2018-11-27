<@public>
<div class="row workspace-top public-profile">
    <div class="col-md-3 left-aside">
        <div class="workspace-left workspace-profile">
		
        <#include "/includes/ng2_templates/print-id-banner-ng2-template.ftl">
		<print-id-banner-ng2></print-id-banner-ng2>

        </div>
    </div>
    
    <div class="col-md-9 right-aside">
        <div class="workspace-right">
        	<#if (locked)?? && locked>
        		<div class="alert alert-error readme">
		        	<p><b id="error_locked"><@orcid.msg 'public-layout.locked'/></b></p>
		        </div>        		
        	<#elseif (deprecated)??>
	        	<div class="alert alert-error readme">
                    <@orcid.checkFeatureStatus featureName='HTTPS_IDS'>
                        <p><b><@orcid.msg 'public_profile.deprecated_account.1'/>&nbsp;<a href="${baseUri}/${primaryRecord}">${baseUri}/${primaryRecord}</a>&nbsp;<@orcid.msg 'public_profile.deprecated_account.2'/></b></p>
                    </@orcid.checkFeatureStatus> 
                    <@orcid.checkFeatureStatus featureName='HTTPS_IDS' enabled=false> 
    	        		<p><b><@orcid.msg 'public_profile.deprecated_account.1'/>&nbsp;<a href="${baseUriHttp}/${primaryRecord}">${baseUriHttp}/${primaryRecord}</a>&nbsp;<@orcid.msg 'public_profile.deprecated_account.2'/></b></p>
                    </@orcid.checkFeatureStatus>
	        	</div>
	        <#elseif (deactivated)??>
	        	<p class="margin-top-box"><b><@orcid.msg 'public_profile.empty_profile'/></b></p>
			</#if>	        	           
        </div>
    </div>
</div>
</@public>